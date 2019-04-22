package com.leo.controller.note;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.leo.domain.note.Notebook;
import com.leo.service.note.NoteService;
import com.leo.util.Constants;



@RequestMapping("/note")
public class NoteController {
	
	private static Logger logger = LoggerFactory
			.getLogger(NoteController.class);
	
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
		ModelAndView modelAndView = null;
		String userName = null;
		try {
			// 从session中获取用户名
			userName = (String) request.getSession().getAttribute(
					Constants.USER_INFO);
			// 查询用户笔记本
			List<Notebook> allNotebooks = noteService.getAllNotebooks(userName);// 查询所有笔记本
			// 封装返回值
			ModelMap map = new ModelMap();
			map.put("allNoteBook", allNotebooks);
			map.put("recycleNBRowKey", userName + Constants.RECYCLE);
			map.put("starNBRowKey", userName + Constants.STAR);
			map.put("activityNBRowKey", userName + Constants.ACTIVITY);
			
			//作用：将多个对象通过map返回，并以json形式返回，所以要利用MappingJacksonJsonView类来构建ModelAndView对象
			modelAndView = new ModelAndView(new MappingJacksonJsonView(), map);
		} catch (Exception e) {
			logger.error("用户" + userName
					+ "获取所有笔记本异常|方法:getAllNoteBook|参数： userName:" + userName, e);
			e.printStackTrace();
		}
		return modelAndView;
	}

	/**
	 * 正常获取指定用户的所有笔记本（与第一个的区别在于这里需要正式的传参username）
	 * 
	 * @param request
	 * @return
	 */
	
	@RequestMapping("/getAllNotebooksByUser")
	public ModelAndView getAllNoteBookByUserName(HttpServletRequest request,
			String userName) {
		ModelAndView modelAndView = null;
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

}
