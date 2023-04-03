package com.mk.wipower.tcpdump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortHashMapUtil {
	
	private static class MyMapComparatorIntStr implements Comparator<Map.Entry<Integer, String>>
	{
	    @Override
	    public int compare(Map.Entry<Integer, String> a, Map.Entry<Integer, String> b) {
	        return a.getKey().compareTo(b.getKey());
	    }
	}
	
	private static class MyMapComparatorIntLong implements Comparator<Map.Entry<Integer, Long>>
	{
	    @Override
	    public int compare(Map.Entry<Integer, Long> a, Map.Entry<Integer, Long> b) {
	        return a.getKey().compareTo(b.getKey());
	    }
	}
	
	public static List<Map.Entry<Integer, String>> sortHashMapIntStr(Map<Integer, String> map)
	{
		List<Map.Entry<Integer, String>> entries = 
				new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
		Collections.sort(entries, new MyMapComparatorIntStr());
		return entries;
	}

	public static List<Map.Entry<Integer, Long>> sortHashMapIntLong(Map<Integer, Long> map)
	{
		List<Map.Entry<Integer, Long>> entries = 
				new ArrayList<Map.Entry<Integer, Long>>(map.entrySet());
		Collections.sort(entries, new MyMapComparatorIntLong());
		return entries;
	}
	
	public static void main(String[] args)
	{
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		hm.put(9, "NINE");
		hm.put(8, "EIGHT");
		hm.put(10, "TEN");
		hm.put(1, "ONE");
		hm.put(11, "ELEVEN");
		hm.put(0, "ZERO");
		Map<Integer, String> map = (Map<Integer, String>)hm;
		List<Map.Entry<Integer, String>> result = sortHashMapIntStr(map);
		for(int i = 0; i < result.size(); i ++)
		{
			Map.Entry<Integer, String> m = result.get(i);
			System.out.println(m.getKey() + "  " + m.getValue());
		}
	}
}
