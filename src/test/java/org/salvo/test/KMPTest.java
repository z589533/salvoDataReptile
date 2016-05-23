package org.salvo.test;

public class KMPTest {

	public static void main(String[] args) {
		KMPTest ktest = new KMPTest("java字符串搜索是否包含关键词数组，求效率最高的算法有一个关键词数组，里面全是string然后有一个字符串进来，要求判断这个字符串是否包含关键词数组中的string（有一个命中就返回true，全部不命中返回false）。要求速度最快的算法。请问java中有现成的这种方法实现么？ ", "asdasdasd");

		ktest.debugNextArr();
		int theLoc = ktest.getIndexOfStr();

		System.out.println();
		System.out.println("匹配位置在：" + theLoc);

	}

	private int[] _nextArr = null;
	private String _originStr = null;
	private String _moduleStr = null;


	public KMPTest(String originStr, String moduleStr) {
		_originStr = originStr;
		_moduleStr = moduleStr;
		_nextArr = caculate_nextArr();
	}

	/**
	 * 计算next 数组的值。
	 */
	private int[] caculate_nextArr() {
		if (_moduleStr == null || _moduleStr.length() == 0) {
			return null;
		}
		int[] theNextArr = new int[_moduleStr.length()];
		for (int i = 0; i < _moduleStr.length(); i++) {
			if (i == 0) {
				theNextArr[i] = 0;
			} else if (i == 1) {
				if (_moduleStr.charAt(0) == _moduleStr.charAt(1)) {
					theNextArr[i] = 1;
				} else {
					theNextArr[i] = 0;
				}

			}

			else {
				int theLength2 = i;
				boolean hasEqual = false;

				for (int j = theLength2 - 1; j >= 0; j--) {
					String prefix_str = _moduleStr.substring(0, j + 1);
					String suffix_str = _moduleStr.substring(theLength2 - j, theLength2 + 1);
					if (prefix_str.endsWith(suffix_str)) {
						hasEqual = true;
						theNextArr[i] = prefix_str.length();

						break;
					} else {

					}
				}
				if (hasEqual == false) {
					theNextArr[i] = 0;
				}
			}
		}
		// ---

		return theNextArr;
	}

	public void debugNextArr() {
		if (_nextArr != null) {
			System.out.println("next array的值：");
			for (int tmp : _nextArr) {
				System.out.print(tmp + "   ");
			}
		}
	}

	public int getIndexOfStr() {
		
		

		if (_moduleStr == null || _moduleStr.length() <= 0) {
			return -1;
		}
		if (_originStr == null || _originStr.length() <= 0) {
			return -1;
		}
		if (_originStr.length() < _moduleStr.length()) {
			return -1;
		}
		int res = -1;
		int totalLength = _originStr.length();
		boolean flag_end = false;

		int origin_loc = 0;
		int module_loc = 0;
		while (flag_end == false) {

			char c_origin = _originStr.charAt(origin_loc);
			char c_module = _moduleStr.charAt(module_loc);

			if (c_origin == c_module) {
				if (module_loc == _moduleStr.length() - 1) {
					res = origin_loc - module_loc;
					break;
				} else {

					origin_loc++;
					module_loc++;
				}

			} else {
				if (module_loc == 0) {
					origin_loc++;
					module_loc = 0;
					if (origin_loc >= totalLength) {
						break;
					}
				} else {
					if (module_loc <= 0) {
						module_loc++;
						origin_loc++;
					} else {
						int m_callback = _nextArr[module_loc - 1];
						module_loc = m_callback;
					}
				}
				continue;

			}
			if (origin_loc >= totalLength) {
				break;
			}

		}
		return res;
	}
}
