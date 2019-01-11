package org.b3log.latke.cache.bwae.redis;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.b3log.latke.cache.Cache;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Serializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

	private static final Logger LOGGER = Logger.getLogger(RedisCache.class.getName());

	private long maxCount = Long.MAX_VALUE;
	private long hitCount;
	private long missCount;
	private long putCount;
	private long cachedCount;
	private JedisPool connectionPool;

	private String name = null;
	private String host = null;
	private int port = 0;
	private String password = null;
	private int db = 1;

	public void initConfig() {
		System.getenv().forEach((k, v) -> {
			try {
				if (k.startsWith("AE_BS_REDIS_")) {
					if (k.endsWith("_HOST")) {
						host = v;
					} else if (k.endsWith("_PORT")) {
						port = Integer.parseInt(v);
					} else if (k.endsWith("_PASSWORD")) {
						password = v;
					}
				}
			} catch (Exception e) {
			}
		});
	}

	public RedisCache(String name) {
		try {
			this.name = name;
			if (name != null) {
				db = 1 + (Math.abs(name.hashCode()) % 8);
			} else {
				db = 1;
			}
			LOGGER.log(Level.ERROR, "RedisCache {0} use db {1}", name, db);
			initConfig();
			connectionPool = new JedisPool(new JedisPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, password);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "RedisCache init error", e);
		}
	}

	private Jedis getJedis() {
		Jedis jedis = null;
		if (connectionPool != null) {
			jedis = connectionPool.getResource();
		}
		if (jedis != null) {
			jedis.select(db);
		}
		return jedis;
	}

	private void returnJedis(Jedis jedis, boolean error) {
		if (error) {
			connectionPool.returnBrokenResource(jedis);
		} else {
			connectionPool.returnResource(jedis);
		}
	}

	@Override
	public boolean contains(K key) {
		return null != get(key);
	}

	@Override
	public void put(K key, V value) {
		Jedis jedis = getJedis();
		if (jedis == null) {
			return;
		}
		boolean error = true;
		putCount++;
		boolean isnew = !contains(key);
		try {
			jedis.set(key.toString().getBytes(), Serializer.serialize((Serializable) value));
			error = false;
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Cache error[key=" + key + ']', e);
		} finally {
			returnJedis(jedis, error);
		}
		if (isnew) {
			cachedCount++;
		}
	}

	@Override
	public void putAsync(K key, V value) {
		put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		Jedis jedis = getJedis();
		if (jedis == null) {
			return null;
		}
		boolean error = true;
		try {
			byte[] v = jedis.get(key.toString().getBytes());
			error = false;
			if (v != null) {
				hitCount++;
				return (V) Serializer.deserialize(v);
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets cached object failed[key=" + key + ']', e);
		} finally {
			returnJedis(jedis, error); 
		}
		missCount++;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long inc(K key, long delta) {
		V ret = get(key);

		if (null == ret || !(ret instanceof Long)) {
			final Long v = delta;

			ret = (V) v;
			put(key, ret);
		}

		if (ret instanceof Long) {
			final Long v = (Long) ret + delta;

			ret = (V) v;
			put(key, ret);
		}

		return (Long) ret;
	}

	@Override
	public void remove(K key) {
		Jedis jedis = getJedis();
		if (jedis == null) {
			return;
		}
		boolean error = true;
		try {
			cachedCount -= jedis.del(key.toString().getBytes());
			error = false;
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Remove failed[key=" + key + ']', e);
		}finally {
			returnJedis(jedis, error);
		}
	}

	@Override
	public void remove(Collection<K> keys) {
		for (final K key : keys) {
            remove(key);
        }
	}

	@Override
	public void removeAll() {
		missCount = 0;
		cachedCount = 0;
		hitCount = 0;
	}

	@Override
	public void setMaxCount(long maxCount) {
		this.maxCount = maxCount;
	}

	@Override
	public long getMaxCount() {
		return maxCount;
	}

	@Override
	public long getHitCount() {
		return hitCount;
	}

	@Override
	public long getMissCount() {
		return missCount;
	}

	@Override
	public long getPutCount() {
		return putCount;
	}

	@Override
	public long getCachedCount() {
		return cachedCount;
	}

	@Override
	public long getCachedBytes() {
		return -1;
	}

	@Override
	public long getHitBytes() {
		return -1;
	}

	@Override
	public void collect() {

	}
}
