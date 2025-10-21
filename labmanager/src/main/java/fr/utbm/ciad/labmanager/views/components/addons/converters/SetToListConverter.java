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

package fr.utbm.ciad.labmanager.views.components.addons.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A converter from {@code Set} in presentation to {@code List} in model.
 *
 * @param <T> the type of data in the collections.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class SetToListConverter<T> implements Converter<Set<T>, List<T>> {

    private static final long serialVersionUID = 5691670818689027428L;

    @Override
    public Result<List<T>> convertToModel(Set<T> value, ValueContext context) {
        if (value == null) {
            return Result.ok(new ArrayList<>());
        }
        return Result.ok(value.stream().toList());
    }

    @Override
    public Set<T> convertToPresentation(List<T> value, ValueContext context) {
        if (value == null) {
            return new HashSet<>();
        }
        return value.stream().collect(Collectors.toSet());
    }

}
