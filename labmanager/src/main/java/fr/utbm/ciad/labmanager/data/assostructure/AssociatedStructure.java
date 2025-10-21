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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of an associated structure that is created by an research organization.
 * An associated structure may be a private company that is created from the lab's works,
 * some research group.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Entity
@Table(name = "AssociatedStructures")
public class AssociatedStructure implements Serializable, JsonSerializable, Comparable<AssociatedStructure>, AttributeProvider, IdentifiableEntity {

    private static final long serialVersionUID = 2147999578401059993L;

    /**
     * Identifier of the project
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Name of the structure
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String name;

    /**
     * Acronym of the structure.
     */
    @Column
    private String acronym;

    /**
     * Description of the structure
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String description;

    /**
     * Creation date of the structure.
     */
    @Column
    private LocalDate creationDate;

    /**
     * Creation duration of the structure.
     */
    @Column
    private int creationDuration;

    /**
     * The budget that is dedicated to the creation of the structure in k€.
     */
    @Column
    private float budget;

    /**
     * Type of the structure.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private AssociatedStructureType type;

    /**
     * If the structure creation is confidential or not.
     */
    @Column
    private boolean confidential;

    /**
     * If the structure is validated by a local authority.
     */
    @Column
    private boolean validated;

    /**
     * Organization which is funding the structure creation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ResearchOrganization fundingOrganization;

    /**
     * List of holders involved in the structure creation.
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "associated_structure_holders_mapping",
            joinColumns = @JoinColumn(name = "idStructures"),
            inverseJoinColumns = @JoinColumn(name = "idStructureHolder"))
    private Set<AssociatedStructureHolder> holders;

    /**
     * List of projects that may be associated to this structure.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Project> projects;

    /**
     * Construct an empty project.
     */
    public AssociatedStructure() {
        //
    }

