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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.publication.AbstractConferenceBasedPublication;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;
import jakarta.persistence.*;
import org.arakhne.afc.util.IntegerList;
import org.arakhne.afc.util.ListUtil;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * Scientific or scientific culture dissemination conference.
 * This class provides all the necessary information and tools for managing the conferences and their quality indicators.
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
@Table(name = "Conferences")
public class Conference extends AbstractContextData implements JsonSerializable, AttributeProvider, IdentifiableEntity {

    private static final long serialVersionUID = -2286554831898694393L;

    /**
     * Identifier of the journal in the database.
     *
     * <p>Using this instead of {@link GenerationType#IDENTITY} allows for JOINED or TABLE_PER_CLASS inheritance types to work.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    /**
     * Acronym of the conference.
     */
    @Column
    private String acronym;

    /**
     * Name of the conference.
     */
    @Column
    private String name;

    /**
     * Name of the publisher of the conference.
     */
    @Column
    private String publisher;

    /**
     * URL to the page of the conference if it is independent of the annual occurrence.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String conferenceUrl;

    /**
     * Identifier of the conference on the Core ranking website.
     */
    @Column
    private String coreId;

    /**
     * ISBN number if the conference has one.
     */
    @Column
    private String isbn;

    /**
     * ISSN number if the conference has one.
     */
    @Column
    private String issn;

    /**
     * Indicates if the journal is an open-access journal. If this field
     * is not set, we don't know if the journal is open access.
     */
    @Column(nullable = true)
    private Boolean openAccess;

    /**
     * Indicates if the journal was validated by an authority.
     */
    @Column(nullable = false)
    private boolean validated;

    /**
     * History of the quality indicators for this conference.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "conference_conference_annual_indicators_mapping",
            joinColumns = {
                    @JoinColumn(name = "conference_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "indicators_id", referencedColumnName = "id")
            })
    @MapKey(name = "referenceYear")
    private Map<Integer, ConferenceQualityAnnualIndicators> qualityIndicators;

    /**
     * Reference to the super conference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Conference enclosingConference;

    /**
     * List of papers that are published in this conference.
     *
     * @since 4.0
     */
    @OneToMany(mappedBy = "conference", fetch = FetchType.LAZY)
    private Set<AbstractConferenceBasedPublication> publishedPapers;

    /**
     * Construct an empty conference.
     */
    public Conference() {
        //
    }

