package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.BatteryLogRunnable;
import com.example.myandroidwifi.runnable.CPURunnable;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.ScreenUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenOnlyPage extends Activity{
	private Button button_screenonly;
	private TextView textV_1_screenonly, textV_2_screenonly;
	private EditText editV_1_screenonly, editV_2_screenonly;
	private PowerManager.WakeLock wakeLock; 
	public Thread mThread_battery;
	public Thread mThread_CPU;
	public ScreenUtil screenUtil;
	public Handler handler;
	public Runnable logScreenRunnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		findViewsById();

        wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "ScreenOnlyPage");
        mThread_battery = new Thread(new BatteryLogRunnable());
        mThread_CPU = new Thread(new CPURunnable());

        screenUtil = new ScreenUtil(ScreenOnlyPage.this);
        
		Constants.low_power = false;
		handler = new Handler();
        
		WifiInfoUtil wifiInfoUtil = new WifiInfoUtil(getApplicationContext(), "ScreenOnlyPage");
		wifiInfoUtil.closeWifi();
		Toast.makeText(getApplicationContext(), "wifi is now closed", Toast.LENGTH_LONG).show();
		
		if(!Constants.runningFlag)
			button_screenonly.setText("Start");
		else {
			button_screenonly.setText("Stop");
		}
		
		logScreenRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				screenUtil = new ScreenUtil(ScreenOnlyPage.this);
				LogUtil.logScreen(screenUtil.getScreenResult());
				handler.postDelayed(logScreenRunnable, Constants.low_power? 
						Constants.SLEEP_INTERVAL_LOG_SCREEN_LOW_POWER : Constants.SLEEP_INTERVAL_LOG_SCREEN);
			}
		};
		Toast.makeText(getApplicationContext(), "Welcome to the Screen Only Mode page!", Toast.LENGTH_LONG).show();
		button_screenonly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!Constants.runningFlag)
					{
					int num_brightness = 1, writeLog = 0;
					String str_brightness = editV_1_screenonly.getText().toString();
					try{
						num_brightness = Integer.parseInt(str_brightness);
					}catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), 
								"Screen brightness : must enter a integer between 1 and 100", Toast.LENGTH_LONG).show();
						return;
					}
					if(num_brightness <=0 || num_brightness > 100)
					{
						Toast.makeText(getApplicationContext(), 
								"Screen brightness : must enter a integer between 1 and 100", Toast.LENGTH_LONG).show();
						return;
					}

					String str_writeLog = editV_2_screenonly.getText().toString();
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
								"Attention : You choose to not write log files. ", Toast.LENGTH_LONG).show();
						}
					
					start();
					}
				else
				{
					stop();
				}
			}
		});
	}

	public void findViewsById()
	{
		setContentView(R.layout.screen_only_page);
		button_screenonly = (Button)findViewById(R.id.button_screenonly);
		textV_1_screenonly = (TextView)findViewById(R.id.textV_1_screenonly);
		textV_2_screenonly = (TextView)findViewById(R.id.textV_2_screenonly);
		editV_1_screenonly = (EditText)findViewById(R.id.editV_1_screenonly);
		editV_2_screenonly = (EditText)findViewById(R.id.editV_2_screenonly);
	}
	
	protected void onResume(){
		wakeLock.acquire();
		//screenUtil.resetBrightness();
		super.onResume();
	}
	
	protected void onDestroy(){
		if(wakeLock!=null){
			wakeLock.release();
		}
		if(handler!=null)
			handler.removeCallbacks(logScreenRunnable);
		finish();
		super.onDestroy();
	}
	
	protected void onPause()
	{
		/*
		if(GetPacketsPage.mFlag)
		{
		Intent myIntent = new Intent(EquivalentPage.this, EquivalentPage.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(myIntent);
		}
		*/
        super.onPause();
	}
	
	
	public void start()
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LogUtil.setLogType("ScreenOnly");
		LogUtil.resetDateString();
		screenUtil.resetBrightness();
		Constants.runningFlag = true;
		Constants.low_power_threhold = 3;
		button_screenonly.setText("Stop");
		editV_1_screenonly.setVisibility(View.GONE);
		editV_2_screenonly.setVisibility(View.GONE);
		textV_1_screenonly.setVisibility(View.GONE);
		textV_2_screenonly.setVisibility(View.GONE);
		handler.postDelayed(logScreenRunnable, 1000);
		Constants.executor.execute(mThread_battery);
		Constants.executor.execute(mThread_CPU);
	}
	
	public void stop()
	{
		if(handler!=null)
			handler.removeCallbacks(logScreenRunnable);
		LogUtil.logScreen(screenUtil.getScreenResult());
		Constants.runningFlag = false;
		button_screenonly.setText("Start");
		mThread_battery.interrupt();
		mThread_CPU.interrupt();
		finish();
	}
}
