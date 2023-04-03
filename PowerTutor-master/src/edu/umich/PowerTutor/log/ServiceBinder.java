package edu.umich.PowerTutor.log;

import android.os.Binder;

public class ServiceBinder extends Binder
{
	public String getText()
	{
		return TextDisplayService.msg;
	}
}
