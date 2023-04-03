package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CalculateAverageValue {
	public static final String ROOT_DIR = "/home/mk/0916/0916_2111_59_RandomWalk_1000000/";
	public static final String INPUT_FILE =  ROOT_DIR 
			+ "Primitive4_VERY_STRONG";
	public static final String OUTPUT_RESULT = INPUT_FILE + ".average";
	
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
		int count = 0;
		double total = 0;
//		br.readLine();
		while((line = br.readLine()) != null)
		{
			total += Double.parseDouble(line);
			count ++;
		}
		writeIntoFile(OUTPUT_RESULT, "Average = " + ((double) total / count));
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
