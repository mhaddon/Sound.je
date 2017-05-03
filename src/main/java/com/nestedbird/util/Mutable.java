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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a container object which contains an immutable value.
 * The purpose of this class is that we can overwrite the value and treat it as if it is a mutable value.
 * This allows us to use and modify an external immutable and non-final variable from a lambda expression
 *
 * @param <T> type of the mutated value
 */
public class Mutable<T> {

    /**
     * Creates a new mutable object
     *
     * @param <T>     the type of the mutable object
     * @param initial the initial value
     * @return the mutable object
     */
    public static <T> Mutable<T> of(final T initial) {
        return new Mutable<>(initial);
    }

    /**
     * Creates a new empty mutable object.
     *
     * @param <T> type of the mutable object
     * @return the mutable object
     */
    public static <T> Mutable<T> empty() {
        return new Mutable<>();
    }

    /**
     * The value that we are mutating
     */
    private T value;

    private Mutable() {
    }

    private Mutable(final T initial) {
        this.value = initial;
    }

    /**
     * Mutate consumer.
     *
     * @param <C>   the type parameter
     * @param value the function that returns the value we want to mutate with
     * @return the consumer
     */
    public <C> Consumer<C> mutate(final Function<C, T> value) {
        return y -> mutate(value.apply(y));
    }

    /**
     * mutates the value inside this object
     *
     * @param override the new value
     */
    public void mutate(final T override) {
        this.value = override;
    }

    /**
     * Mutates the value if the conditional equals true
     *
     * @param supplier    supplier that returns the override value
     * @param conditional the result of the conditional
     */
    public void mutateIf(final Supplier<T> supplier, final boolean conditional) {
        if (conditional) {
            this.mutate(supplier.get());
        }
    }

    /**
     * Mutates the value if the conditional equals true
     *
     * @param value       supplier that returns the override value
     * @param conditional supplier that returns the conditional result
     */
    public void mutateIf(final Supplier<T> value, final Supplier<Boolean> conditional) {
        if (conditional.get()) {
            this.mutate(value.get());
        }
    }

    /**
     * Mutates the value if the conditional equals true
     *
     * @param <C>         the type parameter
     * @param value       function that returns the override value
     * @param conditional function that returns the conditional result
     * @return the consumer
     */
    public <C> Consumer<C> mutateIf(final Function<C, T> value, final Function<C, Boolean> conditional) {
        return y -> mutateIf(value.apply(y), conditional.apply(y));
    }

    /**
     * Mutates the value if the conditional equals true
     *
     * @param override    the value to override the value
     * @param conditional the result of the conditional
     */
    public void mutateIf(final T override, final boolean conditional) {
        if (conditional) {
            this.mutate(override);
        }
    }

    /**
     * Retrieves the value of this mutable object
     *
     * @return the value
     */
    public T get() {
        return value;
    }

    /**
     * Passes the value to an optional isPresent
     *
     * @return is this value null
     */
    public Boolean isPresent() {
        return Optional.ofNullable(value).isPresent();
    }

    /**
     * Returns the value of this mutable object wrapped in an Optional
     *
     * @return value wrapped with an optional
     */
    public Optional<T> ofNullable() {
        return Optional.ofNullable(value);
    }

    /**
     * Does the value of this mutable object equal null
     *
     * @return boolean boolean
     */
    public Boolean isNull() {
        return value == null;
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Indicates whether some other object is "equal to" this Mutable. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also a {@code Mutable} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Mutable)) {
            return false;
        }

        final Mutable<?> other = (Mutable<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns a non-empty string representation of this Mutable suitable for
     * debugging.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return String.format("Mutable[%s]", value.toString());
    }
}
