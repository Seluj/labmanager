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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.comparators.AuthorshipComparator;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Abstract representation of a research publication.
 * This class contains the fields that are usually shared between many of the different types of research publications.
 * These types of research publications are specifically defined in subtypes.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://thorben-janssen.com/complete-guide-inheritance-strategies-jpa-hibernate/"
 */
@Entity
@Table(name = "Publications")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Publication_Type")
public abstract class Publication extends AbstractContextData implements Production, JsonSerializable, Comparable<Publication>, AttributeProvider {

    private static final long serialVersionUID = -5980560007123809890L;

    /**
     * Identifier of the publication.
     * The generated value type is set to {@link GenerationType#AUTO} instead of {@link GenerationType#IDENTITY}
     * because it allows to use an inheritance strategy of type {@link InheritanceType.JOINED} and {@link InheritanceType.TABLE_PER_CLASS}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Type of the publication. This type of publication may enable/disable several fields of the publication (see sub types of {@link Publications}).
     */
    @Column
    @Enumerated(EnumType.STRING)
    private PublicationType type;

    /**
     * Title of a publication.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String title;

    /**
     * Text that is the abstract of the publication.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    @Lob
    private String abstractText;

    /**
     * List of keywords. The separator of the keywords may be a coma ({@code ,}) or a column (@code ;}).
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    @Lob
    private String keywords;

    /**
     * Date of publication. Only the year is really mandatory and significant. The day and month are usually ignored.
     *
     * @see #publicationYear
     */
    @Column
    private LocalDate publicationDate;

    /**
     * Year of publication.
     */
    @Column
    private int publicationYear = getDefaultPublicationYear();

    /**
     * ISBN number if the publication has one.
     */
    @Column
    private String isbn;

    /**
     * ISSN number if the publication has one.
     */
    @Column
    private String issn;

    /**
     * DOI identifier if the publication has one.
     */
    @Column
    private String doi;

    /**
     * identifier on HAL if the publication has one.
     */
    @Column
    private String halId;

    /**
     * Extra URL if the publication has one.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String extraUrl;

    /**
     * URL of a associated video if the publication has one.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String videoUrl;

    /**
     * URL of the publication page on the DBLP if the publication has one.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String dblpUrl;

    /**
     * URL of a associated video if the publication has one. It is preferred to have a local URL, i.e. without the host name.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String pathToDownloadablePDF;

    /**
     * URL of a associated award certificate if the publication has one. It is preferred to have a local URL, i.e. without the host name.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String pathToDownloadableAwardCertificate;

    /**
     * Indicates if the administrator of the database content has manually validated this publication to be accepted in
     * the database. This validation may make the difference when multiple publications are similar. In this case,
     * the validated ones may be considered as part of the the reference and the other may be removed from the database.
     */
    @Column(nullable = false)
    private boolean manualValidationForced;

    /**
     * The major language used for writing the publication. The default language depends on the definition in {@link PublicationLanguage}.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private PublicationLanguage majorLanguage = PublicationLanguage.getDefault();

    /**
     * Authorships specify the authors of the publication and their position in the list of authors.
     */
    @OneToMany(mappedBy = "publication", fetch = FetchType.LAZY)
    private Set<Authorship> authorships = new HashSet<>();

    /**
     * Indicates if the publication was validated by an authority.
     */
    @Column(nullable = false)
    private boolean validated;

