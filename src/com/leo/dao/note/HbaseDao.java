package com.leo.dao.note;

import java.util.List;

import org.apache.hadoop.hbase.client.ResultScanner;

import com.leo.domain.note.Notebook;

public interface HbaseDao {

	ResultScanner getAllNotebooks(String userName);

	boolean addNotebook(String rowkey, String notebookName, Long createTime, int status);

	boolean addNotebookList(String nbRowkey, String nRowkey, String noteName, Long createTime, int status);

}
