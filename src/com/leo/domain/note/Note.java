package com.leo.domain.note;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Note {
	//rowkey
	private String rowkey;
	//名字
	private String notename;
	//创建时间
	private String createTme;
	//笔记状态
	private String status;
	//笔记内容
	private String content;


}
