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
		
		System.out.println("运行RedisDaoImpl中的方法");
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
	
	/**
	 * 修改notebook的名称
	 * @param oldNotebookString newNotebookString
	 * 
	 */

	@Override
	public boolean updateNotebook(String userName, String oldNotebookString, String newNotebookString) {
		
		//获取所有笔记本列表
		List<String> bookList=RedisTool.getAllNotebooks(userName);
		//索引下标
		int index=0;
		for(String book:bookList) {
			//找到旧信息，则删除并插入新的
			if(book.equals(oldNotebookString)) {
				System.out.println("找到旧信息,Redis修改之");
				RedisTool.updateNotebookname(userName, newNotebookString, index);
				return true;
			}
			else 
				index++;
		}
		return false;
	}
	
	
	/**
	 * 删除notebook
	 * @param oldNotebookString newNotebookString
	 * 
	 */
	@Override
	public boolean deleteNotebook(String rowKey,String userName,String notebookName, String createTime,String status) {
		
		String notebookString=rowKey+Constants.REDIS_SPLIT+notebookName
				+Constants.REDIS_SPLIT+createTime+Constants.REDIS_SPLIT+status;
		
		return RedisTool.deleteNotebook(userName, notebookString);
	}

	@Override
	public void deleteNewAdded(String userName) {
		// TODO Auto-generated method stub
		RedisTool.deleteNewAdded(userName);
	}

	@Override
	public boolean updateNotebookList(String userName,List<Notebook> notebooks) {
		// TODO Auto-generated method stub
		System.out.println("根据hbase中数据更新redis---------");
		for(Notebook book:notebooks) {
		String redisValueString=book.getRowkey()+Constants.REDIS_SPLIT+book.getNotebookName()+Constants.REDIS_SPLIT
				+book.getCreateTime()+Constants.REDIS_SPLIT+book.getState();
		
		if(!addNotebook(userName, redisValueString)) {
			return false;
		}
		}
		return true;
	}

}
