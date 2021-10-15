package com.metoo.lucene;

/**
 * 
 * <p>
 * Title: LuceneVo.java
 * </p>
 * 
 * <p>
 * Description: 全文检索控制类，该类完成商品索引建立、索引更新、索引删除，用户可以根据关键字分词搜索
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 湖南创发科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang,jy,jxz
 * 
 * @date 2014-6-5
 * 
 * 
 * @version koala_b2b2c v2.0 2015版
 */
public class LuceneVo {
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String TYPE = "type";
	public static final String Cat = "cat";
	public static final String CONTENT = "content";
	public static final String Arabic_TITLE = "arabic_title";
	public static final String Arabic_CONTENT = "arabic_content";
	public static final String URL = "url";
	public static final String ADD_TIME = "add_time";
	public static final String GOODS_SALENUM = "goods_salenum";
	public static final String GOODS_COLLECT = "goods_collect";
	public static final String WELL_EVALUATE = "well_evaluate";
	public static final String STORE_PRICE = "store_price";
	public static final String COST_PRICE = "cost_price";
	public static final String CURR_PRICE = "curr_price";
	public static final String GOODS_INVENTORY = "goods_inventory";
	public static final String GOODS_TYPE = "goods_type";
	public static final String GOODS_EVAS = "goods_evas";
	public static final String MAIN_PHOTO_URL = "main_photo_url";
	public static final String PHOTOS_URL = "photos_url";
	public static final String GOODS_RATE = "goods_rate";
	public static final String GOODS_AREA = "goods_area";
	public static final String GOODS_BRAND = "goods_brand";
	public static final String GOODS_CLASS = "goods_class";
	public static final String GOODS_TRANSFEE = "goods_transfee";
	public static final String GOODS_COD = "goods_cod";
	public static final String WHETHER_ACTIVE = "whether_active";
	public static final String F_SALE_TYPE = "f_sale_type";
	public static final String GOODS_SERIAL = "goods_serial";
	public static final String STORE_ID = "store_id";
	public static final String GOODS_WEIGHTINESS = "goods_weightiness";
	public static final String SEO_KEYWORD = "seo_keyword";
	public static final String GOODS_PROPERTY = "goods_property";

	private Long vo_id;// 商品ID
	private String vo_title;// 商品名称
	private String vo_content;// 商品描述，这里将会过滤html字符
	private String vo_arabic_title;// 商品阿语名称
	private String vo_arabic_content; //商品阿语描述
	private String vo_type;// 索引类型，商品为goods，店铺为store ,生活购为lifegoods
	private String vo_cat;// 商品分类，对应大商城商品分类
	private String vo_url;// 商品访问URL
	private String vo_rate;// 折扣率
	private long vo_add_time;// 商品添加时间
	private int vo_goods_salenum;// 商品销售数量
	private int vo_goods_collect;// 商品收藏数量
	private double vo_well_evaluate;// 商品好评率
	private double vo_store_price;// 商品店铺售价
	private double vo_cost_price;// 商品原价
	private int vo_goods_inventory;// 商品库存
	private int vo_goods_type;// 商品类型 0-自营 1-第三方经营
	private int vo_goods_evas;// 商品评价数量
	private int vo_goods_cod;// 是否支持货到付款，默认为0：支持货到付款，-1为不支持货到付款
	private String vo_main_photo_url;// 主图片路径
	private String vo_photos_url;// 图片路径
	private String vo_goods_area;// 地区
	private String vo_goods_brandname;// 商品所属品牌名称
	private String vo_goods_class;// 商品所属分类,格式为：二级分类id+"_"+三级分类id
	private String vo_goods_transfee;// 商品运费承担方式，0为买家承担，1为卖家承担
	private String vo_store_username;// 入驻商家的用户名
	private int vo_whether_active;// 搜索时显示是否为活动 0为无活动
									// 1为团购,2为活动，3为满送，4为满减，5为组合，6为F码，7为预售。
	private int vo_f_sale_type;// 是否为F码销售商品，0为不是F码销售商品，1为F码销售商品
	private double vo_curr_price;// 商品当前价格
	private String vo_sell_price;
	private int vo_goods_status;// 商品状态，仓库中的商品（已下架）禁止被搜索
	private String vo_goods_serial;// 商品SKU
	private Long vo_store_id;//入驻商家的id
	private double vo_goods_weightiness;// 商品权重
	private String vo_seo_keywords;// 商品seo关键字
	private String vo_goods_property;

	public Long getVo_store_id() {
		return vo_store_id;
	}

	public void setVo_store_id(Long vo_store_id) {
		this.vo_store_id = vo_store_id;
	}

	public int getVo_goods_status() {
		return vo_goods_status;
	}

	public void setVo_goods_status(int vo_goods_status) {
		this.vo_goods_status = vo_goods_status;
	}

	public String getVo_goods_serial() {
		return vo_goods_serial;
	}

	public void setVo_goods_serial(String vo_goods_serial) {
		this.vo_goods_serial = vo_goods_serial;
	}

	public int getVo_f_sale_type() {
		return vo_f_sale_type;
	}

	public void setVo_f_sale_type(int vo_f_sale_type) {
		this.vo_f_sale_type = vo_f_sale_type;
	}

	public int getVo_goods_cod() {
		return vo_goods_cod;
	}

	public void setVo_goods_cod(int vo_goods_cod) {
		this.vo_goods_cod = vo_goods_cod;
	}

	public String getVo_goods_transfee() {
		return vo_goods_transfee;
	}

	public void setVo_goods_transfee(String vo_goods_transfee) {
		this.vo_goods_transfee = vo_goods_transfee;
	}

