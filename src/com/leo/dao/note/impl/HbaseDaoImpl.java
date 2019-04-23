package com.leo.dao.note.impl;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

import com.leo.dao.note.HbaseDao;
import com.leo.domain.note.Notebook;
import com.leo.util.Constants;
import com.leo.util.HbaseTool;

public class HbaseDaoImpl implements HbaseDao {

	/*通过userName从hbase中读取笔记本列表*/
	
	@Override
	public ResultScanner getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		 // public static void getData(String tableName,String rowKey,String colFamily,String col)throws  IOException{        
		ResultScanner scanner = null;
		try {
		//构建正则表达式 username_*	
		    String regex=userName+"_"+"*";        
			Table table = HbaseTool.connection.getTable(TableName.valueOf(Constants.NBTABLENAME));
			Scan scan = new Scan();
			RowFilter filter=new RowFilter(CompareOp.EQUAL, new RegexStringComparator(regex));// 创建正则表达式filter,比较为相等
			scan.setFilter(filter);// 设置filter
			//设置查询列族
			scan.addFamily(Bytes.toBytes(Constants.NOTEBOOK_FAMILY));
			//设置查询列
			scan.addColumn(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_NAME));
			scan.addColumn(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_CREATETIME));
			scan.addColumn(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), Bytes.toBytes(Constants.NOTEBOOK_COLUMN_STATUS));
			
			//查询
			scanner = table.getScanner(scan);
			 // 通过scan查询结果
			table.close();// 关闭table
			
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return scanner;
	}

}
