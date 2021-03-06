package com.metoo.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.beans.BeanUtils;
import com.metoo.core.beans.BeanWrapper;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.AdvertPosition;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsSpecification;
import com.metoo.foundation.domain.GoodsType;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.query.GoodsClassQueryObject;
import com.metoo.foundation.domain.query.StoreQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IAdvertPositionService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSpecificationService;
import com.metoo.foundation.service.IGoodsTypeService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.manage.admin.tools.StoreTools;

/**
 * 
 * <p>
 * Title: GoodsClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????B2B2C???????????????????????????
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
 * @author erikzhang
 * 
 * @date 2014-4-25
 * 
 * @version koala_b2b2c v2.0 2015??? 
 */
@Controller
public class GoodsClassManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsbrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private StoreTools storeTools;
	

	/**
	 * GoodsClass?????????
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_list.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_list.htm")
	public ModelAndView goods_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		GoodsClassQueryObject qo = new GoodsClassQueryObject(currentPage, mv,
				"sequence", "asc");
		String params = "";
		qo.addQuery("obj.parent.id is null", null);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsClass.class, mv);
		IPageList pList = this.goodsClassService.list(qo);
		/*String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_class_list.htm", "", "", pList, mv);
		*/
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		return mv;
	}

	/**
	 * goodsClass????????????
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_add.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_add.htm")
	public ModelAndView goods_class_add(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.parent.id is null",
				null, -1, -1);
		List<GoodsType> gts = this.goodsTypeService.query(
				"select obj from GoodsType obj", null, -1, -1);
		if (pid != null && !pid.equals("")) {
			GoodsClass obj = new GoodsClass();
			GoodsClass parent = this.goodsClassService.getObjById(Long
					.parseLong(pid));
			obj.setParent(parent);
			obj.setDisplay(true);
			obj.setRecommend(true);
			mv.addObject("obj", obj);
		}
		Map params = new HashMap();
		params.put("ap_status", 1);
		params.put("ap_type", "img");
		List<AdvertPosition> aps = this.advertPositionService
				.query("select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_type=:ap_type order by obj.addTime desc",
						params, -1, -1);
		mv.addObject("aps", aps);
		mv.addObject("gcs", gcs);
		mv.addObject("gts", gts);
		return mv;
	}

	/**
	 * goodsClass  ????????????
	 */
	//@SecurityMapping(title = "????????????????????????", value = "/admin/goods_class_ulist.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_ulist.htm")
	public ModelAndView goods_class_ulist(HttpServletRequest request, HttpServletResponse response,
			String currentPage){
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_ulist.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0 , request, response );
		StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
		sqo.addQuery("obj.store_status", new SysMap("store_status", 15), "=");
		IPageList pList = this.storeService.list(sqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
/*		List<Store> store_list = pList.getResult();
		List<List<GoodsClass>> store_Gc_list = new ArrayList<List<GoodsClass>>();;
		for(Store store : store_list){
			List<GoodsClass> Gc= this.storeTools.query_store_detail_MainGc(store.getGc_detail_info());
			store_Gc_list.add(Gc);
		}*/
		mv.addObject("storeTools", storeTools);
		return mv;
		
	}
	
