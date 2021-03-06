package com.metoo.module.app.view.web.action;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.view.web.tool.AppLoginViewTools;
import com.metoo.module.app.view.web.tool.AppCartViewTools;

import net.sf.json.JSONObject;

/**
 * <p>	
 * 	Title: MAppLoginViewActionV3.class
 * </p>
 * 
 * <p>
 * 	Description: 游客身份登录合并优惠券（待优化为购物车游客id合并游客身份id）
 * </p>
 * 
 * 
 * @author 46075
 *
 */
@Controller
@RequestMapping("/app/v3")
public class AppLoginViewActionV3 {
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private AppCartViewTools mCartViewTools;
	@Autowired
	private AppLoginViewTools mappLoginViewTools;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param token
	 * @descript 登录合并购物车
	 */
	@RequestMapping("app_token_login.json")
	@ResponseBody
	public String app_login_tokenV2(HttpServletRequest request, HttpServletResponse response, String userName,
			String password, String visitor_id, String token, String uid) {
		int code = -3;
		String msg = "";
		Result result = null;
		Map<String, Object> json_map = new HashMap<String, Object>();
		String user_id = "";
		String user_name = "";
		String user_sex = "";
		String login_token = "";
		User login_user = null;
		String firebase_token = "";
		// 合并firebase 游客身份信息
		if(!CommUtil.null2String(uid).equals("")){
			User firebaseUser = this.userService.getObjById(CommUtil.null2Long(uid));
			if(firebaseUser != null){
				firebase_token = firebaseUser.getFirebase_token();
			}
		}
		if (userName != null && !userName.equals("") && password != null && !password.equals("")
				&& CommUtil.null2String(token).equals("")) {
			password = Md5Encrypt.md5(password).toLowerCase();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("email", userName);
			params.put("mobile", userName);
			params.put("telephone", userName);
			params.put("userName", userName.replace(" ", ""));
			params.put("deleteStatus", 0);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.deleteStatus=:deleteStatus and obj.userName =:userName or obj.email=:email or obj.mobile=:mobile or obj.telephone=:telephone",
					params, -1, -1);
			if (users.size() > 0) {
				for (User u : users) {
					if (!u.getPassword().equals(password)) {
						code = -2;
					} else {
						if (u.getUserRole().equalsIgnoreCase("admin")) {
							code = -1;
						} else {
							code = 4200;
							user_id = CommUtil.null2String(u.getId());
							user_sex = CommUtil.null2String(u.getSex());
							user_name = u.getUserName();
							login_token = CommUtil.randomString(12) + user_id;
							u.setApp_login_token(login_token.toLowerCase());
							u.setFirebase_token(firebase_token);
							this.userService.update(u);
							login_user = u;
							break;
						}
					}
				}
			} else {
				code = -4;
			}
		} else {
			if (!CommUtil.null2String(token).equals("")) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("app_login_token", token);
				List<User> users_t = this.userService.query(
						"select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
						params, -1, -1);
				User user = this.userService.getObjByProperty(null, "app_login_token", token);
				if (null == user) {
					code = -5;
				} else {
					/*
					 * for (User obj : users_t) { usr_name = obj.getUsername();
					 * usr_password = obj.getPassword(); }
					 */ // if (usr_name != null && !usr_name.equals("") &&
						// usr_password != null && !usr_password.equals("")) {
						// if (users_t.size() > 0) {
						// for (User u : users_t) {
					/*
					 * if (!user.getPassword().equals(usr_password)) { code =
					 * "-2"; } else {
					 */
					if (user.getUserRole().equalsIgnoreCase("admin")) {
						code = -1;
					} else {
						user_id = CommUtil.null2String(user.getId());
						user_sex = CommUtil.null2String(user.getSex());
						user_name = user.getUserName();
						code = 4200;
						login_token = token;
						user.setApp_login_token(token);
						user.setFirebase_token(firebase_token);
						this.userService.update(user);
						login_user = user;
					}
					// }
					// }
					/*
					 * } else { code = "-4"; }
					 */
					// }
				}
			}
		}
		if (code == 4200) {
			json_map.put("verify", this.mappLoginViewTools.create_appverify(login_user));
			json_map.put("user_id", user_id.toString());
			json_map.put("userName", user_name);
			json_map.put("token", login_token);
			if (CommUtil.null2Int(user_sex) == -1) {
				json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/" + "resources"
						+ "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member.png");
			}
			if (CommUtil.null2Int(user_sex) == 0) {
				json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/" + "resources"
						+ "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member0.png");
			}
			if (CommUtil.null2Int(user_sex) == 1) {
				json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer() + "/" + "resources"
						+ "/" + "style" + "/" + "common" + "/" + "images" + "/" + "member1.png");
			}
			// 记录积分明细
			/*
			 * if(this.configService.getSysConfig().isIntegral()){
			 * login_user.setIntegral(this.configService.getSysConfig().
			 * getMemberSignIn());
			 * 
			 * }
			 */
			Cookie[] cookies = request.getCookies();
			// 登录成功，合并当前用户与未登录时购物车
			String cart_session_id = "";
			if (visitor_id != null && !"".equals(visitor_id)) {
				cart_session_id = visitor_id;
			} else {
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals("cart_session_id")) {
							cart_session_id = CommUtil.null2String(cookie.getValue());
						}
					}
				}
			}
			if (cart_session_id.equals("")) {
				cart_session_id = UUID.randomUUID().toString();
				Cookie cookie = new Cookie("cart_session_id", cart_session_id);
				//cookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(cookie);
			}
			// 合并游客购物车 使用了购物车session_id;优化为同一游客身份id
			List<GoodsCart> cartsList = this.mCartViewTools.cartListCalc(request, login_user, cart_session_id);
			String tourists = "";
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("tourists")) {
						tourists = CommUtil.null2String(cookie.getValue());
						this.mappLoginViewTools.margeCoupon(login_user, tourists);
					}
				}
			}
			
		}
//		int i = 1;
//		String s = "";
//		Gson gson = new Gson();
//		switch (i) {
//		case 0:
//			s = JSONObject.toJSONString(new Result(code, msg, json_map));
//			break;
//		case 1:
//			s = gson.toJson(new Result(code, msg, json_map));
//			break;
//		case 2:
//			s = Json.toJson(new Result(code, msg, json_map));
//		default:
//			break;
//		}
		//return JSONObject.toJSONString(new Result(code, msg, json_map));
		 //return JSONObject.fromObject(new Result(code, msg, json_map)).toString();
		//return Json.toJson(new Result(code, msg, json_map), JsonFormat.compact());
		Cookie cookie = new Cookie("COOLIE_TOKEN", login_token);
		response.addCookie(cookie);
		return JSONObject.fromObject(new Result(code, msg, json_map)).toString();
	}
}
