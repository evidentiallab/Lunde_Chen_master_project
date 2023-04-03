package com.example.myandroidwifi.runnable;

import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;
import android.content.Context;

public class WifiRunnable implements Runnable{
	private Context context;
	private Thread logThread;
	public Runnable logRunnable;
	public long startTime;
	public String startStr;
	public WifiInfoUtil mWifiInfoUtil;
	
	public WifiRunnable(Context contxt)
	{
		this.context = contxt;
		this.mWifiInfoUtil = new WifiInfoUtil(context, "WifiRunnable");
		logRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(Constants.runningFlag)
				{
					logResult();
					try {
						if(!Constants.low_power)
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_WIFI);
						else
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_WIFI_LOW_POWER);
					} catch (Exception e) {
						// TODO: handle exception
						LogUtil.logE(e.getMessage());
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
		/*
		while(Constants.runningFlag)
		{
			try {
				Thread.sleep(Constants.SLEEP_INTERVAL_READ_WIFI);
			} catch (Exception e) {
				// TODO: handle exception
				LogUtil.logE(e.getMessage());
			}
		}
		*/
	}
	
	public void logResult()
	{
		mWifiInfoUtil = new WifiInfoUtil(context, "WifiRunnable");
		String logStr = "";
		String nowtimeStr = Constants.getNowTime();
		logStr += "from " + startStr + " to " + nowtimeStr + "\n";
		long passedTime = System.currentTimeMillis() - startTime;
	    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
	    logStr += mWifiInfoUtil.toString();
	    LogUtil.logWifi(logStr);
	}

	public void stopLog()
	{
		if(logThread != null && !logThread.isInterrupted())
			logThread.interrupt();
	}
	
}
