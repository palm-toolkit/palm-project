package de.rwth.i9.palm.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.config.CacheConfiguration;

@Configuration
@EnableCaching
public class CachingConfig implements CachingConfigurer
{
	@Bean( destroyMethod = "shutdown" )
	public net.sf.ehcache.CacheManager ehCacheManager()
	{
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setName( "palmCache" );
		cacheConfiguration.setMemoryStoreEvictionPolicy( "LRU" );
		cacheConfiguration.setMaxEntriesLocalHeap( 1000 );
		cacheConfiguration.setMaxEntriesLocalDisk( 1000 );
		cacheConfiguration.setEternal( false );

		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
		config.addCache( cacheConfiguration );

		return net.sf.ehcache.CacheManager.newInstance( config );
	}

	@Bean
	@Override
	public CacheManager cacheManager()
	{
		return new EhCacheCacheManager( ehCacheManager() );
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator()
	{
		return new SimpleKeyGenerator();
	}

	@Override
	public CacheResolver cacheResolver()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

}