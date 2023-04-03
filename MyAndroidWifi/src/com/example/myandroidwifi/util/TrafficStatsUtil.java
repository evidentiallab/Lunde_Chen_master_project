package com.example.myandroidwifi.util;

import android.net.TrafficStats;

public class TrafficStatsUtil {

	public static long init_RxBytes, init_TxBytes, init_RxPackets, init_TxPackets;
	
	public static void init()
	{
		init_RxBytes = TrafficStats.getTotalRxBytes();
		init_TxBytes = TrafficStats.getTotalTxBytes();
		init_RxPackets = TrafficStats.getTotalRxPackets();
		init_TxPackets = TrafficStats.getTotalTxPackets();
		LogUtil.logV("init_RxBytes  : " + init_RxBytes);
		LogUtil.logV("init_TxBytes  : " + init_TxBytes);
		LogUtil.logV("init_RxPackets  : " + init_RxPackets);
		LogUtil.logV("init_TxPackets  : " + init_TxPackets);
	}
	
	public static long getBytesRx()
	{
		long rx = TrafficStats.getTotalRxBytes();
		return rx - init_RxBytes;
	}
	
	public static long getBytesTx()
	{
		long tx = TrafficStats.getTotalTxBytes();
		return tx - init_TxBytes;
	}
	
	public static long getBytes()
	{
		return getBytesRx() + getBytesTx();
	}
	
	public static long getPacketsRp()
	{
		long rp = TrafficStats.getTotalRxPackets();
		return rp - init_RxPackets;
	}
	
	public static long getPacketsTp()
	{
		long tx = TrafficStats.getTotalTxPackets();
		return tx - init_TxPackets;
	}
	
	public static long getPackets()
	{
		return getPacketsRp() + getPacketsTp();
	}
	
	public static String getResult()
	{
		String str = "";
		long rxb = getBytesRx();
		long txb = getBytesTx();
		long b = rxb + txb;
		long rxp = getPacketsRp();
		long txp = getPacketsTp();
		long p = rxp + txp;
		str += "Rx bytes : " + rxb + "  (" + Constants.bytesToKbMbGb(rxb) + ")\n";
		str += "Tx bytes : " + txb + "  (" + Constants.bytesToKbMbGb(txb) + ")\n";
		str += "Total bytes : " + b + "  (" + Constants.bytesToKbMbGb(b) + ")\n";
		str += "Rx packets : " + rxp + "\n";
		str += "Tx Packets : " + txp + "\n";
		str += "Total Packets : " + p + "\n";
		return str;
	}
}
