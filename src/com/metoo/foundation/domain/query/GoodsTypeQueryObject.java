package com.metoo.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.query.QueryObject;

public class GoodsTypeQueryObject extends QueryObject {
	public GoodsTypeQueryObject(String construct, String currentPage,
			ModelAndView mv, String orderBy, String orderType) {
		super(construct, currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	public GoodsTypeQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GoodsTypeQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	
}
