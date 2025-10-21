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

import java.net.URI;

/**
 * A converter that is removing the spaces at the ends of the input string and removing unnecessary components of a DOI number.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StringToDoiConverter implements Converter<String, String> {

    private static final long serialVersionUID = -4008986061392704710L;

    @Override
    public Result<String> convertToModel(String value, ValueContext context) {
        if (value == null) {
            return Result.ok(""); //$NON-NLS-1$
        }
        final var value0 = value.trim();
        var value1 = value0;
        try {
            final var url = new URI(value0).toURL();
            value1 = url.getPath();
            if (value1.startsWith("/")) { //$NON-NLS-1$
                value1 = value1.substring(1);
            }
        } catch (Throwable ex) {
            value1 = value0;
        }
        return Result.ok(value1);
    }

    @Override
    public String convertToPresentation(String value, ValueContext context) {
        if (value == null) {
            return ""; //$NON-NLS-1$
        }
        return value.trim();
    }

}
