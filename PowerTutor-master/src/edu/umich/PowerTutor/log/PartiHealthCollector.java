package edu.umich.PowerTutor.log;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.TextView;
import edu.umich.PowerTutor.service.ICounterService;
import edu.umich.PowerTutor.service.UMLoggerService;
import edu.umich.PowerTutor.service.UidInfo;
import edu.umich.PowerTutor.util.Counter;
import edu.umich.PowerTutor.util.SystemInfo;

public class PartiHealthCollector {
	public static final int KEY_CURRENT_POWER = 0;
	public static final int KEY_AVERAGE_POWER = 1;
	public static final int KEY_TOTAL_ENERGY = 2;
	public static boolean isLogging = false;
	public static ArrayBlockingQueue<String> myArrBlockQueue2;
	private Context context;
	private Thread myThread = null;
	private ICounterService icounterService;
	private MyCounterServiceConnection connection;
	private UidInfo[] uidInfos;
	String [] componentNames;
	int noUidMask;
	private int uid;
	private Intent serviceIntent;
	TextView textV_2;
	SystemInfo sysInfo;
	PackageManager pm;
	String logStr = "";
	
	
	class MyCounterServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			icounterService = ICounterService.Stub.asInterface((IBinder)service);
	        // DataDisplay.tv2_displayTwoLn("onServiceConnected");
			try {
		        componentNames = icounterService.getComponents();
		        noUidMask = icounterService.getNoUidMask();
		        // DataDisplay.tv2_displayTwoLn("noUidMask: " + noUidMask);
		        refreshView();
		        // handler.post(infoCollector);
			} catch (Exception e) {
				// TODO: handle exception
		        icounterService = null;
		        // DataDisplay.tv2_displayTwoLn("onServiceConnectedException:  icounterService = null");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			icounterService = null;
			context.unbindService(connection);
			context.bindService(serviceIntent, connection, 0);
		}
	}
	
	public PartiHealthCollector(Context context)
	{
		// this.context = ContextUtil.GetGlobleApplicationContext();
		this.context = context;
		uid = SystemInfo.AID_ALL;
		noUidMask = 0;
		serviceIntent = new Intent(context, UMLoggerService.class);
		connection = new MyCounterServiceConnection();
	    context.bindService(serviceIntent, connection, 0);
	    sysInfo = SystemInfo.getInstance();
	    pm = context.getPackageManager();
	    myArrBlockQueue2 = new  ArrayBlockingQueue<String>(PowerConstants.ARR_BLOCK_QUEUE_SIZE);
	    
	    if(!PowerConstants.createPath()) ;
	    	// DataDisplay.tv2_displayOneLn("Create Path Fail.");
	    else ;//DataDisplay.tv2_displayOneLn("Create Path Success.");
	    
	   //  DataDisplay.tv2_displayOneLn("ThreadId in PowerDataCollector: " + Thread.currentThread().getId());
		
		if(null==myThread)
		{
			myThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 while(isLogging)
					 {
						 try {
							sleep();
							// System.out.println(getLogData(KEY_CURRENT_POWER));
						    myArrBlockQueue2.add(getLogData(KEY_CURRENT_POWER));
						    System.out.println(myArrBlockQueue2.toString());
							logStr = "";
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				}
			});
		}
	}
	
	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	public String getLogData(int keyId)
	{
		 DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss ");
		 Date date = new Date();
		 String dateString = dateFormat2.format(date);
		 logStr = dateString + " ";
		
		String contentToShow = "";
	    double total = 0.0; //////////////////////////////////
		try {
		 if(icounterService!=null);
			//{
			// long totals[] = icounterService.getTotals(uid, Counter.WINDOW_TOTAL);   /////////////////
			// logStr += "uid: " + uid + " ";
			//tv3_display("totals.length: " + totals.length);
			//tv3_display("componentNames.length: " + componentNames.length);
			//logStr += "GENERAL POWER USAGE: ";
			//for(int i=0; i<totals.length&&i<componentNames.length;i++)
			//{
			//	logStr += componentNames[i] + " :" + totals[i] + " mJ" + " ";
			//}
			//}
			//else {
			//	// logStr += "infoCollector: icounterService == null" + " ";
			//	logStr = "";
			//}
			 
	     byte[] rawUidInfo = icounterService.getUidInfo(
         Counter.WINDOW_TOTAL,
		        noUidMask | 0);     //////////////////////******////////////
		 if(rawUidInfo != null) {
		 uidInfos = (UidInfo[])new ObjectInputStream(
		            new ByteArrayInputStream(rawUidInfo)).readObject(); 
		// logStr += "noUidMask: "+ noUidMask + " ";
		// logStr += "noUidMask | 0: "+ (noUidMask | 0)+ " ";
		// logStr += "uidInfos.length: "+ uidInfos.length+ " ";
		 for(UidInfo uidInfo : uidInfos)
		 {
			  if(uidInfo.uid == SystemInfo.AID_ALL) continue;
	          switch(keyId) {
	            case KEY_CURRENT_POWER:
	              uidInfo.key = uidInfo.currentPower;
	              uidInfo.unit = "W";
	              contentToShow = "Current";
	              break;
	            case KEY_AVERAGE_POWER:
	              uidInfo.key = uidInfo.totalEnergy /
	                  (uidInfo.runtime == 0 ? 1 : uidInfo.runtime);
	              uidInfo.unit = "W";
	              contentToShow = "Average Power";
	              break;
	            case KEY_TOTAL_ENERGY:
	              uidInfo.key = uidInfo.totalEnergy;
	              uidInfo.unit = "J";
	              contentToShow = "Total";
	              break;
	            default:
	              uidInfo.key = uidInfo.currentPower;
	              uidInfo.unit = "W";
	              contentToShow = "Current";
	          }
	          total += uidInfo.key;
		 }
		 if(total == 0) total = 1;
		 for(UidInfo uidInfo : uidInfos) {
			 uidInfo.percentage = 100.0 * uidInfo.key / total;
			 String name = sysInfo.getUidName(uidInfo.uid, pm);
			 
			// if(name.contains("PowerTutor"))
			// {
			//	 logStr += name.toUpperCase() + " ";
				 // logStr += "uid: " + uidInfo.uid+ " ";
			//	 logStr += contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit+ " ";
			//     logStr += "Total: " + uidInfo.totalEnergy + " mJ"+ " ";
			//     logStr += "Average: " + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW"+ " ";
			//     logStr += "Runtime: " + uidInfo.runtime + " seconds"+ " ";
			//	 logStr += "Percentage: " + doubleOne(uidInfo.percentage) + "%"+ " ";
			//     for(int i=0;i<componentNames.length;i++)
			//     {
			//    	 logStr += componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW"+ " ";
			//     }
			     //logStr += "runtime: " + uidInfo.runtime + " sec"+ " ";
			     //tv2_displayOneLn("PowerTutor\nruntime:\n" + uidInfo.runtime + " seconds");
			     //tv2_displayTwoLn("GetMeans: \n" + icounterService.getMeans(uidInfo.uid, Counter.WINDOW_TOTAL)[0] + " mW"); /////******/////
			//	 }
			 if(name.contains("Participatory") && name.contains("Health"))
			 {
				 logStr += name.toUpperCase() + " ";
				 // logStr += "uid: " + uidInfo.uid+ " ";
				 logStr += contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit+ " ";
			     logStr += "Total: " + uidInfo.totalEnergy + " mJ"+ " ";
			     logStr += "Average: " + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW"+ " ";
			    // logStr += "Runtime: " + uidInfo.runtime + " seconds"+ " ";
				 logStr += "Percentage: " + doubleOne(uidInfo.percentage) + "%"+ " ";
		     for(int i=0;i<componentNames.length;i++)
		     {
		    	 logStr += componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW"+ " ";
		     }
		     //logStr += "runtime: " + uidInfo.runtime + " sec"+ " ";
		     //tv2_displayOneLn("PartiHealth\nruntime:\n" + uidInfo.runtime + " seconds");
		     //tv2_displayTwoLn("GetMeans: \n" + icounterService.getMeans(uidInfo.uid, Counter.WINDOW_TOTAL)[0] + " mW"); /////******/////
			 }
			 
			// if(name.contains("UC"))
			// {
			//	 logStr += "UC WEB BROWSER" + " ";
				 // logStr += "uid: " + uidInfo.uid+ " ";
			//	 logStr += contentToShow + ": " + uidInfo.key + " m" + uidInfo.unit+ " ";
			//     logStr += "Total: " + uidInfo.totalEnergy + " mJ"+ " ";
			//     logStr += "Average: " + uidInfo.totalEnergy / (uidInfo.runtime == 0 ? 1 : uidInfo.runtime) + " mW"+ " ";
			//     logStr += "Runtime: " + uidInfo.runtime + " seconds"+ " ";
			//	 logStr += "Percentage: " + doubleOne(uidInfo.percentage) + "%"+ " ";
		    // for(int i=0;i<componentNames.length;i++)
		    // {
		    //	 logStr += componentNames[i] + " : " + icounterService.getComponentHistory(1, i, uidInfo.uid)[0] + " mW"+ " ";
		    // }
		     //logStr += "runtime: " + uidInfo.runtime + " sec"+ " ";
		     //tv2_displayOneLn("PartiHealth\nruntime:\n" + uidInfo.runtime + " seconds");
		     //tv2_displayTwoLn("GetMeans: \n" + icounterService.getMeans(uidInfo.uid, Counter.WINDOW_TOTAL)[0] + " mW"); /////******/////
			// }
			 
		 }
		 }
		 }catch(Exception e)
		 {
		 }
		return logStr;
	}
	
	public void start()
	{
		if(myThread!=null)
		{
			 PowerConstants.executor.execute(myThread);
			// myThread.start();
		}
	}
	
	public void stop()
	{
		if(myThread!=null)
		{
			myThread.interrupt();
			context.unbindService(connection);
		}
	}
	
	
	private void sleep() throws InterruptedException {
		TimeUnit.SECONDS.sleep(PowerConstants.POWER_DATA_COLLECT_INTERVAL);
	}

	
	
	public void refreshView()
	{
		 if(icounterService == null)
		 {
			//  DataDisplay.tv2_displayOneLn("icounterService == null ");
		 }
		 else {
			 // DataDisplay.tv2_displayOneLn("refreshView: icounterService != null ");
		}
		
		if(uid == SystemInfo.AID_ALL) {
		      /* If we are reporting global power usage then just set noUidMask to 0 so
		       * that all components get displayed.
		       */
		     noUidMask = 0;
		    //  DataDisplay.tv2_displayOneLn("refreshView: noUidMask: "+ noUidMask);
		    }
	}
	
	public String doubleOne(double num)
    {
    	DecimalFormat df = new DecimalFormat("#.0");
    	String str = df.format(num);
    	return str;
    }
	

	
	
	
}
