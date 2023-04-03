package edu.umich.PowerTutor.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class TextDisplayService extends Service{
	String fileStr;
	public static String msg;
	public final static int GREATEST_BYTES_TO_SHOW = 300; 
	public final static int BUFFER_SIZE_IN_KB = 20;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("onBind");
		return new ServiceBinder();
	}

	public void execute(Context ctx, ServiceConnection conn, String fileStr)
	{
		msg = "";
		File file = new File(fileStr);
		InputStreamReader reader;
		FileInputStream input;
		if(!file.exists())
			msg = "File not exists";

		else {
			if(file.length()>=200 * 1024)
				{
				msg = "The file size is " + (int)file.length()/1024 + " Kb, too big to show all contents.\n";
				msg += "We will show the first " + GREATEST_BYTES_TO_SHOW  + " Kb of it:\n\n";
				}
			else 
				msg = "The file size is " + (int)file.length()/1024 + " Kb. \n\n";
			
			int count = 0;
			try {
				input = new FileInputStream(file);
				reader = new InputStreamReader(input,"gb2312");
				char[]buffer = new char[1024 * BUFFER_SIZE_IN_KB];
				int readedBytes = 0;
				while((readedBytes=reader.read(buffer))!=-1 && count<GREATEST_BYTES_TO_SHOW / BUFFER_SIZE_IN_KB){				
					{
						msg+=new String(buffer,0,readedBytes);
						count ++;
					}
			}
				reader.close();
				input.close();			
			} catch (FileNotFoundException e) {		
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}
		ctx.bindService(new Intent(ctx, TextDisplayService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
	public void onDestroy()
	{
		super.onDestroy();
	}
}
