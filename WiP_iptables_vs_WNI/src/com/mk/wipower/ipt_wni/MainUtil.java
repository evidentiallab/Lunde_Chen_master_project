package com.mk.wipower.ipt_wni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class MainUtil {
	private Context context;
	
	public MainUtil(Context contxt)
	{
		this.context = contxt;
	}
	
	public void popUpAlert(String alertmessage)
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setMessage(alertmessage);
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle("Alert");
		alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alertBuilder.show();
	}
	
	public void popUpAlert(String title, String alertmessage)
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setMessage(alertmessage);
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle(title);
		alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alertBuilder.show();
	}
	
	public void makeToast(String msg, boolean LONG)
	{
		Toast.makeText(context, msg, LONG? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}
	
	
}
