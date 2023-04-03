package com.mk.wipower.download;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

public class MyDownloadService extends Service{
	
	private Handler hanlder_withLooper;
	private Runnable runnable;
	private DownloadManager downloadmanager;
	public static String url; 
//    private String url = "http://www2.census.gov/census_2010/04-Summary_File_1/Alaska/ak2010.sf1.zip";  //35Mb,较慢
//	private String url = "http://down.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk"; //22.8Mb, 较快
//	public String url = "http://gdown.baidu.com/data/wisegame/ea93eb209c1d5fe9/FunnyYoga_2.apk"; // 3.0Mb
//	private String url = "http://www.scilab.org/download/5.4.1/scilab-5.4.1.bin.linux-i686.tar.gz";
//	private String url = "http://down.m.duoku.com/game/50000/50550/20140812205747_DuoKu.apk";
//	private String url = "http://down.m.duoku.com/game/59000/59195/20140716194413_DuoKu.apk";
//	private String url = "http://imgskype.gmw.cn/software/SkypeSetupFull.exe"; 
//	private String url = "http://www.baidu.com/img/bdlogo.png";
	private MyReceiver myReceiver;

	private Handler mHandler;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void onCreate() {
    	System.out.println("ServiceOnCreate: MyDownloadService");
    	super.onCreate();
    	
    	downloadmanager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
		
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.content.Intent.ACTION_SCREEN_OFF);
        filter.addAction(android.content.Intent.ACTION_SCREEN_ON);
        filter.addAction(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, filter);   
        
        mHandler = new Handler();
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		if(intent == null)
			return;
		
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt("which")) {
		case -10:
			System.out.println("Service created with success!");
			break;
		case 1:
			startDownload(downloadmanager, url);
			break;
		case 2:
			deleteDownload(downloadmanager, url);
			stopSelf();
			break;
		default:
			break;
		}
	}
	
	@Override
    public void onDestroy()
	{
		unregisterReceiver(myReceiver);
		System.out.println("Service onDestroy");
		super.onDestroy();
	}
	
	public long startDownload(DownloadManager manager, String str)
	{
		DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(str));
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, "/");
		request.setTitle(url);
		request.setDescription("download test");
		long id = manager.enqueue(request);
		System.out.println("Start Download, download id: "  + id);
		
		Intent intent = new Intent();
		intent.setAction("com.mk.wipower.downloadmanager.action.startdownload");
		sendBroadcast(intent);
		
		return id;
	}
	
	public boolean deleteDownload(DownloadManager manager, String str)
	{
		DownloadManager.Query query = new DownloadManager.Query();
		Cursor cursor = manager.query(query);
		
		boolean flag = false;
		boolean flag2 = false;
		
		if(cursor.getCount()==0)
			System.out.println("0 in the cursor!");
		else
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
			{
				String urlD = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
				if(str.equalsIgnoreCase(urlD))
				{
					flag = true;
					break;
				}
			}
		
		if(flag)
		{
			long removeId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
			System.out.println("flag == true");
			System.out.println("En effet, .COLUMN_ID==" + DownloadManager.COLUMN_ID);
			cursor.close();
			if(manager.remove(removeId)>0)
				{
					System.out.println("remove " + str + "successfully!");
					flag2 = true;
				}
		}
			return flag2;
	}
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase(android.content.Intent.ACTION_SCREEN_OFF))
			{
				System.out.println("Screen Off received");
//				startDownload(downloadmanager, url);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startDownload(downloadmanager, url);
					}
				},  800); //30不行，8不行，//2可以！
			}
			else if(intent.getAction().equalsIgnoreCase(android.content.Intent.ACTION_SCREEN_ON))
			{
//				System.out.println("Screen On received");
//				deleteDownload(downloadmanager, url);
			}
			else if(intent.getAction().equalsIgnoreCase(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{
//				System.out.println("Download completed");
//				Intent intent2 = new Intent();
//				intent2.setAction("com.mk.wipower.downloadmanager.action.finishdownload");
//				sendBroadcast(intent2);
			}
		}
	}
}
