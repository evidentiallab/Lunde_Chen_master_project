package com.mk.wipower.rssioverhead;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainPage  extends Activity {
	
	private Button bttn_start, bttn_stop, bttn_start_series, bttn_whichrate;
	private ToggleButton toggle_bttn;
	private MyReceiver myReceiver;
	private Handler mHandler;
	private Runnable mRunnable, logRssiRunnable, whichRateRunnable;
	private WifiInfoUtil mWifiInfoUtil;
	public boolean flag = false;
	int a = 0;
	public static String logFileName = getNowTime("yyyyMMddHHmm");
	PowerManager.WakeLock wakeLock; 
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("201409032219");
        setContentView(R.layout.main_page);
        
        wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "ScreenPlusWifiPage");
        
        bttn_start       = (Button)  findViewById(R.id.bttn_start)       ;
        bttn_stop        = (Button)  findViewById(R.id.bttn_stop)        ;
        bttn_start_series= (Button)  findViewById(R.id.bttn_start_series);
        bttn_whichrate   = (Button)  findViewById(R.id.bttn_whichrate);
        
        toggle_bttn      = (ToggleButton)findViewById(R.id.toggle_bttn)  ;
        
        myReceiver = new MyReceiver();
        mHandler = new Handler();
        mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
        
        mRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(flag)
				{
//					System.out.println("Rssi: " + mWifiInfoUtil.getReceivedSignalStrengthIdicator());
					mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
					a = mWifiInfoUtil.getReceivedSignalStrengthIdicator();
				}
				else {
					a = 1 + 1;
				}
//				System.out.println("a = " + a);
				System.out.println("WifiInforUtil.a = " + WifiInfoUtil.a);
				mHandler.postDelayed(this, 20);
			}
		};
        
		logRssiRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
				LogUtil.logRssiSeriesFile("" + mWifiInfoUtil.getReceivedSignalStrengthIdicator(), logFileName);
				mHandler.postDelayed(logRssiRunnable, 1000);
			}
		};
		
		whichRateRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
				int b = mWifiInfoUtil.getReceivedSignalStrengthIdicator();
				if(a != b)
				{
					LogUtil.logRssiSeriesFile(getNowTime("HH:mm:ss SSS"), logFileName);
					a = b;
				}
				mHandler.postDelayed(whichRateRunnable, 50);
			}
		};
		
//		bttn_start.setVisibility(View.INVISIBLE);
        bttn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.postDelayed(mRunnable, 1000);
				bttn_start.setVisibility(View.INVISIBLE);
				bttn_start_series.setVisibility(View.INVISIBLE);
				bttn_whichrate.setVisibility(View.INVISIBLE);
			}
		});
        
        bttn_start_series.setVisibility(View.INVISIBLE);
        bttn_start_series.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logFileName = getNowTime("yyyyMMddHHmm") + "_Rssi_Series";
				mHandler.postDelayed(logRssiRunnable, 100);
				bttn_start.setVisibility(View.INVISIBLE);
				bttn_start_series.setVisibility(View.INVISIBLE);
				bttn_whichrate.setVisibility(View.INVISIBLE);
			}
		});
        
        bttn_whichrate.setVisibility(View.INVISIBLE);
        bttn_whichrate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logFileName = getNowTime("yyyyMMddHHmm") + "_which_rate";
				mHandler.postDelayed(whichRateRunnable, 100);
				bttn_start.setVisibility(View.INVISIBLE);
				bttn_start_series.setVisibility(View.INVISIBLE);
				bttn_whichrate.setVisibility(View.INVISIBLE);
			}
        });
        
        bttn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        
        toggle_bttn.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					flag = true;
					System.out.println("True");
				}
				else {
					flag = false;
					System.out.println("False");
				}
			}
        });
	}

	protected void onResume(){
		wakeLock.acquire();
		super.onResume();
	}
	
	
	public void onDestroy()
	{
		if(wakeLock!=null){
			wakeLock.release();
		}
	   mHandler.removeCallbacks(mRunnable);
	   mHandler.removeCallbacks(logRssiRunnable);
	   mHandler.removeCallbacks(whichRateRunnable);
	   super.onDestroy();	
	}
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			}
	}
	
	////////////////////////
	public static String getNowTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: SSS ");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getNowTime(String formatStr)
	{
		DateFormat dateFormat = new SimpleDateFormat(formatStr);
		Date date = new Date();
		return dateFormat.format(date);
	}
	////////////////////////
}

