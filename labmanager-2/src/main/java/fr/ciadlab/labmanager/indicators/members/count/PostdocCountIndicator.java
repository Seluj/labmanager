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

package fr.ciadlab.labmanager.indicators.members.count;

import java.util.List;
import java.util.stream.Collectors;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.member.MemberStatus;
import fr.ciadlab.labmanager.entities.member.Membership;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.indicators.AbstractInstantIndicator;
import fr.ciadlab.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/** Count the current number of Postdocs in a specific organization.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.3
 */
@Component
public class PostdocCountIndicator extends AbstractInstantIndicator {

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 */
	public PostdocCountIndicator(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants) {
		super(messages, constants);
	}

	@Override
	public String getName() {
		return getMessage("postdocCountIndicator.name"); //$NON-NLS-1$
	}

	@Override
	public String getLabel(Unit unit) {
		return getLabelWithoutYears("postdocCountIndicator.label"); //$NON-NLS-1$
	}

	@Override
	protected Number computeValue(ResearchOrganization organization) {
		final List<Membership> postdocs = organization.getMemberships().parallelStream().filter(
				it -> it.isActive() && it.getMemberStatus() == MemberStatus.POSTDOC)
				.collect(Collectors.toList());
		final long nb = postdocs.size();
		setComputationDetails(postdocs, it -> it.getPerson().getFullNameWithLastNameFirst());
		return Long.valueOf(nb);
	}

}
