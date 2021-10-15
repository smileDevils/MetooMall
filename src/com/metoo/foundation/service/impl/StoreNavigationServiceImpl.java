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
import com.metoo.foundation.domain.StoreNavigation;
import com.metoo.foundation.service.IStoreNavigationService;

@Service
@Transactional
public class StoreNavigationServiceImpl implements IStoreNavigationService{
	@Resource(name = "storeNavigationDAO")
	private IGenericDAO<StoreNavigation> storeNavigationDao;
	
	public boolean save(StoreNavigation storeNavigation) {
		/**
		 * init other field here
		 */
		try {
			this.storeNavigationDao.save(storeNavigation);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public StoreNavigation getObjById(Long id) {
		StoreNavigation storeNavigation = this.storeNavigationDao.get(id);
		if (storeNavigation != null) {
			return storeNavigation;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeNavigationDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeNavigationIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeNavigationIds) {
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
		GenericPageList pList = new GenericPageList(StoreNavigation.class,construct, query,
				params, this.storeNavigationDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(StoreNavigation storeNavigation) {
		try {
			this.storeNavigationDao.update( storeNavigation);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<StoreNavigation> query(String query, Map params, int begin, int max){
		return this.storeNavigationDao.query(query, params, begin, max);
		
	}
}
