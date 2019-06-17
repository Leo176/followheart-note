package com.leo.domain.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
//@RequiredArgsConstructor
@NoArgsConstructor
public class Notebook {
	//rowkey
	private String rowKey;
	//notebookname（nbn）:笔记本名称
	private String name;
	//createTime（ct）:创建时间
	private String createTime;
	//status（st）:状态
	private String state;
	//noteList（nl）：笔记本下的笔记列表，是个json串（noteRowKey|name| createTime| status）
	private String noteList;

}
