package com.example.myandroidwifi.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppInfoUtil {
	
	private Context context;
	private PackageManager packageManager;
	public static ApplicationInfo myAppInfo;
	public static int myUid;
	
	public AppInfoUtil(Context contxt)
	{
		this.context = contxt;
		this.packageManager = context.getPackageManager();
	}
	
	public ApplicationInfo getMyApplicationInfo()
	{
		ApplicationInfo uidInfo = null;
		try {
			uidInfo =  packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uidInfo;
	}

	public static ApplicationInfo getMyApplicationInfoStatic()
	{
		return myAppInfo;
	}
	
	public static int getMyUid()
	{
		return myUid;
	}
	
	public void init()
	{
		myAppInfo = getMyApplicationInfo();
		myUid = myAppInfo.uid;
		LogUtil.logV("myUid : " + myUid);
	}
}
