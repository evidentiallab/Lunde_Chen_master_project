package com.mk.wipower.cpu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class CpuService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
    	System.out.println("ServiceOnCreate: MyDownloadService");
    	super.onCreate();
    	
	}
	
	public void onStart(Intent intent, int startId) 
	{
		
		
	}
	
	public void onDestroy()
	{
		 super.onDestroy();
	}
	
	

}
