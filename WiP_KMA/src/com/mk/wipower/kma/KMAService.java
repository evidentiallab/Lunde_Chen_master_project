package com.mk.wipower.kma;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

import weka.estimators.KernelEstimator;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class KMAService extends Service{
	
	Handler hanlder_withLooper;
	Runnable runnable;
	public static KernelEstimator ke = null;

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
				go();
				System.out.println("FUCK");
				hanlder_withLooper.postDelayed(this, 1000);
			}
		};
    }
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		if(ke == null)
		{
			ke = new KernelEstimator(0.5);
		}
		hanlder_withLooper.postDelayed(runnable, 1 * 1000);
		
		if(intent.getExtras().getString("what").equalsIgnoreCase("stop"))
			stopSelf();
	}
	
	public void go()
	{
    	//使之与KMA一样的CPU消耗
    	int i = 0;
    	for (int j = 0; j < 50; j++) 
		{
				i += Math.log10(Math.PI)+j;
				i += Math.pow(Math.PI, Math.E) / j;
				i = 1;
		}
    	System.out.println("go");
	}

	
	
	@Override
    public void onDestroy()
	{
		hanlder_withLooper.removeCallbacks(runnable);
		super.onDestroy();
	}
	
}
