package com.leo.util;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;


public class HbaseTool {
	public static Configuration configuration;
	public static Connection connection;
	public static Admin admin;
	//静态代码段进行连接
	static {
		System.setProperty("hadoop.home.dir","E:\\hadoop-common-bin-master\\hadoop-common-bin-master\\2.7.1");
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.property.clientPort", Constants.HBASE_CLIENT_PORT);  
        configuration.set("hbase.zookeeper.quorum", Constants.HBASE_ZOOKEEPER_QUORUM);  
        //configuration.set("hbase.master",Constants.HBASE_MASTER); 
        try {
			connection = ConnectionFactory.createConnection(configuration);
			admin = connection.getAdmin();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
