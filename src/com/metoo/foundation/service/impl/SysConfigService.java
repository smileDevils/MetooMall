package com.metoo.foundation.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metoo.core.constant.Globals;
import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.service.IQueryService;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.core.weixin.pojo.WeixinAccessToken;
import com.metoo.core.weixin.utils.AdvancedUtil;
import com.metoo.core.weixin.utils.PayCommonUtil;
import com.metoo.core.weixin.utils.WeixinUtil;
import com.metoo.foundation.dao.FreeApplyLogDAO;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Activity;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.CombinPlan;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.Favorite;
import com.metoo.foundation.domain.FreeApplyLog;
import com.metoo.foundation.domain.FreeGoods;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsLog;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.GroupInfo;
import com.metoo.foundation.domain.GroupJoiner;
import com.metoo.foundation.domain.GroupLifeGoods;
import com.metoo.foundation.domain.IntegralGoodsCart;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.Message;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.PayoffLog;
import com.metoo.foundation.domain.ReturnGoodsLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.StorePoint;
import com.metoo.foundation.domain.StoreStat;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.Template;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.domain.ZTCGoldLog;
import com.metoo.foundation.service.IGroupJoinerService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.lucene.LuceneUtil;
import com.metoo.lucene.LuceneVo;
import com.metoo.lucene.tools.LuceneVoTools;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.admin.tools.StatTools;
import com.metoo.module.app.buyer.domain.SOAPUtils;
import com.metoo.module.app.domain.AppPushLog;
import com.metoo.module.app.pojo.Ddu;
import com.metoo.module.app.service.IAppPushLogService;
import com.metoo.module.app.view.tools.AppPushTools;
import com.metoo.msg.MsgTools;
import com.metoo.msg.email.SpelTemplate;
import com.metoo.view.web.tools.GoodsViewTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional
public class SysConfigService implements ISysConfigService {
	@Resource(name = "sysConfigDAO")
	private IGenericDAO<SysConfig> sysConfigDAO;
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDAO;
	@Resource(name = "goodsLogDAO")
	private IGenericDAO<GoodsLog> goodsLogDAO;
	@Resource(name = "zTCGlodLogDAO")
	private IGenericDAO<ZTCGoldLog> zTCGlodLogDAO;
	@Resource(name = "storeDAO")
	private IGenericDAO<Store> storeDAO;
	@Resource(name = "templateDAO")
	private IGenericDAO<Template> templateDAO;
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;
	@Resource(name = "messageDAO")
	private IGenericDAO<Message> messageDAO;
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDAO;
	@Resource(name = "payoffLogDAO")
	private IGenericDAO<PayoffLog> payoffLogDAO;
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDAO;
	@Resource(name = "groupLifeGoodsDAO")
	private IGenericDAO<GroupLifeGoods> groupLifeGoodsDAO;
	@Resource(name = "groupGoodsDAO")
	private IGenericDAO<GroupGoods> groupGoodsDAO;
	@Resource(name = "couponInfoDAO")
	private IGenericDAO<CouponInfo> couponInfoDAO;
	@Resource(name = "groupInfoDAO")
	private IGenericDAO<GroupInfo> groupInfoDAO;
	@Resource(name = "freeGoodsDAO")
	private IGenericDAO<FreeGoods> freeGoodsDAO;
	@Resource(name = "combinPlanDAO")
	private IGenericDAO<CombinPlan> combinPlanDAO;
	@Resource(name = "buyGiftDAO")
	private IGenericDAO<BuyGift> buyGiftDAO;
	@Resource(name = "integralGoodsCartDAO")
	private IGenericDAO<IntegralGoodsCart> integralGoodsCartDAO;
	@Resource(name = "enoughReduceDAO")
	private IGenericDAO<EnoughReduce> enoughReduceDAO;
	@Resource(name = "storeStatDAO")
	private IGenericDAO<StoreStat> storeStatDAO;
	@Resource(name = "mobileVerifyCodeDAO")
	private IGenericDAO<VerifyCode> mobileVerifyCodeDAO;
	@Resource(name = "goodsClassDAO")
	private IGenericDAO<GoodsClass> goodsClassDAO;
	@Resource(name = "storePointDAO")
	private IGenericDAO<StorePoint> storePointDAO;
	@Resource(name = "groupDAO")
	private IGenericDAO<Group> groupDAO;
	@Resource(name = "activityDAO")
	private IGenericDAO<Activity> activityDAO;
	@Resource(name = "activityGoodsDAO")
	private IGenericDAO<ActivityGoods> activityGoodsDAO;
	@Resource(name = "orderFormLogDAO")
	private IGenericDAO<OrderFormLog> orderFormLogDAO;
	@Resource(name = "returnGoodsLogDAO")
	private IGenericDAO<ReturnGoodsLog> returnGoodsLogDAO;
	@Resource(name = "evaluateDAO")
	private IGenericDAO<Evaluate> evaluateDAO;
	@Resource(name = "accessoryDAO")
	private IGenericDAO<Accessory> accessoryDAO;
	@Resource(name = "favoriteDAO")
	private IGenericDAO<Favorite> favoriteDAO;
	@Resource(name = "integralLogDAO")
	private IGenericDAO<IntegralLog> integralLogDao;
	@Autowired
	private StatTools statTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	//@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IAppPushLogService appPushLogService;
	@Autowired
	private AppPushTools appPushTools;
	@Autowired
	private FreeApplyLogDAO freeApplyLogDAO;
	@Autowired
	private IQueryService queryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGroupJoinerService groupJoinerService;
	@Autowired
	private DatabaseTools databaseTools;

	@Transactional(readOnly = false)
	public boolean delete(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		return false;
	}

