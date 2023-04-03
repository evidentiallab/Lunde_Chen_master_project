package com.example.myandroidwifi.page;

import java.util.Arrays;
import java.util.Comparator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.myandroidwifi.R;
import com.example.myandroidwifi.util.ConnectivityUtil;
import com.example.myandroidwifi.util.ConnectivityUtil.AndroidApp;


public class ConnectivityPage extends Activity implements OnCheckedChangeListener
{
	private ListView listview;
	private ToggleButton toggle_bttn_connectivitypage;
	private Button bttn_selectall_connectivitypage, bttn_clearall_connectivitypage;
	public static final String TAG = "Wi-Power Iptables";
	public static boolean selectAll = false;
	public static boolean clearAll = false;
	BaseAdapter adapter = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		checkPreferences();
		setContentView(R.layout.connectivity_settings_page);
		
		toggle_bttn_connectivitypage = (ToggleButton)findViewById(R.id.toggle_bttn_connectivitypage);
		bttn_selectall_connectivitypage = (Button)findViewById(R.id.bttn_selectall_connectivitypage);
		bttn_clearall_connectivitypage = (Button)findViewById(R.id.bttn_clearall_connectivitypage);
		ConnectivityUtil.assertBinaries(this, true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Force re-loading the application list
		Log.d(TAG, "onStart() - Forcing APP list reload!");
		ConnectivityUtil.applications = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setTitle("Connectivity Settings");
		
		if (this.listview == null) {
			this.listview = (ListView) this.findViewById(R.id.listview);
		}
		showOrLoadApplications();
		
		// setPageClickability();
		
		Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.listview.setAdapter(null);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		
	}
	
	private void checkPreferences() {
		final SharedPreferences prefs = getSharedPreferences(ConnectivityUtil.PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		boolean changed = false;
		
		/* delete the old preference names */
		if (prefs.contains("AllowedUids")) {
			editor.remove("AllowedUids");
			changed = true;
		}
		if (prefs.contains("Interfaces")) {
			editor.remove("Interfaces");
			changed = true;
		}
		if (changed)
			editor.commit();
	}

	private void showOrLoadApplications() {
		final Resources res = getResources();
		if (ConnectivityUtil.applications == null) {
			// The applications are not cached.. so lets display the progress
			// dialog
			final ProgressDialog progress = ProgressDialog.show(this,
					res.getString(R.string.working),
					res.getString(R.string.reading_apps), true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					try {
						progress.dismiss();
					} catch (Exception ex) {
					}
					showApplications();
				}
			};
			new Thread() {
				public void run() {
					ConnectivityUtil.getApps(ConnectivityPage.this);
					handler.sendEmptyMessage(0);
				}
			}.start();
		} else {
			// the applications are cached, just show the list
			showApplications();
		}
	}

