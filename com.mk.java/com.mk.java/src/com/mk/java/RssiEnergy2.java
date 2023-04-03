package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class RssiEnergy2 {
	public static final String ROOT_DIR = "/home/mk/0831/";
	public static final String INPUT_CSV =  ROOT_DIR 
			+ "2";
//	public static final String OUTPUT_RESULT = INPUT_CSV + ".result";
	public static final String OUTPUT_RESULT = ROOT_DIR + "3";
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
		int count = 1;
//		br.readLine();
		while((line = br.readLine()) != null)
		{
			StringTokenizer tok = new StringTokenizer(line, " ");
			String r = tok.nextToken();
//			double e1_tmp =  Double.parseDouble(tok.nextToken()) * 13671 / 70.819663;
			double e1 = 0, e2 = 0, e3 = 0;
			e1 = Double.parseDouble(tok.nextToken());
			e2 = Double.parseDouble(tok.nextToken());
			e3 = Double.parseDouble(tok.nextToken());
			if(count >=6 && count <=14)
			{
				e1 -= 2000d;
				e2 -= 2000d;
				e3 -= 2000d;
			}
			writeIntoFile(OUTPUT_RESULT, r + " " + e1 + " " + e2 + " " + e3);
			count ++;
		}
		
		
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
