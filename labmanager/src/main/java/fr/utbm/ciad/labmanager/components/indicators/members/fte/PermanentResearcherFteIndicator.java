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

package fr.utbm.ciad.labmanager.components.indicators.members.fte;

import fr.utbm.ciad.labmanager.components.indicators.AbstractAnnualIndicator;
import fr.utbm.ciad.labmanager.components.indicators.members.count.ResearcherCountIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Count the number of research full-time equivalents per year.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see ResearcherCountIndicator
 * @since 2.2
 */
@Component
public class PermanentResearcherFteIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = 8512865687681055729L;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public PermanentResearcherFteIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants) {
        super(messages, constants, AbstractAnnualIndicator::average);
    }

    /**
     * Replies if the given membership is for a permanent researcher.
     *
     * @param membership the membership to test.
     * @param startDate  the start date of the active period.
     * @param endDate    the end date of the active period.
     * @return {@code true} if the membership is for a permanent researcher.
     */
    public static boolean isPermanentResearcher(Membership membership, LocalDate startDate, LocalDate endDate) {
        if (membership != null && membership.isActiveIn(startDate, endDate) && membership.isPermanentPosition()) {
            final var status = membership.getMemberStatus();
            return !status.isExternalPosition() && status != MemberStatus.PHD_STUDENT
                    && status.isResearcher() && status.isPermanentPositionAllowed();
        }
        return false;
    }

    private static void updateValues(Map<Integer, Number> values, Membership membership, int startYear, int endYear) {
        final var fte = membership.getMemberStatus().getUsualResearchFullTimeEquivalent();
        for (var year = startYear; year <= endYear; ++year) {
            final var dayCount = membership.daysInYear(year);
            float annualFte = dayCount / LocalDate.of(year, 1, 1).lengthOfYear();
            annualFte *= fte;
            values.merge(Integer.valueOf(year), Float.valueOf(annualFte),
                    (k0, k1) -> {
                        final var sum = k0.floatValue() + k1.floatValue();
                        return Float.valueOf(sum);
                    });
        }
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "permanentResearcherFteIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithoutYears(locale, "permanentResearcherFteIndicator.label"); //$NON-NLS-1$
    }

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var startDate = LocalDate.of(startYear, 1, 1);
        final var endDate = LocalDate.of(endYear, 12, 31);
        final var values = new ConcurrentHashMap<Integer, Number>();
        organization.getDirectOrganizationMemberships()
                .parallelStream()
                .filter(it -> isPermanentResearcher(it, startDate, endDate))
                .forEach(it -> {
                    updateValues(values, it, startYear, endYear);
                });
        //
        setComputationDetails(values);
        return values;
    }

}
