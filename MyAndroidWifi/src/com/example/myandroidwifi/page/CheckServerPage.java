package com.example.myandroidwifi.page;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.util.Random;
import java.util.StringTokenizer;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.util.Constants;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TextView;

public class CheckServerPage extends Activity {
	private Button button_1_checkserverpage;
	private TextView textV_baidu_1_checkserverpage, 
				textV_sohu_1_checkserverpage, textV_sina_1_checkserverpage, 
				textV_yahoo_1_checkserverpage;
	private TextView textV_1_checkserverpage, textV_baidu_2_checkserverpage, 
				textV_sohu_2_checkserverpage, textV_sina_2_checkserverpage, 
				textV_yahoo_2_checkserverpage;
	private EditText editV_1_checkserverpage;
	private TextView[] textViews1 = new TextView[4];
	private TextView[] textViews2 = new TextView[4];
	GetXMLTaskLocal task_local;
	GetXMLTaskWeb task_web_1, task_web_2, task_web_3, task_web_4;
	float number;
	private Handler mHandler;
	String numberStr;
	private static final String[] WEB_URLS = {
		"http://www.baidu.com",
		"http://www.sohu.com",
		"http://www.sina.com.cn",
		"http://www.yahoo.com"
		};
	private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle("Local Server & Internet Connection");
		setActivityView();
		mHandler = new Handler();

