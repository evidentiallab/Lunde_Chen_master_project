package com.mk.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class XplotToGnuplot_HONG {
	public static String xplotFileStr = "/home/mk/0902/0902_3_有了wakeclock/可以写进论文/0904/a2b_tput.xpl";
	public static String gnuFileStr = 	"/home/mk/0902/0902_3_有了wakeclock/可以写进论文/0904/a2b_tput_gnu_hong";
	
	public static void main(String[] args) throws IOException
	{
		File xplotFile = new File(xplotFileStr);
		File gnuFile = new File(gnuFileStr);
		if (gnuFile.exists()) {
			gnuFile.delete();
		}
		BufferedReader br = new BufferedReader(new FileReader(xplotFile));
		String line;
		int ok = -3;
		int startValue = -1;
		write("0 0", gnuFile); //
   	 	while((line = br.readLine())!= null)
   	 	{
   	 		if(ok >= 200)
   	 		{
   	 			StringTokenizer tok = new StringTokenizer(line, " .dot");
   	 			startValue = Integer.parseInt(tok.nextToken().substring(6) + tok.nextToken().substring(0, 3));
   	 			ok = 10;
   	 		}
   	 		else if(ok >= 0)
   	 		{
   	 			ok ++;
   	 			 if(line.equalsIgnoreCase("red"))
   	 			 {
   	 				 if(ok >= 100)
   	 					 ok = 200;
   	 				 else ok = 1;
   	 			 }
   	 			
   	 			if(ok == 2)
   	 			{
//   	 				StringTokenizer tok = new StringTokenizer(line, " .dot");
//   	 				String result = (Integer.parseInt(tok.nextToken().substring(6) + 
//   	 						tok.nextToken().substring(0, 3)) - startValue + 100) + 
//   	 						" " + Double.parseDouble(tok.nextToken()) / (1024d * 1024d);
   	 			StringTokenizer tok = new StringTokenizer(line, " .dot");
	 				String result = (Integer.parseInt(tok.nextToken().substring(6) + 
	 						tok.nextToken().substring(0, 3)) - startValue + 100) + 
	 						" " + Double.parseDouble(tok.nextToken()) / (2d * 1024d);
   	 				System.out.println(result);
   	 				write(result, gnuFile);
   	 			}
   	 		}
   	 		else {
   	 			if(line.contains("inst."))
   	 				ok ++;
   	 			if(line.contains("avg."))
   	 				ok ++;
				if(ok == -1)
					ok = 100;
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
