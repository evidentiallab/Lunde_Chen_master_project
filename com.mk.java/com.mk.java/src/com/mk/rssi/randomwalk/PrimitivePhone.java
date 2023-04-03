package com.mk.rssi.randomwalk;

import java.io.File;

public class PrimitivePhone extends Phone implements PhoneInterface{
	
	public long timeInStrongTotal;
	public long timeInLittleWeakTotal;
	public long timeInWeakTotal;
	
	public void initPhone()
	{
		this.time_prev = -1;
	  	this.time_curr = -2; // because we judge one time in the initiation
	  	this.quality_prev = -1;
	  	this.quality_curr = -2;
	  	this.quality_prev_String = "UNKNOWN";
	  	this.quality_curr_String = "UNKNOWN";
	  	
	    this.timeInStrongTotal 			= 0L;
	    this.timeInLittleWeakTotal 		= 0L;
	    this.timeInWeakTotal			= 0L;
	    this.downloading = true;
	}
	
	public void afterInitOneWalk()
	{
		judge();
		time_curr = 0;
		time_prev = 0;
		
		quality_prev = quality_curr;
    	quality_prev_String = quality_curr_String;
	}
	
    public void judge()
    {
  	  time_curr ++;
  	  if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(false))
  	  {
  		  quality_curr 			= Constants.VERY_STRONG;
  		  quality_curr_String 	= Constants.STR_VERY_STRONG;
  		  
  		  timeInStrongTotal ++;
  	  }
  	  else if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(false))
  	  {
  		  quality_curr 			= Constants.LITTLE_WEAK;
  		  quality_curr_String 	= Constants.STR_LITTLE_WEAK;
  		  
  		  timeInLittleWeakTotal ++;
  	  }
  	  else {
  		  quality_curr 			= Constants.WEAK;
  		  quality_curr_String 	= Constants.STR_WEAK;
  		  
  		  timeInWeakTotal ++;
  	  }
  	  
  	  if (time_curr <= 0) {

  	  }
  	  else if(quality_prev == quality_curr)
  	  {
  		  
  	  }
  	  else 
  	  {
  		  logInfo();
  	  }
  	  this.energyAndThroughput();
    }
    
    public void energyAndThroughput()
	{
		if (this.downloading) {
			if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(true))
			{
				this.sizeDownladed += this.throughputStrongWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadStrongWifi;
			}
			else if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(true))
			{
				this.sizeDownladed += this.throughputLittleWeakWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadLittleWeakWifi;
			}
			else if(RandomWalkRssi.getInstance().isIn_rayon_all(true))
			{
				this.sizeDownladed += this.throughputWeakWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadWeakWifi;
			}
		}
		else {
			this.eneryConsumed += this.energyConstantWifiOpenNoDownload;
		}
	}
    
    public void afterOneWalk()
    {
    	logInfo();
    	if(RandomWalkRssi.getInstance().flag_next_walk)
		  {
	    	  this.time_prev = -1;
	    	  this.time_curr = -2; // because we judge one time in the initiation
	    	  this.quality_prev = -1;
	    	  this.quality_curr = -2;
	    	  this.quality_prev_String = "UNKNOWN";
	    	  this.quality_curr_String = "UNKNOWN";
//	    	  RandomWalkRssi.getInstance().flag_next_walk = false; // ..
		  }
    	
//    	energyAndThroughput();
    }
    
    public void logInfo()
    {
  	  	  System.out.println(String.format("%-10s", quality_prev_String)+ " finished, duration = " + (time_curr - time_prev) 
//  			  + " seconds"
//  			  	+ "\nnext: \n" + quality_curr_String
  			  	);
  	  
  	  	  logSessionsOfStrength("" + (time_curr - time_prev));  	  
		  
		  quality_prev = quality_curr;
		  quality_prev_String = quality_curr_String;
		  time_prev = time_curr;
    }

	@Override
	public void setLogType() {
		// TODO Auto-generated method stub
		this.logType = "Primitive4";
	}
	
	public void logStatistics()
	{
//		Constants.write("totalSteps 			" 	+ RandomWalkRssi.getInstance().totalWalkSteps,
//																				new File(Constants.ROOT_DIR + logType + "_Statistics"));
//		Constants.write("timeInStrongTotal 		" 	+ timeInStrongTotal, 		new File(Constants.ROOT_DIR + logType + "_Statistics"));
//		Constants.write("timeInLittleWeakTotal 	" 	+ timeInLittleWeakTotal,	new File(Constants.ROOT_DIR + logType + "_Statistics"));
//		Constants.write("timeInWeakTotal 		" 	+ timeInWeakTotal, 			new File(Constants.ROOT_DIR + logType + "_Statistics"));
//		this.logEnergyAndThroughPut();
		super.logStatistics();
	}

}
