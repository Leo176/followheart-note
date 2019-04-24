package com.leo.dao.note;

import java.util.List;

import com.leo.domain.note.Note;
import com.leo.domain.note.Notebook;

public interface RedisDao {

	List<Notebook> getAllNotebooks(String userName);

	boolean addNotebook(String userName, String redisValueString);

	void deleteNewAdded(String userName);

}
