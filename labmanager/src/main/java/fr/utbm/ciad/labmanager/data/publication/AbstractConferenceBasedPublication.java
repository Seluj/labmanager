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

package fr.utbm.ciad.labmanager.data.publication;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

/**
 * Abstract publication that is related to a conference.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Entity
public abstract class AbstractConferenceBasedPublication extends Publication implements ConferenceBasedPublication {

    private static final long serialVersionUID = -3189258044589563034L;

    /**
     * Reference to the conference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Conference conference;

    /**
     * Number of the conference occurrence.
     *
     * @since 3.6
     */
    @Column
    private int conferenceOccurrenceNumber;

    /**
     * Constructor by copy.
     *
     * @param publication                the publication to copy.
     * @param conferenceOccurrenceNumber the number of the conference's occurrence.
     */
    public AbstractConferenceBasedPublication(Publication publication, int conferenceOccurrenceNumber) {
        super(publication);
        this.conferenceOccurrenceNumber = conferenceOccurrenceNumber < 0 ? 0 : conferenceOccurrenceNumber;
    }

    /**
     * Construct an empty publication.
     */
    public AbstractConferenceBasedPublication() {
        //
    }

    private static void appendConferenceName(StringBuilder buf, Conference conference, int year) {
        buf.append(conference.getName());
        if (!Strings.isNullOrEmpty(conference.getAcronym())) {
            buf.append(" (").append(conference.getAcronym()).append("-").append(year % 100).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    @Override
    public int hashCode() {
        if (getId() != 0) {
            return Long.hashCode(getId());
        }
        var h = super.hashCode();
        h = HashCodeUtils.add(h, this.conference);
        h = HashCodeUtils.add(h, this.conferenceOccurrenceNumber);
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
        final var other = (AbstractConferenceBasedPublication) obj;
        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }
        return super.equals(other)
                && Objects.equals(this.conference, other.conference)
                && this.conferenceOccurrenceNumber == other.conferenceOccurrenceNumber;
    }

    @Override
    public void forEachAttribute(AttributeConsumer consumer) throws IOException {
        super.forEachAttribute(consumer);
        if (getConferenceOccurrenceNumber() > 0) {
            consumer.accept("conferenceOccurrenceNumber", Integer.valueOf(getConferenceOccurrenceNumber())); //$NON-NLS-1$
        }
        if (isRanked()) {
            consumer.accept("coreRanking", getCoreRanking()); //$NON-NLS-1$
        }
    }

    @Override
    public int getConferenceOccurrenceNumber() {
        return this.conferenceOccurrenceNumber;
    }

    @Override
    public void setConferenceOccurrenceNumber(int number) {
        if (number > 0) {
            this.conferenceOccurrenceNumber = number;
        } else {
            this.conferenceOccurrenceNumber = 0;
        }
    }

    /**
     * Change the number of the occurrence of the conference in which the publication was published.
     * <p>
     * In the example of the "14th International Conference on Systems", the occurrence number is "14".
     *
     * @param number the conference occurrence number.
     * @see #setConference(Conference)
     */
    public final void setConferenceOccurrenceNumber(Number number) {
        if (number == null) {
            setConferenceOccurrenceNumber(0);
        } else {
            setConferenceOccurrenceNumber(number.intValue());
        }
    }

    @Override
    public String getPublicationTarget() {
        final var buf = new StringBuilder();
        final var conference = getConference();
        if (conference != null) {
            final var number = getConferenceOccurrenceNumber();
            if (number > 1) {
                buf.append(number).append(ConferenceBasedPublication.getNumberDecorator(number, getMajorLanguage())).append(" "); //$NON-NLS-1$
            }
            final var year = getPublicationYear();
            appendConferenceName(buf, conference, year);
            Conference enclosingConference = conference.getEnclosingConference();
            if (enclosingConference != null) {
                final var identifiers = new HashSet<Long>();
                identifiers.add(Long.valueOf(conference.getId()));
                while (enclosingConference != null && identifiers.add(Long.valueOf(enclosingConference.getId()))) {
                    buf.append(", ").append("in").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    appendConferenceName(buf, enclosingConference, year);
                    enclosingConference = enclosingConference.getEnclosingConference();
                }
            }
        }
        return buf.toString();
    }

    @Override
    public Conference getConference() {
        return this.conference;
    }

    @Override
    public void setConference(Conference conference) {
        this.conference = conference;
    }

    @Override
    public CoreRanking getCoreRanking() {
        final var conference = getConference();
        if (conference != null) {
            return conference.getCoreIndexByYear(getPublicationYear());
        }
        return CoreRanking.NR;
    }

    @Override
    public boolean isRanked() {
        final var conference = getConference();
        if (conference != null) {
            return conference.getCoreIndexByYear(getPublicationYear()) != CoreRanking.NR;
        }
        return false;
    }

    /**
     * Replies the ISBN number that is associated to this publication.
     * This functions delegates to the conference.
     *
     * @return the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     * @deprecated See {@link Conference#getISBN()}
     */
    @Override
    @Deprecated(since = "4.0")
    public String getISBN() {
        if (this.conference != null) {
            return this.conference.getISBN();
        }
        return null;
    }

    /**
     * Change the ISBN number that is associated to this publication.
     * This functions delegates to the conference.
     *
     * @param isbn the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     * @deprecated See {@link Conference#setISBN(String)}
     */
    @Override
    @Deprecated(since = "4.0")
    public void setISBN(String isbn) {
        if (this.conference != null) {
            this.conference.setISBN(isbn);
        }
    }

    /**
     * Replies the ISSN number that is associated to this publication.
     * This functions delegates to the conference.
     *
     * @return the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     * @deprecated See {@link Conference#getISSN()}
     */
    @Override
    @Deprecated(since = "4.0")
    public String getISSN() {
        if (this.conference != null) {
            return this.conference.getISSN();
        }
        return null;
    }

    /**
     * Change the ISSN number that is associated to this publication.
     * This functions delegates to the conference.
     *
     * @param issn the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     * @deprecated See {@link Conference#setISSN(String)}
     */
    @Override
    @Deprecated(since = "4.0")
    public final void setISSN(String issn) {
        if (this.conference != null) {
            this.conference.setISSN(issn);
        }
    }

    @Override
    public Boolean getOpenAccess() {
        final var conference = getConference();
        if (conference != null) {
            return conference.getOpenAccess();
        }
        return null;
    }

}
