package com.example.myandroidwifi.service;

import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("NewApi")
public class MyAppSpeedService extends Service{
	    private Timer timer;
	    private TimerTask timerTask;
	    private int mUid;
	    private long fois;

	    @Override
	    public IBinder onBind(Intent intent) {
	        return null;
	    }

	    @Override
	    public void onCreate() {
	    	System.out.println("ServiceOnCreateMappSpeedService");
	    	super.onCreate();
	    }

	    @Override
	    public void onStart(Intent intent, int startId) {
	    	  PackageManager packageManager = this.getPackageManager();
		      ApplicationInfo info = null;
		        try {
		            info = packageManager.getApplicationInfo(this.getPackageName(),
		                    PackageManager.GET_META_DATA);
		            mUid = info.uid;
		            System.out.println("mUid : " + mUid);
		        } catch (NameNotFoundException e) {
		            e.printStackTrace();
		        }

		        fois = 0L;
		        
		        timer = new Timer();
		        timerTask = new TimerTask() {

		            @Override
		            public void run() {

		                getTrafficStats();
		            }
		        };
		        
	        if (timer != null && timerTask != null) {
	            preTx = curTx = TrafficStats.getUidTxBytes(mUid);
	            preRx = curRx = TrafficStats.getUidRxBytes(mUid);
	            preTotalX = curTotalX = (curTx + curRx);
	            
	            preTp = curTp = TrafficStats.getUidTxPackets(mUid);
	            preRp = curRp = TrafficStats.getUidRxPackets(mUid);
	            preTotalP = curTotalP = (curTp + curRp);
	            
	            prePhoneTx = curPhoneTx = TrafficStats.getTotalTxBytes();
	            prePhoneRx = curPhoneRx = TrafficStats.getTotalRxBytes();
	            prePhoneTotalX = curPhoneTotalX = (curPhoneTx + curPhoneRx);
	            
	            prePhoneTp = curPhoneTp = TrafficStats.getTotalTxPackets();
	            prePhoneRp = curPhoneRp = TrafficStats.getTotalRxPackets();
	            prePhoneTotalP = curPhoneTotalP = (curPhoneTp + curPhoneRp);
	            
	            timer.schedule(timerTask, 
	            		Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_MILLIS, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_MILLIS);
	            }
	        // do nothing to avoid multi-start
	    }

	    @Override
	    public void onDestroy() {

	        getTrafficStats(); // last count

	        if (timerTask != null) {
	            timerTask.cancel();
	        }
	        if (timer != null) {
	            timer.cancel();
	            timer.purge();
	        }
	        super.onDestroy();
	    }

	    private long curTx = -1L, curRx = -1L, preTx = -1L, preRx = -1L, preTotalX = -1L, curTotalX = -1L;
	    private long curTp = -1L, curRp = -1L, preTp = -1L, preRp = -1L, preTotalP = -1L, curTotalP = -1L;
	    private long curPhoneTp = -1L, curPhoneRp = -1L, prePhoneTp = -1L, 
	    			 prePhoneRp = -1L, prePhoneTotalP = -1L, curPhoneTotalP = -1L;
	    private long curPhoneTx = -1L, curPhoneRx = -1L, curPhoneTotalX = -1L,
	    		     prePhoneTx = -1L, prePhoneRx = -1L, prePhoneTotalX = -1L;

