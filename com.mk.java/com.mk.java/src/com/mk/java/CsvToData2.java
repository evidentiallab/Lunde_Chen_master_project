package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class CsvToData2 {
	public static final String ROOT_DIR = "/home/mk/0904/";
	public static final String INPUT_CSV =  ROOT_DIR 
			+ "chenlunde20140903_download_1_files_3Mb_yoga_chez_intel_42dBm.csv";
	public static final String OUTPUT_RESULT = INPUT_CSV + ".result";
	
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		try
		{
			File file = new File(OUTPUT_RESULT);
			if(file.exists())
				file.delete();
		}
		catch(Exception e){}
		
		FileReader fr = new FileReader(new File(INPUT_CSV));
		BufferedReader br = new BufferedReader(fr);
		String line;
		long count = 0;
		br.readLine();
		while((line = br.readLine()) != null)
		{
			StringTokenizer tok = new StringTokenizer(line, ",");
			tok.nextToken();
//			if(count >= 60 * 5000 && count <= 100 * 5000)
//			{
//				
//			}
//			else if(count >= 25 * 5000 && count <= 32 * 5000)
//			{
//				
//			}
//			else if(count >= 117 * 5000)
//			{
//				
//			}
//			else 
				if(count <= 0.60 * 5000)
			{
				
			}
			else 
				if(count % 50 == 0)
				{
					writeIntoFile(OUTPUT_RESULT, (Double.parseDouble(tok.nextToken()) + 3d) + "");
				}
			count ++;
		}
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