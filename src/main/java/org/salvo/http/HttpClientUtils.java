package org.salvo.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.salvo.base.SalvoConfig;
import org.salvo.callable.HttpGetCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http请求线程池
 */
public class HttpClientUtils {

	public final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private static void config(HttpRequestBase httpRequestBase) {
		// httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1;
		// WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
		// httpRequestBase.setHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		// httpRequestBase.setHeader("Accept-Language",
		// "en-US,en,zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		// httpRequestBase.setHeader("Accept-Charset",
		// "ISO-8859-1,utf-8,UTF-8,gbk,gb2312;q=0.7,*;q=0.7");
		// httpRequestBase.setHeader("Host","n.firefoxchina.cn");
		// httpRequestBase.setHeader("accept","*/*");
		// httpRequestBase.setHeader("connection","Keep-Alive");
		// httpRequestBase.setHeader("user-agent","Mozilla/4.0 (compatible; MSIE
		// 6.0; Windows NT 5.1;SV1)");

		// 火狐标准
		httpRequestBase.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
		httpRequestBase.setHeader("Accept", "*/*");
		httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		httpRequestBase.setHeader("Accept-Charset", "ISO-8859-1,utf-8,UTF-8,gbk,gb2312;q=0.7,*;q=0.7");
		httpRequestBase.setHeader("Connection", "keep-alive");
		httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate");
		httpRequestBase.setHeader("Cache-Control", "max-age=0");

		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(SalvoConfig.HTTP_TIME_OUT)
				.setConnectTimeout(SalvoConfig.HTTP_TIME_OUT).setSocketTimeout(SalvoConfig.HTTP_TIME_OUT).setRedirectsEnabled(false).build();
		httpRequestBase.setConfig(requestConfig);
	}

