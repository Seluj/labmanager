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

package fr.utbm.ciad.labmanager.data.organization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolder;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Research organization or group or researchers.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "ResearchOrgs")
public class ResearchOrganization extends AbstractContextData implements JsonSerializable, Comparable<ResearchOrganization>, AttributeProvider, IdentifiableEntity {

    /**
     * Default type for research organizations.
     */
    public static final ResearchOrganizationType DEFAULT_TYPE = ResearchOrganizationType.LABORATORY;
    private static final long serialVersionUID = -450531251083286848L;
    private static final String RNSR_URL = "https://appliweb.dgri.education.fr/rnsr/PresenteStruct.jsp?PUBLIC=OK&numNatStruct="; //$NON-NLS-1$

    /**
     * Identifier of the organization.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Acronym of the research organization.
     */
    @Column
    private String acronym;

    /**
     * Name of the research organization.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String name;

    /**
     * Textual description of the research organization.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    @Lob
    private String description;

    /**
     * Indicates if the organization is marked as a major organization on the server.
     *
     * @since 2.2
     */
    @Column
    private boolean majorOrganization;

    /**
     * Number of the organization in the "Repertoire National des Structures de Recherche"
     * of the French Ministry of Research.
     *
     * @since 2.2
     */
    @Column
    private String rnsr;

    /**
     * Identifier of the organization for the French Ministry of Research.
     *
     * @since 2.2
     */
    @Column
    private String nationalIdentifier;

    /**
     * The country of the organization.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode country = CountryCode.getDefault();

    /**
     * The type of organization.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResearchOrganizationType type = DEFAULT_TYPE;

    /**
     * The URL of the organization.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String organizationUrl;

    /**
     * Reference to the sub organizations.
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "SuperOrganization_SubOrganization",
            joinColumns = {@JoinColumn(name = "superorganization_id")},
            inverseJoinColumns = {@JoinColumn(name = "suborganization_id")}
    )
    private Set<ResearchOrganization> subOrganizations;

    /**
     * Reference to the super organizations.
     */
    @ManyToMany(mappedBy = "subOrganizations", fetch = FetchType.EAGER)
    private Set<ResearchOrganization> superOrganizations;

    /**
     * References to the postal addresses of the organization.
     */
    @OneToMany(fetch = FetchType.LAZY)
    private Set<OrganizationAddress> addresses = new HashSet<>();

    /**
     * Name of the logo of the project if it has one.
     *
     * @since 3.2
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String pathToLogo;

    /**
     * Indicates if the address was validated by an authority.
     */
    @Column(nullable = false)
    private boolean validated;

    /**
     * List of teaching activities for the organization.
     */
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeachingActivity> teachingActivities;

