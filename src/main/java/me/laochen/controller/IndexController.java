package me.laochen.controller;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

import me.laochen.annotation.RequestMapper;
import me.laochen.annotation.RequestParam;
import me.laochen.controller.core.AbstractController;
import me.laochen.vo.http.TplResponseVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller("/index")
public class IndexController extends AbstractController {
	private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Override
	public TplResponseVO execute(FullHttpRequest request) {
		TplResponseVO responseVO = new TplResponseVO();
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("name", "世界");
		responseVO.setTplFileName("index.html");
		responseVO.setData(model);
		return responseVO;
	}
	
	@RequestMapper("/profile")
	public String userProfile(@RequestParam("_uid") Long uid, Map<String,Object> params,FullHttpRequest request)
	{	
		logger.debug("_uid="+uid);
		logger.debug("request param length is "+params.size());
		
		return "uid="+uid;
	}
}
