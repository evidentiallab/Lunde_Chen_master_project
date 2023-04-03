package com.mk.wipower.cpu;

public class DoubleLinkedListIntUtil {
	public int size;
	public Node[] nodes;
	public Node curr;
	
	public DoubleLinkedListIntUtil(int nowValue, int... arr)
	{
		System.out.println("DoubleLinkedList construction begin");
		this.size = arr.length;
		this.nodes = new Node[size];
		for(int i = 0; i< size; i ++)
		{
			nodes[i] = new Node(arr[i]);
		}
		for(int i = 0; i < size - 1; i ++)
		{
			nodes[i].next = nodes[i+1];
		}
		nodes[size - 1].next = nodes[0];
		for(int i = 1; i < size; i ++)
		{
			nodes[i].last = nodes[i-1];
		}
		nodes[0].last = nodes[size-1];
		this.curr = nodes[0];
		while(curr.value < nowValue)
		{
			getNext();
			if(curr.equals(nodes[0]))
				break;
		}
		System.out.println("DoubleLinkedList construction finished");
		System.out.println(curr.value);
	}
	
	public int getNextInt()
	{
		curr = curr.getNext();
		return curr.value;
	}
	
	public int getLastInt()
	{
		curr = curr.getLast();
		return curr.value;
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

	class Node
	{
		public Node last;
		public Node next;
		public int value;
		
		public Node(int v)
		{
			this.value = v;
		}
		
		public String toString()
		{
			return Integer.toString(value);
		}
		
		public void setNext(Node node)
		{
			this.next = node;
		}
		
		public Node getNext()
		{
			return this.next;
		}
		
		public void setLast(Node node)
		{
			this.last = node;
		}
		
		public Node getLast()
		{
			return this.last;
		}
		
	}
