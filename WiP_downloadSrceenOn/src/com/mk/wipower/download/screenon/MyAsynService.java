package com.mk.wipower.download.screenon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

public class MyAsynService extends Service{

	private Handler mHandler;
	private Runnable mRunnable2;
	private MyAsynTaskWeb task_web_1, task_web_2, task_web_3, task_web_4;
	private int count2 = 0, count1 = -1;
	private MyReceiver myReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		System.out.println("ServiceOnCreate: MyAsyncService");
		super.onCreate();
	}
	
	public void onStart(Intent intent, int startId) {
		
		switch (intent.getExtras().getInt("action")) {
		case 1:
			System.out.println("Service created with success!");
			myReceiver = new MyReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(android.content.Intent.ACTION_SCREEN_OFF);
	        registerReceiver(myReceiver, filter); 
			break;
		case 2:
			stopSelf();
			break;
		default:
			break;
		}
	}
	
	public void onDestroy()
	{
		task_web_1.cancel(true);
		task_web_2.cancel(true);
		task_web_3.cancel(true);
		task_web_4.cancel(true);
		
		mHandler.removeCallbacks(mRunnable2);
		mHandler = null;
		
		unregisterReceiver(myReceiver);
		
		super.onDestroy();
	}
	
	public void asynWork()
	{
		try
		{
		if(count2 % 6 == 1)
		{
//			System.out.println("AsyncTask ... ");
			task_web_1 = new MyAsynTaskWeb();
        	task_web_1.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(1)});
		}
		
		if(count2 % 6 == 2)
		{
//			System.out.println("AsyncTask ... ");
			task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(2)});
		}
		
        if(count2 % 6 == 3)
        {
//        	System.out.println("AsyncTask ... ");
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(2)});
        	
        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(3)});
        }
        if(count2 % 6 == 4)
        {
//        	System.out.println("AsyncTask ... ");
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(2)});

        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(3)});
        	
        	task_web_4 = new MyAsynTaskWeb();
        	task_web_4.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(4)});
        }
        if(count2 % 6 == 5)
        {
        	task_web_1 = new MyAsynTaskWeb();
        	task_web_1.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(1)});
        	
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(2)});

        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(3)});
        	
        	task_web_4 = new MyAsynTaskWeb();
        	task_web_4.executeOnExecutor(MainPage.executor, new Integer[]{Integer.valueOf(4)});
        }
		}
		catch(Exception ex){}
	}
	
	private class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase(android.content.Intent.ACTION_SCREEN_OFF))
			{
				mHandler = new Handler();
				mRunnable2 = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						count1 ++;
						System.out.println("Hello world!");
						int j = 1;
						for(int i = 0; i < 10000; i ++)
						{
							j += i;
						}
						System.out.println(j + "");
						if(count1 % 1500 == 0)
						{
							count2 ++;
							asynWork();
						}
						mHandler.postDelayed(this, 20); // 30 * 1000
					}
				};
				mHandler.postDelayed(mRunnable2, 100);
			}
		}
	}
}
