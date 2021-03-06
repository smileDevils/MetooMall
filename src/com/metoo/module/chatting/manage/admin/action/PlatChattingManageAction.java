package com.metoo.module.chatting.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.chatting.domain.Chatting;
import com.metoo.module.chatting.domain.ChattingConfig;
import com.metoo.module.chatting.domain.ChattingLog;
import com.metoo.module.chatting.domain.query.ChattingLogQueryObject;
import com.metoo.module.chatting.service.IChattingConfigService;
import com.metoo.module.chatting.service.IChattingLogService;
import com.metoo.module.chatting.service.IChattingService;
import com.metoo.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: ChattingViewAction.java
 * </p>
 * 
 * <p>
 * Description: ??????????????????,?????????????????????????????????????????????????????????
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ????????????????????????????????? www.koala.com
 * </p>
 * 
 * @author hezeng
 * 
 * @date 2014???5???22???
 * 
 * @version koala_b2b2c 2.0
 */
@Controller
public class PlatChattingManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IChattingService chattingService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private IChattingConfigService chattingconfigService;
	@Autowired
	private IAccessoryService accessoryService;

	/*
	 * ???????????????????????????????????????
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting.htm")
	public ModelAndView plat_chatting(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("chatting/plat_chatting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		session.setAttribute("chatting_session", "chatting_session");
		ChattingConfig config = new ChattingConfig();
		Map params = new HashMap();
		params.put("config_type", 1);// ???????????????
		params.put("chatting_display", 0);// ??????
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.config_type=:config_type  and obj.chatting_display=:chatting_display and obj.logs.size>0 order by addTime desc",
						params, -1, -1);
		// ??????????????????????????????
		params.clear();
		params.put("config_type", 1);// ???????????????
		List<ChattingConfig> config_list = this.chattingconfigService
				.query("select obj from ChattingConfig obj where obj.config_type=:config_type ",
						params, 0, 1);
		if (config_list.size() == 0) {
			config.setAddTime(new Date());
			config.setConfig_type(1);
			config.setKf_name("??????????????????");
			this.chattingconfigService.save(config);
		} else {
			config = config_list.get(0);
		}
		params.clear();
		params.put("config_type", 1);// ???????????????
		params.put("plat_read", 0);
		List<ChattingLog> logs = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.config.config_type=:config_type and obj.plat_read=:plat_read order by addTime asc",
						params, -1, -1);// ????????????????????????
		mv.addObject("logs", logs);
		mv.addObject("chattingConfig", config);
		mv.addObject("chattings", chattings);// ?????????????????????,

		return mv;
	}

	/*
	 * ???????????????????????????????????????
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_open.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting_open.htm")
	public ModelAndView plat_chatting_open(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("chatting/plat_chatting_open.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		Map params = new HashMap();
		params.put("chatting_id", chatting.getId());
		params.put("plat_read", 0);// ????????????????????????
		List<ChattingLog> logs = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.plat_read=:plat_read order by addTime asc",
						params, -1, -1);
		for (ChattingLog cl : logs) {// ??????????????????????????????????????????????????????????????????
			cl.setPlat_read(1);// ???????????????????????????
			this.chattinglogService.update(cl);
		}
		if (chatting.getGoods_id() != null) {
			Long gid = chatting.getGoods_id();
			Goods goods = this.goodsService.getObjById(gid);
			mv.addObject("goods", goods);
		}
		mv.addObject("chatting", chatting);
		mv.addObject("objs", logs);
		return mv;
	}

	/*
	 * ???????????????????????????????????????
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_close.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting_close.htm")
	public void plat_chatting_close(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		boolean ret = true;
		if (ret) {
			Chatting chatting = this.chattingService.getObjById(CommUtil
					.null2Long(chatting_id));
			chatting.setChatting_display(-1);
			ret = this.chattingService.update(chatting);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ???????????????????????????????????? type;// ???????????????0?????????????????????????????????1???????????????????????? mark;// ?????????????????????0????????????1????????????
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_refresh.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting_refresh.htm")
	public ModelAndView plat_chatting_refresh(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("chatting/plat_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		if (chatting != null) {
			Map params = new HashMap();
			params.put("chatting_id", CommUtil.null2Long(chatting_id));
			params.put("plat_read", 0);
			List<ChattingLog> logs = this.chattinglogService
					.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.plat_read=:plat_read order by addTime asc",
							params, -1, -1);
			for (ChattingLog cl : logs) {
				cl.setPlat_read(1);// ?????????????????????
				this.chattinglogService.update(cl);
			}
			mv.addObject("objs", logs);
			mv.addObject("chatting", chatting);
			HttpSession session = request.getSession(false);
			String chatting_session = CommUtil.null2String(session
					.getAttribute("chatting_session"));
			if (session != null && !session.equals("")) {
				mv.addObject("chatting_session", chatting_session);
			}

		}
		return mv;
	}

	/**
	 * ???????????????????????????????????? type;// ???????????????0???????????????????????????1???????????????????????? mark;// ?????????????????????0????????????1????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/plat_refresh_users.htm")
	public ModelAndView plat_refresh_users(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("chatting/chatting_users.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("config_type", 1);// ???????????????
		params.put("chatting_display", 0);// ??????
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.config.config_type=:config_type  and obj.chatting_display=:chatting_display and obj.logs.size>0 order by addTime desc",
						params, -1, -1);
		params.clear();
		params.put("config_type", 1);// ???????????????
		params.put("plat_read", 0);
		List<ChattingLog> logs = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.config.config_type=:config_type and obj.plat_read=:plat_read order by addTime asc",
						params, -1, -1);// ????????????????????????
		mv.addObject("logs", logs);
		mv.addObject("chattings", chattings);// ?????????????????????,
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_save.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting_save.htm")
	public ModelAndView plat_chatting_save(HttpServletRequest request,
			HttpServletResponse response, String text, String chatting_id,
			String font, String font_size, String font_colour) {
		ModelAndView mv = new JModelAndView("chatting/plat_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		ChattingLog log = new ChattingLog();
		log.setAddTime(new Date());
		log.setChatting(chatting);
		log.setContent(text);
		// ??????????????????????????????
		log.setFont(font);
		log.setFont_size(font_size);
		log.setFont_colour(font_colour);
		// ????????????????????????????????????
		if (!font.equals(chatting.getConfig().getFont()) && !font.equals("")) {
			chatting.getConfig().setFont(font);
		}
		if (!font_size.equals(chatting.getConfig().getFont_size())
				&& !font_size.equals("")) {
			chatting.getConfig().setFont_size(font_size);
		}
		if (!font_colour.equals(chatting.getConfig().getFont_colour())
				&& !font_colour.equals("")) {
			chatting.getConfig().setFont_colour(font_colour);
		}
		this.chattingconfigService.update(chatting.getConfig());
		log.setPlat_read(1);// ??????????????????????????????????????????
		this.chattinglogService.save(log);
		List<ChattingLog> logs = new ArrayList<ChattingLog>();
		logs.add(log);
		mv.addObject("objs", logs);// ????????????????????????
		// ????????????sessin
		HttpSession session = request.getSession(false);
		session.removeAttribute("chatting_session");
		session.setAttribute("chatting_session", "chatting_session");
		String chatting_session = CommUtil.null2String(session
				.getAttribute("chatting_session"));
		if (session != null && !session.equals("")) {
			mv.addObject("chatting_session", chatting_session);
		}
		return mv;
	}

	/**
	 * ??????????????????
	 */

	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_set.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_chatting_set.htm")
	public void plat_chatting_set(HttpServletRequest request,
			HttpServletResponse response, String chattingConfig_id,
			String kf_name, String content, String reply_open) {
		ChattingConfig config = this.chattingconfigService.getObjById(CommUtil
				.null2Long(chattingConfig_id));
		if (kf_name != null && !kf_name.equals("")) {
			config.setKf_name(kf_name);
		}
		if (content != null && !content.equals("")) {
			config.setQuick_reply_content(content);
		}
		if (reply_open != null && !reply_open.equals("")) {
			config.setQuick_reply_open(CommUtil.null2Int(reply_open));
			if (reply_open.equals("1")
					&& config.getQuick_reply_content() == null) {
				config.setQuick_reply_content("????????????????????????????????????");
			}
		}
		this.chattingconfigService.update(config);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(config.getQuick_reply_open());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/plat_img_upload.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_img_upload.htm")
	public void plat_img_upload(HttpServletRequest request,
			HttpServletResponse response, String cid)
			throws FileUploadException {
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "chatting";
		Map map = new HashMap();
		Map json_map = new HashMap();
		Accessory photo = null;
		try {
			map = CommUtil.saveFileToServer(request, "image", "", "", null);
			String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png|.tbi|.TBI)$";
			String imgp = (String) map.get("fileName");
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(imgp.toLowerCase());
			if (matcher.find()) {
				map = CommUtil.saveFileToServer(request, "image",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/chatting");
					photo.setWidth((Integer) map.get("width"));
					photo.setHeight((Integer) map.get("height"));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					String src = CommUtil.getURL(request) + "/"
							+ photo.getPath() + "/" + photo.getName();
					String img = "<img id='waiting_img' src='"
							+ src
							+ "' onclick='show_image(this)' style='max-height:50px;cursor:pointer'/>";
					Chatting chatting = this.chattingService
							.getObjById(CommUtil.null2Long(cid));
					ChattingLog log = new ChattingLog();
					log.setAddTime(new Date());
					log.setChatting(chatting);
					log.setContent(img);
					log.setPlat_read(1);// ??????????????????????????????????????????
					this.chattinglogService.save(log);
					json_map.put("src", src);
					json_map.put("code", "success");
				}
			} else {
				json_map.put("code", "error");
			}
			String json = Json.toJson(json_map, JsonFormat.compact());
			try {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				writer = response.getWriter();
				writer.print(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/plat_show_history.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("/admin/plat_show_history.htm")
	public ModelAndView plat_show_history(HttpServletRequest request,
			HttpServletResponse response, String chatting_id, String currentPage) {
		ModelAndView mv = new JModelAndView("chatting/history_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		ChattingLogQueryObject qo = new ChattingLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.chatting.id",
				new SysMap("chatting_id", chatting.getId()), "=");
		qo.setPageSize(20);
		IPageList pList = this.chattinglogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/admin/plat_show_history.htm", "", "", pList, mv);
		mv.addObject("chatting_id", chatting.getId());
		return mv;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/plat_chatting_ajax_refresh.htm*", rtype = "admin", rname = "????????????", rcode = "self_chatting", rgroup = "??????")
	@RequestMapping("admin/plat_chatting_ajax_refresh.htm")
	public void plat_chatting_ajax_refresh(HttpServletRequest request,
			HttpServletResponse response) {
		Map params = new HashMap();
		int size = 0;
		params.clear();
		params.put("plat_read", 0);// user_read:???????????????????????????
		List logs = this.chattinglogService
				.query("select obj.id from ChattingLog obj where obj.plat_read=:plat_read order by addTime asc",
						params, -1, -1);
		size = logs.size();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
