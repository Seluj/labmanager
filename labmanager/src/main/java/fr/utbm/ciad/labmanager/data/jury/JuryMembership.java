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

package fr.utbm.ciad.labmanager.data.jury;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import jakarta.persistence.*;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Description of a jury for Master, PhD or HDR.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "JuryMemberships")
public class JuryMembership implements Serializable, AttributeProvider, Comparable<JuryMembership>, IdentifiableEntity {

    private static final long serialVersionUID = -7198696115797240723L;

    /**
     * Identifier of the jury.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Reference to the person.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    /**
     * Type of jury membership.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private JuryMembershipType type;

    /**
     * Type of defense.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private JuryType defenseType;

    /**
     * Date of the jury.
     */
    @Column
    private LocalDate date;

    /**
     * Reference to the person.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person candidate;

    /**
     * Name of the university of the person who passes the exam.
     */
    @Column
    private String university;

    /**
     * The country of the organization.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode country = CountryCode.getDefault();

    /**
     * Title of the works.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String title;

    /**
     * Names of the promoters or directors of the candidate.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Person> promoters;

    /**
     * Construct a jury with the given values.
     *
     * @param person      the person.
     * @param type        the type of membership for the person.
     * @param defenseType the type of defense.
     * @param date        the date of the jury.
     * @param title       the title of the evaluated works.
     * @param candidate   the name of the person who passes the exam.
     * @param university  the name of the university of the candidate.
     * @param country     the country of the university.
     * @param promoters   the names of the promoters or directors of the candidate.
     */
    public JuryMembership(Person person, JuryMembershipType type, JuryType defenseType, LocalDate date, String title,
                          Person candidate, String university, CountryCode country, List<Person> promoters) {
        this.person = person;
        this.type = type;
        this.defenseType = defenseType;
        this.date = date;
        this.title = title;
        this.candidate = candidate;
        this.university = university;
        this.country = country;
        this.promoters = promoters;
    }

    /**
     * Construct an empty jury.
     */
    public JuryMembership() {
        //
    }

    private static String buildLongTypeLabelKey(JuryMembershipType positionType, JuryType defenseType, boolean isFrance, Gender gender) {
        final var key = new StringBuilder("juryMembership."); //$NON-NLS-1$
        key.append(defenseType.name());
        key.append("_"); //$NON-NLS-1$
        key.append(positionType.name());
        if (isFrance) {
            key.append("_fr"); //$NON-NLS-1$
        } else {
            key.append("_other"); //$NON-NLS-1$
        }
        var g = gender;
        if (g == null || g == Gender.NOT_SPECIFIED) {
            g = Gender.OTHER;
        }
        key.append("_"); //$NON-NLS-1$
        key.append(g.name());
        return key.toString();
    }

    /**
     * Replies all the localization key for the long label that corresponds to the type of this jury membership.
     * The keys are provided from the most important to the less important.
     *
     * @param gender the gender of the person.
     * @return the ordering index of each key.
     * @see #getLongTypeLabelKey
     */
    public static Map<String, Integer> getAllLongTypeLabelKeys(Gender gender) {
        final var keys = new TreeMap<String, Integer>();
        final var index = new MutableInt();
        buildLongTypeLabelKeys(keys, index, false, gender);
        buildLongTypeLabelKeys(keys, index, true, gender);
        return keys;
    }

