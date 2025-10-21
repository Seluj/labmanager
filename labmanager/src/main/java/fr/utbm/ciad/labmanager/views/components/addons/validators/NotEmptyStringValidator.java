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
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * A string validator that matches not empty strings.
 * This validator differs from {@link StringLengthValidator} in the
 * fact is it considered the trimed input string, i.e., the
 * input stream without the spaces at the ends of the string.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class NotEmptyStringValidator extends StringLengthValidator {

    private static final long serialVersionUID = -6883058042534909675L;

    private static final Integer ONE = Integer.valueOf(1);

    /**
     * Constructor.
     *
     * @param errorMessage the message to display in case the value does not validate. Parameter {@code {0}} is replaced by the invalid entity in the message.
     */
    public NotEmptyStringValidator(String errorMessage) {
        super(errorMessage, ONE, null);
    }

    @Override
    public String toString() {
        return "NotEmptyStringValidator"; //$NON-NLS-1$
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        final var trimedValue = value == null ? value : value.trim();
        return super.apply(trimedValue, context);
    }

}