	@Transactional(readOnly = true)
	public SysConfig getSysConfig() {
		// TODO Auto-generated method stub
		List<SysConfig> configs = this.sysConfigDAO.query(
				"select obj from SysConfig obj", null, -1, -1);
		if (configs != null && configs.size() > 0) {
			SysConfig sc = configs.get(0);
			if (sc.getUploadFilePath() == null) {
				sc.setUploadFilePath(Globals.UPLOAD_FILE_PATH);
			}
			if (sc.getSysLanguage() == null) {
				sc.setSysLanguage(Globals.DEFAULT_SYSTEM_LANGUAGE);
			}
			if (sc.getWebsiteName() == null || sc.getWebsiteName().equals("")) {
				sc.setWebsiteName(Globals.DEFAULT_WBESITE_NAME);
			}
			if (sc.getCloseReason() == null || sc.getCloseReason().equals("")) {
				sc.setCloseReason(Globals.DEFAULT_CLOSE_REASON);
			}
			if (sc.getTitle() == null || sc.getTitle().equals("")) {
				sc.setTitle(Globals.DEFAULT_SYSTEM_TITLE);
			}
			if (sc.getImageSaveType() == null
					|| sc.getImageSaveType().equals("")) {
				sc.setImageSaveType(Globals.DEFAULT_IMAGESAVETYPE);
			}
			if (sc.getImageFilesize() == 0) {
				sc.setImageFilesize(Globals.DEFAULT_IMAGE_SIZE);
			}
			if (sc.getSmallWidth() == 0) {
				sc.setSmallWidth(Globals.DEFAULT_IMAGE_SMALL_WIDTH);
			}
			if (sc.getSmallHeight() == 0) {
				sc.setSmallHeight(Globals.DEFAULT_IMAGE_SMALL_HEIGH);
			}
			if (sc.getMiddleWidth() == 0) {
				sc.setMiddleWidth(Globals.DEFAULT_IMAGE_MIDDLE_WIDTH);
			}
			if (sc.getMiddleHeight() == 0) {
				sc.setMiddleHeight(Globals.DEFAULT_IMAGE_MIDDLE_HEIGH);
			}
			if (sc.getBigHeight() == 0) {
				sc.setBigHeight(Globals.DEFAULT_IMAGE_BIG_HEIGH);
			}
			if (sc.getBigWidth() == 0) {
				sc.setBigWidth(Globals.DEFAULT_IMAGE_BIG_WIDTH);
			}
			if (sc.getImageSuffix() == null || sc.getImageSuffix().equals("")) {
				sc.setImageSuffix(Globals.DEFAULT_IMAGE_SUFFIX);
			}
			if (sc.getStoreImage() == null) {
				Accessory storeImage = new Accessory();
				storeImage.setPath("resources/style/common/images");
				storeImage.setName("store.jpg");
				sc.setStoreImage(storeImage);
			}
			if (sc.getGoodsImage() == null) {
				Accessory goodsImage = new Accessory();
				goodsImage.setPath("resources/style/common/images");
				goodsImage.setName("good.jpg");
				sc.setGoodsImage(goodsImage);
			}
			if (sc.getMemberIcon() == null) {
				Accessory memberIcon = new Accessory();
				memberIcon.setPath("resources/style/common/images");
				memberIcon.setName("member.jpg");
				sc.setMemberIcon(memberIcon);
			}
			if (sc.getSecurityCodeType() == null
					|| sc.getSecurityCodeType().equals("")) {
				sc.setSecurityCodeType(Globals.SECURITY_CODE_TYPE);
			}
			if (sc.getWebsiteCss() == null || sc.getWebsiteCss().equals("")) {
				sc.setWebsiteCss(Globals.DEFAULT_THEME);
			}
			if (sc.getPayoff_date() == null) {
				Calendar cale = Calendar.getInstance();
				cale.set(Calendar.DAY_OF_MONTH,
						cale.getActualMaximum(Calendar.DAY_OF_MONTH));
				sc.setPayoff_date(cale.getTime());
			}
			if (sc.getSmsURL() == null || sc.getSmsURL().equals("")) {
				sc.setSmsURL(Globals.DEFAULT_SMS_URL);
			}
			if (sc.getAuto_order_notice() == 0) {
				sc.setAuto_order_notice(3);
			}
			if (sc.getAuto_order_evaluate() == 0) {
				sc.setAuto_order_evaluate(7);
			}
			if (sc.getAuto_order_return() == 0) {
				sc.setAuto_order_return(7);
			}
			if (sc.getAuto_order_confirm() == 0) {
				sc.setAuto_order_confirm(7);
			}
			if (sc.getGrouplife_order_return() == 0) {
				sc.setGrouplife_order_return(7);
			}
			if(sc.getExpiration_point() == 0){
				sc.setExpiration_point(3);
			}
			return sc;
		} else {
			SysConfig sc = new SysConfig();
			sc.setUploadFilePath(Globals.UPLOAD_FILE_PATH);
			sc.setWebsiteName(Globals.DEFAULT_WBESITE_NAME);
			sc.setSysLanguage(Globals.DEFAULT_SYSTEM_LANGUAGE);
			sc.setTitle(Globals.DEFAULT_SYSTEM_TITLE);
			sc.setSecurityCodeType(Globals.SECURITY_CODE_TYPE);
			sc.setEmailEnable(Globals.EAMIL_ENABLE);
			sc.setCloseReason(Globals.DEFAULT_CLOSE_REASON);
			sc.setImageSaveType(Globals.DEFAULT_IMAGESAVETYPE);
			sc.setImageFilesize(Globals.DEFAULT_IMAGE_SIZE);
			sc.setSmallWidth(Globals.DEFAULT_IMAGE_SMALL_WIDTH);
			sc.setSmallHeight(Globals.DEFAULT_IMAGE_SMALL_HEIGH);
			sc.setMiddleHeight(Globals.DEFAULT_IMAGE_MIDDLE_HEIGH);
			sc.setMiddleWidth(Globals.DEFAULT_IMAGE_MIDDLE_WIDTH);
			sc.setBigHeight(Globals.DEFAULT_IMAGE_BIG_HEIGH);
			sc.setBigWidth(Globals.DEFAULT_IMAGE_BIG_WIDTH);
			sc.setImageSuffix(Globals.DEFAULT_IMAGE_SUFFIX);
			sc.setComplaint_time(Globals.DEFAULT_COMPLAINT_TIME);
			sc.setWebsiteCss(Globals.DEFAULT_THEME);
			sc.setSmsURL(Globals.DEFAULT_SMS_URL);
			Accessory goodsImage = new Accessory();
			goodsImage.setPath("resources/style/common/images");
			goodsImage.setName("good.jpg");
			sc.setGoodsImage(goodsImage);
			Accessory storeImage = new Accessory();
			storeImage.setPath("resources/style/common/images");
			storeImage.setName("store.jpg");
			sc.setStoreImage(storeImage);
			Accessory memberIcon = new Accessory();
			memberIcon.setPath("resources/style/common/images");
			memberIcon.setName("member.jpg");
			sc.setMemberIcon(memberIcon);
			Calendar cale = Calendar.getInstance();
			cale.set(Calendar.DAY_OF_MONTH,
					cale.getActualMaximum(Calendar.DAY_OF_MONTH));
			sc.setPayoff_date(cale.getTime());
			sc.setAuto_order_notice(3);
			sc.setAuto_order_evaluate(7);
			sc.setAuto_order_return(7);
			sc.setAuto_order_confirm(7);
			sc.setGrouplife_order_return(7);
			return sc;
		}
	}

	@Transactional(readOnly = false)
	public boolean save(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		try {
			this.sysConfigDAO.save(shopConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean update(SysConfig shopConfig) {
		// TODO Auto-generated method stub
		try {
			this.sysConfigDAO.update(shopConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ??????????????????????????????????????????00:00:01????????? ????????????????????????????????????try catch??????????????????????????????????????????
	 * 
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runTimerByDay() {
		// TODO Auto-generated method stub
		SysConfig sysConfig = this.getSysConfig();
		// ?????????????????????
		Map params = new HashMap();
		params.put("ztc_status", 2);
		List<Goods> goods_audit_list = this.goodsDAO.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);// ??????????????????????????????????????????
		for (Goods goods : goods_audit_list) {
			if (goods.getZtc_begin_time().before(new Date())) {
				goods.setZtc_dredge_price(goods.getZtc_price());
				goods.setZtc_status(3);
				this.goodsDAO.update(goods);
			}
		}
		params.clear();
		params.put("ztc_status", 3);
		goods_audit_list = this.goodsDAO.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);
		for (Goods goods : goods_audit_list) {// ????????????????????????????????????????????????????????????????????????
			if (goods.getZtc_gold() >= goods.getZtc_price()) {
				goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
				goods.setZtc_dredge_price(goods.getZtc_price());
				this.goodsDAO.update(goods);
				ZTCGoldLog log = new ZTCGoldLog();
				log.setAddTime(new Date());
				log.setZgl_content("?????????????????????");
				log.setZgl_gold(goods.getZtc_price());
				log.setZgl_goods_id(goods.getId());
				log.setGoods_name(goods.getGoods_name());
				log.setStore_name(goods.getGoods_store().getStore_name());
				log.setUser_name(goods.getGoods_store().getUser().getUsername());
				log.setZgl_type(1);
				this.zTCGlodLogDAO.save(log);
			} else {
				goods.setZtc_status(0);
				goods.setZtc_dredge_price(0);
				goods.setZtc_pay_status(0);
				goods.setZtc_apply_time(null);
				this.goodsDAO.update(goods);
			}
		}
		// ??????????????????,2015???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		List<Store> stores = this.storeDAO.query(
				"select obj from Store obj where obj.validity is not null",
				null, -1, -1);
		for (Store store : stores) {
			if (store.getValidity().before(new Date())) {// ???????????????????????????
				store.setStore_status(25);// ?????????????????????25?????????????????????
				this.storeDAO.update(store);
				Template template = this.templateDAO.getBy(null, "mark",
						"msg_toseller_store_auto_closed_notify");
				if (template != null && template.isOpen()) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(template.getContent());
					msg.setFromUser(this.userDAO.getBy(null, "userName",
							"admin"));
					msg.setStatus(0);
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageDAO.save(msg);
				}
			}
		}
		// ????????????1?????????????????????????????????????????????????????????
	/*	params.clear();
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		List<GoodsCart> cart_list = this.goodsCartDAO
				.query("select obj from GoodsCart obj where obj.user.id is null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.remove(gc.getId());
		}*/
		// ????????????7??????????????????????????????????????????????????????
		/*params.clear();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		cart_list = this.goodsCartDAO
				.query("select obj from GoodsCart obj where obj.user.id is not null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.remove(gc.getId());
		}*/
		// ????????????7????????????????????????????????????????????????
		/*params.clear();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		params.put("addTime", cal.getTime());
		List<IntegralGoodsCart> ig_cart_list = this.integralGoodsCartDAO
				.query("select obj from IntegralGoodsCart obj where obj.addTime<=:addTime",
						params, -1, -1);
		for (IntegralGoodsCart igc : ig_cart_list) {
			this.integralGoodsCartDAO.remove(igc.getId());
		}*/
		// ??????????????????????????????
		int payoff_count = sysConfig.getPayoff_count();
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int allDate = a.get(Calendar.DATE);// ???????????????
		String selected = "";
		if (payoff_count == 1) {
			selected = CommUtil.null2String(allDate);
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		}
		Date payoff_data = new Date();
		int now_date = payoff_data.getDate();
		String str[] = selected.split(",");
		for (String payoff_date : str) {
			if (CommUtil.null2Int(payoff_date) >= now_date) {
				payoff_data.setDate(CommUtil.null2Int(payoff_date));
				payoff_data.setHours(0);
				payoff_data.setMinutes(00);
				payoff_data.setSeconds(01);
				break;
			}
		}
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "???";
			} else {
				ms = ms + str[i] + "??????";
			}
		}
		ms = "?????????"
				+ DateFormat.getDateInstance(DateFormat.FULL)
						.format(new Date()) + "???????????????????????????" + ms + "?????????????????????????????????";
		sysConfig.setPayoff_mag_default(ms);
		sysConfig.setPayoff_date(payoff_data);
		this.sysConfigDAO.update(sysConfig);
		params.clear();
		params.put("status", 1);
		List<PayoffLog> payofflogs_1 = this.payoffLogDAO
				.query("select obj from PayoffLog obj where obj.status=:status order by addTime desc",
						params, -1, -1);// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		for (PayoffLog temp : payofflogs_1) {
			temp.setStatus(0);
			this.payoffLogDAO.update(temp);
		}
		params.clear();
		params.put("status", 0);
		params.put("PayoffTime", sysConfig.getPayoff_date());
		List<PayoffLog> payofflogs = this.payoffLogDAO
				.query("select obj from PayoffLog obj where obj.status=:status and obj.addTime<:PayoffTime order by addTime desc",
						params, -1, -1);// ???????????????????????????????????????
		for (PayoffLog obj : payofflogs) {
			OrderForm of = this.orderFormDAO.get(CommUtil.null2Long(obj
					.getO_id()));
			Date Payoff_date = this.getSysConfig().getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			boolean payoff = false;// ???????????????????????????
			if (Payoff_date.after(now) && Payoff_date.before(next)) {
				payoff = true;
			}
			if (of.getOrder_cat() == 2) {
				if (of.getOrder_status() == 20 && payoff) {// ?????????????????????
					obj.setStatus(1);// ??????????????????????????????
				}
			}
			if (of.getOrder_cat() == 0) {
				if (of.getOrder_status() >= 40 && payoff) {// ?????????????????????????????????????????????????????????
					obj.setStatus(1);// ??????????????????????????????
				}
				if (obj.getPayoff_type() == -1) {// ????????????????????????????????????????????????????????????????????????
					if (of.getOrder_status() >= 40 && payoff) {// ?????????????????????????????????????????????????????????
						obj.setStatus(3);
						obj.setApply_time(new Date());
					}
				}
			}
			this.payoffLogDAO.update(obj);
		}
		// ???????????????????????????
		/*params.clear();
		params.put("status", 1);
		params.put("end_time", new Date());
		List<GroupLifeGoods> groups = this.groupLifeGoodsDAO
				.query("select obj from GroupLifeGoods obj where obj.group_status=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupLifeGoods group : groups) {
			group.setGroup_status(-2);
			groupLifeGoodsDAO.update(group);
			// ????????????
			String goodslife_lucene_path = System.getProperty("metoob2b2c.root")
					+ File.separator + "luence" + File.separator + "lifegoods";
			File filelife = new File(goodslife_lucene_path);
			if (!filelife.exists()) {
				CommUtil.createFolder(goodslife_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goodslife_lucene_path);
			lucene.delete_index(CommUtil.null2String(group.getId()));
		}*/
		// ?????????????????????
		
