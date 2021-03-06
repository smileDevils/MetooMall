package com.metoo.module.app.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.xpath.DefaultXPath;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.GoodsSku;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.Transport;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreStatService;
import com.metoo.foundation.service.ISystemTipService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserService;
import com.metoo.module.app.buyer.domain.SOAPUtils;
import com.metoo.module.app.pojo.WebserviceResultBean;



@Controller
public class MTest {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ITransportService transportService;
	@Autowired 
	private IUserService userService;
	@Autowired 
	private IEvaluateService evaluateService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private IOrderFormService orderService;
	public static List LIST = new ArrayList();
	public static Map MAP = new HashMap();
	/**
	 * 9-26
	 * @param request
	 * @param response
	 */
	@RequestMapping("/goods_tranFee.json")
	public void goods_tranFee(HttpServletRequest request, HttpServletResponse response){
		Map params = new HashMap();
		params.put("goods_type", 1);
		List<Goods> goodses = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type and transport_id=null", params, -1, -1);
		List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);	
		for(Goods obj : goodses){
			System.err.println(obj.getId());
				if(obj.getGoods_transfee() == 0){ //???????????????????????????0??????????????????1???????????????
					if(obj.getGoods_store().getGrade().getGradeName().equals("China")){
						for(Transport tobj : transPort){
							if(tobj.getTrans_name().equals("China")){
								System.out.println(tobj.getTrans_name());
								obj.setTransport(tobj);
								this.goodsService.update(obj);
							}
						}
					}else{
						if(obj.getGoods_store().getGrade().getGradeName().equals("Dubai")){	
							for(Transport tobj : transPort){
								if(tobj.getTrans_name().equals("Dubai")){
									System.out.println(tobj.getTrans_name());
									obj.setTransport(tobj);
									this.goodsService.update(obj);
								}
							}
						}
					}
				}
		}
	}
	
	@RequestMapping("/goods_map.json")
	public void goods_map(HttpServletRequest request, HttpServletResponse response){
		Map params = new HashMap();
		params.put("goods_type", 1);
		
		List<Goods> goodses = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type and transport_id=null", params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for(Goods obj : goodses){
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGoods_name());
			list.add(map);
		}
		try {
			response.getWriter().print(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private int eva_rate(String rate) {
		int score = 0;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 2;
		} else if (rate.equals("c")) {
			score = 3;
		} else if (rate.equals("d")) {
			score = 4;
		} else if (rate.equals("e")) {
			score = 5;
		}
		return score;
	}
	
	
	  @Test
	  public void traverse(HttpServletRequest request, HttpServletResponse resposne){
		  Map<String, String> map=new HashMap<String, String>();
		  map.put("??????1", "???");
		  map.put("??????2", "???");
		  map.put("??????3", "???");
		  map.put("??????4", "???");
		  map.put("??????5", "???");
		  //???????????????map????????????????????????for??????map.keySet()??????????????????key?????????value???
		  for(String s:map.keySet()){
		   System.out.println("key : "+s+" value : "+map.get(s));
		  }
		  System.out.println("====================================");
		  //?????????????????????????????????????????????for??????
		  for(String s1:map.keySet()){//??????map??????
		   System.out.println("???key ???"+s1);
		  }
		  for(String s2:map.values()){//??????map??????
		   System.out.println("???value ???"+s2);
		  }
		  System.out.println("===================================="); 
		  //???????????????Map.Entry<String, String>?????????for?????????????????????key??????value
		  for(Map.Entry<String, String> entry : map.entrySet()){
		   System.out.println("??? key ???"+entry.getKey()+" ???value ???"+entry.getValue());
		  }
		  System.out.println("====================================");
		  //?????????Iterator??????????????????????????????Map.Entry<String, String>????????????getKey()???getValue()
		  Iterator<Map.Entry<String, String>> it=map.entrySet().iterator();
		  while(it.hasNext()){
		   Map.Entry<String, String> entry=it.next();
		   System.out.println("???key ???"+entry.getKey()+" value ???"+entry.getValue());
		  }
		  System.out.println("====================================");
		 }
	  
	  @RequestMapping("/return.json")
	  public void requestparam4(HttpServletRequest request, HttpServletResponse response,String token) {
		  	Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
		  Map<String, String> map=new HashMap<String, String>();
		  map.put("??????1", "???");
		  map.put("??????2", "???");
		  map.put("??????3", "???");
		  map.put("??????4", "???");
		  map.put("??????5", "???");
		  
		  try { 
			response.getWriter().print(JSON.toJSONString(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
		
	  @Test
	  public void sel(){
		  System.out.println("select");
	  }
	  
	  @RequestMapping("/return2.json")
	  @ResponseBody
	  public void returnJson2(HttpServletRequest request, HttpServletResponse response){
		 /* Map<String, String> map=new HashMap<String, String>();
		  map.put("??????1", "???");
		  map.put("??????2", "???");
		  map.put("??????3", "???");
		  map.put("??????4", "???");
		  map.put("??????4", "???");
		  map.put("??????5", "???");*/
		  List<Integer> list = new ArrayList<Integer>();
		  list.add(1);
		  list.add(2);
		  list.add(2);
		  list.add(3);
		  list.add(4);
		  list.add(5);
		  
		  
		  
		  try {
			//response.getWriter().print(Json.toJson(list, JsonFormat.compact()));
			response.getWriter().print(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  @RequestMapping("/global_changes.json")
	  public void goods_cgoods(HttpServletRequest request, HttpServletResponse response){
		  Map params = new HashMap();
		  params.put("goods_type", 1);
		  List<Goods> goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type", params, -1, -1);
		  int num = 0;
		  for(Goods goods : goods_list){
			  if(goods.getGoodsSkuList().size() != 0){
				  List<GoodsSku> goodsSkuList = goods.getGoodsSkuList();
				  List<Integer> inventorys = new ArrayList<Integer>(); 
				  for(GoodsSku goodsSku : goodsSkuList){
					  inventorys.add(goodsSku.getGoods_inventory());
				  }
				  goods.setGoods_inventory(inventorys.size() == 0 ? 0 : CommUtil.null2Int(Collections.min(inventorys)));
				  this.goodsService.save(goods);
			  }
		  }
		  
		  try {
			response.getWriter().print(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
/*	  
	  @RequestMapping("/cookie.json")
	  public void cookie(HttpServletRequest request, HttpServletResponse response){
		  String requestUrl = request.getRequestURL().toString();//???????????????URL??????
	       String requestUri = request.getRequestURI();//?????????????????????
	       String queryString = request.getQueryString();//???????????????URL????????????????????????
	       String remoteAddr = request.getRemoteAddr();//??????????????????IP??????
	       String remoteHost = request.getRemoteHost();
	       int remotePort = request.getRemotePort();
	       String remoteUser = request.getRemoteUser();
	       String method = request.getMethod();//????????????URL????????????????????????
	       String pathInfo = request.getPathInfo();
	       String localAddr = request.getLocalAddr();//??????WEB????????????IP??????
	       String localName = request.getLocalName();//??????WEB?????????????????????
	      
	       Cookie[] cookies = request.getCookies();
	       List<Map> cookie_list = new ArrayList<Map>();
	       for(Cookie cookie : cookies){
	    	   Map cookie_map = new HashMap();
	    	   cookie_map.put("comment", cookie.getComment());
	    	   cookie_map.put("domain", cookie.getDomain());
	    	   cookie_map.put("maxAge", cookie.getMaxAge());
	    	   cookie_map.put("name", cookie.getName());
	    	   cookie_map.put("path", cookie.getPath());
	    	   cookie_map.put("secure", cookie.getSecure());
	    	   cookie_map.put("value", cookie.getValue());
	    	   cookie_map.put("version", cookie.getVersion());
	    	   cookie_list.add(cookie_map);
	       }
	       
	       //???????????????????????????????????????UTF-8????????????????????????????????????????????????????????????????????????????????????
	       response.setHeader("content-type", "text/html;charset=UTF-8");
	       PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       out.write("????????????????????????????????????");
	       out.write("<hr/>");
	       out.write("?????????URL?????????"+requestUrl);
	       out.write("<br/>");
	       out.write("??????????????????"+requestUri);
	       out.write("<br/>");
	       out.write("?????????URL???????????????????????????"+queryString);
	       out.write("<br/>");
	       out.write("????????????IP?????????"+remoteAddr);
	       out.write("<br/>");
	       out.write("????????????????????????"+remoteHost);
	       out.write("<br/>");
	       out.write("?????????????????????"+remotePort);
	       out.write("<br/>");
	       out.write("remoteUser???"+remoteUser);
	       out.write("<br/>");
	       out.write("????????????????????????"+method);
	       out.write("<br/>");
	       out.write("pathInfo???"+pathInfo);
	       out.write("<br/>");
	       out.write("localAddr???"+localAddr);
	       out.write("<br/>");
	       out.write("localName???"+localName);
	       out.write("<br/>");
	       out.write("Cookie???"+cookie_list);
	  }*/
	  
	 /*@RequestMapping("add_cookie.json")
	  public void add_cookie(HttpServletRequest request, HttpServletResponse response) throws IOException{
		    response.setCharacterEncoding("UTF-8");//?????????????????????????????????
	        response.setContentType("text/plain");//???????????????????????????
	        PrintWriter out=response.getWriter();
			 Cookie cok = new Cookie("goodscookie", "123");
		     cok.setDomain(CommUtil.generic_domain(request));
		     cok.setMaxAge(60 * 60 * 24 * 7);
		     cok.setDomain(CommUtil.generic_domain(request));
		     response.addCookie(cok);
	  }*/
	  
	  
	  public void className(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException{
		  Goods obj = new Goods();
		  obj.setGoods_name("name");
		Class class1 = Class.forName(obj.getClass().getName());
		 Method[] m =  class1.getDeclaredMethods();
		 class1.getMethods();
	  }
	  
	  
	  @RequestMapping("/orderTimes.json")
	  public void runTimeOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			params.put("update", 1);
			List<OrderForm> orderForms = this.orderService.query("select obj from OrderForm obj where obj.update_status=:update", params, -1, 10);
			if(orderForms.size() > 0){
				for(OrderForm order : orderForms){
					if(order.getOrder_status() == 30 && order.getUpdate_status() == 1){
						Map express_map = Json.fromJson(Map.class,
								order.getExpress_info());
						//??????ddu
						String sendMsg = appendXmlContextTow("DDU9239348", "E00030");//??????????????????
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
					        	WebserviceResultBean ret = parseSoapMessage(xmlResult);
					        	
					        	SOAPMessage msg = formatSoapString(xmlResult);
					            SOAPBody body = msg.getSOAPBody();
					            Iterator<SOAPElement> iterator = body.getChildElements();
					            PrintBody(iterator, null);
					            System.out.println(MAP);
					            
					        	order.setUpdate_status(2);
					        	/*String BDate = SOAPUtils.getXmlMessageByName(xmlResult, "BDate");
					        	String BNumber = SOAPUtils.getXmlMessageByName(xmlResult, "BNumber");
					        	String CStatus = SOAPUtils.getXmlMessageByName(xmlResult, "CStatus"); */
					        	String array = SOAPUtils.getXmlMessageByName(xmlResult, "responseArray");
					        	
					        	//System.out.println(array);
					        	Map json_map = new HashMap();
								order.setExpress_info(Json.toJson(json_map));
					        	this.orderService.update(order);
					        }
					}
				}
			}else{
				params.clear();
				params.put("update", 2);
				orderForms = this.orderService.query("select obj from OrderForm obj where obj.update_status=:update", params, -1, -1);
				if(orderForms.size() > 0){
					for(OrderForm order : orderForms){
						order.setUpdate_status(1);
						this.orderService.update(order);
					}
				}
				
			}
			response.getWriter().print("update is null");
		}
	  
	  /**
	     * ??????soapXML
	     * @param soapXML
	     * @return
	     */
	    public static WebserviceResultBean parseSoapMessage(String soapXML) {
	        WebserviceResultBean resultBean = new WebserviceResultBean();
	        try {
	            SOAPMessage msg = formatSoapString(soapXML);
	            SOAPBody body = msg.getSOAPBody();
	            Iterator<SOAPElement> iterator = body.getChildElements();
	            parse(iterator, resultBean);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return resultBean;
	    }
	  

	   /**
	     * ???soap?????????????????????SOAPMessage
	     *
	     * @param soapString
	     * @return
	     * @see [?????????#????????????#??????]
	     */
	    public static SOAPMessage formatSoapString(String soapString) {
	        MessageFactory msgFactory;
	        try {
	            msgFactory = MessageFactory.newInstance();
	            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(),
	                    new ByteArrayInputStream(soapString.getBytes("UTF-8")));
	            reqMsg.saveChanges();
	            return reqMsg;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    
	 
	    /**
	     * ??????soap xml
	     * @param iterator
	     * @param resultBean
	     */
	    private static void parse(Iterator<SOAPElement> iterator, WebserviceResultBean resultBean) {
	        while (iterator.hasNext()) {
	            SOAPElement element = iterator.next();
	            if ("ns:BASEINFO".equals(element.getNodeName())) {
	                continue;
	            } else if ("ns1:GetStatusDetailsResponse".equals(element.getNodeName())) {
	                Iterator<SOAPElement> it = element.getChildElements();
	                SOAPElement el = null;
	                while (it.hasNext()) {
	                    el = it.next();
	                    if ("RESULT".equals(el.getLocalName())) {
	                        resultBean.setResult(el.getValue());
	                        System.out.println("#### " + el.getLocalName() + "  ====  " + el.getValue());
	                    } else if ("REMARK".equals(el.getLocalName())) {
	                        resultBean.setRemark(null != el.getValue() ? el.getValue() : "");
	                        System.out.println("#### " + el.getLocalName() + "  ====  " + el.getValue());
	                    } else if ("XMLDATA".equals(el.getLocalName())) {
	                        resultBean.setXmlData(el.getValue());
	                        System.out.println("#### " + el.getLocalName() + "  ====  " + el.getValue());
	                    }
	                }
	            } else if (null == element.getValue()
	                    && element.getChildElements().hasNext()) {
	                parse(element.getChildElements(), resultBean);
	            }
	        }
	    }
	 
	 
	    private static void PrintBody(Iterator<SOAPElement> iterator,String side) {
	        while (iterator.hasNext()) {
	        	Object o=iterator.next();
	        	if(o!=null)  {
	        		SOAPElement element=null;
	        		try{
	                    element = (SOAPElement) o;
	                    
	                    MAP.put(element.getNodeName(), element.getValue());
	        		}catch(Exception e){}
	        		 if ( element !=null ) {
	                   PrintBody(element.getChildElements(), side + "-----");
	                 }

	        	}
	        }
	    }
		
	    
	  public static String getXmlMessageByName(String xmlResult, String nodeName) throws DocumentException {
	        Document doc = DocumentHelper.parseText(xmlResult);
	        DefaultXPath xPath = new DefaultXPath("//" + nodeName);
	        xPath.setNamespaceURIs(Collections.singletonMap("ns1", "http://cn.gov.chinatax.gt3nf.nfzcpt.service/"));
	        List list = xPath.selectNodes(doc);
	        if (!list.isEmpty() && list.size() > 0) {
	            Element node = (Element) list.get(0);
	            return node.getText();
	        }
	        return "";
	    }
	  
		/**
	     * @methodsName appendXmlContextTow
	     * @description ??????????????????
	     * @param ddutaskRequest
	     * @return String
	     */
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
