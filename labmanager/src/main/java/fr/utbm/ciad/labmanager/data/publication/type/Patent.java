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

package fr.utbm.ciad.labmanager.data.publication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * A patent.
 *
 * <p>This type has no equivalent in the BibTeX types. It is inspired from: {@code misc}, {@code techreport}.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@DiscriminatorValue("Patent")
public class Patent extends Publication {

    private static final long serialVersionUID = -4184253717435746463L;

    /**
     * Name of the institution that is supporting the patent.
     */
    @Column
    private String institution;

    /**
     * Geographical address of the institution that published the patent. It is usually a city and a country.
     */
    @Column
    private String address;

    /**
     * Type of patent.
     */
    @Column
    private String patentType;

    /**
     * Number that is assigned by the institution to the patent.
     */
    @Column
    private String patentNumber;

    /**
     * Construct a patent with the given values.
     *
     * @param publication the publication to copy.
     * @param institution the name of the institution in which the patent was published.
     * @param address     the geographical address of the institution. Usually a city and a country.
     * @param type        the type of patent.
     * @param number      the number of the patent.
     */
    public Patent(Publication publication, String institution, String address, String type, String number) {
        super(publication);
        this.institution = institution;
        this.address = address;
        this.patentType = type;
        this.patentNumber = number;
    }

    /**
     * Construct an empty patent.
     */
    public Patent() {
        //
    }

    @Override
    public int hashCode() {
        if (getId() != 0) {
            return Long.hashCode(getId());
        }
        var h = super.hashCode();
        h = HashCodeUtils.add(h, this.patentNumber);
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
        final var other = (Patent) obj;
        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }
        return super.equals(other)
                && Objects.equals(this.patentNumber, other.patentNumber);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        super.forEachAttribute(messages, locale, consumer);
        if (!Strings.isNullOrEmpty(getInstitution())) {
            consumer.accept("institution", getInstitution()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            consumer.accept("address", getAddress()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPatentNumber())) {
            consumer.accept("patentNumber", getPatentNumber()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPatentType())) {
            consumer.accept("patentType", getPatentType()); //$NON-NLS-1$
        }
    }

    @Override
    @JsonIgnore
    public String getWherePublishedShortDescription() {
        final var buf = new StringBuilder();
        buf.append(getInstitution());
        final var b0 = !Strings.isNullOrEmpty(getPatentNumber());
        final var b1 = !Strings.isNullOrEmpty(getPatentType());
        if (b0 && b1) {
            buf.append(", n. "); //$NON-NLS-1$
            buf.append(getPatentNumber());
            buf.append(" ("); //$NON-NLS-1$
            buf.append(getPatentType());
            buf.append(")"); //$NON-NLS-1$
        } else if (b0) {
            buf.append(", n. "); //$NON-NLS-1$
            buf.append(getPatentNumber());
        } else if (b1) {
            buf.append(getPatentType());
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getAddress());
        }
        if (!Strings.isNullOrEmpty(getISBN())) {
            buf.append(", ISBN "); //$NON-NLS-1$
            buf.append(getISBN());
        }
        if (!Strings.isNullOrEmpty(getISSN())) {
            buf.append(", ISSN "); //$NON-NLS-1$
            buf.append(getISSN());
        }
        return buf.toString();
    }

    @Override
    public String getPublicationTarget() {
        return getInstitution();
    }

    /**
     * Replies the name of the institution in which the patent was published.
     *
     * @return the name of the institution.
     */
    public String getInstitution() {
        return this.institution;
    }

    /**
     * Chage the name of the institution in which the patent was published.
     *
     * @param name the name of the institution.
     */
    public void setInstitution(String name) {
        this.institution = Strings.emptyToNull(name);
    }

    /**
     * Replies the geographical address where the patent was published. It is usually a city and a country.
     *
     * @return the address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Change the geographical address where the patent was published. It is usually a city and a country.
     *
     * @param address the address.
     */
    public void setAddress(String address) {
        this.address = Strings.emptyToNull(address);
    }

    /**
     * Replies the type of patent.
     *
     * @return the type description.
     */
    public String getPatentType() {
        return this.patentType;
    }

    /**
     * Change the type of patent.
     *
     * @param type the type description.
     */
    public void setPatentType(String type) {
        this.patentType = Strings.emptyToNull(type);
    }

    /**
     * Replies the number that was assigned by the institution to the patent.
     *
     * @return the report number.
     */
    public String getPatentNumber() {
        return this.patentNumber;
    }

    /**
     * Replies the number that was assigned by the institution to the patent.
     *
     * @param number the report number.
     */
    public void setPatentNumber(String number) {
        this.patentNumber = Strings.emptyToNull(number);
    }

    @Override
    public boolean isRanked() {
        return false;
    }

}


