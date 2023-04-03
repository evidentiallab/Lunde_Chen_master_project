package com.example.myandroidwifi.runnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;

import android.content.Context;

public class RssiRunnable implements Runnable{
	private Context context;
	private WifiInfoUtil mWifiInfoUtil;
	public HashMap<Integer, Long> map;
	public Runnable logRunnable;
	private Thread logThread;
	public long startTime;
	public String startStr;
	public long fois;
	
	public RssiRunnable(Context contxt)
	{
		this.context = contxt;
		map = new HashMap<Integer, Long>();
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
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_RSSI);
						else {
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_RSSI_LOW_POWER);
						}
						// Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};
	}
	
	
	public void logResult()
	{
		String logStr = "";
		String nowtimeStr = Constants.getNowTime();
		logStr += "from " + startStr + " to " + nowtimeStr + "\n";
		long passedTime = System.currentTimeMillis() - startTime;
	    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
		Set<Integer> set = map.keySet();
		logStr += set.toString() + "\n";
		Iterator<Integer> itorator = set.iterator();
		DecimalFormat df = new DecimalFormat("#.0000");
		 while(itorator.hasNext())
	        {
	        	int mrssi = itorator.next();
	        	Long mfois = map.get(mrssi);
	        	logStr += mrssi +  "   " + mfois + "   " + df.format(100.0000 * mfois / fois) + " %\n";
	        }
		 LogUtil.logRssi(logStr);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		startStr = Constants.getNowTime();
		logThread = new Thread(logRunnable);
		logThread.start();
		fois = 0;
		while(Constants.runningFlag)
		{
			mWifiInfoUtil = new WifiInfoUtil(context, "RssiRunnable");
			int rssi = mWifiInfoUtil.getReceivedSignalStrengthIdicator();
			if(map.keySet().contains(rssi))
				map.put(rssi, map.get(rssi)+1);
			else {
				map.put(rssi, 1L);
			}
			fois ++;
			try {
				Thread.sleep(Constants.SLEEP_INTERVAL_READ_RSSI);
			} catch (Exception e) {
				// TODO: handle exception
				LogUtil.logE("Rssi Runnable : " + e.getMessage());
			}
		}
		//if(!logThread.isInterrupted())
		//	logThread.interrupt();
	}
	
	public void stopLog()
	{
		if(logThread != null && !logThread.isInterrupted())
			logThread.interrupt();
	}
}
