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

import com.google.common.base.Strings;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;

import java.net.URI;

/**
 * A string validator for URL addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class UrlValidator extends AbstractValidator<String> {

    private static final long serialVersionUID = -5751450579330516322L;

    private final boolean allowEmptyValue;

    /**
     * Creates a validator for checking that a string is a syntactically valid
     * URL address.
     * <p>
     * This constructor creates a validator which doesn't accept an empty string
     * as a valid URL address. Use {@link #UrlValidator(String, boolean)}
     * constructor with {@code true} as a value for the second argument to
     * create a validator which not accepts an empty string.
     *
     * @param errorMessage the message to display in case the value does not validate. Parameter {@code {0}} is replaced by the invalid entity in the message.
     * @see #UrlValidator(String, boolean)
     */
    public UrlValidator(String errorMessage) {
        this(errorMessage, false);
    }

    /**
     * Creates a validator for checking that a string is a syntactically valid
     * URL address.
     *
     * @param errorMessage the message to display in case the value does not validate.
     * @param allowEmpty   if {@code true} then an empty string passes the validation,
     *                     otherwise the validation fails.
     */
    public UrlValidator(String errorMessage, boolean allowEmpty) {
        super(errorMessage);
        this.allowEmptyValue = allowEmpty;
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        return toResult(value, isValid(value));
    }

    @Override
    public String toString() {
        return "UrlValidatorValidator"; //$NON-NLS-1$
    }

    /**
     * Returns whether the given string matches the regular expression.
     *
     * @param value the string to match
     * @return true if the string matched, false otherwise
     */
    protected boolean isValid(String value) {
        if (Strings.isNullOrEmpty(value) || value.isBlank()) {
            return this.allowEmptyValue;
        }
        try {
            final var url = new URI(value.trim()).toURL();
            assert url != null;
            // URL was built, the format is good.
            return true;
        } catch (Throwable ex) {
            // Invalid format of the URL
            return false;
        }
    }

}
