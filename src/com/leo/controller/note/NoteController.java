package com.leo.controller.note;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.leo.domain.note.Note;
import com.leo.domain.note.Notebook;
import com.leo.service.note.NoteService;
import com.leo.util.Constants;
import com.leo.util.RowkeyUtil;

import net.sf.json.JSONArray;


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
	@RequestMapping("/getAllNoteBook")
	public ModelAndView getAllNotebooks(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView() ;
		String userName = null;
		// 从session中获取用户名
		userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
		System.out.println("需要查询的用户名字：  "+userName);
		try {
			List<Notebook> allNoteBook = noteService.getAllNotebooks(userName);// 查询所有笔记本
			// 封装返回值
			ModelMap map = new ModelMap();
			map.put("allNoteBook", allNoteBook);
			map.put("recycleBtRowKey", userName + Constants.RECYCLE);
			map.put("starBtRowKey", userName + Constants.STAR);
			map.put("activityBtRowKey", userName + Constants.ACTIVITY);
			System.out.println("noteBookList: "+ allNoteBook);
			//作用：将多个对象通过map返回，并以json形式返回，所以要利用MappingJacksonJsonView类来构建ModelAndView对象
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);	
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "获取所有笔记本异常|方法:getAllNoteBook|参数： userName:" + userName, e);
			e.printStackTrace();
		}
		logger.debug("用户" + userName
				+ "获取所有笔记本|方法:getAllNoteBook|参数： userName:" + userName);
		return modelAndView;
	}

	/**
	 * 正常获取指定用户的所有笔记本（与第一个的区别在于这里需要正式的传参username）
	 * 
	 * @param request
	 * @return
	 */
	
	@RequestMapping("/getAllNoteBookByUserName")
	public ModelAndView getAllNoteBookByUserName(HttpServletRequest request,String userName) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			request.getSession().setAttribute(Constants.USER_INFO, userName);
			List<Notebook> allNoteBook = noteService.getAllNotebooks(userName);// 查询所有笔记本
			ModelMap map = new ModelMap();
			map.put("allNoteBook", allNoteBook);
			map.put("recycleNBRowKey", userName + Constants.RECYCLE);
			map.put("starNBRowKey", userName + Constants.STAR);
			map.put("activityNBRowKey", userName + Constants.ACTIVITY);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "获取所有笔记本异常|方法:getAllNoteBookByUserName|参数： userName:"
					+ userName, e);
			e.printStackTrace();
		}
		logger.debug("用户" + userName
				+ "显性根据用户名获取所有笔记本|方法:getAllNoteBookByUserName|参数： userName:" + userName);
		return modelAndView;
	}
	/**
	 * 新建笔记本
	 * 
	 * @param notebookName
	 * @return
	 */
	
	@RequestMapping("/addNoteBook")
	public ModelAndView addNotebook(HttpServletRequest request, String noteBookName) {
		
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
			boolean isSuccessful=noteService.addNotebook(userName,rowkey,noteBookName,createTime,"0");
			
			ModelMap map = new ModelMap();
			map.put("success", isSuccessful);
			//map.put("notebookName", noteBookName);
			map.put("resource", rowkey);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName+ "新建笔记本异常|方法:addNotebook|参数： userName:"
		+ userName+" notebookName: "+noteBookName, e);
			e.printStackTrace();
		}
		logger.debug("用户" + userName
				+ "新建笔记本|方法:addNotebook|参数： userName:" + userName+" notebookName: "+noteBookName);
		return modelAndView;
	}
	
	
	/**
	 * 修改笔记本信息
	 * 
	 * @param notebookName
	 * @return
	 */
	
	@RequestMapping(value="/updateNoteBook")
	public ModelAndView updateNotebook(HttpServletRequest request,String oldNoteBookName
			, String newNoteBookName, String rowKey) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=rowKey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.updateNotebook(splits[0],splits[1],"0", oldNoteBookName, newNoteBookName);
			ModelMap map = new ModelMap();
			map.put("success", isSuccessful);
			//map.put("newNotebookName", newNoteBookName);
			//map.put("rowkey", rowKey);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "修改笔记本异常|方法:updateNotebook|参数： userName:"
		+ userName+" oldnNotebookName: "+oldNoteBookName, e);
			e.printStackTrace();
		}
		logger.debug("用户" 
				+ "修改笔记本信息|方法:updateNotebook|参数： oldNoteBookName:" + oldNoteBookName+" newNoteBookName: "+newNoteBookName);
		return modelAndView;
	}
	/**
	 * 删除笔记本
	 * 
	 * @param notebookName rowkey
	 * @return
	 */
	
	@RequestMapping(value="/deleteNoteBook")
	public ModelAndView deleteNotebook(HttpServletRequest request,String rowKey, String noteBookName) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=rowKey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.deleteNotebook(rowKey, splits[0], noteBookName, splits[1], "0");
			
			ModelMap map = new ModelMap();
			map.put("success", isSuccessful);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
			
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "删除笔记本异常|方法:deleteNotebook|参数： rowkey:"
		+ rowKey+" notebookName: "+noteBookName, e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "删除笔记本|方法:deleteNotebook|参数： rowKey:" + rowKey+"   noteBookName: "+noteBookName);
		return modelAndView;
	}
	
	/**
	 * 获取笔记本的笔记列表(根据笔记本的rowkey获取笔记列表)
	 * 
	 * @param request
	 * @param rowkey  
	 * @return
	 */
	@RequestMapping("/getNoteListByNotebook")
	public ModelAndView getNoteListByNotebook(HttpServletRequest request,
			String rowkey) {
		ModelAndView modelAndView = null;
		try {
			// 获取笔记本的笔记列表
			List<Note> noteList = noteService.getNoteListByNotebook(rowkey);
			if(noteList.isEmpty()) {
				System.out.println("获取笔记本的笔记列表----得到的List<Note> noteList为空");
			}else {
				System.out.println("获取笔记本的笔记列表----成功得到List<Note>noteList且不为空");
			}
			ModelMap map = new ModelMap();
			map.put("noteList", noteList);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			logger.error("用户" + userName
					+ "获取笔记本的笔记异常|方法getNoteListByNotebook|参数:rowkey:" + rowkey,
					e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "根据笔记本的rowkey获取笔记列表|方法:getNoteListByNotebook|参数： rowKey:" + rowkey);
		return modelAndView;
	}
	/**
	 * 新建笔记
	 * 
	 * @param noteName、nbRowkey、userName
	 * @return
	 */
	
	@RequestMapping(value="/addNote")
	public ModelAndView addNote(HttpServletRequest request, String noteName,
			String noteBookRowkey) {
		
		ModelAndView modelAndView = new ModelAndView();
		// 从session中获取用户名
		String userName = (String) request.getSession().getAttribute(Constants.USER_INFO);
		// 创建时间戳
		Long createTimeLong = System.currentTimeMillis();
		String createTime=createTimeLong.toString();
		//生成笔记rowkey
		String nRowkey=RowkeyUtil.getRowkey(userName,createTime);
		try {
			
			//调用服务接口,内容为空，content=""
			boolean isSuccessful=noteService.addNote(noteBookRowkey,nRowkey,noteName,createTime,"0","");
			
			ModelMap map = new ModelMap();
			map.put("success", isSuccessful);
			//map.put("nbRowkey", noteBookRowkey);
			map.put("resource", nRowkey);
			//map.put("noteName", noteName);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName+ "新建笔记异常|方法:addNote|参数： userName:"
		+ userName+" noteName: "+noteName, e);
			e.printStackTrace();
		}
		//modelAndView.setViewName("newNoteDetails");
		logger.debug("用户"
				+ "新建笔记|方法:addNote|参数： noteName:" + noteName+"   noteBookRowkey "+noteBookRowkey);
		return modelAndView;
	}
	/**
	 * 修改笔记信息(包括标题和内容) -----未完成字数限制功能
	 * 
	 * @param 
	 * @return
	 */
	
	@RequestMapping(value="/updateNote")
	public ModelAndView updateNote(HttpServletRequest request, String noteName,
			String oldNoteName, String noteRowKey, String content,
			String noteBookRowkey) {
		
		ModelAndView modelAndView = new ModelAndView();
		//从rowkey中得到userName和createTime
		String splits[]=noteBookRowkey.split(Constants.rowkey_split);
		try {
			
			//调用服务接口
			boolean isSuccessful=noteService.updateNote(noteBookRowkey,noteRowKey,oldNoteName
					,noteName,splits[1],"0",content);
			ModelMap map = new ModelMap();
			map.put("success", isSuccessful);
			//map.put("newNoteName", noteBookRowkey);
			//map.put("oldNoteName", oldNoteName);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName=(String) request.getSession().getAttribute(Constants.USER_INFO);
			logger.error("用户" + userName+ "修改笔记异常|方法:updateNote|参数： nbRowkey:"
		+ noteBookRowkey+" oldNoteName: "+oldNoteName, e);
			e.printStackTrace();
		}
		//modelAndView.setViewName("updateNote");
		logger.debug("用户"
				+ "修改笔记|方法:updateNote|参数： oldNoteName:" + oldNoteName+"   content: "+content);
		return modelAndView;
	}
	

	/**
	 * 获取笔记详情
	 * 
	 * @param request
	 * @param noteRowkey
	 * @return
	 */
	@RequestMapping("/getNote")
	public ModelAndView getNote(HttpServletRequest request, String noteRowkey) {
		ModelAndView modelAndView = null;
		Note note=new Note();
		try {
			// 查询笔记详情
			 note= noteService.getNoteByRowkey(noteRowkey);
			ModelMap map = new ModelMap();
			if (note != null) {
				map.put("note", note);
				map.put("success", true);
			} else {
				map.put("success", false);
			}
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			logger.error("用户" + userName + "获取笔记内容异常|方法：getNote|参数:noteRowkey:"
					+ noteRowkey, e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "获取笔记详情|方法:getNote|参数： noteRowkey:" + noteRowkey+"   返回note: "+note);
		return modelAndView;
	}

	/**
	 * 删除笔记-------其实是为了删除回收站中的笔记而开发的，普通文件夹中的笔记删除会调用moveAndDeleteNote方法删除至回收站
	 * 
	 * @param request
	 * @param oldNoteName
	 * @param noteRowKey
	 * @param noteBookRowkey
	 * @return
	 */
	@RequestMapping(value = "/deleteNote")
	public ModelAndView deleteNote(HttpServletRequest request,
			String oldNoteName, String noteRowKey, String noteBookRowkey) {
		ModelAndView modelAndView = null;
		try {
			boolean isSucc=noteService.deleteNote(noteBookRowkey, noteRowKey, oldNoteName);
			ModelMap map = new ModelMap();
			map.put("success", isSucc);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			logger.error("用户" + userName
					+ "删除笔记异常|方法:deleteNote|参数：oldNoteName:" + oldNoteName
					+ ";noteRowKey:" + noteRowKey + ";noteBookRowkey:"
					+ noteBookRowkey, e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "删除回收站中的笔记|方法:deleteNote|参数： noteRowKey:" + noteRowKey+"   笔记名称: "+oldNoteName);
		return modelAndView;
	}
	
	/**
	 * 移动并删除笔记,适用于删除到垃圾箱、笔记迁移
	 * 
	 * @param request
	 * @param noteName
	 * @param oldNoteName
	 * @param noteRowKey
	 * @param content
	 * @param noteBookRowkey
	 * @return
	 */
	@RequestMapping(value = "/moveAndDeleteNote")
	public ModelAndView moveAndDeleteNote(HttpServletRequest request,
			String noteRowKey, String oldNoteBookRowkey,
			String newNoteBookRowkey,String noteName) {
		ModelAndView modelAndView = null;
		try {
			//移动笔记
			boolean isSucc = noteService.moveAndDeleteNote(noteRowKey,
					oldNoteBookRowkey, newNoteBookRowkey,noteName);
			ModelMap map = new ModelMap();
			map.put("success", isSucc);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			logger.error("用户" + userName
					+ "移动并删除笔记异常|方法：moveAndDeleteNote|参数：noteRowKey:"
					+ noteRowKey + ";oldNoteBookRowkey:" + oldNoteBookRowkey
					+ ";newNoteBookRowkey:" + newNoteBookRowkey, e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "迁移笔记|方法:moveAndDeleteNote|参数： oldNoteBookRowkey:" + oldNoteBookRowkey+" newNotebookRowkey: "+newNoteBookRowkey);
		return modelAndView;
	}
	

	
	/**
	 * 收藏笔记
	 * 
	 * @param request
	 * @param noteRowKey
	 * @return
	 */
	@RequestMapping(value = "/starOtherNote")
	public ModelAndView starOtherNote(HttpServletRequest request,
			String noteRowKey) {
		String userName = (String) request.getSession().getAttribute(
				Constants.USER_INFO);
		ModelAndView modelAndView = null;
		try {
			
			String starNBRowKey = userName + Constants.STAR;
			boolean moveNote = noteService.starOtherNote(noteRowKey,
					starNBRowKey);
			ModelMap map = new ModelMap();
			map.put("success", moveNote);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "收藏笔记异常|方法：starOtherNote|参数：noteRowKey:" + noteRowKey, e);
			e.printStackTrace();
		}
		logger.debug("用户"
				+ "收藏笔记|方法:starOtherNote|参数： noteRowkey:" + noteRowKey);
		return modelAndView;
	}

	/**
	 * 显示活动列表页面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/showActivity")
	public String showActivity(HttpServletRequest request) throws Exception {
		return "active/activity";
	}

	/**
	 * 打开活动细节页面
	 * 
	 * @param request
	 * @param rowKey
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/openDetail")
	public String openDetail(HttpServletRequest request, String rowKey)
			throws Exception {
		request.setAttribute("rowKey", rowKey);
		return "active/activity_detail";
	}
	/**
	 * 活动笔记
	 * 
	 * @param request
	 * @param noteName
	 * @param oldNoteName
	 * @param noteRowKey
	 * @param content
	 * @param noteBookRowkey
	 * @return
	 */
	@RequestMapping(value = "/activeMyNote")
	public ModelAndView activeMyNote(HttpServletRequest request,
			String noteRowKey, String oldNoteBookRowkey,
			String newNoteBookRowkey) {
		ModelAndView modelAndView = null;
		
		try {
			
			// String activityBtRowKey = userName+Constants.ACTIVITY;
			boolean isSucc = noteService.note2Activity(noteRowKey,
					newNoteBookRowkey);
			ModelMap map = new ModelMap();
			map.put("success", isSucc);
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			String userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			logger.error("用户" + userName
					+ "参与活动笔记失败|方法：activeMyNote|参数：noteRowKey:" + noteRowKey
					+ ";参加活动的原笔记本rowKey:" + oldNoteBookRowkey
					+ ";活动笔记本rowKey:" + newNoteBookRowkey, e);
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	
	
/*	
	*//********************************************以下功能禁用***************************************************************//*
	
	*//**
	 * 分享笔记
	 * 
	 * @param rowKey
	 *            ：rowKey
	 * @param page
	 * @return
	 *//*
	@RequestMapping("/shareNote")
	public ModelAndView shareNote(HttpServletRequest request,
			HttpServletResponse response, String rowKey) {
		ModelMap map = new ModelMap();
		try {
			boolean shareNote = noteService.shareNote(rowKey);// 将笔记名字和内容创建索引，不存储，其他信息存储不索引
			if (shareNote) {
				map.put("success", true);
			} else {
				map.put("success", false);
			}
		} catch (Exception e) {
			logger.error("分享笔记异常:TechnologyController  &&  rowKey:" + rowKey, e);
			e.printStackTrace();
		}
		return new ModelAndView(new MappingJacksonJsonView(), map);
	}

	@RequestMapping("/search")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String key, Integer page) {
		ModelMap map = new ModelMap();
		List<Article> articles = new ArrayList<Article>();
		if (page == null || page.equals("")) {
			page = 1;
		}
		try {
			boolean tecFlag = false;
			articles = noteService.search(key, page);
			JSONArray wes = JSONArray.fromObject(articles);
			request.getSession().setAttribute("technologys", articles);
			request.getSession().setAttribute("tecFlag", tecFlag);
			map.put("wes", wes.toString());
			map.put("page", page);
			map.put("key", key);

		} catch (Exception e) {
			logger.error("从lucene中查询笔记异常:TechnologyController  &&  key:" + key,
					e);
			e.printStackTrace();

		}
		return new ModelAndView("question/questions_result", map);
	}

	*//**
	 * 从lucene中分页查询更多笔记
	 * 
	 * @param key
	 *            :输入的关键字
	 * @param page
	 *            :页码
	 * @return
	 *//*
	@RequestMapping("/searchMore")
	public ModelAndView searchMore(HttpServletRequest request,
			HttpServletResponse response, String key, Integer page) {
		ModelMap map = new ModelMap();
		List<Article> articles = new ArrayList<Article>();// 封装笔记信息
		try {
			articles = noteService.search(key, page);// 从lucene中查询笔记信息
			JSONArray wes = JSONArray.fromObject(articles);// 将笔记信息list转为json
			map.put("urls", wes.toString());
			map.put("page", page + 1);
			map.put("key", key);
		} catch (Exception e) {
			logger.error("从lucene中查询更多笔记异常:TechnologyController  &&  key:"
					+ key + "；page" + page, e);
			e.printStackTrace();
		}
		return new ModelAndView(new MappingJacksonJsonView(), map);
	}*/
	 
}
