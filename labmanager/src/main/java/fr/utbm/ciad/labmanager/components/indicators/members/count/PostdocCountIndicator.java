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
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Count the current number of Postdocs in a specific organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.3
 */
@Component
public class PostdocCountIndicator extends AbstractInstantIndicator {

    private static final long serialVersionUID = -8488463133479874269L;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public PostdocCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants) {
        super(messages, constants);
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "postdocCountIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithoutYears(locale, "postdocCountIndicator.label"); //$NON-NLS-1$
    }

    @Override
    protected Number computeValue(ResearchOrganization organization) {
        final var postdocs = organization.getDirectOrganizationMemberships().parallelStream().filter(
                        it -> it.isActive() && it.getMemberStatus() == MemberStatus.POSTDOC)
                .collect(Collectors.toList());
        final var nb = postdocs.size();
        setComputationDetails(postdocs, it -> it.getPerson().getFullNameWithLastNameFirst());
        return Long.valueOf(nb);
    }

}
