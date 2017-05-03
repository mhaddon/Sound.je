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

package com.nestedbird.modules.formparser;

import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.UUIDConverter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class processes the payload of a form and saves the data to the database
 * Because different variable types may be processed differently, this class supports methods for handling
 * all sorts of different datatypes and situations.
 */
@Configuration
@Slf4j
public class FormParse {
    /**
     * The application context so we can search for beans
     */
    private final ApplicationContext appContext;

    /**
     * Instantiates a new Form parse.
     *
     * @param appContext the app context
     */
    @Autowired
    public FormParse(final ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Parses a HTTPServletRequest and saves the information onto an existing entity
     *
     * @param <T>            - Class of entity
     * @param existingEntity - Entity to be written over
     * @param request        - HTTPRequest information
     * @return - Entity with new information
     */
    public <T extends BaseEntity> T parse(final T existingEntity, final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        parser.loopData((key, value) -> writeToEntity(existingEntity, key, value));

        return existingEntity;
    }

    /**
     * Write the value to the existingEntity field with the name of key
     *
     * @param <T>            Type of the entity
     * @param existingEntity The entity we are changing
     * @param key            The key we are changing
     * @param value          The new value
     */
    private <T extends BaseEntity> void writeToEntity(T existingEntity, String key, Object value) {
        final PropertyAccessor accessor = PropertyAccessorFactory.forDirectFieldAccess(existingEntity);

        if (accessor.getPropertyType(key) != null) {
            try {
                if (value.getClass().equals(JSONObject.class)) {
                    writeObjectToEntity(accessor, key, (JSONObject) value);
                } else if (value.getClass().equals(JSONArray.class)) {
                    writeArrayToEntity(accessor, key, (JSONArray) value);
                } else if (isFieldValid(accessor, key, existingEntity.getClass())) {
                    writeValueToEntity(accessor, key, value);
                }
            } catch (JSONException e) {
                logger.info("[FormParse] [writeToEntity] Unable To Process JSON", e);
            }
        }
    }

    /**
     * Write object to the entity
     *
     * @param accessor The accessor for the existing entity
     * @param key      The fields name we are overwriting
     * @param value    The new value
     * @throws JSONException the json exception
     */
    private void writeObjectToEntity(final PropertyAccessor accessor,
                                     final String key,
                                     final JSONObject value) throws JSONException {
        final ResolvableType type = accessor.getPropertyTypeDescriptor(key).getResolvableType();

        accessor.setPropertyValue(key, parseObject(value, type));
    }

    /**
     * Write array to entity.
     *
     * @param accessor The accessor for the existing entity
     * @param key      The fields name we are overwriting
     * @param value    The new value
     * @throws JSONException the json exception
     */
    private void writeArrayToEntity(final PropertyAccessor accessor,
                                    final String key,
                                    final JSONArray value) throws JSONException {
        final ResolvableType type = accessor.getPropertyTypeDescriptor(key).getResolvableType();

        accessor.setPropertyValue(key, parseArray(value, type));
    }

    /**
     * Write normal value to entity.
     *
     * @param accessor The accessor for the existing entity
     * @param key      The fields name we are overwriting
     * @param value    The new value
     */
    private void writeValueToEntity(final PropertyAccessor accessor,
                                    final String key,
                                    final Object value) {
        final ResolvableType type = accessor.getPropertyTypeDescriptor(key).getResolvableType();

        accessor.setPropertyValue(key, parseValue(value, type));
    }

    /**
     * Is value array an array of database entities
     *
     * @param type type of value
     * @return the boolean
     */
    private Boolean isValueArrayOfDatabaseEntities(final Class type) {
        return BaseEntity.class.isAssignableFrom(type) && type.getAnnotation(SchemaRepository.class) != null;
    }

    /**
     * Is value a database entity
     *
     * @param type type of value
     * @return the boolean
     */
    private Boolean isValueDatabaseEntity(final Class type) {
        return BaseEntity.class.isAssignableFrom(type) &&
                type.getAnnotation(SchemaRepository.class) != null;
    }

    /**
     * Is value a period.
     *
     * @param type type of value
     * @return the boolean
     */
    private Boolean isValuePeriod(final ResolvableType type) {
        return type.getRawClass().isAssignableFrom(Period.class);
    }

    /**
     * Is value an enum
     *
     * @param type type of the value
     * @return boolean
     */
    private Boolean isValueEnum(final ResolvableType type) {
        return Enum.class.isAssignableFrom(type.getRawClass());
    }

    /**
     * Is value a database index
     *
     * @param type the type of value
     * @return boolean
     */
    private Boolean isValueDatabaseIndex(final ResolvableType type) {
        return type.getRawClass().getAnnotation(SchemaRepository.class) != null;
    }

    /**
     * Is value a JODA DateTime
     *
     * @param value the value
     * @param type  the type of the value
     * @return boolean
     */
    private Boolean isValueDateTime(final Object value, final ResolvableType type) {
        return (type.getRawClass().isAssignableFrom(DateTime.class)) && (value.toString().length() > 0);
    }

    /**
     * Is value a comma separated value of database indexes
     *
     * @param type the type of the value
     * @return boolean
     */
    private Boolean isValueCSVDatabaseIndexes(final ResolvableType type) {
        return java.util.Collection.class.isAssignableFrom(type.getRawClass()) &&
                (type.getGeneric(0) != null) &&
                (type.getGeneric(0).getRawClass().getAnnotation(SchemaRepository.class) != null);
    }

    /**
     * Some Types can just be written instantly to the class, this method tells you if a type does not need
     * additional processing
     *
     * @param fieldType - The type of field
     * @return boolean boolean
     */
    private Boolean isTypeStandard(final Class fieldType) {
        final List<Class> standardTypes = Arrays.asList(
                String.class,
                Long.class,
                Integer.class,
                Double.class,
                Boolean.class
        );
        return standardTypes.contains(fieldType);
    }

    /**
     * Checks whether the target field is final, meaning it should not be written over...
     * apparently spring writes over final variables
     *
     * @param objectClass - The class of the object we are checking
     * @param fieldName   - The name of the field we are checking
     * @return - Whether or not the field is final
     */
    private boolean isFieldFinal(final Class objectClass, final String fieldName) {
        final Mutable<Boolean> returnVar = Mutable.of(true);
        getField(objectClass, fieldName)
                .ifPresent(field -> returnVar.mutate(Modifier.isFinal(field.getModifiers())));
        return returnVar.get();
    }

    /**
     * Is the field editable according to the SchemaView annotation
     *
     * @param accessor the accessor
     * @param key      the key
     * @return boolean boolean
     */
    private Boolean isFieldEditable(final PropertyAccessor accessor, final String key) {
        final SchemaView schemaView = accessor.getPropertyTypeDescriptor(key).getAnnotation(SchemaView.class);

        if (schemaView != null) {
            final boolean isLocked = schemaView.locked() && accessor.getPropertyValue(key) != null;
            final boolean isVisible = schemaView.visible();

            return !isLocked && isVisible;
        }

        return false;
    }

    /**
     * Can the field be written to and read from?
     * This is according to the springs accessor, if it has no setter or getter it still can somehow be read to...
     * apparently
     *
     * @param accessor  - The spring propertyAccessor          -
     * @param fieldName - The fieldName
     * @return boolean boolean
     */
    private Boolean isFieldInteractable(final PropertyAccessor accessor, final String fieldName) {
        return accessor.isReadableProperty(fieldName) &&
                accessor.isWritableProperty(fieldName);
    }

    /**
     * Is field valid for editing
     *
     * @param accessor    the accessor
     * @param fieldName   the field name
     * @param entityClass the entity class
     * @return the boolean
     */
    private boolean isFieldValid(final PropertyAccessor accessor, final String fieldName, final Class entityClass) {
        return isFieldInteractable(accessor, fieldName) &&
                isFieldEditable(accessor, fieldName) &&
                doesSetterExist(entityClass, fieldName, accessor.getPropertyType(fieldName)) &&
                !isFieldFinal(entityClass, fieldName);
    }

    /**
     * Saves and associate these database entities to this object
     *
     * @param value database entities to process
     * @param type  type of the database entities
     * @return new list of database entities
     * @throws JSONException the exception
     */
    private List<Object> parseArrayOfDatabaseEntities(final JSONArray value, final Class type) throws JSONException {
        final List<Object> elements = new ArrayList<>();
        for (Integer i = 0; i < value.length(); i++) {
            if (!value.isNull(i)) {
                final JSONObject data = (JSONObject) value.get(i);
                elements.add(parseBaseEntity(data, type));
            }
        }
        return elements;
    }

    /**
     * processes an array
     *
     * @param value value to process
     * @param type  type of this array
     * @return new list
     * @throws JSONException the exception
     */
    private Object parseArray(final JSONArray value, final ResolvableType type) throws JSONException {
        final List<Object> elements = new ArrayList<>();
        if (isValueArrayOfDatabaseEntities(type.getGeneric(0).getRawClass())) {
            elements.addAll(parseArrayOfDatabaseEntities(value, type.getGeneric(0).getRawClass()));
        }
        return elements;
    }

    /**
     * Updates a BaseEntity in the database
     *
     * @param value     data to edit
     * @param fieldType type of baseentity
     * @return edited base entity
     * @throws JSONException the exception
     */
    private BaseEntity parseBaseEntity(final JSONObject value, final Class fieldType) throws JSONException {
        final JpaRepository repository = getFieldRepository(fieldType);
        Optional<BaseEntity> entity = Optional.empty();

        if (value.has("id") && (!value.isNull("id"))) {
            final String id = value.get("id").toString();
            entity = Optional.ofNullable((BaseEntity) repository.findOne(id));
        }

        if (!entity.isPresent()) {
            try {
                entity = Optional.ofNullable((BaseEntity) Class.forName(fieldType.getName()).newInstance());
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e1) {
                logger.info("[FormParse] [parseBaseEntity] Failure To Create Class Instance", e1);
            }
        }
        entity.ifPresent(e -> {
            final ParameterMapParser parser = new ParameterMapParser(value);
            parser.loopData((key, data) -> writeToEntity(e, key, data));
            repository.saveAndFlush(e);
        });

        return entity.orElseGet(null);
    }

    /**
     * Parses an Object
     *
     * @param value the value
     * @param type  the type of the value
     * @return new Object
     * @throws JSONException the exception
     */
    private Object parseObject(final JSONObject value, final ResolvableType type) throws JSONException {
        Object returnVar = value;
        if (isValueDatabaseEntity(type.getClass())) {
            returnVar = parseBaseEntity(value, type.getClass());
        }
        return returnVar;
    }

    /**
     * Parses a simple value
     *
     * @param value the value
     * @param type  the value type
     * @return parsed result
     */
    private Object parseValue(final Object value, final ResolvableType type) {
        Object returnVar = null;
        if (isTypeStandard(type.getRawClass())) {
            returnVar = value;
        } else if (isValueCSVDatabaseIndexes(type)) {
            returnVar = parseCSVDatabaseIndexes(value.toString(), type.getGeneric(0).getRawClass());
        } else if (isValueDateTime(value, type)) {
            returnVar = parseDateTime(value);
        } else if (isValueDatabaseIndex(type)) {
            returnVar = parseDatabaseIndex(value.toString(), type.getRawClass());
        } else if (isValuePeriod(type)) {
            returnVar = parsePeriod(value.toString());
        } else if (isValueEnum(type)) {
            returnVar = parseEnum(value.toString(), type.getRawClass());
        }
        return returnVar;
    }

    /**
     * Parse Period
     *
     * @param value value to parse
     * @return new Period
     */
    private Period parsePeriod(final String value) {
        return Period.seconds(Integer.parseInt(value));
    }

    /**
     * Parse enum
     *
     * @param value value of enum
     * @param type  type of enum
     * @return enum index
     */
    @SuppressWarnings("unchecked")
    private Object parseEnum(final String value, final Class type) {
        return Enum.valueOf(type, value);
    }

    /**
     * Parse Database Index
     * Retrieves the object from the database so we can save the object to the entity
     *
     * @param value           entity id
     * @param repositoryClass repository of entity
     * @return the entity
     */
    private Object parseDatabaseIndex(final String value, final Class repositoryClass) {
        final JpaRepository repository = getFieldRepository(repositoryClass);
        return repository.findOne(value);
    }

    /**
     * Parse JODA DateTime
     *
     * @param value joda datetime value
     * @return new Joda DateTime
     */
    private DateTime parseDateTime(final Object value) {
        return DateTime.parse(value.toString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Parses a comma separated value of database indexes.
     * Retrieves all the items from the database and adds them to an array to save them to the object
     *
     * @param value           CSV of indexes
     * @param repositoryClass repository of entities
     * @return array of entities
     */
    private Collection<BaseEntity> parseCSVDatabaseIndexes(final String value, final Class repositoryClass) {
        final Set<BaseEntity> elements = new HashSet<>();
        final JpaRepository repository = getFieldRepository(repositoryClass);
        Arrays.stream(value.split(","))
                .distinct()
                .filter(Objects::nonNull)
                .filter(UUIDConverter::isUUID)
                .forEach(id -> elements.add((BaseEntity) repository.getOne(id)));
        return elements;
    }

    /**
     * Finds the JpaRepository for a class
     *
     * @param entityType - Class we want to find a JpaRepository for
     * @return field repository
     */
    private JpaRepository getFieldRepository(final Class<?> entityType) {
        final Class repositoryClass = entityType.getAnnotation(SchemaRepository.class).value();
        return (JpaRepository) appContext.getBean(repositoryClass);
    }

    /**
     * For some reason I cannot use Class.getField, as it does not do anything.
     * This method loops over DeclaredFields of the object and all its parents
     *
     * @param objectClass - The object we are searching in
     * @param fieldName   - The fields name we are searching for
     * @return - The field
     */
    private Optional<Field> getField(final Class objectClass, final String fieldName) {
        Class currentClass = objectClass;
        final Mutable<Field> field = Mutable.of(null);
        do {
            final Field foundField = Arrays.stream(currentClass.getDeclaredFields())
                    .filter(e -> e.getName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            field.mutateIf(foundField, foundField != null);

            currentClass = currentClass.getSuperclass();
        } while (currentClass.getSuperclass() != null);

        if (!field.isPresent()) {
            logger.info("[FormParse] [getField] Unable To Find Field", new NoSuchFieldException(fieldName));
        }

        return field.ofNullable();
    }

    /**
     * Find the fields setter method. This is a simple search and it looks for get plus the variable name capitalised.
     * For example
     * name     => setName
     * bigName  => setBigName
     *
     * @param objectClass - The object we are searching in
     * @param fieldName   - The name of the field we want to find the setter for
     * @param fieldType   the field type
     * @return - The setter method
     * @throws NoSuchMethodException the no such method exception
     */
    private Method getFieldSetter(final Class<?> objectClass,
                                  final String fieldName,
                                  final Class fieldType) throws NoSuchMethodException {
        return objectClass.getMethod("set" + StringUtils.capitalize(fieldName), fieldType);
    }

    /**
     * When checking for a fields setter, we need to specify the class of the variable we expect the setter to
     * take, so we know if it will accept the variable we expect it to.
     * Certain setters use different variable types in setting than the actual variables type.
     * For example, DateTime sets its variables with strings rather than DateTime.
     *
     * @param fieldType - The type of variable of the field
     * @return - The type of variable we will use in the setter
     */
    private Class<?> getFieldSetterType(final Class<?> fieldType) {
        Class<?> returnClass = fieldType;

        if (fieldType.isAssignableFrom(DateTime.class)) {
            returnClass = String.class;
        }

        return returnClass;
    }

    /**
     * Returns whether or not a field has a getter
     *
     * @param objectClass - The object we are searching in
     * @param fieldName   - The name of the field we want to find the getter for
     * @return boolean boolean
     */
    private Boolean doesGetterExist(final Class<?> objectClass, final String fieldName) {
        try {
            getFieldGetter(objectClass, fieldName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Find the fields getter method. This is a simple search and it looks for get plus the variable name capitalised.
     * For example
     * name     => getName
     * bigName  => getBigName
     *
     * @param objectClass - The object we are searching in
     * @param fieldName   - The name of the field we want to find the getter for
     * @return - The getter method
     * @throws NoSuchMethodException the no such method exception
     */
    private Method getFieldGetter(final Class<?> objectClass, final String fieldName) throws NoSuchMethodException {
        return objectClass.getMethod("get" + StringUtils.capitalize(fieldName));
    }

    /**
     * Returns whether or not a field has a setter
     *
     * @param objectClass - The object we are searching in
     * @param fieldName   - The name of the field we want to find the setter for
     * @param fieldType   the field type
     * @return boolean boolean
     */
    private Boolean doesSetterExist(final Class<?> objectClass, final String fieldName, final Class fieldType) {
        try {
            // This intentionally doesn't immediately return, so the try can trip without returning
            getFieldSetter(objectClass, fieldName, getFieldSetterType(fieldType));
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
