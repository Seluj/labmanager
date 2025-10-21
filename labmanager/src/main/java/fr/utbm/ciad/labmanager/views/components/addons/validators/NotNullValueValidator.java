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
import com.vaadin.flow.data.validator.AbstractValidator;

/**
 * A validator that matches not {@code null} value.
 *
 * @param <T> the type of the value to be tested.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class NotNullValueValidator<T> extends AbstractValidator<T> {

    private static final long serialVersionUID = 7471157821901818568L;

    /**
     * Constructor.
     *
     * @param errorMessage the message to display in case the value does not validate. Parameter {@code {0}} is replaced by the invalid entity in the message.
     */
    public NotNullValueValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String toString() {
        return "NotNullValueValidator"; //$NON-NLS-1$
    }

    /**
     * Replies if the given value is valid or not.
     *
     * @param value
     * @return
     */
    protected boolean isValid(T value) {
        return value != null;
    }

    @Override
    public ValidationResult apply(T value, ValueContext context) {
        return toResult(value, isValid(value));
    }

}
