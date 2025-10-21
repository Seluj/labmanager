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

package fr.utbm.ciad.labmanager.data.conference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.QualityAnnualIndicators;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.util.Locale;

/**
 * History of the quality indicators for a conference.
 *
 * @author $Author: sgalland$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Entity
@Table(name = "ConferenceAnnualIndicators")
public class ConferenceQualityAnnualIndicators implements QualityAnnualIndicators, AttributeProvider {

    private static final long serialVersionUID = 1212711963054404563L;

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
     * CORE Index.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private CoreRanking coreIndex;

    /**
     * Default constructor.
     */
    public ConferenceQualityAnnualIndicators() {
        this.referenceYear = 0;
        this.coreIndex = null;
    }

    /**
     * Construct an history with the given values.
     *
     * @param year      the year of the entry.
     * @param coreIndex the CORE index.
     */
    public ConferenceQualityAnnualIndicators(int year, CoreRanking coreIndex) {
        this.referenceYear = year;
        this.coreIndex = coreIndex;
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the database id.
     *
     * @param id the new database id;
     * @since 4.0
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
        final var other = (ConferenceQualityAnnualIndicators) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return this.referenceYear == other.referenceYear;
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getCoreIndex() != null) {
            consumer.accept("coreIndex", getCoreIndex()); //$NON-NLS-1$
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
     * Replies the index of the conference from CORE source.
     *
     * @return the index, or {@code null} if not defined.
     */
    public CoreRanking getCoreIndex() {
        return this.coreIndex;
    }

    /**
     * Change the index of the conference from CORE source.
     *
     * @param index the CORE index.
     */
    public void setCoreIndex(CoreRanking index) {
        this.coreIndex = index;
    }

    /**
     * Change the index of the conference from CORE source.
     *
     * @param index the CORE index.
     */
    public void setCoreIndex(String index) {
        if (Strings.isNullOrEmpty(index)) {
            setCoreIndex((CoreRanking) null);
        } else {
            setCoreIndex(CoreRanking.valueOfCaseInsensitive(index));
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

    @Override
    public boolean isSignificant() {
        return this.coreIndex != null;
    }

}
