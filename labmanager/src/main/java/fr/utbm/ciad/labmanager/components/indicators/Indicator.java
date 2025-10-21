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
import fr.utbm.ciad.labmanager.utils.Unit;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A computed value that indicates a key element for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public interface Indicator {

    /**
     * Clear any buffered value.
     */
    void clear();

    /**
     * Replies the key that identify the the indicator.
     *
     * @return the name.
     */
    String getKey();

    /**
     * Replies the name of the indicator.
     *
     * @param locale the locale in which the label must be replied.
     * @return the name.
     */
    String getName(Locale locale);

    /**
     * Replies the details of the computation of the indicator.
     * The details provides information about how the computation was done for the indicator.
     *
     * @return the details.
     * @since 2.4
     */
    String getComputationDetails();

    /**
     * Replies the label associated to the indicator.
     * The label describes the meaning of the indicator.
     * This label may be adapted to the associated value that is
     * provided as argument.
     *
     * @param unit   the unit to be used for displaying the value.
     * @param locale the locale in which the label must be replied.
     * @return the label.
     */
    String getLabel(Unit unit, Locale locale);

    /**
     * Replies the start of the reference period for this indicator.
     *
     * @return the start date.
     */
    LocalDate getReferencePeriodStart();

    /**
     * Replies the end of the reference period for this indicator.
     *
     * @return the end date.
     */
    LocalDate getReferencePeriodEnd();

    /**
     * Replies the value of the indicator in the form of a number. The unit of the replied value is
     * provided by {@link #getValueUnit()}.
     *
     * @param organization the organization for which the indicator should be computed.
     * @param logger       the logger to use for put a message in the log.
     * @return the number value, or {@code null} if the indicator does not compute a numeric value.
     * @see #getValueUnit()
     */
    Number getNumericValue(ResearchOrganization organization, Logger logger);

}
