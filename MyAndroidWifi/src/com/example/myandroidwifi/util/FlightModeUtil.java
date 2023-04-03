package com.example.myandroidwifi.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.widget.Toast;

public class FlightModeUtil {
	private Context context;
	
	public FlightModeUtil(Context contxt)
	{
		this.context = contxt;
	}
	
	public void checkFlightMode()
	{
		boolean isEnabled = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        if(isEnabled)
        {
        	System.out.println("Fight mode is on");
        	Toast.makeText(context, "Now we are in the flight mode", Toast.LENGTH_LONG).show();
        }
        else {
			System.out.println("Flight mode is off");
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Flight Mode Alert");
			builder.setMessage("Flight mode is not on, please turn on " +
								"flight mode to continue the experiment.");
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					checkFlightMode();
				}
			});
			builder.show();
		}
	}
}
