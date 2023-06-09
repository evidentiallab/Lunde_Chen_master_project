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

package edu.umich.PowerTutor.ui;

import edu.umich.PowerTutor.service.UMLoggerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartupReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
	  
	    //System.out.println("BroadcastReceiver: StartupReceiver, onReceive");

	  
    SharedPreferences prefs =
        PreferenceManager.getDefaultSharedPreferences(context);
    if(prefs.getBoolean("runOnStartup", true)) {
      Intent serviceIntent = new Intent(context, UMLoggerService.class);
      context.startService(serviceIntent);
    }
  }
}

