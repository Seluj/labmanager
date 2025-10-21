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

package fr.utbm.ciad.labmanager.utils.io.ris;

import ch.difty.kris.KRisIO;
import ch.difty.kris.domain.RisRecord;
import ch.difty.kris.domain.RisType;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceUtils;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.*;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PrePublicationFactory;
import fr.utbm.ciad.labmanager.services.publication.type.*;
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.bibtex.ConferenceFake;
import fr.utbm.ciad.labmanager.utils.io.bibtex.JournalFake;
import fr.utbm.ciad.labmanager.utils.io.bibtex.MissedConferenceException;
import fr.utbm.ciad.labmanager.utils.io.bibtex.MissedJournalException;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for RIS based on the Kris library.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/RIS_(file_format)"
 * @since 3.7
 */
@Component
public class KrisRIS extends AbstractRIS {

    private final PrePublicationFactory prePublicationFactory;

    private final JournalPaperService journalPaperService;

    private final JournalService journalService;

    private final ConferenceService conferenceService;

    private final ConferencePaperService conferencePaperService;

    private final BookService bookService;

    private final BookChapterService bookChapterService;

    private final ThesisService thesisService;

    private final ReportService reportService;

    private final KeyNoteService keyNoteService;

    private final JournalEditionService journalEditionService;

    private final MiscDocumentService miscDocumentService;

    private final PersonService personService;

    private final DoiTools doiTools;

    /**
     * Constructor. This constructor is ready for injection.
     *
     * @param messages               the accessor to the localized strings.
     * @param prePublicationFactory  the factory of pre-publications.
     * @param journalService         the service for accessing the journals.
     * @param conferenceService      the service for accessing the conferences.
     * @param personService          the service for managing the persons.
     * @param bookService            the book service.
     * @param bookChapterService     the book chapter service.
     * @param conferencePaperService the conference paper service.
     * @param journalPaperService    the journal paper service.
     * @param miscDocumentService    the service for misc documents.
     * @param reportService          the service for reports.
     * @param thesisService          the service for theses.
     * @param keyNoteService         the service for keynotes.
     * @param journalEditionService  the service for journal editions.
     * @param doiTools               the service for manipulating DOI.
     */
    public KrisRIS(
            @Autowired MessageSourceAccessor messages,
            @Autowired PrePublicationFactory prePublicationFactory,
            @Autowired JournalService journalService,
            @Autowired ConferenceService conferenceService,
            @Autowired PersonService personService,
            @Autowired BookService bookService,
            @Autowired BookChapterService bookChapterService,
            @Autowired ConferencePaperService conferencePaperService,
            @Autowired JournalPaperService journalPaperService,
            @Autowired MiscDocumentService miscDocumentService,
            @Autowired ReportService reportService,
            @Autowired ThesisService thesisService,
            @Autowired KeyNoteService keyNoteService,
            @Autowired JournalEditionService journalEditionService,
            @Autowired DoiTools doiTools) {
        super(messages);
        this.prePublicationFactory = prePublicationFactory;
        this.journalService = journalService;
        this.journalPaperService = journalPaperService;
        this.conferenceService = conferenceService;
        this.conferencePaperService = conferencePaperService;
        this.bookService = bookService;
        this.bookChapterService = bookChapterService;
        this.thesisService = thesisService;
        this.reportService = reportService;
        this.keyNoteService = keyNoteService;
        this.journalEditionService = journalEditionService;
        this.miscDocumentService = miscDocumentService;
        this.personService = personService;
        this.doiTools = doiTools;
    }

