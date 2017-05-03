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

package com.nestedbird.views;

import com.nestedbird.jackson.ApiError;
import com.nestedbird.jackson.SessionData;
import com.nestedbird.models.user.UserService;
import com.nestedbird.util.Mutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * This controller handles login related events
 */
@Controller
public class LoginController {

    private final UserService userService;

    /**
     * Instantiates a new Login controller.
     *
     * @param userService the user service
     */
    @Autowired
    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * On login failure we display the error instead of session information
     *
     * @param request the request
     * @return the session error
     */
    @RequestMapping(value = "/session", params = {"failure"})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object getSessionError(final HttpServletRequest request) {
        final Exception recentException = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (recentException != null) {
            return ApiError.builder()
                    .status(400)
                    .error(recentException.getClass().getSimpleName())
                    .message(recentException.getMessage())
                    .build();
        }

        return ApiError.builder()
                .status(200)
                .error("OK")
                .message("OK")
                .build();
    }

    /**
     * Gets the current session information and returns it
     *
     * @param request HTTPServletRequest
     * @return session information as a json
     */
    @RequestMapping(value = "/session")
    @ResponseBody
    public SessionData getSession(final HttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        final String tokenValue = token.getToken();
        final String tokenParameterName = token.getParameterName();
        final String tokenHeaderName = token.getHeaderName();

        final Mutable<String> username = Mutable.of(null);
        final Mutable<String> userId = Mutable.of(null);
        final Mutable<String[]> userAuthorities = Mutable.of(new String[0]);

        Optional.ofNullable(authentication.getName())
                .filter(name -> !name.equals("anonymousUser"))
                .ifPresent(name ->
                        userService.findFirstByEmail(name).ifPresent(user -> {
                            username.mutate(user.getEmail());
                            userId.mutate(user.getId());
                            userAuthorities.mutate(
                                    authentication
                                            .getAuthorities()
                                            .stream()
                                            .map(GrantedAuthority::getAuthority)
                                            .toArray(String[]::new));
                        })
                );

        return SessionData.builder()
                .tokenValue(tokenValue)
                .tokenParameterName(tokenParameterName)
                .tokenHeaderName(tokenHeaderName)
                .email(username.get())
                .userId(userId.get())
                .authorities(userAuthorities.get())
                .build();
    }
}
