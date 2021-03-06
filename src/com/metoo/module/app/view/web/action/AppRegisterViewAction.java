package com.metoo.module.app.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.EmailMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.core.tools.ResponseUtils;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.Coupon;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.Document;
import com.metoo.foundation.domain.GameLog;
import com.metoo.foundation.domain.GameTask;
import com.metoo.foundation.domain.GoodsVoucher;
import com.metoo.foundation.domain.GoodsVoucherInfo;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.Role;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.AddressQueryObject;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAlbumService;
import com.metoo.foundation.service.IAppGameLogService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.ICouponInfoService;
import com.metoo.foundation.service.ICouponService;
import com.metoo.foundation.service.IDocumentService;
import com.metoo.foundation.service.IGameTaskService;
import com.metoo.foundation.service.IGoodsVoucherInfoService;
import com.metoo.foundation.service.IGoodsVoucherService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IRoleService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.modul.app.game.tree.tools.AppFriendBuyerTools;
import com.metoo.modul.app.game.tree.tools.AppGameAwardTools;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.view.tools.AppGoodsVoucherTools;
import com.metoo.module.app.view.web.tool.AppobileTools;
import com.metoo.msg.MsgTools;

@Controller
@RequestMapping("/app/")
public class AppRegisterViewAction {

	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private AppobileTools mobileTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGameTaskService gameTaskService;
	@Autowired
	private AppGameAwardTools appGameAwardTools;
	@Autowired
	private IAppGameLogService appGameLogService;
	@Autowired
	private AppFriendBuyerTools appFriendBuyerTools;
	@Autowired
	private AppGoodsVoucherTools appGoodsVoucherTools;
	@Autowired
	private IGoodsVoucherService goodsVoucherService;
	@Autowired
	private IGoodsVoucherInfoService goodsVoucherInfoService;

	private static final String REGEX1 = "(.*?????????.*)";
	private static final String REGEX2 = "(.*admin.*)";

