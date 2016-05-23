package org.salvo.test;

import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.salvo.http.HttpClientUtils;

public class Test2 {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String url = "http://38.103.161.155/forum/forum-143-2068.html";
		print("Fetching %s...", url);
		
		String info = HttpClientUtils.sendRequsetCallableGET(url);
		//Document doc = Jsoup.parse(info, HttpClientUtils.captureHost(visitUrl));

		Document doc = Jsoup.connect(url).get();
		Elements links = doc.select("a[href]");
		Elements media = doc.select("[src]");
		Elements imports = doc.select("link[href]");

		//图片 js css等拦截器
//		print("\nMedia: (%d)", media.size());
		
//		for (Element src : media) {
//			if (src.tagName().equals("img"))
//				print(" * %s: <%s> %sx%s (%s)", src.tagName(), src.attr("abs:src"), src.attr("width"),
//						src.attr("height"), trim(src.attr("alt"), 20));
//			else
//				print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//		}

		
//		print("\nImports: (%d)", imports.size());
//		for (Element link : imports) {
//			print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
//		}

		print("\nLinks: (%d)", links.size());
		for (Element link : links) {
System.out.println( link.attr("abs:href"));
		}
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}
}
