package com.skywds.android.updateexample;

import java.io.File;

import com.skywds.android.bsdiffpatch.JniApi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	//增量更新按钮
	private Button mBsPatchBtn;
	//包名
	private String mPackageName;
	//旧版apk文件路径1
	private String mLocalApkPath;
	//旧版apk文件路径2
	private String mLocalApkPath2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//获得包名
		mPackageName = MainActivity.this.getPackageName();
		mLocalApkPath = "/data/app/" + mPackageName + "-1.apk";
		mLocalApkPath2 = "/data/app/" + mPackageName + "-2.apk";
		
		toast(mLocalApkPath);
		toast(mLocalApkPath2);
		
		///data/app/com.skywds.android.updateexample-1.apk
		init();
		
	}
	
	private void init()
	{
		//增量更新按钮
		this.mBsPatchBtn = (Button) findViewById(R.id.bspatch_btn);
		this.mBsPatchBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(new BsPatchRunnable()).start();
			}
		});
	}
	
	/**
	 * 增量更新库。需要放到异步线程中使用 !!! 
	 * */
	public class BsPatchRunnable implements Runnable
	{

		@Override
		public void run() {
			if( !Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ))
			{
				toast("未检测到外部存储设备，无法导出文件");
				return ;
			}
			
			File file = new File( mLocalApkPath );
			if( !file.exists() )
			{
				file = new File( mLocalApkPath2 );
			}
			
			if( file.exists() && file.canRead() )
			{
				toast("文件可以读取");
			}
			else
			{
				toast("文件不存在或 文件无法读取");
				return;
			}
			
			//旧版apk存放文件
			String oldFile = file.getAbsolutePath();
			
			//新版本apk存放文件
			String newFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bsdiff" + File.separator + "new.apk";
			
			//将增量更新patch文件放在   /mnt/sdcard/bsdiff/update.patch 这里
			String updateFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bsdiff" + File.separator + "update.patch";
			
			//创建新版本apk所在文件夹
			new File( newFile ).getParentFile().mkdirs();
			
			File patchFile = new File( updateFile );
			if( !patchFile.exists() )
			{
				toast("增量更新文件不存在");
				return ;
			}
			
			//增量更新生成新版apk安装包
			int bspatch = JniApi.bspatch(oldFile, newFile, updateFile);
			
			toast("增量更新文件处理完成:" + bspatch);
			
			if( bspatch == 0 )
			{
				toast("补丁包处理完毕, 开始安装新版本");
			}
			else
			{
				toast("补丁包处理失败");
			}
			
			//安装新版apk文件
			installNewApk( newFile );
			
		}
		
	}
	
	/**
	 * 安装apk
	 * */
	public void installNewApk( String filePath )
	{
		// 核心是下面几句代码  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(new File(filePath)),  
                "application/vnd.android.package-archive");  
        MainActivity.this.startActivity(intent); 
	}
	
	
	public void toast( String str )
	{
		Message msg = Message.obtain();
		msg.arg1 = 0x123;
		msg.obj = str;
		mHandler.sendMessage(msg);
	}
	
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			if( msg.what == 0x123 )
				Toast.makeText(MainActivity.this, "" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
			
		}
	};
	
	
}
