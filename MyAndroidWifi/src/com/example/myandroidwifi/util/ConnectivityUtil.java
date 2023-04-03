
package com.example.myandroidwifi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.page.ConnectivityPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public final class ConnectivityUtil {
	
	/** 指定 UID 给 "任何程序" */
	public static final int SPECIAL_UID_ANY = -10;
	/** 指定 UID 给 Linux Kernel */
	public static final int SPECIAL_UID_KERNEL = -11;
	/** 脚本文件名 */
	public static final String SCRIPT_FILE_IPTABLES = "wipoweriptables.sh";
	public static final String SCRIPT_FILE_TCPDUMP = "wipowertcpdump.sh";
	public static final String SCRIPT_FILE_EMPTY = "empty.sh";
	// 设置
	public static final String PREFS_NAME = "WiPowerPrefs";
	public static final String PREF_3G_UIDS = "AllowedUids3G";
	public static final String PREF_WIFI_UIDS = "AllowedUidsWifi";
	public static final String PREF_ENABLED = "Enabled";

	// 应用程序缓存
	public static AndroidApp applications[] = null;
	// 是否有 root 权限
	private static boolean hasroot = false;
	// 是否 ARMv6 设备 (-1: unknown, 0: no, 1: yes)
	private static int isARMv6 = -1;

	public static void alert(Context ctx, CharSequence msg) {
		if (ctx != null) {
			new AlertDialog.Builder(ctx).setNeutralButton(android.R.string.ok, null).setMessage(msg).show();
		}
	}

	private static boolean isARMv6() {
		if (isARMv6 == -1) {
			BufferedReader r = null;
			try {
				isARMv6 = 0;
				r = new BufferedReader(new FileReader("/proc/cpuinfo"));
				for (String line = r.readLine(); line != null; line = r.readLine()) {
					if (line.startsWith("Processor") && line.contains("ARMv6")) {
						isARMv6 = 1;
						break;
					} else if (line.startsWith("CPU architecture") && (line.contains("6TE") || line.contains("5TE"))) {
						isARMv6 = 1;
						break;
					}
				}
			} catch (Exception ex) {
			} finally {
				if (r != null)
					try {
						r.close();
					} catch (Exception ex) {
					}
			}
		}
		return (isARMv6 == 1);
	}

	private static String scriptHeaderIptables(Context ctx) {
		final String dir = ctx.getCacheDir().getAbsolutePath();
		final String myiptables = dir + (isARMv6() ? "/iptables_g1" : "/iptables_n1");        // !!!!!!
		return "" + "IPTABLES=iptables\n" + "BUSYBOX=busybox\n" + "GREP=grep\n" + "ECHO=echo\n"
				+ "# Try to find busybox\n" + "if "
				+ dir
				+ "/busybox_g1 --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX="
				+ dir
				+ "/busybox_g1\n"
				+ "	GREP=\"$BUSYBOX grep\"\n"
				+ "	ECHO=\"$BUSYBOX echo\"\n"
				+ "elif busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=busybox\n"
				+ "elif /system/xbin/busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=/system/xbin/busybox\n"
				+ "elif /system/bin/busybox --help >/dev/null 2>/dev/null ; then\n"
				+ "	BUSYBOX=/system/bin/busybox\n"
				+ "fi\n"
				+ "# Try to find grep\n"
				+ "if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "	if $ECHO 1 | $BUSYBOX grep -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "		GREP=\"$BUSYBOX grep\"\n"
				+ "	fi\n"
				+ "	# Grep is absolutely required\n"
				+ "	if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n"
				+ "		$ECHO The grep command is required. Wi-Power will not work.\n"
				+ "		exit 1\n"
				+ "	fi\n"
				+ "fi\n"
				+ "# Try to find iptables\n"
				+ "if "
				+ myiptables
				+ " --version >/dev/null 2>/dev/null ; then\n"
				+ "	IPTABLES=" + myiptables + "\n" + "fi\n" + "";
	}		
	
	public static void copyRawFile(Context ctx, int resid, File file, String mode) throws IOException,
			InterruptedException {
		final String abspath = file.getAbsolutePath();
		// Write the iptables binary
		final FileOutputStream out = new FileOutputStream(file);
		final InputStream is = ctx.getResources().openRawResource(resid);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
		// Change the permissions
		if(mode != null)   /// I added this
		 Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
	}

	private static boolean applyIptablesRulesImpl(Context ctx, List<Integer> uidsWifi, List<Integer> uids3g,
			boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		assertBinaries(ctx, showErrors);
		final String ITFS_WIFI[] = { "tiwlan+", "wlan+", "eth+" };
		final String ITFS_3G[] = { "rmnet+", "pdp+", "ppp+", "uwbr+", "wimax+" };
		final StringBuilder script = new StringBuilder();
		try {
			int code;
			script.append(scriptHeaderIptables(ctx));
			script.append(""
					+ "$IPTABLES --version || exit 1\n"
					+ "# Create the droidwall chains if necessary\n"
					+ "$IPTABLES -L droidwall >/dev/null 2>/dev/null || $IPTABLES --new droidwall || exit 2\n"
					+ "$IPTABLES -L droidwall-3g >/dev/null 2>/dev/null || $IPTABLES --new droidwall-3g || exit 3\n"
					+ "$IPTABLES -L droidwall-wifi >/dev/null 2>/dev/null || $IPTABLES --new droidwall-wifi || exit 4\n"
					+ "$IPTABLES -L droidwall-reject >/dev/null 2>/dev/null || $IPTABLES --new droidwall-reject || exit 5\n"
					+ "# Add droidwall chain to OUTPUT chain if necessary\n"
					+ "$IPTABLES -L OUTPUT | $GREP -q droidwall || $IPTABLES -A OUTPUT -j droidwall || exit 6\n"
					+ "# Flush existing rules\n" + "$IPTABLES -F droidwall || exit 7\n"
					+ "$IPTABLES -F droidwall-3g || exit 8\n" + "$IPTABLES -F droidwall-wifi || exit 9\n"
					+ "$IPTABLES -F droidwall-reject || exit 10\n" + "");
			// Check if logging is enabled
			
			script.append("" + "# Create the reject rule (log disabled)\n"
						+ "$IPTABLES -A droidwall-reject -j REJECT || exit 11\n" + "");
			
			script.append("# Main rules (per interface)\n");
			for (final String itf : ITFS_3G) {
				script.append("$IPTABLES -A droidwall -o ").append(itf).append(" -j droidwall-3g || exit\n");
			}
			for (final String itf : ITFS_WIFI) {
				script.append("$IPTABLES -A droidwall -o ").append(itf).append(" -j droidwall-wifi || exit\n");
			}

			script.append("# Filtering rules\n");
			final String targetRule = "RETURN";
			final boolean any_3g = uids3g.indexOf(SPECIAL_UID_ANY) >= 0;
			final boolean any_wifi = uidsWifi.indexOf(SPECIAL_UID_ANY) >= 0;
			if (!any_wifi) {
				// When "white listing" wifi, we need to ensure that the dhcp
				// and wifi users are allowed
				int uid = android.os.Process.getUidForName("dhcp");
				if (uid != -1) {
					script.append("# dhcp user\n");
					script.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner ").append(uid)
							.append(" -j RETURN || exit\n");
				}
				uid = android.os.Process.getUidForName("wifi");
				if (uid != -1) {
					script.append("# wifi user\n");
					script.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner ").append(uid)
							.append(" -j RETURN || exit\n");
				}
			}
			if (!any_3g) 
			{
				/* release/block individual applications on this interface */
				for (final Integer uid : uids3g) {
					if (uid >= 0)
						script.append("$IPTABLES -A droidwall-3g -m owner --uid-owner ").append(uid).append(" -j ")
								.append(targetRule).append(" || exit\n");
				}
			}
			if (!any_wifi)
			{
				/* release/block individual applications on this interface */
				for (final Integer uid : uidsWifi) {
					if (uid >= 0)
						script.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner ").append(uid).append(" -j ")
								.append(targetRule).append(" || exit\n");
				}
			}
			
			if (!any_3g) {
					if (uids3g.indexOf(SPECIAL_UID_KERNEL) >= 0) {
						script.append("# hack to allow kernel packets on white-list\n");
						script.append("$IPTABLES -A droidwall-3g -m owner --uid-owner 0:999999999 -j droidwall-reject || exit\n");
					} else {
						script.append("$IPTABLES -A droidwall-3g -j droidwall-reject || exit\n");
					}
				}
			
			if (!any_wifi) {
					if (uidsWifi.indexOf(SPECIAL_UID_KERNEL) >= 0) {
						script.append("# hack to allow kernel packets on white-list\n");
						script.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner 0:999999999 -j droidwall-reject || exit\n");
					} else {
						script.append("$IPTABLES -A droidwall-wifi -j droidwall-reject || exit\n");
					}
				}
			
			final StringBuilder res = new StringBuilder();
			code = runScriptAsRoot(ctx, script.toString(), SCRIPT_FILE_IPTABLES,res);
			if (showErrors && code != 0) {
				String msg = res.toString();
				Log.e("Wi-Power", msg);
				// Remove unnecessary help message from output
				if (msg.indexOf("\nTry `iptables -h' or 'iptables --help' for more information.") != -1) {
					msg = msg.replace("\nTry `iptables -h' or 'iptables --help' for more information.", "");
				}
				alert(ctx, "Error applying iptables rules. Exit code: " + code + "\n\n" + msg.trim());
			} else {
				return true;
			}
		} catch (Exception e) {
			if (showErrors)
				alert(ctx, "error refreshing iptables: " + e);
		}
		return false;
	}

	public static boolean applySavedIptablesRules(Context ctx, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		final List<Integer> uids_wifi = new LinkedList<Integer>();
		if (savedUids_wifi.length() > 0) {
			// Check which applications are allowed on wifi
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_wifi.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}
		final List<Integer> uids_3g = new LinkedList<Integer>();
		if (savedUids_3g.length() > 0) {
			// Check which applications are allowed on 2G/3G
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_3g.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}
		return applyIptablesRulesImpl(ctx, uids_wifi, uids_3g, showErrors);
	}

	public static boolean applyIptablesRules(Context ctx, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		saveRules(ctx);
		return applySavedIptablesRules(ctx, showErrors);
	}

	public static void saveRules(Context ctx) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final AndroidApp[] apps = getApps(ctx);
		// Builds a pipe-separated list of names
		final StringBuilder newuids_wifi = new StringBuilder();
		final StringBuilder newuids_3g = new StringBuilder();
		for (int i = 0; i < apps.length; i++) {
			if (apps[i].selected_wifi) {
				if (newuids_wifi.length() != 0)
					newuids_wifi.append('|');
				newuids_wifi.append(apps[i].uid);
			}
			if (apps[i].selected_3g) {
				if (newuids_3g.length() != 0)
					newuids_3g.append('|');
				newuids_3g.append(apps[i].uid);
			}
		}
		// save the new list of UIDs
		final Editor edit = prefs.edit();
		edit.putString(PREF_WIFI_UIDS, newuids_wifi.toString());
		edit.putString(PREF_3G_UIDS, newuids_3g.toString());
		edit.commit();
	}

	public static boolean purgeIptables(Context ctx, boolean showErrors) {
		StringBuilder res = new StringBuilder();
		try {
			assertBinaries(ctx, showErrors);
			int code = runScriptAsRoot(ctx, scriptHeaderIptables(ctx) + "$IPTABLES -F droidwall\n"
					+ "$IPTABLES -F droidwall-reject\n" + "$IPTABLES -F droidwall-3g\n"
					+ "$IPTABLES -F droidwall-wifi\n", SCRIPT_FILE_IPTABLES, res);
			if (code == -1) {
				if (showErrors)
					alert(ctx, "error purging iptables. exit code: " + code + "\n" + res);
				return false;
			}
			return true;
		} catch (Exception e) {
			if (showErrors)
				alert(ctx, "error purging iptables: " + e);
			return false;
		}
	}
	

	public static AndroidApp[] getApps(Context ctx) {
		if (applications != null) {
			// return cached instance
			return applications;
		}
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		// allowed application names separated by pipe '|' (persisted)
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		int selected_wifi[] = new int[0];
		int selected_3g[] = new int[0];
		if (savedUids_wifi.length() > 0) {
			// Check which applications are allowed
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			selected_wifi = new int[tok.countTokens()];
			for (int i = 0; i < selected_wifi.length; i++) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						selected_wifi[i] = Integer.parseInt(uid);
					} catch (Exception ex) {
						selected_wifi[i] = -1;
					}
				}
			}
			// Sort the array to allow using "Arrays.binarySearch" later
			Arrays.sort(selected_wifi);
		}
		if (savedUids_3g.length() > 0) {
			// Check which applications are allowed
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			selected_3g = new int[tok.countTokens()];
			for (int i = 0; i < selected_3g.length; i++) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						selected_3g[i] = Integer.parseInt(uid);
					} catch (Exception ex) {
						selected_3g[i] = -1;
					}
				}
			}
			// Sort the array to allow using "Arrays.binarySearch" later
			Arrays.sort(selected_3g);
		}
		try {
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
			final HashMap<Integer, AndroidApp> map = new HashMap<Integer, AndroidApp>();
			final Editor edit = prefs.edit();
			boolean changed = false;
			String name = null;
			String cachekey = null;
			AndroidApp app = null;
			for (final ApplicationInfo apinfo : installed) {
				app = map.get(apinfo.uid);
				// filter applications which are not allowed to access the
				// Internet
				if (app == null
						&& PackageManager.PERMISSION_GRANTED != pkgmanager.checkPermission(
								Manifest.permission.INTERNET, apinfo.packageName)) {
					continue;
				}
				// try to get the application label from our cache -
				// getApplicationLabel() is horribly slow!!!!
				cachekey = "cache.label." + apinfo.packageName;
				name = prefs.getString(cachekey, "");
				if (name.length() == 0) {
					// get label and put on cache
					name = pkgmanager.getApplicationLabel(apinfo).toString();
					edit.putString(cachekey, name);
					changed = true;
				}
				if (app == null) {
					app = new AndroidApp();
					app.uid = apinfo.uid;
					app.names = new String[] { name };
					map.put(apinfo.uid, app);
				} else {
					final String newnames[] = new String[app.names.length + 1];
					System.arraycopy(app.names, 0, newnames, 0, app.names.length);
					newnames[app.names.length] = name;
					app.names = newnames;
				}
				// check if this application is selected
				if (!app.selected_wifi && Arrays.binarySearch(selected_wifi, app.uid) >= 0) {
					app.selected_wifi = true;
				}
				if (!app.selected_3g && Arrays.binarySearch(selected_3g, app.uid) >= 0) {
					app.selected_3g = true;
				}
			}
			if (changed) {
				edit.commit();
			}
			/* add special applications to the list */
			final AndroidApp special[] = {
					new AndroidApp(SPECIAL_UID_ANY, "(Any application) - Same as selecting all applications", false,
							false),
					new AndroidApp(SPECIAL_UID_KERNEL, "(Kernel) - Linux kernel", false, false),
					new AndroidApp(android.os.Process.getUidForName("root"), "(root) - Applications running as root",
							false, false),
					new AndroidApp(android.os.Process.getUidForName("media"), "Media server", false, false),
					new AndroidApp(android.os.Process.getUidForName("vpn"), "VPN networking", false, false),
					new AndroidApp(android.os.Process.getUidForName("shell"), "Linux shell", false, false), };
			for (int i = 0; i < special.length; i++) {
				app = special[i];
				if (app.uid != -1 && !map.containsKey(app.uid)) {
					// check if this application is allowed
					if (Arrays.binarySearch(selected_wifi, app.uid) >= 0) {
						app.selected_wifi = true;
					}
					if (Arrays.binarySearch(selected_3g, app.uid) >= 0) {
						app.selected_3g = true;
					}
					map.put(app.uid, app);
				}
			}
			applications = new AndroidApp[map.size()];
			int index = 0;
			for (AndroidApp application : map.values())
				applications[index++] = application;
			return applications;
		} catch (Exception e) {
			alert(ctx, "error: " + e);
		}
		return null;
	}


	public static boolean hasRootAccess(Context ctx, String scriptName, boolean showErrors) {  ///???? scriptName???
		if (hasroot)
			return true;
		final StringBuilder res = new StringBuilder();
		try {
			// Run an empty script just to check root access
			if (runScriptAsRoot(ctx, "exit 0", scriptName, res) == 0) {
				hasroot = true;
				return true;
			}
		} catch (Exception e) {
		}
		if (showErrors) {
			alert(ctx,
					"Could not acquire root access.\n"
							+ "Please make sure Wi-Power " +
							"has enough permissions to execute the \"su\" command.\n\n"
							+ "Error message: " + res.toString());
		}
		return false;
	}


	public static int runScript(Context ctx, String script,  String scriptName, StringBuilder res, long timeout, boolean asroot) {
		// final File file = new File(ctx.getCacheDir(), SCRIPT_IPTABLES);
		final File file = new File(ctx.getCacheDir(), scriptName);
		final ScriptRunner runner = new ScriptRunner(file, script, res, asroot);
		runner.start();
		try {
			if (timeout > 0) {
				runner.join(timeout);
			} else {
				runner.join();
			}
			if (runner.isAlive()) {
				// Timed-out
				runner.interrupt();
				runner.join(150);
				runner.destroy();
				runner.join(50);
			}
		} catch (InterruptedException ex) {}
		return runner.exitcode;
	}

	public static int runScriptAsRoot(Context ctx, String script, String scriptName, StringBuilder res, long timeout) {
		return runScript(ctx, script, scriptName, res, timeout, true);
	}


	public static int runScriptAsRoot(Context ctx, String script, String scriptName, StringBuilder res) throws IOException {
		return runScriptAsRoot(ctx, script, scriptName, res, 40000);
	}


	public static int runScript(Context ctx, String script, String scriptName, StringBuilder res) throws IOException {
		return runScript(ctx, script, scriptName, res, 40000, false);
	}

	public static boolean assertBinaries(Context ctx, boolean showErrors) {
		boolean changed = false;
		try {
			// Check iptables_g1
		    File file = new File(ctx.getCacheDir(), "iptables_g1");
			// File file = new File("/system/xbin/", "iptables_g1");
			Toast.makeText(ctx, file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
			if ((!file.exists()) && isARMv6()) {
				copyRawFile(ctx, R.raw.iptables_g1, file, "755");
				changed = true;
			}
			// Check iptables_n1
			file = new File(ctx.getCacheDir(), "iptables_n1");
			// file = new File("/system/xbin/", "iptables_g1");
			Toast.makeText(ctx, file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
			if ((!file.exists()) && (!isARMv6())) {
				copyRawFile(ctx, R.raw.iptables_n1, file, "755");
				changed = true;
			}
			// Check busybox
			file = new File(ctx.getCacheDir(), "busybox_g1");
			// file = new File("/system/xbin/", "iptables_g1");
			Toast.makeText(ctx, file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
			if (!file.exists()) {
				copyRawFile(ctx, R.raw.busybox_g1, file, "755");
				changed = true;
			}
			file = new File(ctx.getCacheDir(), "tcpdumparm");
			// file = new File("/system/xbin/", "iptables_g1");
			Toast.makeText(ctx, file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
			if (!file.exists()) {
				copyRawFile(ctx, R.raw.tcpdumparm, file, "777"); ///////////////
				changed = true;
			}
			if (changed) {
				Toast.makeText(ctx, R.string.toast_bin_installed, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			if (showErrors)
				alert(ctx, "Error installing binary files: " + e);
			return false;
		}
		return true;
	}

	public static boolean isEnabled(Context ctx) {
		if (ctx == null)
			return false;
		return ctx.getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_ENABLED, false);
	}


	public static void setEnabled(Context ctx, boolean enabled) {
		if (ctx == null)
			return;
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		if (prefs.getBoolean(PREF_ENABLED, false) == enabled) {
			return;
		}
		final Editor edit = prefs.edit();
		edit.putBoolean(PREF_ENABLED, enabled);
		if (!edit.commit()) {
			alert(ctx, "Error writing to preferences");
			return;
		}
	}

	public static void applicationRemoved(Context ctx, int uid) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		// allowed application names separated by pipe '|' (persisted)
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		final String uid_str = uid + "";
		boolean changed = false;
		// look for the removed application in the "wi-fi" list
		if (savedUids_wifi.length() > 0) {
			final StringBuilder newuids = new StringBuilder();
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			while (tok.hasMoreTokens()) {
				final String token = tok.nextToken();
				if (uid_str.equals(token)) {
					Log.d(ConnectivityPage.TAG, "Removing UID " + token + " from the wi-fi list (package removed)!");
					changed = true;
				} else {
					if (newuids.length() > 0)
						newuids.append('|');
					newuids.append(token);
				}
			}
			if (changed) {
				editor.putString(PREF_WIFI_UIDS, newuids.toString());
			}
		}
		// look for the removed application in the "3g" list
		if (savedUids_3g.length() > 0) {
			final StringBuilder newuids = new StringBuilder();
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			while (tok.hasMoreTokens()) {
				final String token = tok.nextToken();
				if (uid_str.equals(token)) {
					Log.d(ConnectivityPage.TAG, "Removing UID " + token + " from the 3G list (package removed)!");
					changed = true;
				} else {
					if (newuids.length() > 0)
						newuids.append('|');
					newuids.append(token);
				}
			}
			if (changed) {
				editor.putString(PREF_3G_UIDS, newuids.toString());
			}
		}
		// if anything has changed, save the new prefs...
		if (changed) {
			editor.commit();
			if (isEnabled(ctx)) {
				// .. and also re-apply the rules if the firewall is enabled
				applySavedIptablesRules(ctx, false);
			}
		}
	}


	public static final class AndroidApp {
		/** linux user id */
		int uid;
		/** application names belonging to this user id */
		public String names[];
		/** indicates if this application is selected for wifi */
		public boolean selected_wifi;
		/** indicates if this application is selected for 3g */
		public boolean selected_3g;
		/** toString cache */
		String tostr;

		public AndroidApp() {
		}

		public AndroidApp(int uid, String name, boolean selected_wifi, boolean selected_3g) {
			this.uid = uid;
			this.names = new String[] { name };
			this.selected_wifi = selected_wifi;
			this.selected_3g = selected_3g;
		}

		/**
		 * Screen representation of this application
		 */
		@Override
		public String toString() {
			if (tostr == null) {
				final StringBuilder s = new StringBuilder();
				if (uid > 0)
					s.append(uid + " : ");
				for (int i = 0; i < names.length; i++) {
					if (i != 0)
						s.append(", ");
					s.append(names[i]);
				}
				s.append("\n");
				tostr = s.toString();
			}
			return tostr;
		}
	}

	private static final class ScriptRunner extends Thread {
		private final File file;
		private final String script;
		private final StringBuilder res;
		private final boolean asroot;
		public int exitcode = -1;
		private Process exec;

		public ScriptRunner(File file, String script, StringBuilder res, boolean asroot) {
			this.file = file;
			this.script = script;
			this.res = res;
			this.asroot = asroot;
		}

		@Override
		public void run() {
			try {
				file.createNewFile();
				final String abspath = file.getAbsolutePath();
				// make sure we have execution permission on the script file
				Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();
				// Write the script to be executed
				final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
				if (new File("/system/bin/sh").exists()) {
					out.write("#!/system/bin/sh\n");
				}
				out.write(script);
				if (!script.endsWith("\n"))
					out.write("\n");
				out.write("exit\n");
				out.flush();
				out.close();
				if (this.asroot) {
					// Create the "su" request to run the script
					 exec = Runtime.getRuntime().exec("su -c " + abspath);  // Original 
					// exec = Runtime.getRuntime().exec("/data/su " + abspath);  // modified by mk
				} else {
					// Create the "sh" request to run the script
					 exec = Runtime.getRuntime().exec("sh " + abspath); // Original 
				}
				InputStreamReader r = new InputStreamReader(exec.getInputStream());  ///!!!!!!!!!Great great great !!!
				final char buf[] = new char[1024];
				int read = 0;
				// Consume the "stdout"
				while ((read = r.read(buf)) != -1) {
					if (res != null)
						res.append(buf, 0, read);
				}
				// Consume the "stderr"
				r = new InputStreamReader(exec.getErrorStream());
				read = 0;
				while ((read = r.read(buf)) != -1) {
					if (res != null)
						res.append(buf, 0, read);
				}
				// get the process exit code
				if (exec != null)
					this.exitcode = exec.waitFor();
			} catch (InterruptedException ex) {
				if (res != null)
					res.append("\nOperation timed-out");
			} catch (Exception ex) {
				if (res != null)
					res.append("\n" + ex);
			} finally {
				destroy();
			}
		}

		/**
		 * Destroy this script runner
		 */
		public synchronized void destroy() {
			if (exec != null)
				exec.destroy();
			exec = null;
		}
	}
}
