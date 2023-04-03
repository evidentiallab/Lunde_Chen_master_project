package com.example.myandroidwifi.page;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.util.PhoneInfoUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DebugPage extends Activity {
	Button bttn_1_debugpage, bttn_2_debugpage;
	WifiInfoUtil mWifiInfoUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewsById();
		mWifiInfoUtil = new WifiInfoUtil(DebugPage.this, "DebugPage");
		
		bttn_1_debugpage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(PhoneInfoUtil.isOSVersion4dotx())
					mWifiInfoUtil.setGoodWifi4dotx();
				else {
					mWifiInfoUtil.setGoodWifi2dotx();
				}
			}
		});
		
		bttn_2_debugpage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(PhoneInfoUtil.isOSVersion4dotx())
					mWifiInfoUtil.setBadWifi4dotx();
				else {
					mWifiInfoUtil.setBadWifi2dotx();
				}
			}
		});
	}
	public void findViewsById()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.debug_page);
		bttn_1_debugpage = (Button)findViewById(R.id.bttn_1_debugpage);
		bttn_2_debugpage = (Button)findViewById(R.id.bttn_2_debugpage);
	}

}
