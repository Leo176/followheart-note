package com.leo.domain.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor()
public class Note {
	//rowKey
	private String rowKey;
	//名字
	private String name;
	//创建时间
	private String createTime;
	//笔记状态
	private String status;
	//笔记内容
	private String content;

	public Note(String rowKey,String name,String createTime,String status) {
		this.rowKey=rowKey;
		this.name=name;
		this.createTime=createTime;
		this.status=status;
	}

}
