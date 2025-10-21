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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * JPA repository for person invitations.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public interface PersonInvitationRepository extends JpaRepository<PersonInvitation, Long>, JpaSpecificationExecutor<PersonInvitation> {

    /**
     * Replies the invitations for the given person whatever if he/she is invitee or inviter.
     *
     * @param guestId   the identifier of the guest.
     * @param inviterId the identifier of the inviter.
     * @return the invitations for the person.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    List<PersonInvitation> findAllByGuestIdOrInviterId(long guestId, long inviterId);

}
