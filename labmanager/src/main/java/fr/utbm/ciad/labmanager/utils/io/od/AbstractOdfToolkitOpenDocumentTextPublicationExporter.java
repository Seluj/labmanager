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

package fr.utbm.ciad.labmanager.utils.io.od;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.type.*;
import fr.utbm.ciad.labmanager.utils.io.AbstractPublicationExporter;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.Progression;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.ByteArrayOutputStream;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Locale;

/**
 * Exporter of publications to Open Document Text based on the ODF toolkit.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://odftoolkit.org"
 * @since 2.0.0
 */
public abstract class AbstractOdfToolkitOpenDocumentTextPublicationExporter extends AbstractPublicationExporter implements OpenDocumentTextPublicationExporter {

    private static final String MESSAGES_PREFIX = "abstractOdfToolkitOpenDocumentTextExporter."; //$NON-NLS-1$

    /**
     * Text helper for building text elements in ODT format.
     */
    protected final OdfTextDocumentHelper textHelper;

    /**
     * Constructor.
     *
     * @param messages   the accessor to the localized message.
     * @param textHelper the helper for building the text elements.
     */
    public AbstractOdfToolkitOpenDocumentTextPublicationExporter(MessageSourceAccessor messages, OdfTextDocumentHelper textHelper) {
        super(messages);
        this.textHelper = textHelper;
    }

