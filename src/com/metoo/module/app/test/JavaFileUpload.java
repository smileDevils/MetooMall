package com.metoo.module.app.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.service.ISysConfigService;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Controller
@RequestMapping("/webServer/upload/")
public class JavaFileUpload {
	
	@Autowired
	private ISysConfigService configService;

	@RequestMapping("/photo.json")
	@ResponseBody
	public String upload(HttpServletRequest request, HttpServletResponse response, String path) {
		
		
	/*	String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext().getRealPath("/") + uploadFilePath
				+ File.separator + "test123";*/
		
		String imageWebService = configService.getSysConfig().getImageWebServer();
		String saveFilePathName = imageWebService + "/upload/testTree";
		
		Map<String, Object> adMap = new HashMap<String, Object>();
		String fileName = CommUtil.randomInt(6)+ ".png";
		
		try {
			CommUtil.saveFileToServer(request, "img", path, fileName, null);
			return "Success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error";
	}

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
				// ???????????????
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
	
	
	public static Map saveFileToServer2(HttpServletRequest request, String filePath, String saveFilePathName,
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
				float fileSize = Float.valueOf(file.getSize());// ??????????????????????????????k
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				// ???????????????
				DataOutputStream out = new DataOutputStream(
						new FileOutputStream(saveFilePathName + File.separator + saveFileName));
				InputStream is = null;
				try {
					is = file.getInputStream();
					byte[] buffer = new byte[is.available()];
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

	
	
	
	@RequestMapping("/multipartFile.json")
	@ResponseBody
    public static boolean imageUpload(String path, MultipartFile multipartFile) {
		String spath = System.getProperty("user.dir");
        if (multipartFile == null && multipartFile.getSize() <= 0) {
            return false;
        }
  
		
        //?????????
        String originalName = multipartFile.getOriginalFilename();// ???????????????
        
        String fileName = UUID.randomUUID().toString().replace("-", "");
        
        String extend = originalName.substring(originalName.lastIndexOf("."));
        
        String picNewName = fileName + extend;
        
        String imgRealPath = path + File.separator + picNewName;
        // ????????????
   /*     if (extendes == null) {
			extendes = new String[] { "jpg", "jpeg", "gif", "bmp", "tbi", "png" };
		}
		for (String s : extendes) {
			if (extend.toLowerCase().equals(s))
				flag = true;
		}*/
		
    	float fileSize = Float.valueOf(multipartFile.getSize());// ??????????????????????????????k
    	
        try {
            //????????????-???multipartFile????????????image?????????
            File imageFile = new File(imgRealPath);
            if(!imageFile.exists()){
            	 imageFile.mkdirs();
            }
            multipartFile.transferTo(imageFile);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
	
	/**
	 * ??????Base64????????????????????? (1: ??????????????????; )
	 */
	@RequestMapping("base64.json")
	@ResponseBody
	 public String GenerateImage(String imgStr) throws Exception {
	    if (imgStr == null) // ??????????????????
	      return "";
	    BASE64Decoder decoder = new BASE64Decoder();

	    // Base64??????,??????????????????????????????Base64?????????????????????
	    imgStr = imgStr.replaceAll(" ", "+");

	    byte[] buffer = decoder.decodeBuffer(imgStr.replace("data:image/jpeg;base64,", ""));
	    
	    for (int i = 0; i < buffer.length; ++i) {
	      if (buffer[i] < 0) {// ??????????????????
	    	  buffer[i] += 256;
	      }
	    }
	    String imgName = getRandomFileName()+".jpg";
	    
	    String dbUrl = "";
	    // ??????jpeg??????D:\test\attendance\src\main\webapp\assets\images\leave
	    String imgFilePath = "C:\\Users\\46075\\Pictures\\temp\\temploadBase\\"+imgName;//??????????????????
	    
	    OutputStream out = new FileOutputStream(imgFilePath);
	    // ?????????????????????????????????????????????????????????????????????????????????????????????byte????????????????????????
	    BufferedOutputStream bos = new BufferedOutputStream(out);
	    out.write(buffer);
	    out.flush();
	    out.close();
	    
	    return "sd";
	  }
	
	  public static String getRandomFileName() {

	    SimpleDateFormat simpleDateFormat;

	    simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

	    Date date = new Date();

	    String str = simpleDateFormat.format(date);

	    Random random = new Random();

	    int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// ??????5????????????

	    return rannum + str;// ????????????
	  }
	
	  /**
	     * ???????????????????????????????????????????????????????????????Base64????????????
	     *
	     * @param imgPath
	     */
	    public static String GetImageStr(String imgPath) {
	        String imgFile = imgPath;// ??????????????????
	        InputStream in = null;
	        byte[] data = null;
	        String encode = null; // ??????Base64?????????????????????????????????
	        // ???????????????Base64??????
	        BASE64Encoder encoder = new BASE64Encoder();
	        try {
	            // ????????????????????????
	            in = new FileInputStream(imgFile);
	            data = new byte[in.available()];
	            in.read(data);
	            encode = encoder.encode(data);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                in.close();
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	        return encode;
	    }
	 
	    /**
	     * ???????????????????????????Base64?????????????????????
	     *
	     * @param imgData     ????????????
	     * @param imgFilePath ?????????????????????
	     */
	    public static boolean GenerateImage(String imgData, String imgFilePath) throws IOException {
	        if (imgData == null) // ??????????????????
	            return false;
	        BASE64Decoder decoder = new BASE64Decoder();
	        OutputStream out = null;
	        try {
	            out = new FileOutputStream(imgFilePath);
	            // Base64??????
	            byte[] b = decoder.decodeBuffer(imgData);
	            for (int i = 0; i < b.length; ++i) {
	                if (b[i] < 0) {// ??????????????????
	                    b[i] += 256;
	                }
	            }
	            out.write(b);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            out.flush();
	            out.close();
	            return true;
	        }
	    }
	 
	    /**
	     * ??? MultipartFile file ??????????????????????????????base64???????????????
	     * @param file
	     * @return
	     * @throws Exception
	     */
	    public static String uploadiong(MultipartFile file) throws Exception{
	        boolean image = isImage(file.getInputStream());
	        if (image) {
	            BASE64Encoder encoder = new BASE64Encoder();
	            String imageString = "data:image/jpg;base64," + encoder.encode(file.getBytes());
	            return imageString;
	        }else {
	            return null;
	        }
	    }
	 
	    public static boolean isImage(InputStream inputStream) {
	        if (inputStream == null) {
	            return false;
	        }
	        Image img;
	        try {
	            img = ImageIO.read(inputStream);
	            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
	        } catch (Exception e) {
	            return false;
	        }
	    }
	
}
