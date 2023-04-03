package com.mk.wipower.ipt_wni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;

public final class ConnectivityUtil {
	
	public static final int SPECIAL_UID_ANY = -10;
	public static final int SPECIAL_UID_KERNEL = -11;
	public static final String SCRIPT_FILE_IPTABLES_BLOCK = "wipoweriptables_block.sh";
	public static final String SCRIPT_FILE_IPTABLES_UNBLOCK = "wipoweriptables_unblock.sh";
	public static final String SCRIPT_FILE_EMPTY = "empty.sh";

	private static boolean hasroot = false;
	private static int isARMv6 = -1;
	
	public static String TAG = "ConnectivityUtil";

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
			} catch (Exception ex) {} 
			finally {
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
		if(mode != null)   /////
		 Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
	}
	
	///////////////********************************************//////////////////
	public static boolean applyIptables(Context ctx, boolean showErrors)
	{
		if (ctx == null) {
			return false;
		}
		try {
			int code;
			final StringBuilder res = new StringBuilder();
			// code = runScriptAsRoot(ctx, script.toString(), SCRIPT_FILE_IPTABLES_BLOCK,res);
			code = runScriptAsRoot(ctx, SCRIPT_FILE_IPTABLES_BLOCK, res);
			if (showErrors && code != 0) {
			} else {
				return true;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}
	
	///////////////********************************************//////////////////
	public static boolean purgeIptables(Context ctx, boolean showErrors) {
		StringBuilder res = new StringBuilder();
		try {
			int code = runScriptAsRoot(ctx, SCRIPT_FILE_IPTABLES_UNBLOCK, res);
			if (code == -1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean hasRootAccess(Context ctx, String scriptName, boolean showErrors) {  ///???? scriptName???
		if (hasroot)
			return true;
		final StringBuilder res = new StringBuilder();
		try {
			// Run an empty script just to check root access
			if (runScriptAsRoot(ctx, scriptName, res) == 0) {
				hasroot = true;
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static int runScript(Context ctx, String scriptName, StringBuilder res, long timeout, boolean asroot) {
		final File file = new File(ctx.getCacheDir(), scriptName);
		final ScriptRunner runner = new ScriptRunner(file, res, asroot);
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

	public static int runScriptAsRoot(Context ctx, String scriptName, StringBuilder res, long timeout) {
		return runScript(ctx, scriptName, res, timeout, true);
	}


	public static int runScriptAsRoot(Context ctx, String scriptName, StringBuilder res) throws IOException {
		return runScriptAsRoot(ctx, scriptName, res, 40000);
	}


	public static int runScript(Context ctx, String scriptName, StringBuilder res) throws IOException {
		return runScript(ctx, scriptName, res, 40000, false);
	}

	public static boolean assertBinaries(Context ctx, boolean showErrors) {
		boolean changed = false;
		try {
			// Check iptables_g1
		    File file = new File(ctx.getCacheDir(), "iptables_g1");
			if ((!file.exists()) && isARMv6()) {
				copyRawFile(ctx, R.raw.iptables_g1, file, "755");
				changed = true;
			}
			// Check iptables_n1
			file = new File(ctx.getCacheDir(), "iptables_n1");
			if ((!file.exists()) && (!isARMv6())) {
				copyRawFile(ctx, R.raw.iptables_n1, file, "755");
				changed = true;
			}
			// Check busybox
			file = new File(ctx.getCacheDir(), "busybox_g1");
			if (!file.exists()) {
				copyRawFile(ctx, R.raw.busybox_g1, file, "755");
				changed = true;
			}
			if (changed) {
			}
		} catch (Exception e) {
			if (showErrors)
			return false;
		}
		return true;
	}
	
	public static void generateScripts(Context ctx, boolean showError)
	{
		File file_block = new File(ctx.getCacheDir(), SCRIPT_FILE_IPTABLES_BLOCK);
		File file_unblock = new File(ctx.getCacheDir(), SCRIPT_FILE_IPTABLES_UNBLOCK);
		File file_empty = new File(ctx.getCacheDir(), SCRIPT_FILE_EMPTY);
		
		final String ITFS_WIFI[] = { "tiwlan+", "wlan+", "eth+" };
		final String ITFS_3G[] = { "rmnet+", "pdp+", "ppp+", "uwbr+", "wimax+" };
		
		assertBinaries(ctx, showError);
		
		StringBuilder sb_block = new StringBuilder();
		sb_block.append(scriptHeaderIptables(ctx));
		sb_block.append(""
				+ "$IPTABLES --version || exit 1\n"
				+ "# Create the droidwall chains if necessary\n"
				+ "$IPTABLES -L droidwall >/dev/null 2>/dev/null || $IPTABLES --new droidwall || exit 2\n"
//				+ "$IPTABLES -L droidwall-3g >/dev/null 2>/dev/null || $IPTABLES --new droidwall-3g || exit 3\n"
				+ "$IPTABLES -L droidwall-wifi >/dev/null 2>/dev/null || $IPTABLES --new droidwall-wifi || exit 4\n"
				+ "$IPTABLES -L droidwall-reject >/dev/null 2>/dev/null || $IPTABLES --new droidwall-reject || exit 5\n"
				+ "# Add droidwall chain to OUTPUT chain if necessary\n"
				+ "$IPTABLES -L OUTPUT | $GREP -q droidwall || $IPTABLES -A OUTPUT -j droidwall || exit 6\n"
				+ "# Flush existing rules\n" + "$IPTABLES -F droidwall || exit 7\n"
//				+ "$IPTABLES -F droidwall-3g || exit 8\n" 
				+ "$IPTABLES -F droidwall-wifi || exit 9\n"
				+ "$IPTABLES -F droidwall-reject || exit 10\n" + "");
		sb_block.append("" + "# Create the reject rule (log disabled)\n"
				+ "$IPTABLES -A droidwall-reject -j REJECT || exit 11\n" + "");
		sb_block.append("# Main rules (per interface)\n");
//		for (final String itf : ITFS_3G) {
//			sb_block.append("$IPTABLES -A droidwall -o ").append(itf).append(" -j droidwall-3g || exit\n");
//		}
		for (final String itf : ITFS_WIFI) {
			sb_block.append("$IPTABLES -A droidwall -o ").append(itf).append(" -j droidwall-wifi || exit\n");
		}
		
//		int uid = android.os.Process.getUidForName("dhcp");
//		if (uid != -1) {
//			sb_block.append("# dhcp user\n");
//			sb_block.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner ").append(uid)
//					.append(" -j RETURN || exit\n");
//		}
//		uid = android.os.Process.getUidForName("wifi");
//		if (uid != -1) {
//			sb_block.append("# wifi user\n");
//			sb_block.append("$IPTABLES -A droidwall-wifi -m owner --uid-owner ").append(uid)
//					.append(" -j RETURN || exit\n");
//		}
		
//		sb_block.append("$IPTABLES -A droidwall-3g -j droidwall-reject || exit\n");
		sb_block.append("$IPTABLES -A droidwall-wifi -j droidwall-reject || exit\n"); ///!!!!!!!!!!
		
		String scprit_block = sb_block.toString();
		
		StringBuilder sb_unblock = new StringBuilder();
		sb_unblock.append(scriptHeaderIptables(ctx));
		sb_unblock.append("$IPTABLES -F droidwall\n"
				+ "$IPTABLES -F droidwall-reject\n" 
//				+ "$IPTABLES -F droidwall-3g\n"
				+ "$IPTABLES -F droidwall-wifi\n");
		String scprit_unblock = sb_unblock.toString();
		
		StringBuilder sb_empty = new StringBuilder();
		sb_empty.append("exit 0");
		String scprit_empty = sb_empty.toString();
		
		generateScript(file_block, scprit_block);
		generateScript(file_unblock, scprit_unblock);
		generateScript(file_empty, scprit_empty);
	}
	
	public static void generateScript(File file, String script)
	{
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
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static final class ScriptRunner extends Thread {
		private final File file;
//		private final String script;
		private final StringBuilder res;
		private final boolean asroot;
		public int exitcode = -1;
		private Process exec;

		// public ScriptRunner(File file, String script, StringBuilder res, boolean asroot) {
		public ScriptRunner(File file, StringBuilder res, boolean asroot) {
			this.file = file;
//			this.script = script;
			this.res = res;
			this.asroot = asroot;
		}

		@Override
		public void run() {
			try {
				final String abspath = file.getAbsolutePath();
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
				System.out.println("res: " + res);
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
