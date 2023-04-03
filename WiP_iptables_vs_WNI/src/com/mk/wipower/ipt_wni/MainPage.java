package com.mk.wipower.ipt_wni;

import com.mk.wipower.ipt_wni.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainPage extends Activity {
	private Button bttn_wni, bttn_iptables, bttn_stop;
	private CheckBox checkB;
	private Handler handler_withLoop;
	private Runnable wniRunnable, iptRunnable;
	private Thread servletThread;
	private PowerManager.WakeLock wakeLock; 
	private static int fois = 0;
	private static final long SLEEP_TIME_HANLDER = 20 * 1000; // 之前是 10 * 1000
	private static final long WAIT_TIME_START = 2 * 1000;
	
	private WifiInfoUtil mWifiInfoUtil;
	public ScreenUtil screenUtil;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_page);
        
        wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "MainPage");
        
        bttn_wni      = (Button)  findViewById(R.id.bttn_wni);
        bttn_iptables = (Button)  findViewById(R.id.bttn_iptables);
        bttn_stop     = (Button)  findViewById(R.id.bttn_stop);
        checkB        = (CheckBox)findViewById(R.id.checkB);
        
        bttn_stop.setVisibility(View.INVISIBLE);
        handler_withLoop = new Handler();
        
        mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
        screenUtil = new ScreenUtil(MainPage.this);
        
        ConnectivityUtil.generateScripts(MainPage.this, true);
        if (ConnectivityUtil.hasRootAccess(MainPage.this, ConnectivityUtil.SCRIPT_FILE_EMPTY, true))
        {
        	(new MainUtil(MainPage.this)).popUpAlert("Has root access");
        }
        else {
        	(new MainUtil(MainPage.this)).popUpAlert("NO ROOT ACCESS", "NO root access");
		}
        
        LogUtil.resetDateString();
        
        wniRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				fois ++;
				Toast.makeText(getApplicationContext(),
						"Granted WiP_iptables_vs_WNI to get root permission", Toast.LENGTH_SHORT)
						.show();
				
				if(fois % 2 == 0)
				{
					mWifiInfoUtil.openWifi();
//					System.out.println(ScreenUtil.getNowTime() + ": open Wi-Fi");
//					LogUtil.writeLog(ScreenUtil.getNowTime() + ": open Wi-Fi", "WNI_IPT");
				}
				else {
					mWifiInfoUtil.closeWifi();
//					System.out.println(ScreenUtil.getNowTime() + ": close Wi-Fi");
//					LogUtil.writeLog(ScreenUtil.getNowTime() + ": close Wi-Fi", "WNI_IPT");
				}

				handler_withLoop.postDelayed(this, SLEEP_TIME_HANLDER);
			}
		};
        
		iptRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				fois ++;
				if(fois % 2 == 0)
				{
					ConnectivityUtil.applyIptables(MainPage.this, true);
//					System.out.println(ScreenUtil.getNowTime() + ": block iptables");
//					LogUtil.writeLog(ScreenUtil.getNowTime() + ": block iptables", "WNI_IPT");
				}
				else {
					ConnectivityUtil.purgeIptables(MainPage.this, true);
//					System.out.println(ScreenUtil.getNowTime() + ": unblock iptables");
//					LogUtil.writeLog(ScreenUtil.getNowTime() + ": unblock iptables", "WNI_IPT");
				}
				
				handler_withLoop.postDelayed(this, SLEEP_TIME_HANLDER);
			}
		};
        
        
        bttn_wni.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start();
				handler_withLoop.postDelayed(wniRunnable, WAIT_TIME_START);
			}
		});
        
        bttn_iptables.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start();
				handler_withLoop.postDelayed(iptRunnable, WAIT_TIME_START);
			}
		});
    }

    protected void onResume()
	{
		wakeLock.acquire();
		///////////////
		// mWifiInfoUtil.aquireWifiLock(); ////////
		super.onResume();
	}
    
    protected void onDestroy(){
		if(wakeLock!=null){
			///////////////
			// mWifiInfoUtil.releaseWifiLock();
			wakeLock.release();
		}
		super.onDestroy();
	}
    
    public void onCheckboxClicked(View view)
	{
		boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
		case R.id.checkB:
			if(!checked)
			{
				ServletRunnable.runFlag = false;
				System.out.println(ServletRunnable.runFlag);
			}
			else {
				ServletRunnable.runFlag = true;
				System.out.println(ServletRunnable.runFlag);
			}
			break;
		}
	}
    
    public void start()
    {
    	servletThread = new Thread(new ServletRunnable());
    	if(ServletRunnable.runFlag)
    	{
    		ServletRunnable.executor.execute(servletThread);
    	}
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		screenUtil.resetBrightness(1);
    	
    	bttn_wni.setVisibility(View.INVISIBLE);
    	bttn_iptables.setVisibility(View.INVISIBLE);
    	checkB.setVisibility(View.INVISIBLE);
    	bttn_stop.setVisibility(View.VISIBLE);
    	
    	bttn_stop.setTextColor(Color.BLACK);
    	bttn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				handler_withLoop.removeCallbacks(wniRunnable);
				handler_withLoop.removeCallbacks(iptRunnable);
				
				ServletRunnable.runFlag = false;
				
				if(!servletThread.isInterrupted())
					servletThread.interrupt();
				
				System.out.println("servletThread interrupted: " + servletThread.isInterrupted());
				
				finish();
			}
		});
    	
    }
    
}
