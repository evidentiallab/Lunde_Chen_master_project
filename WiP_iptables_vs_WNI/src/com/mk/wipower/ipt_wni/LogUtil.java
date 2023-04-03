package com.mk.wipower.ipt_wni;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogUtil {
	
	public static String ROOT_DIR  = "/mnt/sdcard/MyAndroidWifi/";
	
	public static String dateString="NoDate";
	
	public static String logType = "";
	
	public static void setLogType(String typeString)
	{
		logType = typeString;
	}
	
	public static String getLogPrefix()
	{
		return ROOT_DIR  + dateString + "_" + logType;
	}
	
	public static void logWifi(String str)
	{
		//if()
		writeLog(str, "Wifi");
	}
	
	public static void logRssiFile(String str, String fileName)
	{
		writeFile(str, fileName);
	}
	
	public static void logTcpdumpFile(String str, String fileName)
	{
		writeFile(str, fileName);
	}
	
	public static void resetDateString()
	{
		DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmm");
		Date date = new Date();
		dateString = dateFormat2.format(date);
	}
	 
	public static void writeLog(String str, String type)
	{
		String fileStr = getLogPrefix() + "_log_" + type + ".txt";
		File file = new File(fileStr);
//		if(writeLogFlag)
			write(str, file);
	}
	
	public static void writeFile(String str, String fileName)
	{
		File file = new File(ROOT_DIR  + fileName);
//		if(writeLogFlag)
			write(str, file);
	}
	
	public static void write(String str, File file) {
	try {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fileWriter = new FileWriter(file, true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println(str);
		printWriter.flush();
		fileWriter.flush();
		printWriter.close();
		fileWriter.close();
		} catch (IOException e) {
		}
	}
	
}
