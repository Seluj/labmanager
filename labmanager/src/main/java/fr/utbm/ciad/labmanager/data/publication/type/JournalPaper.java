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

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.publication.AbstractJournalBasedPublication;
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
 * Paper in a journal.
 *
 * <p>This type is equivalent to the BibTeX types: {@code article}, {@code incollection}.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@DiscriminatorValue("JournalPaper")
public class JournalPaper extends AbstractJournalBasedPublication {

    private static final long serialVersionUID = 3238033613426660222L;

    /**
     * Number that represent the volume of the journal.
     */
    @Column
    private String volume;

    /**
     * Number of the journal.
     */
    @Column
    private String number;

    /**
     * Range of pages that corresponds to the paper in the journal.
     */
    @Column
    private String pages;

    /**
     * Name of series of the journal.
     */
    @Column
    private String series;

    /**
     * Construct a journal paper with the given values.
     *
     * @param publication the publication to copy.
     * @param volume      the volume of the journal.
     * @param number      the number of the journal.
     * @param pages       the pages in the journal.
     * @param series      the series of the journal.
     */
    public JournalPaper(Publication publication, String volume, String number, String pages, String series) {
        super(publication);
        this.volume = volume;
        this.number = number;
        this.pages = pages;
        this.series = series;
    }

    /**
     * Construct an empty journal paper.
     */
    public JournalPaper() {
        //
    }


    @Override
    public int hashCode() {
        if (getId() != 0) {
            return Long.hashCode(getId());
        }
        var h = super.hashCode();
        h = HashCodeUtils.add(h, this.volume);
        h = HashCodeUtils.add(h, this.number);
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
        final var other = (JournalPaper) obj;
        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }
        return super.equals(other)
                && Objects.equals(this.volume, other.volume)
                && Objects.equals(this.number, other.number);
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
        if (!Strings.isNullOrEmpty(getSeries())) {
            consumer.accept("series", getSeries()); //$NON-NLS-1$
        }
    }

    @Override
    public String getWherePublishedShortDescription() {
        final var buf = new StringBuilder();
        buf.append(getJournal().getJournalName());
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
        if (!Strings.isNullOrEmpty(getJournal().getPublisher())) {
            buf.append(", "); //$NON-NLS-1$
            buf.append(getJournal().getPublisher());
        }
        if (!Strings.isNullOrEmpty(getJournal().getISBN())) {
            buf.append(", ISBN "); //$NON-NLS-1$
            buf.append(getJournal().getISBN());
        }
        if (!Strings.isNullOrEmpty(getJournal().getISSN())) {
            buf.append(", ISSN "); //$NON-NLS-1$
            buf.append(getJournal().getISSN());
        }
        return buf.toString();
    }

    /**
     * Replies the volume number of the journal in which the publication was published.
     *
     * @return the volume number.
     */
    public String getVolume() {
        return this.volume;
    }

    /**
     * Change the volume number of the journal in which the publication was published.
     *
     * @param volume the volume number.
     */
    public void setVolume(String volume) {
        this.volume = Strings.emptyToNull(volume);
    }

    /**
     * Replies the number of the journal in which the publication was published.
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
     * Replies the page range in the journal in which the publication was published.
     *
     * @return the number.
     */
    public String getPages() {
        return this.pages;
    }

    /**
     * Change the page range in the journal in which the publication was published.
     *
     * @param range the page range.
     */
    public void setPages(String range) {
        this.pages = Strings.emptyToNull(range);
    }

    /**
     * Replies the series of the journal in which the publication was published.
     *
     * @return the series.
     */
    public String getSeries() {
        return this.series;
    }

    /**
     * Change the series of the journal in which the publication was published.
     *
     * @param series the series.
     */
    public void setSeries(String series) {
        this.series = Strings.emptyToNull(series);
    }

}
