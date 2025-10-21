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

package fr.utbm.ciad.labmanager.views.components.addons.details;

import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus.Status;
import com.vaadin.flow.data.binder.BindingValidationStatusHandler;

/**
 * Status handler that update the section marker in case of validation error.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DetailsWithErrorMarkStatusHandler implements BindingValidationStatusHandler {

    private static final long serialVersionUID = -7779834137714568018L;

    private final HasValidationProperties field;

    private final DetailsWithErrorMark section;

    /**
     * Constructor.
     *
     * @param field   the field to validate.
     * @param section the section in which the field is located.
     */
    public DetailsWithErrorMarkStatusHandler(HasValidationProperties field, DetailsWithErrorMark section) {
        this.field = field;
        this.section = section;
    }

    @Override
    public void statusChange(BindingValidationStatus<?> statusChange) {
        final var message = statusChange.getMessage().orElse(""); //$NON-NLS-1$
        // Use the default displaying of the status message
        this.field.setErrorMessage(message);
        this.field.setInvalid(statusChange.isError());
        // Update the section message if needed
        final var status = statusChange.getStatus();
        if (status == Status.OK || status == Status.ERROR) {
            if (this.section != null) {
                this.section.updateStatus(this.field, message, null);
            }
        }
    }

}
