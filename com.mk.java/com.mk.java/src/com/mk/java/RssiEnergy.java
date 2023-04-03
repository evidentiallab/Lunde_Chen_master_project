package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class RssiEnergy {

	public static final String ROOT_DIR = "/home/mk/0831/";
	public static final String INPUT_CSV =  ROOT_DIR 
			+ "rssi_result_2";
	public static final String OUTPUT_RESULT = INPUT_CSV + ".result";
	public static double coe[][] =
		{	{1.7, 2.2}, //1
			{1.4, 1.8}, //2
			{1.3, 1.6}, //3
			{1.2, 1.5}, //4
			{1.2, 1.4}, //5
		};
	
	
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
		int count = 0;
		br.readLine();
		while((line = br.readLine()) != null)
		{
			StringTokenizer tok = new StringTokenizer(line, " ");
			String r = tok.nextToken();
			double e1_tmp =  Double.parseDouble(tok.nextToken()) * 13671 / 70.819663;
			double e1 = 0, e2 = 0, e3 = 0;
			if(count < 5)
			{
				e1 = e1_tmp * (1 +  (7 - count) / 10) * (1 + (new UniformRealDistribution(0.1, 0.3)).sample());
				e2 = e1_tmp * coe[count][0] * (1 + (new UniformRealDistribution(0.1, 0.3)).sample());
				e3 = e1_tmp * coe[count][1] * (1 + (new UniformRealDistribution(0.1, 0.3)).sample());
			}
			else if(count < 8)
			{
				e1 = e1_tmp * (1 + (new UniformRealDistribution(0.1, 0.3)).sample());
				e2 = e1 * (1 + (new UniformRealDistribution(0.1, 0.4)).sample());
				e3 = e2 * (1 + (new UniformRealDistribution(0.1, 0.4)).sample());
			}
			else if(count <= 11)
			{
				e1 = e1_tmp * (1 + (new UniformRealDistribution(0.1, 0.2)).sample());
				e2 = e1 * (1 + (new UniformRealDistribution(0.1, 0.3)).sample());
				e3 = e2 * (1 + (new UniformRealDistribution(0.1, 0.2)).sample());
			}
			else {
				e1 = e1_tmp;
				e2 = e1 * (1 + (new UniformRealDistribution(0, (double)(30d - count) / 70d )).sample());
				e3 = e1 * (1 + (new UniformRealDistribution(0, 0.2)).sample());
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
