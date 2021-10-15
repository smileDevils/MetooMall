package com.metoo.manage.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: WeixinManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统定时任务控制器，每半小时执行一次  获取微信access_token
 * B2B2C2015版开始，系统定时器方法移到configService中，执行方法分别为runTimerByDay，runTimerByHalfhour
 * 移到configService中能够有效保持所有数据一致性。（hezeng）
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
 * @author erikzhang，hezeng
 * 
 * @date 2014-5-13
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Component(value = "weixin_job")
public class WeixinManageAction {
	@Autowired
	private ISysConfigService configService;

	public void execute() throws Exception {
		// TODO Auto-generated method stub
		this.configService.runTimerWeixinByHalfHour();
	}

}
