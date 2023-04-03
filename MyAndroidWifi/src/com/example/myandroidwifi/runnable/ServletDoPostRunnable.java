package com.example.myandroidwifi.runnable;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;
import com.example.myandroidwifi.util.TrafficStatsUtil;
import com.example.myandroidwifi.util.WifiInfoUtil;



public class ServletDoPostRunnable implements Runnable{
    	String result = "";
    	public long fois_send = 0;
    	public long fois_received = 0;
		InputStream in = null;
    	public static String STRING_TO_THE_SERVLET = "C";
    	WifiInfoUtil mWifiInfoUtil;
		public Runnable logRunnable;
		public Runnable doPostRunnable;
		private Thread logThread;
		private Thread doPostThread;
		public long startTime;
		public String startStr;
	
	public ServletDoPostRunnable()
	{
		logRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(Constants.runningFlag)
				{
					if(fois_send>0)
						logResult();
					try {
						if(!Constants.low_power)
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_PACKETS);
						else {
							Thread.sleep(Constants.SLEEP_INTERVAL_LOG_PACKETS_LOW_POWER);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		};
		
		doPostRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Constants.length_of_string_received<=Constants.CHANGE_TO_WIFI_BACK_UP_THREHOLD)
				{
				try {
		             URL url = new URL(Constants.URL_WIFI);
		             URLConnection conn = url.openConnection();
		             HttpURLConnection httpConn = (HttpURLConnection) conn;
		             httpConn.setRequestMethod("POST");
		             httpConn.setDoInput(true); 
		             httpConn.setDoOutput(true); 
		             httpConn.connect();
		             DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
		             
		             /////
		             if(fois_received > (1000 / Constants.SLEEP_INTERVAL_SEND_PACKETS) * 5 * 60 && fois_received % ((1000 / Constants.SLEEP_INTERVAL_SEND_PACKETS) * 10) == 0)
		            	 STRING_TO_THE_SERVLET += "C";
		             /////
		             
		             dataStream.writeBytes(STRING_TO_THE_SERVLET);
		             dataStream.flush(); 
		             dataStream.close();
		     		 fois_send ++;
		     		 int responseCode = httpConn.getResponseCode();
		             if (responseCode == HttpURLConnection.HTTP_OK) {
		                  in = httpConn.getInputStream();
		                  fois_received ++;
		                  //////Ceci est indispensable, sinon la vitesse de ///////////////////////////
		                  //////transmission observee diminue tres significativement!!!!///////////////
		                  String receivedStr = Constants.streamToString(in);
		                  System.out.println(receivedStr.length());
		                  /////////////////////////////!!!!!!!!!!!!!!!!!!!!!///////////////////////
		             }
		        } catch (Exception ex) {
		        	LogUtil.logE(ex.getMessage());
		        }
				if(in!=null) {
						
				} else {
					result = "Error: Service not returning result";
				}
				}
				else {
					try {
						 long n = fois_send % 5L;
			             URL url = new URL(Constants.URL_WIFI + (n + 1));
			             URLConnection conn = url.openConnection();
			             HttpURLConnection httpConn = (HttpURLConnection) conn;
			             httpConn.setRequestMethod("POST");
			             httpConn.setDoInput(true); 
			             httpConn.setDoOutput(true); 
			             httpConn.connect();
			             DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
			             dataStream.writeBytes(STRING_TO_THE_SERVLET);
			             dataStream.flush(); 
			             dataStream.close();
			     		 fois_send ++;
			     		 int responseCode = httpConn.getResponseCode();
			             if (responseCode == HttpURLConnection.HTTP_OK) {
			                  in = httpConn.getInputStream();
			                  fois_received ++;
			                  //////Ceci est indispensable, sinon la vitesse de ///////////////////////////
			                  //////transmission observee diminue tres significativement!!!!///////////////
			                  String receivedStr = Constants.streamToString(in);
			                  System.out.println(receivedStr.length());
			                  /////////////////////////////!!!!!!!!!!!!!!!!!!!!!///////////////////////
			             }
			        } catch (Exception ex) {
			        	LogUtil.logE(ex.getMessage());
			        }
					if(in!=null) {
							
					} else {
						result = "Error: Service not returning result";
					}
				}
			}
		};
	}
	
	public void run() {
		// TODO Auto-generated method stub
		startStr = Constants.getNowTime();
		startTime = System.currentTimeMillis();
		logThread = new Thread(logRunnable);
		logThread.start();
        while(Constants.runningFlag)
        {
        (new Thread(doPostRunnable)).start();
		sleep();
		}
        // if(!logThread.isInterrupted())
        //	logThread.interrupt();
	}
	
	public void logResult()
	{
		String logStr = "";
		String strEnd = Constants.getNowTime();
        logStr += "from " + startStr + " to " + strEnd + "\n";
        long passedTime = System.currentTimeMillis() - startTime;
	    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
        logStr += fois_send + " times are sent" + "\n";
        logStr += fois_received + " times are received" + "\n";
        logStr += TrafficStatsUtil.getResult() + "\n";
        LogUtil.logServlet(logStr);
	}
	
	
	public void sleep()
	{
		try {
			Thread.sleep(Constants.SLEEP_INTERVAL_SEND_PACKETS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopLog()
	{
		if(logThread != null && !logThread.isInterrupted())
			logThread.interrupt();
	}
	
}
