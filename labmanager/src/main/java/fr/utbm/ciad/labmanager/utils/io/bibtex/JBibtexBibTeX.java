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

package fr.utbm.ciad.labmanager.utils.io.bibtex;

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
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.bibtex.bugfix.BugfixLaTeXPrinter;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.jbibtex.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.jbibtex.BibTeXEntry.*;

/**
 * Implementation of the utilities for BibTeX based on the JBibtex library.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class JBibtexBibTeX extends AbstractBibTeX {

    /**
     * Field {@code _internal_db_id}.
     */
    protected static final Key KEY_INTERNAL_DB_ID = new Key("_internal_db_id"); //$NON-NLS-1$

    /**
     * Field {@code abstract}.
     */
    protected static final Key KEY_ABSTRACT = new Key(KEY_ABSTRACT_NAME);

    /**
     * Field {@code keywords}.
     */
    protected static final Key KEY_KEYWORD = new Key(KEY_KEYWORDS_NAME);

    /**
     * Field {@code isbn}.
     */
    protected static final Key KEY_ISBN = new Key(KEY_ISBN_NAME);

    /**
     * Field {@code issn}.
     */
    protected static final Key KEY_ISSN = new Key(KEY_ISSN_NAME);

    /**
     * Field {@code halId}.
     */
    protected static final Key KEY_HALID = new Key(KEY_HALID_NAME);

    /**
     * Field {@code dblp}.
     */
    protected static final Key KEY_DBLP = new Key(KEY_DBLP_NAME);

    /**
     * Field {@code video}.
     */
    protected static final Key KEY_VIDEO = new Key(KEY_VIDEO_NAME);

    /**
     * Field {@code language}.
     */
    protected static final Key KEY_LANGUAGE = new Key(KEY_LANGUAGE_NAME);

    /**
     * Field {@code _publication_type}.
     */
    protected static final Key KEY_PUBLICATION_TYPE = new Key("_publication_type"); //$NON-NLS-1$

    /**
     * Field {@code _publication_type_name}.
     */
    protected static final Key KEY_PUBLICATION_TYPE_NAME = new Key("_publication_type_name"); //$NON-NLS-1$

    /**
     * Field {@code _publication_category}.
     */
    protected static final Key KEY_PUBLICATION_CATEGORY = new Key("_publication_category"); //$NON-NLS-1$

    /**
     * Field {@code _publication_category_name}.
     */
    protected static final Key KEY_PUBLICATION_CATEGORY_NAME = new Key("_publication_category_name"); //$NON-NLS-1$

    /**
     * Field {@code _scimago_qindex}.
     */
    protected static final Key KEY_SCIMAGO_QINDEX = new Key("_scimago_qindex"); //$NON-NLS-1$

    /**
     * Field {@code _wos_qindex}.
     */
    protected static final Key KEY_WOS_QINDEX = new Key("_wos_qindex"); //$NON-NLS-1$

    /**
     * Field {@code _impact_factor}.
     */
    protected static final Key KEY_IMPACT_FACTOR = new Key("_impact_factor"); //$NON-NLS-1$

    /**
     * Field {@code _core_ranking}.
     */
    protected static final Key KEY_CORE_RANKING = new Key("_core_ranking"); //$NON-NLS-1$

    private static final String MESSAGE_PREFIX = "jBibtexBibTeX."; //$NON-NLS-1$

    private final PrePublicationFactory prePublicationFactory;

    private final JournalService journalService;

    private final ConferenceService conferenceService;

    private final PersonService personService;

    private final BookService bookService;

    private final BookChapterService bookChapterService;

    private final ConferencePaperService conferencePaperService;

    private final JournalPaperService journalPaperService;

    private final MiscDocumentService miscDocumentService;

    private final ReportService reportService;

    private final ThesisService thesisService;

    /**
     * Constructor. This constructor is ready for injection.
     *
     * @param messages               the accessor to the localized messages.
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
     */
    public JBibtexBibTeX(
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
            @Autowired ThesisService thesisService) {
        super(messages);
        this.prePublicationFactory = prePublicationFactory;
        this.journalService = journalService;
        this.conferenceService = conferenceService;
        this.personService = personService;
        this.bookService = bookService;
        this.bookChapterService = bookChapterService;
        this.conferencePaperService = conferencePaperService;
        this.journalPaperService = journalPaperService;
        this.miscDocumentService = miscDocumentService;
        this.reportService = reportService;
        this.thesisService = thesisService;
    }

    /**
     * Convert any special macro from a TeX string into its equivalent in the current character encoding.
     * For example, the macros {@code \'e} is translated to {@code é}.
     * <p>
     * The conversion in the opposite direction is supported by {@link #toTeXString(String)}.
     *
     * @param texString the TeX data.
     * @param entry     the BibTeX entry in which the LaTeX string is appearing. If this argument is {@code null},
     *                  the BibTeX entry is unknown.
     * @return the Java string that corresponds to the given TeX data.
     * @throws Exception if the TeX string cannot be parsed.
     * @see #toTeXString(String)
     */
    protected static String parseTeXString(String texString, BibTeXEntry entry) throws Exception {
        if (!Strings.isNullOrEmpty(texString)) {
            try {
                final var latexParser = new LaTeXParser();
                var latexObjects = latexParser.parse(texString);
                final var latexPrinter = new BugfixLaTeXPrinter(false);
                final var plainTextString = latexPrinter.print(latexObjects);
                return plainTextString;
            } catch (Throwable ex) {
                final var bibtexEntry = entry != null ? entry.getKey().getValue() : null;
                throw new RuntimeException(
                        "Unable to parse the following LaTeX text: " + texString //$NON-NLS-1$
                                + "\nSource error is: " + ex.getLocalizedMessage() //$NON-NLS-1$
                                + "\nBibTeX entry is: " + (Strings.isNullOrEmpty(bibtexEntry) ? "n/c" : bibtexEntry), //$NON-NLS-1$ //$NON-NLS-2$
                        ex);
            }
        }
        return null;
    }

    /**
     * Replies if the given field may contains LaTeX markup.
     *
     * @param field the field to test.
     * @return {@code true} if the field's value may contain LaTeX markup.
     */
    protected static boolean isLaTeXField(Key field) {
        return !(KEY_CROSSREF.equals(field)
                || KEY_DOI.equals(field)
                || KEY_EPRINT.equals(field)
                || KEY_KEY.equals(field)
                || KEY_URL.equals(field)
                || KEY_HALID.equals(field)
                || KEY_DBLP.equals(field)
                || KEY_VIDEO.equals(field));
    }

    private static String field(BibTeXEntry entry, Key key) throws Exception {
        final var value = entry.getField(key);
        if (value != null) {
            var strValue = ConferenceUtils.normalizeString(value.toUserString());
            if (isLaTeXField(key)) {
                strValue = parseTeXString(strValue, entry);
            }
            return Strings.emptyToNull(strValue);
        }
        return null;
    }

    private static String fieldRequired(BibTeXEntry entry, Key key) throws Exception {
        final var value = entry.getField(key);
        if (value != null) {
            var strValue = ConferenceUtils.normalizeString(value.toUserString());
            if (isLaTeXField(key)) {
                strValue = parseTeXString(strValue, entry);
            }
            if (!Strings.isNullOrEmpty(strValue)) {
                return strValue;
            }
        }
        throw new IllegalStateException("Field '" + key.getValue() + "' is required for entry: " + entry.getKey().getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static String fieldRequiredCleanPrefix(BibTeXEntry entry, Key key) throws Exception {
        var value = field(entry, key);
        value = ConferenceUtils.removePrefixArticles(value);
        if (!Strings.isNullOrEmpty(value)) {
            return value;
        }
        throw new IllegalStateException("Field '" + key.getValue() + "' is required for entry: " + entry.getKey().getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static String field(BibTeXEntry entry, String key) throws Exception {
        return field(entry, new Key(key));
    }

    private static String pages(BibTeXEntry entry) throws Exception {
        var texValue = field(entry, KEY_PAGES);
        if (!Strings.isNullOrEmpty(texValue)) {
            // Some person uses the "--" LaTeX operator for representing a range
            texValue = texValue.replaceAll("\\-+", "-"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return texValue;
    }

    private static String or(String v1, String v2) {
        if (!Strings.isNullOrEmpty(v1)) {
            return v1;
        }
        if (!Strings.isNullOrEmpty(v2)) {
            return v2;
        }
        return null;
    }

    private static String orRequired(BibTeXEntry entry, Key k1, Key k2) throws Exception {
        final var value = or(field(entry, k1), field(entry, k2));
        if (!Strings.isNullOrEmpty(value)) {
            return value;
        }
        throw new IllegalStateException("Field '" + k1.getValue() + "' or '" + k2.getValue() //$NON-NLS-1$ //$NON-NLS-2$
                + "' is required for entry: " + entry.getKey().getValue()); //$NON-NLS-1$
    }

    private static PublicationLanguage language(BibTeXEntry entry) throws Exception {
        final var label = field(entry, new Key(KEY_LANGUAGE_NAME));
        return PublicationLanguage.valueOfCaseInsensitive(label);
    }

    private static int year(BibTeXEntry entry) throws Exception {
        final var yearValue = field(entry, KEY_YEAR);
        if (Strings.isNullOrEmpty(yearValue)) {
            throw new IllegalArgumentException("Invalid year format for: " + entry.getKey().getValue()); //$NON-NLS-1$
        }
        final int year;
        try {
            year = Integer.parseUnsignedInt(yearValue);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid year format for: " + entry.getKey().getValue(), ex); //$NON-NLS-1$
        }
        return year;
    }

    private static LocalDate date(BibTeXEntry entry) throws Exception {
        final var year = year(entry);
        final var monthValue = field(entry, KEY_MONTH);
        if (!Strings.isNullOrEmpty(monthValue)) {
            switch (monthValue) {
                case "01": //$NON-NLS-1$
                case "jan": //$NON-NLS-1$
                case "January": //$NON-NLS-1$
                    return LocalDate.parse(year + "-01-01"); //$NON-NLS-1$
                case "02": //$NON-NLS-1$
                case "feb": //$NON-NLS-1$
                case "February": //$NON-NLS-1$
                    return LocalDate.parse(year + "-02-01"); //$NON-NLS-1$
                case "03": //$NON-NLS-1$
                case "mar": //$NON-NLS-1$
                case "March": //$NON-NLS-1$
                    return LocalDate.parse(year + "-03-01"); //$NON-NLS-1$
                case "04": //$NON-NLS-1$
                case "apr": //$NON-NLS-1$
                case "April": //$NON-NLS-1$
                    return LocalDate.parse(year + "-04-01"); //$NON-NLS-1$
                case "05": //$NON-NLS-1$
                case "may": //$NON-NLS-1$
                case "May": //$NON-NLS-1$
                    return LocalDate.parse(year + "-05-01"); //$NON-NLS-1$
                case "06": //$NON-NLS-1$
                case "jun": //$NON-NLS-1$
                case "June": //$NON-NLS-1$
                    return LocalDate.parse(year + "-06-01"); //$NON-NLS-1$
                case "07": //$NON-NLS-1$
                case "jul": //$NON-NLS-1$
                case "July": //$NON-NLS-1$
                    return LocalDate.parse(year + "-07-01"); //$NON-NLS-1$
                case "08": //$NON-NLS-1$
                case "aug": //$NON-NLS-1$
                case "August": //$NON-NLS-1$
                    return LocalDate.parse(year + "-08-01"); //$NON-NLS-1$
                case "09": //$NON-NLS-1$
                case "sep": //$NON-NLS-1$
                case "September": //$NON-NLS-1$
                    return LocalDate.parse(year + "-09-01"); //$NON-NLS-1$
                case "10": //$NON-NLS-1$
                case "oct": //$NON-NLS-1$
                case "October": //$NON-NLS-1$
                    return LocalDate.parse(year + "-10-01"); //$NON-NLS-1$
                case "11": //$NON-NLS-1$
                case "nov": //$NON-NLS-1$
                case "November": //$NON-NLS-1$
                    return LocalDate.parse(year + "-11-01"); //$NON-NLS-1$
                case "12": //$NON-NLS-1$
                case "dec": //$NON-NLS-1$
                case "December": //$NON-NLS-1$
                    return LocalDate.parse(year + "-12-01"); //$NON-NLS-1$
                default:
                    //
            }
        }
        return null;
    }

    private static Key createBibTeXId(Publication publication) {
        return new Key(publication.getPreferredStringId());
    }

    private static void addField(BibTeXEntry entry, Key key, QuartileRanking value) {
        if (value != null) {
            entry.addField(key, new StringValue(value.toString(), StringValue.Style.BRACED));
        }
    }

    private static void addField(BibTeXEntry entry, Key key, CoreRanking value) {
        if (value != null) {
            entry.addField(key, new StringValue(value.toString(), StringValue.Style.BRACED));
        }
    }

    private static void addField(BibTeXEntry entry, Key key, int value) {
        entry.addField(key, new DigitStringValue(Integer.toString(value)));
    }

    private static void addMonthField(BibTeXEntry entry, LocalDate value) {
        if (value != null) {
            final String monthValue;
            switch (value.getMonth()) {
                case APRIL:
                    monthValue = "apr"; //$NON-NLS-1$
                    break;
                case AUGUST:
                    monthValue = "aug"; //$NON-NLS-1$
                    break;
                case DECEMBER:
                    monthValue = "dec"; //$NON-NLS-1$
                    break;
                case FEBRUARY:
                    monthValue = "feb"; //$NON-NLS-1$
                    break;
                case JANUARY:
                    monthValue = "jan"; //$NON-NLS-1$
                    break;
                case JULY:
                    monthValue = "jul"; //$NON-NLS-1$
                    break;
                case JUNE:
                    monthValue = "jun"; //$NON-NLS-1$
                    break;
                case MARCH:
                    monthValue = "mar"; //$NON-NLS-1$
                    break;
                case MAY:
                    monthValue = "may"; //$NON-NLS-1$
                    break;
                case NOVEMBER:
                    monthValue = "nov"; //$NON-NLS-1$
                    break;
                case OCTOBER:
                    monthValue = "oct"; //$NON-NLS-1$
                    break;
                case SEPTEMBER:
                    monthValue = "sep"; //$NON-NLS-1$
                    break;
                default:
                    return;
            }
            entry.addField(KEY_MONTH, new DigitStringValue(monthValue));
        }
    }

    @Override
    public String parseTeXString(String texString) throws Exception {
        return parseTeXString(texString, null);
    }

    @Override
    public Stream<Publication> getPublicationStreamFrom(Reader bibtex, boolean keepBibTeXId, boolean assignRandomId,
                                                        boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference, Progression progression) throws Exception {
        final var progress = progression == null ? new DefaultProgression() : progression;
        progress.setProperties(0, 0, 100, false);
        try (var filteredReader = new CharacterFilterReader(bibtex)) {
            final var bibtexParser = new BibTeXParser();
            final var database = bibtexParser.parse(filteredReader);
            progress.setValue(50);
            if (database != null) {
                final var subProgress = progress.subTask(50, 0, database.getEntries().size());
                return database.getEntries().entrySet().stream().map(it -> {
                    try {
                        return createPublicationFor(it.getKey(), it.getValue(), keepBibTeXId, assignRandomId, ensureAtLeastOneMember,
                                createMissedJournal, createMissedConference);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        subProgress.increment();
                    }
                });
            }
        }
        return Collections.<Publication>emptyList().stream();
    }

    /**
     * Replies the publication type that could support the given BibTeX entry.
     *
     * @param entry the BibTeX entry.
     * @return the publication type.
     */
    @SuppressWarnings("static-method")
    protected PublicationType getPublicationTypeFor(BibTeXEntry entry) {
        if (BibTeXEntry.TYPE_ARTICLE.equals(entry.getType())
                || BibTeXEntry.TYPE_INCOLLECTION.equals(entry.getType())) {
            return PublicationType.INTERNATIONAL_JOURNAL_PAPER;
        }
        if (BibTeXEntry.TYPE_INPROCEEDINGS.equals(entry.getType())
                || BibTeXEntry.TYPE_CONFERENCE.equals(entry.getType())) {
            return PublicationType.INTERNATIONAL_CONFERENCE_PAPER;
        }
        if (BibTeXEntry.TYPE_BOOK.equals(entry.getType())) {
            return PublicationType.INTERNATIONAL_BOOK;
        }
        if (BibTeXEntry.TYPE_BOOKLET.equals(entry.getType())
                || BibTeXEntry.TYPE_INBOOK.equals(entry.getType())) {
            return PublicationType.INTERNATIONAL_BOOK_CHAPTER;
        }
        if (BibTeXEntry.TYPE_PHDTHESIS.equals(entry.getType())) {
            return PublicationType.PHD_THESIS;
        }
        if (BibTeXEntry.TYPE_MASTERSTHESIS.equals(entry.getType())) {
            return PublicationType.MASTER_THESIS;
        }
        if (BibTeXEntry.TYPE_TECHREPORT.equals(entry.getType())) {
            return PublicationType.TECHNICAL_REPORT;
        }
        if (BibTeXEntry.TYPE_MANUAL.equals(entry.getType())) {
            return PublicationType.TUTORIAL_DOCUMENTATION;
        }
        if (BibTeXEntry.TYPE_MISC.equals(entry.getType())
                || BibTeXEntry.TYPE_PROCEEDINGS.equals(entry.getType())
                || BibTeXEntry.TYPE_UNPUBLISHED.equals(entry.getType())) {
            return PublicationType.OTHER;
        }
        throw new IllegalArgumentException("Unsupported type of the BibTeX entry: " + entry.getType()); //$NON-NLS-1$
    }

    private Journal findJournalOrCreateProxy(Key key, String journalName, String dbId, String referencePublisher, String referenceIssn) {
        try {
            return findJournal(key, journalName, dbId, referencePublisher, referenceIssn);
        } catch (MissedJournalException ex) {
            // Create a proxy journal that is not supposed to be saved in the database.
            return new JournalFake(journalName, referencePublisher, referenceIssn);
        }
    }

    private Journal findJournal(Key key, String journalName, String dbId, String referencePublisher, String referenceIssn) {
        if (!Strings.isNullOrEmpty(dbId)) {
            try {
                final var id = Integer.parseInt(dbId);
                final var journal = this.journalService.getJournalById(id);
                if (journal != null) {
                    return journal;
                }
            } catch (Throwable ex) {
                // Silent
            }
        }
        var journals = this.journalService.getJournalsByName(journalName);
        if (journals == null || journals.isEmpty()) {
            throw new MissedJournalException(key.getValue(), journalName);
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
        throw new IllegalArgumentException("Too many journals for entry " + key.getValue() //$NON-NLS-1$
                + " with the journal name: " + journalName //$NON-NLS-1$
                + "<br/>\nPlease fix the publisher and ISSN in your BibTeX. If the ambiguity is still present, select one in the following list and write the Id in the field " + KEY_INTERNAL_DB_ID.getValue() //$NON-NLS-1$
                + " field in your BibTeX:<pre>" + msg + "</pre>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Conference findConferenceOrCreateProxy(Key key, String conferenceName, String dbId, String referencePublisher,
                                                   String referenceIsbn, String referenceIssn) {
        try {
            return findConference(key, conferenceName, dbId, referencePublisher, referenceIsbn, referenceIssn);
        } catch (MissedConferenceException ex) {
            // Create a proxy conference that is not supposed to be saved in the database.
            return new ConferenceFake(conferenceName, referencePublisher, referenceIsbn, referenceIssn);
        }
    }

    private Conference findConference(Key key, String conferenceName, String dbId, String referencePublisher,
                                      String referenceIsbn, String referenceIssn) {
        if (!Strings.isNullOrEmpty(dbId)) {
            try {
                final var id = Integer.parseInt(dbId);
                final var conference = this.conferenceService.getConferenceById(id);
                if (conference != null) {
                    return conference;
                }
            } catch (Throwable ex) {
                // Silent
            }
        }
        var conferences = this.conferenceService.getConferencesByName(conferenceName);
        if (conferences == null || conferences.isEmpty()) {
            throw new MissedConferenceException(key.getValue(), conferenceName);
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
        throw new IllegalArgumentException("Too many conferences for entry " + key.getValue() //$NON-NLS-1$
                + " with the conference name: " + conferenceName //$NON-NLS-1$
                + "<br/>\nPlease fix the publisher, ISBN and ISSN in your BibTeX. If the ambiguity is still present, select one in the following list and write the Id in the field " + KEY_INTERNAL_DB_ID.getValue() //$NON-NLS-1$
                + " field in your BibTeX:<pre>" + msg + "</pre>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Extract the publication from a BibTeX entry.
     * This function does not save the publication in the database.
     *
     * @param key                    the key of the entry.
     * @param entry                  the entry itself.
     * @param keepBibTeXId           indicates if the BibTeX keys should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the BibTeX keys are provided to the publication.
     *                               If this argument is {@code false}, the BibTeX keys are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    indicates if the missed journal should be created in the database.
     * @param createMissedConference indicates if the missed conference should be created in the database.
     * @return the publication.
     * @throws Exception if LaTeX code cannot be parsed.
     */
    protected Publication createPublicationFor(Key key, BibTeXEntry entry, boolean keepBibTeXId, boolean assignRandomId,
                                               boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference) throws Exception {
        final var type = getPublicationTypeFor(entry);
        if (type != null) {
            // Create a generic publication
            final var genericPublication = this.prePublicationFactory.createPrePublication(
                    type,
                    fieldRequired(entry, KEY_TITLE),
                    field(entry, KEY_ABSTRACT_NAME),
                    field(entry, KEY_KEYWORDS_NAME),
                    date(entry), year(entry),
                    field(entry, KEY_ISBN_NAME),
                    field(entry, KEY_ISSN_NAME),
                    field(entry, KEY_DOI),
                    field(entry, KEY_HALID),
                    field(entry, KEY_URL),
                    field(entry, KEY_VIDEO_NAME),
                    field(entry, KEY_DBLP_NAME),
                    null, // Ignore PDF file
                    null, // Ignore award certificate file
                    language(entry));
            genericPublication.setPublicationYear(year(entry));

            // Generate the publication instance
            final Publication finalPublication;
            switch (type) {
                case INTERNATIONAL_JOURNAL_PAPER:
                    var journalName = fieldRequired(entry, KEY_JOURNAL);
                    final Journal journal;
                    if (createMissedJournal) {
                        journal = findJournalOrCreateProxy(key, journalName,
                                field(entry, KEY_INTERNAL_DB_ID),
                                field(entry, KEY_PUBLISHER),
                                genericPublication.getISSN());
                    } else {
                        journal = findJournal(key, journalName,
                                field(entry, KEY_INTERNAL_DB_ID),
                                field(entry, KEY_PUBLISHER),
                                genericPublication.getISSN());
                    }
                    assert journal != null;
                    final JournalPaper journalPaper = this.journalPaperService.createJournalPaper(genericPublication,
                            field(entry, KEY_VOLUME),
                            field(entry, KEY_NUMBER),
                            pages(entry),
                            field(entry, KEY_SERIES),
                            journal,
                            false);
                    finalPublication = journalPaper;
                    break;
                case INTERNATIONAL_CONFERENCE_PAPER:
                    var conferenceName = fieldRequiredCleanPrefix(entry, KEY_BOOKTITLE);
                    final var nameComponents = ConferenceUtils.parseConferenceName(conferenceName);
                    final Conference conference;
                    if (createMissedConference) {
                        conference = findConferenceOrCreateProxy(key, nameComponents.name,
                                field(entry, KEY_INTERNAL_DB_ID),
                                field(entry, KEY_PUBLISHER),
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    } else {
                        conference = findConference(key, nameComponents.name,
                                field(entry, KEY_INTERNAL_DB_ID),
                                field(entry, KEY_PUBLISHER),
                                genericPublication.getISBN(),
                                genericPublication.getISSN());
                    }
                    assert conference != null;
                    final var conferencePaper = this.conferencePaperService.createConferencePaper(genericPublication,
                            conference,
                            nameComponents.occurrenceNumber,
                            field(entry, KEY_VOLUME),
                            field(entry, KEY_NUMBER),
                            pages(entry),
                            field(entry, KEY_EDITOR),
                            field(entry, KEY_SERIES),
                            field(entry, KEY_ORGANIZATION),
                            field(entry, KEY_ADDRESS),
                            false);
                    finalPublication = conferencePaper;
                    break;
                case INTERNATIONAL_BOOK:
                    finalPublication = this.bookService.createBook(genericPublication,
                            field(entry, KEY_VOLUME),
                            field(entry, KEY_NUMBER),
                            pages(entry),
                            field(entry, KEY_EDITION),
                            field(entry, KEY_EDITOR),
                            field(entry, KEY_SERIES),
                            field(entry, KEY_PUBLISHER),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case INTERNATIONAL_BOOK_CHAPTER:
                    finalPublication = this.bookChapterService.createBookChapter(genericPublication,
                            fieldRequired(entry, KEY_BOOKTITLE),
                            field(entry, KEY_CHAPTER),
                            field(entry, KEY_EDITION),
                            field(entry, KEY_VOLUME),
                            field(entry, KEY_NUMBER),
                            pages(entry),
                            field(entry, KEY_EDITOR),
                            field(entry, KEY_SERIES),
                            field(entry, KEY_PUBLISHER),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case PHD_THESIS:
                case MASTER_THESIS:
                    finalPublication = this.thesisService.createThesis(genericPublication,
                            orRequired(entry, KEY_SCHOOL, KEY_INSTITUTION),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case TECHNICAL_REPORT:
                    finalPublication = this.reportService.createReport(genericPublication,
                            or(field(entry, KEY_NUMBER), field(entry, KEY_EDITION)),
                            field(entry, KEY_TYPE),
                            fieldRequired(entry, KEY_INSTITUTION),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case TUTORIAL_DOCUMENTATION:
                    finalPublication = this.reportService.createReport(genericPublication,
                            or(field(entry, KEY_NUMBER), field(entry, KEY_EDITION)),
                            field(entry, KEY_TYPE),
                            orRequired(entry, KEY_ORGANIZATION, KEY_PUBLISHER),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case OTHER:
                    finalPublication = this.miscDocumentService.createMiscDocument(genericPublication,
                            field(entry, KEY_NUMBER),
                            fieldRequired(entry, KEY_HOWPUBLISHED),
                            field(entry, KEY_TYPE),
                            field(entry, KEY_ORGANIZATION),
                            field(entry, KEY_PUBLISHER),
                            field(entry, KEY_ADDRESS),
                            false);
                    break;
                case ARTISTIC_PRODUCTION:
                case EUROPEAN_PATENT:
                case HDR_THESIS:
                case INTERNATIONAL_JOURNAL_EDITION:
                case INTERNATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
                case INTERNATIONAL_KEYNOTE:
                case INTERNATIONAL_ORAL_COMMUNICATION:
                case INTERNATIONAL_PATENT:
                case INTERNATIONAL_POSTER:
                case INTERNATIONAL_PRESENTATION:
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
                case PROJECT_REPORT:
                case RESEARCH_TOOL:
                case RESEARCH_TRANSFERT_REPORT:
                case SCIENTIFIC_CULTURE_BOOK:
                case SCIENTIFIC_CULTURE_BOOK_CHAPTER:
                case SCIENTIFIC_CULTURE_PAPER:
                case TEACHING_DOCUMENT:
                case INTERNATIONAL_TV_OR_ONLINE_SHOW:
                case NATIONAL_TV_OR_ONLINE_SHOW:
                case INTERNATIONAL_NEWS_PAPER:
                case NATIONAL_NEWS_PAPER:
                default:
                    throw new IllegalArgumentException("Unsupported type of publication for BibTeX entry: " + key.getValue()); //$NON-NLS-1$
            }

            // Generate the author list
            final var authorField = orRequired(entry, KEY_AUTHOR, KEY_EDITOR);
            try {
                final var authors = this.personService.extractPersonsFrom(authorField, true, assignRandomId, ensureAtLeastOneMember);
                if (authors.isEmpty()) {
                    throw new IllegalArgumentException("No author for the BibTeX entry: " + key.getValue()); //$NON-NLS-1$
                }
                finalPublication.setTemporaryAuthors(authors);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Invalid BibTeX entry: " + key.getValue() + ". " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (keepBibTeXId) {
                final var cleanKey = entry.getKey().getValue().replaceAll("[^a-zA-Z0-9_-]+", "_"); //$NON-NLS-1$ //$NON-NLS-2$
                finalPublication.setPreferredStringId(cleanKey);
            }

            if (assignRandomId) {
                finalPublication.setId(generateUUID().intValue());
            }

            return finalPublication;
        }
        throw new IllegalArgumentException("Unsupported type of BibTeX entry for: " + key.getValue()); //$NON-NLS-1$
    }

    @Override
    public void exportPublications(Writer output, Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                   Progression progression, Logger logger) throws IOException {
        progression.setProperties(0, 0, 100, false);
        final var database = createDatabase(publications, configurator, progression.subTask(95));
        final var bibtexFormatter = new BibTeXFormatter();
        bibtexFormatter.format(database, output);
        progression.end();
    }

    /**
     * Create a JBibTeX database with the given list of publications.
     *
     * @param publications the publications to put into the database.
     * @param configurator the configurator of the export.
     * @param progression  the progression indicator.
     * @return the JBibTeX database.
     */
    protected BibTeXDatabase createDatabase(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) {
        progression.setProperties(0, 0, publications.size(), false);
        final var db = new BibTeXDatabase();
        for (final var publication : publications) {
            addPublication(db, publication, configurator.getLocaleOrLanguageLocale(publication.getMajorLanguage()));
            progression.increment();
        }
        progression.end();
        return db;
    }

    /**
     * Add a publication into a JBibTeX database.
     *
     * @param database    the JBibTeX database.
     * @param publication the publication to put into the database.
     * @param locale      the locale to use.
     */
    protected void addPublication(BibTeXDatabase database, Publication publication, Locale locale) {
        final BibTeXEntry entry;
        switch (publication.getType()) {
            case INTERNATIONAL_JOURNAL_PAPER:
            case INTERNATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
            case NATIONAL_JOURNAL_PAPER:
            case NATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE:
            case SCIENTIFIC_CULTURE_PAPER:
                entry = createBibTeXEntry((JournalPaper) publication, locale);
                break;
            case INTERNATIONAL_CONFERENCE_PAPER:
            case INTERNATIONAL_ORAL_COMMUNICATION:
            case INTERNATIONAL_POSTER:
            case NATIONAL_CONFERENCE_PAPER:
            case NATIONAL_ORAL_COMMUNICATION:
            case NATIONAL_POSTER:
                entry = createBibTeXEntry((ConferencePaper) publication, locale);
                break;
            case INTERNATIONAL_BOOK:
            case NATIONAL_BOOK:
            case SCIENTIFIC_CULTURE_BOOK:
                entry = createBibTeXEntry((Book) publication, locale);
                break;
            case INTERNATIONAL_BOOK_CHAPTER:
            case NATIONAL_BOOK_CHAPTER:
            case SCIENTIFIC_CULTURE_BOOK_CHAPTER:
                entry = createBibTeXEntry((BookChapter) publication, locale);
                break;
            case HDR_THESIS:
            case PHD_THESIS:
            case MASTER_THESIS:
                entry = createBibTeXEntry((Thesis) publication, locale);
                break;
            case INTERNATIONAL_JOURNAL_EDITION:
            case NATIONAL_JOURNAL_EDITION:
                entry = createBibTeXEntry((JournalEdition) publication, locale);
                break;
            case INTERNATIONAL_KEYNOTE:
            case NATIONAL_KEYNOTE:
                entry = createBibTeXEntry((KeyNote) publication, locale);
                break;
            case TECHNICAL_REPORT:
            case PROJECT_REPORT:
            case RESEARCH_TRANSFERT_REPORT:
            case TEACHING_DOCUMENT:
            case TUTORIAL_DOCUMENTATION:
                entry = createBibTeXEntry((Report) publication, locale);
                break;
            case INTERNATIONAL_PATENT:
            case EUROPEAN_PATENT:
            case NATIONAL_PATENT:
                entry = createBibTeXEntry((Patent) publication, locale);
                break;
            case ARTISTIC_PRODUCTION:
            case RESEARCH_TOOL:
            case INTERNATIONAL_PRESENTATION:
            case NATIONAL_PRESENTATION:
            case INTERNATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
            case NATIONAL_SCIENTIFIC_CULTURE_PRESENTATION:
            case INTERNATIONAL_TV_OR_ONLINE_SHOW:
            case NATIONAL_TV_OR_ONLINE_SHOW:
            case INTERNATIONAL_NEWS_PAPER:
            case NATIONAL_NEWS_PAPER:
            case OTHER:
                entry = createBibTeXEntry((MiscDocument) publication, locale);
                break;
            default:
                throw new IllegalArgumentException("Unsupported publication type for export: " + publication.getType()); //$NON-NLS-1$
        }
        if (entry != null) {
            database.addObject(entry);
        }
    }

    private void addField(BibTeXEntry entry, Key key, String value) {
        addField(entry, key, value, false);
    }

    private void addField(BibTeXEntry entry, Key key, String value, boolean protectAcronyms) {
        if (!Strings.isNullOrEmpty(value)) {
            final var txt = toTeXString(value, protectAcronyms);
            entry.addField(key, new StringValue(txt, StringValue.Style.BRACED));
        }
    }

    private void addPageField(BibTeXEntry entry, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            var texValue = toTeXString(value);
            if (!Strings.isNullOrEmpty(texValue)) {
                // Usually the range of values is represented with "--" in LaTeX
                texValue = texValue.replaceAll("\\-+", "--"); //$NON-NLS-1$ //$NON-NLS-2$
                entry.addField(KEY_PAGES, new StringValue(texValue, StringValue.Style.BRACED));
            }
        }
    }

    private void fillBibTeXEntry(BibTeXEntry entry, Publication publication, Key authorKey, Locale locale) {
        addField(entry, KEY_TITLE, publication.getTitle(), true);

        final var authorNames = new StringBuilder();
        for (final var person : publication.getAuthors()) {
            if (authorNames.length() > 0) {
                authorNames.append(" and "); //$NON-NLS-1$
            }
            authorNames.append(person.getLastName());
            authorNames.append(", "); //$NON-NLS-1$
            authorNames.append(person.getFirstName());
        }
        addField(entry, authorKey, authorNames.toString());

        addField(entry, KEY_YEAR, publication.getPublicationYear());
        addMonthField(entry, publication.getPublicationDate());
        addField(entry, KEY_DOI, publication.getDOI());
        addField(entry, KEY_ISBN, publication.getISBN());
        addField(entry, KEY_ISSN, publication.getISSN());
        addField(entry, KEY_URL, publication.getExtraURL());
        addField(entry, KEY_DBLP, publication.getDblpURL());
        addField(entry, KEY_VIDEO, publication.getVideoURL());
        addField(entry, KEY_ABSTRACT, publication.getAbstractText());
        addField(entry, KEY_KEYWORD, publication.getKeywords());
        addField(entry, KEY_LANGUAGE, publication.getMajorLanguage().name());

        final var type = publication.getType();
        final var cat = publication.getCategory();
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final var loc = java.util.Locale.getDefault();
        try {
            if (type != null) {
                addField(entry, KEY_PUBLICATION_TYPE, type.name());
                addField(entry, KEY_PUBLICATION_TYPE_NAME, type.getLabel(getMessageSourceAccessor(), locale));
            }
            if (cat != null) {
                addField(entry, KEY_PUBLICATION_CATEGORY, cat.name());
                addField(entry, KEY_PUBLICATION_CATEGORY_NAME, cat.getLabel(getMessageSourceAccessor(), locale));
            }
        } finally {
            java.util.Locale.setDefault(loc);
        }
    }

    private void addNoteForJournal(BibTeXEntry entry, PublicationLanguage language,
                                   QuartileRanking scimago, QuartileRanking wos, float impactFactor, Locale locale) {
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(language.getLocale());
            final var note = new StringBuilder();
            final var scimagoNorm = scimago == null || scimago == QuartileRanking.NR ? null : scimago;
            final var wosNorm = wos == null || wos == QuartileRanking.NR ? null : wos;
            if (scimagoNorm != null && wosNorm != null) {
                if (scimagoNorm != wosNorm) {
                    note.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "SCIMAGO_WOS_QUARTILES", //$NON-NLS-1$
                            new Object[]{scimagoNorm.toString(), wosNorm.toString()}, locale));
                } else {
                    note.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "QUARTILES", //$NON-NLS-1$
                            new Object[]{scimagoNorm.toString()}, locale));
                }
            } else if (scimagoNorm != null) {
                note.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "SCIMAGO_QUARTILE", //$NON-NLS-1$
                        new Object[]{scimagoNorm.toString()}, locale));
            } else if (wosNorm != null) {
                note.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "WOS_QUARTILE", //$NON-NLS-1$
                        new Object[]{wosNorm.toString()}, locale));
            }
            if (impactFactor > 0f) {
                if (note.length() > 0) {
                    note.append(", "); //$NON-NLS-1$
                }
                note.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "IMPACT_FACTOR", //$NON-NLS-1$
                        new Object[]{Float.valueOf(impactFactor)}, locale));
            }
            addField(entry, KEY_NOTE, note.toString());
        } finally {
            java.util.Locale.setDefault(loc);
        }
    }

    private void addNoteForConference(BibTeXEntry entry, PublicationLanguage language, CoreRanking core, Locale locale) {
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(language.getLocale());
            final var coreNorm = core == null || core == CoreRanking.NR ? null : core;
            if (coreNorm != null) {
                addField(entry, KEY_NOTE, getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "CORE_RANKING", //$NON-NLS-1$
                        new Object[]{coreNorm.toString()}, locale));
            }
        } finally {
            java.util.Locale.setDefault(loc);
        }
    }

    /**
     * Create a JBibTeX entry for a journal paper.
     *
     * @param paper  the journal paper to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(JournalPaper paper, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_ARTICLE, createBibTeXId(paper));
        fillBibTeXEntry(entry, paper, KEY_AUTHOR, locale);
        final var journal = paper.getJournal();
        if (journal != null) {
            addField(entry, KEY_JOURNAL, journal.getJournalName());
            addField(entry, KEY_ISBN, journal.getISBN());
            addField(entry, KEY_ISSN, journal.getISSN());
            addField(entry, KEY_PUBLISHER, journal.getPublisher());
            addField(entry, KEY_ADDRESS, journal.getAddress());
        }
        addField(entry, KEY_VOLUME, paper.getVolume());
        addField(entry, KEY_NUMBER, paper.getNumber());
        addPageField(entry, paper.getPages());
        addField(entry, KEY_SERIES, paper.getSeries());
        if (paper.getScimagoQIndex() != null && paper.getScimagoQIndex() != QuartileRanking.NR) {
            addField(entry, KEY_SCIMAGO_QINDEX, paper.getScimagoQIndex());
        }
        if (paper.getWosQIndex() != null && paper.getWosQIndex() != QuartileRanking.NR) {
            addField(entry, KEY_WOS_QINDEX, paper.getWosQIndex());
        }
        if (paper.getImpactFactor() > 0f) {
            addField(entry, KEY_IMPACT_FACTOR, getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "RAW_IMPACT_FACTOR", //$NON-NLS-1$
                    new Object[]{Float.valueOf(paper.getImpactFactor())}, locale));
        }
        addNoteForJournal(entry, paper.getMajorLanguage(), paper.getScimagoQIndex(),
                paper.getWosQIndex(), paper.getImpactFactor(), locale);
        return entry;
    }

    /**
     * Create a JBibTeX entry for a conference paper.
     *
     * @param paper  the conference paper to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(ConferencePaper paper, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_INPROCEEDINGS, createBibTeXId(paper));
        fillBibTeXEntry(entry, paper, KEY_AUTHOR, locale);
        addField(entry, KEY_BOOKTITLE, paper.getPublicationTarget());
        addField(entry, KEY_VOLUME, paper.getVolume());
        addField(entry, KEY_NUMBER, paper.getNumber());
        addPageField(entry, paper.getPages());
        addField(entry, KEY_SERIES, paper.getSeries());
        addField(entry, KEY_EDITOR, paper.getEditors());
        addField(entry, KEY_ORGANIZATION, paper.getOrganization());
        addField(entry, KEY_ADDRESS, paper.getAddress());
        if (paper.getCoreRanking() != null && paper.getCoreRanking() != CoreRanking.NR) {
            addField(entry, KEY_CORE_RANKING, paper.getCoreRanking());
        }
        if (paper.getConference() != null) {
            addField(entry, KEY_ISBN, paper.getConference().getISBN());
            addField(entry, KEY_ISSN, paper.getConference().getISSN());
            addField(entry, KEY_PUBLISHER, paper.getConference().getPublisher());
        }
        addNoteForConference(entry, paper.getMajorLanguage(), paper.getCoreRanking(), locale);
        return entry;
    }

    /**
     * Create a JBibTeX entry for a book.
     *
     * @param book   the book to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(Book book, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_BOOK, createBibTeXId(book));
        fillBibTeXEntry(entry, book, KEY_AUTHOR, locale);
        addField(entry, KEY_EDITION, book.getEdition());
        addField(entry, KEY_SERIES, book.getSeries());
        addField(entry, KEY_VOLUME, book.getVolume());
        addField(entry, KEY_NUMBER, book.getNumber());
        addPageField(entry, book.getPages());
        addField(entry, KEY_EDITOR, book.getEditors());
        addField(entry, KEY_PUBLISHER, book.getPublisher());
        addField(entry, KEY_ADDRESS, book.getAddress());
        return entry;
    }

    /**
     * Create a JBibTeX entry for a book chapter.
     *
     * @param chapter the book chapter to put into the database.
     * @param locale  the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(BookChapter chapter, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_INCOLLECTION, createBibTeXId(chapter));
        fillBibTeXEntry(entry, chapter, KEY_AUTHOR, locale);
        addField(entry, KEY_BOOKTITLE, chapter.getBookTitle());
        addField(entry, KEY_CHAPTER, chapter.getChapterNumber());
        addField(entry, KEY_EDITION, chapter.getEdition());
        addField(entry, KEY_SERIES, chapter.getSeries());
        addField(entry, KEY_VOLUME, chapter.getVolume());
        addField(entry, KEY_NUMBER, chapter.getNumber());
        addPageField(entry, chapter.getPages());
        addField(entry, KEY_EDITOR, chapter.getEditors());
        addField(entry, KEY_PUBLISHER, chapter.getPublisher());
        addField(entry, KEY_ADDRESS, chapter.getAddress());
        return entry;
    }

    /**
     * Create a JBibTeX entry for a thesis.
     *
     * @param thesis the thesis to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(Thesis thesis, Locale locale) {
        final var pubType = thesis.getType() == PublicationType.MASTER_THESIS ? TYPE_MASTERSTHESIS : TYPE_PHDTHESIS;
        final var entry = new BibTeXEntry(pubType, createBibTeXId(thesis));
        fillBibTeXEntry(entry, thesis, KEY_AUTHOR, locale);
        addField(entry, KEY_SCHOOL, thesis.getInstitution());
        addField(entry, KEY_ADDRESS, thesis.getAddress());
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(thesis.getMajorLanguage().getLocale());
            addField(entry, KEY_TYPE, thesis.getType().getLabel(getMessageSourceAccessor(), locale));
        } finally {
            java.util.Locale.setDefault(loc);
        }
        return entry;
    }

    /**
     * Create a JBibTeX entry for a journal edition.
     *
     * @param edition the journal edition to put into the database.
     * @param locale  the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(JournalEdition edition, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_BOOK, createBibTeXId(edition));
        fillBibTeXEntry(entry, edition, KEY_EDITOR, locale);
        final var journal = edition.getJournal();
        if (journal != null) {
            addField(entry, KEY_JOURNAL, journal.getJournalName());
            addField(entry, KEY_ISBN, journal.getISBN());
            addField(entry, KEY_ISSN, journal.getISSN());
            addField(entry, KEY_PUBLISHER, journal.getPublisher());
            addField(entry, KEY_ADDRESS, journal.getAddress());
        }
        addField(entry, KEY_VOLUME, edition.getVolume());
        addField(entry, KEY_NUMBER, edition.getNumber());
        addPageField(entry, edition.getPages());
        if (edition.getScimagoQIndex() != null && edition.getScimagoQIndex() != QuartileRanking.NR) {
            addField(entry, KEY_SCIMAGO_QINDEX, edition.getScimagoQIndex());
        }
        if (edition.getWosQIndex() != null && edition.getWosQIndex() != QuartileRanking.NR) {
            addField(entry, KEY_WOS_QINDEX, edition.getWosQIndex());
        }
        if (edition.getImpactFactor() > 0f) {
            addField(entry, KEY_IMPACT_FACTOR, getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "RAW_IMPACT_FACTOR", //$NON-NLS-1$
                    new Object[]{Float.valueOf(edition.getImpactFactor())}, locale));
        }
        addNoteForJournal(entry, edition.getMajorLanguage(), edition.getScimagoQIndex(),
                edition.getWosQIndex(), edition.getImpactFactor(), locale);
        return entry;
    }

    /**
     * Create a JBibTeX entry for a keynote.
     *
     * @param keynote the keynote to put into the database.
     * @param locale  the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(KeyNote keynote, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_INPROCEEDINGS, createBibTeXId(keynote));
        fillBibTeXEntry(entry, keynote, KEY_AUTHOR, locale);
        addField(entry, KEY_BOOKTITLE, keynote.getPublicationTarget());
        addField(entry, KEY_EDITOR, keynote.getEditors());
        addField(entry, KEY_ORGANIZATION, keynote.getOrganization());
        addField(entry, KEY_ADDRESS, keynote.getAddress());
        // Force the Java locale to get the text that is corresponding to the language of the paper
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(keynote.getMajorLanguage().getLocale());
            addField(entry, KEY_NOTE, keynote.getType().getLabel(getMessageSourceAccessor(), locale));
        } finally {
            java.util.Locale.setDefault(loc);
        }
        return entry;
    }

    /**
     * Create a JBibTeX entry for a report.
     *
     * @param report the report to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(Report report, Locale locale) {
        final BibTeXEntry entry;
        if (report.getType() == PublicationType.TEACHING_DOCUMENT
                || report.getType() == PublicationType.TUTORIAL_DOCUMENTATION) {
            entry = new BibTeXEntry(TYPE_MANUAL, createBibTeXId(report));
            fillBibTeXEntry(entry, report, KEY_AUTHOR, locale);
            addField(entry, KEY_EDITION, report.getReportNumber());
            addField(entry, KEY_ORGANIZATION, report.getInstitution());
            addField(entry, KEY_ADDRESS, report.getAddress());
            addField(entry, KEY_NOTE, report.getReportType());
        } else {
            entry = new BibTeXEntry(TYPE_TECHREPORT, createBibTeXId(report));
            fillBibTeXEntry(entry, report, KEY_AUTHOR, locale);
            addField(entry, KEY_NUMBER, report.getReportNumber());
            addField(entry, KEY_INSTITUTION, report.getInstitution());
            addField(entry, KEY_ADDRESS, report.getAddress());
            addField(entry, KEY_NOTE, report.getReportType());
        }
        return entry;
    }

    /**
     * Create a JBibTeX entry for a patent.
     *
     * @param patent the patent to put into the database.
     * @param locale the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(Patent patent, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_MISC, createBibTeXId(patent));
        fillBibTeXEntry(entry, patent, KEY_AUTHOR, locale);
        addField(entry, KEY_ADDRESS, patent.getAddress());
        addField(entry, KEY_NOTE, patent.getPatentType());
        // Force the Java locale to get the text that is corresponding to the language of the patent
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(patent.getMajorLanguage().getLocale());
            final var howPublished = new StringBuilder();
            if (!Strings.isNullOrEmpty(patent.getPatentNumber())
                    && !Strings.isNullOrEmpty(patent.getInstitution())) {
                howPublished.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "PATENT_NUMBER_INSTITUTION", //$NON-NLS-1$
                        new Object[]{patent.getPatentNumber(), patent.getInstitution()}, locale));
            } else if (!Strings.isNullOrEmpty(patent.getPatentNumber())) {
                howPublished.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "PATENT_NUMBER", //$NON-NLS-1$
                        new Object[]{patent.getPatentNumber()}, locale));
            } else if (!Strings.isNullOrEmpty(patent.getInstitution())) {
                howPublished.append(getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + "PATENT_INSTITUTION", //$NON-NLS-1$
                        new Object[]{patent.getInstitution()}, locale));
            }
            addField(entry, KEY_HOWPUBLISHED, howPublished.toString());
        } finally {
            java.util.Locale.setDefault(loc);
        }
        return entry;
    }

    /**
     * Create a JBibTeX entry for a misc document.
     *
     * @param document the misc document to put into the database.
     * @param locale   the locale to use.
     * @return the JBibTeX entry.
     */
    protected BibTeXEntry createBibTeXEntry(MiscDocument document, Locale locale) {
        final var entry = new BibTeXEntry(TYPE_MISC, createBibTeXId(document));
        fillBibTeXEntry(entry, document, KEY_AUTHOR, locale);
        addField(entry, KEY_HOWPUBLISHED, document.getHowPublished());
        addField(entry, KEY_TYPE, document.getDocumentType());
        addField(entry, KEY_NUMBER, document.getDocumentNumber());
        addField(entry, KEY_ORGANIZATION, document.getOrganization());
        addField(entry, KEY_PUBLISHER, document.getPublisher());
        addField(entry, KEY_ADDRESS, document.getAddress());
        return entry;
    }

}
