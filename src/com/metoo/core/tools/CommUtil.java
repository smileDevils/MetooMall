package com.metoo.core.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.metoo.core.query.support.IPageList;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.GameGoods;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsVoucher;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSkuService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.lucene.LuceneResult;
import com.metoo.lucene.LuceneVo;
import com.metoo.manage.admin.tools.StoreTools;
import com.metoo.module.app.buyer.domain.Http;
import com.metoo.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: CommUtil.java
 * </p>
 * 
 * <p>
 * Description:
 * ????????????????????????????????????,????????????????????????????????????ModelAndView????????????????????????$!CommUtil.xxx??????????????????
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: ?????????????????????????????? www.koala.com
 * </p>
 * 
 * @author hu
 * 
 * @date 2019-10-10
 * 
 * @version metoo_b2b2c v1.0 2019???
 */
@Component
public class CommUtil {
	
	@Autowired
	private ISysConfigService configService;
	
	private static CommUtil commUtil;
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSkuService goodsSkuService;

	@PostConstruct
	public void init() {
		commUtil = this;
		commUtil.configService = this.configService;
		commUtil.userService = this.userService;
	}

	private static final java.text.SimpleDateFormat dateFormat = new

	java.text.SimpleDateFormat("yyyy-MM-dd");

	public static String first2low(String str) {
		String s = "";
		s = str.substring(0, 1).toLowerCase() + str.substring(1);
		return s;
	}

	public static String first2upper(String str) {
		String s = "";
		s = str.substring(0, 1).toUpperCase() + str.substring(1);
		return s;
	}

	/**
	 * @description ??????""?????????????????????
	 * @param blank
	 * @return
	 */
	public static int blank(String blank) {
		StringTokenizer st = new StringTokenizer(blank, " ");
		int numble = st.countTokens();
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
		}
		return numble;

	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static List<String> str2list(String s) throws IOException {
		List<String> list = new ArrayList<String>();
		s = CommUtil.null2String(s);
		if (!s.equals("")) {
			StringReader fr = new StringReader(s);
			BufferedReader br = new BufferedReader(fr);
			String aline = "";
			while ((aline = br.readLine()) != null) {
				list.add(aline);
			}
		}
		return list;
	}

	public static java.util.Date formatDate(String s) {
		java.util.Date d = null;
		try {
			d = dateFormat.parse(s);
		} catch (Exception e) {
		}
		return d;
	}

	public static java.util.Date formatDate(String s, String format) {
		java.util.Date d = null;
		try {
			SimpleDateFormat dFormat = new java.text.SimpleDateFormat(format);
			d = dFormat.parse(s);
		} catch (Exception e) {
		}
		return d;
	}

