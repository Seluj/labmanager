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

package fr.utbm.ciad.labmanager.utils;

import java.util.Objects;

/**
 * Utilities for computing a hash code.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public final class HashCodeUtils {

    private final static int INIT = 1;

    private final static int PRIME = 31;

    private HashCodeUtils() {
        //
    }

    /**
     * Start hash code.
     *
     * @return the hash code.
     */
    public static int start() {
        return INIT;
    }

    /**
     * Add a byte field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, byte field) {
        return PRIME * h + Byte.hashCode(field);
    }

    /**
     * Add a short field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, short field) {
        return PRIME * h + Short.hashCode(field);
    }

    /**
     * Add an int field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, int field) {
        return PRIME * h + Integer.hashCode(field);
    }

    /**
     * Add a long field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, long field) {
        return PRIME * h + Long.hashCode(field);
    }

    /**
     * Add a float field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, float field) {
        return PRIME * h + Float.hashCode(field);
    }

    /**
     * Add a double field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, double field) {
        return PRIME * h + Double.hashCode(field);
    }

    /**
     * Add a boolean field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, boolean field) {
        return PRIME * h + Boolean.hashCode(field);
    }

    /**
     * Add an object field to the hash code.
     *
     * @param h     the hash code.
     * @param field the field value.
     * @return the new hash code.
     */
    public static int add(int h, Object field) {
        return PRIME * h + Objects.hashCode(field);
    }

}
