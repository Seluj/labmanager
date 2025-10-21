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
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.publication.AbstractConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.util.Locale;

/**
 * Keynote in a conference or a workshop.
 *
 * <p>This type has no equivalent in the BibTeX types. It is inspired by: {@code inproceedings}, {@code conference}.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@DiscriminatorValue("KeyNote")
public class KeyNote extends AbstractConferenceBasedPublication {

    private static final long serialVersionUID = 6305786419717116761L;

    /**
     * List of names of the editors of the proceedings of the event.
     * The list of names is usually a sequence of names separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String editors;

    /**
     * Name of the institution that has organized the event.
     */
    @Column
    private String organization;

    /**
     * Geographical location of the event. Usually, it is a city and a country.
     */
    @Column
    private String address;

    /**
     * Construct a conference paper with the given values.
     *
     * @param publication                the publication to copy.
     * @param conferenceOccurrenceNumber the number of the conference's occurrence.
     * @param editors                    the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
     *                                   with {@code AND}.
     * @param orga                       the name of the organization institution.
     * @param address                    the geographical location of the event, usually a city and a country.
     */
    public KeyNote(Publication publication, int conferenceOccurrenceNumber, String editors, String orga, String address) {
        super(publication, conferenceOccurrenceNumber);
        this.editors = editors;
        this.organization = orga;
        this.address = address;
    }

    /**
     * Construct an empty conference paper.
     */
    public KeyNote() {
        //
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        super.forEachAttribute(messages, locale, consumer);
        if (!Strings.isNullOrEmpty(getEditors())) {
            consumer.accept("editors", getEditors()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getOrganization())) {
            consumer.accept("organization", getOrganization()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            consumer.accept("address", getAddress()); //$NON-NLS-1$
        }
    }

    @Override
    @JsonIgnore
    public String getWherePublishedShortDescription() {
        final var buf = new StringBuilder();
        buf.append(getPublicationTarget());
        if (!Strings.isNullOrEmpty(getOrganization())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getOrganization());
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getAddress());
        }
        if (!Strings.isNullOrEmpty(getConference().getISBN())) {
            buf.append(", ISBN "); //$NON-NLS-1$
            buf.append(getConference().getISBN());
        }
        if (!Strings.isNullOrEmpty(getConference().getISSN())) {
            buf.append(", ISSN "); //$NON-NLS-1$
            buf.append(getConference().getISSN());
        }
        return buf.toString();
    }

    /**
     * Replies the editors of the proceedings of the event (conference or workshop) in which the publication was published.
     * The editors is usually a list of names, separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
     *
     * @return the editor names.
     */
    public String getEditors() {
        return this.editors;
    }

    /**
     * Change the editors of the proceedings of the event (conference or workshop) in which the publication was published.
     * The editors is usually a list of names, separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
     *
     * @param names the editor names.
     */
    public void setEditors(String names) {
        this.editors = Strings.emptyToNull(names);
    }

    /**
     * Replies the name of the organization of the event (conference or workshop) in which the publication was published.
     *
     * @return the organization name.
     */
    public String getOrganization() {
        return this.organization;
    }

    /**
     * Change the name of the organization of the event (conference or workshop) in which the publication was published.
     *
     * @param name the organization name.
     */
    public void setOrganization(String name) {
        this.organization = Strings.emptyToNull(name);
    }

    /**
     * Replies the geographic location of the event (conference or workshop) in which the publication was published.
     * The location is usually a city and a country.
     *
     * @return the address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Change the geographic location of the event (conference or workshop) in which the publication was published.
     * The location is usually a city and a country.
     *
     * @param address the address.
     */
    public void setAddress(String address) {
        this.address = Strings.emptyToNull(address);
    }

}
