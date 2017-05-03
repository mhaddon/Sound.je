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

import org.apache.lucene.document.Document;
import org.hibernate.search.analyzer.Discriminator;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Map;

/**
 * The type Core bridge discriminator.
 */
abstract class CoreBridgeDiscriminator implements FieldBridge, Discriminator {
    @Override
    public final void set(final String name,
                          final Object submittedValue,
                          final Document document,
                          final LuceneOptions luceneOptions) {

        final BridgeController.Presets presets = BridgeController.builder()
                .baseName(name)
                .document(document)
                .luceneOptions(luceneOptions)
                .build().new Presets();

        save(name, submittedValue, presets);
    }

    /**
     * Save.
     *
     * @param name           the name
     * @param submittedValue the submitted value
     * @param presets        the presets
     */
    abstract void save(final String name,
                       final Object submittedValue,
                       final BridgeController.Presets presets);

    @Override
    public final String getAnalyzerDefinitionName(final Object value,
                                                  final Object entity,
                                                  final String fieldName) {
        final Map<String, String> analysers = getFieldAnalysers();
        final String trimmedFieldName = fieldName.replaceAll("^\\.+", "");

        if (analysers.containsKey(trimmedFieldName)) {
            return analysers.get(trimmedFieldName);
        }
        return analysers.get("default");
    }

    /**
     * Gets field analysers.
     *
     * @return the field analysers
     */
    abstract Map<String, String> getFieldAnalysers();
}
