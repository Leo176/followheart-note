import com.leo.domain.note.Notebook;

public class RandomTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//输出时间戳
		//Long createTime = System.currentTimeMillis();
		//System.out.println(createTime);
		
		//测试lombok插件
		Notebook notebook=new Notebook("ghj", "sd", "d", "d","d");
		//Notebook notebook=new Notebook();
		//notebook.setNotebookName("hello");
		System.out.println(notebook.getClass());
		System.out.println(notebook);
	}

}
