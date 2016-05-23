package org.salvo.bloom.filter;

import org.salvo.bloom.bitMap.IntMap;
import org.salvo.bloom.bitMap.LongMap;
import org.salvo.bloom.iface.BitMap;
import org.salvo.bloom.iface.Filter;

public class ELFFilter implements Filter {

	private BitMap bm = null;

	private long size = 0;

	public ELFFilter(long maxValue, int MACHINENUM) throws Exception {
		this.size = maxValue;
		if (MACHINENUM == 32) {
			bm = new IntMap((int) (size / MACHINENUM));
		} else if (MACHINENUM == 64) {
			bm = new LongMap((int) (size / MACHINENUM));
		} else {
			throw new Exception("传入的机器位数有误");
		}
	}

	public ELFFilter(long maxValue) {
		size = maxValue ;
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
		long g = 0;
		
		int length = str.length() ;
		for (int i = 0; i < length; i++) {
			hash = (hash << 4) + str.charAt(i);
			g = hash & 0xF0000000;
			if (g > 0) {
				hash ^= g >> 24;
			}
			hash &= ~g;
		}

		return hash % size;
	}

}
