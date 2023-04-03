package com.mk.rssi.randomwalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
	public static String ROOT_DIR = "/home/mk/0905/";

	public static final int VERY_STRONG 	= 0;
    public static final int LITTLE_WEAK 	= 1;
    public static final int WEAK 			= 2;
    
    public static final String STR_VERY_STRONG 	=  "VERY_STRONG";
    public static final String STR_STRONG 	 	= 		"STRONG";
    public static final String STR_LITTLE_WEAK 	=  "LITTLE_WEAK";
    public static final String STR_WEAK			= 		  "WEAK";
    
    public static void write(String str, File file) 
    {
    	try 
    	{
  			if (!file.getParentFile().exists())
  			{
  				file.getParentFile().mkdirs();
  			}
  			FileWriter fileWriter = new FileWriter(file, true);
  			PrintWriter printWriter = new PrintWriter(fileWriter);
  			printWriter.println(str);
  			printWriter.flush();
  			fileWriter.flush();
  			printWriter.close();
  			fileWriter.close();
  		} catch(IOException e)
  		{}
  	}
    
    public static String getNowTime()
	{
		 DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
		 Date date = new Date();
		 return dateFormat.format(date);
	}
	
	public static String getNowTime(String formatStr)
	{
		 DateFormat dateFormat = new SimpleDateFormat(formatStr);
		 Date date = new Date();
		 return dateFormat.format(date);
	}

	public static String timeMillisToString(long timeMillis)
	{
		int hour = 0, min = 0, sec = 0;
		sec = (int) (timeMillis / 1000);
		String secStr = String.valueOf(sec);
		if(sec>=60)
		{
			min = (int)(sec/60);
			sec = sec % 60;
		}
		if(min>=60)
		{
			hour = (int)(min/60);
			min = min % 60;
		}
		return (String.format("%02d", hour) + " h, "
				+ String.format("%02d", min) + " min, " + String.format("%02d", sec) + " sec,  soit " 
				+ secStr + " seconds");
	}
	
	public static String bytesToKbMbGb(long size)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double msize = size;
		if(msize > 1024L)
		{
			msize /= 1024L;
			if(msize > 1024L)
			{
				msize /= 1024L;
				if(msize > 1024L)
					{
					msize /= 1024L;
					return df.format(msize) + " Gb";
					}
				return df.format(msize) + " Mb";
			}
			return df.format(msize) + " Kb";
		}
		return df.format(msize) + " B";
	}
	
	public static String bpsToKbpsMbpsGbps(long speed, int interval)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double msize = speed;
		if(msize > 1024L)
		{
			msize /= 1024L;
			if(msize > 1024L)
			{
				msize /= 1024L;
				if(msize > 1024L)
					{
					msize /= 1024L;
					return df.format(msize) + " Gb/" + interval + " s";
					}
				return df.format(msize) + " Mb/" + interval + " s";
			}
			return df.format(msize) + " Kb/" + interval + " s";
		}
		return df.format(msize) + " B/" + interval + " s";
	}
	
	public  static String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				// sb.append(line + "\n");
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
