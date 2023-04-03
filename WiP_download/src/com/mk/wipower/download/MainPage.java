package com.mk.wipower.download;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainPage extends Activity {
	private Button bttn_start, bttn_stop, bttn_pre, bttn_next;
	private TextView textV;
	private Intent intent;
	private Bundle bundle;
	private String[] arr = 
	{
			"http://www2.census.gov/census_2010/04-Summary_File_1/Alaska/ak2010.sf1.zip",  //35Mb,较慢
			"http://down.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk", //22.8Mb, 较快
			"3.0 Mb",
			"http://gdown.baidu.com/data/wisegame/ea93eb209c1d5fe9/FunnyYoga_2.apk", // 3.0Mb
			"3.3 Mb",
			"http://bs.baidu.com/appstore/apk_259558DEB119E70487F456739AFCDCD7.apk", // 3.3 Mb
			"3.5 Mb, CCTV xinwen",
			"http://bs.baidu.com/appstore/apk_1125DFFCE9D1750A9381398627C10DDC.apk", // 
			"328.2 KB", 
			"http://pic1.win4000.com/wallpaper/1/50a5ebe73e101.jpg",
			"428 Kb",
			"http://pic1.win4000.com/wallpaper/2/53bb801946714.jpg",
			"96 KB",
			"http://t12.baidu.com/it/u=278276783,582562630&fm=56",
			"28.7 KB", 
			"http://t12.baidu.com/it/u=530946911,1045651454&fm=56",
			"57.8 KB",
			"http://a.hiphotos.baidu.com/image/pic/item/a8773912b31bb051a8993ed8347adab44bede085.jpg",
			"725.6 KB",
			"http://pic.yesky.com/135/36778635.shtml",
			"228 KB",
			"http://a.hiphotos.baidu.com/image/pic/item/f603918fa0ec08faf1c2a8335bee3d6d54fbda60.jpg",
			"170 KB",
			"http://c.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf4a0941561f0d3572c11dfcf3a.jpg",
			"184 KB",
			"http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbdb0e89aa6f224f4a21a4dd76.jpg",
			"79.5",
			"http://h.hiphotos.baidu.com/image/pic/item/a08b87d6277f9e2fe01d06871d30e924b899f311.jpg",
			"123 KB",
			"http://c.hiphotos.baidu.com/image/pic/item/9345d688d43f8794c6d111a6d01b0ef41bd53a0f.jpg",
			"41.6 KB",
			"http://h.hiphotos.baidu.com/image/pic/item/024f78f0f736afc3a6fc1d64b119ebc4b745123a.jpg",
			"      ",
			"http://www.scilab.org/download/5.4.1/scilab-5.4.1.bin.linux-i686.tar.gz", // 163 Mb
			"http://down.m.duoku.com/game/50000/50550/20140812205747_DuoKu.apk", // 102 Mb
			"http://down.m.duoku.com/game/59000/59195/20140716194413_DuoKu.apk", // 55.8 Mb
			"http://imgskype.gmw.cn/software/SkypeSetupFull.exe", // 33.9 Mb
			"http://www.baidu.com/img/bdlogo.png"
		};
	private DoubleLinkedListStringUtil list = new DoubleLinkedListStringUtil("", arr);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		
		bttn_start       = (Button)  findViewById(R.id.bttn_start)       ;
        bttn_stop        = (Button)  findViewById(R.id.bttn_stop)        ;
        bttn_pre         = (Button)  findViewById(R.id.bttn_pre)         ;
        bttn_next        = (Button)  findViewById(R.id.bttn_next)        ;
        
        textV = (TextView)findViewById(R.id.textV);
        
        intent = new Intent(MainPage.this, MyDownloadService.class);
		bundle = new Bundle();
		
		bundle.putInt("which", -10);
		intent.putExtras(bundle);
		MainPage.this.startService(intent);
        
		bttn_pre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyDownloadService.url = list.getLast();
				textV.setText(MyDownloadService.url );
			}
		});
		
		bttn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyDownloadService.url = list.getNext();
				textV.setText(MyDownloadService.url );
			}
		});
		
        bttn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				bundle.putInt("which", 1);
				intent.putExtras(bundle);
				MainPage.this.startService(intent);
			}
		});
        
        bttn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				bundle.putInt("which", 2);
				intent.putExtras(bundle);
				MainPage.this.startService(intent);
				finish();
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}
