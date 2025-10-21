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

package fr.utbm.ciad.labmanager.components.indicators.members.count;

import fr.utbm.ciad.labmanager.components.indicators.AbstractInstantIndicator;
import fr.utbm.ciad.labmanager.components.indicators.members.fte.PermanentResearcherFteIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Count the current number of permanent researchers in a specific organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see ResearcherCountIndicator
 * @see PermanentResearcherFteIndicator
 * @since 2.2
 */
@Component
public class PermanentResearcherCountIndicator extends AbstractInstantIndicator {

    private static final long serialVersionUID = -5012100767009218661L;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public PermanentResearcherCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants) {
        super(messages, constants);
    }

    /**
     * Replies if the given membership is for a permanent researcher.
     *
     * @param membership the membership to test.
     * @return {@code true} if the membership is for a permanent researcher.
     */
    public static boolean isPermanentResearcher(Membership membership) {
        if (membership != null && membership.isActive() && membership.isPermanentPosition()) {
            final var status = membership.getMemberStatus();
            return !status.isExternalPosition() && status != MemberStatus.PHD_STUDENT
                    && status.isResearcher() && status.isPermanentPositionAllowed();
        }
        return false;
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "permanentResearcherCountIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithoutYears(locale, "permanentResearcherCountIndicator.label"); //$NON-NLS-1$
    }

    @Override
    protected Number computeValue(ResearchOrganization organization) {
        final var researchers = organization.getDirectOrganizationMemberships()
                .parallelStream()
                .filter(PermanentResearcherCountIndicator::isPermanentResearcher)
                .collect(Collectors.toList());
        final var nb = researchers.size();
        setComputationDetails(researchers, it -> it.getPerson().getFullNameWithLastNameFirst());
        return Long.valueOf(nb);
    }

}
