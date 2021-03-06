package com.metoo.view.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Article;
import com.metoo.foundation.domain.ArticleClass;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GoodsCase;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsFloor;
import com.metoo.foundation.domain.Message;
import com.metoo.foundation.domain.Partner;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IArticleClassService;
import com.metoo.foundation.service.IArticleService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsCaseService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsFloorService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGroupGoodsService;
import com.metoo.foundation.service.IGroupService;
import com.metoo.foundation.service.IIntegralGoodsCartService;
import com.metoo.foundation.service.IMessageService;
import com.metoo.foundation.service.INavigationService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IPartnerService;
import com.metoo.foundation.service.IRoleService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.app.buyer.domain.Result;
import com.metoo.view.web.tools.GoodsCaseViewTools;
import com.metoo.view.web.tools.GoodsClassViewTools;
import com.metoo.view.web.tools.GoodsFloorViewTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.NavViewTools;
import com.metoo.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: Complaint.java
 * </p>
 * 
 * <p>
 * Description:?????????????????????
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ????????????????????????????????? www.metoo.com
 * </p>
 * 
 * @author erikzhang???jinxinzhe???hezeng
 * 
 * @date 2014-4-25
 * 
 * @version metoo_b2b2c v2.0 2015??? 
 */
@Controller
public class IndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCaseService goodsCaseService;
	@Autowired
	private GoodsCaseViewTools goodsCaseViewTools;
	@Autowired
	private IIntegralGoodsCartService integralGoodsCartService;
	@Autowired
	private IGoodsClassService gcService;

	private int index_recommend_count = 5;// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	
	@RequestMapping("/query_gc.htm")
	public String query_gc(String pid, int count) {
		Result result = new Result();
		Map gcsMap = new HashMap();
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("display", true);
		
		if (!CommUtil.null2String(pid).equals("")) {
			params.put("pid", CommUtil.null2Long(pid));
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc",
							params, 0, count);
		} else {
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, 0, count);
			List<Map> arealist = new ArrayList<Map>();
			for(GoodsClass goodsClass:gcs){
				Map gcMap = new HashMap();
				gcMap.put("id", goodsClass.getId());
				gcMap.put("gcname", goodsClass.getClassName());
				arealist.add(gcMap);
				gcsMap.put("gcs", arealist);
			}
		}
		
		if(!gcsMap.isEmpty()){
			result = new Result(0,"????????????",gcsMap);
		}else{
			result = new Result(1,"??????????????????????????????");
		}
		String addressTemp = Json.toJson(result, JsonFormat.compact());
		return addressTemp;
	}

	@RequestMapping("/query_gcs.htm")
	public ModelAndView query_gcs(HttpServletRequest request,
			HttpServletResponse response,String pid, int count) {
		ModelAndView mv = new JModelAndView("gcs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("display", true);
		
		if (!CommUtil.null2String(pid).equals("")) {
			params.put("pid", CommUtil.null2Long(pid));
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc",
							params, 0, count);
			mv.addObject("gcs",gcs);
		} else {
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, 0, count);
			mv.addObject("gcs", gcs);
		}
		
		return mv;
	}
	@RequestMapping("/query_gcs_list.htm")
	public List<GoodsClass> query_gcs_list(HttpServletRequest request,
			HttpServletResponse response,String pid, int count) {
		
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("display", true);
		
		if (!CommUtil.null2String(pid).equals("")) {
			params.put("pid", CommUtil.null2Long(pid));
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc",
							params, 0, count);
		} else {
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, 0, count);
		}
		
		return gcs;
	}
	
	/**
	 * ????????????????????????????????????????????????httpInclude.include("/top.htm")??????????????????
	 * 
	 * @param request
	 * @return
	 */
