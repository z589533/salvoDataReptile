package org.salvo.test;

import java.io.IOException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.salvo.http.HttpClientUtils;
import org.salvo.queue.LinkFilter;
import org.salvo.queue.SpiderQueue;
import org.salvo.tool.HtmlParserTool;

public class Test1 {
	/**
	 * 使用种子初始化URL队列
	 */
	private void initCrawlerWithSeeds(String[] seeds) {
		for (int i = 0; i < seeds.length; i++)
			SpiderQueue.addUnvisitedUrl(seeds[i]);
	}

	// 定义过滤器，提取以 http://www.xxxx.com开头的链接
	public void crawling(String[] seeds) throws IOException {
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.startsWith("http://38.103.161.155/"))
					return true;
				else
					return false;
			}
		};
		// 初始化 URL 队列
		initCrawlerWithSeeds(seeds);
		// 循环条件：待抓取的链接不空且抓取的网页不多于 1000
		while (!SpiderQueue.unVisitedUrlsEmpty() && SpiderQueue.getVisitedUrlNum() <= 2000) {
			// 队头 URL 出队列
			String visitUrl = (String) SpiderQueue.unVisitedUrlDeQueue();
			if (visitUrl == null)
				continue;
			// DownTool downLoader = new DownTool();
			// 下载网页
			// downLoader.downloadFile(visitUrl);
			System.out.println(visitUrl);
			String info = HttpClientUtils.sendRequsetCallableGET(visitUrl);
			Document doc = Jsoup.parse(info, HttpClientUtils.captureHost(visitUrl));
			System.out.println("标题="+doc.title());
			// 该 URL 放入已访问的 URL 中
			SpiderQueue.addVisitedUrl(visitUrl);
			// 提取出下载网页中的 URL
			Set<String> links = HtmlParserTool.extracLinks(info,visitUrl);
			// 新的未访问的 URL 入队
			for (String link : links) {
				SpiderQueue.addUnvisitedUrl(link);
			}
		}
	}

	// main 方法入口
	public static void main(String[] args) {
		Test1 crawler = new Test1();
		try {
			crawler.crawling(new String[] { "http://v.youku.com/v_show/id_XMTU2ODEzNDE3Mg==.html?f=27238238&from=y1.3-idx-beta-1519-23042.223465.4-1" });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
