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

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.server.VaadinRequest;
import org.slf4j.Logger;

import java.lang.ref.SoftReference;

/**
 * An abstract composite that has an attached logger.
 *
 * @param <T> the type of the inner component.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLoggerComposite<T extends Component> extends Composite<T> {

    private static final long serialVersionUID = 7245641998371360026L;

    private final ContextualLoggerFactory loggerFactory;

    private SoftReference<Logger> logger;

    /**
     * Constructor.
     *
     * @param loggerFactory the factory to be used for the composite logger.
     */
    public AbstractLoggerComposite(ContextualLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    /**
     * Replies the username of the authenticated user.
     *
     * @return the username or {@code null}.
     */
    public static String getAuthenticatedUserName() {
        final var request = VaadinRequest.getCurrent();
        if (request != null) {
            final var principal = request.getUserPrincipal();
            if (principal != null) {
                return Strings.emptyToNull(principal.getName());
            }
        }
        return null;
    }

    /**
     * Replies the logger than should be used by this component.
     *
     * @return the logger, never {@code null}.
     */
    public synchronized Logger getLogger() {
        var log = this.logger == null ? null : this.logger.get();
        if (log == null) {
            log = this.loggerFactory.getLogger(getClass().getName(), getAuthenticatedUserName());
            this.logger = new SoftReference<>(log);
        }
        return log;
    }

}
