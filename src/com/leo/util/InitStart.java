package com.leo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class InitStart {

	//此方法spring后端一运行会立即执行
	@SuppressWarnings("unused")
	private void InitSystem() {
		System.setProperty("hadoop.home.dir", "E:\\hadoop-common-bin-master\\hadoop-common-bin-master\\2.7.1");
		ClassLoader loader = InitStart.class.getClassLoader();
		
		try {
			
		//从redis配置资源文件读取配置信息并加载至Constants类的静态属性
		InputStream redisInputStream = loader.getResourceAsStream("redis.properties");
		Properties redisPro = new Properties();
		redisPro.load(redisInputStream);
		String redis_ip = redisPro.getProperty("redis_ip");
		int redis_port = Integer.parseInt(redisPro.getProperty("redis_port"));
		int redis_timeout = Integer.parseInt(redisPro.getProperty("redis_timeout"));
		String redis_auth = redisPro.getProperty("redis_auth");
		String redis_split = redisPro.getProperty("redis_split");
		Constants.REDIS_SERVER_ADDRESS=redis_ip;
		Constants.REDIS_SERVER_PORT=redis_port;
		Constants.REDIS_TIMOUT=redis_timeout;
		Constants.REDIS_AUTH=redis_auth;
		Constants.REDIS_SPLIT=redis_split;
		
		
		//hbase配置
		InputStream hbaseInputStream = loader.getResourceAsStream("hbase.properties");
		Properties hbasePro = new Properties();
		hbasePro.load(hbaseInputStream);
		String hbase_zookeeper_quorum = hbasePro.getProperty("hbase_zookeeper_quorum");//地址
		String hbase_zookeeper_property_clientPort = hbasePro.getProperty("hbase_zookeeper_property_clientPort");//端口
		//int hbase_pool_size = Integer.parseInt(hbasePro.getProperty("hbase_pool_size"));//连接池
		String hbase_master=hbasePro.getProperty("hbase_master");
		int hbase_writebuffer=Integer.parseInt(hbasePro.getProperty("hbaseWriteBuffer"));
		Constants.HBASE_ZOOKEEPER_QUORUM=hbase_zookeeper_quorum;
		Constants.HBASE_CLIENT_PORT=hbase_zookeeper_property_clientPort;
		Constants.HBASE_MASTER=hbase_master;
		Constants.HBASE_WRITE_BUFFER = hbase_writebuffer;//文件检索目录
		
		//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
