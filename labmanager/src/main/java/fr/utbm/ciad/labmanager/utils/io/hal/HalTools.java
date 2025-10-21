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

package fr.utbm.ciad.labmanager.utils.io.hal;

import java.net.URL;

/**
 * Utilities for HAL identifiers.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
public interface HalTools {

    /**
     * Base URL for all HAL websites with trailing slash.
     *
     * @since 3.8
     */
    String HAL_URL_BASE = "https://hal.science/"; //$NON-NLS-1$

    /**
     * Replies the HAL number that is specified inside the given HAL URL.
     * A HAL URL is based on the standard {@link "https://hal.science"} to which
     * the HAL number is added.
     *
     * @param url the HAL URL.
     * @return the HAL number, never {@code null}.
     * @throws IllegalArgumentException if the given URL cannot be parsed.
     */
    String getHALNumberFromHALUrl(URL url);

    /**
     * Replies the HAL number that is specified inside the given HAL URL.
     * A HAL URL is based on the standard {@link "https://hal.science"} to which
     * the HAL number is added.
     *
     * @param url the HAL URL.
     * @return the HAL number, or {@code null} if the given url is not a valid HAL url.
     * @since 3.6
     */
    default String getHALNumberFromHALUrlOrNull(URL url) {
        try {
            return getHALNumberFromHALUrl(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the HAL number that is specified inside the given HAL URL.
     * A HAL URL is based on the standard {@link "https://hal.science"} to which
     * the HAL number is added.
     *
     * @param url the HAL URL.
     * @return the HAL number, never {@code null}.
     * @throws IllegalArgumentException if the given URL cannot be parsed.
     */
    String getHALNumberFromHALUrl(String url);

    /**
     * Replies the HAL number that is specified inside the given HAL URL.
     * A HAL URL is based on the standard {@link "https://hal.science"} to which
     * the HAL number is added.
     *
     * @param url the HAL URL.
     * @return the HAL number, or {@code null} if the given url is not a valid HAL url.
     */
    default String getHALNumberFromHALUrlOrNull(String url) {
        try {
            return getHALNumberFromHALUrl(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the HAL URL from the given HAL number.
     * A HAL URL is based on the standard {@link "https://hal.science"} to which
     * the HAL number is added.
     *
     * @param number the HAL number.
     * @return the HAL URL.
     */
    URL getHALUrlFromHALNumber(String number);

}
