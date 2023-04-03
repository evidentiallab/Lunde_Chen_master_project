package com.mk.wipower.ipt_wni;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

public class ScreenUtil{
	public WindowManager.LayoutParams layoutParams;
	public int brightnessMode;
	public Activity activity;
	
	public ScreenUtil(Activity actvty)
	{
		this.activity = actvty;
		initScreen();
	}
	
	public void initScreen()
	{
		layoutParams = activity.getWindow().getAttributes();
		try {
			brightnessMode = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getScreenResult()
	{
	initScreen();
	String str = "";
	str += getNowTime() + "\n";
	str += "getBrightnessFloat : " + getBrightnessFloat() + "\n";
	//str += "layoutParam.aplha : " + layoutParams.alpha + "\n";
	//str += "layoutParams.width : " + layoutParams.width + "\n";
	//str += "layoutParams.height : " + layoutParams.height + "\n";
	str += "getBrightnessMode : " + getBrightnessMode() + "\n";
	return str;
	}
	
	public void resetBrightness(int brightness)
	{
		if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
		    Settings.System.putInt(activity.getContentResolver(), 
		    		Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		layoutParams.screenBrightness = (float) (brightness * 0.01); // set brightness
		activity.getWindow().setAttributes(layoutParams);
	}

	public float getBrightnessFloat()
	{
		 return layoutParams.screenBrightness;
	}
	
	public String getBrightnessString()
	{
		return Settings.System.SCREEN_BRIGHTNESS;
	}
	
	public String getBrightnessMode()
	{
		switch (brightnessMode) {
		case 0:
			return "SCRENN_BRIGHTNESS_MODE_MANUAL";
		case 1:
			return "SCRENN_BRIGHTNESS_MODE_AUTOMATIC";
		default:
			return "SCRENN_BRIGHTNESS_MODE_UNKNOWN";
		}
	}
	
	////////////////////////
	
	public static String getNowTime()
	{
		 DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
		 Date date = new Date();
		 return dateFormat.format(date);
	}
	
	public static String getNowTime(String formatStr)
	{
		 DateFormat dateFormat = new SimpleDateFormat(formatStr);
		 Date date = new Date();
		 return dateFormat.format(date);
	}
}
