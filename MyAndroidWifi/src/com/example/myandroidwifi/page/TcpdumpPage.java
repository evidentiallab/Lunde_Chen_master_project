package com.example.myandroidwifi.page;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.RssiRunnableNouvelle;
import com.example.myandroidwifi.runnable.TcpdumpRunnable;
import com.example.myandroidwifi.util.ConnectivityUtil;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.DoubleLinkedListStringUtil;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.ScreenUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TcpdumpPage extends Activity {

	private ToggleButton toggle_bttn_tcpdumppage;
	private Button bttn_moins_2_tcpdumppage, bttn_clear_2_tcpdumppage, bttn_plus_2_tcpdumppage;
	private Button bttn_moins_4_tcpdumppage, bttn_default_4_tcpdumppage, bttn_plus_4_tcpdumppage;
	private TextView textV_1_2_tcpdumppage, textV_rssi_1_tcpdumppage, textV_rssi_2_tcpdumppage;
	private EditText editV_1_tcpdumppage, editV_3_tcpdumppage, editV_5_1_1_tcpdumppage, editV_5_1_2_tcpdumppage;
	private WifiInfoUtil mWifiInfoUtil;
	private ScreenUtil screenUtil;
	private Handler handler_withLooper, handler_noLooper;
	private Runnable mRssiRunnable;
	private TcpdumpRunnable tcpdumpRunnable;
	private RssiRunnableNouvelle rssiRunnableNouvelle;
	private Thread tcpdumpThread;
	private Thread rssiThread;
	public static String appName;
	private  String logFileName_Tcpdump;
	private  String logFileName_Rssi;
	private String date_plus_app;
	private DoubleLinkedListStringUtil linkList, linkCommands;
	private String[] arr = {"Test","All_Apps", "Browser", 
						"QQ", "Weixin", "Youku", "Souhu", 
						"CBox", "Tudou", "YYLive", "Skype"};
	private String[] commands = 
	{
			"" + "\n-duration -minute 10",
			" -n 300 tcp" + "\n-duration -minute 10",
			" -n 300 udp" + "\n-duration -minute 10",
			" -A" + "\n-duration -minute 10",
			" -n" + "\n-duration -minute 10",
			" -w $MYDIR/" +  "0001.pcap" + "\n-duration -minute 10",
			" src 192.168.1.101" + "\n-duration -minute 10", 
			" dst 192.168.1.101" + "\n-duration -minute 10", 
	};
	
	public static boolean isDumping = false; // 
	public static String edText_1 = "";  // 
	public static String edText_3 = "";  //
	public static String text_1_2 = "";  //
	private int duration = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tcpdump_page);
		if(!isDumping)
			setTitle("Tcpdump (Idle)");
		else {
			setTitle("Tcpdump (Working)");
		}
		
		ConnectivityUtil.assertBinaries(TcpdumpPage.this, true);
		
		final String cacheDir = TcpdumpPage.this.getCacheDir().getAbsolutePath();
		
		bttn_moins_2_tcpdumppage = (Button)findViewById(R.id.bttn_moins_2_tcpdumppage);
		bttn_clear_2_tcpdumppage = (Button)findViewById(R.id.bttn_clear_2_tcpdumppage);
		bttn_plus_2_tcpdumppage = (Button)findViewById(R.id.bttn_plus_2_tcpdumppage);
		
		bttn_moins_4_tcpdumppage = (Button)findViewById(R.id.bttn_moins_4_tcpdumppage);
		bttn_default_4_tcpdumppage = (Button)findViewById(R.id.bttn_default_4_tcpdumppage);
		bttn_plus_4_tcpdumppage = (Button)findViewById(R.id.bttn_plus_4_tcpdumppage);
		
		textV_1_2_tcpdumppage = (TextView)findViewById(R.id.textV_1_2_tcpdumppage);
		textV_rssi_1_tcpdumppage = (TextView)findViewById(R.id.textV_rssi_1_tcpdumppage);
		textV_rssi_2_tcpdumppage = (TextView)findViewById(R.id.textV_rssi_2_tcpdumppage);
		editV_1_tcpdumppage = (EditText)findViewById(R.id.editV_1_tcpdumppage);
		editV_3_tcpdumppage = (EditText)findViewById(R.id.editV_3_tcpdumppage);
		editV_5_1_1_tcpdumppage = (EditText)findViewById(R.id.editV_5_1_1_tcpdumppage);
		editV_5_1_2_tcpdumppage = (EditText)findViewById(R.id.editV_5_1_2_tcpdumppage);
		
		handler_withLooper = new Handler();
		mRssiRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiInfoUtil = new WifiInfoUtil(TcpdumpPage.this, "ChoosePage");
				textV_rssi_2_tcpdumppage.setText(mWifiInfoUtil.getReceivedSignalStrengthIdicator() + " dBm");
				handler_withLooper.postDelayed(this, 100);
			}
		};
		handler_withLooper.post(mRssiRunnable);
		
		screenUtil = new ScreenUtil(TcpdumpPage.this);
		handler_noLooper = new Handler()
		{
			public void handleMessage (Message msg)
			{
				if(msg.what == 1)
				{
					try
					{
					final int brightness = Integer.parseInt(editV_5_1_1_tcpdumppage.getText().toString());
					int timeLeft = Integer.parseInt(editV_5_1_2_tcpdumppage.getText().toString());
					handler_withLooper.postDelayed(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Constants.brightness = brightness;
							screenUtil.resetBrightness();
						}}, timeLeft * 1000);
					}
					catch(Exception e)
					{Toast.makeText(getApplicationContext(), "Screen brightness won't be adjusted!", Toast.LENGTH_LONG).show();}
				}
				
				
				else if(msg.what == 2)
				{
					if(duration > 0)
					{
						handler_withLooper.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								stop();
								toggle_bttn_tcpdumppage.setChecked(false);
							}
						}, duration);
					}
				}
			}
		};
		
		
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
		
		linkList = new DoubleLinkedListStringUtil(editV_1_tcpdumppage.getText().toString(), arr);
		linkCommands = new DoubleLinkedListStringUtil("" + "\n-duration -minute 10", commands);
		
		bttn_moins_2_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_1_tcpdumppage.setText(linkList.getLast());
			}
		});
		
		bttn_plus_2_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_1_tcpdumppage.setText(linkList.getNext());
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
			}
		});
		
		bttn_default_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(editV_3_tcpdumppage.getText().toString().equalsIgnoreCase("$TCPDUMPARM"))
				{
					DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmm");
					Date date = new Date();
					date_plus_app = dateFormat2.format(date) + "_" + editV_1_tcpdumppage.getText().toString();
					StringTokenizer tok = null;
					String rssiStr = textV_rssi_2_tcpdumppage.getText().toString();
					tok = new StringTokenizer(rssiStr, "- ");
					// tok.nextToken();
					date_plus_app += ("_Rssi_m_" + tok.nextToken());
					editV_3_tcpdumppage.setText("$TCPDUMPARM" + " -w $MYDIR/" +  date_plus_app + ".pcap" 
																		+ "\n-duration -minute 10");
					logFileName_Tcpdump = date_plus_app + "_Tcpdump.txt";
					logFileName_Rssi = date_plus_app + "_Rssi.txt";
				}
				else {
					editV_3_tcpdumppage.setText("$TCPDUMPARM");
				}
			}
		});
		
		bttn_plus_4_tcpdumppage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editV_3_tcpdumppage.setText("$TCPDUMPARM" + linkCommands.getNext());
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
		
		toggle_bttn_tcpdumppage = (ToggleButton)findViewById(R.id.toggle_bttn_tcpdumppage);
		if(isDumping)
			toggle_bttn_tcpdumppage.setChecked(true);
		else
			toggle_bttn_tcpdumppage.setChecked(false);
		
		
		toggle_bttn_tcpdumppage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				// TODO Auto-generated method stub
				 	File file = new File(cacheDir, "tcpdumparm");
					String tcpdumparmPath = file.getAbsolutePath();
					
					Toast.makeText(getApplicationContext(), tcpdumparmPath, Toast.LENGTH_LONG).show();
					if(isChecked)
				    {
						setTitle("Tcpdump (Working)");
						isDumping = true;
						
						////
						Message msg1 = new Message();
						msg1.what = 1;
						handler_noLooper.sendMessage(msg1);
						
						////
						appName = editV_1_tcpdumppage.getText().toString();
						
						DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmm");
						Date date = new Date();
						String dateString = dateFormat2.format(date);
						
						// logFileName_Tcpdump = dateString + "Tcpdump_" + appName + ".txt";
						
						textV_1_2_tcpdumppage.setVisibility(0);  //GONE
						text_1_2 = "Tcpdump log file : \n" +  logFileName_Tcpdump;
						textV_1_2_tcpdumppage.setText(text_1_2);
						
						String editV3String = editV_3_tcpdumppage.getText().toString();
						StringTokenizer tok = new StringTokenizer(editV3String, "\n");
						String tcpdumpcommand = tok.nextToken();
						String durationcommand;
						
						if (ConnectivityUtil.hasRootAccess(TcpdumpPage.this, ConnectivityUtil.SCRIPT_FILE_EMPTY, true)) 
						{
							Toast.makeText(TcpdumpPage.this,
									"has root access", Toast.LENGTH_SHORT).show();
							// tcpdumpRunnable = TcpdumpRunnable.getInstance("$TCPDUMPARM", cacheDir, true);
							tcpdumpRunnable = new TcpdumpRunnable(
									tcpdumpcommand, cacheDir, true, logFileName_Tcpdump);
							rssiRunnableNouvelle = new RssiRunnableNouvelle(TcpdumpPage.this, logFileName_Rssi);
							
							tcpdumpThread = new Thread(tcpdumpRunnable);
							rssiThread = new Thread(rssiRunnableNouvelle);
							
						} 
						else {
							Toast.makeText(TcpdumpPage.this,
									"no root access", Toast.LENGTH_SHORT).show();
							// tcpdumpRunnable = TcpdumpRunnable.getInstance("$TCPDUMPARM", cacheDir, false);
							tcpdumpRunnable = new TcpdumpRunnable(
									tcpdumpcommand, cacheDir, false, logFileName_Tcpdump);
							rssiRunnableNouvelle = new RssiRunnableNouvelle(TcpdumpPage.this, logFileName_Rssi);
							
							tcpdumpThread = new Thread(tcpdumpRunnable);
							rssiThread = new Thread(rssiRunnableNouvelle);
						}
						Constants.executor.execute(tcpdumpThread);
						Constants.executor.execute(rssiThread);
						
						try {
							durationcommand = tok.nextToken();
							StringTokenizer tok2 = new StringTokenizer(durationcommand, " ");
							tok2.nextToken();
							String timeUnit = tok2.nextToken();
							String durationString = tok2.nextToken();
							duration = Integer.parseInt(durationString);
							duration *= 1000;
							if(timeUnit.contains("sec"))
								duration *= 1;
							if(timeUnit.contains("min"))
								duration *= 60;
						} catch (Exception e) {
							// TODO: handle exception
						}
						if(duration > 0)
						{
							Message msg2 = new Message();
							msg2.what = 2;
							handler_noLooper.sendMessage(msg2);
						}
				    }
					else 
					{
						stop();
						new Thread(new TcpdumpRunnable(
								"killall tcpdumparm", cacheDir, true, logFileName_Tcpdump)).start();
					}
			}
		}); 
	}
	
	
	public void stop()
	{
		setTitle("Tcpdump (Idle)");
		isDumping = false;
		
		rssiRunnableNouvelle.stop();
		
		textV_1_2_tcpdumppage.setVisibility(8);  //GONE
		
		try {
			tcpdumpRunnable.writeTcpdumpLogSorted();
			
			 if(tcpdumpThread!=null)
				tcpdumpThread.interrupt();
			 tcpdumpRunnable = null;
			 
			 if(rssiThread!=null)
				 rssiThread.interrupt();
			 rssiThread = null;
			 
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.logTcpdumpFile("Exception STOP: " + e.getMessage(), logFileName_Tcpdump);
		}
	}
	
	public void onDestroy()
	{
		edText_1 = editV_1_tcpdumppage.getText().toString();
		edText_3 = editV_3_tcpdumppage.getText().toString();
		handler_withLooper.removeCallbacks(mRssiRunnable);
		super.onDestroy();
	}
	
}
