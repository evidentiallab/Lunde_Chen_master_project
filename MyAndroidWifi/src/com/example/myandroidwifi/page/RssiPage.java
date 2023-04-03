package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.CPURunnable;
import com.example.myandroidwifi.runnable.RssiConnectionRunnable;
import com.example.myandroidwifi.runnable.RssiRunnable;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.ScreenUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RssiPage extends Activity {
	private CheckBox checkB_rssipage;
	private Button button_rssipage;
	private TextView textV_1_rssipage, textV_2_rssipage;
	private EditText editV_1_rssipage, editV_2_rssipage;
	private PowerManager.WakeLock wakeLock; 
	public Thread mThread_Rssi;
	//public Thread mThread_battery;
	//public Thread mThread_CPU;
	//public Thread mThread_wifi;
	public Thread mThread_RssiConnection;
	public WifiInfoUtil mWifiInfoUtil;
	public Handler handler;
	public Runnable logScreenRunnable;
	public ScreenUtil screenUtil;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		findViewsById();
		mWifiInfoUtil = new WifiInfoUtil(RssiPage.this, "RssiPage");
		mWifiInfoUtil.openWifi();
		Toast.makeText(getApplicationContext(), "wifi is now opened", Toast.LENGTH_LONG).show();
		
		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "RssiPage");
		
		mThread_Rssi = new Thread(new RssiRunnable(RssiPage.this));
		//mThread_battery = new Thread(new BatteryLogRunnable());
		//mThread_CPU = new Thread(new CPURunnable());
		//mThread_wifi = new Thread(new WifiRunnable(RssiPage.this));
		mThread_RssiConnection = new Thread(new RssiConnectionRunnable());
		mThread_RssiConnection.start();
		// Constants.executor.execute(mThread_RssiConnection);
		
		checkB_rssipage.setChecked(true);
		Constants.SLEEP_INTERVAL_READ_RSSI = Constants.SLEEP_INTERVAL_READ_RSSI_SPECIAL;
		
		LogUtil.writeLogFlag = true;
		if(!Constants.runningFlag)
			button_rssipage.setText("START");
		else {
			button_rssipage.setText("STOP");
		}
		
		Constants.low_power = false;
		screenUtil = new ScreenUtil(RssiPage.this);
		handler = new Handler();
		/*
		logScreenRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				screenUtil = new ScreenUtil(RssiPage.this);
				LogUtil.logScreen(screenUtil.getScreenResult());
				handler.postDelayed(logScreenRunnable, Constants.low_power? 
						Constants.SLEEP_INTERVAL_LOG_SCREEN_LOW_POWER : Constants.SLEEP_INTERVAL_LOG_SCREEN);
			}
		};
		*/
		
		Toast.makeText(getApplicationContext(), "Welcome to the Rssi Mode page!", Toast.LENGTH_LONG).show();
		button_rssipage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!Constants.runningFlag)
				{
					int num_brightness = 1, writeLog = 0;
					String str_brightness = editV_1_rssipage.getText().toString();
					try{
						num_brightness = Integer.parseInt(str_brightness);
					}catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), 
								"Screen brightness : must enter a integer between 1 and 100", Toast.LENGTH_LONG).show();
						return;
					}
					if(num_brightness < 0 || num_brightness > 100)
					{
						Toast.makeText(getApplicationContext(), 
								"Screen brightness : must enter a integer between 0 and 100", Toast.LENGTH_LONG).show();
						return;
					}
					
					String str_writeLog = editV_2_rssipage.getText().toString();
					try {
						writeLog = Integer.parseInt(str_writeLog);
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(getApplicationContext(), 
								"WriteLog? : must enter 0 or 1", Toast.LENGTH_LONG).show();
						return;
					}
					if(writeLog!= 0 && writeLog != 1)
					{
						Toast.makeText(getApplicationContext(), 
								"WriteLog? : must enter 0 or 1", Toast.LENGTH_LONG).show();
						return;
					}
					
					Constants.brightness = num_brightness;
					if(writeLog == 1)
						LogUtil.writeLogFlag = true;
					else
						{
						LogUtil.writeLogFlag = false;
						Toast.makeText(getApplicationContext(), 
								"Attention : You choose to not write log files.", Toast.LENGTH_LONG).show();
						}
					
					if(num_brightness == 0)
					{
					AlertDialog.Builder builder = new AlertDialog.Builder(RssiPage.this);
					builder.setMessage("Screen brightness set at 0, are you sure to continue?");
					builder.setTitle("Attention");
					builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							start();
						}
					});
					builder.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							return;
						}
					});
					builder.setCancelable(false);
					builder.show();
					}
					else {
						start();
					}
				}
				else {
					stop();
				}
			}
		});
	}
	
	protected void onResume()
	{
		wakeLock.acquire();
		mWifiInfoUtil.aquireWifiLock();
		super.onResume();
	}
	
	protected void onDestroy(){
		if(wakeLock!=null){
			// wifiLock.release();
			wakeLock.release();
		}
		//if(handler!=null)
			//handler.removeCallbacks(logScreenRunnable);
		Constants.SLEEP_INTERVAL_READ_RSSI = Constants.SLEEP_INTERVAL_READ_RSSI_NORMAL;
		super.onDestroy();
	}
	
	public void onCheckboxClicked(View view)
	{
		boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
		case R.id.checkB_rssipage:
			if(!checked)
			{
				Constants.SLEEP_INTERVAL_READ_RSSI = Constants.SLEEP_INTERVAL_READ_RSSI_NORMAL;
			}
			else {
				Constants.SLEEP_INTERVAL_READ_RSSI = Constants.SLEEP_INTERVAL_READ_RSSI_SPECIAL;
			}
			break;
		}
	}
	
	public void findViewsById()
	{
		setContentView(R.layout.rssi_page);
		button_rssipage = (Button)findViewById(R.id.button_rssipage);
		textV_1_rssipage = (TextView)findViewById(R.id.textV_1_rssipage);
		textV_2_rssipage = (TextView)findViewById(R.id.textV_2_rssipage);
		editV_1_rssipage = (EditText)findViewById(R.id.editV_1_rssipage);
		editV_2_rssipage = (EditText)findViewById(R.id.editV_2_rssipage);
		checkB_rssipage = (CheckBox)findViewById(R.id.checkB_rssipage);
	}
	
	public void start()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RssiPage.this);
		alertBuilder.setMessage("We are connection to : " + Constants.URL_WIFI);
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle("Alert");
		alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				LogUtil.setLogType("RssiPage");
				LogUtil.resetDateString();
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				screenUtil.resetBrightness();
				editV_1_rssipage.setVisibility(View.GONE);
				editV_2_rssipage.setVisibility(View.GONE);
				textV_1_rssipage.setVisibility(View.GONE);
				textV_2_rssipage.setVisibility(View.GONE);
				checkB_rssipage.setVisibility(View.GONE);
				Constants.runningFlag = true;
				//handler.postDelayed(logScreenRunnable, 1000);
				button_rssipage.setText("STOP");
				Constants.executor.execute(mThread_RssiConnection);
				Constants.executor.execute(mThread_Rssi);
				//Constants.executor.execute(mThread_wifi);
				//Constants.executor.execute(mThread_battery);
				//Constants.executor.execute(mThread_CPU);
			}
		});
		alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Constants.runningFlag = false;
				mThread_RssiConnection.interrupt();
				finish();
			}
		});
		alertBuilder.show();
	}
	
	public void stop()
	{
		if(handler!=null)
			handler.removeCallbacks(logScreenRunnable);
		Constants.runningFlag = false;
		button_rssipage.setText("START");
		mThread_Rssi.interrupt();
		//mThread_wifi.interrupt();
		//mThread_battery.interrupt();
		//mThread_CPU.interrupt();
		mThread_RssiConnection.interrupt();
		finish();
	}
	
	
}
