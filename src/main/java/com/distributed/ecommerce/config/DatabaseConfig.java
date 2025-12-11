package com.distributed.ecommerce.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    public HikariConfig masterHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.slave")
    public HikariConfig slaveHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource masterDataSource() {
        return new HikariDataSource(masterHikariConfig());
    }

    @Bean
    public DataSource slaveDataSource() {
        return new HikariDataSource(slaveHikariConfig());
    }

    @Bean
    @Primary
    public DataSource routingDataSource() {
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("master", masterDataSource());
        dataSources.put("slave", slaveDataSource());

        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource());
        return routingDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(routingDataSource());
        em.setPackagesToScan("com.distributed.ecommerce.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.cache.use_second_level_cache", true);
        properties.put("hibernate.cache.region.factory_class",
                "org.hibernate.cache.jcache.JCacheRegionFactory");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    public static class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            return DatabaseContextHolder.getDbType();
        }
    }

    public static class DatabaseContextHolder {
        private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

        public static void setDbType(String dbType) {
            contextHolder.set(dbType);
        }

        public static String getDbType() {
            return contextHolder.get();
        }

        public static void clearDbType() {
            contextHolder.remove();
        }
    }
}