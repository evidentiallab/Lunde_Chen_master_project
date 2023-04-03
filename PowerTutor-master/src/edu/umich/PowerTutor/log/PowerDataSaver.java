package edu.umich.PowerTutor.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PowerDataSaver {
	private Thread saveThread, readThread;
	private List<String> saveBuffer;
	
	public PowerDataSaver()
	{
		saveBuffer = new ArrayList<String>();
		
		if(readThread==null)
			readThread = new Thread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					read();
				}
			});
			
		
		if(saveThread==null)
			saveThread = new Thread(new Runnable() {
				
				public void run() {
					save();
				}
			});
	}
	
	public void start()
	{
		if(saveThread!=null)
		{
			PowerConstants.executor.execute(readThread);
			PowerConstants.executor.execute(saveThread);
		}
	}
	
	public void stop()
	{
		if(saveThread!=null)
			{
			saveThread.interrupt();
			readThread.interrupt();
			}
	}

	public void sleep() throws InterruptedException
	{
		TimeUnit.SECONDS.sleep(PowerConstants.POWER_DATA_SAVE_INTERVAL);
	}
	
	public void read()
	{
		while(PowerDataCollector.isLogging == true)
		{
		try {
			synchronized (saveThread) {
				String takeStr = PowerDataCollector.myArrBlockQueue.take();
				saveBuffer.add(takeStr);       /////////////////
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	public void save()
	{
		while(PowerDataCollector.isLogging == true)
		{
			try {
				sleep();
				
				synchronized (saveThread) {         ///????
					if (saveBuffer.size() == 0) {
						continue;
					}
				write(saveBuffer, 0);
				saveBuffer.clear();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void write(List<String> buf, int type)
	{
		 DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		 Date date = new Date();
		 String dateString = dateFormat2.format(date);
		String fileStr = PowerConstants.ROOT_DIR  + dateString+ "_power_log.txt";
		File file = new File(fileStr);
		write(buf, file);
	}
	
	
	public void write(List<String> buf, File file) {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			for (Iterator dataIterator=buf.iterator(); dataIterator.hasNext();) {
				printWriter.println(dataIterator.next());
			}		
			printWriter.flush();
			fileWriter.flush();
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
	
}
