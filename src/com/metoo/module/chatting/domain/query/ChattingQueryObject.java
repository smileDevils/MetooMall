package com.metoo.module.chatting.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.query.QueryObject;

public class ChattingQueryObject extends QueryObject {
	public ChattingQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public ChattingQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