    private static String fieldRequired(RisRecord record, String fieldName, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    return value;
                }
            }
        }
        throw new IllegalStateException("Field '" + fieldName + "' is required for record: " + record.getReferenceId()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static String field(RisRecord record, String fieldName, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    private static String fieldKeywords(RisRecord record, String fieldName, List<String> fieldValue) throws Exception {
        if (fieldValue != null && !fieldValue.isEmpty()) {
            final var kws = new StringBuilder();
            for (final var kw : fieldValue) {
                if (!Strings.isNullOrEmpty(kw)) {
                    if (kws.length() > 0) {
                        kws.append("; "); //$NON-NLS-1$
                    }
                    kws.append(kw);
                }
            }
            return Strings.emptyToNull(kws.toString());
        }
        return null;
    }

    private static String fieldIsbn(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    if (value.replaceAll("[^0-9a-zA-Z]+", "").length() >= 10) { //$NON-NLS-1$ //$NON-NLS-2$
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private static String fieldIssn(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    if (value.replaceAll("[^0-9a-zA-Z]+", "").length() == 8) { //$NON-NLS-1$ //$NON-NLS-2$
                        return value;
                    }
                }
            }
        }
        return null;
    }

    private static int fieldYear(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    if (value.matches("^[0-9]+$")) { //$NON-NLS-1$
                        try {
                            return Integer.parseInt(value);
                        } catch (Throwable ex) {
                            //
                        }
                    }
                }
            }
        }
        throw new IllegalStateException("Field 'year' is required for record: " + record.getReferenceId()); //$NON-NLS-1$
    }

    private static LocalDate fieldDate(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    try {
                        return LocalDate.parse(value);
                    } catch (Throwable ex) {
                        //
                    }
                }
            }
        }
        return null;
    }

    private static PublicationLanguage fieldLanguage(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    final var lang = PublicationLanguage.valueOfCaseInsensitive(value, null);
                    if (lang != null) {
                        return lang;
                    }
                }
            }
        }
        return PublicationLanguage.getDefault();
    }

    private static String fieldPages(RisRecord record, String startPage, String endPage) throws Exception {
        var spage = 0;
        if (!Strings.isNullOrEmpty(startPage)) {
            try {
                spage = Integer.parseInt(startPage);
            } catch (Throwable ex) {
                spage = 0;
            }
        }
        var epage = 0;
        if (!Strings.isNullOrEmpty(endPage)) {
            try {
                epage = Integer.parseInt(endPage);
            } catch (Throwable ex) {
                epage = 0;
            }
        }
        if (spage > 1 && epage > 1) {
            if (spage <= epage) {
                return spage + "-" + epage; //$NON-NLS-1$
            }
            return epage + "-" + spage; //$NON-NLS-1$
        }
        if (spage > 1) {
            return Integer.toString(spage);
        }
        if (epage > 1) {
            return Integer.toString(epage);
        }
        return null;
    }

    private static String fieldRequiredCleanPrefix(RisRecord record, String fieldName, String... fieldValue) throws Exception {
        var value = field(record, fieldName, fieldValue);
        value = ConferenceUtils.removePrefixArticles(value);
        if (!Strings.isNullOrEmpty(value)) {
            return value;
        }
        throw new IllegalStateException("Field '" + fieldName + "' is required for entry: " + record.getReferenceId()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void exportPublications(Writer output, Collection<? extends Publication> publications,
                                   ExporterConfigurator configurator, Progression progression) throws IOException {
        progression.setProperties(0, 0, publications.size() + 1, false);
        final var records = new ArrayList<RisRecord>();
        final var iterator = publications.iterator();
        while (iterator.hasNext()) {
            final var publication = iterator.next();
            exportPublication(configurator.getLocaleOrLanguageLocale(publication.getMajorLanguage()), publication, records);
            progression.increment();
        }
        KRisIO.export(records, output);
        progression.end();
    }

    /**
     * Export a single publication to RIS record.
     *
     * @param locale      the locale to use for the export.
     * @param publication the publication to export.
     * @param records     the receiver of the new record.
     */
    protected void exportPublication(Locale locale, Publication publication, List<RisRecord> records) {
        RisRecord record = null;
        switch (publication.getType()) {
            case INTERNATIONAL_JOURNAL_PAPER:
            case INTERNATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
            case NATIONAL_JOURNAL_PAPER:
            case NATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
                record = createRecord((JournalPaper) publication, RisType.JOUR, locale);
                break;
            case INTERNATIONAL_CONFERENCE_PAPER:
            case NATIONAL_CONFERENCE_PAPER:
            case INTERNATIONAL_ORAL_COMMUNICATION:
            case NATIONAL_ORAL_COMMUNICATION:
            case INTERNATIONAL_POSTER:
            case NATIONAL_POSTER:
                record = createRecord((ConferencePaper) publication, locale);
                break;
            case INTERNATIONAL_BOOK:
            case NATIONAL_BOOK:
            case SCIENTIFIC_CULTURE_BOOK:
                record = createRecord((Book) publication, locale);
                break;
            case INTERNATIONAL_BOOK_CHAPTER:
            case NATIONAL_BOOK_CHAPTER:
            case SCIENTIFIC_CULTURE_BOOK_CHAPTER:
                record = createRecord((BookChapter) publication, locale);
                break;
            case HDR_THESIS:
            case PHD_THESIS:
            case MASTER_THESIS:
                record = createRecord((Thesis) publication, locale);
                break;
            case INTERNATIONAL_JOURNAL_EDITION:
            case NATIONAL_JOURNAL_EDITION:
                record = createRecord((JournalEdition) publication, locale);
                break;
            case INTERNATIONAL_KEYNOTE:
            case NATIONAL_KEYNOTE:
                record = createRecord((KeyNote) publication, locale);
                break;
            case TECHNICAL_REPORT:
            case PROJECT_REPORT:
            case RESEARCH_TRANSFERT_REPORT:
            case TEACHING_DOCUMENT:
            case TUTORIAL_DOCUMENTATION:
                record = createRecord((Report) publication, locale);
                break;
            case INTERNATIONAL_PATENT:
            case EUROPEAN_PATENT:
            case NATIONAL_PATENT:
                record = createRecord((Patent) publication, locale);
                break;
            case SCIENTIFIC_CULTURE_PAPER:
                record = createRecord((JournalPaper) publication, RisType.MGZN, locale);
                break;
            case ARTISTIC_PRODUCTION:
                record = createRecord((MiscDocument) publication, RisType.ART, locale);
                break;
            case RESEARCH_TOOL:
                record = createRecord((MiscDocument) publication, RisType.COMP, locale);
                break;
            case INTERNATIONAL_PRESENTATION:
            case NATIONAL_PRESENTATION:
            case INTERNATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
            case NATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
                record = createRecord((MiscDocument) publication, RisType.PCOMM, locale);
                break;
            case INTERNATIONAL_TV_OR_ONLINE_SHOW:
            case NATIONAL_TV_OR_ONLINE_SHOW:
                record = createRecord((MiscDocument) publication, RisType.MPCT, locale);
                break;
            case INTERNATIONAL_NEWS_PAPER:
            case NATIONAL_NEWS_PAPER:
                record = createRecord((MiscDocument) publication, RisType.NEWS, locale);
                break;
            case OTHER:
                record = createRecord((MiscDocument) publication, RisType.GEN, locale);
                break;
            default:
                throw new IllegalArgumentException("Unsupported publication type for export: " + publication.getType()); //$NON-NLS-1$
        }
        if (record != null) {
            records.add(record);
        }
    }

    /**
     * Create a record builder with the attributes that are shared by all the types of publications.
     *
     * @param risType        the type of the record.
     * @param publication    the publication to export.
     * @param insertIssnIsbn indicates if the ISBN/ISSN number should be inserted.
     * @param locale         the lcoale to use for the export.
     * @return the record builder.
     */
    protected RisRecord.Builder createStandardRecord(RisType risType, Publication publication, boolean insertIssnIsbn, Locale locale) {
        final var authors = publication.getAuthors().stream()
                .map(it -> it.getLastName() + ", " + it.getFirstName()) //$NON-NLS-1$
                .collect(Collectors.toList());
        final List<String> keywords;
        if (Strings.isNullOrEmpty(publication.getKeywords())) {
            keywords = Collections.emptyList();
        } else {
            keywords = Arrays.asList(publication.getKeywords().split("\\s*[,;:./]\\s*")).stream().filter(it -> !Strings.isNullOrEmpty(it)).toList(); //$NON-NLS-1$
        }
        final var url = Arrays.asList(
                        publication.getExtraURL(), publication.getDblpURL(), publication.getVideoURL()).stream()
                .filter(it -> !Strings.isNullOrEmpty(it))
                .findFirst().orElse(null);
        final var type = publication.getType();
        final var cat = publication.getCategory();
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final String publicationType;
        final String publicationTypeName;
        if (cat != null) {
            publicationType = cat.name();
            publicationTypeName = cat.getLabel(getMessageSourceAccessor(), locale);
        } else if (type != null) {
            publicationType = type.name();
            publicationTypeName = type.getLabel(getMessageSourceAccessor(), locale);
        } else {
            publicationType = null;
            publicationTypeName = null;
        }
        var builder = new RisRecord.Builder()
                .type(risType)
                .title(publication.getTitle())
                .authors(authors)
                .publicationYear(Integer.toString(publication.getPublicationYear()))
                .abstr(publication.getAbstractText())
                .keywords(keywords)
                .doi(publication.getDOI())
                .url(url)
                .language(publication.getMajorLanguage().name())
                .custom1(publicationType)
                .custom2(publicationTypeName);
        if (insertIssnIsbn) {
            final var isbnissn = Arrays.asList(publication.getISBN(), publication.getISSN()).stream()
                    .filter(it -> !Strings.isNullOrEmpty(it))
                    .findFirst().orElse(null);
            builder = builder.isbnIssn(isbnissn);
        }
        return builder;
    }

    /**
     * Create the RIS record for the given journal paper.
     *
     * @param publication the publication to export.
     * @param risType     the type of paper.
     * @param locale      the locale to use for the export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(JournalPaper publication, RisType risType, Locale locale) {
        var builder = createStandardRecord(risType, publication, false, locale);
        final var journal = publication.getJournal();
        if (journal != null) {
            final var isbnissn = Arrays.asList(journal.getISBN(), journal.getISSN()).stream()
                    .filter(it -> !Strings.isNullOrEmpty(it))
                    .findFirst().orElse(null);
            builder = builder.periodicalNameFullFormatJO(journal.getJournalName())
                    .publisher(journal.getPublisher())
                    .publishingPlace(journal.getAddress())
                    .isbnIssn(isbnissn);
        }
        builder = builder
                .volumeNumber(publication.getVolume())
                .numberOfVolumes(publication.getNumber())
                .section(publication.getSeries());
        final var range = parsePages(publication.getPages());
        if (range != null) {
            builder = builder.startPage(range.getMin().toString()).endPage(range.getMax().toString());
        }
        if (publication.getScimagoQIndex() != null && publication.getScimagoQIndex() != QuartileRanking.NR) {
            builder = builder.custom3(publication.getScimagoQIndex().name());
        }
        if (publication.getWosQIndex() != null && publication.getWosQIndex() != QuartileRanking.NR) {
            builder = builder.custom4(publication.getWosQIndex().name());
        }
        if (publication.getImpactFactor() > 0f) {
            builder = builder.custom5(Float.toString(publication.getImpactFactor()));
        }
        return builder.build();
    }

    /**
     * Create the RIS record for the given conference paper.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(ConferencePaper publication, Locale locale) {
        var builder = createStandardRecord(RisType.CPAPER, publication, false, locale);
        final var conference = publication.getConference();
        if (conference != null) {
            final var isbnissn = Arrays.asList(conference.getISBN(), conference.getISSN()).stream()
                    .filter(it -> !Strings.isNullOrEmpty(it))
                    .findFirst().orElse(null);
            builder = builder.secondaryTitle(conference.getName())
                    .publisher(conference.getPublisher())
                    .isbnIssn(isbnissn);
        }
        builder = builder
                .secondaryTitle(publication.getPublicationTarget())
                .publishingPlace(publication.getAddress())
                .volumeNumber(publication.getVolume())
                .numberOfVolumes(publication.getNumber())
                .tertiaryTitle(publication.getSeries())
                .editor(publication.getEditors())
                .custom4(publication.getOrganization());
        final var range = parsePages(publication.getPages());
        if (range != null) {
            builder = builder.startPage(range.getMin().toString()).endPage(range.getMax().toString());
        }
        if (publication.getCoreRanking() != null && publication.getCoreRanking() != CoreRanking.NR) {
            builder = builder.custom3(publication.getCoreRanking().name());
        }
        return builder.build();
    }

    /**
     * Create the RIS record for the given book.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(Book publication, Locale locale) {
        var builder = createStandardRecord(RisType.BOOK, publication, true, locale)
                .publisher(publication.getPublisher())
                .publishingPlace(publication.getAddress())
                .editor(publication.getEditors())
                .volumeNumber(publication.getVolume())
                .numberOfVolumes(publication.getNumber())
                .edition(publication.getEdition())
                .section(publication.getSeries());
        final var range = parsePages(publication.getPages());
        if (range != null) {
            builder = builder.startPage(range.getMin().toString()).endPage(range.getMax().toString());
        }
        return builder.build();
    }

    /**
     * Create the RIS record for the given book chapter.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(BookChapter publication, Locale locale) {
        var builder = createStandardRecord(RisType.CHAP, publication, true, locale)
                .secondaryTitle(publication.getBookTitle())
                .section(publication.getChapterNumber())
                .publisher(publication.getPublisher())
                .publishingPlace(publication.getAddress())
                .editor(publication.getEditors())
                .volumeNumber(publication.getVolume())
                .numberOfVolumes(publication.getNumber())
                .edition(publication.getEdition())
                .tertiaryTitle(publication.getSeries());
        final var range = parsePages(publication.getPages());
        if (range != null) {
            builder = builder.startPage(range.getMin().toString()).endPage(range.getMax().toString());
        }
        return builder.build();
    }

    /**
     * Create the RIS record for the given thesis.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(Thesis publication, Locale locale) {
        final var thesisType = publication.getType().getLabel(getMessageSourceAccessor(), locale);
        final var builder = createStandardRecord(RisType.THES, publication, true, locale)
                .publisher(publication.getInstitution())
                .publishingPlace(publication.getAddress())
                .custom3(thesisType);
        return builder.build();
    }

    /**
     * Create the RIS record for the given journal edition.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(JournalEdition publication, Locale locale) {
        var builder = createStandardRecord(RisType.EDBOOK, publication, true, locale);
        final var journal = publication.getJournal();
        if (journal != null) {
            final var isbnissn = Arrays.asList(journal.getISBN(), journal.getISSN()).stream()
                    .filter(it -> !Strings.isNullOrEmpty(it))
                    .findFirst().orElse(null);
            builder = builder.periodicalNameFullFormatJO(journal.getJournalName())
                    .publisher(journal.getPublisher())
                    .publishingPlace(journal.getAddress())
                    .isbnIssn(isbnissn);
        }
        builder = builder
                .volumeNumber(publication.getVolume())
                .numberOfVolumes(publication.getNumber());
        final var range = parsePages(publication.getPages());
        if (range != null) {
            builder = builder.startPage(range.getMin().toString()).endPage(range.getMax().toString());
        }
        if (publication.getScimagoQIndex() != null && publication.getScimagoQIndex() != QuartileRanking.NR) {
            builder = builder.custom3(publication.getScimagoQIndex().name());
        }
        if (publication.getWosQIndex() != null && publication.getWosQIndex() != QuartileRanking.NR) {
            builder = builder.custom4(publication.getWosQIndex().name());
        }
        if (publication.getImpactFactor() > 0f) {
            builder = builder.custom5(Float.toString(publication.getImpactFactor()));
        }
        return builder.build();
    }

    /**
     * Create the RIS record for the given conference keynote.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(KeyNote publication, Locale locale) {
        final var keyNoteType = publication.getType().getLabel(getMessageSourceAccessor(), locale);
        final var builder = createStandardRecord(RisType.HEAR, publication, true, locale)
                .periodicalNameFullFormatJO(publication.getPublicationTarget())
                .editor(publication.getEditors())
                .publisher(publication.getOrganization())
                .publishingPlace(publication.getAddress())
                .custom3(keyNoteType);
        return builder.build();
    }

    /**
     * Create the RIS record for the given report.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(Report publication, Locale locale) {
        final var builder = createStandardRecord(RisType.RPRT, publication, true, locale)
                .volumeNumber(publication.getReportNumber())
                .publisher(publication.getInstitution())
                .publishingPlace(publication.getAddress())
                .custom3(publication.getReportType());
        return builder.build();
    }

    /**
     * Create the RIS record for the given patent.
     *
     * @param publication the publication to export.
     * @return the RIS record.
     */
    protected RisRecord createRecord(Patent publication, Locale locale) {
        final var builder = createStandardRecord(RisType.PAT, publication, true, locale)
                .publishingPlace(publication.getAddress())
                .publisherStandardNumber(publication.getPatentNumber())
                .publisher(publication.getInstitution())
                .custom3(publication.getPatentType());
        return builder.build();
    }

    /**
     * Create the RIS record for the given misc document.
     *
     * @param publication the publication to export.
     * @param risType     the RIS type for the given publication.
     * @return the RIS record.
     */
    protected RisRecord createRecord(MiscDocument publication, RisType risType, Locale locale) {
        final var builder = createStandardRecord(risType, publication, true, locale)
                .secondaryTitle(publication.getHowPublished())
                .volumeNumber(publication.getDocumentNumber())
                .editor(publication.getOrganization())
                .publisher(publication.getPublisher())
                .publishingPlace(publication.getAddress())
                .custom3(publication.getDocumentType());
        return builder.build();
    }

    @Override
    public Stream<Publication> getPublicationStreamFrom(Reader ris, boolean keepRisId, boolean assignRandomId,
                                                        boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference,
                                                        Locale locale, Progression progression)
            throws Exception {
        return KRisIO.processToStream(ris).map(it -> {
            try {
                return createPublicationFor(it, keepRisId, assignRandomId, ensureAtLeastOneMember, createMissedJournal, createMissedConference, locale);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).filter(it -> it != null);
    }

    private String fieldDoi(RisRecord record, String... fieldValue) throws Exception {
        if (fieldValue != null) {
            for (final var value : fieldValue) {
                if (!Strings.isNullOrEmpty(value)) {
                    final var doi = this.doiTools.getDOINumberFromDOIUrlOrNull(value);
                    if (!Strings.isNullOrEmpty(doi)) {
                        return doi;
                    }
                }
            }
        }
        return null;
    }

    private Journal findJournalOrCreateProxy(RisRecord record, String journalName, String referencePublisher, String referenceIssn) {
        try {
            return findJournal(record, journalName, referencePublisher, referenceIssn);
        } catch (MissedJournalException ex) {
            // Create a proxy journal that is not supposed to be saved in the database.
            return new JournalFake(journalName, referencePublisher, referenceIssn);
        }
    }

    private Journal findJournal(RisRecord record, String journalName, String referencePublisher, String referenceIssn) {
        var journals = this.journalService.getJournalsByName(journalName);
        if (journals == null || journals.isEmpty()) {
            throw new MissedJournalException(record.getReferenceId(), journalName);
        }
        if (journals.size() == 1) {
            return journals.iterator().next();
        }
        final var js = new LinkedList<Journal>();
        final var msg = new StringBuilder();
        for (final var journal : journals) {
            if (Objects.equals(journal.getISSN(), referenceIssn)
                    || journal.getPublisher().contains(referencePublisher)) {
                js.add(journal);
            }
            msg.append("<br>\n* Id: ").append(journal.getId()); //$NON-NLS-1$
            msg.append("<br/>\n  Journal name: ").append(journal.getJournalName()); //$NON-NLS-1$
            msg.append("<br/>\n  Publisher: ").append(journal.getPublisher()); //$NON-NLS-1$
            msg.append("<br/>\n  ISSN: ").append(journal.getISSN()); //$NON-NLS-1$
        }
        if (js.size() == 1) {
            return js.get(0);
        }
        throw new IllegalArgumentException("Too many journals for record " + record.getReferenceId() //$NON-NLS-1$
                + " with the journal name: " + journalName //$NON-NLS-1$
                + "<br/>\nPlease fix the publisher and ISSN in your RIS." //$NON-NLS-1$
                + "The journal candidates are:<pre>" + msg + "</pre>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Conference findConferenceOrCreateProxy(RisRecord record, String conferenceName, String referencePublisher,
                                                   String referenceIsbn, String referenceIssn) {
        try {
            return findConference(record, conferenceName, referencePublisher, referenceIsbn, referenceIssn);
        } catch (MissedConferenceException ex) {
            // Create a proxy conference that is not supposed to be saved in the database.
            return new ConferenceFake(conferenceName, referencePublisher, referenceIsbn, referenceIssn);
        }
    }

    private Conference findConference(RisRecord record, String conferenceName, String referencePublisher,
                                      String referenceIsbn, String referenceIssn) {
        var conferences = this.conferenceService.getConferencesByName(conferenceName);
        if (conferences == null || conferences.isEmpty()) {
            throw new MissedConferenceException(record.getReferenceId(), conferenceName);
        }
        if (conferences.size() == 1) {
            return conferences.iterator().next();
        }
        final var js = new LinkedList<Conference>();
        final var msg = new StringBuilder();
        for (final var conference : conferences) {
            if (Objects.equals(conference.getISBN(), referenceIsbn)
                    || Objects.equals(conference.getISSN(), referenceIssn)
                    || conference.getPublisher().contains(referencePublisher)) {
                js.add(conference);
            }
            msg.append("<br>\n* Id: ").append(conference.getId()); //$NON-NLS-1$
            msg.append("<br/>\n  Conference name: ").append(conference.getNameOrAcronym()); //$NON-NLS-1$
            msg.append("<br/>\n  Publisher: ").append(conference.getPublisher()); //$NON-NLS-1$
            msg.append("<br/>\n  ISBN: ").append(conference.getISBN()); //$NON-NLS-1$
            msg.append("<br/>\n  ISSN: ").append(conference.getISSN()); //$NON-NLS-1$
        }
        if (js.size() == 1) {
            return js.get(0);
        }
        throw new IllegalArgumentException("Too many conferences for record " + record.getReferenceId() //$NON-NLS-1$
                + " with the conference name: " + conferenceName //$NON-NLS-1$
                + "<br/>\nPossible candidates are:<pre>" + msg + "</pre>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Extract the publication from a RIS record.
     * This function does not save the publication in the database.
     *
     * @param record                 the entry itself.
     * @param keepRisId              indicates if the RIS reference id should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the reference ids are provided to the publication.
     *                               If this argument is {@code false}, the reference ids are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    indicates if the missed journal should be created in the database.
     * @param createMissedConference indicates if the missed conference should be created in the database.
     * @return the publication.
     * @throws Exception if RIS record cannot be parsed.
     */
    protected Publication createPublicationFor(RisRecord record, boolean keepRisId, boolean assignRandomId,
                                               boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference,
                                               Locale locale) throws Exception {
        final var type = getPublicationTypeFor(record, locale);
        if (type != null) {
            // Create a generic publication
            final var year = fieldYear(record, record.getPublicationYear());
            final var genericPublication = this.prePublicationFactory.createPrePublication(
                    type,
                    fieldRequired(record, "title", record.getTitle(), record.getAlternativeTitle(), record.getPrimaryTitle(), record.getSecondaryTitle()), //$NON-NLS-1$
                    field(record, "abstract", record.getAbstr(), record.getAbstr2()), //$NON-NLS-1$
                    fieldKeywords(record, "keywords", record.getKeywords()), //$NON-NLS-1$
                    fieldDate(record, record.getDate(), record.getPrimaryDate(), record.getAccessDate()),
                    year,
                    fieldIsbn(record, record.getIsbnIssn()),
                    fieldIssn(record, record.getIsbnIssn()),
                    fieldDoi(record, record.getDoi(), record.getReferenceId()),
                    null, // Ignore HAL id
                    field(record, "url", record.getUrl()), //$NON-NLS-1$
                    null, // Ignore video name
                    null, // Ignore DBLP id
                    null, // Ignore PDF file
                    null, // Ignore award certificate file
                    fieldLanguage(record, record.getLanguage()));
            genericPublication.setPublicationYear(year);

            // Generate the publication instance
            final Publication finalPublication;
            switch (type) {
                case INTERNATIONAL_JOURNAL_PAPER:
                    var journalName = fieldRequired(record, "journal", record.getPeriodicalNameFullFormatJO(), //$NON-NLS-1$
                            record.getPeriodicalNameFullFormatJF(), record.getPeriodicalNameStandardAbbrevation(),
                            record.getPeriodicalNameUserAbbrevation(), record.getBt());
                    var publisherName = field(record, "publisher", record.getPublisher()); //$NON-NLS-1$
                    Journal journal;
                    if (createMissedJournal) {
                        journal = findJournalOrCreateProxy(record, journalName, publisherName, genericPublication.getISSN());
                    } else {
                        journal = findJournal(record, journalName, publisherName, genericPublication.getISSN());
                    }
                    assert journal != null;
                    final var journalPaper = this.journalPaperService.createJournalPaper(genericPublication,
                            field(record, "volume", record.getVolumeNumber()), //$NON-NLS-1$
                            field(record, "number", record.getNumberOfVolumes()), //$NON-NLS-1$
                            fieldPages(record, record.getStartPage(), record.getEndPage()),
                            field(record, "series", record.getSection()), //$NON-NLS-1$
                            journal,
                            false);
                    finalPublication = journalPaper;
                    break;
                case INTERNATIONAL_JOURNAL_EDITION:
                    journalName = fieldRequired(record, "journal", record.getPeriodicalNameFullFormatJO(), //$NON-NLS-1$
                            record.getPeriodicalNameFullFormatJF(), record.getPeriodicalNameStandardAbbrevation(),
                            record.getPeriodicalNameUserAbbrevation(), record.getBt());
                    publisherName = field(record, "publisher", record.getPublisher()); //$NON-NLS-1$
                    if (createMissedJournal) {
                        journal = findJournalOrCreateProxy(record, journalName, publisherName, genericPublication.getISSN());
                    } else {
                        journal = findJournal(record, journalName, publisherName, genericPublication.getISSN());
                    }
                    assert journal != null;
                    final var journalEdition = this.journalEditionService.createJournalEdition(genericPublication,
                            field(record, "volume", record.getVolumeNumber()), //$NON-NLS-1$
                            field(record, "number", record.getNumberOfVolumes()), //$NON-NLS-1$
                            fieldPages(record, record.getStartPage(), record.getEndPage()),
                            journal,
                            false);
                    finalPublication = journalEdition;
                    break;
                case INTERNATIONAL_CONFERENCE_PAPER:
                    var conferenceName = fieldRequiredCleanPrefix(record, "conference", //$NON-NLS-1$
                            record.getSecondaryTitle(), record.getBt());
                    var nameComponents = ConferenceUtils.parseConferenceName(conferenceName);
                    Conference conference;
                    if (createMissedConference) {
                        conference = findConferenceOrCreateProxy(record, nameComponents.name,
                                field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    } else {
                        conference = findConference(record, nameComponents.name,
                                field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    }
                    assert conference != null;
                    final var conferencePaper = this.conferencePaperService.createConferencePaper(genericPublication,
                            conference,
                            nameComponents.occurrenceNumber,
                            field(record, "volume", record.getVolumeNumber()), //$NON-NLS-1$
                            field(record, "number", record.getNumberOfVolumes()), //$NON-NLS-1$
                            fieldPages(record, record.getStartPage(), record.getEndPage()),
                            field(record, "editor", record.getEditor()), //$NON-NLS-1$
                            field(record, "series", record.getTertiaryTitle()), //$NON-NLS-1$
                            field(record, "organization", record.getCustom4()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    finalPublication = conferencePaper;
                    break;
                case INTERNATIONAL_KEYNOTE:
                    conferenceName = fieldRequiredCleanPrefix(record, "conference", //$NON-NLS-1$
                            record.getSecondaryTitle(), record.getBt());
                    nameComponents = ConferenceUtils.parseConferenceName(conferenceName);
                    if (createMissedConference) {
                        conference = findConferenceOrCreateProxy(record, nameComponents.name,
                                field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    } else {
                        conference = findConference(record, nameComponents.name,
                                field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    }
                    assert conference != null;
                    //int conferenceOccurrenceNumber, String editors, String orga, String address
                    finalPublication = this.keyNoteService.createKeyNote(genericPublication,
                            conference,
                            nameComponents.occurrenceNumber,
                            field(record, "editor", record.getEditor()), //$NON-NLS-1$
                            field(record, "organization", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case INTERNATIONAL_BOOK:
                    finalPublication = this.bookService.createBook(genericPublication,
                            field(record, "volume", record.getVolumeNumber()), //$NON-NLS-1$
                            field(record, "number", record.getNumberOfVolumes()), //$NON-NLS-1$
                            fieldPages(record, record.getStartPage(), record.getEndPage()),
                            field(record, "edition", record.getEdition()), //$NON-NLS-1$
                            field(record, "editor", record.getEditor()), //$NON-NLS-1$
                            field(record, "series", record.getSection()), //$NON-NLS-1$
                            field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case INTERNATIONAL_BOOK_CHAPTER:
                    finalPublication = this.bookChapterService.createBookChapter(genericPublication,
                            fieldRequired(record, "booktitle", record.getSecondaryTitle()), //$NON-NLS-1$
                            field(record, "chapternumber", record.getSection()), //$NON-NLS-1$
                            field(record, "edition", record.getEdition()), //$NON-NLS-1$
                            field(record, "volume", record.getVolumeNumber()), //$NON-NLS-1$
                            field(record, "number", record.getNumberOfVolumes()), //$NON-NLS-1$
                            fieldPages(record, record.getStartPage(), record.getEndPage()),
                            field(record, "editor", record.getEditor()), //$NON-NLS-1$
                            field(record, "series", record.getTertiaryTitle()), //$NON-NLS-1$
                            field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case PHD_THESIS:
                case MASTER_THESIS:
                    finalPublication = this.thesisService.createThesis(genericPublication,
                            fieldRequired(record, "institution", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case TECHNICAL_REPORT:
                case TUTORIAL_DOCUMENTATION:
                    finalPublication = this.reportService.createReport(genericPublication,
                            field(record, "number", record.getVolumeNumber(), record.getEdition(), record.getAccessionNumber()), //$NON-NLS-1$
                            field(record, "type", record.getCustom3()), //$NON-NLS-1$
                            fieldRequired(record, "institution", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case ARTISTIC_PRODUCTION:
                case INTERNATIONAL_PRESENTATION:
                case RESEARCH_TOOL:
                case SCIENTIFIC_CULTURE_PAPER:
                case INTERNATIONAL_TV_OR_ONLINE_SHOW:
                case INTERNATIONAL_NEWS_PAPER:
                case OTHER:
                    finalPublication = this.miscDocumentService.createMiscDocument(genericPublication,
                            field(record, "documentnumber", record.getVolumeNumber()), //$NON-NLS-1$
                            fieldRequired(record, "howpublished", record.getSecondaryTitle()), //$NON-NLS-1$
                            field(record, "documenttype", record.getCustom3()), //$NON-NLS-1$
                            field(record, "organization", record.getEditor()), //$NON-NLS-1$
                            field(record, "publisher", record.getPublisher()), //$NON-NLS-1$
                            field(record, "address", record.getPublishingPlace()), //$NON-NLS-1$
                            false);
                    break;
                case EUROPEAN_PATENT:
                case HDR_THESIS:
                case INTERNATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
                case INTERNATIONAL_ORAL_COMMUNICATION:
                case INTERNATIONAL_PATENT:
                case INTERNATIONAL_POSTER:
                case INTERNATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
                case NATIONAL_BOOK:
                case NATIONAL_BOOK_CHAPTER:
                case NATIONAL_CONFERENCE_PAPER:
                case NATIONAL_JOURNAL_EDITION:
                case NATIONAL_JOURNAL_PAPER:
                case NATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
                case NATIONAL_KEYNOTE:
                case NATIONAL_ORAL_COMMUNICATION:
                case NATIONAL_PATENT:
                case NATIONAL_POSTER:
                case NATIONAL_PRESENTATION:
                case NATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
                case NATIONAL_TV_OR_ONLINE_SHOW:
                case NATIONAL_NEWS_PAPER:
                case PROJECT_REPORT:
                case RESEARCH_TRANSFERT_REPORT:
                case SCIENTIFIC_CULTURE_BOOK:
                case SCIENTIFIC_CULTURE_BOOK_CHAPTER:
                case TEACHING_DOCUMENT:
                default:
                    throw new IllegalArgumentException("Unsupported type of publication for RIS record: " + record.getReferenceId()); //$NON-NLS-1$
            }

            // Generate the author list
            final String authorsField;
            final var authorsList = record.getAuthors();
            if (authorsList == null || authorsList.isEmpty()) {
                authorsField = field(record, "authoreditor", record.getEditor()); //$NON-NLS-1$
            } else {
                final var bb = new StringBuilder();
                for (final var author : authorsList) {
                    if (bb.length() > 0) {
                        bb.append(" and "); //$NON-NLS-1$
                    }
                    bb.append(author);
                }
                authorsField = bb.toString();
            }
            try {
                final var authors = this.personService.extractPersonsFrom(authorsField, true, assignRandomId, ensureAtLeastOneMember);
                if (authors.isEmpty()) {
                    throw new IllegalArgumentException("No author for the RIS record: " + record.getReferenceId()); //$NON-NLS-1$
                }
                finalPublication.setTemporaryAuthors(authors);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Invalid RIS record: " + record.getReferenceId() + ". " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (keepRisId) {
                final var cid = record.getReferenceId();
                if (Strings.isNullOrEmpty(cid)) {
                    finalPublication.setPreferredStringId(null);
                } else {
                    final var cleanId = cid.replaceAll("[^a-zA-Z0-9_-]+", "_"); //$NON-NLS-1$ //$NON-NLS-2$
                    finalPublication.setPreferredStringId(cleanId);
                }
            }

            if (assignRandomId) {
                finalPublication.setId(generateUUID().intValue());
            }

            return finalPublication;
        }
        throw new IllegalArgumentException("Unsupported type of RIS record for: " + record.getReferenceId()); //$NON-NLS-1$
    }

    private boolean isMasterThesis(RisRecord record, Locale locale) {
        final var type = record.getCustom3();
        if (!Strings.isNullOrEmpty(type)) {
            try {
                final var loc = java.util.Locale.getDefault();
                final var lang = fieldLanguage(record, record.getLanguage());
                final String label;
                try {
                    java.util.Locale.setDefault(lang.getLocale());
                    label = PublicationType.MASTER_THESIS.getLabel(getMessageSourceAccessor(), locale);
                } finally {
                    java.util.Locale.setDefault(loc);
                }
                return type.equalsIgnoreCase(label);
            } catch (Throwable ex) {
                //
            }
        }
        return false;
    }

    /**
     * Replies the publication type that could support the given RIS record.
     *
     * @param record the RIS record.
     * @return the publication type.
     */
    protected PublicationType getPublicationTypeFor(RisRecord record, Locale locale) {
        switch (record.getType()) {
            case EJOUR:
            case JFULL:
            case JOUR:
            case SER:
                return PublicationType.INTERNATIONAL_JOURNAL_PAPER;
            case CPAPER:
                return PublicationType.INTERNATIONAL_CONFERENCE_PAPER;
            case BOOK:
            case EBOOK:
            case ENCYC:
                return PublicationType.INTERNATIONAL_BOOK;
            case CHAP:
            case ECHAP:
                return PublicationType.INTERNATIONAL_BOOK_CHAPTER;
            case HEAR:
                return PublicationType.INTERNATIONAL_KEYNOTE;
            case PCOMM:
                return PublicationType.INTERNATIONAL_PRESENTATION;
            case EDBOOK:
                return PublicationType.INTERNATIONAL_JOURNAL_EDITION;
            case MANSCPT:
            case THES:
                if (isMasterThesis(record, locale)) {
                    return PublicationType.MASTER_THESIS;
                }
                return PublicationType.PHD_THESIS;
            case DICT:
            case GOVDOC:
            case LEGAL:
            case RPRT:
            case STAND:
            case STAT:
                return PublicationType.TECHNICAL_REPORT;
            case GRANT:
            case PAT:
                return PublicationType.INTERNATIONAL_PATENT;
            case MGZN:
            case NEWS:
                return PublicationType.INTERNATIONAL_NEWS_PAPER;
            case MPCT:
                return PublicationType.INTERNATIONAL_TV_OR_ONLINE_SHOW;
            case ADVS:
            case ART:
            case MULTI:
            case MUSIC:
            case SOUND:
            case VIDEO:
                return PublicationType.ARTISTIC_PRODUCTION;
            case AGGR:
            case COMP:
            case DATA:
            case DBASE:
                return PublicationType.RESEARCH_TOOL;
            case SLIDE:
                return PublicationType.TUTORIAL_DOCUMENTATION;
            case ABST:
            case ANCIENT:
            case BILL:
            case BLOG:
            case CASE:
            case CHART:
            case CLSWK:
            case CONF:
            case CTLG:
            case ELEC:
            case EQUA:
            case FIGURE:
            case GEN:
            case ICOMM:
            case INPR:
            case MAP:
            case PAMP:
            case UNBILL:
            case UNPB:
                return PublicationType.OTHER;
            default:
                break;
        }
        throw new IllegalArgumentException("Unsupported type of the RIS record: " + record.getType()); //$NON-NLS-1$
    }

}
