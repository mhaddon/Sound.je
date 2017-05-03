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

package com.nestedbird.components.bridges;

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.roles.Role;
import com.nestedbird.modules.entitysearch.SearchAnalysers;

import java.util.*;

/**
 * The type Role bridge.
 */
public class RoleBridge extends CoreBridgeDiscriminator {

    @Override
    @SuppressWarnings("unchecked")
    public void save(final String name,
                     final Object submittedValue,
                     final BridgeController.Presets presets) {

        Optional.ofNullable(submittedValue).ifPresent(value -> {
            if (Collection.class.isAssignableFrom(value.getClass())) {
                final List<Role> roles = new ArrayList<>((Collection) value);
                roles.stream()
                        .filter(AuditedEntity::getActive)
                        .map(Role::getName)
                        .forEach(presets::addStringToDocument);
            } else {
                Optional.of((Role) value)
                        .filter(AuditedEntity::getActive)
                        .map(Role::getName)
                        .ifPresent(presets::addStringToDocument);
            }
        });
    }

    @Override
    Map<String, String> getFieldAnalysers() {
        final Map<String, String> map = new HashMap<>();

        map.put("default", SearchAnalysers.ENGLISH_WORD_ANALYSER);

        return map;
    }
}