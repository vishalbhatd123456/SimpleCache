import java.util.concurrent.*;
import java.util.*;

public class Cache<K, V> {
    private final Map<K, V> cacheMap = new ConcurrentHashMap<>();
    private final Map<K, Long> expiryMap = new ConcurrentHashMap<>();
    private final long ttl; // Time-to-live in milliseconds

    public Cache(long ttl) {
        this.ttl = ttl;
    }

    public void put(K key, V value) {
        cacheMap.put(key, value);
        expiryMap.put(key, System.currentTimeMillis() + ttl);
    }

    public V get(K key) {
        if (isExpired(key)) {
            remove(key);
            return null;
        }
        return cacheMap.get(key);
    }

    public void remove(K key) {
        cacheMap.remove(key);
        expiryMap.remove(key);
    }

    public void clear() {
        cacheMap.clear();
        expiryMap.clear();
    }

    private boolean isExpired(K key) {
        Long expiryTime = expiryMap.get(key);
        return expiryTime == null || expiryTime < System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Cache<String, String> cache = new Cache<>(5000); // 5 seconds TTL

        cache.put("key1", "value1");
        System.out.println("Key1: " + cache.get("key1")); // Should print "value1"

        try {
            Thread.sleep(6000); // Wait for TTL to expire
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Key1: " + cache.get("key1")); // Should print "null"
    }
}
