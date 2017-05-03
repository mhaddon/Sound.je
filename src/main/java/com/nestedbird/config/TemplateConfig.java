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

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for configuring the template engine
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class TemplateConfig {

    /**
     * Bean to configure thymeleaf view resolver
     *
     * @return the thymeleaf view resolver
     */
    @Bean(name = "viewResolver")
    public ThymeleafViewResolver getViewResolver() {
        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(getTemplateEngine());
        return viewResolver;
    }

    /**
     * Bean to configure the template engine
     *
     * @return the template engine
     */
    @Bean(name = "templateEngine")
    public SpringTemplateEngine getTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        final Set<IDialect> dialects = new HashSet<>();
        dialects.add(new LayoutDialect());
        templateEngine.setAdditionalDialects(dialects);
        templateEngine.setTemplateResolver(getTemplateResolver());
        return templateEngine;
    }

    /**
     * Bean to get the template resolver
     *
     * @return the template resolver
     */
    @Bean(name = "templateResolver")
    public ServletContextTemplateResolver getTemplateResolver() {
        final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("XHTML");
        return templateResolver;
    }

    /**
     * Bean to configure view resolver
     *
     * @param cnm the content negotion manager
     * @return the view resolver
     */
    @Bean
    public ViewResolver cnViewResolver(final ContentNegotiationManager cnm) {
        final ContentNegotiatingViewResolver cnvr =
                new ContentNegotiatingViewResolver();
        cnvr.setContentNegotiationManager(cnm);
        return cnvr;
    }
}
