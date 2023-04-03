package com.mk.java;

import org.apache.commons.math3.distribution.NormalDistribution;

public class IntNormalSampling {
	public int delta;
	
	public void sample()
	{
		double a = (new NormalDistribution(0, 2.5)).sample();
		delta = (int) Math.abs(a) % 6;
		if(a < 0)
			delta *= -1;
		System.out.println("delta = " + delta);
	}
	
	public static void main(String[] args)
	{
		System.out.println(" -15 % 4 = " + (-15 % 4));
		System.out.println(" -17 % 4 = " + (-17 % 4));
		System.out.println(" -12 % 4 = " + (-12 % 4));
		System.out.println(" -12 % 5 = " + (-12 % 5));
		System.out.println(" -8 % 6 = " + (-8 % 6));
		
		while(true)
		{
			new IntNormalSampling().sample();
		}
		
	}
}
