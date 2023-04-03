package com.example.myandroidwifi.page;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.ServletDoPostRunnable;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.DoubleLinkedListIntUtil;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SpinnerAdapter;

public class ChoosePage extends Activity implements Runnable{
	private Spinner spinner_choosepage;
	private CheckBox checkB_cpu_choosepage, checkB_battery_choosepage, checkB_rssi_choosepage, checkB_screen_choosepage,
			         checkB_bytes_choosepage, checkB_packets_choosepage, checkB_speed_choosepage, checkB_maxvalue_choosepage;
	private Button button_choosepage, bttn_moins_choosepage, bttn_plus_choosepage, bttn_clear_choosepage;
	private RelativeLayout relativeL_1_choosepage, relativeL_2_choosepage, relativeL_3_choosepage, 
						   relativeL_4_choosepage, relativeL_5_choosepage;
	private RelativeLayout[] arrRelativeL;
	private EditText[] arrEditV;
	private final static int OK = 0;
	private TextView textV_1_choosepage, textV_2_choosepage, textV_3_choosepage, textV_4_choosepage, textV_5_choosepage,
					 textV_wifi_1_choosepage, textV_wifi_2_choosepage;
	private EditText editV_1_choosepage, editV_2_choosepage, editV_3_choosepage, editV_4_choosepage, editV_5_choosepage;
	private String str1, str2, str3, str4, str5;
	private int num1, num2, num3, num4, num5;
	private Handler handler_withLooper, handler_noLooper;
	private WifiInfoUtil mWifiInfoUtil;
	private DoubleLinkedListIntUtil mLinkedList;
    private int[] arr1 = {1, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1291, 1292, 1293, 2739, 2740, 2741, 2742, 
    		4187, 4188, 4189, 5635, 5636, 5637, 7083, 7084, 7085, 
    		8605, 8607, 8817, 8818, 8819, 10265, 10266, 10267, 11713, 11714, 11715};
    private int[] arr2 = {1, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1291, 1292, 1293, 2739, 2740, 2741, 2742, 
    		4187, 4188, 4189, 5635, 5636, 5637, 7083, 7084, 7085, 
    		8605, 8607, 8817, 8818, 8819, 10265, 10266, 10267, 11713, 11714, 11715};
    private int[] arr3 = {0, 20, 40, 50, 100, 125, 200, 250, 400, 500, 800, 1000, 2000}; 
    private int[] arr4 = {0, 1}; 
    private int[] arr5 = {0, 1, 20};
    private Object[] objects;
    private String[] arrSpinner = {"DEFAULT", "CLEAR ALL", "DEBUG", "SAMSUNG", "BYTES SPEED", "PACKETS SPEED", "REPORT"};
	private Runnable mRunnable;
	public int clickedV = -1;
	String text = "";
	public long size;
	boolean mConstructOK = true;
	private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		pref = getApplicationContext().getSharedPreferences("WiPowerChoosePage", Context.MODE_WORLD_READABLE);
		findViewsById();
		readValueFromPref();
		
		Constants.low_power = false;
		LogUtil.writeLogFlag = false;
		
		mWifiInfoUtil = new WifiInfoUtil(getApplicationContext(), "ChoosePage");
		mWifiInfoUtil.openWifi();
		Toast.makeText(getApplicationContext(), "wifi is now opened", Toast.LENGTH_LONG).show();
		
