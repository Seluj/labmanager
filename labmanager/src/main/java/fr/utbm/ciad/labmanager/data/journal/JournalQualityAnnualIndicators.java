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

package fr.utbm.ciad.labmanager.data.journal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.QualityAnnualIndicators;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.util.Locale;

/**
 * History of the quality indicators for a journal.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "JournalAnnualIndicators")
public class JournalQualityAnnualIndicators implements QualityAnnualIndicators, AttributeProvider {

    private static final long serialVersionUID = -3671513001937890573L;

    /**
     * Identifier for the history entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private long id;

    /**
     * Year for the entry.
     */
    @Column
    @ColumnDefault("0")
    @JsonIgnore
    private int referenceYear;

    /**
     * Scimargo Q-Index.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private QuartileRanking scimagoQIndex;

    /**
     * JCR/Web-Of-Science Q-Index.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private QuartileRanking wosQIndex;

    /**
     * Impact factor.
     */
    @Column
    @ColumnDefault("0")
    private float impactFactor;

    /**
     * Default constructor.
     */
    public JournalQualityAnnualIndicators() {
        this.referenceYear = 0;
        this.scimagoQIndex = null;
        this.wosQIndex = null;
        this.impactFactor = 0;
    }

    /**
     * Construct an history with the given values.
     *
     * @param year            the year of the entry.
     * @param scimagoQuartile the Scimargo Q-index.
     * @param wosQuartile     the JCR/Web-Of-Science Q-Index.
     * @param impactFactor    the journal impact factor.
     */
    public JournalQualityAnnualIndicators(int year, QuartileRanking scimagoQuartile,
                                          QuartileRanking wosQuartile, float impactFactor) {
        this.referenceYear = year;
        this.scimagoQIndex = scimagoQuartile;
        this.wosQIndex = wosQuartile;
        this.impactFactor = impactFactor;
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the database identifier.
     *
     * @param id the new database identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.referenceYear);
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
        final var other = (JournalQualityAnnualIndicators) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return this.referenceYear == other.referenceYear;
    }

    /**
     * {@inheritDoc}
     * <p>The attributes that are not considered by this function are:<ul>
     * </ul>
     */
    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getScimagoQIndex() != null) {
            consumer.accept("scimagoQIndex", getScimagoQIndex()); //$NON-NLS-1$
        }
        if (getWosQIndex() != null) {
            consumer.accept("wosQIndex", getWosQIndex()); //$NON-NLS-1$
        }
        if (getImpactFactor() > 0f) {
            consumer.accept("impactFactor", Float.valueOf(getImpactFactor())); //$NON-NLS-1$
        }
        if (getReferenceYear() > 0) {
            consumer.accept("referenceYear", Integer.valueOf(getReferenceYear())); //$NON-NLS-1$
        }
    }

    @Override
    public int getReferenceYear() {
        return this.referenceYear;
    }

    @Override
    public void setReferenceYear(int year) {
        this.referenceYear = year;
    }

    /**
     * Replies the Q-Index of the journal from Scimago source.
     *
     * @return the Q-Index, or {@code null} if not defined.
     */
    public QuartileRanking getScimagoQIndex() {
        return this.scimagoQIndex;
    }

    /**
     * Change the Q-Index of the journal from Scimago source.
     *
     * @param quartile the Q-Index.
     */
    public void setScimagoQIndex(QuartileRanking quartile) {
        this.scimagoQIndex = quartile;
    }

    /**
     * Change the Q-Index of the journal from Scimago source.
     *
     * @param quartile the Q-Index.
     */
    public final void setScimagoQIndex(String quartile) {
        if (Strings.isNullOrEmpty(quartile)) {
            setScimagoQIndex((QuartileRanking) null);
        } else {
            setScimagoQIndex(QuartileRanking.valueOfCaseInsensitive(quartile));
        }
    }

    /**
     * Replies the Q-Index of the journal from JCR/Web-Of-Science source.
     *
     * @return the Q-Index, or {@code null} if not defined.
     */
    public QuartileRanking getWosQIndex() {
        return this.wosQIndex;
    }

    /**
     * Change the Q-Index of the journal from JCR/Web-Of-Science source.
     *
     * @param quartile the Q-Index.
     */
    public void setWosQIndex(QuartileRanking quartile) {
        this.wosQIndex = quartile;
    }

    /**
     * Change the Q-Index of the journal from JCR/Web-Of-Science source.
     *
     * @param quartile the Q-Index.
     */
    public final void setWosQIndex(String quartile) {
        if (Strings.isNullOrEmpty(quartile)) {
            setWosQIndex((QuartileRanking) null);
        } else {
            setWosQIndex(QuartileRanking.valueOfCaseInsensitive(quartile));
        }
    }

    /**
     * Replies the impact factor of the journal.
     *
     * @return the impact factor, or {@code 0} if not defined
     */
    public float getImpactFactor() {
        return this.impactFactor;
    }

    /**
     * Change the impact factor of the journal.
     *
     * @param impactFactor the impact factor that is a positive number.
     */
    public void setImpactFactor(float impactFactor) {
        if (impactFactor >= 0f) {
            this.impactFactor = impactFactor;
        } else {
            this.impactFactor = 0f;
        }
    }

    /**
     * Change the impact factor of the journal.
     *
     * @param impactFactor the impact factor that is a positive number.
     */
    public final void setImpactFactor(Number impactFactor) {
        if (impactFactor == null) {
            setImpactFactor(0f);
        } else {
            setImpactFactor(impactFactor.floatValue());
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

    @Override
    public boolean isSignificant() {
        return this.scimagoQIndex != null || this.wosQIndex != null || this.impactFactor > 0f;
    }

}
