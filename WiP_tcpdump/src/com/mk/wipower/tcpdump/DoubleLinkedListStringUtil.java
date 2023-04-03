package com.mk.wipower.tcpdump;

public class DoubleLinkedListStringUtil {
	public int size;
	public NodeStr[] NodeStrs;
	public NodeStr curr;
	
	public DoubleLinkedListStringUtil(String nowValue, String... arr)
	{
		System.out.println("DoubleLinkedList construction begin");
		this.size = arr.length;
		this.NodeStrs = new NodeStr[size];
		for(int i = 0; i< size; i ++)
		{
			NodeStrs[i] = new NodeStr(arr[i]);
		}
		for(int i = 0; i < size - 1; i ++)
		{
			NodeStrs[i].next = NodeStrs[i+1];
		}
		NodeStrs[size - 1].next = NodeStrs[0];
		for(int i = 1; i < size; i ++)
		{
			NodeStrs[i].last = NodeStrs[i-1];
		}
		NodeStrs[0].last = NodeStrs[size-1];
		this.curr = NodeStrs[0];
		
		int count = 0;
		while(!curr.value.equalsIgnoreCase(nowValue) && count < size)
		{
			getNext();
			count ++;
			
		}
		System.out.println("DoubleLinkedList construction finished");
		System.out.println(curr.value);
	}
	
	public String getNext()
	{
		curr = curr.getNext();
		return curr.toString();
	}

	public String getLast()
	{
		curr = curr.getLast();
		return curr.toString();
	}
	
}

	class NodeStr
	{
		public NodeStr last;
		public NodeStr next;
		public String value;
		
		public NodeStr(String v)
		{
			this.value = v;
		}
		
		public String toString()
		{
			return value;
		}
		
		public void setNext(NodeStr NodeStr)
		{
			this.next = NodeStr;
		}
		
		public NodeStr getNext()
		{
			return this.next;
		}
		
		public void setLast(NodeStr NodeStr)
		{
			this.last = NodeStr;
		}
		
		public NodeStr getLast()
		{
			return this.last;
		}
		
}
