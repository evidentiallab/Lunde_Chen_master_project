package com.mk.wipower.tcpdump;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogUtil {
	
	public static boolean writeLogFlag = true;
	
	public static String dateString="NoDate";
	
	public static String logType = "";
	
	public static boolean isLogMaxRx = true;
	public static boolean isLogBytes = true;
	public static boolean isLogPackets = true;
	//public static boolean
	//public static boolean
	//public static boolean
	//public static boolean
	
	public static void setLogType(String typeString)
	{
		logType = typeString;
	}
	
	public static String getLogPrefix()
	{
		return Utils.ROOT_DIR  + dateString + "_" + logType;
	}
	
	public static void logMaxRx(String str)
	{
		//if()
		writeLog(str, "MaxRx");
	}
	
	public static void logMaxMUidRx(String str)
	{
		//if()
		writeLog(str, "MaxMUidRx");
	}
	
	public static void logE(String str)
	{
		//if()
		writeLog(str, "E");
	}
	
	public static void logV(String str)
	{
		//if()
		writeLog(str, "V");
	}
	
	public static void logCPU(String str)
	{
		//if()
		writeLog(str, "CPU");
	}
	
	public static void logServlet(String str)
	{
		//if()
		writeLog(str, "Servlet");
	}
	
	public static void logBattery(String str)
	{
		//if()
		writeLog(str, "Battery");
	}
	
	public static void logTime_Battery(String str)
	{
		//if()
		writeLog(str, "Time_Battery");
	}
	
	public static void logWifi(String str)
	{
		//if()
		writeLog(str, "Wifi");
	}
	
	public static void logScreen(String str)
	{
		//if()
		writeLog(str, "Screen");
	}
	
	public static void logRssi(String str)
	{
		//if()
		writeLog(str, "Rssi");
	}
	
	public static void logRssiFile(String str, String fileName)
	{
		writeFile(str, fileName);
	}
	
	public static void logMyAppBytesSpeedTx(String str)
	{
		//if()
		writeLog(str, "MyAppBytesSpeedTx");
	}
	
	public static void logMyAppBytesSpeedRx(String str)
	{
		//if()
		writeLog(str, "MyAppBytesSpeedRx");
	}
	
	public static void logMyAppBytesSpeedTotalX(String str)
	{
		//if()
		writeLog(str, "MyAppBytesSpeedTotalX");
	}
	
	public static void logMyAppPacketsSpeedTp(String str)
	{
		//if()
		writeLog(str, "MyAppPacketsSpeedTp");
	}
	
	public static void logMyAppPacketsSpeedRp(String str)
	{
		//if()
		writeLog(str, "MyAppPacketsSpeedRp");
	}
	
	public static void logMyAppPacketsSpeedTotalP(String str)
	{
		//if()
		writeLog(str, "MyAppPacketsSpeedTotalP");
	}
	
	public static void logMyPhoneBytesSpeedTx(String str)
	{
		//if()
		writeLog(str, "MyPhoneBytesSpeedTx");
	}
	
	public static void logMyPhoneBytesSpeedRx(String str)
	{
		//if()
		writeLog(str, "MyPhoneBytesSpeedRx");
	}
	
	public static void logMyPhoneBytesSpeedTotalX(String str)
	{
		//if()
		writeLog(str, "MyPhoneBytesSpeedTotalX");
	}
	
	public static void logMyPhonePacketsSpeedTp(String str)
	{
		//if()
		writeLog(str, "MyPhonePacketsSpeedTp");
	}
	
	public static void logMyPhonePacketsSpeedRp(String str)
	{
		//if()
		writeLog(str, "MyPhonePacketsSpeedRp");
	}
	
	public static void logMyPhonePacketsSpeedTotalP(String str)
	{
		//if()
		writeLog(str, "MyPhonePacketsSpeedTotalP");
	}
	
	public static void logAccumPacketsTp(String str)
	{
		//if()
		writeLog(str, "AccumPacketsTp");
	}
	
	public static void logAccumPacketsRp(String str)
	{
		//if()
		writeLog(str, "AccumPacketsRp");
	}
	
	public static void logAccumPacketsTotalP(String str)
	{
		//if()
		writeLog(str, "AccumPacketsTotalP");
	}
	
	public static void logAccumBytesTx(String str)
	{
		//if()
		writeLog(str, "AccumBytesTx");
	}
	
	public static void logAccumBytesRx(String str)
	{
		//if()
		writeLog(str, "AccumBytesRx");
	}
	
	public static void logAccumBytesTotalX(String str)
	{
		//if()
		writeLog(str, "AccumBytesTotalX");
	}
	
	public static void logTrafficCompare(String str)
	{
		//if()
		writeLog(str, "TrafficCompare");
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
		String fileStr = getLogPrefix() + "_log" + type + ".txt";
		File file = new File(fileStr);
		if(writeLogFlag)
			write(str, file);
	}
	
	public static void writeFile(String str, String fileName)
	{
		File file = new File(Utils.ROOT_DIR  + fileName);
		if(writeLogFlag)
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
