package com.metoo.msg;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.alibaba.fastjson.JSONObject;
import com.metoo.core.constant.Globals;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.PopupAuthenticator;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.Template;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ITemplateService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.msg.email.SpelTemplate;

/**
 * 
 * <p>
 * Title: MsgTools.java<???p>
 * 
 * <p>
 * Description: ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * ?????????????????????????????????????????????????????????erikzhang??? <???p>
 * <p>
 * ??????????????????????????? ??????json?????? buyer_id:???????????????????????????user.id seller_id:???????????????,?????????user.id
 * sender_id:????????????user.id receiver_id:????????????user.id order_id:??????????????? ??????order.id
 * childorder_id?????????????????????id goods_id:?????????id self_goods: ????????????????????? ?????????????????????????????? ????????????
 * SysConfig.title,???jinxinzhe???
 * 
 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * ?????????????????????????????????????????????????????????hezeng???
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015<???p>
 * 
 * <p>
 * Company: ????????????????????????????????? www.koala.com<???p>
 * 
 * @author erikzhang???jinxinzhe???hezeng
 * 
 * @date 2014-4-24
 * 
 * @version koala_b2b2c v2.0 2015???
 */
@Component
public class MsgTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private OrderFormTools orderFormTools;

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 *            :??????json?????????????????????????????????
	 * @param order_id
	 *            ?????????id???
	 * @throws Exception
	 */
	@Async
	public void sendSmsCharge(String web, String mark, String mobile, String json, String order_id, String store_id)
			throws Exception {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			Store store = null;
			boolean flag = false;
			Map function_map = new HashMap();
			List<Map> function_maps = new ArrayList<Map>();
			if (store_id != null && !store_id.equals("")) {
				store = this.storeService.getObjById(CommUtil.null2Long(store_id));
				if (store.getStore_sms_count() > 0) {
					function_maps = (List<Map>) Json.fromJson(store.getSms_email_info());
					for (Map temp_map2 : function_maps) {

						if (template != null
								&& CommUtil.null2String(temp_map2.get("type"))
										.equals(CommUtil.null2String(template.getType()))
								&& CommUtil.null2String(temp_map2.get("mark")).equals(template.getMark())) {
							function_map = temp_map2;
							if (CommUtil.null2Int(function_map.get("sms_open")) == 1) {// ????????????????????????
								flag = true;
								break;
							} else {
								System.out.println("????????????????????????????????????");
							}
						}
					}
				}
			}
			if (flag && template != null && template.isOpen()) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				Map map = this.queryJson(json);
				if (mobile != null && !mobile.equals("")) {
					if (order_id != null) {
						OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
						User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
						context.setVariable("buyer", buyer);
						if (store != null) {
							context.setVariable("seller", store.getUser());
						}
						context.setVariable("config", this.configService.getSysConfig());
						context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
						context.setVariable("webPath", web);
						context.setVariable("order", order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
						User receiver = this.userService.getObjById(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map.get("sender_id"));
						User sender = this.userService.getObjById(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService.getObjById(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map.get("seller_id"));
						User seller = this.userService.getObjById(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long temp_order_id = CommUtil.null2Long(map.get("order_id"));
						OrderForm orderForm = this.orderFormService.getObjById(temp_order_id);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
						OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService.getObjById(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods").toString());
					}
					context.setVariable("config", this.configService.getSysConfig());
					context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
					String content = ex.getValue(context, String.class);
					boolean result = this.sendSMS(mobile, content);
					if (result) {// ??????????????????????????????????????????
						System.out.println("??????????????????");
						if (store != null) {
							store.setStore_sms_count(store.getStore_sms_count() - 1);// ?????????????????????1
							function_map.put("sms_count", CommUtil.null2Int(function_map.get("sms_count")) + 1);// ?????????????????????????????????1
							String sms_email_json = Json.toJson(function_maps, JsonFormat.compact());
							store.setSend_sms_count(store.getSend_sms_count() + 1);
							store.setSms_email_info(sms_email_json);
							this.storeService.update(store);
						}
					}
				}
			}
		} else {
			System.out.println("????????????????????????????????????");
		}
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @param order_id
	 *            :???????????????????????????
	 * @param store_id
	 *            :?????????????????????id
	 * @throws Exception
	 */
	@Async
	public void sendEmailCharge(String weburl, String mark, String email, String json, String order_id, String store_id)
			throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			Store store = null;
			boolean flag = false;
			Map function_map = new HashMap();
			List<Map> function_maps = new ArrayList<Map>();
			if (store_id != null && !store_id.equals("")) {
				store = this.storeService.getObjById(CommUtil.null2Long(store_id));
				if (store != null && store.getStore_email_count() > 0) {
					function_maps = (List<Map>) Json.fromJson(store.getSms_email_info());
					for (Map temp_map2 : function_maps) {
						if (template != null
								&& CommUtil.null2String(temp_map2.get("type"))
										.equals(CommUtil.null2String(template.getType()))
								&& CommUtil.null2String(temp_map2.get("mark")).equals(template.getMark())) {
							function_map = temp_map2;
							if (CommUtil.null2Int(function_map.get("email_open")) == 1) {// ????????????????????????
								flag = true;
								break;
							} else {
								flag = false;
								System.out.println("????????????????????????????????????");
							}
						}
					}
				} else {
					System.out.println("??????????????????????????????");
				}
			}
			if (flag && template != null && template.isOpen()) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				Map map = this.queryJson(json);
				String subject = template.getTitle();
				if (order_id != null) {
					OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					context.setVariable("buyer", buyer);
					if (store != null) {
						context.setVariable("seller", store.getUser());
					}
					context.setVariable("config", this.configService.getSysConfig());
					context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", weburl);
					context.setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
					User receiver = this.userService.getObjById(receiver_id);
					context.setVariable("receiver", receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService.getObjById(sender_id);
					context.setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.getObjById(buyer_id);
					context.setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService.getObjById(seller_id);
					context.setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long temp_order_id = CommUtil.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService.getObjById(temp_order_id);
					context.setVariable("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
					OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
					context.setVariable("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService.getObjById(goods_id);
					context.setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.setVariable("seller", map.get("self_goods").toString());
				}
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
				context.setVariable("webPath", weburl);
				Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
				String content = ex.getValue(context, String.class);
				boolean result = this.sendEmail(email, subject, content);
				if (result) {// ??????????????????????????????????????????
					System.out.println("??????????????????");
					if (store != null) {
						store.setStore_email_count(store.getStore_email_count() - 1);// ?????????????????????1
						function_map.put("email_count", CommUtil.null2Int(function_map.get("email_count")) + 1);// ?????????????????????????????????1
						String sms_email_json = Json.toJson(function_maps, JsonFormat.compact());
						store.setSms_email_info(sms_email_json);
						store.setSend_email_count(store.getSend_email_count() + 1);
						this.storeService.update(store);
					}
				}
			}
		} else {
			System.out.println("????????????????????????????????????");
		}
	}

	/**
	 * ??????????????????????????????????????????????????????????????????
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */
	@Async
	public void sendSmsFree(String web, String mark, String mobile, String json, String order_id) throws Exception {
		if (this.configService.getSysConfig().isSmsEnbale()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if (template != null && template.isOpen()) {
				Map map = this.queryJson(json);
				if (mobile != null && !mobile.equals("")) {
					ExpressionParser exp = new SpelExpressionParser();
					EvaluationContext context = new StandardEvaluationContext();
					if (order_id != null) {
						OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
						User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
						context.setVariable("buyer", buyer);
						context.setVariable("config", this.configService.getSysConfig());
						context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
						context.setVariable("webPath", web);
						context.setVariable("order", order);
					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
						User receiver = this.userService.getObjById(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map.get("sender_id"));
						User sender = this.userService.getObjById(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService.getObjById(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map.get("seller_id"));
						User seller = this.userService.getObjById(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long order_id_temp = CommUtil.null2Long(map.get("order_id"));
						OrderForm orderForm = this.orderFormService.getObjById(order_id_temp);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
						OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService.getObjById(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods").toString());
					}
					context.setVariable("config", this.configService.getSysConfig());
					context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
					String content = ex.getValue(context, String.class);
					boolean ret = this.sendSMS(mobile, content);
					if (ret) {
						System.out.println("??????????????????");
					} else {
						System.out.println("??????????????????");
					}
				}
			}
		} else {
			System.out.println("????????????????????????????????????");
		}
	}

	/**
	 * ??????????????????????????? ???????????????????????????????????????
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */
	@Async
	public void sendEmailFree(String web, String mark, String email, String json, String order_id) throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if (template != null && template.isOpen()) {
				Map map = this.queryJson(json);
				String subject = template.getTitle();
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				if (order_id != null) {
					OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
					User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
					context.setVariable("buyer", buyer);
					context.setVariable("config", this.configService.getSysConfig());
					context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					context.setVariable("order", order);
				}
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
					User receiver = this.userService.getObjById(receiver_id);
					context.setVariable("receiver", receiver);
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService.getObjById(sender_id);
					context.setVariable("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.getObjById(buyer_id);
					context.setVariable("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService.getObjById(seller_id);
					context.setVariable("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long order_id_temp = CommUtil.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService.getObjById(order_id_temp);
					context.setVariable("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
					OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
					context.setVariable("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService.getObjById(goods_id);
					context.setVariable("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.setVariable("seller", map.get("self_goods").toString());
				}
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
				context.setVariable("webPath", web);
				Expression ex = exp.parseExpression(template.getContent(), new SpelTemplate());
				String content = ex.getValue(context, String.class);
				this.sendEmail(email, subject, content);
				System.out.println("??????????????????");
			} else {
				System.out.println("?????????????????????????????????");
			}
		}
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param request
	 * @param mark
	 * @param mobile
	 * @param json
	 * @throws Exception
	 */
	@Async
	public void sendEmail(String web, String mark, String email, String json, String order_id, User user)
			throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			if (template != null && template.isOpen()) {
				String[] str = email.split(",");
				for (String em : str) {
					Map map = this.queryJson(json);
					String subject = template.getTitle();
					String customName = template.getUser_name();
					// ??????SpEL?????????????????????
					ExpressionParser exp = new SpelExpressionParser();
					// ???????????????????????????????????????????????????
					// ???????????????????????????EvaluationContext
					EvaluationContext context = new StandardEvaluationContext();
					if (order_id != null) {
						OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
						List<Map> goodsList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
						String goods_id = "";
						String goods_name = "";
						String goods_count = "";
						String goods_img = "";
						String goods_spec = "";
						Goods goods = null;
						for (Map obj : goodsList) {
							goods_name = obj.get("goods_name").toString();
							goods_count = obj.get("goods_count").toString();
							goods_img = this.configService.getSysConfig().getImageWebServer() + "/"
									+ obj.get("goods_mainphoto_path");
							goods_spec = obj.get("goods_color") + "," + obj.get("goods_gsp_val");
							goods_id = obj.get("goods_id").toString();
							goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));

						}
						User buyer = this.userService.getObjById(CommUtil.null2Long(order.getUser_id()));
						// ??????????????????bean
						int[] list = new int[] { 10, 20, 30 };
						// context.setVariable("goodsList", list);
						context.setVariable("goodsList", goodsList);
						context.setVariable("goods", goods);
						context.setVariable("goods_name", goods_name);
						context.setVariable("goods_count", goods_count);
						context.setVariable("goods_img", goods_img);
						context.setVariable("goods_spec", goods_spec);
						context.setVariable("goods_id", goods_id);
						context.setVariable("order_total", order.getTotalPrice());
						context.setVariable("buyer", buyer.getUserName());
						context.setVariable("store", order.getStore_name());
						context.setVariable("order", order.getOrder_id());
						context.setVariable("orderPrice", order.getTotalPrice());
						context.setVariable("config", this.configService.getSysConfig());
						context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
						/* context.setVariable("webPath", web); */

					}
					if (map.get("receiver_id") != null) {
						Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
						User receiver = this.userService.getObjById(receiver_id);
						context.setVariable("receiver", receiver);
					}
					if (map.get("sender_id") != null) {
						Long sender_id = CommUtil.null2Long(map.get("sender_id"));
						User sender = this.userService.getObjById(sender_id);
						context.setVariable("sender", sender);
					}
					if (map.get("buyer_id") != null) {
						Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
						User buyer = this.userService.getObjById(buyer_id);
						context.setVariable("buyer", buyer);
					}
					if (map.get("seller_id") != null) {
						Long seller_id = CommUtil.null2Long(map.get("seller_id"));
						User seller = this.userService.getObjById(seller_id);
						context.setVariable("seller", seller);
					}
					if (map.get("order_id") != null) {
						Long order_id_temp = CommUtil.null2Long(map.get("order_id"));
						OrderForm orderForm = this.orderFormService.getObjById(order_id_temp);
						context.setVariable("orderForm", orderForm);
					}
					if (map.get("childorder_id") != null) {
						Long childorder_id = CommUtil.null2Long(map.get("childorder_id"));
						OrderForm orderForm = this.orderFormService.getObjById(childorder_id);
						context.setVariable("child_orderForm", orderForm);
					}
					if (map.get("goods_id") != null) {
						Long goods_id = CommUtil.null2Long(map.get("goods_id"));
						Goods goods = this.goodsService.getObjById(goods_id);
						context.setVariable("goods", goods);
					}
					if (map.get("self_goods") != null) {
						context.setVariable("seller", map.get("self_goods").toString());
					}
					if (null != user) {
						context.setVariable("userName", user.getUsername());
						context.setVariable("password", user.getPwd());
					}
					context.setVariable("config", this.configService.getSysConfig());
					context.setVariable("send_time", CommUtil.formatLongDate(new Date()));
					context.setVariable("webPath", web);
					System.out.println(template.getContent());
					// ???????????????
					Expression ex = exp.parseExpression(template.getContent(), new TemplateParserContext());
					// ?????????????????????????????????spring??????????????????ApplicationContext
					String content = ex.getValue(context, String.class);
					System.out.println("content :" + content);
					this.sendEmail(em, subject, content, customName);
					System.out.println("??????????????????");
				}
			} else {
				System.out.println("?????????????????????????????????");
			}
		}
	}

	/**
	 * @description velocity????????????
	 * @param web
	 *            ????????????
	 * @param mark
	 *            ??????????????????
	 * @param email
	 *            ?????????email
	 * @param order_id
	 *            ??????id
	 * @param user
	 *            ?????????
	 * @throws Exception
	 */
	@Async
	public void sendJMail(String web, String mark, String email, List<Long> order_ids, User user) throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			VelocityEngine velocityEngine = new VelocityEngine();

			velocityEngine.init();

			Velocity.init();

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();
			if (template != null && template.isOpen()) {
				String subject = template.getTitle();
				String customName = template.getUser_name();
				String content = template.getContent();
				Set<Long> set = new HashSet<Long>();
				/*
				 * for(Object id : order_ids){ set.add(CommUtil.null2Long(id));
				 * }
				 */
				Map params = new HashMap();
				params.put("order_ids", order_ids);
				List<OrderForm> orders = this.orderFormService
						.query("select obj from OrderForm obj where obj.id in (:order_ids)", params, -1, -1);
				context.put("webPath", web);
				context.put("orderFormTools", orderFormTools);
				context.put("orders", orders);
				if (user != null) {
					context.put("user", user);
				}
				StringWriter stringWriter = new StringWriter();
				try {
					Velocity.evaluate(context, stringWriter, "mystring", content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.sendEmail(email, subject, stringWriter.toString(), customName);
			}
		}
	}

	@Async
	public void sendJMail(String web, String mark, List<Long> order_ids, User user) throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			VelocityEngine velocityEngine = new VelocityEngine();

			velocityEngine.init();

			Velocity.init();

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();
			if (template != null && template.isOpen()) {
				String subject = template.getTitle();
				String customName = template.getUser_name();
				String content = template.getContent();
				Set<Long> set = new HashSet<Long>();
				/*
				 * for(Object id : order_ids){ set.add(CommUtil.null2Long(id));
				 * }
				 */
				Map params = new HashMap();
				params.put("order_ids", order_ids);
				List<OrderForm> orders = this.orderFormService
						.query("select obj from OrderForm obj where obj.id in (:order_ids)", params, -1, -1);
				context.put("webPath", web);
				context.put("orderFormTools", orderFormTools);
				context.put("orders", orders);
				if (user != null) {
					context.put("user", user);
				}
				StringWriter stringWriter = new StringWriter();
				try {
					Velocity.evaluate(context, stringWriter, "mystring", content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String user_email = "";
				Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
				Matcher matcher = emailPattern.matcher(CommUtil.null2String(user.getEmail()));
				if (user.getEmail() != null && !user.getEmail().equals("") && matcher.matches()) {// ???????????????????????????????????????
					user_email = user.getEmail();
				}
				this.sendEmail(user_email, subject, stringWriter.toString(), customName);
			}
		}
	}

	/**
	 * 
	 * @param web
	 * @param mark
	 * @param email
	 * @param order_id
	 * @param user
	 * @throws Exception
	 */
	@Async
	public void sendJMail(String web, String mark, String[] email, String order_id, User user, List<Map> list)
			throws Exception {
		if (this.configService.getSysConfig().isEmailEnable()) {
			Template template = this.templateService.getObjByProperty(null, "mark", mark);
			VelocityEngine velocityEngine = new VelocityEngine();

			velocityEngine.init();

			Velocity.init();

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();
			if (template != null && template.isOpen() && !CommUtil.null2String(email).equals("")) {
				String subject = template.getTitle();
				String customName = template.getUser_name();
				String content = template.getContent();
				OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
				List<Map> goodsList = new ArrayList<Map>();
				context.put("webPath", web);
				if (order != null) {
					goodsList = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
					// context.put("objs", goodsList);
					context.put("order", order);
				} else {
					goodsList = list;
				}
				context.put("objs", goodsList);
				if (user != null) {
					context.put("user", user);
				}

				if (!context.equals("")) {
					StringWriter stringWriter = new StringWriter();
					Velocity.evaluate(context, stringWriter, "mystring", content);
					this.sendEmail(email, subject, stringWriter.toString(), customName);
				}
			}
		}
	}

	/**
	 * ????????????????????????
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendSMS(String mobile, String content) throws UnsupportedEncodingException {
		boolean result = true;
		if (this.configService.getSysConfig().isSmsEnbale()) {
			String url = this.configService.getSysConfig().getSmsURL();
			String userName = this.configService.getSysConfig().getSmsUserName();
			String password = this.configService.getSysConfig().getSmsPassword();
			String appkey = this.configService.getSysConfig().getSmsAppkey();
			String secretkey = this.configService.getSysConfig().getSmsSecretkey();
			// Globals.DEFAULT_SMS_URL
			SmsBase sb = new SmsBase(url, userName, password, appkey, secretkey);// ?????????????????????????????????
			String ret = null;
			try {
				ret = sb.postJson(mobile, content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ret.equals("0")) {
				result = true;
			} else {
				result = false;
			}
			/*
			 * if (!ret.substring(0, 3).equals("000")) { result = false; }
			 */
		} else {
			result = false;
			System.out.println("?????????????????????????????????");
		}
		return result;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = true;
		if (this.configService.getSysConfig().isEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig().getEmailUser();

			String to_mail_address = email;
			if (username != null && password != null && !username.equals("") && !password.equals("")
					&& smtp_server != null && !smtp_server.equals("") && to_mail_address != null
					&& !to_mail_address.trim().equals("")) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties mailProps = new Properties();
				// ??????mail.smtp.auth?????????????????????????????????????????????
				// ??????mail.transport.protocol??????????????????????????????
				// ??????mail.host?????????????????????????????????????????????
				mailProps.put("mail.smtp.auth", "true");
				mailProps.put("username", username);
				mailProps.put("password", password);
				mailProps.put("mail.smtp.host", smtp_server);
				Session mailSession = Session.getInstance(mailProps, auth);
				MimeMessage message = new MimeMessage(mailSession);
				try {
					// message.setFrom(new InternetAddress(from_mail_address));
					try {
						message.setFrom(new InternetAddress(from_mail_address, "Soarmall"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_mail_address));
					message.setSubject(subject);
					MimeMultipart multi = new MimeMultipart("related");
					BodyPart bodyPart = new MimeBodyPart();
					bodyPart.setDataHandler(new DataHandler(content, "text/html;charset=UTF-8"));// ????????????
					// bodyPart.setText(content);
					multi.addBodyPart(bodyPart);
					message.setContent(multi);
					message.saveChanges();
					Transport.send(message);// ??????smtp??????????????????????????????
					ret = true;
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				}
			} else {
				ret = false;
			}
		} else {
			ret = false;
			System.out.println("?????????????????????????????????");
		}
		return ret;
	}

	/**
	 * @desctiption ???????????????????????? - ??????????????????
	 * @param email
	 * @param subject
	 * @param content
	 * @param customName
	 * @return
	 */
	public boolean sendEmail(String email, String subject, String content, String customName) {
		boolean ret = true;
		if (this.configService.getSysConfig().isEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig().getEmailUser();
			String to_mail_address = email;
			List<String> emails = new ArrayList<String>();
			emails.add("460751446@qq.com");
			emails.add("1223414075@qq.com");
			emails.add("11943732@qq.com");
			// emails.add(email);
			InternetAddress internetAddress[] = new InternetAddress[emails.size()];
			for (int i = 0; i < emails.size(); i++) {
				try {
					internetAddress[i] = new InternetAddress(emails.get(i));
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// && to_mail_address != null
			// && !to_mail_address.trim().equals("")
			if (username != null && password != null && !username.equals("") && !password.equals("")
					&& smtp_server != null && !smtp_server.equals("")) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties mailProps = new Properties();// ??????properties???????????????????????????
														// //??????????????????
				// ??????mail.smtp.auth?????????????????????????????????????????????
				// ??????mail.transport.protocol??????????????????????????????
				// ??????mail.host?????????????????????????????????????????????
				mailProps.put("mail.smtp.auth", "true");// ??????????????????SMTP??????
				mailProps.put("username", username);
				mailProps.put("password", password);
				mailProps.put("mail.smtp.host", smtp_server);
				// ???session???????????????????????????????????????
				Session mailSession = Session.getInstance(mailProps, auth);
				// ???true?????????????????????console)??????????????????????????????
				mailSession.setDebug(true);
				MimeMessage message = new MimeMessage(mailSession);// ??????????????????
				try {
					// message.setFrom(new InternetAddress(from_mail_address));
					try {
						// 2.???????????????
						// ?????? InternetAddress ????????????????????????: ??????, ???????????????(???????????????,
						// ?????????????????????), ????????????????????????
						// mimeMessage.setFrom(new
						// InternetAddress("from_mail_address","customName","UTF-8"));
						// ???????????????
						message.setFrom(new InternetAddress(from_mail_address, customName)); // ??????????????????
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// ???????????????
					message.setReplyTo(new Address[] { new InternetAddress(from_mail_address) });
					// ?????????????????????
					/*
					 * message.setRecipients(Message.RecipientType.TO,
					 * internetAddress);
					 */
					if (to_mail_address != null && !to_mail_address.equals("")) {
						message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_mail_address));// ?????????
					}

					// ????????? ?????????BCC ???????????????Message.RecipientType.CC
					// Message.RecipientType.TO
					message.addRecipients(MimeMessage.RecipientType.BCC, internetAddress);

					message.setSubject(subject);// ???????????????
					// ??????Multipart???????????????????????????bodypart???????????????????????????
					MimeMultipart multi = new MimeMultipart("related");
					// ??????????????????
					BodyPart bodyPart = new MimeBodyPart();
					// ????????????
					bodyPart.setDataHandler(new DataHandler(content, "text/html;charset=UTF-8"));
					// bodyPart.setText(content);
					multi.addBodyPart(bodyPart);

					message.setContent(multi); // ???html???????????? ???????????????
					// ????????????
					message.saveChanges();
					Transport.send(message);// ??????smtp??????????????????????????????
					ret = true;
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				}
			} else {
				ret = false;
			}
		} else {
			ret = false;
			System.out.println("?????????????????????????????????");
		}
		return ret;
	}

	@Async
	public void sendEmail(String[] email, String subject, String content, String customName) {
		boolean ret = true;
		if (this.configService.getSysConfig().isEmailEnable()) {
			String username = "";
			String password = "";
			String smtp_server = "";
			String from_mail_address = "";
			username = this.configService.getSysConfig().getEmailUserName();
			password = this.configService.getSysConfig().getEmailPws();
			smtp_server = this.configService.getSysConfig().getEmailHost();
			from_mail_address = this.configService.getSysConfig().getEmailUser();
			InternetAddress internetAddress[] = new InternetAddress[email.length];
			for (int i = 0; i < email.length; i++) {
				try {
					internetAddress[i] = new InternetAddress(email[i]);
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!CommUtil.null2String(username).equals("") && !CommUtil.null2String(password).equals("")
					&& !CommUtil.null2String(smtp_server).equals("") && email.length > 0) {
				Authenticator auth = new PopupAuthenticator(username, password);
				Properties properties = new Properties();
				properties.put("mail.smtp.auth", "true");// ??????????????????SMTP??????
				properties.put("username", username);
				properties.put("password", password);
				properties.put("mail.smtp.host", smtp_server);
				// ???session???????????????????????????????????????
				Session mailSession = Session.getInstance(properties, auth);
				// ???true?????????????????????console)??????????????????????????????
				mailSession.setDebug(true);
				MimeMessage message = new MimeMessage(mailSession);// ??????????????????
				// MimeMessageHelper helper = new MimeMessageHelper(message,
				// "UTF-8");
				try {
					// ???????????????
					// ?????? InternetAddress ????????????????????????: ??????, ???????????????(???????????????, ?????????????????????),
					// ????????????????????????
					message.setFrom(new InternetAddress(from_mail_address, customName)); // ??????????????????
					// ?????????
					message.setReplyTo(internetAddress);
					message.setRecipients(Message.RecipientType.TO, internetAddress);
					// ???????????????
					message.setSubject(subject);

					// ??????Multipart???????????????????????????bodypart???????????????????????????
					MimeMultipart multi = new MimeMultipart("related");
					// ??????????????????
					BodyPart bodyPart = new MimeBodyPart();
					// ????????????
					bodyPart.setDataHandler(new DataHandler(content, "text/html;charset=UTF-8"));
					// bodyPart.setText(content);
					multi.addBodyPart(bodyPart);

					message.setContent(multi); // ???html???????????? ???????????????

					// ????????????
					message.saveChanges();
					Transport.send(message);// ??????smtp??????????????????????????????
					ret = true;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ??????json??????
	 * 
	 * @param json
	 * @return
	 */
	private Map queryJson(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/*
	 * SMTP ?????????????????? (??? SSL ?????????????????????????????? 25, ???????????????, ??????????????? SSL ??????,??????????????????????????? SMTP
	 * ??????????????????, ??????????????????????????????????????????, QQ?????????SMTP(SLL)?????????465???587, ???????????????????????????)
	 */
	/*
	 * final String smtpPort = "465"; props.setProperty("mail.smtp.port",
	 * smtpPort); props.setProperty("mail.smtp.socketFactory.class",
	 * "javax.net.ssl.SSLSocketFactory");
	 * props.setProperty("mail.smtp.socketFactory.fallback", "false");
	 * props.setProperty("mail.smtp.socketFactory.port", smtpPort);
	 */
}
