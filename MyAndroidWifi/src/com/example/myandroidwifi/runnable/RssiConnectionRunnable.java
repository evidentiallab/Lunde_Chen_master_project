package com.example.myandroidwifi.runnable;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.example.myandroidwifi.util.Constants;

public class RssiConnectionRunnable implements Runnable{
	public static int fois = 0;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(fois == 0)
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
            		fois ++;
            		Constants.URL_WIFI += receivedStr;
            		System.out.println(Constants.URL_WIFI);
            	}
            }
            else {
           	 return;
			}
			}
            catch (Exception ex)
 			{
 			}
		
		while(Constants.runningFlag)
		{
			try {
	             URL url_wifi = new URL(Constants.URL_WIFI);
	             URLConnection conn = url_wifi.openConnection();
	             HttpURLConnection httpConn = (HttpURLConnection) conn;
	             httpConn.setRequestMethod("POST");
	             httpConn.setDoInput(true); 
	             httpConn.setDoOutput(true); 
	             httpConn.connect();
	             DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
	             if(fois == 1)
	            	 dataStream.writeBytes(1 + "");
	             else {
	            	 dataStream.writeBytes("C");
				}
	    		 
	             dataStream.flush(); 
	             dataStream.close();

	             int responseCode = httpConn.getResponseCode();
	             if (responseCode == HttpURLConnection.HTTP_OK) {
	             	System.out.println("Server connection : OK");
	             	InputStream in = httpConn.getInputStream();
	             	String receivedStr = Constants.streamToString(in);
	             	fois ++;
	             	}
	             else {}
	 			} 
			catch (Exception ex)
	 			{
				fois = 0;
	 			}
			try{
				Thread.sleep(Constants.SLEEP_INTERVAL_RSSI_CONNECTION);
			}catch(Exception e)
			{
			}
		}
	}
}
