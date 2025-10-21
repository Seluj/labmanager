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
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Description of a user of the application.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Entity
@Table(name = "ApplicationUser")
public class User implements Serializable, AttributeProvider, Comparable<User>, IdentifiableEntity {

    /**
     * Default role for the application user.
     */
    public static final UserRole DEFAULT_ROLE = UserRole.USER;
    private static final long serialVersionUID = 7961987477984383310L;
    /**
     * Identifier of the jury.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Role of the application user.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role = DEFAULT_ROLE;

    /**
     * Login of the application user.
     */
    @Column
    private String login;

    /**
     * Reference to the person.
     */
    @OneToOne(fetch = FetchType.EAGER)
    private Person person;

    /**
     * Construct an empty user.
     */
    public User() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.person);
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final var other = (User) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.person, other.person);
    }

    @Override
    public int compareTo(User o) {
        return EntityUtils.getPreferredUserComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getRole() != null) {
            consumer.accept("role", getRole()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getLogin())) {
            consumer.accept("login", getLogin()); //$NON-NLS-1$
        }
        if (getPerson() != null) {
            consumer.accept("person", getPerson()); //$NON-NLS-1$
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the membership identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the user role in the application.
     *
     * @return the role.
     */
    public UserRole getRole() {
        return this.role;
    }

    /**
     * Change the role of the user in the application.
     *
     * @param role user role . If it is {@code null}, the role is reset to its default value.
     */
    public void setRole(UserRole role) {
        this.role = role == null ? DEFAULT_ROLE : role;
    }

    /**
     * Change the role of the user in the application.
     *
     * @param role user role . If it is {@code null}, the role is reset to its default value.
     */
    public final void setRole(String role) {
        if (Strings.isNullOrEmpty(role)) {
            setRole((UserRole) null);
        } else {
            setRole(UserRole.valueOfCaseInsensitive(role));
        }
    }

    /**
     * Replies the user login in the application.
     *
     * @return the login.
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Change the login of the user in the application.
     *
     * @param login user login.
     */
    public void setLogin(String login) {
        this.login = Strings.emptyToNull(login);
    }

    /**
     * Replies the person related to application user.
     *
     * @return the person.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the person related to application user.
     *
     * @param person the person.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getLogin());
    }

}
