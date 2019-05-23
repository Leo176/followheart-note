package com.leo.controller.note;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.leo.domain.note.Notebook;
import com.leo.service.note.NoteService;
import com.leo.util.Constants;
import com.leo.util.RowkeyUtil;


@Controller("noteController")
@RequestMapping("/note")
public class NoteController {
	
	//统一采用RESTFUl风格和驼峰命名法
	
	private static Logger logger = LoggerFactory.getLogger(NoteController.class);
	
	@Resource
	private NoteService noteService;

	/**
	 * 登录后获取指定用户的所有笔记本
	 * 
	 * @param request
	 * @return 通过ModelMap返回 List<NoteBook>和三种特殊笔记本的rowkey(以json格式返回)
	 */
	@RequestMapping("/getAllNotebooks")
	public ModelAndView getAllNotebooks(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView() ;
		String userName = null;
		// 从session中获取用户名
		userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
		System.out.println("需要查询的用户名字：  "+userName);
		try {
			// 查询用户笔记本
			List<Notebook> allNotebooks = noteService.getAllNotebooks(userName);// 查询所有笔记本
			// 封装返回值
			ModelMap map = new ModelMap();
			map.put("allNoteBook", allNotebooks);
			map.put("recycleNBRowKey", userName + Constants.RECYCLE);
			map.put("starNBRowKey", userName + Constants.STAR);
			map.put("activityNBRowKey", userName + Constants.ACTIVITY);
			System.out.println("allNoteBook: "+ allNotebooks);
			//作用：将多个对象通过map返回，并以json形式返回，所以要利用MappingJacksonJsonView类来构建ModelAndView对象
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "获取所有笔记本异常|方法:getAllNoteBook|参数： userName:" + userName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("showNotebook");
		return modelAndView;
	}

	/**
	 * 正常获取指定用户的所有笔记本（与第一个的区别在于这里需要正式的传参username）
	 * 
	 * @param request
	 * @return
	 */
	
	@RequestMapping("/getAllNotebooksByUser")
	public ModelAndView getAllNoteBookByUserName(HttpServletRequest request,String userName) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			request.getSession().setAttribute(Constants.USER_INFO, userName);
			List<Notebook> allNoteBook = noteService.getAllNotebooks(userName);// 查询所有笔记本
			ModelMap map = new ModelMap();
			map.put("allNoteBook", allNoteBook);
			map.put("recycleBtRowKey", userName + Constants.RECYCLE);
			map.put("starBtRowKey", userName + Constants.STAR);
			map.put("activityBtRowKey", userName + Constants.ACTIVITY);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "获取所有笔记本异常|方法:getAllNoteBookByUserName|参数： userName:"
					+ userName, e);
			e.printStackTrace();
		}
		return modelAndView;
	}
	/**
	 * 新建笔记本
	 * 
	 * @param notebookName
	 * @return
	 */
	
	@RequestMapping("/addNotebook/{notebookName}")
	public ModelAndView addNotebook(HttpServletRequest request,@PathVariable String notebookName) {
		
		ModelAndView modelAndView = new ModelAndView();
		// 从session中获取用户名
		String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
		// 创建时间戳
		Long createTimeLong = System.currentTimeMillis();
		String createTime=createTimeLong.toString();
		//生成rowkey
		String rowkey=RowkeyUtil.getRowkey(userName,createTime);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.addNotebook(userName,rowkey,notebookName,createTime,"0");
			
			ModelMap map = new ModelMap();
			map.put("isSuccessful", isSuccessful);
			map.put("notebookName", notebookName);
			map.put("newNoteookRowkey", rowkey);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName+ "新建笔记本异常|方法:addNotebook|参数： userName:"
		+ userName+" notebookName: "+notebookName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("newNotebookDetails");
		return modelAndView;
	}
	
	
	/**
	 * 修改笔记本信息
	 * 
	 * @param notebookName
	 * @return
	 */
	
	@RequestMapping(value="/updateNotebook/oldNotebookName")
	public ModelAndView updateNotebook(HttpServletRequest request,String rowkey,@PathVariable String oldNotebookName
			,String newNotebookName) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=rowkey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.updateNotebook(splits[0],splits[1],"0", oldNotebookName, newNotebookName);
			ModelMap map = new ModelMap();
			map.put("isSuccessful", isSuccessful);
			map.put("newNotebookName", newNotebookName);
			map.put("rowkey", rowkey);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "修改笔记本异常|方法:updateNotebook|参数： userName:"
		+ userName+" oldnNotebookName: "+oldNotebookName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("updateNotebook");
		return modelAndView;
	}
	/**
	 * 删除笔记本
	 * 
	 * @param notebookName rowkey
	 * @return
	 */
	
	@RequestMapping(value="/deleteNotebook/notebookName")
	public ModelAndView deleteNotebook(HttpServletRequest request,String rowkey,@PathVariable String notebookName) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=rowkey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.deleteNotebook(rowkey, splits[0], notebookName, splits[1], "0");
			
			ModelMap map = new ModelMap();
			map.put("isSuccessful", isSuccessful);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
			
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "删除笔记本异常|方法:deleteNotebook|参数： rowkey:"
		+ rowkey+" notebookName: "+notebookName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("updateNotebook");
		return modelAndView;
	}
	
	/**
	 * 新建笔记
	 * 
	 * @param noteName、nbRowkey、userName
	 * @return
	 */
	
	@RequestMapping(value="/addNote/{noteName}")
	public ModelAndView addNote(HttpServletRequest request,@PathVariable String noteName) {
		
		ModelAndView modelAndView = new ModelAndView();
		// 从session中获取用户名
		String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
		// 从session中获取所属笔记本rowkey
		String nbRowkey = (String) request.getSession().getAttribute("nbRowkey");
		// 创建时间戳
		Long createTimeLong = System.currentTimeMillis();
		String createTime=createTimeLong.toString();
		//生成笔记rowkey
		String nRowkey=RowkeyUtil.getRowkey(userName,createTime);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.addNote(nbRowkey,nRowkey,noteName,createTime,"0");
			
			ModelMap map = new ModelMap();
			map.put("isSuccessful", isSuccessful);
			map.put("nbRowkey", nbRowkey);
			map.put("nRowkey", nRowkey);
			map.put("noteName", noteName);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName+ "新建笔记异常|方法:addNote|参数： userName:"
		+ userName+" noteName: "+noteName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("newNoteDetails");
		return modelAndView;
	}
	/**
	 * 修改笔记信息(包括标题和内容) 
	 * 
	 * @param 
	 * @return
	 */
	
	@RequestMapping(value="/updateNote/oldNoteName")
	public ModelAndView updateNote(HttpServletRequest request,String nbRowkey,String nRowkey
			,@PathVariable String oldNoteName,String newNoteName,String content) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=nbRowkey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.updateNote(nbRowkey,nRowkey,oldNoteName
					,newNoteName,splits[1],"0");
			ModelMap map = new ModelMap();
			map.put("isSuccessful", isSuccessful);
			map.put("newNoteName", newNoteName);
			map.put("oldNoteName", oldNoteName);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "修改笔记异常|方法:updateNote|参数： nbRowkey:"
		+ nbRowkey+" oldNoteName: "+oldNoteName, e);
			e.printStackTrace();
		}
		modelAndView.setViewName("updateNote");
		return modelAndView;
	}
	
}
