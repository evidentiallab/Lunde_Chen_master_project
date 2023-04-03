package com.mk.wipower.ipt_wni;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServletRunnable implements Runnable{
    	String result = "";
    	public long fois_send = 0;
    	public long fois_received = 0;
		InputStream in = null;
		
    	public static String STRING_TO_THE_SERVLET = "C";
    	public static int LENGHT_STR_FROM_SERVLET = 1;
		public static int SLEEP_INTERVAL_SEND_PACKETS = 100;
		
		public static String SERVER_ADDRESS = "192.168.1.100";
		public static String URL;
		public static String URL_WIFI;
		public static String URL_HELLO; 
		public static String URL_REGULATION;
		
		public static ExecutorService executor = Executors.newCachedThreadPool();
		public static boolean runFlag = false;
		
	public ServletRunnable()
	{
		updateURL();
		System.out.println("ServletRunnable Construction finished");
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
		if(runFlag)
		{
			System.out.println("ServletRunnable if");
			(new Thread(getServletRunnable(LENGHT_STR_FROM_SERVLET))).start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        while(runFlag)
        {
        	System.out.println("ServletRunnable while");
        	(new Thread(getServletRunnable(-1))).start();
        	sleep();
		}
	}
	
	public Runnable getServletRunnable(final int lengthS)
	{	
		return new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
		             URL url = new URL(URL_WIFI);
		             URLConnection conn = url.openConnection();
		             HttpURLConnection httpConn = (HttpURLConnection) conn;
		             httpConn.setRequestMethod("POST");
		             httpConn.setDoInput(true); 
		             httpConn.setDoOutput(true); 
		             httpConn.connect();
		             DataOutputStream dataStream = new DataOutputStream(conn.getOutputStream());
		             
		             /////
		             // if(fois_received > (1000 / SLEEP_INTERVAL_SEND_PACKETS) * 5 * 60 && fois_received % ((1000 / SLEEP_INTERVAL_SEND_PACKETS) * 10) == 0)
		             //	 STRING_TO_THE_SERVLET += "C";
		             /////
		             
		             if(lengthS <= 0)
		                dataStream.writeBytes(Integer.toString(lengthS));
		             else
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
		                  String receivedStr = streamToString(in);
		                  System.out.println(receivedStr.length());
		                  /////////////////////////////!!!!!!!!!!!!!!!!!!!!!///////////////////////
		             }
		        } catch (Exception ex) {
		        	
		        }
				if(in!=null) {
						
				} else {
					result = "Error: Service not returning result";
				}
			}
		};
	}
	
	public void sleep()
	{
		try {
			System.out.println("sleep");
			Thread.sleep(SLEEP_INTERVAL_SEND_PACKETS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateURL()
	{
		System.out.println(SERVER_ADDRESS);
		URL = "http://" + SERVER_ADDRESS + ":8080/HelloMyWifi/";
		// public final static String URL = "http://192.168.1.100:8080/HelloMyWifi/";
		URL_WIFI = URL + "wifi_wni_ipt"; ///!!!!!!!!!!!!!!!!!!!!!
		URL_HELLO = URL + "hello"; 
		URL_REGULATION = URL + "regulation";
	}
	
	public static String streamToString(InputStream is) 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				// sb.append(line + "\n");
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
