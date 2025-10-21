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

package fr.utbm.ciad.labmanager.views.components.projects.fields;

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the supervisions.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultProjectFieldFactory implements ProjectFieldFactory {

    private final ProjectService projectService;

    private final PersonFieldFactory personFieldFactory;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param projectService     the service for accessing the project entities from the JPA database.
     * @param personFieldFactory the factory for creating the person fields.
     * @param messages           accessor to the localized messages.
     */
    public DefaultProjectFieldFactory(
            @Autowired ProjectService projectService,
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired MessageSourceAccessor messages) {
        this.projectService = projectService;
        this.personFieldFactory = personFieldFactory;
        this.messages = messages;
    }

    @Override
    public MultiProjectNameField createMultiProjectNameField(
            SerializableBiConsumer<Project, Consumer<Project>> creationWithUiCallback,
            SerializableBiConsumer<Project, Consumer<Project>> creationWithoutUiCallback,
            Consumer<Project> initializer) {
        return new MultiProjectNameField(this.projectService,
                creationWithUiCallback, creationWithoutUiCallback, initializer);
    }

    @Override
    public ProjectBudgetListGridField createProjectBudgetField() {
        return new ProjectBudgetListGridField(this.messages);
    }

    @Override
    public ProjectMemberListGridField createProjectMemberField(Logger logger) {
        return new ProjectMemberListGridField(this.personFieldFactory, this.messages, logger);
    }

}
