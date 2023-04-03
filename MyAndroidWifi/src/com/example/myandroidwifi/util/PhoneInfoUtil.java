package com.example.myandroidwifi.util;

public class PhoneInfoUtil {

	public static boolean isOSVersion4dotx()
	{
		return android.os.Build.VERSION.SDK_INT>=14;
	}
	
	public static int getOSVersion()
	{
		return android.os.Build.VERSION.SDK_INT;
	}
}
