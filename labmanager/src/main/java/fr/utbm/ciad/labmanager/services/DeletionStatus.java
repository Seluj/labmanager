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

package fr.utbm.ciad.labmanager.services;

import org.springframework.context.support.MessageSourceAccessor;

import java.io.Serializable;
import java.util.Locale;

/**
 * Abstract implementation of a Spring boot service.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface DeletionStatus extends Serializable {

    /**
     * Default status for valid deletion.
     */
    DeletionStatus OK = new DeletionStatus() {

        private static final long serialVersionUID = -2185083045918120523L;

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public Throwable getException(MessageSourceAccessor messages, Locale locale) {
            return null;
        }

    };

    /**
     * Indicates if the deletion is possible without error.
     *
     * @return {@code true} if deletion is possible; {@code false}
     * if deletion is not possible.
     */
    boolean isOk();

    /**
     * If the status represents an error, the corresponding exception is replied.
     *
     * @param messages the provider of localized messages.
     * @param locale   the locale to use for generating the messages.
     * @return the error or {@code null} if the status represents a valid state.
     */
    Throwable getException(MessageSourceAccessor messages, Locale locale);

}
