package com.metoo.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.annotations.Parameter;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.ip.IPSeeker;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.JsonUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.GoodsSku;
import com.metoo.foundation.domain.CombinPlan;
import com.metoo.foundation.domain.Consult;
import com.metoo.foundation.domain.ConsultSatis;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.FootPoint;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsLog;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.GoodsTest;
import com.metoo.foundation.domain.GoodsTypeProperty;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.UserGoodsClass;
import com.metoo.foundation.domain.query.ConsultQueryObject;
import com.metoo.foundation.domain.query.EvaluateQueryObject;
import com.metoo.foundation.domain.query.GoodsQueryObject;
import com.metoo.foundation.domain.virtual.GoodsCompareView;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IBuyGiftService;
import com.metoo.foundation.service.ICombinPlanService;
import com.metoo.foundation.service.IConsultSatisService;
import com.metoo.foundation.service.IConsultService;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.IFootPointService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsLogService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;
import com.metoo.foundation.service.IGoodsTestService;
import com.metoo.foundation.service.IGoodsTypePropertyService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreNavigationService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserGoodsClassService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.view.web.tools.ActivityViewTools;
import com.metoo.view.web.tools.AreaViewTools;
import com.metoo.view.web.tools.ConsultViewTools;
import com.metoo.view.web.tools.EvaluateViewTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.IntegralViewTools;
import com.metoo.view.web.tools.StoreViewTools;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 
 * <p>
 * Title: GoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: ?????????????????????,????????????????????????????????????????????????????????????
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: ????????????????????????????????? www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-28
 * 
 * @version koala_b2b2c v2.0 2015??? 
 */
