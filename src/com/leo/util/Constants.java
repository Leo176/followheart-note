package com.leo.util;

import java.nio.ByteBuffer;

public class Constants {
	
	//Hbase配置
	public static String HBASE_CLIENT_PORT;
	public static String HBASE_ZOOKEEPER_QUORUM;
	public static String HBASE_MASTER;
	public static long HBASE_WRITE_BUFFER;   //写缓存大小
	
	//Redis配置
	public static String REDIS_SERVER_ADDRESS;
	public static int REDIS_SERVER_PORT;
	public static int REDIS_TIMOUT;
	public static String REDIS_AUTH;
	public static String REDIS_SPLIT; //分隔符


	public static String USER_INFO="user_info";
	
	//笔记本配置
	public static String NBTABLENAME="notebook";   //hbase中笔记本的表名
	public static String NOTEBOOK_FAMILY="nbinfo";    //hbase中笔记本表的列族名
	public static String NOTEBOOK_COLUMN_NAME="nbn";    //hbase中笔记本表的列名-笔记本名称
	public static String NOTEBOOK_COLUMN_CREATETIME="ct";    //hbase中笔记本表的列名-创建时间
	public static String NOTEBOOK_COLUMN_STATUS="st";    //hbase中笔记本表的列名-状态
	public static String NOTEBOOK_COLUMN_NOTELIST="nl";    //hbase中笔记本表的列名-笔记列表
	

	
	/**rowkey的分隔符“_”*/
	public static String rowkey_split="_";
	
	/**特殊笔记文件夹的标记*/
	public static String STAR=rowkey_split+"star";  //收藏夹 user_star
	public static String RECYCLE=rowkey_split+"recycle";  //回收站 user_recycle
	public static String ACTIVITY=rowkey_split+"activity";  //活动夹  user_activity
	

	
	
	
	
}
