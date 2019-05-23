import com.leo.domain.note.Notebook;
import com.leo.util.Constants;

public class RandomTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//输出时间戳
		//Long createTime = System.currentTimeMillis();
		//System.out.println(createTime);
		
		//测试lombok插件
		//Notebook notebook=new Notebook("ghj", "sd", "d", "d","d");
		//Notebook notebook=new Notebook();
		//notebook.setNotebookName("hello");
		//System.out.println(notebook.getClass());
		//System.out.println(notebook);
		
		
		//  用于测试转义   结果表明只有|需要强行转义  加\\     _可转可不转
		/** 
		String rowKey="leo_15522222_5555_1515";
		System.out.println("rowkey: "+rowKey);
		String[] split = rowKey.split("_");
		for(String a:split) {
			System.out.println(a);
		}
		*/
		/**
		String aString;
		String bString;
		String cString="dfs";
		aString="dfs";
		//bString="dfs";
		bString=cString;
		if(aString==bString)
			System.out.println(true);
		else
			System.out.println(false);
		if(aString.equals(bString))
			System.out.println(true);
		else
			System.out.println(false);
			*/
	}

}
