/*
Copyright (C) 2011 The University of Michigan

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Please send inquiries to powertutor@umich.edu
*/

package edu.umich.PowerTutor.components;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import edu.umich.PowerTutor.log.PowerConstants;
import edu.umich.PowerTutor.phone.PhoneConstants;
import edu.umich.PowerTutor.service.IterationData;
import edu.umich.PowerTutor.service.PowerData;
import edu.umich.PowerTutor.util.Recycler;
import edu.umich.PowerTutor.util.SystemInfo;

public class Wifi extends PowerComponent {
  public static class WifiData extends PowerData {
    private static Recycler<WifiData> recycler = new Recycler<WifiData>();

    public static WifiData obtain() {
      WifiData result = recycler.obtain();
      if(result != null) return result;
      return new WifiData();
    }

    @Override
    public void recycle() {
    	logWifi("recycle");
      recycler.recycle(this);
    }

    public boolean wifiOn;
    public double packets;
    public long uplinkBytes;
    public long downlinkBytes;
    public double uplinkRate;
    public double linkSpeed;
    public int powerState;

    private WifiData() {
    }

    public void init(double packets, long uplinkBytes, long downlinkBytes,
                     double uplinkRate, double linkSpeed, int powerState) {
      wifiOn = true;
      this.packets = packets;
      logWifi("packets = " + packets);
      this.uplinkBytes = uplinkBytes;
      logWifi("uplinkBytes = " + uplinkBytes);
      this.downlinkBytes = downlinkBytes;
      logWifi("downlinkBytes = " + downlinkBytes);
      this.uplinkRate = uplinkRate;
      logWifi("uplinkRate = " + uplinkRate);
      this.linkSpeed = linkSpeed;
      logWifi("linkSpeed = " + linkSpeed);
      this.powerState = powerState;
      logWifi("powerState = " + powerState);
    }

    public void init() {
      logWifi("init");
      wifiOn = false;
    }

    public void writeLogDataInfo(OutputStreamWriter out) throws IOException {
      StringBuilder res = new StringBuilder();
      res.append("Wifi-on ").append(wifiOn).append("\n");
      if(wifiOn) {
        res.append("Wifi-packets ").append((long)Math.round(packets))
           .append("\nWifi-uplinkBytes ").append(uplinkBytes)
           .append("\nWifi-downlinkBytes ").append(downlinkBytes)
           .append("\nWifi-uplink ").append((long)Math.round(uplinkRate))
           .append("\nWifi-speed ").append((long)Math.round(linkSpeed))
           .append("\nWifi-state ").append(Wifi.POWER_STATE_NAMES[powerState])
           .append("\n");
      }
      out.write(res.toString());
    }
  }

  public static final int POWER_STATE_LOW = 0;
  public static final int POWER_STATE_HIGH = 1;
  public static final String[] POWER_STATE_NAMES = {"LOW", "HIGH"};

  private static final String TAG = "Wifi";

  private PhoneConstants phoneConstants;
  private WifiManager wifiManager;
  private SystemInfo sysInfo;
  
  private long lastLinkSpeed;
  private int[] lastUids;
  private WifiStateKeeper wifiState;
  private SparseArray<WifiStateKeeper> uidStates;

  private String transPacketsFile;
  private String readPacketsFile;
  private String transBytesFile;
  private String readBytesFile;
  private File uidStatsFolder;

  public Wifi(Context context, PhoneConstants phoneConstants) {
    this.phoneConstants = phoneConstants;
    wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    logWifi("getSystemService(Context.WIFI_SERVICE)");
    sysInfo = SystemInfo.getInstance();
    logWifi("sysInfo = SystemInfo.getInstance()");
    logWifi("wifiManager.getConnectionInfo(): " + wifiManager.getConnectionInfo().toString());
    logWifi("wifiManager.isWifiEnabled(): " + wifiManager.isWifiEnabled());
    
    /* Try to grab the interface name.  If we can't find it will take a wild
     * stab in the dark.
     */
    logWifi("Try to grab the interface name.  If we can't find it will take a wild stab in the dark.");
    String interfaceName = SystemInfo.getInstance().getProperty("wifi.interface");
    logWifi("interfaceName = SystemInfo.getInstance().getProperty(\"wifi.interface\")");
    logWifi("interfaceName: " + interfaceName);
    logWifi("SystemInfo.getInstance().getProperty(\"wifi.interface\")");
    /////////////IMPORTANT CHANGE///////if(interfaceName == null) interfaceName = "eth0";
    interfaceName = "lo";
    logWifi("interfaceName: " + interfaceName);
    
    lastLinkSpeed = -1;
    wifiState = new WifiStateKeeper(phoneConstants.wifiHighLowTransition(),
                                    phoneConstants.wifiLowHighTransition());
    logWifi("new WifiStateKeep(" + phoneConstants.wifiHighLowTransition() + ", " + phoneConstants.wifiLowHighTransition() + ")");
    uidStates = new SparseArray<WifiStateKeeper>();
    logWifi("new SparseArry<WifiStateKeeper>");
    transPacketsFile = "/sys/devices/virtual/net/" +
                       interfaceName + "/statistics/tx_packets";
    logWifi("transPacketsFile: " + transPacketsFile);
    readPacketsFile = "/sys/devices/virtual/net/" +
                      interfaceName + "/statistics/rx_packets";
    logWifi("readPacketsFile: " + readPacketsFile);
    transBytesFile = "/sys/devices/virtual/net/" +
                     interfaceName + "/statistics/tx_bytes";
    logWifi("transBytesFile: " + transBytesFile);
    readBytesFile = "/sys/devices/virtual/net/" +
                    interfaceName + "/statistics/rx_bytes";
    logWifi("readBytesFile: " + readBytesFile);
    uidStatsFolder = new File("/proc/uid_stat");
    logWifi("uidStatsFolder: " + "/proc/uid_stat");
  }

