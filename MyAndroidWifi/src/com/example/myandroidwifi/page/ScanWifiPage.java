package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.R.layout;
import com.example.myandroidwifi.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ScanWifiPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_wifi_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_wifi_page, menu);
		return true;
	}

}
