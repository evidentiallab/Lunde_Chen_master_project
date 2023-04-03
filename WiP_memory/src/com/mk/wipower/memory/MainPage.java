package com.mk.wipower.memory;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mk.wipower.wip_memory.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainPage  extends Activity {
	
	private Button bttn_start, bttn_stop, bttn_invisible_1, bttn_invisible_2;
	private LinearLayout layout_linear;
	private TextView[] text_views;
	private MyReceiver myReceiver;
	private Handler mHandler;
	private Runnable memoInfoRunnable;
	public int count = 0;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_page);
        
        bttn_start       = (Button)  findViewById(R.id.bttn_start)       ;
        bttn_stop        = (Button)  findViewById(R.id.bttn_stop)        ;
        bttn_invisible_1 = (Button)  findViewById(R.id.bttn_invisible_1) ;
        bttn_invisible_2 = (Button)  findViewById(R.id.bttn_invisible_2) ;
        
        layout_linear = (LinearLayout) findViewById(R.id.layout_linear);
        text_views = new TextView[100];
        
        myReceiver = new MyReceiver();
        mHandler = new Handler();
        
        memoInfoRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MemoryInfo mi = new MemoryInfo();
				ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				activityManager.getMemoryInfo(mi);
//				System.out.println("//////// " + count + " //////////");s
//				System.out.println("Total Memo        : " + bytesToKbMbGb(mi.totalMem)  );
				System.out.println(count + " : Available Memory  : " + bytesToKbMbGb(mi.availMem)  );
//				System.out.println("Threshold         : " + bytesToKbMbGb(mi.threshold) );
//				System.out.println("Low Memory?       : " + mi.lowMemory );
				System.out.println();
				
				mHandler.postDelayed(this, 1000);
			}
		};
		
		mHandler.postDelayed(memoInfoRunnable, 1000);
		
		
        
        final Intent intent_start = new Intent(MainPage.this, MemoryUpService.class);
        intent_start.putExtra("what", "start");
        
        final Intent intent_stop = new Intent(MainPage.this, MemoryUpService.class);
        intent_stop.putExtra("what", "stop");
        
        bttn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registerReceiver(myReceiver, new IntentFilter("com.mk.wipower.memory.UP"));
				MainPage.this.startService(intent_start);
			}
		});
        
        bttn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("STOP");
				unregisterReceiver(myReceiver);
				MainPage.this.startService(intent_stop);
				finish();
			}
		});
	}

	public void onResume()
	{
		super.onResume();
	}
	
	
	public void onDestroy()
	{
	   bttn_invisible_1.setOnClickListener(null);
	   bttn_invisible_2.setOnClickListener(null);
	   mHandler.removeCallbacks(memoInfoRunnable);
	   super.onDestroy();	
	}
	
//	public static class MyReceiver extends BroadcastReceiver{
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			if(intent.getExtras().getBoolean("up") == true)
			{
				count ++;
				
				/*
				bttn_start.setText(getNowTime("HHmmss"));
				
				if(count % 2 == 0)
				{
					bttn_invisible_1.setOnClickListener(null);
//					bttn_invisible_2.setOnClickListener(null);
					bttn_invisible_1.setOnClickListener(new View.OnClickListener() {
					
//						private byte[] mBytes = new byte[((int) (count / 10) + 1) * 10 * 1024 * 1024];
//						private byte[] mBytes = new byte[20 * 1024 * 1024];
						private byte[] mBytes = new byte[20 * 1024 * 1024];
					
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println(bytesToKbMbGb(mBytes.length));
						}
					});
				}
				else {
//					bttn_invisible_1.setOnClickListener(null);
					bttn_invisible_2.setOnClickListener(null);
					bttn_invisible_2.setOnClickListener(new View.OnClickListener() {
						
						private byte[] mBytes = new byte[20 * 1024 * 1024];
					
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println(bytesToKbMbGb(mBytes.length));
						}
					});
				}
				
				*/ 
				{
					text_views[count] = new TextView(MainPage.this);
					text_views[count].setWidth(LayoutParams.WRAP_CONTENT);
					text_views[count].setHeight(LayoutParams.WRAP_CONTENT);
					text_views[count].setText("Hallo Welt!");
					text_views[count].setVisibility(View.GONE);
					text_views[count].setOnClickListener(new View.OnClickListener() {
						private byte[] mBytes = new byte[10 * 1024 * 1024];
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println(bytesToKbMbGb(mBytes.length));
						}
					});
					layout_linear.addView(text_views[count]);
				}
			}
		}
	}
	
	////////////////////////
	public static String getNowTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
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
	
	
	public static String bytesToKbMbGb(long size)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double msize = size;
		if(msize > 1024L)
		{
			msize /= 1024L;
			if(msize > 1024L)
			{
				msize /= 1024L;
				if(msize > 1024L)
					{
					msize /= 1024L;
					return df.format(msize) + " Gb";
					}
				return df.format(msize) + " Mb";
			}
			return df.format(msize) + " Kb";
		}
		return df.format(msize) + " B";
	}
	
	public static String bpsToKbpsMbpsGbps(long speed, int interval)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double msize = speed;
		if(msize > 1024L)
		{
			msize /= 1024L;
			if(msize > 1024L)
			{
				msize /= 1024L;
				if(msize > 1024L)
					{
					msize /= 1024L;
					return df.format(msize) + " Gb/" + interval + " s";
					}
				return df.format(msize) + " Mb/" + interval + " s";
			}
			return df.format(msize) + " Kb/" + interval + " s";
		}
		return df.format(msize) + " B/" + interval + " s";
	}
	
	
}
