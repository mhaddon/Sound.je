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

import com.nestedbird.config.RegistrationConfigSettings;
import com.nestedbird.models.roles.RoleService;
import com.nestedbird.models.user.User;
import com.nestedbird.models.user.UserRepository;
import com.nestedbird.models.user.UserService;
import com.nestedbird.models.verificationtoken.VerificationToken;
import com.nestedbird.models.verificationtoken.VerificationTokenRepository;
import com.nestedbird.models.verificationtoken.VerificationTokenType;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.ratelimiter.RateLimit;
import com.nestedbird.util.EmailUtil;
import com.nestedbird.util.QueryBlock;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * This controller handles register events.
 */
@Controller
public class RegisterController {

    /**
     * Provides an abstraction over the email functionality
     */
    private final EmailUtil emailUtil;

    private final UserRepository userRepository;

    private final UserService userService;

    private final RoleService roleService;

    private final VerificationTokenRepository verificationTokenRepository;

    private final RegistrationConfigSettings registrationConfigSettings;

    /**
     * Instantiates a new Register controller.
     *
     * @param registrationConfigSettings  the registration config settings
     * @param emailUtil                   the email util
     * @param userRepository              the user repository
     * @param verificationTokenRepository the verification token repository
     * @param userService                 the user service
     * @param roleService                 the role service
     */
    @Autowired
    public RegisterController(final RegistrationConfigSettings registrationConfigSettings,
                              final EmailUtil emailUtil,
                              final UserRepository userRepository,
                              final VerificationTokenRepository verificationTokenRepository,
                              final UserService userService,
                              final RoleService roleService) {
        this.registrationConfigSettings = registrationConfigSettings;
        this.emailUtil = emailUtil;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Handles the request to register a new user
     *
     * @param request Http Form Request data
     * @return server response
     */
    @RequestMapping("login/register/create")
    @RateLimit(value = 5, limitPerMinute = 6)
    public ResponseEntity registerUser(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());

        return QueryBlock.create()

                // Ensure registration is currently enabled
                .require(registrationConfigSettings.getEnabled(), e -> e
                        .fail(HttpStatus.SERVICE_UNAVAILABLE, "Registration is currently unavailable").done())

                // Ensure the request has sent the required data
                .require(parser.has("email") &&
                        parser.has("password") &&
                        parser.has("fname") &&
                        parser.has("lname"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> {
                            data.put("email", parser.get("email").toString());
                            data.put("password", parser.get("password").toString());
                            data.put("fname", parser.get("fname").toString());
                            data.put("lname", parser.get("lname").toString());
                        }))

                // Ensure the email address is not already registered
                .require(data -> !userService.findFirstByEmail(data.get("email").toString()).isPresent(), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "User already exists").done())

