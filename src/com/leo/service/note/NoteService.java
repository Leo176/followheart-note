package com.leo.service.note;

import java.util.List;

import com.leo.domain.note.Note;
import com.leo.domain.note.Notebook;

public interface NoteService {

	List<Notebook> getAllNotebooks(String userName);

	boolean addNotebook(String userName,String rowkey, String notebookName, String createTime,String status);
	
	boolean updateNotebook(String userName,String createTime, String status, String oldNotebookName, String newNotebookName2);

	boolean deleteNotebook(String rowKey, String userName, String notebookName, String createTime, String status);
	
	boolean addNote(String nbRowkey, String nRowkey, String noteName, String createTime, String status,String content);

	boolean updateNote(String nbRowkey, String nRowkey, String oldNoteName, String newNoteName, String createTime,
			String status, String content);
	//通过笔记本的rowKey获取笔记列表
	List<Note> getNoteListByNotebook(String rowkey);
	//通过笔记的rowKey获取笔记详情(包括内容)
	Note getNoteByRowkey(String noteRowkey);
	//删除笔记
	boolean deleteNote(String noteBookRowkey, String noteRowKey, String oldNoteName);
	//移动并删除笔记,适用于删除到垃圾箱、笔记迁移
	boolean moveAndDeleteNote(String noteRowKey, String oldNoteBookRowkey, String newNoteBookRowkey, String noteName);

	boolean starOtherNote(String noteRowKey, String starNBRowKey);
	//笔记参与活动
	boolean note2Activity(String noteRowKey, String activityBookRowkey);

	
	
	
}
