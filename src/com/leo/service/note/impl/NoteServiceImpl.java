package com.leo.service.note.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.leo.dao.note.HbaseDao;
import com.leo.dao.note.RedisDao;
import com.leo.domain.note.Notebook;
import com.leo.service.note.NoteService;

@Service
public class NoteServiceImpl implements NoteService {
	@Resource(name = "redisDaoImpl")
	private RedisDao redisDao;
	@Resource(name = "hbaseDaoImpl")
	private HbaseDao hbaseDao;

//根据userName获取笔记本列表,先从redis中获取，如果获取失败，再从hbase中获取，并更新至redis中
	@Override
	public List<Notebook> getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		List<Notebook>notebookList1=null;
		List<Notebook>notebookList2=null;
		//从redis中获取
		notebookList1=redisDao.getAllNotebooks(userName);
		
		//从redis中获取失败,则从hbase中获取
		//  1 如果皆为空则说明真的为空，无需更新至redis中
		//  2否则更新至redis中
		if(notebookList1==null) {
			notebookList2=hbaseDao.getAllNotebooks(userName);
			if(notebookList2==null) {
				//无需更新redis
			}
			else {
				//更新redis
			}
			return notebookList2;
		}
		else {
			return notebookList1;
		}
	}

}