/*	@RequestMapping("/top.htm")
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}
	*/
	/**
	 * ?????????????????????????????????????????????????????????httpInclude.include("/nav.htm")??????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String filePathGoodsClass = this.configService.getSysConfig()
				.getUploadFilePath()+"/"+"GoodsClass";
		String filePathIndexSubject = this.configService.getSysConfig()
				.getUploadFilePath()+"/"+"index_subject";
		mv.addObject("filePathGoodsClass", filePathGoodsClass);
		mv.addObject("filePathIndexSubject", filePathIndexSubject);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		
		return mv;
	}
	

	/**
	 * ???????????????????????????????????????????????????????????????httpInclude.include("/nav1.htm")??????????????????
//	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/nav1.htm")
	public ModelAndView nav1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("nav1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		String id = CommUtil.null2String(request.getAttribute("id"));
		mv.addObject("op", "/" + op + "/index.htm");
		if ("activity".equals(op) && id != null && !id.equals("")) {
			mv.addObject("op", "activity/index_" + id + ".htm");
		}
		if ("subject".equals(op) && id != null && !id.equals("")) {
			mv.addObject("op", "subject/view_" + id + ".htm");
		}
		return mv;
	}
	
	
	/*public ModelAndView_spec_info(HttpServlerRequest request, HttpServletRespose response, ){
		
	}*/

	/**
	 * ????????????head?????????????????????logo???????????????????????????????????????httpInclude.include("/head.htm")??????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/head.htm")
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// ?????????????????????
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// ??????????????????cookie?????????
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// ??????????????????user?????????
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// ??????????????????????????????????????????carts_cookie??????????????????????????????????????????
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {// ????????????????????????
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.delete(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		} else {
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		}
		// ???cookie????????????user??????????????????????????????
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// ???cookie??????????????????cart_list???
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {// ???carts_cookie?????????cart_list???
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {// ???carts_user?????????cart_list???
			carts_list.add(gc);
		}
		// ????????????????????????????????????????????????,?????????????????????????????????
		List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts_list) {
			if (gc.getCart_type() != null && gc.getCart_type().equals("combin")) {
				if (gc.getCombin_main() != 1) {
					combin_carts_list.add(gc);
				}
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
		return mv;
	}

	/**
	 * ??????????????????????????????include??????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/login_head.htm")
	public ModelAndView login_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("login_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping("/floor.htm")
	public ModelAndView floor(HttpServletRequest request,
			HttpServletResponse response) throws InterruptedException {
		ModelAndView mv = new JModelAndView("floor.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		mv.addObject("floors", floors);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	/**
	 * ??????????????????????????????????????????????????????httpInclude.include("/footer.htm")??????????????????
	 * 
	 * @param request
	 * @return
	 */
	/*@RequestMapping("/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		Map params = new HashMap();
		Set marks = new TreeSet();// ?????????????????????4??????????????????????????????????????????erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shanjiaxuzhi");
		marks.add("chatting_article");
		marks.add("Discovery");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
	
		mv.addObject("acs", acs);
		return mv;
	}*/

	
	/*@RequestMapping("/uae/index.htm")
	public ModelAndView index_uae_ar(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
		
	}*/
	
	/**
	 * ????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.clear();
		List<Partner> img_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("text_partners", text_partners);
		params.clear();
		Set marks = new TreeSet();// ?????????????????????4??????????????????????????????????????????erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shanjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("acs", acs);
		params.clear();
		String[] class_marks = { "gonggao", "guize", "anquan", "zhinan" }; // ????????????????????????????????????????????????????????????
		Map articles = new LinkedHashMap();
		for (String m : class_marks) {
			params.put("class_mark", m);
			params.put("display", true);
			params.put("type", "user");
			List<Article> article = this.articleService
					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.type=:type and obj.display=:display order by obj.addTime desc",
							params, 0, 3);
			articles.put(m, article);
		}
		mv.addObject("articles", articles);
		mv.addObject("goodsCaseViewTools", goodsCaseViewTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		params.clear();
		params.put("show_index", true);
		params.put("audit", 1);
		List<GoodsBrand> goodsBrands = this.goodsBrandService
				.query("select new GoodsBrand(id,name,brandLogo) from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
						params, 0, 10);
		int i =goodsBrands.size();
		mv.addObject("goodsBrands", goodsBrands);
		mv.addObject("navTools", navTools);
		// ????????????6????????????????????????,?????????????????????????????????????????????????????????????????????
		/*params.clear();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("group_type", 0);
		List<Group> groups = this.groupService
				.query("select obj.id from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
						params, -1, -1);
		List<GroupGoods> group_goods_list = new ArrayList<GroupGoods>();
		if (groups.size() > 0) {
			params.clear();
			params.put("group_id", CommUtil.null2Long(groups.get(0)));
			params.put("gg_status", 1);
			group_goods_list = this.groupGoodsService
					.query("select new GroupGoods(id,gg_price,gg_img) from GroupGoods obj where obj.group.id=:group_id and obj.gg_status=:gg_status order by obj.gg_selled_count desc",
							params, 0, 6);
		}
		mv.addObject("group_goods_list", group_goods_list);*/
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("close.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	@RequestMapping("/404.htm")
	public ModelAndView error404(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/404.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		if (userAgent == null || userAgent.indexOf("Mobile") == -1) {
			mv = new JModelAndView("404.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("navTools", navTools);
		return mv;
	}

	@RequestMapping("/500.htm")
	public ModelAndView error500(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/500.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userAgent = request.getHeader("user-agent");
		mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		if (userAgent == null || userAgent.indexOf("Mobile") == -1) {
			mv = new JModelAndView("500.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		mv.addObject("navTools", navTools);
		return mv;
	}
	
	/*@RequestMapping("/500.htm")
	public String error500(HttpServletRequest request,
			HttpServletResponse response) {
		int code = -1;
		String msg = "";
		String userAgent = request.getHeader("user-agent");
		if (userAgent == null || userAgent.indexOf("Mobile") > 0) {
			code = 500;
			msg = "";
		}
		return JSONObject.toJSONString(new Result(code,msg));
	}*/
	/**
	 * ???????????????????????????
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_class.htm")
	public ModelAndView goods_class(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_class.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		params.put("recommend", true);
		List<GoodsClass> recommend_gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.recommend=:recommend order by obj.sequence asc",
						params, 0, 7);
		mv.addObject("gcs", gcs);
		mv.addObject("recommend_gcs", recommend_gcs);
		return mv;
	}

	@RequestMapping("/switch_case_goods.htm")
	public ModelAndView switch_case_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_random, String caseid) {
		ModelAndView mv = new JModelAndView("switch_case_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCase goodscase = this.goodsCaseService.getObjById(CommUtil
				.null2Long(caseid));
		List list = (List) Json.fromJson(goodscase.getCase_content());
		List<Goods> goods_list = new ArrayList<Goods>();
		int length = list.size();
		if (length > 6) {
			int begin = CommUtil.null2Int(goods_random) * 6;
			int i = 0;
			while (i < 6) {
				long id = CommUtil.null2Long(list.get((begin + i) % length));
				Map params = new HashMap();
				params.put("id", CommUtil.null2Long(id));
				List<Goods> objs = this.goodsService
						.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
								params, 0, 1);
				if (objs.size() > 0) {
					goods_list.add(objs.get(0));
				}
				i++;
			}
		} else {
			for (Object id : list) {
				Map params = new HashMap();
				params.put("id", CommUtil.null2Long(id));
				List<Goods> objs = this.goodsService
						.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
								params, 0, 1);
				if (objs.size() > 0) {
					goods_list.add(objs.get(0));
				}
			}
		}
		mv.addObject("goods", goods_list);
		return mv;
	}

	/**
	 * ??????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/outline.htm")
	public ModelAndView outline(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "???????????????????????????????????????????????????");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}

	/**
	 * ajax??????head?????????????????? ??????cookie?????????????????????
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/head_ajax_usercenter.htm")
	public ModelAndView head_ajax_usercenter(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("head_ajax_usercenter.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
			int[] status = new int[] { 10, 30 }; // ????????? ????????? ?????????
			for (int i = 0; i < status.length; i++) {
				int size = this.orderFormService.query(
						"select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id="
								+ user.getId().toString()
								+ " and obj.order_status =" + status[i] + "",
						null, -1, -1).size();
				mv.addObject("order_size_" + status[i], size);
			}
			Map map = new HashMap();
			map.put("status", 0);
			map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Message> msgs = this.messageService
					.query("select count(obj.id) from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
							map, -1, -1);
			mv.addObject("msg_size", msgs.get(0));
		}
		List<Goods> objs = new ArrayList<Goods>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] goods_id = cookie.getValue().split(",");
					int j = 5;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						Goods goods = new Goods();
						goods = this.goodsService.getObjById(CommUtil
								.null2Long(goods_id[i]));
						if (goods != null)
							objs.add(goods);
					}
				}
			}
		}
		mv.addObject("objs", objs);
		return mv;
	}
}
