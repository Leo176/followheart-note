package com.leo.dao.note.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.leo.dao.note.RedisDao;
import com.leo.domain.note.Notebook;
import com.leo.util.RedisTool;

import redis.clients.jedis.Jedis;

@Service("redisDaoImpl")
public class RedisDaoImpl implements RedisDao {

	@Override
	public List<Notebook> getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		RedisTool.getAllNotebooks(userName);
		return null;
	}

}
