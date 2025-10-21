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

package fr.utbm.ciad.labmanager.data.invitation;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Type of persin invitation.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public enum PersonInvitationType {

    /**
     * Incoming guest professor.
     */
    INCOMING_GUEST_PROFESSOR {
        @Override
        public boolean isOutgoing() {
            return false;
        }
    },

    /**
     * Incoming guest PhD student.
     */
    INCOMING_GUEST_PHD_STUDENT {
        @Override
        public boolean isOutgoing() {
            return false;
        }
    },

    /**
     * Outgoing guest.
     */
    OUTGOING_GUEST {
        @Override
        public boolean isOutgoing() {
            return true;
        }
    };

    private static final String MESSAGE_PREFIX = "personInvitationType."; //$NON-NLS-1$

    /**
     * Replies the jury membership that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the membership, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a membership.
     */
    public static PersonInvitationType valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid person invitation type: " + name); //$NON-NLS-1$
    }

    /**
     * Replies if this type is for outgoing invitations.
     *
     * @return {@code true} if the type of invitation is for outgoing invitations, {@code false} if it is for
     * incoming invitations.
     */
    public abstract boolean isOutgoing();

    /**
     * Replies the label of the type.
     *
     * @param messages the accessor to the lcoalized names.
     * @param locale   the locale to use.
     * @return the label of the type.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
