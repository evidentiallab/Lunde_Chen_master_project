package com.mk.rssi.randomwalk;

public class PrimitivePhone3 extends PrimitivePhone2{
	
	@Override
	public void energyAndThroughput()
	{
		if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(false))
			this.downloading = true;
		else {
			this.downloading = false;
		}
		
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
			if(downloading_pre)
			{
				this.eneryConsumed += 5 * this.energyConstantWifiOpenDownloadLittleWeakWifi;
				////
//				this.sizeDownladed -= this.throughputWeakWifi;
			}
		}
		
		downloading_pre = downloading;
	}

	@Override
	public void setLogType() {
		// TODO Auto-generated method stub
		this.logType = "Primitive3";
	}

}
