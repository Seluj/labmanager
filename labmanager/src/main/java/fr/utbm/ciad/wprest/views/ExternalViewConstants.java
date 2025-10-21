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

package fr.utbm.ciad.wprest.views;

/**
 * Definition of global constants for external views.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class ExternalViewConstants {

    /**
     * Name of the endpoint parameter "dbId".
     */
    public static final String DBID_ENDPOINT_PARAMETER = "dbId"; //$NON-NLS-1$

    /**
     * Name of the endpoint for obtaining the Vcard of a person.
     */
    public static final String PERSON_VCARD_ENDPOINT = "personVcard"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "inAttachment".
     */
    public static final String INATTACHMENT_ENDPOINT_PARAMETER = "inAttachment"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "organization".
     */
    public static final String ORGANIZATION_ENDPOINT_PARAMETER = "organization"; //$NON-NLS-1$

    private ExternalViewConstants() {
        //
    }

}
