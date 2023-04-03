package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.R.layout;
import com.example.myandroidwifi.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RealTimeStatsPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_time_stats_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.real_time_stats_page, menu);
		return true;
	}

}
