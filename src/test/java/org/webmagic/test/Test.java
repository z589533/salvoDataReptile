package org.webmagic.test;

public class Test  {

//	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
//
//	@Override
//	public void process(Page page) {
//		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
//		page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//		page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//		if (page.getResultItems().get("name") == null) {
//			// skip this page
//			page.setSkip(true);
//		}
//		page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
//	}
//
//	@Override
//	public Site getSite() {
//		return site;
//	}
//
//	public static void main(String[] args) {
//		Spider.create(new GithubRepoPageProcessor()).addUrl("http://www.027peixun.com/").thread(5).run();
//	}
}
