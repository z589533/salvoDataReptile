package org.salvo.base;

/**
 * 公共参数
 * @author Administrator
 *
 */
public class SalvoConfig {

	public final static int HTTP_TIME_OUT = 6000;// 链接超时时间
	public static final int HTTP_MAXTOTAL = 200;// 最大连接数增
	public static final int HTTP_DEFAULTMAXPERROUTE = 50;// 每个路由基础的连接
	public static final int HTTP_MAXPERROUTE = 50;// 目标主机的最大连接数
	public static final int HTTP_RETRY_COUNT = 5;// 异常重试的次数

	
	public static final String CHARSET_GBK="gbk";
	public static final String CHARSET_UTF_8="utf-8";
	public static final String CHARSET_GB2312="gb2312";
	public static final String CHARSET_ISO_8859_1="iso-8859-1";	
	
}