    /**
     * List of associated structures that have been funded by the organization.
     */
    @OneToMany(mappedBy = "fundingOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssociatedStructure> fundedAssociatedStructures;

    /**
     * List of associated structures' holders that have been linked to the organization as main organization.
     */
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssociatedStructureHolder> associatedStructureHolderOrganization;

    /**
     * List of associated structures' holders that have been linked to the organization as super organization.
     */
    @OneToMany(mappedBy = "superOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssociatedStructureHolder> associatedStructureHolderSuperOrganization;

    /**
     * List of memberships that have the organization as direct organization.
     */
    @OneToMany(mappedBy = "researchOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Membership> directOrganizationMemberships;

    /**
     * List of memberships that have the organization as super organization.
     */
    @OneToMany(mappedBy = "superResearchOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Membership> superOrganizationMemberships;

    /**
     * List of projects that have the organization as coordinator.
     */
    @OneToMany(mappedBy = "coordinator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> coordinatedProjects;

    /**
     * List of projects that have the organization as LEAR.
     */
    @OneToMany(mappedBy = "learOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> learOrganizationProjects;

    /**
     * List of projects that have the organization as local organization.
     */
    @OneToMany(mappedBy = "localOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> localOrganizationProjects;

    /**
     * List of projects that have the organization as super organization.
     */
    @OneToMany(mappedBy = "superOrganization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> superOrganizationProjects;

    /**
     * List of projects that have the organization as other partner.
     */
    @ManyToMany(mappedBy = "otherPartners", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> otherPartnerProjects;

    /**
     * Construct an empty research organization.
     */
    public ResearchOrganization() {
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
        final var other = (ResearchOrganization) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.acronym, other.acronym)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public int compareTo(ResearchOrganization o) {
        return EntityUtils.getPreferredResearchOrganizationComparator().compare(this, o);
    }

    /**
     * {@inheritDoc}
     * <p>The attributes that are not considered by this function are:<ul>
     * <li>{@code id}</li>
     * <li>{@code superOrganization}</li>
     * <li>{@code subOrganizations}</li>
     * </ul>
     */
    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        assert consumer != null : "How to consume an attribute if the consumer is null?"; //$NON-NLS-1$
        if (getId() != 0) {
            consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAcronym())) {
            consumer.accept("acronym", getAcronym()); //$NON-NLS-1$
        }
        if (getCountry() != null) {
            consumer.accept("country", getCountry()); //$NON-NLS-1$
            consumer.accept("countryLabel", getCountryDisplayName()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getDescription())) {
            consumer.accept("description", getDescription()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getName())) {
            consumer.accept("name", getName()); //$NON-NLS-1$
        }
        consumer.accept("majorOrganization", Boolean.valueOf(isMajorOrganization())); //$NON-NLS-1$
        if (!Strings.isNullOrEmpty(getRnsr())) {
            consumer.accept("rnsr", getRnsr()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getNationalIdentifier())) {
            consumer.accept("nationalIdentifier", getNationalIdentifier()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getOrganizationURL())) {
            consumer.accept("organizationURL", getOrganizationURL()); //$NON-NLS-1$
        }
        if (getType() != null) {
            consumer.accept("type", getType()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPathToLogo())) {
            consumer.accept("pathToLogo", getPathToLogo()); //$NON-NLS-1$
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
        final var organizations = JsonUtils.cache(generator);
        final var persons = JsonUtils.cache(generator);
        //
        generator.writeArrayFieldStart("addresses"); //$NON-NLS-1$
        for (final var address : getAddresses()) {
            organizations.writeReferenceOrObject(address, () -> {
                JsonUtils.writeObjectAndAttributes(generator, address);
            });
        }
        generator.writeEndArray();
        //
        generator.writeArrayFieldStart("superOrganizations"); //$NON-NLS-1$
        for (final var superorga : getSuperOrganizations()) {
            organizations.writeReferenceOrObject(superorga, () -> {
                JsonUtils.writeObjectAndAttributes(generator, superorga);
            });
        }
        generator.writeEndArray();
        //
        generator.writeArrayFieldStart("subOrganizations"); //$NON-NLS-1$
        for (final var suborga : getSubOrganizations()) {
            organizations.writeReferenceOrObject(suborga, () -> {
                JsonUtils.writeObjectAndAttributes(generator, suborga);
            });
        }
        generator.writeEndArray();
        //
        generator.writeArrayFieldStart("memberships"); //$NON-NLS-1$
        for (final var membership : getDirectOrganizationMemberships()) {
            generator.writeStartObject();
            membership.forEachAttribute((attrName, attrValue) -> {
                JsonUtils.writeField(generator, attrName, attrValue);
            });
            persons.writeReferenceOrObjectField("person", membership.getPerson(), () -> { //$NON-NLS-1$
                JsonUtils.writeObjectAndAttributes(generator, membership.getPerson());
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
     * Change the identifier of the research organization.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the super organizations.
     *
     * @return the set of super organizations.
     */
    public Set<ResearchOrganization> getSuperOrganizations() {
        if (this.superOrganizations == null) {
            this.superOrganizations = new HashSet<>();
        }
        return this.superOrganizations;
    }

    /**
     * Change the super organizations. This function does not change the set
     * of suborganizations of the provided super organizations.
     *
     * @param newSuperOrganizations the set of super organizations that will replace the current super organizations.
     * @see #setSubOrganizations(Set)
     */
    public void setSuperOrganizations(Set<ResearchOrganization> newSuperOrganizations) {
        if (this.superOrganizations == null) {
            this.superOrganizations = new HashSet<>();
        } else {
            this.superOrganizations.clear();
        }
        if (newSuperOrganizations != null) {
            this.superOrganizations.addAll(newSuperOrganizations);
        }
    }

    /**
     * Replies the sub organizations.
     *
     * @return the set of sub organizations.
     * @see #setSuperOrganizations(Set)
     */
    public Set<ResearchOrganization> getSubOrganizations() {
        if (this.subOrganizations == null) {
            this.subOrganizations = new HashSet<>();
        }
        return this.subOrganizations;
    }

    /**
     * Change the sub organizations. This function does not change the set
     * of super organizations of the provided sub organizations.
     *
     * @param newSubOrganizations the set of sub organizations that will replace the current suborganizations.
     * @see #setSuperOrganizations(Set)
     */
    public void setSubOrganizations(Set<ResearchOrganization> newSubOrganizations) {
        if (this.subOrganizations == null) {
            this.subOrganizations = new HashSet<>();
        } else {
            this.subOrganizations.clear();
        }
        if (newSubOrganizations != null) {
            this.subOrganizations.addAll(newSubOrganizations);
        }
    }

    /**
     * Replies if the given organization is a super organization of this organization.
     *
     * @param candidate the organization to search for in the super orgnaizations.
     * @return {@code true} if the given {@code candidate} is a super organization of this organization.
     * @since 4.0
     */
    public boolean isSubOrganizationOf(ResearchOrganization candidate) {
        final var found = new TreeSet<>(EntityUtils.getPreferredResearchOrganizationComparator());
        final var buffer = new LinkedList<ResearchOrganization>();
        buffer.addAll(getSuperOrganizations());
        while (!buffer.isEmpty()) {
            final var superOrga = buffer.removeFirst();
            if (found.add(superOrga)) {
                if (superOrga.equals(candidate)) {
                    return true;
                }
                buffer.addAll(superOrga.getSuperOrganizations());
            }
        }
        return false;
    }

    /**
     * Replies the acronym or the name of the research organization, that order.
     *
     * @return the acronym or name.
     * @see #getNameOrAcronym()
     * @see #getAcronym()
     * @see #getName()
     * @see #getAcronymAndName()
     */
    public String getAcronymOrName() {
        return Strings.isNullOrEmpty(this.acronym) ? this.name : this.acronym;
    }

    /**
     * Replies the name or the acronym of the research organization, that order.
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
     * Replies the acronym and the name of the research organization, if they exist, with the format {@code "Acronym - Name"}.
     *
     * @return the acronym and the name, or {@code null} if there is neither acronym nor name.
     * @see #getAcronymOrName()
     * @see #ACRONYM_NAME_SEPARATOR
     * @since 4.0
     */
    public String getAcronymAndName() {
        return getAcronymAndName(EntityConstants.FULL_ACRONYM_NAME_SEPARATOR);
    }

    /**
     * Replies the acronym and the name of the research organization, if they exist.
     *
     * @param separator the string to be written between the acronym and the name.
     * @return the acronym and the name, or {@code null} if there is neither acronym nor name.
     * @see #getAcronymOrName()
     * @since 4.0
     */
    public String getAcronymAndName(String separator) {
        final var buffer = new StringBuilder();
        if (!Strings.isNullOrEmpty(this.acronym)) {
            buffer.append(this.acronym);
        }
        if (!Strings.isNullOrEmpty(this.name)) {
            if (buffer.length() > 0 && !Strings.isNullOrEmpty(separator)) {
                buffer.append(separator);
            }
            buffer.append(this.name);
        }
        return Strings.emptyToNull(buffer.toString());
    }

    /**
     * Replies the acronym of the research organization.
     *
     * @return the acronym.
     */
    public String getAcronym() {
        return this.acronym;
    }

    /**
     * Change the acronym of the research organization.
     *
     * @param acronym the acronym.
     */
    public void setAcronym(String acronym) {
        this.acronym = Strings.emptyToNull(acronym);
    }

    /**
     * Replies the name of the research organization.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Change the name of the research organization.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = Strings.emptyToNull(name);
    }

    /**
     * Replies the textual description.
     *
     * @return the textual description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Change the textual description.
     *
     * @param description the textual description.
     */
    public void setDescription(String description) {
        this.description = Strings.emptyToNull(description);
    }

    /**
     * Replies the name of the country of the organization.
     *
     * @return the display name of the country.
     */
    public String getCountryDisplayName() {
        return this.country.getDisplayCountry();
    }

    /**
     * Replies the country of the organization.
     *
     * @return the country, never {@code null}.
     */
    public CountryCode getCountry() {
        return this.country;
    }

    /**
     * Change the country of the organization.
     *
     * @param country the country, or {@code null} if the country is the default country.
     * @see #DEFAULT_COUNTRY
     */
    public void setCountry(CountryCode country) {
        if (country == null) {
            this.country = CountryCode.getDefault();
        } else {
            this.country = country;
        }
    }

    /**
     * Change the country of the organization.
     *
     * @param country the country.
     */
    public final void setCountry(String country) {
        if (Strings.isNullOrEmpty(country)) {
            setCountry((CountryCode) null);
        } else {
            setCountry(CountryCode.valueOfCaseInsensitive(country));
        }
    }

    /**
     * Replies the URL of the research organization.
     *
     * @return the URL.
     */
    public String getOrganizationURL() {
        return this.organizationUrl;
    }

    /**
     * Change the URL of the research organization.
     *
     * @param url the URL.
     */
    public void setOrganizationURL(String url) {
        this.organizationUrl = Strings.emptyToNull(url);
    }

    /**
     * Change the URL of the research organization.
     *
     * @param url the URL.
     */
    public final void setOrganizationURL(URL url) {
        final String value;
        if (url != null) {
            value = url.toExternalForm();
        } else {
            value = null;
        }
        setOrganizationURL(value);
    }

    /**
     * Replies the URL of the research organization.
     *
     * @return the URL.
     */
    public final URL getOrganizationURLObject() {
        try {
            return new URI(getOrganizationURL()).toURL();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the type of organization.
     *
     * @return the type.
     */
    public ResearchOrganizationType getType() {
        return this.type;
    }

    /**
     * Change the type of organization.
     *
     * @param type the country, or {@code null} if the type is the default one.
     * @see #DEFAULT_TYPE
     */
    public void setType(ResearchOrganizationType type) {
        if (type == null) {
            this.type = DEFAULT_TYPE;
        } else {
            this.type = type;
        }
    }

    /**
     * Change the type of the organization.
     *
     * @param type the type.
     */
    public final void setType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setType((ResearchOrganizationType) null);
        } else {
            setType(ResearchOrganizationType.valueOfCaseInsensitive(type));
        }
    }

    /**
     * Replies the addresses of this organization.
     *
     * @return the addresses.
     * @since 2.0
     */
    public Set<OrganizationAddress> getAddresses() {
        if (this.addresses == null) {
            this.addresses = new TreeSet<>(EntityUtils.getPreferredOrganizationAddressComparator());
        }
        return this.addresses;
    }

    /**
     * Change the addresses of this organization.
     *
     * @param addresses the addresses.
     * @since 2.0
     */
    public void setAddresses(Set<OrganizationAddress> addresses) {
        if (this.addresses == null) {
            this.addresses = new TreeSet<>(EntityUtils.getPreferredOrganizationAddressComparator());
        } else {
            this.addresses.clear();
        }
        if (addresses != null) {
            this.addresses.addAll(addresses);
        }
    }

    /**
     * Replies the number of the organization in the
     * "Repertoire National des Structures de Recherche" (RNSR).
     *
     * @return the RNSR number.
     * @see #getRnsrUrl()
     */
    public String getRnsr() {
        return this.rnsr;
    }

    /**
     * change the number of the organization in the
     * "Repertoire National des Structures de Recherche" (RNSR).
     *
     * @param rnsr the RNSR number.
     */
    public void setRnsr(String rnsr) {
        this.rnsr = Strings.emptyToNull(rnsr);
    }

    /**
     * Replies the URL of the page of the organization on the
     * "Repertoire National des Structures de Recherche" (RNSR).
     *
     * @return the URL to the RNSR, or {@code null} if the organization has no RNSR number.
     * @see #getRnsr()
     */
    public URL getRnsrUrl() {
        final var number = getRnsr();
        if (!Strings.isNullOrEmpty(number)) {
            try {
                return new URI(RNSR_URL + number).toURL();
            } catch (Throwable ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Replies the number of the organization for the national ministry of research.
     *
     * @return the national identifier.
     */
    public String getNationalIdentifier() {
        return this.nationalIdentifier;
    }

    /**
     * Change the number of the organization for the national ministry of research.
     *
     * @param identifier the national identifier.
     */
    public void setNationalIdentifier(String identifier) {
        this.nationalIdentifier = Strings.emptyToNull(identifier);
    }

    /**
     * Replies if this organization is mared as a major organization.
     *
     * @return {@code true} if this organization is major.
     */
    public boolean isMajorOrganization() {
        return this.majorOrganization;
    }

    /**
     * Change if this organization is marked as a major organization.
     * The major organization is those for which the labmanager is deployed.
     *
     * @param major {@code true} if this organization is major.
     */
    public void setMajorOrganization(boolean major) {
        this.majorOrganization = major;
    }

    /**
     * Change if this organization is marked as a major organization.
     * The major organization is those for which the labmanager is deployed.
     *
     * @param major {@code true} if this organization is major.
     */
    public final void setMajorOrganization(Boolean major) {
        if (major == null) {
            setMajorOrganization(false);
        } else {
            setMajorOrganization(major.booleanValue());
        }
    }

    /**
     * Replies a path to a downloadable logo that represents the organization.
     *
     * @return the path or {@code null}.
     * @since 3.2
     */
    public String getPathToLogo() {
        return this.pathToLogo;
    }

    /**
     * Change the path to a downloadable logo that represents the organization.
     *
     * @param path the path or {@code null}.
     */
    public void setPathToLogo(String path) {
        this.pathToLogo = Strings.emptyToNull(path);
    }

    /**
     * Replies if this organization was validated by an authority.
     *
     * @return {@code true} if the organization is validated.
     * @since 3.2
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this organization was validated by an authority.
     *
     * @param validated {@code true} if the organization is validated.
     * @since 3.2
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this organization was validated by an authority.
     *
     * @param validated {@code true} if the organization is validated.
     * @since 3.2
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    /**
     * Replies if this organization is located outside Europe.
     *
     * @return {@code true} if the organization is outside Europe.
     * @since 3.6
     */
    public boolean isInternational() {
        return !getCountry().isEuropeanSovereign();
    }

    /**
     * Replies if this organization is located inside Europe.
     *
     * @return {@code true} if the organization is inside Europe.
     * @since 3.6
     */
    public boolean isEuropean() {
        return getCountry().isEuropeanSovereign();
    }

    /**
     * Replies if this organization is located in France.
     *
     * @return {@code true} if the organization is in France.
     * @since 3.6
     */
    public boolean isFrench() {
        return getCountry().isFrance();
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getAcronym(), getName());
    }

    /**
     * Replies the list of teaching activities associated to the organization.
     *
     * @return the list of teaching activities.
     * @since 4.0
     */
    public Set<TeachingActivity> getTeachingActivities() {
        if (this.teachingActivities == null) {
            this.teachingActivities = new HashSet<>();
        }
        return this.teachingActivities;
    }

    /**
     * Change the list of teaching activities associated to the organization.
     *
     * @param list the list of activities.
     * @since 4.0
     */
    public void setTeachingActivities(Set<TeachingActivity> list) {
        if (this.teachingActivities != null) {
            this.teachingActivities.clear();
        } else {
            this.teachingActivities = new HashSet<>();
        }
        if (list != null) {
            this.teachingActivities.addAll(list);
        }
    }

    /**
     * Replies the list of associated structures that have been funded by the organization.
     *
     * @return the list of associated structures.
     * @since 4.0
     */
    public Set<AssociatedStructure> getFundedAssociatedStructures() {
        if (this.fundedAssociatedStructures == null) {
            this.fundedAssociatedStructures = new HashSet<>();
        }
        return this.fundedAssociatedStructures;
    }

    /**
     * Change the list of associated structures that have been funded by the organization.
     *
     * @param list the list of associated structures.
     * @since 4.0
     */
    public void setFundedAssociatedStructures(Set<AssociatedStructure> list) {
        if (this.fundedAssociatedStructures != null) {
            this.fundedAssociatedStructures.clear();
        } else {
            this.fundedAssociatedStructures = new HashSet<>();
        }
        if (list != null) {
            this.fundedAssociatedStructures.addAll(list);
        }
    }

    /**
     * Replies the list of associated structures' holders that have been linked to this organization as main organization.
     *
     * @return the list of associated structures' holders.
     * @since 4.0
     */
    public Set<AssociatedStructureHolder> getMainOrganizationAssociatedStructuresHolders() {
        if (this.associatedStructureHolderOrganization == null) {
            this.associatedStructureHolderOrganization = new HashSet<>();
        }
        return this.associatedStructureHolderOrganization;
    }

    /**
     * Change the list of associated structures' holders that have been linked to this organization as main organization.
     *
     * @param list the list of associated structures' holders.
     * @since 4.0
     */
    public void setMainOrganizationAssociatedStructuresHolders(Set<AssociatedStructureHolder> list) {
        if (this.associatedStructureHolderOrganization != null) {
            this.associatedStructureHolderOrganization.clear();
        } else {
            this.associatedStructureHolderOrganization = new HashSet<>();
        }
        if (list != null) {
            this.associatedStructureHolderOrganization.addAll(list);
        }
    }

    /**
     * Replies the list of associated structures' holders that have been linked to this organization as super organization.
     *
     * @return the list of associated structures' holders.
     * @since 4.0
     */
    public Set<AssociatedStructureHolder> getSuperOrganizationAssociatedStructuresHolders() {
        if (this.associatedStructureHolderSuperOrganization == null) {
            this.associatedStructureHolderSuperOrganization = new HashSet<>();
        }
        return this.associatedStructureHolderSuperOrganization;
    }

    /**
     * Change the list of associated structures' holders that have been linked to this organization as super organization.
     *
     * @param list the list of associated structures' holders.
     * @since 4.0
     */
    public void setSuperOrganizationAssociatedStructuresHolders(Set<AssociatedStructureHolder> list) {
        if (this.associatedStructureHolderSuperOrganization != null) {
            this.associatedStructureHolderSuperOrganization.clear();
        } else {
            this.associatedStructureHolderSuperOrganization = new HashSet<>();
        }
        if (list != null) {
            this.associatedStructureHolderSuperOrganization.addAll(list);
        }
    }

    /**
     * Replies the list of memberships that have this organization as direct organization.
     *
     * @return the list of memberships.
     * @since 4.0
     */
    public Set<Membership> getDirectOrganizationMemberships() {
        if (this.directOrganizationMemberships == null) {
            this.directOrganizationMemberships = new HashSet<>();
        }
        return this.directOrganizationMemberships;
    }

    /**
     * Change the list of memberships that have this organization as direct organization.
     *
     * @param list the list of memberships.
     * @since 4.0
     */
    public void setDirectOrganizationMemberships(Set<Membership> list) {
        if (this.directOrganizationMemberships != null) {
            this.directOrganizationMemberships.clear();
        } else {
            this.directOrganizationMemberships = new HashSet<>();
        }
        if (list != null) {
            this.directOrganizationMemberships.addAll(list);
        }
    }

    /**
     * Replies the list of memberships that have this organization as super organization.
     *
     * @return the list of memberships.
     * @since 4.0
     */
    public Set<Membership> getSuperOrganizationMemberships() {
        if (this.superOrganizationMemberships == null) {
            this.superOrganizationMemberships = new HashSet<>();
        }
        return this.superOrganizationMemberships;
    }

    /**
     * Change the list of memberships that have this organization as super organization.
     *
     * @param list the list of memberships.
     * @since 4.0
     */
    public void setSuperOrganizationMemberships(Set<Membership> list) {
        if (this.superOrganizationMemberships != null) {
            this.superOrganizationMemberships.clear();
        } else {
            this.superOrganizationMemberships = new HashSet<>();
        }
        if (list != null) {
            this.superOrganizationMemberships.addAll(list);
        }
    }

    /**
     * Replies the list of projects that have this organization as coordinator.
     *
     * @return the list of projects.
     * @since 4.0
     */
    public Set<Project> getCoordinatedProjects() {
        if (this.coordinatedProjects == null) {
            this.coordinatedProjects = new HashSet<>();
        }
        return this.coordinatedProjects;
    }

    /**
     * Change the list of projects that have this organization as coordinator.
     *
     * @param list the list of projects.
     * @since 4.0
     */
    public void setCoordinatedProjects(Set<Project> list) {
        if (this.coordinatedProjects != null) {
            this.coordinatedProjects.clear();
        } else {
            this.coordinatedProjects = new HashSet<>();
        }
        if (list != null) {
            this.coordinatedProjects.addAll(list);
        }
    }

    /**
     * Replies the list of projects that have this organization as LEAR.
     *
     * @return the list of projects.
     * @since 4.0
     */
    public Set<Project> getLearOrganizationProjects() {
        if (this.learOrganizationProjects == null) {
            this.learOrganizationProjects = new HashSet<>();
        }
        return this.learOrganizationProjects;
    }

    /**
     * Change the list of projects that have this organization as LEAR.
     *
     * @param list the list of projects.
     * @since 4.0
     */
    public void setLearOrganizationProjects(Set<Project> list) {
        if (this.learOrganizationProjects != null) {
            this.learOrganizationProjects.clear();
        } else {
            this.learOrganizationProjects = new HashSet<>();
        }
        if (list != null) {
            this.learOrganizationProjects.addAll(list);
        }
    }

    /**
     * Replies the list of projects that have this organization as local organization.
     *
     * @return the list of projects.
     * @since 4.0
     */
    public Set<Project> getLocalOrganizationProjects() {
        if (this.localOrganizationProjects == null) {
            this.localOrganizationProjects = new HashSet<>();
        }
        return this.localOrganizationProjects;
    }

    /**
     * Change the list of projects that have this organization as local organization.
     *
     * @param list the list of projects.
     * @since 4.0
     */
    public void setLocalOrganizationProjects(Set<Project> list) {
        if (this.localOrganizationProjects != null) {
            this.localOrganizationProjects.clear();
        } else {
            this.localOrganizationProjects = new HashSet<>();
        }
        if (list != null) {
            this.localOrganizationProjects.addAll(list);
        }
    }

    /**
     * Replies the list of projects that have this organization as super organization.
     *
     * @return the list of projects.
     * @since 4.0
     */
    public Set<Project> getSuperOrganizationProjects() {
        if (this.superOrganizationProjects == null) {
            this.superOrganizationProjects = new HashSet<>();
        }
        return this.superOrganizationProjects;
    }

    /**
     * Change the list of projects that have this organization as super organization.
     *
     * @param list the list of projects.
     * @since 4.0
     */
    public void setSuperOrganizationProjects(Set<Project> list) {
        if (this.superOrganizationProjects != null) {
            this.superOrganizationProjects.clear();
        } else {
            this.superOrganizationProjects = new HashSet<>();
        }
        if (list != null) {
            this.superOrganizationProjects.addAll(list);
        }
    }

    /**
     * Replies the list of projects that have this organization as other partner.
     *
     * @return the list of projects.
     * @since 4.0
     */
    public Set<Project> getOtherPartnerProjects() {
        if (this.otherPartnerProjects == null) {
            this.otherPartnerProjects = new HashSet<>();
        }
        return this.otherPartnerProjects;
    }

    /**
     * Change the list of projects that have this organization as other partner.
     *
     * @param list the list of projects.
     * @since 4.0
     */
    public void setOtherPartnerProjects(Set<Project> list) {
        if (this.otherPartnerProjects != null) {
            this.otherPartnerProjects.clear();
        } else {
            this.otherPartnerProjects = new HashSet<>();
        }
        if (list != null) {
            this.otherPartnerProjects.addAll(list);
        }
    }

    /**
     * Replies the organizations that are employers.
     *
     * @return the list of employing organizations.
     * @since 4.0
     */
    public Set<ResearchOrganization> getEmployingOrganizations() {
        if (getType().isEmployer()) {
            return Collections.singleton(this);
        }
        final var employers = new HashSet<ResearchOrganization>();
        final var treated = new TreeSet<>(EntityUtils.getPreferredResearchOrganizationComparator());
        final var candidates = new LinkedList<ResearchOrganization>();
        candidates.add(this);
        while (!candidates.isEmpty()) {
            final var candidate = candidates.removeFirst();
            if (treated.add(candidate)) {
                if (candidate.getType().isEmployer()) {
                    employers.add(candidate);
                } else {
                    candidates.addAll(candidate.getSuperOrganizations());
                }
            }
        }
        return employers;
    }

}
