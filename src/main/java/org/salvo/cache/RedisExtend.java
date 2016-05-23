package org.salvo.cache;

import org.salvo.base.SalvoObjectConfig;
import org.salvo.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

@Service("RedisExtend")
public class RedisExtend {
	@Autowired
	@Qualifier("redisDao")
	private RedisDao redisDao;

	/**
	 * redis保存对象
	 * 
	 * @return
	 */
	public String setModel(String key, Object val) {
		String info = redisDao.setModel(key.getBytes(), SerializeUtil.serialize(val));
		return info;
	}

	/**
	 * redis保存对象-时间值
	 * 
	 * @return
	 */
	public String setModel(String key, int seconds, Object val) {
		String info = redisDao.setModel(key.getBytes(), seconds, SerializeUtil.serialize(val));
		return info;
	}

	/**
	 * redis保存对象
	 * 
	 * json转换
	 * 
	 * @return
	 */
	public String setJsonModel(String key, Object val) {
		String info = redisDao.set(key, SalvoObjectConfig.gson.toJson(val));
		return info;
	}

	/**
	 * redis保存对象-时间值
	 * 
	 * @return
	 */
	public String setJsonModel(String key, int seconds, Object val) {
		String info = redisDao.set(key, seconds, SalvoObjectConfig.gson.toJson(val));
		return info;
	}

	/**
	 * 设置基本值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key, String value) {
		return redisDao.set(key, value);
	}

	/**
	 * 设置基本值-带时间
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key, int seconds, String value) {
		return redisDao.set(key, seconds, value);
	}

	/**
	 * 获取
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return redisDao.get(key);
	}

	/**
	 * redis获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getModel(String key) {
		byte[] data = redisDao.getModel((key).getBytes());
		Object obj = SerializeUtil.unserialize(data);
		return obj;
	}

	/**
	 * redis获取对象 json转换
	 * 
	 * @param <T>
	 * 
	 * @param key
	 * @return
	 */
	public <T> T getJsonModel(String key, T t) {
		String json = redisDao.get(key);
		t = SalvoObjectConfig.gson.fromJson(json, new TypeToken<T>() {
		}.getType());
		return t;
	}

	/**
	 * redis判断对象是否存在
	 * 
	 * @param key
	 * @return
	 */
	public Boolean existModel(String key) {
		return redisDao.existModel(key.getBytes());
	}

	/**
	 * redis判断对象是否存在
	 * 
	 * @param key
	 * @return
	 */
	public Boolean exist(String key) {
		return redisDao.exist(key);
	}

	/**
	 * 删除
	 */
	public long removekey(String key) {
		return redisDao.delkey(key);
	}

	/**
	 * 
	 * json附加
	 * 
	 * @return
	 */
	public long appendJsonModel(String key, Object value) {
		long info = redisDao.append(key, SalvoObjectConfig.gson.toJson(value));
		return info;
	}

	/**
	 * 附加
	 * 
	 * @return
	 */
	public long appendModel(String key, Object val) {
		long info = redisDao.append(key.getBytes(), SerializeUtil.serialize(val));
		return info;
	}
}
