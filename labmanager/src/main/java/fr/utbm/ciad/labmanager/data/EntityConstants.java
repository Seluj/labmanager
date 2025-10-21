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

/**
 * Tools and configuration for the JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class EntityConstants {

    /**
     * Size of {@code VARCHAR} fields for large texts.
     * By default, the length of columns is set to 255. This value
     * permits to override this size for string-based columns.
     * <p>This value is defined in order to be database independent for most of the {@code VARCHAR} specifications.
     */
    public static final int LARGE_TEXT_SIZE = 32672;

    /**
     * Size of {@code VARCHAR} fields for very small texts.
     * By default, the length of columns is set to 32. This value
     * permits to override this size for string-based columns.
     * <p>This value is defined in order to be database independent for most of the {@code VARCHAR} specifications.
     */
    public static final int VERY_SMALL_TEXT_SIZE = 32;

    /**
     * Default separator between the acronym and name.
     *
     * @see #getAcronymAndName()
     * @since 4.0
     */
    public static final String ACRONYM_NAME_SEPARATOR = "-"; //$NON-NLS-1$

    /**
     * Default separation string between the acronym and name. This string contains the {@link #ACRONYM_NAME_SEPARATOR} with white spaces before and after it.
     *
     * @since 4.0
     */
    public static final String FULL_ACRONYM_NAME_SEPARATOR = " " + ACRONYM_NAME_SEPARATOR + " "; //$NON-NLS-1$ //$NON-NLS-2$

    private EntityConstants() {
        //
    }

}
