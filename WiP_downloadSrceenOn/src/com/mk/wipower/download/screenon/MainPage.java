package com.mk.wipower.download.screenon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainPage extends Activity{
	private Button bttn_start, bttn_start_2, bttn_start_3, bttn_stop, bttn_pre, bttn_next;
	private Spinner spinner_color, spinner_brightness, spinner_size;
	private TextView textVURL, textVSpinnerColor, textViewSpinnerBrightness, textViewSpinnerFileSize;
	private LinearLayout linearLayout, linearLayout2;
	
	private Intent intent;
	private Bundle bundle;
	
	private PowerManager.WakeLock wakeLock; 
	private ScreenUtil mScreenUtil;
	private DownloadManager downloadManager;
	private String url; 
	private Handler mHandler;
	private Runnable mRunnable, mRunnable2;
	private MyReceiver myReceiver;
	private boolean downloadFlag;
	public static ExecutorService executor = Executors.newCachedThreadPool();
	private MyAsynTaskWeb task_web_1, task_web_2, task_web_3, task_web_4;
	private List<Long> idList = new ArrayList<Long>();
	private int count2 = 0;
	private int brightness = 1;
	
	private int[] arrBrightness = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 0};
	private String[] arrColor = {"Black", "White", "Red","Green", "Blue"};
	private String[] arrSize = {"328_329KB", "22KB_22KB", "3.0MB_3.3MB"};
	public static int whichFileGroup = 0;
	
	private boolean flagButton1 = false, flagButton2 = false, flagButton3 = false;

	private String[] arr = 
	{
				"http://down.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk", //22.8Mb, 较快
				"3.0 Mb -->",
				"http://gdown.baidu.com/data/wisegame/ea93eb209c1d5fe9/FunnyYoga_2.apk", // 3.0Mb
				"3.3 Mb -->",
				"http://bs.baidu.com/appstore/apk_259558DEB119E70487F456739AFCDCD7.apk", // 3.3 Mb
				"3.5 Mb, CCTV xinwen -->",
				"http://bs.baidu.com/appstore/apk_1125DFFCE9D1750A9381398627C10DDC.apk", // 
				"328.2 KB -->", 
				"http://pic1.win4000.com/wallpaper/1/50a5ebe73e101.jpg",
				"428 Kb -->",
				"http://pic1.win4000.com/wallpaper/2/53bb801946714.jpg",
				"329 KB -->",
				"http://pic1.win4000.com/wallpaper/6/54505b8d7fc76.jpg",
				"96 KB -->",
				"http://t12.baidu.com/it/u=278276783,582562630&fm=56",
				"28.7 KB -->", 
				"http://t12.baidu.com/it/u=530946911,1045651454&fm=56",
				"57.8 KB -->",
				"http://a.hiphotos.baidu.com/image/pic/item/a8773912b31bb051a8993ed8347adab44bede085.jpg",
				"725.6 KB -->",
				"http://pic.yesky.com/135/36778635.shtml",
				"228 KB -->",
				"http://a.hiphotos.baidu.com/image/pic/item/f603918fa0ec08faf1c2a8335bee3d6d54fbda60.jpg",
				"170 KB -->",
				"http://c.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf4a0941561f0d3572c11dfcf3a.jpg",
				"184 KB -->",
				"http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbdb0e89aa6f224f4a21a4dd76.jpg",
				"79.5 -->",
				"http://h.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fe01d06871d30e924b899f311.jpg",
				"123 KB -->",
				"http://c.hiphotos.baidu.com/image/pic/item/9345d688d43f8794c6d111a6d01b0ef41bd53a0f.jpg",
				"41.6 KB -->",
				"http://h.hiphotos.baidu.com/image/pic/item/024f78f0f736afc3a6fc1d64b119ebc4b745123a.jpg",
				"http://www.scilab.org/download/5.4.1/scilab-5.4.1.bin.linux-i686.tar.gz", // 163 Mb
				"http://down.m.duoku.com/game/50000/50550/20140812205747_DuoKu.apk", // 102 Mb
				"http://down.m.duoku.com/game/59000/59195/20140716194413_DuoKu.apk", // 55.8 Mb
				"http://imgskype.gmw.cn/software/SkypeSetupFull.exe", // 33.9 Mb
				"http://www.baidu.com/img/bdlogo.png"
			};
	private DoubleLinkedListStringUtil list = new DoubleLinkedListStringUtil("", arr);

	public static final String[] WEB_URLS = {
//		"http://www.baidu.com",
//		"http://www.sohu.com",
//		"http://www.sina.com.cn",
//		"http://www.yahoo.com"
		
		"http://pic1.win4000.com/wallpaper/6/54505b8d7fc76.jpg", // 329 KB
		"http://pic1.win4000.com/wallpaper/1/50a5ebe73e101.jpg", // 328 KB
		"http://pic1.win4000.com/wallpaper/6/54505b8d7fc76.jpg", // 329 KB
		"http://pic1.win4000.com/wallpaper/1/50a5ebe73e101.jpg", // 328 KB
		
		"http://www.baidu.com/img/bdlogo.png", // 22KB
		"http://www.baidu.com/img/bdlogo.png",
		"http://www.baidu.com/img/bdlogo.png",
		"http://www.baidu.com/img/bdlogo.png",
		
		"http://gdown.baidu.com/data/wisegame/ea93eb209c1d5fe9/FunnyYoga_2.apk",
		"http://bs.baidu.com/appstore/apk_259558DEB119E70487F456739AFCDCD7.apk", 
		"http://gdown.baidu.com/data/wisegame/ea93eb209c1d5fe9/FunnyYoga_2.apk",
		"http://bs.baidu.com/appstore/apk_259558DEB119E70487F456739AFCDCD7.apk", 
		
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_page);
		
		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).
        		newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "MainPage");
		mScreenUtil = new ScreenUtil(MainPage.this);
		
		System.out.println("DEBUG -1");
		
		intent = new Intent(MainPage.this, MyAsynService.class);
		bundle = new Bundle();
		
		bttn_start       = (Button)  findViewById(R.id.bttn_start)       ;
		bttn_start_2     = (Button)  findViewById(R.id.bttn_start_2)     ;
		bttn_start_3     = (Button)  findViewById(R.id.bttn_start_3)     ;
        bttn_stop        = (Button)  findViewById(R.id.bttn_stop)        ;
        bttn_pre         = (Button)  findViewById(R.id.bttn_pre)         ;
        bttn_next        = (Button)  findViewById(R.id.bttn_next)        ;
        spinner_color	 		= (Spinner) 		findViewById(R.id.spinner_color)    	;
        spinner_brightness    	= (Spinner) 		findViewById(R.id.spinner_brightness)	;
        spinner_size	 		= (Spinner) 		findViewById(R.id.spinner_size)    		;
        linearLayout     		= (LinearLayout) 	findViewById(R.id.layout_linear)  		;
        linearLayout2     		= (LinearLayout) 	findViewById(R.id.layout_linear2)  		;
        
        textVURL = (TextView)findViewById(R.id.textV);
		
        System.out.println("DEBUG 0");
        
		downloadFlag = true;
		downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
		
        myReceiver = new MyReceiver();     
        mHandler = new Handler();
        
        System.out.println("DEBUG 1");
        
        MyBrightnessSpinnerAdapter mBrightnessSpinnerAdapter = new MyBrightnessSpinnerAdapter();
        spinner_brightness.setAdapter(mBrightnessSpinnerAdapter);
        spinner_brightness.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				brightness = arrBrightness[position];
				if(brightness != 0)
					mScreenUtil.resetBrightness(brightness);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
        });
        
        MyColorSpinnerAdapter mColorSpinnerAdapter = new MyColorSpinnerAdapter();
        spinner_color.setAdapter(mColorSpinnerAdapter);
        spinner_color.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					linearLayout.setBackgroundColor(Color.BLACK);
					linearLayout2.setBackgroundColor(Color.BLACK);
					textVSpinnerColor.setBackgroundColor(Color.BLACK);
					bttn_stop.setBackgroundColor(Color.BLACK);
					break;
				case 1:
					linearLayout.setBackgroundColor(Color.WHITE);
					linearLayout2.setBackgroundColor(Color.WHITE);
					textVSpinnerColor.setBackgroundColor(Color.WHITE);
					bttn_stop.setBackgroundColor(Color.WHITE);
					break;	
				case 2:
					linearLayout.setBackgroundColor(Color.RED);
					linearLayout2.setBackgroundColor(Color.RED);
					textVSpinnerColor.setBackgroundColor(Color.RED);
					bttn_stop.setBackgroundColor(Color.RED);
					break;	
				case 3:
					linearLayout.setBackgroundColor(Color.GREEN);
					linearLayout2.setBackgroundColor(Color.GREEN);
					textVSpinnerColor.setBackgroundColor(Color.GREEN);
					bttn_stop.setBackgroundColor(Color.GREEN);
					break;
				case 4:
					linearLayout.setBackgroundColor(Color.BLUE);
					linearLayout2.setBackgroundColor(Color.BLUE);
					textVSpinnerColor.setBackgroundColor(Color.BLUE);
					bttn_stop.setBackgroundColor(Color.BLUE);
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        });
        
        MyFileSizeSpinnerAdapter mFileSizeSpinnerAdapter = new MyFileSizeSpinnerAdapter();
        spinner_size.setAdapter(mFileSizeSpinnerAdapter);
        spinner_size.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				whichFileGroup = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        System.out.println("DEBUG 2");
        
        bttn_pre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				url = list.getLast();
				textVURL.setText(url);
			}
		});
		
		bttn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				url = list.getNext();
				textVURL.setText(url);
			}
		});
		
		bttn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mScreenUtil.resetBrightness(brightness);
				
		        IntentFilter filter = new IntentFilter();
		        filter.addAction(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		        registerReceiver(myReceiver, filter);   
				
		        mRunnable = new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startDownload(downloadManager, url);
						if(count2 == 20 && brightness == 0)
							mScreenUtil.resetBrightness(20);
						mHandler.postDelayed(this, 30 * 1000);
					}
		        };
				mHandler.postDelayed(mRunnable, 5 * 1000);
				
				bttn_next	.setVisibility(View.INVISIBLE);
				bttn_pre	.setVisibility(View.INVISIBLE);
				bttn_start  .setVisibility(View.INVISIBLE);
				bttn_start_2.setVisibility(View.INVISIBLE);
				bttn_start_3.setVisibility(View.INVISIBLE);
				textVURL		.setVisibility(View.INVISIBLE);
				spinner_brightness.setVisibility(View.INVISIBLE);
				spinner_color.setVisibility(View.INVISIBLE);
				spinner_size.setVisibility(View.INVISIBLE);
				linearLayout2.setVisibility(View.INVISIBLE);
				
				flagButton1 = true;
				
				Intent intent = new Intent();
				intent.setAction("com.mk.wipower.downloadmanager.action.startdownload");
				sendBroadcast(intent);
				mScreenUtil.resetBrightness(brightness);
			}
		});
		
		bttn_start_2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mScreenUtil.resetBrightness(brightness);
				
		        mRunnable2 = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						count2 ++;
						asynWork();
						if(count2 == 20 && brightness == 0)
							mScreenUtil.resetBrightness(20);
						mHandler.postDelayed(this, 30 * 1000);
					}
				};
				mHandler.postDelayed(mRunnable2, 10 * 1000);
				
				bttn_next	.setVisibility(View.INVISIBLE);
				bttn_pre	.setVisibility(View.INVISIBLE);
				bttn_start  .setVisibility(View.INVISIBLE);
				bttn_start_2.setVisibility(View.INVISIBLE);
				bttn_start_3.setVisibility(View.INVISIBLE);
				textVURL		.setVisibility(View.INVISIBLE);
				spinner_brightness.setVisibility(View.INVISIBLE);
				spinner_color.setVisibility(View.INVISIBLE);
				spinner_size.setVisibility(View.INVISIBLE);
				linearLayout2.setVisibility(View.INVISIBLE);
				
				flagButton2 = true;
				
