package com.skywds.android.bsdiffpatch;

public class JniApi {
	
	/**
	 * 2015-09-07
	 * @author brok1n
	 * @param oldPath 旧版apk文件绝对路径
	 * @param newPath 生成新版apk文件存放的绝对路径
	 * @param patchPath 更新补丁包文件存放绝对路径
	 * 
	 * @return 1 or 0
	 * 		0  生成新版本apk文件成功
	 * 		1  生成新版本apk文件失败
	 * 
	 * */
	public static native int bspatch( String oldPath, String newPath, String patchPath );

	static
	{
		System.loadLibrary( "bsdiff" );
	}
	
}
