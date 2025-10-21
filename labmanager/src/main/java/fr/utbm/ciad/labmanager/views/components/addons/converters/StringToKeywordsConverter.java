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

import java.util.regex.Pattern;

/**
 * A converter that is removing the spaces at the ends of the input string and replacing any keyword separator by a coma character.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StringToKeywordsConverter implements Converter<String, String> {

    private static final long serialVersionUID = 2062988250883892074L;
    private static final String DEFAULT_SEPARATOR = ","; //$NON-NLS-1$
    private static final Pattern SEPARATORS = Pattern.compile("\\s*[,;:_.]\\s*"); //$NON-NLS-1$

    @Override
    public Result<String> convertToModel(String value, ValueContext context) {
        if (value == null) {
            return Result.ok(""); //$NON-NLS-1$
        }
        final var matcher = SEPARATORS.matcher(value.trim());
        final var result = matcher.replaceAll(DEFAULT_SEPARATOR + " "); //$NON-NLS-1$
        return Result.ok(result);
    }

    @Override
    public String convertToPresentation(String value, ValueContext context) {
        if (value == null) {
            return ""; //$NON-NLS-1$
        }
        return value.trim();
    }

}
