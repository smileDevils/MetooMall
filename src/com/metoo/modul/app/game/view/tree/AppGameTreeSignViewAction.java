package com.metoo.modul.app.game.view.tree;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.ResponseUtils;
import com.metoo.foundation.domain.GameAward;
import com.metoo.foundation.domain.GameGoods;
import com.metoo.foundation.domain.Sign;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IGameAwardRandomService;
import com.metoo.foundation.service.IGameAwardService;
import com.metoo.foundation.service.ISignService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.impl.GameAwardRandomServiceImpl;
import com.metoo.module.app.buyer.domain.Result;

@Controller
@RequestMapping("app/v1/game/tree/sign")
public class AppGameTreeSignViewAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISignService signService;
	@Autowired
	private IGameAwardService gameAwardService;
	@Autowired
	private IGameAwardRandomService gameAwardRandomService;

	public static void main(String[] args) {
		int i = 2 % 8;
		System.out.println(i);
	}

	@RequestMapping("/querySign.json")
	@ResponseBody
	public Object querySign(HttpServletRequest request, HttpServletResponse response, String token) {
		

		int code = -1;
		String msg = "";
		Map map = new HashMap();
		if (!CommUtil.null2String(token).equals("")) {
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user != null) {
				Sign sign = user.getSign();
				int WhetherSign = -1;// 0??????????????????????????? 1???????????? -1????????????
				String last_time = "";// ?????????????????????
				if (sign == null) {
					sign = new Sign();
					sign.setAddTime(new Date());
					sign.setUser(user);
					sign.setCount(0);
					this.signService.save(sign);
				} else {
					Date update_time = sign.getUpdate_time();
					last_time = CommUtil.formatTime("yyyy-MM-dd HH:mm", update_time);
					// ?????????????????????????????? ????????????????????????
					/*
					 * Calendar c = Calendar.getInstance(); int first_day =
					 * c.get(c.DAY_OF_MONTH); if (first_day == 1) {
					 * sign.setContinueSign(0); this.signService.update(sign); }
					 */
					// ????????????????????????
					if (sign.getContinueSign() > 0) {
						SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
						Date date = null;
						try {
							try {
								date = format.parse(last_time);
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Calendar current = Calendar.getInstance();
						Calendar today = Calendar.getInstance(); // ??????
						today.set(Calendar.YEAR, current.get(Calendar.YEAR));
						today.set(Calendar.MONTH, current.get(Calendar.MONTH));
						today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
						// Calendar.HOUR??????12?????????????????????
						// Calendar.HOUR_OF_DAY??????24?????????????????????
						today.set(Calendar.HOUR_OF_DAY, 0);
						today.set(Calendar.MINUTE, 0);
						today.set(Calendar.SECOND, 0);

						Calendar yesterday = Calendar.getInstance(); // ??????

						yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
						yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
						yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
						yesterday.set(Calendar.HOUR_OF_DAY, 0);
						yesterday.set(Calendar.MINUTE, 0);
						yesterday.set(Calendar.SECOND, 0);

						current.setTime(date);
						// ??????????????????
						if (current.after(today)) {
							WhetherSign = 1;// ?????? ?????????
						} else if (current.before(today) && current.after(yesterday)) {
							WhetherSign = -1;// ?????? ?????????
						} else {
							// ??????-????????????????????????
							WhetherSign = -1;// ?????????
							sign.setContinueSign(0);
							this.signService.update(sign);
						}
					}
				}
				int count = sign.getCount();
				map.put("last_time", last_time);// ??????????????????
				map.put("count", sign.getCount());// ???????????????
				map.put("WhetherSign", WhetherSign);// ????????????????????????
				map.put("continueSign", sign.getContinueSign());// ??????????????????????????????
				
				// ????????????
				Map params = new HashMap();
				params.put("type", 7);
				List<GameAward> gameAwards = this.gameAwardService
						.query("SELECT obj " + "FROM GameAward obj " + "WHERE obj.type=:type", params, -1, -1);
				List<Map> awardList = new ArrayList<Map>();
				if (gameAwards.size() > 0) {
					GameAward gameAward = gameAwards.get(0);
					awardList = Json.fromJson(List.class, gameAward.getWater());
				}
				
				// ???????????????????????? ?????? 
				int period = this.configService.getSysConfig().getPeriod();
				int remainder = sign.getContinueSign() % period;// ????????????????????? +1
				if(sign.getContinueSign() < 8){
					remainder = sign.getContinueSign()/* + 1*/;
					if(WhetherSign == -1){// ?????????????????????????????????????????????1
						remainder = remainder + 1;
					}
				}else{
					if(sign.getContinueSign() % period == 0){
						if(WhetherSign == 1){
							remainder = period;
						}else{
							remainder = 1;
						}
					}else{
						if(WhetherSign == -1){
							remainder = sign.getContinueSign() % period + 1;
						}else{
							remainder = sign.getContinueSign() % period;
						}
//						remainder = sign.getContinueSign() % period;
					}
				}
				map.put("remainder", remainder);// ?????????????????????
				boolean flag = sign.getContinueSign() % period == 0 ? true : false;
				int number = period;// ???????????? 3:24
				if (flag) {
					number = new Double(CommUtil.div(sign.getContinueSign(), period)).intValue();/* ? 1
							: new Double(CommUtil.div(sign.getContinueSign(), period)).intValue();*/
					if(WhetherSign == -1){// ?????????????????????????????????????????????1
						number = number + 1;
					}
				} else {
					number = new Double(CommUtil.div(sign.getContinueSign(), period)).intValue() + 1;
				}
				int days = number * period + 1;
				int day = days - period;
				List<Map> list = new ArrayList<>();
				int index = 0;
				for (int i = day; i < days; i++) {
					LinkedHashMap dayMap = new LinkedHashMap();// ???????????????????????????
					dayMap.put("day", i);
					if(number > 1){
						dayMap.put("award", awardList.get(period-1));
					}else{
						dayMap.put("award", awardList.get(index));
					}
					list.add(dayMap);
					index++;
				}
				map.put("list", list);
				return ResponseUtils.ok(map);
			} else {
				code = -100;
				msg = "token Invalidation";

			}
		} else {
			code = -100;
			msg = "token Invalidation";
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	/*
		int code = -1;
		String msg = "";
		Map map = new HashMap();
		if (!CommUtil.null2String(token).equals("")) {
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user != null) {
				Sign sign = user.getSign();
				int WhetherSign = -1;// 0??????????????????????????? 1???????????? -1????????????
				String last_time = "";// ?????????????????????
				if (sign == null) {
					sign = new Sign();
					sign.setAddTime(new Date());
					sign.setUser(user);
					sign.setCount(0);
					this.signService.save(sign);
				} else {
					Date update_time = sign.getUpdate_time();
					last_time = CommUtil.formatTime("yyyy-MM-dd HH:mm", update_time);
					// ?????????????????????????????? ????????????????????????
					
					 * Calendar c = Calendar.getInstance(); int first_day =
					 * c.get(c.DAY_OF_MONTH); if (first_day == 1) {
					 * sign.setContinueSign(0); this.signService.update(sign); }
					 
					// ????????????????????????
					if (sign.getContinueSign() > 0) {
						SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
						Date date = null;
						try {
							try {
								date = format.parse(last_time);
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Calendar current = Calendar.getInstance();
						Calendar today = Calendar.getInstance(); // ??????
						today.set(Calendar.YEAR, current.get(Calendar.YEAR));
						today.set(Calendar.MONTH, current.get(Calendar.MONTH));
						today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
						// Calendar.HOUR??????12?????????????????????
						// Calendar.HOUR_OF_DAY??????24?????????????????????
						today.set(Calendar.HOUR_OF_DAY, 0);
						today.set(Calendar.MINUTE, 0);
						today.set(Calendar.SECOND, 0);

						Calendar yesterday = Calendar.getInstance(); // ??????

						yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
						yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
						yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
						yesterday.set(Calendar.HOUR_OF_DAY, 0);
						yesterday.set(Calendar.MINUTE, 0);
						yesterday.set(Calendar.SECOND, 0);

						current.setTime(date);
						// ??????????????????
						if (current.after(today)) {
							WhetherSign = 1;// ?????? ?????????
						} else if (current.before(today) && current.after(yesterday)) {
							WhetherSign = -1;// ?????? ?????????
						} else {
							// ??????-????????????????????????
							WhetherSign = -1;// ?????????
							sign.setContinueSign(0);
							this.signService.update(sign);
						}
					}
				}
				int count = sign.getCount();
				map.put("last_time", last_time);// ??????????????????
				map.put("count", sign.getCount());// ???????????????
				map.put("WhetherSign", WhetherSign);// ????????????????????????
				map.put("continueSign", sign.getContinueSign());// ??????????????????????????????
				
				// ????????????
				Map params = new HashMap();
				params.put("type", 7);
				List<GameAward> gameAwards = this.gameAwardService
						.query("SELECT obj " + "FROM GameAward obj " + "WHERE obj.type=:type", params, -1, -1);
				List<Map> awardList = new ArrayList<Map>();
				if (gameAwards.size() > 0) {
					GameAward gameAward = gameAwards.get(0);
					awardList = Json.fromJson(List.class, gameAward.getWater());
				}
				
				// ???????????????????????? ?????? 
				int period = this.configService.getSysConfig().getPeriod();
				int remainder = sign.getContinueSign() % period;// ????????????????????? +1
				if(sign.getContinueSign() < 8){
					remainder = sign.getContinueSign() + 1;
					if(WhetherSign == -1){// ?????????????????????????????????????????????1
						remainder = remainder + 1;
					}
				}else{
					if(sign.getContinueSign() % period == 0){
						if(WhetherSign == 1){
							remainder = period;
						}else{
							remainder = 1;
						}
					}else{
						remainder =	sign.getContinueSign() % period;
					}
				}
				map.put("remainder", remainder);// ?????????????????????
				boolean flag = sign.getContinueSign() % period == 0 ? true : false;
				int number = period;// ???????????? 3:24
				if (flag) {
					number = new Double(CommUtil.div(sign.getContinueSign(), period)).intValue(); ? 1
							: new Double(CommUtil.div(sign.getContinueSign(), period)).intValue();
					if(WhetherSign == -1){// ?????????????????????????????????????????????1
						number = number + 1;
					}
				} else {
					number = new Double(CommUtil.div(sign.getContinueSign(), period)).intValue() + 1;
				}
				int days = number * period + 1;
				int day = days - period;
				List<Map> list = new ArrayList<>();
				int index = 0;
				for (int i = day; i <= days; i++) {
					LinkedHashMap dayMap = new LinkedHashMap();// ???????????????????????????
					dayMap.put("day", i);
					if(number > 1){
						dayMap.put("award", awardList.get(period-1));
					}else{
						dayMap.put("award", awardList.get(index));
					}
					list.add(dayMap);
					index++;
				}
				map.put("list", list);
				return ResponseUtils.ok(map);
			} else {
				code = -100;
				msg = "token Invalidation";

			}
		} else {
			code = -100;
			msg = "token Invalidation";
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	*/}
	

	/**
	 * 
	 * @param request
	 * @param response
	 * @param token
	 * @return
	 * @description ??????
	 */
	@RequestMapping("/checkin.json")
	@ResponseBody
	public String checkin(HttpServletRequest request, HttpServletResponse response, String token) {
		int code = -1;
		String msg = "";
		Map map = new HashMap();
		if (!CommUtil.null2String(token).equals("")) {
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user != null) {
				int WhetherSign = 0;// 0??????????????????????????? 1???????????? -1????????????
				Sign sign = user.getSign();
				int index = 0;// ????????????
				boolean flag = false;
				if (sign == null) {
					// ???????????????
					sign = new Sign();
					sign.setAddTime(new Date());
					sign.setUpdate_time(new Date());
					sign.setContinueSign(1);
					sign.setCount(1);
					sign.setIntegral(1);
					sign.setUser(user);
					this.signService.save(sign);
					flag = true;
					code = 4200;
					msg = "Successfuly";
				} else {
					// ????????????????????????????????? ???????????????????????? ????????????????????????????????????
			/*		Calendar c = Calendar.getInstance();
					int first_day = c.get(c.DAY_OF_MONTH);
					if (first_day == 1) {// ???????????????????????????????????????
						sign.setContinueSign(0);
						this.signService.update(sign);
					}*/
					// ???????????????????????? ????????????????????????
					Date update_time = sign.getUpdate_time();
					if (update_time != null) {
						String last_time = CommUtil.formatTime("yyyy-MM-dd HH:mm", update_time);
						SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
						Date date = null;
						try {
							try {
								date = format.parse(last_time);
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}

						Calendar current = Calendar.getInstance();
						Calendar today = Calendar.getInstance(); // ??????
						today.set(Calendar.YEAR, current.get(Calendar.YEAR));
						today.set(Calendar.MONTH, current.get(Calendar.MONTH));
						today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
						// Calendar.HOUR??????12?????????????????????
						// Calendar.HOUR_OF_DAY??????24?????????????????????
						today.set(Calendar.HOUR_OF_DAY, 0);
						today.set(Calendar.MINUTE, 0);
						today.set(Calendar.SECOND, 0);
						Calendar yesterday = Calendar.getInstance(); // ??????

						yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
						yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
						yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
						yesterday.set(Calendar.HOUR_OF_DAY, 0);
						yesterday.set(Calendar.MINUTE, 0);
						yesterday.set(Calendar.SECOND, 0);
						current.setTime(date);

						if (current.before(today) && current.after(yesterday)) {
							// ???????????????????????????
							WhetherSign = -1;// ?????????
						} else if (current.after(today)) {
							// ???????????????????????????
							WhetherSign = 1;
						} else {
							// ????????????????????????
							sign.setContinueSign(0);
							this.signService.update(sign);
						}
					}
					// ???????????????????????????
					Map params = new HashMap();
					params.put("user_id", user.getId());
					List<Sign> signs = this.signService.query(
							"select obj from Sign obj where date(update_time) = curdate() and obj.user.id=:user_id",
							params, -1, -1);
					if (signs.size() == 0) {
						sign.setUpdate_time(new Date());
						sign.setContinueSign(sign.getContinueSign() + 1);
						sign.setCount(sign.getCount() + 1);
						this.signService.update(sign);
						int period = this.configService.getSysConfig().getPeriod();
						if(sign.getContinueSign() < 8){
							index = sign.getContinueSign() % period;
						}else{
							index = sign.getContinueSign() % period == 0 ? period : sign.getContinueSign() % period;
						}
						flag = true;
						code = 4200;
						msg = "Successfuly";
					} else {
						code = 4401;
						msg = "You have signed in today";
					}
				}
				// ??????
				if(flag){
					Object randomAward = this.gameAwardRandomService.randomAward(user, index - 1);
					map.put("randomAward", randomAward);
				}
			} else {
				code = -100;
				msg = "token Invalidation";
			}
		} else {
			code = -100;
			msg = "token Invalidation";
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	}

	// ???????????????????????????
	public static Date getBeginDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		// return getDayStartTime(calendar.getTime());
		return calendar.getTime();
	}

	// ???????????????????????????
	public static Date getEndDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		int day = calendar.getActualMaximum(5);
		calendar.set(getNowYear(), getNowMonth() - 1, day);
		// return getDayEndTime(calendar.getTime());
		return calendar.getTime();
	}

	// ????????????????????????
	public static Integer getNowYear() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return Integer.valueOf(gc.get(1));
	}

	// ????????????????????????
	public static int getNowMonth() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return gc.get(2) + 1;
	}

	// ?????????????????????????????????
	public static Timestamp getDayStartTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// ?????????????????????????????????
	public static Timestamp getDayEndTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return new Timestamp(calendar.getTimeInMillis());
	}
}
