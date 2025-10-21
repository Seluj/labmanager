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

package fr.utbm.ciad.labmanager.views.components.teaching.editors;

import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.teaching.TeachingService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.teaching.editors.regular.EmbeddedTeachingActivityEditor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a teaching activity editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultTeachingActivityEditorFactory implements TeachingActivityEditorFactory {

    private final DownloadableFileManager fileManager;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final TeachingService teachingService;

    private final PersonFieldFactory personFieldFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param teachingService                the service for accessing the JPA entities for teaching activities.
     * @param activityCreationStatusComputer the tool for computer the creation status for the teaching activity.
     * @param fileManager                    the manager of the downloadable files.
     * @param personFieldFactory             the factory for creating the person fields.
     * @param userService                    the service for accessing the JPA entities for users.
     * @param organizationFieldFactory       the factory for creating the organization fields.
     * @param authenticatedUser              the connected user.
     * @param messages                       the accessor to the localized messages (Spring layer).
     */
    public DefaultTeachingActivityEditorFactory(
            @Autowired TeachingService teachingService,
            @Autowired DownloadableFileManager fileManager,
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired OrganizationFieldFactory organizationFieldFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.teachingService = teachingService;
        this.fileManager = fileManager;
        this.organizationFieldFactory = organizationFieldFactory;
        this.personFieldFactory = personFieldFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<TeachingActivity> createContextFor(TeachingActivity activity, Logger logger) {
        return this.teachingService.startEditing(activity, logger);
    }

    @Override
    public AbstractEntityEditor<TeachingActivity> createAdditionEditor(EntityEditingContext<TeachingActivity> context) {
        return new EmbeddedTeachingActivityEditor(context, null, this.fileManager, this.personFieldFactory,
                this.organizationFieldFactory, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<TeachingActivity> createUpdateEditor(EntityEditingContext<TeachingActivity> context) {
        return new EmbeddedTeachingActivityEditor(context, null, this.fileManager, this.personFieldFactory,
                this.organizationFieldFactory, this.authenticatedUser, this.messages);
    }

}
