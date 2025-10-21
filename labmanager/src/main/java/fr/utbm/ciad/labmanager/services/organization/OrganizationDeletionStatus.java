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

package fr.utbm.ciad.labmanager.services.organization;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Status for the organization deletion.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public enum OrganizationDeletionStatus implements DeletionStatus {

    /**
     * Deletion is impossible because the organization has linked teaching activities.
     */
    TEACHING_ACTIVITY,

    /**
     * Deletion is impossible because the organization has linked funded associated structures.
     */
    FUNDED_ASSOCIATED_STRUCTURE,

    /**
     * Deletion is impossible because the organization has linked associated structures holder as main organization.
     */
    MAIN_ORGANIZATION_ASSOCIATED_STRUCTURE_HOLDER,

    /**
     * Deletion is impossible because the organization has linked associated structures holder as super organization.
     */
    SUPER_ORGANIZATION_ASSOCIATED_STRUCTURE_HOLDER,

    /**
     * Deletion is impossible because the organization has linked membership with direct organization.
     */
    DIRECT_ORGANIZATION_MEMBERSHIP,

    /**
     * Deletion is impossible because the organization has linked membership with super organization.
     */
    SUPER_ORGANIZATION_MEMBERSHIP,

    /**
     * Deletion is impossible because the organization has linked project with coordinator position.
     */
    PROJECT_COORDINATOR,

    /**
     * Deletion is impossible because the organization has linked project with LEAR position.
     */
    PROJECT_LEAR_ORGANIZATION,

    /**
     * Deletion is impossible because the organization has linked project with local organization position.
     */
    PROJECT_LOCAL_ORGANIZATION,

    /**
     * Deletion is impossible because the organization has linked project with other partner position.
     */
    PROJECT_OTHER_PARTNER_ORGANIZATION,

    /**
     * Deletion is impossible because the organization has linked project with super organization position.
     */
    PROJECT_SUPER_ORGANIZATION;

    private static final String MESSAGE_PREFIX = "organizationDeletionStatus."; //$NON-NLS-1$

    /**
     * Replies the status of organization deletion that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the status of organization deletion, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static OrganizationDeletionStatus valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid status of organization deletion: " + name); //$NON-NLS-1$
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
