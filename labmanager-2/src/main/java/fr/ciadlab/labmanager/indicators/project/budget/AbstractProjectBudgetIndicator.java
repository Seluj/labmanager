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

package fr.ciadlab.labmanager.indicators.project.budget;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.entities.project.Project;
import fr.ciadlab.labmanager.indicators.AbstractAnnualIndicator;
import fr.ciadlab.labmanager.service.project.ProjectService;
import fr.ciadlab.labmanager.utils.Unit;
import org.springframework.context.support.MessageSourceAccessor;

/** Count the total budget of projects for an organization. The budget values are stored in k€ in the JPA entities.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public abstract class AbstractProjectBudgetIndicator extends AbstractAnnualIndicator {

	private ProjectService projectService;

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 * @param projectService the service for accessing the projects.
	 */
	public AbstractProjectBudgetIndicator(
			MessageSourceAccessor messages,
			Constants constants,
			ProjectService projectService) {
		this(messages, constants, AbstractAnnualIndicator::sum, projectService);
	}

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 * @param mergingFunction the function that should be used for merging the annual values.
	 *      If it is {@code null}, the {@link #sum(Map)} is used.
	 * @param projectService the service for accessing the projects.
	 */
	public AbstractProjectBudgetIndicator(
			MessageSourceAccessor messages,
			Constants constants,
			Function<Map<Integer, Number>, Number> mergingFunction,
			ProjectService projectService) {
		super(messages, constants, mergingFunction);
		this.projectService = projectService;
	}
	
	/** Replies the unit of the values that are stored in the JPA entities.
	 * By default, it is {@link Unit#KILO}. This function could be overridden
	 * by sub-classes.
	 * 
	 * @return the unit.
	 */
	@SuppressWarnings("static-method")
	protected Unit getValueUnitInJPA() {
		return Unit.KILO;
	}

	@Override
	public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
		final List<Project> projects = this.projectService.getProjectsByOrganizationId(organization.getId());
		//
		Stream<Project> stream = filterByYearWindow(projects, it -> Integer.valueOf(it.getStartYear()));
		stream = stream.filter(it -> isSelectableProject(it));
		//
		final Unit dataUnit = getValueUnitInJPA();
		final Map<Integer, Number> projectsPerYear = stream.collect(Collectors.toConcurrentMap(
				it -> Integer.valueOf(it.getStartYear()),
				it -> Double.valueOf(dataUnit.convertToUnit(it.getTotalLocalOrganizationBudget())),
				(a, b) -> Double.valueOf(a.doubleValue() + b.doubleValue())));
		//
		setComputationDetails(projectsPerYear);
		return projectsPerYear;
	}

	/** Replies if the given project category is valid for being selected.
	 *
	 * @param project the project to test.
	 * @return {@code true} if the given project is valid. 
	 */
	public abstract boolean isSelectableProject(Project project);

}
