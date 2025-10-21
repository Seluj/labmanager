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
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Address of an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Entity
@Table(name = "OrgAddresses")
public class OrganizationAddress implements Serializable, JsonSerializable, Comparable<OrganizationAddress>, AttributeProvider, IdentifiableEntity {

    private static final long serialVersionUID = 6630654880175784574L;

    /**
     * Identifier of the address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Symbolic name of the address.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Complementatry information that may appear before the rest.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String complement;

    /**
     * Building number and street in the address.
     */
    @Column(nullable = false, length = EntityConstants.LARGE_TEXT_SIZE)
    private String street;

    /**
     * The ZIP code of the address.
     */
    @Column
    private String zipCode;

    /**
     * City.
     */
    @Column(nullable = false)
    private String city;

    /**
     * Coordinates of the address on a map tool such as Google Map..
     */
    @Column
    private String mapCoordinates;

    /**
     * Link to the Google Map
     */
    @Column
    private String googleMapLink;

    /**
     * URL of a associated background if the address has one.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String pathToBackgroundImage;

    /**
     * Indicates if the address was validated by an authority.
     */
    @Column(nullable = false)
    private boolean validated;

    /**
     * Persons who are associated to the address.
     */
    @ManyToMany(mappedBy = "organizationAddress", fetch = FetchType.LAZY)
    private Set<Membership> memberships = new HashSet<>();

    /**
     * Construct an organization address from the given values.
     *
     * @param id             the identifier of the organization.
     * @param name           the symbolic name of the address.
     * @param complement     the complementary information that may appear before the rest of the address information.
     * @param street         the building number and street name.
     * @param zipCode        the ZIP code.
     * @param city           the city.
     * @param mapCoordinates the coordinates for a mapping tool.
     */
    public OrganizationAddress(int id, String name, String complement, String street, String zipCode, String city, String mapCoordinates) {
        this.id = id;
        this.name = name;
        this.complement = complement;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.mapCoordinates = mapCoordinates;
    }

    /**
     * Construct an empty organization address.
     */
    public OrganizationAddress() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
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
        final var other = (OrganizationAddress) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int compareTo(OrganizationAddress o) {
        return EntityUtils.getPreferredOrganizationAddressComparator().compare(this, o);
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
        if (!Strings.isNullOrEmpty(getName())) {
            consumer.accept("name", getName()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getComplement())) {
            consumer.accept("complementary", getComplement()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getStreet())) {
            consumer.accept("street", getStreet()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getZipCode())) {
            consumer.accept("zipCode", getZipCode()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getCity())) {
            consumer.accept("city", getCity()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getMapCoordinates())) {
            consumer.accept("mapCoordinates", getMapCoordinates()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getMapCoordinates())) {
            consumer.accept("googleMapLink", getGoogleMapLink()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPathToBackgroundImage())) {
            consumer.accept("pathToBackgroundImage", getPathToBackgroundImage()); //$NON-NLS-1$
        }
        consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
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
     * Replies the complementary information that may appear before the other information.
     *
     * @return the complementary information.
     */
    public String getComplement() {
        return this.complement;
    }

    /**
     * Change the complementary information that may appear before the other information.
     *
     * @param complement the complementary information.
     */
    public void setComplement(String complement) {
        this.complement = Strings.emptyToNull(complement);
    }

    /**
     * Replies the building number and street of the address.
     *
     * @return the building number and street.
     */
    public String getStreet() {
        return this.street;
    }

    /**
     * Change the building number and street of the address.
     *
     * @param street the building number and street.
     */
    public void setStreet(String street) {
        this.street = Strings.emptyToNull(street);
    }

    /**
     * Replies the ZIP code of the address.
     *
     * @return the ZIP code.
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * Change the ZIP code of the address.
     *
     * @param zipCode the ZIP code.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = Strings.emptyToNull(zipCode);
    }

    /**
     * Replies the city of the address.
     *
     * @return the city.
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Change the city of the address.
     *
     * @param city the city.
     */
    public void setCity(String city) {
        this.city = Strings.emptyToNull(city);
    }

    /**
     * Replies the coordinates of the address for a mapping system. There coordinates are usually GPS based.
     *
     * @return the map coordinates.
     * @see #getGoogleMapURL()
     */
    public String getMapCoordinates() {
        return this.mapCoordinates;
    }

    /**
     * Changes the coordinates of the address for a mapping system. There coordinates are usually GPS based.
     *
     * @param mapCoordinates the map coordinates.
     */
    public void setMapCoordinates(String mapCoordinates) {
        this.mapCoordinates = Strings.emptyToNull(mapCoordinates);
    }

    /**
     * Replies the Link to the Google Map that corresponds to this address.
     *
     * @return the link to the Google Map of the research organization, or {@code null}.
     * @see #getMapCoordinates()
     * @see #getGoogleMapURL()
     */
    public String getGoogleMapLink() {
        return this.googleMapLink;
    }

    /**
     * Change the Link to the Google Map that corresponds to this address.
     *
     * @param link the link to the Google Map of the research organization, or {@code null}.
     */
    public void setGoogleMapLink(String link) {
        this.googleMapLink = Strings.emptyToNull(link);
    }

    /**
     * Replies the URL to the Google Map that corresponds to this address.
     *
     * @return the URL to the Google Map of the research organization, or {@code null}.
     * @throws MalformedURLException if the URL cannot be built.
     * @see #getMapCoordinates()
     */
    public URL getGoogleMapURL() throws MalformedURLException {
        final var link = getGoogleMapLink();
        if (!Strings.isNullOrEmpty(link)) {
            try {
                return new URI(link).toURL();
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the full address composed by the building number, the street, the ZIP code and the city.
     *
     * @return the full address.
     */
    public String getFullAddress() {
        var separator = " "; //$NON-NLS-1$
        final var builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(getComplement())) {
            builder.append(getComplement());
            separator = ", "; //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getStreet())) {
            if (builder.length() > 0) {
                builder.append(separator);
                separator = " "; //$NON-NLS-1$
            }
            builder.append(getStreet());
        }
        if (!Strings.isNullOrEmpty(getZipCode())) {
            if (builder.length() > 0) {
                builder.append(separator);
                separator = " "; //$NON-NLS-1$
            }
            builder.append(getZipCode());
        }
        if (!Strings.isNullOrEmpty(getCity())) {
            if (builder.length() > 0) {
                builder.append(separator);
                separator = " "; //$NON-NLS-1$
            }
            builder.append(getCity());
        }
        return builder.toString();
    }

    /**
     * Replies the background image for the address.
     *
     * @return the background image.
     */
    public String getPathToBackgroundImage() {
        return this.pathToBackgroundImage;
    }

    /**
     * Change the city of the address.
     *
     * @param path the local path to the background image.
     */
    public void setPathToBackgroundImage(String path) {
        this.pathToBackgroundImage = Strings.emptyToNull(path);
    }

    /**
     * Replies if this address was validated by an authority.
     *
     * @return {@code true} if the address is validated.
     * @since 3.2
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this address was validated by an authority.
     *
     * @param validated {@code true} if the address is validated.
     * @since 3.2
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this address was validated by an authority.
     *
     * @param validated {@code true} if the address is validated.
     * @since 3.2
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getName());
    }

    /**
     * Replies the memberships that are associated to this organization address.
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
     * Change the memberships that are associated to this organization address.
     *
     * @param memberships the memberships associated to this address.
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

}
