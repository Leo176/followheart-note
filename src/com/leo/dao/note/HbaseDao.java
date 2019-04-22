package com.leo.dao.note;

import java.util.List;

import com.leo.domain.note.Notebook;

public interface HbaseDao {

	List<Notebook> getAllNotebooks(String userName);

}
