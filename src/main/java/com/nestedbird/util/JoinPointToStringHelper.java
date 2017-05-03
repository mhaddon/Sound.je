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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

import java.util.Optional;

/**
 * The type Join point to string helper.
 */
@UtilityClass
public class JoinPointToStringHelper {
    /**
     * To string string.
     *
     * @param jp the jp
     * @return the string
     */
    public static String toString(JoinPoint jp) {
        StringBuilder sb = new StringBuilder();
        appendType(sb, getType(jp));
        Signature signature = jp.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature ms = (MethodSignature) signature;
            sb.append("#");
            sb.append(ms.getMethod().getName());
            sb.append("(");
            appendTypes(sb, ms.getMethod().getParameterTypes());
            sb.append(")");
        }
        return sb.toString();
    }

    private static Class<?> getType(JoinPoint jp) {
        return Optional.ofNullable(jp.getSourceLocation())
                .map(SourceLocation::getWithinType)
                .orElse(jp.getSignature().getDeclaringType());
    }

    private static void appendTypes(StringBuilder sb, Class<?>[] types) {
        for (int size = types.length, i = 0; i < size; i++) {
            appendType(sb, types[i]);
            if (i < size - 1) {
                sb.append(",");
            }
        }
    }

    private static void appendType(StringBuilder sb, Class<?> type) {
        if (type.isArray()) {
            appendType(sb, type.getComponentType());
            sb.append("[]");
        } else {
            sb.append(type.getName());
        }
    }
}