  @Override
  public IterationData calculateIteration(long iteration) {
	logWifi("calculateIteration " + iteration);
    IterationData result = IterationData.obtain();
    logWifi("result = IterationData.obtain()");

    int wifiStateFlag = wifiManager.getWifiState();
    logWifi("wifiStateFlag = wifiManager.getWifiState(): " + wifiStateFlag);
    if(wifiStateFlag != WifiManager.WIFI_STATE_ENABLED &&
       wifiStateFlag != WifiManager.WIFI_STATE_DISABLING) {
      /* We need to allow the real iterface state keeper to reset it's state
       * so that the next update it knows it's coming back from an off state.
       * We also need to clear all the uid information.
       */
      logWifi("wifiStateFlag != WifiManager.WIFI_STATE_ENABLED && " +
      		"wifiStateFlag != WifiManager.WIFI_STATE_DISABLING ");
      wifiState.interfaceOff();
      uidStates.clear();
      lastLinkSpeed = -1;

      WifiData data = WifiData.obtain();
      logWifi("data = WifiData.obtain()");
      data.init();
      logWifi("data.init()");
      result.setPowerData(data);
      logWifi("result.setPowerData(data)");
      return result;
    }

    long transmitPackets = sysInfo.readLongFromFile(transPacketsFile);
    long receivePackets = sysInfo.readLongFromFile(readPacketsFile);
    long transmitBytes = sysInfo.readLongFromFile(transBytesFile);
    long receiveBytes = sysInfo.readLongFromFile(readBytesFile);
    if(transmitPackets == -1 || receivePackets == -1 ||
       transmitBytes == -1 || receiveBytes == -1) {
      /* Couldn't read interface data files. */
      Log.w(TAG, "Failed to read packet and byte counts from wifi interface");
      logWifi("Failed to read packet and byte counts from wifi interface");
      return result;
    }

    /* Update the link speed every 15 seconds as pulling the WifiInfo structure
     * from WifiManager is a little bit expensive.  This isn't really something
     * that is likely to change very frequently anyway.
     */
    if(iteration % 15 == 0 || lastLinkSpeed == -1) {
      lastLinkSpeed = wifiManager.getConnectionInfo().getLinkSpeed();
      logWifi("lastLinkSpeed = wifiManager.getConnectionInfo().getLinkSpeed()" + lastLinkSpeed);
    }
    double linkSpeed = lastLinkSpeed;

    logWifi("wifiState.isInitialized()" + wifiState.isInitialized());
    if(wifiState.isInitialized()) {
      wifiState.updateState(transmitPackets, receivePackets,
                            transmitBytes, receiveBytes);
      WifiData data = WifiData.obtain();
      data.init(wifiState.getPackets(), wifiState.getUplinkBytes(),
                wifiState.getDownlinkBytes(), wifiState.getUplinkRate(),
                linkSpeed, wifiState.getPowerState());
      result.setPowerData(data);
      logWifi("result.setPowerData(data)");
    } else {
      wifiState.updateState(transmitPackets, receivePackets,
                            transmitBytes, receiveBytes);
      logWifi("wifiState.updateState " + transmitPackets + " "+ receivePackets + " "+ transmitBytes + " "+ receiveBytes);
    }

    lastUids = sysInfo.getUids(lastUids);
    logWifi("sysInfo.getUids(lastUids)");
    if(lastUids != null) for(int uid : lastUids) {
    	logWifi("uid:" + uid);
      if(uid == -1) {
        continue;
      }
      try {
        WifiStateKeeper uidState = uidStates.get(uid);
        logWifi("uidStates.get(uid)");
        if(uidState == null) {
        	logWifi("uidState == null");
          uidState = new WifiStateKeeper(phoneConstants.wifiHighLowTransition(),
                                    phoneConstants.wifiLowHighTransition());
          uidStates.put(uid, uidState);
        }

        if(!uidState.isStale()) {
          /* We use a huerstic here so that we don't poll for uids that haven't
           * had much activity recently.
           */
        	logWifi("!uidState.isStale()");
          continue;
        }
          
        /* These read operations are the expensive part of polling. */
        receiveBytes = sysInfo.readLongFromFile(
            "/proc/uid_stat/" + uid + "/tcp_rcv");
        logWifi("sysInfo.readLongFromFile " + "/proc/uid_stat/" + uid + "/tcp_rcv");
        logWifi("receiveBytes " + receiveBytes);
        transmitBytes = sysInfo.readLongFromFile(
            "/proc/uid_stat/" + uid + "/tcp_snd");
        logWifi("sysInfo.readLongFromFile " + "/proc/uid_stat/" + uid + "/tcp_snd");
        logWifi("transmitBytes " + transmitBytes);

        if(receiveBytes == -1 || transmitBytes == -1) {
          Log.w(TAG, "Failed to read uid read/write byte counts");
          logWifi("Failed to read uid read/write byte counts");
        } else if(uidState.isInitialized()) {
          /* We only have information about bytes received but what we really
           * want is the number of packets received so we just have to
           * estimate it.
           */
          logWifi("uidState.isInitialized()");
          long deltaTransmitBytes = transmitBytes - uidState.getTransmitBytes();
          long deltaReceiveBytes = receiveBytes - uidState.getReceiveBytes();
          long estimatedTransmitPackets = (long)Math.round(deltaTransmitBytes /
                                      wifiState.getAverageTransmitPacketSize());
          long estimatedReceivePackets = (long)Math.round(deltaReceiveBytes /
                                      wifiState.getAverageReceivePacketSize());
          if(deltaTransmitBytes > 0 && estimatedTransmitPackets == 0) {
            estimatedTransmitPackets = 1;
          }
          if(deltaReceiveBytes > 0 && estimatedReceivePackets == 0) {
            estimatedReceivePackets = 1;
          }

          boolean active = transmitBytes != uidState.getTransmitBytes() ||
                           receiveBytes != uidState.getReceiveBytes();
          uidState.updateState(
              uidState.getTransmitPackets() + estimatedTransmitPackets,
              uidState.getReceivePackets() + estimatedReceivePackets,
              transmitBytes, receiveBytes);
          logWifi(" uidState.updateState(uidState.getTransmitPackets() + " +
          		"estimatedTransmitPackets,uidState.getReceivePackets() + " +
          		"estimatedReceivePackets, transmitBytes, receiveBytes)");
          if(active) {
            logWifi("if(active) : true");
            WifiData uidData = WifiData.obtain();
            uidData.init(uidState.getPackets(), uidState.getUplinkBytes(),
                         uidState.getDownlinkBytes(), uidState.getUplinkRate(),
                         linkSpeed, uidState.getPowerState());
            result.addUidPowerData(uid, uidData);
            logWifi("result.addUidPowerData(uid, uidData)");
          }
        } else {
          uidState.updateState(0, 0, transmitBytes, receiveBytes);
          logWifi("uidState.updateState(0, 0, transmitBytes, receiveBytes) + " + transmitBytes + receiveBytes );
        }
      } catch(NumberFormatException e) {
        Log.w(TAG, "Non-uid files in /proc/uid_stat");
        logWifi("Non-uid files in /proc/uid_stat");
      }
    }

    return result;
  }

