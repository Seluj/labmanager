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

package fr.utbm.ciad.labmanager.components.indicators.project.budget;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.project.ProjectCategory;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Sum the budgets of academic projects.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
public class AcademicProjectBudgetIndicator extends AbstractProjectBudgetIndicator {

    private static final long serialVersionUID = 3698699052178668817L;

    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param projectService the service for accessing the projects.
     */
    public AcademicProjectBudgetIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired ProjectService projectService) {
        super(messages, constants, projectService);
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "academicProjectBudgetIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithYears(locale, "academicProjectBudgetIndicator.label", unit.getLabel()); //$NON-NLS-1$
    }

    @Override
    public boolean isSelectableProject(Project project) {
        if (project != null) {
            final var cat = project.getCategory();
            return cat == ProjectCategory.COMPETITIVE_CALL_PROJECT || cat == ProjectCategory.AUTO_FUNDING;
        }
        return false;
    }

}
