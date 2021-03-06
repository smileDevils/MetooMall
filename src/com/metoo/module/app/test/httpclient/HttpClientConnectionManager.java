package com.metoo.module.app.test.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Component
public class HttpClientConnectionManager {

	    public static String KEY_STATUS_CODE = "statusCode";
	    public static String KEY_CONTENT = "content";
		private static CloseableHttpClient ossClient;
	    
	    public static CloseableHttpClient OSSHelper() throws Exception {
	        // ?????????????????????????????????https??????
	        SSLContext sslcontext = createIgnoreVerifySSL();

	        // ????????????http???https???????????????socket?????????????????????
	        SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslcontext,
	                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
	                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", ssl).build();
	        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	        HttpClients.custom().setConnectionManager(connManager);

	        return ossClient = HttpClients.custom().setConnectionManager(connManager).build();
	    }
	    
	    /**
	     * ????????????
	     * 
	     * @return
	     * @throws NoSuchAlgorithmException
	     * @throws KeyManagementException
	     */
	    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {

	        SSLContext sc = SSLContext.getInstance("TLS");

	        // ????????????X509TrustManager?????????????????????????????????????????????????????????
	        X509TrustManager trustManager = new X509TrustManager() {
	            @Override
	            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
	                    String paramString) throws CertificateException {}

	            @Override
	            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
	                    String paramString) throws CertificateException {}

	            @Override
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sc.init(null, new TrustManager[] { trustManager }, null);
	        return sc;
	    }
	 
	    //??????HttpClient?????????????????????
	    private final static PoolingHttpClientConnectionManager poolConnManager 
	    								= new PoolingHttpClientConnectionManager();  //??????????????????
	    
	    private final static HttpRequestRetryHandler httpRequestRetryHandler 
	    								= new HttpRequestRetryHandler() {  //retry handler
	        public boolean retryRequest(IOException exception,
	                                    int executionCount, HttpContext context) {
	            if (executionCount >= 5) {
	                return false;
	            }
	            if (exception instanceof NoHttpResponseException) {
	                return true;
	            }
	            if (exception instanceof InterruptedIOException) {
	                return false;
	            }
	            if (exception instanceof UnknownHostException) {
	                return false;
	            }
	            if (exception instanceof ConnectTimeoutException) {
	                return false;
	            }
	            HttpClientContext clientContext = HttpClientContext
	                    .adapt(context);
	            HttpRequest request = clientContext.getRequest();
	 
	            if (!(request instanceof HttpEntityEnclosingRequest)) {
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    static RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(10000)//???????????????????????????
				.setConnectTimeout(10000)// ?????????????????????
				.setConnectionRequestTimeout(10000)// ???????????????????????????????????????
				.build();
	 
	    static {   //?????????????????? ????????????????????? ??? ??????????????????????????????
	        poolConnManager.setMaxTotal(2000); //???????????????
	        poolConnManager.setDefaultMaxPerRoute(1000);// ????????????host???????????????????????????
	    }
	 
	    /**
	     * ########################### core code#######################
	     * @return
	     */
	    private static CloseableHttpClient getCloseableHttpClient() {
	        CloseableHttpClient httpClient = HttpClients.custom()
	                .setConnectionManager(poolConnManager)
	                .setRetryHandler(httpRequestRetryHandler)
	                .setDefaultRequestConfig(requestConfig)
	                .build();
	 
	        return httpClient;
	    }
	 
	    /**
	     * buildResultMap
	     *
	     * @param response
	     * @param entity
	     * @return
	     * @throws IOException
	     */
	    private static Map<String, Object> buildResultMap(CloseableHttpResponse response, HttpEntity entity) throws
	            IOException {
	        Map<String, Object> result;
	        result = new HashMap<>(2);
	        result.put(KEY_STATUS_CODE, response.getStatusLine().getStatusCode());  //status code
	        if (entity != null) {
	            result.put(KEY_CONTENT, EntityUtils.toString(entity, "UTF-8")); //message content
	        }
	        return result;
	    }
	 
	    /**
	     * send json by post method
	     *
	     * @param url
	     * @param message
	     * @return
	     * @throws Exception
	     */
	    public static Map<String, Object> postJson(String url, String token, Map<String,String> params) throws Exception {
	    	CloseableHttpClient asd = OSSHelper();
	        Map<String, Object> result = null;
	        HttpPost httpPost = new HttpPost(url);
	        
	        CloseableHttpResponse response = null;
	        try {
	            httpPost.setHeader("Accept", "application/json;charset=UTF-8");
	            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//	            httpPost.setHeader("Authorization",  "Basic" + token);
	            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	            for(Map.Entry<String,String> entry : params.entrySet()){
	            	pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
	            }
	            httpPost.setEntity(new UrlEncodedFormEntity(pairs,"UTF-8"));
	            response = ossClient.execute(httpPost);
	            HttpEntity entity = response.getEntity();
	            // response.getStatusLine().getStatusCode();
	            result = buildResultMap(response, entity);
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (response != null) {
	                try {
	                    EntityUtils.consume(response.getEntity());
	                    response.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return result;
	    }
	    
	    public void getstrign(){
	    	
	    }
	    
	}
