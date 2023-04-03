package com.example.myandroidwifi.runnable;

import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.TrafficStatsUtil;

public class PhoneTrafficeRunnable implements Runnable{
	public Runnable logRunnable;
	private Thread logThread;
	public long startTime;
	public String startStr;
	public long fois4G_Tx, fois4G_Rx, fois4G_TxRx;
	public long preTx, preRx, preTxRx;
	public long fourG;
	
	
	public PhoneTrafficeRunnable()
	{
		
		fois4G_Tx = fois4G_Rx = fois4G_TxRx = 0L;
		preTx = preRx = preTxRx = -1L;
		fourG = Integer.MAX_VALUE * 2L + 1L;
		
		logRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(Constants.runningFlag)
				{
					String logStr = "\n";
					String nowtimeStr = Constants.getNowTime();
					logStr += "from " + startStr + " to " + nowtimeStr + "\n";
					long passedTime = System.currentTimeMillis() - startTime;
					long passedTimeSec = passedTime/1000L;
					if(passedTimeSec == 0)
						passedTimeSec = 1;
				    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
				    
				    long Tx = TrafficStatsUtil.getBytesTx();
				    if(Tx < preTx)
				    	fois4G_Tx ++;
				    preTx = Tx;
					LogUtil.logAccumBytesTx(logStr + (fois4G_Tx * fourG + Tx) 
							+ "   " +  Constants.bytesToKbMbGb(fois4G_Tx * fourG + Tx)
							+ "\n every second : " + (double) (fois4G_Tx * fourG + Tx)/passedTimeSec + " bytes"
							);
					
					long Rx = TrafficStatsUtil.getBytesRx();
					if(Rx < preRx)
						fois4G_Rx ++;
					preRx = Rx;
					LogUtil.logAccumBytesRx(logStr + (fois4G_Rx * fourG + Rx)
							+ "   " + Constants.bytesToKbMbGb(fois4G_Rx * fourG + Rx)
							+ "\n every second : " + (double) (fois4G_Rx * fourG + Rx)/passedTimeSec + " bytes"
							);
					
					long TxRx = TrafficStatsUtil.getBytes();
					if(TxRx < preTxRx)
						fois4G_TxRx ++;
					preTxRx = TxRx;
					LogUtil.logAccumBytesTotalX(logStr + (fois4G_TxRx * fourG + TxRx)
							+ "    " + Constants.bytesToKbMbGb(fois4G_TxRx * fourG + TxRx)
							+ "\n every second : " + (double) (fois4G_TxRx * fourG + TxRx)/passedTimeSec + " bytes"
							); 
					
					long Tp = TrafficStatsUtil.getPacketsTp();
					long Rp = TrafficStatsUtil.getPacketsRp();
					long TpRp = TrafficStatsUtil.getPackets();
					
					LogUtil.logAccumPacketsTp(logStr + Tp + "\n every second : " + (double) Tp / passedTimeSec + " packets");
					LogUtil.logAccumPacketsRp(logStr + Rp + "\n every second : " + (double) Rp / passedTimeSec + " packets"); 
					LogUtil.logAccumPacketsTotalP(logStr + TpRp + "\n every second : " + (double) TpRp / passedTimeSec + " packets");
					
					try {
						if(!Constants.low_power)
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_ACCUTUMED_TRAFFIC);
						else {
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_ACCUTUMED_TRAFFIC_LOW_POWER);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};
	}
	
	public void run() {
		// TODO Auto-generated method stub
		TrafficStatsUtil.init();
		startTime = System.currentTimeMillis();
		startStr = Constants.getNowTime();
		Thread logThread = new Thread(logRunnable);
		logThread.start();
	}
	
	public void stopLog()
	{
		if(logThread != null && !logThread.isInterrupted())
			logThread.interrupt();
	}

}
