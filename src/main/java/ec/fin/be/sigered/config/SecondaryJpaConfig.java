/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 *
 * @author rmoran
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "ec.fin.be.sigered.secondary.repository", // Paquete de tus repositorios secundarios
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryJpaConfig {

    @Value("${spring.datasource.secondary.jndi-name}")
    private String jndiName;
    
     @Value("${spring.jpa.secondary.database-platform}")
    private String secondaryDialect;

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource(jndiName);
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder, 
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        
         Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", secondaryDialect);
        
        return builder
                .dataSource(dataSource)
                .packages("ec.fin.be.sigered.secondary.entity") 
                .persistenceUnit("secondaryPU")
                .properties(properties)
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
