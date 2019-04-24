package com.leo.dao.note.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.leo.dao.note.RedisDao;
import com.leo.domain.note.Notebook;
import com.leo.util.Constants;
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
		
		List<Notebook>notebookList=new ArrayList<Notebook>();;
		//得到bookList
		List<String>bookList=RedisTool.getAllNotebooks(userName);
		//从bookList中读取信息并封装成List<Notebook>
		//bookString典例:senfeng_134223232343|aaaddd|1401761871307|0
		for(String bookString:bookList) {
			System.out.println("bookstring:  "+bookString);
			Notebook notebook = new Notebook();
			String[]splits=bookString.split("\\"+Constants.REDIS_SPLIT);
			notebook.setRowkey(splits[0]);
			notebook.setNotebookName(splits[1]);
			notebook.setCreateTime(splits[2]);
			notebook.setState(splits[3]);
			notebookList.add(notebook);
		}
		return notebookList;
	}

	@Override
	public boolean addNotebook(String userName, String redisValueString) {
		// TODO Auto-generated method stub
		
		if(RedisTool.addNotebook(userName,redisValueString))
			return true;
		else return false;
	}

	@Override
	public void deleteNewAdded(String userName) {
		// TODO Auto-generated method stub
		RedisTool.deleteNewAdded(userName);
	}

}
