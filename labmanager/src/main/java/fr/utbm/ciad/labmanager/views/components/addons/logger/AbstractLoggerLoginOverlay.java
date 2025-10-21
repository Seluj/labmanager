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

package fr.utbm.ciad.labmanager.views.components.addons.logger;

import com.vaadin.flow.component.login.LoginOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract login overlay that has an attached logger.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLoggerLoginOverlay extends LoginOverlay {

    private static final long serialVersionUID = 5543122807947052110L;

    /**
     * Replies the logger than should be used by this component.
     *
     * @return the logger, never {@code null}.
     */
    public synchronized Logger getLogger() {
        return LoggerFactory.getLogger(getClass().getName());
    }

}
