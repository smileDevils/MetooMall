package com.metoo.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.Authentication;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;


import com.metoo.core.annotation.Log;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.constant.Globals;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.qrcode.QRCodeUtil;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.LogType;
import com.metoo.foundation.domain.StoreStat;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.SystemTip;
import com.metoo.foundation.domain.Template;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.EmailModelQueryObject;
import com.metoo.foundation.domain.query.SystemTipQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IEmailModelService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.IStoreStatService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ISystemTipService;
import com.metoo.foundation.service.ITemplateService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.StatTools;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.msg.MsgTools;
import com.metoo.msg.email.SpelTemplate;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ????????????????????????????????? www.metoo.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-9
 * 
 * @version metoo_b2b2c v2.0 2015???
 */
@Controller
public class BaseManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private StatTools statTools;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IStoreStatService storestatService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IEmailModelService emailModelService;

	/**
	 * ????????????????????????????????????????????????UserRole????????????,??????????????????????????????
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Log(title = "????????????", type = LogType.LOGIN)
	@RequestMapping("/login_success.json")
	public void login_success(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (SecurityUserHolder.getCurrentUser() != null) {

			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			//
			if (this.configService.getSysConfig().isIntegral()) {
				if (user.getLoginDate() == null
						|| user.getLoginDate().before(CommUtil.formatDate(CommUtil.formatShortDate(new Date())))) {
					user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getMemberDayLogin());
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("??????" + CommUtil.formatLongDate(new Date()) + "????????????"
							+ this.configService.getSysConfig().getMemberDayLogin() + "???");
					log.setIntegral(this.configService.getSysConfig().getMemberDayLogin());
					log.setIntegral_user(user);
					log.setType("login");
					this.integralLogService.save(log);
				}
			}
			//
			user.setLoginDate(new Date());
			user.setLoginIp(CommUtil.getIpAddr(request));
			user.setLoginCount(user.getLoginCount() + 1);
			this.userService.update(user);

			HttpSession session = request.getSession(false);// ??????session?????????
															// ????????????????????????????????????????????????NULL

			String login_type = (String) session.getAttribute("type");
			session.setAttribute("user", user);
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("lastLoginDate", new Date());// ??????????????????
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// ????????????IP
			session.setAttribute("login", true);// ??????????????????(??????????????????)

			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success.htm";

			if (!CommUtil.null2String(request.getSession(false).getAttribute("refererUrl")).equals("")) {
				url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

				// ????????????
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
					// if (true) {
					// ???????????????????????????token????????????
					Map usermap = new HashMap();
					User userinfo = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					usermap.put("user_truename", userinfo.getTrueName());
					usermap.put("user_name", userinfo.getUserName());
					usermap.put("user_nickname", userinfo.getNickName());
					usermap.put("user_password", userinfo.getPassword());
					if (userinfo.getSex() == -1) {
						usermap.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/"
								+ "resources" + "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member.png");
					}
					if (userinfo.getSex() == 0) {
						usermap.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/"
								+ "resources" + "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member0.png");
					}
					if (userinfo.getSex() == 1) {
						usermap.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/"
								+ "resources" + "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member1.png");
					}
					usermap.put("user_sex", userinfo.getSex());
					usermap.put("user_id", userinfo.getId());
					usermap.put("user_token", userinfo.getApp_login_token());
					Result result = new Result(4200, "success", usermap);
					response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
				} else {
					if (login_role.equalsIgnoreCase("admin")) {
						if (role.indexOf("ADMIN") >= 0) {
							url = CommUtil.getURL(request) + "/admin/index.htm";
							request.getSession(false).setAttribute("admin_login", true);
						}
					}
					if (login_role.equalsIgnoreCase("seller") && role.indexOf("SELLER") >= 0) {
						url = CommUtil.getURL(request) + "/seller/index.htm";
						request.getSession(false).setAttribute("seller_login", true);
					}
					if (!CommUtil.null2String(request.getSession(false).getAttribute("refererUrl")).equals("")) {
						url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
						request.getSession(false).removeAttribute("refererUrl");
					}

					// ????????????
					if ("weixin".equalsIgnoreCase(login_type)) {
						if (!"".equals(session.getAttribute("returnUrl"))
								&& session.getAttribute("returnUrl") != null) {

							url = CommUtil.getURL(request) + session.getAttribute("returnUrl");
						} else {
							url = CommUtil.getURL(request) + "/wap/index.htm";
						}
					}
					response.sendRedirect(url);
				}
			}

		} else {
			String url = CommUtil.getURL(request) + "/index.htm";
			response.sendRedirect(url);
		}

	}

	/**
	 * ????????????????????????URL??????
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/logout_success.htm")
	public void logout_success(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		boolean admin_login = CommUtil.null2Boolean(session.getAttribute("admin_login"));
		boolean seller_login = CommUtil.null2Boolean(session.getAttribute("seller_login"));
		/* String targetUrl = CommUtil.getURL(request) + "/user/login.htm"; */
		// [?????????pc ??????????????????????????????????????????????????????????????????]
		String targetUrl = CommUtil.getURL(request) + "/seller/login.htm";
		/*
		 * if (admin_login) { targetUrl = CommUtil.getURL(request) +
		 * "/index.htm"; } if (seller_login) { targetUrl =
		 * CommUtil.getURL(request) + "/index.htm"; }
		 */
		if (admin_login) {
			targetUrl = CommUtil.getURL(request) + "/admin/login.htm";
		}
		if (seller_login) {
			targetUrl = CommUtil.getURL(request) + "/seller/login.htm";
		}
		//
		String userName = CommUtil.null2String(session.getAttribute("userName"));
		// System.out.println(userName);
		Object[] objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.length; i++) {
			if (CommUtil.null2String(objs[i]).equals(userName)) {
				SessionInformation[] ilist = this.sessionRegistry.getAllSessions(objs[i], true);
				for (int j = 0; j < ilist.length; j++) {
					SessionInformation sif = ilist[j];
					// ??????????????????
					sif.expireNow();
					this.sessionRegistry.removeSessionInformation(sif.getSessionId());
				}
			}
		}
		//
		session.removeAttribute("admin_login");
		session.removeAttribute("seller_login");
		session.removeAttribute("user");
		session.removeAttribute("userName");
		session.removeAttribute("login");
		session.removeAttribute("role");
		session.removeAttribute("cart");
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession(false)
				.removeAttribute("user");
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
			targetUrl = CommUtil.getURL(request) + "/wap/index.htm";

		}
		response.sendRedirect(targetUrl);
	}

	/**
	 * ??????????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_error.json")
	public ModelAndView login_error(HttpServletRequest request, HttpServletResponse response) {
		String login_role = (String) request.getSession(false).getAttribute("login_role");
		ModelAndView mv = null;
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
			/*
			 * String targetUrl = CommUtil.getURL(request) + "/wap/index.htm";
			 * try { response.sendRedirect(targetUrl); } catch (IOException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); }
			 */
			Result result = new Result(1, "error");
			String temp = Json.toJson(result, JsonFormat.compact());
			try {
				response.getWriter().print(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String metoo_view_type = CommUtil.null2String(request.getSession(false).getAttribute("metoo_view_type"));
		if (metoo_view_type != null && !metoo_view_type.equals("")) {
			if (metoo_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id);
			}
		} else {
			if (login_role == null)
				login_role = "user";
			if (login_role.equalsIgnoreCase("admin")) {
				mv = new JModelAndView("admin/blue/login_error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
			}
			if (login_role.equalsIgnoreCase("seller")) {
				mv = new JModelAndView("error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("url", CommUtil.getURL(request) + "/seller/login.htm");
			}
			if (login_role.equalsIgnoreCase("user")) {
				mv = new JModelAndView("error.html", configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("url", CommUtil.getURL(request) + "/seller/login.htm");
			}
		}
		mv.addObject("op_title", "Login??Failure");
		return mv;
	}

	/**
	 * ????????????
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/index.htm*", rtype = "admin", rname = "??????????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/index.htm")
	public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/manage.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/admin/welcome.htm*", rtype = "admin", rname = "????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/welcome.htm")
	public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/welcome.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Properties props = System.getProperties();
		mv.addObject("os", props.getProperty("os.name"));
		mv.addObject("java_version", props.getProperty("java.version"));
		mv.addObject("shop_version", Globals.DEFAULT_SHOP_VERSION);
		mv.addObject("database_version", this.databaseTools.queryDatabaseVersion());
		mv.addObject("web_server_version", request.getSession(false).getServletContext().getServerInfo());

		List<StoreStat> states = this.storeStatService.query("select obj from StoreStat obj order by obj.addTime desc",
				null, -1, -1);
		Map params = new HashMap();
		params.put("st_status", 0);
		List<SystemTip> sts = this.systemTipService.query(
				"select obj from SystemTip obj where obj.st_status=:st_status order by obj.st_level desc", params, -1,
				-1);
		StoreStat stat2 = null;
		if (states.size() > 0) {
			stat2 = states.get(0);
		} else {
			stat2 = new StoreStat();
		}
		mv.addObject("stat", stat2);
		mv.addObject("sts", sts);
		return mv;
	}

	@SecurityMapping(title = "???????????????", value = "/admin/sys_tip_list.htm*", rtype = "admin", rname = "???????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/sys_tip_list.htm")
	public ModelAndView sys_tip_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/sys_tip_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SystemTipQueryObject qo = new SystemTipQueryObject(currentPage, mv, orderBy, orderType);
		qo.setOrderBy("st_status asc,obj.st_level desc,obj.addTime");
		qo.setOrderType("desc");
		IPageList pList = this.systemTipService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/sys_tip_del.htm*", rtype = "admin", rname = "???????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/sys_tip_del.htm")
	public String sys_tip_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil.null2Long(id));
				this.systemTipService.delete(Long.parseLong(id));
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/sys_tip_do.htm*", rtype = "admin", rname = "???????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/sys_tip_do.htm")
	public String sys_tip_do(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil.null2Long(id));
				st.setSt_status(1);
				this.systemTipService.save(st);
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "????????????", value = "/admin/aboutus.htm*", rtype = "admin", rname = "????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/aboutus.htm")
	public ModelAndView aboutus(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/aboutus.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/admin/set_site.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_site", rgroup = "??????")
	@RequestMapping("/admin/set_site.htm")
	public ModelAndView site_set(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_site_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/admin/set_image.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_image", rgroup = "??????")
	@RequestMapping("/admin/set_image.htm")
	public ModelAndView set_image(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_image_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/sys_config_save.htm*", rtype = "admin", display = false, rname = "??????????????????", rcode = "admin_config_save", rgroup = "??????")
	@RequestMapping("/admin/sys_config_save.htm")
	public ModelAndView sys_config_save(HttpServletRequest request, HttpServletResponse response, String id,
			String list_url, String op_title, String app_download, String android_download, String ios_download,
			String android_seller_download, String ios_seller_download, String app_seller_download) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);// [{codeStat=},
															// {id=1},
															// {poweredby=welcome-1},
															// {img0_text=},
															// {op_title=??????????????????},
															// {title=Ebuyair.com},
															// {company_name=Ebuyair-1},
															// {img2_text=},
															// {sina_domain_code=},
															// {android_version=},
															// {sina_login_id=},
															// {android_download=},
															// {share_code=},
															// {sina_login=false},
															// {securityCodeRegister_ck=on},
															// {qq_login_id=},
															// {securityCodeType=normal},
															// {id=1},
															// {poweredby=welcome-1},
															// {img0_text=},
															// {op_title=??????????????????},
															// {title=Ebuyair.com},
															// {company_name=Ebuyair-1},
															// {img2_text=},
															// {sina_domain_code=}]
		}
		if (sysConfig.getAddress() != null && !sysConfig.getAddress().equals("")) {
			// [http://ae.metoo-souq.com/]
			String address = sysConfig.getAddress();
			if (address.indexOf("http://") < 0) {
				address = "http://" + address;
				sysConfig.setAddress(address);
			}
		}
		// ??????????????????logo
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		/*
		 * String saveFilePathName = request.getSession().getServletContext()
		 * .getRealPath("/") + uploadFilePath + File.separator + "system";
		 */
		// String saveFilePathName = uploadFilePath + File.separator + "system";
		// String saveFilePathName = "upload/system";
		String saveFilePathName = uploadFilePath + "/" + "system";
		// CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig().getWebsiteLogo() == null ? ""
					: this.configService.getSysConfig().getWebsiteLogo().getName();
			map = CommUtil.httpsaveFileToServer(request, "websiteLogo", saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setWebsiteLogo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getWebsiteLogo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ??????????????????
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "goodsImage", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getGoodsImage() == null ? ""
					: this.configService.getSysConfig().getGoodsImage().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setGoodsImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getGoodsImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ??????????????????
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "storeImage", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getStoreImage() == null ? ""
					: this.configService.getSysConfig().getStoreImage().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setStoreImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getStoreImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ??????????????????
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "memberIcon", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getMemberIcon() == null ? ""
					: this.configService.getSysConfig().getMemberIcon().getName();
			if (fileName != null && fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setMemberIcon(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getMemberIcon();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ????????????Logo
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "admin_login_img", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getAdmin_login_logo() == null ? ""
					: this.configService.getSysConfig().getAdmin_login_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_login_logo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getAdmin_login_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ????????????Logo
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "admin_manage_img", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getAdmin_manage_logo() == null ? ""
					: this.configService.getSysConfig().getAdmin_manage_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_manage_logo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getAdmin_manage_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ???????????????????????????Logo??????
		map.clear();
		try {
			map = CommUtil.httpsaveFileToServer(request, "qrLogo", saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getQr_logo() == null ? ""
					: this.configService.getSysConfig().getQr_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName((String) map.get("fileName"));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setQr_logo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getQr_logo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ??????app
		sysConfig.setApp_download(CommUtil.null2Int(app_download));
		sysConfig.setAndroid_download(android_download);
		sysConfig.setIos_download(ios_download);
		if (CommUtil.null2Int(app_download) == 1) {// ??????app?????????????????????????????????
			String destPath = System.getProperty("metoob2b2c.root") + uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = request.getSession().getServletContext().getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo().getPath() + File.separator
						+ this.configService.getSysConfig().getQr_logo().getName();
			}
			String download_url = CommUtil.getURL(request) + "/app_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator + "app_dowload.png", true);
		}
		// ??????app
		sysConfig.setApp_seller_download(CommUtil.null2Int(app_seller_download));
		sysConfig.setAndroid_seller_download(android_seller_download);
		sysConfig.setIos_seller_download(ios_seller_download);
		if (CommUtil.null2Int(app_seller_download) == 1) {// ????????????app?????????????????????????????????
			String destPath = System.getProperty("metoob2b2c.root") + uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = request.getSession().getServletContext().getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo().getPath() + File.separator
						+ this.configService.getSysConfig().getQr_logo().getName();
			}
			String download_url = CommUtil.getURL(request) + "/app_seller_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator + "app_seller_download.png", true);
		}
		if (sysConfig.getHotSearch() != null && !sysConfig.getHotSearch().equals("")) {
			sysConfig.setHotSearch(sysConfig.getHotSearch().replaceAll("???", ","));// ?????????????????????
		}
		if (sysConfig.getKeywords() != null && !sysConfig.getKeywords().equals("")) {
			sysConfig.setKeywords(sysConfig.getKeywords().replaceAll("???", ","));
		}
		// ????????????????????????????????????,?????????????????????????????????????????????????????????
		String imageSuffix = sysConfig.getImageSuffix();
		String[] suffix_list = new String[] { "php", "asp", "jsp", "html", "htm", "cgi", "action", "js", "css" };
		for (String suffix : suffix_list) {
			imageSuffix = imageSuffix.replaceAll(suffix, "");
		}
		sysConfig.setImageSuffix(imageSuffix);
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		for (int i = 0; i < 4; i++) {
			try {
				map.clear();
				String fileName = "";
				if (sysConfig.getLogin_imgs().size() > i) {
					fileName = sysConfig.getLogin_imgs().get(i).getName();
				}
				map = CommUtil.httpsaveFileToServer(request, "img" + i, saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory img = new Accessory();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt((String) map.get("mime"));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth((Integer) map.get("width"));
						img.setHeight((Integer) map.get("height"));
						img.setAddTime(new Date());
						img.setConfig(sysConfig);
						this.accessoryService.save(img);
					}
				} else {
					if (map.get("fileName") != "") {
						Accessory img = sysConfig.getLogin_imgs().get(i);
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt(CommUtil.null2String(map.get("mime")));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth(CommUtil.null2Int(map.get("width")));
						img.setHeight(CommUtil.null2Int(map.get("height")));
						img.setConfig(sysConfig);
						this.accessoryService.update(img);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", op_title);// [????????????????????????????????????????????????site_setting.htm]
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "Email??????", value = "/admin/set_email.htm*", rtype = "admin", rname = "Email??????", rcode = "admin_set_email", rgroup = "??????")
	@RequestMapping("/admin/set_email.htm")
	public ModelAndView set_email(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_email_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/admin/set_sms.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_sms", rgroup = "??????")
	@RequestMapping("/admin/set_sms.htm")
	public ModelAndView set_sms(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_sms_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "SEO??????", value = "/admin/set_seo.htm*", rtype = "admin", rname = "SEO??????", rcode = "admin_set_seo", rgroup = "??????")
	@RequestMapping("/admin/set_seo.htm")
	public ModelAndView set_seo(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_seo_setting.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/admin/set_second_domain.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_second_domain", rgroup = "??????")
	@RequestMapping("/admin/set_second_domain.htm")
	public ModelAndView set_second_domain(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_second_domain.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "????????????????????????", value = "/admin/set_second_domain_save.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_second_domain", rgroup = "??????")
	@RequestMapping("/admin/set_second_domain_save.htm")
	public ModelAndView set_second_domain_save(HttpServletRequest request, HttpServletResponse response, String id,
			String domain_allow_count, String sys_domain, String second_domain_open) {
		String serverName = request.getServerName().toLowerCase();
		ModelAndView mv = new JModelAndView("admin/blue/success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// System.out.println("???????????????"+Globals.SSO_SIGN);
		if (Globals.SSO_SIGN) {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(CommUtil.null2Boolean(second_domain_open));
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv.addObject("op_title", "????????????????????????");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_second_domain.htm");
		} else {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(false);
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "????????????????????????????????????");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_second_domain.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "QQ????????????", value = "/admin/set_site_qq.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_second_domain", rgroup = "??????")
	@RequestMapping("/admin/set_site_qq.htm")
	public ModelAndView set_site_qq(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_second_domain.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * ?????????????????????????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/admin/logout.htm")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
					SecurityContextHolder.getContext().getAuthentication().getCredentials(),
					user.get_common_Authorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		return "redirect:../index.htm";
	}

	/**
	 * ????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/login.htm")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/login.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		request.getSession(false).removeAttribute("verify_code");
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping("/success.htm")
	public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/error.htm")
	public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);

		}
		mv.addObject("op_title", request.getSession(false).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(false).getAttribute("url"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/exception.htm")
	public ModelAndView exception(HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mv = new JModelAndView("error.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/exception.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
		} else {
			mv.addObject("op_title", "??????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/authority.htm")
	public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/authority.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
		if (domain_error) {
			mv = new JModelAndView("error.html", configService.getSysConfig(), this.userConfigService.getUserConfig(),
					1, request, response);
			mv.addObject("op_title", "???????????????????????????http://www.metoo.com??????");
		}
		return mv;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/voice.htm")
	public ModelAndView voice(HttpServletRequest request, HttpServletResponse response) {
		return new JModelAndView("include/flash/soundPlayer.swf", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}

	/**
	 * flash???????????????
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getCode.htm")
	public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code=" + (String) session.getAttribute("verify_code"));
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws IOException
	 */
	@SecurityMapping(title = "???????????????????????????", value = "/admin/restore_img.htm*", rtype = "admin", rname = "??????????????????", rcode = "admin_index", display = false, rgroup = "??????")
	@RequestMapping("/admin/restore_img.htm")
	public void restore_img(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
		SysConfig config = this.configService.getSysConfig();
		Map map = new HashMap();
		if (type.equals("member")) {// ??????????????????????????????
			Accessory acc = config.getMemberIcon();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getMemberIcon();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("member.jpg");
			config.setMemberIcon(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/member.jpg");
		}
		if (type.equals("goods")) {// ??????????????????????????????
			Accessory acc = config.getGoodsImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getGoodsImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("good.jpg");
			config.setGoodsImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/good.jpg");
		}
		if (type.equals("store")) {// ??????????????????????????????
			Accessory acc = config.getStoreImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getStoreImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("store.jpg");
			config.setStoreImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request) + "/resources/style/common/images/store.jpg");
		}
		if (type.equals("admin_login_img")) {// ????????????????????????????????????Logo
			Accessory acc = config.getAdmin_login_logo();
			config.setAdmin_login_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path",
					CommUtil.getURL(request) + "/resources/style/system/manage/blue/images/login/login_logo.png");
		}
		if (type.equals("admin_manage_img")) {// ????????????????????????????????????Logo
			Accessory acc = config.getAdmin_manage_logo();
			config.setAdmin_manage_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path", CommUtil.getURL(request) + "/resources/style/system/manage/blue/images/logo.png");
		}
		map.put("type", type);
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print(Json.toJson(map, JsonFormat.compact()));
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @throws ClassNotFoundException
	 */
	@RequestMapping("/upload.htm")
	public void upload(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException {
		/*
		 * String saveFilePathName1 = request.getSession().getServletContext()
		 * .getRealPath("/") +
		 * this.configService.getSysConfig().getUploadFilePath() +
		 * File.separator + "common";
		 */
		String saveFilePathName = this.configService.getSysConfig().getUploadFilePath() + "/" + "common";
		String webPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
		if (this.configService.getSysConfig().getAddress() != null
				&& !this.configService.getSysConfig().getAddress().equals("")) {
			webPath = this.configService.getSysConfig().getAddress() + webPath;
		}
		JSONObject obj = new JSONObject();
		try {
			Map map = CommUtil.httpsaveFileToServer(request, "imgFile", saveFilePathName, null, null);
			/*
			 * String url = webPath + "/" +
			 * this.configService.getSysConfig().getUploadFilePath() +
			 * "/common/" + map.get("fileName");
			 */
			String url = this.configService.getSysConfig().getImageWebServer() + "/" + saveFilePathName + "/"
					+ map.get("fileName");
			obj.put("error", 0);
			obj.put("url", url);
		} catch (IOException e) {
			obj.put("error", 1);
			obj.put("message", e.getMessage());
			e.printStackTrace();
		}
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/js.htm")
	public ModelAndView js(HttpServletRequest request, HttpServletResponse response, String js) {
		ModelAndView mv = new JModelAndView("resources/js/" + js + ".js", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}

	@RequestMapping("/admin/test_mail1.htm")
	public void test_email1(HttpServletResponse response, String email) {
		String subject = this.configService.getSysConfig().getTitle() + "????????????";
		boolean ret = this.msgTools.sendEmail(email, subject, subject);
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

	@RequestMapping("/admin/test_mail.htm")
	public void test_email(HttpServletResponse response, HttpServletRequest request, String email) {
		// String subject = this.configService.getSysConfig().getTitle() +
		// "????????????";
		Template template = this.templateService.getObjByProperty(null, "mark", "email_tobuyer_order_submit_ok_notify");
		String subject = template.getTitle();
		ExpressionParser exp = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext();// SpelExpressionParser???Spring?????????ExpressionParser????????????????????????
		context.setVariable("config", this.configService.getSysConfig());
		context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
		context.setVariable("webPath", CommUtil.getURL(request));
		context.setVariable("user", "hkk");
		Expression ex = exp.parseExpression(template.getContent(), // ?????????????????????????????????Expression?????????SpelExpression
				new TemplateParserContext());// SpelTemplate
												// TemplateParserContext
		String content = ex.getValue(context, String.class);
		boolean ret = this.msgTools.sendEmail(email, subject, content, template.getUser_name());
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

	@Test
	public void testSpelExpress() {
		// ??????SpringEL?????????
		String template = "#{#user}????????????";// ??????????????????,??????#{}???????????????????????????#user???????????????????????????????????????????????????
		ExpressionParser paser = new SpelExpressionParser();// ????????????????????????

		// ??????evaluationContext.setVariable????????????????????????????????????
		EvaluationContext context = new StandardEvaluationContext();
		context.setVariable("user", "??????");

		// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
		Expression expression = paser.parseExpression(template, new TemplateParserContext());

		// ??????Expression.getValue()???????????????????????????????????????Evalution????????????????????????????????????????????????????????????????????????
		System.out.println(expression.getValue(context, String.class));
	}

	@RequestMapping("/admin/email.htm")
	public void email(HttpServletResponse response, String email, String content) {
		String content1 = "";
		switch (content) {
		case "5":
			content1 = "????????????????????????";
			break;
		case "6":
			content1 = "??????????????????";
			break;
		case "10":
			content1 = "????????????????????????";
			break;
		case "11":
			content1 = "??????????????????";
			break;
		case "15":
			content1 = "????????????";
			break;
		case "20":
			content1 = "????????????";
			break;
		case "25":
			content1 = "????????????";
			break;
		case "26":
			content1 = "?????????????????????";
			break;
		}
		String subject1 = this.configService.getSysConfig().getTitle() + ":" + content1;
		boolean ret = this.msgTools.sendEmail(email, subject1, subject1);
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

	@RequestMapping("/admin/test_sms.htm")
	public void test_sms(HttpServletResponse response, String mobile) throws UnsupportedEncodingException {
		String content = this.configService.getSysConfig().getTitle() + " Test";
		boolean ret = false;
		try {
			ret = this.msgTools.sendSMS(mobile, content);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	 * ??????????????????????????????????????????blue
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "websiteCss??????", value = "/admin/set_websiteCss.htm*", rtype = "admin", rname = "????????????", rcode = "admin_set_site", rgroup = "??????")
	@RequestMapping("/admin/set_websiteCss.htm")
	public void set_websiteCss(HttpServletRequest request, HttpServletResponse response, String webcss) {
		SysConfig obj = this.configService.getSysConfig();
		if (!webcss.equals("blue") && !webcss.equals("black")) {
			webcss = "blue";
		}
		obj.setWebsiteCss(webcss);
		this.configService.update(obj);
	}

}
