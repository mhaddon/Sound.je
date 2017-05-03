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

package com.nestedbird.components.userdetails;

import com.nestedbird.components.authentication.LoginAttemptService;
import com.nestedbird.models.roles.Role;
import com.nestedbird.models.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Details service.
 */
@Component
public class DetailsService implements UserDetailsService {
    private UserService userService;
    private LoginAttemptService loginAttemptService;
    private HttpServletRequest request;

    /**
     * Sets user service.
     *
     * @param userService the user service
     */
    @Autowired
    public void setUserRepository(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Sets login attempt service.
     *
     * @param loginAttemptService the login attempt service
     */
    @Autowired
    public void setLoginAttemptService(final LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Sets request.
     *
     * @param request the request
     */
    @Autowired
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Retrieves user by username
     *
     * @param email users email address
     * @return userdetails
     * @throws RuntimeException user must be a valid login target
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws RuntimeException {
        if (loginAttemptService.hasExceededLoginAttempts(email)) {
            throw new RuntimeException("This account has had too many failed login attempts, please try again shortly.");
        } else if (loginAttemptService.isIPBlocked(getClientIP())) {
            throw new RuntimeException("This IP has had too many failed login attempts, please try again later.");
        }

        return userService.findFirstByEmail(email)
                .map(user ->
                        new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                user.getEnabled(),
                                true,
                                true,
                                user.getActive(),
                                getAuthorities(new ArrayList<>(user.getRoles()))
                        )
                ).orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + email));
    }

    /**
     * Retrieves the IP address of the user
     *
     * @return ip address
     */
    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * Get user authorities
     *
     * @param roles what roles the user has
     * @return list of authorities
     */
    public final Collection<? extends GrantedAuthority> getAuthorities(final List<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
                .collect(Collectors.toList());
    }
}
