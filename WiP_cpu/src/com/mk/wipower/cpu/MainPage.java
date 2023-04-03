package com.mk.wipower.cpu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import weka.estimators.KernelEstimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainPage  extends Activity {
	
	private Button bttn_start, bttn_stop, bttn_pre, bttn_next;
	private ToggleButton toggle_bttn_register;
	private TextView textV;
	private MyReceiver myReceiver;
	public boolean workflag = false;
	public boolean stopall = false;
	public Thread workThread;
	public static final long cpu_cycle = 200; 
	public long cpu_work;
	public long cpu_sleep;
	public int percent = 50;
	public int[] arr = {10, 20, 30, 40, 50, 60, 70, 80, 85, 90, 92, 95, 98, 100};
	public DoubleLinkedListIntUtil link;
	public static ExecutorService executor = Executors.newCachedThreadPool();
	
	public void anotherwork()
	{
		double i = 1;
		int j = 0;
		
		//华为：10000, 100 --> 24%
		//华为：50000, 20 --> 40%
		//华为：400000, 20 --> 60% (max 92%)
		//华为：1000000, 20 --> 95%
		//华为：10000000, 0 --> 96% 
		//华为：10000000, 20 --> 
		//三星：
		for (j = 0; j < 10000000; j++) 
		{
				if(stopall)
					return;
				i += Math.log10(Math.PI)+j;
				i += Math.pow(Math.PI, Math.E) / j;
				i = 1;
		}
		
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		anotherwork();
	}
	
	public Runnable anotherscheduleRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!stopall) {
				anotherwork();
			}
		}
	};
	
	public Runnable workRunnable = new Runnable() {
		
		@Override
		public synchronized void run() {
			// TODO Auto-generated method stub
			double i = 1;
			int j = 0;
			int k = 0;
			while (workflag) {
//				System.out.println("workRunnable" + k);
				for (j = 0; j < 1000000 && workflag; j++) {
					if(stopall)
						return;
					i += Math.log10(Math.PI)+j;
					i += Math.pow(Math.PI, Math.E) / j;
					i = 1;
				}
				k ++;
			}
			System.out.println("j = " + j);
		}
	};
	
	public Runnable scheduleRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			workThread = new Thread(workRunnable);
			cpuschedule();
		}
		
		public void cpuschedule() // 要不要 synchronized??
		{
			if(stopall)
				return;
			
			try {
				if(workflag == true)
				{
//					System.out.println("working");
					Thread.sleep(cpu_work);   // work duration
					workflag = false;
					cpuschedule();
				}
				else {
//					System.out.println("sleeping");
//					workThread.interrupt();
					Thread.sleep(cpu_sleep);  // sleep duration
					workflag = true;
//					(new Thread(workRunnable)).start(); 
					workThread.start();
					cpuschedule(); 
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_page);
        
        textV = (TextView)findViewById(R.id.textV);
        
        bttn_start       = (Button)  findViewById(R.id.bttn_start)       ;
        bttn_stop        = (Button)  findViewById(R.id.bttn_stop)        ;
        bttn_pre         = (Button)  findViewById(R.id.bttn_pre)         ;
        bttn_next        = (Button)  findViewById(R.id.bttn_next)        ;
        toggle_bttn_register = (ToggleButton)findViewById(R.id.toggle_bttn_register);
        
        link = new DoubleLinkedListIntUtil(percent, arr);
        textV.setText(percent + "% CPU usage");
        
        bttn_pre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				percent = link.getLastInt();
				timeAlloc();
			}
		});
        
        bttn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				percent = link.getNextInt();
				timeAlloc();
			}
		});
        
        bttn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				stopall = false;
				
//				(new Thread(scheduleRunnable)).start();
//				(new Thread(anotherscheduleRunnable)).start();
				
//				executor.execute(new Thread(scheduleRunnable));
				executor.execute(new Thread(anotherscheduleRunnable));
				
				bttn_start.setClickable(false);
			}
		});
        
        bttn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopall = true;
				bttn_start.setClickable(true);
			}
		});
        
        toggle_bttn_register.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
				    myReceiver = new MyReceiver();
					IntentFilter filter = new IntentFilter();
					filter.addAction("com.mk.wipower.downloadmanager.action.finishdownload");
					filter.addAction("com.mk.wipower.downloadmanager.action.startdownload");
					registerReceiver(myReceiver, filter);
				}
				else {
					if(myReceiver != null)
						unregisterReceiver(myReceiver);
				}
				
				
			}
        });
        
	}

	public void timeAlloc()
	{
		cpu_work = (long) cpu_cycle * percent / 100;
		cpu_sleep = cpu_cycle - cpu_work;
		textV.setText(percent + "% CPU usage");
		System.out.println("cpu_work: " + cpu_work);
		System.out.println("cpu_sleep: " + cpu_sleep);
	}
	
	public void onResume()
	{
		super.onResume();
	}
	
	
	public void onDestroy()
	{
	   if(myReceiver != null)
		   unregisterReceiver(myReceiver);
	   super.onDestroy();	
	}
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase("com.mk.wipower.downloadmanager.action.finishdownload"))
			{
				System.out.println("CPU: finishdownload received");
				stopall = true;
			}
			else if(intent.getAction().equalsIgnoreCase("com.mk.wipower.downloadmanager.action.startdownload"))
			{
				System.out.println("CPU: startdownload received");
				bttn_start.setClickable(false);
				stopall = false;
//				(new Thread(scheduleRunnable)).start();
				executor.execute(new Thread(anotherscheduleRunnable));
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
	
}