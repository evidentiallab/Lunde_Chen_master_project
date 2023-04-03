package edu.umich.PowerTutor.log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PowerConstants {
	public static ExecutorService executor = Executors.newCachedThreadPool();
	public static int POWER_DATA_COLLECT_INTERVAL = 1; // unit: seconds
	public static int POWER_DATA_SAVE_INTERVAL = 1; // unit: seconds
	public static int ARR_BLOCK_QUEUE_SIZE = 50000;
	public static final String ROOT_DIR = "/mnt/sdcard/PowerTutorData/";
	
	public static boolean createPath()
	{
		File file = null;
		file = new File(ROOT_DIR);
		if(!file.exists())
			if(!file.mkdir())
				return false;
		return true;
	}
	
	/**
	 * Unfinished
	 * @return
	 */
	public static String getDate()
	{
		return null;
	}
	
}
