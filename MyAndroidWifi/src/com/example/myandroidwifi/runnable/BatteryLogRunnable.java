package com.example.myandroidwifi.runnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.myandroidwifi.page.ScreenPlusWifiPage;
import com.example.myandroidwifi.util.Constants;
import com.example.myandroidwifi.util.LogUtil;

import android.os.Process;

public class BatteryLogRunnable implements Runnable{
	private Method methodReadProcFile;
	public static final int PROC_SPACE_TERM = (int)' ';
	public static final int PROC_OUT_LONG = 0x2000;
	private static final int[] READ_LONG_FORMAT = new int[] {
	    PROC_SPACE_TERM|PROC_OUT_LONG
	  };
	private long[] readBuf;

	  private static final String[] VOLTAGE_FILES = {
	    "/sys/class/power_supply/battery/voltage_now",
	    "/sys/class/power_supply/battery/batt_vol",
	  };
	  private static final double[] VOLTAGE_CONV = {
	    1e-6, // Source in microvolts.
	    1e-3, // Source in millivolts.
	  };
	  
	  private static final String MIN_VOLTAGE_FILE = "/sys/class/power_supply/battery/voltage_max_design";
	  private static final String MAX_VOLTAGE_FILE = "/sys/class/power_supply/battery/voltage_min_design";

	  private static final String[] CURRENT_FILES = {
	    "/sys/class/power_supply/battery/current_now",
	    //"/sys/class/power_supply/battery/batt_current", Doesn't seem good
	  };
	  private static final double[] CURRENT_CONV = {
	    1e-6, // Source in microamps.
	    //1e-3, // Source in milliamps.
	  };

	  private static final String[] TEMP_FILES = {
	    "/sys/class/power_supply/battery/temp",
	    "/sys/class/power_supply/battery/batt_temp",
	  };
	  private static final double[] TEMP_CONV = {
	    1e-1, // Source in tenths of a centigrade.
	    1e-1, // Source in tenths of a centigrade.
	  };

	  private static final String[] CHARGE_FILES = {
	    "/sys/class/power_supply/battery/charge_counter",
	  };
	  private static final double[] CHARGE_CONV = {
	    60*60*1e-6, // Source in micro amp hours.
	  };

	  private static final String[] CAPACITY_FILES = {
	    "/sys/class/power_supply/battery/capacity",
	  };
	  private static final double[] CAPACITY_CONV = {
	    1e-2, // Source in percentage.
	  };

	  private static final String[] FULL_CAPACITY_FILES = {
	    "/sys/class/power_supply/battery/full_bat",
	  };
	  private static final double[] FULL_CAPACITY_CONV = {
	    60*60*1e-6, // Source in micro amp hours.
	  };
	  String voltageFile;
	  String currentFile;
	  String tempFile;
	  String chargeFile;
	  String capacityFile;
	  String fullCapacityFile;

	  double voltageConv;
	  double currentConv;
	  double tempConv;
	  double chargeConv;
	  double capacityConv;
	  double fullCapacityConv;
	  
	  long startTime;
	  
	  public void stopLog()
		{
			
		}
	  
	  public void init()
	  	{
		  startTime = System.currentTimeMillis();
		  
		  try {
		        methodReadProcFile = Process.class.getMethod("readProcFile", String.class,
		            int[].class, String[].class, long[].class, float[].class);
		      } catch(NoSuchMethodException e) {
		         
		      }
		    readBuf = new long[1];
		    // Get voltage information.
		    for(int i = 0; i < VOLTAGE_FILES.length; i++) {
		      if(new File(VOLTAGE_FILES[i]).exists()) {
		        voltageFile = VOLTAGE_FILES[i];
		        voltageConv = VOLTAGE_CONV[i];
		      }
		    }

		    // Get current information.
		    for(int i = 0; i < CURRENT_FILES.length; i++) {
		      if(new File(CURRENT_FILES[i]).exists()) {
		        currentFile = CURRENT_FILES[i];
		        currentConv = CURRENT_CONV[i];
		      }
		    }

		    // Get temperature information.
		    for(int i = 0; i < TEMP_FILES.length; i++) {
		      if(new File(TEMP_FILES[i]).exists()) {
		        tempFile = TEMP_FILES[i];
		        tempConv = TEMP_CONV[i];
		      }
		    }

		    // Get charge information.
		    for(int i = 0; i < CHARGE_FILES.length; i++) {
		      if(new File(CHARGE_FILES[i]).exists()) {
		        chargeFile = CHARGE_FILES[i];
		        chargeConv = CHARGE_CONV[i];
		      }
		    }

		    // Get capacity information.
		    for(int i = 0; i < CAPACITY_FILES.length; i++) {
		      if(new File(CAPACITY_FILES[i]).exists()) {
		        capacityFile = CAPACITY_FILES[i];
		        capacityConv = CAPACITY_CONV[i];
		      }
		    }

		    // Get full capacity information.
		    for(int i = 0; i < FULL_CAPACITY_FILES.length; i++) {
		      if(new File(FULL_CAPACITY_FILES[i]).exists()) {
		        fullCapacityFile = FULL_CAPACITY_FILES[i];
		        fullCapacityConv = FULL_CAPACITY_CONV[i];
		      }
		    }
	  }
	  
