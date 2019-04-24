package com.leo.dao.note.impl;

import java.io.IOException;

import java.util.List;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.leo.controller.note.NoteController;
import com.leo.dao.note.HbaseDao;
import com.leo.domain.note.Notebook;
import com.leo.util.Constants;
import com.leo.util.HbaseTool;


@Service("hbaseDaoImpl")
public class HbaseDaoImpl implements HbaseDao {
	
	private static Logger logger = LoggerFactory.getLogger(NoteController.class);

	/*通过userName从hbase中读取笔记本列表*/
	
	@Override
	public ResultScanner getAllNotebooks(String userName) {
		// TODO Auto-generated method stub
		 // public static void getData(String tableName,String rowKey,String colFamily,String col)throws  IOException{        
		System.out.println("从hbase中获取笔记本数据----------");
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

	//hbase中插入数据
	@Override
	public boolean addNotebook(String rowkey, String notebookName, Long createTime, int status) {
		// TODO Auto-generated method stub
		try {
		Table table = HbaseTool.connection.getTable(TableName.valueOf(Constants.NBTABLENAME));
		
        Put put = new Put(Bytes.toBytes(rowkey));
        put.add(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), 
        		Bytes.toBytes(Constants.NOTEBOOK_COLUMN_NAME), Bytes.toBytes(notebookName));
        put.add(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), 
        		Bytes.toBytes(Constants.NOTEBOOK_COLUMN_CREATETIME), Bytes.toBytes(createTime));
        put.add(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), 
        		Bytes.toBytes(Constants.NOTEBOOK_COLUMN_STATUS), Bytes.toBytes(status));
        table.put(put);
        table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("HbaseDao 中插入笔记本数据失败-------rowkey" + rowkey
					+ "新建笔记本异常|方法:addNotebook", e);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	//添加新的笔记，在notebook表的notelist中添加信息
	@Override
	public boolean addNotebookList(String nbRowkey, String nRowkey, String noteName, Long createTime, int status) {
		// TODO Auto-generated method stub
		
		/*获取某单元格数据*/
		/**	   * @param tableName 表名
			   * @param rowKey 行键
			   * @param colFamily 列族
			   * @param col 列限定符
		        * @throws IOException     */
		try {
		        Table table = HbaseTool.connection.getTable(TableName.valueOf(Constants.NBTABLENAME));
				
		        Get get = new Get(Bytes.toBytes(nbRowkey));
			    get.addColumn(Bytes.toBytes(Constants.NOTEBOOK_FAMILY),Bytes.toBytes(Constants.NOTEBOOK_COLUMN_NOTELIST));
		        //获取的result数据是结果集，还需要格式化输出想要的数据才行
		        Result result = table.get(get);
		        String note=nRowkey+Constants.REDIS_SPLIT+noteName+Constants.REDIS_SPLIT+createTime
						+Constants.REDIS_SPLIT+status;
		        String newNoteList;
		        if(result.isEmpty()) {
		        	newNoteList=note;
		        }
		       // Get the latest version of the specified column. Note: this call clones the value content
		        //of the hosting Cell. See getValueAsByteBuffer(byte[], byte[]), etc., or listCells() 
		       // if you would avoid the cloning
		        else{
		        	String oldNoteList=new String(result.getValue(Constants.NOTEBOOK_FAMILY.getBytes()
		        			 ,Constants.NOTEBOOK_COLUMN_NOTELIST.getBytes()));
		        	newNoteList=oldNoteList+","+note;
		        }
		    		  
		        Put put = new Put(Bytes.toBytes(nbRowkey));
		        //插入新的
		        put.add(Bytes.toBytes(Constants.NOTEBOOK_FAMILY), 
		        		Bytes.toBytes(Constants.NOTEBOOK_COLUMN_NOTELIST), Bytes.toBytes(newNoteList));
		        table.put(put);
		        table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