	@RequestMapping("v1/register.json")
	public void register_json(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> registermap = new HashMap<String, Object>();
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty(null, "mark", "reg_agree");
		registermap.put("dos_content", doc.getContent());
		registermap.put("dos_id", doc.getId());
		Result result = new Result(4200, "success", registermap);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("v1/verify_username.json")
	public void verify_username(HttpServletRequest request, HttpServletResponse response, String userName, String id) {
		int code = 4200;
		String message = "Success";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("userName", userName.replace(" ", ""));
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService.query(
				"select obj.id from User obj where (obj.userName=:userName or obj.mobile=:userName or obj.email=:userName) and obj.id!=:id",
				params, -1, -1);
		if (users.size() > 0) {
			code = 4225;
			message = "The user already exists";
		}
		Result result = new Result(code, message);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ??????Email
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("v1/verify_email.json")
	public void verify_email(HttpServletRequest request, HttpServletResponse response, String email, String id) {
		int ret = -1;
		String msg = "";
		if (!CommUtil.null2String(email).equals("")) {
			Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = pattern.matcher(CommUtil.null2String(email));
			if (matcher.matches()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("email", email);
				params.put("id", CommUtil.null2Long(id));
				List<User> users = this.userService
						.query("select obj.id from User obj where obj.email=:email and obj.id!=:id", params, -1, -1);
				if (users.size() == 0) {
					ret = 4200;
					msg = "Success";
				}
			} else {
				ret = 4400;
				msg = "Email format error";
			}
		}
		Result result = new Result(ret, msg);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @description ?????????????????????????????????????????????
	 * @param request
	 * @param response
	 * @param email
	 * @param id
	 */
	@RequestMapping("v2/verify_email.json")
	public void verify_email2(HttpServletRequest request, HttpServletResponse response, String email, String mobile,
			String id) {
		int code = -1;
		String msg = "";
		if (!CommUtil.null2String(email).equals("")) {
			Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = pattern.matcher(CommUtil.null2String(email));
			Map map = this.mobileTools.mobile(mobile);
			if (matcher.matches()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("email", email);
				params.put("id", CommUtil.null2Long(id));
				List<User> users = this.userService
						.query("select obj from User obj where obj.email=:email and obj.id!=:id", params, -1, -1);
				if (users.size() > 0) {
					boolean flag = false;
					for (User user : users) {
						if (map.get("areaMobile").toString().equals(user.getTelephone())) {
							flag = true;
							break;
						}
					}
					if (flag) {
						code = 4200;
						msg = "Success";
					} else {
						code = 4300;
						msg = "The mailbox is already in use";
					}
				} else {
					code = 4200;
					msg = "Success";
				}
			} else {
				code = 4400;
				msg = "Email format error";
			}
		} else {
			code = 4403;
			msg = "The mailbox is empty";
		}
		Result result = new Result(code, msg);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("v3/verify_email.json")
	public void verify_email3(HttpServletRequest request, HttpServletResponse response, String email, String token,
			String password) {
		int code = 4200;
		String msg = "Success";
		User user = null;
		if (token != null && !token.equals("")) {
			user = this.userService.getObjByProperty(null, "app_login_token", token);
		}
		if (user != null) {
			if (!CommUtil.null2String(email).equals("") && !CommUtil.null2String(password).equals("")) {
				Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
				Matcher matcher = pattern.matcher(CommUtil.null2String(email));
				if (matcher.matches()) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("email", email);
					List<User> users = this.userService.query("select obj from User obj where obj.email=:email", params,
							-1, -1);
					if (users.size() > 0) {
						if (!users.get(0).getId().equals(user.getId())) {
							code = 4300;
							msg = "The mailbox is already in use";
						} else {
							// ??????????????????
							password = Md5Encrypt.md5(password).toLowerCase();
							if (!user.getPassword().equals(password)) {
								code = 4400;
								msg = "Password error";
							}
						}
					}
				} else {
					code = 4400;
					msg = "Email format error";
				}

			} else {
				code = 4400;
				msg = "Password error";
			}
		} else {
			code = -100;
			msg = "Log in";
		}
		Result result = new Result(code, msg);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ??????Mobile
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("v1/verify_mobile.json")
	public void verify_mobile(HttpServletRequest request, HttpServletResponse response, String mobile, String id) {
		int code = -1;
		String msg = "";
		boolean flag = this.mobileTools.verify(mobile);
		Map map = this.mobileTools.mobile(mobile);
		String areaMobile = (String) map.get("areaMobile");
		String userMobile = (String) map.get("userMobile");
		if (flag) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mobile", userMobile);
			params.put("userName", areaMobile);
			params.put("id", CommUtil.null2Long(id));
			List<User> users = this.userService.query(
					"select obj.id from User obj where obj.mobile=:mobile or obj.userName=:userName and obj.id!=:id",
					params, -1, -1);
			if (users.size() > 0) {
				code = 4300;
				msg = "The current account is registered";
			} else {
				code = 4200;
				msg = "Success";
			}
		} else {
			code = 4400;
			msg = "Format error";
		}
		this.send_json(Json.toJson(new Result(code, msg), JsonFormat.compact()), response);
	}

	/**
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param email
	 * @param mobile
	 * @param code
	 * @param user_type
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SQLException
	 * @description 1,?????????????????? 2,??????????????????
	 */
	@EmailMapping(title = "????????????", value = "register_finish")
	@RequestMapping("v1/register_finish.json")
	public void register_finish_json(HttpServletRequest request, HttpServletResponse response, String userName,
			String password, String email, String mobile, String code, String user_type, String invitation,
			@RequestParam(value = "imei", required = true) String imei, String uid)
			throws HttpException, InterruptedException {
		Result result = null;
		Map<String, Object> registerMap = new HashMap<String, Object>();
		List<User> invi = new ArrayList<User>();
		boolean flag = true;
		boolean register = false;
		// ???????????????????????????????????????????????????????????????????????????
		if (invitation != null && !invitation.equals("")) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", invitation);
			invi = this.userService.query("select obj from User obj where obj.code=:code", params, -1, -1);
			if (invi.size() < 0) {
				result = new Result(4205, "???????????????");
			} else {
				
				// ??????IMEI???
				/*if (!imei.equals("")) {
					User imeiUser = this.userService.getObjByProperty(null, "imei", imei);
					if (imeiUser != null) {
						result = new Result(4215, "???????????????????????????");
					} else {
						flag = true;
					}
				}*/
			}
		} else {
			register = true;
		}
		if (flag || register) {
			try {
				boolean reg = true;
				// ?????????????????????????????????????????????????????????????????????
				/*
				 * if (code != null && !code.equals("")) { code =
				 * CommUtil.filterHTML(code);// ??????????????? } //
				 * System.out.println(this.configService.getSysConfig().
				 * isSecurityCodeRegister()); if
				 * (this.configService.getSysConfig().isSecurityCodeRegister ())
				 * { if (!request.getSession(false).getAttribute("verify_code")
				 * .equals(code)) { reg = false; } }
				 */
				// ???????????????????????? ????????? admin ??????????????????
				if (userName.matches(REGEX1) || userName.toLowerCase().matches(REGEX2)) {
					reg = false;
				}
				if (reg) {
					Map<String, String> map = new HashMap<String, String>();
					if (mobile != null && !mobile.equals("")) {
						boolean mFlag = this.mobileTools.verify(mobile);
						if (mFlag) {
							map = this.mobileTools.mobile(mobile);
							mobile = (String) map.get("areaMobile");
						}
					}
					String firebase_token = "";
					// ??????firebase ??????????????????
					if (!CommUtil.null2String(uid).equals("")) {
						User firebaseUser = this.userService.getObjById(CommUtil.null2Long(uid));
						if (firebaseUser != null) {
							firebase_token = firebaseUser.getFirebase_token();
						}
					}
					User user = new User();
					user.setUserName(userName.replace(" ", ""));
					user.setUserRole("BUYER");
					user.setAddTime(new Date());
					user.setEmail(email);
					user.setMobile(map.get("userMobile"));
					user.setTelephone(map.get("areaMobile"));
					user.setAvailableBalance(BigDecimal.valueOf(0));
					user.setFreezeBlance(BigDecimal.valueOf(0));
					user.setPassword(Md5Encrypt.md5(password).toLowerCase());
					user.setPwd(password);
					user.setImei(imei);
					user.setSex(-1);
					user.setFirebase_token(firebase_token);
					// user.setRaffle(configService.getSysConfig().getRegister_lottery());
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("type", "BUYER");
					List<Role> roles = this.roleService.query("select new Role(id) from Role obj where obj.type=:type",
							params, -1, -1);
					user.getRoles().addAll(roles);
					
					String query = "select * from metoo_lucky_draw where switchs = 1";
					Map resultSet = this.databaseTools.selectIns(query, null, "register");
					int lucky = CommUtil.null2Int(resultSet.get("lucky"));
					user.setRaffle(lucky);
					registerMap.put("raffle", lucky);
					
					// ?????????
					boolean goods_voucher_flag = true;
					if (invitation != null && !invitation.equals("")) {
						User friend = null;
						params.clear();
						params.put("code", invitation);
						List<User> userList = this.userService.query("select obj from User obj where obj.code=:code",
								params, -1, -1);
						if(userList.size() > 0){
							goods_voucher_flag = false;
							friend = userList.get(0);
							friend.setPointNum(friend.getPointNum() + 1);
							user.setPointName(friend.getUsername());
							user.setPointId(friend.getId());
							user.setParent(friend);
							this.userService.update(friend);
							this.userService.save(user);	
							// ??????????????????
							this.appFriendBuyerTools.creatFriend(user, friend);
						}
						
						// ????????????
						params.clear();
						params.put("type", 5);
						params.put("status", 1);
						List<GameTask> gameTasks = this.gameTaskService.query(
								"SELECT obj FROM GameTask obj WHERE obj.status=:status AND obj.type=:type",
								params, -1, -1);
						List gameAward = null;
						if (gameTasks.size() > 0) {
							GameTask gameTask = gameTasks.get(0);
							if(gameTask.getGameAward() != null){
								// ??????-????????????
								gameAward = this.appGameAwardTools.createUpgradeAward(friend, gameTask.getGameAward());
								// ??????????????????
							}
						}
						// ?????????????????? ???????????????????????????
						this.appFriendBuyerTools.registerGameLog(user, friend, gameAward.toString());
						
						// ???????????????
						// 1, ??????????????????????????????
						params.clear();
						params.put("pointId", friend.getId());
						List<User> users = this.userService.query("SELECT obj FROM User obj WHERE obj.pointId=:pointId ", params, -1, -1);
						// 2, ????????????????????????
						params.clear();
						params.put("type", 6);// 6: ???????????????
						List<GoodsVoucher> point_goods_voucher_list = this.goodsVoucherService.query("SELECT obj FROM GoodsVoucher obj WHERE obj.type=:type", params, -1, -1);
						if(users.size() > 3){
							params.clear();
							params.put("type", 7);// 6: ???????????????
							point_goods_voucher_list = this.goodsVoucherService.query("SELECT obj FROM GoodsVoucher obj WHERE obj.type=:type", params, -1, -1);
						}
						GoodsVoucher goodsVoucherPoint = point_goods_voucher_list.get(0);
						// ???????????????????????????
						this.appGoodsVoucherTools.getVoucher(goodsVoucherPoint, friend);
						// ??????????????????
						String message = "Reward for inviting " + user.getUserName() + " to register";
						String message_sa = "???????????? ?????????? " + user.getUserName() + " ??????????????";
						this.appGoodsVoucherTools.createLog(goodsVoucherPoint, friend, 2, 0, message, message_sa, null);
						params.clear();
						params.put("type", 5);// ??????????????????
						List<GoodsVoucher> goods_voucher_list = this.goodsVoucherService.query("SELECT obj FROM GoodsVoucher obj WHERE obj.type=:type", params, -1, -1);
						if(goods_voucher_list.size() > 0){
							GoodsVoucher goodsVoucher = goods_voucher_list.get(0);
							// ?????????????????????
							this.appGoodsVoucherTools.getVoucher(goodsVoucher, user);
							// ??????????????????
							String message1 = "Reward for registration";
							String message_sa1 = "???????????? ??????????????";
							this.appGoodsVoucherTools.createLog(goodsVoucher, user, 3, 0, message1, message_sa1, null);
							registerMap.put("voucher", goodsVoucher.getNumber());
						}
					}else{
						this.userService.save(user);
					}
					
					/*if (this.configService.getSysConfig().isIntegral()) { // [??????]
						user.setIntegral(this.configService.getSysConfig().getMemberRegister());
						// ??????????????????
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("??????????????????" + this.configService.getSysConfig().getMemberRegister() + "???");
						log.setIntegral(this.configService.getSysConfig().getMemberRegister());
						log.setIntegral_user(user);
						log.setType("reg");
						this.integralLogService.save(log);
					}*/ 
					
					// ????????????????????????
					Album album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_default(true);
					album.setAlbum_name("????????????");
					album.setAlbum_sequence(-10000);
					album.setUser(user);
					this.albumService.save(album);
					request.getSession(false).removeAttribute("verify_code");
					// ?????????
					params.clear();
					params.put("employ_type", 1);// ???????????????
					List<Coupon> coupons = this.couponService
							.query("select obj from Coupon obj where obj.employ_type=:employ_type", params, -1, -1);
					for (Coupon coupon : coupons) {
						int size = coupon.getCouponinfos().size();
						if (size <= coupon.getCoupon_count() || coupon.getCoupon_count() == 0) {
							CouponInfo info = new CouponInfo();
							info.setAddTime(new Date());
							info.setUser(user);
							info.setCoupon(coupon);
							info.setCoupon_sn(UUID.randomUUID().toString());
							info.setStore_id(CommUtil.null2Long("-1"));
							this.couponInfoService.save(info);
							registerMap.put("coupon_amount", coupon.getCoupon_amount());
						}
					}
					// ????????? ??????????????????????????????????????????
					if(goods_voucher_flag){
						params.clear();
						params.put("type", 4);// ???????????????
						List<GoodsVoucher> goods_voucher_list = this.goodsVoucherService.query("SELECT obj FROM GoodsVoucher obj WHERE obj.type=:type", params, -1, -1);
						if(goods_voucher_list.size() > 0){
							// ?????????????????????
							GoodsVoucher goodsVoucher = goods_voucher_list.get(0);
							this.appGoodsVoucherTools.getVoucher(goodsVoucher, user);
							registerMap.put("voucher", goodsVoucher.getNumber());
							// ??????????????????
							String message1 = "Reward for registration";
							String message_sa1 = "???????????? ??????????????";
							this.appGoodsVoucherTools.createLog(goodsVoucher, user, 1, 0, message1, message_sa1, null);
						}
					}
					result = new Result(0, "Success", registerMap);
				} else {
					result = new Result(4204, "???????????????????????????'admin and ?????????'");
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = new Result(2, "error");
			}
		} else {
			result = new Result(4, "????????????");
		}
		this.send_json(Json.toJson(result, JsonFormat.compact()), response);
	}

	@EmailMapping(title = "????????????", value = "wap_register")
	@RequestMapping("v1/wap_register.json")
	public void wap_register(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String area_id, String area_info, String mobile, String true_name, String email)
			throws SQLException, UnsupportedEncodingException {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		Map<String, Object> registerMap = new HashMap();
		Result result = null;
		boolean flag = this.mobileTools.verify(mobile);
		if (flag) {
			map = this.mobileTools.mobile(mobile);
			String areaMobile = (String) map.get("areaMobile");
			Map params = new HashMap();
			params.put("email", areaMobile);
			params.put("mobile", areaMobile);
			params.put("userName", areaMobile.replace(" ", ""));
			params.put("areaMobil", areaMobile);
			params.put("deleteStatus", 0);
			List<User> users = this.userService.query(
					"select obj from User obj where obj.deleteStatus=:deleteStatus and obj.userName =:userName or obj.userName =:areaMobil or obj.email=:email or obj.mobile=:mobile",
					params, -1, -1);
			User user = null;
			if (users.size() > 0) {
				user = users.get(0);
				user.setEmail(email);
				this.userService.update(user);
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
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query("select new Role(id) from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				String query = "select * from metoo_lucky_draw where switchs = 1";
				Map resultSet = this.databaseTools.selectIns(query, null, "order");
				int lucky = CommUtil.null2Int(resultSet.get("lucky"));
				map.put("raffle", lucky);
				user.setRaffle(lucky);
				registerMap.put("raffle", lucky);
				this.userService.save(user);
			}
			AddressQueryObject qo = new AddressQueryObject(currentPage, mv, null, null);
			qo.addQuery("obj.user.id", new SysMap("user_id", CommUtil.null2Long(user.getId())), "=");
			qo.addQuery("obj.area.id", new SysMap("area_id", CommUtil.null2Long(area_id)), "=");
			qo.addQuery("obj.area_info", new SysMap("area_info", area_info), "=");
			qo.addQuery("obj.mobile", new SysMap("mobile", map.get("areaMobile").toString()), "=");
			qo.addQuery("obj.trueName", new SysMap("true_name", true_name), "=");
			IPageList pList = this.addressService.list(qo);
			List<Address> addressList = pList.getResult();
			Address address = new Address();
			if (addressList.size() == 0) {
				address.setAddTime(new Date());
				address.setTrueName(true_name);
				address.setArea_info(area_info);
				address.setMobile(map.get("phoneNumber").toString());
				address.setTelephone(map.get("phoneNumber").toString());
				address.setDefault_val(1);
				Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
				address.setArea(area);
				User addressUser = this.userService.getObjById(CommUtil.null2Long(user.getId()));
				address.setUser(addressUser);
				address.setEmail(email);
				this.addressService.save(address);
			}

			// ?????????????????????????????????????????????
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String sms_mobile = mobile;
				/*
				 * String content =
				 * "Congratulations on the success of your order in Soarmall." +
				 * " Your account: " + map.get("areaMobile").toString() +
				 * " Your password: 123456" +
				 * " App download link:http://app.soarmall.com/download/ " +
				 * " Contact us: " + " service@soarmall.com" +
				 * " WhatsApp: + 86 18900700488";
				 */

				String content = "Thank you for browsing soarmall, our website insist on giving the best service and goods to every customer."
						+ " Account: " + map.get("areaMobile").toString() + " Password: 123456"
						+ " Welcome to the best shopping website soarmall!" + " WhatsApp: + 86 18900700488"
						+ " Email: service@soarmall.com";
				if (!areaMobile.equals("88888888")) {
					try {
						boolean sms_flag = this.msgTools.sendSMS(sms_mobile, content);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			registerMap.put("pwd", "123456");
			registerMap.put("userName", user.getUserName());
			registerMap.put("phoneNumber", map.get("phoneNumber").toString());
			result = new Result(4200, "Success", registerMap);
		} else {
			result = new Result(4400, "Wrong number format");
		}
		this.send_json(Json.toJson(result, JsonFormat.compact()), response);
	}

	/**
	 * ??????firesetoken
	 * 
	 * @param request
	 * @param response
	 * @param token
	 * @param firebase_token
	 * @return
	 */
	@RequestMapping("bind_firebase.json")
	@ResponseBody
	public Object bindFirebase(HttpServletRequest request, HttpServletResponse response, String token,
			String firebase_token) {
		Map map = new HashMap();
		User user = null;
		if (firebase_token != null && !firebase_token.equals("")) {
			User visitor = this.userService.getObjByProperty(null, "firebase_token", firebase_token);
			if (visitor == null) {
				boolean flag = true;
				if (token != null && !token.equals("")) {
					user = this.userService.getObjByProperty(null, "app_login_token", token);
					if (user != null) {
						flag = false;
						user.setFirebase_token(firebase_token);
						this.userService.update(user);
					}

				}
				// ??????????????????
				if (flag) {
					user = new User();
					user.setAddTime(new Date());
					user.setFirebase_token(firebase_token);
					user.setUserRole("BUYER");
					Map params = new HashMap();
					params.put("type", "BUYER");
					List<Role> roles = this.roleService.query("select new Role(id) from Role obj where obj.type=:type",
							params, -1, -1);
					user.getRoles().addAll(roles);
					user.setUser_type(4);
					String number = CommUtil.randomInt(9);
					user.setUserName(number);
					user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
					user.setAutomatic("1");
					String login_token = CommUtil.randomString(12) + user.getId();
					user.setApp_login_token(login_token);
					map.put("token", login_token);
					this.userService.save(user);
				}
				map.put("user_id", user.getId());
			} else {
				map.put("user_id", visitor.getId());
				map.put("token", visitor.getApp_login_token());
			}
			return new Result(4200, "Success", map);
		}
		return new Result(4400, "Parameter error");
	}

	private void send_json(String json, HttpServletResponse response) {
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