    private static void buildLongTypeLabelKeys(Map<String, Integer> keys, MutableInt index, boolean isFrance, Gender gender) {
        for (final var positionType : JuryMembershipType.values()) {
            for (final var defenseType : JuryType.values()) {
                final var key = buildLongTypeLabelKey(positionType, defenseType, isFrance, gender);
                keys.put(key, index.getValue());
                index.increment();
            }
        }
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.candidate);
        h = HashCodeUtils.add(h, this.defenseType);
        h = HashCodeUtils.add(h, this.person);
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
        final var other = (JuryMembership) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.candidate, other.candidate)
                && Objects.equals(this.defenseType, other.defenseType)
                && Objects.equals(this.person, other.person);
    }

    @Override
    public int compareTo(JuryMembership o) {
        return EntityUtils.getPreferredJuryMembershipComparator().compare(this, o);
    }

    /**
     * {@inheritDoc}
     * <p>The attributes that are not considered by this function are:<ul>
     * <li>{@code id}</li>
     * <li>{@code person}</li>
     * <li>{@code researchOrganization}</li>
     * </ul>
     */
    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getType() != null) {
            consumer.accept("type", getType()); //$NON-NLS-1$
        }
        if (getDefenseType() != null) {
            consumer.accept("defenseType", getDefenseType()); //$NON-NLS-1$
        }
        if (getDate() != null) {
            consumer.accept("date", getDate()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getTitle())) {
            consumer.accept("title", getTitle()); //$NON-NLS-1$
        }
        if (getCandidate() != null) {
            consumer.accept("candidate", getCandidate()); //$NON-NLS-1$
        }
        if (getUniversity() != null) {
            consumer.accept("university", getUniversity()); //$NON-NLS-1$
        }
        if (getCountry() != null) {
            consumer.accept("country", getCountry()); //$NON-NLS-1$
            consumer.accept("countryLabel", getCountryDisplayName()); //$NON-NLS-1$
        }
        if (getPromoters() != null && !getPromoters().isEmpty()) {
            consumer.accept("promoters", getPromoters()); //$NON-NLS-1$
        }
        if (getPerson() != null) {
            consumer.accept("person", getPerson()); //$NON-NLS-1$
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the membership identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the person related to this jury.
     *
     * @return the person.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the person related to this jury.
     *
     * @param person the person.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Replies the type of membership in the jury.
     *
     * @return the status.
     */
    public JuryMembershipType getType() {
        return this.type;
    }

    /**
     * Change the type of membership in the jury.
     *
     * @param type the type.
     */
    public void setType(JuryMembershipType type) {
        this.type = type;
    }

    /**
     * Change the type of membership in the jury.
     *
     * @param type the type.
     */
    public final void setType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setType((JuryMembershipType) null);
        } else {
            setType(JuryMembershipType.valueOfCaseInsensitive(type));
        }
    }

    /**
     * Replies the type of defense.
     *
     * @return the type.
     */
    public JuryType getDefenseType() {
        return this.defenseType;
    }

    /**
     * Change the type of defense.
     *
     * @param type the type.
     */
    public void setDefenseType(JuryType type) {
        this.defenseType = type;
    }

    /**
     * Change the type of defense.
     *
     * @param type the type.
     */
    public final void setDefenseType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setDefenseType((JuryType) null);
        } else {
            setDefenseType(JuryType.valueOfCaseInsensitive(type));
        }
    }

    /**
     * Replies the date of the jury.
     *
     * @return the date of the jury.
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Change the date of the jury.
     *
     * @param date the date of the jury.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Change the date of the jury.
     *
     * @param date the date of the jury.
     */
    public final void setDate(String date) {
        if (Strings.isNullOrEmpty(date)) {
            setDate((LocalDate) null);
        } else {
            setDate(LocalDate.parse(date));
        }
    }

    /**
     * Replies the title of the evaluated works.
     *
     * @return the title of the evaluated works.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the evaluated works.
     *
     * @param title the title of the evaluated works.
     */
    public void setTitle(String title) {
        this.title = Strings.emptyToNull(title);
    }

    /**
     * Replies the candidate who passes the exam.
     *
     * @return the name of the candidate.
     */
    public Person getCandidate() {
        return this.candidate;
    }

    /**
     * Change the candidate who passes the exam.
     *
     * @param candidate the name of the candidate.
     */
    public void setCandidate(Person candidate) {
        this.candidate = candidate;
    }

    /**
     * Replies the university who passes the exam.
     *
     * @return the name of the candidate's university.
     */
    public String getUniversity() {
        return this.university;
    }

    /**
     * Change the name of the university of the candidate.
     *
     * @param university the name of the university.
     */
    public void setUniversity(String university) {
        this.university = Strings.emptyToNull(university);
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
     * Replies the promoters or directors of the candidate.
     *
     * @return the names of the promoters or directors of the candidate.
     */
    public List<Person> getPromoters() {
        if (this.promoters == null) {
            return Collections.emptyList();
        }
        return this.promoters;
    }

    /**
     * Change the promoters or directors of the candidate.
     *
     * @param names the names of the promoters or directors of the candidate.
     */
    public void setPromoters(List<Person> names) {
        if (this.promoters == null) {
            this.promoters = new ArrayList<>();
        } else {
            this.promoters.clear();
        }
        if (names != null) {
            this.promoters.addAll(names);
        }
    }

    /**
     * Replies the localization key for the long label that corresponds to the type of this jury membership.
     *
     * @param gender the gender of the person.
     * @return the key.
     * @see #getAllLongTypeLabelKeys
     */
    public String getLongTypeLabelKey(Gender gender) {
        return buildLongTypeLabelKey(getType(), getDefenseType(), getCountry().isFrance(), gender);
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getTitle());
    }

}
