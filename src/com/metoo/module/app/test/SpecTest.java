package com.metoo.module.app.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iskyshop.core.tools.CommUtil;
import com.metoo.foundation.domain.GoodsSku;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSkuService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;

@Controller
public class SpecTest {
	
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService sepcPropertyService;
	@Autowired
	private IGoodsSkuService goodsSkuService;
	@Autowired
	private IAccessoryService accesoryService;
	
	/**
	 * 删除老数据多规格
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	//@RequestMapping("/spec_delete.json")
	public void specDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map params = new HashMap();
		params.put("inventory_type", "spec");
		List<Goods> obj = this.goodsService.query("select obj from Goods obj where obj.inventory_type=:inventory_type",
				params, -1, -1);
		for(Goods goods : obj){
			if(goods.getInventory_type().equals("spec")){
				List<GoodsSku> GoodsSkuList = goods.getGoodsSkuList();
				boolean flag = false;
				for(GoodsSku goodsSku : GoodsSkuList){
						if(!goodsSku.getCombination_id().equals("") && goodsSku.getSpec_color() == null){
							
							String combination = goodsSku.getCombination_id();
							String[] ids = combination.split("_");
							System.out.println(goodsSku.getId());
							System.out.println(combination);
						/*	params.clear();
							params.put("ids", CommUtil.null2Long(ids));
							List<GoodsSpecProperty> secObjs =this.sepcPropertyService.query("select obj from GoodsSpecProperty obj where obj.id in (:ids)",
									params, -1, -1);*/
							List<GoodsSpecProperty> specs = new ArrayList<GoodsSpecProperty>();
							for(String id : ids){
								GoodsSpecProperty spec = this.sepcPropertyService.getObjById(CommUtil.null2Long(id));
								if(spec != null){
									specs.add(spec);
								}
							}
							if(specs != null && specs.size()>0 && !specs.isEmpty()){
								for(GoodsSpecProperty specObj : specs){
									if(specObj.getSpec().getName().equals("Color")){
										flag = true;
										this.goodsSkuService.delete(goodsSku.getId());
									}
								}
								
							}
						}
				}
				if(flag){
					goods.getGoodsSkuList().clear();
					goods.getGoods_specs().clear();
					this.goodsService.update(goods);
				}
				
			}
		}
	}
}