	public static String formatTime(String format, Object v) {
		if (v == null)
			return null;
		if (v.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(v);
	}

	public static String formatLongDate(Object v) {
		if (v == null || v.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(v);
	}

	public static String formatShortDate(Object v) {
		if (v == null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(v);
	}

	public static String formatNumDate(Object v) {
		if (v == null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(v);
	}

	public static String decode(String s) {
		String ret = s;
		try {
			ret = URLDecoder.decode(s.trim(), "UTF-8");
		} catch (Exception e) {
		}
		return ret;
	}

	public static String encode(String s) {
		String ret = s;
		try {
			ret = URLEncoder.encode(s.trim(), "UTF-8");
		} catch (Exception e) {
		}
		return ret;
	}

	public static String convert(String str, String coding) {
		String newStr = "";
		if (str != null)
			try {
				newStr = new String(str.getBytes("ISO-8859-1"), coding);
			} catch (Exception e) {
				return newStr;
			}
		return newStr;
	}

	/**
	 * saveFileToServer ??????????????????????????????
	 * 
	 * @param filePath????????????????????????
	 *            ???
	 * @param saveFilePathName????????????????????????
	 * @param saveFileName??????????????????
	 * @param extendes???????????????????????????
	 *            , *
	 * @return ????????????map???map??????4???????????????????????????????????????fileName,?????????????????????????????????fileSize,,
	 *         ???????????????????????????????????????errors,????????????????????????map?????????smallFileName??????????????????????????????
	 */
	public static Map saveAccountFileToServer(HttpServletRequest request, MultipartFile file, String saveFilePathName,
			String saveFileName, String[] extendes) throws IOException {
		Map map = new HashMap();
		if (file != null && !file.isEmpty()) {

			String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.toLowerCase();
			if (saveFileName == null || saveFileName.trim().equals("")) {
				saveFileName = UUID.randomUUID().toString() + "." + extend;
			}
			if (saveFileName.lastIndexOf(".") < 0) {
				saveFileName = saveFileName + "." + extend;
			}
			float fileSize = Float.valueOf(file.getSize());// ??????????????????????????????k
			List<String> errors = new java.util.ArrayList<String>();
			boolean flag = true;
			if (extendes == null) {
				extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
			}
			for (String s : extendes) {
				if (extend.toLowerCase().equals(s))
					flag = true;
			}
			if (flag) {
				File path = new File(saveFilePathName);
				if (!path.exists()) {
					path.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				DataOutputStream out = new DataOutputStream(
						new FileOutputStream(saveFilePathName + File.separator + saveFileName));
				InputStream is = null;
				try {
					is = file.getInputStream();
					int size = (int) (fileSize);
					byte[] buffer = new byte[size];
					while (is.read(buffer) > 0) {
						out.write(buffer);
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				} finally {
					if (is != null) {
						is.close();
					}
					if (out != null) {
						out.close();
					}
				}
				if (isImg(extend)) {
					File img = new File(saveFilePathName + File.separator + saveFileName);
					try {
						BufferedImage bis = ImageIO.read(img);
						int w = bis.getWidth();
						int h = bis.getHeight();
						map.put("width", w);
						map.put("height", h);
					} catch (Exception e) {

					}
				}
				map.put("mime", extend);
				map.put("fileName", saveFileName);
				map.put("fileSize", fileSize);
				map.put("error", errors);
				map.put("oldName", file.getOriginalFilename());

			} else {

				errors.add("?????????????????????");
			}
		} else {
			map.put("width", 0);
			map.put("height", 0);
			map.put("mime", "");
			map.put("fileName", "");
			map.put("fileSize", 0.0f);
			map.put("oldName", "");
		}
		return map;
	}

	public static boolean isImg(String extend) {
		boolean ret = false;
		List<String> list = new java.util.ArrayList<String>();
		list.add("jpg");
		list.add("jpeg");
		list.add("bmp");
		list.add("gif");
		list.add("png");
		list.add("tif");
		list.add("tbi");
		for (String s : list) {
			if (s.equals(extend))
				ret = true;
		}
		return ret;
	}

	/**
	 * ???????????????????????????gif png???????????????png????????????
	 * 
	 * @param pressImg
	 *            ????????????
	 * @param targetImg
	 *            ????????????
	 * @param pos
	 *            ????????????????????????????????????
	 * @param alpha
	 *            ?????????????????????
	 */
	public final static void waterMarkWithImage(String pressImg, String targetImg, int pos, float alpha) {
		try {
			// ????????????
			Image theImg = Toolkit.getDefaultToolkit().getImage(targetImg);
			theImg.flush();
			BufferedImage bis = toBufferedImage(theImg);
			int width = theImg.getWidth(null);
			int height = theImg.getHeight(null);
			bis = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bis.createGraphics();
			g.drawImage(theImg, 0, 0, width, height, null);

			// ????????????
			File _filebiao = new File(pressImg);
			Image src_biao = ImageIO.read(_filebiao);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha / 100));
			int width_biao = src_biao.getWidth(null);
			int height_biao = src_biao.getHeight(null);
			int x = 0;
			int y = 0;
			if (pos == 1) {

			}
			if (pos == 2) {
				x = (width - width_biao) / 2;
				y = 0;
			}
			if (pos == 3) {
				x = width - width_biao;
				y = 0;
			}
			if (pos == 4) {
				x = width - width_biao;
				y = (height - height_biao) / 2;
			}
			if (pos == 5) {
				x = width - width_biao;
				y = height - height_biao;
			}
			if (pos == 6) {
				x = (width - width_biao) / 2;
				y = height - height_biao;
			}
			if (pos == 7) {
				x = 0;
				y = height - height_biao;
			}
			if (pos == 8) {
				x = 0;
				y = (height - height_biao) / 2;
			}
			if (pos == 9) {
				x = (width - width_biao) / 2;
				y = (height - height_biao) / 2;
			}
			g.drawImage(src_biao, x, y, width_biao, height_biao, null);
			// ??????????????????
			g.dispose();
			FileOutputStream out = new FileOutputStream(targetImg);
			ImageIO.write(bis, "JPEG", out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ??????????????? V1.3?????????????????????????????????????????????
	 * 
	 * @param source
	 *            ?????????
	 * @param target
	 *            ????????????
	 * @param width
	 *            ?????????????????????????????????????????????
	 * @return ????????????????????????
	 */
	public static boolean createSmall(String source, String target, int width, int height) {
		try {
			// sun.awt.image.ToolkitImage@492d8cff
			Image img = Toolkit.getDefaultToolkit().getImage(source);
			BufferedImage bis = toBufferedImage(img);
			int w = bis.getWidth();
			int h = bis.getHeight();
			int nw = width;
			int nh = (nw * h) / w;
			ImageCompress.ImageScale(source, target, width, height);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * ???????????????bufferedimage,??????????????????ICC???????????????????????????????????????
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		// boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			/*
			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
			 */

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
			 */
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * ??????????????? V 1.3????????????
	 * 
	 * @param source
	 *            ?????????
	 * @param target
	 *            ????????????
	 * @param width
	 *            ?????????????????????????????????????????????
	 * @return ????????????????????????
	 */
	public static boolean createSmall_old(String source, String target, int width) {
		try {
			File sourceFile = new File(source);
			File targetFile = new File(target);
			BufferedImage bis = ImageIO.read(sourceFile);
			int w = bis.getWidth();
			int h = bis.getHeight();
			int nw = width;
			int nh = (nw * h) / w;
			ImageScale is = new ImageScale();
			is.saveImageAsJpg(source, target, nw, nh);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @param filePath
	 *            ????????????????????????????????????
	 * @param outPath
	 *            ??????????????????????????????
	 * @param markContent
	 *            ???????????????
	 * @param markContentColor
	 *            ?????????????????????
	 * @param font
	 *            ???????????? ?????????
	 * @param left
	 *            ?????????????????????????????????????????????
	 * @param top
	 *            ??????????????????????????????????????????
	 * @param qualNum
	 *            ????????????
	 * @return
	 */
	public static boolean waterMarkWithText(String filePath, String outPath, String text, String markContentColor,
			Font font, int pos, float qualNum) {
		Image theImg = Toolkit.getDefaultToolkit().getImage(filePath);
		theImg.flush();
		BufferedImage bis = toBufferedImage(theImg);
		int width = bis.getWidth(null);
		int height = bis.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("??????", Font.BOLD, 30);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(getColor(markContentColor));
		g.setBackground(Color.white);
		g.drawImage(theImg, 0, 0, null);
		FontMetrics metrics = new FontMetrics(font) {
		};
		Rectangle2D bounds = metrics.getStringBounds(text, null);
		int widthInPixels = (int) bounds.getWidth();
		int heightInPixels = (int) bounds.getHeight();
		int left = 0;
		int top = heightInPixels;
		if (pos == 1) {

		}
		if (pos == 2) {
			left = width / 2;
			top = heightInPixels;
		}
		if (pos == 3) {
			left = width - widthInPixels;
			top = heightInPixels;
		}
		if (pos == 4) {
			left = width - widthInPixels;
			top = height / 2;
		}
		if (pos == 5) {
			left = width - widthInPixels;
			top = height - heightInPixels;
		}
		if (pos == 6) {
			left = width / 2;
			top = height - heightInPixels;
		}
		if (pos == 7) {
			left = 0;
			top = height - heightInPixels;
		}
		if (pos == 8) {
			left = 0;
			top = height / 2;
		}
		if (pos == 9) {
			left = width / 2;
			top = height / 2;
		}
		g.drawString(text, left, top); // ?????????????????????????????????????????????????????????
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage, "JPEG", out);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean createFolder(String folderPath) {
		boolean ret = true;
		try {
			java.io.File myFilePath = new java.io.File(folderPath);
			if (!myFilePath.exists() && !myFilePath.isDirectory()) {
				ret = myFilePath.mkdirs();
				if (!ret) {
					System.out.println("?????????????????????");
				}
			}
		} catch (Exception e) {
			System.out.println("?????????????????????");
			ret = false;
		}
		return ret;
	}

	public static List toRowChildList(List list, int perNum) {
		// System.out.println("??????toRowChildList");
		List l = new java.util.ArrayList();
		if (list == null)
			return l;
		// System.out.println("?????????"+list.size());
		// System.out.println("perNum:"+perNum);
		for (int i = 0; i < list.size(); i += perNum) {
			List cList = new ArrayList();
			for (int j = 0; j < perNum; j++)
				if (i + j < list.size())
					cList.add(list.get(i + j));
			l.add(cList);
		}
		return l;
	}

	public static List copyList(List list, int begin, int end) {
		List l = new ArrayList();
		if (list == null)
			return l;
		if (end > list.size())
			end = list.size();
		for (int i = begin; i < end; i++) {
			l.add(list.get(i));
		}
		return l;
	}

	public static boolean isNotNull(Object obj) {
		if (obj != null && !obj.toString().equals("")) {
			return true;
		} else
			return false;
	}

	/**
	 * ??????????????????
	 * 
	 * @param oldPath
	 *            String ??????????????? ??????c:/fqf.txt
	 * @param newPath
	 *            String ??????????????? ??????f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // ???????????????
				InputStream inStream = new FileInputStream(oldPath); // ???????????????
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // ????????? ????????????
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("?????????????????????????????? ");
			e.printStackTrace();
		}
	}

	/**
	 * ???????????????????????????????????????????????????????????????
	 * 
	 * @param path
	 *            ???????????????????????????
	 * @return ?????????????????? true??????????????? false???
	 */
	public static boolean deleteFolder(String path) {
		boolean flag = false;
		File file = new File(path);
		// ?????????????????????????????????
		if (!file.exists()) { // ??????????????? false
			return flag;
		} else {
			// ?????????????????????
			if (file.isFile()) { // ????????????????????????????????????
				return deleteFile(path);
			} else { // ????????????????????????????????????
				return deleteDirectory(path);
			}
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param path
	 *            ???????????????????????????
	 * @return ??????????????????????????????true???????????????false
	 */
	public static boolean deleteFile(String path) {
		boolean flag = false;
		File file = new File(path);
		// ??????????????????????????????????????????
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @param path
	 *            ??????????????????????????????
	 * @return ????????????????????????true???????????????false
	 */
	public static boolean deleteDirectory(String path) {
		// ??????sPath?????????????????????????????????????????????????????????
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		// ??????dir???????????????????????????????????????????????????????????????
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// ?????????????????????????????????(???????????????)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// ???????????????
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // ???????????????
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// ??????????????????
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ?????????????????????urlwriter??????
	 * 
	 * @param url
	 * @param currentPage
	 * @param pages
	 * @return
	 */
	public static String showPageStaticHtml(String url, int currentPage, int pages) {
		String s = "";
		if (pages > 0) {
			if (currentPage >= 1) {
				s += "<a href='" + url + "_1.htm'>??????</a> ";
				if (currentPage > 1)
					s += "<a href='" + url + "_" + (currentPage - 1) + ".htm'>?????????</a> ";
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				s += "??????";
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++)
					if (i == currentPage)
						s += "<a class='this' href='" + url + "_" + i + ".htm'>" + i + "</a> ";
					else
						s += "<a href='" + url + "_" + i + ".htm'>" + i + "</a> ";
				s += "??????";
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					s += "<a href='" + url + "_" + (currentPage + 1) + ".htm'>?????????</a> ";
				}
				s += "<a href='" + url + "_" + pages + ".htm'>??????</a> ";
			}
		}
		return s;
	}

	/**
	 * ??????????????????????????????get????????????
	 * 
	 * @param url
	 * @param params
	 * @param currentPage
	 * @param pages
	 * @return
	 */
	public static String showPageHtml(String url, String params, int currentPage, int pages) {
		String s = "";
		if (pages > 0) {
			if (currentPage >= 1) {
				s += "<a href='" + url + "?currentPage=1" + params + "'>??????</a> ";
				if (currentPage > 1)
					s += "<a href='" + url + "?currentPage=" + (currentPage - 1) + params + "'>?????????</a> ";
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				s += "??????";
				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++)
					if (i == currentPage)
						s += "<a class='this' href='" + url + "?currentPage=" + i + params + "'>" + i + "</a> ";
					else
						s += "<a href='" + url + "?currentPage=" + i + params + "'>" + i + "</a> ";
				s += "??????";
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					s += "<a href='" + url + "?currentPage=" + (currentPage + 1) + params + "'>?????????</a> ";
				}
				s += "<a href='" + url + "?currentPage=" + pages + params + "'>??????</a> ";
			}
		}
		// s+=" ??????<input type=text size=2>???";
		return s;
	}

	/**
	 * ????????????????????????????????????????????????form?????????????????????????????????
	 * 
	 * @param currentPage
	 * @param pages
	 * @return
	 */
	public static String showPageFormHtml(int currentPage, int pages) {
		String s = "";
		if (pages > 0) {
			if (currentPage >= 1) {
				s += "<a class='btn_first' href='javascript:void(0);' title='??????' onclick='return gotoPage(1)'>First</a> ";
				if (currentPage > 1)
					s += "<a class='btn_prev' href='javascript:void(0);' title='?????????' onclick='return gotoPage("
							+ (currentPage - 1) + ")'>Previous Page</a> ";
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {

				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++)
					if (i == currentPage)
						s += "<a class='this' href='javascript:void(0);' title='???" + i + "???' onclick='return gotoPage("
								+ i + ")'>" + i + "</a> ";
					else
						s += "<a href='javascript:void(0);' title='???" + i + "???' onclick='return gotoPage(" + i +

								")'>" + i + "</a> ";

			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					s += "<a class='btn_next' href='javascript:void(0);' title='?????????' onclick='return gotoPage("
							+ (currentPage + 1) + ")'>Next</a> ";
				}
				s += "<a class='btn_last' href='javascript:void(0);' title='??????' onclick='return gotoPage(" + pages
						+ ")'>Last</a> ";
			}
		}
		// s+=" ??????<input type=text size=2>???";
		return s;
	}

	/**
	 * ajax?????????????????????json????????????
	 * 
	 * @param url
	 * @param params
	 * @param currentPage
	 * @param pages
	 * @return
	 */
	public static String showPageAjaxHtml(String url, String params, int currentPage, int pages) {
		String s = "";
		if (pages > 0) {
			String address = url + "?1=1" + params;
			if (currentPage >= 1) {
				/*
				 * s +=
				 * "<a href='javascript:void(0);' onclick='return ajaxPage(\"" +
				 * address + "\",1,this)'>??????</a> ";
				 */
				s += "<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + (currentPage - 1)
						+ ",this)'>Previous Page</a> ";
			}

			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {

				for (int i = beginPage, j = 0; i <= pages && j < 6; i++, j++)
					if (i == currentPage)
						s += "<a class='this' href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\","
								+ i + ",this)'>" + i + "</a> ";
					else
						s += "<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + i +

								",this)'>" + i + "</a> ";

			}
			if (currentPage <= pages) {
				s += "<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address + "\"," + (currentPage + 1)
						+ ",this)'>Next page</a> ";
				/*
				 * s +=
				 * "<a href='javascript:void(0);' onclick='return ajaxPage(\"" +
				 * address + "\"," + pages + ",this)'>??????</a> ";
				 */
			}
			// s+=" ??????<input type=text size=2>???";
		}
		return s;
	}

	/**
	 * ????????????????????????ModelAndView???
	 * 
	 * @param url
	 *            ??????url
	 * @param staticURL
	 *            ????????????URL?????????urlrewrite???????????????
	 * @param params
	 *            ?????????URL?????????
	 * @param pList
	 *            ????????????????????????c
	 * @param mv
	 *            ???????????????s
	 */
	public static void saveIPageList2ModelAndView(String url, String staticURL, String params, IPageList pList,
			ModelAndView mv) {
		if (pList != null) {
			mv.addObject("objs", pList.getResult());
			mv.addObject("totalPage", new Integer(pList.getPages()));// ?????????
			mv.addObject("pageSize", pList.getPageSize());// ??????????????????
			mv.addObject("rows", new Integer(pList.getRowCount()));// ????????????????????????
			mv.addObject("currentPage", new Integer(pList.getCurrentPage()));// ????????????????????????

			mv.addObject("gotoPageHTML", CommUtil.showPageHtml(url, params, pList.getCurrentPage(), pList.getPages()));
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
			mv.addObject("gotoPageStaticHTML",
					CommUtil.showPageStaticHtml(staticURL, pList.getCurrentPage(), pList.getPages()));
			mv.addObject("gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(url, params, pList.getCurrentPage(), pList.getPages()));

		}
	}

	public static List<Map> saveIPageList2ModelAndView2(IPageList pList) {
		List<Address> addresslist = pList.getResult();
		List<Map> addressList = new ArrayList<Map>();
		for (Address obj : addresslist) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("userName", obj.getTrueName());
			map.put("areaInfo", obj.getArea_info());
			map.put("zip", obj.getZip());
			map.put("telephone", obj.getTelephone());
			map.put("mobile", obj.getMobile());
			map.put("defaultVal", obj.getDefault_val());
			map.put("areaUserName", obj.getUser() != null ? obj.getUser().getTrueName() : "");
			Area area = obj.getArea();
			if (null != area) {
				if (area.getLevel() == 2) {
					map.put("country", obj.getArea().getParent().getParent().getAreaName());
					map.put("city", obj.getArea().getParent().getAreaName());
					map.put("area", obj.getArea().getAreaName());
				} else if (area.getLevel() == 1) {
					map.put("country", obj.getArea().getParent().getAreaName());
					map.put("city", obj.getArea().getAreaName());
					map.put("area", "");
				}
			}
			addressList.add(map);
		}
		return addressList;

	}

	/**
	 * ???IPageList?????????ModelAndView?????????????????????????????????????????????????????????objs???????????????????????????????????????????????????????????????
	 * 
	 * @param url
	 * @param staticURL
	 * @param params
	 * @param result_name
	 * @param pList
	 * @param mv
	 */
	public static void saveIPageList2ModelAndView2(String url, String staticURL, String params, String prefix,
			IPageList pList, ModelAndView mv) {
		if (pList != null) {
			mv.addObject(prefix + "_objs", pList.getResult());
			mv.addObject(prefix + "_totalPage", new Integer(pList.getPages()));
			mv.addObject(prefix + "_pageSize", pList.getPageSize());
			mv.addObject(prefix + "_rows", new Integer(pList.getRowCount()));
			mv.addObject(prefix + "_currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject(prefix + "_gotoPageHTML",
					CommUtil.showPageHtml(url, params, pList.getCurrentPage(), pList.getPages()));
			mv.addObject(prefix + "_gotoPageFormHTML",
					CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
			mv.addObject(prefix + "_gotoPageStaticHTML",
					CommUtil.showPageStaticHtml(staticURL, pList.getCurrentPage(), pList.getPages()));
			mv.addObject(prefix + "_gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(url, params, pList.getCurrentPage(), pList.getPages()));
		}
	}

	/**
	 * ???lucene???????????????????????????
	 * 
	 * @param pList
	 * @param mv
	 */
	public static void saveLucene2ModelAndView(LuceneResult pList, ModelAndView mv) {
		if (pList != null) {
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("totalPage", pList.getPages());
			mv.addObject("pageSize", pList.getPageSize());
			mv.addObject("rows", pList.getRows());
			mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
		}
	}

	public static char randomChar() {
		char[] chars = new char[] { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i',
				'I', 'j', 'J', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S',
				't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z' };
		int index = (int) (Math.random() * 52) - 1;
		if (index < 0) {
			index = 0;
		}
		return chars[index];
	}

	public static String[] splitByChar(String s, String c) {
		String[] list = s.split(c);
		return list;
	}

	public static String[] creatArray(int length) {
		if (length > 0) {
			return new String[length];
		}
		return null;
	}

	public static Object requestByParam(HttpServletRequest request, String param) {
		if (!request.getParameter(param).equals("")) {
			return request.getParameter(param);
		} else
			return null;

	}

	public static String substring(String s, int maxLength) {
		if (!StringUtils.hasLength(s))
			return s;
		if (s.length() <= maxLength) {
			return s;
		} else
			return s.substring(0, maxLength) + "...";
	}

	public static String substringfrom(String s, String from) {
		if (s.indexOf(from) < 0)
			return "";
		return s.substring(s.indexOf(from) + from.length());
	}
	
	public static boolean isEmpty(String object){
		if(object != null && !object.equals("")){
			return true;
		}
		return false;
	}

	public static int null2Int(Object s) {
		int v = 0;
		if (s != null)
			try {
				v = Integer.parseInt(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	public static int null2Int_1(Object s) {
		int v = -1;
		if (s != null)
			try {
				v = Integer.parseInt(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	public static float null2Float(Object s) {
		float v = 0.0f;
		if (s != null)
			try {
				v = Float.parseFloat(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	public static double null2Double(Object s) {
		double v = 0.0;
		if (s != null)
			try {
				v = Double.parseDouble(null2String(s));
			} catch (Exception e) {
			}
		return v;
	}

	public static BigDecimal null2BigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null && !value.equals("")) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
						+ " into a BigDecimal.");
			}
		}
		return ret;
	}

	/*
	 * [??????Bigdecimal ??????????????????]
	 */
	public static String bigDeCimal(BigDecimal a) {
		return a.stripTrailingZeros().toPlainString();
	}

	public static boolean null2Boolean(Object s) {
		boolean v = false;
		if (s != null)
			try {
				v = Boolean.parseBoolean(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	/**
	 * [ ?????????????????????null????????????????????????????????????]
	 * 
	 * @param s
	 * @return
	 */
	public static String null2String(Object s) {
		return s == null ? "" : s.toString().trim();
	}

	public static Long null2Long(Object s) {
		Long v = -1l;
		if (s != null)
			try {
				v = Long.parseLong(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	public static void compareTo(List<Map<String, Object>> list){
		Collections.sort(list, new Comparator<Map<String, Object>>() {
	        @Override
	        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	            String key1 = o1.get("probably").toString();
	            String key2 = o2.get("probably").toString();
	            return key2.compareTo(key1);
	        }
	    });
		
	}
	

	public static void compareTo2(List<GameGoods> list){
		Collections.sort(list, new Comparator<GameGoods>() {
	        @Override
	        public int compare(GameGoods o1, GameGoods o2) {
	            String key1 = o1.getProbably().toString();
	            String key2 = o2.getProbably().toString();
	            return key2.compareTo(key1);
	        }
	    });
		
	}

	
	public static void compareTo3(List<GoodsVoucher> list){
		Collections.sort(list, new Comparator<GoodsVoucher>() {
	        @Override
	        public int compare(GoodsVoucher o1, GoodsVoucher o2) {
	            String key1 = o1.getProbably().toString();
	            String key2 = o2.getProbably().toString();
	            return key2.compareTo(key1);
	        }
	    });
	}
	
	
	public static void compareTo(List<Map<String, Object>> list, Long id){
		Collections.sort(list, new Comparator<Map<String, Object>>() {
	        @Override
	        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	        	Map map1 = Json.fromJson(Map.class, Json.toJson(o1.get("probably")));
	        	Map map2 = Json.fromJson(Map.class, Json.toJson(o2.get("probably")));
	        	String key1 = "";
	        	for (Object key : map1.keySet()) {  
	        		key1 = (String) map1.get(key);  
	        	}
	        	String key2 = "";
	         	for (Object key : map2.keySet()) {  
	         		key2 = (String) map2.get(key);  
	        	}
	            return key2.compareTo(key1);
	        }
	    });
	}

	public static void compareToGoods(List<GameGoods> list, Long id){
		Collections.sort(list, new Comparator<GameGoods>() {
	        @Override
	        public int compare(GameGoods o1, GameGoods o2) {
	        	Map map1 = Json.fromJson(Map.class, o1.getProbably());
        		Map map2 = Json.fromJson(Map.class, o2.getProbably());
	        	String key1 = map1.get(id.toString()).toString();
	        	String key2 = map2.get(id.toString()).toString();
	            return key2.compareTo(key1);
	        }
	    });
		
	}
	public static void compareToVoucher(List<GoodsVoucher> list, Long id){
		Collections.sort(list, new Comparator<GoodsVoucher>() {
	        @Override
	        public int compare(GoodsVoucher o1, GoodsVoucher o2) {
	        	Map map1 = Json.fromJson(Map.class, o1.getProbably());
        		Map map2 = Json.fromJson(Map.class, o2.getProbably());
	        	String key1 = map1.get(id.toString()).toString();
	        	String key2 = map2.get(id.toString()).toString();
	            return key2.compareTo(key1);
	        }
	    });
	}
	
	public static String getTimeInfo(long time) {
		int hour = (int) time / (1000 * 60 * 60);
		long balance = time - hour * 1000 * 60 * 60;
		int minute = (int) balance / (1000 * 60);
		balance = balance - minute * 1000 * 60;
		int seconds = (int) balance / 1000;
		String ret = "";
		if (hour > 0)
			ret += hour + "??????";
		if (minute > 0)
			ret += minute + "???";
		else if (minute <= 0 && seconds > 0)
			ret += "???";
		if (seconds > 0)
			ret += seconds + "???";
		return ret;
	}
	
	public static String autoComplete(Accessory accessory){
		if(accessory != null){
			return commUtil.configService.getSysConfig().getImageWebServer() + "/" + accessory.getPath() + "/"
					+ accessory.getName();
		}
		return null;
	}

	/**
	 * ????????????IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			java.net.InetAddress addr = null;
			try {
				addr = java.net.InetAddress.getLocalHost();
			} catch (java.net.UnknownHostException e) {
				e.printStackTrace();
			}
			ip = CommUtil.null2String(addr.getHostAddress());// ????????????IP
		}
		return ip;
	}

	public static int indexOf(String s, String sub) {
		return s.trim().indexOf(sub.trim());
	}

	public static Map cal_time_space(Date begin, Date end) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l = end.getTime() - begin.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long second = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		Map map = new HashMap();
		map.put("day", day);
		map.put("hour", hour);
		map.put("min", min);
		map.put("second", second);
		return map;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param length
	 *            UUID.randomUUID.toString()
	 * @return
	 */
	public static final String randomString(int length) {
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];// nextInt:???????????????????????????????????????????????????????????????
		}
		return new String(randBuffer);
	}

	public static final String randomLowercase(int length) {
		char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789abcdefghijklmnopqrstuvwxyz")
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];// nextInt:???????????????????????????????????????????????????????????????
		}
		return new String(randBuffer);
	}

	/**
	 * ???????????????int??????(APP)
	 * 
	 * @param length
	 * @return
	 */
	public static final String randomIntApp(int length) {
		char[] numbersAndLetters = ("0123456789" + "9876543210").toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(20)];// nextInt:???????????????????????????????????????????????????????????????
		}
		return new String(randBuffer);
	}

	public static final String randomInt(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] numbersAndLetters = ("0123456789").toCharArray();

		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}

	/**
	 * ????????????????????????
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getDateDistance(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}

	
	/**
	 * ???????????????
	 * @return
	 */
	public static Date dayStart(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * ???????????????
	 * @return
	 */
	public static Date dayEnd(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
	
	public static boolean isNow(Date date) {
        //????????????
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //?????????????????????
        String nowDay = sf.format(now);
         
        //???????????????
        String day = sf.format(date);
         
        return day.equals(nowDay);
    }
	
	
	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double div(Object a, Object b) {
		double ret = 0.0;
		if (!null2String(a).equals("") && !null2String(b).equals("")) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0)
				ret = e.divide(f, 3, BigDecimal.ROUND_DOWN).doubleValue();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		
		System.out.println(df.format(ret));
		System.out.println(Double.valueOf(df.format(ret)));
		
		return Double.valueOf(df.format(ret));
	}
	
	public static double div02(Object a, Object b) {
		double ret = 0.0;
		if (!null2String(a).equals("") && !null2String(b).equals("")) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0)
				ret = e.divide(f, 3, BigDecimal.ROUND_DOWN).doubleValue();
		}
		DecimalFormat df = new DecimalFormat("0.000");
		System.out.println(df.format(ret));
		System.out.println(Double.valueOf(df.format(ret)));
		return Double.valueOf(df.format(ret));
	}

	public static double div4(Object a, Object b) {
		double ret = 0.0;
		if (!null2String(a).equals("") && !null2String(b).equals("")) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0)
				ret = e.divide(f, 10, BigDecimal.ROUND_DOWN).doubleValue();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double subtract(Object a, Object b) {
		double ret = 0.0;
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		ret = e.subtract(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * ??????????????????
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double add(Object a, Object b) {
		double ret = 0.0;
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		ret = e.add(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	/**
	 * ???????????????
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double mul(Object a, Object b) {// ??????
		BigDecimal e = new BigDecimal(CommUtil.null2Double(a));
		BigDecimal f = new BigDecimal(CommUtil.null2Double(b));
		double ret = e.multiply(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret));
	}

	public static String formatMoney(Object money) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(money);
	}

	public static int M2byte(float m) {
		float a = m * 1024 * 1024;
		return (int) a;
	}

	public static boolean convertIntToBoolean(int intValue) {
		return (intValue != 0);
	}

	public static String getURL(HttpServletRequest request) {
		String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
		String url = "//" + request.getServerName();
		if (null2Int(request.getServerPort()) != 80) {
			url = url + ":" + null2Int(request.getServerPort()) + contextPath;
		} else {
			url = url + contextPath;
		}
		return url;
	}

	public static String getHttpURL(HttpServletRequest request) {
		String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
		String url = "http://" + request.getServerName();
		if (null2Int(request.getServerPort()) != 80) {
			url = url + ":" + null2Int(request.getServerPort()) + contextPath;
		} else {
			url = url + contextPath;
		}
		return url;
	}

	/**
	 * ?????????????????? ??????Jsoup?????????????????????????????????
	 */
	private final static Whitelist user_content_filter = Whitelist.relaxed();
	static {
		user_content_filter.addTags("embed", "object", "param", "span", "div", "font");
		user_content_filter.addAttributes("div", "width", "height", "style");
		user_content_filter.addAttributes("span", "width", "height", "style");
		user_content_filter.addAttributes("ul", "width", "height", "style");
		user_content_filter.addAttributes("li", "width", "height", "style");
		user_content_filter.addAttributes("table", "width", "height", "style");
		user_content_filter.addAttributes("tr", "width", "height", "style");
		user_content_filter.addAttributes("td", "width", "height", "style");
		user_content_filter.addAttributes(":all", "style", "class", "id", "name");
		user_content_filter.addAttributes("object", "classid", "codebase");
		user_content_filter.addAttributes("param", "name", "value");
		user_content_filter.addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen",
				"allowScriptAccess", "flashvars", "name", "type", "pluginspage");
	}

	public static String filterHTML(String content) {
		String s = Jsoup.clean(content, user_content_filter);
		return s;
	}

	public static int parseDate(String type, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (type.equals("y")) {
			return cal.get(Calendar.YEAR);
		}
		if (type.equals("M")) {
			return cal.get(Calendar.MONTH) + 1;
		}
		if (type.equals("d")) {
			return cal.get(Calendar.DAY_OF_MONTH);
		}
		if (type.equals("H")) {
			return cal.get(Calendar.HOUR_OF_DAY);
		}
		if (type.equals("m")) {
			return cal.get(Calendar.MINUTE);
		}
		if (type.equals("s")) {
			return cal.get(Calendar.SECOND);
		}
		return 0;
	}

	// ????????????url??????,????????????
	public static int[] readImgWH(String imgurl) {
		boolean b = false;
		try {
			// ?????????url
			URL url = new URL(imgurl);
			// ????????????????????????
			java.io.BufferedInputStream bis = new BufferedInputStream(url.openStream());
			// ???????????????????????????
			byte[] bytes = new byte[100];
			// ????????????????????????????????????
			OutputStream bos = new FileOutputStream(new File("C:\\thetempimg.gif"));
			int len;
			while ((len = bis.read(bytes)) > 0) {
				bos.write(bytes, 0, len);
			}
			bis.close();
			bos.flush();
			bos.close();
			// ???????????????
			b = true;
		} catch (Exception e) {
			// ?????????????????????
			b = false;
		}
		int[] a = new int[2];
		if (b) {// ????????????
			// ????????????
			java.io.File file = new java.io.File("C:\\thetempimg.gif");
			BufferedImage bi = null;
			boolean imgwrong = false;
			try {
				// ????????????
				bi = javax.imageio.ImageIO.read(file);
				try {
					// ???????????????????????????????????????,???????????????????????????
					int i = bi.getType();
					imgwrong = true;
				} catch (Exception e) {
					imgwrong = false;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (imgwrong) {
				a[0] = bi.getWidth(); // ?????? ??????
				a[1] = bi.getHeight(); // ?????? ??????
			} else {
				a = null;
			}
			// ????????????
			file.delete();
		} else {// ???????????????
			a = null;
		}
		return a;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param request
	 * @param acc
	 * @return
	 */
	public static boolean del_acc(HttpServletRequest request, Accessory acc) {
		boolean ret = true;
		boolean ret1 = true;
		if (acc != null) {
			String path = request.getSession().getServletContext().getRealPath("/") + acc.getPath() + File.separator
					+ acc.getName();
			String small_path = request.getSession().getServletContext().getRealPath("/") + acc.getPath()
					+ File.separator + acc.getName() + "_small." + acc.getExt();
			ret = deleteFile(path);
			ret1 = deleteFile(small_path);
		}
		return ret && ret1;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param path
	 * @return
	 */
	public static boolean fileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param s
	 * @param c
	 * @return
	 */
	public static int splitLength(String s, String c) {
		int v = 0;
		if (!s.trim().equals("")) {
			v = s.split(c).length;
		}
		return v;
	}

	/**
	 * ??????file?????????????????????????????????????????????????????????
	 * 
	 * @param file
	 * @return
	 */
	static int totalFolder = 0;
	static int totalFile = 0;

	public static double fileSize(File folder) {
		if (folder.exists()) {
			totalFolder++;
			// System.out.println("Folder: " + folder.getName());
			long foldersize = 0;
			File[] filelist = folder.listFiles();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].isDirectory()) {
					foldersize += fileSize(filelist[i]);
				} else {
					totalFile++;
					foldersize += filelist[i].length();
				}
			}
			return div(foldersize, 1024);
		} else
			return 0;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param file
	 * @return
	 */
	public static int fileCount(File file) {
		if (file == null) {
			return 0;
		}
		if (!file.isDirectory()) {
			return 1;
		}
		int fileCount = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				fileCount++;
			} else if (f.isDirectory()) {
				fileCount++;
				fileCount += fileCount(file); // ???????????????????????????????????????????????????
			}
		}
		return fileCount;
	}

	/**
	 * ???????????????????????????URL
	 * 
	 * @param request
	 */
	public static String get_all_url(HttpServletRequest request) {
		String query_url = request.getRequestURI();
		if (request.getQueryString() != null && !request.getQueryString().equals("")) {
			query_url = query_url + "?" + request.getQueryString();
		}
		return query_url;
	}

	/**
	 * ??????html??????????????????java Color
	 * 
	 * @param color
	 * @return
	 */
	public static Color getColor(String color) {
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			return null;
		}
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	/**
	 * ????????????a????????????????????????length????????????????????????
	 * 
	 * @param a
	 * @param length
	 * @return
	 */
	public static Set<Integer> randomInt(int a, int length) {
		Set<Integer> list = new TreeSet<Integer>();
		int size = length;
		if (length > a) {
			size = a;
		}
		while (list.size() < size) {
			Random random = new Random();
			int b = random.nextInt(a);
			list.add(b);
		}
		return list;
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param obj
	 * @param len
	 * @return
	 */
	public static Double formatDouble(Object obj, int len) {
		Double ret = 0.0;
		String format = "0.0";
		for (int i = 1; i < len; i++) {
			format = format + "0";
		}
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(obj));
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param strName
	 * @return
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {

				if (!isChinese(c)) {
					count = count + 1;
					System.out.print(c);
				}
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ??????IP??????????????????????????????
	 * 
	 * @param IP
	 * @return
	 */
	public static String trimSpaces(String IP) {//
		while (IP.startsWith(" ")) {
			IP = IP.substring(1, IP.length()).trim();
		}
		while (IP.endsWith(" ")) {
			IP = IP.substring(0, IP.length() - 1).trim();
		}
		return IP;
	}

	/**
	 * ?????????????????????IP
	 * 
	 * @param IP
	 * @return
	 */
	public static boolean isIp(String IP) {//
		boolean b = false;
		IP = trimSpaces(IP);
		if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String s[] = IP.split("\\.");
			if (Integer.parseInt(s[0]) < 255)
				if (Integer.parseInt(s[1]) < 255)
					if (Integer.parseInt(s[2]) < 255)
						if (Integer.parseInt(s[3]) < 255)
							b = true;
		}
		return b;
	}

	/**
	 * ???????????????????????????www???????????????
	 * 
	 * @param request
	 *            ????????????
	 * @return ??????????????????
	 */
	public static String generic_domain(HttpServletRequest request) {
		String system_domain = "localhost";
		String serverName = request.getServerName();
		if (isIp(serverName)) {
			system_domain = serverName;
		} else {
			if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
				system_domain = serverName;
			} else {
				system_domain = serverName.substring(serverName.indexOf(".") + 1);
			}
		}
		System.out.println(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getRequestURI());
		System.out.println(request.getScheme() + "://" + request.getServerName());
		return system_domain;
	}

	/**
	 * ?????????????????????????????????
	 */
	public boolean JudgeIsMoblie(HttpServletRequest request) {
		boolean isMoblie = false;
		String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
				"opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
				"nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
				"docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
				"techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
				"wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech",
				"gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320",
				"176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz",
				"brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi",
				"keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-",
				"moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port", "prox",
				"qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal",
				"smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v", "voda",
				"wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-", "Googlebot-Mobile" };
		if (request.getHeader("User-Agent") != null) {
			for (String mobileAgent : mobileAgents) {
				if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
					isMoblie = true;
					break;
				}
			}
		}
		return isMoblie;
	}

	/**
	 * 
	 * @param str
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String generic_star(String str, int begin, int end) {
		if (str.length() > begin && str.length() >= end) {
			return str.replaceAll(str.substring(begin, end), "********");
		} else {
			return str;
		}
	}

	/**
	 * ???json?????????List<Map>
	 * 
	 * @param json
	 * @return
	 */
	public static List<Map> Json2List(String json) {
		List<Map> list = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			list = Json.fromJson(List.class, json);
		}
		return list;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????
	 * 
	 * @param str
	 * @return ??????1???????????????????????????2??????????????????+?????????????????????3??????????????????+????????????+?????????????????????4??????????????????+????????????+????????????+
	 *         ????????????
	 */
	public static int checkInput(String str) {
		int num = 0;
		num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[a-z]").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[A-Z]").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[-.!@#$%^&*()+?><]").matcher(str).find() ? num + 1 : num;
		return num;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			// System.out.println("?????????????????????????????????????????????????????????");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// int line = 1;
			// ?????????????????????????????????null???????????????
			while ((tempString = reader.readLine()) != null) {
				// ????????????
				// System.out.println("line " + line + ": " + tempString);
				// line++;
				sb.append(tempString);
			}
			reader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void saveIPageList2ModelAndView(String url, String staticURL, String params, int currentPage,
			int pageCount, int pageSize, ModelAndView mv) {

		mv.addObject("totalPage", new Integer(pageCount));
		mv.addObject("pageSize", pageSize);
		mv.addObject("currentPage", new Integer(currentPage));
		mv.addObject("gotoPageHTML", CommUtil.showPageHtml(url, params, currentPage, pageCount));
		mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(currentPage, pageCount));
		mv.addObject("gotoPageStaticHTML", CommUtil.showPageStaticHtml(staticURL, currentPage, pageCount));
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, params, currentPage, pageCount));

	}

	public static String fourHomesfive(String arg) {

		// BigDecimal bd = new BigDecimal(Double.parseDouble(arg));
		// DecimalFormat df = new DecimalFormat("#.00");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(Double.parseDouble(arg));
	}

	public static Area getAreaById(String id) {
		Area _area = new Area();
		IAreaService areaService = (IAreaService) SpringUtil.getObject(IAreaService.class);
		_area = areaService.getObjById(null2Long(id));
		return _area;

	}

	public static Area getAreaByName(String name) {
		IAreaService areaService = (IAreaService) SpringUtil.getObject(IAreaService.class);

		List<Area> areas = areaService.query("select obj from Area obj where obj.areaName = '" + name
				+ "' and obj.parent.id is null order by obj.sequence asc", null, -1, -1);
		if (areas.size() > 0)
			return areas.get(0);
		return null;
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// ??????"-"??????
		String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
		return temp;
	}

	// ?????????????????????UUID
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}
		return ss;
	}

	public static Long null2LongNew(Object s) {
		Long v = 0l;
		if (s != null)
			try {
				v = Long.parseLong(s.toString());
			} catch (Exception e) {
			}
		return v;
	}

	/**
	 * ???lucene???????????????????????????
	 * 
	 * @param pList
	 * @param mv
	 */
	public static void saveLucene2ModelAndView(LuceneResult pList, ModelAndView mv, GoodsViewTools vt,
			IGoodsService is) {
		if (pList != null) {

			List<LuceneVo> newList = new ArrayList<LuceneVo>();
			List<LuceneVo> list = pList.getVo_list();
			for (LuceneVo vo : list) {
				String ggStatus = vt.query_activity_status(String.valueOf(vo.getVo_id()), "group_buy");
				if ("true".equals(ggStatus)) {

					Goods goods = is.getObjById(vo.getVo_id());
					List<GroupGoods> ggList = goods.getGroup_goods_list();
					GroupGoods gg = null;
					if (null != ggList && 0 < ggList.size()) {

						for (GroupGoods ggs : ggList) {

							if (ggs.getGg_status() == 1) {

								gg = ggs;

							}
						}
					}
					if (null != gg) {

						vo.setVo_store_price(gg.getGg_price().doubleValue());
					}
				}

				newList.add(vo);
			}

			mv.addObject("objs", newList);
			mv.addObject("totalPage", pList.getPages());
			mv.addObject("pageSize", pList.getPageSize());
			mv.addObject("rows", pList.getRows());
			mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
		}
	}

	public static void main(String[] args) throws Exception {

		System.out.println(getUUID(2)[1]);
	}

	/**
	 * ????????????Api??????????????????
	 * 
	 * @param filePath
	 * @param saveFilePathName
	 * @param saveFileName
	 * @param extendes 
	 *            , *
	 * @return ????????????map???map??????4???????????????????????????????????????fileName,?????????????????????????????????fileSize,,
	 *         ???????????????????????????????????????errors,????????????????????????map?????????smallFileName??????????????????????????????
	 */
	public static Map httpsaveFileToServer(HttpServletRequest request, String filePath, String saveFilePathName,
			String saveFileName, String[] extendes) throws IOException {
		String imgName = null;
		String goodsExtend = null;
		String code = null;
		Map map = new HashMap();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile commonsMmultipartFile = (CommonsMultipartFile) multipartRequest.getFile(filePath);
		if (commonsMmultipartFile != null && !commonsMmultipartFile.isEmpty()) {
			DiskFileItem diskFileItem = (DiskFileItem) commonsMmultipartFile.getFileItem();
			File file = diskFileItem.getStoreLocation();
			//System.out.println("???????????????" + commonsMmultipartFile.getOriginalFilename());
			String extend = commonsMmultipartFile.getOriginalFilename()
					.substring(commonsMmultipartFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
			if (saveFileName == null || saveFileName.trim().equals("")) {
				saveFileName = UUID.randomUUID().toString() + "." + extend;
			}
			if (saveFileName.lastIndexOf(".") < 0) {
				saveFileName = saveFileName + "." + extend;
			}
			float fileSize = Float.valueOf(commonsMmultipartFile.getSize());// ??????????????????????????????k
			List<String> errors = new java.util.ArrayList<String>();
			boolean flag = true;
			if (extendes == null) {
				extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
			}
			for (String s : extendes) {
				if (extend.toLowerCase().equals(s))
					flag = true;
			}
			if (flag) {
				String imageWebService = commUtil.configService.getSysConfig().getImageWebServer();
				String path = imageWebService + "/api/upload";
				CloseableHttpClient httpClient = null;
				CloseableHttpResponse response = null;
				httpClient = HttpClients.createDefault();// ??????
															// CloseableHttpClient
				HttpPost httpPost = new HttpPost(path);
				try {
					String httpfilePath = "/" + saveFilePathName;
					MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();// ?????????Http??????????????????
																							// ????????????HttpEntity??????????????????????????????????????????.
					entityBuilder.addPart("file", new FileBody(file));
					entityBuilder.addTextBody("path", httpfilePath);// ??????????????????
					HttpEntity httpEntity = entityBuilder.build();
					httpPost.setEntity(httpEntity);
					response = httpClient.execute(httpPost);
					// ??????????????????
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						String result = EntityUtils.toString(response.getEntity());
						JSONObject jsonObject = JSONObject.parseObject(result);
						code = jsonObject.get("code").toString();
						if (code != null && "0".equals(code)) {
							String msg = jsonObject.get("msg").toString();
							JSONObject object = (JSONObject) jsonObject.get("data");
							String goodsName = object.get("img").toString();
							imgName = goodsName.substring(goodsName.lastIndexOf("/") + 1);
							goodsExtend = imgName.substring(imgName.lastIndexOf(".") + 1).toLowerCase();
							if (isImg(extend)) {
									/*
									 * File img = new File(saveFilePathName + File.separator
									 * + saveFileName);
									 */
									try {
										BufferedImage bis = ImageIO.read(file);
										int w = bis.getWidth();
										int h = bis.getHeight();
										map.put("width", w);
										map.put("height", h);
									} catch (Exception e) {
									}
								}
								map.put("mime", goodsExtend);
								map.put("fileName", imgName);
								map.put("fileSize", fileSize);
								map.put("error", errors);
								map.put("oldName", commonsMmultipartFile.getOriginalFilename());
							
						}
					} else {
						map.put("error", 2);
						map.put("width", 0);
						map.put("height", 0);
						map.put("mime", "");
						map.put("fileName", "");
						map.put("fileSize", 0.0f);
						map.put("oldName", "");
					}
					// ??????
					EntityUtils.consume(resEntity);
				} catch (Exception e) {
					e.printStackTrace();
					map.put("width", 0);
					map.put("height", 0);
					map.put("mime", "");
					map.put("fileName", "");
					map.put("fileSize", 0.0f);
					map.put("oldName", "");
				} finally {
					try {
						if (response != null) {
							response.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (httpClient != null) {
							httpClient.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			} else {
				// System.out.println("?????????????????????");
				errors.add("?????????????????????");
			}
		} else {
			map.put("width", 0);
			map.put("height", 0);
			map.put("mime", "");
			map.put("fileName", "");
			map.put("fileSize", 0.0f);
			map.put("oldName", "");
		}
		return map;
	}

	/**
	 * @description ???????????????????????????
	 * @param request
	 * @param response
	 * @param image_id
	 * @param user_id
	 * @return
	 */
	public static int httpDelFileToServer(HttpServletRequest request, HttpServletResponse response, String params) {
		Map map = new HashMap();
		Boolean ret = false;
		int code = -1;
		if (params != null && !params.equals("")) {
			String url = commUtil.configService.getSysConfig().getImageWebServer() + "/api/delete";
			String res = Http.doPost(url, params);
			if (res != null || !res.equals("")) {
				JsonParser parse = new JsonParser(); // ??????json?????????
				JsonObject json = (JsonObject) parse.parse(res); // ??????jsonObject??????
				code = json.get("code").getAsInt();
				return code;
			}
		}
		return code;
	}

	/**
	 * 
	 * @param request
	 * @param filePath
	 * @param saveFilePathName
	 * @param saveFileName
	 * @param extendes
	 * @return
	 * @throws IOException
	 */
	public static Map saveFileToServer(HttpServletRequest request, String filePath, String saveFilePathName,
			String saveFileName, String[] extendes) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePath);
		Map map = new HashMap();
		if (file != null && !file.isEmpty()) {
			String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.toLowerCase();
			if (saveFileName == null || saveFileName.trim().equals("")) {
				saveFileName = UUID.randomUUID().toString() + "." + extend;
			}
			if (saveFileName.lastIndexOf(".") < 0) {
				saveFileName = saveFileName + "." + extend;
			}
			float fileSize = Float.valueOf(file.getSize());// ??????????????????????????????k
			List<String> errors = new java.util.ArrayList<String>();
			boolean flag = true;
			if (extendes == null) {
				extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
			}
			for (String s : extendes) {
				if (extend.toLowerCase().equals(s))
					flag = true;
			}
			if (flag) {
				File path = new File(saveFilePathName);
				if (!path.exists()) {
					path.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				DataOutputStream out = new DataOutputStream(
						new FileOutputStream(saveFilePathName + File.separator + saveFileName));
				InputStream is = null;
				try {
					is = file.getInputStream();
					int size = (int) (fileSize);
					byte[] buffer = new byte[size];
					while (is.read(buffer) > 0) {
						out.write(buffer);
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				} finally {
					if (is != null) {
						is.close();
					}
					if (out != null) {
						out.close();
					}
				}
				if (isImg(extend)) {
					File img = new File(saveFilePathName + File.separator + saveFileName);
					try {
						BufferedImage bis = ImageIO.read(img);
						int w = bis.getWidth();
						int h = bis.getHeight();
						map.put("width", w);
						map.put("height", h);
					} catch (Exception e) {
						// map.put("width", 200);
						// map.put("heigh", 100);
					}
				}
				map.put("mime", extend);
				map.put("fileName", saveFileName);
				map.put("fileSize", fileSize);
				map.put("error", errors);
				map.put("oldName", file.getOriginalFilename());
				// System.out.println("????????????????????????????????????:" + fileName);
			} else {
				// System.out.println("?????????????????????");
				errors.add("?????????????????????");
			}
		} else {
			map.put("width", 0);
			map.put("height", 0);
			map.put("mime", "");
			map.put("fileName", "");
			map.put("fileSize", 0.0f);
			map.put("oldName", "");
		}
		return map;
	}

	/**
	 * ???????????????
	 * 
	 * @param json
	 * @param response
	 */
	public static void returnJson(String json, HttpServletResponse response) {
		response.setContentType("application/json");
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

	/**
	 * @description ????????????????????????
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	private static Date randomDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date start = format.parse(beginDate);
			Date end = format.parse(endDate);

			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());
			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}
	
	public static User verifyToken(String token){
		if(!CommUtil.null2String(token).equals("")){
			User user = commUtil.userService.getObjByProperty(null, "app_login_token", token);
			if(user != null){
				return user;
			}
		}
		return null;
		
	}
	
	/**
	 * ??????????????????Cookie
	 * @param request
	 * @return
	 */
/*	public static String language(HttpServletRequest request){
		String language = "en";
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals("lang")){
					language = cookie.getValue();
				}
			}
		}
		return language;
	}*/

	public static String language(HttpServletRequest request){
		String language = "en";
		String acceptLanguage = request.getHeader("lang");
		if(acceptLanguage != null){
			int i = acceptLanguage.indexOf("sa");
			if(acceptLanguage != null && acceptLanguage.indexOf("sa") >= 0){
				language = "sa";
			}
		}
		return language;
	}
}
