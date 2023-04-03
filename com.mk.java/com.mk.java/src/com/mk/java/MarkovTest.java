package com.mk.java;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class MarkovTest {

	public static final double[][] matA = {
		{0.2, 0.4, 0.4}, 
		{0.1, 0.6, 0.3},
		{0.1, 0.3, 0.6}}; 
	
	public static final double[][] matB = 
		{
		{0.9, 0.1}, 
		{0.2, 0.8}
		}; 
	
	public int multiNomialSample(double... params)
	{
		double sum = 0;
		double e = 0.001;
		for(int i = 0; i < params.length; i ++)
		{
			sum += params[i];
		}
		if(sum < 1 - e || sum > 1 + e)
		 return -1;
		
		int which = 0;
		double point = params[0];
		// Don't use the Random() of java, it sucks!!!
		double random = new UniformRealDistribution().sample();   
		while(point <= random)
		{
			which ++;
			point += params[which];
		}
		return which;
	}
	
	public void verifierMultiNomial()
	{
		int A = 0, B = 0, C = 0, D = 0;
		int which;
		for(int i = 0; i < 50000; i ++)
		{
			which = multiNomialSample(matA[0]);
			if(which == 0)
				A ++;
			if(which == 1)
				B ++;
			if(which == 2)
				C ++;
			D ++;
		}
		System.out.println("A = " + A + ", that's  " + (double) A / D);
		System.out.println("B = " + B + ", that's  " + (double) B / D);
		System.out.println("C = " + C + ", that's  " + (double) C / D);
	}
	
	public void verifierMarkov()
	{
		int A = 0, B = 0, C = 0;
		int which;
		double[] vector = matB[0];
		for(int i = 0; i < 500000; i ++)
		{
			which = multiNomialSample(vector);
			if(which == 0)
			{
				A ++;
				C ++;
				vector = matB[0];
				System.out.println("A");
			}
			else if(which == 1)
			{
				B ++;
				C ++;
				vector = matB[1];
			}
		}
		System.out.println("A = " + A + ", that's  " + (double) A / C);
		System.out.println("B = " + B + ", that's  " + (double) B / C);
	}
	
	public static void main(String[] args)
	{
		MarkovTest test = new MarkovTest();
		test.verifierMarkov();
	}
	
	
}
