package com.example.myandroidwifi.runnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.example.myandroidwifi.util.ConnectivityUtil;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.SortHashMapUtil;

// 使用单例模式

public class TcpdumpRunnable implements Runnable{
	private boolean asroot;
    private boolean addHeader;
	// private boolean mshowErrors;
    private StringBuilder res;
    private Process exec;
    private int exitcode = -1;
    private String script;
    private String cacheDir;
    private HashMap<Integer, Long> hmap;
    private String logFileName;
	
	public TcpdumpRunnable(String scrpt, String cchDir, boolean asrt, String logFileNme)
	{
		this.script = scrpt;
		this.cacheDir = cchDir;
		this.asroot = asrt;
		this.logFileName = logFileNme;
	}	
	
	public String scriptHeaderTcpdumparm() {
		final String dir = cacheDir;
		final String mytcpdumparm = dir + "/tcpdumparm";        // !!!!!!
		return "" + "TCPDUMPARM=tcpdumparm\n" + "BUSYBOX=busybox\n" + "GREP=grep\n" + "ECHO=echo\n"
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
				+ "MYDIR=/mnt/sdcard/MyAndroidWifi\n"  ////!!!!!!!!!
				+ "TCPDUMPARM=" + mytcpdumparm + "\n";
	}

	
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			final File file = new File(this.cacheDir, ConnectivityUtil.SCRIPT_FILE_TCPDUMP);
			file.createNewFile();
			final String scriptPath = file.getAbsolutePath();
			
			Pattern p = Pattern.compile("length\\s*[0-9]*");
			this.hmap = new HashMap<Integer, Long>();
			
			// make sure we have execution permission on the script file
			Runtime.getRuntime().exec("chmod 777 " + scriptPath).waitFor();
			// Write the script to be executed
			StringBuilder sb = new StringBuilder();
			sb.append(scriptHeaderTcpdumparm());
			sb.append(script);
			final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
			if (new File("/system/bin/sh").exists()) {
				out.write("#!/system/bin/sh\n");
			}
			out.write(sb.toString());
			
			if (!script.endsWith("\n"))
					out.write("\n");
			// out.write("exit\n");
			out.flush();
			out.close();
			
			// Run script as root
			if (asroot) {
			    // Create the "su" request to run the script
				 exec = Runtime.getRuntime().exec("su -c " + scriptPath + "\n");  // Original 
				  LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: su -c " + scriptPath, logFileName);
			} else {
				// Create the "sh" request to run the script
				exec = Runtime.getRuntime().exec("sh " + scriptPath + "\n"); // Original	
				 LogUtil.logTcpdumpFile(" Runtime.getRuntime().exec: sh " + scriptPath, logFileName);
			}
							
			BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));  ///!!!!!!!!!Great great great !!!
			// Consume the "stdout"
			String line;
			while ((line = br.readLine()) != null) {
				
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
			catch(Exception e){LogUtil.logTcpdumpFile("Exception: " + e.getMessage(), logFileName);}
	}

	
	public void writeTcpdumpLogUnsorted()
	{
		Set<Integer> set = hmap.keySet();
		Iterator<Integer> iterator = set.iterator();
		while(iterator.hasNext())
		{
		    int length = iterator.next();
		    LogUtil.logTcpdumpFile(length + "   " +  hmap.get(length), logFileName);
		}
		
		hmap = null;
		this.hmap = new HashMap<Integer, Long>();
	}
	
	public void writeTcpdumpLogSorted()
	{
		Map<Integer, Long> map = (Map<Integer, Long>) hmap;
		List<Map.Entry<Integer, Long>> result = SortHashMapUtil.sortHashMapIntLong(map);
		
		for(int i = 0; i < result.size(); i ++)
		{
			Map.Entry<Integer, Long> m = result.get(i);
			// System.out.println(m.getKey() + "  " + m.getValue());
			LogUtil.logTcpdumpFile(m.getKey() + "  " + m.getValue(), logFileName);
		}
		
		hmap = null;
		this.hmap = new HashMap<Integer, Long>();
	}
	
}