		/*params.clear();
		params.put("status", -2);
		params.put("end_time", new Date());
		List<GroupGoods> groupgoodes = this.groupGoodsDAO
				.query("select obj from GroupGoods obj where obj.gg_status!=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupGoods group : groupgoodes) {
			group.setGg_status(-2);
			groupGoodsDAO.update(group);
			params.clear();
			params.put("gid", group.getId());
			List<Goods> goods_list = this.goodsDAO.query(
					"select obj from Goods obj where obj.group.id=:gid",
					params, -1, -1);
			
			for (Goods goods : goods_list) {// ??????????????????????????????
				goods.setGroup(null);
				goods.setGroup_buy(0);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsDAO.update(goods);
				// ????????????
				String goodsgroup_lucene_path = System.getProperty("metoob2b2c.root")
						+ File.separator + "luence" + File.separator
						+ "groupgoods";
				File filegroup = new File(goodsgroup_lucene_path);
				if (!filegroup.exists()) {
					CommUtil.createFolder(goodsgroup_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goodsgroup_lucene_path);
				lucene.delete_index(CommUtil.null2String(group.getId()));
			}
			// ????????????
			String goodsgroup_lucene_path = System.getProperty("metoob2b2c.root")
					+ File.separator + "luence" + File.separator + "groupgoods";
			File filegroup = new File(goodsgroup_lucene_path);
			if (!filegroup.exists()) {
				CommUtil.createFolder(goodsgroup_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goodsgroup_lucene_path);
			lucene.delete_index(CommUtil.null2String(group.getId()));
		}
		*/
		// ???????????????????????????
		/*params.clear();
		params.put("status", 2);
		params.put("begin_time", new Date());
		List<GroupGoods> begin_groupgoodes = this.groupGoodsDAO
				.query("select obj from GroupGoods obj where obj.gg_status=:status and obj.beginTime<=:begin_time",
						params, -1, -1);
		String goods_lucene_path = System.getProperty("metoob2b2c.root")
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(goods_lucene_path);
		for (GroupGoods gg : begin_groupgoodes) {
			gg.setGg_status(1);
			groupGoodsDAO.update(gg);
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(2);
			goods.setGroup(gg.getGroup());
			goods.setGoods_current_price(gg.getGg_price());
			this.goodsDAO.update(goods);
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(gg.getId());
			vo.setVo_title(gg.getGg_name());
			vo.setVo_content(gg.getGg_content());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(gg.getGg_price()));
			vo.setVo_curr_price(CommUtil.null2Double(gg.getGg_price()));
			vo.setVo_add_time(gg.getAddTime().getTime());
			vo.setVo_goods_salenum(gg.getGg_selled_count());
			if (gg.getGg_img() != null) {
				vo.setVo_main_photo_url(gg.getGg_img().getPath() + "/"
						+ gg.getGg_img().getName());
			}
			vo.setVo_cat(gg.getGg_gc().getId().toString());
			vo.setVo_rate(CommUtil.null2String(gg.getGg_rebate()));
			vo.setVo_goods_area(gg.getGg_ga().getId().toString());
			lucene.writeIndex(vo);
		}
		// ??????????????????????????????
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<GroupInfo> groupInfos = this.groupInfoDAO
				.query("select obj from GroupInfo obj where obj.status=:status and obj.lifeGoods.endTime<=:end_time",
						params, -1, -1);
		for (GroupInfo info : groupInfos) {
			info.setStatus(-1);
			groupInfoDAO.update(info);
		}*/
		// ??????????????????????????????
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<CouponInfo> couponInfos = this.couponInfoDAO
				.query("select obj from CouponInfo obj where obj.status=:status and obj.coupon.coupon_end_time<=:end_time",
						params, -1, -1);
		for (CouponInfo couponInfo : couponInfos) {
			couponInfo.setStatus(-1);
			couponInfoDAO.update(couponInfo);
		}
		// ??????????????????????????????
		/*params.clear();
		params.put("combin_status", 1);
		params.put("combin_status0", 0);
		params.put("endTime", new Date());
		List<CombinPlan> combins = this.combinPlanDAO
				.query("select obj from CombinPlan obj where obj.combin_status=:combin_status and obj.endTime<=:endTime or obj.combin_status=:combin_status0 and obj.endTime<=:endTime",
						params, -1, -1);
		for (CombinPlan obj : combins) {
			obj.setCombin_status(-2);
			this.combinPlanDAO.update(obj);
			Goods goods = this.goodsDAO.get(obj.getMain_goods_id());
			if (goods.getCombin_status() == 1) {
				if (obj.getCombin_type() == 0) {
					if (goods.getCombin_suit_id().equals(obj.getId())) {
						goods.setCombin_suit_id(null);
					}
				} else {
					if (goods.getCombin_parts_id().equals(obj.getId())) {
						goods.setCombin_parts_id(null);
					}
				}
				goods.setCombin_status(0);
				this.goodsDAO.update(goods);
			}
		}*/
		// ??????????????????????????????
		/*params.clear();
		params.put("gift_status", 10);
		params.put("end_time", new Date());
		List<BuyGift> bgs = this.buyGiftDAO
				.query("select obj from BuyGift obj where obj.gift_status=:gift_status and obj.endTime<=:end_time",
						params, -1, -1);
		for (BuyGift bg : bgs) {
			bg.setGift_status(20);
			List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
			maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
			for (Map map : maps) {
				Goods goods = this.goodsDAO.get(CommUtil.null2Long(map
						.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					this.goodsDAO.update(goods);
				}
			}
			this.buyGiftDAO.update(bg);
		}*/
		// ????????????????????????
		params.clear();
		params.put("erstatus", 10);
		params.put("erend_time", new Date());
		List<EnoughReduce> er = this.enoughReduceDAO
				.query("select obj from EnoughReduce obj where obj.erstatus=:erstatus and obj.erend_time<=:erend_time",
						params, -1, -1);
		for (EnoughReduce enoughReduce : er) {
			enoughReduce.setErstatus(20);
			this.enoughReduceDAO.update(enoughReduce);
			String goods_json = enoughReduce.getErgoods_ids_json();
			List<String> goods_id_list = (List) Json.fromJson(goods_json);
			for (String goods_id : goods_id_list) {
				Goods ergood = this.goodsDAO.get(CommUtil.null2Long(goods_id));
				ergood.setEnough_reduce(0);
				ergood.setOrder_enough_reduce_id("");
				this.goodsDAO.update(ergood);
			}
		}
		// ???????????????0?????????
		/*params.clear();
		params.put("freeStatus", 5);
		params.put("endTime", new Date());
		List<FreeGoods> fgs = this.freeGoodsDAO
				.query("select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.endTime<=:endTime",
						params, -1, -1);
		for (FreeGoods fg : fgs) {
			fg.setFreeStatus(10);
			this.freeGoodsDAO.update(fg);
			params.clear();
			params.put("freeId", fg.getId());
			List<FreeApplyLog> fals = this.freeApplyLogDAO
					.query("select obj from FreeApplyLog obj where obj.freegoods_id=:freeId and obj.evaluate_status=0",
							params, -1, -1);
			for (FreeApplyLog fal : fals) {
				fal.setEvaluate_status(2);
				this.freeApplyLogDAO.update(fal);
			}
		}
*/
	}

	/**
	 * ??????????????????????????????????????????????????????????????? ????????????????????????????????????try catch??????????????????????????????????????????
	 * 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runTimerByHalfhour() throws Exception {
		// TODO Auto-generated method stub
		// ????????????
		SysConfig sc = this.getSysConfig();
		List<StoreStat> stats = this.storeStatDAO.query(
				"select obj from StoreStat obj", null, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		stat.setAddTime(new Date());
		//[??????Calendar.getInstance()????????????????????????????????????????????????????????????????????????????????????????????????]
		Calendar cal = Calendar.getInstance();
		//[?????????]
		cal.add(Calendar.MINUTE, 1);
		stat.setNext_time(cal.getTime());
		//[ ????????????????????????]
		stat.setWeek_complaint(this.statTools.query_complaint(-7));
		stat.setWeek_goods(this.statTools.query_goods(-7));
		stat.setWeek_order(this.statTools.query_order(-7));
		stat.setWeek_store(this.statTools.query_store(-7));
		stat.setWeek_user(this.statTools.query_user(-7));
		stat.setWeek_live_user(this.statTools.query_live_user(-7));
		stat.setWeek_ztc(this.statTools.query_ztc(-7));
		stat.setWeek_delivery(this.statTools.query_delivery(-7));
		stat.setAll_goods(this.statTools.query_all_goods());
		stat.setAll_store(this.statTools.query_all_store());
		stat.setAll_user(this.statTools.query_all_user());
		stat.setStore_audit(this.statTools.query_audit_store());
		stat.setOrder_amount(BigDecimal.valueOf(this.statTools
				.query_all_amount()));
		stat.setNot_payoff_num(this.statTools.query_payoff());
		stat.setNot_refund(this.statTools.query_refund());
		stat.setNot_grouplife_refund(this.statTools.query_grouplife_refund());
		stat.setAll_sale_amount(CommUtil.null2Int(sc.getPayoff_all_sale()));
		stat.setAll_commission_amount(CommUtil.null2Int(sc
				.getPayoff_all_commission()));
		stat.setAll_payoff_amount(CommUtil.null2Int(sc.getPayoff_all_amount()));
		stat.setAll_payoff_amount_reality(CommUtil.null2Int(sc
				.getPayoff_all_amount_reality()));
		stat.setAll_user_balance(BigDecimal.valueOf(this.statTools
				.query_all_user_balance()));
		stat.setZtc_audit_count(this.statTools.query_ztc_audit());
		stat.setDelivery_audit_count(this.statTools.query_delivery_audit());
		stat.setSelf_goods(this.statTools.query_self_goods());
		stat.setSelf_storage_goods(this.statTools.query_self_storage_goods());
		stat.setSelf_order_shipping(this.statTools.query_self_order_shipping());
		stat.setSelf_order_pay(this.statTools.query_self_order_pay());
		stat.setSelf_order_evaluate(this.statTools.query_self_order_evaluate());
		stat.setSelf_all_order(this.statTools.query_self_all_order());
		stat.setSelf_return_apply(this.statTools.query_self_return_apply());
		stat.setSelf_grouplife_refund(this.statTools
				.query_self_groupinfo_return_apply());
		stat.setGoods_audit(this.statTools.query_goods_audit());
		stat.setSelf_goods_consult(this.statTools.query_self_consult());
		stat.setSelf_activity_goods(this.statTools.query_self_activity_goods());
		stat.setSelf_group_goods(this.statTools.query_self_group_goods());
		stat.setSelf_group_life(this.statTools.query_self_group_life());
		stat.setSelf_free_goods(this.statTools.query_self_free_goods());
		if (stats.size() > 0) {
			this.storeStatDAO.update(stat);
		} else
			this.storeStatDAO.save(stat);
		// ?????????????????????
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -1);
		Map params = new HashMap();
		params.put("time", cal.getTime());
		List<Serializable> mvcs = this.mobileVerifyCodeDAO.query(
				"select obj.id from VerifyCode obj where obj.addTime<=:time",
				params, -1, -1);
		for (Serializable id : mvcs) {
			this.mobileVerifyCodeDAO.remove((Long) id);
		}

		// ???????????????????????????
		List<GoodsClass> gcs = this.goodsClassDAO.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		for (GoodsClass gc : gcs) {
			double description_evaluate = 0;
			double service_evaluate = 0;
			double ship_evaluate = 0;
			params.clear();
			params.put("gc_id", gc.getId());
			params.put("store_status", 15);// ?????????????????????????????????????????????????????????
			List<StorePoint> sp_list = this.storePointDAO
					.query("select obj from StorePoint obj where obj.store.gc_main_id=:gc_id and obj.store.store_status=:store_status",
							params, -1, -1);
			for (StorePoint sp : sp_list) {
				description_evaluate = CommUtil.add(description_evaluate,
						sp.getDescription_evaluate());
				service_evaluate = CommUtil.add(service_evaluate,
						sp.getService_evaluate());
				ship_evaluate = CommUtil.add(ship_evaluate,
						sp.getShip_evaluate());
			}
			gc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
					description_evaluate, sp_list.size())));
			gc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
					service_evaluate, sp_list.size())));
			gc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(ship_evaluate,
					sp_list.size())));
			this.goodsClassDAO.update(gc);
		}
		// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		List<Group> groups = this.groupDAO.query(
				"select obj from Group obj order by obj.addTime", null, -1, -1);
		
		
		for (Group group : groups) {
			if (group.getBeginTime().before(new Date())
					&& group.getEndTime().after(new Date())) {
				group.setStatus(0);
				this.groupDAO.update(group);
			}
			
			boolean goodUpFlag = true;
			if (group.getEndTime().before(new Date())) {
				group.setStatus(-2);
				this.groupDAO.update(group);
				for (GroupGoods gg : group.getGg_list()) {
					gg.setGg_status(-2);
					
					this.groupGoodsDAO.update(gg);
					Goods goods = gg.getGg_goods();
					goods.setGroup_buy(0);
					goods.setGroup(null);
					goods.setGoods_current_price(goods.getStore_price());
					this.goodsDAO.update(goods);
				}
			} 
			
			
			if (group.getEndTime().after(new Date())) {
				group.setStatus(0);
				this.groupDAO.update(group);
				for (GroupGoods gg : group.getGg_list()) {
					Goods goods = gg.getGg_goods();
					goods.setGroup_buy(2);
					goods.setGroup(group);
					goods.setGoods_current_price(gg.getGg_price());
					this.goodsDAO.update(goods);
				}
			} 
			
		}
		//???????????????????????????????????????
//		params.clear();
//		List<GroupJoiner> jList = this.groupJoinerDAO.query("select obj from GroupJoiner o where " +
//				"o.", params, -1, -1);
		
//		String sql = "select b.user_id, b.is_group_creator, b.joiner_count, c.totalPrice, b.add_integral, "
//		+ "b.id, c.id as order_id, a.gg_group_count from " 
//		+ "(SELECT y.child_group_id, x.gg_group_count from metoo_group_goods x, metoo_group_joiner y where x.id=y.rela_group_goods_id "
//	//	+ "and y.joiner_count < x.gg_group_count " 
//		+ "and y.is_group_creator='1' and NOW() >= date_sub(y.create_time,interval - x.gg_max_count HOUR)) a, "
//		+ "metoo_group_joiner b, metoo_orderform c  where a.child_group_id=b.child_group_id "
//		+ "and b.rela_order_form_id=c.id and b.status='1'";
		
		String sql = "SELECT b.child_group_id, a.gg_group_count, b.joiner_count from metoo_group_goods a," +
				" metoo_group_joiner b where a.id=b.rela_group_goods_id and b.is_group_creator='1' " +
				"and NOW() >= date_sub(b.create_time,interval - a.gg_max_count HOUR) and b.status='1'";
		
		List<Object[]> jList = this.queryService.nativeQuery(sql, null, -1, -1);
		
		if (null != jList) {
			
		}
		
		for (Object[] oo : jList) {
			
			
			int joinCount = ((BigInteger)oo[2]).intValue();
			int groupCount = (Integer)oo[1];
			String groupId = (String)oo[0];
			
		//	long orderId = ((BigInteger)oo[6]).longValue();
			
			if (joinCount >= groupCount) {
				
				
				List<Object[]> dnList = this.queryService.query("select t.user_id, t.add_integral, t.rela_order_form_id " +
						"from metoo_group_joiner t " +
						"where t.child_group_id='"+groupId+"' and t.status='1'", null, -1, -1);
				if (null != dnList) {
					
					for (Object o : dnList) {
						
						long uId = (Long.parseLong((String)oo[0]));
						int integ = (int)(((BigInteger)oo[1]).longValue());
						long orderId = ((BigInteger)oo[2]).longValue();
						
						User u = this.userService.getObjById(uId);
						if (null != u) {
							
							u.setIntegral(u.getIntegral() + integ);
							this.userService.save(u);
							
							if(integ > 0){
								IntegralLog log = new IntegralLog();
								log.setAddTime(new Date());
								log.setContent("??????????????????"
										+ integ + "???");
								log.setIntegral(integ);
								log.setIntegral_user(u);
								log.setType("order");
								this.integralLogDao.save(log);
							
								//?????????????????????
								User parent_user =  u.getParent();
								if(parent_user != null){
									parent_user.setIntegral(parent_user.getIntegral() + integ);
									this.userService.save(parent_user);
									
									IntegralLog log1 = new IntegralLog();
									log1.setAddTime(new Date());
									log1.setContent("?????????????????????"
											+ integ + "???");
									log1.setIntegral(integ);
									log1.setIntegral_user(parent_user);
									log1.setType("chind_order");
									this.integralLogDao.save(log1);
								}
								
								//??????????????????????????????50%??????
								User grant_user =  u.getParent().getParent();
								if(grant_user != null){
									Integer ti =Math.round(integ/2);
									grant_user.setIntegral( grant_user.getIntegral() + ti);
									this.userService.save(grant_user);
									
									IntegralLog log2 = new IntegralLog();
									log2.setAddTime(new Date());
									log2.setContent("???????????????????????????"
											+ ti + "???");
									log2.setIntegral(ti);
									log2.setIntegral_user(grant_user);
									log2.setType("third_order");
									this.integralLogDao.save(log2);
								}
								
							}
							
						}
						
						OrderForm of = this.orderFormDAO.get(orderId);
						of.setOrder_status(20);
						orderFormDAO.update(of);
					}
				}
				
			} else {
				
				
				List<Object[]> nnList = this.queryService.query("select t.id,  t.rela_order_form_id " +
						"from metoo_group_joiner t " +
						"where t.child_group_id='"+groupId+"' and t.status='1'", null, -1, -1);
				for (Object o : nnList) {
					long id = ((BigInteger)oo[0]).longValue();
					long orderId = ((BigInteger)oo[1]).longValue();
					
					GroupJoiner gji = this.groupJoinerService.getObjById(id);
					gji.setAdd_integral(0);
					gji.setStatus("2");
					this.groupJoinerService.update(gji);
					
					OrderForm of = this.orderFormDAO.get(orderId);
					
					if (of.getTotalPrice().doubleValue() > 0) {
						try {
							Map<String, String> map = weixinPayRefund(this.getSysConfig(), of);
						
							if("SUCCESS".equalsIgnoreCase(map.get("return_code"))){//????????????api??????????????????
								
								of.setOrder_status(70);
								of.setRefund_fee(Integer.parseInt(map.get("refund_fee")));
								of.setRefund_id(map.get("refund_id"));
								of.setRefund_out_no(map.get("out_refund_no"));
							} else {
								
								of.setOrder_status(75);
								
							}
							orderFormDAO.update(of);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}	
					}
				}
			}
			
		}
		
		// ??????????????????????????????????????????????????????,??????????????????????????????????????????
		params.clear();
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> acts = this.activityDAO
				.query("select obj from Activity obj where obj.ac_end_time<=:ac_end_time and obj.ac_status=:ac_status",
						params, -1, -1);
		for (Activity act : acts) {
			act.setAc_status(0);
			this.activityDAO.update(act);
			for (ActivityGoods ac : act.getAgs()) {
				ac.setAg_status(-2);// ????????????
				this.activityGoodsDAO.update(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);// ???????????????????????????
				goods.setActivity_goods_id(null);
				this.goodsDAO.update(goods);
			}
		}
		// ????????????????????????????????????????????????????????????????????????
		int auto_order_notice = sc.getAuto_order_notice();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_notice);
		params.put("shipTime", cal.getTime());
		params.put("auto_confirm_email", true);
		params.put("auto_confirm_sms", true);
		List<OrderForm> notice_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and (obj.auto_confirm_email=:auto_confirm_email or obj.auto_confirm_sms=:auto_confirm_sms)",
						params, -1, -1);
		for (OrderForm of : notice_ofs) {
			if (!of.isAuto_confirm_email()) {// ?????????????????????
				boolean email = this.send_email(of,
						"email_tobuyer_order_will_confirm_notify");
				if (email) {
					of.setAuto_confirm_email(true);
					this.orderFormDAO.update(of);
				}
			}
			if (!of.isAuto_confirm_sms()) {
				User buyer = this.userDAO.get(CommUtil.null2Long(of
						.getUser_id()));
				boolean sms = this.send_sms(of, buyer.getMobile(),
						"sms_tobuyer_order_will_confirm_notify");
				if (sms) {
					of.setAuto_confirm_sms(true);
					this.orderFormDAO.update(of);
				}
			}
		}
		// ???????????????????????????????????????
		//begin
		/*int auto_order_confirm = sc.getAuto_order_confirm();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_confirm);
		params.put("shipTime", cal.getTime());
		params.put("order_status", 30);
		List<OrderForm> confirm_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.shipTime<=:shipTime and obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : confirm_ofs) {
			cal.setTime(of.getShipTime());
			cal.add(Calendar.DAY_OF_YEAR,
					auto_order_confirm + of.getOrder_confirm_delay());// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			if (cal.getTime().before(new Date())) {
				of.setOrder_status(40);// ??????????????????
				this.orderFormDAO.update(of);

				Store store = this.storeDAO.get(CommUtil.null2Long(of
						.getStore_id()));
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("????????????");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(of);
				this.orderFormLogDAO.save(ofl);
				if (sc.isEmailEnable() && of.getOrder_form() == 0) {
					this.send_email(of,
							"email_toseller_order_receive_ok_notify");
				}
				if (sc.isSmsEnbale() && of.getOrder_form() == 0) {
					this.send_sms(of, store.getUser().getMobile(),
							"sms_toseller_order_receive_ok_notify");
				}
				if (of.getPayType().equals("payafter")) {// ?????????????????????????????????????????????????????????????????????????????????f (of.getPayment().getMark().equals("payafter")) {
					this.update_goods_inventory(of);// ??????????????????
				}
				// ????????????????????????
				if (of.getOrder_form() == 0) {// ??????????????????????????????,?????????????????????????????????????????????????????????????????????????????????
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("????????????????????????");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(of.getId()));
					plog.setOrder_id(of.getOrder_id().toString());
					plog.setCommission_amount(of.getCommission_amount());// ????????????????????????
					plog.setGoods_info(of.getGoods_info());
					plog.setOrder_total_price(of.getGoods_amount());// ????????????????????????
					plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(
							of.getGoods_amount(), of.getCommission_amount())));// ???????????????????????????????????????=?????????????????????-???????????????
					this.payoffLogDAO.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), store.getStore_sale_amount())));// ?????????????????????????????????
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(of.getCommission_amount(),
									store.getStore_commission_amount())));// ???????????????????????????
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));// ???????????????????????????
					this.storeDAO.update(store);
					// ???????????????????????????????????????
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil
							.add(of.getCommission_amount(),
									sc.getPayoff_all_commission())));
					this.sysConfigDAO.update(sc);
				}
			}
		}*/
		//end
		
		// ???????????????????????????????????????????????????????????????
		int auto_order_evaluate = sc.getAuto_order_evaluate();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_evaluate);
		params.put("auto_order_evaluate", cal.getTime());
		params.put("order_status_40", 40);
		List<OrderForm> confirm_evaluate_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.confirmTime<=:auto_order_evaluate and obj.order_status=:order_status_40 order by addTime asc",
						params, -1, -1);
		for (OrderForm order : confirm_evaluate_ofs) {
			order.setOrder_status(65);
			this.orderFormDAO.update(order);

			User user = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			// ??????????????????
			user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(
					user.getUser_goods_fee(), order.getTotalPrice())));
			this.userDAO.update(user);
		}
		// ?????????????????????????????????????????????????????????????????????????????????
		int auto_order_return = sc.getAuto_order_return();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		params.put("return_shipTime", cal.getTime());
		params.put("order_status", 40);
		List<OrderForm> confirm_return_ofs = this.orderFormDAO
				.query("select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status",
						params, -1, -1);
		for (OrderForm order : confirm_return_ofs) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				m.put("goods_return_status", -1);
				gls.putAll(m);
				new_maps.add(m);
			}
			order.setGoods_info(Json.toJson(new_maps));
			this.orderFormDAO.update(order);
			Map rgl_params = new HashMap();
			rgl_params.put("goods_return_status", "-2");
			rgl_params.put("return_order_id", order.getId());
			List<ReturnGoodsLog> rgl = this.returnGoodsLogDAO
					.query("select obj from ReturnGoodsLog obj where obj.goods_return_status is not :goods_return_status and obj.return_order_id=:return_order_id",
							rgl_params, -1, -1);
			for (ReturnGoodsLog r : rgl) {
				r.setGoods_return_status("-2");
				this.returnGoodsLogDAO.update(r);
			}
			/*// ??????????????????
			int user_integral = (int) CommUtil.div(order.getTotalPrice(),
					sc.getConsumptionRatio());
			if (user_integral > sc.getEveryIndentLimit()) {
				user_integral = sc.getEveryIndentLimit();
			}
			User orderUser = this.userDAO.get(CommUtil.null2Long(order
					.getUser_id()));
			orderUser.setIntegral(orderUser.getIntegral() + user_integral);
			// ??????????????????
			if (sc.isIntegral()) {
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("????????????" + user_integral + "???");
				log.setIntegral(user_integral);
				log.setIntegral_user(orderUser);
				log.setType("order");
				this.integralLogDao.save(log);
			}*/
		}
		// ?????????????????????????????????[evaluate_goods ?????????????????????]
		List<Goods> goods_list = this.goodsDAO.query(
				"select distinct obj.evaluate_goods from Evaluate obj ", null,
				-1, -1);
		for (Goods goods : goods_list) {
			// ???????????????????????????????????????
			double description_evaluate = 0;
			params.clear();
			params.put("evaluate_goods_id", goods.getId());
			List<Evaluate> eva_list = this.evaluateDAO
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
							params, -1, -1);
			//[add ??????????????????]
			for (Evaluate eva : eva_list) {
				description_evaluate = CommUtil.add(
						eva.getDescription_evaluate(), description_evaluate);
			}
			//[div ?????????????????????]
			description_evaluate = CommUtil.div(description_evaluate,
					eva_list.size());
			
			goods.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			if (eva_list.size() > 0) {// ????????????????????????
				// ??????????????????????????????
				double well_evaluate = 0;
				double well_evaluate_num = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				//params.put("evaluate_buyer_val", 5);
				String id = CommUtil.null2String(goods.getId());
				int num = this.databaseTools.queryNum("select SUM(evaluate_buyer_val) from "
						+ Globals.DEFAULT_TABLE_SUFFIX 
						+ "evaluate where evaluate_goods_id="
						+ id 
						+ " and evaluate_buyer_val BETWEEN 1 AND 5 ");
				
				well_evaluate_num = CommUtil.mul(5, eva_list.size());
				well_evaluate = CommUtil.div(num, well_evaluate_num);
				goods.setWell_evaluate(BigDecimal.valueOf(well_evaluate));
				// ??????????????????????????????
				double middle_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				//params.put("evaluate_buyer_val", 3);
				List<Evaluate> middle_list = this.evaluateDAO
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val BETWEEN 2 AND 3",
								params, -1, -1);
				middle_evaluate = CommUtil.div(middle_list.size(),
						eva_list.size());
				goods.setMiddle_evaluate(BigDecimal.valueOf(middle_evaluate));
				// ??????????????????????????????
				double bad_evaluate = 0;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", 1);
				List<Evaluate> bad_list = this.evaluateDAO
						.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id and obj.evaluate_buyer_val=:evaluate_buyer_val",
								params, -1, -1);
				bad_evaluate = CommUtil.div(bad_list.size(), eva_list.size());
				goods.setBad_evaluate(BigDecimal.valueOf(bad_evaluate));
			}
			this.goodsDAO.update(goods);
		}
		//??????????????????
		/*params.clear();
		List<Store> stores = new ArrayList<Store>();
		params.put("store_status", 15);
		//params.put("store_name", "Dowell");
		stores = this.storeDAO.query("select obj from Store obj where obj.store_status=:store_status", params, -1, -1);
		
		for(Store store : stores){
				List<Goods> goodslist = store.getGoods_list();	
				if(!goodslist.isEmpty()){
					for(Goods goods : goodslist){
						double weight = 0.0;
						//??????????????????
						double well_weight = CommUtil.mul(goods.getWell_evaluate(), "0.08"); // ?????????
						weight += well_weight;
						double discount = CommUtil.mul(goods.getGoods_discount_rate(), 0.005); // ?????????
						weight += discount;
						double inventory = CommUtil.mul(goods.getGoods_inventory(), 0.01);//??????
						weight += inventory;
						System.out.println(inventory);
						
						double transport = 0.0;//????????????
						switch (goods.getGoods_store().getGrade().getGradeName()) {
						
						case "Local": transport = CommUtil.mul(1, 0.05);
									weight += transport;
							break;
						case "FBS": transport = CommUtil.mul(2, 0.05);
									weight += transport;
							break;
						
						default:
								transport = CommUtil.mul(0, 0.05);
								weight += transport;
							break;
						}
						double transportfee = 0.0;//??????????????????
						if(goods.getGoods_transfee() == 0){
							transportfee = CommUtil.mul(1, 0.05);
						}else{
							transportfee = CommUtil.mul(2, 0.05);
						}
						weight += transportfee;
						
						double div = CommUtil.div(goods.getGoods_collect(), goods.getGoods_click());
						double collect_click = CommUtil.mul(div, 0.05);//??????/??????
						weight += collect_click;
						
						params.clear();
						params.put("evaluate_goods_id", goods.getId());
						List<Evaluate> eva_list = this.evaluateDAO
								.query("select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
										params, -1, -1);
						double div1 = CommUtil.div(eva_list.size(), goods.getGoods_salenum());//?????????
						double evaluate_salenum = CommUtil.mul(div1, 0.05);
						weight += evaluate_salenum;
						
						double goods_type = 0.0;//????????????
						if(goods.getGoods_type() == 0){
							goods_type = CommUtil.mul(2, 0.08);
						}else{
							goods_type = CommUtil.mul(1, 0.08);
						}
						weight += goods_type;
						
						double goods_enough = 0.0;//??????????????????
						if(goods.getEnough_reduce() == 0){
							goods_enough = CommUtil.mul(0, 0.03);
						}else{
							goods_enough = CommUtil.mul(1, 0.03);
						}
						weight += goods_enough;
						
						double enough_free = 0.0;//??????????????????
						if(goods.getEnough_free() == 0){
							enough_free = CommUtil.mul(0, 0.03);
						}else{
							enough_free = CommUtil.mul(1, 0.03);
						}
						weight += enough_free;
						
						double order_enough_give_status = 0.0;//??????????????????
						if(goods.getOrder_enough_give_status() == 0){
							order_enough_give_status = CommUtil.mul(0, 0.03);
						}else{
							order_enough_give_status = CommUtil.mul(1, 0.03);
						}
						weight += order_enough_give_status;
						
						if(goods.getCarts().size() > 0){//?????????/????????? 
							double  div2 = CommUtil.div(goods.getCarts().size(), goods.getGoods_click());
							double cart_click = CommUtil.mul(div2, 0.03);
							weight += cart_click;
						}
						if(goods.getCarts().size() > 0){//????????????/????????? 
							double div3 = CommUtil.div(goods.getGoods_salenum(), goods.getGoods_click());
							double salenum_click = CommUtil.mul(div3, 0.03);
							weight += salenum_click;
						}
						
						if(goods.getGoods_brand() == null ){ // brand
							 CommUtil.mul(, 0.05);
						}else{
							
						}
						
						//CommUtil.mul(goods.getGoods_brand() != null ? , 0.01);
						goods.setWeightiness(CommUtil.null2BigDecimal(weight));
						this.goodsDAO.update(goods);
					}
				}
			}*/
		// ????????????????????????
		params.clear();
		params.put("goods_status", 2);
		List<Goods> goods_list2 = this.goodsDAO
				.query("select obj from Goods obj where obj.goods_status=:goods_status ",
						params, -1, -1);
		for (Goods goods : goods_list2) {
			if (goods.getGoods_seller_time().after(new Date())) {
				goods.setGoods_status(0);
				this.goodsDAO.update(goods);
				// ??????lucene??????
				String goods_lucene_path = System.getProperty("metoob2b2c.root")
						+ File.separator + "luence" + File.separator + "goods";
				LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.writeIndex(vo);
			}
		}
		// ???????????????????????????
		params.clear();
