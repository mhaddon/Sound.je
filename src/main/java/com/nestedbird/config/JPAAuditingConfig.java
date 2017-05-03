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

import com.nestedbird.models.user.User;
import com.nestedbird.models.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This class is responsible for configuring the auditor
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
@EnableJpaAuditing
public class JPAAuditingConfig {
    /**
     * The type Security auditor.
     */
    public static class SecurityAuditor implements AuditorAware<User> {
        private final UserService userService;

        /**
         * Instantiates a new Security auditor.
         * +
         *
         * @param userService the user service
         */
        SecurityAuditor(final UserService userService) {
            this.userService = userService;
        }

        @Override
        public User getCurrentAuditor() {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            return userService.findFirstByEmail(authentication.getPrincipal().toString()).orElse(null);
        }
    }

    private final UserService userService;

    /**
     * Instantiates a new Jpa auditing config.
     *
     * @param userService the user service
     */
    @Autowired
    public JPAAuditingConfig(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Create auditor provider.
     *
     * @return the auditor aware
     */
    @Bean
    public AuditorAware<User> createAuditorProvider() {
        return new SecurityAuditor(userService);
    }

    /**
     * Create auditing listener.
     *
     * @return the auditing entity listener
     */
    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }
}
