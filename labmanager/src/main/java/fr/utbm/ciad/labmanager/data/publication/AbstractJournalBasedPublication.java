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
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.ranking.JournalRankingSystem;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Abstract publication that is related to a journal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Entity
public abstract class AbstractJournalBasedPublication extends Publication implements JournalBasedPublication {

    private static final long serialVersionUID = 5389842889949388812L;

    /**
     * Reference to the journal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Journal journal;

    /**
     * Constructor by copy.
     *
     * @param publication the publication to copy.
     */
    public AbstractJournalBasedPublication(Publication publication) {
        super(publication);
    }

    /**
     * Construct an empty publication.
     */
    public AbstractJournalBasedPublication() {
        //
    }

    @Override
    public int hashCode() {
        if (getId() != 0) {
            return Long.hashCode(getId());
        }
        var h = super.hashCode();
        h = HashCodeUtils.add(h, this.journal);
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
        final var other = (AbstractJournalBasedPublication) obj;
        if (getId() != 0 && other.getId() != 0) {
            return getId() == other.getId();
        }
        return super.equals(other)
                && Objects.equals(this.journal, other.journal);
    }

    @Override
    public void forEachAttribute(AttributeConsumer consumer) throws IOException {
        super.forEachAttribute(consumer);
        if (isRanked()) {
            consumer.accept("scimagoQIndex", getScimagoQIndex()); //$NON-NLS-1$
            consumer.accept("scimagoCategory", getScimagoCategory()); //$NON-NLS-1$
            consumer.accept("wosQIndex", getWosQIndex()); //$NON-NLS-1$
            consumer.accept("wosCategory", getWosCategory()); //$NON-NLS-1$
            consumer.accept("impactFactor", Float.valueOf(getImpactFactor())); //$NON-NLS-1$
        }
    }

    @Override
    public String getPublicationTarget() {
        final var buf = new StringBuilder();
        final var journal = getJournal();
        if (journal != null) {
            buf.append(journal.getJournalName());
            if (!Strings.isNullOrEmpty(journal.getPublisher())) {
                buf.append(", "); //$NON-NLS-1$
                buf.append(journal.getPublisher());
            }
        }
        return buf.toString();
    }

    @Override
    public Journal getJournal() {
        return this.journal;
    }

    @Override
    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    @Override
    public QuartileRanking getScimagoQIndex() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getScimagoQIndexByYear(getPublicationYear());
        }
        return QuartileRanking.NR;
    }

    @Override
    public String getScimagoCategory() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getScimagoCategory();
        }
        return null;
    }

    @Override
    public QuartileRanking getWosQIndex() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getWosQIndexByYear(getPublicationYear());
        }
        return QuartileRanking.NR;
    }

    @Override
    public String getWosCategory() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getWosCategory();
        }
        return null;
    }

    @Override
    public float getImpactFactor() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getImpactFactorByYear(getPublicationYear());
        }
        return 0f;
    }

    @Override
    public boolean isRanked() {
        return isRanked(null);
    }

    @Override
    public boolean isRanked(JournalRankingSystem rankingSystem) {
        final var journal = getJournal();
        if (journal != null) {
            if (rankingSystem != null) {
                switch (rankingSystem) {
                    case SCIMAGO:
                        return journal.getScimagoQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
                    case WOS:
                        return journal.getWosQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
                    default:
                }
            }
            return journal.getScimagoQIndexByYear(getPublicationYear()) != QuartileRanking.NR
                    || journal.getWosQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
        }
        return false;
    }

    @Override
    public PublicationCategory getCategory(JournalRankingSystem rankingSystem) {
        final var rankingSystem0 = rankingSystem == null ? JournalRankingSystem.getDefault() : rankingSystem;
        final Supplier<Boolean> rank;
        switch (rankingSystem0) {
            case SCIMAGO:
                rank = () -> {
                    final var r = getScimagoQIndex();
                    return Boolean.valueOf(r != QuartileRanking.NR);
                };
                break;
            case WOS:
                rank = () -> {
                    final var r = getWosQIndex();
                    return Boolean.valueOf(r != QuartileRanking.NR);
                };
                break;
            default:
                throw new IllegalStateException();
        }
        return getCategoryWithSupplier(rank);
    }

    /**
     * Replies the ISBN number that is associated to this publication.
     * This functions delegates to the journal.
     *
     * @return the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     * @deprecated See {@link Journal#getISBN()}
     */
    @Override
    @Deprecated(since = "2.0.0")
    public String getISBN() {
        if (this.journal != null) {
            return this.journal.getISBN();
        }
        return null;
    }

    /**
     * Change the ISBN number that is associated to this publication.
     * This functions delegates to the journal.
     *
     * @param isbn the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     * @deprecated See {@link Journal#setISBN(String)}
     */
    @Override
    @Deprecated(since = "2.0.0")
    public void setISBN(String isbn) {
        if (this.journal != null) {
            this.journal.setISBN(isbn);
        }
    }

    /**
     * Replies the ISSN number that is associated to this publication.
     * This functions delegates to the journal.
     *
     * @return the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     * @deprecated See {@link Journal#getISSN()}
     */
    @Override
    @Deprecated(since = "2.0.0")
    public String getISSN() {
        if (this.journal != null) {
            return this.journal.getISSN();
        }
        return null;
    }

    /**
     * Change the ISSN number that is associated to this publication.
     * This functions delegates to the journal.
     *
     * @param issn the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     * @deprecated See {@link Journal#setISSN(String)}
     */
    @Override
    @Deprecated(since = "2.0.0")
    public final void setISSN(String issn) {
        if (this.journal != null) {
            this.journal.setISSN(issn);
        }
    }

    @Override
    public Boolean getOpenAccess() {
        final var journal = getJournal();
        if (journal != null) {
            return journal.getOpenAccess();
        }
        return null;
    }

}