//				Intent intent = new Intent();
//				intent.setAction("com.mk.wipower.downloadmanager.action.startdownload");
//				sendBroadcast(intent);
				mScreenUtil.resetBrightness(brightness);
			}
		});
		
		
		bttn_start_3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				bttn_next	.setVisibility(View.INVISIBLE);
				bttn_pre	.setVisibility(View.INVISIBLE);
				bttn_start  .setVisibility(View.INVISIBLE);
				bttn_start_2.setVisibility(View.INVISIBLE);
				bttn_start_3.setVisibility(View.INVISIBLE);
				textVURL		.setVisibility(View.INVISIBLE);
				spinner_brightness.setVisibility(View.INVISIBLE);
				spinner_color.setVisibility(View.INVISIBLE);
				spinner_size.setVisibility(View.INVISIBLE);
				linearLayout2.setVisibility(View.INVISIBLE);
				
				bundle.putInt("action", 1);
				intent.putExtras(bundle);
				startService(intent);
				flagButton3 = true;
				
			}
		});
		
		bttn_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent();
				
				intent2.setAction("com.mk.wipower.downloadmanager.action.finishdownload");
				sendBroadcast(intent2);
				finish();
			}
		});
		
		System.out.println("DEBUG 1");
	}
	
	public void startDownload(DownloadManager manager, String str)
	{
		if(downloadFlag)
		{
			downloadFlag = false;
			
			DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(str));
			request.setDestinationInExternalPublicDir(
					Environment.DIRECTORY_DOWNLOADS, "/");
			request.setTitle(url);
			request.setDescription("download test");
			long id = manager.enqueue(request);
			idList.add(id);
			System.out.println("Start download, download id: "  + id + " , url : " + url);
			
//			asynWork();
		}
		else {
			System.out.println("continue downloading ... ");
		}
	}
	
	public void asynWork()
	{
		try
		{
		if(count2 % 6 == 1)
		{
//			System.out.println("AsyncTask ... ");
			task_web_1 = new MyAsynTaskWeb();
        	task_web_1.executeOnExecutor(executor, new Integer[]{Integer.valueOf(1)});
		}
		
		if(count2 % 6 == 2)
		{
//			System.out.println("AsyncTask ... ");
			task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(executor, new Integer[]{Integer.valueOf(2)});
		}
		
        if(count2 % 6 == 3)
        {
//        	System.out.println("AsyncTask ... ");
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(executor, new Integer[]{Integer.valueOf(2)});
        	
        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(executor, new Integer[]{Integer.valueOf(3)});
        }
        if(count2 % 6 == 4)
        {
//        	System.out.println("AsyncTask ... ");
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(executor, new Integer[]{Integer.valueOf(2)});

        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(executor, new Integer[]{Integer.valueOf(3)});
        	
        	task_web_4 = new MyAsynTaskWeb();
        	task_web_4.executeOnExecutor(executor, new Integer[]{Integer.valueOf(4)});
        }
        if(count2 % 6 == 5)
        {
        	task_web_1 = new MyAsynTaskWeb();
        	task_web_1.executeOnExecutor(executor, new Integer[]{Integer.valueOf(1)});
        	
        	task_web_2 = new MyAsynTaskWeb();
        	task_web_2.executeOnExecutor(executor, new Integer[]{Integer.valueOf(2)});

        	task_web_3 = new MyAsynTaskWeb();
        	task_web_3.executeOnExecutor(executor, new Integer[]{Integer.valueOf(3)});
        	
        	task_web_4 = new MyAsynTaskWeb();
        	task_web_4.executeOnExecutor(executor, new Integer[]{Integer.valueOf(4)});
        }
		}
		catch(Exception ex){}
	}
	
	public void onResume()
	{
		wakeLock.acquire();
		super.onResume();
	}
	
	@SuppressLint("Wakelock") public void onDestroy()
	{
		if(flagButton1)
		{
			unregisterReceiver(myReceiver);
			mHandler.removeCallbacks(mRunnable);
			
			for(int i = 0; i < idList.size(); i ++)
			{
			  System.out.println("delete " + idList.get(i) + " : " + downloadManager.remove(idList.get(i)));
			}
			
			FileFilter filter = new PrefixFileFilter("-");
			File[] files = (new File("/sdcard/" + Environment.DIRECTORY_DOWNLOADS)).listFiles(filter);
			try {
				  for(int i = 0; i < files.length; i ++)
					FileUtils.forceDelete(files[i]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		if(flagButton2)
		{
			task_web_1.cancel(true);
			task_web_2.cancel(true);
			task_web_3.cancel(true);
			task_web_4.cancel(true);
//			task_web_1 = null;
//			task_web_2 = null;
//			task_web_3 = null;
//			task_web_4 = null;
			mHandler.removeCallbacks(mRunnable2);
		}
		
		if(flagButton3)
		{
			bundle.putInt("action", 2);
			intent.putExtras(bundle);
			startService(intent);
		}
		
		if(wakeLock!=null){
			wakeLock.release();
		}
		super.onDestroy();
	}
	
	class MyBrightnessSpinnerAdapter implements SpinnerAdapter{

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrBrightness.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrBrightness[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			textViewSpinnerBrightness = new TextView(MainPage.this);
			textViewSpinnerBrightness.setTextColor(Color.argb(255, 180, 180, 180));
			textViewSpinnerBrightness.setBackgroundResource(android.R.color.transparent);
			textViewSpinnerBrightness.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			textViewSpinnerBrightness.setText("Brightness : " + arrBrightness[position]);
			textViewSpinnerBrightness.setGravity(Gravity.CENTER);
			return textViewSpinnerBrightness;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textV = new TextView(MainPage.this);
			textV.setBackgroundResource(android.R.color.transparent);
			//textV.setTextColor(Color.BLUE);
			textV.setTextColor(Color.argb(255, 130, 130, 130));
			textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			textV.setText(arrBrightness[position] + "%");
			textV.setGravity(Gravity.CENTER);
			textV.setPadding(0, 20, 0, 20);
			return textV;
		}
	}
	
	class MyFileSizeSpinnerAdapter implements SpinnerAdapter{

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrSize.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrSize[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			textViewSpinnerFileSize = new TextView(MainPage.this);
			textViewSpinnerFileSize.setTextColor(Color.argb(255, 180, 180, 180));
			textViewSpinnerFileSize.setBackgroundResource(android.R.color.transparent);
			textViewSpinnerFileSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			textViewSpinnerFileSize.setText("File Size : " + arrSize[position]);
			textViewSpinnerFileSize.setGravity(Gravity.CENTER);
			return textViewSpinnerFileSize;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textV = new TextView(MainPage.this);
			textV.setBackgroundResource(android.R.color.transparent);
			//textV.setTextColor(Color.BLUE);
			textV.setTextColor(Color.argb(255, 130, 130, 130));
			textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			textV.setText(arrSize[position]);
			textV.setGravity(Gravity.CENTER);
			textV.setPadding(0, 20, 0, 20);
			return textV;
		}
		
	}
	
	
	class MyColorSpinnerAdapter implements SpinnerAdapter{

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrColor.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			textVSpinnerColor = new TextView(MainPage.this);
			textVSpinnerColor.setTextColor(Color.argb(255, 180, 180, 180));
			textVSpinnerColor.setBackgroundResource(android.R.color.transparent);
			textVSpinnerColor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			textVSpinnerColor.setText("Color : " + arrColor[position]);
			textVSpinnerColor.setGravity(Gravity.CENTER);
			return textVSpinnerColor;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textV = new TextView(MainPage.this);
			textV.setBackgroundResource(android.R.color.transparent);
			//textV.setTextColor(Color.BLUE);
			textV.setTextColor(Color.argb(255, 130, 130, 130));
			textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			textV.setText(arrColor[position]);
			textV.setGravity(Gravity.CENTER);
			textV.setPadding(0, 20, 0, 20);
			return textV;
		}
		
	}
	
    private class MyReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{
				downloadFlag = true;
			}
		}
	}
}


