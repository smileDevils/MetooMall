package com.metoo.manage.buyer.action;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.domain.query.UserQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ITemplateService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.manage.admin.tools.StoreTools;
import com.metoo.msg.MsgTools;

import sun.misc.BASE64Decoder;


/**
 * 
 * <p>
 * Title: AccountBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:?????????????????????????????????
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
 * @author erikzhang???hezeng???jinxinzhe
 * 
 * @date 2014-4-28
 * 
 * 
 * @version koala_b2b2c v2.0 2015???
 */
@Controller
public class AccountBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private MsgTools msgTools;
	/** ?????????????????????????????? */
	private static final String DEFAULT_AVATAR_FILE_EXT = ".jpg";
	/** ????????? */
	private static BASE64Decoder _decoder = new BASE64Decoder();
	/** ???????????? */
	public static final String OPERATE_RESULT_CODE_SUCCESS = "200";
	/** ???????????? */
	public static final String OPERATE_RESULT_CODE_FAIL = "400";

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_nav.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_nav.htm")
	public ModelAndView account_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/buyer/account.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account.htm")
	public ModelAndView account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_save.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_save.htm")
	public ModelAndView account_save(HttpServletRequest request,
			HttpServletResponse response, String area_id, String birthday) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = (User) wf.toPo(request, this.userService
				.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id));
		}
		//[??????????????????]
		if (birthday != null && !birthday.equals("")) {
			String y[] = birthday.split("-");
			Calendar calendar = new GregorianCalendar();
			int years = calendar.get(Calendar.YEAR) - CommUtil.null2Int(y[0]);
			user.setYears(years);
		}
		this.userService.update(user);
		//[????????????????????????]
		mv.addObject("op_title", "Personal information has been successfully modified");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
		return mv;
	}

 	@SecurityMapping(title = "????????????", value = "/buyer/account_password.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_password.htm")
	public ModelAndView account_password(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_password.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	} 

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_password_save.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_password_save.htm")
	public ModelAndView account_password_save(HttpServletRequest request,
			HttpServletResponse response, String old_password,
			String new_password) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getPassword().equals(
				Md5Encrypt.md5(old_password).toLowerCase())) {
			
			user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			boolean ret = this.userService.update(user);
			//[??????????????????]
			mv.addObject("op_title", "Password changed successfully");
			if (ret) {
				String content = "?????????"
						+ SecurityUserHolder.getCurrentUser().getUserName()
						+ "???????????????" + CommUtil.formatLongDate(new Date())
						+ "????????????????????????????????????" + new_password + ",??????????????????["
						+ this.configService.getSysConfig().getTitle() + "]";
				this.msgTools.sendSMS(user.getMobile(), content);
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//[???????????????????????????????????????]
			mv.addObject("op_title", "The original password was entered incorrectly and the modification failed");
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/buyer/account_password.htm");
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/buyer/account_email.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_email.htm")
	public ModelAndView account_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_email.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_email_save.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_email_save.htm")
	public ModelAndView account_email_save(HttpServletRequest request,
			HttpServletResponse response, String password, String email) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
			user.setEmail(email);
			this.userService.update(user);
			mv.addObject("op_title", "The mailbox was successfully modified");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//[???????????????????????????????????????]
			mv.addObject("op_title", "Password input error, mailbox modification failure");
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/buyer/account_email.htm");
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/buyer/account_avatar.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_avatar.htm")
	public ModelAndView account_avatar(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_avatar.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@SecurityMapping(title = "????????????", value = "/buyer/upload_avatar.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/upload_avatar.htm")
	public void upload_avatar(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "account";
			// <2>.???????????????????????????????????????Id???????????????????????????????????????????????????
			/*String customParams = CommUtil.null2String(request
					.getParameter("custom_params"));
			System.out.println("custom_params = " + customParams);*/
			// <3>. ????????????
			// ---????????????
			String imageType = CommUtil.null2String(request
					.getParameter("image_type"));
			if ("".equals(imageType)) {
				imageType = DEFAULT_AVATAR_FILE_EXT;
			}
			// ???????????????
			String bigAvatarContent = CommUtil.null2String(request
					.getParameter("myfiles"));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String bigAvatarName = SecurityUserHolder.getCurrentUser().getId()
					+ "_big";
			// @@@???????????????
			saveImage(saveFilePathName, imageType, bigAvatarContent,
					bigAvatarName);
			Accessory photo = new Accessory();
			photo.setName(bigAvatarName + imageType);
			photo.setExt(imageType);
			photo.setPath(uploadFilePath + "/account");
			//this.accessoryService.save(photo);
			response.setContentType("text/xml");
			// ??????????????????
			response.getWriter().write(OPERATE_RESULT_CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/xml");
			// ??????????????????
			response.getWriter().write(OPERATE_RESULT_CODE_FAIL);
		}
	}

	/**
	 * ????????????
	 * 
	 * @param filePath
	 *            ????????????
	 * @param imageType
	 *            ????????????(.jpg???.png???.gif)
	 * @param avatarContent
	 *            ????????????
	 * @param avatarName
	 *            ????????????(??????????????????)
	 * @throws IOException
	 */
	private void saveImage(String filePath, String imageType,
			String avatarContent, String avatarName) throws IOException {
		avatarContent = CommUtil.null2String(avatarContent);
		if (!"".equals(avatarContent)) {
			if ("".equals(avatarName)) {
				avatarName = UUID.randomUUID().toString()
						+ DEFAULT_AVATAR_FILE_EXT;
			} else {
				avatarName = avatarName + imageType;
			}
			byte[] data = _decoder.decodeBuffer(avatarContent);
			File f = new File(filePath + File.separator + avatarName);
			if (!f.exists()) {
				f.mkdir();
			}
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
			dos.write(data);
			dos.flush();
			dos.close();
		}
	}
	
	
	@RequestMapping("/filesUpload.htm")
    //requestParam???????????????????????????????????????
    public void filesUpload(@RequestParam("myfiles") MultipartFile files,
            HttpServletRequest request,HttpServletResponse response) throws IOException {
		User user = null;
        List<String> list = new ArrayList<String>();
        try { 
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "account";
		String url = uploadFilePath + File.separator + "account";
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String name = user.getUserName();
      /*  if (files != null && files.length > 0) {*/
            /*for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                // ????????????
            
            int width =  CommUtil.null2Int(map.get("width"));
            }*/
        	  if (files != null && !files.isEmpty()) {
            Map   map = saveFile(request, files,saveFilePathName);
            Accessory accessory = new Accessory();
            accessory.setAddTime(new Date());
            accessory.setExt((String) map.get("mime"));
            accessory.setPath(url);
            accessory.setWidth(CommUtil.null2Int(map.get("width")));
            accessory.setHeight(CommUtil.null2Int(map.get("height")));
            accessory.setName(CommUtil.null2String(map.get("fileName")));
            accessory.setUser(user);
            this.accessoryService.save(accessory);
            response.setContentType("text/xml");
			// ??????????????????
			
				response.getWriter().write(OPERATE_RESULT_CODE_SUCCESS);
        	  }
        	  }
			} catch (IOException e) {
				e.printStackTrace();
				response.setContentType("text/xml");
				// ??????????????????
				response.getWriter().write(OPERATE_RESULT_CODE_FAIL);
			}
    }

    private Map saveFile(HttpServletRequest request,
            MultipartFile file,String path) {
    	Map map = new HashMap();
    	if(file != null && !file.isEmpty()){
    		 try {
				map = CommUtil.saveAccountFileToServer(request, file, path, null,
						null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return map;	
    }

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_mobile.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_mobile.htm")
	public ModelAndView account_mobile(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_mobile.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_mobile_save.htm")
	public ModelAndView account_mobile_save(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,
			String mobile) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
				"mobile", mobile);
		if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
			user.setMobile(mobile);
			this.userService.update(user);
			this.mobileverifycodeService.delete(mvc.getId());
			//[??????????????????]
			mv.addObject("op_title", "Mobile phone binding is successful");
			// ???????????????????????????????????????
			String content = "?????????"
					+ SecurityUserHolder.getCurrentUser().getUserName()
					+ "???????????????" + CommUtil.formatLongDate(new Date())
					+ "????????????????????????["
					+ this.configService.getSysConfig().getTitle() + "]";
			this.msgTools.sendSMS(user.getMobile(), content);
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//[????????????????????????????????????]
			mv.addObject("op_title", "Verification code error, phone binding failed");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/account_mobile.htm");
		}
		return mv;
	}

	/**
	 * ??????????????????
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "??????????????????", value = "/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_mobile_sms.htm")
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		if (type.equals("mobile_vetify_code")) {
			//String code = CommUtil.randomString(4).toUpperCase();
			String code = "8888";
			String content = "?????????"
					+ SecurityUserHolder.getCurrentUser().getUserName()
					+ "???????????????????????????"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "??????????????????????????????????????????" + code + "???["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().isSmsEnbale()) {
				//boolean ret1 = this.msgTools.sendSMS(mobile, content);
				boolean ret1 = true;
				if (ret1) {
					VerifyCode mvc = this.mobileverifycodeService
							.getObjByProperty(null, "mobile", mobile);
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.update(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
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
	}

	@SecurityMapping(title = "????????????", value = "/buyer/account_bind.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_bind.htm")
	public ModelAndView account_bind(HttpServletRequest request,
			HttpServletResponse response, String error) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_bind.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		mv.addObject("error", error);
		return mv;
	}

	@SecurityMapping(title = "??????????????????", value = "/buyer/account_bind_cancel.htm*", rtype = "buyer", rname = "????????????", rcode = "user_center", rgroup = "????????????")
	@RequestMapping("/buyer/account_bind_cancel.htm")
	public String account_bind_cancel(HttpServletRequest request,
			HttpServletResponse response, String account) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_bind.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2String(account).equals("qq")) {
			user.setQq_openid(null);
		}
		if (CommUtil.null2String(account).equals("sina")) {
			user.setSina_openid(null);
		}
		this.userService.update(user);
		return "redirect:account_bind.htm";
	}
}