    /**
     * Constructor.
     *
     * @param id                  the identifier of the structure in the database.
     * @param acronym             the acronym of the structure.
     * @param name                the name of the structure.
     * @param description         the description of the structure.
     * @param creationDate        the date of creation of the structure.
     * @param creationDuration    the duration of creation of the structure.
     * @param budget              the budget for creating the budget.
     * @param type                the type of structure.
     * @param confidential        indicates if the structure information is confidential or not.
     * @param validated           indicates if a local authority has validated the structure information.
     * @param fundingOrganization the organization that is funding the structure.
     * @param holders             the list of the participants in the structure creation.
     */
    public AssociatedStructure(int id, String acronym, String name, String description, LocalDate creationDate,
                               int creationDuration, float budget, AssociatedStructureType type, boolean confidential, boolean validated,
                               ResearchOrganization fundingOrganization, Set<AssociatedStructureHolder> holders) {
        assert type != null;
        this.id = id;
        this.acronym = acronym;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.creationDuration = creationDuration;
        this.budget = budget;
        this.type = type;
        this.confidential = confidential;
        this.validated = validated;
        this.fundingOrganization = fundingOrganization;
        this.holders = holders;
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.acronym);
        h = HashCodeUtils.add(h, this.name);
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
        final var other = (AssociatedStructure) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.acronym, other.acronym)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public int compareTo(AssociatedStructure o) {
        return EntityUtils.getPreferredAssociatedStructureComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        assert consumer != null : "How to consume an attribute if the consumer is null?"; //$NON-NLS-1$
        if (getId() != 0) {
            consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAcronym())) {
            consumer.accept("acronym", getAcronym()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getName())) {
            consumer.accept("name", getName()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getDescription())) {
            consumer.accept("description", getDescription()); //$NON-NLS-1$
        }
        if (getCreationDate() != null) {
            consumer.accept("creationDate", getCreationDate()); //$NON-NLS-1$
        }
        if (getCreationDuration() > 0) {
            consumer.accept("creationDuration", Integer.valueOf(getCreationDuration())); //$NON-NLS-1$
        }
        if (getBudget() > 0f) {
            consumer.accept("budget", Float.valueOf(getBudget())); //$NON-NLS-1$
        }
        if (getType() != null) {
            consumer.accept("type", getType()); //$NON-NLS-1$
        }
        consumer.accept("confidential", Boolean.valueOf(isConfidential())); //$NON-NLS-1$
        consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        final var organizations = JsonUtils.cache(generator);
        //
        final var organization = getFundingOrganization();
        organizations.writeReferenceOrObjectField("fundingOrganization", organization, () -> { //$NON-NLS-1$
            JsonUtils.writeObjectAndAttributes(generator, organization);
        });
        //
        generator.writeArrayFieldStart("holders"); //$NON-NLS-1$
        for (final var holder : getHolders()) {
            generator.writeStartObject();
            final var person = holder.getPerson();
            organizations.writeReferenceOrObjectField("person", person, () -> { //$NON-NLS-1$
                JsonUtils.writeObjectAndAttributes(generator, person);
            });
            final var role = holder.getRole();
            JsonUtils.writeField(generator, "role", role); //$NON-NLS-1$
            final var roleDescription = holder.getRoleDescription();
            if (!Strings.isNullOrEmpty(roleDescription)) {
                JsonUtils.writeField(generator, "roleDescription", roleDescription); //$NON-NLS-1$
            }
            final var orga = holder.getOrganization();
            organizations.writeReferenceOrObjectField("organization", orga, () -> { //$NON-NLS-1$
                JsonUtils.writeObjectAndAttributes(generator, orga);
            });
            final var superOrga = holder.getOrganization();
            organizations.writeReferenceOrObjectField("superOrganization", superOrga, () -> { //$NON-NLS-1$
                JsonUtils.writeObjectAndAttributes(generator, superOrga);
            });
            generator.writeEndObject();
        }
        generator.writeEndArray();
        //
        generator.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        serialize(generator, serializers);
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the identifier of the structure.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the acronym or name of the structure.
     *
     * @return the acronym or name.
     * @see #getNameOrAcronym()
     */
    public final String getAcronymOrName() {
        final var acronym = getAcronym();
        if (Strings.isNullOrEmpty(acronym)) {
            return getName();
        }
        return acronym;
    }

    /**
     * Replies the name or acronym of the structure.
     *
     * @return the name or acronym.
     * @see #getAcronymOrName()
     */
    public final String getNameOrAcronym() {
        final var name = getName();
        if (Strings.isNullOrEmpty(name)) {
            return getAcronym();
        }
        return name;
    }

    /**
     * Replies the acronym of the structure.
     *
     * @return the acronym.
     */
    public String getAcronym() {
        return this.acronym;
    }

    /**
     * Change the acronym of the structure.
     *
     * @param acronym the acronym.
     */
    public void setAcronym(String acronym) {
        this.acronym = Strings.emptyToNull(acronym);
    }

    /**
     * Replies the name of the structure.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Change the name of the structure.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = Strings.emptyToNull(name);
    }

    /**
     * Replies the description of the structure.
     *
     * @return the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Change the description of the structure.
     *
     * @param description the description.
     */
    public void setDescription(String description) {
        this.description = Strings.emptyToNull(description);
    }

    /**
     * Replies the date of creation.
     *
     * @return the date, or {@code null}.
     */
    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    /**
     * Change the date of creation.
     *
     * @param date the date, or {@code null}.
     */
    public void setCreationDate(LocalDate date) {
        this.creationDate = date;
    }

    /**
     * Change the date of creation.
     *
     * @param date the date, or {@code null}.
     */
    public final void setCreationDate(String date) {
        try {
            setCreationDate(LocalDate.parse(date));
        } catch (Throwable ex) {
            setCreationDate((LocalDate) null);
        }
    }

    /**
     * Replies the duration of creation (in months).
     *
     * @return the duration, or {@code null}.
     */
    public int getCreationDuration() {
        return this.creationDuration;
    }

    /**
     * Change the duration of creation (in months).
     *
     * @param duration the duration, or {@code null}.
     */
    public void setCreationDuration(int duration) {
        if (duration < 0) {
            this.creationDuration = 0;
        } else {
            this.creationDuration = duration;
        }
    }

    /**
     * Change the duration of creation.
     *
     * @param duration the duration, or {@code null}.
     */
    public final void setCreationDuration(String duration) {
        try {
            setCreationDuration(Integer.parseInt(duration));
        } catch (Throwable ex) {
            setCreationDuration(0);
        }
    }

    /**
     * Replies the budget for the creation.
     *
     * @return the budget.
     */
    public float getBudget() {
        return this.budget;
    }

    /**
     * Change the budget for the creation.
     *
     * @param budget the budget in k-euros.
     */
    public void setBudget(float budget) {
        if (budget < 0f) {
            this.budget = 0f;
        } else {
            this.budget = budget;
        }
    }

    /**
     * Change the budget for the creation.
     *
     * @param budget the budget in k-euros.
     */
    public final void setBudget(Number budget) {
        if (budget != null) {
            setBudget(budget.floatValue());
        } else {
            setBudget(0f);
        }
    }

    /**
     * Replies the type of structure.
     *
     * @return structure type.
     */
    public AssociatedStructureType getType() {
        return this.type;
    }

    /**
     * Change the type of structure.
     *
     * @param type the structure type.
     */
    public void setType(AssociatedStructureType type) {
        this.type = type;
    }

    /**
     * Change the type of structure.
     *
     * @param type the structure type.
     */
    public final void setType(String type) {
        try {
            setType(AssociatedStructureType.valueOfCaseInsensitive(type));
        } catch (Throwable ex) {
            setType((AssociatedStructureType) null);
        }
    }

    /**
     * Replies if this project is marked as confidential or not.
     *
     * @return {@code true} if the project is confidential.
     */
    public boolean isConfidential() {
        return this.confidential;
    }

    /**
     * Change the flag that indicates if this project is marked as confidential or not.
     *
     * @param confidential {@code true} if the project is confidential.
     */
    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    /**
     * Change the flag that indicates if this project is marked as confidential or not.
     *
     * @param confidential {@code true} if the project is confidential.
     */
    public final void setConfidential(Boolean confidential) {
        if (confidential != null) {
            setConfidential(confidential.booleanValue());
        } else {
            setConfidential(false);
        }
    }

    /**
     * Replies if this journal was validated by an authority.
     *
     * @return {@code true} if the journal is validated.
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this journal was validated by an authority.
     *
     * @param validated {@code true} if the journal is validated.
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this journal was validated by an authority.
     *
     * @param validated {@code true} if the journal is validated.
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    /**
     * Replies the funding organization of the structure.
     *
     * @return the funding organization, or {@code null} if the funding organization is unknown.
     */
    public ResearchOrganization getFundingOrganization() {
        return this.fundingOrganization;
    }

    /**
     * Change the funding organization of the structure.
     *
     * @param organization the funding organization, or {@code null} if the organization is unknown.
     */
    public void setFundingOrganization(ResearchOrganization organization) {
        this.fundingOrganization = organization;
    }

    /**
     * Replies the holders of the structure creation.
     *
     * @return the holders.
     */
    public List<AssociatedStructureHolder> getHolders() {
        final var raw = getHoldersRaw();
        assert raw != null;
        final var sortedList = raw.stream().sorted(EntityUtils.getPreferredAssociatedStructureHolderComparator()).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * Change the list of holders.
     *
     * @param holders the holders.
     */
    public void setHolders(List<? extends AssociatedStructureHolder> holders) {
        if (this.holders == null) {
            this.holders = new HashSet<>();
        } else {
            this.holders.clear();
        }
        if (holders != null) {
            this.holders.addAll(holders);
        }
    }

    /**
     * Replies the reference to the storage area without any change or filtering.
     *
     * @return the holders.
     */
    public Set<AssociatedStructureHolder> getHoldersRaw() {
        if (this.holders == null) {
            this.holders = new HashSet<>();
        }
        return this.holders;
    }

    /**
     * Replies the list of projects that may be associated to this structure.
     *
     * @return the list of associated projects, never {@code null}.
     */
    public Set<Project> getProjects() {
        if (this.projects == null) {
            this.projects = new HashSet<>();
        }
        return this.projects;
    }

    /**
     * Change the list of projects that may be associated to this structure.
     *
     * @param projects the list of associated projects, never {@code null}.
     */
    public void setProjects(Collection<? extends Project> projects) {
        if (this.projects == null) {
            this.projects = new HashSet<>();
        } else {
            this.projects.clear();
        }
        if (projects != null) {
            this.projects.addAll(projects);
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getAcronym(), getName());
    }

}
