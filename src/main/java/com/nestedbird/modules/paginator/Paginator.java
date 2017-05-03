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

package com.nestedbird.modules.paginator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Paginator.
 *
 * @param <T> the type parameter
 */
public class Paginator<T> {
    /**
     * Of paginator.
     *
     * @param <T>      the type parameter
     * @param pageable the pageable
     * @return the paginator
     */
    public static <T> Paginator<T> of(final Pageable pageable) {
        return new Paginator<>(pageable);
    }

    private final Pageable pageable;

    /**
     * Instantiates a new Paginator.
     *
     * @param pageable the pageable
     */
    public Paginator(final Pageable pageable) {
        this.pageable = pageable;
    }

    /**
     * This paginates the contents in the list
     *
     * @param list the list of data we are paginating
     * @return the page
     */
    public Page<T> paginate(final List<T> list) {
        final int page = pageable.getPageNumber();
        final int count = pageable.getPageSize();

        final int fromIndex = page * count;
        final int toIndex = ((page + 1) * count) - 1;

        return new PageImpl<>(truncateList(list, fromIndex, toIndex), pageable, list.size());
    }

    /**
     * Safely truncate a list
     *
     * @param list      list to truncate
     * @param fromIndex index to truncate from
     * @param toIndex   index to truncate to
     * @return new list
     */
    private List<T> truncateList(final List<T> list,
                                 final int fromIndex,
                                 final int toIndex) {
        final List<T> clonedList = cloneList(list);
        int newToIndex = toIndex;

        if (newToIndex > clonedList.size()) {
            newToIndex = clonedList.size();
        }

        return (fromIndex >= 0 &&
                newToIndex >= 0 &&
                fromIndex <= newToIndex &&
                fromIndex <= clonedList.size()) ? clonedList.subList(fromIndex, newToIndex) : new ArrayList<>();
    }

    /**
     * Clone a list
     *
     * @param list list to clone
     * @return new list
     */
    private List<T> cloneList(final List<T> list) {
        return !list.isEmpty() ? ((List) ((ArrayList) list).clone()) : new ArrayList<>();
    }
}
