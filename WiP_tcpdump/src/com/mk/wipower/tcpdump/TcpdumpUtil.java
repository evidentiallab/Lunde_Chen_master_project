package com.mk.wipower.tcpdump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class TcpdumpUtil{
	// private boolean mshowErrors;
    private static StringBuilder res;
    private static Process exec;
    private static int exitcode = -1;
    private static HashMap<Integer, Long> hmap;
	public static final String SCRIPT_FILE_TCPDUMP_START = "tcpdump_start.sh";
	public static final String SCRIPT_FILE_TCPDUMP_STOP  = "tcpdump_stop.sh";
	public static final String SCRIPT_FILE_EMPTY 		 = "empty.sh";
	
	public static void copyRawFile(Context ctx, int resid, File file, String mode) throws IOException,InterruptedException 
	{
		final String abspath = file.getAbsolutePath();

		final FileOutputStream out = new FileOutputStream(file);
		final InputStream is = ctx.getResources().openRawResource(resid);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();

		if(mode != null)   
			Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
	}
	
	public static boolean assertBinaries(Context ctx) {
		try {
			// Check tcpdumparm
		    File file = new File(ctx.getCacheDir(), "tcpdumparm");
			if ((!file.exists())) {
				copyRawFile(ctx, R.raw.tcpdumparm, file, "777");
			}
			
			// Check busybox
			file = new File(ctx.getCacheDir(), "busybox_g1");
			if (!file.exists()) {
				copyRawFile(ctx, R.raw.busybox_g1, file, "777");
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static String scriptHeaderTcpdumparm(Context ctx) {
		final String dir = ctx.getCacheDir().getAbsolutePath();
		final String mytcpdumparm = dir + "/tcpdumparm";        // !!!!!!
		return  "MYDIR=/mnt/sdcard/MyAndroidWifi\n"  ////!!!!!!!!!
				+ "TCPDUMPARM=" + mytcpdumparm + "\n";
	}

	
	public static void resetStartDumpScript(Context ctx, String script)
	{
		File file_start = new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_START);
		
		StringBuilder sb_start = new StringBuilder();
		sb_start.append(scriptHeaderTcpdumparm(ctx));
		sb_start.append(script); //
		
		String scprit_start = sb_start.toString();
		
		generateScript(file_start, scprit_start);
	}
	
	public static void generateScripts(Context ctx)
	{
		File file_start = new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_START);
		File file_stop  = new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_STOP );
		File file_empty = new File(ctx.getCacheDir(), SCRIPT_FILE_EMPTY);
		
		StringBuilder sb_start = new StringBuilder();
		sb_start.append(scriptHeaderTcpdumparm(ctx));
		sb_start.append("$TCPDUMPARM"); //
		
		String scprit_start = sb_start.toString();
		String scprit_stop  = "killall tcpdumparm";
		String scprit_empty = "exit 0";
		
		generateScript(file_start, scprit_start);
		generateScript(file_stop, scprit_stop);
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
	
	public static void startDump(Context ctx)
	{
		try
		{
			Pattern p = Pattern.compile("length\\s*[0-9]*");
			hmap = null;
			hmap = new HashMap<Integer, Long>();
			
			// Run script as root
			boolean asroot = true;
			if (asroot) {
			    // Create the "su" request to run the script
				 String scriptPath = (new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_START)).toString();
				 System.out.println(scriptPath);
				 exec = Runtime.getRuntime().exec("su -c " + scriptPath + "\n");  // Original 
				 LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: su -c " + scriptPath, 
						 MainPage.logFileName_Tcpdump);
			} else {
				// Create the "sh" request to run the script
				String scriptPath = (new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_START)).toString();
				 exec = Runtime.getRuntime().exec("sh " + scriptPath + "\n"); // Original	
				 LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: sh " + scriptPath, 
						 MainPage.logFileName_Tcpdump);
			}
							
			BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));  //!!!!!!!Great great great !!!
			// Consume the "stdout"
			String line;
			while ((line = br.readLine()) != null) {
				
				Matcher m = p.matcher(line);
				if(m.find())
				{
					// System.out.println(m.start() + "  " + m.end());
					String s = line.substring(m.start(), m.end());
					StringTokenizer tok = new StringTokenizer(s);
					tok.nextToken();
					s = tok.nextToken();
					int length = Integer.parseInt(s);
					if(hmap.containsKey(length))
						hmap.put(length, (long)hmap.get(length) + 1L);
					else {
						hmap.put(length, 1L);
					}
				}
			}
			// Consume the "stderr"
			BufferedReader r = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
			while ((line = r.readLine()) != null) {
				// LogUtil.logTcpdump(line);
				Matcher m = p.matcher(line);
				if(m.find())
				{
					// System.out.println(m.start() + "  " + m.end());
					String s = line.substring(m.start(), m.end());
					StringTokenizer tok = new StringTokenizer(s);
					tok.nextToken();
					s = tok.nextToken();
					int length = Integer.parseInt(s);
					if(hmap.containsKey(length))
						hmap.put(length, (long)hmap.get(length) + 1L);
					else {
						hmap.put(length, 1L);
					}
				}
			}
			// get the process exit code
			if (exec != null)
				exitcode = exec.waitFor();
			}
			catch(Exception e){LogUtil.logTcpdumpFile("Exception: " + e.getMessage(), 
					MainPage.logFileName_Tcpdump);}
	}
	
	public static boolean emptyRun(Context ctx)
	{
		try{
			String scriptPath = (new File(ctx.getCacheDir(), SCRIPT_FILE_EMPTY)).toString();
			System.out.println(scriptPath);
			Runtime.getRuntime().exec("su -c " + scriptPath + "\n").waitFor();
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public static void stopDump(Context ctx) throws IOException, InterruptedException
	{
		boolean asroot = true;
		if (asroot) {
		    // Create the "su" request to run the script
			String scriptPath = (new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_STOP)).toString();
			System.out.println(scriptPath);
			 exec = Runtime.getRuntime().exec("su -c " + scriptPath + "\n");  // Original 
			 LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: su -c " + scriptPath, 
					 MainPage.logFileName_Tcpdump);
		} else {
			// Create the "sh" request to run the script
			String scriptPath = (new File(ctx.getCacheDir(), SCRIPT_FILE_TCPDUMP_STOP)).toString();
			exec = Runtime.getRuntime().exec("sh " + scriptPath + "\n"); // Original	
			LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: sh " + scriptPath, 
					MainPage.logFileName_Tcpdump);
		}
		if (exec != null)
			exitcode = exec.waitFor();
		
		writeTcpdumpLogSorted();
		destroy();	
	}
	
	/**
	 * Destroy this script runner
	 */
	public static synchronized void destroy() {
		if (exec != null)
			exec.destroy();
		exec = null;
	}
	
	public static void writeTcpdumpLogUnsorted()
	{
		Set<Integer> set = hmap.keySet();
		Iterator<Integer> iterator = set.iterator();
		while(iterator.hasNext())
		{
		    int length = iterator.next();
		    LogUtil.logTcpdumpFile(length + "   " +  hmap.get(length), 
		    		MainPage.logFileName_Tcpdump);
		}
		
		hmap = null;
		hmap = new HashMap<Integer, Long>();
	}
	
	public static void writeTcpdumpLogSorted()
	{
		Map<Integer, Long> map = (Map<Integer, Long>) hmap;
		List<Map.Entry<Integer, Long>> result = SortHashMapUtil.sortHashMapIntLong(map);
		
		for(int i = 0; i < result.size(); i ++)
		{
			Map.Entry<Integer, Long> m = result.get(i);
			// System.out.println(m.getKey() + "  " + m.getValue());
			LogUtil.logTcpdumpFile(m.getKey() + "  " + m.getValue(), 
					MainPage.logFileName_Tcpdump);
		}
		
		hmap = null;
		hmap = new HashMap<Integer, Long>();
	}
	
	
}
