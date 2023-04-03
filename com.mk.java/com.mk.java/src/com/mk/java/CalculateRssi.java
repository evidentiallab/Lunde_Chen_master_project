package com.mk.java;

public class CalculateRssi {
	
//	public static final double coefficient_n_fois_10 = -34.454;
	public static double coefficient_n_fois_10;
	public static final double rssi_1_meter = -34;
	public static final double ksi = 85;
	
	public static void main(String[] args)
	{
		System.out.println("" + rssi(36));
	}
	
	public static double rssi(double distance)
	{
		coefficient_n_fois_10 = (-52 - rssi_1_meter - ksi) / StrictMath.log(15);
		return rssi_1_meter + coefficient_n_fois_10 * StrictMath.log(distance) + ksi;
	}
}
