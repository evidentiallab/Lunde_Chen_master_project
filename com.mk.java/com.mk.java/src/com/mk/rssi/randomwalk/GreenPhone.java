package com.mk.rssi.randomwalk;

public class GreenPhone extends Phone implements PhoneInterface{
	
	public static final int good 		= 0;
	public static final int acceptable 	= 1;
	public static final int bad 		= 2;
	public int curr_perception;
	public int prev_perception;
	
	public GreenPhone()
	{  	  
	    
	}

	public void initPhone()
	{
		this.downloadTimeCountOneSession 	= 0;
	    this.downloadTimeTotal 			= 0L;
	    this.downloadTimeLittleWeak 	= 0L;
	    this.downloadTimeWeak			= 0L;
	    this.timeNotDownloadStrong		= 0L;
	    
	    this.downloading = false;
	    this.curr_perception = getStrengthGreen();
	    this.prev_perception = getStrengthGreen();
	}
	
	public int getStrengthGreen()
	{
		switch (RandomWalkRssi.getInstance().getNowSignalStrength()) {
		case Constants.VERY_STRONG:
			return good;
		case Constants.LITTLE_WEAK:
			return acceptable;
		case Constants.WEAK:
			return bad;
		default:
			return -1;
		}
	}
	
	public int getStrengthGreenReal()
	{
		switch (RandomWalkRssi.getInstance().getNowSignalStrengthReal()) {
		case Constants.VERY_STRONG:
			return good;
		case Constants.LITTLE_WEAK:
			return acceptable;
		case Constants.WEAK:
			return bad;
		default:
			return -1;
		}
	}
	
	public void afterInitOneWalk()
	{
		this.downloading = false;
		this.curr_perception = getStrengthGreen();
	    this.prev_perception = getStrengthGreen();
	    updateCurrPerception();
	    this.energyAndThroughput();
	}
	
	public void afterOneWalk()
	{
		if(RandomWalkRssi.getInstance().flag_next_walk)
		{
			if(downloadTimeCountOneSession != 0)
			{
//				logDownloadTimeCountOneSession(downloadTimeCountOneSession);
				downloadTimeCountOneSession = 0;
			}
		}
//		energyAndThroughput();
	}
	
	@Override
	public void judge() {
		// TODO Auto-generated method stub
		
		if((RandomWalkRssi.getInstance().totalWalkSteps - 2) % Phone.rssiUpdateRateGreen == 0)
		{
			updateCurrPerception();
		}
		adaptDownloadStrategy()	;
		energyAndThroughput();
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
			else  
			{
				this.sizeDownladed += this.throughputWeakWifi;
				this.eneryConsumed += this.energyConstantWifiOpenDownloadWeakWifi;
			}
		}
		else {
			this.eneryConsumed += 5 * this.energyConstantWifiOpenNoDownload;
		}
	}
	
	public void adaptDownloadStrategy()
	{
		if(downloading) // 此处 downloading 其实是 downloading_pre 的意思
		{
			if(curr_perception == bad)
				stopDownload();
			else {
				downloadTimeCountOneSession ++;
				downloadTimeTotal ++;
				
				switch (getStrengthGreenReal()) {
				case good:
					
					break;
				case acceptable:
					downloadTimeLittleWeak ++;
					break;
				case bad:
					downloadTimeWeak ++;
					break;
				default:
					break;
				}
			}
		}
		else{
			if(curr_perception == good)
			{
				startDownload();
			}
			else {
				if(getStrengthGreen() == good)
				{
					timeNotDownloadStrong ++;
				}
			}
		}
	}
	
	public void startDownload()
	{
		downloading = true;
		downloadTimeCountOneSession = 0;
	}
	
	public void stopDownload()
	{
		downloadTimeCountOneSession ++;
		downloadTimeTotal ++;
		downloading = false;
		logDownloadTimeCountOneSession(downloadTimeCountOneSession);
//		downloadTimeCountOneSession = 0;
//		this.eneryConsumed += 4 * this.energyConstantWifiOpenDownloadLittleWeakWifi;
		this.eneryConsumed += 2 * this.energyConstantWifiOpenDownloadLittleWeakWifi;
	}
	
	public void updateCurrPerception()
	{
		switch (prev_perception) {
		case good:
			switch (getStrengthGreen()) {
			case good:
				curr_perception = good;
				break;
			case acceptable:
				curr_perception = acceptable;
				break;
			case bad:
				curr_perception = bad;
				break;
			default:
				break;
			}
			break;
		case acceptable:
			switch (getStrengthGreen()) {
			case good:
				curr_perception = good;
				break;
			case acceptable:
				curr_perception = bad;
				break;
			case bad:
				curr_perception = bad;
				break;
			default:
				break;
			}
			break;
		case bad:
			switch (getStrengthGreen()) {
			case good:
				curr_perception = acceptable;
				break;
			case acceptable:
				curr_perception = bad;
				break;
			case bad:
				curr_perception = bad;
				break;
			default:
				break;
			}
			break;
		default:
			System.out.println("SOMETHING WRONG");
			break;
		}
		prev_perception = curr_perception;
	}
	
	@Override
	public void setLogType() {
		// TODO Auto-generated method stub
		this.logType = "Green";
	}
}
