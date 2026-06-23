package com.killa.sierravp.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * CAPA DE DATAWAREHOUSE — Servidor de DataWareHouse (modelo dimensional).
 *
 * Segundo datasource, independiente del transaccional. Los repositorios del
 * paquete {@code com.killa.sierravp.dw.repository} usan esta unidad de
 * persistencia. Representa el "otro servidor" que pide el documento; en la
 * demo apunta al mismo MySQL pero a un esquema distinto.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.killa.sierravp.dw.repository",
        entityManagerFactoryRef = "dwEntityManagerFactory",
        transactionManagerRef = "dwTransactionManager")
public class DataWareHouseDataSourceConfig {

    @Bean
    public DataSource dwDataSource(
            @Value("${sierravp.dw.url}") String url,
            @Value("${sierravp.dw.user}") String user,
            @Value("${sierravp.dw.pass}") String pass) {
        return DataSourceBuilder.create()
                .url(url)
                .username(user)
                .password(pass)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean dwEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dwDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.killa.sierravp.dw.domain")
                .persistenceUnit("datawarehouse")
                .build();
    }

    @Bean
    public PlatformTransactionManager dwTransactionManager(
            @Qualifier("dwEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
