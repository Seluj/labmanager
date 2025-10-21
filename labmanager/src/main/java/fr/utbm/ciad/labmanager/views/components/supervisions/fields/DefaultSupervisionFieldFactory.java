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

package fr.utbm.ciad.labmanager.views.components.supervisions.fields;

import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a publication editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultSupervisionFieldFactory implements SupervisionFieldFactory {

    private final PersonFieldFactory personFieldFactory;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param personFieldFactory the factory for creating the person fields.
     * @param messages           accessor to the localized messages.
     */
    public DefaultSupervisionFieldFactory(
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired MessageSourceAccessor messages) {
        this.personFieldFactory = personFieldFactory;
        this.messages = messages;
    }

    @Override
    public SupervisorListGridField createSupervisorField(Logger logger) {
        return new SupervisorListGridField(this.personFieldFactory, this.messages, logger);
    }

}
