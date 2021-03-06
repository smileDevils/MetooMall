package com.metoo.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.service.IQueryService;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.weixin.utils.PayCommonUtil;
import com.metoo.core.weixin.utils.WeixinUtil;
import com.metoo.foundation.domain.GroupJoiner;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.Payment;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.wechat.WeixinTemplate;
import com.metoo.foundation.domain.wechat.WeixinTemplateParam;
import com.metoo.foundation.service.IGroupJoinerService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IOrderFormLogService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IPaymentService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.weixin.view.tools.OrderPayTools;

@Controller
public class WeixinOrderPayAction  {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IQueryService queryService;
	@Autowired
	private IGroupJoinerService groupJoinerService;
	@Autowired
	private OrderPayTools orderPayTools;
	
	private String nonceStr = "";//?????????
	private Logger log = Logger.getLogger(WeixinOrderPayAction.class);
	
	@RequestMapping("/wap/pay_order.htm")
	public void pay_order(HttpServletRequest request,
			HttpServletResponse response,String orderId){
		OrderForm order = this.orderFormService.getObjById(Long.valueOf(orderId));
		User user = SecurityUserHolder.getCurrentUser();
		SysConfig config = configService.getSysConfig();
		
		String notify_url = "http://wx.fensekaola.com/wap/weixin_return.htm";
		
		
		SortedMap<String,Object> parameters = new TreeMap<String, Object>();
		parameters.put("appId", config.getWeixin_appId());
		parameters.put("nonceStr", nonceStr); // ?????????????????????????????????
		parameters.put("key", config.getWeixin_mch_key());//????????????
		Map<String, String> map = weixinPrePay(user.getOpenId(),notify_url,config,order,request);  
		try {
			if("SUCCESS".equalsIgnoreCase(map.get("result_code"))){//????????????api??????????????????
				
				if(map != null){
					parameters.put("package", "prepay_id="+map.get("prepay_id"));//???????????????????????????
				}
				parameters.put("signType", "MD5");//????????????
				parameters.put("timeStamp",String.valueOf(System.currentTimeMillis() / 1000));//????????????????????????
				
				//String paySign = PayCommonUtil.seconedCreateSign("UTF-8",parameters);
				String paySign = PayCommonUtil.createSign("UTF-8", parameters,config.getWeixin_mch_key());
				
				parameters.put("paySign", paySign);//??????
				
			}
			
//		String sendUrl = "http://wx.fensekaola.com/wap/buyer/center.htm";
//		if (order.getOrder_cat() == 3) {
//			
//			sendUrl = "http://wx.fensekaola.com/wap/goods.htm?id=" + order.get;
////		}
//			
		
		
		
		String send_url = "http://wx.fensekaola.com/wap/buyer/center.htm";
		
		if (order.getOrder_cat() == 3) {
			String sqls = "SELECT a.gg_goods_id from metoo_group_goods a " +
					"where a.id in ( " +
					"SELECT b.rela_group_goods_id from metoo_group_joiner b " + 
					"where b.rela_order_form_id=:orderId)";
			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put("orderId", orderId);
			List list = this.queryService.nativeQuery(sqls, pMap, -1, -1);
			long goodsId = 0L;
			if (null != list && 0 < list.size()) {
				
				//Object[] ao = ()list.get(0);
				BigInteger bi = (BigInteger)list.get(0);
				goodsId = bi.longValue();
			}
			
			send_url = "http://wx.fensekaola.com/wap/goods.htm?id=" + goodsId;
		}
//		
		parameters.put("sendUrl", send_url);//???????????????????????????????????????
			
		parameters.put("result_code", map.get("result_code"));//???????????????
		parameters.put("err_code_des", map.get("err_code_des"));//????????????
		String json = Json.toJson(parameters, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		
			writer = response.getWriter();
			writer.print(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/wap/pay_refund.htm")
	public void pay_refund(HttpServletRequest request,
			HttpServletResponse response,String orderId){
		OrderForm order = this.orderFormService.getObjById(Long.valueOf(orderId));
		SysConfig config = configService.getSysConfig();
		
		
		
		SortedMap<String,Object> parameters = new TreeMap<String, Object>();
		parameters.put("appId", config.getWeixin_appId());
		parameters.put("nonceStr", nonceStr); // ?????????????????????????????????
		parameters.put("key", config.getWeixin_mch_key());//????????????
		Map<String, String> map = weixinPayRefund(config, order);  
		try {
			if("SUCCESS".equalsIgnoreCase(map.get("return_code"))){//????????????api??????????????????
				
				order.setOrder_status(70);
				order.setRefund_fee(Integer.parseInt(map.get("refund_fee")));
				order.setRefund_id(map.get("refund_id"));
				order.setRefund_out_no(map.get("out_refund_no"));
				
					
//				
//				//String paySign = PayCommonUtil.seconedCreateSign("UTF-8",parameters);
//				String paySign = PayCommonUtil.createSign("UTF-8", parameters,config.getWeixin_mch_key());
//				
//				parameters.put("paySign", paySign);//??????
				
			} else {
				
				order.setOrder_status(75);
			}
			orderFormService.update(order);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
//		String sendUrl = "http://wx.fensekaola.com/wap/buyer/center.htm";
//		if (order.getOrder_cat() == 3) {
//			
//			sendUrl = "http://wx.fensekaola.com/wap/goods.htm?id=" + order.get;
////		}
//			
		
		
		
		
	}
	
	public Map<String, String> weixinPrePay(String openid,String notify_url,SysConfig config,OrderForm order, HttpServletRequest request) { 
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();  
        parameterMap.put("appid", config.getWeixin_appId());  
        parameterMap.put("mch_id", config.getMch_id());  
        //?????????
        nonceStr = PayCommonUtil.getRandomString(32);
        parameterMap.put("nonce_str", nonceStr);  
        parameterMap.put("body", order.getId().toString());
        parameterMap.put("out_trade_no", order.getOrder_id());
		
        parameterMap.put("fee_type", "CNY");  
        BigDecimal total = order.getTotalPrice().multiply(new BigDecimal(100));  
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");  
       parameterMap.put("total_fee", df.format(total)); 
      //  parameterMap.put("total_fee", "1");   ????????????1????????????
        parameterMap.put("spbill_create_ip", request.getRemoteAddr());  
        parameterMap.put("notify_url", notify_url);
        parameterMap.put("trade_type", "JSAPI");
        //trade_type???JSAPI??? openid????????????
        parameterMap.put("openid", openid);
        String sign = "";
        try {
			sign = PayCommonUtil.createSign("UTF-8", parameterMap,config.getWeixin_mch_key());
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		} 
        parameterMap.put("sign", sign); 
        System.out.println("sign : "+ sign);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);  
        System.out.println(requestXML);  
        String result = PayCommonUtil.httpsRequest(  
                "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",  
                requestXML);  
        System.out.println(result);  
        Map<String, String> map = null;  
        try {  
            map = PayCommonUtil.doXMLParse(result);  
        } catch (JDOMException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return map;        
    }
	
	//??????????????????
	@RequestMapping("/wap/weixin_return.htm")
	public String notify(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String result;//??????????????????????????????
		String inputLine;
		String notityXml = "";
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		//????????????????????????
		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notityXml += inputLine;
			}
			request.getReader().close();
		} catch (Exception e) {
			e.printStackTrace();
			result = setXml("fail","xml????????????");
		}
		if (StringUtils.isEmpty(notityXml)) {
			result = setXml("fail","xml??????");
		}
		Map map = PayCommonUtil.doXMLParse(notityXml);
		// ??????????????????
		String appid = (String) map.get("appid");//??????ID
		String attach = (String) map.get("attach");//???????????????
		String bank_type = (String) map.get("bank_type");//????????????
		String cash_fee = (String) map.get("cash_fee");//??????????????????
		String fee_type = (String) map.get("fee_type");//????????????
		String is_subscribe = (String) map.get("is_subscribe");//????????????????????????
		String mch_id = (String) map.get("mch_id");//?????????
		String nonce_str = (String) map.get("nonce_str");//???????????????
		String openid = (String) map.get("openid");//????????????
		String out_trade_no = (String) map.get("out_trade_no");// ?????????????????????
		String result_code = (String) map.get("result_code");// ????????????
		String return_code = (String) map.get("return_code");// SUCCESS/FAIL
		String sign = (String) map.get("sign");// ????????????
		String time_end = (String) map.get("time_end");//??????????????????
		String total_fee = (String) map.get("total_fee");// ??????????????????
		String trade_type = (String) map.get("trade_type");//????????????
		String transaction_id = (String) map.get("transaction_id");//?????????????????????
		
		System.out.println("**************************************************************************************************");
		System.out.println(appid+"-------------------??????ID");
		System.out.println(attach+"-------------------???????????????");
		System.out.println(bank_type+"-------------------????????????");
		System.out.println(cash_fee+"-------------------??????????????????");
		System.out.println(fee_type+"-------------------????????????");
		System.out.println(is_subscribe+"-------------------????????????????????????");
		System.out.println(mch_id+"-------------------?????????");
		System.out.println(nonce_str+"-------------------???????????????");
		System.out.println(openid+"-------------------????????????");
		System.out.println(out_trade_no+"-------------------?????????????????????");
		System.out.println(result_code+"-------------------????????????");
		System.out.println(return_code+"------------------- SUCCESS/FAIL");
		System.out.println(sign+"-------------------????????????-?????????????????????");
		System.out.println(time_end+"-------------------??????????????????");
		System.out.println(total_fee+"-------------------??????????????????");
		System.out.println(trade_type+"-------------------????????????");
		System.out.println(transaction_id+"-------------------?????????????????????");
		System.out.println("**************************************************************************************************");
		
		if("SUCCESS".equalsIgnoreCase(result_code)){//???????????????success ??????????????????
			//??????????????????
			OrderForm main_order = null;
			Map<String,Object> pmap = new HashMap<String, Object>();
			pmap.put("order_id", out_trade_no);
			String sql = "select obj from OrderForm obj where order_id = :order_id";
			List<OrderForm> orders = this.orderFormService.query(sql, pmap, 0, 1);
			if(orders != null){
				main_order = orders.get(0);
			}
			//??????????????????
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", openid);
			List<User> userList = this.userService.query("select obj from User obj where obj.openId = :id ", params, -1, -1);
			User user = null;
			if(userList.size()>0){
				user = userList.get(0);
			}

			if (main_order != null
					&& main_order.getOrder_status() < 20) {// ????????????????????????????????????????????????
				try {
					main_order.setOut_order_id(transaction_id);//??????????????? ,?????????????????????
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");//?????????mm??????????????????????
					main_order.setPayTime(sdf.parse(time_end));//?????????????????? 
					if (main_order.getOrder_cat() == 3) {
						main_order.setOrder_status(66);//??????????????????????????????????????????
					} else {
						
						main_order.setOrder_status(20);//????????????????????????????????????
					}
					
					Payment pm  = this.paymentService.getObjByProperty(null, "mark", "wx_app");
					main_order.setPayment(pm);
					this.orderFormService.update(main_order);
					
					System.out.println("order_id: " + out_trade_no);
					System.out.println("order_form_key_id: " + main_order.getId());
					long moId = main_order.getId();
					//???????????????????????????
					String sqls = "update metoo_group_joiner a set a.joiner_count=a.joiner_count+1 where a.child_group_id in ( " +
							"select b.child_group_id from " +
							"(select t.child_group_id from metoo_group_joiner t where t.rela_order_form_id=:orderId) b)";
					pmap.clear();
					pmap.put("orderId", moId);
					this.queryService.executeNativeSQL(sqls, pmap);
					
					sqls = "update metoo_group_joiner t set t.status='1'" +
							" where t.rela_order_form_id=:orderId";
					

					this.queryService.executeNativeSQL(sqls, pmap);

					
					//??????????????????????????????????????????????????????????????????
					orderPayTools.update_goods_inventory(main_order);
					OrderFormLog main_ofl = new OrderFormLog();
					main_ofl.setAddTime(new Date());
					main_ofl.setLog_info("????????????");
					main_ofl.setLog_user(user);
					main_ofl.setOf(main_order);
					this.orderFormLogService.save(main_ofl);
				
					
					int integral  = ((Math.round(Float.parseFloat(total_fee)/100)) <= 0 ? 1 : Math.round(Float.parseFloat(total_fee)/100));
					
					//???????????????????????????????????????????????????
					if (main_order.getOrder_cat() == 3) {  //?????????????????????
						
						sqls = "SELECT a.id from metoo_group_joiner a, " +
								"(SELECT t1.child_group_id from metoo_group_joiner t1, metoo_orderform t2 where " +
								"t1.rela_order_form_id=t2.id " +
								"and t2.order_id=:order_id " +
								") b " +
								"where a.child_group_id=b.child_group_id";
							//	+ "and a.is_group_creator=1";
						pmap.clear();
						pmap.put("order_id", main_order.getOrder_id());
						List list = this.queryService.nativeQuery(sqls, pmap, -1, -1);
						if (null != list && 0 < list.size()) {
							
							long id = ((BigInteger)list.get(0)).longValue();
							GroupJoiner gj = groupJoinerService.getObjById(id);
							if (gj.getRela_order_form_id() == main_order.getId()) {
								
								gj.setAdd_integral(CommUtil.null2LongNew(gj.getAdd_integral()) + integral);
								groupJoinerService.update(gj);
							} else {
								
								if ("1".equals(gj.getIs_group_creator())) {
//									String userId = gj.getUser_id();
//									
//									if (Long.parseLong(userId) != user.getId()) {
////										User u = this.userService.getObjById(Long.parseLong(userId));
////										if (null != u) {
////											
////											u.setIntegral(CommUtil.null2Int(u.getIntegral()) + integral);
////											this.userService.save(u);
////										}
										
										gj.setAdd_integral(CommUtil.null2LongNew(gj.getAdd_integral()) + integral);
										groupJoinerService.update(gj);
//									}
									
								}
								
							}
						}
						
					} else {
						
						//?????????????????????????????? 
						
						user.setIntegral(user.getIntegral() + integral);
						this.userService.save(user);
						
						if(integral > 0){
							IntegralLog log = new IntegralLog();
							log.setAddTime(new Date());
							log.setContent("??????????????????"
									+ integral + "???");
							log.setIntegral(integral);
							log.setIntegral_user(user);
							log.setType("order");
							this.integralLogService.save(log);
						
							//?????????????????????
							User parent_user =  user.getParent();
							if(parent_user != null){
								parent_user.setIntegral(parent_user.getIntegral() + integral);
								this.userService.save(parent_user);
								
								IntegralLog log1 = new IntegralLog();
								log1.setAddTime(new Date());
								log1.setContent("?????????????????????"
										+ integral + "???");
								log1.setIntegral(integral);
								log1.setIntegral_user(parent_user);
								log1.setType("chind_order");
								this.integralLogService.save(log1);
							}
							
							//??????????????????????????????50%??????
							User grant_user =  user.getParent().getParent();
							if(grant_user != null){
								Integer ti =Math.round(integral/2);
								grant_user.setIntegral( grant_user.getIntegral() + ti);
								this.userService.save(grant_user);
								
								IntegralLog log2 = new IntegralLog();
								log2.setAddTime(new Date());
								log2.setContent("???????????????????????????"
										+ ti + "???");
								log2.setIntegral(ti);
								log2.setIntegral_user(grant_user);
								log2.setType("third_order");
								this.integralLogService.save(log2);
							}
							
						}
					}

					//?????????????????????????????????????????????
					WeixinTemplate tem=new WeixinTemplate();
					tem.setTemplateId("88_o40vOQtDcv-Uw4NETXyhDfFyFh7irWWhPIdDXD-M");
					tem.setTopColor("#00DD00");
					tem.setToUser(openid);
					tem.setUrl("http://wx.fensekaola.com/wap/buyer/center.htm?op=center"); 
					  
					List<WeixinTemplateParam> paras=new ArrayList<WeixinTemplateParam>();
					paras.add(new WeixinTemplateParam("first","????????????????????????????????????","#FF3333"));
					paras.add(new WeixinTemplateParam("keyword1",main_order.getOrder_id(),"#0044BB"));
					paras.add(new WeixinTemplateParam("keyword2",main_order.getAddTime().toString(),"#0044BB"));
					paras.add(new WeixinTemplateParam("keyword3",String.valueOf(Float.parseFloat(total_fee)/100)+"???","#0044BB"));
					paras.add(new WeixinTemplateParam("keyword4",bank_type,"#0044BB"));
					paras.add(new WeixinTemplateParam("Remark","?????????????????????????????????!!!!","#AAAAAA"));
							
					tem.setTemplateParamList(paras);
							
					boolean tempResult=WeixinUtil.sendTemplateMsg(configService.getSysConfig().getWeixin_token(),tem);
					
					System.out.println("........................");
					System.out.println(tempResult);

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}else{
				System.out.println("?????????????????????");
				log.info("?????????????????????");
			}
			
		}else{
			System.out.println("fail ????????????");
			log.info("fail ????????????");
		}
		return "redirect:http://wx.fensekaola.com/wap/buyer/center.htm";
	}
	
	//??????xml ??????????????????
	public static String setXml(String return_code, String return_msg) {
		SortedMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("return_code", return_code);
		parameters.put("return_msg", return_msg);
		return "<xml><return_code><![CDATA[" + return_code + "]]>" + 
				"</return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
	}
	
	
	
	/**
	 * ????????????
	 * @param config
	 * @param order
	 * @param request
	 * @return
	 */
	public Map<String, String> weixinPayRefund(SysConfig config, OrderForm order) { 
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();  
        parameterMap.put("appid", config.getWeixin_appId());
        String mch_id = config.getMch_id();
        parameterMap.put("mch_id", mch_id);  
        //?????????
        nonceStr = PayCommonUtil.getRandomString(32);
        parameterMap.put("nonce_str", nonceStr);
        parameterMap.put("out_trade_no", order.getOrder_id());
   //     parameterMap.put("transaction_id", order.getOut_order_id());
        String out_refund_no = SecurityUserHolder.getCurrentUser()
				.getId() + CommUtil.formatTime("yyyyMMddhhmmssSSS",
						new Date());
        parameterMap.put("out_refund_no", out_refund_no);
        
        BigDecimal total = order.getTotalPrice().multiply(new BigDecimal(100));  
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");  
        String totalFee = df.format(total);
        parameterMap.put("total_fee", totalFee); 
        parameterMap.put("refund_fee", totalFee); 
        parameterMap.put("refund_fee_type", "CNY");  
        
      //  parameterMap.put("total_fee", "1");   ????????????1????????????
        String sign = "";
        try {
			sign = PayCommonUtil.createSign("UTF-8", parameterMap,config.getWeixin_mch_key());
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		} 
        parameterMap.put("sign", sign); 
        System.out.println("sign : "+ sign);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);  
        System.out.println("????????????xml: " + requestXML);  
        Map<String, String> map = null;  
        try {  
        	String result = PayCommonUtil.executeBySslPost("https://api.mch.weixin.qq.com/secapi/pay/refund", requestXML,
        		"D:/cert/apiclient_cert.p12", mch_id);
	        System.out.println("????????????xml: " + result);  
	       
            map = PayCommonUtil.doXMLParse(result);  
        } catch (JDOMException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  catch (Exception ex) {
        	
        	ex.printStackTrace();
        }
        return map;        
    }
	

}