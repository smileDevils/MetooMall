package com.metoo.module.app.view.web.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.metoo.core.annotation.EmailMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.GoodsSku;
import com.metoo.foundation.domain.Coupon;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GoodsLog;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.GoodsSpecification;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.PointRecord;
import com.metoo.foundation.domain.ShipAddress;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.StoreLog;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IBuyGiftService;
import com.metoo.foundation.service.ICombinPlanService;
import com.metoo.foundation.service.ICouponInfoService;
import com.metoo.foundation.service.ICouponService;
import com.metoo.foundation.service.IDeliveryAddressService;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsLogService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSkuService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;
import com.metoo.foundation.service.IGroupGoodsService;
import com.metoo.foundation.service.IGroupInfoService;
import com.metoo.foundation.service.IGroupLifeGoodsService;
import com.metoo.foundation.service.IIntegralGoodsOrderService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IMessageService;
import com.metoo.foundation.service.IOrderFormLogService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IPaymentService;
import com.metoo.foundation.service.IPayoffLogService;
import com.metoo.foundation.service.IPointRecordService;
import com.metoo.foundation.service.IPointService;
import com.metoo.foundation.service.IPredepositLogService;
import com.metoo.foundation.service.IShipAddressService;
import com.metoo.foundation.service.IStoreLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ITemplateService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.lucene.tools.LuceneVoTools;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.admin.tools.PaymentTools;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.manage.buyer.tools.CartTools;
import com.metoo.manage.delivery.tools.DeliveryAddressTools;
import com.metoo.manage.seller.tools.CombinTools;
import com.metoo.manage.seller.tools.StoreLogTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.view.web.tool.AppCartViewTools;
import com.metoo.module.app.view.web.tool.AppGoodsViewTools;
import com.metoo.module.app.view.web.tool.AppobileTools;
import com.metoo.msg.MsgTools;
import com.metoo.pay.tools.PayTools;
import com.metoo.view.web.tools.ActivityViewTools;
import com.metoo.view.web.tools.BuyGiftViewTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.GroupViewTools;
import com.metoo.view.web.tools.IntegralViewTools;
import com.metoo.view.web.tools.StoreViewTools;

/**
 * <p>
 * Title: AppCartViewActionV2.java
 * </p>
 * 
 * <p>
 * Description: V2 ?????????????????????
 * </p>
 * 
 * @author 46075
 *
 */
@Controller
@RequestMapping("app/v2/")
public class AppCartViewActionV2 {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private AppGoodsViewTools appGoodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IIntegralGoodsOrderService igorderService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private ActivityViewTools activityTools;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private DeliveryAddressTools DeliveryAddressTools;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private AppCartViewTools appCartViewTools;
	@Autowired
	private StoreLogTools storeLogTools;
	@Autowired
	private IStoreLogService storeLogService;
	@Autowired
	private IPointService pointService;
	@Autowired
	private AppobileTools mobileTools;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IGoodsSkuService goodsSkuService;
	@Autowired
	private IPointRecordService pointRecordService;
	@Autowired
	private IIntegralLogService intergralLogService;

