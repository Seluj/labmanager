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
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.function.SerializableFunction;
import fr.utbm.ciad.labmanager.data.publication.Authorship;
import fr.utbm.ciad.labmanager.data.publication.comparators.AuthorshipComparator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A validator that matches an authorship that is not {@code null}, not equals to other entities of the same type and with valid rank.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AuthorshipSetValidator extends DisjointEntityIterableValidator<Authorship, Set<Authorship>> {

    private static final long serialVersionUID = 1521956677730670806L;

    private final SerializableFunction<Authorship, String> rankMessageProvider;

    /**
     * Constructor.
     *
     * @param nullErrorMessage the message to display in case the value does not validate because it is {@code null}. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param rankErrorMessage the message to display in case the value does not validate because its rank is invalid.
     */
    public AuthorshipSetValidator(String nullErrorMessage, String rankErrorMessage) {
        this(nullErrorMessage, null, rankErrorMessage, null);
    }

    /**
     * Constructor.
     *
     * @param nullErrorMessage     the message to display in case the value does not validate because it is {@code null}. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param disjointErrorMessage the message to display in case the value does not validate because it is equal to another entity of the same type. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @param rankErrorMessage     the message to display in case the value does not validate because its rank is invalid.
     * @param disjointValidator    dynamic function that replies if the given value is disjoint with other reference values.
     */
    public AuthorshipSetValidator(String nullErrorMessage, String disjointErrorMessage, String rankErrorMessage, Predicate<Authorship> disjointValidator) {
        super(nullErrorMessage, disjointErrorMessage, true, disjointValidator);
        this.rankMessageProvider = value -> disjointErrorMessage.replace("{0}", String.valueOf(value)); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return "AuthorshipSetValidator"; //$NON-NLS-1$
    }

    /**
     * Returns the error message for the given value when its rank is not valid.
     *
     * @param value an invalid value.
     * @return the formatted error message.
     */
    protected String getRankErrorMessage(Authorship value) {
        return this.rankMessageProvider.apply(value);
    }

    /**
     * Invoked to build the validation result when the authorship has not a valid rank value.
     *
     * @param value the value that is not valid.
     * @return the validation result.
     */
    protected ValidationResult toRankErrorResult(Authorship value) {
        return ValidationResult.error(getRankErrorMessage(value));
    }

    @Override
    public ValidationResult apply(Set<Authorship> value, ValueContext context) {
        if (value == null || value.isEmpty()) {
            return toNullErrorResult(null);
        }
        final var found = new HashSet<Authorship>();
        final var iterator = value.stream().sorted(AuthorshipComparator.DEFAULT).iterator();
        int order = -1;
        while (iterator.hasNext()) {
            final var innerValue = iterator.next();
            if (isNull(innerValue)) {
                return toNullErrorResult(innerValue);
            }
            if (!found.add(innerValue) || !isDisjoint(innerValue)) {
                return toDisjointErrorResult(innerValue);
            }
            final var currentOrder = innerValue.getAuthorRank();
            if (currentOrder <= order) {
                return toRankErrorResult(innerValue);
            }
            order = currentOrder;
        }
        return ValidationResult.ok();
    }

}
