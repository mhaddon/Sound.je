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

package com.nestedbird.components.authentication;

import com.nestedbird.config.LoginConfigSettings;
import org.joda.time.DateTime;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * This service records failed and successful login attempts and concludes when an account has been temporarily blocked
 */
@Service
public class LoginAttemptService {

    private static final String ATTEMPTS_REDIS_KEY = "loginAttempts";

    private final RedissonClient redissonClient;

    private final LoginConfigSettings loginConfigSettings;

    /**
     * Instantiates a new Login attempt service.
     *
     * @param redissonClient      the redisson client
     * @param loginConfigSettings the login settings
     */
    @Autowired
    public LoginAttemptService(final RedissonClient redissonClient,
                               final LoginConfigSettings loginConfigSettings) {
        this.redissonClient = redissonClient;
        this.loginConfigSettings = loginConfigSettings;
    }

    /**
     * Record a login success, also remove recent login failures with the same email and ip address
     *
     * @param email  email address of successful login
     * @param ipAddr IP address of requester
     */
    void loginSucceeded(final String email, final String ipAddr) {
        final RList<LoginAttempt> loginAttempts = redissonClient.getList(ATTEMPTS_REDIS_KEY);
        loginAttempts.add(LoginAttempt.builder().email(email).ipAddr(ipAddr).success(true).build());

        // remove failed attempts for this account by this ip address
        loginAttempts.removeAll(loginAttempts.stream()
                .filter(e -> !e.getSuccess() && e.getEmail().equals(email) && e.getIpAddr().equals(ipAddr))
                .collect(Collectors.toList()));
    }

    /**
     * Record a login failure
     *
     * @param email  email of failed login
     * @param ipAddr ip address of requester
     */
    void loginFailed(final String email, final String ipAddr) {
        final RList<LoginAttempt> loginAttempts = redissonClient.getList(ATTEMPTS_REDIS_KEY);
        loginAttempts.add(LoginAttempt.builder().success(false).email(email).ipAddr(ipAddr).build());
    }

    /**
     * Has the email exceeded login attempts
     *
     * @param email email to check
     * @return if the email exceeds login attempts
     */
    public boolean hasExceededLoginAttempts(final String email) {
        final RList<LoginAttempt> loginAttempts = redissonClient.getList(ATTEMPTS_REDIS_KEY);

        return loginAttempts.stream()
                .filter(e -> !e.getSuccess())
                .filter(e -> e.getEmail().equals(email))
                .filter(e -> (new DateTime()).minusMinutes(loginConfigSettings.getEmailAttemptsCooldownInMinutes()).isBefore(e.getDateTime()))
                .count() > loginConfigSettings.getMaxEmailLoginAttempts();
    }

    /**
     * Is this IP currently blocked
     *
     * @param ipAddr IP Address to check
     * @return If it is blocked
     */
    public boolean isIPBlocked(final String ipAddr) {
        final RList<LoginAttempt> loginAttempts = redissonClient.getList(ATTEMPTS_REDIS_KEY);

        return loginAttempts.stream()
                .filter(e -> !e.getSuccess())
                .filter(e -> e.getIpAddr().equals(ipAddr))
                .filter(e -> (new DateTime()).minusMinutes(loginConfigSettings.getIpAttemptsCooldownInMinutes()).isBefore(e.getDateTime()))
                .count() > loginConfigSettings.getMaxIPLoginAttempts();
    }
}