    /**
     * List of scientific axes that are associated to this publication.
     *
     * @since 3.5
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<ScientificAxis> scientificAxes = new HashSet<>();

    @Transient
    private List<Person> temporaryAuthors = null;

    @Transient
    private String preferredStringId = null;


    /**
     * Constructor by copy.
     *
     * @param publication is the publication to copy.
     */
    protected Publication(Publication publication) {
        assert publication != null;
        this.id = publication.getId();
        this.authorships.addAll(publication.getAuthorships());
        this.title = publication.getTitle();
        this.abstractText = publication.getAbstractText();
        this.keywords = publication.getKeywords();
        this.publicationDate = publication.getPublicationDate();
        this.publicationYear = publication.getPublicationYear();
        this.isbn = publication.getISBN();
        this.issn = publication.getISSN();
        this.doi = publication.getDOI();
        this.extraUrl = publication.getExtraURL();
        this.videoUrl = publication.getVideoURL();
        this.dblpUrl = publication.getDblpURL();
        this.pathToDownloadablePDF = publication.getPathToDownloadablePDF();
        this.majorLanguage = publication.getMajorLanguage();
        this.pathToDownloadableAwardCertificate = publication.getPathToDownloadableAwardCertificate();
        this.type = publication.getType();
        this.manualValidationForced = publication.getManualValidationForced();
        this.validated = publication.isValidated();
        this.halId = publication.getHalId();
        this.preferredStringId = publication.getPreferredStringId();
        this.scientificAxes.addAll(publication.getScientificAxes());
    }

    /**
     * Create a publication with the given field values.
     *
     * @param type         the type of the publication. It cannot be {@code null}.
     * @param title        the title of the publication.
     * @param abstractText the text of the abstract for the publication.
     * @param keywords     the keywords, seperated by coma or column characters
     * @param date         the date of publication. If it is {@code null}, then the {@code year} should be considered only.
     * @param year         the year of publication.
     * @param isbn         the ISBN number if any.
     * @param issn         the ISSN number if any.
     * @param doi          the DOI reference number if any.
     * @param halId        the HAL reference number if any.
     * @param extraUrl     an URL to a page associated to the publication.
     * @param videoUrl     an URL to a video associated to the publication.
     * @param dblpUrl      the URL to the DBLP page of the publication if any.
     * @param pdfPath      the path (may be an URL, but preferably a simple path) to a downloadable PDF file for the publication.
     * @param awardPath    the path (may be an URL, but preferably a simple path) to a downloadable PDF file that is a award certificate associated to the publication.
     * @param language     the major language used for writing the publication. It cannot be {@code null}.
     */
    public Publication(PublicationType type, String title, String abstractText, String keywords,
                       LocalDate date, int year, String isbn, String issn,
                       String doi, String halId, String extraUrl, String videoUrl, String dblpUrl, String pdfPath,
                       String awardPath, PublicationLanguage language) {
        assert type != null;
        assert language != null;
        this.type = type;
        this.title = title;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.publicationDate = date;
        this.publicationYear = year;
        this.isbn = isbn;
        this.issn = issn;
        this.doi = doi;
        this.halId = halId;
        this.extraUrl = extraUrl;
        this.videoUrl = videoUrl;
        this.dblpUrl = dblpUrl;
        this.pathToDownloadablePDF = pdfPath;
        this.pathToDownloadableAwardCertificate = awardPath;
        this.majorLanguage = language;
    }

    /**
     * Default constructor that generate a publication with "empty" fields.
     */
    protected Publication() {
        //
    }

