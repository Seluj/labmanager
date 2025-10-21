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

package fr.utbm.ciad.labmanager.data;

import java.io.Serializable;

/**
 * Interface that represents a set of ranking indicators.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface QualityAnnualIndicators extends Serializable, IdentifiableEntity {

    /**
     * Replies the year for this history entry.
     *
     * @return the year.
     */
    int getReferenceYear();

    /**
     * Change the year for this history entry.
     *
     * @param year the year.
     */
    void setReferenceYear(int year);

    /**
     * Change the year for this history entry.
     *
     * @param year the year.
     */
    default void setReferenceYear(Number year) {
        if (year == null) {
            setReferenceYear(0);
        } else {
            setReferenceYear(year.intValue());
        }
    }

    /**
     * Replies at least one quality indicator is defined inside this object.
     * The indicators may be quartile, impact factor or CORE index depending on
     * the implementation of this collection of indicators.
     *
     * @return {@code true} if one indicator is provided. Otherwise {@code false}.
     * @since 4.0
     */
    boolean isSignificant();

}
