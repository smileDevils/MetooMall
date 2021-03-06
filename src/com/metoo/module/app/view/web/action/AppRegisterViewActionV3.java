package com.metoo.module.app.view.web.action;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.Role;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.AddressQueryObject;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IRoleService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.manage.buyer.tool.AppAddressBuyerTools;
import com.metoo.module.app.view.web.tool.AppobileTools;
import com.metoo.msg.MsgTools;

/**
 * 
 * <p>
 * Title: MAppRegisterViewActionV3.class
 * </p>
 * 
 * <p>
 * Descript: 合并游客身份优惠券
 * </p>
 * 
 * 
 * @author 46075
 *
 */
@Controller
@RequestMapping("/app/v3")
public class AppRegisterViewActionV3 {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private AppobileTools mobileTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private AppAddressBuyerTools appAddressBuyerTools;
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param user_name
	 * @param password
	 * @param email
	 * @param mobile
	 * @param area_id
	 * @param area_info
	 * @return
	 * @description 1,合并游客优惠券 2,赠送抽奖次数
	 * 
	 */
	@RequestMapping("wap_register.json")
	@ResponseBody
	public String aotuRegister(HttpServletRequest request, HttpServletResponse response, String true_name,
			String password, String email, String mobile, Long area_id, String area_info, String visitor_id,
			String uid) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int code = -1;
		String msg = "";
		String regist_password = "";
		Map map = new HashMap();
		Map<String, Object> registerMap = new HashMap();
		boolean flag = this.mobileTools.verify(mobile);
		User user = null;
		if (flag) {
			String firebase_token = "";
			if (!CommUtil.null2String(uid).equals("")) {
				User firebase_user = this.userService.getObjById(CommUtil.null2Long(uid));
				firebase_token = firebase_user.getFirebase_token();

			}
			Map<String, Object> params = new HashMap<String, Object>();
			map = this.mobileTools.mobile(mobile);
			String areaMobile = (String) map.get("areaMobile");
			if (!CommUtil.null2String(password).equals("")) {
				regist_password = password;
				password = Md5Encrypt.md5(password).toLowerCase();
				params.clear();
				params.put("userName", areaMobile);
				params.put("password", password);
				params.put("telephone", areaMobile);
				List<User> users = this.userService.query(
						"select obj from User obj where obj.userName=:userName or obj.telephone=:telephone and obj.password=:password",
						params, -1, -1);
				if (users.size() > 0) {
					user = users.get(0);
					if (!CommUtil.null2String(email).equals("")) {
						user.setEmail(email);
						user.setFirebase_token(firebase_token);
						this.userService.update(user);
					}
				} else {
					code = 4011;
					msg = "wrong password";
				}
			} else {
				// 二次验证手机号
				List<User> users = new ArrayList<User>();
				try {
					params.put("email", areaMobile);
					params.put("mobile", areaMobile);
					params.put("userName", areaMobile);
					params.put("areaMobil", areaMobile);
					params.put("deleteStatus", 0);
					users = this.userService.query(
							"select obj from User obj where obj.deleteStatus=:deleteStatus and obj.userName =:userName or obj.userName =:areaMobil or obj.email=:email or obj.mobile=:mobile",
							params, -1, -1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				if (users.size() > 0) {
					code = 4300;
					msg = "The current account is registered";
				} else {
					user = new User();
					user.setUserName(map.get("areaMobile").toString());
					user.setMobile(map.get("userMobile").toString());
					user.setTelephone(map.get("areaMobile").toString());
					user.setAddTime(new Date());
					user.setUserRole("BUYER");
					user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
					user.setPwd("123456");
					user.setAutomatic("1");
					user.setEmail(email);
					user.setFirebase_token(firebase_token);
					params.clear();
					params.put("type", "BUYER");
					List<Role> roles = this.roleService.query("select new Role(id) from Role obj where obj.type=:type",
							params, -1, -1);
					user.getRoles().addAll(roles);
					String query = "select * from metoo_lucky_draw where switchs = 1";
					Map resultSet = this.databaseTools.selectIns(query, null, "register");
					int lucky = CommUtil.null2Int(resultSet.get("lucky"));
					map.put("raffle", lucky);
					user.setRaffle(lucky);
					registerMap.put("raffle", lucky);
					this.userService.save(user);
				}
			}
			if (user != null) {
				// 验证用户是否已有当前地址
				this.appAddressBuyerTools.verifyAdr(user, area_id, area_info, areaMobile);
				String pwd = "123456";
				if (!regist_password.equals("")) {
					pwd = regist_password;
				}
				// 给自动注册买家发送注册通知短信
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String sms_mobile = mobile;
					/*
					 * String content =
					 * "Thank you for browsing soarmall, our website insist on giving the best service and goods to every customer."
					 * + " Account: " + map.get("areaMobile").toString() +
					 * " Password: " + pwd +
					 * " Welcome to the best shopping website soarmall!" +
					 * " WhatsApp: + 86 18900700488" +
					 * " Email: service@soarmall.com";
					 */
					String content = "Thanks for browsing Soarmall, Your account: " + map.get("areaMobile").toString()
							+ "Password: " + pwd + ". WhatsApp:8618900700488 Email:Service@soarmall.com";
					if (!areaMobile.equals("88888888")) {
						try {
							this.msgTools.sendSMS(sms_mobile, content);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				registerMap.put("pwd", pwd);
				registerMap.put("userName", user.getUserName());
				registerMap.put("phoneNumber", map.get("phoneNumber").toString());
				code = 4200;
				msg = "Success";
			}
		} else {
			code = 4400;
			msg = "Wrong number format";
		}
		return Json.toJson(new Result(code, msg, registerMap));
	}

}
