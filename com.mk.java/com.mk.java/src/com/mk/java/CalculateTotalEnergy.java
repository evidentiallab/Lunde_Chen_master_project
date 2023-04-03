package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class CalculateTotalEnergy {
	public static final String ROOT_DIR = "/home/mk/0904/";
	public static final String INPUT_CSV =  ROOT_DIR 
			+ "chenlunde20140903_download_1_files_3Mb_yoga_chez_intel_42dBm.csv.result";

	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		FileReader fr = new FileReader(new File(INPUT_CSV));
		BufferedReader br = new BufferedReader(fr);
		String line;
		long count = 0;
		double total = 0;
		br.readLine();
		while((line = br.readLine()) != null)
		{
			total += Double.parseDouble(line);
		}
		
		System.out.println("Total = " + total);
		System.out.println("Total * 3.7 = " + total * 3.7);
	}
}
