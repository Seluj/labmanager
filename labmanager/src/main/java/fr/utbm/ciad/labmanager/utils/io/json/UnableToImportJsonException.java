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

package fr.utbm.ciad.labmanager.utils.io.json;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Exception thrown when it is impossible to import a JSON.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class UnableToImportJsonException extends Exception {

    private static final long serialVersionUID = 8962180458951007213L;

    /**
     * Constructor.
     *
     * @param mainKey    the name of the main key in the JSON for which the exception is thrown.
     * @param elementIdx the index of the element in the main key for which the exception is thrown
     * @param source     the source object that corresponds to the data extract from the JSON.
     * @param cause      the original cause.
     */
    public UnableToImportJsonException(String mainKey, int elementIdx, Object source, Throwable cause) {
        super(buildMessage(mainKey, elementIdx, source), cause);
    }

    private static String buildMessage(String mainKey, int elementIdx, Object source) {
        final var msg = new StringBuilder();
        msg.append("Unable to import JSON data in "); //$NON-NLS-1$
        msg.append(mainKey);
        msg.append("["); //$NON-NLS-1$
        msg.append(elementIdx);
        msg.append("]"); //$NON-NLS-1$
        try {
            final var mapper = JsonUtils.createMapper();
            String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);
            msg.append(" = "); //$NON-NLS-1$
            msg.append(jsonResult);
        } catch (JsonProcessingException ex) {
            //
        }
        return msg.toString();
    }

}
