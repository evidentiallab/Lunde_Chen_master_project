package com.example.myandroidwifi.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.widget.Toast;

public class WifiInfoUtil {
	private Context context;
	private WifiManager wifiManager;
	private android.net.wifi.WifiInfo wifiInfo;
	private DhcpInfo dhcpInfo;
	private WifiLock wifiLock;
	
	public WifiInfoUtil(Context con, String page)
	{
		this.context = con;
		this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.wifiInfo = wifiManager.getConnectionInfo();
		this.dhcpInfo = wifiManager.getDhcpInfo();
		this.wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, page);
	}
	
	public void aquireWifiLock()
	{
		wifiLock.acquire();
	}
	
	public void releaseWifiLock()
	{
		wifiLock.release();
	}
	
	public WifiInfo getWifiInfo()
	{
		return wifiInfo;
	}
	
	public WifiManager getWifiManager()
	{
		return wifiManager;
	}
	
	public String getBSSID()
	{
		return wifiInfo.getBSSID();
	}
	
	public String getSSID()
	{
		return wifiInfo.getSSID();
	}
	
	public int getIpAdress()
	{
		return wifiInfo.getIpAddress();
	}
	
	public int getLinkSpeedMbps()
	{
		return wifiInfo.getLinkSpeed();
	}
	
	public String getMacAdress()
	{
		return wifiInfo.getMacAddress();
	}
	
	public int getReceivedSignalStrengthIdicator()
	{
		return wifiInfo.getRssi();
	}
	
	public double getChanel() //GHz
	{
		return 0.0;
	}
	
	public boolean isWifiConnected() {  
        if (context != null) {  
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager  
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            if (mWiFiNetworkInfo != null) {  
                return mWiFiNetworkInfo.isAvailable();  
            }  
        }  
        return false;  
    }
	
	public String getWifiState()
	{
		String state = "WIFI_STATE_UNKNOWN";
		switch (wifiManager.getWifiState()) {
		case  0:
			state = "WIFI_STATE_DISABLING";
			break;
		case  1:
			state = "WIFI_STATE_DISABLED";
			break;
		case  2:
			state = "WIFI_STATE_ENABLING";
			break;
		case  3:
			state = "WIFI_STATE_ENABLED";
			break;
		case  4:
			state = "WIFI_STATE_UNKNOWN";
			break;
		default:
			break;
		}
		return state;
	}
	
	public boolean isWifiEnabled()
	{
		return wifiManager.isWifiEnabled();
	}
	
	public boolean getHiddenSSID()
	{
		// true if this network does not broadcast its SSID, so an SSID-specific probe request must be used for scans.
		return wifiInfo.getHiddenSSID();
	}
	
	@SuppressWarnings("static-access")
	public NetworkInfo.DetailedState getDetailedState(SupplicantState supplicantState)
	{
		return wifiInfo.getDetailedStateOf(supplicantState);
	}
	
	public String toString()
	{
		String str = "";
		if(isWifiConnected())
		{
		str += "BBSSID : " + getBSSID() + "\n";
		str += "SSID : " + getSSID() + "\n";
		str += "getHiddenSSID : " + getHiddenSSID() + "\n"; 
		str += "IP : " + getIpAdress() + "\n";
		str += "LinkSpeed : " + getLinkSpeedMbps() + " Mbps\n";
		str += "MacAdress : " + getMacAdress() + "\n";
		str += "WifiState : " + getWifiState() + "\n";
		str += "DhcpInfo : " + dhcpInfo.toString() + "\n";
		str += "Rssi : " + getReceivedSignalStrengthIdicator() + "\n";
		str += wifiInfo.toString() + "\n";
		}
		else {
			str = "Wifi is not connected. \n";
		}
		return str;
	}
	
	
			public static void setIpAssignment(String assign , WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
			setEnumField(wifiConf, assign, "ipAssignment");
			}

			public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
			NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException{
			Object linkProperties = getField(wifiConf, "linkProperties");
			if(linkProperties == null)return;
			Class laClass = Class.forName("android.net.LinkAddress");
			Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
			Object linkAddress = laConstructor.newInstance(addr, prefixLength);

			ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
			mLinkAddresses.clear();
			mLinkAddresses.add(linkAddress);
			}

			public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException{
			Object linkProperties = getField(wifiConf, "linkProperties");
			if(linkProperties == null)return;
			Class routeInfoClass = Class.forName("android.net.RouteInfo");
			Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
			Object routeInfo = routeInfoConstructor.newInstance(gateway);

			ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
			mRoutes.clear();
			mRoutes.add(routeInfo);
			}

			public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
			Object linkProperties = getField(wifiConf, "linkProperties");
			if(linkProperties == null)return;

			ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
			mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
			mDnses.add(dns);
			}

			public static Object getField(Object obj, String name)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
			Field f = obj.getClass().getField(name);
			Object out = f.get(obj);
			return out;
			}

			public static Object getDeclaredField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			Object out = f.get(obj);
			return out;
			}

			public static void setEnumField(Object obj, String value, String name)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
			Field f = obj.getClass().getField(name);
			f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
			}


	
	public void openWifi()
	{
		wifiManager.setWifiEnabled(true);
	}
	
	public void closeWifi()
	{
		wifiManager.setWifiEnabled(false);
	}
	
	public void setBadWifi2dotx()
	{
		android.provider.Settings.System.putString(context.getContentResolver(),
				android.provider.Settings.System.WIFI_USE_STATIC_IP, "1");
	    android.provider.Settings.System.putString(context.getContentResolver(), 
	    		android.provider.Settings.System.WIFI_STATIC_DNS1, "202.112.128.51");
	    android.provider.Settings.System.putString(context.getContentResolver(), 
	    		android.provider.Settings.System.WIFI_STATIC_DNS2, "192.168.2.2");
	    android.provider.Settings.System.putString(context.getContentResolver(), 
	    		android.provider.Settings.System.WIFI_STATIC_GATEWAY, "192.168.0.5");
	    android.provider.Settings.System.putString(context.getContentResolver(), 
	    		android.provider.Settings.System.WIFI_STATIC_NETMASK, "255.255.255.0");
	    String ip = "50";
	    Random rd = new Random();
		 ip = Integer.toString(50 + rd.nextInt()%40);
	    android.provider.Settings.System.putString(context.getContentResolver(), 
	    		android.provider.Settings.System.WIFI_STATIC_IP, "192.168.1." + ip);
	    Toast.makeText(context, "set (bad) static ip : " + "192.168.1." + ip, Toast.LENGTH_LONG).show();
		WifiInfoUtil mWifiInfoUtil = new WifiInfoUtil(context, "NewPage");
		MainUtil mmUtil = new MainUtil(context);
		mmUtil.popUpAlert(mWifiInfoUtil.toString());
	}
	
	public void setGoodWifi2dotx()
	{
		android.provider.Settings.System.putString(context.getContentResolver(),
				android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");
		WifiInfoUtil mWifiInfoUtil = new WifiInfoUtil(context, "NewPage");
		MainUtil mmUtil = new MainUtil(context);
		mmUtil.popUpAlert(mWifiInfoUtil.toString());
	}
	
	public void setBadWifi4dotx()
	{
		String ip = "50";
		WifiConfiguration mWifiConfiguration = null;
		WifiInfoUtil mWifiInfoUtil = new WifiInfoUtil(context, "NewPage");
		WifiManager mWifiManager = mWifiInfoUtil.wifiManager;
		List<WifiConfiguration> configuredNetworkes = mWifiManager.getConfiguredNetworks();
		for(WifiConfiguration conf : configuredNetworkes)
		{
			if(conf.networkId == mWifiInfoUtil.wifiInfo.getNetworkId())
				mWifiConfiguration = conf;
			break;
		}
		 try{
		     setIpAssignment("STATIC", mWifiConfiguration); //or "DHCP" for dynamic setting
			 Random rd = new Random();
			 ip = Integer.toString(50 + rd.nextInt()%40);
		     setIpAddress(InetAddress.getByName("192.168.1." + ip), 24, mWifiConfiguration);
		     setGateway(InetAddress.getByName("192.168.0.2"), mWifiConfiguration);
		     setDNS(InetAddress.getByName("127.127.127.127"), mWifiConfiguration);
		     mWifiManager.updateNetwork(mWifiConfiguration); //apply the setting
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		WifiInfoUtil mWifiInfoUtil2 = new WifiInfoUtil(context, "NewPage");
		MainUtil mmUtil = new MainUtil(context);
		mmUtil.makeToast("set (bad) static ip : " + "192.168.1." + ip, true);
		mmUtil.popUpAlert(mWifiInfoUtil2.toString());
	}
	
	public void setGoodWifi4dotx()
	{
		//String ip = "150";
		WifiConfiguration mWifiConfiguration = null;
		WifiInfoUtil mWifiInfoUtil = new WifiInfoUtil(context, "NewPage");
		WifiManager mWifiManager = mWifiInfoUtil.wifiManager;
		List<WifiConfiguration> configuredNetworkes = mWifiManager.getConfiguredNetworks();
		for(WifiConfiguration conf : configuredNetworkes)
		{
			if(conf.networkId == mWifiInfoUtil.wifiInfo.getNetworkId())
				mWifiConfiguration = conf;
			break;
		}
		 try{
		     setIpAssignment("DHCP", mWifiConfiguration); //or "DHCP" for dynamic setting
		     //Random rd = new Random();
			 //ip = Integer.toString(150 + rd.nextInt()%40);
		     //setIpAddress(InetAddress.getByName("192.168.1." + ip), 24, mWifiConfiguration);
		     //setGateway(InetAddress.getByName("192.168.1.1"), mWifiConfiguration);
		     //setDNS(InetAddress.getByName("202.112.128.51"), mWifiConfiguration);
		     mWifiManager.updateNetwork(mWifiConfiguration); //apply the setting
		    }catch(Exception e){
		        e.printStackTrace();
		    }
		WifiInfoUtil mWifiInfoUtil2 = new WifiInfoUtil(context, "NewPage");
		MainUtil mmUtil = new MainUtil(context);
		//mmUtil.makeToast("set (good) static ip : " + "192.168.1." + ip, true);
		mmUtil.popUpAlert(mWifiInfoUtil2.toString());
	}
	
}