	  	public boolean hasVoltage() {
		    return voltageFile != null;
		  }

		  public double getVoltage() {
		    if(voltageFile == null) {
		    	return -1.0;
		    }
		    long volt = readLongFromFile(voltageFile);
		    return volt == -1 ? -1.0 : voltageConv * volt;
		  }

		  public boolean hasCurrent() {
		    return currentFile != null;
		  }

		  public double getCurrent() {
		    long curr = readLongFromFile(currentFile);
		    return curr == -1 ? -1.0 : currentConv * curr;
		  }

		  public boolean hasTemp() {
		    return tempFile != null;
		  }

		  public double getTemp() {
		    if(tempFile == null) {
		    	return -1.0;
		    }
		    long temp = readLongFromFile(tempFile);
		    return temp == -1 ? -1.0 : tempConv * temp;
		  }

		  public boolean hasCharge() {
		    return chargeFile != null ||
		           hasFullCapacity() && hasCapacity();
		  }

		  public double getCharge() {
		    if(chargeFile == null) {
		      double r1 = getCapacity();
		      double r2 = getFullCapacity();
		      return r1 < 0 || r2 < 0 ? -1.0 : r1 * r2;
		    }
		    long charge = readLongFromFile(chargeFile);
		    return charge == -1 ? -1.0 : chargeConv * charge;
		  }

		  public boolean hasCapacity() {
		    return capacityFile != null;
		  }

		  public int getCapacity() {
		    if(capacityFile == null) return -100;
		    long cap = readLongFromFile(capacityFile);
		    return (int) (cap == -1 ? -1.0 : capacityConv * cap * 100);
		  }

		  public boolean hasFullCapacity() {
		    return fullCapacityFile != null;
		  }

		  public double getFullCapacity() {
		    if(fullCapacityFile == null) return -1.0;
		    long cap = readLongFromFile(fullCapacityFile);
		    return cap == -1 ? -1.0 : fullCapacityConv * cap;
		  }
		  public double getMinVoltage()
		  {
			  File file = new File(MIN_VOLTAGE_FILE);
			  if(file.exists())
				  return readLongFromFile(MIN_VOLTAGE_FILE) * voltageConv;
			  else return 0;
		  }
		  
		  public double getMaxVoltage()
		  {
			  File file = new File(MAX_VOLTAGE_FILE);
			  if(file.exists())
				  return readLongFromFile(MAX_VOLTAGE_FILE) * voltageConv;
			  else return 0;
		  }
		  
		  /* Returns -1 on failure. */
		  public long readLongFromFile(String file) {
		    if(methodReadProcFile == null) return -1;
		    try {
		      if((Boolean)methodReadProcFile.invoke(
		          null, file, READ_LONG_FORMAT, null, readBuf, null)) {
		        return readBuf[0];
		      }
		    } catch(IllegalAccessException e) {
		    } catch(InvocationTargetException e) {
		    }
		    return -1L;
		  }
			
			public String getLogString()
			{
				 String logStr = "";
				 String dateString = Constants.getNowTime();
				 logStr = dateString + "" + "\n";
				    long passedTime = System.currentTimeMillis() - startTime;
				    logStr += "passed time : " + Constants.timeMillisToString(passedTime)+ "\n";
				 	logStr += percent + "\n";
				    // logStr += getVoltage() + "\n";
					return logStr;
			}
			
			public void sleep()
			{
				try {
					Thread.sleep(Constants.SLEEP_INTERVAL_READ_AND_LOG_BATTERY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				init();
				while(Constants.runningFlag)
				{
					int mpercent = (int) getCapacity();
					if(mpercent!=percent)
						{
						percent = mpercent;
						LogUtil.logBattery(getLogString());
						if(percent <= Constants.low_power_threhold)
							Constants.low_power = true;
						}
					/*
					String dateString = Constants.getNowTime();
					if(dateString.endsWith("00 "))
					{
					    long passedTime = System.currentTimeMillis() - startTime;
						LogUtil.logTime_Battery( 
								dateString + "  " + getCapacity() 
								+ "   ( passed time :  " + Constants.timeMillisToString(passedTime) + " )");
					}
					*/
					sleep();
				}
			}
			
			
			
			public int percent = 101;
			

}
