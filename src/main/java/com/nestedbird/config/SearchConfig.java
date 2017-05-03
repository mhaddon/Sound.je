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

package com.nestedbird.config;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class is responsible for configuring the search
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class SearchConfig {
    /**
     * The Entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create search factory bean
     *
     * @return the search factory
     */
    @Bean
    public SearchFactory searchFactory() {
        return fullTextEntityManager().getSearchFactory();
    }

    /**
     * Create FullTextEntityManager bean
     *
     * @return the full text entity manager
     */
    @Bean
    public FullTextEntityManager fullTextEntityManager() {
        return org.hibernate.search.jpa.Search.
                getFullTextEntityManager(entityManager);
    }
}
