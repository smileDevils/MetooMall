package com.metoo.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.service.ICouponInfoService;

@Service
@Transactional
public class CouponInfoServiceImpl implements ICouponInfoService {
	@Resource(name = "couponInfoDAO")
	private IGenericDAO<CouponInfo> couponInfoDao;

	public boolean save(CouponInfo couponInfo) {
		/**
		 * init other field here
		 */
		try {
			this.couponInfoDao.save(couponInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public CouponInfo getObjById(Long id) {
		CouponInfo couponInfo = this.couponInfoDao.get(id);
		if (couponInfo != null) {
			return couponInfo;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.couponInfoDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> couponInfoIds) {
		// TODO Auto-generated method stub
		for (Serializable id : couponInfoIds) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(CouponInfo.class,
				construct, query, params, this.couponInfoDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	public boolean update(CouponInfo couponInfo) {
		try {
			this.couponInfoDao.update(couponInfo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<CouponInfo> query(String query, Map params, int begin, int max) {
		return this.couponInfoDao.query(query, params, begin, max);

	}
}