	/**
	 * Show the list of applications
	 */
	private void showApplications() {
		final AndroidApp[] apps = ConnectivityUtil.getApps(this);  //// !!!!!!!!!!!!
		// Sort applications - selected first, then alphabetically
		Arrays.sort(apps, new Comparator<AndroidApp>() {
			public int compare(AndroidApp o1, AndroidApp o2) {
				if ((o1.selected_wifi | o1.selected_3g) == (o2.selected_wifi | o2.selected_3g)) {
					return o1.names[0].compareTo(o2.names[0]);
				}
				if (o1.selected_wifi || o1.selected_3g)
					return -1;
				return 1;
			}
		});
		final LayoutInflater inflater = getLayoutInflater();
		adapter = new ArrayAdapter<AndroidApp>(this,
				R.layout.connectivity_listitem, R.id.itemtext, apps) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ListEntry entry;
				if (convertView == null) {
					// Inflate a new view
					convertView = inflater.inflate(R.layout.connectivity_listitem, parent,
							false);
					entry = new ListEntry();
					entry.box_wifi = (CheckBox) convertView
							.findViewById(R.id.itemcheck_wifi_connectivity);
					entry.box_3g = (CheckBox) convertView
							.findViewById(R.id.itemcheck_3g_connectivity);
					entry.text = (TextView) convertView
							.findViewById(R.id.itemtext);
					convertView.setTag(entry);
					entry.box_wifi
							.setOnCheckedChangeListener(ConnectivityPage.this);

					
					entry.box_3g.setOnCheckedChangeListener(ConnectivityPage.this);
				} else {
					// Convert an existing view
					entry = (ListEntry) convertView.getTag();
				}
				
				final AndroidApp app = apps[position];
				entry.text.setText(app.toString());
				final CheckBox box_wifi = entry.box_wifi;
				box_wifi.setTag(app);
				box_wifi.setChecked(app.selected_wifi);
				
				final CheckBox box_3g = entry.box_3g;
				box_3g.setTag(app);
				box_3g.setChecked(app.selected_3g);
				
				//
				if(selectAll)
					box_wifi.setChecked(true);
				if(clearAll)
					box_wifi.setChecked(false);
				
				if(ConnectivityUtil.isEnabled(ConnectivityPage.this))
					box_wifi.setClickable(false);
				else {
					box_wifi.setClickable(true);
				}
				//
				return convertView;
			}
		};
		
		this.listview.setAdapter(adapter);
		
		setPageClickability();
	}


	private void disableOrEnable() {
		final boolean enable = !ConnectivityUtil.isEnabled(this); /// !!
		Log.d(TAG, "Changing enabled status to: " + enable);
		Toast.makeText(getApplicationContext(), "Changing enabled status to: " + enable, Toast.LENGTH_LONG).show();
		ConnectivityUtil.setEnabled(this, enable);
		if (enable) {
			applyOrSaveRules();
			setTitle("Connectivity Settings (Running)");
			// bttn_selectall_connectivitypage.setTextColor(Color.)
		} else {
			purgeRules();  //!!!!
			setTitle("Connectivity Settings");
		}

        setPageClickability();
	}
	

	private void applyOrSaveRules() {
		final Resources res = getResources();
		final boolean enabled = ConnectivityUtil.isEnabled(this);
		final ProgressDialog progress = ProgressDialog.show(this, res
				.getString(R.string.working), res
				.getString(enabled ? R.string.applying_rules
						: R.string.saving_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (enabled) {
					Log.d("Wi-Power Iptables", "Applying rules.");
					if (ConnectivityUtil.hasRootAccess(ConnectivityPage.this, ConnectivityUtil.SCRIPT_FILE_EMPTY, true)
							&& ConnectivityUtil.applyIptablesRules(ConnectivityPage.this, true)) {
						Toast.makeText(ConnectivityPage.this,
								R.string.rules_applied, Toast.LENGTH_SHORT)
								.show();
					} else {
						Log.d("Wi-Power Iptables", "Failed - Disabling firewall.");
						ConnectivityUtil.setEnabled(ConnectivityPage.this, false);
					}
				} else {
					Log.d("Wi-Power Iptables", "Saving rules.");
					ConnectivityUtil.saveRules(ConnectivityPage.this);
					Toast.makeText(ConnectivityPage.this, R.string.rules_saved,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}

	private void purgeRules() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this,
				res.getString(R.string.working),
				res.getString(R.string.deleting_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (!ConnectivityUtil.hasRootAccess(ConnectivityPage.this, ConnectivityUtil.SCRIPT_FILE_IPTABLES, true))
					return;
				if (ConnectivityUtil.purgeIptables(ConnectivityPage.this, true)) {
					Toast.makeText(ConnectivityPage.this, R.string.rules_deleted,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}

	
	public void setPageClickability()
	{
		boolean enabled = ConnectivityUtil.isEnabled(ConnectivityPage.this);
		
		if(!enabled)
		bttn_selectall_connectivitypage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAll = true;
				clearAll = false;
				adapter.notifyDataSetChanged();
				// selectAll = false;
			}
		});
		else {
			bttn_selectall_connectivitypage.setClickable(false);
		}
		
		if(!enabled)
		bttn_clearall_connectivitypage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearAll = true;
				selectAll = false;
				adapter.notifyDataSetChanged();
				// clearAll = false;
			}
		});
		else {
			bttn_clearall_connectivitypage.setClickable(false);
		}
		
		if(enabled)
			setTitle("Connectivity Settings (Running)");
		
		toggle_bttn_connectivitypage.setChecked(enabled);
		toggle_bttn_connectivitypage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
					selectAll = false;
					clearAll = false;
					disableOrEnable();
					adapter.notifyDataSetChanged();
			}
		});
	}
	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final AndroidApp app = (AndroidApp) buttonView.getTag();
		if (app != null) {
			switch (buttonView.getId()) {
			case R.id.itemcheck_wifi_connectivity:
				app.selected_wifi = isChecked;
				break;
			case R.id.itemcheck_3g_connectivity:
				app.selected_3g = isChecked;
				break;
			}
		}
	}

	private static class ListEntry {
		private CheckBox box_wifi;
		private CheckBox box_3g;
		private TextView text;
	}
}
