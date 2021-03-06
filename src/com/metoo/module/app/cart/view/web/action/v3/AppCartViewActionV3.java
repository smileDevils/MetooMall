package com.metoo.module.app.cart.view.web.action.v3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.metoo.core.annotation.EmailMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.database.DatabaseTools;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.Coupon;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Game;
import com.metoo.foundation.domain.GameTask;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GoodsVoucherInfo;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.ShipAddress;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.StoreLog;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IBuyGiftService;
import com.metoo.foundation.service.ICouponInfoService;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IGameService;
import com.metoo.foundation.service.IGameTaskService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IOrderFormLogService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IShipAddressService;
import com.metoo.foundation.service.IStoreLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.seller.tools.StoreLogTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.modul.app.game.tree.tools.AppGameAwardTools;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.module.app.view.tools.AppGoodsVoucherTools;
import com.metoo.module.app.view.web.tool.AppCartViewTools;
import com.metoo.module.app.view.web.tool.AppGoodsViewTools;
import com.metoo.module.app.view.web.tool.AppLuckyDrawTools;
import com.metoo.module.app.view.web.tool.AppRegisterViewTools;
import com.metoo.msg.MsgTools;

/**
 * <p>
 * Title: AppCartViewActionV4.java
 * </p>
 * 
 * <p>
 * Description: ?????????????????????; ??????????????????????????????????????????;
 * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * </p>
 * 
 * @author hkk
 *
 */

@Controller
@RequestMapping("app/v3")
public class AppCartViewActionV3 {

	@Autowired
	private IUserService userService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private AppCartViewTools appCartViewTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private AppGoodsViewTools appGoodsViewToolds;
	@Autowired
	private StoreLogTools storeLogTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreLogService storeLogService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private AppLuckyDrawTools appLuckyDrawTools;
	@Autowired
	private AppRegisterViewTools appRegisterViewTools;
	@Autowired
	private IGameService gameService;
	@Autowired
	private IGameTaskService gameTaskService;
	@Autowired
	private AppGameAwardTools appGameAwardTools;
	@Autowired
	private AppGoodsVoucherTools appGoodsVoucherTools;
	
