package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.BatteryLogRunnable;
import com.example.myandroidwifi.runnable.CPURunnable;
import com.example.myandroidwifi.runnable.MaxTrafficValueRunnable;
import com.example.myandroidwifi.runnable.RssiRunnable;
import com.example.myandroidwifi.runnable.ServletDoPostRunnable;
import com.example.myandroidwifi.runnable.PhoneTrafficeRunnable;
import com.example.myandroidwifi.runnable.WifiRunnable;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.ScreenUtil;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenPlusWifiPage extends Activity{
	PowerManager.WakeLock wakeLock; 
	WifiLock wifiLock;
	WifiManager wifiManager;
	private Button button_screenpluswifi;
	private TextView textV_screenpluswifi;
	private EditText editV_screenpluswifi;
	public Thread mThread_servlet;
	public Thread mThread_battery;
	public Thread mThread_CPU;
	public Thread mThread_wifi;
	public Thread mThread_rssi;
	public Thread mThread_traffic;
	// public Thread mThread_maxTrafficValue;
	
	public ServletDoPostRunnable mRunnable_servlet;
	public BatteryLogRunnable mRunnable_battery;
	public CPURunnable mRunnable_CPU;
	public WifiRunnable mRunnable_wifi;
	public RssiRunnable mRunnable_rssi;
	public PhoneTrafficeRunnable mRunnable_traffic;
	
	public ScreenUtil screenUtil;
	public Thread startThread;
	public Handler handler;
	public Runnable startRunnable;
	public Runnable logScreenRunnable;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		findViewsById();
		editV_screenpluswifi.setVisibility(View.GONE);
		textV_screenpluswifi.setVisibility(View.GONE);
		
		this.wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "ScreenPlusWifiPage");
        wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "ScreenPlusWifiPage");
        
        mRunnable_servlet = new ServletDoPostRunnable();
        mRunnable_battery = new BatteryLogRunnable();
        mRunnable_CPU = new CPURunnable();
        mRunnable_wifi = new WifiRunnable(ScreenPlusWifiPage.this);
        mRunnable_rssi = new RssiRunnable(ScreenPlusWifiPage.this);
        mRunnable_traffic = new PhoneTrafficeRunnable();
        
		mThread_servlet = new Thread(mRunnable_servlet);
		mThread_battery = new Thread(mRunnable_battery);
		mThread_CPU = new Thread(mRunnable_CPU);
		mThread_wifi = new Thread(mRunnable_wifi);
		mThread_rssi = new Thread(mRunnable_rssi);
		mThread_traffic = new Thread(mRunnable_traffic);
		//mThread_maxTrafficValue = new Thread(new MaxTrafficValueRunnable());

		screenUtil = new ScreenUtil(ScreenPlusWifiPage.this);
		
		
		
		handler = new Handler();
		logScreenRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				screenUtil = new ScreenUtil(ScreenPlusWifiPage.this);
				LogUtil.logScreen(screenUtil.getScreenResult());
				handler.postDelayed(logScreenRunnable, Constants.low_power? 
						Constants.SLEEP_INTERVAL_LOG_SCREEN_LOW_POWER : Constants.SLEEP_INTERVAL_LOG_SCREEN);
			}
		};
		
		if(LogUtil.writeLogFlag == false)
			popAlertWriteLog();
		
		if(Constants.brightness == 0)
			popAlertBrightness();
		
		if(!Constants.runningFlag)
			button_screenpluswifi.setText("Start");
		else {
			button_screenpluswifi.setText("Stop");
		}
		
		startRunnable = new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				LogUtil.setLogType("ScreenPlusWifi");
		 		LogUtil.resetDateString();
		 		Constants.runningFlag = true;
		 		Constants.low_power_threhold = 5;
				String explication = 
						Constants.getNowTime() + "\nresult for the experiment :\n" +
						"length_of_string_sent " + Constants.length_of_string_sent +
						"\nlength_of_string_received " + Constants.length_of_string_received + 
						"\ninterval_sleep_send_packets " + Constants.SLEEP_INTERVAL_SEND_PACKETS + 
						"\nscreen brightness " + Constants.brightness + 
						"\n";
		 		LogUtil.logWifi(explication);
		 		LogUtil.logServlet(explication);
		 		LogUtil.logCPU(explication);
		 		LogUtil.logBattery(explication);
		 		LogUtil.logTime_Battery(explication);
		 		LogUtil.logScreen(explication);
		 		LogUtil.logAccumBytesRx(explication);
		 		LogUtil.logAccumBytesTotalX(explication);
		 		LogUtil.logAccumBytesTx(explication);
		 		LogUtil.logAccumPacketsRp(explication);
		 		LogUtil.logAccumPacketsTotalP(explication);
		 		LogUtil.logAccumPacketsTp(explication);
		 		LogUtil.logMyAppBytesSpeedRx(explication);
		 		LogUtil.logMyAppBytesSpeedTotalX(explication);
		 		LogUtil.logMyAppBytesSpeedTx(explication);
		 		LogUtil.logMyAppPacketsSpeedRp(explication);
		 		LogUtil.logMyAppPacketsSpeedTotalP(explication);
		 		LogUtil.logMyAppPacketsSpeedTp(explication);
		 		LogUtil.logMyPhoneBytesSpeedRx(explication);
		 		LogUtil.logMyPhoneBytesSpeedTotalX(explication);
		 		LogUtil.logMyPhoneBytesSpeedTx(explication);
		 		LogUtil.logMyPhonePacketsSpeedRp(explication);
		 		LogUtil.logMyPhonePacketsSpeedTotalP(explication);
		 		LogUtil.logMyPhonePacketsSpeedTp(explication);
		 		LogUtil.logTrafficCompare(explication);
				LogUtil.logScreen(screenUtil.getScreenResult());
				//LogUtil.logMaxRx(explication);
				//LogUtil.logMaxMUidRx(explication);
				Constants.executor.execute(mThread_servlet);
				Constants.executor.execute(mThread_battery);
				Constants.executor.execute(mThread_CPU);
				Constants.executor.execute(mThread_wifi);
				Constants.executor.execute(mThread_rssi);
				Constants.executor.execute(mThread_traffic);
				//Constants.executor.execute(mThread_maxTrafficValue);
			}
		};
		
		button_screenpluswifi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!Constants.runningFlag)
					{
					start();
					}
				else
				{
					stop();
				}
			}
		});
	}
	
	protected void onResume(){
		wakeLock.acquire();
		wifiLock.acquire();
		super.onResume();
	}
	
	protected void onDestroy(){
		if(wakeLock!=null){
			wakeLock.release();
		}
		// wifiLock.release();
		handler.removeCallbacks(logScreenRunnable);
		super.onDestroy();
	}
	
	protected void onPause()
	{
		/*
		if(mFlag)
		{
		Intent myIntent = new Intent(ScreenPlusWifiPage.this, ScreenPlusWifiPage.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(myIntent);
		}
		*/
        super.onPause();
	}

	public void findViewsById()
	{
		setContentView(R.layout.screen_plus_wifi_page);
		button_screenpluswifi = (Button)findViewById(R.id.button_screenpluswifi);
		textV_screenpluswifi = (TextView)findViewById(R.id.textV_screenpluswifi);
		editV_screenpluswifi = (EditText)findViewById(R.id.editV_screenpluswifi);
	}
	
	public void popAlertBrightness()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ScreenPlusWifiPage.this);
		alertBuilder.setMessage("You choose to set the brightness to 0, which" +
				" means that you cannot use the phone until the battery is drilled off. Take care...");
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle("Take care!");
		alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alertBuilder.show();
	}
	
	public void popAlertWriteLog()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ScreenPlusWifiPage.this);
		alertBuilder.setMessage("You set WriteLog? to 0, no log will be written. Take care...");
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle("Take care!");
		alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alertBuilder.show();
	}
	
	public void start()
	{	
		startThread = new Thread(startRunnable);
		startThread.start();
		if(LogUtil.writeLogFlag == false)
			Toast.makeText(getApplicationContext(), "Attetion: you choose no log mode", Toast.LENGTH_LONG).show();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		screenUtil.resetBrightness();
		handler.postDelayed(logScreenRunnable, 1000);
		button_screenpluswifi.setText("Stop");
		Intent intent = new Intent("com.example.myandroidwifi.service.MyAppSpeedService");
		startService(intent);
	}
	
	public void stop()
	{
		if(handler!=null)
			handler.removeCallbacks(logScreenRunnable);
		Constants.runningFlag = false;
		button_screenpluswifi.setText("Start");
		Intent intent = new Intent("com.example.myandroidwifi.service.MyAppSpeedService");
		stopService(intent);
		
		if(mRunnable_battery!=null)
			mRunnable_battery.stopLog();
		if(mRunnable_servlet!=null)
			mRunnable_servlet.stopLog();
		if(mRunnable_rssi!=null)
			mRunnable_rssi.stopLog();
		if(mRunnable_traffic!=null)
			mRunnable_traffic.stopLog();
		if(mRunnable_wifi!=null)
			mRunnable_wifi.stopLog();
		if(mRunnable_CPU!=null)
			mRunnable_CPU.stopLog();
		
		mThread_servlet.interrupt();
		mThread_battery.interrupt();
		mThread_CPU.interrupt();
		mThread_wifi.interrupt();
		mThread_rssi.interrupt();
		mThread_traffic.interrupt();
		//mThread_maxTrafficValue.interrupt();
		finish();
	}
	
	
}