	/**
	 * @descript V2?????????????????????????????????
	 * @param request
	 * @param response
	 * @param load_class
	 * @param token
	 * @param visitor_id
	 * @param language
	 * @return
	 * 
	 */
	@RequestMapping("cartList.json")
	@ResponseBody
	public String goodsCartLoadV2(HttpServletRequest request, HttpServletResponse response, String load_class,
			@RequestParam(value = "token") String token, String visitor_id, String language) {
		Result result = null;
		Map<String, Object> goods_cart_map = new HashMap<String, Object>();
		Date date = new Date();
		User user = null;
		if (!"".equals(CommUtil.null2String(token))) {
			user = this.userService.getObjByProperty(null, "app_login_token", token);
		}
		// List<GoodsCart> carts = this.metoo_cart_calc(user); ?????????????????? ??????????????????????????????
		String cart_session_id = "";
		if (visitor_id != null && !"".equals(visitor_id)) {
			cart_session_id = visitor_id;
		} else {
			Cookie[] cookies = request.getCookies();
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
			// cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<GoodsCart> cartsList = this.appCartViewTools.cartListCalc(request, user, cart_session_id);// ???????????????????????????????????????
		if (cartsList.size() > 0) {
			Set<Long> buyGiftSet = new HashSet<Long>();
			Set<Long> storeIds = new HashSet<Long>();
			List<GoodsCart> native_goods = new ArrayList<GoodsCart>();// ??????????????????
			Map<Long, List<GoodsCart>> enoughReduceMap = new HashMap<Long, List<GoodsCart>>();// ??????????????????enough_reduce
			List<GoodsCart> point_goods = new ArrayList<GoodsCart>();// ?????????????????????
																		// ????????????????????????????????????
			List<GoodsCart> gameTreeCart = new ArrayList<>();
			List<GoodsCart> gameDebrisCart = new ArrayList<>();
			Map<Long, String> erString = new HashMap<Long, String>();
 			for (GoodsCart cart : cartsList) {
				if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
					BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
					if (bg.getBeginTime().before(date)) {
						buyGiftSet.add(cart.getGoods().getBuyGift_id());
					} else {
						native_goods.add(cart);
					}
				} else// if (true) {// cart.getCart_type().equals("1") ||
									// cart.getCart_type().equals("2")
					if (cart.getGoods().getPoint() == 1 && cart.getGoods().getPoint_status() == 10) {
						int expiration = this.configService.getSysConfig().getExpiration_point();
						/*
						 * Calendar calendar = Calendar.getInstance();
						 * calendar.add(Calendar.DAY_OF_YEAR, -expiration);
						 * calendar.setTime(new Date()); //
						 * calendar.add(Calendar.DAY_OF_YEAR, expiration);
						 * calendar.add(Calendar.MINUTE, -2);
						 * System.out.println(cart.getAddTime().getTime());
						 * System.out.println(calendar.getTime().getTime());
						 * System.out.println(CommUtil.subtract(cart.getAddTime(
						 * ).getTime(), calendar.getTime().getTime()) <= 0);
						 */
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, -expiration);
						if (CommUtil.subtract(cart.getAddTime().getTime(), calendar.getTime().getTime()) <= 0) { // ???????????????????????????????????????????????????
							cart.setCart_status(2);
							this.goodsCartService.update(cart);
					//	}
					}
					native_goods.add(0, cart);
				} else if (cart.getGoods().getEnough_reduce() == 1) {// ?????????
					String er_id = cart.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
					if (er.getErstatus() == 10 && er.getErbegin_time().before(date)) {
						if (enoughReduceMap.containsKey(er.getId())) {
							enoughReduceMap.get(er.getId()).add(cart);
						} else {
							List<GoodsCart> list = new ArrayList<GoodsCart>();
							list.add(cart);
							enoughReduceMap.put(er.getId(), list);
							// Map map = (Map)
							// Json.fromJson(er.getEr_json());
							/*
							 * double k = 0; String str = ""; for (Object key :
							 * map.keySet()) { if (k == 0) { k =
							 * Double.parseDouble(key.toString()); str =
							 * "The activity products buy "+k+
							 * " AED, you can enjoy the discount"; } if
							 * (Double.parseDouble(key.toString()) < k) { k =
							 * Double.parseDouble(key.toString()); str =
							 * "The activity product buy "+k+
							 * " AED, you can enjoy the discount"; } }
							 */
							erString.put(er.getId(), er.getEr_json());
						}
					} else {
						native_goods.add(cart);
					}
				}else if(cart.getCart_type().equals("2")) {
					gameDebrisCart.add(cart);
				}else if(cart.getCart_type().equals("5")){
					gameTreeCart.add(cart);
				} else {
					native_goods.add(cart);
				}
				if (!storeIds.contains(cart.getGoods().getGoods_store().getId())) {
					storeIds.add(cart.getGoods().getGoods_store().getId());
				}
			}
			List<GoodsCart> erCarts = new ArrayList<GoodsCart>();// ???????????????????????????(?????????)
			List<List<Map<String, Object>>> enoughReduceGoodsList = new ArrayList<List<Map<String, Object>>>();
			for (Long key : enoughReduceMap.keySet()) {
				erCarts = enoughReduceMap.get(key);
				enoughReduceGoodsList.add(this.appCartViewTools.cartGoods(erCarts, storeIds, erString, key, language));
			}
			goods_cart_map.put("ermaps", enoughReduceGoodsList);
			

			// ?????????????????????
			/*
			 * if (buyGiftSet.size() > 0) {// ???????????????????????????(?????????) Map<Long,
			 * List<GoodsCart>> buyGiftMap = new HashMap<Long,
			 * List<GoodsCart>>(); for (Long id : buyGiftSet) {
			 * buyGiftMap.put(id, new ArrayList<GoodsCart>()); } for (GoodsCart
			 * cart : cartsList) { if
			 * (cart.getGoods().getOrder_enough_give_status() == 1 &&
			 * cart.getGoods().getBuyGift_id() != null) { if
			 * (buyGiftMap.containsKey(cart.getGoods().getBuyGift_id())) {
			 * buyGiftMap.get(cart.getGoods().getBuyGift_id()).add(cart); } } }
			 * List<GoodsCart> buyGiftLis = new ArrayList<GoodsCart>(); for
			 * (Long buyGiftKey : buyGiftMap.keySet()) { buyGiftLis =
			 * buyGiftMap.get(buyGiftKey); List<Map<String, Object>>
			 * buyGiftGoodsList = new ArrayList<Map<String, Object>>(); for
			 * (GoodsCart obj : buyGiftLis) { Map<String, Object> map = new
			 * HashMap<String, Object>(); map.put("goods_cart_id", obj.getId());
			 * map.put("goods_main_photo", obj.getGoods().getGoods_main_photo()
			 * == null ? "" :
			 * this.configService.getSysConfig().getImageWebServer() + "/" +
			 * obj.getGoods().getGoods_main_photo().getPath() + "/" +
			 * obj.getGoods().getGoods_main_photo().getName());
			 * map.put("goods_id", obj.getGoods().getId());
			 * map.put("goods_name", obj.getGoods().getGoods_name());
			 * map.put("goods_type", obj.getGoods().getGoods_type());
			 * map.put("goods_inventory", obj.getGoods().getGoods_inventory());
			 * map.put("goods_curren_price", obj.getPrice());
			 * map.put("goods_store_price", obj.getGoods().getStore_price());
			 * map.put("goods_spec", obj.getSpec_info() == null ? "" :
			 * obj.getSpec_info()); map.put("goods_status",
			 * obj.getGoods().getGoods_status()); map.put("store_status",
			 * obj.getGoods().getGoods_store().getStore_status());
			 * map.put("goods_collect", obj.getGoods().getGoods_collect()); if
			 * (obj.getGoods().getGoods_store() != null) { map.put("store_name",
			 * obj.getGoods().getGoods_store().getStore_name());
			 * map.put("store_id", obj.getGoods().getGoods_store().getId());
			 * map.put("store_logo",
			 * obj.getGoods().getGoods_store().getStore_logo() == null ? "" :
			 * this.configService.getSysConfig().getImageWebServer() + "/" +
			 * obj.getGoods().getGoods_store().getStore_logo().getPath() + "/" +
			 * obj.getGoods().getGoods_store().getStore_logo().getName()); }
			 * buyGiftGoodsList.add(map); }
			 * goods_cart_map.put("buyGiftGoodsList", buyGiftGoodsList);
			 * 
			 * BuyGift buyGift =
			 * goodsViewTools.query_buyGift(CommUtil.null2String(buyGiftKey));
			 * List<Map> bg_goodslist = new ArrayList<Map>(); for (Map bgt :
			 * CommUtil.Json2List(buyGift.getGift_info())) { Map bgtmap = new
			 * HashMap(); bgtmap.put("storegoods_count",
			 * bgt.get("storegoods_count"));// [????????????100????????????????????????100 //
			 * storegoods_count???1?????????????????????????????????????????? // ????????????200??????????????????200 //
			 * ???????????????1???????????????????????????199??????????????????199,???????????????????????????????????????goods_count]
			 * bgtmap.put("goods_id", bgt.get("goods_id"));
			 * bgtmap.put("goods_name", bgt.get("goods_name"));
			 * bgtmap.put("goods_price", bgt.get("goods_price"));
			 * bgtmap.put("goods_main_photo",
			 * this.configService.getSysConfig().getImageWebServer() + "/" +
			 * bgt.get("goods_main_photo")); bg_goodslist.add(bgtmap); }
			 * goods_cart_map.put("bg_goods_info", bg_goodslist); } }
			 */
			Map<String, List<GoodsCart>> separate_carts = this.appCartViewTools.separateCombin(native_goods);// ????????????????????????????????????????????????
			List<GoodsCart> normalGoodsCart = (List<GoodsCart>) separate_carts.get("normal");// ???????????????????????????
			List<Map<String, Object>> normalList = this.appCartViewTools.cartGoods(normalGoodsCart, storeIds, null, null,
					language);
			goods_cart_map.put("normalmap", normalList);
			// ????????????
			List<Map<String, Object>> gameDebrisMaps = this.appCartViewTools.cartGoods(gameDebrisCart, storeIds, null, null,
					language);
			goods_cart_map.put("gameDebrisMaps", gameDebrisMaps);
			// ????????????
			List<Map<String, Object>> gameTreeMaps = this.appCartViewTools.cartGoods(gameTreeCart, storeIds, null, null,
					language);
			goods_cart_map.put("gameTreeMaps", gameTreeMaps);
			/*
			 * List<Map> pointlist = this.appCartViewTools.queryGoods(point_goods,
			 * storeIds, null, null); goods_cart_map.put("pointlist",
			 * pointlist);
			 */
			/*
			 * List<GoodsCart> combin = (List<GoodsCart>)
			 * separate_carts.get("combin");// ???????????????????????????
			 * //[????????????????????????????????????"combin"] List<Map> combinlist =
			 * this.appCartViewTools.queryGoods(combin, storeIds);
			 * goods_cart_map.put("combinlist", combinlist);
			 * goods_cart_map.put("cart_num", combin.size());
			 */
		}
		goods_cart_map.put("cart_session_id", cart_session_id);
		goods_cart_map.put("cart_num", cartsList.size());
		goods_cart_map.put("order_free_express_price", this.configService.getSysConfig().getEnoughfree_price());
		result = new Result(4200, "Success", goods_cart_map);
		return Json.toJson(result, JsonFormat.compact());
	}

	@RequestMapping("addToCart.json")
	@ResponseBody
	public String addToCart(HttpServletRequest request, HttpServletResponse response, String goods_id, String count,
			String color, String gsp, String cart_type, String visitor_id, Integer game, String token) {
		int code = -1;
		String msg = "";
		User user = null;
		if (!"".equals(token)) {
			user = this.userService.getObjByProperty(null, "app_login_token", token);
		}
		Map map = new HashMap();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		boolean point = true;
		boolean point_record = true;
		boolean point_number = true;
		if (obj.getPoint() == 1 && obj.getPoint_status() == 10) {
			if (user == null) {
				point = false;
			} else {
				Map params = new HashMap();
				params.put("goods_id", obj.getId());
				params.put("user_id", user.getId());
				List<PointRecord> PointRecords = this.pointRecordService.query(
						"SELECT obj FROM PointRecord obj WHERE obj.goods_id=:goods_id and obj.user_id=:user_id", params,
						-1, -1);
				if (PointRecords.size() > 0) {
					point_record = false;
				} else if (user.getPointNum() < obj.getPointNum()) {
					point_number = false;
				}
			}
		}
		if (point) {
			if (point_record) {
				boolean addFlag = false;
				/*
				 * if (user != null && user.getPointNum() >= obj.getPointNum())
				 * { addFlag = true; } else if (user == null) { addFlag = true;
				 * } else
				 */ if (point_number) {
					if (obj != null && obj.getGoods_status() == 0 || obj.getGoods_status() == 4) {
						if (obj.getGoods_store().getStore_status() == 15) {
							// ?????????????????????????????????????????????????????????
							if ("".equals(color) || "".equals(gsp)) {
								// ??????????????????
								Map<String, String> gspMap = generic_default_gsp(obj);
								color = gspMap.get("color");
								gsp = gspMap.get("gsp");
							}
							// ??????????????????
							int goods_inventory = 0;
							int oversea_inventory = 0;
							/*
							 * int goods_inventory = CommUtil
							 * .null2Int(this.appCartViewTools.
							 * generic_default_info_color(obj, gsp,
							 * color).get("count"));
							 */
							// ???????????????????????????
							if (obj.getInventory_type().equals("all")) {
								goods_inventory = obj.getGoods_inventory();
								oversea_inventory = obj.getOversea_inventory();
							} else {
								Map<String, Object> goods = this.appCartViewTools.generic_default_info_color(obj, gsp,
										color);
								goods_inventory = CommUtil.null2Int(goods.get("count"));
								oversea_inventory = CommUtil.null2Int(goods.get("oversea_inventory"));
							}
							if (CommUtil.subtract(goods_inventory, count) >= 0
									|| CommUtil.subtract(oversea_inventory, count) >= 0) {
								if (obj.getInventory_type().equals("spec") && "".equals(color) && "".equals(gsp)) {
									code = 4400;
									msg = "Please select specifications";
								} else {
									String cart_session_id = "";
									/*if (null != visitor_id && !"".equals(visitor_id)) {
										cart_session_id = visitor_id;
									} else {
										Cookie[] cookies = request.getCookies();
										if (cookies != null) {
											for (Cookie cookie : cookies) {
												if (cookie.getName().equals("cart_session_id")) {
													cart_session_id = CommUtil.null2String(cookie.getValue());
												}
											}
										}
									}*/
									Cookie[] cookies = request.getCookies();
									if (cookies != null) {
										for (Cookie cookie : cookies) {
											if (cookie.getName().equals("cart_session_id")) {
												cart_session_id = CommUtil.null2String(cookie.getValue());
											}
										}
									}
									
									if (null == cart_session_id || cart_session_id.equals("")) {
										cart_session_id = UUID.randomUUID().toString();
										Cookie cookie = new Cookie("cart_session_id", cart_session_id);
										// cookie.setDomain(CommUtil.generic_domain(request));
										response.addCookie(cookie);
									}

									List<GoodsCart> cartsList = new ArrayList<GoodsCart>();// ?????????????????????
									List<GoodsCart> cookieCartList = new ArrayList<GoodsCart>();// ????????????????????????
																								// pc
									// List<GoodsCart> mobileCartList = new
									// ArrayList<GoodsCart>(); // ???????????????????????? app
									List<GoodsCart> userCartList = new ArrayList<GoodsCart>();// ????????????????????????
									/*
									 * if (!"".equals(token)) { user =
									 * this.userService.getObjByProperty(null,
									 * "app_login_token", token); }
									 */
									Map<String, Object> params = new HashMap<String, Object>();
									if (null != user) {
										// ?????????????????????????????????
										if (!"".equals(cart_session_id)) {
											params.clear();
											params.put("cart_session_id", cart_session_id);
											params.put("cart_status", 0);
											params.put("deleteStatus", 0);
											cookieCartList = this.goodsCartService.query(
													"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status and obj.deleteStatus=:deleteStatus",
													params, -1, -1);
											// ????????????????????????????????????????????????
											if (user.getStore() != null) {
												for (GoodsCart cart : cookieCartList) {
													if (cart.getGoods().getGoods_store().getId()
															.equals(user.getStore().getId())) {
														this.goodsCartService.delete(cart.getId());
													}
												}
											}
											// ??????????????????????????????
											params.clear();
											params.put("user_id", user.getId());
											params.put("cart_status", 0);
											params.put("deleteStatus", 0);
											userCartList = this.goodsCartService.query(
													"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.deleteStatus=:deleteStatus",
													params, -1, -1);
										} else {
											params.clear();
											params.put("user_id", user.getId());
											params.put("cart_status", 0);
											params.put("deleteStatus", 0);
											userCartList = this.goodsCartService.query(
													"select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status and obj.deleteStatus=:deleteStatus",
													params, -1, -1);
										}
									} else {
										if (!"".equals(cart_session_id)) {
											params.clear();
											params.put("cart_session_id", cart_session_id);
											params.put("cart_status", 0);
											params.put("deleteStatus", 0);
											cookieCartList = this.goodsCartService.query(
													"select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status and obj.deleteStatus=:deleteStatus",
													params, -1, -1);
										}
									}

									// ?????????????????????cookie?????????????????????
									if (user != null) {
										for (GoodsCart userCart : userCartList) {
											cartsList.add(userCart);
										}
										if (userCartList.size() > 0) {
											for (GoodsCart cookieCart : cookieCartList) {
												for (GoodsCart userCart : userCartList) {
													if (userCart.getGoods().getId()
															.equals(cookieCart.getGoods().getId())) {
														boolean colorFlag = false;
														boolean gspFlag = false;
														if (null != cookieCart.getColor()
																&& !"".equals(cookieCart.getColor())) {
															if (cookieCart.getColor().equals(userCart.getColor())) {
																colorFlag = true;
															}
														}
														if (cookieCart.getSpec_info().equals(userCart.getSpec_info())) {
															gspFlag = true;
														}
														if (colorFlag && gspFlag) {
															this.goodsCartService.delete(cookieCart.getId());
														}

														if (!colorFlag || !gspFlag) {
															// ???cookieCart?????????userCart
															cookieCart.setCart_session_id(null);
															cookieCart.setUser(user);
															this.goodsCartService.update(cookieCart);
															cartsList.add(cookieCart);
														}
													}
												}
											}
										} else {
											for (GoodsCart cookieCart : cookieCartList) {
												cookieCart.setCart_session_id(null);
												cookieCart.setUser(user);
												this.goodsCartService.update(cookieCart);
												cartsList.add(cookieCart);
											}
										}
									} else {
										for (GoodsCart cookieCart : cookieCartList) {
											cartsList.add(cookieCart);
										}
									}
									boolean gspFlag = false;
									boolean colorFlag = false;
									boolean gameFlag = true;
									String[] goods_gsp = CommUtil.null2String(gsp).split(",");
									if (game <= 0) {
										Arrays.sort(goods_gsp);
										for (GoodsCart goodsCart : cartsList) {
											if (goodsCart.getGoods().getId().equals(obj.getId())) {
												if (goods_gsp != null && goods_gsp.length > 0
														&& goodsCart.getGsps().size() > 0 && null != color
														&& !"".equals(color) || !"".equals(goodsCart.getColor())) {
													if (goods_gsp != null && goods_gsp.length > 0
															&& goodsCart.getGsps().size() > 0) {
														String[] gc_gsp = new String[goodsCart.getGsps().size()];
														for (int i = 0; i < goodsCart.getGsps().size(); i++) {
															gc_gsp[i] = goodsCart.getGsps().get(i) != null
																	? goodsCart.getGsps().get(i).getId().toString()
																	: "";
														}
														Arrays.sort(gc_gsp);
														if (obj.getId().equals(goodsCart.getGoods().getId())
																&& gc_gsp.equals(goods_gsp)) {
															gspFlag = true;
														}
													}
													if (null != color && !"".equals(color)
															|| !"".equals(goodsCart.getColor())) {
														if (color.toString().equals(goodsCart.getColor())) {
															colorFlag = true;
														}
													}
												} else {
													colorFlag = true;
													gspFlag = true;
												}
											}
										}
									} else {
										// ???????????????????????????????????????
										params.clear();
										params.put("user_id", user.getId());
										params.put("goods_id", CommUtil.null2Long(goods_id));
										params.put("cart_type", "2");
										List<GoodsCart> goodsCarts = this.goodsCartService.query(
												"select obj from GoodsCart obj where obj.user.id=:user_id and obj.goods.id=:goods_id and obj.cart_type=:cart_type",
												params, -1, -1);
										if(goodsCarts.size() > 0){
											gameFlag = false;
										}else{
											GoodsCart goodsCart = goodsCarts.get(0);
											goodsCart.setCount(goodsCart.getCount() + 1);
											this.goodsCartService.update(goodsCart);
										}
									}
									// ???????????????????????????????????????????????????????????? ??????????????? L???????????? M??????????????????????????????????????????????????????+1
									if (!gspFlag || !colorFlag && gameFlag) {
										GoodsCart goodsCart = new GoodsCart();
										goodsCart.setAddTime(new Date());
										if (obj.getPoint() == 1 && obj.getPoint_status() == 10) {
											goodsCart.setPrice(CommUtil.null2BigDecimal(0));
											goodsCart.setCart_type("2");
											int pint_num = user.getPointNum() - obj.getPointNum();
											user.setPointNum(pint_num);
											PointRecord pr = new PointRecord();
											pr.setAddTime(new Date());
											pr.setGoods_id(obj.getId());
											pr.setUser_id(user.getId());
											pr.setPoint_num(obj.getPointNum());
											pr.setPay_time(new Date());
											pr.setRemaining_num(pint_num);
											this.pointRecordService.save(pr);
											this.userService.update(user);
										} else {
											BigDecimal goods_price = CommUtil.null2BigDecimal(
													this.appCartViewTools.generic_default_info_color(obj, gsp, color)
															.get("goods_current_price"));
											goodsCart.setPrice(goods_price);
											if (game != null && game == 1) {
												goodsCart.setCart_type("2");
											} else {
												goodsCart.setCart_type("0");
											}

										}
										goodsCart.setCart_form(cart_type);
										goodsCart.setCount(Integer.parseInt(count) == 0 ? 1 : Integer.parseInt(count));
										goodsCart.setColor(color);
										goodsCart.setCart_gsp(gsp);
										goodsCart.setGoods(obj);

										// ????????????????????????????????????
										GoodsLog goodsLog = goodsViewTools.getTodayGoodsLog(obj.getId());
										goodsLog.setGoods_cart(goodsLog.getGoods_cart() + 1);
										this.goodsLogService.update(goodsLog);
										if (null == user) {
											goodsCart.setCart_session_id(cart_session_id);
										} else {
											goodsCart.setUser(user);
										}
										String spec_info = "";
										for (String gspId : goods_gsp) {
											GoodsSpecProperty goodsSpecProperty = this.goodsSpecPropertyService
													.getObjById(CommUtil.null2Long(gspId));
											goodsCart.getGsps().add(goodsSpecProperty);
											if (goodsSpecProperty != null) {
												spec_info = goodsSpecProperty.getSpec().getName() + ":"
														+ goodsSpecProperty.getValue() + "," + spec_info;
											}
										}
										goodsCart.setSpec_info(spec_info);
										this.goodsCartService.save(goodsCart);
									}
									code = 4200;
									msg = "Success";
									map.put("cart_session_id", cart_session_id);
								}
							} else {
								code = 4206;
								msg = "Goods in short stock";
							}
						} else {
							code = 4208;
							msg = "The merchandise is off the shelves";
						}
					} else {
						code = 4403;
						msg = "The product ID is empty";
					}
				} else {
					code = 4211;
					msg = "Invite number unqualified";
				}
			} else {
				code = 4212;
				msg = "You can only change them once per person";
			}
		} else {
			code = -100;
			msg = "token Invalidation";
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	}

	public Map<String, String> generic_default_gsp(Goods goods) {
		Map map = new HashMap();
		String gsp = "";
		// ????????????
		Set<Map<String, Object>> colorMaps = this.appGoodsViewTools.goodsColor(CommUtil.null2String(goods.getId()));
		if (colorMaps.size() > 0) {
			for (Map<String, Object> colorMap : colorMaps) {
				map.put("color", colorMap.get("value"));
				break;
			}
		}
		List<GoodsSpecification> specs = this.goodsViewTools.generic_spec(CommUtil.null2String(goods.getId()));
		for (GoodsSpecification spec : specs) {
			// System.out.println(spec.getName());
			for (GoodsSpecProperty prop : goods.getGoods_specs()) {
				if (prop.getSpec().getId().equals(spec.getId())) {
					gsp = prop.getId() + "," + gsp;
					break;
				}
			}
		}
		map.put("gsp", gsp);
		return map;
	};

	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param count
	 * @param gcs
	 * @param gift_id
	 * @param visitor_id
	 * @param token
	 * @descript ?????????????????????
	 */
	@RequestMapping("goodsCountAdjust.json")
	public void goodsCountAdjust(HttpServletRequest request, HttpServletResponse response, String gc_id, String count,
			String gcs, String gift_id, String visitor_id, String token) {
		Result result = null;
		Map params = new HashMap();
		User user = null;
		if (!"".equals(token)) {
			user = this.userService.getObjByProperty(null, "app_login_token", token);
		}
		String cart_session_id = "";
		if (null != visitor_id && !"".equals(visitor_id)) {
			cart_session_id = visitor_id;
		} else {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("cart_session_id")) {
						cart_session_id = CommUtil.null2String(cookie.getValue());
					}
				}
			}
		}
		if (null != cart_session_id || cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			// cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<GoodsCart> carts = this.appCartViewTools.cartListCalc(request, user, cart_session_id);
		Map map = new HashMap();
		int code = 4200;// 0?????????????????????2??????????????????,3????????????????????????
		String msg = "Success";
		double gc_price = 0.00;// ??????GoodsCart?????????
		double priceDifference = 0.00;// ????????????????????????????????????????????????
		double total_price = 0.00;// ??????????????????
		String cart_type = "";// ???????????????????????????
		String order_enough_reduce_id = "";
		Goods goods = null;
		int temp_count = 1;
		if (CommUtil.null2Int(count) > 1) {
			temp_count = CommUtil.null2Int(count);
		}
		GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
		if (gc != null) {
			if (CommUtil.null2String(temp_count).length() <= 9) {
				if (gc.getId().toString().equals(gc_id)) {
					cart_type = CommUtil.null2String(gc.getCart_type());
					goods = gc.getGoods();
					if (cart_type.equals("0") || cart_type.equals("1") || cart_type.equals("gg")) {
						// ?????????????????????
						/*
						 * if (cart_type.equals("app") ||
						 * cart_type.equals("web") || cart_type.equals("gg"))
						 * {// ?????????????????????
						 */ if (goods.getGroup_buy() == 2) {// ??????????????????
							GroupGoods gg = new GroupGoods();
							for (GroupGoods gg1 : goods.getGroup_goods_list()) {
								if (gg1.getGg_goods().getId().equals(goods.getId())) {
									gg = gg1;
									break;
								}
							}
							if (gg.getGg_count() >= CommUtil.null2Int(temp_count)) {
								gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(gg.getGg_price())));
								gc_price = CommUtil.mul(gg.getGg_price(), temp_count);
								gc.setCount(CommUtil.null2Int(temp_count));
								this.goodsCartService.update(gc);
							} else {
								if (gg.getGg_count() == 0) {
									gc.setCount(0);
									this.goodsCartService.update(gc);
								}
								code = 3;
							}
						} else if (goods.getActivity_status() == 2) {// ??????????????????
							if (user != null) {
								gc_price = CommUtil.mul(gc.getPrice(), temp_count);
							}
						} else {
							String gsp = "";
							for (GoodsSpecProperty gs : gc.getGsps()) {
								gsp = gs.getId() + "," + gsp;
							}
							int inventory = goods.getGoods_inventory();
							if (("spec").equals(goods.getInventory_type())) {
								inventory = (int) this.appCartViewTools
										.generic_default_info_color(goods, gsp, gc.getColor()).get("count");
							}
							if (inventory >= CommUtil.null2Int(temp_count)
									&& CommUtil.null2String(temp_count).length() <= 9
									&& gc.getGoods().getGroup_buy() != 2) {
								if (gc.getId().toString().equals(gc_id)) {
									gc.setCount(CommUtil.null2Int(temp_count));
									this.goodsCartService.update(gc);
									gc_price = CommUtil.mul(gc.getPrice(), temp_count);
								}
							} else {
								if (inventory == 0) {
									gc.setCount(0);
									this.goodsCartService.update(gc);
								}
								code = 4206;
								msg = "Goods in short stock";
							}
						}
						/*
						 * if (cart_type.equals("combin") && gc.getCombin_main()
						 * == 1) {// ????????????????????? ????????????????????? if
						 * (goods.getGoods_inventory() >=
						 * CommUtil.null2Int(count)) {
						 * gc.setCount(CommUtil.null2Int(count));
						 * this.goodsCartService.update(gc); String
						 * suit_all_price = "0.00"; GoodsCart suit = gc; Map
						 * suit_map = (Map)
						 * Json.fromJson(suit.getCombin_suit_info());
						 * suit_map.put("suit_count", CommUtil.null2Int(count));
						 * suit_all_price =
						 * CommUtil.formatMoney(CommUtil.mul(CommUtil.null2Int(
						 * count),
						 * CommUtil.null2Double(suit_map.get("plan_goods_price")
						 * ))); suit_map.put("suit_all_price",
						 * suit_all_price);// ??????????????????=????????????*?????? String new_json =
						 * Json.toJson(suit_map, JsonFormat.compact());
						 * suit.setCombin_suit_info(new_json);
						 * suit.setCount(CommUtil.null2Int(count));
						 * this.goodsCartService.update(suit); gc_price =
						 * CommUtil.null2Double(suit_all_price); } else { if
						 * (goods.getGoods_inventory() == 0) { gc.setCount(0);
						 * this.goodsCartService.update(gc); } code = 2; } }
						 */
					}
					// ???????????????????????????????????? ?????????????????????
					/*
					 * if (gift_id != null) { BuyGift bg =
					 * this.buyGiftService.getObjById(CommUtil.null2Long(gift_id
					 * )); Set<Long> bg_ids = new HashSet<Long>(); if (bg !=
					 * null) { bg_ids.add(bg.getId()); } List<GoodsCart> g_carts
					 * = new ArrayList<GoodsCart>(); if
					 * (CommUtil.null2String(gcs).equals("")) { for (GoodsCart
					 * gCart : carts) { if
					 * (gCart.getGoods().getOrder_enough_give_status() == 1 &&
					 * gCart.getGoods().getBuyGift_id() != null) {
					 * bg_ids.add(gCart.getGoods().getBuyGift_id()); } } g_carts
					 * = carts; } else { String[] gc_ids = gcs.split(","); for
					 * (String g_id : gc_ids) { GoodsCart goodsCart =
					 * this.goodsCartService
					 * .getObjById(CommUtil.null2Long(g_id)); if (goodsCart !=
					 * null &&
					 * goodsCart.getGoods().getOrder_enough_give_status() == 1
					 * && goodsCart.getGoods().getBuyGift_id() != null) {
					 * bg_ids.add(goodsCart.getGoods().getBuyGift_id());
					 * g_carts.add(goodsCart); } } } Map<Long, List<GoodsCart>>
					 * gc_map = new HashMap<Long, List<GoodsCart>>(); for (Long
					 * id : bg_ids) { gc_map.put(id, new
					 * ArrayList<GoodsCart>()); } for (GoodsCart cart : g_carts)
					 * { if (cart.getGoods().getOrder_enough_give_status() == 1
					 * && cart.getGoods().getBuyGift_id() != null) { for
					 * (Map.Entry<Long, List<GoodsCart>> entry :
					 * gc_map.entrySet()) { if
					 * (cart.getGoods().getBuyGift_id().equals(entry.getKey()))
					 * { entry.getValue().add(cart); } } } } List<String>
					 * enough_bg_ids = new ArrayList<String>(); for
					 * (Map.Entry<Long, List<GoodsCart>> entry :
					 * gc_map.entrySet()) { BuyGift buyGift =
					 * this.buyGiftService.getObjById(entry.getKey()); //
					 * ??????????????????????????????????????????????????? List<GoodsCart> arrs =
					 * entry.getValue(); BigDecimal bd = new BigDecimal("0.00");
					 * for (GoodsCart arr : arrs) { bd =
					 * bd.add(BigDecimal.valueOf(CommUtil.mul(arr.getPrice(),
					 * arr.getCount()))); } if
					 * (bd.compareTo(buyGift.getCondition_amount()) >= 0) {
					 * enough_bg_ids.add(buyGift.getId().toString()); } }
					 * map.put("bg_ids", enough_bg_ids); }
					 */
				}
			} else {
				code = 4206;
				msg = "Goods in short stock";
			}
			map.put("count", gc.getCount());
		}
		total_price = this.appCartViewTools.calCartPrice(carts, gcs);
		Map price_map = this.appCartViewTools.calEnoughReducePrice(carts, gcs);
		List enoughReduce = this.appCartViewTools.calcActivityPricedifference(gcs);
		map.put("enoughReduce", enoughReduce);
		Map<Long, String> erMap = (Map<Long, String>) price_map.get("erString");
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("enough_reduce_price", CommUtil.formatMoney(price_map.get("reduce")));
		map.put("before", CommUtil.formatMoney(price_map.get("all")));
		for (long k : erMap.keySet()) {
			map.put("erString" + k, erMap.get(k));
		}
		double total = this.appCartViewTools.calCartPrice_free(carts, gcs);
		map.put("total_test", total);
		// ???????????????
		double subsidy_price = 0.0;
		if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
			double enoughfree_price = this.configService.getSysConfig().getEnoughfree_price();
			if (CommUtil.subtract(total, enoughfree_price) <= 0) {
				subsidy_price = CommUtil.subtract(enoughfree_price, total);
			}
		}
		map.put("subsidy_price", subsidy_price);
		result = new Result(code, msg, map);
		this.send_json(Json.toJson(result, JsonFormat.compact()), response);
	}

	/**
	 * @descript ???????????????????????? [?????????????????????]
	 * @param request
	 * @param response
	 * @param gcs
	 * @param giftids
	 * @param token
	 * @param language
	 * @return
	 * 
	 */
	@RequestMapping("cart_order.json")
	@ResponseBody
	public String cart_order(HttpServletRequest request, HttpServletResponse response, String gcs, String giftids,
			String token, String language, Boolean voucher) {
		Result result = null;
		int code = -1;
		String msg = "";
		Map cartMap = new HashMap();
		if (CommUtil.null2String(token).equals("")) {
			result = new Result(-100, "token Invalidation");
		} else {
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user == null) {
				result = new Result(-100, "token Invalidation");
			} else {
				if (CommUtil.null2String(gcs).equals("")) {
					code = 4400;
					msg = "parameter error,gcs is null";
				} else {
					// ??????
					List<GoodsCart> goodsCarts = this.appCartViewTools.cart_calc(user);
					boolean flag = true;
					if (goodsCarts.size() > 0) {
						for (GoodsCart goodsCart : goodsCarts) {
							if (!goodsCart.getUser().getId().equals(user.getId())) {
								flag = false;
								break;
							}
						}
					}
					boolean goods_cod = true;// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
					int tax_invoice = 1;// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
					if (flag && goodsCarts.size() > 0) {
						Map params = new HashMap();
						params.put("user_id", user.getId());
						params.put("defaulr_val", 1);
						List<Address> addresses = this.addressService.query(
								"select obj from Address obj where obj.user.id=:user_id and obj.default_val=:defaulr_val",
								params, -1, -1);
						if (addresses.size() > 0) {
							List<Map> addressMap = this.appCartViewTools.queryAddress(addresses);
							String area_abbr = "";
							String area_id = "";
							for (Map map : addressMap) {
								area_id = CommUtil.null2String(map.get("area_id"));
							}
							cartMap.put("address", addressMap);// ??????????????????
							String cart_session = CommUtil.randomString(32);// ????????????session
							request.getSession(false).setAttribute("cart_session", cart_session);
							Date date = new Date();
							Map erpMap = this.appCartViewTools.calEnoughReducePrice(goodsCarts, gcs);// ????????????????????????????????????????????????
							cartMap.put("cart_session", cart_session);
							cartMap.put("order_goods_price", erpMap.get("all"));
							cartMap.put("order_er_price", erpMap.get("reduce"));

							List map_list = new ArrayList();
							List<Object> store_list = new ArrayList<Object>();
							for (GoodsCart goodsCart : goodsCarts) {
								if (goodsCart.getGoods().getGoods_type() == 1) {
									if (goodsCart.getGoods().getGoods_store().getStore_status() == 15) {
										store_list.add(goodsCart.getGoods().getGoods_store().getId());
									}
								} else {
									store_list.add("self");
								}
							}
							HashSet hs = new HashSet(store_list);
							store_list.removeAll(store_list);
							store_list.addAll(hs);
							String[] gc_ids = CommUtil.null2String(gcs).split(",");
							List<Goods> ac_goodses = new ArrayList<Goods>();
							if (giftids != null && !giftids.equals("")) {
								String[] gift_ids = giftids.split(",");
								for (String gift_id : gift_ids) {
									Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gift_id));
									if (goods != null) {
										ac_goodses.add(goods);
									}
								}
							}
							boolean ret = false;
							if (ac_goodses.size() > 0) {
								ret = true;
							}
							// ????????????????????????
							List<GoodsCart> goodsCartList = new ArrayList<GoodsCart>();
							for (Object store_id : store_list) {
								if (store_id != "self" && !store_id.equals("self")) {// ????????????
									List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
									List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
									Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
									Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
									Map erString = new HashMap();
									for (Goods g : ac_goodses) {
										if (g.getGoods_type() == 1
												&& g.getGoods_store().getId().toString().equals(store_id.toString())) {
											gift_map.put(g, new ArrayList<GoodsCart>());
										}
									}
									for (GoodsCart gc : goodsCarts) {
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
												if (gc.getGoods().getGoods_store() != null) {
													if (gc.getGoods().getGoods_store().getId().equals(store_id)) {
														if (ret && gift_map.size() > 0
																&& gc.getGoods().getOrder_enough_give_status() == 1
																&& gc.getGoods().getBuyGift_id() != null) {
															BuyGift bg = this.buyGiftService
																	.getObjById(gc.getGoods().getBuyGift_id());
															if (bg.getBeginTime().before(date)) {
																for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
																		.entrySet()) {
																	if (entry.getKey().getBuyGift_id()
																			.equals(gc.getGoods().getBuyGift_id())) {
																		entry.getValue().add(gc);
																	} else {
																		gc_list.add(gc);
																		goodsCartList.add(gc);
																	}
																}
															} else {
																gc_list.add(gc);
																goodsCartList.add(gc);
															}
														} else if (gc.getGoods().getEnough_reduce() == 1) {
															String er_id = gc.getGoods().getOrder_enough_reduce_id();
															EnoughReduce er = this.enoughReduceService
																	.getObjById(CommUtil.null2Long(er_id));
															if (er.getErstatus() == 10
																	&& er.getErbegin_time().before(date)) {
																if (ermap.containsKey(er.getId())) {
																	ermap.get(er.getId()).add(gc);
																} else {
																	List<GoodsCart> list = new ArrayList<GoodsCart>();
																	list.add(gc);
																	ermap.put(er.getId(), list);
																	Map map = (Map) Json.fromJson(er.getEr_json());
																	double k = 0;
																	String str = "";
																	for (Object key : map.keySet()) {
																		if (k == 0) {
																			k = Double.parseDouble(key.toString());
																			str = "The activity product buy " + k
																					+ " AED, you can enjoy the discount";
																		}
																		if (Double.parseDouble(key.toString()) < k) {
																			k = Double.parseDouble(key.toString());
																			str = "The activity product buy " + k
																					+ " AED, you can enjoy the discount";
																		}
																	}

																	erString.put(er.getId(), str);
																	erString.put("er_json", map);
																}
															} else {
																gc_list.add(gc);
																goodsCartList.add(gc);
															}

														} else {
															gc_list.add(gc);
															goodsCartList.add(gc);
														}
														amount_gc_list.add(gc);
													}
												}
											}
										}
									}
									if ((gc_list != null && gc_list.size() > 0)
											|| (gift_map != null && gift_map.size() > 0)
											|| (ermap != null && ermap.size() > 0)) {
										Map map = new HashMap();
										Map amount_gc = this.appCartViewTools.calEnoughReducePrice(amount_gc_list, gcs);
										if (gift_map.size() > 0) {
											map.put("ac_goods", gift_map);
										}
										double price = 0.0;
										if (ermap.size() > 0) {
											map.put("er_goods", ermap);
											price = (double) amount_gc.get("after");
											map.put("store_goods_amount", amount_gc.get("after"));
											map.put("store_enough_reduce", amount_gc.get("reduce"));
											map.put("erString", amount_gc.get("erString"));
											map.put("er_json", amount_gc.get("er_info"));
										}
										map.put("store_id", store_id);
										map.put("store_goods_price",
												this.appCartViewTools.calCartPrice(amount_gc_list, gcs));
										map.put("store_goods_amount", amount_gc.get("all"));// ????????????
										map.put("store_total_price", price > 0 ? price : amount_gc.get("all"));
										map.put("gc_list", gc_list);
										map_list.add(map);
									}
									for (GoodsCart gc : gc_list) {
										if (gc.getGoods().getGoods_cod() == -1
												|| gc.getGoods().getGoods_choice_type() == 1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????
											goods_cod = false;
										}
										if (gc.getGoods().getTax_invoice() == 0) {// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
											tax_invoice = 0;
										}
									}
								} else {// ????????????????????????
									List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
									List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
									Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
									Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
									Map<Long, String> erString = new HashMap<Long, String>();
									for (Goods g : ac_goodses) {
										if (g.getGoods_type() == 0) {
											gift_map.put(g, new ArrayList<GoodsCart>());
										}
									}
									for (GoodsCart gc : goodsCarts) {
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
												if (gc.getGoods().getGoods_store() == null) {
													if (ret && gift_map.size() > 0
															&& gc.getGoods().getOrder_enough_give_status() == 1
															&& gc.getGoods().getBuyGift_id() != null) {
														BuyGift bg = this.buyGiftService
																.getObjById(gc.getGoods().getBuyGift_id());
														if (bg.getBeginTime().before(date)) {
															for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
																	.entrySet()) {
																if (entry.getKey().getBuyGift_id()
																		.equals(gc.getGoods().getBuyGift_id())) {
																	entry.getValue().add(gc);
																} else {
																	gc_list.add(gc);
																}
															}
														} else {
															gc_list.add(gc);
														}
													} else if (gc.getGoods().getEnough_reduce() == 1) {

														String er_id = gc.getGoods().getOrder_enough_reduce_id();
														EnoughReduce er = this.enoughReduceService
																.getObjById(CommUtil.null2Long(er_id));
														if (er.getErbegin_time().before(date)) {
															if (ermap.containsKey(er.getId())) {
																ermap.get(er.getId()).add(gc);
															} else {
																List<GoodsCart> list = new ArrayList<GoodsCart>();
																list.add(gc);
																ermap.put(er.getId(), list);
																Map map = (Map) Json.fromJson(er.getEr_json());
																double k = 0;
																String str = "";
																for (Object key : map.keySet()) {
																	if (k == 0) {
																		k = Double.parseDouble(key.toString());
																		str = "The activity product buy " + k
																				+ " AED, you can enjoy the discount";
																	}
																	if (Double.parseDouble(key.toString()) < k) {
																		k = Double.parseDouble(key.toString());
																		str = "The activity product buy " + k
																				+ " AED, you can enjoy the discount";
																	}
																}

																erString.put(er.getId(), str);
															}
														} else {
															gc_list.add(gc);
														}

													} else {
														gc_list.add(gc);
													}
													amount_gc_list.add(gc);
												}
											}
										}
									}
									if ((gc_list != null && gc_list.size() > 0)
											|| (gift_map != null && gift_map.size() > 0)
											|| (ermap != null && ermap.size() > 0)) {
										Map map = new HashMap();
										Map amount_gc = this.appCartViewTools.calEnoughReducePrice(amount_gc_list, gcs);
										if (gift_map.size() > 0) {
											map.put("ac_goods", gift_map);
										}
										double price = 0.0;
										if (ermap.size() > 0) {
											map.put("er_goods", ermap);
											price = (double) amount_gc.get("after");
											map.put("store_goods_amount", amount_gc.get("after"));
											map.put("store_enough_reduce", amount_gc.get("reduce"));
											map.put("erString", amount_gc.get("erString"));
											map.put("er_json", amount_gc.get("er_info"));
										}
										map.put("store_id", store_id);
										map.put("store_goods_price",
												this.appCartViewTools.calCartPrice(amount_gc_list, gcs));
										map.put("store_goods_amount", amount_gc.get("all"));
										map.put("store_total_price", price > 0 ? price : amount_gc.get("all"));
										map.put("gc_list", gc_list);
										map_list.add(map);
									}
									for (GoodsCart gc : gc_list) {
										if (gc.getGoods().getGoods_cod() == -1
												|| gc.getGoods().getGoods_choice_type() == 1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????
											goods_cod = false;
										}
										if (gc.getGoods().getTax_invoice() == 0) {// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
											tax_invoice = 0;
										}
									}
								}
							}
							if(map_list.size() > 0){
								// ???????????????????????? 
								// ??????
								double total_ship_price = 0;
								List<SysMap> list = transportTools.transportation_cart2(goodsCartList, null, null, area_id);
								Map sysmap = new HashMap();
								for (SysMap obj : list) {
									if (obj.getKey().equals("Express")) {
										total_ship_price = CommUtil.null2Double(obj.getValue());
									}
								}
								// [???????????? -- ????????????????????????????????????????????????]
								this.appCartViewTools.userInfo(user.getId(), cartMap);
								// ?????????????????????????????????????????????
								this.appCartViewTools.cart_order(map_list, area_id, CommUtil.null2String(user.getId()),
										cartMap, language, voucher, total_ship_price);
								cartMap.put("gcs", gcs);
								result = new Result(4200, "Success", cartMap);
							}else{
								result = new Result(5, "Order has been submitted");
							}
						} else {
							result = new Result(2, "Please file in the shipping address");
						}
					} else {
						result = new Result(3, "Order has been submitted");
					}
				}
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param token
	 *            ??????token??????
	 * @param gcs
	 *            ???????????????ids
	 * @param gc_id
	 *            ?????????????????????id
	 * @param count
	 *            ??????????????????
	 * @param address_id
	 *            ????????????id
	 * @param storeCouponId
	 *            ???????????????id
	 * @param generalCouponId
	 *            ??????????????? id
	 * @param cart_session
	 *            ????????????
	 * @param language
	 *            ??????
	 * @param integral
	 *            ??????????????????
	 * @return
	 * @descript ??????????????????????????????
	 */
	@RequestMapping("cart_order_adjust.json")
	@ResponseBody
	public String cart_order_adjust(HttpServletRequest request, HttpServletResponse response, String token, String gcs,
			String gc_id, String count, String address_id, String storeCouponId, String generalCouponId,
			String cart_session, String language, String integral, Boolean voucher) {
		int code = -1;// 0?????????????????????2??????????????????,3????????????????????????
		String msg = "";
		Map cartMap = new HashMap();
		if (token.equals("")) {
			code = -100;
			msg = "token Invalidation";
		} else {
			Map params = new HashMap();
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user == null) {
				code = -100;
				msg = "token Invalidation";
			} else {
				// 0?????????????????????2??????????????????,3????????????????????????
				double gc_price = 0;// ?????????????????????
				GoodsCart goodsCart = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
				Map erMap = new HashMap();// ??????????????????
				if (goodsCart != null) {
					Goods obj = goodsCart.getGoods();
					if (CommUtil.null2String(count).length() <= 9) {
						String gsp = "";
						int goods_inventory = 0;
						int oversea_inventory = 0;
						if (obj.getInventory_type().equals("spec")) {
							for (GoodsSpecProperty gs : goodsCart.getGsps()) {
								gsp = gs.getId() + ",";
							}
						}
						// ???????????????????????????
						if (obj.getInventory_type().equals("all")) {
							goods_inventory = obj.getGoods_inventory();
							oversea_inventory = obj.getOversea_inventory();
						} else {
							Map<String, Object> goods = this.appCartViewTools.generic_default_info_color(obj, gsp,
									goodsCart.getColor());
							goods_inventory = CommUtil.null2Int(goods.get("count"));
							oversea_inventory = CommUtil.null2Int(goods.get("oversea_inventory"));
						}
						if (CommUtil.subtract(goods_inventory, count) >= 0
								|| CommUtil.subtract(oversea_inventory, count) >= 0
										&& CommUtil.null2String(count).length() <= 9) {
							Date date = new Date();
							gc_price = CommUtil.mul(count, goodsCart.getPrice());
							goodsCart.setCount(CommUtil.null2Int(count));
							this.goodsCartService.update(goodsCart);
							if (obj.getEnough_reduce() == 1) {// ????????????
								String er_id = obj.getOrder_enough_reduce_id();
								EnoughReduce er = this.enoughReduceService.getObjById(CommUtil.null2Long(er_id));
								if (er.getErstatus() == 10 && er.getErbegin_time().before(date)) {
									String er_ids = er.getErgoods_ids_json();
									String er_json = er.getEr_json();
									double er_price = 0;// ????????????
									String er_str = "";
									Map json_map = (Map) Json.fromJson(er_json);
									for (Object key : json_map.keySet()) {
										if (gc_price > CommUtil.null2Double(key)) {
											er_price = CommUtil.null2Double(json_map.get(key));
											er_str = "Full AED " + key + " minus " + er_price;
											gc_price = CommUtil.subtract(gc_price, er_price);
											erMap.put("er_price", er_price);
											cartMap.put("enoughReduce", erMap);
										}
									}
								}
							} else {
								if (goods_inventory == 0) {
									goodsCart.setCount(0);
									this.goodsCartService.update(goodsCart);
								}
							}
							cartMap.put("gc_price", gc_price);
							Address address = this.addressService.getObjById(CommUtil.null2Long(address_id));
							if (address != null) {
								Area area = address.getArea();
								String area_id = "";
								String area_abbr = "";
								if (area != null) {
									area_id = CommUtil.null2String(area.getId());
									area_abbr = area.getParent().getAbbr();
								}
								boolean goods_cod = true;// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
								if (gcs != null && !gcs.equals("")) {
									String cart_session1 = (String) request.getSession(false)
											.getAttribute("cart_session");
									if (cart_session.equals(cart_session1)) {
										List<GoodsCart> carts = this.goodsCartService.query(
												"select obj from GoodsCart obj where id in(" + gcs + ")", null, -1, -1);
										Map erpMap = this.appCartViewTools.calEnoughReducePrice(carts, gcs);// ????????????????????????????????????????????????
										cartMap.put("cart_session", cart_session);
										// cartMap.put("order_goods_price",
										// erpMap.get("all"));
										// cartMap.put("order_er_price",
										// erpMap.get("reduce"));
										List<Object> store_list = new ArrayList<Object>();
										List map_list = new ArrayList();
										for (GoodsCart goodsCart1 : carts) {
											if (goodsCart1.getGoods().getGoods_type() == 1) {
												store_list.add(goodsCart1.getGoods().getGoods_store().getId());
											}
										}
										HashSet hs = new HashSet(store_list);
										store_list.removeAll(store_list);
										store_list.addAll(hs);
										String[] gc_ids = CommUtil.null2String(gcs).split(",");
										List<Goods> ac_goodses = new ArrayList<Goods>();
										boolean ret = false;
										if (ac_goodses.size() > 0) {
											ret = true;
										}
										// ????????????????????????
										List<GoodsCart> goodsCartList = new ArrayList<GoodsCart>();
										for (Object store_id : store_list) {
											if (store_id != "self" && !store_id.equals("self")) {// ????????????
												List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
												List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
												Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
												Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
												Map<Long, String> erString = new HashMap<Long, String>();
												for (Goods g : ac_goodses) {
													if (g.getGoods_type() == 1 && g.getGoods_store().getId().toString()
															.equals(store_id.toString())) {
														gift_map.put(g, new ArrayList<GoodsCart>());
													}
												}
												for (GoodsCart gc : carts) {
													for (String gcid : gc_ids) {
														if (!CommUtil.null2String(gcid).equals("")
																&& CommUtil.null2Long(gcid).equals(gc.getId())) {
															if (gc.getGoods().getGoods_store() != null) {
																if (gc.getGoods().getGoods_store().getId()
																		.equals(store_id)) {
																	if (ret && gift_map.size() > 0
																			&& gc.getGoods()
																					.getOrder_enough_give_status() == 1
																			&& gc.getGoods().getBuyGift_id() != null) {
																		BuyGift bg = this.buyGiftService.getObjById(
																				gc.getGoods().getBuyGift_id());
																		if (bg.getBeginTime().before(date)) {
																			for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
																					.entrySet()) {
																				if (entry.getKey().getBuyGift_id()
																						.equals(gc.getGoods()
																								.getBuyGift_id())) {
																					entry.getValue().add(gc);
																				} else {
																					gc_list.add(gc);
																					goodsCartList.add(gc);
																				}
																			}
																		} else {
																			gc_list.add(gc);
																			goodsCartList.add(gc);
																		}
																	} else if (gc.getGoods().getEnough_reduce() == 1) {

																		String er_id = gc.getGoods()
																				.getOrder_enough_reduce_id();
																		EnoughReduce er = this.enoughReduceService
																				.getObjById(CommUtil.null2Long(er_id));
																		if (er.getErbegin_time().before(date)) {
																			if (ermap.containsKey(er.getId())) {
																				ermap.get(er.getId()).add(gc);
																			} else {
																				List<GoodsCart> list = new ArrayList<GoodsCart>();
																				list.add(gc);
																				ermap.put(er.getId(), list);
																				Map map = (Map) Json
																						.fromJson(er.getEr_json());
																				double k = 0;
																				String str = "";
																				for (Object key : map.keySet()) {
																					if (k == 0) {
																						k = Double.parseDouble(
																								key.toString());
																						str = "The activity product buy "
																								+ k
																								+ " AED, you can enjoy the discount";
																					}
																					if (Double.parseDouble(
																							key.toString()) < k) {
																						k = Double.parseDouble(
																								key.toString());
																						str = "The activity product buy "
																								+ k
																								+ " AED, you can enjoy the discount";
																					}
																				}

																				erString.put(er.getId(), str);
																			}
																		} else {
																			gc_list.add(gc);
																			goodsCartList.add(gc);
																		}

																	} else {
																		gc_list.add(gc);
																		goodsCartList.add(gc);
																	}
																	amount_gc_list.add(gc);
																}
															}
														}
													}
												}
												if ((gc_list != null && gc_list.size() > 0)
														|| (gift_map != null && gift_map.size() > 0)
														|| (ermap != null && ermap.size() > 0)) {
													Map map = new HashMap();
													Map amount_gc = this.appCartViewTools
															.calEnoughReducePrice(amount_gc_list, gcs);// ??????????????????
													if (gift_map.size() > 0) {
														map.put("ac_goods", gift_map);
													}
													double price = 0.0;
													if (ermap.size() > 0) {
														map.put("er_goods", ermap);
														price = CommUtil.null2Double(amount_gc.get("after"));
														map.put("store_goods_amount", amount_gc.get("after"));
														map.put("store_enough_reduce", amount_gc.get("reduce"));
														map.put("erString", amount_gc.get("erString"));
														map.put("er_json", amount_gc.get("er_info"));
													}
													String coupon_id = this.appCartViewTools.coupon(storeCouponId,
															CommUtil.null2String(store_id));// ???????????????????????????????????????
													map.put("coupon_id", coupon_id);
													map.put("store_id", store_id);
													map.put("store_goods_price",
															this.appCartViewTools.calCartPrice(amount_gc_list, gcs));
													map.put("store_goods_amount", amount_gc.get("all"));
													map.put("store_total_price",
															price > 0 ? price : amount_gc.get("all"));
													map.put("gc_list", gc_list);
													map_list.add(map);

												}
												for (GoodsCart gc : gc_list) {
													if (gc.getGoods().getGoods_cod() == -1
															|| gc.getGoods().getGoods_choice_type() == 1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????
														goods_cod = false;
													}
												}
											} else {// ????????????
												List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
												List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
												Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
												Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
												Map<Long, String> erString = new HashMap<Long, String>();
												for (Goods g : ac_goodses) {
													if (g.getGoods_type() == 0) {
														gift_map.put(g, new ArrayList<GoodsCart>());
													}
												}
												for (GoodsCart gc : carts) {
													for (String gcid : gc_ids) {
														if (!CommUtil.null2String(gcid).equals("")
																&& CommUtil.null2Long(gcid).equals(gc.getId())) {
															if (gc.getGoods().getGoods_store() == null) {
																if (ret && gift_map.size() > 0
																		&& gc.getGoods()
																				.getOrder_enough_give_status() == 1
																		&& gc.getGoods().getBuyGift_id() != null) {
																	BuyGift bg = this.buyGiftService
																			.getObjById(gc.getGoods().getBuyGift_id());
																	if (bg.getBeginTime().before(date)) {
																		for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
																				.entrySet()) {
																			if (entry.getKey().getBuyGift_id().equals(
																					gc.getGoods().getBuyGift_id())) {
																				entry.getValue().add(gc);
																			} else {
																				gc_list.add(gc);
																			}
																		}
																	} else {
																		gc_list.add(gc);
																	}
																} else if (gc.getGoods().getEnough_reduce() == 1) {

																	String er_id = gc.getGoods()
																			.getOrder_enough_reduce_id();
																	EnoughReduce er = this.enoughReduceService
																			.getObjById(CommUtil.null2Long(er_id));
																	if (er.getErbegin_time().before(date)) {
																		if (ermap.containsKey(er.getId())) {
																			ermap.get(er.getId()).add(gc);
																		} else {
																			List<GoodsCart> list = new ArrayList<GoodsCart>();
																			list.add(gc);
																			ermap.put(er.getId(), list);
																			Map map = (Map) Json
																					.fromJson(er.getEr_json());
																			double k = 0;
																			String str = "";
																			for (Object key : map.keySet()) {
																				if (k == 0) {
																					k = Double.parseDouble(
																							key.toString());
																					str = "The activity product buy "
																							+ k
																							+ " AED, you can enjoy the discount";
																				}
																				if (Double.parseDouble(
																						key.toString()) < k) {
																					k = Double.parseDouble(
																							key.toString());
																					str = "The activity product buy "
																							+ k
																							+ " AED, you can enjoy the discount";
																				}
																			}

																			erString.put(er.getId(), str);
																		}
																	} else {
																		gc_list.add(gc);
																	}

																} else {
																	gc_list.add(gc);
																}
																amount_gc_list.add(gc);
															}
														}
													}
												}
												if ((gc_list != null && gc_list.size() > 0)
														|| (gift_map != null && gift_map.size() > 0)
														|| (ermap != null && ermap.size() > 0)) {
													Map map = new HashMap();
													Map ergcMap = this.appCartViewTools
															.calEnoughReducePrice(amount_gc_list, gcs);// ??????????????????
													if (gift_map.size() > 0) {
														map.put("ac_goods", gift_map);
													}
													if (ermap.size() > 0) {
														map.put("er_goods", ermap);
														map.put("erString", ergcMap.get("erString"));
														map.put("er_json", ergcMap.get("er_info"));
													}
													map.put("store_id", store_id);
													map.put("store_goods_price",
															this.appCartViewTools.calCartPrice(amount_gc_list, gcs));
													map.put("store_enough_reduce", ergcMap.get("reduce"));
													map.put("gc_list", gc_list);
													map_list.add(map);
												}
												for (GoodsCart gc : gc_list) {
													if (gc.getGoods().getGoods_cod() == -1
															|| gc.getGoods().getGoods_choice_type() == 1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????
														goods_cod = false;
													}
												}
											}
										}
										// ???????????????????????? 
										// ??????
										double total_ship_price = 0;
										List<SysMap> list = transportTools.transportation_cart2(goodsCartList, null, null, area_id);
										Map sysmap = new HashMap();
										for (SysMap map : list) {
											if (map.getKey().equals("Express")) {
												total_ship_price = CommUtil.null2Double(map.getValue());
											}
										}
										// ??????????????????
										this.appCartViewTools.cart_order_adjust(map_list, area_abbr, area_id,
												CommUtil.null2String(user.getId()), cartMap, generalCouponId, language,
												integral, voucher, total_ship_price);
										code = 4200;
										msg = "Success";
									} else {
										code = 4204;
										msg = "The order has expired";
									}
								} else {
									code = 4205;
									msg = "The product has been taken off the shelves";
								}
							} else {
								code = 4207;
								msg = "Please select the shipping address";
							}
						} else {
							code = 4206;
							msg = "Goods in short stock";
						}
					} else {
						code = 4206;
						msg = "Goods in short stock";
					}
				} else {
					code = 4205;
					msg = "The commodity does not exist";
				}
			}
		}
		return Json.toJson(new Result(code, msg, cartMap));
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param cart_session
	 * @param store_id
	 * @param addr_id
	 * @param gcs
	 * @param delivery_time
	 * @param delivery_type
	 * @param delivery_id
	 * @param payType
	 * @param gifts
	 * @param mobile
	 * @param mobile_verify_code
	 * @param generalCouponId
	 * @param storeCouponId
	 * @param order_type
	 * @param token
	 * @return
	 * @descript
	 */

	@ResponseBody
	@EmailMapping("cart_pay.json")
	@RequestMapping(value = "cart_pay.json", produces = { "application/json;charset=UTF-8" })
	public String cart_pay(HttpServletRequest request, HttpServletResponse response, String cart_session,
			String store_id, String addr_id, String gcs, String delivery_time, String delivery_type, String delivery_id,
			String payType, String gifts, String generalCouponId, String storeCouponId, String order_type,
			String integral, String token) {
		Result result = null;
		Map goods_cart3_map = new HashMap();
		ModelAndView mv = new JModelAndView("goods_cart3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2String(token).equals("")) {
			result = new Result(-100, "token Invalidation");
		} else {
			Map params = new HashMap();
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user == null) {
				result = new Result(-100, "token Invalidation");
			} else {
				String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
				if (CommUtil.null2String(cart_session1).equals(cart_session)
						&& !CommUtil.null2String(store_id).equals("")) {
					List<GoodsCart> order_carts = new ArrayList<GoodsCart>();
					Address address = this.addressService.getObjById(CommUtil.null2Long(addr_id));
					Date date = new Date();
					String[] gc_ids = gcs.split(",");
					String[] gift_ids = gifts.split(",");
					List<Goods> gift_goods = new ArrayList<Goods>();
					for (String gid : gift_ids) {
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
						if (goods != null) {
							BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(goods.getBuyGift_id()));
							if (bg != null && bg.getBeginTime().before(date)) {
								gift_goods.add(goods);
							}
						}
					}
					for (String gc_id : gc_ids) {
						if (!gc_id.equals("")) {
							GoodsCart car = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
							if (car != null) {
								order_carts.add(car);
							}
						}
					}

					for (String gc_id : gc_ids) {
						GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
						if (gc != null && gc.getGoods().getGoods_cod() == -1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????
							if (!payType.equals("online")) {// ??????????????????????????????????????????????????????????????????????????????????????????
								result = new Result(1, "?????????????????????????????????????????????");
								return Json.toJson(result, JsonFormat.compact());
							}
						}
					}
					if (order_carts.size() > 0 && address != null) {
						// ???????????????????????????????????????0?????????
						boolean inventory_very = true;
						for (GoodsCart gc : order_carts) {
							if (gc.getCount() == 0) {
								inventory_very = false;
							}
							int goods_inventory = CommUtil.null2Int(this.appCartViewTools
									.generic_default_info_color(gc.getGoods(), gc.getCart_gsp(), gc.getColor())
									.get("count"));// ????????????????????????
							if (goods_inventory == 0 || goods_inventory < gc.getCount()) {
								inventory_very = false;
							}
						}
						if (inventory_very) {
							User buyer = this.userService.getObjById(CommUtil.null2Long(user.getId()));
							OrderForm main_order = null;
							if (payType.equals("payafter")) {// ??????????????????
								request.getSession(false).removeAttribute("cart_session");// ????????????????????????????????????????????????????????????????????????
								String store_ids[] = store_id.split(",");
								List<Map> child_order_maps = new ArrayList<Map>();
								int whether_gift_in = 0;// ???????????????????????? ????????????????????????
														// ????????????whether_gift??????1
								String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
								Long order_id = null;

								double order_amount = 0.0;// ??????????????????
								double discounts_amount = 0.0;// ?????????????????????
								double freight_amount = 0.0;
								double payment_amount = 0;// ????????????
								List<Long> cart_order_ids = new ArrayList<Long>();
								for (int i = 0; i < store_ids.length; i++) {// ????????????id????????????????????????

									String sid = store_ids[i];
									Store store = null;
									List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
									List<Map> map_list = new ArrayList<Map>();
									if (sid != "self" && !sid.equals("self")) {
										store = this.storeService.getObjById(CommUtil.null2Long(sid));
									}
									String tempString = "";
									boolean oversea_very = true;
									boolean flag = false;
									for (GoodsCart gc : order_carts) { // ?????????:
																		// ??????????????????????????????????????????????????????????????????????????????????????????
										if (gc.getGoods().getGoods_type() == 1) {// ????????????
											boolean add = false;
											for (String gc_id : gc_ids) {
												if (!CommUtil.null2String(gc_id).equals("")
														&& gc.getId().equals(CommUtil.null2Long(gc_id))) {// ?????????????????????????????????????????????
													add = true;
													break;
												}
											}
											for (GoodsCart gcc : order_carts) { // ?????????:
												// ??????????????????????????????????????????????????????????????????????????????????????????
												if (gcc.getGoods().getGoods_type() == 1) {// ????????????
													for (String gc_id : gc_ids) {
														GoodsCart goodsCart = this.goodsCartService
																.getObjById(CommUtil.null2Long(gc_id));
														if (goodsCart != null
																&& goodsCart.getGoods().getGoods_transfee() == 0) {// ?????????????????????????????????????????????????????????????????????
															flag = true;
															break;
														}
													}
												}
											}

											if (add) {
												if (gc.getGoods().getGoods_store().getId()
														.equals(CommUtil.null2Long(sid))) {
													String goods_type = "";
													if ("combin" == gc.getCart_type()
															|| "combin".equals(gc.getCart_type())) {
														if (gc.getCombin_main() == 1) { // [1????????????????????????0????????????????????????]
															goods_type = "combin";
														}
													}
													if ("group" == gc.getCart_type()
															|| "group".equals(gc.getCart_type())) {
														goods_type = "group";
													}
													int goods_inventory = 0;
													int oversea_inventory = 0;
													Goods obj = gc.getGoods();
													if (gc.getGoods().getInventory_type().equals("all")) {
														goods_inventory = obj.getGoods_inventory();
														oversea_inventory = obj.getOversea_inventory();
													} else {
														Map<String, Object> spec = this.appCartViewTools
																.generic_default_info_color(obj, gc.getCart_gsp(),
																		gc.getColor());
														goods_inventory = CommUtil.null2Int(spec.get("count"));
														oversea_inventory = CommUtil
																.null2Int(spec.get("oversea_inventory"));
													}
													if (oversea_inventory < gc.getCount()) {
														oversea_very = false;
													}
													// ??????????????????
													Map json_map = new HashMap();
													json_map.put("goods_id", gc.getGoods().getId());
													json_map.put("goods_name", gc.getGoods().getGoods_name());
													json_map.put("ksa_goods_name", gc.getGoods().getKsa_goods_name());
													json_map.put("goods_choice_type",
															gc.getGoods().getGoods_choice_type());
													json_map.put("goods_type", goods_type);
													json_map.put("goods_color", gc.getColor());
													String goods_mainphoto_path = "";
													String sku_accessory = "";
													String goods_sku = "";
													if (gc.getGoods().getInventory_type().equals("all")) {
														goods_sku = gc.getGoods().getGoods_serial();
														goods_mainphoto_path = gc.getGoods().getGoods_main_photo()
																.getPath() + "/"
																+ gc.getGoods().getGoods_main_photo().getName()
																+ "_small." + gc.getGoods().getGoods_main_photo()
																		.getExt().toString();
													} else {
														goods_sku = this.appCartViewTools
																.generic_default_info_color(gc.getGoods(),
																		gc.getCart_gsp(), gc.getColor())
																.get("sku").toString();
														sku_accessory = this.appCartViewTools
																.generic_default_info_color(gc.getGoods(),
																		gc.getCart_gsp(), gc.getColor())
																.get("accessory").toString();
													}
													json_map.put("goods_sku", goods_sku);
													json_map.put("goods_mainphoto_path", sku_accessory.equals("")
															? goods_mainphoto_path : sku_accessory);
													json_map.put("goods_weight", gc.getGoods().getGoods_weight());
													json_map.put("goods_length", gc.getGoods().getGoods_length());
													json_map.put("goods_width", gc.getGoods().getGoods_width());
													json_map.put("goods_high", gc.getGoods().getGoods_high());
													json_map.put("goods_count", gc.getCount());
													json_map.put("goods_price", gc.getPrice());
													json_map.put("goods_current_price", gc.getPrice());
													json_map.put("goods_all_price",
															CommUtil.mul(gc.getPrice(), gc.getCount()));// ????????????
													json_map.put("goods_commission_price",
															this.appCartViewTools.getGoodsCommission(gc));// ????????????????????????
													json_map.put("goods_commission_rate",
															gc.getGoods().getGc().getCommission_rate());// ??????????????????????????????
													json_map.put("goods_payoff_price",
															CommUtil.subtract(
																	CommUtil.mul(gc.getPrice(), gc.getCount()),
																	this.appCartViewTools.getGoodsCommission(gc)));// ?????????????????????=??????????????????-???????????????
													json_map.put("goods_gsp_val", gc.getSpec_info());
													json_map.put("goods_gsp_ids", gc.getCart_gsp());
													json_map.put("evaluate", 1);
													// json_map.put("goods_snapshoot",
													// tempString);
													String goods_domainPath = CommUtil.getURL(request) + "/goods_"
															+ gc.getGoods().getId() + ".htm";
													String store_domainPath = CommUtil.getURL(request) + "/store_"
															+ gc.getGoods().getGoods_store().getId() + ".htm";
													if (this.configService.getSysConfig().isSecond_domain_open()
															&& gc.getGoods().getGoods_store()
																	.getStore_second_domain() != ""
															&& gc.getGoods().getGoods_type() == 1) {
														String store_second_domain = "http://"
																+ gc.getGoods().getGoods_store()
																		.getStore_second_domain()
																+ "." + CommUtil.generic_domain(request);
														goods_domainPath = store_second_domain + "/goods_"
																+ gc.getGoods().getId() + ".htm";
														store_domainPath = store_second_domain;
													}
													json_map.put("goods_domainPath", goods_domainPath);// ????????????????????????
													json_map.put("store_domainPath", store_domainPath);// ????????????????????????
													// ??????????????????????????????
													if (goods_type.equals("combin")) {
														json_map.put("combin_suit_info", gc.getCombin_suit_info());
													}
													map_list.add(json_map);
													gc_list.add(gc);

												}
											}
										} else {// ????????????
											boolean add = false;
											for (String gc_id : gc_ids) {
												if (!CommUtil.null2String(gc_id).equals("")
														&& gc.getId().equals(CommUtil.null2Long(gc_id))) {// ?????????????????????????????????????????????
													add = true;
													break;
												}
											}
											if (add) {
												if (sid == "self" || sid.equals("self")) {
													String goods_type = "";
													if ("combin" == gc.getCart_type()
															|| "combin".equals(gc.getCart_type())) {
														if (gc.getCombin_main() == 1) {
															goods_type = "combin";
														}

													}
													if ("group" == gc.getCart_type()
															|| "group".equals(gc.getCart_type())) {
														goods_type = "group";
													}
													final String genId = user.getId() + UUID.randomUUID().toString()
															+ ".html";
													final String goodsId = gc.getGoods().getId().toString();
													String uploadFilePath = this.configService.getSysConfig()
															.getUploadFilePath();
													final String saveFilePathName = request.getSession()
															.getServletContext().getRealPath("/") + uploadFilePath
															+ File.separator + "snapshoot" + File.separator + genId;
													File file = new File(
															request.getSession().getServletContext().getRealPath("/")
																	+ uploadFilePath + File.separator + "snapshoot");
													if (!file.exists()) {
														file.mkdir();
													}
													final String url = CommUtil.getURL(request);
													Thread t = new Thread(new Runnable() {
														public void run() {
															HttpClient client = new HttpClient();
															HttpMethod method = new GetMethod(
																	url + "/goods_" + goodsId + ".htm");
															try {
																client.executeMethod(method);
															} catch (HttpException e2) {
																// TODO
																// Auto-generated
																// catch
																// block
																e2.printStackTrace();
															} catch (IOException e2) {
																// TODO
																// Auto-generated
																// catch
																// block
																e2.printStackTrace();
															}
															String tempString = "";
															try {
																tempString = method.getResponseBodyAsString();
															} catch (IOException e2) {
																// TODO
																// Auto-generated
																// catch
																// block
																e2.printStackTrace();
															}
															method.releaseConnection();
															BufferedWriter writer = null;
															try {
																writer = new BufferedWriter(
																		new FileWriter(saveFilePathName));
															} catch (IOException e1) {
																e1.printStackTrace();
															}
															try {
																writer.append(tempString);
																writer.flush();// ?????????????????????????????????????????????????????????????????????????????????
																writer.close();
															} catch (IOException e) {
																e.printStackTrace();
															}
														}
													});
													t.start();
													Map json_map = new HashMap();
													json_map.put("goods_id", gc.getGoods().getId());
													json_map.put("goods_name", gc.getGoods().getGoods_name());
													json_map.put("ksa_goods_name", gc.getGoods().getKsa_goods_name());
													json_map.put("goods_choice_type",
															gc.getGoods().getGoods_choice_type());
													json_map.put("goods_weight", gc.getGoods().getGoods_weight());
													json_map.put("goods_length", gc.getGoods().getGoods_length());
													json_map.put("goods_width", gc.getGoods().getGoods_width());
													json_map.put("goods_high", gc.getGoods().getGoods_high());
													json_map.put("goods_spu", gc.getGoods().getGoods_serial());
													json_map.put("goods_sku",
															this.appCartViewTools
																	.generic_default_info_color(gc.getGoods(),
																			gc.getCart_gsp(), gc.getColor())
																	.get("sku"));
													json_map.put("goods_count", gc.getCount());
													json_map.put("goods_price", gc.getPrice());// ????????????
													json_map.put("goods_all_price",
															CommUtil.mul(gc.getPrice(), gc.getCount()));// ????????????
													json_map.put("goods_gsp_val", gc.getSpec_info());
													json_map.put("goods_color", gc.getColor());
													json_map.put("goods_gsp_ids",
															gc.getCart_gsp() == null ? "" : gc.getCart_gsp());
													json_map.put("goods_snapshoot", CommUtil.getURL(request) + "/"
															+ uploadFilePath + "/snapshoot/" + genId);
													json_map.put("evaluate", 1);
													if (gc.getGoods().getGoods_main_photo() != null) {
														json_map.put("goods_mainphoto_path",
																gc.getGoods().getGoods_main_photo().getPath() + "/"
																		+ gc.getGoods().getGoods_main_photo().getName()
																		+ "_small."
																		+ gc.getGoods().getGoods_main_photo().getExt());
													} else {
														json_map.put("goods_mainphoto_path",
																this.configService.getSysConfig().getGoodsImage()
																		.getPath() + "/"
																		+ this.configService.getSysConfig()
																				.getGoodsImage().getName());
													}
													json_map.put("goods_domainPath", CommUtil.getURL(request)
															+ "/goods_" + gc.getGoods().getId() + ".htm");// ????????????????????????
													// ??????????????????????????????
													if (goods_type.equals("combin")) {
														json_map.put("combin_suit_info", gc.getCombin_suit_info());
													}
													// ????????????????????????

													map_list.add(json_map);
													gc_list.add(gc);
												}
											}
										}
									}
									// ????????????
									double store_goods_amount = 0;// ??????????????????
									double store_total_price = 0;// ????????????--?????????????????????
									double store_exclude_freight = 0;// ???????????????????????????
									double reduce = 0.0;// ??????????????????
									double store_ship_price = 0.00;// ??????????????????
									double ship_price = 0; // ??????????????????
									double store_shipping_included = 0;// ??????????????????????????????
									double order_goods_amount = 0;// ???????????????????????????
									double order_total_price = 0;// ????????????
									double order_ship_price = 0;// ?????????????????????
									double order_discounts_amount = 0;// ??????????????????
									OrderForm of = new OrderForm();

									// ????????????????????? ??????
									// store_total_price =
									// this.appCartViewTools.calCartPrice(gc_list,
									// gcs);
									// ????????????
									//
									Map ermap = this.appCartViewTools.calEnoughReducePrice(gc_list, gcs);
									String er_json = (String) ermap.get("er_json");
									double all_goods = Double.parseDouble(ermap.get("all").toString());// ??????????????????
									reduce = Double.parseDouble(ermap.get("reduce").toString());
									store_total_price = CommUtil.null2Double(ermap.get("after"));
									order_discounts_amount += reduce;
									// [?????????????????????]
									String coupon_id = this.appCartViewTools.coupon(storeCouponId,
											CommUtil.null2String(store.getId()));
									// ????????????????????????id??????
									// String coupon_id =
									// request.getParameter("couponId");
									/*
									 * Map storeCoupon =
									 * Json.fromJson(Map.class, storeCouponId);
									 * String coupon_id =
									 * storeCoupon.get(CommUtil.null2String(
									 * store. getId())).toString();
									 */

									// ???????????????
									double coupon_amount = 0.0;
									if (coupon_id != null && !coupon_id.equals("")) {
										CouponInfo couponInfo = this.couponInfoService
												.getObjById(CommUtil.null2Long(coupon_id));
										if (couponInfo != null) {
											Coupon storeCoupon = couponInfo.getCoupon();
											if (user.getId().equals(couponInfo.getUser().getId())) {
												if (storeCoupon.getCoupon_end_time().after(new Date())
														&& couponInfo.getStatus() == 0
														&& CommUtil.subtract(store_total_price,
																storeCoupon.getCoupon_order_amount()) >= 0) {
													couponInfo.setStatus(1);
													this.couponInfoService.update(couponInfo);
													Map couponMap = new HashMap();
													couponMap.put("couponinfo_id", couponInfo.getId());
													couponMap.put("couponinfo_sn", couponInfo.getCoupon_sn());
													couponMap.put("coupon_amount",
															couponInfo.getCoupon().getCoupon_amount());
													if (store_ids.length > 1) {
														double rate = CommUtil.div(
																couponInfo.getCoupon().getCoupon_amount(),
																store_total_price);
														couponMap.put("coupon_goods_rate", rate);
													} else {
														couponMap.put("coupon_goods_rate", 1);
													}
													of.setCoupon_info(Json.toJson(couponMap, JsonFormat.compact()));
													order_discounts_amount += (double) (CommUtil.subtract(
															store_total_price, storeCoupon.getCoupon_amount()) <= 0
																	? store_total_price
																	: CommUtil.null2Double(
																			storeCoupon.getCoupon_amount()));
													store_total_price = CommUtil.subtract(store_total_price,
															storeCoupon.getCoupon_amount()) <= 0 ? 0
																	: CommUtil.subtract(store_total_price,
																			storeCoupon.getCoupon_amount());
													coupon_amount = storeCoupon.getCoupon_amount().doubleValue();
												}
											}
										}
									}

									store_exclude_freight = store_total_price;
									// ??????
									String transport = "Express";
									List<SysMap> sms = this.transportTools.cart_pay_transportation(gc_list,
											CommUtil.null2String(address.getArea().getId()));
									for (SysMap sm : sms) {
										if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
											ship_price = ship_price + CommUtil.null2Double(sm.getValue());// ??????????????????
										} else {
											store_ship_price = CommUtil.null2Double(sm.getValue());
										}
									}
									store_shipping_included = CommUtil.add(store_exclude_freight, ship_price);// ???????????????????????????
									order_total_price = store_shipping_included;// ??????????????????
									order_goods_amount = CommUtil.add(order_goods_amount, store_exclude_freight); // ?????????????????????
									order_amount += order_goods_amount;
									discounts_amount += order_discounts_amount;
									freight_amount += ship_price;

									// VAT(??????) ?????????????????????vat?????????vat
									Map map = this.appCartViewTools.getCommission(gc_list);
									double commission = CommUtil.null2Double(map.get("commission"));
									double vat = CommUtil.null2Double(map.get("vat"));
									double logistics_vat = CommUtil
											.mul(this.configService.getSysConfig().getLogistics_vat_rate(), ship_price);
									/*
									 * String order_store_id = "0"; if (sid !=
									 * "self" && !sid.equals("self")) {
									 * order_store_id =
									 * CommUtil.null2String(store.getId()); }
									 */
									String SM = "SM" + CommUtil.randomString(5) + user.getId();
									of.setOrder_id(SM);
									of.setAddTime(new Date());
									// ???????????????????????? -- ??????????????????
									if (address.getArea().getLevel() == 2) {
										of.setReceiver_area(address.getArea().getParent().getParent().getAreaName()
												+ " " + address.getArea().getParent().getAreaName()
												+ address.getArea().getAreaName());
										of.setReceiver_state(address.getArea().getParent().getParent().getAreaName());
										of.setReceiver_city(address.getArea().getParent().getAreaName());
										of.setReceiver_street(address.getArea().getAreaName());
									} else if (address.getArea().getLevel() == 1) {
										of.setReceiver_area(address.getArea().getParent().getAreaName()
												+ address.getArea().getAreaName());
										of.setReceiver_state(address.getArea().getParent().getAreaName());
										of.setReceiver_city(address.getArea().getAreaName());
										of.setReceiver_street("");
									}
									of.setReceiver_Name(address.getTrueName());
									of.setReceiver_area_info(address.getArea_info());
									of.setReceiver_mobile(address.getMobile());
									of.setReceiver_telephone(address.getTelephone());
									of.setReceiver_zip(address.getZip());
									of.setReceiver_email(address.getEmail());
									of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
									of.setEnough_reduce_info(er_json);
									of.setTransport_type(flag == true ? "0" : "1");
									of.setTransport(transport);
									of.setOrder_status(10);// ?????????
									of.setUser_id(buyer.getId().toString());
									of.setUser_name(buyer.getUserName());
									of.setGoods_info(Json.toJson(map_list, JsonFormat.compact()));// ??????????????????json??????
									of.setGoods_amount(BigDecimal.valueOf(store_exclude_freight));
									of.setMsg(request.getParameter("msg_" + sid));
									of.setInvoiceType(CommUtil.null2Int(request.getParameter("invoiceType")));
									of.setInvoice(request.getParameter("invoice"));
									of.setShip_price(BigDecimal.valueOf(ship_price));
									of.setStore_ship_price(BigDecimal.valueOf(store_ship_price));
									of.setTotalPrice(BigDecimal.valueOf(order_total_price));
									of.setOrder_cat(0);
									// of.setSnapshooot(tempString);
									if (sid.equals("self") || sid == "self") {
										of.setOrder_form(1);// ????????????????????????
									} else {
										of.setCommission_amount(BigDecimal.valueOf(commission));// ???????????????????????????
										of.setCommission_vat(BigDecimal.valueOf(vat));
										of.setLogistics_vat(BigDecimal.valueOf(logistics_vat));
										of.setOrder_form(0);// ??????????????????
										of.setStore_id(store.getId().toString());
										of.setStore_name(store.getStore_name());
									}
									of.setOrder_type(order_type);// App??????
									of.setDelivery_time(delivery_time);
									of.setDelivery_type(0);
									/*
									 * if (CommUtil.null2Int(delivery_type) == 1
									 * && delivery_id != null &&
									 * !delivery_id.equals("")) {//
									 * ????????????????????????json?????? of.setDelivery_type(1);
									 * DeliveryAddress deliveryAddr =
									 * this.deliveryaddrService
									 * .getObjById(CommUtil.null2Long(
									 * delivery_id)); String service_time =
									 * "??????"; if
									 * (deliveryAddr.getDa_service_type() == 1)
									 * { service_time =
									 * deliveryAddr.getDa_begin_time() + "??????" +
									 * deliveryAddr.getDa_end_time() + "???"; }
									 * params.clear(); params.put("id",
									 * deliveryAddr.getId());
									 * params.put("da_name",
									 * deliveryAddr.getDa_name());
									 * params.put("da_content",
									 * deliveryAddr.getDa_content());
									 * params.put("da_contact_user",
									 * deliveryAddr.getDa_contact_user());
									 * params.put("da_tel",
									 * deliveryAddr.getDa_tel());
									 * params.put("da_address",
									 * deliveryAddr.getDa_area().getParent().
									 * getParent().getAreaName() +
									 * deliveryAddr.getDa_area().getParent().
									 * getAreaName() +
									 * deliveryAddr.getDa_area().getAreaName() +
									 * deliveryAddr.getDa_address());
									 * params.put("da_service_day",
									 * this.DeliveryAddressTools
									 * .query_service_day(deliveryAddr.
									 * getDa_service_day()));
									 * params.put("da_service_time",
									 * service_time);
									 * of.setDelivery_address_id(deliveryAddr.
									 * getId( ));
									 * of.setDelivery_info(Json.toJson(params,
									 * JsonFormat.compact())); }
									 */
									// ?????????????????????
									if (oversea_very) {
										params.clear();
										params.put("repository", "1");
										List<ShipAddress> sa = this.shipAddressService.query(
												"select obj from ShipAddress obj where obj.repository=:repository",
												params, -1, -1);
										if (sa.size() > 0)
											of.setShip_addr_id(sa.get(0).getId());
									}
									if (i == store_ids.length - 1) {
										of.setOrder_main(1);
										// ????????????????????????????????????????????????????????????????????????????????????????????????json???????????????
										// ??????????????????????????????????????????????????????
										// ????????????????????????????????? order_amount
										// ???????????????
										CouponInfo couponInfo = this.couponInfoService
												.getObjById(CommUtil.null2Long(generalCouponId));
										if (couponInfo != null && couponInfo.getStatus() == 0
												&& couponInfo.getCoupon().getCoupon_end_time().after(new Date())) {
											if (CommUtil.subtract(order_amount,
													couponInfo.getCoupon().getCoupon_order_amount()) > 0) {
												double coupon_price = couponInfo.getCoupon().getCoupon_amount()
														.doubleValue();
												if (CommUtil.subtract(order_amount,
														couponInfo.getCoupon().getCoupon_order_amount()) > 0) {
													couponInfo.setStatus(1);
													this.couponInfoService.update(couponInfo);
													Map coupon_map = new HashMap();
													coupon_map.put("couponinfo_id", couponInfo.getId());
													coupon_map.put("couponinfo_sn", couponInfo.getCoupon_sn());
													coupon_map.put("coupon_amount",
															couponInfo.getCoupon().getCoupon_amount());
													of.setGeneral_coupon(Json.toJson(coupon_map, JsonFormat.compact()));
													of.setCoupon_amount(new BigDecimal(coupon_price));
													discounts_amount += CommUtil.subtract(order_amount,
															coupon_price) >= 0 ? coupon_price : order_amount;
													order_amount = CommUtil.subtract(order_amount, coupon_price) >= 0
															? CommUtil.subtract(order_amount, coupon_price)
															: order_amount;
												}
											}
										}

										// ????????????
										// ?????? - ??????????????????????????????
										if (this.configService.getSysConfig().isIntegral()) {
											if (CommUtil.null2Int(integral) == 1) {
												int use_integral = user.getIntegral();
												double integral_price = CommUtil.mul(use_integral,
														this.configService.getSysConfig().getIntegralExchangeRate());
												if (integral_price > 0) {
													if (CommUtil.subtract(order_amount, integral_price) >= 0) {
														user.setIntegral(0);
													} else {
														user.setIntegral(new Double(CommUtil.mul(
																this.configService.getSysConfig()
																		.getIntegralExchangeRate(),
																CommUtil.subtract(order_amount, integral_price)))
																		.intValue());
													}
													this.userService.update(user);
													discounts_amount += CommUtil.subtract(order_amount,
															integral_price) >= 0
																	? CommUtil.subtract(order_amount, integral_price)
																	: order_amount;
													order_amount = CommUtil.subtract(order_amount, integral_price) >= 0
															? CommUtil.subtract(order_amount, integral_price)
															: order_amount;
													of.setIntegral(new BigDecimal(integral_price));
													// ??????????????????--??????????????????--????????????
												}
											}
										}
										payment_amount = order_amount;
										// ????????????
										// ???????????????-?????????????????????????????????
										boolean enough_free = false;
										if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
											if (CommUtil.subtract(order_amount,
													this.configService.getSysConfig().getEnoughfree_price()) >= 0) {
												// ????????????????????????-???????????????????????????
												of.setEnough_free(1);
												of.setPlatform_ship_price(new BigDecimal(0));
												discounts_amount += freight_amount;
												enough_free = true;
											} else {
												payment_amount += freight_amount;
												of.setPlatform_ship_price(new BigDecimal(freight_amount));
											}
										}
										of.setPayTime(new Date());
										of.setPayType("COD");
										of.setOrder_status(16);
										if (whether_gift_in > 0) {
											of.setWhether_gift(1);
										}
										if (child_order_maps.size() > 0) {
											// ????????????????????????????????????????????????child_order_maps
											List childOrder = new ArrayList();
											if (enough_free) {
												childOrder = this.appCartViewTools.updateChildOrder(child_order_maps);
											}
											of.setChild_order_detail(
													Json.toJson(childOrder.size() == 0 ? child_order_maps : childOrder,
															JsonFormat.compact()));
										}
									}
									of.setPayment_amount(new BigDecimal(payment_amount));
									of.setDiscounts_amount(new BigDecimal(discounts_amount));
									// ????????????????????????
									boolean orderFlag = this.orderFormService.save(of);
									if (i == store_ids.length - 1) {// ?????????????????????????????????
										main_order = of;
										order_id = of.getId();
										goods_cart3_map.put("order_id", of.getId());
										goods_cart3_map.put("order_num", of.getOrder_id());
										if (of.getPayType() != null && of.getPayType().equals("COD")) {
											this.appCartViewTools.updateGoodsInventory(of);// ??????????????????
										}
										StoreLog storeLog = this.storeLogTools
												.getTodayStoreLog(CommUtil.null2Long(of.getStore_id()));
										storeLog.setPlaceorder(storeLog.getPlaceorder() + 1);
										if (this.orderFormTools.queryOrder(of.getStore_name())) {
											storeLog.setRepetition(
													storeLog.getPlaceorder() == 1 ? 0 : storeLog.getRepetition() + 1);
										}
										this.storeLogService.update(storeLog);
										cart_order_ids.add(of.getId());
										if (of.getOrder_main() == 1
												&& !CommUtil.null2String(of.getChild_order_detail()).equals("")) {
											List<Map> maps = this.orderFormTools
													.queryGoodsInfo(of.getChild_order_detail());
											for (Map child_map : maps) {
												OrderForm child = this.orderFormService
														.getObjById(CommUtil.null2Long(child_map.get("order_id")));
												cart_order_ids.add(child.getId());
												child.setOrder_status(16);
												child.setPayType("COD");
												child.setPayTime(new Date());
												this.orderFormService.update(child);
												if (of.getPayType() != null && of.getPayType().equals("COD")) {// ?????????????????????????????????????????????????????????????????????????????????
													this.appCartViewTools.updateGoodsInventory(child);// ??????????????????
												}
												// ????????????????????????????????????????????????????????????????????????????????????
												StoreLog storeLogc = this.storeLogTools
														.getTodayStoreLog(CommUtil.null2Long(of.getStore_id()));
												storeLogc.setPlaceorder(storeLogc.getPlaceorder() + 1);
												if (this.orderFormTools.queryOrder(of.getStore_name())) {
													storeLogc.setRepetition(storeLogc.getRepetition() + 1);
												}
												this.storeLogService.update(storeLogc);
												/*
												 * if
												 * (child_order.getOrder_form()
												 * == 0) {
												 * this.msgTools.sendSmsCharge(
												 * CommUtil.getURL( request),
												 * "sms_toseller_payafter_pay_ok_notify",
												 * store.getUser().getMobile(),
												 * null, CommUtil.null2String(
												 * child_order.getId()),
												 * child_order.getStore_id());
												 * this.msgTools.sendEmailCharge
												 * (CommUtil.getURL( request),
												 * "email_toseller_payafter_pay_ok_notify",
												 * store.getUser().getEmail(),
												 * null, CommUtil.null2String(
												 * child_order.getId()),
												 * child_order.getStore_id()); }
												 */
											}
										}
										// ??????????????????
										OrderFormLog ofl = new OrderFormLog();
										ofl.setAddTime(new Date());
										ofl.setLog_info("????????????????????????");
										ofl.setLog_user(user);
										ofl.setOf(of);
										this.orderFormLogService.save(ofl);
										String query = "select * from metoo_lucky_draw where switchs = 1";
										ResultSet res = this.databaseTools.selectIn(query);
										int lucky = 0;
										try {
											while (res.next()) {
												lucky = res.getInt("order");
											}
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										user.setRaffle(user.getRaffle() + lucky);
										this.userService.update(user);
									}
									if (orderFlag) {
										// ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
										if (store_ids.length > 1) {
											Map order_map = new HashMap();
											order_map.put("order_id", of.getId());
											order_map.put("order_number", of.getOrder_id());
											order_map.put("order_status", of.getOrder_status());
											order_map.put("coupon_amount", coupon_amount);
											order_map.put("enough_reduce_amount", reduce);
											order_map.put("goods_amount", of.getGoods_amount());
											order_map.put("ship_price", of.getShip_price());
											order_map.put("totalPrice", of.getTotalPrice());
											order_map.put("store_id", store.getId());
											order_map.put("store_name", store.getStore_name());
											order_map.put("enough_free", 0);
											order_map.put("store_logo",
													store.getStore_logo() != null ? store.getStore_logo().getPath()
															+ "/" + store.getStore_logo().getName() : "");
											order_map.put("order_goods_info", of.getGoods_info());
											child_order_maps.add(order_map);
										}
										for (GoodsCart gc : gc_list) {// ??????????????????????????????????????????
											if (gc.getCart_type() != null && gc.getCart_type().equals("combin")
													&& gc.getCombin_main() == 1) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
												Map combin_map = new HashMap();
												combin_map.put("combin_mark", gc.getCombin_mark());
												combin_map.put("combin_main", 1);
												List<GoodsCart> suits = this.goodsCartService.query(
														"select obj from GoodsCart obj where obj.combin_mark=:combin_mark and obj.combin_main!=:combin_main",
														combin_map, -1, -1);
												for (GoodsCart suit : suits) {
													gc.getGsps().clear();
													this.goodsCartService.delete(suit.getId());
												}
											}
											for (String gc_id : gc_ids) {
												if (!CommUtil.null2String(gc_id).equals("")
														&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
													gc.getGsps().clear();
													this.goodsCartService.delete(gc.getId());
												}
											}
										}

										OrderFormLog ofl = new OrderFormLog();
										ofl.setAddTime(new Date());
										ofl.setOf(of);
										ofl.setLog_info("????????????");
										ofl.setLog_user(user);
										this.orderFormLogService.save(ofl);
									}
								}
								goods_cart3_map.put("cart_order_ids", cart_order_ids);

								// ???????????????????????????????????????????????????
								/*
								 * if (main_order.getOrder_form() == 0) {
								 * this.msgTools.sendEmailCharge(CommUtil.
								 * getURL( request),
								 * "email_tobuyer_order_submit_ok_notify",
								 * buyer.getEmail(), null,
								 * CommUtil.null2String(main_order.getId()),
								 * main_order.getStore_id());
								 * this.msgTools.sendSmsCharge(CommUtil.getURL(
								 * request),
								 * "sms_tobuyer_order_submit_ok_notify",
								 * buyer.getMobile(), null,
								 * CommUtil.null2String(main_order.getId()),
								 * main_order.getStore_id()); } else {
								 * this.msgTools.sendEmailFree(CommUtil.getURL(
								 * request),
								 * "email_tobuyer_order_submit_ok_notify",
								 * buyer.getEmail(), null,
								 * CommUtil.null2String(main_order.getId()));
								 * this.msgTools.sendSmsFree(CommUtil.getURL(
								 * request ),
								 * "sms_tobuyer_order_submit_ok_notify",
								 * buyer.getMobile(), null,
								 * CommUtil.null2String(main_order.getId())); }
								 */
								result = new Result(0, "????????????????????????", goods_cart3_map);
							}
						} else {// ????????????????????????????????????????????????????????????
							result = new Result(2, "?????????????????????");
						}
					} else {
						result = new Result(3, "??????????????????");
					}
				} else {
					result = new Result(4, "??????????????????");
				}
				/*
				 * }else{ result = new Result(5,"???????????????"); }
				 */
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param token
	 *            ??????????????????
	 * @param id
	 *            ??????id
	 * @param gsp
	 *            ????????????
	 * @param color
	 *            ??????????????????
	 * @param count
	 *            ????????????
	 * @param type
	 *            ????????????
	 * @param language
	 *            ??????
	 * @descript ?????????????????????
	 */
	@RequestMapping("buyNow.json")
	@ResponseBody
	public String buyNow(HttpServletRequest request, HttpServletResponse response, String token, String id, String gsp,
			String color, String count, String type, String language) {
		int code = -1;// -100 ????????????
		String msg = "";
		Result result = null;
		Map map = new HashMap();
		if (!CommUtil.null2String(token).equals("")) {
			Map params = new HashMap();
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user != null) {
				Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
				Store goodsStore = obj.getGoods_store();
				User storeUser = goodsStore.getUser();
				if (obj != null && obj.getGoods_store().getStore_status() == 15) {
					if (!user.getId().equals(storeUser.getId())) {
						int oversea_inventory = 0;
						int goods_inventory = 0;
						// ???????????????????????????
						if (obj.getInventory_type().equals("all")) {
							goods_inventory = obj.getGoods_inventory();
							oversea_inventory = obj.getOversea_inventory();
						} else {
							Map<String, Object> goods = this.appCartViewTools.generic_default_info_color(obj, gsp,
									color);
							goods_inventory = CommUtil.null2Int(goods.get("count"));
							oversea_inventory = CommUtil.null2Int(goods.get("oversea_inventory"));
						}
						if (CommUtil.subtract(goods_inventory, count) > 0
								|| CommUtil.subtract(oversea_inventory, count) > 0) {
							if (obj.getInventory_type().equals("spec") && "".equals(color) && "".equals(gsp)) {
								code = 4400;
								msg = "Please select specifications";
							} else {
								params.clear();
								params.put("user_id", user.getId());
								params.put("defaulr_val", 1);
								List<Address> addresses = this.addressService.query(
										"select obj from Address obj where obj.user.id=:user_id and obj.default_val=:defaulr_val",
										params, -1, -1);
								if (addresses.size() > 0) {
									String area_abbr = "";
									String area_id = "";
									for (Address address : addresses) {
										Map addressMap = this.appCartViewTools.orderAddress(address);
										map.put("address", addressMap);
										area_abbr = (String) addressMap.get("area_abbr");
										area_id = (String) addressMap.get("area_id");
									}
									String cart_session = CommUtil.randomString(32);
									request.getSession(false).setAttribute("cart_session", cart_session);
									map.put("cart_session", request.getSession(false).getAttribute("cart_session"));
									this.appCartViewTools.userInfo(user.getId(), map);// ????????????
									this.appCartViewTools.buyNowV2(request, response, map, obj, area_abbr, area_id,
											CommUtil.null2String(user.getId()), gsp, color, CommUtil.null2Int(count),
											type, language); // ????????????
									code = 4200;
									msg = "Success";
								} else {
									code = 4207;
									msg = "The default address is empty";
									map.put("id", id);
									map.put("gsp", gsp);
									map.put("color", color);
									map.put("count", count);
									map.put("type", type);
									map.put("language", language);
								}
							}
						} else {
							code = 4206;
							msg = "Goods in short stock";
						}
					} else {
						code = 4203;
						msg = "You cannot buy goods from your own shop";
					}
				} else {
					code = 4208;
					msg = "The merchandise is off the shelves";
				}
			} else {
				code = -100;
			}
		} else {
			code = -100;
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	}

	/**
	 * @param request
	 * @param response
	 * @param token
	 *            ??????????????????
	 * @param id
	 *            ????????????id
	 * @param goods_id
	 *            ??????id
	 * @param gsp
	 *            ????????????
	 * @param color
	 *            ??????????????????
	 * @param count
	 *            ????????????
	 * @param cart_session
	 *            ????????????
	 * @param storeCouponId
	 *            ???????????????
	 * @param generalCouponId
	 *            ???????????????
	 * @param type
	 *            ????????????
	 * @param language
	 *            ??????
	 * @param integral
	 *            ????????????????????????
	 * @description buyNow ??????????????????????????????????????? ????????????????????????
	 */
	@RequestMapping("orderCountAdjust.json")
	// @RequestMapping("buyNowOrder.json")
	@ResponseBody
	public String orderCountAdjustV1(HttpServletRequest request, HttpServletResponse response, String token, String id,
			String goods_id, String gsp, String color, String count,
			@RequestParam(value = "cart_session", required = true) String cart_session, String storeCouponId,
			String generalCouponId, String type, String language, String integral) {
		Map<String, Object> map = new HashMap<String, Object>();
		int code = -1;
		String msg = "";
		double goods_price = 0; // ????????????
		double store_goods_amount = 0;// ??????????????????????????? -- ?????? ??????????????????????????????
		double order_total_price = 0; // ????????????--?????????????????????
		double store_total_price = 0; // ????????????--????????????
		double ship_price = 0; // ??????
		double store_shipping_included = 0;// ??????????????????????????????
		double store_exclude_freight = 0;// ??????????????????
		double order_discounts_amount = 0;// ??????????????????
		double ship_subsidy_price = 0;// ????????????????????????????????????
		double order_ship_price = 0;// ?????????????????????
		double order_free_express_price = 0;// ?????????????????????????????????

		User user = this.userService.getObjByProperty(null, "app_login_token", token);
		String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
		if (cart_session1 != null && !cart_session.equals("")) {
			if (cart_session1.equals(cart_session)) {
				Goods obj = this.goodsService.getObjById(Long.parseLong(goods_id));
				if (obj != null && obj.getGoods_store().getStore_status() == 15) {
					if (count == null && Integer.parseInt(count) < 1) {
						code = 4406;
						msg = "Purchase quantity must not be less than 1";
					} else {
						boolean inventory_very = true;
						int goods_inventory = 0;
						int oversea_inventory = 0;
						if (obj.getInventory_type().equals("all")) {
							goods_inventory = obj.getGoods_inventory();
							oversea_inventory = obj.getOversea_inventory();
						} else {
							Map<String, Object> goods = this.appCartViewTools.generic_default_info_color(obj, gsp,
									color);
							goods_inventory = CommUtil.null2Int(goods.get("count"));
							oversea_inventory = CommUtil.null2Int(goods.get("oversea_inventory"));
						}
						if (CommUtil.subtract(goods_inventory, count) > 0
								|| CommUtil.subtract(oversea_inventory, count) > 0) {
							if (obj.getInventory_type().equals("spec") && "".equals(color) && "".equals(gsp)) {
								code = 4402;
								msg = "Please select the product specifications";
							} else {
								Store store = obj.getGoods_store();
								Address address = this.addressService.getObjById(Long.parseLong(id));
								if (address != null) {
									Map priceMap = this.appCartViewTools.buyNowEnoughReducePriceV2(obj, gsp, color,
											CommUtil.null2Int(count));
									goods_price = CommUtil.null2Double(priceMap.get("goods_price"));
									store_goods_amount = CommUtil.null2Double(priceMap.get("goods_amount"));
									store_total_price = CommUtil.null2Double(priceMap.get("after_goods_amount"));
									// ??????
									if (obj.getEnough_reduce() == 1) {
										String er_id = obj.getOrder_enough_reduce_id();
										EnoughReduce enoughReduce = this.enoughReduceService
												.getObjById(CommUtil.null2Long(er_id));
										if (enoughReduce.getErbegin_time().before(new Date())) {
											Map erMap = new HashMap();
											erMap.put("er_tag", enoughReduce.getErtag());
											LinkedHashMap json = JSONObject.parseObject(enoughReduce.getEr_json(),
													LinkedHashMap.class, Feature.OrderedField);
											// JSONObject json =
											// JSONObject.parseObject(enoughReduce.getEr_json());
											erMap.put("er_json", json);
											erMap.put("er_price", priceMap.get("reduce"));
											erMap.put("after", priceMap.get("after"));
											order_discounts_amount += CommUtil.null2Double(priceMap.get("reduce"));
											map.put("enoughReduce", erMap);
										}
									}
									/*
									 * if (this.configService.getSysConfig().
									 * getServer_version() == 2) { if
									 * (store.getTransport() != null &&
									 * store.getTransport().getExpress_company()
									 * != null) { if
									 * (store.getTransport().getExpress_company(
									 * ).getEnabled() == 1) { flag =
									 * this.appCartViewTools.matching(store,
									 * address.getArea().getParent().getAbbr());
									 * } } else { flag = false; } }
									 */
									// ?????????????????????
									boolean store_coupon = false;
									double store_coupon_price = 0;
									if (storeCouponId != null && !storeCouponId.equals("")) {
										CouponInfo couponInfo = this.couponInfoService
												.getObjById(CommUtil.null2Long(storeCouponId));
										if (couponInfo != null) {
											if (user.getId().equals(couponInfo.getUser().getId())) {
												if (CommUtil.subtract(store_total_price,
														couponInfo.getCoupon().getCoupon_order_amount()) >= 0) {
													store_coupon_price = couponInfo.getCoupon().getCoupon_amount()
															.doubleValue();
													order_discounts_amount += CommUtil.subtract(store_total_price,
															store_coupon_price) <= 0 ? store_total_price
																	: store_coupon_price;
													store_total_price = CommUtil.subtract(store_total_price,
															store_coupon_price) <= 0 ? 0
																	: CommUtil.subtract(store_total_price,
																			store_coupon_price);
												}
											}
										}
									}

									// ?????????????????????
									List<Map> couponinfo = this.appCartViewTools.userCoupon(store.getId(),
											CommUtil.null2Double(store_goods_amount), user.getId());
									map.put("store_couponinfo", couponinfo);
									store_exclude_freight = store_total_price;

									// ??????
									Area area = this.areaService.getObjById(address.getArea().getId());
									String volume = "";
									ship_price = this.transportTools.buyNowTransFree(request, response,
											CommUtil.null2String(obj.getId()), "express", volume,
											this.appCartViewTools.getCity(area), CommUtil.null2Int(count), gsp, color);
									store_shipping_included = CommUtil.add(store_exclude_freight, ship_price);// ?????????????????????

									// ???????????????
									boolean ship_flag = false;
									if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
										order_free_express_price = this.configService.getSysConfig()
												.getEnoughfree_price();
										if (CommUtil.subtract(store_exclude_freight,
												this.configService.getSysConfig().getEnoughfree_price()) >= 0) {
											order_discounts_amount += ship_price;
										} else {
											ship_flag = true;
										}
									}
									order_total_price = store_exclude_freight;
									List<Map> generalCouponinfo = this.appCartViewTools.userCoupon(null,
											store_exclude_freight, user.getId());// ?????????????????????
									// ???????????????
									double platform_coupon_price = 0;
									if (generalCouponId != null && !generalCouponId.equals("")) {
										CouponInfo couponInfo = this.couponInfoService
												.getObjById(CommUtil.null2Long(generalCouponId));
										if (user.getId().equals(couponInfo.getUser().getId())) {
											if (CommUtil.subtract(store_exclude_freight,
													couponInfo.getCoupon().getCoupon_order_amount()) >= 0) {

												platform_coupon_price = CommUtil
														.null2Double(couponInfo.getCoupon().getCoupon_amount());

												order_total_price = CommUtil.subtract(store_exclude_freight,
														platform_coupon_price) >= 0
																? CommUtil.subtract(store_exclude_freight,
																		platform_coupon_price)
																: 0;
												order_discounts_amount += CommUtil.subtract(store_exclude_freight,
														platform_coupon_price) >= 0 ? platform_coupon_price : 0;
											}
										}
									}

									// ??????
									if (this.configService.getSysConfig().isIntegral()) {
										if (CommUtil.null2Int(integral) == 1) {
											int use_integral = user.getIntegral();
											double integral_price = CommUtil.mul(use_integral,
													this.configService.getSysConfig().getIntegralExchangeRate());
											if (integral_price > 0) {
												order_total_price = CommUtil.subtract(order_total_price,
														integral_price) >= 0
																? CommUtil.subtract(order_total_price, integral_price)
																: 0;
												order_discounts_amount += integral_price;
											}
										}
									}

									if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
										if (ship_flag) {
											order_total_price = CommUtil.add(order_total_price, ship_price);
											order_ship_price = ship_price;
											ship_subsidy_price = CommUtil.subtract(
													this.configService.getSysConfig().getEnoughfree_price(),
													store_exclude_freight);
										} else {
											order_ship_price = 0;
										}
									}

									map.put("store_goods_amount", store_goods_amount);
									map.put("store_total_price", store_total_price);
									map.put("ship_price", ship_price);
									map.put("ship_subsidy_price", ship_subsidy_price);
									map.put("order_ship_price", order_ship_price);
									map.put("order_discounts_amount", order_discounts_amount);
									map.put("pay_total_price", order_total_price);
									// double order_free_express_price =
									// 0;//?????????????????????????????????
									map.put("order_free_express_price", order_free_express_price);
									map.put("enoughfree_status",
											this.configService.getSysConfig().getEnoughfree_status());
									/*
									 * if
									 * (transMap.get("flag").toString().equals(
									 * "1")) { map.put("pay_total_price",
									 * order_total_price); } else { if
									 * (transMap.get("flag").toString().equals(
									 * "0")) { map.put("pay_total_price",
									 * CommUtil.add(order_total_price,
									 * ship_price)); } }
									 */
									map.put("general_couponinfo", generalCouponinfo);
									code = 4200;
									msg = "Success";

								} else {
									code = 4207;
									msg = "Please select the shipping address";
								}
							}
						} else {
							code = 4206;
							msg = "Goods in short stock";
						}
					}
				} else {
					code = 4205;
					msg = "The merchandise is off the shelves";
				}
			} else {
				code = 4204;
				msg = "The order has expired";
			}
		} else {
			code = 4400;
			msg = "session??????";
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param token
	 *            ??????????????????
	 * @param goods_id
	 *            ??????id
	 * @param gsp
	 *            ????????????
	 * @param color
	 *            ??????????????????
	 * @param count
	 *            ????????????
	 * @param address_id
	 *            ????????????id
	 * @param payType
	 *            ????????????
	 * @param cart_session
	 *            ????????????
	 * @param storeCouponId
	 *            ???????????????
	 * @param generalCouponId
	 *            ???????????????
	 * @param mobile
	 *            ????????????
	 * @param mobile_verify_code
	 *            ?????????
	 * @param store_id
	 *            ??????id
	 * @param order_type
	 *            ????????????
	 * @param integral
	 *            ????????????????????????
	 * @return
	 */
	@EmailMapping(value = "payOnDelivery", title = "v3/payOnDelivery.json")
	@RequestMapping("payOnDelivery.json")
	// @RequestMapping("payOnDelivery.json")
	@ResponseBody
	public String payOnDelivery(HttpServletRequest request, HttpServletResponse response, String token, String goods_id,
			String gsp, String color, String count, String address_id, String payType, String cart_session,
			String storeCouponId, String generalCouponId, String mobile, String mobile_verify_code, String store_id,
			String order_type, String integral) {
		Result result = null;
		Map map = new HashMap();
		int code = -1;
		String msg = "";
		User user = null;
		if (!CommUtil.null2String(token).equals("")) {
			user = this.userService.getObjByProperty(null, "app_login_token", token);
		}
		if (user != null) {
			Store store = this.storeService.getObjById(Long.parseLong(store_id));
			String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
			if (CommUtil.null2String(cart_session1).equals(cart_session) && store.getStore_status() == 15) {
				Address address = this.addressService.getObjById(Long.parseLong(address_id));
				Goods obj = this.goodsService.getObjById(Long.parseLong(goods_id));
				if (address != null) {
					if (obj != null && obj.getGoods_status() == 0) {
						boolean inventory_very = true;
						boolean oversea_very = false;
						int goods_inventory = 0;
						int oversea_inventory = 0;
						// ???????????????????????????
						if (obj.getInventory_type().equals("all")) {
							goods_inventory = obj.getGoods_inventory();
							oversea_inventory = obj.getOversea_inventory();
						} else {
							Map<String, Object> goods = this.appCartViewTools.generic_default_info_color(obj, gsp,
									color);
							goods_inventory = CommUtil.null2Int(goods.get("count"));
							oversea_inventory = CommUtil.null2Int(goods.get("oversea_inventory"));
						}
						if (CommUtil.subtract(oversea_inventory, count) > 0) {
							oversea_very = true;
						}
						if (CommUtil.subtract(goods_inventory, count) > 0
								|| CommUtil.subtract(oversea_inventory, count) > 0) {
							OrderForm main_order = null;
							/*
							 * if (payType.equals("payafter")) {// ?????????????????? Map
							 * payafter_payTypemap = new HashMap(); String
							 * pay_session = CommUtil.randomString(32);
							 * request.getSession(false).setAttribute(
							 * "pay_session", pay_session);
							 * payafter_payTypemap.put("pay_session",
							 * pay_session); map.put("payafter_payTypemap",
							 * payafter_payTypemap); }
							 */
							double all_of_price = 0;
							request.getSession(false).removeAttribute("cart_session");// ???????????????????????????????????????????????????
							// ????????????
							List<Map> orderGoodsMap = new ArrayList<Map>();
							orderGoodsMap.add(this.appCartViewTools.orderGoods(obj, count, gsp, color));
							double store_goods_amount = 0; // ????????????
							double store_total_price = 0; // ????????????
							double ship_price = 0; // ????????????
							double store_shipping_included = 0; // ??????????????????????????????
							double store_exclude_freight = 0; // ??????????????????
							double order_total_price = 0;// ????????????
							double order_discounts_amount = 0;// ??????????????????
							double payment_price = 0; // ??????????????????
							double user_ship_price = 0; // ????????????
							double store_ship_price = 0; // ????????????
							double order_ship_price = 0;// ?????????????????????
							String er_string = null;
							double reduce = 0;
							double after = 0;
							String er_json = "";
							// ??????
							Map enoughReducePrice = this.appCartViewTools.buyNowEnoughReducePriceV2(obj, gsp, color,
									CommUtil.null2Int(count));
							store_total_price = CommUtil.null2Double(enoughReducePrice.get("after_goods_amount"));
							if (obj.getEnough_reduce() == 1) {
								String er_id = obj.getOrder_enough_reduce_id();
								EnoughReduce enoughReduce = this.enoughReduceService
										.getObjById(CommUtil.null2Long(er_id));
								if (enoughReduce.getErbegin_time().before(new Date())) {
									Map erMap = new HashMap();// ????????????
									er_json = enoughReduce.getEr_json();

									reduce = CommUtil.null2Double(enoughReducePrice.get("reduce"));// ??????????????????
									after = CommUtil.null2Double(enoughReducePrice.get("after"));// ?????????????????????

								}
							}
							// ?????????????????????
							boolean store_coupon = false;
							double store_coupon_price = 0;
							String store_coupon_info = "";
							if (storeCouponId != null && !storeCouponId.equals("")) {
								CouponInfo couponInfo = this.couponInfoService
										.getObjById(CommUtil.null2Long(storeCouponId));
								if (couponInfo != null) {
									Coupon coupon = couponInfo.getCoupon();
									if (user.getId().equals(couponInfo.getUser().getId())) {
										if (coupon != null && coupon.getCoupon_end_time().after(new Date())) {
											if (CommUtil.subtract(store_total_price,
													couponInfo.getCoupon().getCoupon_order_amount()) >= 0) {
												couponInfo.setStatus(1);
												this.couponInfoService.update(couponInfo);
												// ???????????????????????????
												Map couponMap = new HashMap();
												couponMap.put("couponinfo_id", couponInfo.getId());
												couponMap.put("couponinfo_sn", couponInfo.getCoupon_sn());
												couponMap.put("coupon_amount",
														couponInfo.getCoupon().getCoupon_amount());
												double rate = CommUtil.div(couponInfo.getCoupon().getCoupon_amount(),
														store_goods_amount);
												couponMap.put("coupon_goods_rate", rate);
												store_coupon_info = Json.toJson(couponMap, JsonFormat.compact());
												store_coupon_price = CommUtil
														.null2Double(couponInfo.getCoupon().getCoupon_amount());
												store_total_price = CommUtil.subtract(store_total_price,
														store_coupon_price) > 0
																? CommUtil.subtract(store_total_price,
																		store_coupon_price)
																: 0;
											}
										}
									}
								}
							}
							store_exclude_freight = store_total_price;
							order_total_price = store_exclude_freight;
							// ???????????????
							boolean self_coupon = false;
							double platform_coupon_price = 0;
							String general_coupon_info = "";
							if (generalCouponId != null && !generalCouponId.equals("")) {
								CouponInfo couponInfo = this.couponInfoService
										.getObjById(CommUtil.null2Long(generalCouponId));
								if (user.getId().equals(couponInfo.getUser().getId())) {
									Coupon coupon = couponInfo.getCoupon();
									if (coupon != null && coupon.getCoupon_end_time().after(new Date())) {
										if (CommUtil.subtract(store_exclude_freight,
												couponInfo.getCoupon().getCoupon_order_amount()) >= 0) {
											Map couponMap = new HashMap();
											couponMap.put("couponinfo_id", couponInfo.getId());
											couponMap.put("couponinfo_sn", couponInfo.getCoupon_sn());
											couponMap.put("coupon_amount", couponInfo.getCoupon().getCoupon_amount());
											couponMap.put("rate_price", couponInfo.getCoupon().getCoupon_amount());
											couponMap.put("coupon_goods_rate", 1);
											general_coupon_info = Json.toJson(couponMap, JsonFormat.compact());
											if (CommUtil.subtract(store_exclude_freight,
													couponInfo.getCoupon().getCoupon_order_amount()) >= 0) {
												platform_coupon_price = CommUtil
														.null2Double(couponInfo.getCoupon().getCoupon_amount());
												order_total_price = CommUtil.subtract(store_exclude_freight,
														platform_coupon_price) >= 0
																? CommUtil.subtract(store_exclude_freight,
																		platform_coupon_price)
																: 0;
											}
											couponInfo.setStatus(1);
											this.couponInfoService.update(couponInfo);
										}
									}
								}
							}

							// ??????
							if (this.configService.getSysConfig().isIntegral()) {
								if (CommUtil.null2Int(integral) == 1) {
									int use_integral = user.getIntegral();
									double integral_price = CommUtil.mul(use_integral,
											this.configService.getSysConfig().getIntegralExchangeRate());
									if (integral_price > 0) {
										order_total_price = CommUtil.subtract(order_total_price, integral_price) >= 0
												? CommUtil.subtract(order_total_price, integral_price) : 0;
										order_discounts_amount += integral_price;
									}
								}
							}
							// ??????
							String transPort = "";
							String transport_type = "-1";
							Area area = this.areaService.getObjById(address.getArea().getId());
							String volume = "";
							ship_price = this.transportTools.buyNowTransFree(request, response,
									CommUtil.null2String(obj.getId()), "express", volume,
									this.appCartViewTools.getCity(area), CommUtil.null2Int(count), gsp, color);
							transPort = "Express";
							store_shipping_included = CommUtil.add(store_exclude_freight, ship_price);// ?????????????????????

							// ???????????????
							/*
							 * if (this.configService.getSysConfig().
							 * getEnoughfree_status() == 1) { if
							 * (CommUtil.subtract(store_exclude_freight,
							 * this.configService.getSysConfig().
							 * getEnoughfree_price()) >= 0) { // ??????
							 * order_discounts_amount += ship_price; } else {
							 * map.put("transMap", transMap); } } else { //
							 * ?????????????????? if (store.getEnough_free() == 1 &&
							 * CommUtil.subtract(store.getEnough_free_price(),
							 * store_exclude_freight) <= 0) {// ??????
							 * user_ship_price = 0; store_ship_price =
							 * ship_price; transport_type = "1"; } else { // ??????
							 * order_total_price =
							 * CommUtil.add(order_total_price, ship_price);
							 * user_ship_price = ship_price; store_ship_price =
							 * 0; transport_type = "0"; } }
							 */

							// ???????????????
							if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
								order_ship_price = CommUtil.subtract(ship_price, ship_price * 2);
								if (CommUtil.subtract(store_exclude_freight,
										this.configService.getSysConfig().getEnoughfree_price()) >= 0) {
									// ??????
									user_ship_price = 0;
									store_ship_price = ship_price;
									transport_type = "1";
								} else {
									user_ship_price = ship_price;
									store_ship_price = 0;
									transport_type = "0";
									order_total_price = CommUtil.add(order_total_price, ship_price);
								}
							}
							// ????????????
							double commission_amount = this.appCartViewTools.getGoodsOrderCommission(obj,
									Integer.parseInt(count));
							// ????????????VAT
							double commission_vat = CommUtil.mul(commission_amount, obj.getGc().getCommission_vat());
							// ??????VAT
							double logistics_vat = CommUtil
									.mul(this.configService.getSysConfig().getLogistics_vat_rate(), ship_price);

							// ???????????? double goods_vat =
							// CommUtil.mul(store_exclude_freight, 0.15);

							// ????????????
							OrderForm of = new OrderForm();
							of.setOrder_main(1);
							of.setAddTime(new Date());
							of.setStore_id(store_id);
							String SM = "SM" + CommUtil.randomString(5) + user.getId();
							of.setOrder_id(SM);
							// ????????????????????????
							of.setReceiver_Name(address.getTrueName());
							if (address.getArea().getLevel() == 2) {
								of.setReceiver_area(address.getArea().getParent().getParent().getAreaName() + " "
										+ address.getArea().getParent().getAreaName()
										+ address.getArea().getAreaName());
								of.setReceiver_state(address.getArea().getParent().getParent().getAreaName());
								of.setReceiver_city(address.getArea().getParent().getAreaName());
								of.setReceiver_street(address.getArea().getAreaName());
							} else if (address.getArea().getLevel() == 1) {
								of.setReceiver_area(
										address.getArea().getParent().getAreaName() + address.getArea().getAreaName());
								of.setReceiver_state(address.getArea().getParent().getAreaName());
								of.setReceiver_city(address.getArea().getAreaName());
								of.setReceiver_street("");
							}
							of.setReceiver_area_info(address.getArea_info());
							of.setReceiver_mobile(address.getMobile());
							of.setReceiver_telephone(address.getTelephone());
							of.setReceiver_zip(address.getZip());
							of.setReceiver_email(address.getEmail());
							of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
							of.setEnough_reduce_info(er_json);
							of.setTransport_type(transport_type);
							of.setTransport(transPort);
							of.setUser_id(user.getId().toString());
							of.setUser_name(user.getUserName());
							of.setGoods_info(Json.toJson(orderGoodsMap, JsonFormat.compact()));// ??????????????????json??????
							of.setShip_price(BigDecimal.valueOf(user_ship_price));
							of.setStore_ship_price(BigDecimal.valueOf(store_ship_price));
							of.setGoods_amount(BigDecimal.valueOf(store_goods_amount));
							of.setTotalPrice(BigDecimal.valueOf(order_total_price));
							of.setCoupon_info(store_coupon_info);
							of.setGeneral_coupon(general_coupon_info);
							if (oversea_very) {
								Map params = new HashMap();
								params.put("repository", "1");
								List<ShipAddress> sa = this.shipAddressService.query(
										"select obj from ShipAddress obj where obj.repository=:repository", params, -1,
										-1);
								if (sa.size() > 0)
									of.setShip_addr_id(sa.get(0).getId());
							}
							of.setOrder_form(0);
							of.setCommission_amount(BigDecimal.valueOf(commission_amount));// ???????????????????????????
							// of.setGoods_vat(BigDecimal.valueOf(goods_vat));
							of.setCommission_vat(BigDecimal.valueOf(commission_vat));
							of.setLogistics_vat(CommUtil.null2BigDecimal(logistics_vat));
							of.setOrder_form(0);// ??????????????????
							of.setOrder_cat(5);
							of.setStore_id(store.getId().toString());
							of.setStore_name(store.getStore_name());
							of.setOrder_type(order_type);
							of.setDelivery_type(0);

							boolean orderFlag = this.orderFormService.save(of);
							List<Long> cart_order_ids = new ArrayList<Long>();
							cart_order_ids.add(of.getId());
							map.put("cart_order_ids", cart_order_ids);
							// ????????????
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							of.setPay_msg("COD");
							of.setPayTime(new Date());
							of.setPayType("COD");
							of.setOrder_status(16);
							if (of.getPayType() != null && of.getPayType().equals("COD")) {
								// ?????????????????????????????????????????????????????????????????????????????????
								this.appGoodsViewTools.updateGoodsInventory(of);
								// ??????????????????
							}
							// ????????????
							StoreLog storeLog = this.storeLogTools
									.getTodayStoreLog(CommUtil.null2Long(of.getStore_id()));
							storeLog.setPlaceorder(storeLog.getPlaceorder() + 1);
							if (this.orderFormTools.queryOrder(of.getStore_name())) {
								storeLog.setRepetition(
										storeLog.getPlaceorder() == 1 ? 0 : storeLog.getRepetition() + 1);
							}
							this.storeLogService.update(storeLog);
							// ??????????????????
							ofl.setAddTime(new Date());
							ofl.setLog_info("????????????????????????");
							ofl.setLog_user(user);
							ofl.setOf(of);
							this.orderFormLogService.save(ofl);
							String query = "select * from metoo_lucky_draw where switchs = 1";
							ResultSet res = this.databaseTools.selectIn(query);
							int lucky = 0;
							try {
								while (res.next()) {
									lucky = res.getInt("order");
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							user.setRaffle(user.getRaffle() + lucky);
							this.userService.update(user);
							request.getSession(false).removeAttribute("pay_session");
							map.put("msg", "Cash on delivery submitted success, waiting for delivery");
							map.put("raffle", lucky);
							final String id = CommUtil.null2String(of.getId());
							Thread t = new Thread(new Runnable() {
								private String order_id = id;

								public void run() {
									String email = "1223414075@qq.com,11943732@qq.com,460751446@qq.com";
									try {
										msgTools.sendEmail("", "email_tobuyer_order_submit_ok_notify", email, null,
												order_id, null);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
							t.start();
							code = 4200;
							msg = "Success";
						} else {
							code = 4206;
							msg = "Goods in short stock";
						}
					} else {
						code = 4205;
						msg = "The product has been taken off the shelves";
					}
				} else {
					code = 4207;
					msg = "Please select the shipping address";
				}
			} else {
				code = 4204;
				msg = "The order has expired";
			}
		} else {
			code = -100;
			msg = "User is not login";
		}
		return Json.toJson(new Result(code, msg, map));
	}

	private void send_json(String json, HttpServletResponse response) {
		// this.send_json(Json.toJson(new Result(code, msg, map),
		// JsonFormat.compact()), response);
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
