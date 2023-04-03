package com.mk.wipower.tcpdump;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainPage extends Activity {

	private ToggleButton toggle_bttn_dump_tcpdumppage, toggle_bttn_register_tcpdumppage;
	private Button bttn_moins_2_tcpdumppage, bttn_clear_2_tcpdumppage, bttn_plus_2_tcpdumppage;
	private Button bttn_moins_4_tcpdumppage, bttn_default_4_tcpdumppage, bttn_plus_4_tcpdumppage, bttn_update_4_tcpdumppage;
	private TextView textV_1_2_tcpdumppage, textV_rssi_1_tcpdumppage, textV_rssi_2_tcpdumppage;
	private EditText editV_1_tcpdumppage, editV_3_tcpdumppage;
	private WifiInfoUtil mWifiInfoUtil;
	private Handler handler_withLooper;
	private Runnable mRssiRunnable;
	private RssiRunnableNouvelle rssiRunnableNouvelle;
	private Thread rssiThread;
	public  static String appName;
	public  static String logFileName_Tcpdump;
	public  static String logFileName_Rssi;
	public  static String logFileName_pcap;
	private static String date_plus_app;
	private DoubleLinkedListStringUtil linkApp, linkCommands;
	private String[] arr = {"Test","All_Apps", "Browser", 
						"QQ", "Weixin", "Youku", "Souhu", 
						"CBox", "Tudou", "YYLive", "Skype", "DownloadManager", "GreenWiFiService"};
	private String[] commands = 
	{
			"",
			" -n 300 tcp",
			" -n 300 udp" ,
			" -A",
			" -n",
			" -w $MYDIR/" +  "0001.pcap",
			" src 192.168.1.101", 
			" dst 192.168.1.101", 
	};
	
	public static boolean isDumping = false; // 
	public static String edText_1 = "";  // 
	public static String edText_3 = "";  //
	public static String text_1_2 = "";  //
	
	public MyReceiver myReceiver;
	public IntentFilter filter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		if(!isDumping)
			setTitle("Tcpdump (Idle)");
		else {
			setTitle("Tcpdump (Working)");
		}
		
//		final String cacheDir = MainPage.this.getCacheDir().getAbsolutePath();
		
		TcpdumpUtil.assertBinaries(MainPage.this);
		TcpdumpUtil.generateScripts(MainPage.this);
		if(!TcpdumpUtil.emptyRun(MainPage.this))
			(new MainUtil(MainPage.this)).popUpAlert("Has NO root access!");
		else
			(new MainUtil(MainPage.this)).popUpAlert("Good, u have root access!");
		
		bttn_moins_2_tcpdumppage = (Button)findViewById(R.id.bttn_moins_2_tcpdumppage);
		bttn_clear_2_tcpdumppage = (Button)findViewById(R.id.bttn_clear_2_tcpdumppage);
		bttn_plus_2_tcpdumppage = (Button)findViewById(R.id.bttn_plus_2_tcpdumppage);
		
		bttn_moins_4_tcpdumppage = (Button)findViewById(R.id.bttn_moins_4_tcpdumppage);
		bttn_default_4_tcpdumppage = (Button)findViewById(R.id.bttn_default_4_tcpdumppage);
		bttn_plus_4_tcpdumppage = (Button)findViewById(R.id.bttn_plus_4_tcpdumppage);
		bttn_update_4_tcpdumppage = (Button)findViewById(R.id.bttn_update_4_tcpdumppage);
		
		textV_1_2_tcpdumppage = (TextView)findViewById(R.id.textV_1_2_tcpdumppage);
		textV_rssi_1_tcpdumppage = (TextView)findViewById(R.id.textV_rssi_1_tcpdumppage);
		textV_rssi_2_tcpdumppage = (TextView)findViewById(R.id.textV_rssi_2_tcpdumppage);
		editV_1_tcpdumppage = (EditText)findViewById(R.id.editV_1_tcpdumppage);
		editV_3_tcpdumppage = (EditText)findViewById(R.id.editV_3_tcpdumppage);
		
		handler_withLooper = new Handler();
		mRssiRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiInfoUtil = new WifiInfoUtil(MainPage.this, "MainPage");
				textV_rssi_2_tcpdumppage.setText(mWifiInfoUtil.getReceivedSignalStrengthIdicator() + " dBm");
				handler_withLooper.postDelayed(mRssiRunnable, 100);
			}
		};
		handler_withLooper.postDelayed(mRssiRunnable, 100);
		
		if(edText_1.equalsIgnoreCase(""))
			editV_1_tcpdumppage.setText(arr[0]);
		else {
			editV_1_tcpdumppage.setText(edText_1);
		}
		
		if(edText_3.equalsIgnoreCase(""))
			editV_3_tcpdumppage.setText("$TCPDUMPARM" + commands[0]);
		else {
			editV_3_tcpdumppage.setText(edText_3);
		}
		
		linkApp = new DoubleLinkedListStringUtil(editV_1_tcpdumppage.getText().toString(), arr);
		linkCommands = new DoubleLinkedListStringUtil("", commands);
		
		bttn_moins_2_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_1_tcpdumppage.setText(linkApp.getLast());
			}
		});
		
		bttn_plus_2_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_1_tcpdumppage.setText(linkApp.getNext());
			}
		});
		
		bttn_clear_2_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_1_tcpdumppage.setText("");
			}
		});
		
		bttn_moins_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_3_tcpdumppage.setText("$TCPDUMPARM" + linkCommands.getLast());
				prepareStart();
				TcpdumpUtil.resetStartDumpScript(MainPage.this, editV_3_tcpdumppage.getText().toString());
			}
		});
		
		bttn_update_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				prepareStart();
				TcpdumpUtil.resetStartDumpScript(MainPage.this, editV_3_tcpdumppage.getText().toString());
			}
		});
		
		bttn_default_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editV_3_tcpdumppage.getText().toString().equalsIgnoreCase("$TCPDUMPARM"))
				{
					prepareStart();
					editV_3_tcpdumppage.setText("$TCPDUMPARM" + " -w $MYDIR/" +  logFileName_pcap);
					TcpdumpUtil.resetStartDumpScript(MainPage.this, editV_3_tcpdumppage.getText().toString());
				}
				else {
					editV_3_tcpdumppage.setText("$TCPDUMPARM");
					prepareStart();
					TcpdumpUtil.resetStartDumpScript(MainPage.this, editV_3_tcpdumppage.getText().toString());
				}
			}
		});
		
		bttn_plus_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_3_tcpdumppage.setText("$TCPDUMPARM" + linkCommands.getNext());
				prepareStart();
				TcpdumpUtil.resetStartDumpScript(MainPage.this, editV_3_tcpdumppage.getText().toString());
			}
		});
		
		if(!isDumping)
			{
			textV_1_2_tcpdumppage.setVisibility(8);  //GONE
			}
		else {
			textV_1_2_tcpdumppage.setVisibility(0);
			textV_1_2_tcpdumppage.setText(text_1_2);
		}
		
		toggle_bttn_dump_tcpdumppage = (ToggleButton)findViewById(R.id.toggle_bttn_dump_tcpdumppage);
		toggle_bttn_register_tcpdumppage = (ToggleButton)findViewById(R.id.toggle_bttn_register_tcpdumppage);
		
		if(isDumping)
			toggle_bttn_dump_tcpdumppage.setChecked(true);
		else
			toggle_bttn_dump_tcpdumppage.setChecked(false);
		
		toggle_bttn_dump_tcpdumppage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				// TODO Auto-generated method stub
					if(isChecked)
				    {
						setTitle("Tcpdump (Working)");
						toggle_bttn_register_tcpdumppage.setVisibility(View.INVISIBLE);
						isDumping = true;
						appName = editV_1_tcpdumppage.getText().toString();
						
						DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmm");
						Date date = new Date();
						String dateString = dateFormat2.format(date);
						
						textV_1_2_tcpdumppage.setVisibility(0);  //GONE
						text_1_2 = "Tcpdump log file : \n" +  logFileName_Tcpdump;
						textV_1_2_tcpdumppage.setText(text_1_2);
						
						String editV3String = editV_3_tcpdumppage.getText().toString();
						 
						Runnable runnable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TcpdumpUtil.startDump(MainPage.this);
							}
						};
						(new Thread(runnable)).start();
						
						rssiRunnableNouvelle = new RssiRunnableNouvelle(MainPage.this, logFileName_Rssi);
						rssiThread = new Thread(rssiRunnableNouvelle);
						Utils.executor.execute(rssiThread);
				    }
					else 
					{
						try {
							stop();
							toggle_bttn_register_tcpdumppage.setVisibility(View.VISIBLE);
//							MainPage.this.finish();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
		}); 
		
		toggle_bttn_register_tcpdumppage.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
			    {
					toggle_bttn_dump_tcpdumppage.setVisibility(View.INVISIBLE);
					myReceiver = new MyReceiver();
					filter = new IntentFilter();
					filter.addAction("com.mk.wipower.downloadmanager.action.finishdownload");
					filter.addAction("com.mk.wipower.downloadmanager.action.startdownload");
					
					registerReceiver(myReceiver, filter);
			    }
				else {
					if(myReceiver!=null)
						unregisterReceiver(myReceiver);
					toggle_bttn_dump_tcpdumppage.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	public void prepareStart()
	{
		DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmm"); 
		Date date = new Date();
		date_plus_app = dateFormat2.format(date) + "_" + editV_1_tcpdumppage.getText().toString();
		StringTokenizer tok = null;
		String rssiStr = textV_rssi_2_tcpdumppage.getText().toString();
		tok = new StringTokenizer(rssiStr, "- ");
		// tok.nextToken();
		date_plus_app += ("_Rssi_m_" + tok.nextToken());
		logFileName_Tcpdump = date_plus_app + "_Tcpdump.txt";
		logFileName_Rssi = date_plus_app + "_Rssi.txt";
		logFileName_pcap = date_plus_app + ".pcap";
	}
	
	public void stop() throws IOException, InterruptedException
	{
		TcpdumpUtil.stopDump(MainPage.this);
		setTitle("Tcpdump (Idle)");
		isDumping = false;
		
		rssiRunnableNouvelle.stop();
		textV_1_2_tcpdumppage.setVisibility(8);  //GONE
		
		try {
			 if(rssiThread!=null)
				 rssiThread.interrupt();
			 rssiThread = null;
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.logTcpdumpFile("Exception STOP rssiThread : " + e.getMessage(), logFileName_Tcpdump);
			rssiThread = null;
		}
	}
	
	public void onDestroy()
	{
		edText_1 = editV_1_tcpdumppage.getText().toString();
		edText_3 = editV_3_tcpdumppage.getText().toString();
		handler_withLooper.removeCallbacks(mRssiRunnable);
		
		if(myReceiver != null)
			unregisterReceiver(myReceiver);
		
		super.onDestroy();
	}
	
	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase("com.mk.wipower.downloadmanager.action.finishdownload"))
			{
				System.out.println("Download Finished (Frome WiP_tcpdump)");
				try {
					stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				bttn_default_4_tcpdumppage.	setVisibility(View.VISIBLE);
				bttn_plus_4_tcpdumppage.	setVisibility(View.VISIBLE);
				bttn_update_4_tcpdumppage.	setVisibility(View.VISIBLE);
				bttn_moins_4_tcpdumppage.	setVisibility(View.VISIBLE);
			}
			else if(intent.getAction().equalsIgnoreCase("com.mk.wipower.downloadmanager.action.startdownload"))
			{
				prepareStart();
				TcpdumpUtil.resetStartDumpScript(MainPage.this, "$TCPDUMPARM" + " -w $MYDIR/" +  logFileName_pcap);
				
				Runnable runnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						TcpdumpUtil.startDump(MainPage.this);
					}
				};
				(new Thread(runnable)).start();
			    
				rssiRunnableNouvelle = new RssiRunnableNouvelle(MainPage.this, logFileName_Rssi);
				rssiThread = new Thread(rssiRunnableNouvelle);
				Utils.executor.execute(rssiThread);
				
				bttn_default_4_tcpdumppage.	setVisibility(View.INVISIBLE);
				bttn_plus_4_tcpdumppage.	setVisibility(View.INVISIBLE);
				bttn_update_4_tcpdumppage.	setVisibility(View.INVISIBLE);
				bttn_moins_4_tcpdumppage.	setVisibility(View.INVISIBLE);
			}
			
			
		}
	}
}
