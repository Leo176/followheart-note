package com.leo.util;

//生成rowkey的辅助类
public class RowkeyUtil {

	//生成的rowkey的方法
	public static String getRowkey(String userName, Long createTime) {
		// TODO Auto-generated method stub
		return userName+Constants.rowkey_split+createTime;
	}
	
	

}