		textV_wifi_1_choosepage.setText("Wifi Rssi : ");
		handler_withLooper = new Handler();
		mRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiInfoUtil = new WifiInfoUtil(ChoosePage.this, "ChoosePage");
				textV_wifi_2_choosepage.setText(mWifiInfoUtil.getReceivedSignalStrengthIdicator() + " dBm");
				handler_withLooper.postDelayed(this, 100);
			}
		};
		handler_withLooper.post(mRunnable);

		handler_noLooper = new Handler()
		{
			public void handleMessage (Message msg)
			{
				///////////////////////////DEBUG///////////////////////
				if(num4 == 0)
					mConstructOK = true;
				//////////////////////////DEBUG////////////////////////
				
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChoosePage.this);
				alertBuilder.setMessage(text);
				alertBuilder.setCancelable(false);
				alertBuilder.setTitle("Alert");
				alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(mConstructOK)
						{
							Intent getPckintent = new Intent(ChoosePage.this, ScreenPlusWifiPage.class);
							startActivity(getPckintent);
							finish();
						}
						else
							button_choosepage.setText("ERROR");
					}
				});
				alertBuilder.show();
			}
		};
		
		
		button_choosepage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int OKorNot = isOK();
				if(OKorNot == OK)
				{
					Constants.length_of_string_sent = num1;
					
					if(str2.equalsIgnoreCase("S"))
						Constants.length_of_string_received = -1;
					else
						Constants.length_of_string_received = num2;
					
					Constants.SLEEP_INTERVAL_SEND_PACKETS = num3;
					Constants.brightness = num5;
					if(num4 == 0)
						LogUtil.writeLogFlag = false;
					else
						LogUtil.writeLogFlag = true;
					
					button_choosepage.setText("Wait a moment...");
					button_choosepage.setClickable(false);
					Thread thread = new Thread(ChoosePage.this);
					thread.start();
				}
				else Toast.makeText(getApplicationContext(), 
						"all numbers entered in the editview must be integer", Toast.LENGTH_LONG).show();
			}
		});
		
		
		arrRelativeL = new RelativeLayout[6];
		arrRelativeL[1] = relativeL_1_choosepage;
		arrRelativeL[2] = relativeL_2_choosepage;
		arrRelativeL[3] = relativeL_3_choosepage;
		arrRelativeL[4] = relativeL_4_choosepage;
		arrRelativeL[5] = relativeL_5_choosepage;
		arrEditV = new EditText[6];
		objects = new Object[6];
		objects[1] = arr1;
		objects[2] = arr2;
		objects[3] = arr3;
		objects[4] = arr4;
		objects[5] = arr5;
		arrEditV[1] = editV_1_choosepage;
		arrEditV[2] = editV_2_choosepage;
		arrEditV[3] = editV_3_choosepage;
		arrEditV[4] = editV_4_choosepage;
		arrEditV[5] = editV_5_choosepage;
		
		for(int i = 1;i<=5;i++)
		{
			final int choosen = i;
			arrRelativeL[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int j = 1; j <= 5; j++)
					{
						arrRelativeL[j].setBackgroundResource(android.R.color.black);
					}
					clickedV = choosen;
					v.setBackgroundResource(R.drawable.text_cover);
					int[] arr = null;
					int nowValue = 1;
					try {
						nowValue = Integer.parseInt(arrEditV[choosen].getText().toString());
					} catch (Exception e) {
						// TODO: handle exception
						nowValue = 1;
					}
					mLinkedList = new DoubleLinkedListIntUtil(nowValue, (int[]) objects[choosen]);
				}
			});
		}
		
		bttn_clear_choosepage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickedV != -1)
				{
					arrEditV[clickedV].setText("");
				}
			}
		});
		
		bttn_moins_choosepage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickedV != -1)
				{
					arrEditV[clickedV].setText(mLinkedList.getLast());
				}
			}
		});
		
		bttn_plus_choosepage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickedV != -1)
				{
					arrEditV[clickedV].setText(mLinkedList.getNext());
				}
			}
		});
		
		MySpinnerAdapter mSpinnerAdapter = new MySpinnerAdapter();
		spinner_choosepage.setAdapter(mSpinnerAdapter);
		spinner_choosepage.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					// fillInValue("C", "S", "500", "1", "0", 1, 1, 1,1, 1, 1, 1, 1);
					readValueFromPref();
					break;
				case 1:
					fillInValue("", "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0);
					break;
				case 2:
					fillInValue("C", "S", "500", "0", "1", 0, 0, 0, 0, 0, 0, 0, 0);
					break;
				case 3:
					fillInValue(null, null, null, null, "1", 2, 2, 2, 2, 2, 2, 2, 2);
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void onDestroy()
	{
		if(handler_withLooper!=null)
			handler_withLooper.removeCallbacks(mRunnable);
		saveValueInPref();
		super.onDestroy();
	}

	public void findViewsById()
	{
		setContentView(R.layout.choose_page);
		button_choosepage = (Button)findViewById(R.id.button_choosepage);
		bttn_clear_choosepage = (Button)findViewById(R.id.bttn_clear_choosepage);
		bttn_moins_choosepage = (Button)findViewById(R.id.bttn_moins_choosepage);
		bttn_plus_choosepage = (Button)findViewById(R.id.bttn_plus_choosepage);
		relativeL_1_choosepage = (RelativeLayout)findViewById(R.id.relativL_1_choosepage);
		relativeL_2_choosepage = (RelativeLayout)findViewById(R.id.relativL_2_choosepage);
		relativeL_3_choosepage = (RelativeLayout)findViewById(R.id.relativL_3_choosepage);
		relativeL_4_choosepage = (RelativeLayout)findViewById(R.id.relativL_4_choosepage);
		relativeL_5_choosepage = (RelativeLayout)findViewById(R.id.relativL_5_choosepage);
		textV_1_choosepage = (TextView)findViewById(R.id.textV_1_choosepage);
		textV_2_choosepage = (TextView)findViewById(R.id.textV_2_choosepage);
		textV_3_choosepage = (TextView)findViewById(R.id.textV_3_choosepage);
		textV_4_choosepage = (TextView)findViewById(R.id.textV_4_choosepage);
		textV_5_choosepage = (TextView)findViewById(R.id.textV_5_choosepage);
		textV_wifi_1_choosepage = (TextView)findViewById(R.id.textV_wifi_1_choosepage);
		textV_wifi_2_choosepage = (TextView)findViewById(R.id.textV_wifi_2_choosepage);
		editV_1_choosepage = (EditText)findViewById(R.id.editV_1_choosepage);
		editV_2_choosepage = (EditText)findViewById(R.id.editV_2_choosepage);
		editV_3_choosepage = (EditText)findViewById(R.id.editV_3_choosepage);
		editV_4_choosepage = (EditText)findViewById(R.id.editV_4_choosepage);
		editV_5_choosepage = (EditText)findViewById(R.id.editV_5_choosepage);
		spinner_choosepage = (Spinner)findViewById(R.id.spinner_choosepage);
		checkB_battery_choosepage = (CheckBox)findViewById(R.id.checkB_battery_choosepage);
		checkB_cpu_choosepage = (CheckBox)findViewById(R.id.checkB_cpu_choosepage);
		checkB_rssi_choosepage = (CheckBox)findViewById(R.id.checkB_rssi_choosepage);
		checkB_bytes_choosepage = (CheckBox)findViewById(R.id.checkB_bytes_choosepage);
		checkB_packets_choosepage = (CheckBox)findViewById(R.id.checkB_packets_choosepage);
		checkB_speed_choosepage = (CheckBox)findViewById(R.id.checkB_speed_choosepage);
		checkB_maxvalue_choosepage = (CheckBox)findViewById(R.id.checkB_maxvalue_choosepage);
		checkB_screen_choosepage = (CheckBox)findViewById(R.id.checkB_screen_choosepage);
	}

	public void onCheckboxClicked(View view)
	{
		boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
		
		}
	}
	
	public void fillInValue(String s1, String s2, String s3, String s4, String s5, 
						    int cb_battery, int cb_rssi, int cb_cpu, int cb_screen, 
						    int cb_bytes, int cb_packets, int cb_speed, int cb_maxvalue
			)
	{
		if(s1 != null)
			editV_1_choosepage.setText(s1);
		if(s2 != null)
			editV_2_choosepage.setText(s2);
		if(s3 != null)
			editV_3_choosepage.setText(s3);
		if(s4 != null)
			editV_4_choosepage.setText(s4);
		if(s5 != null)
			editV_5_choosepage.setText(s5);
		if(cb_battery == 0)
		{
			checkB_battery_choosepage.setChecked(false);
		}
		else if(cb_battery == 1)
		{
			checkB_battery_choosepage.setChecked(true);
		}
		
		if(cb_rssi == 0)
		{
			checkB_rssi_choosepage.setChecked(false);
		}
		else if(cb_rssi == 1)
		{
			checkB_rssi_choosepage.setChecked(true);
		}
		
		if(cb_cpu == 0)
		{
			checkB_cpu_choosepage.setChecked(false);
		}
		else if(cb_cpu == 1)
		{
			checkB_cpu_choosepage.setChecked(true);
		}
		
		if(cb_screen == 0)
		{
			checkB_screen_choosepage.setChecked(false);
		}
		else if(cb_screen == 1)
		{
			checkB_screen_choosepage.setChecked(true);
		}
		
		if(cb_bytes == 0)
		{
			checkB_bytes_choosepage.setChecked(false);
		}
		else if(cb_bytes == 1)
		{
			checkB_bytes_choosepage.setChecked(true);
		}
		
		if(cb_packets == 0)
		{
			checkB_packets_choosepage.setChecked(false);
		}
		else if(cb_packets == 1)
		{
			checkB_packets_choosepage.setChecked(true);
		}
		
		if(cb_speed == 0)
		{
			checkB_speed_choosepage.setChecked(false);
		}
		else if(cb_speed == 1)
		{
			checkB_speed_choosepage.setChecked(true);
		}
		
		if(cb_maxvalue == 0)
		{
			checkB_maxvalue_choosepage.setChecked(false);
		}
		else if(cb_maxvalue == 1)
		{
			checkB_maxvalue_choosepage.setChecked(true);
		}
	}
	
	public String getValueFromEditView()
	{
		String s1 = editV_1_choosepage.getText().toString();
		String s2 = editV_2_choosepage.getText().toString();
		String s3 = editV_3_choosepage.getText().toString();
		String s4 = editV_4_choosepage.getText().toString();
		String s5 = editV_5_choosepage.getText().toString();
		
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
		System.out.println(s4);
		System.out.println(s5);
		
		if(s1.length() == 0) s1="null";
		if(s2.length() == 0) s2="null";
		if(s3.length() == 0) s3="null";
		if(s4.length() == 0) s4="null";
		if(s5.length() == 0) s5="null";
		
		return s1 + " " + s2 + " " + s3 + " " + s4 + " " + s5;
	}
	
	public void saveValueInPref()
	{
		Editor editor = pref.edit();
		editor.putString("editviewsvalues", getValueFromEditView());
		editor.commit();
	}
	
	public void readValueFromPref()
	{
		String storedValue = pref.getString("editviewsvalues", null);
		if(storedValue==null) return;
		String[] s = new String[5];
		StringTokenizer tok = new StringTokenizer(storedValue, " "); 
		for(int i = 0; i < 5; i ++)
		  {
			if(tok.hasMoreElements())
			s[i] = tok.nextToken();
		  }
		
		fillInValue(s[0], s[1], s[2], s[3], s[4], 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public int isOK()
	{
		str1 = editV_1_choosepage.getText().toString();
		str2 = editV_2_choosepage.getText().toString();
		str3 = editV_3_choosepage.getText().toString();
		str4 = editV_4_choosepage.getText().toString();
		str5 = editV_5_choosepage.getText().toString();
		
		try
		{
			if(str1.equalsIgnoreCase("C"))
			{
				num1 = 1;
			}
			else
			{
				num1 = Integer.parseInt(str1);
				if(num1 <= 0 || num1 >= 50000000)
					return 1;
			}
		}
		catch(Exception e)
		{
			return 1;
		}
		try
		{
			num2 = Integer.parseInt(str2);
			if(num2 <= 0 || num2 >= 50000000)
			return 2;
		}
		catch(Exception e)
		{
			if(str2.equalsIgnoreCase("S")) ;
			else return 2;
		}
		try
		{
			num3 = Integer.parseInt(str3);
			if(num3 >= 5000)
				return 3;
		}
		catch(Exception e)
		{
			return 3;
		}
		try
		{
			num4 = Integer.parseInt(str4);
			if(num4!=0 && num4!=1)
				return 4;
		}
		catch(Exception e)
		{
			return 4;
		}
		try
		{
			num5 = Integer.parseInt(str5);
			if(num5<0 || num5>100)
				return 5;
		}
		catch(Exception e)
		{
			return 5;
		}
		
		 text = "";
		 text += "Length of string to server : " + str1 + "\n";
 		 text += "Length of string from server : " + str2 + "\n";
 		 text += "Sleep interval : " + str3 + "\n";
 		 text += "WriteLog : " + (str4.equals("1")? "true" : "false") + "\n";
 		 text += "Screen brightness : " + str5 + "\n";
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(Constants.length_of_string_received<=Constants.CHANGE_TO_WIFI_BACK_UP_THREHOLD)
		{
 		try {
 			URL url_regulation = new URL(Constants.URL_REGULATION);
            URLConnection conn = url_regulation.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true); 
            httpConn.setDoOutput(true); 
            httpConn.connect();
            DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
           	dataStream.writeBytes("REQUEST_SERVLET");
            dataStream.flush(); 
            dataStream.close();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            	InputStream in = httpConn.getInputStream();
            	String receivedStr = Constants.streamToString(in);
            	if(receivedStr.length() != 0)
            	{
            		System.out.println("Regulation connection : OK");
            		Constants.URL_WIFI += receivedStr;
            		System.out.println(Constants.URL_WIFI);
            		text += "\nWe are connecting to " + Constants.URL_WIFI + "\n";
            	}
            }
            else {
           	 mConstructOK = false;
           	 return;
			}
             
             URL url_wifi = new URL(Constants.URL_WIFI);
             conn = url_wifi.openConnection();
             httpConn = (HttpURLConnection) conn;
             httpConn.setRequestMethod("POST");
             httpConn.setDoInput(true); 
             httpConn.setDoOutput(true); 
             httpConn.connect();
             dataStream = new DataOutputStream(conn.getOutputStream());
             if(Constants.length_of_string_received != -1)
            	 dataStream.writeBytes(Constants.length_of_string_received + "");
             else {
            	 dataStream.writeBytes("C");
			}
    		 
             dataStream.flush(); 
             dataStream.close();

             responseCode = httpConn.getResponseCode();
             if (responseCode == HttpURLConnection.HTTP_OK) {
            	text += "Server connection : OK\n";
             	System.out.println("Server connection : OK");
             	InputStream in = httpConn.getInputStream();
             	String receivedStr = Constants.streamToString(in);
             	Constants.length_of_string_received = receivedStr.length() - 1;
             	}
             else {
            	 text += "Server connection : Error\n";
            	 mConstructOK = false;
            	 return;
			}
 			} catch (Exception ex)
 			{
 			}
		}
		else {
			try {
				Constants.URL_WIFI += "Backup";
	 			URL url_regulation = new URL(Constants.URL_WIFI);
	            URLConnection conn = url_regulation.openConnection();
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setRequestMethod("POST");
	            httpConn.setDoInput(true); 
	            httpConn.setDoOutput(true); 
	            httpConn.connect();
	            DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
	           	dataStream.writeBytes(Constants.length_of_string_received + "");
	            dataStream.flush(); 
	            dataStream.close();

	            int responseCode = httpConn.getResponseCode();
	            if (responseCode == HttpURLConnection.HTTP_OK) {
	            	InputStream in = httpConn.getInputStream();
	            	String receivedStr = Constants.streamToString(in);
	            	if(receivedStr.length() != 0)
	            	{
	            		System.out.println("WifiBackup connection : OK");
	            		System.out.println(Constants.URL_WIFI);
	            		text += "We are connecting to " + Constants.URL_WIFI + "\n";
	            	}
	            }
	            else {
	           	 mConstructOK = false;
	           	 return;
				}
			}
			catch (Exception ex)
 			{
 			}
		}

		LogUtil.logV(Constants.URL_WIFI + "\n");
 		ServletDoPostRunnable.STRING_TO_THE_SERVLET = "C";
 		size = Constants.length_of_string_sent;
 		go();
 		if(!mWifiInfoUtil.isWifiEnabled())
 			{
 			mConstructOK = false;
 			text += "Wifi closed, please open it\n";
 			}
 		
 		if(mConstructOK)
 		{
 		 text += "Wifi opened : OK\n";
 		 text += "Construction finished, we can go now.\n";
 		}
 		handler_noLooper.sendMessage(new Message());
	}
	
	public void go()
	{
		if(size == 1)
			ServletDoPostRunnable.STRING_TO_THE_SERVLET = "C";
		else if(size % 2 == 0)
		{
			size /= 2;
			go();
			ServletDoPostRunnable.STRING_TO_THE_SERVLET += ServletDoPostRunnable.STRING_TO_THE_SERVLET;
		}
		else {
			size = (size-1)/2;
			go();
			ServletDoPostRunnable.STRING_TO_THE_SERVLET += ServletDoPostRunnable.STRING_TO_THE_SERVLET + "C";
		}
	}
	
	class MySpinnerAdapter implements SpinnerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrSpinner.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrSpinner[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textV = new TextView(ChoosePage.this);
			textV.setTextColor(Color.argb(255, 180, 180, 180));
			textV.setBackgroundResource(android.R.color.black);
			textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			textV.setText("Working mode : " + arrSpinner[position]);
			textV.setGravity(Gravity.CENTER);
			return textV;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textV = new TextView(ChoosePage.this);
			textV.setBackgroundResource(android.R.color.black);
			//textV.setTextColor(Color.BLUE);
			textV.setTextColor(Color.argb(255, 130, 130, 130));
			textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			textV.setText(arrSpinner[position]);
			textV.setGravity(Gravity.CENTER);
			textV.setPadding(0, 20, 0, 20);
			return textV;
		}
	}
}