//	@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_look.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	/*@RequestMapping("/admin/goods_class_look.htm")
	public ModelAndView goods_class_look(HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage){
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_look.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0 , request, response );
		if(id != null && !id.equals("")){
			Store obj = this.storeService.getObjById(CommUtil.null2Long(id));
			List<GoodsClass> Gc_Main = storeTools.query_store_detail_MainGc(obj.getGc_detail_info());
			mv.addObject("Gc_Main", Gc_Main);
		}
		return mv;
	}*/
	
	//@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_update.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_update.htm")
	public String goods_class_update(HttpServletRequest request, HttpServletResponse response,
			String mulitId, String currentPage){
		String[] ids =  mulitId.split(",");
		for(String id : ids){
			if(id != null && !id.equals("")){
				Store obj = this.storeService.getObjById(CommUtil.null2Long(id));
				List<Map> array_list = new ArrayList<Map>();
				List<GoodsClass> Gc_Main = new ArrayList<GoodsClass>();
				List<GoodsClass> goodsClass_parent = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null", null, -1, -1);
				for(GoodsClass gc_parent : goodsClass_parent){
					Map gc_map = new HashMap();
					gc_map.put("m_id", gc_parent.getId());
					Set<GoodsClass> gc_set = this.goodsClassService.getObjById(CommUtil.null2Long(gc_parent.getId())).getChilds();
					List gc_list_gc = new ArrayList();
					for(GoodsClass gc_child : gc_set){
						gc_list_gc.add(CommUtil.null2Long(gc_child.getId()));
					}
					gc_map.put("gc_list", gc_list_gc);
					array_list.add(gc_map);
				}
				obj.setGc_detail_info(Json.toJson(array_list, JsonFormat.compact()));
				this.storeService.update(obj);
			}
		}
				return "redirect:goods_class_ulist.htm?currentPage=" + currentPage;
	}
	
	/**
	 * goodsClass????????????
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_edit.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_edit.htm")
	public ModelAndView goods_class_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsClass goodsClass = this.goodsClassService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", goodsClass);
			List<GoodsClass> gcs = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id is null",
							null, -1, -1);
			List<GoodsType> gts = this.goodsTypeService.query(
					"select obj from GoodsType obj", null, -1, -1);
			Map params = new HashMap();
			params.put("ap_status", 1);
			params.put("ap_type", "img");
			List<AdvertPosition> aps = this.advertPositionService
					.query("select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_type=:ap_type order by obj.addTime desc",
							params, -1, -1);
			if (goodsClass.getGc_advert() != null) {
				Map map = (Map) Json.fromJson(goodsClass.getGc_advert());
				mv.addObject("adv_type",
						CommUtil.null2String(map.get("adv_type")));
				mv.addObject("adv_id", CommUtil.null2String(map.get("adv_id")));
				Accessory acc = this.accessoryService.getObjById(CommUtil
						.null2Long(map.get("acc_id")));
				if (acc != null) {
					/*mv.addObject("acc_img", CommUtil.getURL(request) + "/"
							+ acc.getPath() + "/" + acc.getName());*/
					mv.addObject("acc_img", this.configService.getSysConfig().getImageWebServer() + "/" +acc.getPath()+ "/" + acc.getName());
					
				}
				if (map.get("acc_url") != null) {
					mv.addObject("acc_url",
							CommUtil.null2String(map.get("acc_url")));
				}
			}
			if (goodsClass.getGb_info() != null
					&& !goodsClass.getGb_info().equals("")) {
				List<Map> map_list = (List<Map>) Json.fromJson(goodsClass
						.getGb_info());
				List gbs = new ArrayList();
				for (Map map : map_list) {
					GoodsBrand gb = this.goodsbrandService.getObjById(CommUtil
							.null2Long(map.get("id")));
					if (gb != null) {
						gbs.add(gb);
					}
				}
				mv.addObject("gbs", gbs);
			}
			mv.addObject("aps", aps);
			mv.addObject("gcs", gcs);
			mv.addObject("gts", gts);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * goodsClass????????????
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "??????????????????", value = "/admin/goods_class_save.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_save.htm")
	public ModelAndView goods_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String pid,
			String goodsTypeId, String currentPage, String list_url,
			String add_url, String child_link, String commission_link,
			String commission_rate, String guarantee, String guarantee_link,
			String adv_type, String ap_id, String gc_color, String gbs_ids,
			String gc_adv_url, String server_vat_link, String commission_vat) {
		WebForm wf = new WebForm();
		GoodsClass goodsClass = null;
		if (id.equals("")) {// ??????
			goodsClass = wf.toPo(request, GoodsClass.class);
			goodsClass.setAddTime(new Date());
		} else {// ????????????
			GoodsClass obj = this.goodsClassService.getObjById(Long
					.parseLong(id));
			goodsClass = (GoodsClass) wf.toPo(request, obj);
		}
		GoodsClass parent = this.goodsClassService.getObjById(CommUtil
				.null2Long(pid));
		if (parent != null) {
			goodsClass.setParent(parent);
			goodsClass.setLevel(parent.getLevel() + 1);
		}
		GoodsType goodsType = this.goodsTypeService.getObjById(CommUtil
				.null2Long(goodsTypeId));
		goodsClass.setGoodsType(goodsType);
		Set<Long> ids = this.genericIds(goodsClass);
		// ??????????????????????????????????????????
		if (CommUtil.null2Boolean(child_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setGoodsType(goodsType);
					this.goodsClassService.update(gc);
				}
			}
		}
		// ??????????????????????????????????????????
		if (CommUtil.null2Boolean(commission_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setCommission_rate(new BigDecimal(commission_rate));
					this.goodsClassService.update(gc);
				}
			}
		}
		//?????????VAT??????????????? server_link
		if (CommUtil.null2Boolean(server_vat_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setCommission_vat(new BigDecimal(commission_vat));
					this.goodsClassService.update(gc);
				}
			}
		}
		// ???????????????????????????????????????
		if (CommUtil.null2Boolean(guarantee_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.goodsClassService.getObjById(gc_id);
					gc.setGuarantee(new BigDecimal(guarantee));
					this.goodsClassService.update(gc);
				}
			}
		}
		// ??????????????????
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		/*String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "class_icon";*/
		String saveFilePathName = uploadFilePath + "/" + "class_icon";
		Map map = new HashMap();
		try {
			String fileName = goodsClass.getIcon_acc() == null ? ""
					: goodsClass.getIcon_acc().getName();
			map = CommUtil.httpsaveFileToServer(request, "icon_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					goodsClass.setIcon_acc(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = goodsClass.getIcon_acc();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ????????????
		Map map2 = new HashMap();
		try {
			String fileName = "";
			map2 = CommUtil.httpsaveFileToServer(request, "gc_adv",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map2.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					Map map_temp = new HashMap();
					map_temp.put("acc_id", photo.getId());
					map_temp.put("acc_url", gc_adv_url);
					goodsClass.setGc_advert(Json.toJson(map_temp,
							JsonFormat.compact()));
				}
			} else {
				if (map2.get("fileName") != "") {
					Accessory photo = null;
					if (goodsClass.getGc_advert() != null) {
						Map json_map = (Map) Json.fromJson(goodsClass
								.getGc_advert());
						String acc_id = CommUtil.null2String(json_map
								.get("acc_id"));
						photo = this.accessoryService.getObjById(CommUtil
								.null2Long(acc_id));
					}
					photo = goodsClass.getIcon_acc();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					Map map_temp = new HashMap();
					map_temp.put("acc_id", photo.getId());
					map_temp.put("acc_url", gc_adv_url);
					goodsClass.setGc_advert(Json.toJson(map_temp,
							JsonFormat.compact()));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (CommUtil.null2Int(adv_type) == 0) {// ??????????????????
			AdvertPosition ap = this.advertPositionService.getObjById(CommUtil
					.null2Long(ap_id));
			Map map_temp = new HashMap();
			if (ap != null) {
				map_temp.put("adv_id", ap.getId());// ???????????????id
			} else {
				map_temp.put("adv_id", "");
			}
			goodsClass
					.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		}
		if (goodsClass.getGc_advert() != null) {// ????????????????????????
			Map map_temp = (Map) Json.fromJson(goodsClass.getGc_advert());
			map_temp.put("adv_type", CommUtil.null2Int(adv_type));
			goodsClass
					.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		} else {
			Map map_temp = new HashMap();
			map_temp.put("adv_type", CommUtil.null2Int(adv_type));
			goodsClass
					.setGc_advert(Json.toJson(map_temp, JsonFormat.compact()));
		}
		if (gc_color != null && !gc_color.equals("")) {
			goodsClass.setGc_color(gc_color);
		}
		if (gbs_ids != null && !gbs_ids.equals("")) {
			String[] gb_ids = gbs_ids.split(",");
			List list_temp = new ArrayList();
			for (String gb_id : gb_ids) {
				if (gb_id != null && !gb_id.equals("")) {
					GoodsBrand gb = this.goodsbrandService.getObjById(CommUtil
							.null2Long(gb_id));
					Map map_temp = new HashMap();
					map_temp.put("id", gb.getId());
					map_temp.put("name", gb.getName());
					map_temp.put("imgPath", gb.getBrandLogo().getPath());
					map_temp.put("imgName", gb.getBrandLogo().getName());
					list_temp.add(map_temp);
				}
			}
			goodsClass.setGb_info(Json.toJson(list_temp, JsonFormat.compact()));
		} else {
			goodsClass.setGb_info(null);
		}
		if (id.equals("")) {
			this.goodsClassService.save(goodsClass);
		} else {
			this.goodsClassService.update(goodsClass);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "????????????????????????");
		mv.addObject("list_url", list_url);
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid);
		}
		return mv;
	}

	@SecurityMapping(title = "????????????????????????", value = "/admin/goods_class_data.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_data.htm")
	public ModelAndView goods_class_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_class_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("pid", Long.parseLong(pid));
		List<GoodsClass> gcs = this.goodsClassService.query(
				"select obj from GoodsClass obj where obj.parent.id =:pid",
				map, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "????????????Ajax??????", value = "/admin/goods_class_ajax.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_ajax.htm")
	public void goods_class_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsClass gc = this.goodsClassService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsClass.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(gc);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.goodsClassService.update(gc);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ????????????????????????id??????id
	 * @param gc
	 * @return
	 */
	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		//[????????????????????????]
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	@SecurityMapping(title = "????????????????????????", value = "/admin/goods_class_recommend.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_recommend.htm")
	public String goods_class_recommend(HttpServletRequest request,
			String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsClass gc = this.goodsClassService.getObjById(Long
						.parseLong(id));
				gc.setRecommend(true);
				this.goodsClassService.update(gc);
			}
		}
		return "redirect:goods_class_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "????????????????????????", value = "/admin/goods_class_del.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_class_del.htm")
	public String goods_class_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Set<Long> list = this.genericIds(this.goodsClassService
						.getObjById(Long.parseLong(id)));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", list);
				List<GoodsClass> gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.id in (:ids) order by obj.level desc",
								params, -1, -1);
				for (GoodsClass gc : gcs) {
					for (Goods goods : gc.getGoods_list()) {
						goods.setGc(null);
						this.goodsService.update(goods);
					}
					GoodsType type = gc.getGoodsType();
					if (type != null) {
						type.getGcs().remove(gc);
						this.goodsTypeService.update(type);
					}
					for (GoodsSpecification gsp : gc.getSpec_detail()) {
						gsp.getSpec_goodsClass_detail().remove(gc);
						gsp.setGoodsclass(gc);
						this.goodsSpecificationService.update(gsp);
					}
					gc.setChilds(null);
					gc.getSpec_detail().clear();
					this.goodsClassService.update(gc);
					this.goodsClassService.delete(gc.getId());
				}
			}
		}
		return "redirect:goods_class_list.htm?currentPage=" + currentPage;

	}

	@RequestMapping("/admin/goods_class_verify.htm")
	public void goods_class_verify(HttpServletRequest request,
			HttpServletResponse response, String className, String id,
			String pid) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("className", className);
		params.put("id", CommUtil.null2Long(id));
		params.put("pid", CommUtil.null2Long(pid));
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.className=:className and obj.id!=:id and obj.parent.id=:pid",
						params, -1, -1);
		if (gcs != null && gcs.size() > 0) {
			ret = false;
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

	@SecurityMapping(title = "????????????id??????????????????", value = "/admin/goods_goodsType_brand.htm*", rtype = "admin", rname = "????????????", rcode = "goods_class", rgroup = "??????")
	@RequestMapping("/admin/goods_goodsType_brand.htm")
	public void goods_goodsType_brand(HttpServletRequest request,
			HttpServletResponse response, String type_id) {
		GoodsType gt = this.goodsTypeService.getObjById(CommUtil
				.null2Long(type_id));
		List<Map> list = new ArrayList<Map>();
		if (gt != null) {
			for (GoodsBrand gb : gt.getGbs()) {
				Map map = new HashMap();
				map.put("id", gb.getId());
				map.put("name", gb.getName());
				list.add(map);
			}
		}
		String temp = Json.toJson(list, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}