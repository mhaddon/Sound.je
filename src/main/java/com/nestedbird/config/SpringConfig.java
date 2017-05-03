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

import com.nestedbird.components.userdetails.DetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import javax.servlet.Filter;

/**
 * This class is responsible for configuring up Spring
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
@EnableJpaRepositories("com.nestedbird.models")
@ComponentScan
@Slf4j
class SpringConfig extends WebMvcConfigurerAdapter {

    /**
     * User details service details service.
     *
     * @return the details service
     */
    @Bean
    @Description("Keeps track of all currently connected users")
    public DetailsService userDetailsService() {
        return new DetailsService();

    }

    //WebMvcConfigurerAdapter
    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).
                favorParameter(true).
                parameterName("mediaType").
                ignoreAcceptHeader(true).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML).
                mediaType("json", MediaType.APPLICATION_JSON);
    }

    /**
     * Bean name view resolver view resolver.
     *
     * @return the view resolver
     */
    @Bean
    public ViewResolver beanNameViewResolver() {
        return new BeanNameViewResolver();
    }

    /**
     * Servlet container embedded servlet container factory.
     *
     * @return the embedded servlet container factory
     * @source http://stackoverflow.com/questions/39146476/how-does-spring-boot-control-tomcat-cache
     */
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        return new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                final int cacheSize = 256 * 1024;
                final StandardRoot standardRoot = new StandardRoot(context);
                standardRoot.setCacheMaxSize(cacheSize);
                context.setResources(standardRoot);

                logger.info(String.format("New cache size (KB): %d", context.getResources().getCacheMaxSize()));
            }
        };
    }

    /**
     * Some filter registration filter registration bean.
     *
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        final FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(etagFilter());
        registration.addUrlPatterns("/api/*");
        registration.setName("etagFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * Etag filter filter.
     *
     * @return the filter
     */
    @Bean(name = "etagFilter")
    public Filter etagFilter() {
        return new ShallowEtagHeaderFilter();
    }
}