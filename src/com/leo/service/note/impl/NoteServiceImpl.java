package com.leo.service.note.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import com.leo.dao.note.HbaseDao;
import com.leo.dao.note.RedisDao;
import com.leo.domain.note.Notebook;
import com.leo.service.note.NoteService;
import com.leo.util.Constants;

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
		List<Notebook>notebookList1=new ArrayList<Notebook>();
		List<Notebook>notebookList2=new ArrayList<Notebook>();
		//从redis中获取
		notebookList1=redisDao.getAllNotebooks(userName);
		
		
		//用于测试，将notebookList1人为设为null，以测试hbase功能
		//notebookList1=null;
		
		
		//从redis中获取失败,则从hbase中获取
		//  1 如果皆为空则说明真的为空，无需更新至redis中
		//  2否则更新至redis中
		if(notebookList1==null) {
			ResultScanner results=hbaseDao.getAllNotebooks(userName);
			//逐条读取至Notebook Bean中
			for(Result result:results) {
				Notebook book=new Notebook();
				book.setRowkey(Bytes.toString(result.getRow())); //rowkey
				book.setNotebookName(Bytes.toString(result.getValue(
						Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_NAME))));
				book.setCreateTime(Bytes.toString(result.getValue(
						Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_CREATETIME))));
				book.setState(Bytes.toString(result.getValue(
						Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_STATUS))));
			System.out.println("NoteServiceImpl中打印booknote: "+book);
				notebookList2.add(book);
			}
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

	@Override
	public boolean addNotebook(String userName,String rowkey, String notebookName,Long createTime,int status) {
		// TODO Auto-generated method stub
		//构建redisValueString
		String redisValueString=rowkey+Constants.REDIS_SPLIT+notebookName+Constants.REDIS_SPLIT+createTime
				+Constants.REDIS_SPLIT+status;
		boolean isRedisSuccessful=redisDao.addNotebook(userName,redisValueString);
		//如果redis失败，整个过程失败
		if(!isRedisSuccessful)
			return false;
		boolean isHbaseSuccessful=hbaseDao.addNotebook(rowkey,notebookName,createTime,status);
		//皆成功
		if(isRedisSuccessful&&isHbaseSuccessful)
			return true;
		//redis成功  hbase失败：删除redis新增的内容
		else{
			redisDao.deleteNewAdded(userName);
			return false;
		}
	}

	@Override
	public boolean addNote(String nbRowkey, String nRowkey, String noteName, Long createTime, int status) {
		// TODO Auto-generated method stub
		//notebook表中的notelist(nl)列添加信息
		boolean isSuccessful=hbaseDao.addNotebookList(nbRowkey,nRowkey,noteName,createTime,status);
		return isSuccessful;
	}

}