  private static class WifiStateKeeper {
    private long lastTransmitPackets;
    private long lastReceivePackets;
    private long lastTransmitBytes;
    private long lastReceiveBytes;
    private long lastTime;

    private int powerState;
    private double lastPackets;
    private double lastUplinkRate;
    private double lastAverageTransmitPacketSize;
    private double lastAverageReceivePacketSize;

    private long deltaUplinkBytes;
    private long deltaDownlinkBytes;

    private double highLowTransition;
    private double lowHighTransition;

    private long inactiveTime;

    public WifiStateKeeper(double highLowTransition, double lowHighTransition) {
      this.highLowTransition = highLowTransition;
      this.lowHighTransition = lowHighTransition;
      lastTransmitPackets = lastReceivePackets = lastTransmitBytes =
          lastTime = -1;
      powerState = POWER_STATE_LOW;
      lastPackets = lastUplinkRate = 0;
      lastAverageTransmitPacketSize = 1000;
      lastAverageReceivePacketSize = 1000;
      inactiveTime = 0;
    }

    public void interfaceOff() {
      lastTime = SystemClock.elapsedRealtime();
      powerState = POWER_STATE_LOW;
    }

    public boolean isInitialized() {
      return lastTime != -1;
    }

    public void updateState(long transmitPackets, long receivePackets,
                            long transmitBytes, long receiveBytes) {
      long curTime = SystemClock.elapsedRealtime();
      if(lastTime != -1 && curTime > lastTime) {
        double deltaTime = curTime - lastTime;
        lastUplinkRate = (transmitBytes - lastTransmitBytes) / 1024.0 *
                            7.8125 / deltaTime;
        lastPackets = receivePackets + transmitPackets -
                      lastReceivePackets - lastTransmitPackets;
        deltaUplinkBytes = transmitBytes - lastTransmitBytes;
        deltaDownlinkBytes = receiveBytes - lastReceiveBytes;
        if(transmitPackets != lastTransmitPackets) {
          lastAverageTransmitPacketSize = 0.9 * lastAverageTransmitPacketSize +
                                  0.1 * (transmitBytes - lastTransmitBytes) /
                                  (transmitPackets - lastTransmitPackets);
        }
        if(receivePackets != lastReceivePackets) {
          lastAverageReceivePacketSize = 0.9 * lastAverageReceivePacketSize +
                                  0.1 * (receiveBytes - lastReceiveBytes) /
                                  (receivePackets - lastReceivePackets);
        }

        if(receiveBytes != lastReceiveBytes ||
           transmitBytes != lastTransmitBytes) {
          inactiveTime = 0;
        } else {
          inactiveTime += curTime - lastTime;
        }

        if(lastPackets < highLowTransition) {
          powerState = POWER_STATE_LOW;
        } else if(lastPackets > lowHighTransition) {
          powerState = POWER_STATE_HIGH;
        }
      }
      lastTime = curTime;
      lastTransmitPackets = transmitPackets;
      lastReceivePackets = receivePackets;
      lastTransmitBytes = transmitBytes;
      lastReceiveBytes = receiveBytes;
    }

