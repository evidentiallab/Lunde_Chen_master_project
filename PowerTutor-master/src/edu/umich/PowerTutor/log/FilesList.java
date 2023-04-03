package edu.umich.PowerTutor.log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.umich.PowerTutor.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class FilesList extends Activity {
	private String dirStr;
	private ListView listV_files;
	private TextView textV_file_name, textV_file_size;
	private List<HashMap<String, String>> fileInfo;
	FileListAdapter adapter;
	HashMap<String, String> map;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files_list);
		
		listV_files = (ListView)findViewById(R.id.listV_files);
		
		dirStr = getIntent().getStringExtra("dirStr");
		
		fileInfo = new ArrayList<HashMap<String, String>>();
		
		File file = new File(dirStr);
		File[] files = file.listFiles();
		for(int i=0;i<files.length;i++)
		{	
			String name = files[i].getName();
			if(name.contains("."))
			{
			map = new HashMap<String, String>();
			map.put("name", name);
			map.put("size", (int)files[i].length()/1024 +  " Kb");
			fileInfo.add(map);
			}
		}
		
		adapter = new FileListAdapter();
		listV_files.setAdapter(adapter);
		
		listV_files.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FilesList.this, TextFileDisplay.class);
				intent.putExtra("fileStr", dirStr + fileInfo.get(arg2).get("name"));
				startActivity(intent);
			}
		});
		
	}
	
	
	class FileListAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fileInfo.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_listview_item, null);
			textV_file_name = (TextView)convertView.findViewById(R.id.textV_file_name);
			textV_file_size = (TextView)convertView.findViewById(R.id.textV_file_size);
			textV_file_name.setText(fileInfo.get(position).get("name"));
			textV_file_size.setText(fileInfo.get(position).get("size"));
				
			return convertView;
		}
		
	}
}

