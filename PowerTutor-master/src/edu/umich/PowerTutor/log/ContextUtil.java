package edu.umich.PowerTutor.log;

import android.app.Application;
import android.content.Context;

public class ContextUtil extends Application {
	
	/**
	 * @param context
	 */
	public ContextUtil(Context context) {
		super();
		ContextUtil.contextApplication = context;
	}
	public static Context GetGlobleApplicationContext() {
		return contextApplication;
	}
	static Context contextApplication;
}