    public int getPowerState() {
    	logWifi("getPowerState()" + powerState);
      return powerState;
    }

    public double getPackets() {
    	logWifi("getPackets()" + lastPackets);
      return lastPackets;
    }

    public long getUplinkBytes() {
    	logWifi("getUplinkBytes()" + deltaUplinkBytes);
      return deltaUplinkBytes;
    }

    public long getDownlinkBytes() {
    	logWifi("getDownlinkBytes()" + deltaDownlinkBytes);
      return deltaDownlinkBytes;
    }

    public double getUplinkRate() {
    	logWifi("getUplinkRate()" + lastUplinkRate);
      return lastUplinkRate;
    }

    public double getAverageTransmitPacketSize() {
    	logWifi("getAverageTransmitPacketSize()" + lastAverageTransmitPacketSize);
      return lastAverageTransmitPacketSize;
    }

    public double getAverageReceivePacketSize() {
    	logWifi("getAverageReceivePacketSize()" + lastAverageReceivePacketSize);
      return lastAverageReceivePacketSize;
    }

    public long getTransmitPackets() {
    	logWifi("getTransmitPackets()" + lastTransmitPackets);
      return lastTransmitPackets;
    }

    public long getReceivePackets() {
    	logWifi("getReceivePackets()" + lastReceivePackets);
      return lastReceivePackets;
    }

    public long getTransmitBytes() {
    	logWifi("getTransmitBytes()" + lastTransmitBytes);
      return lastTransmitBytes;
    }
  
    public long getReceiveBytes() {
      logWifi("getReceiveBytes()" + lastReceiveBytes);
      return lastReceiveBytes;
    }

    /* The idea here is that we don't want to have to read uid information
     * every single iteration for each uid as it just takes too long.  So here
     * we are designing a hueristic that helps us avoid polling for too many
     * uids.
     */
    public boolean isStale() {
      long curTime = SystemClock.elapsedRealtime();
      return curTime - lastTime > (long)Math.min(10000, inactiveTime);
    }
  }

  private long readLongFromFile(String filePath) {
	logWifi("readLongFromFile: " + filePath);
    return sysInfo.readLongFromFile(filePath);
  }

  @Override
  public boolean hasUidInformation() {
      logWifi("hasUidInformation() " + uidStatsFolder.exists());
    return uidStatsFolder.exists();
  }

  @Override
  public String getComponentName() {
      logWifi("getComponentName() " + "WifiFIFIFI");
    return "Wifi";
  }
  
  
  public static void logWifi(String str)
	{
		 DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHH");
		 Date date = new Date();
		 String dateString = dateFormat2.format(date);
		String fileStr = PowerConstants.ROOT_DIR  + dateString+ "_wifi_log.txt";
		 File file = new File(fileStr);
		 write(str, file);
	}
	
	
	public static void write(String str, File file) {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(str);
			printWriter.flush();
			fileWriter.flush();
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}
