package com.leo.service.note;

import java.util.List;

import com.leo.domain.note.Notebook;

public interface NoteService {

	List<Notebook> getAllNotebooks(String userName);
}
