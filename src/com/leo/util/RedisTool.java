package com.leo.util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTool {
	static {
	//初始化连接池
	JedisPoolConfig jpc = new JedisPoolConfig();
	jpc.setMaxActive(100);
	//
	jp = new JedisPool(jpc, Constants.SERVER_ADDRESS, Constants.SERVER_PORT, 2000);
	}
	销毁连接池
	jp.destroy();


	public static void getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		
	}

}
