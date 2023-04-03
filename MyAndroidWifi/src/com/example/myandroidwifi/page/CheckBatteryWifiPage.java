package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class CheckBatteryWifiPage extends Activity{
	private TextView textV_battery_checkbwpage;
	String batteryStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_battery_wifi_page);
		textV_battery_checkbwpage = (TextView)findViewById(R.id.textV_battery_checkbwpage);
	}

	
	class AnotherBatteryChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 Toast.makeText(context, "SOMETHEING CHANGED", Toast.LENGTH_LONG).show();
		}
	}
}
