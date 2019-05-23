package com.leo.service.note;

import java.util.List;

import com.leo.domain.note.Notebook;

public interface NoteService {

	List<Notebook> getAllNotebooks(String userName);

	boolean addNotebook(String userName,String rowkey, String notebookName, String createTime,String status);
	
	boolean updateNotebook(String userName,String createTime, String status, String oldNotebookName, String newNotebookName2);

	boolean deleteNotebook(String rowKey, String userName, String notebookName, String createTime, String status);
	
	boolean addNote(String nbRowkey, String nRowkey, String noteName, String createTime, String status);

	boolean updateNote(String nbRowkey, String nRowkey, String oldNoteName, String newNoteName, String createTime,
			String status);

	
	
	
}
