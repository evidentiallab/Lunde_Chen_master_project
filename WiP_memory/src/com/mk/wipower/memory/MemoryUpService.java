package com.mk.wipower.memory;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class MemoryUpService extends Service{
	
	Handler hanlder_withLooper;
	Runnable runnable;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
    	System.out.println("ServiceOnCreate: MemoryUpService");
    	super.onCreate();
    	
    	hanlder_withLooper = new Handler();
    	runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				System.out.println("runnbale println");
				// Intent intent = new Intent(MemoryUpService.this, MainPage.class);
				Intent intent = new Intent();
				intent.setAction("com.mk.wipower.memory.UP");
				intent.putExtra("up", true);
				sendBroadcast(intent);
				hanlder_withLooper.postDelayed(this, 5 * 1000);
			}
		};
    }
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		hanlder_withLooper.postDelayed(runnable, 5 * 1000);
		
		if(intent.getExtras().getString("what").equalsIgnoreCase("stop"))
			stopSelf();
	}
	
	
	@Override
    public void onDestroy()
	{
		hanlder_withLooper.removeCallbacks(runnable);
		super.onDestroy();
	}
	
}
