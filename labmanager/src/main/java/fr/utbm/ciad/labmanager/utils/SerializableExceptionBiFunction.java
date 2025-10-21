/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
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

import java.io.Serializable;

/**
 * Two parameter function that is serializable and may throw an exception.
 *
 * @param <I0> the type of first input argument.
 * @param <I1> the type of second input argument.
 * @param <O>  the type of returned value.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@FunctionalInterface
public interface SerializableExceptionBiFunction<I0, I1, O> extends Serializable {

    /**
     * Run the function.
     *
     * @param input0 the first input argument.
     * @param input1 the second input argument.
     * @return the output value.
     * @throws Exception if there is some error inside.
     */
    O apply(I0 input0, I1 input1) throws Exception;

}
