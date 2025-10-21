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

package fr.utbm.ciad.labmanager.views.components.assocstructures.fields;

import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory for building the fields related to the associated structures.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultAssociatedStructureFieldFactory implements AssociatedStructureFieldFactory {

    private final PersonFieldFactory personFieldFactory;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param personFieldFactory       the factory for creating the person fields.
     * @param organizationFieldFactory the factory for creating the organization fields.
     * @param messages                 accessor to the localized messages.
     */
    public DefaultAssociatedStructureFieldFactory(
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired OrganizationFieldFactory organizationFieldFactory,
            @Autowired MessageSourceAccessor messages) {
        this.personFieldFactory = personFieldFactory;
        this.organizationFieldFactory = organizationFieldFactory;
        this.messages = messages;
    }

    @Override
    public AssociatedStructureHolderListGridField createHolderField(Logger logger) {
        return new AssociatedStructureHolderListGridField(this.personFieldFactory, this.organizationFieldFactory, this.messages,
                logger);
    }

}
