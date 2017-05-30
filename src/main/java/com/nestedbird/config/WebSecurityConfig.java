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
import com.nestedbird.modules.permissions.EndpointPermissionsManifest;
import lombok.experimental.UtilityClass;
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

    private final EndpointPermissionsManifest endpointPermissions;

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
                             ServerConfigSettings serverConfigSettings, final EndpointPermissionsManifest endpointPermissionsManifest) {
        this.secureCookie = secureCookie;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
        this.serverConfigSettings = serverConfigSettings;
        this.endpointPermissions = endpointPermissionsManifest;
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
        web.ignoring().antMatchers(
                HttpMethod.GET,
                endpointPermissions.ignore()
        );
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception { // @formatter:off
        http
            .authorizeRequests()

                // Public GET resources
                .antMatchers(
                    HttpMethod.GET,
                    endpointPermissions.publicGET()
                ).permitAll()

                // Public POST resources
                .antMatchers(
                    HttpMethod.POST,
                    endpointPermissions.publicPOST()
                ).permitAll()

                // MODERATOR level privilege GET resources
                .antMatchers(
                    HttpMethod.GET,
                    endpointPermissions.moderatorGET()
                ).hasAuthority(Permissions.PRIV_MODERATOR)

                // ADMIN level privilege GET resources
                .antMatchers(
                    HttpMethod.GET,
                    endpointPermissions.adminGET()
                ).hasAuthority(Permissions.PRIV_ADMIN)

                // CREATE ENTITY level privilege POST resources
                .antMatchers(
                    HttpMethod.POST,
                    endpointPermissions.entityPOST()
                ).hasAuthority(Permissions.PRIV_CREATE_ENTITY)

                // UPDATE ENTITY level privilege PUT resources
                .antMatchers(
                    HttpMethod.PUT,
                    endpointPermissions.entityPUT()
                ).hasAuthority(Permissions.PRIV_UPDATE_ENTITY)

                // DELETE ENTITY level privilege DELETE resources
                .antMatchers(
                    HttpMethod.DELETE,
                    endpointPermissions.entityDELETE()
                ).hasAuthority(Permissions.PRIV_DELETE_ENTITY)

                .anyRequest()
                    .denyAll()
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
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                    .permitAll()
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

    @UtilityClass
    static final private class Permissions {
        static private final String PRIV_ADMIN = "PRIV_ADMIN";
        static private final String PRIV_MODERATOR = "PRIV_MODERATOR";
        static private final String PRIV_CREATE_ENTITY = "PRIV_CREATE_ENTITY";
        static private final String PRIV_UPDATE_ENTITY = "PRIV_UPDATE_ENTITY";
        static private final String PRIV_DELETE_ENTITY = "PRIV_DELETE_ENTITY";
    }
}