    /**
     * Construct by copying the given conference (exception id and quality indicators).
     *
     * @param conference the conference to be copied.
     */
    public Conference(Conference conference) {
        assert conference != null;
        this.acronym = conference.getAcronym();
        this.name = conference.getName();
        this.publisher = conference.getPublisher();
        this.conferenceUrl = conference.getConferenceURL();
        this.coreId = conference.getCoreId();
        this.publisher = conference.getPublisher();
        this.isbn = conference.getISBN();
        this.issn = conference.getISSN();
        this.openAccess = conference.getOpenAccess();
        this.validated = conference.isValidated();
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.acronym);
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
        final var other = (Conference) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.acronym, other.acronym)
                && Objects.equals(this.name, other.name);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getId() != 0) {
            consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAcronym())) {
            consumer.accept("acronym", getAcronym()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getName())) {
            consumer.accept("name", getName()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPublisher())) {
            consumer.accept("publisher", getPublisher()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getConferenceURL())) {
            consumer.accept("conferenceURL", getConferenceURL()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getCoreId())) {
            consumer.accept("coreId", getCoreId()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getISBN())) {
            consumer.accept("isbn", getISBN()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getISSN())) {
            consumer.accept("issn", getISSN()); //$NON-NLS-1$
        }
        if (getOpenAccess() != null) {
            consumer.accept("openAccess", getOpenAccess()); //$NON-NLS-1$
        }
        consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        //
        generator.writeObjectFieldStart("qualityIndicators"); //$NON-NLS-1$
        for (final var indicators : getQualityIndicators().entrySet()) {
            generator.writeFieldId(indicators.getKey().intValue());
            generator.writeObject(indicators.getValue());
        }
        //
        final var conferences = JsonUtils.cache(generator);
        conferences.writeReferenceOrObjectField("enclosingConference", getEnclosingConference(), () -> { //$NON-NLS-1$
            JsonUtils.writeObjectAndAttributes(generator, getEnclosingConference());
        });
        //
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
     * Change the identifier of the journal in the database.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the acronym of the conference.
     *
     * @return the acronym.
     */
    public String getAcronym() {
        return this.acronym;
    }

    /**
     * Change the acronym of the conference.
     *
     * @param acronym the acronym.
     */
    public void setAcronym(String acronym) {
        this.acronym = Strings.emptyToNull(acronym);
    }

    /**
     * Replies the name of the conference.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Change the name of the conference.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = Strings.emptyToNull(name);
    }

    /**
     * Replies the acronym or the name of the conference, in that order.
     *
     * @return the acronym or name.
     * @see #getNameOrAcronym()
     * @see #getAcronym()
     * @see #getName()
     */
    public String getAcronymOrName() {
        return Strings.isNullOrEmpty(this.acronym) ? this.name : this.acronym;
    }

    /**
     * Replies the name or the acronym of the conference, in that order.
     *
     * @return the name or acronym.
     * @see #getAcronymOrName()
     * @see #getAcronym()
     * @see #getName()
     */
    public String getNameOrAcronym() {
        return Strings.isNullOrEmpty(this.name) ? this.acronym : this.name;
    }

    /**
     * Replies the acronym and the name of the conference, if they exist, with the format {@code "Acronym - Name"}.
     *
     * @return the acronym and the name, or {@code null} if there is neither acronym nor name.
     * @see #getAcronymOrName()
     * @see #ACRONYM_NAME_SEPARATOR
     * @since 4.0
     */
    public String getAcronymAndName() {
        return getAcronymAndName(EntityConstants.FULL_ACRONYM_NAME_SEPARATOR);
    }

    /**
     * Replies the acronym and the name of the conference, if they exist.
     *
     * @param separator the string to be written between the acronym and the name.
     * @return the acronym and the name, or {@code null} if there is neither acronym nor name.
     * @see #getAcronymOrName()
     * @since 4.0
     */
    public String getAcronymAndName(String separator) {
        final var buffer = new StringBuilder();
        if (!Strings.isNullOrEmpty(this.acronym)) {
            buffer.append(this.acronym);
        }
        if (!Strings.isNullOrEmpty(this.name)) {
            if (buffer.length() > 0 && !Strings.isNullOrEmpty(separator)) {
                buffer.append(separator);
            }
            buffer.append(this.name);
        }
        return Strings.emptyToNull(buffer.toString());
    }

    /**
     * Replies the name of publisher of the journal.
     *
     * @return the name.
     */
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * Change the name of publisher of the journal.
     *
     * @param name the name.
     */
    public void setPublisher(String name) {
        this.publisher = Strings.emptyToNull(name);
    }

    /**
     * Replies the URL of conference if it is independent of the annual occurrence.
     *
     * @return the URL.
     */
    public String getConferenceURL() {
        return this.conferenceUrl;
    }

    /**
     * Change the URL of conference if it is independent of the annual occurrence.
     *
     * @param url the URL.
     */
    public void setConferenceURL(String url) {
        this.conferenceUrl = Strings.emptyToNull(url);
    }

    /**
     * Change the URL of the conference if it is independent of the annual occurrence.
     *
     * @param url the URL.
     */
    public final void setConferenceURL(URL url) {
        setConferenceURL(url == null ? null : url.toExternalForm());
    }

    /**
     * Replies the URL of conference if it is independent of the annual occurrence.
     *
     * @return the URL.
     */
    public final URL getConferenceURLObject() {
        try {
            return new URI(getConferenceURL()).toURL();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the identifier of the conference on the CORE website.
     *
     * @return the identifier.
     */
    public String getCoreId() {
        return this.coreId;
    }

    /**
     * Change the identifier of the conference on the CORE website.
     *
     * @param id the identifier.
     */
    public void setCoreId(String id) {
        this.coreId = Strings.emptyToNull(id);
    }

    /**
     * Replies if the conference is open access. If it is not known, this function returns {@code null}.
     *
     * @return the open-access status of the conference, or {@code null} if it is not known.
     */
    public Boolean getOpenAccess() {
        return this.openAccess;
    }

    /**
     * Change the flag that indicates if the conference is open access. If it is not known, this function returns {@code null}.
     *
     * @param openAccess the open-access status of the conference, or {@code null} if it is not known.
     */
    public void setOpenAccess(Boolean openAccess) {
        this.openAccess = openAccess;
    }

    /**
     * Replies the quality indicators of the conference.
     *
     * @return the indicators.
     */
    public Map<Integer, ConferenceQualityAnnualIndicators> getQualityIndicators() {
        if (this.qualityIndicators == null) {
            this.qualityIndicators = new TreeMap<>();
        }
        return this.qualityIndicators;
    }

    /**
     * Change the quality indicators of the conference.
     *
     * @param indicators the indicators.
     * @since 4.0
     */
    public void setQualityIndicators(Map<Integer, ConferenceQualityAnnualIndicators> indicators) {
        if (this.qualityIndicators == null) {
            this.qualityIndicators = new TreeMap<>();
        } else {
            this.qualityIndicators.clear();
        }
        if (indicators != null && !indicators.isEmpty()) {
            this.qualityIndicators.putAll(indicators);
        }
    }

    /**
     * Replies the quality indicators of the conference for the given year.
     *
     * @param year the year to search for.
     * @return the indicators or {@code null} if none were defined.
     */
    public final ConferenceQualityAnnualIndicators getQualityIndicatorsForYear(int year) {
        final var allIndicators = getQualityIndicators();
        if (allIndicators == null) {
            return null;
        }
        return allIndicators.get(Integer.valueOf(year));
    }

    /**
     * Replies the quality indicators that is fitting the given predicates and with an year lower or equal
     * to the given value.
     *
     * @param year     the year to search for.
     * @param selector the object that permits to select the best quality indicators.
     * @return the indicators or {@code null} if none were defined.
     */
    public final ConferenceQualityAnnualIndicators getQualityIndicatorsFor(int year, Predicate<ConferenceQualityAnnualIndicators> selector) {
        final var allIndicators = getQualityIndicators();
        if (allIndicators != null) {
            final var ids = new IntegerList(allIndicators.keySet());
            final var start = ListUtil.floorIndex(ids, (a, b) -> Integer.compare(a.intValue(), b.intValue()), Integer.valueOf(year));
            for (var i = start; i >= 0; --i) {
                final var indicators = allIndicators.get(ids.get(i));
                if (indicators != null && selector.test(indicators)) {
                    return indicators;
                }
            }
        }
        return null;
    }

    /**
     * Replies if the conference has quality indicators for the given year.
     *
     * @param year the year to search for.
     * @return {@code true} if the journal has quality indicators.
     */
    public boolean hasQualityIndicatorsForYear(int year) {
        return getQualityIndicatorsForYear(year) != null;
    }

    /**
     * Replies the CORE index of the conference from the CORE source.
     * If there is no index known for the given year, this function replies
     * the index for the highest year that is below the given one.
     *
     * @param year the year to search for.
     * @return the index of the conference for the given year, never {@code null}.
     */
    public CoreRanking getCoreIndexByYear(int year) {
        final var indicators = getQualityIndicatorsFor(year, it -> it.getCoreIndex() != null);
        if (indicators != null) {
            final var ranking = indicators.getCoreIndex();
            if (ranking != null) {
                return ranking;
            }
        }
        return CoreRanking.NR;
    }

    /**
     * Change the index of the conference from the CORE source.
     *
     * @param year  the year to search for.
     * @param index the CORE index of the conference for the given year, or {@code null} if not defined.
     * @return the impacted annual indicators object
     */
    public ConferenceQualityAnnualIndicators setCoreIndexByYear(int year, CoreRanking index) {
        var indicators = getQualityIndicatorsForYear(year);
        if (indicators != null) {
            indicators.setCoreIndex(index);
        } else {
            indicators = new ConferenceQualityAnnualIndicators(year, index);
            if (this.qualityIndicators == null) {
                this.qualityIndicators = new TreeMap<>();
            }
            this.qualityIndicators.put(Integer.valueOf(year), indicators);
        }
        return indicators;
    }

    /**
     * Replies if the conference has CORE index for the given year.
     * If there is no CORE index known for the given year, this function replies
     * the CORE index for the highest year that is below the given one.
     *
     * @param year the year to search for.
     * @return {@code true} if the journal has CORE index.
     */
    public boolean hasCoreIndexForYear(int year) {
        final var index = getCoreIndexByYear(year);
        return index != CoreRanking.NR;
    }

    /**
     * Replies the ISBN number that is associated to this conference.
     *
     * @return the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     */
    public String getISBN() {
        return this.isbn;
    }

    /**
     * Change the ISBN number that is associated to this conference.
     *
     * @param isbn the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     */
    public void setISBN(String isbn) {
        this.isbn = Strings.emptyToNull(isbn);
    }

    /**
     * Replies the ISSN number that is associated to this conference.
     *
     * @return the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     */
    public String getISSN() {
        return this.issn;
    }

    /**
     * Change the ISSN number that is associated to this conference.
     *
     * @param issn the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     */
    public void setISSN(String issn) {
        this.issn = Strings.emptyToNull(issn);
    }

    /**
     * Replies if this conference was validated by an authority.
     *
     * @return {@code true} if the conference is validated.
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this conference was validated by an authority.
     *
     * @param validated {@code true} if the conference is validated.
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this conference was validated by an authority.
     *
     * @param validated {@code true} if the conference is validated.
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    /**
     * Replies the reference to the enclosing conference.
     *
     * @return the enclosing conference or {@code null}.
     */
    public Conference getEnclosingConference() {
        return this.enclosingConference;
    }

    /**
     * Change the reference to the enclosing conference.
     *
     * @param conf the enclosing conference or {@code null}.
     */
    public void setEnclosingConference(Conference conf) {
        this.enclosingConference = conf;
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getAcronym(), getName());
    }

    /**
     * Replies the set of published papers in this conference.
     *
     * @return the published papers.
     * @since 4.0
     */
    public Set<AbstractConferenceBasedPublication> getPublishedPapers() {
        if (this.publishedPapers == null) {
            this.publishedPapers = new HashSet<>();
        }
        return this.publishedPapers;
    }

    /**
     * Change the set of published papers in this conference.
     *
     * @param papers the published papers.
     * @since 4.0
     */
    public void setPublishedPapers(Set<? extends AbstractConferenceBasedPublication> papers) {
        if (this.publishedPapers != null) {
            this.publishedPapers.clear();
        } else {
            this.publishedPapers = new HashSet<>();
        }
        if (papers != null) {
            this.publishedPapers.addAll(papers);
        }
    }

    /**
     * Replies if the conference has at least one published paper attached to it.
     *
     * @return {@code true} if a paper is attached to this conference.
     * @since 4.0
     */
    public boolean hasPublishedPaper() {
        return this.publishedPapers != null && !this.publishedPapers.isEmpty();
    }

}
