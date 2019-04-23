package com.leo.dao.note.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.leo.dao.note.RedisDao;
import com.leo.domain.note.Notebook;
import com.leo.util.RedisTool;

import redis.clients.jedis.Jedis;

@Service("redisDaoImpl")
public class RedisDaoImpl implements RedisDao {

	/**
	 * @param userName
	 * @return List<Notebook>
	 */
	@Override
	public List<Notebook> getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		
		List<Notebook>notebookList=null;
		//得到bookList
		List<String>bookList=RedisTool.getAllNotebooks(userName);
		//从bookList中读取信息并封装成List<Notebook>
		//bookString典例:senfeng_134223232343|aaaddd|1401761871307|0
		for(String bookString:bookList) {
			Notebook notebook = null;
			String[]splits=bookString.split("|");
			notebook.setRowkey(splits[0]);
			notebook.setNotebookName(splits[1]);
			notebook.setCreateTime(splits[2]);
			notebook.setState(splits[3]);
			notebookList.add(notebook);
		}
		return notebookList;
	}

}
