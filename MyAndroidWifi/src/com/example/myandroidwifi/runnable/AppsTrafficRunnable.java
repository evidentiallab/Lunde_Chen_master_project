package com.example.myandroidwifi.runnable;

import java.io.RandomAccessFile;

import android.content.pm.ApplicationInfo;

public class AppsTrafficRunnable implements Runnable{
	
	private String rcvPath;
	private String sndPath;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void setReadPath(ApplicationInfo appInfo)
	{
		this.rcvPath = "/proc/uid_stat/" + appInfo.uid + "/tcp_rcv";
		this.sndPath = "/proc/uid_stat/" + appInfo.uid + "/tcp_snd";
	}
	
	public long readData(String fileStr)
	{
		long result = -1L;
		try {
			RandomAccessFile rdAcFile = new RandomAccessFile(fileStr, "r");
			result = Long.parseLong(rdAcFile.readLine());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1L;
		}
		return result;
	}
	
	
	
}