	/**
	 * ???????????????
	 * @param request
	 * @param response
	 * @param gcs
	 * @param giftids
	 * @param token
	 * @param language
	 * @param voucher
	 * @return
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
																	}
																}
															} else {
																gc_list.add(gc);
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
															}

														} else {
															gc_list.add(gc);
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
							// [???????????? -- ????????????????????????????????????????????????]
							this.appCartViewTools.userInfo(user.getId(), cartMap);
							// ?????????????????????????????????????????????
							this.appCartViewTools.cart_order(map_list, area_id, CommUtil.null2String(user.getId()),
									cartMap, language, voucher, 0);
							cartMap.put("gcs", gcs);
							result = new Result(4200, "Success", cartMap);

						} else {
							result = new Result(2, "????????????");
						}
					} else {
						result = new Result(3, "?????????????????????");
					}
				}
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}

	
	/**
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
	 * @param payment_platform gh:glocash???pp:Paypal???bw:baiwan
	 * @return
	 * @descript ????????????????????????
	 */
	@ResponseBody
	@EmailMapping("cart_pay.json")
	@RequestMapping(value = "cart_pay.json", produces = { "application/json;charset=UTF-8" })
	public String cart_pay(HttpServletRequest request, HttpServletResponse response, String cart_session,
			String store_id, String addr_id, String gcs, String delivery_time, String delivery_type, String delivery_id,
			String payType, String gifts, String generalCouponId, String storeCouponId, String order_type,
			String integral, String token, String email, Boolean voucher, String payment_platform) {
		String msg = "";
		int code = -1;
		Map map = new HashMap();
		if (CommUtil.null2String(token).equals("")) {
			code = -100;
			msg = "token Invalidation";
		} else {
			Map params = new HashMap();
			User user = this.userService.getObjByProperty(null, "app_login_token", token);
			if (user == null) {
				code = -100;
				msg = "token Invalidation";
			} else {
				boolean isEmpty = true;
				boolean verify = true;
				if (payType.equals("online")) {
					if(payment_platform != null && payment_platform.equals("glocash")){
						if (!CommUtil.null2String(email).equals("")) {
							verify = appRegisterViewTools.verify_email(email);
							if (verify) {
								user.setEmail(email);
								this.userService.update(user);
							}
						} else {
							isEmpty = false;
						}
					}
				}
				if (isEmpty) {
					if (verify) {
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
									BuyGift bg = this.buyGiftService
											.getObjById(CommUtil.null2Long(goods.getBuyGift_id()));
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
										code = 1;
										msg = "?????????????????????????????????????????????";
										return Json.toJson(new Result(code, msg), JsonFormat.compact());
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
									request.getSession(false).removeAttribute("cart_session");// ????????????????????????????????????????????????????????????????????????
									/*
									 * if (payType.equals("payafter")) {//
									 * ?????????????????? String pay_session =
									 * CommUtil.randomString(32);
									 * request.getSession(false).setAttribute(
									 * "pay_session", pay_session);
									 * map.put("pay_session", pay_session); }
									 */
									String store_ids[] = store_id.split(",");
									List<Map> child_order_maps = new ArrayList<Map>();
									int whether_gift_in = 0;// ???????????????????????? ????????????????????????
															// ????????????whether_gift??????1
									String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
									double order_amount = 0.0;// ??????????????????
									double discounts_amount = 0.0;// ?????????????????????
									double freight_amount = 0.0;// 
									double total_ship_price = 0;// ????????????????????????
									double payment_amount = 0;// ????????????
									List<Long> cart_order_ids = new ArrayList<Long>();// ????????????id
																						// ???????????????????????????
									// ????????????????????????
									List<GoodsCart> goodsCartList = new ArrayList<GoodsCart>();
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
										boolean trans_flag = false; // ??????????????????
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
												for (GoodsCart gcc : order_carts) { // ?????????://
																					// ??????????????????????????????????????????????????????????????????????????????????????????
													if (gcc.getGoods().getGoods_type() == 1) {// ????????????
														for (String gc_id : gc_ids) {
															GoodsCart goodsCart = this.goodsCartService
																	.getObjById(CommUtil.null2Long(gc_id));
															if (goodsCart != null
																	&& goodsCart.getGoods().getGoods_transfee() == 0) {// ?????????????????????????????????????????????????????????????????????
																trans_flag = true;
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
														json_map.put("ksa_goods_name",
																gc.getGoods().getKsa_goods_name());
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
														goodsCartList.add(gc);

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
														File file = new File(request.getSession().getServletContext()
																.getRealPath("/") + uploadFilePath + File.separator
																+ "snapshoot");
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
														json_map.put("ksa_goods_name",
																gc.getGoods().getKsa_goods_name());
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
																			+ gc.getGoods().getGoods_main_photo()
																					.getName()
																			+ "_small." + gc.getGoods()
																					.getGoods_main_photo().getExt());
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
										// ??????????????????
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
										 * Json.fromJson(Map.class,
										 * storeCouponId); String coupon_id =
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
										Map commission_vat = this.appCartViewTools.getCommission(gc_list);
										double commission = CommUtil.null2Double(commission_vat.get("commission"));
										double vat = CommUtil.null2Double(commission_vat.get("vat"));
										double logistics_vat = CommUtil.mul(
												this.configService.getSysConfig().getLogistics_vat_rate(), ship_price);
										/*
										 * String order_store_id = "0"; if (sid
										 * != "self" && !sid.equals("self")) {
										 * order_store_id =
										 * CommUtil.null2String(store.getId());
										 * }
										 */
										String order_id = "SM" + CommUtil.randomString(5) + user.getId();
										of.setOrder_id(order_id);
										of.setAddTime(new Date());
										// ???????????????????????? -- ??????????????????
										if (address.getArea().getLevel() == 2) {
										/*	of.setReceiver_area(address.getArea().getParent().getParent().getAreaName()
													+ " " + address.getArea().getParent().getAreaName()
													+ address.getArea().getAreaName());*/
										
											of.setReceiver_state(
													address.getArea().getParent().getParent().getAreaName());
											of.setReceiver_area(address.getArea().getParent().getAreaName());
											of.setReceiver_city(address.getArea().getAreaName());
											of.setReceiver_street("");
										} else if (address.getArea().getLevel() == 1) {
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
										of.setTransport_type(trans_flag == true ? "0" : "1");
										of.setTransport(transport);

										of.setOrder_status(10);// ?????????
										of.setPayType(payType);
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
										 * if (CommUtil.null2Int(delivery_type)
										 * == 1 && delivery_id != null &&
										 * !delivery_id.equals("")) {//
										 * ????????????????????????json??????
										 * of.setDelivery_type(1);
										 * DeliveryAddress deliveryAddr =
										 * this.deliveryaddrService
										 * .getObjById(CommUtil.null2Long(
										 * delivery_id)); String service_time =
										 * "??????"; if
										 * (deliveryAddr.getDa_service_type() ==
										 * 1) { service_time =
										 * deliveryAddr.getDa_begin_time() +
										 * "??????" + deliveryAddr.getDa_end_time()
										 * + "???"; } params.clear();
										 * params.put("id",
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
										 * deliveryAddr.getDa_area().getParent()
										 * . getParent().getAreaName() +
										 * deliveryAddr.getDa_area().getParent()
										 * . getAreaName() +
										 * deliveryAddr.getDa_area().getAreaName
										 * () + deliveryAddr.getDa_address());
										 * params.put("da_service_day",
										 * this.DeliveryAddressTools
										 * .query_service_day(deliveryAddr.
										 * getDa_service_day()));
										 * params.put("da_service_time",
										 * service_time);
										 * of.setDelivery_address_id(
										 * deliveryAddr. getId( ));
										 * of.setDelivery_info(Json.toJson(
										 * params, JsonFormat.compact())); }
										 */
										if (oversea_very) {
											params.clear();
											params.put("repository", "1");
											List<ShipAddress> sa = this.shipAddressService.query(
													"select obj from ShipAddress obj where obj.repository=:repository",
													params, -1, -1);
											if (sa.size() > 0)
												of.setShip_addr_id(sa.get(0).getId());
										}
										boolean voucher_flag = false;
										if (i == store_ids.length - 1) {
											of.setOrder_main(1);
											// ??????????????????id Order-water
											params.clear();
											params.put("type", 1);
											List<Game> games = this.gameService.query(
													"SELECT obj FROM Game obj WHERE obj.type=:type", params, -1,
													-1);
											if (games.size() > 0) {
												if (games.get(0) != null && games.get(0).getStatus() == 0) {
													params.clear();
													params.put("status", 0);
													params.put("title", "Order-water");
													List<GameTask> gameTasks = this.gameTaskService.query("SELECT obj FROM GameTask obj WHERE obj.status=:status AND obj.title=:title", params,
															-1, -1);
													if(gameTasks.size() > 0){
														GameTask gameTask = gameTasks.get(0);
														of.setGame_task_id(gameTask.getId());
													}
												}
											}

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
														of.setGeneral_coupon(
																Json.toJson(coupon_map, JsonFormat.compact()));
														of.setCoupon_amount(new BigDecimal(coupon_price));
														discounts_amount += CommUtil.subtract(order_amount,
																coupon_price) >= 0 ? coupon_price : order_amount;
														order_amount = CommUtil.subtract(order_amount,
																coupon_price) >= 0
																		? CommUtil.subtract(order_amount, coupon_price)
																		: order_amount;
													}
												}
											}

											// ???????????? - ??????????????????????????????
											if (this.configService.getSysConfig().isIntegral()) {
												if (CommUtil.null2Int(integral) == 1) {
													int use_integral = user.getIntegral();
													double integral_price = CommUtil.mul(use_integral,
															this.configService.getSysConfig()
																	.getIntegralExchangeRate());
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
																		? CommUtil.subtract(order_amount,
																				integral_price)
																		: order_amount;
														order_amount = CommUtil.subtract(order_amount,
																integral_price) >= 0
																		? CommUtil.subtract(order_amount,
																				integral_price)
																		: order_amount;
														of.setIntegral(new BigDecimal(integral_price));
														// ??????????????????--??????????????????--????????????
													}
												}
											}
											payment_amount = order_amount;
											
											// ?????? ????????????????????????
											List<SysMap> total_sms = this.transportTools.cart_pay_transportation(goodsCartList,
													CommUtil.null2String(address.getArea().getId()));
											for (SysMap sm : total_sms) {
												if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
													total_ship_price = CommUtil.null2Double(sm.getValue());// ??????????????????
												} else {
													store_ship_price = CommUtil.null2Double(sm.getValue());
												}
											}
											// VAT(??????) ?????????????????????vat?????????vat
											Map commission_vat_1 = this.appCartViewTools.getCommission(goodsCartList);
											double commission_1 = CommUtil.null2Double(commission_vat_1.get("commission"));
											double vat_1 = CommUtil.null2Double(commission_vat_1.get("vat"));
											double logistics_vat_1 = CommUtil.mul(
													this.configService.getSysConfig().getLogistics_vat_rate(), total_ship_price);
											// ????????????????????????
											// ??????
											// ??????
											// vat ?????????
											of.setCommission_amount(BigDecimal.valueOf(commission_1));// ???????????????????????????
											of.setCommission_vat(BigDecimal.valueOf(vat_1));
											of.setLogistics_vat(BigDecimal.valueOf(logistics_vat_1));
											of.setShip_price(BigDecimal.valueOf(ship_price));
											of.setStore_ship_price(BigDecimal.valueOf(store_ship_price));
											
											
											// ???????????????-?????????????????????????????????
											boolean enough_free = false;
											if (this.configService.getSysConfig().getEnoughfree_status() == 1) {
												if (CommUtil.subtract(order_amount,
														this.configService.getSysConfig().getEnoughfree_price()) >= 0) {
													// ????????????????????????-???????????????????????????
													of.setEnough_free(1);
													of.setPlatform_ship_price(new BigDecimal(0));
													// ????????????????????????
//													discounts_amount += freight_amount;
													discounts_amount += total_ship_price;
													enough_free = true;
												} else {
													// ????????????????????????
//													payment_amount += freight_amount;
													payment_amount += total_ship_price;
													of.setPlatform_ship_price(new BigDecimal(total_ship_price));
												}
											}
											
											// ?????????
											// ???????????????????????????????????????
											double goodsVoucherPrice = this.appGameAwardTools.queryGoodsVoucher(user, voucher);
											if(goodsVoucherPrice > 0 && voucher){
												payment_amount = CommUtil.subtract(payment_amount, goodsVoucherPrice) >= 0 ? CommUtil.subtract(order_total_price, goodsVoucherPrice) :0;
												of.setGoods_voucher_price(new BigDecimal(goodsVoucherPrice));
												discounts_amount += payment_amount;
												voucher_flag = true;
												// ???????????????????????????
												String message = "Purchase consumption";
												String message_sa = "?????????????? ????????????";
												this.appGoodsVoucherTools.createLog(null, user, 1, 1, message, message_sa, goodsVoucherPrice);
											}
											if (whether_gift_in > 0) {
												of.setWhether_gift(1);
											}
											if (child_order_maps.size() > 0) {
												// ????????????????????????????????????????????????child_order_maps
												List childOrder = new ArrayList();
												if (enough_free) {
													childOrder = this.appCartViewTools
															.updateChildOrder(child_order_maps);
												}
												of.setChild_order_detail(Json.toJson(
														childOrder.size() == 0 ? child_order_maps : childOrder,
														JsonFormat.compact()));
											}
										}
										of.setPayment_amount(new BigDecimal(payment_amount));
										//????????????-???????????????????????????
										if(payment_platform != null && !payment_platform.equals("")){
											BigDecimal UsdTotalPrice = this.appCartViewTools.exchange_rate(payment_platform, of.getPayment_amount());
											map.put("UsdTotalPrice", UsdTotalPrice);
											if(CommUtil.subtract(UsdTotalPrice, 0) <= 0){
												map.put("flag", true);
												of.setOrder_status(20);
											}else{
												map.put("flag", false);
											}
											of.setUsd_payment_amount(UsdTotalPrice);
										}
										of.setDiscounts_amount(new BigDecimal(discounts_amount));
										//
										int order_status = 10;
										if ("payafter".equals(payType)) {
											order_status = 16;
											of.setPayTime(new Date());
											of.setOrder_status(order_status);
										}

										// ??????????????????????????????
										boolean flag = this.orderFormService.save(of);
										if(voucher_flag){
											// ???????????????????????????????????????
											List<GoodsVoucherInfo> goodsVoucherInfoList = this.appGameAwardTools.editGoodsVoucher(user, of);
											of.setGoodsVoucherInfoList(goodsVoucherInfoList);
										}

										if (i == store_ids.length - 1) {// ?????????????????????????????????
											main_order = of;
											map.put("order_id", of.getId());
											map.put("order_num", of.getOrder_id());
											map.put("TotalPrice", of.getPayment_amount());
											
											if (of.getPayType() != null && of.getPayType().equals("payafter")) {
												of.setOrder_status(order_status);
												this.appCartViewTools.updateGoodsInventory(of);// ???????????????????????????????????????????????????????????????

												// ?????????????????????????????????????????????????????????
												int lucky = this.appLuckyDrawTools.getLuckyDraw();
												user.setRaffle(user.getRaffle() + lucky);
												this.userService.update(user);

												// ??????????????????????????????????????????????????????????????????
												StoreLog storeLog = this.storeLogTools
														.getTodayStoreLog(CommUtil.null2Long(of.getStore_id()));
												storeLog.setPlaceorder(storeLog.getPlaceorder() + 1);
												if (this.orderFormTools.queryOrder(of.getStore_name())) {
													storeLog.setRepetition(storeLog.getPlaceorder() == 1 ? 0
															: storeLog.getRepetition() + 1);
												}
												this.storeLogService.update(storeLog);

											}
											cart_order_ids.add(of.getId());// ????????????id???????????????

											// ?????????????????????????????????????????????????????????????????????????????????????????????(?????????????????????0??????????????????????????????????????????????????????)
											if (of.getOrder_main() == 1
													&& !CommUtil.null2String(of.getChild_order_detail()).equals("")) {
												List<Map> maps = this.orderFormTools
														.queryGoodsInfo(of.getChild_order_detail());
												for (Map child_map : maps) {
													OrderForm child = this.orderFormService
															.getObjById(CommUtil.null2Long(child_map.get("order_id")));
													cart_order_ids.add(child.getId());
													child.setOrder_status(order_status);
													child.setPayType(payType);
													child.setPayTime(new Date());
													if(map.get("flag").toString() != null && map.get("flag").toString().equals("true")){
														child.setOrder_status(20);
													}
													this.orderFormService.update(child);
													if (of.getPayType() != null && of.getPayType().equals("COD")) {// ?????????????????????????????????????????????????????????????????????????????????
														this.appCartViewTools.updateGoodsInventory(child);// ??????????????????

														// COD:
														// ?????????????????????????????????????????????????????????
														/*
														 * 1. StoreLog storeLogc
														 * = this.storeLogTools
														 * .getTodayStoreLog(
														 * CommUtil.null2Long(of
														 * .getStore_id()));
														 * storeLogc.
														 * setPlaceorder(
														 * storeLogc.
														 * getPlaceorder() + 1);
														 * if
														 * (this.orderFormTools.
														 * queryOrder(of.
														 * getStore_name())) {
														 * storeLogc.
														 * setRepetition(
														 * storeLogc.
														 * getRepetition() + 1);
														 * }
														 * this.storeLogService.
														 * update(storeLogc);
														 */
														// 2.
														StoreLog log = this.storeLogTools
																.getTodayStoreLog(CommUtil.null2Long(of.getStore_id()));

													}
													// ????????????????????????????????????????????????????????????????????????????????????

													// ????????????
													/*
													 * if (child_order.
													 * getOrder_form() == 0) {
													 * this.msgTools.
													 * sendSmsCharge(
													 * CommUtil.getURL(
													 * request),
													 * "sms_toseller_payafter_pay_ok_notify",
													 * store.getUser().getMobile
													 * (), null,
													 * CommUtil.null2String(
													 * child_order.getId()),
													 * child_order.getStore_id()
													 * ); this.msgTools.
													 * sendEmailCharge
													 * (CommUtil.getURL(
													 * request),
													 * "email_toseller_payafter_pay_ok_notify",
													 * store.getUser().getEmail(
													 * ), null,
													 * CommUtil.null2String(
													 * child_order.getId()),
													 * child_order.getStore_id()
													 * ); }
													 */
												}
											}

										}
										if (flag) {
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
									map.put("cart_order_ids", cart_order_ids);

									// ???????????????????????????????????????????????????
									/*
									 * if (main_order.getOrder_form() == 0) {
									 * this.msgTools.sendEmailCharge(CommUtil.
									 * getURL( request),
									 * "email_tobuyer_order_submit_ok_notify",
									 * buyer.getEmail(), null,
									 * CommUtil.null2String(main_order.getId()),
									 * main_order.getStore_id());
									 * this.msgTools.sendSmsCharge(CommUtil.
									 * getURL( request),
									 * "sms_tobuyer_order_submit_ok_notify",
									 * buyer.getMobile(), null,
									 * CommUtil.null2String(main_order.getId()),
									 * main_order.getStore_id()); } else {
									 * this.msgTools.sendEmailFree(CommUtil.
									 * getURL( request),
									 * "email_tobuyer_order_submit_ok_notify",
									 * buyer.getEmail(), null,
									 * CommUtil.null2String(main_order.getId()))
									 * ; this.msgTools.sendSmsFree(CommUtil.
									 * getURL( request ),
									 * "sms_tobuyer_order_submit_ok_notify",
									 * buyer.getMobile(), null,
									 * CommUtil.null2String(main_order.getId()))
									 * ; }
									 */
									code = 4200;
									msg = "Success";
								} else {// ????????????????????????????????????????????????????????????
									code = 2;
									msg = "?????????????????????";
								}
							} else {
								code = 3;
								msg = "??????????????????";
							}
						} else {
							code = 4;
							msg = "??????????????????";
						}
					} else {
						code = 4400;
						msg = "Email format error";
					}
				} else {
					code = 4403;
					msg = "The mailbox is empty";
				}
			}
		}
		return Json.toJson(new Result(code, msg, map), JsonFormat.compact());
	}

}
