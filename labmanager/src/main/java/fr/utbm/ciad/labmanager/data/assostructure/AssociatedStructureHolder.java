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

package fr.utbm.ciad.labmanager.data.assostructure;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Description of an holder for the creation of an associated structure.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Entity
@Table(name = "AssociatedStructureHolders")
public class AssociatedStructureHolder implements Serializable, AttributeProvider, Comparable<AssociatedStructureHolder>, IdentifiableEntity {

    private static final long serialVersionUID = 4459761172461217306L;

    /**
     * Identifier of the associated structure holder.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Holder of the associated structure creation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    /**
     * Role of the member.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private HolderRole role = HolderRole.SCIENTIFIC_HEAD;

    /**
     * Role of the member.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String roleDescription;

    /**
     * Organization of the person.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ResearchOrganization organization;

    /**
     * Super-Organization of the organization of the person.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ResearchOrganization superOrganization;

    /**
     * Construct an empty member.
     */
    public AssociatedStructureHolder() {
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
        final var other = (AssociatedStructureHolder) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.person, other.person);
    }

    @Override
    public int compareTo(AssociatedStructureHolder o) {
        return EntityUtils.getPreferredAssociatedStructureHolderComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getRole() != null) {
            consumer.accept("role", getRole()); //$NON-NLS-1$
            consumer.accept("roleDescription", getRoleDescription()); //$NON-NLS-1$
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the holder identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the holding person.
     *
     * @return the person.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the holding person.
     *
     * @param person the person.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Replies the role of the person in the associated structure.
     *
     * @return the role.
     */
    public HolderRole getRole() {
        return this.role;
    }

    /**
     * Change the role of the member in the project.
     *
     * @param role the role.
     */
    public void setRole(HolderRole role) {
        if (role == null) {
            this.role = HolderRole.SCIENTIFIC_HEAD;
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
            setRole((HolderRole) null);
        } else {
            setRole(HolderRole.valueOfCaseInsensitive(role));
        }
    }

    /**
     * Replies a short description of the role of the person in the associated structure.
     *
     * @return the role description.
     */
    public String getRoleDescription() {
        return this.roleDescription;
    }

    /**
     * Change short description of the role of the member in the project.
     *
     * @param description the role description.
     */
    public void setRoleDescription(String description) {
        this.roleDescription = Strings.emptyToNull(description);
    }

    /**
     * Replies the organization of the holding person.
     *
     * @return the organization.
     */
    public ResearchOrganization getOrganization() {
        return this.organization;
    }

    /**
     * Change the organization of the holding person.
     *
     * @param organization the organization.
     */
    public void setOrganization(ResearchOrganization organization) {
        this.organization = organization;
    }

    /**
     * Replies the super organization of the holding person.
     *
     * @return the organization.
     */
    public ResearchOrganization getSuperOrganization() {
        return this.superOrganization;
    }

    /**
     * Change the super organization of the holding person.
     *
     * @param organization the super organization.
     */
    public void setSuperOrganization(ResearchOrganization organization) {
        this.superOrganization = organization;
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

}
