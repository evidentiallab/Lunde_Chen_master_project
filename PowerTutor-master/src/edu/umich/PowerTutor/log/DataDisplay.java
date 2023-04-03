package edu.umich.PowerTutor.log;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;

import edu.umich.PowerTutor.R;
import edu.umich.PowerTutor.service.ICounterService;
import edu.umich.PowerTutor.service.UMLoggerService;
import edu.umich.PowerTutor.service.UidInfo;
import edu.umich.PowerTutor.ui.UMLogger;
import edu.umich.PowerTutor.util.Counter;
import edu.umich.PowerTutor.util.SystemInfo;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class DataDisplay extends Activity{
	private TextView textV_1;
	private static TextView textV_2;
	private TextView textV_3;
	private TextView textV_4;
	private TextView textV_5;
	private TextView textV_6; 
	private TextView textV_001, textV_002, textV_003, textV_004, textV_005, textV_006;
	private Button buttonV_1, buttonV_2, buttonV_3, buttonV_4, buttonV_5;
	private ScrollView scrollV_1;
	private static ScrollView scrollV_2;
	private ScrollView scrollV_3;
	private ScrollView scrollV_4;
	private ScrollView scrollV_5;
	private ScrollView scrollV_6;
	private Handler handler, handler2;
	private ICounterService icounterService;
	private MyCounterServiceConnection connection;
	private Intent serviceIntent;
	public static boolean handlerPosted = false;
	private UidInfo[] uidInfos;
	String [] componentNames;
	int noUidMask;
	private int uid;
	private static int handlerCount = 0;
	private String[] title_textV = 
		{"LogCat",
		 "UC Web",
		 "Total" + "\n",
		 "PartiHealth \n",
		 "Average \n",
		 "PowerTutor \n"
		};
	private String displayText_1 = "";
	private static String displayText_2 = "";
	private String displayText_3 = "";
	private String displayText_4 = "";
	private String displayText_5 = "";
	private String displayText_6 = "";
	public static final int KEY_CURRENT_POWER = 0;
	public static final int KEY_AVERAGE_POWER = 1;
	public static final int KEY_TOTAL_ENERGY = 2;
	SystemInfo sysInfo;

	PackageManager pm;
	
	
	class MyCounterServiceConnection implements ServiceConnection
	{
		public  MyCounterServiceConnection()
		{
			System.out.println("I new a MyCounterServiceConnection()");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			icounterService = ICounterService.Stub.asInterface((IBinder)service);
	        tv1_displayTwoLn("onServiceConnected");
			try {
		        componentNames = icounterService.getComponents();
		        noUidMask = icounterService.getNoUidMask();
		        tv1_displayTwoLn("noUidMask: " + noUidMask);
		        refreshView();
		        // handler.post(infoCollector);
			} catch (Exception e) {
				// TODO: handle exception
		        icounterService = null;
		        tv1_displayTwoLn("onServiceConnectedException:  icounterService = null");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			icounterService = null;
			getApplicationContext().unbindService(connection);
			getApplicationContext().bindService(serviceIntent, connection, 0);
		}
	}
	
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_display);
		displayTitleTextV();
		textV_1 = (TextView)findViewById(R.id.textV_1);
		textV_2 = (TextView)findViewById(R.id.textV_2);
		textV_3 = (TextView)findViewById(R.id.textV_3);
		textV_4 = (TextView)findViewById(R.id.textV_4);
		textV_5 = (TextView)findViewById(R.id.textV_5);
		textV_6 = (TextView)findViewById(R.id.textV_6);
		scrollV_1 = (ScrollView)findViewById(R.id.scrollV_1);
		scrollV_2 = (ScrollView)findViewById(R.id.scrollV_2);
		scrollV_3 = (ScrollView)findViewById(R.id.scrollV_3);
		scrollV_4 = (ScrollView)findViewById(R.id.scrollV_4);
		scrollV_5 = (ScrollView)findViewById(R.id.scrollV_5);
		scrollV_6 = (ScrollView)findViewById(R.id.scrollV_6);
		
		// textV_3.setMovementMethod(ScrollingMovementMethod.getInstance());
		tv1_displayTwoLn("onCreate ");

		buttonV_1 = (Button)findViewById(R.id.buttonV_1);
		buttonV_2 = (Button)findViewById(R.id.buttonV_2);
		buttonV_3 = (Button)findViewById(R.id.buttonV_3);
		buttonV_4 = (Button)findViewById(R.id.buttonV_4);
		buttonV_5 = (Button)findViewById(R.id.buttonV_5);
		
		uid = getIntent().getIntExtra("uid", SystemInfo.AID_ALL);
		
		tv1_displayTwoLn("uid: " + uid);
		
		tv1_displayOneLn("ThreadId in UI: " + Thread.currentThread().getId());
		
		serviceIntent = new Intent(this, UMLoggerService.class);
		connection = new MyCounterServiceConnection();
	    getApplicationContext().bindService(serviceIntent, connection, 0);

	    
	    sysInfo = SystemInfo.getInstance();
	    pm = this.getPackageManager();
	    
	    //powerDataCollector = new PowerDataCollector();
		// powerDataCollector = new PowerDataCollector(ContextUtil.GetGlobleApplicationContext());
	     handler = new Handler();
	     handler2 = new Handler();
	     
	    if(handlerPosted == false)
	    buttonV_1.setText("Start Display");
	    else {
			buttonV_1.setText("Stop Display");
		}
	    buttonV_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(handlerPosted == false)
				{
					handler2.post(infoCollector);
					tv1_displayTwoLn("handlerPosted");
					handlerPosted = true;
					buttonV_1.setText("Stop Display");
				}
				else {
					handler2.removeCallbacks(infoCollector);
					tv1_displayTwoLn("handlerRemoved");
					buttonV_1.setText("Start Display");
					handlerPosted = false;
				}
			}
		});
	    
	    buttonV_2.setVisibility(0);
	    buttonV_2.setText("Refresh");
	    buttonV_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayTextVContent(KEY_CURRENT_POWER);   /////////////////////////
			}
		});
	    
	    buttonV_3.setVisibility(0);
	    if(PowerDataCollector.isLogging==false)
	    buttonV_3.setText("Start Log");
	    else buttonV_3.setText("Stop Log");
	    buttonV_3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(PowerDataCollector.isLogging == false && PartiHealthCollector.isLogging == false)
				{
					////////
					PowerDataCollector.isLogging = true;
					UMLogger.powerDataCollector.start();
					UMLogger.powerDataSaver.start();
					
					PartiHealthCollector.isLogging = true;
					UMLogger.partiHealthCollector.start();
					UMLogger.partiHealthSaver.start();
					
					
					buttonV_3.setText("StopLog");
				}
				else {
					PowerDataCollector.isLogging = false;
					UMLogger.powerDataCollector.stop();
					UMLogger.powerDataSaver.stop();
					
					PartiHealthCollector.isLogging = false;
					UMLogger.partiHealthCollector.start();
					UMLogger.partiHealthSaver.start();
					
					buttonV_3.setText("StartLog");
					/////////
				}
			}
		});
	    
	    buttonV_4.setVisibility(0);
	    buttonV_4.setText("Wifi");
	    buttonV_4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DataDisplay.this, FilesList.class);
				// intent.putExtra("dirStr", "/sys/devices/virtual/net/" + "eth0" + "/statistics/");
				intent.putExtra("dirStr", "/sys/devices/virtual/net/" + "lo" + "/statistics/");
				startActivity(intent);
			}
		});
	    
	    buttonV_5.setVisibility(0);
	    buttonV_5.setText("Show Log");
	    buttonV_5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DataDisplay.this, FilesList.class);
				intent.putExtra("dirStr", PowerConstants.ROOT_DIR);
				startActivity(intent);
			}
		});
	    
	}
	
	public void onResume()
	{
		super.onResume();
		handler = new Handler();
		
	    getApplicationContext().bindService(serviceIntent, connection, 0);
	    
	    refreshView();
	}
	
	/*
	public void onPause()
	{
		super.onPause();
		
		tv1_displayTwoLn("onPause");
		
	    getApplicationContext().unbindService(connection);
	    if(infoCollector != null) {
	        handler2.removeCallbacks(infoCollector);
	        handler2 = null;
	      }
	}
	
	*/
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Intent intent = new Intent(DataDisplay.this, UMLogger.class);
			// startActivity(intent);
			 getApplicationContext().unbindService(connection);
			 finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	public void onDestoy()
	{				
		if(PowerDataCollector.isLogging == true)
		{
			PowerDataCollector.isLogging = false;
			powerDataCollector.stop();
			powerDataSaver.stop();
			buttonV_3.setText("StartLog");
			/////////
		}
		super.onDestroy();
	}
	*/
	
	Runnable infoCollector = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if(icounterService!=null)
				{
				long totals[] = icounterService.getTotals(uid, Counter.WINDOW_TOTAL);   /////////////////
				tv3_displayOneLn("");
				tv3_displayTwoLn("## " + handlerCount + " ##");
				tv3_displayTwoLn("uid: " + uid);
				//tv3_display("totals.length: " + totals.length);
				//tv3_display("componentNames.length: " + componentNames.length);
				for(int i=0; i<totals.length&&i<componentNames.length;i++)
				{
					tv3_displayOneLn(componentNames[i] + " :" + totals[i] + " mJ");
				}
				}
				else {
					tv1_displayTwoLn("infoCollector: icounterService == null");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handlerCount ++;
			/*
			try {
				Thread.sleep(1000);
				tv1_displayOneLn("ThreadId in handler: " + Thread.currentThread().getId()); ////////////
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			if(handler2!=null)
			{
			handler2.postDelayed(infoCollector, 1000);
			}

		}
	};
	
	
	Runnable testRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			tv1_displayTwoLn("testRunnable: hello!");
			handler2.postDelayed(testRunnable, 2000);
		}
	};
	
	public void refreshView()
	{
		 if(icounterService == null)
		 {
			 tv1_displayTwoLn("icounterService == null ");
		 }
		 else {
			 tv1_displayTwoLn("refreshView: icounterService != null ");
		}
		
		if(uid == SystemInfo.AID_ALL) {
		      /* If we are reporting global power usage then just set noUidMask to 0 so
		       * that all components get displayed.
		       */
		     noUidMask = 0;
		     tv1_displayTwoLn("refreshView: noUidMask: "+ noUidMask);
		    }
	}
	
	public void displayTextVContent(int keyId)
	{
	    String contentToShow = "";
	    double total = 0.0; //////////////////////////////////
		 try {
		      byte[] rawUidInfo = icounterService.getUidInfo(
		          Counter.WINDOW_TOTAL,
		          noUidMask | 0);     //////////////////////******////////////
		      if(rawUidInfo != null) {
		 uidInfos = (UidInfo[])new ObjectInputStream(
		            new ByteArrayInputStream(rawUidInfo)).readObject(); 
		 tv1_displayTwoLn("displayTextVContent");
		 tv1_displayTwoLn("noUidMask: "+ noUidMask);
		 tv1_displayTwoLn("noUidMask | 0: "+ (noUidMask | 0));
		 tv1_displayTwoLn("uidInfos.length: "+ uidInfos.length);
		 for(UidInfo uidInfo : uidInfos)
		 {
			  if(uidInfo.uid == SystemInfo.AID_ALL) continue;
	          switch(keyId) {
	            case KEY_CURRENT_POWER:
	              uidInfo.key = uidInfo.currentPower;
	              uidInfo.unit = "W";
	              contentToShow = "Current Power";
	              break;
	            case KEY_AVERAGE_POWER:
	              uidInfo.key = uidInfo.totalEnergy /
	                  (uidInfo.runtime == 0 ? 1 : uidInfo.runtime);
	              uidInfo.unit = "W";
	              contentToShow = "Average Power";
	              break;
	            case KEY_TOTAL_ENERGY:
	              uidInfo.key = uidInfo.totalEnergy;
	              uidInfo.unit = "J";
	              contentToShow = "Total Energy";
	              break;
	            default:
	              uidInfo.key = uidInfo.currentPower;
	              uidInfo.unit = "W";
	              contentToShow = "Current Power";
	          }
	          total += uidInfo.key;
		 }
		 if(total == 0) total = 1;
		 for(UidInfo uidInfo : uidInfos) {
			 uidInfo.percentage = 100.0 * uidInfo.key / total;
			 String name = sysInfo.getUidName(uidInfo.uid, pm);
			 if(name.contains("PowerTutor"))
			 {
				 tv6_displayTwoLn("");
			     tv6_displayOneLn("## "+ name );
			     tv6_displayOneLn("uid: " + uidInfo.uid);
			     tv6_displayOneLn(contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit);
			     tv6_displayOneLn("percentage: " + doubleOne(uidInfo.percentage) + "%");
			     for(int i=0;i<componentNames.length;i++)
			     {
			    	 tv6_displayOneLn(componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW");
			     }
			     
			     tv6_displayOneLn("runtime: " + uidInfo.runtime + " sec");
			     tv6_displayOneLn("TotalEnergy: " + uidInfo.totalEnergy + " mJ");
			     
			     //tv2_displayOneLn("PowerTutor\nruntime:\n" + uidInfo.runtime + " seconds");
			     //tv2_displayTwoLn("GetMeans: \n" + icounterService.getMeans(uidInfo.uid, Counter.WINDOW_TOTAL)[0] + " mW"); /////******/////

			     tv5_displayOneLn("PowerTutor\nruntime:\n" + uidInfo.runtime + " seconds");
			     tv5_displayTwoLn("Average: \n" + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW");
			     
				 }
			 if(name.contains("Participatory") && name.contains("Health"))
			 {
			 tv4_displayTwoLn("");
		     tv4_displayOneLn("## "+ name);
		     tv4_displayOneLn("uid: " + uidInfo.uid);
		     tv4_displayOneLn(contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit);
		     tv4_displayOneLn("percentage: " + doubleOne(uidInfo.percentage) + "%");
		     for(int i=0;i<componentNames.length;i++)
		     {
		    	 tv4_displayOneLn(componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW");
		     }
		     
		     tv4_displayOneLn("runtime: " + uidInfo.runtime + " sec");
		     tv4_displayOneLn("TotalEnergy: " + uidInfo.totalEnergy + " mJ");
		     
		     //tv2_displayOneLn("PartiHealth\nruntime:\n" + uidInfo.runtime + " seconds");
		     //tv2_displayTwoLn("GetMeans: \n" + icounterService.getMeans(uidInfo.uid, Counter.WINDOW_TOTAL)[0] + " mW"); /////******/////
	
		     tv5_displayOneLn("PartiHealth\nruntime:\n" + uidInfo.runtime + " seconds");
		     tv5_displayTwoLn("Average: \n" + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW");
			 }
			 if(name.contains("UC"))
			 {
				 tv2_displayTwoLn("");
			     tv2_displayOneLn("## "+ name );
			     tv2_displayOneLn("uid: " + uidInfo.uid);
			     tv2_displayOneLn(contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit);
			     tv2_displayOneLn("percentage: " + doubleOne(uidInfo.percentage) + "%");
			     for(int i=0;i<componentNames.length;i++)
			     {
			    	 tv2_displayOneLn(componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW");
			     }
			     
			     tv2_displayOneLn("runtime: " + uidInfo.runtime + " sec");
			     tv2_displayOneLn("TotalEnergy: " + uidInfo.totalEnergy + " mJ");
			     tv5_displayOneLn("UC Web\nruntime:\n" + uidInfo.runtime + " seconds");
			     tv5_displayTwoLn("Average: \n" + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW");
			 }
			 
			 
			 
		 }
		 }
		 }catch(Exception e)
		 {
		 }
	}
	
	
	public void tv6_displayTwoLn(String str)
	{
		displayText_6 += str+ "\n\n";
		textV_6.setText(displayText_6);
		scrollV_6.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv6_displayOneLn(String str)
	{
		displayText_6 += str + "\n";
		textV_6.setText(displayText_6);
		scrollV_6.fullScroll(View.FOCUS_DOWN);
	}
	
	
	public void tv5_displayTwoLn(String str)
	{
		displayText_5 += str+ "\n\n";
		textV_5.setText(displayText_5);
		scrollV_5.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv5_displayOneLn(String str)
	{
		displayText_5 += str + "\n";
		textV_5.setText(displayText_5);
		scrollV_5.fullScroll(View.FOCUS_DOWN);
	}
	

	public void tv4_displayTwoLn(String str)
	{
		displayText_4 += str+ "\n\n";
		textV_4.setText(displayText_4);
		scrollV_4.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv4_displayOneLn(String str)
	{
		displayText_4 += str + "\n";
		textV_4.setText(displayText_4);
		scrollV_4.fullScroll(View.FOCUS_DOWN);
	}
	
	
	public void tv3_displayTwoLn(String str)
	{
		displayText_3 += str+ "\n\n";
		textV_3.setText(displayText_3);
		scrollV_3.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv3_displayOneLn(String str)
	{
		displayText_3 += str + "\n";
		textV_3.setText(displayText_3);
		scrollV_3.fullScroll(View.FOCUS_DOWN);
	}
	
	public static void tv2_displayTwoLn(String str)
	{
		displayText_2 += str + "\n\n";
		textV_2.setText(displayText_2);
		scrollV_2.fullScroll(View.FOCUS_DOWN);
	}
	
	public static void tv2_displayOneLn(String str)
	{
		displayText_2 += str + "\n";
		textV_2.setText(displayText_2);
		scrollV_2.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv1_displayTwoLn(String str)
	{
		displayText_1 += str + "\n\n";
		textV_1.setText(displayText_1);
		scrollV_1.fullScroll(View.FOCUS_DOWN);
	}
	
	public void tv1_displayOneLn(String str)
	{
		displayText_1 += str + "\n";
		textV_1.setText(displayText_1);
		scrollV_1.fullScroll(View.FOCUS_DOWN);
	}
	
	public String doubleOne(double num)
	    {
	    	DecimalFormat df = new DecimalFormat("#.0");
	    	String str = df.format(num);
	    	return str;
	    }
	
	public void displayTitleTextV()
	{
		textV_001 = (TextView)findViewById(R.id.textV_001);
		textV_002 = (TextView)findViewById(R.id.textV_002);
		textV_003 = (TextView)findViewById(R.id.textV_003);
		textV_004 = (TextView)findViewById(R.id.textV_004);
		textV_005 = (TextView)findViewById(R.id.textV_005);
		textV_006 = (TextView)findViewById(R.id.textV_006);
		textV_001.setText(title_textV[0]);
		textV_002.setText(title_textV[1]);
		textV_003.setText(title_textV[2]);
		textV_004.setText(title_textV[3]);
		textV_005.setText(title_textV[4]);
		textV_006.setText(title_textV[5]);
	}
}
