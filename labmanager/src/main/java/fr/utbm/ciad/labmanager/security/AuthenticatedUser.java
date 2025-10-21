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

package fr.utbm.ciad.labmanager.security;

import com.google.common.base.Strings;
import com.vaadin.flow.spring.security.AuthenticationContext;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Connected application user.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;

    private final AuthenticationContext authenticationContext;

    /**
     * Constructor.
     *
     * @param authenticationContext the context of authentication of the application.
     * @param userRepository        the repository to have access to the application users.
     */
    public AuthenticatedUser(@Autowired AuthenticationContext authenticationContext, @Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Replies the name of the user. This function is implemented for logging purpose only.
     *
     * @param userContext the user context
     * @return the name or the empty string.
     */
    public static String getUserName(AuthenticationContext userContext) {
        if (userContext != null) {
            final var auth = userContext.getAuthenticatedUser(UserDetails.class);
            if (auth.isPresent()) {
                return Strings.nullToEmpty(auth.get().getUsername());
            }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Replies the name of the user. This function is implemented for logging purpose only.
     *
     * @param user the user
     * @return the name or the empty string.
     */
    public static String getUserName(AuthenticatedUser user) {
        if (user != null) {
            final var ouser = user.get();
            if (ouser.isPresent()) {
                return Strings.nullToEmpty(ouser.get().getLogin());
            }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Replies the authenticated user.
     *
     * @return the user.
     */
    @Transactional
    public Optional<User> get() {
        final var userName = getUserName(this.authenticationContext);
        if (!Strings.isNullOrEmpty(userName)) {
            final var user = this.userRepository.findByLogin(userName);
            return user;
        }
        return Optional.empty();
    }

    /**
     * Log out the user.
     */
    public void logout() {
        this.authenticationContext.logout();
    }

}