//		params.put("info", "eva_temp");
//		List<Accessory> acc = this.accessoryDAO
//				.query("select new Accessory(id) from Accessory obj where obj.info=:info",
//						params, -1, -1);
//		for (Accessory accessory : acc) {
//			boolean ret = CommUtil.deleteFile(System
//					.getProperty("metoob2b2c.root")
//					+ File.separator
//					+ accessory.getPath()
//					+ File.separator
//					+ accessory.getName());
//			if (ret) {
//				this.accessoryDAO.remove(accessory.getId());
//			}
//		}
		// ?????????????????????????????????????????????????????????????????????????????????
		List<Favorite> favs = this.favoriteDAO.query(
				"select obj from Favorite obj where obj.type=0", null, -1, -1);
		BigDecimal bd = new BigDecimal(0.00);
		User fromUser = this.userDAO.getBy(null, "userName", "admin");
		for (Favorite fav : favs) {
			Goods goods = this.goodsDAO.get(fav.getGoods_id());
			if (goods.getPrice_history() != null) {
				if (goods != null
						&& goods.getGoods_current_price().compareTo(
								fav.getGoods_current_price()) < 0) {
					String msg_content = "??????????????????" + goods.getGoods_name()
							+ "???????????????????????????";
					// ?????????????????????
					User user = this.userDAO.get(fav.getUser_id());
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setStatus(0);
					msg.setType(0);
					msg.setContent(msg_content);
					msg.setFromUser(fromUser);
					msg.setToUser(user);
					this.messageDAO.save(msg);
					fav.setGoods_current_price(goods.getGoods_current_price() == null ? goods.getGoods_price() : goods.getGoods_current_price());
					this.favoriteDAO.update(fav);
				}
			}
		}
		params.clear();
		params.put("send_type", 1);
		params.put("status", 0);
		params.put("sendtime", new Date());
		List<AppPushLog> appPushLoglist = this.appPushLogService
				.query("select obj from AppPushLog obj where obj.send_type=:send_type and obj.status=:status and obj.sendtime<=:sendtime",
						params, -1, -1);
		for (AppPushLog appPushLog : appPushLoglist) {
			if (appPushLog.getDevice() == 0) {
				this.appPushTools.android_push(appPushLog);// ???????????????????????????
				this.appPushTools.ios_push(appPushLog);// ?????????ios????????????
			} else if (appPushLog.getDevice() == 1) {
				this.appPushTools.android_push(appPushLog);// ???????????????????????????
			} else if (appPushLog.getDevice() == 2) {
				this.appPushTools.ios_push(appPushLog);// ?????????ios????????????
			}
		}
	}

	private boolean send_email(OrderForm order, String mark) throws Exception {
		SysConfig sc = this.getSysConfig();
		Template template = this.templateDAO.getBy(null, "mark", mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			Store store = this.storeDAO.get(CommUtil.null2Long(order
					.getStore_id()));
			String email = store.getUser().getEmail();
			String subject = template.getTitle();
			User buyer = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			boolean ret = this.msgTools.sendEmail(email, subject, content);
			return ret;
		} else
			return false;
	}

	private boolean send_sms(OrderForm order, String mobile, String mark)
			throws Exception {
		SysConfig sc = this.getSysConfig();
		Store store = this.storeDAO
				.get(CommUtil.null2Long(order.getStore_id()));
		Template template = this.templateDAO.getBy(null, "mark", mark);
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			User buyer = this.userDAO
					.get(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			boolean ret = this.msgTools.sendSMS(mobile, content);
			return ret;
		} else
			return false;
	}

	/**
	 * ??????????????????
	 * 
	 * @param order
	 */
	private void update_goods_inventory(OrderForm order) {
		// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil
				.null2String(order.getId()));
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(
					CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_count(gg.getGg_count() - goods_count);
						this.groupGoodsDAO.update(gg);
						// ??????lucene??????
						String goods_lucene_path = System
								.getProperty("user.dir")
								+ File.separator
								+ "luence" + File.separator + "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()),
								luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
			List<String> gsps = new ArrayList<String>();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
					.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
			String spectype = "";
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				gsps.add(gsp.getId().toString());
				spectype += gsp.getSpec().getName() + ":" + gsp.getValue()
						+ " ";
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(goods
					.getId());
			todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum()
					+ goods_count);
			Map<String, Integer> logordermap = (Map<String, Integer>) Json
					.fromJson(todayGoodsLog.getGoods_order_type());
			String ordertype = order.getOrder_type();
			if (logordermap.containsKey(ordertype)) {
				logordermap.put(ordertype, logordermap.get(ordertype)
						+ goods_count);
			} else {
				logordermap.put(ordertype, goods_count);
			}
			todayGoodsLog.setGoods_order_type(Json.toJson(logordermap,
					JsonFormat.compact()));

			Map<String, Integer> logspecmap = (Map<String, Integer>) Json
					.fromJson(todayGoodsLog.getGoods_sale_info());

			if (logspecmap.containsKey(spectype)) {
				logspecmap
						.put(spectype, logspecmap.get(spectype) + goods_count);
			} else {
				logspecmap.put(spectype, goods_count);
			}
			todayGoodsLog.setGoods_sale_info(Json.toJson(logspecmap,
					JsonFormat.compact()));

			this.goodsLogDAO.update(todayGoodsLog);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- goods_count);
			} else {
				List<HashMap> list = Json
						.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put("count", CommUtil.null2Int(temp.get("count"))
								- goods_count);
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(list,
						JsonFormat.compact()));
			}
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// ????????????????????????????????????????????????
				}
			}
			this.goodsDAO.update(goods);
			// ??????lucene??????
			String goods_lucene_path = System.getProperty("metoob2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()),
					luceneVoTools.updateGoodsIndex(goods));
		}

	}

	/**
	 * ?????????????????????????????????????????????access_token
	 */
	@Override
	public void runTimerWeixinByHalfHour() throws Exception {
		System.out.println("................");
		System.out.println("??????token");
		String appId = getSysConfig().getWeixin_appId();
		String appSecret = getSysConfig().getWeixin_appSecret();
		WeixinAccessToken wot = AdvancedUtil.getAccessToken(appId, appSecret);
		getSysConfig().setWeixin_token(wot.getAccessToken());
		
		JSONObject json = new JSONObject();
		String jsapi_ticket = getSysConfig().getWeixin_jsapi_ticket();
		//??????ticket
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+ wot.getAccessToken() + "&type=jsapi";
	    json = WeixinUtil.httpRequest(url, "GET", null);
	    
	    if (json != null) {
	        jsapi_ticket = json.getString("ticket");
	        getSysConfig().setWeixin_jsapi_ticket(jsapi_ticket);
	    }
	    
        sysConfigDAO.update(getSysConfig());
        
        System.out.println("????????????");
        System.out.println("..................");

	}
	
	
	/**
	 * ????????????
	 * @param config
	 * @param order
	 * @param request
	 * @return
	 */
	private Map<String, String> weixinPayRefund(SysConfig config, OrderForm order) throws Exception { 
		SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();  
		parameterMap.put("appid", config.getWeixin_appId());
		String mch_id = config.getMch_id();
		parameterMap.put("mch_id", mch_id);  
		//?????????
		String nonceStr = PayCommonUtil.getRandomString(32);
		parameterMap.put("nonce_str", nonceStr);
		parameterMap.put("out_trade_no", order.getOrder_id());
		//     parameterMap.put("transaction_id", order.getOut_order_id());
		String out_refund_no = order.getUser_id() + CommUtil.formatTime("yyyyMMddhhmmssSSS",
						new Date());
		parameterMap.put("out_refund_no", out_refund_no);

		BigDecimal total = order.getTotalPrice().multiply(new BigDecimal(100));  
		java.text.DecimalFormat df=new java.text.DecimalFormat("0");  
		String totalFee = df.format(total);
		parameterMap.put("total_fee", totalFee); 
		parameterMap.put("refund_fee", totalFee); 
		parameterMap.put("refund_fee_type", "CNY");  

		//  parameterMap.put("total_fee", "1");   ????????????1????????????
		String sign = PayCommonUtil.createSign("UTF-8", parameterMap,config.getWeixin_mch_key());
		parameterMap.put("sign", sign); 
		System.out.println("sign : "+ sign);
		String requestXML = PayCommonUtil.getRequestXml(parameterMap);  
		System.out.println("????????????xml: " + requestXML);  
		Map<String, String> map = null;  
		String result = PayCommonUtil.executeBySslPost("https://api.mch.weixin.qq.com/secapi/pay/refund", requestXML,
				"D:/cert/apiclient_cert.p12", mch_id);
		System.out.println("????????????xml: " + result);  

		map = PayCommonUtil.doXMLParse(result);  
		return map;        
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????? ????????????????????????????????????try catch??????????????????????????????????????????
	 * 
	 * @return
	 * @throws Exception
	 */
	 @Transactional(rollbackFor = Exception.class)
	 public void runTimeOrder() throws Exception {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		params.put("update", 1);
		List<OrderForm> orderForms = this.orderFormDAO.query("select obj from OrderForm obj where obj.update_status=:update", params, -1, 10);
		if(orderForms.size() > 0){
			for(OrderForm order : orderForms){
				if(order.getOrder_status() == 30 && order.getUpdate_status() == 1){
					Map express_map = Json.fromJson(Map.class,
							order.getExpress_info());
					//??????ddu
					 String sendMsg = appendXmlContextTow(order.getShipCode(), CommUtil.null2String(express_map.get("express_company_mark")));//??????????????????
					 InputStreamReader isr = null;// ??????HTTP??????
				     BufferedReader inReader = null;
				     StringBuffer result = null;
				     OutputStream outObject = null;
				    
					 URL url = new URL("http://courier.ddu-express.com/api/webservice.php?wsdl");
					 HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(); //[JDK?????????Java.net.HttpURLConnection]
					 //???????????????????????????GET
					 httpConn.setRequestMethod("POST");
					 /**
			            *  ???????????????
			            *  ????????????(?????????http)?????????URLConnection??? ?????????HttpURLConnection,??????????????????????????????HttpURLConnection???????????????,????????????HttpURLConnection?????????API.??????: HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection; ]
			    		*  ??????HTTP??????????????????
			     	    *  ?????????????????????????????????????????????java?????? 
			            *  ??????????????????,???????????????????????????,???WEB?????????????????????????????????????????????java.io.EOFException) 
			            */
					httpConn.setRequestProperty("Content-Length",
				                    String.valueOf(sendMsg.getBytes().length));
		            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		            httpConn.setDoOutput(true);//???????????????httpUrlConnection????????????????????????post????????????????????????http??????????????????????????????true, ??????????????????false;
		            httpConn.setDoInput(true); //???????????????httpUrlConnection???????????????????????????true;
		            outObject = httpConn.getOutputStream();// ??????HTTP??????,??????getOutputStream??????????????????connect(???????????????????????????connect()????????? ????????????????????????????????????connect()?????????)
		            outObject.write(sendMsg.getBytes());
		            if (200 != (httpConn.getResponseCode())) {
		                throw new Exception("HTTP Request is not success, Response code is " + httpConn.getResponseCode());
		            }
		           
		            isr = new InputStreamReader(
		                    httpConn.getInputStream(), "utf-8"); // ??????HTTP???????????????????????????
		            inReader = new BufferedReader(isr);
		            result = new StringBuffer();
		            String inputLine;
		            while ((inputLine = inReader.readLine()) != null) {
		                result.append(inputLine);
		            }
		            String xmlResult = result.toString().replace("<", "<");
			        //????????????????????????1????????????
			        String responseCode = SOAPUtils.getXmlMessageByName(xmlResult, "responseCode");
			        if("1".equals(responseCode)){
			        	JSONArray jsonArray = dom4jXml(xmlResult);
			        	if(jsonArray != null){
			        		List<String> list = new ArrayList<String>();
			        		for(Object obj : jsonArray){
			                    JSONObject jo = (JSONObject) obj;
			                    String status = jo.get("status").toString();
			                    list.add(status);
			        		}
			        		if(list.contains("DELIVERED")){
			        			order.setUpdate_status(-1);
			        		}else{
			        			order.setUpdate_status(2);
			        		}
			        	}
			        	order.setLogistics_info(jsonArray.toString());
			        	this.orderFormDAO.update(order);
			        }
				}
			}
		}else{
			params.clear();
			params.put("update", 2);
			orderForms = this.orderFormDAO.query("select obj from OrderForm obj where obj.update_status=:update", params, -1, -1);
			if(orderForms.size() > 0){
				for(OrderForm order : orderForms){
					order.setUpdate_status(1);
					this.orderFormDAO.update(order);
				}
			}
		}
	}
	
	/**
	 * ??????ddu??????xml
	 * @param interfaceData
	 * @return
	 * @throws Exception
	 */
	public static JSONArray dom4jXml(String interfaceData) throws Exception{
        List<Element> elementList = null;
        List<Ddu> Ddus = null;
        try {
            SAXReader sr = new SAXReader();
            Document document = sr.read(new ByteArrayInputStream(interfaceData.getBytes()));
            Element root = document.getRootElement();
            elementList = root.elements();
            Ddus = new ArrayList();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        for (Element e : elementList) {
            // ???????????????????????????
            Element e1 = e.element("GetStatusDetailsResponse");
            Element e2 = e1.element("return");
            Element e3 = e2.element("responseArray");
            List<Element> elementList1 = e3.elements();
            for (Element el:elementList1){
                Ddu ddu = new Ddu();
                ddu.setTime(el.elementText("SDate"));
                ddu.setStatus(el.elementText("CStatus"));
                ddu.setLocation(el.elementText("SLocation"));
                ddu.setDetails(el.elementText("SDetails"));
                Ddus.add(ddu);
            }
        }
        	return JSONArray.fromObject(Ddus);
    }
		
	 private static String appendXmlContextTow(String BookingNumber, String CompanyCode) {
	        // ??????????????????
	    	List<String> list  = new ArrayList<String>();
	        StringBuffer stringBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com.foresee.top.service/\">\n" +
	                "  <soapenv:Body>\n" +
	                "  <GetStatusDetails>\n" +
	                "  <SaveProductResult>\n" +
	                "    <BookingNumber>" + BookingNumber + "</BookingNumber>\n" +
	                "    <CompanyCode>" + CompanyCode + "</CompanyCode>\n" +
	                "  </SaveProductResult>\n" +
	                "  </GetStatusDetails>\n" +
	                "  </soapenv:Body>\n" +
	                "</soapenv:Envelope>");
	        return stringBuffer.toString();
	    }
	
}
