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

package fr.utbm.ciad.labmanager.utils.io.html;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.type.*;
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import fr.utbm.ciad.labmanager.utils.io.AbstractPublicationExporter;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.hal.HalTools;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.springframework.context.support.MessageSourceAccessor;

import java.text.DecimalFormat;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Utilities for exporting publications to HTML.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class AbstractHtmlExporter extends AbstractPublicationExporter implements HtmlExporter {

    private static final String MESSAGES_PREFIX = "abstractHtmlExporter."; //$NON-NLS-1$

    /**
     * Application constants.
     */
    protected final ConfigurationConstants constants;

    /**
     * Tools for managing DOI.
     */
    protected final DoiTools doiTools;

    /**
     * Tools for managing HAL identifiers.
     */
    protected final HalTools halTools;

    /**
     * Constructor.
     *
     * @param constants the accessor to the application constants.
     * @param messages  the accessor to the localized messages.
     * @param doiTools  the accessor to the DOI tools.
     * @param halTools  the tools for manipulating HAL identifiers.
     */
    public AbstractHtmlExporter(ConfigurationConstants constants, MessageSourceAccessor messages, DoiTools doiTools, HalTools halTools) {
        super(messages);
        this.constants = constants;
        this.doiTools = doiTools;
        this.halTools = halTools;
    }

    /**
     * Replies the string representation of left quotes.
     *
     * @return the left quotes.
     */
    public static String getLeftQuotes() {
        return "&ldquo;"; //$NON-NLS-1$
    }

    /**
     * Replies the string representation of right quotes.
     *
     * @return the left quotes.
     */
    public static String getRightQuotes() {
        return "&rdquo;"; //$NON-NLS-1$
    }

    /**
     * Replies the string representation of the given number if it is strictly positive.
     *
     * @param number the number.
     * @return the formatted number or {@code null} if the number is negative or nul.
     */
    protected static String formatNumberIfStrictlyPositive(float number) {
        if (number > 0f) {
            final var format = new DecimalFormat("#0.000"); //$NON-NLS-1$
            return format.format(number);
        }
        return null;
    }

    /**
     * Replies the HTML string representation of the given UTF8 string.
     *
     * @param utfString the UTF8 string.
     * @return the HTML string.
     */
    protected static String toHtml(CharSequence utfString) {
        if (utfString == null || utfString.length() == 0) {
            return Strings.nullToEmpty(null);
        }
        var len = utfString.length();
        final var buffer = new StringBuffer(len);
        // true if last char was blank
        var lastWasBlankChar = false;
        for (var i = 0; i < len; i++) {
            final var c = utfString.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    buffer.append("&nbsp;"); //$NON-NLS-1$
                } else {
                    lastWasBlankChar = true;
                    buffer.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                // HTML Special Chars
                if (c == '"')
                    buffer.append("&quot;"); //$NON-NLS-1$
                else if (c == '&')
                    buffer.append("&amp;"); //$NON-NLS-1$
                else if (c == '<')
                    buffer.append("&lt;"); //$NON-NLS-1$
                else if (c == '>')
                    buffer.append("&gt;"); //$NON-NLS-1$
                else if (c == '\n')
                    // Handle Newline
                    buffer.append("<br/>"); //$NON-NLS-1$
                else {
                    int ci = 0xffff & c;
                    if (ci < 160)
                        // nothing special only 7 Bit
                        buffer.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        buffer.append("&#"); //$NON-NLS-1$
                        buffer.append(ci);
                        buffer.append(';');
                    }
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Append the given values, with the given separator to the receiver.
     *
     * @param receiver  the receiver.
     * @param separator the separator of values.
     * @param values    the values to add.
     * @return {@code true} if a value was added.
     */
    protected static boolean append(StringBuilder receiver, String separator, String... values) {
        assert receiver != null;
        assert separator != null;
        assert values != null;
        var first = true;
        var added = false;
        for (final var value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                if (first) {
                    first = false;
                } else {
                    receiver.append(separator);
                }
                receiver.append(value);
                added = true;
            }
        }
        return added;
    }

    /**
     * Append the given value if it is not empty.
     *
     * @param receiver the receiver.
     * @param values   the values to add.
     * @return {@code true} if a value was added.
     */
    protected static boolean append(StringBuilder receiver, String value) {
        return append(receiver, "", value); //$NON-NLS-1$
    }

    /**
     * Replies the value preceded by its decoration if the value is not {@code null} or empty.
     *
     * @param value     the value to decorate.
     * @param decorator the text to append before the value.
     * @return the decorated value.
     */
    protected static String decorateBefore(String value, String decorator) {
        assert decorator != null;
        if (!Strings.isNullOrEmpty(value)) {
            return decorator + value;
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public String separator() {
        return "&nbsp;"; //$NON-NLS-1$
    }

    @Override
    public String doubleSeparator() {
        return separator() + separator();
    }

    /**
     * Replies the name of the person with a format compliant with the HTML output.
     *
     * @param person       the person.
     * @param year         the year of the publication associated to the author.
     * @param configurator the exporter configurator.
     * @return the formatted person name.
     */
    @SuppressWarnings("static-method")
    protected String formatAuthorName(Person person, int year, ExporterConfigurator configurator) {
        assert configurator != null;
        final var status = configurator.getExportedAuthorStatusFor(person, year);
        final var innerName = new StringBuilder();
        innerName.append(toHtml(person.getFirstName()));
        innerName.append(" "); //$NON-NLS-1$
        innerName.append(toHtml(person.getLastName().toUpperCase()));

        final var formattedName = new StringBuilder();
        switch (status) {
            case SELECTED_PERSON:
                if (configurator.isSelectedPersonNameFormatted()) {
                    formattedName.append("<b><u>"); //$NON-NLS-1$
                    formattedName.append(innerName);
                    formattedName.append("</u></b>"); //$NON-NLS-1$
                } else {
                    formattedName.append(innerName);
                }
                break;
            case RESEARCHER:
                if (configurator.isResearcherNameFormatted()) {
                    formattedName.append("<b>"); //$NON-NLS-1$
                    formattedName.append(innerName);
                    formattedName.append("</b>"); //$NON-NLS-1$
                } else {
                    formattedName.append(innerName);
                }
                break;
            case PHD_STUDENT:
                if (configurator.isPhDStudentNameFormatted()) {
                    formattedName.append("<u>"); //$NON-NLS-1$
                    formattedName.append(innerName);
                    formattedName.append("</u>"); //$NON-NLS-1$
                } else {
                    formattedName.append(innerName);
                }
                break;
            case POSTDOC_ENGINEER:
                if (configurator.isPostdocEngineerNameFormatted()) {
                    formattedName.append("<i>"); //$NON-NLS-1$
                    formattedName.append(innerName);
                    formattedName.append("</i>"); //$NON-NLS-1$
                } else {
                    formattedName.append(innerName);
                }
                break;
            case OTHER:
            default:
                formattedName.append(innerName);
                break;
        }
        return formattedName.toString();
    }

    /**
     * Replies the title with a format compliant with the HTML output.
     *
     * @param title        the title.
     * @param configurator the configurator of the exporter.
     * @return the formatted title.
     */
    protected abstract String formatTitle(String title, ExporterConfigurator configurator);

    /**
     * Build the DOI link if the DOI is not empty.
     *
     * @param doi the DOI identifier.
     * @return the link or {@code null}.
     */
    protected String buildDoiLink(String doi) {
        if (!Strings.isNullOrEmpty(doi)) {
            return "<a href=\"" + this.doiTools.getDOIUrlFromDOINumber(doi).toExternalForm() + "\">" //$NON-NLS-1$ //$NON-NLS-2$
                    + toHtml(doi) + "</a>"; //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Append the given ranks to the receiver.
     *
     * @param receiver     the receiver of the HTML.
     * @param scimago      the Scimago ranking.
     * @param wos          the Web-of-Science ranking.
     * @param impactFactor the journal's impact factor.
     * @param locale       the locale to use.
     * @return {@code true} if the receiver has changed.
     */
    protected boolean appendRanks(StringBuilder receiver, QuartileRanking scimago, QuartileRanking wos, float impactFactor, Locale locale) {
        final var impactFactorStr = formatNumberIfStrictlyPositive(impactFactor);
        final var scimagoNorm = scimago == null || scimago == QuartileRanking.NR ? null : scimago;
        final var wosNorm = wos == null || wos == QuartileRanking.NR ? null : wos;
        String rank = null;
        if (scimagoNorm != null && wosNorm != null) {
            if (wosNorm != scimagoNorm) {
                final var scimagoStr = toHtml(scimagoNorm.toString());
                final var wosStr = toHtml(wosNorm.toString());
                if (append(receiver, ", ", //$NON-NLS-1$
                        decorateBefore(scimagoStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "SCIMAGO_PREFIX", locale)), //$NON-NLS-1$
                        decorateBefore(wosStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "WOS_PREFIX", locale)), //$NON-NLS-1$
                        decorateBefore(impactFactorStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "IMPACTFACTOR_PREFIX", locale)))) { //$NON-NLS-1$
                    receiver.append(". "); //$NON-NLS-1$
                    return true;
                }
            } else {
                rank = toHtml(scimagoNorm.toString());
            }
        } else if (scimagoNorm != null) {
            rank = toHtml(scimagoNorm.toString());
        } else if (wosNorm != null) {
            rank = toHtml(wosNorm.toString());
        }
        if (append(receiver, ", ", //$NON-NLS-1$
                decorateBefore(rank, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "JOURNALRANK_PREFIX", locale)), //$NON-NLS-1$
                decorateBefore(impactFactorStr, getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "IMPACTFACTOR_PREFIX", locale)))) { //$NON-NLS-1$
            receiver.append(". "); //$NON-NLS-1$
            return true;
        }
        return false;
    }

    /**
     * Append the given ranks to the receiver.
     *
     * @param receiver the receiver of the HTML.
     * @param core     the CORE ranking.
     * @param locale   the locale to use.
     * @return {@code true} if the receiver has changed.
     */
    protected boolean appendRanks(StringBuilder receiver, CoreRanking core, Locale locale) {
        final var coreNorm = core == null || core == CoreRanking.NR ? null : core;
        if (coreNorm != null && append(receiver,
                decorateBefore(toHtml(coreNorm.toString()), getMessageSourceAccessor().getMessage(MESSAGES_PREFIX + "CORE_PREFIX", locale)))) { //$NON-NLS-1$
            receiver.append(". "); //$NON-NLS-1$
            return true;
        }
        return false;
    }

    /**
     * Export in HTML the description of a single book.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, Book publication, Locale locale);

    /**
     * Export in HTML the description of a single book chapter.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, BookChapter publication, Locale locale);

    /**
     * Export in HTML the description of a single conference paper.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, ConferencePaper publication, Locale locale);

    /**
     * Export in HTML the description of a single journal paper.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, JournalPaper publication, Locale locale);

    /**
     * Export in HTML the description of a single journal edition.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, JournalEdition publication, Locale locale);

    /**
     * Export in HTML the description of a single key-note.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, KeyNote publication, Locale locale);

    /**
     * Export in HTML the description of a single report.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, Report publication, Locale locale);

    /**
     * Export in HTML the description of a single thesis.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, Thesis publication, Locale locale);

    /**
     * Export in HTML the description of a single patent.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, Patent publication, Locale locale);

    /**
     * Export in HTML the description of a single document.
     *
     * @param html        the receiver of the HTML.
     * @param publication the publication, never {@code null}.
     * @param locale      the locale to use for exporting.
     */
    protected abstract void exportDescription(StringBuilder html, MiscDocument publication, Locale locale);

    /**
     * Export in HTML the description of a single publication.
     *
     * @param html         the receiver of the HTML.
     * @param publication  the publication, never {@code null}.
     * @param configurator the configurator of the exporter.
     */
    protected void exportDescription(StringBuilder html, Publication publication, ExporterConfigurator configurator) {
        html.append(formatTitle(publication.getTitle(), configurator));

        final var publicationClass = publication.getType().getInstanceType();
        assert publicationClass != null;

        final var curentLocale = configurator.getLocaleOrLanguageLocale(publication.getMajorLanguage());

        if (publicationClass.equals(Book.class)) {
            exportDescription(html, (Book) publication, curentLocale);
        } else if (publicationClass.equals(BookChapter.class)) {
            exportDescription(html, (BookChapter) publication, curentLocale);
        } else if (publicationClass.equals(ConferencePaper.class)) {
            exportDescription(html, (ConferencePaper) publication, curentLocale);
        } else if (publicationClass.equals(JournalEdition.class)) {
            exportDescription(html, (JournalEdition) publication, curentLocale);
        } else if (publicationClass.equals(JournalPaper.class)) {
            exportDescription(html, (JournalPaper) publication, curentLocale);
        } else if (publicationClass.equals(KeyNote.class)) {
            exportDescription(html, (KeyNote) publication, curentLocale);
        } else if (publicationClass.equals(Report.class)) {
            exportDescription(html, (Report) publication, curentLocale);
        } else if (publicationClass.equals(Thesis.class)) {
            exportDescription(html, (Thesis) publication, curentLocale);
        } else if (publicationClass.equals(Patent.class)) {
            exportDescription(html, (Patent) publication, curentLocale);
        } else if (publicationClass.equals(MiscDocument.class)) {
            exportDescription(html, (MiscDocument) publication, curentLocale);
        } else {
            throw new IllegalArgumentException("Unsupported publication type: " + publication.getType()); //$NON-NLS-1$
        }

        final var date = publication.getPublicationDate();
        if (date != null) {
            final var month = date.getMonth();
            assert month != null;
            final var displayName = month.getDisplayName(TextStyle.FULL_STANDALONE, java.util.Locale.getDefault());
            html.append(toHtml(displayName));
            html.append(", "); //$NON-NLS-1$
        }
        final int year = publication.getPublicationYear();
        html.append(year);
        html.append("."); //$NON-NLS-1$
    }

}
