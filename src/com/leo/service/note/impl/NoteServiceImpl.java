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
import com.leo.util.RowkeyUtil;

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
		if(notebookList1.isEmpty()) {
			System.out.println("从redis中获取失败,从hbase中获取");
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
				if(redisDao.updateNotebookList(userName,notebookList2))
					System.out.println("根据hbase更新redis数据成功！");
				else
					System.out.println("根据hbase更新redis数据失败！");
			}
			return notebookList2;
		}
		else {
			return notebookList1;
		}
	}
/**
 * 添加新的笔记本(注意事务操作)
 * 
 */
	@Override
	public boolean addNotebook(String userName,String rowkey, String notebookName, String createTime,String status) {
		
		//构建redisValueString
		
		String redisValueString=rowkey+Constants.REDIS_SPLIT+notebookName+Constants.REDIS_SPLIT+createTime
				+Constants.REDIS_SPLIT+status;
		boolean isRedisSuccessful=redisDao.addNotebook(userName,redisValueString);
		//如果redis失败，整个过程失败
		if(!isRedisSuccessful)
			return false;
		//redis成功，则进行hbase操作
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

	
	/**
	 * 修改笔记本名称
	 * 
	 * 
	 */
	@Override
	public boolean updateNotebook(String userName,String createTime,String status, String oldNotebookName, String newNotebookName) {
		
				String oldNotebookString=userName+Constants.rowkey_split+createTime+Constants.REDIS_SPLIT+oldNotebookName
						+Constants.REDIS_SPLIT+createTime+Constants.REDIS_SPLIT+status;
				String newNotebookString=userName+Constants.rowkey_split+createTime+Constants.REDIS_SPLIT+newNotebookName
						+Constants.REDIS_SPLIT+createTime+Constants.REDIS_SPLIT+status;
				
				boolean isRedisSuccessful=redisDao.updateNotebook(userName,oldNotebookString,newNotebookString);
				
				//如果redis失败，整个过程失败
				if(!isRedisSuccessful)
					return false;
				
				//redis成功，则进行hbase操作
				boolean isHbaseSuccessful=hbaseDao.updateNotebook(userName, createTime, status, oldNotebookName, newNotebookName);
				
				//皆成功
				if(isRedisSuccessful&&isHbaseSuccessful)
					return true;
				
				//redis成功  hbase失败：redis中的内容复原(即反向修改)
				else{
					redisDao.updateNotebook(userName, newNotebookString, oldNotebookString);
					return false;
				}
	}
	
	/**
	 * 删除笔记本
	 * 
	 * 
	 */
	@Override
	public boolean deleteNotebook(String rowKey,String userName,String notebookName, String createTime,String status) {
		
		String rowkey=RowkeyUtil.getRowkey(userName, createTime);
		
		boolean isRedisSuccessful=redisDao.deleteNotebook(rowkey, userName, notebookName, createTime, status);
		
		//如果redis失败，整个过程失败
		if(!isRedisSuccessful)
			return false;
		
		//redis成功，则进行hbase操作
		boolean isHbaseSuccessful=hbaseDao.deleteNotebook(rowkey);
		
		//皆成功
		if(isRedisSuccessful&&isHbaseSuccessful)
			return true;
		
		//redis成功  hbase失败,redis重新增加回刚被删除的记录
		else{
			
			addNotebook(userName, rowkey, notebookName, createTime, status);
			return false;
		}
	}
	
	/**
	 * 新增Note
	 */
	@Override
	public boolean addNote(String nbRowkey, String nRowkey, String noteName, String createTime, String status) {
		// TODO Auto-generated method stub
		
		//notebook表中的notelist(nl)列添加信息
		boolean addToNotebookTable=hbaseDao.addNotebookList(nbRowkey,nRowkey,noteName,createTime,status);
		//note表中添加一行数据
		boolean addToNoteTable=hbaseDao.addToNoteTable(nRowkey, noteName, createTime, status);
		
		/**
		 * 事务管理暂未编写
		 */
		return (addToNotebookTable&&addToNoteTable);
	}
	
	/**
	 * 修改Note
	 */
	@Override
	public boolean updateNote(String nbRowkey, String nRowkey, String oldNoteName
			,String newNoteName, String createTime, String status) {
		
		boolean updataToNotebookTable=true;
		//修改了Note的名字则notebook表中也需修改，否则只需修改note表即可
		if(!oldNoteName.equals(newNoteName))
			//notebook表中的notelist(nl)列修改信息
			updataToNotebookTable=hbaseDao.updateNotebookList(nbRowkey, nRowkey, oldNoteName, newNoteName, createTime, status);
		
		//note表中修改数据
		boolean updateToNoteTable=hbaseDao.updateToNoteTable(nRowkey, newNoteName, createTime, status);
		
		/**
		 * 事务管理暂未编写
		 */
		return (updataToNotebookTable&&updateToNoteTable);
	}

}
