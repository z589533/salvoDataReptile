package org.salvo.tool;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownTool {

	/**
	 * 根据 URL 和网页类型生成需要保存的网页的文件名，去除 URL 中的非文件名字符
	 */
	private String getFileNameByUrl(String url, String contentType) {
		// 移除 "http://" 这七个字符
		url = url.substring(7);
		// 确认抓取到的页面为 text/html 类型
		if (contentType.indexOf("html") != -1) {
			// 把所有的url中的特殊符号转化成下划线
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
		} else {
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + "." + contentType.substring(contentType.lastIndexOf("/") + 1);
		}
		return url;
	}

	/**
	 * 保存网页字节数组到本地文件，filePath 为要保存的文件的相对地址
	 */
	private void saveToLocal(byte[] data, String filePath) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
			for (int i = 0; i < data.length; i++)
				out.write(data[i]);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}