		@SuppressLint("NewApi")
		private void getTrafficStats() {
			System.out.println("getTrafficStats()");
	        curTx = TrafficStats.getUidTxBytes(mUid);
	        curRx = TrafficStats.getUidRxBytes(mUid);
	        curTotalX = curTx + curRx;
	        
	        curTp = TrafficStats.getUidTxPackets(mUid);
	        curRp = TrafficStats.getUidRxPackets(mUid);
	        curTotalP = curTp + curRp;
	        
	        curPhoneTx = TrafficStats.getTotalTxBytes();
	        curPhoneRx = TrafficStats.getTotalRxBytes();
	        curPhoneTotalX = curPhoneTx + curPhoneRx;
	        
	        curPhoneTp = TrafficStats.getTotalTxPackets();
	        curPhoneRp = TrafficStats.getTotalRxPackets();
	        curPhoneTotalP = curPhoneTp + curPhoneRp;
	        
	        String nowTime = Constants.getNowTime() + "  ";
	        
	        if(curTx != -1L && curRx != -1L && preTx != -1L && preRx != -1L)
	        {
	        	LogUtil.logMyAppBytesSpeedTx(nowTime + 
	        			(curTx - preTx) + "    " + 
	        			Constants.bpsToKbpsMbpsGbps(curTx - preTx, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        	LogUtil.logMyAppBytesSpeedRx(nowTime + 
	        			(curRx - preRx) + "    " + 
	        			Constants.bpsToKbpsMbpsGbps(curRx - preRx, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        	LogUtil.logMyAppBytesSpeedTotalX(nowTime + 
	        			(curTotalX - preTotalX) + "    " + 
	        			Constants.bpsToKbpsMbpsGbps(curTotalX - preTotalX, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        }
	        
	        if(curTp != -1L && curRp != -1L && preTp != -1L && preRp != -1L)
	        {
	        	LogUtil.logMyAppPacketsSpeedTp(nowTime + (curTp - preTp) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s");
	        	LogUtil.logMyAppPacketsSpeedRp(nowTime + (curRp - preRp) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s");
	        	LogUtil.logMyAppPacketsSpeedTotalP(nowTime + (curTotalP - preTotalP) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s");
	        }
	        
	        
	        if(curPhoneTx != -1L && curPhoneRx != -1L && prePhoneRx != -1L && prePhoneTx != -1L)
	        {
	        	LogUtil.logMyPhoneBytesSpeedTx(nowTime + 
	        			(curPhoneTx - prePhoneTx) + "    " +
	        			Constants.bpsToKbpsMbpsGbps(curPhoneTx - prePhoneTx, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        	LogUtil.logMyPhoneBytesSpeedRx(nowTime + 
	        			(curPhoneRx - prePhoneRx) + "    " +  
	        			Constants.bpsToKbpsMbpsGbps(curPhoneRx - prePhoneRx, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        	LogUtil.logMyPhoneBytesSpeedTotalX(nowTime + 
	        			(curPhoneTotalX - prePhoneTotalX) + "    " +
	        			Constants.bpsToKbpsMbpsGbps(curPhoneTotalX - prePhoneTotalX, Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC));
	        }
	        
	        if(curPhoneTp != -1L && curPhoneRp != -1L && prePhoneTp!= -1L && prePhoneRp != -1L)
	        {
	        	LogUtil.logMyPhonePacketsSpeedTp(nowTime + (curPhoneTp - prePhoneTp) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s");
	        	LogUtil.logMyPhonePacketsSpeedRp(nowTime + (curPhoneRp - prePhoneRp) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s");
	        	LogUtil.logMyPhonePacketsSpeedTotalP(nowTime + (curPhoneTotalP - prePhoneTotalP) + 
	        			" packets/" + Constants.TRAFFIC_SPEED_SAMPLING_RATE_IN_SEC + " s"); 
	        }
	        
	        preTx = curTx;
        	preRx = curRx;
        	preTotalX = curTotalX;
        	
        	preTp = curTp;
        	preRp = curRp;
        	preTotalP = curTotalP;
        	
        	prePhoneTx = curPhoneTx;
        	prePhoneRx = curPhoneRx;
        	prePhoneTotalX = curPhoneTotalX;
        	
        	prePhoneTp = curPhoneTp;
        	prePhoneRp = curPhoneRp;
        	prePhoneTotalP = curPhoneTotalP;
        	
        	////////////////
        	fois ++;
        	
        	if(fois % 360L == 0)
        		logTrafficCompareResult();
        	////////////////
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
		
		public void logTrafficCompareResult()
		{
			String str = "\n";
			str += "readData \"/proc/uid_stat/" + mUid + "/tcp_rcv\" :" + "\n";
			str += readData("/proc/uid_stat/" + mUid + "/tcp_rcv") + "\n";
			str += "TrafficStats.getUidRxBytes(mUid) : " + "\n";
			str += TrafficStats.getUidRxBytes(mUid) + "\n";
			str += "TrafficStats.getTotalRxBytes() : " + "\n";
			str += TrafficStats.getTotalRxBytes() + "\n";
			str += "TrafficStats.getUidRxPackets(mUid) : " + "\n";
			str += TrafficStats.getUidRxPackets(mUid) + "\n";
			str += "TrafficStats.getTotalRxPackets() : " + "\n";
			str += TrafficStats.getTotalRxPackets() + "\n";
			str += "readData \"/proc/uid_stat/" + mUid + "/tcp_snd\" :" + "\n";
			str += readData("/proc/uid_stat/" + mUid + "/tcp_snd") + "\n";
			str += "TrafficStats.getUidTxBytes(mUid) : " + "\n";
			str += TrafficStats.getUidTxBytes(mUid) + "\n";
			str += "TrafficStats.getTotalTxBytes() : " + "\n";
			str += TrafficStats.getTotalTxBytes() + "\n";
			str += "TrafficStats.getUidTxPackets(mUid) : " + "\n";
			str += TrafficStats.getUidTxPackets(mUid) + "\n";
			str += "TrafficStats.getTotalTxPackets() : " + "\n";
			str += TrafficStats.getTotalTxPackets() + "\n";

			//LogUtil.logTrafficCompare(str);
		}
}
