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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for application users.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Replies the application user with the given login.
     *
     * @param login the login to search for.
     * @return the user.
     */
    Optional<User> findByLogin(String login);

    /**
     * Replies the application user with the given person id.
     *
     * @param personId the identifier of the person to search for.
     * @return the user.
     */
    Optional<User> findByPersonId(long personId);

    /**
     * Replies all the application users with the given role.
     *
     * @param role the role to search for.
     * @return the list of users.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    List<User> findDistinctByRole(UserRole role);

}
