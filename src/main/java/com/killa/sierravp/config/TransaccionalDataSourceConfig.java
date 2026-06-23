package com.killa.sierravp.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * CAPA DE DATOS — Servidor de Datos (BD transaccional / OLTP).
 *
 * Define el datasource PRIMARIO. Los repositorios del paquete
 * {@code com.killa.sierravp.repository} usan esta unidad de persistencia.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.killa.sierravp.repository",
        entityManagerFactoryRef = "txEntityManagerFactory",
        transactionManagerRef = "txTransactionManager")
public class TransaccionalDataSourceConfig {

    @Primary
    @Bean
    @org.springframework.boot.context.properties.ConfigurationProperties("spring.datasource")
    public DataSourceProperties txDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource txDataSource(@Qualifier("txDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean txEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("txDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.killa.sierravp.domain")
                .persistenceUnit("transaccional")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager txTransactionManager(
            @Qualifier("txEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