@Controller
public class GoodsViewAction {
	
	
	@Autowired
	private IGoodsTestService goodsTestService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private ConsultViewTools consultViewTools;
	@Autowired
	private EvaluateViewTools evaluateViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IConsultSatisService consultsatisService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	Result result;
	
	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@RequestMapping("/getPartsPrice.htm")
	public void genericCombinPartsPrice(HttpServletRequest request,
			HttpServletResponse response, String parts_ids, String gid) {
		double all_price = 0.00;
		if (gid != null && !gid.equals("")) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(gid));
			all_price = CommUtil.null2Double(obj.getGoods_current_price());
		}
		if (parts_ids != null && !parts_ids.equals("")) {
			String ids[] = parts_ids.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					Goods obj = this.goodsService.getObjById(CommUtil
							.null2Long(id));
					all_price = CommUtil.add(all_price,
							obj.getGoods_current_price());
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(CommUtil.formatMoney(all_price));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * ??????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/goods.htm")
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		String imageWebServer = this.configService.getSysConfig().getImageWebServer();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj != null) {
			if (this.configService.getSysConfig().isSecond_domain_open()) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
				String serverName = request.getServerName().toLowerCase();
				String secondDomain = CommUtil.null2String(serverName
						.substring(0, serverName.indexOf(".")));
				if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
					secondDomain = "www";
				}
				// System.out.println("?????????????????????????????????????????????" + secondDomain);
				if (!secondDomain.equals("")) {
					if (obj.getGoods_type() == 0) {// ??????????????????????????????????????????
						if (!secondDomain.equals("www")) {
							mv = new JModelAndView("error.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "?????????????????????????????????");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/index.htm");
							return mv;
						}
						// System.out.println("???????????????????????????????????????????????????????????????");
					} else {
						if (!obj.getGoods_store().getStore_second_domain()
								.equals(secondDomain)) {
							mv = new JModelAndView("error.html",
									configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "?????????????????????????????????");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/index.htm");
							// System.out.println("???????????????????????????????????????????????????????????????");
							return mv;
						}
					}
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "?????????????????????????????????");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
				}
			}
			
			// ??????cookie????????????????????????
			Cookie[] cookies = request.getCookies();
			Cookie goodscookie = null;
			int k = 0;
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("goodscookie")) {
						String goods_ids = cookie.getValue();
						int m = 6;
						int n = goods_ids.split(",").length;
						if (m > n) {
							m = n + 1;
						}												  
						String[] new_goods_ids = goods_ids.split(",", m);
						for (int i = 0; i < new_goods_ids.length; i++) {
							if ("".equals(new_goods_ids[i])) {
								for (int j = i + 1; j < new_goods_ids.length; j++) {
									new_goods_ids[i] = new_goods_ids[j];
								}
							}
						}
						String[] new_ids = new String[6];
						for (int i = 0; i < m - 1; i++) {
							if (id.equals(new_goods_ids[i])) {
								k++;
							}
						}
						if (k == 0) {
							new_ids[0] = id;
							for (int j = 1; j < m; j++) {
								new_ids[j] = new_goods_ids[j - 1];
							}
							goods_ids = id + ",";
							if (m == 2) {
								for (int i = 1; i <= m - 1; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							} else {
								for (int i = 1; i < m; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							}
							goodscookie = new Cookie("goodscookie", goods_ids);
						} else {
							new_ids = new_goods_ids;
							goods_ids = "";
							for (int i = 0; i < m - 1; i++) {
								goods_ids += new_ids[i] + ",";
							}
							goodscookie = new Cookie("goodscookie", goods_ids);
						}
						goodscookie.setMaxAge(60 * 60 * 24 * 7);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
						break;
					} else {
						goodscookie = new Cookie("goodscookie", id + ",");
						goodscookie.setMaxAge(60 * 60 * 24 * 7);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
					}
				}
			} else {
				goodscookie = new Cookie("goodscookie", id + ",");
				goodscookie.setMaxAge(60 * 60 * 24 * 7);
				goodscookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(goodscookie);
			}
			
			
			User current_user = SecurityUserHolder.getCurrentUser();
			boolean admin_view = false;// ??????????????????????????????????????????????????????
			if (current_user != null) {
				// ????????????????????????????????????
				Map params = new HashMap();
				params.put("fp_date", CommUtil.formatDate(CommUtil
						.formatShortDate(new Date())));
				params.put("fp_user_id", current_user.getId());
				List<FootPoint> fps = this.footPointService
						.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
								params, -1, -1);
				if (fps.size() == 0) {
					
					FootPoint fp = new FootPoint();
					fp.setAddTime(new Date());
					fp.setFp_date(new Date());
					fp.setFp_user_id(current_user.getId());
					fp.setFp_user_name(current_user.getUsername());
					fp.setFp_goods_count(1);
					Map map = new HashMap();
					map.put("goods_id", obj.getId());
					map.put("goods_name", obj.getGoods_name());
					map.put("goods_sale", obj.getGoods_salenum());
					map.put("goods_time", CommUtil.formatLongDate(new Date()));
					map.put("goods_img_path", obj.getGoods_main_photo() != null
							? imageWebServer + "/" + obj.getGoods_main_photo().getPath() + "/"
									+ obj.getGoods_main_photo().getName()
							: imageWebServer
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getPath()
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getName());
					System.out.println();
					map.put("goods_price", obj.getGoods_current_price());
					map.put("goods_class_id",
							CommUtil.null2Long(obj.getGc().getId()));
					map.put("goods_class_name",
							CommUtil.null2String(obj.getGc().getClassName()));
					List<Map> list = new ArrayList<Map>();
					list.add(map);
					fp.setFp_goods_content(Json.toJson(list,
							JsonFormat.compact()));
					this.footPointService.save(fp);
				} else {
					FootPoint fp = fps.get(0);
					List<Map> list = Json.fromJson(List.class,
							fp.getFp_goods_content());
					boolean add = true;
					for (Map map : list) {// ???????????????????????????
						if (CommUtil.null2Long(map.get("goods_id")).equals(
								obj.getId())) {
							add = false;
						}
					}
					if (add) {
						Map map = new HashMap();
						map.put("goods_id", obj.getId());
						map.put("goods_name", obj.getGoods_name());
						map.put("goods_sale", obj.getGoods_salenum());
						map.put("goods_time",
								CommUtil.formatLongDate(new Date()));
						map.put("goods_img_path",
								obj.getGoods_main_photo() != null
										? imageWebServer + "/"
												+ obj.getGoods_main_photo()
														.getPath()
												+ "/"
												+ obj.getGoods_main_photo()
														.getName() : imageWebServer
												+ "/"
												+ this.configService
														.getSysConfig()
														.getGoodsImage()
														.getPath()
												+ "/"
												+ this.configService
														.getSysConfig()
														.getGoodsImage()
														.getName());
						map.put("goods_price", obj.getGoods_current_price());
						map.put("goods_class_id",
								CommUtil.null2Long(obj.getGc().getId()));
						map.put("goods_class_name", CommUtil.null2String(obj
								.getGc().getClassName()));
						list.add(0, map);// ?????????????????????????????????
						fp.setFp_goods_count(list.size());
						fp.setFp_goods_content(Json.toJson(list,
								JsonFormat.compact()));
						this.footPointService.update(fp);
					}
				}
				current_user = this.userService
						.getObjById(current_user.getId());
				if (current_user.getUserRole().equals("ADMIN")) {
					admin_view = true;
				}
			}

			// ????????????????????????
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj
					.getId());
			todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
			String click_from_str = todayGoodsLog.getGoods_click_from();
			Map<String, Integer> clickmap = (click_from_str != null && !click_from_str
					.equals("")) ? (Map<String, Integer>) Json
					.fromJson(click_from_str) : new HashMap<String, Integer>();
			String from = clickfrom_to_chinese(CommUtil.null2String(request
					.getParameter("from")));
			if (from != null && !from.equals("")) {
				if (clickmap.containsKey(from)) {
					clickmap.put(from, clickmap.get(from) + 1);
				} else {
					clickmap.put(from, 1);
				}
			} else {
				if (clickmap.containsKey("unknow")) {
					clickmap.put("unknow", clickmap.get("unknow") + 1);
				} else {
					clickmap.put("unknow", 1);
				}
			}
			todayGoodsLog.setGoods_click_from(Json.toJson(clickmap,
					JsonFormat.compact()));
			this.goodsLogService.update(todayGoodsLog);
			
			if (obj.getGoods_status() == 0 || admin_view) {
				mv = new JModelAndView("default/store_goods.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if (this.configService.getSysConfig().isZtc_status()
						&& obj.getZtc_status() == 2) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				// ??????????????????????????????
				if (obj.getActivity_status() == 1
						|| obj.getActivity_status() == 2) {// ????????????????????????????????????
					if (!CommUtil.null2String(obj.getActivity_goods_id())
							.equals("")) {
						ActivityGoods ag = this.actgoodsService.getObjById(obj
								.getActivity_goods_id());
						if (ag.getAct().getAc_end_time().before(new Date())) {
							ag.setAg_status(-2);
							this.actgoodsService.update(ag);
							obj.setActivity_status(0);
							obj.setActivity_goods_id(null);
						}
					}
				}
				if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// ????????????????????????
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				if (obj.getCombin_status() == 1) {// ????????????????????????
					Map params = new HashMap();
					params.put("endTime", new Date());
					params.put("main_goods_id", obj.getId());
					List<CombinPlan> combins = this.combinplanService
							.query("select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
									params, -1, -1);
					if (combins.size() > 0) {
						for (CombinPlan com : combins) {
							if (com.getCombin_type() == 0) {
								if (obj.getCombin_suit_id().equals(com.getId())) {
									obj.setCombin_suit_id(null);
								}
							} else {
								if (obj.getCombin_parts_id()
										.equals(com.getId())) {
									obj.setCombin_parts_id(null);
								}
							}
							obj.setCombin_status(0);
						}
					}
				}
				if (obj.getOrder_enough_give_status() == 1) {// ???????????????????????????
					BuyGift bg = this.buyGiftService.getObjById(obj
							.getBuyGift_id());
					if (bg != null && bg.getEndTime().before(new Date())) {
						bg.setGift_status(20);
						List<Map> maps = Json.fromJson(List.class,
								bg.getGift_info());
						maps.addAll(Json.fromJson(List.class,
								bg.getGoods_info()));
						for (Map map : maps) {
							Goods goods = this.goodsService.getObjById(CommUtil
									.null2Long(map.get("goods_id")));
							if (goods != null) {
								goods.setOrder_enough_give_status(0);
								goods.setOrder_enough_if_give(0);
								goods.setBuyGift_id(null);
							}
						}
						this.buyGiftService.update(bg);
					}
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGift", true);
					}
				}
				
				if (obj.getOrder_enough_if_give() == 1) {// ?????????????????????????????????
					BuyGift bg = this.buyGiftService.getObjById(obj
							.getBuyGift_id());
					if (bg != null && bg.getGift_status() == 10) {
						mv.addObject("isGive", true);
					}
				}
				if (obj.getEnough_reduce() == 1) {// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(obj
									.getOrder_enough_reduce_id()));
					if (er.getErstatus() == 10
							&& er.getErbegin_time().before(new Date())
							&& er.getErend_time().after(new Date())) {// ????????????
						mv.addObject("enoughreduce", er);
					} else if (er.getErend_time().before(new Date())) {// ?????????
						er.setErstatus(20);
						this.enoughReduceService.update(er);
						String goods_json = er.getErgoods_ids_json();
						List<String> goods_id_list = (List) Json
								.fromJson(goods_json);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService
									.getObjById(CommUtil.null2Long(goods_id));
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id("");
							this.goodsService.update(ergood);
						}
					}
				}
				this.goodsService.update(obj);
				mv.addObject("obj", obj);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("transportTools", transportTools);
				// ???????????????????????????IP???????????????????????????????????????
				String current_ip = CommUtil.getIpAddr(request);// ????????????IP
				// System.out.println("??????IP???"+current_ip);
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					String current_city = ip.getIPLocation(current_ip)
							.getCountry();
					mv.addObject("current_city", current_city);
				} else {
					mv.addObject("current_city", "????????????");
				}
				// ??????????????????
				List<Area> areas = this.areaService
						.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("areas", areas);
				// ????????????
				Map params = new HashMap();
				params.put("parent_id", obj.getGc().getParent().getId());
				params.put("display", true);
				List<GoodsClass> about_gcs = this.goodsClassService
						.query("select new GoodsClass(id,className) from GoodsClass obj where obj.parent.id=:parent_id and obj.display=:display order by sequence asc",
								params, -1, -1);
				mv.addObject("about_gcs", about_gcs);
				mv.addObject("userTools", userTools);
				mv.addObject("goodsViewTools", goodsViewTools);
				mv.addObject("activityViewTools", activityViewTools);
				if (obj.getGoods_type() == 0) {// ??????????????????
				} else {// ????????????
					this.generic_evaluate(obj.getGoods_store(), mv);
					mv.addObject("store", obj.getGoods_store());
				}
				// ???????????????????????????
				EvaluateQueryObject qo = new EvaluateQueryObject("1", mv,
						"addTime", "desc");
				qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id",
						CommUtil.null2Long(id)), "=");
				qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type",
						"goods"), "=");
				qo.addQuery("obj.evaluate_status", new SysMap(
						"evaluate_status", 0), "=");
				qo.setPageSize(10);
				IPageList eva_pList = this.evaluateService.list(qo);
				String url = CommUtil.getURL(request) + "/goods_evaluation.htm";
				mv.addObject("eva_objs", eva_pList.getResult());
				mv.addObject(
						"eva_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "",
								eva_pList.getCurrentPage(),
								eva_pList.getPages()));
				mv.addObject("evaluateViewTools", this.evaluateViewTools);
				mv.addObject("orderFormTools", this.orderFormTools);
				// ???????????????????????????
				qo = new EvaluateQueryObject("1", mv, "addTime", "desc");
				qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id",
						CommUtil.null2Long(id)), "=");
				qo.setPageSize(10);
				IPageList order_eva_pList = this.evaluateService.list(qo);
				url = CommUtil.getURL(request) + "/goods_order.htm";
				mv.addObject("order_objs", order_eva_pList.getResult());
				mv.addObject("order_gotoPageAjaxHTML", CommUtil
						.showPageAjaxHtml(url, "",
								order_eva_pList.getCurrentPage(),
								order_eva_pList.getPages()));
				// ???????????????????????????
				ConsultQueryObject cqo = new ConsultQueryObject("1", mv,
						"addTime", "desc");
				cqo.addQuery("obj.goods_id",
						new SysMap("goods_id", CommUtil.null2Long(id)), "=");
				cqo.setPageSize(10);
				IPageList pList = this.consultService.list(cqo);
				url = CommUtil.getURL(request) + "/goods_consult.htm";
				mv.addObject("consult_objs", pList.getResult());
				mv.addObject(
						"consult_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "",
								pList.getCurrentPage(), pList.getPages()));
				mv.addObject("consultViewTools", this.consultViewTools);
				
				//[??????????????????]
				Goods similarGoods = this.goodsService.getObjById(CommUtil.null2Long(id));
				if(similarGoods != null){
					Map map = new HashMap();
					map.put("goods_status", 0);
					map.put("gc_id", similarGoods.getGc().getId());
					map.put("gid", CommUtil.null2Long(id));
					List<Goods> class_goods = this.goodsService
							.query("select obj from Goods obj where obj.gc.id=:gc_id and obj.id!=:gid and obj.goods_status=:goods_status order by goods_salenum desc",
									map, 0, 9);
					mv.addObject("class_goods", class_goods);
					
				}
				// ??????????????????????????????
				List<Goods> goods_compare_list = (List<Goods>) request
						.getSession(false).getAttribute("goods_compare_cart");
				int compare = 0;// ????????????????????????????????????session???
				if (goods_compare_list != null) {
					for (Goods goods : goods_compare_list) {
						if (goods.getId().equals(obj.getId())) {
							compare = 1;
						}
					}
				} else {
					goods_compare_list = new ArrayList<Goods>();
				}
				// ???????????????????????????????????????????????????????????????????????????????????????
				int compare_goods_flag = 0;// ????????????????????????????????????????????????????????????????????????
				for (Goods compare_goods : goods_compare_list) {
					if (compare_goods != null) {
						compare_goods = this.goodsService
								.getObjById(compare_goods.getId());
						if (!compare_goods
								.getGc()
								.getParent()
								.getParent()
								.getId()
								.equals(obj.getGc().getParent().getParent()
										.getId())) {
							compare_goods_flag = 1;
						}
					}
				}
			
				mv.addObject("compare_goods_flag", compare_goods_flag);
				mv.addObject("goods_compare_list", goods_compare_list);
				mv.addObject("compare", compare);
				// ????????????
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "?????????????????????????????????");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "?????????????????????????????????");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		}
		return mv;
	}


	
	/**
     * ????????????????????????
     * @param key
     * @return
     */
	public String clickfrom_to_chinese(String key) {
		String str = "??????";
		if (key.equals("search")) {
			str = "??????";
		}
		if (key.equals("floor")) {
			str = "????????????";
		}
		if (key.equals("gcase")) {
			str = "??????";
		}

		return str;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/store_goods_list.htm")
	public ModelAndView store_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String brand_ids, String gs_ids,
			String properties, String all_property_status,
			String detail_property_status, String goods_type,
			String goods_inventory, String goods_transfee, String goods_cod) {
		ModelAndView mv = new JModelAndView("store_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		mv.addObject("gc", gc);
		Set gc_list = new TreeSet();
		if (gc != null) {
			if (gc.getLevel() == 0) {
				gc_list = gc.getChilds();
			} else if (gc.getLevel() == 1) {
				gc_list = gc.getParent().getChilds();
			} else if (gc.getLevel() == 2) {
				gc_list = gc.getParent().getParent().getChilds();
			}
		}
		mv.addObject("gc_list", gc_list);
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "addTime";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(null, currentPage, mv,
				orderBy, orderType);
		Set<Long> ids = null;
		if (gc != null) {
			ids = this.genericIds(gc.getId());
		}
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}
		if (goods_cod != null) {
			gqo.addQuery("obj.goods_cod", new SysMap("goods_cod", 0), "=");
			mv.addObject("goods_cod", goods_cod);
		}
		if (goods_transfee != null) {
			gqo.addQuery("obj.goods_transfee", new SysMap("goods_transfee", 1),
					"=");
			mv.addObject("goods_transfee", goods_transfee);
		}
		gqo.setPageSize(24);// ???????????????????????????24?????????
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		List<Map> goods_property = new ArrayList<Map>();
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id",
						CommUtil.null2Long(brand_id)), "=", "and");
				Map map = new HashMap();
				GoodsBrand brand = this.brandService.getObjById(CommUtil
						.null2Long(brand_id));
				if (brand != null) {
					map.put("name", "??????");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				for (int i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					if (i == 0) {
						gqo.addQuery(
								"and (obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "??????");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id) + ")",
								null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "??????");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "??????");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					}
				}
			}
			if (brand_ids != null && !brand_ids.equals("")) {
				mv.addObject("brand_ids", brand_ids);
			}
		}
		if (!CommUtil.null2String(gs_ids).equals("")) {
			List<List<GoodsSpecProperty>> gsp_lists = this.generic_gsp(gs_ids);
			for (int j = 0; j < gsp_lists.size(); j++) {
				List<GoodsSpecProperty> gsp_list = gsp_lists.get(j);
				if (gsp_list.size() == 1) {
					GoodsSpecProperty gsp = gsp_list.get(0);
					gqo.addQuery("gsp" + j, gsp, "obj.goods_specs",
							"member of", "and");
					Map map = new HashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i = 0; i < gsp_list.size(); i++) {
						if (i == 0) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "and(");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp,
									"obj.goods_specs)", "member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						}
					}
				}
			}
			mv.addObject("gs_ids", gs_ids);
		}
		if (!CommUtil.null2String(properties).equals("")) {
			String[] properties_list = properties.substring(1).split("\\|");
			for (int i = 0; i < properties_list.length; i++) {
				String property_info = CommUtil.null2String(properties_list[i]);
				String[] property_info_list = property_info.split(",");
				GoodsTypeProperty gtp = this.goodsTypePropertyService
						.getObjById(CommUtil.null2Long(property_info_list[0]));
				Map p_map = new HashMap();
				p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
				p_map.put("gtp_value" + i, "%" + property_info_list[1].trim()
						+ "%");
				gqo.addQuery("and (obj.goods_property like :gtp_name" + i
						+ " and obj.goods_property like :gtp_value" + i + ")",
						p_map);
				Map map = new HashMap();
				map.put("name", gtp.getName());
				map.put("value", property_info_list[1]);
				map.put("type", "properties");
				map.put("id", gtp.getId());
				goods_property.add(map);
			}
			mv.addObject("properties", properties);
			// ????????????????????????,|1,??????????????????75cm???|2,??????
			List<GoodsTypeProperty> filter_properties = new ArrayList<GoodsTypeProperty>();
			List<String> hc_property_list = new ArrayList<String>();// ????????????????????????????????????????????????????????????
			if (gc.getGoodsType() != null) {
				for (GoodsTypeProperty gtp : gc.getGoodsType().getProperties()) {
					// System.out.println(gtp.getName() + "," + gtp.getValue());
					boolean flag = true;
					GoodsTypeProperty gtp1 = new GoodsTypeProperty();
					gtp1.setDisplay(gtp.isDisplay());
					gtp1.setGoodsType(gtp.getGoodsType());
					gtp1.setHc_value(gtp.getHc_value());
					gtp1.setId(gtp.getId());
					gtp1.setName(gtp.getName());
					gtp1.setSequence(gtp.getSequence());
					gtp1.setValue(gtp.getValue());
					for (String hc_property : hc_property_list) {
						String[] hc_list = hc_property.split(":");
						if (hc_list[0].equals(gtp.getName())) {
							String[] hc_temp_list = hc_list[1].split(",");
							String[] defalut_list_value = gtp1.getValue()
									.split(",");
							ArrayList<String> defalut_list = new ArrayList<String>(
									Arrays.asList(defalut_list_value));
							for (String hc_temp : hc_temp_list) {
								defalut_list.remove(hc_temp);
							}
							String value = "";
							for (int i = defalut_list.size() - 1; i >= 0; i--) {
								value = defalut_list.get(i) + "," + value;
							}
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}

					}
					if (flag) {
						if (!CommUtil.null2String(gtp.getHc_value()).equals("")) {// ??????????????????
							// System.out.println(gtp.getHc_value());
							String[] list1 = gtp.getHc_value().split("#");
							for (int i = 0; i < properties_list.length; i++) {
								String property_info = CommUtil
										.null2String(properties_list[i]);
								String[] property_info_list = property_info
										.split(",");
								if (property_info_list[1].equals(list1[0])) {// ???????????????????????????????????????
									hc_property_list.add(list1[1]);
								}
							}

						}
						filter_properties.add(gtp);
					} else {
						filter_properties.add(gtp1);
					}
				}
				mv.addObject("filter_properties", filter_properties);
			}
		} else {
			// ????????????????????????
			mv.addObject("filter_properties", gc.getGoodsType() != null ? gc
					.getGoodsType().getProperties() : "");
		}
		if (CommUtil.null2Int(goods_inventory) == 0) {// ??????????????????0
			gqo.addQuery("obj.goods_inventory",
					new SysMap("goods_inventory", 0), ">");
		}
		if (!CommUtil.null2String(goods_type).equals("")
				&& CommUtil.null2Int(goods_type) != -1) {// ??????????????????????????????????????????
			gqo.addQuery("obj.goods_type",
					new SysMap("goods_type", CommUtil.null2Int(goods_type)),
					"=");
		}
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", pList.getRowCount());
		// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		if (this.configService.getSysConfig().isZtc_status()) {
			// ????????????10??????????????????3???????????????
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				// ??????????????????????????????????????????????????????id??????????????????????????????,?????????????????????????????????????????????????????????
				List all_left_ztc_goods = this.goodsService
						.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
						left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				// ??????????????????????????????????????????????????????id??????????????????????????????,?????????????????????????????????????????????????????????
				List all_left_ztc_goods = this.goodsService
						.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, -1, -1);
				left_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
						left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// ????????????,????????????3?????????
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				top_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map2, 0, 3);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				top_ztc_goods = this.goodsService
						.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) "
								+ "order by obj.ztc_dredge_price desc",
								ztc_map2, 0, 3);
			}
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			// ??????????????????????????????????????????????????????id??????????????????????????????,?????????????????????????????????????????????????????????
			List all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if (detail_property_status != null
				&& !detail_property_status.equals("")) {
			mv.addObject("detail_property_status", detail_property_status);
			String temp_str[] = detail_property_status.split(",");
			Map pro_map = new HashMap();
			List pro_list = new ArrayList();
			for (String property_status : temp_str) {
				if (property_status != null && !property_status.equals("")) {
					String mark[] = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		// ???????????????????????????IP???????????????????????????????????????
		String current_ip = CommUtil.getIpAddr(request);// ????????????IP
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			mv.addObject("current_city", current_city);
		} else {
			mv.addObject("current_city", "????????????");
		}
		mv.addObject("goods_inventory", CommUtil.null2Int(goods_inventory));
		mv.addObject("goods_type", CommUtil.null2String(goods_type).equals("")
				? -1
				: CommUtil.null2Int(goods_type));
		mv.addObject("userTools", userTools);
		// ??????????????????????????????
		List<Goods> goods_compare_list = (List<Goods>) request
				.getSession(false).getAttribute("goods_compare_cart");
		// ???????????????????????????????????????????????????????????????????????????????????????
		if (goods_compare_list == null) {
			goods_compare_list = new ArrayList<Goods>();
		}
		int compare_goods_flag = 0;// ????????????????????????????????????????????????????????????????????????
		for (Goods compare_goods : goods_compare_list) {
			if (compare_goods != null) {
				compare_goods = this.goodsService.getObjById(compare_goods
						.getId());
				if (!compare_goods.getGc().getParent().getParent().getId()
						.equals(CommUtil.null2Long(gc_id))) {
					compare_goods_flag = 1;
				}
			}
		}
		mv.addObject("compare_goods_flag", compare_goods_flag);
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * ????????????????????????????????????????????? ?????????????????????$!httpInclude.include("/goods_list_bottom.htm")
	 * ??????????????????,????????????20?????????
	 */
	@RequestMapping("/goods_list_bottom.htm")
	public ModelAndView goods_list_bottom(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_list_bottom.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// ???????????? ??????cookie??????????????? ???????????? ????????????cookie??????????????????
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					your_like_goods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not "
									+ goods.getId()
									+ " order by obj.goods_salenum desc", null,
									0, 20);
					int gcs_size = your_like_goods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService
								.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.id is not "
										+ goods.getId()
										+ " order by obj.goods_salenum desc",
										null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// ??????????????????
							int k = 0;
							for (int j = 0; j < your_like_goods.size(); j++) {
								if (like_goods.get(i).getId()
										.equals(your_like_goods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								your_like_goods.add(like_goods.get(i));
							}
						}
					}
					
					break;
				} else {
					your_like_goods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);
		List<Goods> goods_last = new ArrayList<Goods>();
		Cookie[] cookies_last = request.getCookies();
		Map params = new HashMap();
		Set<Long> ids = new HashSet<Long>();
		if (cookies_last != null) {
			for (Cookie co : cookies_last) {
				if (co.getName().equals("goodscookie")) {
					String[] goods_id = co.getValue().split(",");
					int j = 4;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						ids.add(CommUtil.null2Long(goods_id[i]));
					}
				}
			}
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			goods_last = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id in(:ids)",
							params, -1, -1);
		}
		mv.addObject("goods_last", goods_last);
		return mv;
	}

	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		for (String gd_id_info : gs_id_list) {
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService
					.getObjById(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId()
							.equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_evaluation.htm")
	public ModelAndView goods_evaluation(HttpServletRequest request,
			HttpServletResponse response,String goods_id,
			String currentPage, String goods_eva) {
		ModelAndView mv = new JModelAndView("default/goods_evaluation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"),
				"=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0),
				"=");
		qo.setPageSize(10);
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_eva).equals("")) {
			if (goods_eva.equals("100")) {
				qo.addQuery("obj.evaluate_photos", new SysMap(
						"evaluate_photos", ""), "!=");
			} else {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap(
						"evaluate_buyer_val", CommUtil.null2Int(goods_eva)),
						"=");
			}
		}
		IPageList pList = this.evaluateService.list(qo);
		String url = CommUtil.getURL(request) + "/goods_evaluation.htm";
		mv.addObject("eva_objs", pList.getResult());
		mv.addObject("eva_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}

	@RequestMapping("/goods_detail.htm")
	public ModelAndView goods_detail(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("default/goods_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	@RequestMapping("/goods_order.htm")
	public ModelAndView goods_order(HttpServletRequest request,
			HttpServletResponse response,String goods_id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");

		qo.setPageSize(10);
		IPageList order_eva_pList = this.evaluateService.list(qo);
		String url = CommUtil.getURL(request) + "/goods_order.htm";
		mv.addObject("order_objs", order_eva_pList.getResult());
		mv.addObject(
				"order_gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url, "",
						order_eva_pList.getCurrentPage(),
						order_eva_pList.getPages()));

		return mv;
	}

	@RequestMapping("/goods_consult.htm")
	public ModelAndView goods_consult(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String consult_type,
			String currentPage) {
		ModelAndView mv = new JModelAndView("default/goods_consult.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.goods_id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		if (!CommUtil.null2String(consult_type).equals("")) {
			qo.addQuery("obj.consult_type", new SysMap("consult_type",
					consult_type), "=");
		}
		qo.setPageSize(10);
		IPageList pList = this.consultService.list(qo);
		String url2 = CommUtil.getURL(request) + "/goods_consult.htm";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
				url2, "", pList.getCurrentPage(), pList.getPages()));

		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}
	
	@RequestMapping("/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
		int count = 0;
		double price = 0;
		double act_price = 0;
		double current = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// ??????????????????????????????????????????
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						current = CommUtil.null2Int(temp.get("supp"));
						count = CommUtil.null2Int(temp.get("count"));
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2
				&& SecurityUserHolder.getCurrentUser() != null) {// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods
					.getActivity_goods_id());
			// 0???????????????1???????????????2???????????????3???????????????
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil
					.null2String(SecurityUserHolder.getCurrentUser().getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			act_price = CommUtil.mul(rebate, price);
		}
		map.put("current", current);
		map.put("count", count);
		map.put("price", CommUtil.formatMoney(price));
		if (act_price != 0) {
			map.put("act_price", CommUtil.formatMoney(act_price));
		}
		Result result = new Result(0,"success",map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
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
	
	@RequestMapping("/trans_fee.htm")
	public void trans_fee(HttpServletRequest request,
			HttpServletResponse response, String city_name, String goods_id) {
		Map map = new HashMap();
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		float mail_fee = 0;
		float express_fee = 0;
		float ems_fee = 0;
		if (goods != null && goods.getTransport() != null) {
			mail_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "mail",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()),
					"express", CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "ems",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("mail_fee", mail_fee);
		map.put("express_fee", express_fee);
		map.put("ems_fee", ems_fee);
		map.put("current_city_info", CommUtil.substring(city_name, 5));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/goods_share.htm")
	public ModelAndView goods_share(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_share.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet<Long>();
		if (id != null) {
			ids.add(id);
			Map params = new HashMap();
			params.put("pid", id);
			List id_list = this.goodsClassService
					.query("select obj.id from GoodsClass obj where obj.parent.id=:pid",
							params, -1, -1);
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}

	@RequestMapping("/goods_consult_win.htm")
	public ModelAndView goods_consult_win(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("default/goods_consult_win.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("goods_id", goods_id);
		mv.addObject("time", CommUtil.formatLongDate(new Date()));
		return mv;
	}

	@RequestMapping("/goods_consult_save.htm")
	public ModelAndView goods_consult_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String consult_content, String consult_type, String consult_code) {
		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_code"));
		boolean visit_consult = true;
		if (!this.configService.getSysConfig().isVisitorConsult()) {
			if (SecurityUserHolder.getCurrentUser() == null) {
				visit_consult = false;
			}
		}
		boolean save_ret = true;
		if (visit_consult) {
			if (CommUtil.null2String(consult_code).equals(verify_code)) {
				Consult obj = new Consult();
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(goods_id));
				obj.setAddTime(new Date());
				obj.setConsult_type(consult_type);
				obj.setConsult_content(consult_content);
				User user = SecurityUserHolder.getCurrentUser();
				if (user != null) {
					obj.setConsult_user_id(user.getId());
					obj.setConsult_user_name(user.getUserName());
					obj.setConsult_email(user.getEmail());
				} else {
					obj.setConsult_user_name("??????");
				}
				List<Map> maps = new ArrayList<Map>();
				Map map = new HashMap();
				map.put("goods_id", goods.getId());
				map.put("goods_name", goods.getGoods_name());
				map.put("goods_main_photo", goods.getGoods_main_photo()
						.getPath()
						+ "/"
						+ goods.getGoods_main_photo().getName()
						+ "_small."
						+ goods.getGoods_main_photo().getExt());
				map.put("goods_price", goods.getGoods_current_price());
				String goods_domainPath = CommUtil.getURL(request) + "/goods_"
						+ goods.getId() + ".htm";
				if (this.configService.getSysConfig().isSecond_domain_open()
						&& goods.getGoods_store() != null
						&& goods.getGoods_store().getStore_second_domain() != ""
						&& goods.getGoods_type() == 1) {
					String store_second_domain = "http://"
							+ goods.getGoods_store().getStore_second_domain()
							+ "." + CommUtil.generic_domain(request);
					goods_domainPath = store_second_domain + "/goods_"
							+ goods.getId() + ".htm";
				}
				map.put("goods_domainPath", goods_domainPath);// ????????????????????????
				maps.add(map);
				obj.setGoods_info(Json.toJson(maps, JsonFormat.compact()));
				obj.setGoods_id(goods.getId());
				if (goods.getGoods_store() != null) {
					obj.setStore_id(goods.getGoods_store().getId());
					obj.setStore_name(goods.getGoods_store().getStore_name());
				} else {
					obj.setWhether_self(1);
				}
				save_ret = this.consultService.save(obj);
				request.getSession(false).removeAttribute("consult_code");
			}
		}
		ModelAndView mv = new JModelAndView("default/goods_consult.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ConsultQueryObject qo = new ConsultQueryObject("1", mv, "addTime",
				"desc");
		qo.addQuery("obj.goods_id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		if (!CommUtil.null2String(consult_type).equals("")) {
			qo.addQuery("obj.consult_type", new SysMap("consult_type",
					consult_type), "=");
		}

		qo.setPageSize(10);
		IPageList pList = this.consultService.list(qo);
		String url2 = CommUtil.getURL(request) + "/goods_consult.htm";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
				url2, "", pList.getCurrentPage(), pList.getPages()));

		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;

	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping("/goods_consult_satisfy.htm")
	public void goods_consult_satisfy(HttpServletRequest request,
			HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		Consult obj = this.consultService.getObjById(CommUtil
				.null2Long(consult_id));
		if (user != null) {// ???????????????????????????????????????id???????????????????????????????????????
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService
					.query("select obj from ConsultSatis obj where obj.cs_user_id=:user_id and obj.cs_consult_id=:cs_consult_id",
							params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.save(cs);
				//
				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.update(obj);
			}
		} else {// ?????????????????????ip??????????????????ip??????????????????????????????????????????
			Map params = new HashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService
					.query("select obj from ConsultSatis obj where obj.cs_ip=:cs_ip and obj.cs_consult_id=:cs_consult_id",
							params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				this.consultsatisService.save(cs);
				//
				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.update(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.getSatisfy());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping("/goods_consult_unsatisfy.htm")
	public void goods_consult_unsatisfy(HttpServletRequest request,
			HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		Consult obj = this.consultService.getObjById(CommUtil
				.null2Long(consult_id));
		if (user != null) {// ???????????????????????????????????????id???????????????????????????????????????
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService
					.query("select obj from ConsultSatis obj where obj.cs_user_id=:user_id and obj.cs_consult_id=:cs_consult_id",
							params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.save(cs);
				//
				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.update(obj);
			}
		} else {// ?????????????????????ip??????????????????ip??????????????????????????????????????????
			Map params = new HashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			List<ConsultSatis> css = this.consultsatisService
					.query("select obj from ConsultSatis obj where obj.cs_ip=:cs_ip and obj.cs_consult_id=:cs_consult_id",
							params, -1, -1);
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				this.consultsatisService.save(cs);
				//
				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.update(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.getUnsatisfy());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ????????????????????????
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService
				.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());//[????????????????????????????????????????????????]
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());//[????????????????????????????????????????????????]
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());//[????????????????????????????????????????????????]

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());//[??????????????????]
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());//[??????????????????]
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());//[??????????????????]
			// ???????????????????????????
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100
							? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "value_strong");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100)>100?100
							:CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "value_light");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)>100?100
							:CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "value_light");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}

	/**
	 * ?????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_compare.htm")
	public ModelAndView goods_compare(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_compare.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession()
				.getAttribute("goods_compare_cart");
		List<GoodsCompareView> goods_compare_list = new ArrayList<GoodsCompareView>();
		if (goods_compare_cart != null && goods_compare_cart.size() > 0) {
			Goods goods = goods_compare_cart.get(goods_compare_cart.size() - 1);
			goods = this.goodsService.getObjById(goods.getId());
			GoodsClass gc = goods.getGc();
			if (goods.getGc().getParent() != null) {
				gc = goods.getGc().getParent();
			}
			if (goods.getGc().getParent() != null
					&& goods.getGc().getParent().getParent() != null) {
				gc = goods.getGc().getParent().getParent();
			}
			mv.addObject("gc", gc);
			// 20150227add ??????????????????????????????????????????
			List<GoodsTypeProperty> gtps = null;
			if (gc.getGoodsType() != null) {
				gtps = gc.getGoodsType().getProperties();
			}
			mv.addObject("gtps", gtps);
			for (Goods obj : goods_compare_cart) {
				obj = this.goodsService.getObjById(obj.getId());
				GoodsCompareView gcv = new GoodsCompareView();
				gcv.setBad_evaluate(CommUtil.mul(obj.getBad_evaluate(), 100)
						+ "%");
				gcv.setGoods_brand(obj.getGoods_brand() != null ? obj
						.getGoods_brand().getName() : "-----");
				gcv.setGoods_cod(obj.getGoods_cod() == 0 ? "??????" : "?????????");
				gcv.setGoods_id(obj.getId());
				gcv.setGoods_img(this.generic_goods_img(request, obj));
				gcv.setGoods_name(obj.getGoods_name());
				gcv.setGoods_price(obj.getGoods_current_price());
				gcv.setGoods_url(this.generic_goods_url(request, obj));
				gcv.setGoods_weight(obj.getGoods_weight());
				gcv.setMiddle_evaluate(CommUtil.mul(obj.getMiddle_evaluate(),
						100) + "%");
				gcv.setTax_invoice(obj.getTax_invoice() == 0 ? "?????????" : "??????");
				gcv.setWell_evaluate(CommUtil.mul(obj.getWell_evaluate(), 100)
						+ "%");
				if (gtps != null) {
					List<Map> list = Json.fromJson(List.class,
							obj.getGoods_property());
					Map gcv_props = new HashMap();
					for (GoodsTypeProperty gtp : gtps) {
						for (Map map : list) {
							if (CommUtil.null2Long(map.get("id")).equals(
									gtp.getId())) {
								if (!CommUtil.null2String(map.get("val"))
										.equals("")) {
									gcv_props
											.put(gtp.getName(), map.get("val"));
								} else {
									gcv_props.put(gtp.getName(), "-----");
								}
							}
						}
					}
					gcv.setProps(gcv_props);
				}
				goods_compare_list.add(gcv);
			}

		}
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param obj
	 * @return
	 */
	private String generic_goods_img(HttpServletRequest request, Goods obj) {
		String img = "";
		if (obj.getGoods_main_photo() != null) {
			img = CommUtil.getURL(request) + "/"
					+ obj.getGoods_main_photo().getPath() + "/"
					+ obj.getGoods_main_photo().getName() + "_middle."
					+ obj.getGoods_main_photo().getExt();
		} else {
			img = CommUtil.getURL(request)
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
		}
		return img;
	}

	/**
	 * ????????????url
	 * 
	 * @param request
	 * @param obj
	 * @return
	 */
	private String generic_goods_url(HttpServletRequest request, Goods obj) {
		String url = CommUtil.getURL(request) + "/goods_" + obj.getId()
				+ ".htm";
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& obj.getGoods_type() == 1
				&& !CommUtil.null2String(
						obj.getGoods_store().getStore_second_domain()).equals(
						"")) {
			url = "http://" + obj.getGoods_store().getStore_second_domain()
					+ "." + CommUtil.generic_domain(request) + "/" + "/goods_"
					+ obj.getId() + ".htm";
		}
		return url;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/add_goods_compare_cart.htm")
	public ModelAndView add_goods_compare_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession()
				.getAttribute("goods_compare_cart");
		if (goods_compare_cart == null) {
			goods_compare_cart = new ArrayList<Goods>();
		}
		if (goods_compare_cart.size() < 4) {
			Goods obj = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			boolean add = true;
			for (Goods goods : goods_compare_cart) {
				if (goods.getId().equals(obj.getId())) {
					add = false;
				}
			}
			if (add)
				goods_compare_cart.add(0, obj);
		}
		request.getSession(false).setAttribute("goods_compare_cart",
				goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_goods_compare_cart.htm")
	public ModelAndView remove_goods_compare_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession()
				.getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		request.getSession(false).setAttribute("goods_compare_cart",
				goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_all_goods_compare_cart.htm")
	public ModelAndView remove_all_goods_compare_cart(
			HttpServletRequest request, HttpServletResponse response) {
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession()
				.getAttribute("goods_compare_cart");
		goods_compare_cart.clear();
		request.getSession(false).removeAttribute("goods_compare_cart");
		ModelAndView mv = new JModelAndView("goods_compare_cart_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping("/remove_goods_compart.htm")
	public String remove_goods_compart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		List<Goods> goods_compare_cart = (List<Goods>) request.getSession()
				.getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		request.getSession(false).setAttribute("goods_compare_cart",
				goods_compare_cart);
		return "redirect:goods_compare.htm";
	}
}
