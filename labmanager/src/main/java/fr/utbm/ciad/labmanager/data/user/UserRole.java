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

package fr.utbm.ciad.labmanager.data.user;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Enumeration that represents the roles of a user of the labmanager application.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public enum UserRole {
    /**
     * The user is a regular user of the application.
     */
    USER {
        @Override
        public boolean hasBaseAdministrationRights() {
            return false;
        }

        @Override
        public boolean hasAdvancedAdministrationRights() {
            return false;
        }

        @Override
        public String toGrantedRole() {
            return USER_GRANT;
        }

    },

    /**
     * The user is a responsible in the research organization.
     */
    RESPONSIBLE {
        @Override
        public boolean hasBaseAdministrationRights() {
            return true;
        }

        @Override
        public boolean hasAdvancedAdministrationRights() {
            return false;
        }

        @Override
        public String toGrantedRole() {
            return RESPONSIBLE_GRANT;
        }

    },

    /**
     * The user is an administrator of the application.
     */
    ADMIN {
        @Override
        public boolean hasBaseAdministrationRights() {
            return true;
        }

        @Override
        public boolean hasAdvancedAdministrationRights() {
            return true;
        }

        @Override
        public String toGrantedRole() {
            return ADMIN_GRANT;
        }

    };

    /**
     * Name of the grant for {@code USER}.
     *
     * @see #toGrantedRole()
     */
    public static final String USER_GRANT = "ROLE_USER"; //$NON-NLS-1$

    /**
     * Name of the grant for {@code RESPONSIBLE}.
     *
     * @see #toGrantedRole()
     */
    public static final String RESPONSIBLE_GRANT = "ROLE_RESPONSIBLE"; //$NON-NLS-1$

    /**
     * Name of the grant for {@code ADMIN}.
     *
     * @see #toGrantedRole()
     */
    public static final String ADMIN_GRANT = "ROLE_ADMIN"; //$NON-NLS-1$

    private static final String MESSAGE_PREFIX = "userRole."; //$NON-NLS-1$

    /**
     * Replies the user role that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the user role, to search for.
     * @return the user role.
     * @throws IllegalArgumentException if the given name does not corresponds to a user role.
     */
    public static UserRole valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid user role: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the user role in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the user role in the given language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies if the given role has the base administration rights.
     * Base administration rights include the update of the database content
     * for all the users.
     *
     * @return {@code true} if the role has the rights.
     */
    public abstract boolean hasBaseAdministrationRights();

    /**
     * Replies if the given role has the advanced administration rights.
     * Advanced administration rights include the management of the user
     * accounts.
     *
     * @return {@code true} if the role has the rights.
     */
    public abstract boolean hasAdvancedAdministrationRights();

    /**
     * Replies a string representation of the role's grant.
     *
     * @return the role's grant.
     * @see #grant
     */
    public abstract String toGrantedRole();

}
