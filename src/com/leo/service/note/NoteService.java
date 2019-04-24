package com.leo.service.note;

import java.util.List;

import com.leo.domain.note.Notebook;

public interface NoteService {

	List<Notebook> getAllNotebooks(String userName);

	boolean addNotebook(String userName,String rowkey, String notebookName, Long createTime,int status);

	boolean addNote(String nbRowkey, String nRowkey, String noteName, Long createTime, int status);
}
