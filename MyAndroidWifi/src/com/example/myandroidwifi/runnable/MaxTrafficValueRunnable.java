package com.example.myandroidwifi.runnable;

import android.net.TrafficStats;

import com.example.myandroidwifi.util.AppInfoUtil;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;

public class MaxTrafficValueRunnable implements Runnable{
	
	private long maxRx, maxUidRx;
	private long valueRx, valueUidRx;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int mUid = AppInfoUtil.getMyUid();
		maxRx = 1 * 1024L * 1024L * 1024L;
		maxUidRx = 1 * 1024L * 1024L * 1024L;
		
		while (Constants.runningFlag) 
		{
			valueRx = TrafficStats.getTotalRxBytes();
			valueUidRx = TrafficStats.getUidRxBytes(mUid);
			if(valueRx > maxRx)
			{
				maxRx = valueRx;
				LogUtil.logMaxRx(Constants.getNowTime() + " : " + maxRx);
			}
			if(valueUidRx > maxUidRx)
			{
				maxUidRx = valueUidRx;
				LogUtil.logMaxMUidRx(Constants.getNowTime() + " : " + maxUidRx);
			}
		}
	}
}
