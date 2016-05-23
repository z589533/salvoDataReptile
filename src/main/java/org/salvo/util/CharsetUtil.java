package org.salvo.util;

import org.salvo.base.SalvoConfig;

public class CharsetUtil {
	public static String getKMPCharset(String info) {
		if (KMPUtil.getIndexOfStr(info.toUpperCase(), SalvoConfig.CHARSET_UTF_8.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_UTF_8;
		}
		if (KMPUtil.getIndexOfStr(info.toUpperCase(), SalvoConfig.CHARSET_GB2312.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_GB2312;
		}
		if (KMPUtil.getIndexOfStr(info.toUpperCase(), SalvoConfig.CHARSET_GBK.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_GBK;
		}
		if (KMPUtil.getIndexOfStr(info.toUpperCase(), SalvoConfig.CHARSET_ISO_8859_1.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_ISO_8859_1;
		}
		return SalvoConfig.CHARSET_UTF_8;
	}

	public static String getCharset(String info) {
		if (info.toUpperCase().indexOf(SalvoConfig.CHARSET_UTF_8.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_UTF_8;
		}

		if (info.toUpperCase().indexOf(SalvoConfig.CHARSET_GB2312.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_GB2312;
		}

		if (info.toUpperCase().indexOf(SalvoConfig.CHARSET_GBK.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_UTF_8;
		}

		if (info.toUpperCase().indexOf(SalvoConfig.CHARSET_ISO_8859_1.toUpperCase()) != -1) {
			return SalvoConfig.CHARSET_ISO_8859_1;
		}

		return SalvoConfig.CHARSET_UTF_8;
	}
}
