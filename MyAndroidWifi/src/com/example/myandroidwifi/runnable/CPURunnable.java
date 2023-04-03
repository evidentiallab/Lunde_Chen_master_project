package com.example.myandroidwifi.runnable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;

public class CPURunnable implements Runnable{
	  	private static final String CPU_FREQ_FILE = "/proc/cpuinfo";
	  	private static final String STAT_FILE = "/proc/stat";
		private Method methodReadProcFile;
		public static final int PROC_SPACE_TERM = (int)' ';
		public static final int PROC_OUT_LONG = 0x2000;
		private static final int[] READ_LONG_FORMAT = new int[] {
		    PROC_SPACE_TERM|PROC_OUT_LONG
		  };
		private long[] readBuf;
		private int fois = 0;
		public HashMap<Double, Long> map;
		public Runnable logRunnable;
		private Thread logThread;
		public long startTime;
		public String startStr;

	public CPURunnable()
	{
		map = new HashMap<Double, Long>();
		logRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(Constants.runningFlag)
				{
					if(!map.isEmpty())
					{
						logResult();
					}
					try {
						if(!Constants.low_power)
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_CPU);
						else {
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_CPU_LOW_POWER);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
		};
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		startStr = Constants.getNowTime();
		logThread = new Thread(logRunnable);
		logThread.start();
		while(Constants.runningFlag)
		{
			double freq = 0;
			freq = readCpuFreq();
			if(map.keySet().contains(freq))
				map.put(freq, map.get(freq) + 1);
			else 
				map.put(freq, 1L);
			fois ++;
			try {
				Thread.sleep(Constants.SLEEP_INTERVAL_READ_CPU);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//if(!logThread.isInterrupted())
		//	logThread.interrupt();
	}
	
	public void logResult()
	{
		String logStr = "";
		String endStr = Constants.getNowTime();
		Set<Double> set = map.keySet();
		Iterator<Double> iterator = set.iterator();
		logStr += "\nfrom " + startStr + " to " + endStr + "\n";
		long passedTime = System.currentTimeMillis() - startTime;
	    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
		logStr += "in total " + fois + " records of CPU freq" + "\n";
		logStr += "map.keySet : " + set.toString() + "\n";
		logStr += " CPU Freq and times:" + "\n";
		DecimalFormat df = new DecimalFormat("#.0000");
		while(iterator.hasNext())
		{
			double freq = iterator.next();
			long value = map.get(freq);
			System.out.println("freq: " + freq + "  times: " + value);
		    if(fois != 0)
			logStr += freq + "   " + value + "   " + df.format(value * 100.0000 /fois) +  " %\n";
		}
		LogUtil.logCPU(logStr);
	}
	
	public void stopLog()
	{
		if(logThread != null && !logThread.isInterrupted())
			logThread.interrupt();
	}
	
	
	private double readCpuFreq() {
	    /* Try to read from the /sys/devices file first.  If that doesn't work
	     * try manually inspecting the /proc/cpuinfo file.
	     */
	    long cpuFreqKhz = readLongFromFile(
	      "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
	    if(cpuFreqKhz != -1) {
	      
	      LogUtil.logCPU("cpuFreqKhz: " + "reading from /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
	      return cpuFreqKhz / 1000.0;
	    }

	    FileReader fstream;
	    try {
	      fstream = new FileReader(CPU_FREQ_FILE);
	      if(fois == 0)
	    	  LogUtil.logCPU("Read from the CPU_FREQ_FILE: /proc/cpuinfo");
	    } catch (FileNotFoundException e) {
	      LogUtil.logE("Could not read cpu frequency file");
	      return -1;
	    }
	    BufferedReader in = new BufferedReader(fstream, 500);
	    String line;
	    double cpuFreq = -1;
	    try {
	      while((line = in.readLine()) != null) {
	    	  // log(line);
	        if(line.startsWith("BogoMIPS")) {
	        	cpuFreq = Double.parseDouble(line.trim().split("[ :]+")[1]);
	        }
	      }
	    } catch(IOException e) {
	    } catch(NumberFormatException e) {
	    }
	    if(cpuFreq!= -1)
	    return cpuFreq;
	    LogUtil.logE("Failed to read cpu frequency");
	    return -1;
	  }
	
	  public long readLongFromFile(String file) {
		    if(methodReadProcFile == null) return -1;
		    try {
		      if((Boolean)methodReadProcFile.invoke(
		          null, file, READ_LONG_FORMAT, null, readBuf, null)) {
		        return readBuf[0];
		      }
		    } catch(IllegalAccessException e) {
		    } catch(InvocationTargetException e) {
		    }
		    return -1L;
		  }

}
