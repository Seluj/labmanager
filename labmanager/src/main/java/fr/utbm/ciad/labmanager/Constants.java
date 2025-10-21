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

package fr.utbm.ciad.labmanager;

/**
 * Constant definitions.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.8
 */
public final class Constants {

    /**
     * Major version number of the Lab Manager API.
     *
     * @since 4.0
     */
    public static final String MANAGER_MAJOR_VERSION = "4"; //$NON-NLS-1$

    /**
     * Minor version number of the Lab Manager API.
     *
     * @since 4.0
     */
    public static final String MANAGER_MINOR_VERSION = "0"; //$NON-NLS-1$

    /**
     * Micro version number of the Lab Manager API.
     *
     * @since 4.0
     */
    public static final String MANAGER_MICRO_VERSION = "0"; //$NON-NLS-1$

    /**
     * Version number of the Lab Manager API.
     */
    public static final String MANAGER_VERSION = MANAGER_MAJOR_VERSION + "." + MANAGER_MINOR_VERSION + "." + MANAGER_MICRO_VERSION; //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Build id that is also the date of the release.
     */
    public static final String MANAGER_BUILD_ID = "~"; //$NON-NLS-1$

    private Constants() {
        //
    }

}
