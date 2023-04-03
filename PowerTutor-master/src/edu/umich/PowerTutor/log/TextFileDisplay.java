package edu.umich.PowerTutor.log;

import edu.umich.PowerTutor.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

public class TextFileDisplay extends Activity{
	
	private Button buttonV_upload;
	private TextView textV_textdisplay, textV_textTitle, textV_scrollPercent;
	private String fileStr;
	private ServiceBinder serviceBinder;
	Connection serviceConnection = null;
	
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_display);
		textV_textdisplay = (TextView)findViewById(R.id.textV_textdisplay);
		textV_textTitle = (TextView)findViewById(R.id.textV_textTitle);
		textV_scrollPercent = (TextView)findViewById(R.id.textV_scrollPercent);
		buttonV_upload = (Button)findViewById(R.id.buttonV_upload);
		textV_textdisplay.setMovementMethod(ScrollingMovementMethod.getInstance());
		textV_textdisplay.setScrollbarFadingEnabled(false);
		fileStr = getIntent().getStringExtra("fileStr");
		textV_textTitle.setText(fileStr);
		serviceConnection = new Connection();
		TextDisplayService textDisplayService = new TextDisplayService();
		serviceBinder = new ServiceBinder();
		textDisplayService.execute(TextFileDisplay.this, serviceConnection, fileStr);
	}
	
	class Connection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder arg1) {
			// TODO Auto-generated method stub
			System.out.println("OnServiceConnected");
			serviceBinder = (ServiceBinder)arg1;
			textV_textdisplay.setText(serviceBinder.getText());	
			unbindService(serviceConnection);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			System.out.println("OnServiceDisconnected");
			unbindService(serviceConnection);
			serviceConnection = null;
		}
		
	}
	

}
