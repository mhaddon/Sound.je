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
import com.nestedbird.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

/**
 * This class is responsible for configuring the sites security
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Will we use a secure cookie
     */
    private final Boolean secureCookie;

    private final DetailsService userDetailsService;

    private final DataSource dataSource;

    private final ServerConfigSettings serverConfigSettings;

    /**
     * Instantiates a new Web security config.
     *
     * @param secureCookie         the secure cookie
     * @param userDetailsService   the user details service
     * @param dataSource           the data source
     * @param serverConfigSettings the server config settings
     */
    @Autowired
    public WebSecurityConfig(@Value("${security.secure_cookie}") final Boolean secureCookie,
                             final DetailsService userDetailsService,
                             final DataSource dataSource,
                             ServerConfigSettings serverConfigSettings) {
        this.secureCookie = secureCookie;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
        this.serverConfigSettings = serverConfigSettings;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(User.PASSWORD_ENCODER);
    }

    //    @Override
    //    public void configure(WebSecurity web) throws Exception {
    //        web.debug(true);
    //    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/*.css",
                        "/*.js"
                );
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception { // @formatter:off
        http
            .authorizeRequests() // todo redo this
                .antMatchers(
                        HttpMethod.GET,
                        "/api/*/Users/**/*",
                        "/api/*/Privileges/**/*",
                        "/api/*/Roles/**/*"
                )
                    .hasAuthority("PRIV_ADMIN")
                .antMatchers(
                    HttpMethod.GET,
                        "/",
                    "/*.css",
                    "/*.js",
                    "/session",
                    "/login/register",
                    "/login/reset",
                    "/login/check",
                    "/login/reset/request",
                    "/api/*/*/",
                    "/api/*/Artists/*/Media",
                    "/api/*/Artists/*/Songs",
                    "/api/*/EventTimes/Upcoming",
                    "/api/*/Events/Upcoming",
                    "/api/*/Events/updateFB",
                    "/api/*/Media/Hot",
                    "/api/*/*/*-*-*-*-*"
//                        "/api/v1/ScannedPages/manualrequest"
                )
                    .permitAll()
                .antMatchers(
                        HttpMethod.POST,
                    "/login/check",
                    "/login/register/create",
                    "/login/register/confirm",
                    "/login/reset/confirm"
                )
                    .permitAll()
                .antMatchers(
                    HttpMethod.GET,
                    "/api/*/*/schema"
                )
                    .hasAuthority("PRIV_GET_ENTITY_SCHEMA")
                .antMatchers(
                    HttpMethod.POST,
                    "/api/*/*/",
                    "/api/v1/Media/parseurl"
                )
                    .hasAuthority("PRIV_CREATE_ENTITY")
                .antMatchers(
                    HttpMethod.PUT,
                    "/api/*/*/*-*-*-*-*"

                )
                    .hasAuthority("PRIV_UPDATE_ENTITY")
                .antMatchers(
                    HttpMethod.DELETE,
                    "/api/*/*/*-*-*-*-*"
                )
                    .hasAuthority("PRIV_DELETE_ENTITY")
                .antMatchers(
                    HttpMethod.GET,
                    "/api/v1/ScannedPages/manualrequest",
                    "/api/v1/ScannedPages/testrequest"
                )
                    .hasAuthority("PRIV_ADMIN")
                .anyRequest()
                    .permitAll()
            .and()

            .formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/login")
                    .permitAll()
                .loginProcessingUrl("/login/process")
                .defaultSuccessUrl(serverConfigSettings.getExternalUrl() + "/session")
                .failureUrl(serverConfigSettings.getExternalUrl() + "/session?failure")
            .and()

            .rememberMe()
                .key("appKey")
                .tokenValiditySeconds(604800)
                .tokenRepository(persistentTokenRepository())
                .alwaysRemember(true)
                .useSecureCookie(secureCookie)
            .and()

            .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/session")
            .and()

            .exceptionHandling()
                .accessDeniedPage("/login")
            .and()

            .csrf()
//                .disable()
            .and()

            .headers()
                .defaultsDisabled()
                .cacheControl()
                    .disable()
                .contentTypeOptions()
                .and()
                .frameOptions()
                .and()
                .xssProtection()
        ;

    } // @formatter:on

    /**
     * Persistent token repository persistent token repository.
     *
     * @return the persistent token repository
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
