package org.salvo.bloom.filter;

import org.salvo.bloom.bitMap.IntMap;
import org.salvo.bloom.bitMap.LongMap;
import org.salvo.bloom.iface.BitMap;
import org.salvo.bloom.iface.Filter;

public class TianlFilter implements Filter {

	private BitMap bm = null;

	private long size = 0;

	public TianlFilter(long maxValue, int MACHINENUM) throws Exception {
		this.size = maxValue;
		if (MACHINENUM == 32) {
			bm = new IntMap((int) (size / MACHINENUM));
		} else if (MACHINENUM == 64) {
			bm = new LongMap((int) (size / MACHINENUM));
		} else {
			throw new Exception("传入的机器位数有误");
		}
	}

	public TianlFilter(long maxValue) {
		this.size = maxValue;
		bm = new IntMap((int) (size / 32));
	}

	@Override
	public boolean contains(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		return bm.contains(hash);
	}

	@Override
	public void add(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		bm.add(hash);
	}

	@Override
	public boolean containsAndAdd(String str) {
		// TODO Auto-generated method stub
		long hash = this.myHashCode(str);
		if (bm.contains(hash)) {
			return true;
		} else {
			bm.add(hash);
		}
		return false;
	}

	@Override
	public long myHashCode(String str) {
		// TODO Auto-generated method stub
		long hash = 0;

		int iLength = str.length();
		if (iLength == 0)
			return 0;

		if (iLength <= 256)
			hash = 16777216L * (iLength - 1);
		else
			hash = 4278190080L;

		int i;

		char ucChar;

		if (iLength <= 96) {
			for (i = 1; i <= iLength; i++) {
				ucChar = str.charAt(i - 1);
				if (ucChar <= 'Z' && ucChar >= 'A') {
					ucChar = (char) (ucChar + 32);
				}
				hash += (3 * i * ucChar * ucChar + 5 * i * ucChar + 7 * i + 11 * ucChar) % 16777216;
			}
		} else {
			for (i = 1; i <= 96; i++) {
				ucChar = str.charAt(i + iLength - 96 - 1);
				if (ucChar <= 'Z' && ucChar >= 'A')
					ucChar = (char) (ucChar + 32);
				hash += (3 * i * ucChar * ucChar + 5 * i * ucChar + 7 * i + 11 * ucChar) % 16777216;
			}
		}
		if (hash < 0) {
			hash *= -1;
		}
		return hash % size;
	}

}
