package com.mk.wipower.tcpdump;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;

public class RssiRunnableNouvelle implements Runnable{
	private Context context;
	private WifiInfoUtil mWifiInfoUtil;
	public HashMap<Integer, Long> map;
	public long startTime;
	public String startStr;
	public long fois;
	private String logFileName;
	private boolean running;
	
	public RssiRunnableNouvelle(Context contxt, String fileNme)
	{
		this.context = contxt;
		this.map = new HashMap<Integer, Long>();
		this.logFileName = fileNme;
		this.running = true;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.startTime = System.currentTimeMillis();
		this.startStr = Utils.getNowTime();
		this.fois = 0;
		while(running)
		{
			mWifiInfoUtil = new WifiInfoUtil(context, "RssiRunnableNouvelle");
			int rssi = mWifiInfoUtil.getReceivedSignalStrengthIdicator();
			if(map.keySet().contains(rssi))
				map.put(rssi, map.get(rssi)+1);
			else {
				map.put(rssi, 1L);
			}
			fois ++;
			try {
				Thread.sleep(Utils.SLEEP_INTERVAL_READ_RSSI_NOUVELLE);
			} catch (Exception e) {
				// TODO: handle exception
				LogUtil.logE("Rssi Runnable Nouvelle : " + e.getMessage());
			}
		}
	}
	
	public void logResult()
	{
		String logStr = "";
		String nowtimeStr = Utils.getNowTime();
		logStr += "from " + startStr + " to " + nowtimeStr + "\n";
		long passedTime = System.currentTimeMillis() - startTime;
	    logStr += "passed time : " + Utils.timeMillisToString(passedTime)+ "\n";
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
		 LogUtil.logRssiFile(logStr, logFileName);
	}
	
	public void stop()
	{
		this.running = false;
		logResult();
	}

}