	public String getVo_goods_brandname() {
		return vo_goods_brandname;
	}

	public void setVo_goods_brandname(String vo_goods_brandname) {
		this.vo_goods_brandname = vo_goods_brandname;
	}

	public int getVo_whether_active() {
		return vo_whether_active;
	}

	public void setVo_whether_active(int vo_whether_active) {
		this.vo_whether_active = vo_whether_active;
	}

	public String getVo_store_username() {
		return vo_store_username;
	}

	public void setVo_store_username(String vo_store_username) {
		this.vo_store_username = vo_store_username;
	}

	public String getVo_goods_area() {
		return vo_goods_area;
	}

	public void setVo_goods_area(String vo_goods_area) {
		this.vo_goods_area = vo_goods_area;
	}

	public double getVo_cost_price() {
		return vo_cost_price;
	}

	public void setVo_cost_price(double vo_cost_price) {
		this.vo_cost_price = vo_cost_price;
	}

	public String getVo_rate() {
		return vo_rate;
	}

	public void setVo_rate(String vo_rate) {
		this.vo_rate = vo_rate;
	}

	public String getVo_photos_url() {
		return vo_photos_url;
	}

	public void setVo_photos_url(String vo_photos_url) {
		this.vo_photos_url = vo_photos_url;
	}

	public int getVo_goods_evas() {
		return vo_goods_evas;
	}

	public void setVo_goods_evas(int vo_goods_evas) {
		this.vo_goods_evas = vo_goods_evas;
	}

	public String getVo_main_photo_url() {
		return vo_main_photo_url;
	}

	public void setVo_main_photo_url(String vo_main_photo_url) {
		this.vo_main_photo_url = vo_main_photo_url;
	}

	public int getVo_goods_type() {
		return vo_goods_type;
	}

	public void setVo_goods_type(int vo_goods_type) {
		this.vo_goods_type = vo_goods_type;
	}

	public String getVo_cat() {
		return vo_cat;
	}

	public void setVo_cat(String vo_cat) {
		this.vo_cat = vo_cat;
	}

	public long getVo_add_time() {
		return vo_add_time;
	}

	public void setVo_add_time(long vo_add_time) {
		this.vo_add_time = vo_add_time;
	}

	public int getVo_goods_salenum() {
		return vo_goods_salenum;
	}

	public void setVo_goods_salenum(int vo_goods_salenum) {
		this.vo_goods_salenum = vo_goods_salenum;
	}

	public double getVo_store_price() {
		return vo_store_price;
	}

	public void setVo_store_price(double vo_store_price) {
		this.vo_store_price = vo_store_price;
	}

	public Long getVo_id() {
		return vo_id;
	}

	public void setVo_id(Long vo_id) {
		this.vo_id = vo_id;
	}

	public String getVo_type() {
		return vo_type;
	}

	public void setVo_type(String vo_type) {
		this.vo_type = vo_type;
	}

	public String getVo_title() {
		return vo_title;
	}

	public void setVo_title(String vo_title) {
		this.vo_title = vo_title;
	}

	public String getVo_content() {
		return vo_content;
	}

	public void setVo_content(String vo_content) {
		this.vo_content = vo_content;
	}

	public String getVo_url() {
		return vo_url;
	}

	public void setVo_url(String vo_url) {
		this.vo_url = vo_url;
	}

	public int getVo_goods_collect() {
		return vo_goods_collect;
	}

	public void setVo_goods_collect(int vo_goods_collect) {
		this.vo_goods_collect = vo_goods_collect;
	}

	public double getVo_well_evaluate() {
		return vo_well_evaluate;
	}

	public void setVo_well_evaluate(double vo_well_evaluate) {
		this.vo_well_evaluate = vo_well_evaluate;
	}

	public int getVo_goods_inventory() {
		return vo_goods_inventory;
	}

	public void setVo_goods_inventory(int vo_goods_inventory) {
		this.vo_goods_inventory = vo_goods_inventory;
	}

	public String getVo_goods_class() {
		return vo_goods_class;
	}

	public void setVo_goods_class(String vo_goods_class) {
		this.vo_goods_class = vo_goods_class;
	}

	public double getVo_curr_price() {
		return vo_curr_price;
	}

	public void setVo_curr_price(double vo_curr_price) {
		this.vo_curr_price = vo_curr_price;
	}

	public String getVo_sell_price() {
		return vo_sell_price;
	}

	public void setVo_sell_price(String vo_sell_price) {
		this.vo_sell_price = vo_sell_price;
	}

	public String getVo_arabic_title() {
		return vo_arabic_title;
	}

	public void setVo_arabic_title(String vo_arabic_title) {
		this.vo_arabic_title = vo_arabic_title;
	}

	public String getVo_arabic_content() {
		return vo_arabic_content;
	}

	public void setVo_arabic_content(String vo_arabic_content) {
		this.vo_arabic_content = vo_arabic_content;
	}

	public double getVo_goods_weightiness() {
		return vo_goods_weightiness;
	}

	public void setVo_goods_weightiness(double vo_goods_weightiness) {
		this.vo_goods_weightiness = vo_goods_weightiness;
	}

	public String getVo_seo_keywords() {
		return vo_seo_keywords;
	}

	public void setVo_seo_keywords(String vo_seo_keywords) {
		this.vo_seo_keywords = vo_seo_keywords;
	}

	public String getVo_goods_property() {
		return vo_goods_property;
	}

	public void setVo_goods_property(String vo_goods_property) {
		this.vo_goods_property = vo_goods_property;
	}

}
