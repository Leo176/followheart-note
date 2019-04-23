package com.leo.util;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leo.controller.note.NoteController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisTool {
	
	private static Logger logger = LoggerFactory.getLogger(NoteController.class);
	
	private static JedisPool jedisPool;
	static {
	//初始化连接池
	JedisPoolConfig jpc = new JedisPoolConfig();
	jpc.setMaxIdle(1000);//最大空闲
	jpc.setMaxTotal(10240);//最大连接数
	
	if(jedisPool == null){
		//config：配置参数；1 IP    2 ：默认端口号:6379 可以更改；
		jedisPool = new JedisPool(jpc, Constants.REDIS_SERVER_ADDRESS, Constants.REDIS_SERVER_PORT, 0);
	}
	}
	
	//获取线程池
	public static Jedis getJedis() {
		return jedisPool.getResource();
	}


	public static List<String> getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		Jedis jedis = RedisTool.getJedis();
		List<String> bookList=null;
		try {
			//获取key为{userName}的所有value值，每一个值皆为类似senfeng_134223232343|aaaddd|1401761871307|0这样的字符串
			bookList = jedis.lrange(userName, 0, -1);
			
		} catch (JedisConnectionException e) {
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
				e.printStackTrace();
			}
		} finally {
			if (null != jedis)
			jedisPool.returnResource(jedis);
		}
		return bookList;

	}

}
