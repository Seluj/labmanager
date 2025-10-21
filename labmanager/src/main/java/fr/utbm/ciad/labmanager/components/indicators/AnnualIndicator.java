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

package fr.utbm.ciad.labmanager.components.indicators;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;

import java.time.LocalDate;
import java.util.Map;

/**
 * A computed value that indicates a key element per year for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.3
 */
public interface AnnualIndicator extends Indicator {

    /**
     * Replies the number of years to be supported by default.
     *
     * @return the number of years to support.
     */
    int getYearCount();

    /**
     * Change the number of years to be supported by default.
     *
     * @param years the number of years to support.
     */
    void setYearCount(int years);

    /**
     * Replies the start year of the reference period for this indicator.
     *
     * @return the start year.
     */
    int getReferenceStartYear();

    /**
     * Change the start year of the reference period for this indicator.
     *
     * @param year the start year.
     */
    void setReferenceStartYear(int year);

    /**
     * Replies the end year of the reference period for this indicator.
     *
     * @return the end year.
     */
    default int getReferenceEndYear() {
        return getReferenceStartYear() + getYearCount() - 1;
    }

    @Override
    default LocalDate getReferencePeriodStart() {
        return LocalDate.of(getReferenceStartYear(), 1, 1);
    }

    @Override
    default LocalDate getReferencePeriodEnd() {
        return LocalDate.of(getReferenceEndYear(), 12, 31);
    }

    /**
     * Replies the values for the given time window.
     *
     * @param organization the organization for which indicators should be computed.
     * @param startYear    the first year to consider.
     * @param endYear      the last year to consider.
     * @return the values per year.
     */
    Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear);

    /**
     * Replies the values for the given time window.
     *
     * @param organization the organization for which indicators should be computed.
     * @param startYear    the first year to consider.
     * @param endYear      the last year to consider.
     * @return the values per year.
     */
    default Map<Integer, Number> getValuesPerYear(ResearchOrganization organization) {
        return getValuesPerYear(organization, getReferenceStartYear(), getReferenceEndYear());
    }

}
