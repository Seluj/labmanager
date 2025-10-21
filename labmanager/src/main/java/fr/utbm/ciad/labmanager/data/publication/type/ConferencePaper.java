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
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.publication.AbstractConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.util.Locale;

/**
 * Paper in a conference or a workshop.
 *
 * <p>This type is equivalent to the BibTeX types: {@code inproceedings}, {@code conference}.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@DiscriminatorValue("ConferencePaper")
public class ConferencePaper extends AbstractConferenceBasedPublication {

    private static final long serialVersionUID = -6530003246095719011L;

    /**
     * Volume number of the proceedings of the event.
     */
    @Column
    private String volume;

    /**
     * Number of the proceedings of the event.
     */
    @Column
    private String number;

    /**
     * Page range of the paper in the proceedings of the event.
     */
    @Column
    private String pages;

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
     * Name of number of the series of the proceedings of the event.
     */
    @Column
    private String series;

    /**
     * Construct a conference paper with the given values.
     *
     * @param publication                the publication to copy.
     * @param conferenceOccurrenceNumber the number of the conference's occurrence.
     * @param volume                     the volume of the journal.
     * @param number                     the number of the journal.
     * @param pages                      the pages in the journal.
     * @param editors                    the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
     *                                   with {@code AND}.
     * @param orga                       the name of the organization institution.
     * @param address                    the geographical location of the event, usually a city and a country.
     * @param series                     the number or the name of the series for the conference proceedings.
     */
    public ConferencePaper(Publication publication, int conferenceOccurrenceNumber, String volume, String number, String pages, String editors,
                           String orga, String address, String series) {
        super(publication, conferenceOccurrenceNumber);
        this.volume = volume;
        this.number = number;
        this.pages = pages;
        this.editors = editors;
        this.organization = orga;
        this.address = address;
        this.series = series;
    }

    /**
     * Construct an empty conference paper.
     */
    public ConferencePaper() {
        //
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        super.forEachAttribute(messages, locale, consumer);
        if (!Strings.isNullOrEmpty(getVolume())) {
            consumer.accept("volume", getVolume()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getNumber())) {
            consumer.accept("number", getNumber()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPages())) {
            consumer.accept("pages", getPages()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getEditors())) {
            consumer.accept("editors", getEditors()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getOrganization())) {
            consumer.accept("organization", getOrganization()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            consumer.accept("address", getAddress()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getSeries())) {
            consumer.accept("series", getSeries()); //$NON-NLS-1$
        }
    }

    @Override
    @JsonIgnore
    public String getWherePublishedShortDescription() {
        final var buf = new StringBuilder();
        buf.append(getPublicationTarget());
        if (!Strings.isNullOrEmpty(getVolume())) {
            buf.append(", vol. "); //$NON-NLS-1$
            buf.append(getVolume());
        }
        if (!Strings.isNullOrEmpty(getNumber())) {
            buf.append(", n. "); //$NON-NLS-1$
            buf.append(getNumber());
        }
        if (!Strings.isNullOrEmpty(getPages())) {
            buf.append(", pp. "); //$NON-NLS-1$
            buf.append(getPages());
        }
        if (!Strings.isNullOrEmpty(getOrganization())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getOrganization());
        }
        if (!Strings.isNullOrEmpty(getAddress())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getAddress());
        }
        final Conference conference = getConference();
        if (conference != null) {
            final String publisher = conference.getPublisher();
            if (!Strings.isNullOrEmpty(publisher)) {
                buf.append(", "); //$NON-NLS-1$
                buf.append(publisher);
            }
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
     * Replies the volume number of the event (conference or workshop) in which the publication was published.
     *
     * @return the volume number.
     */
    public String getVolume() {
        return this.volume;
    }

    /**
     * Change the volume number of the event (conference or workshop) in which the publication was published.
     *
     * @param volume the volume number.
     */
    public void setVolume(String volume) {
        this.volume = Strings.emptyToNull(volume);
    }

    /**
     * Replies the number of the event (conference or workshop) in which the publication was published.
     *
     * @return the number.
     */
    public String getNumber() {
        return this.number;
    }

    /**
     * Change the number of the journal in which the publication was published.
     *
     * @param number the number.
     */
    public void setNumber(String number) {
        this.number = Strings.emptyToNull(number);
    }

    /**
     * Replies the page range in the event (conference or workshop) in which the publication was published.
     *
     * @return the number.
     */
    public String getPages() {
        return this.pages;
    }

    /**
     * Change the page range in the event (conference or workshop) in which the publication was published.
     *
     * @param range the page range.
     */
    public void setPages(String range) {
        this.pages = Strings.emptyToNull(range);
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

    /**
     * Replies the name or the number of series of the proceedings of the event (conference or workshop) in which the publication was published.
     *
     * @return the series.
     */
    public String getSeries() {
        return this.series;
    }

    /**
     * Change the name or the number of series of the proceedings of the event (conference or workshop) in which the publication was published.
     *
     * @param series the series.
     */
    public void setSeries(String series) {
        this.series = Strings.emptyToNull(series);
    }

}
