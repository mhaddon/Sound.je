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

package com.nestedbird.util;

import com.google.common.collect.Lists;
import com.nestedbird.config.ServerConfigSettings;
import com.nestedbird.models.user.User;
import com.nestedbird.models.verificationtoken.VerificationToken;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This Email Util class provides a common interface for emails and an abstraction over the EmailService
 */
@Component
@Slf4j
public class EmailUtil {
    /**
     * Is valid email address boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public static boolean isValidEmailAddress(final String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * Email service
     */
    private final EmailService emailService;
    /**
     * Server configuration
     */
    private final ServerConfigSettings serverConfigSettings;

    /**
     * Instantiates a new Email util.
     *
     * @param emailService         the email service
     * @param serverConfigSettings server config settings
     */
    @Autowired
    public EmailUtil(final EmailService emailService, ServerConfigSettings serverConfigSettings) {
        this.emailService = emailService;
        this.serverConfigSettings = serverConfigSettings;
    }

    /**
     * Send verification email.
     *
     * @param user  the user
     * @param token the token
     */
    public void sendVerificationEmail(final User user, final VerificationToken token) {
        final String url = String.format(
                "%s/login/register/confirm?t=%s&u=%s",
                serverConfigSettings.getExternalUrl(),
                token.getId(),
                user.getId());

        //        final String emailBody = String.format(
        //                "<a href='%s'>%s</a>",
        //                url, url);

        final String emailBody = url;

        sendEmail(user, "Verify Email", emailBody);
    }

    /**
     * Send email.
     *
     * @param user    the user
     * @param subject the subject
     * @param body    the body
     */
    public void sendEmail(final User user, final String subject, final String body) {
        Email email = null;
        try {
            email = DefaultEmail.builder()
                    .from(new InternetAddress("hello@nestedbird.com", "Hello"))
                    .to(Lists.newArrayList(new InternetAddress(user.getEmail(), user.getEmail())))
                    .subject(subject)
                    .body(body)
                    .encoding(String.valueOf(Charset.forName("UTF-8"))).build();
        } catch (UnsupportedEncodingException e) {
            logger.info("[EmailUtil] [sendEmail] Failure To Create Email", e);
        }
        emailService.send(email);
    }

    /**
     * Send reset email.
     *
     * @param user  the user
     * @param token the token
     */
    public void sendResetEmail(final User user, final VerificationToken token) {
        final String url = String.format(
                "%s/login/reset?t=%s&u=%s",
                serverConfigSettings.getExternalUrl(),
                token.getId(),
                user.getId());

        //        final String emailBody = String.format(
        //                "<a href='%s'>%s</a>",
        //                url, url);

        final String emailBody = url;


        sendEmail(user, "Verify Email", emailBody);
    }
}
