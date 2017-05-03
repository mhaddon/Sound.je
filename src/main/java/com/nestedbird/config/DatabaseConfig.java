/*
 *  NestedBird  Copyright (C) 2016-2017  Michael Haddon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License version 3
 *  as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nestedbird.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * This class is responsible for configuring the database
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
    /**
     * Database Driver
     */
    private final String dbDriver;

    /**
     * Database Password
     */
    private final String dbPassword;

    /**
     * Database connection URL
     */
    private final String dbUrl;

    /**
     * Database username
     */
    private final String dbUsername;

    /**
     * Hibernate Dialect
     */
    private final String hibernateDialect;

    /**
     * Whether or not SQL should be shown
     */
    private final String hibernateShowSql;

    /**
     * The hibernate table creation/verification method
     */
    private final String hibernateHbm2DdlAuto;

    /**
     * Which packages will we scan
     */
    private final String entitymanagerPackagesToScan;

    private final String hibernateFormatSql;

    private final String hibernateUseSqlComments;

    /**
     * Instantiates a new Database config.
     *
     * @param dbDriver                    the db driver
     * @param dbPassword                  the db password
     * @param dbUrl                       the db url
     * @param dbUsername                  the db username
     * @param hibernateDialect            the hibernate dialect
     * @param hibernateShowSql            the hibernate show sql
     * @param hibernateHbm2DDLAuto        the hibernate hbm 2 ddl auto
     * @param entitymanagerPackagesToScan the entitymanager packages to scan
     * @param hibernateUseSqlComments     the hibernate use sql comments
     * @param hibernateFormatSql          the hibernate format sql
     */
    public DatabaseConfig(@Value("${spring.datasource.driver-class-name}") final String dbDriver,
                          @Value("${spring.datasource.password}") final String dbPassword,
                          @Value("${spring.datasource.url}") final String dbUrl,
                          @Value("${spring.datasource.username}") final String dbUsername,
                          @Value("${spring.jpa.hibernate.dialect}") final String hibernateDialect,
                          @Value("${spring.jpa.show_sql}") final String hibernateShowSql,
                          @Value("${spring.jpa.hibernate.ddl-auto}") final String hibernateHbm2DDLAuto,
                          @Value("${entitymanager.packagesToScan}") final String entitymanagerPackagesToScan,
                          @Value("${spring.jpa.properties.hibernate.use_sql_comments}") final String hibernateUseSqlComments,
                          @Value("${spring.jpa.properties.hibernate.format_sql}") final String hibernateFormatSql) {
        this.dbDriver = dbDriver;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.hibernateDialect = hibernateDialect;
        this.hibernateShowSql = hibernateShowSql;
        this.hibernateHbm2DdlAuto = hibernateHbm2DDLAuto;
        this.entitymanagerPackagesToScan = entitymanagerPackagesToScan;
        this.hibernateFormatSql = hibernateFormatSql;
        this.hibernateUseSqlComments = hibernateUseSqlComments;
    }

    /**
     * Creates a new EntityManager
     *
     * @return the entity manager
     */
    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory(dataSource(), hibernateProperties()).createEntityManager();
    }

    /**
     * Create new EntityManagerFactory
     *
     * @param dataSource          the data source
     * @param hibernateProperties the hibernate properties
     * @return the entity manager factory
     */
    @Bean
    public EntityManagerFactory entityManagerFactory(final DataSource dataSource, final Properties hibernateProperties) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(entitymanagerPackagesToScan);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties);
        em.setPersistenceUnitName("nestedbird");
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();

        return em.getObject();
    }

    /**
     * Creates a datasource bean out of the above connection information.
     *
     * @return new datasource
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    /**
     * Create a new hibernate properties object
     *
     * @return hibernate properties
     */
    @Bean
    public Properties hibernateProperties() {
        final Properties properties = new Properties();

        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.globally_quoted_identifiers", "true");
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.use_sql_comments", hibernateUseSqlComments);
        properties.put("hibernate.format_sql", hibernateFormatSql);
        properties.put("hibernate.hbm2ddl.auto", hibernateHbm2DdlAuto);
        properties.put("hibernate.connection.driver_class", dbDriver);
        properties.put("hibernate.connection.charSet", "utf8mb4");
        properties.put("hibernate.connection.characterEncoding", "utf8");
        properties.put("hibernate.connection.useUnicode", "true");
        properties.put("hibernate.search.default.directory_provider", "filesystem");
        properties.put("hibernate.search.default.indexBase", "./lucene_indexes/");
        properties.put("jadira.usertype.autoRegisterUserTypes", "true");
        properties.put("jadira.usertype.databaseZone", "jvm");
        properties.put("jadira.usertype.javaZone", "jvm");

        return properties;
    }
}
