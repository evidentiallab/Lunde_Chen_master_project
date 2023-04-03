package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.filefilter.PrefixFileFilter;

public class AutomaticStastics {
	private static Process exec;
	private static int exitcode;
	private static final String dirName = "/home/mk/0903/02_stationary/";
	
	
	public static void main(String[] args)
	{
		File dir = new File(dirName);
		
		String[] files = dir.list(new PrefixFileFilter("2014090315"));
		
		 for (int i = 0; i < files.length; i++) {
		     System.out.println(files[i]);
		     File resultFile = new File(dirName + files[i] + ".result");
		     HashMap<Integer, Long> map = new HashMap<Integer, Long>();
		     int fois = 0;
		     long total = 0L;
		     
		     try {
		    	 BufferedReader br = new BufferedReader(new FileReader(new File(dirName + files[i])));
		    	 String line;
		    	 while((line = br.readLine())!= null)
		    	 {
		    		 int rssi = Integer.parseInt(line);
		    		 if(map.keySet().contains(rssi))
		 				map.put(rssi, map.get(rssi)+1);
		 			else {
		 				map.put(rssi, 1L);
		 			}
		    		 total += rssi;
		    		 fois ++;
		    	 }
		    	 br.close();
		    	 
		    	 Set<Integer> set = map.keySet();
		    	 Iterator<Integer> itorator = set.iterator();
		 		 DecimalFormat df = new DecimalFormat("#.0000");
		 		 while(itorator.hasNext())
		 	     {
		 	        	int mrssi = itorator.next();
		 	        	Long mfois = map.get(mrssi);
		 	        	write(mrssi +  "   " + mfois + "   " + df.format(100.0000 * mfois / fois), resultFile);
		 	     }
		 		 
		 		 write("In average (dBm):   " +  df.format(total / fois), resultFile);
		 		 
		 		 double average = (double)total / (double)fois;
		 		 
		 		br = new BufferedReader(new FileReader(new File(dirName + files[i])));
		 		double dv = 0d;
		 		while((line = br.readLine())!= null)
		    	 {
		    		 int rssi = Integer.parseInt(line);
		    		 dv += Math.pow((rssi - average), 2);
		    	 }
		 		br.close();
		 		write("sdv:   " +  (double)dv / (double)fois, resultFile); 
		 		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	}
	
	public static void write(String str, File file) {
		try {
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
				}
				FileWriter fileWriter = new FileWriter(file, true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.println(str);
				printWriter.flush();
				fileWriter.flush();
				printWriter.close();
				fileWriter.close();
			} catch(IOException e)
			{}
	}
}
