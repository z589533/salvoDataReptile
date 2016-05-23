package org.salvo.test;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.salvo.http.HttpClientUtils;
import org.salvo.util.CharsetUtil;

import tk.mybatis.mapper.util.StringUtil;

public class HttpTest {

	// @Test
	// public void HttpGet() {
	//// for (int i = 0; i < 1; i++) {
	//// HttpClientUtils.sendRequsetGET("http://blog.csdn.net/rj042/article/details/6991441");
	//// }
	//
	// }

	@Test
	public void HttpCallableGet() throws UnsupportedEncodingException {
		for (int i = 0; i < 1; i++) {
			String info = HttpClientUtils.sendRequsetCallableGET("http://38.103.161.155/bbs/thread-9650705-1-1.html");

			
//			Document doc = Jsoup.parse(info,"http://www.027peixun.com/");
//			Element eMETA = doc.select("meta").first();
//			String content = eMETA.toString();
//			content = CharsetUtil.getKMPCharset(content);
//			System.out.println("编码="+content);
//			info = new String(info.getBytes("GBK"));
			
			
			System.out.println(info);
		}
	}
}
