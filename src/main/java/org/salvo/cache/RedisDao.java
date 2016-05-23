package org.salvo.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

@Service("redisDao")
public class RedisDao {
	@Autowired
	private ShardedJedisPool shardedJedisPool;

	private final static int SECONDS = 60 * 60 * 24 * 1000 * 1;

	public String set(String key, String value) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.set(key, value);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public String set(String key, int seconds, String value) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.setex(key, seconds, value);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public String get(String key) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.get(key);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public String setModel(byte[] key, byte[] val) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.set(key, val);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public String setModel(byte[] key, int seconds, byte[] val) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.setex(key, seconds, val);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public byte[] getModel(byte[] key) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.get(key);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public Boolean existModel(byte[] key) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.exists(key);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public Boolean exist(String key) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.exists(key);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}

	public long delkey(String key) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.del(key);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}
	
	
	public Long append(String key,String value) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.append(key, value);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}
	
	
	public Long append(byte[] key,byte[] value) {
		ShardedJedis jedis = null;
		boolean success = true;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.append(key, value);
		} catch (JedisException e) {
			success = false;
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			throw e;
		} finally {
			if (success && jedis != null) {
				shardedJedisPool.returnResource(jedis);
			}
		}
	}
}