		pref = getApplicationContext().getSharedPreferences("WiPowerSharedPref", Context.MODE_WORLD_READABLE);
		update();
		button_1_checkserverpage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				update();
			}
		});
	}
	
	public void onResume()
	{
		super.onResume();
		update();
	}
	
	public void setActivityView()
	{
		setContentView(R.layout.check_server_page);
		button_1_checkserverpage = (Button)findViewById(R.id.button_1_checkserverpage);
		textV_1_checkserverpage = (TextView)findViewById(R.id.textV_1_checkserverpage);
		
		textV_baidu_1_checkserverpage = (TextView)findViewById(R.id.textV_baidu_1_checkserverpage);
		textV_sohu_1_checkserverpage = (TextView)findViewById(R.id.textV_sohu_1_checkserverpage);
		textV_sina_1_checkserverpage = (TextView)findViewById(R.id.textV_sina_1_checkserverpage);
		textV_yahoo_1_checkserverpage = (TextView)findViewById(R.id.textV_yahoo_1_checkserverpage);
		textV_baidu_2_checkserverpage = (TextView)findViewById(R.id.textV_baidu_2_checkserverpage);
		textV_sohu_2_checkserverpage = (TextView)findViewById(R.id.textV_sohu_2_checkserverpage);
		textV_sina_2_checkserverpage = (TextView)findViewById(R.id.textV_sina_2_checkserverpage);
		textV_yahoo_2_checkserverpage = (TextView)findViewById(R.id.textV_yahoo_2_checkserverpage);
		
		editV_1_checkserverpage = (EditText)findViewById(R.id.editV_1_checkserverpage);
		editV_1_checkserverpage.setText("Server Address: " + Constants.SERVER_ADDRESS);
		
		textViews1[0] = textV_baidu_1_checkserverpage;
		textViews1[1] = textV_sohu_1_checkserverpage;
		textViews1[2] = textV_sina_1_checkserverpage;
		textViews1[3] = textV_yahoo_1_checkserverpage;
		textViews2[0] = textV_baidu_2_checkserverpage;
		textViews2[1] = textV_sohu_2_checkserverpage;
		textViews2[2] = textV_sina_2_checkserverpage;
		textViews2[3] = textV_yahoo_2_checkserverpage;
	}
	
	public void update()
	{
		Random rd = new Random();
		number = rd.nextInt(100);
		
		numberStr = Float.toString(number);
        task_local = new GetXMLTaskLocal();
        task_local.execute(new String[] {numberStr});
        
        textV_1_checkserverpage.setBackgroundColor(Color.rgb(64, 64, 64)); 
        textV_1_checkserverpage.setText("Rolling ...");
		for(int i = 0; i  < 4; i ++)
		{
			textViews1[i].setBackgroundColor(Color.rgb(64, 64, 64));
			textViews2[i].setBackgroundColor(Color.rgb(64, 64, 64));
			textViews2[i].setText("Connecting...");
		}
        
		try
		{
        task_web_1 = new GetXMLTaskWeb();
        // task_web_1.execute(new Integer[]{Integer.valueOf(1)});
        task_web_1.executeOnExecutor(Constants.executor, new Integer[]{Integer.valueOf(1)});
        // task_web_1.get(3, TimeUnit.SECONDS);
        
        task_web_2 = new GetXMLTaskWeb();
        // task_web_2.execute(new Integer[]{Integer.valueOf(2)});
        task_web_2.executeOnExecutor(Constants.executor, new Integer[]{Integer.valueOf(2)});
        // task_web_2.get(3, TimeUnit.SECONDS);
        
        task_web_3 = new GetXMLTaskWeb();
        // task_web_3.execute(new Integer[]{Integer.valueOf(3)});
        task_web_3.executeOnExecutor(Constants.executor, new Integer[]{Integer.valueOf(3)});
        // task_web_3.get(3, TimeUnit.SECONDS);
        
        task_web_4 = new GetXMLTaskWeb();
        // task_web_4.execute(new Integer[]{Integer.valueOf(4)});
        task_web_4.executeOnExecutor(Constants.executor, new Integer[]{Integer.valueOf(4)});
        // task_web_4.get(3, TimeUnit.SECONDS);
		}
		catch(Exception ex){}
        
		// String serverAdr = pref.getString("server_address", "192.168.1.100");
		StringTokenizer tok = new StringTokenizer(editV_1_checkserverpage.getText().toString(), ": ");
		tok.nextToken();
		tok.nextToken();
		String addr = tok.nextToken();
		
		if(!addr.equalsIgnoreCase(Constants.SERVER_ADDRESS))
		{
			Editor editor = pref.edit();
			editor.putString("server_address", addr);
			editor.commit();
			
			Constants.SERVER_ADDRESS = addr;
			Constants.updateURL();
		}
	}

	public void onDestroy()
	{
		task_web_1.cancel(true);
		task_web_2.cancel(true);
		task_web_3.cancel(true);
		task_web_4.cancel(true);

		task_web_1 = null;
		task_web_2 = null;
		task_web_3 = null;
		task_web_4 = null;
		
		super.onDestroy();
	}
		

	class GetXMLTaskLocal extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... numberStrs) {
            String output = null;
            for (String numberStr : numberStrs) {
               //output = getOutputFromUrl(url);
            	output = getResultFromServlet(numberStr);
            }
            return output;
        }
        
        @Override
        protected void onPostExecute(String output) {
            textV_1_checkserverpage.setText(output);
            if(output.startsWith("Error"))
            	textV_1_checkserverpage.setBackgroundColor(Color.RED);
            else {
            	textV_1_checkserverpage.setBackgroundColor(Color.GREEN);
			}
        }
        
        
        private String getResultFromServlet(String text) {
    		String result = "";
    		InputStream in = callService(text);
    		if(in!=null) {
    			result= Constants.streamToString(in);
    		} else {
    			result = "Error Local Server: You roll " + (int)number + ", but server is not returning result";
    		}
    		return result;
    	}
        
        private InputStream callService(String text) {
            InputStream in = null;
            try {
                 URL url = new URL(Constants.URL_HELLO);
                 URLConnection conn = url.openConnection();
                 HttpURLConnection httpConn = (HttpURLConnection) conn;
                 httpConn.setRequestMethod("POST");
                 httpConn.setDoInput(true); 
                 httpConn.setDoOutput(true); 
                 httpConn.connect();
                 
                 DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
                 
                 dataStream.writeBytes(text);
                 dataStream.flush(); 
                 dataStream.close();

              int responseCode = httpConn.getResponseCode();
                 if (responseCode == HttpURLConnection.HTTP_OK) {
                      in = httpConn.getInputStream();
                 }
            } catch (Exception ex) {
            	
            }
            return in;
       }
    }
	
	
	class GetXMLTaskWeb extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			BufferedReader in = null;
			String result="";
			try {
	             URL url = new URL(WEB_URLS[params[0] - 1]);
	             URLConnection conn = url.openConnection();
	             conn.setRequestProperty("accept", "*/*");
	             conn.connect();
	             in = new BufferedReader(new InputStreamReader(
	            		 conn.getInputStream()));
	             String line;
	             while ((line = in.readLine()) != null) {
	                 if(line.contains("STATUS OK") || line.contains("html"))
	                	 return params[0];
	             }
	        } catch (Exception ex) {
	        	return -params[0];
	        }
			
			return -params[0];
		}
		
		@Override
        protected void onPostExecute(Integer output) {
			if(output >= 0)
			{
				textViews1[output - 1].setBackgroundColor(Color.GREEN);
				textViews2[output - 1].setBackgroundColor(Color.GREEN);
				textViews2[output - 1].setText("Connection SUCCESS");
			}
			else {
				output *= (-1);
				textViews1[output - 1].setBackgroundColor(Color.RED);
				textViews2[output - 1].setBackgroundColor(Color.RED);
				textViews2[output - 1].setText("Connection FAIL");
			}
		}
	}
	
	
	
}