                // Ensure the email address is a real valid email address
                .require(data -> EmailUtil.isValidEmailAddress(data.get("email").toString()), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Invalid email address").done())

                // Ensure the email is less or equal to 30 characters long
                .require(data -> data.get("email").toString().length() <= 30, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Email too long").done())

                // Ensure the password is at least 8 characters long
                .require(data -> data.get("password").toString().length() >= 8, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Password too short").done())

                // Ensure the first name is at least 2 characters long
                .require(data -> data.get("fname").toString().length() >= 2, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "First name too short").done())

                // Ensure the first name is less or equal to 15 characters long
                .require(data -> data.get("fname").toString().length() <= 15, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "First name too long").done())

                // Ensure the last name is less or equal to 15 characters long
                .require(data -> data.get("lname").toString().length() <= 15, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Last name too long").done())

                // If we passed all above successfully then we create the new user and send verification email
                .done(data -> {
                    final User user = User.builder()
                            .email(data.get("email").toString())
                            .password(data.get("password").toString())
                            .firstName(data.get("fname").toString())
                            .lastName(data.get("lname").toString())
                            .enabled(false)
                            .active(true)
                            .build();
                    userRepository.saveAndFlush(user);

                    final VerificationToken token = VerificationToken.builder()
                            .user(user)
                            .type(VerificationTokenType.VERIFY)
                            .build();

                    verificationTokenRepository.saveAndFlush(token);

                    emailUtil.sendVerificationEmail(user, token);
                });
    }

    /**
     * Confirms a successful registration and enables the account
     *
     * @param token  VerificationToken that was sent to the user
     * @param userId Users ID
     * @return server response
     */
    @RequestMapping("login/register/confirm")
    @ResponseBody
    @RateLimit(value = 2, limitPerMinute = 10)
    public ResponseEntity confirmRegistration(@RequestParam("t") final String token, @RequestParam("u") final String userId) {
        final VerificationToken vtoken = verificationTokenRepository.findOne(token);

        return QueryBlock.create()

                // Ensure the verification token exists
                .require(vtoken != null, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid")
                        .done(data -> data.put("user", vtoken.getUser())))

                // Ensure the userid associated to the verification token and the passed userid match
                .require(data -> ((User) data.get("user")).getId().equals(userId), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid").done())

                // Ensure the verification token is the right type of verification token
                .require(data -> vtoken.getType().equals(VerificationTokenType.VERIFY), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid").done())

                // Ensure the user is not already verified
                .require(data -> !((User) data.get("user")).getEnabled(), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "User already verified").done())

                // Ensure the verification token has not already expired, if it has, we will automatically send them a new one
                .require(data -> vtoken.getExpiryDate().isAfter(new DateTime()), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "This token has expired, we have sent you a new one")
                        .onFail(data -> {
                            final User user = (User) data.get("user");
                            final VerificationToken newToken = VerificationToken.builder()
                                    .user(user)
                                    .type(VerificationTokenType.VERIFY)
                                    .build();
                            verificationTokenRepository.saveAndFlush(newToken);
                            emailUtil.sendVerificationEmail(user, newToken);
                        }).done())

                // If we passed all the above successfully then we enable this account
                // The first account enabled is automatically the admin
                .done(data -> {
                    final User user = (User) data.get("user");

                    if (userRepository.findAll().size() == 1) {
                        roleService.findFirstByName("Admin")
                                .ifPresent(user::addRole);
                    } else {
                        roleService.findFirstByName("User")
                                .ifPresent(user::addRole);
                    }

                    user.setEnabled(true);
                    userRepository.saveAndFlush(user);
                    vtoken.setExpiryDate(new DateTime());
                    verificationTokenRepository.saveAndFlush(vtoken);
                }, "Account Successfully Verified");
    }

    /**
     * Reset account password
     *
     * @param email Email attached to password reset request
     * @return server response
     */
    @RequestMapping("login/reset/request")
    @RateLimit(value = 5, limitPerMinute = 5)
    public ResponseEntity resetPassword(@RequestParam("email") final String email) {
        final Optional<User> user = userService.findFirstByEmail(email);

        return QueryBlock.create()

                // Ensure account exists
                .require(user.isPresent(), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Account not found").done())

                // Ensure account is enabled
                .require(user.isPresent() && user.get().getEnabled(), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Account not verified").done())

                // If we pass all of the above succesfully then we will send the user a new reset email
                .done(data -> {
                    final VerificationToken vtoken = VerificationToken.builder()
                            .user(user.get())
                            .type(VerificationTokenType.RESET)
                            .expiryDate((new DateTime()).plusDays(4))
                            .build();

                    verificationTokenRepository.saveAndFlush(vtoken);
                    emailUtil.sendResetEmail(user.get(), vtoken);
                });
    }

    /**
     * Confirms the password reset request.
     *
     * @param request HTTP form data
     * @return server response
     */
    @RequestMapping("login/reset/confirm")
    @RateLimit(value = 2, limitPerMinute = 10)
    public ResponseEntity confirmReset(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());

        return QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("t") &&
                        parser.has("u") &&
                        parser.has("password"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> {
                            data.put("tokenId", parser.get("t").toString());
                            data.put("userId", parser.get("u").toString());
                            data.put("password", parser.get("password").toString());
                            data.put("token", verificationTokenRepository.findOne(data.get("tokenId").toString()));
                        }))

                // Ensure the verification token exists
                .require(data -> data.get("token") != null, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid")
                        .done(data -> data.put("user", ((VerificationToken) data.get("token")).getUser())))

                // Ensure the user in the verification token and the passed userid match
                .require(data -> ((User) data.get("user")).getId().equals(data.get("userId").toString()), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid").done())

                // Ensure the token is a valid reset token
                .require(data -> ((VerificationToken) data.get("token")).getType().equals(VerificationTokenType.RESET), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Authentication key invalid").done())

                // Ensure the token has not expired
                .require(data -> ((VerificationToken) data.get("token")).getExpiryDate().isAfter(new DateTime()), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "This token has expired").done())

                // Ensure the password is at least 8 characters
                .require(data -> data.get("password").toString().length() >= 8, e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Password too short").done())

                // If we pass all the above successfully then we change the password of the user
                .done(data -> {
                    final User user = (User) data.get("user");
                    final VerificationToken token = (VerificationToken) data.get("token");
                    user.setPassword(data.get("password").toString());
                    userRepository.saveAndFlush(user);
                    token.setExpiryDate(new DateTime());
                    verificationTokenRepository.saveAndFlush(token);
                }, "Password Changed");
    }


    /**
     * Checks if an email is registered
     *
     * @param request HTTP Form Data
     * @return returns a 1 if registered, 0 if unregistered
     */
    @RequestMapping("/login/check")
    @ResponseBody
    @RateLimit(value = 3, limitPerMinute = 15)
    public ResponseEntity isEmailRegistered(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());

        return QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("email"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("email", parser.get("email").toString())))

                // Ensure the email address is a real valid email address
                .require(data -> EmailUtil.isValidEmailAddress(data.get("email").toString()), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Invalid email address").done())

                // Check that this email exists
                .done(data -> {
                    Optional<User> user = userService.findFirstByEmail(data.get("email").toString());

                    data.put("result", user.map(e -> "1").orElse("0"));
                }, data -> data.get("result").toString());
    }
}
