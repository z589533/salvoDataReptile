package org.salvo.controller.main;

import java.util.Date;

import org.salvo.cache.RedisExtend;
import org.salvo.http.HttpClientUtils;
import org.salvo.model.Test1;
import org.salvo.service.Test1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/salvo")
public class SalvoController {

	@Autowired
	private Test1Service test1Service;

	@Autowired
	private RedisExtend redisExtend;

	@RequestMapping(value = "urltest")
	public String url(@RequestParam("url") String url) {
		String info = HttpClientUtils.sendRequsetCallableGET(url);
		System.out.println(info.trim());
		info=info.substring(0, 9000);
		Test1 entity = new Test1();
		entity.setId(new Date().getSeconds());
		entity.setContent(info);
		test1Service.save(entity);

		String ds = redisExtend.set("url", 500, info);
		System.out.println(ds);
		return "200";
	}

}
