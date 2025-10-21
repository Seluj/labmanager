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
 * Count the number of current PhD students in a specific organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Component
public class PhdStudentCountIndicator extends AbstractInstantIndicator {

    private static final long serialVersionUID = -992179194048892979L;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public PhdStudentCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants) {
        super(messages, constants);
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "phdStudentCountIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithoutYears(locale, "phdStudentCountIndicator.label"); //$NON-NLS-1$
    }

    @Override
    protected Number computeValue(ResearchOrganization organization) {
        final var students = organization.getDirectOrganizationMemberships().parallelStream().filter(
                        it -> it.isActive() && it.getMemberStatus() == MemberStatus.PHD_STUDENT)
                .collect(Collectors.toList());
        final var nb = students.size();
        setComputationDetails(students, it -> it.getPerson().getFullNameWithLastNameFirst());
        return Long.valueOf(nb);
    }

}
