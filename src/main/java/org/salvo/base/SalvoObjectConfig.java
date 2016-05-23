package org.salvo.base;

import com.google.gson.Gson;

public class SalvoObjectConfig extends SalvoConfig {

	public final static Gson gson = new Gson();

	public final static String[] EXCLUDEURL = new String[] { ".exe", ".jpg", ".png", ".gif", ".js", ".css", ".zip",
			".pdf" ,".apk","mp4","mp3"};
}
