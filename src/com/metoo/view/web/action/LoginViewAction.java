package com.metoo.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.constant.Globals;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.qrcode.QRCodeUtil;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.Document;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.Role;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.service.IAlbumService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IDocumentService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IRoleService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.domain.QRLogin;
import com.metoo.module.app.service.IQRLoginService;
import com.metoo.msg.MsgTools;
import com.metoo.view.web.tools.ImageViewTools;

/**
 * 
 * <p>
 * Title: LoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: ???????????????????????????????????????????????????????????????????????????????????????
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
 * @author erikzhang
 * 
 * @date 2014-5-13
 * 
 * @version koala_b2b2c v2.0 2015???
 */
@Controller
public class LoginViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IVerifyCodeService verifyCodeService;
	@Autowired
	private IQRLoginService qRLoginService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	private static final String REGEX1 = "(.*?????????.*)";
	private static final String REGEX2 = "(.*admin.*)";

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		
		ModelAndView mv = new JModelAndView("login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		request.getSession(false).removeAttribute("verify_code");
		
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));//?????????????????????true
		if (url != null && !url.equals("")) {
			request.getSession(false).setAttribute("refererUrl", url);
		}
		if (domain_error) {
			mv.addObject("op_title", "???????????????????????????http://www.koala.com??????");
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv.addObject("imageViewTools", 0.);
		}
		return mv;
	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty(null, "mark",
				"reg_agree");
		mv.addObject("doc", doc);
		mv.addObject("imageViewTools", imageViewTools);
		return mv;
	}
	


	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/company/register.htm")
	public ModelAndView company_register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("company_register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty(null, "mark",
				"reg_agree");
		mv.addObject("doc", doc);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * ????????????
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 * @throws InterruptedException
	 */
	@RequestMapping("/register_finish.htm")
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email, String code, String user_type) throws HttpException,
			IOException, InterruptedException {
		try {
			boolean reg = true;// ?????????????????????????????????????????????????????????????????????
			if (code != null && !code.equals("")) {
				code = CommUtil.filterHTML(code);// ???????????????
			}
			// System.out.println(this.configService.getSysConfig().isSecurityCodeRegister());
			if (this.configService.getSysConfig().isSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(code)) {
					reg = false;
				}
			}
			// ???????????????????????? ????????? admin ??????????????????
			if (userName.matches(REGEX1)
					|| userName.toLowerCase().matches(REGEX2)) {
				reg = false;
			}
			if (reg) {
				User user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setEmail(email);
				user.setAvailableBalance(BigDecimal.valueOf(0));
				user.setFreezeBlance(BigDecimal.valueOf(0));
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				user.setStore_apply_step(1);
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService
						.query("select new Role(id) from Role obj where obj.type=:type",
								params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) { //[??????]
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("??????????????????"
							+ this.configService.getSysConfig()
									.getMemberRegister() + "???");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.save(log);
				} else {
					this.userService.save(user);
				}
				// ????????????????????????
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("????????????");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				this.albumService.save(album);
				request.getSession(false).removeAttribute("verify_code");
				return "redirect:koala_login.json?username="
						+ CommUtil.encode(userName) + "&password=" + password
						+ "&encode=true";
			} else {
				return "redirect:register.htm";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:index.htm";
		}

	}
	
	

	/**
	 * springsecurity???????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_login_success.htm")
	public String user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		String url = CommUtil.getURL(request) + "/index.htm";
		HttpSession session = request.getSession(false);
		if (session.getAttribute("refererUrl") != null
				&& !session.getAttribute("refererUrl").equals("")) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		if (!bind.equals("")) {
			return "redirect:out_login_success.htm";
		}
		//[???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? seller_apply.htm]
		//String urls = CommUtil.getURL(request) + "/seller_apply.htm";
		return "redirect:" + url;
	}

	/**
	 * ??????????????????????????????????????????????????????QQ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ???????????????????????????QQ??????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/out_login_success.htm")
	public ModelAndView out_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		ModelAndView mv = new JModelAndView(bind + "_login_bind.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		mv.addObject("user", user);
		request.getSession(false).removeAttribute("bind");
		return mv;
	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_dialog_login.htm")
	public ModelAndView user_dialog_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user_dialog_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget1.htm")
	public ModelAndView forget1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("forget1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "????????????????????????????????????????????????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * code 0 ????????? 1????????? 2 ?????????????????????????????? 3:???????????????
	 * app????????????
	 */
	@RequestMapping("/app_forget.json")
	public void app_forget(HttpServletRequest request,
			HttpServletResponse response, String mobile, String mobile_verify_code, String userName, String password){
		Result result = null;
		Map pwdmap = new HashMap();
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			result = new Result(1,"????????????????????????????????????????????????????????????");
		} else {
			//??????????????????????????????
			VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
					"mobile", mobile);
			if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
					this.mobileverifycodeService.delete(mvc.getId());
					// ???????????????????????????????????????
					String content = "?????????"
							+ "Ebuyair?????????????????????" + CommUtil.formatLongDate(new Date())
							+ "?????????????????????["
							+ this.configService.getSysConfig().getTitle() + "]";
					try {
						this.msgTools.sendSMS(mobile, content);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Map params = new HashMap();
					params.put("userName", userName);
					//params.put("email", userName);
					params.put("mobile", mobile);
					List<User> users = this.userService
							.query("select obj from User obj where obj.userName =:userName and obj.mobile =:mobile",
									params, -1, -1);
					if (users.size() > 0) {
						User user = users.get(0);
						if (!CommUtil.null2String(user.getMobile()).equals("")) {
							user.setPassword(Md5Encrypt.md5(password).toLowerCase());
							this.userService.update(user);
							result = new Result(0,"success");
						} else {
							result = new Result(2,"???????????????????????????");
						}

					} else {
						result = new Result(3, "??????????????????,????????????????????????" );
					}
					
				}else{
					result = new Result(1, "???????????????");
				}
		}
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ?????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/forget2.htm")
	public ModelAndView forget2(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView("forget2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "????????????????????????????????????????????????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} else {
			Map params = new HashMap();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			List<User> users = this.userService
					.query("select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile =:mobile",
							params, -1, -1);
			if (users.size() > 0) {
				User user = users.get(0);
				if (!CommUtil.null2String(user.getEmail()).equals("")
						|| !CommUtil.null2String(user.getMobile()).equals("")) {
					mv.addObject("user", user);
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "????????????????????????????????????????????????");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget1.htm");
				}

			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "??????????????????");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping("/forget3.htm")
	public ModelAndView forget3(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new JModelAndView("forget3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (accept_type.equals("email")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null,
					"email", email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "?????????????????????");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "?????????????????????");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2.htm?userName=" + userName);
			}
		}
		if (accept_type.equals("mobile")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty(null,
					"mobile", mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "?????????????????????");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "?????????????????????");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2.htm?userName=" + userName);
			}
		}
		return mv;
	}

	@RequestMapping("/forget4.htm")
	public ModelAndView forget4(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new JModelAndView("forget4.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_session"));
		if (!verify_session1.equals("")
				&& verify_session1.equals(verify_session)) {
			User user = this.userService.getObjByProperty(null, "userName",
					userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			request.getSession(false).removeAttribute("verify_session");
			mv.addObject("op_title", "?????????????????????????????????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "?????????????????????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param code_name
	 * @throws UnsupportedEncodingException
	 */

	@RequestMapping("/reg_code_get.htm")
	public void reg_code_get(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName) throws UnsupportedEncodingException {
		int ret = 0;// 0??????????????????1??????????????????-1????????????????????????-2???????????????????????????
		if (accept_type.equals("email")) {
			if (this.configService.getSysConfig().isEmailEnable()) {
				String subject = this.configService.getSysConfig()
						.getWebsiteName() + " ???????????????";
				String code = CommUtil.randomString(4).toUpperCase();
				VerifyCode vc = this.verifyCodeService.getObjByProperty(null,
						"email", email);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setEmail(email);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "????????????????????????:" + code + ",????????????????????????30?????????";
				boolean ret1 = this.msgTools.sendEmail(email, subject, content);
				if (ret1) {
					ret = 1;
				}
			} else {
				ret = -1;
			}
		}
		if (accept_type.equals("mobile")) {
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String code = CommUtil.randomString(4);
				VerifyCode vc = this.verifyCodeService.getObjByProperty(null,
						"mobile", mobile);
				if (vc == null) {
					vc = new VerifyCode();
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.save(vc);
				} else {
					vc.setAddTime(new Date());
					vc.setCode(code);
					vc.setMobile(mobile);
					vc.setUserName(userName);
					this.verifyCodeService.update(vc);
				}
				String content = "????????????????????????:" + code + ",????????????????????????30????????????"
						+ this.configService.getSysConfig().getWebsiteName()
						+ "???";
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					ret = 1;
				}

			} else {
				ret = -2;
			}
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
	 * ??????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/find_pws.htm")
	public ModelAndView find_pws(HttpServletRequest request,
			HttpServletResponse response, String userName, String email,
			String code) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		String verify_code = (String) session.getAttribute("verify_code");
		if (code.toUpperCase().equals(verify_code)) {
			User user = this.userService.getObjByProperty(null, "userName",
					userName);
			if (user.getEmail().equals(email.trim())) {
				String pws = CommUtil.randomString(6).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle()
						+ "??????????????????";
				String content = user.getUsername() + ",??????????????????????????????????????????????????????????????????"
						+ pws;
				boolean ret = this.msgTools.sendEmail(email, subject, content);
				if (ret) {
					user.setPassword(Md5Encrypt.md5(pws));
					this.userService.update(user);
					mv.addObject("op_title", "??????????????????????????????:<font color=red>"
							+ email + "</font>???????????????????????????");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/user/login.htm");
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "?????????????????????????????????????????????");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget1.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "???????????????????????????");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "??????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1.htm");
		}
		return mv;
	}

	/**
	 * ??????????????????????????????app?????????????????????????????????pc?????????????????????,
	 * 
	 * @param request
	 * @param response
	 * @param accept_type
	 * @param email
	 * @param mobile
	 * @param userName
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/qr_login_img.htm")
	public void qr_login_img(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		String qr_session_id = CommUtil.null2String(request.getSession()
				.getAttribute("qr_session_id"));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		Map map = new HashMap();
		String qr_img_url = "";
		if (qr_session_id.equals("")) {
			qr_session_id = this.generic_qr(request, qr_session_id);
			qr_img_url = CommUtil.getURL(request) + "/" + uploadFilePath
					+ "/qr_login/" + "qr_" + qr_session_id + ".png";
		} else {
			String logo = request.getSession().getServletContext()
					.getRealPath("")
					+ File.separator
					+ uploadFilePath
					+ File.separator
					+ "qr_login"
					+ File.separator
					+ "qr_"
					+ qr_session_id
					+ ".png";
			if (!CommUtil.fileExist(logo)) {
				this.generic_qr(request, qr_session_id);
			}
			qr_img_url = CommUtil.getURL(request) + "/" + uploadFilePath
					+ "/qr_login/" + "qr_" + qr_session_id + ".png";
		}
		map.put("qr_img_url", qr_img_url);
		map.put("qr_session_id", qr_session_id);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/qr_login.htm")
	public void qr_login(HttpServletRequest request,
			HttpServletResponse response, String qr_session_id)
			throws UnsupportedEncodingException {
		Map params = new HashMap();
		params.put("qr_session_id", qr_session_id);
		List<QRLogin> qrlist = this.qRLoginService
				.query("select obj from QRLogin obj where obj.qr_session_id=:qr_session_id",
						params, -1, -1);
		Map map = new HashMap();
		if (qrlist.size() > 0) {
			QRLogin qrlogin = qrlist.get(0);
			map.put("ret", "true");
			map.put("user_id", qrlogin.getUser_id());
			this.qRLoginService.delete(qrlogin.getId());
			String qr_log_mark = CommUtil.randomString(16);
			map.put("qr_log_mark", qr_log_mark);
			HttpSession session = request.getSession(false);
			session.setAttribute("qr_log_mark", qr_log_mark);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/qr_login_confirm.htm")
	public String qr_login_confirm(HttpServletRequest request,
			HttpServletResponse response, String user_id, String qr_log_mark)
			throws UnsupportedEncodingException {
		HttpSession session = request.getSession(false);
		String session_qr_log_mark = session.getAttribute("qr_log_mark")
				.toString();
		String url = "";
		if (qr_log_mark != null && qr_log_mark.equals(session_qr_log_mark)) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			request.getSession(false).removeAttribute("verify_code");// ?????????????????????
			url = "redirect:" + CommUtil.getURL(request)
					+ "/koala_login.json?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ Globals.THIRD_ACCOUNT_LOGIN + user.getPassword();

		}
		return url;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????session???
	 * 
	 * @param request
	 * @param qr_session_id
	 * @return
	 */
	private String generic_qr(HttpServletRequest request, String qr_session_id) {
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String path = request.getSession().getServletContext().getRealPath("")
				+ File.separator + uploadFilePath + File.separator + "qr_login";
		CommUtil.createFolder(path);
		// ???????????????????????????
		String rand_str = "";
		if (!CommUtil.null2String(qr_session_id).equals("")) {
			rand_str = qr_session_id;
		} else {
			rand_str = UUID.randomUUID().toString();
		}
		request.getSession().setAttribute("qr_session_id", rand_str);
		String login_url = CommUtil.getURL(request)
				+ "/koala_login.json?qr_session_id=" + rand_str;
		request.getSession().setAttribute("qr_session_id", rand_str);
		String logoPath = "";
		if (this.configService.getSysConfig().getQr_logo() != null) {
			logoPath = request.getSession().getServletContext()
					.getRealPath("/")
					+ this.configService.getSysConfig().getQr_logo().getPath()
					+ File.separator
					+ this.configService.getSysConfig().getQr_logo().getName();
		}
		QRCodeUtil.encode(login_url, logoPath, path + "/qr_" + rand_str
				+ ".png", true);
		return rand_str;
	}
}
