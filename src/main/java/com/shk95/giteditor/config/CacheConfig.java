/*
package com.shk95.giteditor.config;

import org.ehcache.config.CacheConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@EnableCaching
@Configuration
public class CacheConfig {// Ehcache

	private final ApplicationProperties properties;

	public static <T> void setIfNotNull(Consumer<T> setter, T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

	BiFunction<CacheConfiguration, ApplicationProperties.CacheConfig.Cache, CacheConfiguration> map = (conf, prop) -> {
		setIfNotNull(conf::setMaxEntriesLocalHeap, prop.getMaxEntriesLocalHeap());
		setIfNotNull(conf::setTimeToIdleSeconds, prop.getTimeToIdleSeconds());
		setIfNotNull(conf::setTimeToLiveSeconds, prop.getTimeToLiveSeconds());
		setIfNotNull(conf::setEternal, prop.getEternal());
		setIfNotNull(conf::memoryStoreEvictionPolicy, prop.getMemoryStoreEvictionPolicy());
		setIfNotNull(conf::diskExpiryThreadIntervalSeconds, prop.getDiskExpiryThreadIntervalSeconds());
		return conf;
	};

	public CacheConfig(ApplicationProperties properties) {
		this.properties = properties;
	}

	@Bean
	public EhCacheCacheManager ehCacheManager(CacheManager cacheManager) {
		return new EhCacheCacheManager(cacheManager);
	}

	@Bean
	public CacheManager cacheManager() {
		net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();

		// Named Cache
		properties.getCache().getNames()
			.forEach(prop -> {
				CacheConfiguration cacheConfiguration = new CacheConfiguration();
				cacheConfiguration.setName(prop.getName());
				configuration.addCache(map.apply(cacheConfiguration, prop));
			});
		// Default Cache
		configuration.defaultCache(map.apply(new CacheConfiguration(), properties.getCache().getDefaultCache()));

		return CacheManager.newInstance(configuration);
	}
}
*/
