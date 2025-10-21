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

package fr.utbm.ciad.labmanager.services.member;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Status for the membership deletion.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public enum MembershipDeletionStatus implements DeletionStatus {

    /**
     * Deletion is impossible because the membership has linked supervision.
     */
    SUPERVISION;

    private static final String MESSAGE_PREFIX = "membershipDeletionStatus."; //$NON-NLS-1$

    /**
     * Replies the status of membership deletion that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the status of membership deletion, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static MembershipDeletionStatus valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid status of membership deletion: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the deletion status.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the deletion status.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public Throwable getException(MessageSourceAccessor messages, Locale locale) {
        return new IllegalStateException(getLabel(messages, locale));
    }

}
