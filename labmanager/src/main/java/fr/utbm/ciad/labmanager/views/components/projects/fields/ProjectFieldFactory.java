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
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the projects.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface ProjectFieldFactory {

    /**
     * Create a field for entering multiple project names.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new project using an UI, e.g., an editor. The first argument is the new project entity.
     *                                  The second argument is a lambda that must be invoked to inject the new project in the {@code MultiProjectNameField}.
     *                                  This second lambda takes the created project.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new project without using an UI. The first argument is the new project entity.
     *                                  The second argument is a lambda that must be invoked to inject the new project in the {@code MultiProjectNameField}.
     *                                  This second lambda takes the created project.
     * @param initializer               the initializer of the loaded projects. It may be {@code null}.
     * @return the field, never {@code null}.
     */
    MultiProjectNameField createMultiProjectNameField(
            SerializableBiConsumer<Project, Consumer<Project>> creationWithUiCallback,
            SerializableBiConsumer<Project, Consumer<Project>> creationWithoutUiCallback,
            Consumer<Project> initializer);

    /**
     * Create a field for project's budget to be used in the list grids.
     *
     * @return the field, never {@code null}.
     */
    ProjectBudgetListGridField createProjectBudgetField();

    /**
     * Create a field for project's members to be used in the list grids.
     *
     * @param logger the logger to be used by the component.
     * @return the field, never {@code null}.
     */
    ProjectMemberListGridField createProjectMemberField(Logger logger);

}
