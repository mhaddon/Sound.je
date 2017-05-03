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

package com.nestedbird.modules.schema;

import com.nestedbird.models.core.DataObject;
import com.nestedbird.modules.schema.annotations.SchemaIgnore;
import com.nestedbird.modules.schema.annotations.SchemaView;
import com.nestedbird.util.Mutable;
import lombok.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * SchemaField is a wrapper around a Field object that provides additional functionality
 * It is specifically designed to read the Field element for various Schema information
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaField extends DataObject implements Serializable {

    /**
     * The Field element we are reading from
     */
    private final Field field;

    /**
     * Is this Field visible.
     * By default the answer is false, however if it has a @SchemaView annotation the default is true.
     * We check for a SchemaView annotation or SchemaIgnore annotation
     *
     * @return Whether the field is visible
     */
    public Boolean isVisible() {
        Mutable<Boolean> visible = Mutable.of(false);

        getAnnotation(SchemaView.class)
                .ifPresent(visible.mutate(SchemaView::visible));

        getAnnotation(SchemaIgnore.class)
                .ifPresent(visible.mutate(schemaIgnore -> !schemaIgnore.value()));

        return visible.get();
    }

    /**
     * Gets a specific annotation from the Field, returns it as an optional
     *
     * @param <T>             The annotation type
     * @param annotationClass the annotation class
     * @return the annotation optional
     */
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        T annotation = field.getAnnotation(annotationClass);

        return Optional.ofNullable(annotation);
    }

    /**
     * The type of the Field as a string
     *
     * @return the Fields type
     */
    public String getType() {
        return field.getType().getSimpleName();
    }

    /**
     * Gets the Fields name
     * We check SchemaView to see if this has been overridden
     *
     * @return the Fields name
     */
    public String getName() {
        Mutable<String> name = Mutable.of(field.getName());

        getAnnotation(SchemaView.class)
                .ifPresent(name.mutateIf(SchemaView::name,
                        schemaView -> !schemaView.name().isEmpty()));

        return name.get();
    }

    /**
     * Gets the SchemasView, the View is how the information will be presented to the user.
     * Default is Text
     *
     * @return the Fields view
     */
    public String getView() {
        Mutable<String> view = Mutable.of("Text");

        getAnnotation(SchemaView.class)
                .ifPresent(view.mutate(SchemaView::value));

        return view.get();
    }

    /**
     * Get an array of any Mappings the Field may have
     *
     * @return The mappings as an Array
     */
    public String[] getMappings() {
        Mutable<String[]> mappings = Mutable.of(new String[]{});

        getAnnotation(SchemaView.class)
                .ifPresent(mappings.mutate(SchemaView::mappings));

        return mappings.get();
    }

    /**
     * Is this Field enabled.
     * By default the answer is false, however if it has a @SchemaView annotation the default is true.
     *
     * @return Whether the field is visible
     */
    public Boolean isEnabled() {
        Mutable<Boolean> enabled = Mutable.of(false);

        getAnnotation(SchemaView.class)
                .ifPresent(enabled.mutate(schemaView -> !schemaView.locked()));

        return enabled.get();
    }

    /**
     * Is this field meant to represent an Array
     *
     * @return Whether or not this field is an array
     */
    public Boolean isArray() {
        Mutable<Boolean> array = Mutable.of(false);

        getAnnotation(SchemaView.class)
                .ifPresent(array.mutate(schemaView -> schemaView.value().equalsIgnoreCase("Array")));

        return array.get();
    }

    /**
     * Is this field meant to represent an Enumeration
     *
     * @return Whether or not this field is an enum
     */
    public Boolean isEnum() {
        Mutable<Boolean> enumeration = Mutable.of(false);

        getAnnotation(SchemaView.class)
                .ifPresent(enumeration.mutate(schemaView -> schemaView.value().equalsIgnoreCase("Enumeration")));

        return enumeration.get() && Enum.class.isAssignableFrom(getTypeOverride());
    }

    /**
     * Gets Fields type override.
     *
     * @return the Fields type override
     */
    public Class<?> getTypeOverride() {
        Mutable<Class<?>> type = Mutable.of(field.getType());

        getAnnotation(SchemaView.class)
                .ifPresent(type.mutateIf(SchemaView::type,
                        schemaView -> !schemaView.type().equals(Object.class)));

        return type.get();
    }

    /**
     * Gets validation annotations.
     *
     * @return the validation annotations
     */
    public Map<String, Object> getValidationAnnotations() {
        Map<String, Object> validations = new HashMap<>();

        getAnnotation(javax.validation.constraints.Max.class)
                .ifPresent(max -> validations.put("Max", max));

        getAnnotation(javax.validation.constraints.Min.class)
                .ifPresent(min -> validations.put("Min", min));

        getAnnotation(javax.validation.constraints.NotNull.class)
                .ifPresent(notNull -> validations.put("NotNull", notNull));

        getAnnotation(javax.validation.constraints.Size.class)
                .ifPresent(size -> {
                    validations.put("Size.Max", size.max());
                    validations.put("Size.Min", size.min());
                });

        return validations;
    }
}