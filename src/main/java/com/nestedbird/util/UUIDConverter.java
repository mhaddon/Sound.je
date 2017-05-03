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


import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * This class contains utility functions for controlling UUIDs in string format
 * Its primary role is converting string represented UUIDs to/from Base64
 */
@UtilityClass
public class UUIDConverter {

    /**
     * Pattern we use to detect if this is a real and valid uuid
     */
    private static final Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f‌​]{4}-[0-9a-f]{12}$");

    /**
     * Turns a UUID in string format to a Base64 encoded version
     *
     * @param uuidString String representation of the uuid
     * @return base64 encoded version of the uuid
     * @throws IllegalArgumentException String must be a valid uuid
     * @throws NullPointerException     String cannot be null
     */
    public static String toBase64(final String uuidString) {
        if (uuidString == null) throw new NullPointerException("String cannot be null");
        if (!isUUID(uuidString)) throw new IllegalArgumentException("string must be a valid uuid");

        final UUID uuid = UUID.fromString(uuidString);
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }

    /**
     * Detects whether or not the string is a UUID
     *
     * @param uuidString String represented UUID
     * @return Whether or not the string is a UUID
     */
    public static Boolean isUUID(final String uuidString) {
        return PatternMatcher.of(uuidPattern, uuidString).doesMatch();
    }

    /**
     * Converts a Base64 encoded string to a string represented UUID
     *
     * @param base64String Base64 Representation of the UUID
     * @return String represented UUID
     * @throws NullPointerException     String must not be null
     * @throws IllegalArgumentException String should be 22 characters long
     */
    public static String fromBase64(final String base64String) {
        if (base64String == null) throw new NullPointerException("String cannot be null");
        if (base64String.length() != 22) throw new IllegalArgumentException("String should be 22 characters long");

        final byte[] bytes = Base64.decodeBase64(base64String);
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid.toString();
    }
}