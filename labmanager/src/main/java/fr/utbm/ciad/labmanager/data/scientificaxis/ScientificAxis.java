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

package fr.utbm.ciad.labmanager.data.scientificaxis;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * A scientific axis that represents a transversal research activity
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.5
 */
@Entity
@Table(name = "ScientificAxes")
public class ScientificAxis implements Serializable, JsonSerializable, Comparable<ScientificAxis>, AttributeProvider, IdentifiableEntity {

    private static final long serialVersionUID = -5032257098515820096L;

    /**
     * Identifier of the axis.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Acronym of the scientific axis.
     */
    @Column
    private String acronym;

    /**
     * Name of the scientific axis.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String name;

    /**
     * Creation date of the axis.
     */
    @Column
    private LocalDate startDate;

    /**
     * Termination date of the axis.
     */
    @Column
    private LocalDate endDate;

    /**
     * Indicates if the axis was validated by an authority.
     */
    @Column(nullable = false)
    private boolean validated;

    /**
     * Projects which are associated to the scientific axis.
     */
    @ManyToMany(mappedBy = "scientificAxes", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    /**
     * Publications which are associated to the scientific axis.
     */
    @ManyToMany(mappedBy = "scientificAxes", fetch = FetchType.LAZY)
    private Set<Publication> publications = new HashSet<>();

    /**
     * Persons who are associated to the research axis.
     */
    @ManyToMany(mappedBy = "scientificAxes", fetch = FetchType.LAZY)
    private Set<Membership> memberships = new HashSet<>();

    /**
     * Construct a scientific axis from the given values.
     *
     * @param id           the identifier of the axis.
     * @param acronym      the acronym of the axis.
     * @param name         the name of the axis.
     * @param creationDate the date of creation of the axis.
     * @param description  the textual description of the axis.
     */
    public ScientificAxis(int id, String acronym, String name, LocalDate creationDate) {
        this.id = id;
        this.acronym = acronym;
        this.name = name;
        this.startDate = creationDate;
    }

    /**
     * Construct an empty scientific axis.
     */
    public ScientificAxis() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.acronym);
        h = HashCodeUtils.add(h, this.name);
        h = HashCodeUtils.add(h, this.startDate);
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
        final var other = (ScientificAxis) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.acronym, other.acronym)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.startDate, other.startDate);
    }

    @Override
    public int compareTo(ScientificAxis o) {
        return EntityUtils.getPreferredScientificAxisComparator().compare(this, o);
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
        if (getStartDate() != null) {
            consumer.accept("startDate", getStartDate()); //$NON-NLS-1$
        }
        if (getEndDate() != null) {
            consumer.accept("endDate", getEndDate()); //$NON-NLS-1$
        }
        consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        //
        final var projects = JsonUtils.cache(generator);
        final var publications = JsonUtils.cache(generator);
        final var memberships = JsonUtils.cache(generator);
        //
        generator.writeArrayFieldStart("projects"); //$NON-NLS-1$
        for (final var project : getProjects()) {
            projects.writeReferenceOrObject(project, () -> {
                JsonUtils.writeObjectAndAttributes(generator, project);
            });
        }
        generator.writeEndArray();
        //
        generator.writeArrayFieldStart("publications"); //$NON-NLS-1$
        for (final var publication : getPublications()) {
            publications.writeReferenceOrObject(publication, () -> {
                JsonUtils.writeObjectAndAttributes(generator, publication);
            });
        }
        generator.writeEndArray();
        //
        generator.writeArrayFieldStart("memberships"); //$NON-NLS-1$
        for (final var membership : getMemberships()) {
            memberships.writeReferenceOrObject(membership, () -> {
                JsonUtils.writeObjectAndAttributes(generator, membership);
            });
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
     * Change the identifier of the research organization.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the acronym or the name of the scientific axis, in that order.
     *
     * @return the acronym or name.
     * @see #getNameOrAcronym()
     * @see #getAcronym()
     * @see #getName()
     */
    public String getAcronymOrName() {
        return Strings.isNullOrEmpty(this.acronym) ? this.name : this.acronym;
    }

    /**
     * Replies the acronym followed by the name of the scientific axis.
     *
     * @return the acronym and name.
     * @see #getAcronymAndName()
     * @since 4.0
     */
    public String getAcronymAndName() {
        final StringBuilder buffer = new StringBuilder();
        if (!Strings.isNullOrEmpty(this.acronym)) {
            buffer.append(this.acronym);
        }
        if (!Strings.isNullOrEmpty(this.name)) {
            if (buffer.length() > 0) {
                buffer.append(" - "); //$NON-NLS-1$
            }
            buffer.append(this.name);
        }
        return buffer.toString();
    }

    /**
     * Replies the name or the acronym of the scientific axis, in that order.
     *
     * @return the name or acronym.
     * @see #getAcronymOrName()
     * @see #getAcronym()
     * @see #getName()
     */
    public String getNameOrAcronym() {
        return Strings.isNullOrEmpty(this.name) ? this.acronym : this.name;
    }

    /**
     * Replies the acronym of the scientific axis.
     *
     * @return the acronym.
     */
    public String getAcronym() {
        return this.acronym;
    }

    /**
     * Change the acronym of the scientific axis.
     *
     * @param acronym the acronym.
     */
    public void setAcronym(String acronym) {
        this.acronym = Strings.emptyToNull(acronym);
    }

    /**
     * Replies the name of the scientific axis.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Change the name of the scientific axis.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = Strings.emptyToNull(name);
    }

    /**
     * Replies the start date of the scientific axis.
     *
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * Change the start date of the scientific axis.
     *
     * @param date the start date.
     */
    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    /**
     * Change the start date of the scientific axis.
     *
     * @param date the start date.
     */
    public final void setStartDate(String date) {
        if (Strings.isNullOrEmpty(date)) {
            setStartDate((LocalDate) null);
        } else {
            setStartDate(LocalDate.parse(date));
        }
    }

    /**
     * Replies the end date of the scientific axis.
     *
     * @return the end date.
     */
    public LocalDate getEndDate() {
        return this.endDate;
    }

    /**
     * Change the end date of the scientific axis.
     *
     * @param date the end date.
     */
    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

    /**
     * Change the end date of the scientific axis.
     *
     * @param date the end date.
     */
    public final void setEndDate(String date) {
        if (Strings.isNullOrEmpty(date)) {
            setEndDate((LocalDate) null);
        } else {
            setEndDate(LocalDate.parse(date));
        }
    }

    /**
     * Replies if the scientific axis is active.
     * An axis is active if the current date is inside the axis time window.
     *
     * @return {@code true} if the axis time window contains the current date.
     */
    public boolean isActive() {
        final var now = LocalDate.now();
        return isActiveAt(now);
    }

    /**
     * Replies if the scientific axis is active.
     * An axis is active if the current date is inside the axis time window.
     *
     * @param now the given date to consider.
     * @return {@code true} if the axis time window contains the given date.
     */
    public boolean isActiveAt(LocalDate now) {
        final var start = getStartDate();
        if (start != null && now.isBefore(start)) {
            return false;
        }
        final var end = getEndDate();
        return end == null || !now.isAfter(end);
    }

    /**
     * Replies if the scientific axis is active in the given time range.
     * An axis is active if the current date is inside the axis time window.
     *
     * @param windowStart is the start date of the window, never {@code null}.
     * @param windowEnd   is the end date of the window, never {@code null}.
     * @return {@code true} if the axis time window intersects the given date window.
     */
    public boolean isActiveIn(LocalDate windowStart, LocalDate windowEnd) {
        assert windowStart != null;
        assert windowEnd != null;
        final var start = getStartDate();
        final var end = getEndDate();
        if (start != null) {
            if (end != null) {
                return !windowEnd.isBefore(start) && !windowStart.isAfter(end);
            }
            return !windowEnd.isBefore(start);
        }
        if (end != null) {
            return !windowStart.isAfter(end);
        }
        return true;
    }

    /**
     * Replies if this organization was validated by an authority.
     *
     * @return {@code true} if the organization is validated.
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this organization was validated by an authority.
     *
     * @param validated {@code true} if the organization is validated.
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this organization was validated by an authority.
     *
     * @param validated {@code true} if the organization is validated.
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    /**
     * Replies the projects that are associated to this scientific axis.
     *
     * @return the projects.
     */
    public Set<Project> getProjects() {
        if (this.projects == null) {
            this.projects = new HashSet<>();
        }
        return this.projects;
    }

    /**
     * Change the projects that are associated to this scientific axis.
     * This function does not update the relationship from the publications to the axis.
     *
     * @param projects the projects associated to this axis.
     */
    public void setProjects(Collection<Project> projects) {
        if (this.projects == null) {
            this.projects = new HashSet<>();
        } else {
            this.projects.clear();
        }
        if (projects != null && !projects.isEmpty()) {
            this.projects.addAll(projects);
        }
    }

    /**
     * Replies the publications that are associated to this scientific axis.
     *
     * @return the publications.
     */
    public Set<Publication> getPublications() {
        if (this.publications == null) {
            this.publications = new HashSet<>();
        }
        return this.publications;
    }

    /**
     * Change the publications that are associated to this scientific axis.
     * This function does not update the relationship from the publications to the axis.
     *
     * @param publications the publications associated to this axis.
     */
    public void setPublications(Collection<Publication> publications) {
        if (this.publications == null) {
            this.publications = new HashSet<>();
        } else {
            this.publications.clear();
        }
        if (publications != null && !publications.isEmpty()) {
            this.publications.addAll(publications);
        }
    }

    /**
     * Replies the memberships that are associated to this scientific axis.
     *
     * @return the memberships.
     */
    public Set<Membership> getMemberships() {
        if (this.memberships == null) {
            this.memberships = new HashSet<>();
        }
        return this.memberships;
    }

    /**
     * Change the memberships that are associated to this scientific axis.
     * This function does not update the relationship from the publications to the axis.
     *
     * @param memberships the memberships associated to this axis.
     */
    public void setMemberships(Collection<Membership> memberships) {
        if (this.memberships == null) {
            this.memberships = new HashSet<>();
        } else {
            this.memberships.clear();
        }
        if (memberships != null && !memberships.isEmpty()) {
            this.memberships.addAll(memberships);
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getAcronym());
    }

}