	public static void sendRequsetPost(final String url, Map<String, String> map) {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加到200
		cm.setMaxTotal(SalvoConfig.HTTP_MAXTOTAL);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(SalvoConfig.HTTP_DEFAULTMAXPERROUTE);
		// 将目标主机的最大连接数增加到50
		HttpHost localhost = new HttpHost(captureHost(url), captureHostPort(url));
		cm.setMaxPerRoute(new HttpRoute(localhost), SalvoConfig.HTTP_MAXPERROUTE);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= SalvoConfig.HTTP_RETRY_COUNT) {// 如果已经重试了5次，就放弃
					logger.info("return out=" + url);
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					logger.info("service out url=" + url);
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					logger.info("SSL=" + url);
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					logger.info("time out=" + url);
					return true;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					logger.info("service out=" + url);
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					logger.info("service out=" + url);
					return false;
				}
				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler).build();

		long start = System.currentTimeMillis();
		try {
			ExecutorService executors = Executors.newFixedThreadPool(1);
			CountDownLatch countDownLatch = new CountDownLatch(1);
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();// 带参数
			for (String key : map.keySet()) {
				nvps.add(new BasicNameValuePair(key, map.get(key)));
			}
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// httpPost.set
			config(httpPost);
			// 启动线程抓取
			executors.execute(new PostRunnable(httpClient, httpPost, countDownLatch));
			countDownLatch.await();
			executors.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			logger.info("Thread:" + Thread.currentThread().getName() + ",time=" + System.currentTimeMillis()
					+ ", all Thread finish wait....");
		}

		long end = System.currentTimeMillis();
		logger.info("url=" + url + " waste of time=" + (end - start) + "ms");
	}

	public static void sendRequsetGET(final String url) {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加到200
		cm.setMaxTotal(SalvoConfig.HTTP_MAXTOTAL);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(SalvoConfig.HTTP_DEFAULTMAXPERROUTE);

		// 将目标主机的最大连接数增加到50
		HttpHost localhost = new HttpHost(captureHost(url), captureHostPort(url));
		cm.setMaxPerRoute(new HttpRoute(localhost), SalvoConfig.HTTP_MAXPERROUTE);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= SalvoConfig.HTTP_RETRY_COUNT) {// 如果已经重试了5次，就放弃
					System.out.println("重试失败---已放弃return out=" + url);
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					System.out.println("service out url=" + url);
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					System.out.println("SSL=" + url);
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					System.out.println("time out=" + url);
					return true;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					System.out.println("service out=" + url);
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					System.out.println("service out=" + url);
					return false;
				}
				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}
				if(exception instanceof SocketTimeoutException){
					return true;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler).build();

		// URL列表数组

		long start = System.currentTimeMillis();
		try {
			ExecutorService executors = Executors.newFixedThreadPool(1);
			CountDownLatch countDownLatch = new CountDownLatch(1);
			HttpGet httpget = new HttpGet(url);
			config(httpget);
			// 启动线程抓取
			executors.execute(new GetRunnable(httpClient, httpget, countDownLatch));
			countDownLatch.await();
			executors.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("线程:" + Thread.currentThread().getName() + ",时间=" + System.currentTimeMillis()
					+ ", all Thread finish wait....");

		}

		long end = System.currentTimeMillis();
		System.out.println("耗时=" + (end - start) + "(ms)");
	}

	public static String sendRequsetCallableGET(final String url) {
		String info = null;
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加到200
		cm.setMaxTotal(SalvoConfig.HTTP_MAXTOTAL);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(SalvoConfig.HTTP_DEFAULTMAXPERROUTE);

		// 将目标主机的最大连接数增加到50
		HttpHost localhost = new HttpHost(captureHost(url), captureHostPort(url));
		cm.setMaxPerRoute(new HttpRoute(localhost), SalvoConfig.HTTP_MAXPERROUTE);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= SalvoConfig.HTTP_RETRY_COUNT) {// 如果已经重试了5次，就放弃
					System.out.println("重试失败---已放弃return out=" + url);
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					System.out.println("如果服务器丢掉了连接，那么就重试=" + url);
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					System.out.println("不要重试SSL握手异常=" + url);
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					System.out.println("time out=" + url);
					return true;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					System.out.println("目标服务器不可达=" + url);
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					System.out.println("连接被拒绝=" + url);
					return false;
				}
				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}
				if(exception instanceof SocketTimeoutException){//socket超时
					System.out.println("socket超时="+url);
					return true;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler).build();
		long start = System.currentTimeMillis();
		try {
			ExecutorService executors = Executors.newFixedThreadPool(1);
			CountDownLatch countDownLatch = new CountDownLatch(1);
			HttpGet httpget = new HttpGet(url);
			config(httpget);
			// 启动线程抓取
			Future<String> future = executors.submit(new HttpGetCallable(httpClient, httpget, countDownLatch));
			info = future.get();
			countDownLatch.await();
			executors.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			System.out.println("线程:" + Thread.currentThread().getName() + ",时间=" + System.currentTimeMillis()
					+ ", all Thread finish wait....");

		}
		long end = System.currentTimeMillis();
		System.out.println("耗时=" + (end - start) + "(ms)");
		return info;
	}

	/**
	 * 截取url的域名
	 * 
	 * @param url
	 * @return
	 */
	public static String captureHost(String url) {
		Matcher m = Pattern.compile("^(https://[^/]+|http://[^/]+)").matcher(url);
		while (m.find()) {
			return m.group();
		}
		return m.group();
	}

	/**
	 * 截取url的端口号
	 * 
	 * @param url
	 * @return
	 */
	public static Integer captureHostPort(String url) {
		String regex = "//(.*?):([0-9]+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(url);
		String port = null;
		while (m.find()) {
			port = m.group(2);
		}
		if (port == null) {
			port = "80";
		}
		return Integer.parseInt(port);
	}

	static class PostRunnable implements Runnable {

		private CountDownLatch countDownLatch;
		private final CloseableHttpClient httpClient;
		private final HttpPost httpPost;

		public PostRunnable(CloseableHttpClient httpClient, HttpPost httpPost, CountDownLatch countDownLatch) {
			this.httpClient = httpClient;
			this.httpPost = httpPost;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(httpPost, HttpClientContext.create());
				HttpEntity entity = response.getEntity();
				String info = EntityUtils.toString(entity, "utf-8");
				System.out.println(info);
				EntityUtils.consume(entity);
			} catch (IOException e) {
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
		}
	}

	static class GetRunnable implements Runnable {
		private CountDownLatch countDownLatch;
		private final CloseableHttpClient httpClient;
		private final HttpGet httpget;

		public GetRunnable(CloseableHttpClient httpClient, HttpGet httpget, CountDownLatch countDownLatch) {
			this.httpClient = httpClient;
			this.httpget = httpget;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(httpget, HttpClientContext.create());
				HttpEntity entity = response.getEntity();
				System.out.println(EntityUtils.toString(entity, "utf-8"));
				EntityUtils.consume(entity);
			} catch (IOException e) {
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
		}
	}
}
