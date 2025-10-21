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

import fr.utbm.ciad.labmanager.utils.country.CountryCode;

/**
 * A validator that matches a language that is not {@code null}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class LanguageValidator extends NotNullEnumerationValidator<CountryCode> {

    private static final long serialVersionUID = -8965164018157411102L;

    /**
     * Constructor.
     *
     * @param errorMessage the message to display in case the value does not validate. Parameter {@code {0}} is replaced by the invalid entity in the message.
     */
    public LanguageValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String toString() {
        return "LanguageValidator"; //$NON-NLS-1$
    }

    @Override
    protected boolean isValid(CountryCode value) {
        return super.isValid(value) && value.isLanguageSource();
    }

}
