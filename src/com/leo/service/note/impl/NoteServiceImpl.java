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
import com.leo.domain.note.Note;
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
				book.setRowKey(Bytes.toString(result.getRow())); //rowkey
				book.setName(Bytes.toString(result.getValue(
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
	 * 通过笔记本的rowKey获取笔记列表
	 * @param rowkey
	 */
	@Override
	public List<Note> getNoteListByNotebook(String rowkey) {
		// TODO Auto-generated method stub
		//返回查询的notelist字符串
		List<Note>noteList =new ArrayList<Note>();
		String noteListString=hbaseDao.getNoteListByNotebook(rowkey);
		//字符串为空时
		if(noteListString==null || noteListString.length() == 0) {
			System.out.println("NoteService中通过笔记本的rowKey获取笔记列表得到的字符串notelist为空---------");
			return noteList;
	}
		//字符串不为空时
		else {
			String[]notes=noteListString.split(",");
			for(String noteString:notes) {
				//避免出现空
				if(noteString.length()!=0) {
				String[]info=noteString.split("\\"+Constants.REDIS_SPLIT);
				Note note=new Note(info[0], info[1], info[2], info[3]);
				noteList.add(note);
				}
			}
		}
			return noteList;
	}
	
	/**
	 * 新增Note
	 */
	@Override
	public boolean addNote(String nbRowkey, String nRowkey, String noteName, String createTime, String status,String content) {
		// TODO Auto-generated method stub
		
		//notebook表中的notelist(nl)列添加信息
		boolean addToNotebookTable=hbaseDao.addNotebookList(nbRowkey,nRowkey,noteName,createTime,status);
		//note表中添加一行数据(由于是新增，所以内容设为空)
		boolean addToNoteTable=hbaseDao.addToNoteTable(nRowkey, noteName, createTime, status,content);
		
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
			,String newNoteName, String createTime, String status,String content) {
		
		boolean updataToNotebookTable=true;
		//修改了Note的名字则notebook表中也需修改，否则只需修改note表即可
		if(!oldNoteName.equals(newNoteName))
			//notebook表中的notelist(nl)列修改信息
			updataToNotebookTable=hbaseDao.updateNotebookList(nbRowkey, nRowkey, oldNoteName, newNoteName, createTime, status);
		
		//note表中修改数据
		boolean updateToNoteTable=hbaseDao.updateToNoteTable(nRowkey, newNoteName, createTime, status,content);
		
		/**
		 * 事务管理暂未编写
		 */
		return (updataToNotebookTable&&updateToNoteTable);
	}
	
	/**
	 * 通过笔记的rowKey获取笔记详情(包括内容)
	 * @param noteRowkey
	 */
	@Override
	public Note getNoteByRowkey(String noteRowkey) {
		//调用hbaseDao接口获得Result
		Result result=hbaseDao.getNoteByRowkey(noteRowkey);
		if(result.isEmpty()) {
			System.out.println("note表中不存在该条笔记");
			return null;
		}
		else {
			Note note=new Note();
			note.setRowKey(Bytes.toString(result.getRow())); //rowkey
			note.setName(Bytes.toString(result.getValue(
					Bytes.toBytes(Constants.NOTE_INFO_FAMILY), Bytes.toBytes(Constants.NOTE_COLUMN_NAME))));
			note.setCreateTime(Bytes.toString(result.getValue(
					Bytes.toBytes(Constants.NOTE_INFO_FAMILY), Bytes.toBytes(Constants.NOTE_COLUMN_CREATETIME))));
			note.setStatus(Bytes.toString(result.getValue(
					Bytes.toBytes(Constants.NOTE_INFO_FAMILY), Bytes.toBytes(Constants.NOTE_COLUMN_STATUS))));
			note.setContent(Bytes.toString(result.getValue(
					Bytes.toBytes(Constants.NOTE_CONTENT_FAMILY), Bytes.toBytes(Constants.NOTE_COLUMN_CONTENT))));
			System.out.println("Service中通过笔记的rowKey获取笔记详情 Note："+note);
			return note;
		}
	}
	/**
	 * 删除笔记
	 */
	@Override
	public boolean deleteNote(String noteBookRowkey, String noteRowKey, String oldNoteName) {
		
		String createTime=RowkeyUtil.getCreateTimeFromRowkey(noteRowKey);
		
		//notebook表中的notelist(nl)列删除信息
		boolean deleteInNotebookTable=hbaseDao.deleteNoteInNotebookTable(noteBookRowkey, noteRowKey, oldNoteName
				, createTime, "0");
		
		//note表中修改数据
		boolean deleteInNoteTable=hbaseDao.deleteNoteInNoteTable(noteRowKey);
		
		/**
		 * 事务管理暂未编写
		 * 
		 * 
		 */
		return (deleteInNotebookTable&&deleteInNoteTable);
	}
	
	/**
	 * 移动并删除笔记,适用于删除到垃圾箱、笔记迁移
	 */
	@Override
	public boolean moveAndDeleteNote(String noteRowKey, String oldNoteBookRowkey, String newNoteBookRowkey,
			String noteName) {
		
		String createTime=RowkeyUtil.getCreateTimeFromRowkey(noteRowKey);
		
		//在源笔记本中删除该笔记
		boolean isSucc1=hbaseDao.deleteNoteInNotebookTable(oldNoteBookRowkey, noteRowKey, noteName, createTime, "0");
		//在目标笔记本中增加该笔记
		boolean isSucc2=hbaseDao.addNotebookList(newNoteBookRowkey, noteRowKey, noteName, createTime, "0");
		
		/**
		 * 事务管理暂未编写
		 * 
		 * 
		 */
		
		return (isSucc1&&isSucc2);
	}
	
	/**
	 * 收藏笔记
	 * 
	 * @param starNBRowKey
	 * @param noteRowKey
	 * @return
	 */
	
	@Override
	public boolean starOtherNote(String noteRowKey, String starNBRowKey) {
		
		//先从note表中将特定笔记的信息读出来
		Note note=getNoteByRowkey(noteRowKey);
		//借助hbaseDao中的方法addNotebookList
		//在notebook表中rowkey为 user_star的笔记本中添加特定的笔记信息(即在列notelist中append特定的string即可)
		return hbaseDao.addNotebookList(starNBRowKey, noteRowKey, note.getName(), note.getCreateTime(), note.getStatus());
		
	}
	
	
	/**
	 * 活动笔记
	 */
	@Override
	public boolean note2Activity(String noteRowKey, String activityBookRowKey) {
		Note note = getNoteByRowkey(noteRowKey);// 获取笔记信息
	
		String userName=RowkeyUtil.getUserNameFromRowkey(noteRowKey);//得到用户名
		// 创建时间
		Long createTimeLong = System.currentTimeMillis();
		String createTime=createTimeLong.toString();
		//为了在note表中新增一条记录，新构建一个rowkey
		String newNoteRowkey=RowkeyUtil.getRowkey(userName, createTime);
		//新增笔记，活动文件夹和note表同时增加一条记录
		boolean addNote = addNote(activityBookRowKey, newNoteRowkey, note.getName()
				, note.getCreateTime(), note.getStatus(),note.getContent());
		return addNote;
	}
	


}
