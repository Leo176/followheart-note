package com.leo.dao.note;

import java.util.List;

import com.leo.domain.note.Note;
import com.leo.domain.note.Notebook;

public interface RedisDao {

	List<Notebook> getAllNotebooks(String userName);

	boolean addNotebook(String userName, String redisValueString);

	void deleteNewAdded(String userName);
	
	boolean updateNotebook(String userName,String oldNotebookString,String newNotebookString);
	
	boolean deleteNotebook(String rowKey, String userName, String notebookName, String createTime, String status);
	
	
	//根据hbase中数据更新redis
	boolean updateNotebookList(String userName,List<Notebook> notebooks);



	

}
