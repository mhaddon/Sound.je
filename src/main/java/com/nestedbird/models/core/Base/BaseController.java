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

package com.nestedbird.models.core.Base;

import com.nestedbird.modules.entitysearch.EntitySearch;
import com.nestedbird.modules.formparser.FormParse;
import com.nestedbird.modules.schema.SchemaElement;
import com.nestedbird.modules.schema.SchemaReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The Base Controller contains common endpoints for all BaseEntities.
 * To use the Base Controller a BaseEntities controller needs to extend it
 * The endpoints are relative to the url of the base entities controller
 *
 * @param <E> What type of base entity is this
 */
@RestController
public abstract class BaseController<E extends BaseEntity> {

    /**
     * This searches the lucene storage
     */
    private EntitySearch entitySearch;

    /**
     * Processes form submissions
     */
    private FormParse formParse;

    /**
     * Sets entity search.
     *
     * @param entitySearch the entity search
     */
    @Autowired
    public void setEntitySearch(final EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }

    /**
     * Sets form parse.
     *
     * @param formParse the form parse
     */
    @Autowired
    public void setFormParse(final FormParse formParse) {
        this.formParse = formParse;
    }

    /**
     * retrieves a specific BaseEntity
     *
     * @param id id of base entity
     * @return the base entity
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public E get(@PathVariable final String id) {
        return getRepository().findOne(id);
    }

    /**
     * This method returns the base entities repository.
     * This is meant to be overridden when this class is extended.
     *
     * @return the baseentities repository
     */
    public abstract BaseRepository<E> getRepository();

    /**
     * gets schema of BaseEntity
     *
     * @return baseentity schema
     */
    @RequestMapping(value = "schema", method = RequestMethod.GET)
    public List<SchemaElement> getSchema() {
        return SchemaReader.read(getEntityClass());
    }

    /**
     * This method returns the BaseEntities class.
     * This is meant to be overridden when this class is extended.
     *
     * @return the baseentities class
     */
    public abstract Class<E> getEntityClass();

    /**
     * retrieves all recorded base entity items in paginated format.
     *
     * @param pageable the pagination format
     * @return the base entities paginated
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Page<E> list(final Pageable pageable) {
        return getService().listAllByPage(pageable);
    }

    /**
     * This method returns the BaseEntities service.
     * This is meant to be overridden when this class is extended.
     *
     * @return the baseentities service
     */
    public abstract BaseService<E> getService();

    /**
     * searches the base entities, returns a paginated response
     *
     * @param queryText the lucene query text
     * @param pageable  the pagination format
     * @param sort      the sorting query
     * @return the searched response paginated
     * @throws ParseException the parse exception
     */
    @RequestMapping(value = "/", params = {"query"}, method = RequestMethod.GET)
    public Page<E> search(@RequestParam("query") final String queryText,
                          final Pageable pageable,
                          final Sort sort) throws ParseException {
        final List<E> results = entitySearch.searchOnlyReturnData(getEntityClass(), queryText);
        return entitySearch.paginate(results, pageable, sort);
    }

    /**
     * Create a new BaseEntity
     *
     * @param request the request
     * @return the new base entity
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     * @throws ClassNotFoundException the class not found exception
     */
    @RequestMapping(value = "/", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String create(final HttpServletRequest request) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        // todo change from toJson to Springs automatic encoding
        // the reason why it isnt at the moment is because of a lazy loading bug
        // even though the elements are not lazy loaded
        return processEntity(newObject(), request).toJSON();
    }

    /**
     * Processes an entity saving its information
     *
     * @param initialEntity starting object data
     * @param request       info to edit
     * @return updated entity
     */
    private E processEntity(final E initialEntity, final HttpServletRequest request) {
        final E parsedEntity = formParse.parse(initialEntity, request);
        return getRepository().saveAndFlush(parsedEntity);
    }

    /**
     * Creates a new object out of the currently defined class name
     *
     * @return the new object
     * @throws ClassNotFoundException the class not found exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     */
    @SuppressWarnings("unchecked")
    private E newObject() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (E) Class.forName(getEntityClass().getName()).newInstance();
    }

    /**
     * Update a base entity
     *
     * @param id      Id of the base entity
     * @param request the request
     * @return updated base entity
     */
    @RequestMapping(value = "{id}", method = RequestMethod.PUT,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String update(@PathVariable final String id, final HttpServletRequest request) {
        final E initialEntity = getRepository().findOne(id);

        // todo change from toJson to Springs automatic encoding
        // the reason why it isnt at the moment is because of a lazy loading bug
        // even though the elements are not lazy loaded
        return processEntity(initialEntity, request).toJSON();
    }

    /**
     * Delete base entity
     *
     * @param id id of the base entity
     * @return deleted base entity
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public E delete(@PathVariable final String id) {
        final E existingEntity = getRepository().findOne(id);
        getRepository().delete(existingEntity);
        return existingEntity;
    }
}
