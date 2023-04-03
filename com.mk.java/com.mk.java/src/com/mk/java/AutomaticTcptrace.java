package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.io.filefilter.SuffixFileFilter;

public class AutomaticTcptrace {

	private static Process exec;
	private static int exitcode;
	private static final String dirName = "/home/mk/0902/0902_3_有了wakeclock/";
	
	
	public static void main(String[] args)
	{
		File dir = new File(dirName);
		
		String[] files = dir.list(new SuffixFileFilter(".pcap"));
		
		 for (int i = 0; i < files.length; i++) {
		     System.out.println(files[i]);
		     File resultFile = new File(dirName + files[i] + ".result");
		     
		     try {
		    	System.out.println("/usr/bin/tcptrace -lr " + dirName + files[i] + "\n");
//		    	exec = Runtime.getRuntime().exec("/usr/bin/tcptrace -lr " + dirName + files[i] + " > " + dirName + files[i] + ".result\n"); //这样不行
		    	exec = Runtime.getRuntime().exec("/usr/bin/tcptrace -lr " + dirName + files[i] + "\n");
//		    	exec = Runtime.getRuntime().exec("/usr/bin/tcptrace -Ty " + dirName + files[i] + "\n");
		    	
		    	String line;
		    	
		    	BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));  //
				while ((line = br.readLine()) != null) {
					System.out.println(line);
					write(line, resultFile);
				}
				
				BufferedReader r = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
				while ((line = r.readLine()) != null) {
					System.out.println(line);
				}
				
				if (exec != null)
					exitcode = exec.waitFor();
				else {
					System.out.println("exec == null");
				}
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
