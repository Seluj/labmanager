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

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;

import java.lang.ref.SoftReference;

/**
 * An abstract vertical layout that has an attached logger.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLoggerVerticalLayout extends VerticalLayout {

    private static final long serialVersionUID = 8762944329088364763L;

    private final ContextualLoggerFactory loggerFactory;

    private SoftReference<Logger> logger;

    /**
     * Constructor.
     *
     * @param loggerFactory the factory to be used for the composite logger.
     */
    public AbstractLoggerVerticalLayout(ContextualLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    /**
     * Replies the logger than should be used by this component.
     *
     * @return the logger, never {@code null}.
     */
    public synchronized Logger getLogger() {
        var log = this.logger == null ? null : this.logger.get();
        if (log == null) {
            log = this.loggerFactory.getLogger(getClass().getName(), AbstractLoggerComposite.getAuthenticatedUserName());
            this.logger = new SoftReference<>(log);
        }
        return log;
    }

}
