package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.runnable.RssiConnectionRunnable;
import com.example.myandroidwifi.util.AppInfoUtil;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.FlightModeUtil;
import com.example.myandroidwifi.util.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainPage extends Activity{
	Button bttn_check_server_mainpage, bttn_screen_plus_wifi_mainpage, 
		   bttn_screen_only_mainpage, bttn_scan_mode_mainpage, 
		   bttn_rssi_mainpage, bttn_debugpage_mainpage, bttn_checkbatterywifi_mainpage, 
		   bttn_connectivity_mainpage, bttn_tcpdump_mainpage;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_page);
		bttn_check_server_mainpage = (Button)findViewById(R.id.bttn_check_server_mainpage);
		bttn_screen_plus_wifi_mainpage = (Button)findViewById(R.id.bttn_screen_plus_wifi_mainpage);
		bttn_screen_only_mainpage = (Button)findViewById(R.id.bttn_screen_only_mainpage);
		bttn_scan_mode_mainpage = (Button)findViewById(R.id.bttn_scan_mode_mainpage);
		bttn_rssi_mainpage = (Button)findViewById(R.id.bttn_rssi_mainpage);
		bttn_debugpage_mainpage = (Button)findViewById(R.id.bttn_debugpage_mainpage);
		bttn_checkbatterywifi_mainpage = (Button)findViewById(R.id.bttn_checkbatterywifi_mainpage);
		bttn_connectivity_mainpage = (Button)findViewById(R.id.bttn_connectivity_mainpage);
		bttn_tcpdump_mainpage = (Button)findViewById(R.id.bttn_tcpdump_mainpage);
		bttn_check_server_mainpage.setText("Check Server");
		bttn_screen_plus_wifi_mainpage.setText("Wifi + Screen Mode");
		bttn_screen_only_mainpage.setText("Screen Only Mode");
		bttn_scan_mode_mainpage.setText("Scan + Screen Mode");
		bttn_rssi_mainpage.setText("Rssi Mode");
		
		if(!Constants.createPath())
			System.out.println("Create Path Fail");
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("WiPowerSharedPref", Context.MODE_WORLD_READABLE);
		Constants.SERVER_ADDRESS = pref.getString("server_address", "192.168.1.100");
		Constants.updateURL();
		
		AppInfoUtil appInfoUtil = new AppInfoUtil(getApplicationContext());
			appInfoUtil.init();
		System.out.println("uid : " + AppInfoUtil.getMyApplicationInfoStatic().uid);
		
		// FlightModeUtil mFlightModeUtil = new FlightModeUtil(MainPage.this);
		// mFlightModeUtil.checkFlightMode();
		
		bttn_check_server_mainpage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, CheckServerPage.class);
				startActivity(intent);
			}
		});
		
		bttn_checkbatterywifi_mainpage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, CheckBatteryWifiPage.class);
				startActivity(intent);
			}
		});
		
		bttn_checkbatterywifi_mainpage.setVisibility(View.GONE);
		
		
		bttn_screen_plus_wifi_mainpage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(MainPage.this, PacketsSizeRatePage.class);
				Intent intent = new Intent(MainPage.this, ChoosePage.class);
				startActivity(intent);
			}
		});
		
		bttn_screen_only_mainpage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, ScreenOnlyPage.class);
				startActivity(intent);
			}
		});
		
		bttn_screen_only_mainpage.setVisibility(View.GONE);
		
		bttn_rssi_mainpage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, RssiPage.class);
				startActivity(intent);
			}
		});
		
		bttn_debugpage_mainpage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, DebugPage.class);
				startActivity(intent);
			}
		});
		
		bttn_connectivity_mainpage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, 
						com.example.myandroidwifi.page.ConnectivityPage.class);
				startActivity(intent);
			}
		});
		
		bttn_tcpdump_mainpage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPage.this, 
						com.example.myandroidwifi.page.TcpdumpPage.class);
				startActivity(intent);
			}
		});
	}
	
	protected void onResume()
	{
		Constants.low_power = false;
		LogUtil.writeLogFlag = true;
		Constants.SLEEP_INTERVAL_READ_RSSI = Constants.SLEEP_INTERVAL_READ_RSSI_NORMAL;
		Constants.URL_WIFI = Constants.URL + "wifi";
		RssiConnectionRunnable.fois = 0;
		super.onResume();
	}

}