    /**
     * Replies the string representation of left quotes.
     *
     * @param locale the locale to be used.
     * @return the left quotes.
     */
    public String getLeftQuotes(Locale locale) {
        return getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "LEFT_QUOTES", locale); //$NON-NLS-1$
    }

    /**
     * Replies the string representation of right quotes.
     *
     * @param locale the locale to be used.
     * @return the left quotes.
     */
    public String getRightQuotes(Locale locale) {
        return getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "RIGHT_QUOTES", locale); //$NON-NLS-1$
    }

    @SuppressWarnings("resource")
    @Override
    public byte[] exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                     Progression progression, Logger logger) throws Exception {
        if (publications == null) {
            progression.end();
            return null;
        }
        progression.setProperties(0, 0, publications.size() * 2, false);
        final var odt = OdfTextDocument.newTextDocument();
        exportPublicationsWithGroupingCriteria(publications, configurator, progression.subTask(publications.size()),
                it -> {
                    try {
                        final var element = odt.getContentRoot().newTextHElement(1);
                        element.setTextContent(it);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                },
                it -> {
                    try {
                        final var element = odt.getContentRoot().newTextHElement(2);
                        element.setTextContent(it);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                },
                (it, progress) -> {
                    try {
                        exportFlatList(odt.getContentRoot().newTextListElement(), it, configurator, progress);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
        final byte[] content;
        try (final var output = new ByteArrayOutputStream()) {
            odt.save(output);
            output.flush();
            content = output.toByteArray();
        }
        progression.end();
        return content;
    }

    /**
     * Export the publications in a flat list.
     *
     * @param list         the ODT list in which the publications must be exported.
     * @param publications the publications to export.
     * @param configurator the exporter configurator.
     * @param progression  the progression indicator.
     */
    protected void exportFlatList(TextListElement list, Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                  Progression progression) {
        progression.setProperties(0, 0, publications.size(), false);
        for (final var publication : publications) {
            exportPublication(list, publication, configurator);
            progression.increment();
        }
        progression.end();
    }

    /**
     * Export in ODT a single publication.
     *
     * @param odt          the receiver of the ODT content.
     * @param publication  the publication, never {@code null}.
     * @param configurator the configurator for the exporter.
     */
    public void exportPublication(TextListElement odt, Publication publication, ExporterConfigurator configurator) {
        assert odt != null;
        assert publication != null;
        final var item = odt.newTextListItemElement();
        final var odtText = item.newTextPElement();
        final var loc = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(publication.getMajorLanguage().getLocale());
            exportAuthors(odtText, publication, configurator);
            exportDescription(odtText, publication, configurator);
        } finally {
            java.util.Locale.setDefault(loc);
        }
    }

    /**
     * Replies the name of the person with a format compliant with the HTML output.
     *
     * @param odtText      the receiver of the formatted name.
     * @param person       the person.
     * @param year         the year of the publication associated to the author.
     * @param configurator the configurator for the exporter.
     */
    protected void formatAuthorName(TextPElement odtText, Person person, int year, ExporterConfigurator configurator) {
        assert configurator != null;
        final var status = configurator.getExportedAuthorStatusFor(person, year);
        final var innerName = new StringBuilder();
        innerName.append(person.getFirstName());
        innerName.append(" "); //$NON-NLS-1$
        innerName.append(person.getLastName().toUpperCase());

        TextSpanElement span;
        switch (status) {
            case SELECTED_PERSON:
                span = this.textHelper.newTextBoldUnderline(odtText);
                span.newTextNode(innerName.toString());
                break;
            case RESEARCHER:
                span = this.textHelper.newTextBold(odtText);
                span.newTextNode(innerName.toString());
                break;
            case PHD_STUDENT:
                span = this.textHelper.newTextUnderline(odtText);
                span.newTextNode(innerName.toString());
                break;
            case POSTDOC_ENGINEER:
                span = this.textHelper.newTextItalic(odtText);
                span.newTextNode(innerName.toString());
                break;
            case OTHER:
            default:
                odtText.newTextNode(innerName.toString());
                break;
        }
    }

    /**
     * Append the DOI link if the DOI is not empty.
     *
     * @param receiver the receiver of the ODT content.
     * @param doi      the DOI identifier.
     * @param prefix   the prefix to append to the DOI link.
     * @return {@code true} if the receiver is changed.
     */
    @SuppressWarnings("static-method")
    protected boolean appendDoiLink(TextPElement receiver, String doi, String prefix) {
        if (!Strings.isNullOrEmpty(doi)) {
            receiver.newTextNode(prefix);
            final var aElement = receiver.newTextAElement("https://doi.org/" + doi, null); //$NON-NLS-1$
            aElement.newTextNode(doi);
            return true;
        }
        return false;
    }

    /**
     * Replies the title with a format compliant with the HTML output.
     *
     * @param odtText      the receiver of the ODT content.
     * @param title        the title.
     * @param configurator the configurator of the exporter.
     */
    protected abstract void formatTitle(TextPElement odtText, String title, ExporterConfigurator configurator);

    /**
     * Append the given ranks to the receiver.
     *
     * @param receiver     the receiver of the HTML.
     * @param scimago      the Scimago ranking.
     * @param wos          the Web-of-Science ranking.
     * @param impactFactor the journal's impact factor.
     * @param locale       the locale to be used.
     * @return {@code true} if the receiver has changed.
     */
    protected boolean appendRanks(TextPElement receiver, QuartileRanking scimago, QuartileRanking wos, float impactFactor, Locale locale) {
        final var impactFactorStr = this.textHelper.formatNumberIfStrictlyPositive(impactFactor);
        final var scimagoNorm = scimago == null || scimago == QuartileRanking.NR ? null : scimago;
        final var wosNorm = wos == null || wos == QuartileRanking.NR ? null : wos;
        String rank = null;
        if (scimagoNorm != null && wosNorm != null) {
            if (wosNorm != scimagoNorm) {
                final var scimagoStr = scimagoNorm.toString();
                final var wosStr = wosNorm.toString();
                if (this.textHelper.append(receiver, ", ", //$NON-NLS-1$
                        this.textHelper.decorateBefore(scimagoStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "SCIMAGO_PREFIX", locale)), //$NON-NLS-1$
                        this.textHelper.decorateBefore(wosStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "WOS_PREFIX", locale)), //$NON-NLS-1$
                        this.textHelper.decorateBefore(impactFactorStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "IMPACTFACTOR_PREFIX", locale)))) { //$NON-NLS-1$
                    receiver.newTextNode(". "); //$NON-NLS-1$
                    return true;
                }
            } else {
                rank = scimagoNorm.toString();
            }
        } else if (scimagoNorm != null) {
            rank = scimagoNorm.toString();
        } else if (wosNorm != null) {
            rank = wosNorm.toString();
        }
        if (this.textHelper.append(receiver, ", ", //$NON-NLS-1$
                this.textHelper.decorateBefore(rank, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "JOURNALRANK_PREFIX", locale)), //$NON-NLS-1$
                this.textHelper.decorateBefore(impactFactorStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "IMPACTFACTOR_PREFIX", locale)))) { //$NON-NLS-1$
            receiver.newTextNode(". "); //$NON-NLS-1$
            return true;
        }
        return false;
    }

    /**
     * Append the given ranks to the receiver.
     *
     * @param receiver the receiver of the HTML.
     * @param core     the CORE ranking.
     * @param locale   the locale to be used.
     * @return {@code true} if the receiver has changed.
     */
    protected boolean appendRanks(TextPElement receiver, CoreRanking core, Locale locale) {
        final var coreNorm = core == null || core == CoreRanking.NR ? null : core;
        String rank = null;
        if (coreNorm != null) {
            rank = coreNorm.toString();
        }
        if (this.textHelper.append(receiver,
                this.textHelper.decorateBefore(rank, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "CORE_PREFIX", locale)))) { //$NON-NLS-1$
            receiver.newTextNode(". "); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * Export in ODT the description of a single book.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, Book publication, Locale locale);

    /**
     * Export in ODT the description of a single book chapter.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, BookChapter publication, Locale locale);

    /**
     * Export in ODT the description of a single conference paper.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, ConferencePaper publication, Locale locale);

    /**
     * Export in ODT the description of a single journal paper.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, JournalPaper publication, Locale locale);

    /**
     * Export in ODT the description of a single journal edition.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, JournalEdition publication, Locale locale);

    /**
     * Export in ODT the description of a single key-note.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, KeyNote publication, Locale locale);

    /**
     * Export in ODT the description of a single report.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, Report publication, Locale locale);

    /**
     * Export in ODT the description of a single thesis.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, Thesis publication, Locale locale);

    /**
     * Export in ODT the description of a single patent.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, Patent publication, Locale locale);

    /**
     * Export in ODT the description of a single document.
     *
     * @param odt         the receiver of the ODT content.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(TextPElement odt, MiscDocument publication, Locale locale);


    /**
     * Export in ODT the description of a single publication.
     *
     * @param odtText      the receiver of the ODT content.
     * @param publication  the publication, never {@code null}.
     * @param configurator the configurator of the exporter.
     */
    protected void exportDescription(TextPElement odtText, Publication publication, ExporterConfigurator configurator) {
        formatTitle(odtText, publication.getTitle(), configurator);

        final var publicationClass = publication.getType().getInstanceType();
        assert publicationClass != null;

        final var currentLocale = configurator.getLocaleOrLanguageLocale(publication.getMajorLanguage());

        if (publicationClass.equals(Book.class)) {
            exportDescription(odtText, (Book) publication, currentLocale);
        } else if (publicationClass.equals(BookChapter.class)) {
            exportDescription(odtText, (BookChapter) publication, currentLocale);
        } else if (publicationClass.equals(ConferencePaper.class)) {
            exportDescription(odtText, (ConferencePaper) publication, currentLocale);
        } else if (publicationClass.equals(JournalEdition.class)) {
            exportDescription(odtText, (JournalEdition) publication, currentLocale);
        } else if (publicationClass.equals(JournalPaper.class)) {
            exportDescription(odtText, (JournalPaper) publication, currentLocale);
        } else if (publicationClass.equals(KeyNote.class)) {
            exportDescription(odtText, (KeyNote) publication, currentLocale);
        } else if (publicationClass.equals(Report.class)) {
            exportDescription(odtText, (Report) publication, currentLocale);
        } else if (publicationClass.equals(Thesis.class)) {
            exportDescription(odtText, (Thesis) publication, currentLocale);
        } else if (publicationClass.equals(Patent.class)) {
            exportDescription(odtText, (Patent) publication, currentLocale);
        } else if (publicationClass.equals(MiscDocument.class)) {
            exportDescription(odtText, (MiscDocument) publication, currentLocale);
        } else {
            throw new IllegalArgumentException("Unsupported publication type: " + publication.getType()); //$NON-NLS-1$
        }

        final var date = publication.getPublicationDate();
        if (date != null) {
            final var month = date.getMonth();
            assert month != null;
            final var displayName = month.getDisplayName(TextStyle.FULL_STANDALONE, java.util.Locale.getDefault());
            odtText.newTextNode(displayName);
            odtText.newTextNode(", "); //$NON-NLS-1$
        }
        final var year = publication.getPublicationYear();
        odtText.newTextNode(Integer.toString(year));
        odtText.newTextNode(". "); //$NON-NLS-1$
    }


    /**
     * Export in ODT the authors of a single publication.
     *
     * @param odtText      the receiver of the ODT content.
     * @param publication  the publication, never {@code null}.
     * @param configurator the configurator for the exporter.
     */
    protected void exportAuthors(TextPElement odtText, Publication publication, ExporterConfigurator configurator) {
        assert configurator != null;
        final var year = publication.getPublicationYear();
        var first = true;
        for (final var person : publication.getAuthors()) {
            if (first) {
                first = false;
            } else {
                odtText.newTextNode(", "); //$NON-NLS-1$
            }
            formatAuthorName(odtText, person, year, configurator);
        }
        odtText.newTextNode(". "); //$NON-NLS-1$
    }

}
