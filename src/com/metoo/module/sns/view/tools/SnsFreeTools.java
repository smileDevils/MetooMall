package com.metoo.module.sns.view.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.FreeApplyLog;
import com.metoo.foundation.domain.FreeClass;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IFreeApplyLogService;
import com.metoo.foundation.service.IFreeClassService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: FreeTools.java
 * </p>
 * 
 * <p>
 * Description:0元试用相关工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.koala.com
 * </p>
 * 
 * @author jinxinzhe
 * 
 * @date 2014-11-11
 * 
 * @version koala_b2b2c v2.0 2015版 
 */
@Component
public class SnsFreeTools {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;

	public Goods queryGoods(String goods_id) {
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		return goods;
	}
	
	public User queryEvaluteUser(String user_id) {
		User user= this.userService.getObjById(CommUtil.null2Long(user_id));
		return user;
	}
}
