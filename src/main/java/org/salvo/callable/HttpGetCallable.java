package org.salvo.callable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpGetCallable implements Callable<String> {

	private CountDownLatch countDownLatch;
	private final CloseableHttpClient httpClient;
	private final HttpGet httpget;

	public HttpGetCallable(CloseableHttpClient httpClient, HttpGet httpget, CountDownLatch countDownLatch) {
		this.httpClient = httpClient;
		this.httpget = httpget;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public String call() throws Exception {
		CloseableHttpResponse response = null;
		String info = null;
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			// [Date: Fri, 13 May 2016 13:42:48 GMT, Server: nginx/1.8.1,
			// Content-Type: text/html; charset=utf-8,
			// Transfer-Encoding: chunked, Set-Cookie:
			// PHPSESSID=650tuuk7inajrc1l4irimhpfg1;
			// path=/, Expires: Thu, 19 Nov 1981 08:52:00 GMT,
			// Pragma: no-cache, Cache-Control: private, X-Powered-By: ThinkPHP,
			// X-Via: 1.1 zhouwangtong133:2 (Cdn Cache Server V2.0), Connection:
			// keep-alive, null, null, null, null, null]
			Header Header = response.getFirstHeader("Content-Type");
			// [Expires: Fri, 13 May 2016 13:47:42 GMT,
			// Date: Fri, 13 May 2016 13:45:42 GMT, Server: Tengine/2.1.2,
			// Content-Type: text/html, Transfer-Encoding: chunked,
			// Cache-Control:
			// max-age=120, Last-Modified: Fri, 13 May 2016 13:40:21 GMT, Age:
			// 70,
			// X-Via: 1.1 sdyt32:88 (Cdn Cache Server V2.0), 1.1 xiazai222:4
			// (Cdn Cache Server V2.0), Connection: keep-alive, null, null,
			// null, null, null, null]
			HttpEntity entity = response.getEntity();
			info = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			countDownLatch.countDown();
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return info;
	}

}
