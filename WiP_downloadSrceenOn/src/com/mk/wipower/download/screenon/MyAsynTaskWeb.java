package com.mk.wipower.download.screenon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class MyAsynTaskWeb extends AsyncTask<Integer, Void, Integer> {

	@Override
	protected Integer doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		BufferedReader in = null;
		System.out.println("Asynctask " + params[0]);
		try {
             URL url = new URL(MainPage.WEB_URLS[MainPage.whichFileGroup * 4 + params[0] - 1]);
             URLConnection conn = url.openConnection();
             conn.setRequestProperty("accept", "*/*");
             conn.connect();
             in = new BufferedReader(new InputStreamReader(
            		 conn.getInputStream()));
             String line;
             while ((line = in.readLine()) != null) {
//                 System.out.println(line);
             }
        } catch (Exception ex) {
        	return -params[0];
        }
		
		return -params[0];
	}
	
	@Override
    protected void onPostExecute(Integer output) {
		System.out.println("Download Finished");
		if(output >= 0)
		{
		}
		else {
			output *= (-1);
		}
	}
}