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

package fr.ciadlab.labmanager.indicators.project.count;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.project.Project;
import fr.ciadlab.labmanager.entities.project.ProjectCategory;
import fr.ciadlab.labmanager.service.project.ProjectService;
import fr.ciadlab.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/** Count the number of projects with non-academic partners.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Component
public class IndustrialProjectCountIndicator extends AbstractProjectCountIndicator {

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 * @param projectService the service for accessing the projects.
	 */
	public IndustrialProjectCountIndicator(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired ProjectService projectService) {
		super(messages, constants, projectService);
	}

	@Override
	public String getName() {
		return getMessage("industrialProjectCountIndicator.name"); //$NON-NLS-1$;
	}

	@Override
	public String getLabel(Unit unit) {
		return getLabelWithYears("industrialProjectCountIndicator.label"); //$NON-NLS-1$;
	}

	@Override
	public boolean isCountableProject(Project project) {
		return project != null && project.getCategory() == ProjectCategory.NOT_ACADEMIC_PROJECT;
	}

}
