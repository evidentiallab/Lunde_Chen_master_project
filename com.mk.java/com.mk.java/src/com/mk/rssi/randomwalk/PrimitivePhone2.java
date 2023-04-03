package com.mk.rssi.randomwalk;

public class PrimitivePhone2 extends PrimitivePhone{

	@Override
	public void initPhone()
	{
		this.downloadTimeCountOneSession 	= 0;
	    this.downloadTimeTotal 			= 0L;
	    this.downloadTimeLittleWeak 	= 0L;
	    this.downloadTimeWeak			= 0L;
	    this.timeNotDownloadStrong		= 0L;
	    
		this.downloading = false;
	}
	
	
	@Override
	public void energyAndThroughput()
	{
		if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(false))
			this.downloading = true;
		else {
			this.downloading = false;
		}
		
		if (this.downloading) {
			this.downloadTimeTotal ++;
			if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(true))
			{
				this.sizeDownladed += this.throughputStrongWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadStrongWifi;
			}
			else if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(true))
			{
				this.sizeDownladed += this.throughputLittleWeakWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadLittleWeakWifi;
				this.downloadTimeLittleWeak ++;
			}
			else if(RandomWalkRssi.getInstance().isIn_rayon_all(true))
			{
				this.sizeDownladed += this.throughputWeakWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadWeakWifi;
				this.downloadTimeWeak ++;
			}
		}
		else {
			this.eneryConsumed += this.energyConstantWifiOpenNoDownload;
			if(downloading_pre)
			{
				this.eneryConsumed += 5 * this.energyConstantWifiOpenDownloadLittleWeakWifi;
				////
//				this.sizeDownladed -= this.throughputLittleWeakWifi;
			}
			
			if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(true))
			{
				this.timeNotDownloadStrong ++;
			}
			
		}
		
		downloading_pre = downloading;
	}

	@Override
	public void judge()
	{
		this.energyAndThroughput();
	}
	
	@Override
	public void afterInitOneWalk()
	{
		this.energyAndThroughput();
	}
	
	@Override
	public void afterOneWalk()
    {
//		this.energyAndThroughput(); 
    }
	
	@Override
	public void setLogType() {
		// TODO Auto-generated method stub
		this.logType = "Primitive2";
	}
	
	@Override
	public void logStatistics()
	{
//		this.logEnergyAndThroughPut();
		super.logStatistics();
	}
	
	@Override
	public void logInfo()
    {
		// Do Nothing
    }
}
