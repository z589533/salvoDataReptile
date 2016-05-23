package org.salvo.tool;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.salvo.base.SalvoObjectConfig;
import org.salvo.http.HttpClientUtils;
import org.salvo.queue.LinkFilter;
import org.salvo.util.KMPUtil;

public class HtmlParserTool {
	// 获取一个网站上的链接，filter 用来过滤链接
	public static Set<String> extracLinks(String url, LinkFilter filter) {
		Set<String> links = new HashSet<String>();
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");
			// 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性
			NodeFilter frameFilter = new NodeFilter() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean accept(Node node) {
					if (node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};
			// OrFilter 来设置过滤 <a> 标签和 <frame> 标签
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
			// 得到所有经过过滤的标签
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag)// <a> 标签
				{
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// URL
					if (filter.accept(linkUrl))
						links.add(linkUrl);
				} else// <frame> 标签
				{
					// 提取 frame 里 src 属性的链接， 如 <frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf(">");
					String frameUrl = frame.substring(5, end - 1);
					if (filter.accept(frameUrl))
						links.add(frameUrl);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return links;
	}

	// 获取一个网站上的链接，filter 用来过滤链接
	public static Set<String> extracLinks(String info, String url) {
		Set<String> links = new HashSet<String>();
		Document doc = Jsoup.parse(info, HttpClientUtils.captureHost(url));
		// System.out.println(doc.title());
		Element body = doc.body();
		Elements es = body.select("a");
		for (Iterator it = es.iterator(); it.hasNext();) {
			Element e = (Element) it.next();
			String href = e.absUrl("href");
			// System.out.println("待请求路径=" + e.attr("href"));
			// System.out.println("完整的=" + href + " 不完整的=" + e.attr("href"));
			if (CheckUrl(href)) {
				links.add(href);
			}
		}
		return links;
	}

	// 获取一个网站上的链接，filter 用来过滤链接
	public static Set<String> extracLinks(String url) throws IOException {
		Set<String> links = new HashSet<String>();
		Document doc = Jsoup.connect(url).timeout(60000).get();
		// System.out.println(doc.title());
		// Elements es = body.select("a");
		Elements es = doc.select("a[href]");
		for (Element link : es) {
			String href = link.attr("abs:href");
			if (CheckUrl(href)) {
				links.add(href);
			}
		}
		return links;
	}

	public static boolean CheckUrl(String url) {
		boolean tell = true;
		for (int i = 0; i < SalvoObjectConfig.EXCLUDEURL.length; i++) {
			if (KMPUtil.getIndexOfStr(url, SalvoObjectConfig.EXCLUDEURL[i]) > -1) {
				tell = false;
				break;
			}
		}
		return tell;
	}
}
