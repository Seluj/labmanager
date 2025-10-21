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

import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.function.ThrowingConsumer;
import org.springframework.util.function.ThrowingFunction;

/**
 * Abstract implementation of a Spring boot service.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractService extends AbstractComponent {

    private static final long serialVersionUID = -9185252700109170262L;

    private final SessionFactory sessionFactory;

    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param sessionFactory the factory of JPA session.
     * @since 4.0
     */
    public AbstractService(MessageSourceAccessor messages, ConfigurationConstants constants, SessionFactory sessionFactory) {
        super(messages, constants);
        this.sessionFactory = sessionFactory;
    }

    /**
     * Run the provided code in a JPA session.
     *
     * @param code the code to run. It takes the session as argument.
     * @since 4.0
     */
    @SuppressWarnings("resource")
    public void inSession(ThrowingConsumer<Session> code) {
        Session currentSession = null;
        try {
            currentSession = this.sessionFactory.getCurrentSession();
        } catch (Throwable ex) {
            //
        }
        if (currentSession == null) {
            try (final var session = this.sessionFactory.openSession()) {
                code.accept(session);
            }
        } else {
            code.accept(currentSession);
        }
    }

    /**
     * Run the provided code in a JPA session.
     *
     * @param code the code to run. It takes the session as argument.
     * @return the computed value.
     * @since 4.0
     */
    @SuppressWarnings("resource")
    public <T> T inSessionWithResult(ThrowingFunction<Session, T> code) {
        Session currentSession = null;
        try {
            currentSession = this.sessionFactory.getCurrentSession();
        } catch (Throwable ex) {
            //
        }
        if (currentSession == null) {
            try (final var session = this.sessionFactory.openSession()) {
                return code.apply(session);
            }
        }
        return code.apply(currentSession);
    }

}