    /**
     * Replies the default publication year. By default is the current year.
     *
     * @return the default publication year.
     */
    public static int getDefaultPublicationYear() {
        return LocalDate.now().getYear();
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.title);
        h = HashCodeUtils.add(h, this.publicationYear);
        h = HashCodeUtils.add(h, this.type);
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
        final var other = (Publication) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return this.type == other.type
                && this.publicationYear == other.publicationYear
                && Objects.equals(this.title, other.title);
    }

    @Override
    public int compareTo(Publication o) {
        return EntityUtils.getPreferredPublicationComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        assert consumer != null : "How to consume an attribute if the consumer is null?"; //$NON-NLS-1$
        if (getId() != 0) {
            consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
        }
        if (getPublicationYear() > 0) {
            consumer.accept("publicationYear", Integer.valueOf(getPublicationYear())); //$NON-NLS-1$
        }
        if (getPublicationDate() != null) {
            consumer.accept("publicationDate", getPublicationDate()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getAbstractText())) {
            consumer.accept("abstractText", getAbstractText()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getDblpURL())) {
            consumer.accept("dblpURL", getDblpURL()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getDOI())) {
            consumer.accept("doi", getDOI()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getExtraURL())) {
            consumer.accept("extraURL", getExtraURL()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getHalId())) {
            consumer.accept("halId", getHalId()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getISBN())) {
            consumer.accept("isbn", getISBN()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getISSN())) {
            consumer.accept("issn", getISSN()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getKeywords())) {
            consumer.accept("keywords", getKeywords()); //$NON-NLS-1$
        }
        if (getMajorLanguage() != null) {
            consumer.accept("majorLanguage", getMajorLanguage()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPathToDownloadableAwardCertificate())) {
            consumer.accept("pathToDownloadableAwardCertificate", getPathToDownloadableAwardCertificate()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPathToDownloadablePDF())) {
            consumer.accept("pathToDownloadablePDF", getPathToDownloadablePDF()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getVideoURL())) {
            consumer.accept("videoURL", getVideoURL()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getTitle())) {
            consumer.accept("title", getTitle()); //$NON-NLS-1$
        }
        final var ranked = isRanked();
        consumer.accept("ranked", Boolean.valueOf(ranked)); //$NON-NLS-1$
        if (getType() != null) {
            consumer.accept("type", getType()); //$NON-NLS-1$
            consumer.accept("category", getCategory()); //$NON-NLS-1$
        }
        consumer.accept("manualValidationForced", Boolean.valueOf(getManualValidationForced())); //$NON-NLS-1$
        consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        //
        final var persons = JsonUtils.cache(generator);
        //
        generator.writeArrayFieldStart("authors"); //$NON-NLS-1$
        for (final var author : getAuthors()) {
            persons.writeReferenceOrObject(author, () -> {
                JsonUtils.writeObjectAndAttributes(generator, author);
            });
        }
        generator.writeEndArray();
        // Additional fields that are not associated to attributes
        generator.writeStringField("wherePublishedShortDescription", getWherePublishedShortDescription()); //$NON-NLS-1$
        generator.writeStringField("preferredStringId", getPreferredStringId()); //$NON-NLS-1$
        //
        generator.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        serialize(generator, serializers);
    }

    @Override
    public List<Authorship> getAuthorships() {
        final List<Authorship> sortedList = this.authorships.stream().sorted(AuthorshipComparator.DEFAULT).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * Change the set of authorships.
     * You are not supposed to invoke this function yourself because the authorship set
     * if managed by the API framework.
     *
     * @param authorships the authorships.
     */
    public void setAuthorships(Set<Authorship> authorships) {
        this.authorships = authorships;
    }

    /**
     * Replies the reference to the storage area without any change or filtering.
     *
     * @return the authorships.
     */
    public Set<Authorship> getAuthorshipsRaw() {
        return this.authorships;
    }

    /**
     * Replies the ordered list of authors.
     * The authors are replied in the order provided in the paper.
     *
     * @return the list of authors.
     * @see #getTemporaryAuthors()
     */
    @Override
    public List<Person> getAuthors() {
        if (this.temporaryAuthors != null) {
            return this.temporaryAuthors;
        }
        return getAuthorships().stream().map(it -> it.getPerson()).collect(Collectors.toList());
    }

    /**
     * Replies the ordered list of authors that is stored into a temporary memory space.
     *
     * @return the list of authors.
     * @see #getAuthors()
     */
    public List<Person> getTemporaryAuthors() {
        return this.temporaryAuthors;
    }

    /**
     * Change the ordered list of authors that is stored into a temporary memory space.
     *
     * @param authors the list of authors.
     */
    public void setTemporaryAuthors(List<Person> authors) {
        this.temporaryAuthors = authors;
    }

    /**
     * Add the given authorship.
     *
     * @param authorship the authorship to delete.
     */
    @Transactional
    public void addAuthorship(Authorship authorship) {
        this.authorships.add(authorship);
    }

    /**
     * Delete the given authorship.
     *
     * @param authorship the authorship to delete.
     */
    @Transactional
    public void deleteAuthorship(Authorship authorship) {
        this.authorships.remove(authorship);
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the identifier of the publication. This identifier is useful only from the database point of view. Do not use this
     * identifier in your Java code.
     *
     * @param id the new identifier
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Invoked to compute the default string-based ID for this publication. This ID is not stored in the database
     * and may change according to the usage of the publication instance.
     *
     * @return the identifier
     */
    public String computePreferredStringId() {
        String b = getClass().getSimpleName() +
                '_' +
                getId();
        return b;
    }

    /**
     * Replies the preferred value for a string-based ID for this publication. This ID is not stored in the database
     * and may change according to the usage of the publication instance.
     *
     * @return the identifier
     */
    public String getPreferredStringId() {
        if (Strings.isNullOrEmpty(this.preferredStringId)) {
            this.preferredStringId = computePreferredStringId();
        }
        return this.preferredStringId;
    }

    /**
     * Change the preferred value for a string-based ID for this publication. This ID is not stored in the database
     * and may change according to the usage of the publication instance.
     *
     * @param sid the string-based identifier
     */
    public void setPreferredStringId(String sid) {
        this.preferredStringId = sid;
    }

    /**
     * Replies the type of publication.
     *
     * @return the type, never {@code null}.
     */
    public PublicationType getType() {
        return this.type;
    }

    /**
     * Change the type of publication.
     *
     * @param type the type, never {@code null}.
     */
    public void setType(PublicationType type) {
        this.type = type;
    }

    /**
     * Change the type of publication.
     *
     * @param type the type.
     */
    public final void setType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setType((PublicationType) null);
        } else {
            setType(PublicationType.valueOfCaseInsensitive(type));
        }
    }

    @Override
    public PublicationCategory getCategory() {
        return getCategoryWithSupplier(() -> Boolean.valueOf(isRanked()));
    }

    /**
     * Replies the category of publication.
     *
     * @param ranked the predicate that is indicating if a type is ranked when it is associated to this publication.
     * @return the category, or  {@code null}.
     * @since 3.6
     */
    public PublicationCategory getCategoryWithSupplier(Supplier<Boolean> ranked) {
        final var type = getType();
        if (type == null) {
            return null;
        }
        final var categories = type.getCategories();
        final var size = categories.size();
        if (size <= 0) {
            return PublicationCategory.AP;
        } else if (size == 1) {
            return categories.iterator().next();
        }
        // Multiple categories are available. The selection is done on ranking.
        return type.getCategory(ranked.get().booleanValue());
    }

    /**
     * Replies if the publication is published into a ranked support (journal or conference).
     *
     * @return {@code true} if the publication is in a ranked support.
     */
    public abstract boolean isRanked();

    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the publication.
     *
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = Strings.emptyToNull(title);
    }

    /**
     * Replies the text that is an abstract/summary of the publication.
     *
     * @return the text.
     */
    public String getAbstractText() {
        return this.abstractText;
    }

    /**
     * Change the text that is an abstract/summary of the publication.
     *
     * @param text the text.
     */
    public void setAbstractText(String text) {
        this.abstractText = Strings.emptyToNull(text);
    }

    /**
     * Replies a list of keywords that describe the contribution of the research publication.
     * The keywords are usually separated by coma or column characters.
     *
     * @return the list of keywords.
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Replies a list of keywords that describe the contribution of the research publication.
     * The keywords are usually separated by coma or column characters.
     *
     * @param keywords the list of keywords.
     */
    public void setKeywords(String keywords) {
        this.keywords = Strings.emptyToNull(keywords);
    }

    /**
     * Replies the date of publication.
     *
     * @return the date, or {@code null}.
     */
    public LocalDate getPublicationDate() {
        return this.publicationDate;
    }

    /**
     * Change the date of publication. This function changes also the field that contains the publication year.
     *
     * @param date the date, or {@code null}.
     */
    public void setPublicationDate(LocalDate date) {
        this.publicationDate = date;
        resetPublicationYear();
    }

    /**
     * Change the date of publication. This function changes also the field that contains the publication year.
     *
     * @param date the date, or {@code null}.
     */
    public final void setPublicationDate(String date) {
        if (Strings.isNullOrEmpty(date)) {
            throw new IllegalArgumentException();
        }
        setPublicationDate(LocalDate.parse(date));
    }

    private void resetPublicationYear() {
        if (this.publicationDate != null) {
            this.publicationYear = this.publicationDate.getYear();
        } else {
            this.publicationYear = getDefaultPublicationYear();
        }
    }

    @Override
    public int getPublicationYear() {
        return this.publicationYear;
    }

    /**
     * Change the publication year.
     *
     * @param year is the year of publication.
     */
    public void setPublicationYear(int year) {
        this.publicationYear = year;
    }

    /**
     * Change the publication year.
     *
     * @param year is the year of publication.
     */
    public final void setPublicationYear(Number year) {
        if (year == null) {
            setPublicationYear(0);
        } else {
            setPublicationYear(year.intValue());
        }
    }

    /**
     * Replies the ISBN number that is associated to this publication.
     * Some implementation of this function delegates to the container of the publication, e.g., the journal.
     *
     * @return the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     */
    public String getISBN() {
        return this.isbn;
    }

    /**
     * Change the ISBN number that is associated to this publication.
     * Some implementation of this function delegates to the container of the publication, e.g., the journal.
     *
     * @param isbn the ISBN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/ISBN"
     */
    public void setISBN(String isbn) {
        this.isbn = Strings.emptyToNull(isbn);
    }

    /**
     * Replies the ISSN number that is associated to this publication.
     * Some implementation of this function delegates to the container of the publication, e.g., the journal.
     *
     * @return the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     */
    public String getISSN() {
        return this.issn;
    }

    /**
     * Change the ISSN number that is associated to this publication.
     * Some implementation of this function delegates to the container of the publication, e.g., the journal.
     *
     * @param issn the ISSN number or {@code null}.
     * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
     */
    public void setISSN(String issn) {
        this.issn = Strings.emptyToNull(issn);
    }

    @Override
    public String getDOI() {
        return this.doi;
    }

    /**
     * Change the DOI reference number that is associated to this publication.
     * Usually, the DOI number should not be prefixed by the {@code http://doi.org} prefix.
     *
     * @param doi the DOI reference or {@code null}.
     * @see "https://en.wikipedia.org/wiki/Digital_object_identifier"
     */
    public void setDOI(String doi) {
        this.doi = Strings.emptyToNull(doi);
    }

    /**
     * Replies the HAL reference number that is associated to this publication.
     * Usually, the HAL number should not be prefixed by the {@code http://hal.archives-ouvertes.fr} prefix.
     *
     * @return the HAL reference or {@code null}.
     */
    public String getHalId() {
        return this.halId;
    }

    /**
     * Change the HAL reference number that is associated to this publication.
     * Usually, the HAL number should not be prefixed by the {@code http://hal.archives-ouvertes.fr} prefix.
     *
     * @param hal the HAL reference or {@code null}.
     */
    public void setHalId(String hal) {
        this.halId = Strings.emptyToNull(hal);
    }

    /**
     * Replies any extra URL that is associated to the publication.
     *
     * @return the URL or {@code null}.
     * @see #getExtraURL()
     */
    public final URL getExtraURLObject() {
        try {
            return new URI(getExtraURL()).toURL();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the string representation of any extra URL that is associated to the publication.
     *
     * @return the string representation of the extra URL or {@code null}.
     * @see #getExtraURLObject()
     */
    public String getExtraURL() {
        return this.extraUrl;
    }

    /**
     * Change the extra URL that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     */
    public final void setExtraURL(URL url) {
        final String value;
        if (url != null) {
            value = url.toExternalForm();
        } else {
            value = null;
        }
        setExtraURL(value);
    }

    /**
     * Change the extra URL that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     */
    public void setExtraURL(String url) {
        this.extraUrl = Strings.emptyToNull(url);
    }

    /**
     * Replies any URL of a video that is associated to the publication.
     *
     * @return the URL or {@code null}.
     * @see #getVideoURL()
     */
    public final URL getVideoURLObject() {
        try {
            return new URI(getVideoURL()).toURL();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the string representation of any URL to a video that is associated to the publication.
     *
     * @return the string representation of the video URL or {@code null}.
     * @see #getVideoURLObject()
     */
    public String getVideoURL() {
        return this.videoUrl;
    }

    /**
     * Change the URL of the video that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     */
    public final void setVideoURL(URL url) {
        final String value;
        if (url != null) {
            value = url.toExternalForm();
        } else {
            value = null;
        }
        setVideoURL(value);
    }

    /**
     * Change the URL of the video that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     */
    public void setVideoURL(String url) {
        this.videoUrl = Strings.emptyToNull(url);
    }

    /**
     * Replies URL of a DBLP page that is associated to the publication.
     *
     * @return the URL or {@code null}.
     * @see #getDblpURL()
     * @see "https://dblp.org/"
     */
    public final URL getDblpURLObject() {
        try {
            return new URI(getDblpURL()).toURL();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the string representation of the URL of a DBLP page that is associated to the publication.
     *
     * @return the string representation of the DBLP URL or {@code null}.
     * @see #getDblpURLObject()
     * @see "https://dblp.org/"
     */
    public String getDblpURL() {
        return this.dblpUrl;
    }

    /**
     * Change the URL of the DBLP page that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     * @see "https://dblp.org/"
     */
    public final void setDblpURL(URL url) {
        final String value;
        if (url != null) {
            value = url.toExternalForm();
        } else {
            value = null;
        }
        setDblpURL(value);
    }

    /**
     * Change the URL of the DBLP page that is associated to the publication.
     *
     * @param url the URL or {@code null}.
     * @see "https://dblp.org/"
     */
    public void setDblpURL(String url) {
        this.dblpUrl = Strings.emptyToNull(url);
    }

    /**
     * Replies a path to a downloadable PDF for the publication.
     *
     * @return the path or {@code null}.
     */
    public String getPathToDownloadablePDF() {
        return this.pathToDownloadablePDF;
    }

    /**
     * Change the path to a downloadable PDF for the publication.
     *
     * @param path the path or {@code null}.
     */
    public void setPathToDownloadablePDF(String path) {
        this.pathToDownloadablePDF = Strings.emptyToNull(path);
    }

    /**
     * Replies a path to a downloadable certificate of award for the publication.
     *
     * @return the path or {@code null}.
     */
    public String getPathToDownloadableAwardCertificate() {
        return this.pathToDownloadableAwardCertificate;
    }

    /**
     * Change the path to a downloadable certificate of award for the publication.
     *
     * @param path the path or {@code null}.
     */
    public void setPathToDownloadableAwardCertificate(String path) {
        this.pathToDownloadableAwardCertificate = Strings.emptyToNull(path);
    }

    /**
     * Replies the major language that is used for writing the publication.
     *
     * @return the language, never {@code null}.
     */
    public PublicationLanguage getMajorLanguage() {
        return this.majorLanguage;
    }

    /**
     * Change the major language that is used for writing the publication.
     *
     * @param language the language. If {@code null}, the default language will be assigned.
     */
    public void setMajorLanguage(PublicationLanguage language) {
        if (language == null) {
            this.majorLanguage = PublicationLanguage.getDefault();
        } else {
            this.majorLanguage = language;
        }
    }

    /**
     * Change the major language that is used for writing the publication.
     *
     * @param language the language.
     */
    public final void setMajorLanguage(String language) {
        if (Strings.isNullOrEmpty(language)) {
            setMajorLanguage((PublicationLanguage) null);
        } else {
            setMajorLanguage(PublicationLanguage.valueOfCaseInsensitive(language));
        }
    }

    /**
     * Replies a short description of where the publication is published.
     * This function is a general place-holder for obtaining the place where publication
     * was published. The returned value depends strongly of the type of the publication,
     * and therefore of the function implementation in the sub-types.
     * Comparing to {@link #getPublicationTarget()}, this function may provides a longer
     * description with additional information such as the volume number for example.
     *
     * @return the description.
     * @see #getPublicationTarget()
     */
    public abstract String getWherePublishedShortDescription();

    /**
     * Replies a target of the publication. Depending on the type of the publication,
     * the target may be a journal, a conference, a school, etc.
     * Comparing to {@link #getWherePublishedShortDescription()}, this function
     * provides less information and focuses on the publication's target.
     *
     * @return the publication target.
     * @see #getWherePublishedShortDescription()
     */
    public abstract String getPublicationTarget();

    /**
     * Indicates if the administrator of the database content has manually validated this publication to be accepted in
     * the database. This validation may make the difference when multiple publications are similar. In this case,
     * the validated ones may be considered as part of the the reference and the other may be removed from the database.
     *
     * @return {@code true} if publication was manually validated.
     */
    public boolean getManualValidationForced() {
        return this.manualValidationForced;
    }

    /**
     * Change the flag that indicates if the administrator of the database content has manually validated this publication to be accepted in
     * the database. This validation may make the difference when multiple publications are similar. In this case,
     * the validated ones may be considered as part of the the reference and the other may be removed from the database.
     *
     * @param validated {@code true} if publication was manually validated.
     */
    public void setManualValidationForced(boolean validated) {
        this.manualValidationForced = validated;
    }

    /**
     * Change the flag that indicates if the administrator of the database content has manually validated this publication to be accepted in
     * the database. This validation may make the difference when multiple publications are similar. In this case,
     * the validated ones may be considered as part of the the reference and the other may be removed from the database.
     *
     * @param validated {@code true} if publication was manually validated.
     */
    public final void setManualValidationForced(Boolean validated) {
        if (validated == null) {
            setManualValidationForced(false);
        } else {
            setManualValidationForced(validated.booleanValue());
        }
    }

    /**
     * Replies if this journal was validated by an authority.
     *
     * @return {@code true} if the journal is validated.
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Change the flag that indicates if this journal was validated by an authority.
     *
     * @param validated {@code true} if the journal is validated.
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Change the flag that indicates if this journal was validated by an authority.
     *
     * @param validated {@code true} if the journal is validated.
     */
    public final void setValidated(Boolean validated) {
        if (validated == null) {
            setValidated(false);
        } else {
            setValidated(validated.booleanValue());
        }
    }

    /**
     * Replies if the publication is open access
     *
     * @return {@link Boolean#TRUE} if the publication is open access, {@link Boolean#FALSE} if the publication is not open access,
     * or {@code null} if the open access flag is unknown.
     */
    @SuppressWarnings("static-method")
    public Boolean getOpenAccess() {
        return null;
    }

    /**
     * Replies the scientific axes that are associated to this publication.
     *
     * @return the scientific axes.
     * @since 3.5
     */
    public Set<ScientificAxis> getScientificAxes() {
        if (this.scientificAxes == null) {
            this.scientificAxes = new HashSet<>();
        }
        return this.scientificAxes;
    }

    /**
     * Change the scientific axes that are associated to this publication.
     * The instances of the scientific axes are updated through the JPA links.
     * This function is not updating the references from the axes to this publication
     * directly.
     *
     * @param axes the scientific axes associated to this publication.
     * @since 3.5
     */
    public void setScientificAxes(Collection<ScientificAxis> axes) {
        if (this.scientificAxes == null) {
            this.scientificAxes = new HashSet<>();
        } else {
            this.scientificAxes.clear();
        }
        if (axes != null && !axes.isEmpty()) {
            this.scientificAxes.addAll(axes);
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getTitle());
    }

}
