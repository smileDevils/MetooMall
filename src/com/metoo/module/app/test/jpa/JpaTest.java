package com.metoo.module.app.test.jpa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Article;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.impl.UserServiceImpl;

@Controller
public class JpaTest {

	/*@Autowired
	private IAddressService addressService;
	@Autowired
	private JpaUtil jpaUtil;
	@Autowired
	private IAddressService adressService;
	
	@RequestMapping("jpa.json")
	public void jap(HttpServletRequest request,
			HttpServletResponse response){
		   ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
			        "applicationContext-configuration.xml");
		   
		String id = "319";
		Address obj = addressService.getObjById(Long.parseLong(id));
		System.out.println(obj.getTrueName());
	}
	
	@RequestMapping("jpa_article.json")
	@Test
	public void entityManage(){
	  *//**
     * 创建实体管理类工厂，借助Persistence的静态方法获取
     * 		其中传递的参数为持久化单元名称，需要jpa配置文件中指定
     *//*
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.show_sql", false);
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("metoo_b2b2c", properties);
	
        //EntityManagerFactory factory = Persistence.createEntityManagerFactory("metoo_b2b2c");
        //创建实体管理类
       // EntityManager em = factory.createEntityManager();
		EntityManager em = jpaUtil.getEntityManage();
		 
        //获取事务对象
        EntityTransaction tx = em.getTransaction();
        //开启事物
      
        tx.begin();
        //find and save
        Article obj = em.find(Article.class, Long.parseLong("14"));
        obj.setMark("12345");
        //Article obj1 = em.merge(obj);
        //-------------------------- 执行任务
        Article article = new Article();
        article.setContent("dadsadasda156151......");
        article.setAddTime(new Date());
        article.setTitle("1");
        em.persist(article);
        //--------------------------  结束任务
        //提交事务
        tx.commit();
        //释放资源
        em.close();
        //factory.close();
	}
	
	@RequestMapping("/hql.json")
	public void jpaHql(HttpServletRequest request, 
			HttpServletResponse response){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", Long.parseLong("184"));
		List<Address> address = this.adressService.query("select obj from Address from where id=:id", params, -1, -1);
		System.out.println(address.size());
	}*/
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext cont = new ClassPathXmlApplicationContext("applicationContext-configuration.xml");
		IUserService userService = cont.getBean(UserServiceImpl.class);
		User user = userService.getObjById(Long.parseLong("123"));
		System.out.println(user.getUserName());
	}
}
