package com.mk.wipower.download.screenon;

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
		str += Utils.getNowTime() + "\n";
		str += "getBrightnessFloat : " + getBrightnessFloat() + "\n";
		//str += "layoutParam.aplha : " + layoutParams.alpha + "\n";
		//str += "layoutParams.width : " + layoutParams.width + "\n";
		//str += "layoutParams.height : " + layoutParams.height + "\n";
		str += "getBrightnessMode : " + getBrightnessMode() + "\n";
		return str;
	}
	
	public void resetBrightness(float brightness)
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
}
