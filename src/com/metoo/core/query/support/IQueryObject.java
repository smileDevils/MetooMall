package com.metoo.core.query.support;

import java.util.Map;

import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.query.PageObject;

/**
 * 
 * <p>
 * Title: IQueryObject.java
 * </p>
 * 
 * <p>
 * Description:查询对象接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 湖南创发科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version koala_b2b2c v2.0 2015版 
 */
public interface IQueryObject {
	/**
	 * 获得查询构造器，查询对象指定字段
	 * 
	 * @return
	 */
	String getConstruct();

	/**
	 * 得到一个查询条件语句,其中参数用命名参数title=:title and id=:id
	 * 
	 * @return 返回的条件语句
	 */
	String getQuery();

	/**
	 * 得到查询语句需要的参数对象列表
	 * 
	 * @return 参数对象列表
	 */
	Map getParameters();

	/**
	 * 得到关于分页页面信息对象
	 * 
	 * @return 包装分页信息对象
	 */
	PageObject getPageObj();

	/**
	 * 批量往查询对象中添加查询选项，可以是一个完整的具体查询语句，如title='111' and
	 * status>0，也可以是包括命名参数的的语句，如title=:title and status>0 <code>
	 * NewsDocQueryObject query=new NewsDocQueryObject();
	 * Map map=new HashMap();
	 * map.put("title","xxxx");
	 * query.addQuery("title=:title",map);
	 * </code>
	 * 
	 * @param scope
	 *            查询条件
	 * @param paras
	 *            参数值，如果
	 */
	IQueryObject addQuery(String scope, Map paras);

	/**
	 * 往查询条件中逐个加入查询条件
	 * 
	 * @param field
	 *            属性名称
	 * @param para
	 *            参数值
	 * @param expression
	 *            表达式,如果为null，则使用=。
	 */
	IQueryObject addQuery(String field, SysMap para, String expression);

	/**
	 * 往查询条件中逐个加入集合查询条件，包括 member of 等
	 * 
	 * @param para
	 *            集合参数
	 * @param obj
	 *            需要传递的参数值
	 * @param field
	 *            查询参数
	 * @param expression
	 *            集合表达式 如 member of
	 * @return
	 */
	IQueryObject addQuery(String para, Object obj, String field,
			String expression);

	/**
	 * 清理所有查询条件、查询参数，用在一个方法中多次使用同一个QueryObject
	 * 
	 * @return
	 */
	IQueryObject clearQuery();

}
