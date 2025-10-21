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

package fr.utbm.ciad.labmanager.data.project;

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
 * Description of a member of a project.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Entity
@Table(name = "ProjectMembers")
public class ProjectMember implements Serializable, AttributeProvider, Comparable<ProjectMember>, IdentifiableEntity {

    private static final long serialVersionUID = 7198080633760598725L;

    /**
     * Identifier of the member.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Promoter or director of the candidate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    /**
     * Role of the member.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Role role = Role.PARTICIPANT;

    /**
     * Construct an empty member.
     */
    public ProjectMember() {
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
        final var other = (ProjectMember) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.person, other.person);
    }

    @Override
    public int compareTo(ProjectMember o) {
        return EntityUtils.getPreferredProjectMemberComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getRole() != null) {
            consumer.accept("role", getRole()); //$NON-NLS-1$
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
     * Replies the member person.
     *
     * @return the person.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the member person.
     *
     * @param person the person.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Replies the role of the member in the project.
     *
     * @return the role.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Change the role of the member in the project.
     *
     * @param role the role.
     */
    public void setRole(Role role) {
        if (role == null) {
            this.role = Role.PARTICIPANT;
        } else {
            this.role = role;
        }
    }

    /**
     * Change the role of the member.
     *
     * @param role the role of the member.
     */
    public final void setRole(String role) {
        if (Strings.isNullOrEmpty(role)) {
            setRole((Role) null);
        } else {
            setRole(Role.valueOfCaseInsensitive(role));
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

}
