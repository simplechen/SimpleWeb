package me.laochen.controller;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import me.laochen.annotation.RequestMapper;
import me.laochen.controller.core.AbstractController;
import me.laochen.dao.AdminInfoDAO;
import me.laochen.po.AdminInfo;
import me.laochen.vo.http.TplResponseVO;

import org.springframework.stereotype.Controller;

@Controller("/admin")
public class AdminController extends AbstractController {
	@Resource
	private AdminInfoDAO adminInfoDAO;
	
	@RequestMapper("/list")
	public TplResponseVO list(Map<String,String> params, FullHttpRequest request)
	{
		TplResponseVO tplResponseVO = new TplResponseVO();
		Map<String,Object> model = new HashMap<String, Object>();
		List<AdminInfo> data = new ArrayList<AdminInfo>();
		data = adminInfoDAO.findAll();
		
		for (int i = 0; i < data.size(); i++) {
			System.err.println(data.get(i));
		}
		
		model.put("listData", data);		
		tplResponseVO.setData(model);
		tplResponseVO.setTplFileName("admin_list.html");
		return tplResponseVO;
	}
}
