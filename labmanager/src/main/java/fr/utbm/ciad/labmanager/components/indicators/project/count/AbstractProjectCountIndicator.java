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

package fr.utbm.ciad.labmanager.components.indicators.project.count;

import fr.utbm.ciad.labmanager.components.indicators.AbstractAnnualIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Count the number of projects for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public abstract class AbstractProjectCountIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = -425642005205521686L;

    private final ProjectService projectService;

    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param projectService the service for accessing the projects.
     */
    public AbstractProjectCountIndicator(
            MessageSourceAccessor messages,
            ConfigurationConstants constants,
            ProjectService projectService) {
        this(messages, constants, AbstractAnnualIndicator::sum, projectService);
    }

    /**
     * Constructor.
     *
     * @param messages        the provider of messages.
     * @param constants       the accessor to the constants.
     * @param mergingFunction the function that should be used for merging the annual values.
     *                        If it is {@code null}, the {@link #sum(Map)} is used.
     * @param projectService  the service for accessing the projects.
     */
    public AbstractProjectCountIndicator(
            MessageSourceAccessor messages,
            ConfigurationConstants constants,
            Function<Map<Integer, Number>, Number> mergingFunction,
            ProjectService projectService) {
        super(messages, constants, mergingFunction);
        this.projectService = projectService;
    }

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var projects = this.projectService.getProjectsByOrganizationId(organization.getId());
        //
        var stream = filterByYearWindow(projects, it -> Integer.valueOf(it.getStartYear()));
        stream = stream.filter(it -> isCountableProject(it));
        //
        final Map<Integer, Number> projectsPerYear = stream.collect(Collectors.toConcurrentMap(
                it -> Integer.valueOf(it.getStartYear()),
                it -> Integer.valueOf(1),
                (a, b) -> Integer.valueOf(a.intValue() + b.intValue())));
        //
        setComputationDetails(projectsPerYear);
        return projectsPerYear;
    }

    /**
     * Replies if the given project category is valid for being counted..
     *
     * @param project the project to test.
     * @return {@code true} if the given project is valid.
     */
    public abstract boolean isCountableProject(Project project);

}
