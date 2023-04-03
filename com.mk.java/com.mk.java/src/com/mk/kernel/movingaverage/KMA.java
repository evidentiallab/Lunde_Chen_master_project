package com.mk.kernel.movingaverage;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

import weka.estimators.KernelEstimator;

public class KMA {
	
	public KernelEstimator ke;
	
	public KMA()
	{
		System.out.println((new NormalDistribution()).density(0));
		
		this.ke = new KernelEstimator(0.01);
		while (true) {
			go();
		}
	}
	
	public void go()
	{
		ke.addValue((new NormalDistribution()).sample(), 0.001);
		System.out.println(ke.toString());
		System.out.println("Real value: " + (new NormalDistribution()).density(0));
		System.out.println("Prob (0): " + ke.getProbability(0));
		System.out.println(Arrays.toString(ke.getMeans()));
		System.out.println(ke.getNumKernels());
		System.out.println("\n");
		sleep();
	}
	
	public void sleep()
	{
		try {
			Thread.sleep(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args)
	{
		new KMA();
	}
}
