package com.example.myandroidwifi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Constants {
	public static String SERVER_ADDRESS = "192.168.1.100";
	public static String URL;
	public static String URL_WIFI;
	public static String URL_HELLO; 
	public static String URL_REGULATION;
	public final static String ROOT_DIR = "/mnt/sdcard/MyAndroidWifi/";
	public final static int SLEEP_INTERVAL_LOG_PACKETS = 60 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_PACKETS_LOW_POWER = 60 * 60 * 1000;
	public final static int SLEEP_INTERVAL_READ_AND_LOG_BATTERY = 1 * 1000;
	public final static int SLEEP_INTERVAL_READ_CPU = 50;
	public final static int SLEEP_INTERVAL_LOG_CPU = 120 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_CPU_LOW_POWER = 120 * 60 * 1000;
	public final static int SLEEP_INTERVAL_READ_WIFI = 240 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_WIFI = 240 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_WIFI_LOW_POWER = 240 * 60 * 1000;
	public static int SLEEP_INTERVAL_READ_RSSI = 200;
	public static int SLEEP_INTERVAL_READ_RSSI_NOUVELLE = 200;
	public final static int SLEEP_INTERVAL_READ_RSSI_NORMAL = 500;
	public final static int SLEEP_INTERVAL_READ_RSSI_SPECIAL = 0;
	public final static int SLEEP_INTERVAL_RSSI_CONNECTION = 4 * 1000;
	public final static int SLEEP_INTERVAL_LOG_RSSI = 2 * 60 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_RSSI_LOW_POWER = 120 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_SCREEN = 360 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_SCREEN_LOW_POWER = 360 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_ACCUTUMED_TRAFFIC = 10 * 60 * 1000;
	public final static int SLEEP_INTERVAL_LOG_ACCUTUMED_TRAFFIC_LOW_POWER = 10 * 60 * 1000;
 	public static final int TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC = 1000;
 	public static final int TRAFFIC_SPEED_SAMPLING_RATE_IN_MILLIS = TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC * 1000;
 	public static final int CHANGE_TO_WIFI_BACK_UP_THREHOLD = 5000000;
	public static int SLEEP_INTERVAL_SEND_PACKETS = 1;
	public static int low_power_threhold = 5;
	public static boolean low_power = false;
	public static int length_of_string_received = 1;
	public static int length_of_string_sent = 1;
	public static int brightness = 1;
	public static boolean runningFlag = false;
	public static ExecutorService executor = Executors.newCachedThreadPool();

	public static void updateURL()
	{
		System.out.println(SERVER_ADDRESS);
		URL = "http://" + SERVER_ADDRESS + ":8080/HelloMyWifi/";
		// public final static String URL = "http://192.168.1.100:8080/HelloMyWifi/";
		URL_WIFI = URL + "wifi";
		URL_HELLO = URL + "hello"; 
		URL_REGULATION = URL + "regulation";
	}
	
	public static boolean createPath()
	{
		File file = null;
		file = new File(Constants.ROOT_DIR);
		if(!file.exists())
			if(!file.mkdir())
				return false;
		return true;
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
