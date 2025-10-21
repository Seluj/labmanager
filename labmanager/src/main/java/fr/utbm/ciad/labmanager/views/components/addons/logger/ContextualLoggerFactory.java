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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Logger factory that is creating a logger with the preferred configuration for keeping track of user activities in the app.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class ContextualLoggerFactory implements Serializable {

    private static final long serialVersionUID = -3702473399311388395L;

    /**
     * Create a logger.
     *
     * @param name     the name of the logger.
     * @param userName the user name to attached to the logger.
     * @return the logger.
     */
    @SuppressWarnings("static-method")
    public Logger getLogger(String name, String userName) {
        final var delegate = LoggerFactory.getLogger(name);
        return new AuthenticatedLogger(delegate, userName);
    }

    /**
         * Logger that add the user name of the authenticated user.
         *
         * @author $Author: sgalland$
         * @version $Name$ $Revision$ $Date$
         * @mavengroupid $GroupId$
         * @mavenartifactid $ArtifactId$
         * @since 4.0
         */
        private record AuthenticatedLogger(Logger delegate, String userName) implements Logger, Serializable {

            private static final long serialVersionUID = 5058143185884300143L;

        /**
         * Constructor.
         *
         * @param delegate the delegate logger.
         * @param userName the user name to attached to the logger.
         */
        private AuthenticatedLogger {
        }

            private String buildMessage(String message) {
                if (Strings.isNullOrEmpty(this.userName)) {
                    return message;
                }
                return "[user: " + this.userName + "]: " + message; //$NON-NLS-1$ //$NON-NLS-2$
            }

            @Override
            public boolean isTraceEnabled() {
                return this.delegate.isTraceEnabled();
            }

            @Override
            public boolean isDebugEnabled() {
                return this.delegate.isDebugEnabled();
            }

            @Override
            public boolean isInfoEnabled() {
                return this.delegate.isInfoEnabled();
            }

            @Override
            public boolean isWarnEnabled() {
                return this.delegate.isWarnEnabled();
            }

            @Override
            public boolean isErrorEnabled() {
                return this.delegate.isErrorEnabled();
            }

            @Override
            public String getName() {
                return this.delegate.getName();
            }

            @Override
            public void trace(String msg) {
                this.delegate.trace(buildMessage(msg));
            }

            @Override
            public void trace(String format, Object arg) {
                this.delegate.trace(buildMessage(format), arg);
            }

            @Override
            public void trace(String format, Object arg1, Object arg2) {
                this.delegate.trace(buildMessage(format), arg1, arg2);
            }

            @Override
            public void trace(String format, Object... arguments) {
                this.delegate.trace(buildMessage(format), arguments);
            }

            @Override
            public void trace(String msg, Throwable t) {
                this.delegate.trace(buildMessage(msg), t);
            }

            @Override
            public boolean isTraceEnabled(Marker marker) {
                return this.delegate.isTraceEnabled(marker);
            }

            @Override
            public void trace(Marker marker, String msg) {
                this.delegate.trace(marker, buildMessage(msg));
            }

            @Override
            public void trace(Marker marker, String format, Object arg) {
                this.delegate.trace(marker, buildMessage(format), arg);
            }

            @Override
            public void trace(Marker marker, String format, Object arg1, Object arg2) {
                this.delegate.trace(marker, buildMessage(format), arg1, arg2);
            }

            @Override
            public void trace(Marker marker, String format, Object... argArray) {
                this.delegate.trace(marker, buildMessage(format), argArray);
            }

            @Override
            public void trace(Marker marker, String msg, Throwable t) {
                this.delegate.trace(marker, buildMessage(msg), t);
            }

            @Override
            public void debug(String msg) {
                this.delegate.debug(buildMessage(msg));
            }

            @Override
            public void debug(String format, Object arg) {
                this.delegate.debug(buildMessage(format), arg);
            }

            @Override
            public void debug(String format, Object arg1, Object arg2) {
                this.delegate.debug(buildMessage(format), arg1, arg2);
            }

            @Override
            public void debug(String format, Object... arguments) {
                this.delegate.debug(buildMessage(format), arguments);
            }

            @Override
            public void debug(String msg, Throwable t) {
                this.delegate.debug(buildMessage(msg), t);
            }

            @Override
            public boolean isDebugEnabled(Marker marker) {
                return this.delegate.isDebugEnabled(marker);
            }

            @Override
            public void debug(Marker marker, String msg) {
                this.delegate.debug(marker, buildMessage(msg));
            }

            @Override
            public void debug(Marker marker, String format, Object arg) {
                this.delegate.debug(marker, buildMessage(format), arg);
            }

            @Override
            public void debug(Marker marker, String format, Object arg1, Object arg2) {
                this.delegate.debug(marker, buildMessage(format), arg1, arg2);
            }

            @Override
            public void debug(Marker marker, String format, Object... arguments) {
                this.delegate.debug(marker, buildMessage(format), arguments);
            }

            @Override
            public void debug(Marker marker, String msg, Throwable t) {
                this.delegate.debug(marker, buildMessage(msg), t);
            }

            @Override
            public void info(String msg) {
                this.delegate.info(buildMessage(msg));
            }

            @Override
            public void info(String format, Object arg) {
                this.delegate.info(buildMessage(format), arg);
            }

            @Override
            public void info(String format, Object arg1, Object arg2) {
                this.delegate.info(buildMessage(format), arg1, arg2);
            }

            @Override
            public void info(String format, Object... arguments) {
                this.delegate.info(buildMessage(format), arguments);
            }

            @Override
            public void info(String msg, Throwable t) {
                this.delegate.info(buildMessage(msg), t);
            }

            @Override
            public boolean isInfoEnabled(Marker marker) {
                return this.delegate.isInfoEnabled(marker);
            }

            @Override
            public void info(Marker marker, String msg) {
                this.delegate.info(marker, buildMessage(msg));
            }

            @Override
            public void info(Marker marker, String format, Object arg) {
                this.delegate.info(marker, buildMessage(format), arg);
            }

            @Override
            public void info(Marker marker, String format, Object arg1, Object arg2) {
                this.delegate.info(marker, buildMessage(format), arg1, arg2);
            }

            @Override
            public void info(Marker marker, String format, Object... arguments) {
                this.delegate.info(marker, buildMessage(format), arguments);
            }

            @Override
            public void info(Marker marker, String msg, Throwable t) {
                this.delegate.info(marker, buildMessage(msg), t);
            }

            @Override
            public void warn(String msg) {
                this.delegate.warn(buildMessage(msg));
            }

            @Override
            public void warn(String format, Object arg) {
                this.delegate.warn(buildMessage(format), arg);
            }

            @Override
            public void warn(String format, Object... arguments) {
                this.delegate.warn(buildMessage(format), arguments);
            }

            @Override
            public void warn(String format, Object arg1, Object arg2) {
                this.delegate.warn(buildMessage(format), arg1, arg2);
            }

            @Override
            public void warn(String msg, Throwable t) {
                this.delegate.warn(buildMessage(msg), t);
            }

            @Override
            public boolean isWarnEnabled(Marker marker) {
                return this.delegate.isWarnEnabled(marker);
            }

            @Override
            public void warn(Marker marker, String msg) {
                this.delegate.warn(marker, buildMessage(msg));
            }

            @Override
            public void warn(Marker marker, String format, Object arg) {
                this.delegate.warn(marker, buildMessage(format), arg);
            }

            @Override
            public void warn(Marker marker, String format, Object arg1, Object arg2) {
                this.delegate.warn(marker, buildMessage(format), arg1, arg2);
            }

            @Override
            public void warn(Marker marker, String format, Object... arguments) {
                this.delegate.warn(marker, buildMessage(format), arguments);
            }

            @Override
            public void warn(Marker marker, String msg, Throwable t) {
                this.delegate.warn(marker, buildMessage(msg), t);
            }

            @Override
            public void error(String msg) {
                this.delegate.error(buildMessage(msg));
            }

            @Override
            public void error(String format, Object arg) {
                this.delegate.error(buildMessage(format), arg);
            }

            @Override
            public void error(String format, Object arg1, Object arg2) {
                this.delegate.error(buildMessage(format), arg1, arg2);
            }

            @Override
            public void error(String format, Object... arguments) {
                this.delegate.error(buildMessage(format), arguments);
            }

            @Override
            public void error(String msg, Throwable t) {
                this.delegate.error(buildMessage(msg), t);
            }

            @Override
            public boolean isErrorEnabled(Marker marker) {
                return this.delegate.isErrorEnabled(marker);
            }

            @Override
            public void error(Marker marker, String msg) {
                this.delegate.error(marker, buildMessage(msg));
            }

            @Override
            public void error(Marker marker, String format, Object arg) {
                this.delegate.error(marker, buildMessage(format), arg);
            }

            @Override
            public void error(Marker marker, String format, Object arg1, Object arg2) {
                this.delegate.error(marker, buildMessage(format), arg1, arg2);
            }

            @Override
            public void error(Marker marker, String format, Object... arguments) {
                this.delegate.error(marker, buildMessage(format), arguments);
            }

            @Override
            public void error(Marker marker, String msg, Throwable t) {
                this.delegate.error(marker, buildMessage(msg), t);
            }

        }

}
