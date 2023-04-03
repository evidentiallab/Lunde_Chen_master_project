package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class LowFiltering {
	public static final String ROOT_DIR = "/home/mk/0905/0906_2354_07_RandomWalk_3000000_Directional/";
	public static final String INPUT_FILE =  ROOT_DIR + "Green_TimeCountOneSession";
	public static final String OUTPUT_RESULT = INPUT_FILE + ".result";
	
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		try
		{
			File file = new File(OUTPUT_RESULT);
			if(file.exists())
				file.delete();
		}
		catch(Exception e){}
		
		FileReader fr = new FileReader(new File(INPUT_FILE));
		BufferedReader br = new BufferedReader(fr);
		String line;
		long count = 0;
		int sessionLength;
//		br.readLine();
		while((line = br.readLine()) != null)
		{
			try {
//				System.out.println(line);
				count ++;
				sessionLength = Integer.parseInt(line);
				if(sessionLength == 2)
				{
					if((new UniformRealDistribution()).sample() >= 0.4)
					{
						writeIntoFile(OUTPUT_RESULT, line);
					}
				}
				else if(sessionLength == 3)
				{
					if((new UniformRealDistribution()).sample() >= 0.2)
					{
						writeIntoFile(OUTPUT_RESULT, line);
					}
				}
				else {
					writeIntoFile(OUTPUT_RESULT, line);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		System.out.println("" + count);
		br.close();
	}
	
	public static void writeIntoFile(String fileUrl, String str) throws IOException
	{
		FileWriter fileWriter = new FileWriter(new File(fileUrl), true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println(str);
		printWriter.flush();
		fileWriter.flush();
		printWriter.close();
		fileWriter.close();	
	}

}
