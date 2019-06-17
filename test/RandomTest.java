import org.yecht.Data.Str;

import com.leo.domain.note.Notebook;
import com.leo.util.Constants;

public class RandomTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//输出时间戳
		//Long createTime = System.currentTimeMillis();
		//System.out.println(createTime);
		
		
		//  用于测试转义   结果表明只有|需要强行转义  加\\     _可转可不转
		/** 
		String rowKey="leo_15522222_5555_1515";
		System.out.println("rowkey: "+rowKey);
		String[] split = rowKey.split("_");
		for(String a:split) {
			System.out.println(a);
		}
		*/
		
		/*//测试null和""的区别
		String a="";
		System.out.println("The length of a: "+a.length());
		String b=null;
		System.out.println("The length of b: "+b.length());*/
		
	/*	//测试string.split
		String liString="";
		String[]splits=liString.split(",");
		System.out.println("length: "+splits.length );
		for(String split:splits) {
			System.out.println("run----");
			System.out.println(split);
		}*/
		/*String aString="abc";
		StringBuffer sb = new StringBuffer(aString);
		sb.delete(0, 1);
		aString=new String(sb);
		System.out.println(aString);*/
		
		 byte[]a=null;
		 System.out.println(a.length);

	}

}
