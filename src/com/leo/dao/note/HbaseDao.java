package com.leo.dao.note;

import java.util.List;

import org.apache.hadoop.hbase.client.ResultScanner;

import com.leo.domain.note.Notebook;

public interface HbaseDao {

	ResultScanner getAllNotebooks(String userName);

	boolean addNotebook(String rowkey, String notebookName, String createTime, String status);


	boolean updateNotebook(String userName, String createTime, String status, String oldNotebookName,
			String newNotebookName);

	boolean deleteNotebook(String rowKey);
	
	boolean addNotebookList(String nbRowkey, String nRowkey, String noteName, String createTime, String status);

	boolean addToNoteTable(String nRowkey, String noteName, String createTime, String status);

	boolean updateNotebookList(String nbRowkey, String nRowkey, String oldNoteName, String newNoteName,
			String createTime, String status);

	boolean updateToNoteTable(String nRowkey, String newNoteName, String createTime, String status);

}
