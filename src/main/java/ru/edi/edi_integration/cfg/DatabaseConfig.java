package ru.edi.edi_integration.cfg;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = "ru.edi.edi_integration.db", transactionManagerRef = "TM_EDI_INT")
@EntityScan(basePackages = "ru.edi.edi_integration.entity")
@ComponentScan(basePackages = "ru.edi.edi_integration")
public class DatabaseConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
//        System.out.println("--------------test------------");
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(env.getProperty("project.db.url"));
        hikariDataSource.setUsername(env.getProperty("project.db.username"));
        hikariDataSource.setPassword(env.getProperty("project.db.password"));
        hikariDataSource.setDriverClassName(env.getProperty("project.db.driver-class-name"));

        hikariDataSource.setMinimumIdle(Integer.parseInt(env.getProperty("project.hikari.minimumIdle")));
        hikariDataSource.setMaximumPoolSize(Integer.parseInt(env.getProperty("project.hikari.maximumPoolSize")));
        hikariDataSource.setIdleTimeout(Integer.parseInt(env.getProperty("project.hikari.idleTimeout")));
        hikariDataSource.setPoolName(env.getProperty("project.hikari.poolName"));
        hikariDataSource.setMaxLifetime(Integer.parseInt(env.getProperty("project.hikari.maxLifetime")));
        hikariDataSource.setConnectionTimeout(Integer.parseInt(env.getProperty("project.hikari.connectionTimeout")));
        hikariDataSource.setConnectionTestQuery(env.getProperty("project.hikari.testQuery"));
        return hikariDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("ru.edi.edi_integration.entity");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", env.getProperty("project.hibernate.show_sql"));
        properties.setProperty("hibernate.format_sql", env.getProperty("project.hibernate.format_sql"));
        properties.setProperty("hibernate.use_sql", env.getProperty("project.hibernate.use_sql"));
        properties.setProperty("hibernate.generate_statistics", env.getProperty("project.hibernate.generate_statistics"));
        properties.setProperty("hibernate.id.new_generator_mappings", env.getProperty("project.hibernate.id.new_generator_mappings"));
        properties.setProperty("hibernate.default_schema", env.getProperty("project.hibernate.default_schema"));
        properties.setProperty("hibernate.search.autoregister_listeners", env.getProperty("project.hibernate.search.autoregister_listeners"));
        properties.setProperty("hibernate.bytecode.use_reflection_optimizer", env.getProperty("project.hibernate.bytecode.use_reflection_optimizer"));
        properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", env.getProperty("project.hibernate.jdbc.lob.non_contextual_creation"));
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        em.setJpaProperties(properties);
        return em;
    }

    @Bean("TM_EDI_INT")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
