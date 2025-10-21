/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.addons.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.function.SerializableFunction;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;

import java.util.HashSet;
import java.util.function.Predicate;

/**
 * A validator that matches an entity that is not {@code null} and not equals to other entities of the same type.
 *
 * @param <T> the type of entities
 * @param <I> the type of entity collection
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DisjointEntityIterableValidator<T extends IdentifiableEntity, I extends Iterable<T>> implements Validator<I> {

    private static final long serialVersionUID = -1323939037664923790L;

    private final SerializableFunction<T, String> nullMessageProvider;

    private final SerializableFunction<T, String> disjointMessageProvider;

    private final Predicate<T> disjointValidator;

    private final boolean emptyListIsNullError;

    /**
     * Constructor.
     *
     * @param nullErrorMessage     the message to display in case the value does not validate because it is {@code null}. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param disjointErrorMessage the message to display in case the value does not validate because it is equal to another entity of the same type. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param emptyListIsNullError indicates if the empty list is assimilated to the null error.
     * @param disjointValidator    dynamic function that replies if the given value is disjoint with other reference values.
     */
    public DisjointEntityIterableValidator(String nullErrorMessage, String disjointErrorMessage, boolean emptyListIsNullError, Predicate<T> disjointValidator) {
        this.nullMessageProvider = value -> nullErrorMessage.replace("{0}", String.valueOf(value)); //$NON-NLS-1$
        this.disjointMessageProvider = value -> disjointErrorMessage.replace("{0}", String.valueOf(value)); //$NON-NLS-1$
        this.disjointValidator = disjointValidator;
        this.emptyListIsNullError = emptyListIsNullError;
    }

    /**
     * Constructor.
     *
     * @param nullErrorMessage     the message to display in case the value does not validate because it is {@code null}. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param disjointErrorMessage the message to display in case the value does not validate because it is equal to another entity of the same type. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param emptyListIsNullError indicates if the empty list is assimilated to the null error.
     */
    public DisjointEntityIterableValidator(String nullErrorMessage, String disjointErrorMessage, boolean emptyListIsNullError) {
        this(nullErrorMessage, disjointErrorMessage, emptyListIsNullError, null);
    }

    @Override
    public String toString() {
        return "DisjointEntityIterableValidator"; //$NON-NLS-1$
    }

    /**
     * Returns the error message for the given value when it is {@code null}.
     *
     * @param value an invalid value.
     * @return the formatted error message.
     */
    protected String getNullErrorMessage(T value) {
        return this.nullMessageProvider.apply(value);
    }

    /**
     * Returns the error message for the given value when it is not disjoint with other values.
     *
     * @param value an invalid value.
     * @return the formatted error message.
     */
    protected String getDisjointErrorMessage(T value) {
        return this.disjointMessageProvider.apply(value);
    }

    /**
     * Replies if the given value is {@code null}. If this function replies {@code true}, the "null" error message is returned.
     *
     * @param value the value to check.
     * @return {@code true} if the value is null.
     * @see #toNullErrorResult(IdentifiableEntity)
     */
    protected boolean isNull(T value) {
        return value == null;
    }

    /**
     * Replies if the given value is valid. If this function replies {@code false}, the "disjoint" error message is returned.
     * The value is assumed to be not null when this function is invoked.
     *
     * @param value the value to check.
     * @return {@code true} if the value is not disjoint.
     * #see #toDisjointErrorResult(IdentifiableEntity)
     */
    protected boolean isDisjoint(T value) {
        return this.disjointValidator == null || this.disjointValidator.test(value);
    }

    /**
     * Invoked to build the validation result when the value is {@code null}.
     *
     * @param value the value that is null.
     * @return the validation result.
     * @see #isNull(IdentifiableEntity)
     */
    protected ValidationResult toNullErrorResult(T value) {
        return ValidationResult.error(getNullErrorMessage(value));
    }

    /**
     * Invoked to build the validation result when the value is not disjoint.
     *
     * @param value the value that is not disjoint.
     * @return the validation result.
     * @see #isNotDisjoint(IdentifiableEntity)
     */
    protected ValidationResult toDisjointErrorResult(T value) {
        return ValidationResult.error(getDisjointErrorMessage(value));
    }

    @Override
    public ValidationResult apply(I value, ValueContext context) {
        if (value == null) {
            return toNullErrorResult(null);
        }
        final var iterator = value.iterator();
        if (this.emptyListIsNullError && !iterator.hasNext()) {
            return toNullErrorResult(null);
        }
        final var found = new HashSet<T>();
        while (iterator.hasNext()) {
            final var innerValue = iterator.next();
            if (isNull(innerValue)) {
                return toNullErrorResult(innerValue);
            }
            if (!found.add(innerValue) || !isDisjoint(innerValue)) {
                return toDisjointErrorResult(innerValue);
            }
        }
        return ValidationResult.ok();
    }

}
