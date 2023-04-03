package com.mk.rssi.randomwalk;

import java.io.File;

public class Phone implements PhoneInterface{
	public String logType;
	
    public int time_curr;
    public int time_prev;
    
    public int quality_prev;
    public int quality_curr;
    
    public String quality_prev_String;
    public String quality_curr_String;
    
//    public int count_time_download_in_bad_wifi;
//    public int count_time_not_download_in_good_wifi_with_good_session;
    
//    public double  battery_now;
//    public double  numFilesDownloaded;
//    public double  sizeDownladedCurrentFile;
    
    
   
    // Green
	public int 	downloadTimeCountOneSession;
	public long downloadTimeTotal;
	public long downloadTimeLittleWeak;
	public long downloadTimeWeak;
	public long timeNotDownloadStrong;
	public static int 	rssiUpdateRateGreen = 2; // 3s
	// Green
    
//    public final boolean backServiceRunning 						= false;
//    public final double  battery_total 							= 0;
//    public final double energyConsumedOneBackServiceGoodWifi 		= 0;
//    public final double energyConsumedOneBackServiceBadWifi 		= 0;
//    public final double energyConsumedOneOpenWifi 				= 0;
//    public final double energyConsumedOneCloseWifi 				= 0;
//    public final double energyConstantKMACalculaton 				= 0;
//  public final double energyConstantWifiClosed 					= 0;
//  public final double sizeOneFile 								= 0;
	
    public long  		sizeDownladed;
//    public long 	   	runningTime;
    public long 	   	eneryConsumed;
    public boolean 		downloading;
    public boolean 		downloading_pre = false;
 
	
    public final long 	energyConstantWifiOpenNoDownload 				= 0			  	; //
    public final long 	energyConstantWifiOpenDownloadStrongWifi 		= 5 			; // 5 / 5 * 244.3mA * 3.7V = ? mJ
    public final long 	energyConstantWifiOpenDownloadLittleWeakWifi 	= 4 			; // 4 / 5 * 244.3mA * 3.7V = ? mJ
    public final long 	energyConstantWifiOpenDownloadWeakWifi 			= 4 			; // 4 / 5 * 244.3mA * 3.7V = ? mJ
    public final long 	throughputStrongWifi 							= 5			  	; // 5 / 5 * 1Mb/s
    public final long 	throughputLittleWeakWifi 						= 2		  		; // 2 / 5 * 1Mb/s
    public final long 	throughputWeakWifi 								= 1	 			; // 1 / 5 * 1Mb/s
    
    public Phone()
    {
    	this.eneryConsumed = 0;
    	this.sizeDownladed = 0;
    	setLogType();
    }
    
    public void logSessionsOfStrength(String str)
    {
    	Constants.write(str, new File(Constants.ROOT_DIR + logType + "_" + quality_prev_String));
    }
    
    public void logDownloadTimeCountOneSession(int timeCount)
    {
    	Constants.write("" + timeCount, new File(Constants.ROOT_DIR + logType + "_TimeCountOneSession"));
    }
    
    public void logBatteryNow()
    {
    	
    }
    
    public void logNumFileDownloaded()
    {
    	
    }
    
	@Override
	public void judge() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setLogType() {
		// TODO Auto-generated method stub
		
	}
	
	public void logEnergyAndThroughPut()
	{
		Constants.write("eneryConsumed 		" 		+ eneryConsumed, 		new File(Constants.ROOT_DIR + logType + "_Statistics"));
		Constants.write("sizeDownladed 		" 		+ sizeDownladed, 		new File(Constants.ROOT_DIR + logType + "_Statistics"));
		
	}
	
	public void logStatistics()
	{
		Constants.write("totalSteps 			" 		+ RandomWalkRssi.getInstance().totalWalkSteps,
																					new File(Constants.ROOT_DIR + logType + "_Statistics"));
		Constants.write("downloadTimeTotal 		" 		+ downloadTimeTotal, 		new File(Constants.ROOT_DIR + logType + "_Statistics"));
		Constants.write("downloadTimeLittleWeak " 		+ downloadTimeLittleWeak, 	new File(Constants.ROOT_DIR + logType + "_Statistics"));
		Constants.write("downloadTimeWeak 		" 		+ downloadTimeWeak, 		new File(Constants.ROOT_DIR + logType + "_Statistics"));
		Constants.write("timeNotDownloadStrong 	" 		+ timeNotDownloadStrong, 	new File(Constants.ROOT_DIR + logType + "_Statistics"));
		this.logEnergyAndThroughPut();
	}
}
