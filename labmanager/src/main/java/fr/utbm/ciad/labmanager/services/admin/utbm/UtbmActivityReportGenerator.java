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

package fr.utbm.ciad.labmanager.services.admin.utbm;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.publication.ConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.JournalBasedPublication;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import jakarta.transaction.Transactional;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * A generator of an Excel file that contains the annual activity report with UTBM standard.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class UtbmActivityReportGenerator extends AbstractComponent {

    private static final long serialVersionUID = -6568536563740182838L;

    private final PublicationService publicationService;

    private final ResearchOrganizationService organizationService;

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param publicationService  the publication service.
     * @param organizationService the service for accessing the organizations.
     */
    public UtbmActivityReportGenerator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired PublicationService publicationService,
            @Autowired ResearchOrganizationService organizationService) {
        super(messages, constants);
        this.publicationService = publicationService;
        this.organizationService = organizationService;
    }

    /**
     * Export annual indicators to the UTBM requirements.
     *
     * @param organizationId the identifier of the organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale for the messages to the user.
     * @param progression    the progression indicator.
     * @return the report content (Excel file).
     * @throws Exception if the Excel components cannot be generated.
     */
    @Transactional
    public DownloadableFileDescription exportUtbmAnnualReport(long organizationId, int year, Locale locale, Progression progression) throws Exception {
        final var organization = this.organizationService.getResearchOrganizationById(organizationId).orElseThrow();
        try (final var ods = new OdfSpreadsheetHelper()) {
            progression.setProperties(0, 0, 2, false, getMessage(locale, "UtbmActivityReportGenerator.publications")); //$NON-NLS-1$
            exportPublications(ods, organization, year, progression.subTask(1));
            progression.setComment(getMessage(locale, "UtbmActivityReportGenerator.bytes")); //$NON-NLS-1$
            return new DownloadableFileDescription(
                    ods.getFileExtension(),
                    ods.getMediaType(),
                    ods.toByteArray(true));
        } finally {
            progression.end();
        }
    }

    /**
     * Export the publications in a spreadsheet page that corresponds to the UTBM standard.
     *
     * @param document     the output document.
     * @param organization the organization for which the publications must be extracted.
     * @param year         the reference year that is used for filtering the publications.
     * @param progression  the progression indicator.
     * @throws Exception if the Excel components cannot be generated.
     */
    protected void exportPublications(OdfSpreadsheetHelper document, ResearchOrganization organization, int year, Progression progression) throws Exception {
        progression.setProperties(0, 0, 2, false);
        final var publications = this.publicationService.getPublicationsByOrganizationId(organization.getId(), true, false);
        progression.increment();
        final var subProgress = progression.subTask(1);
        subProgress.setProperties(0, 0, publications.size() + 1, false);
        //
        final var output = document.newTable(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.table.publications", Integer.valueOf(year))); //$NON-NLS-1$
        //
        final var header = output.getHeader();
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.paper_type")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.title")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.journal")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.conference")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_0")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_1")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_2")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_3")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_4")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_5")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_6")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_7")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_8")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_9")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_10")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_11")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_12")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_13")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.author_14")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.doi_hal")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.keywords")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.lab")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.rank")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "UtbmActivityReportGenerator.column.impact_factor")); //$NON-NLS-1$
        subProgress.increment();
        //
        final var content = output.getContent();
        for (final var publication : publications) {
            if (publication.getPublicationYear() == year) {
                final var row = content.appendRow();
                row.append(publication.getCategory().name());
                row.append(publication.getTitle());
                if (publication instanceof JournalBasedPublication paper) {
                    final var journal = paper.getJournal();
                    row.append(publication.getPublicationTarget() + ", " + journal.getPublisher()); //$NON-NLS-1$
                    row.append((String) null);
                } else if (publication instanceof ConferenceBasedPublication paper) {
                    row.append((String) null);
                    final var conference = paper.getConference();
                    if (conference != null) {
                        row.append(publication.getPublicationTarget() + ", " + conference.getPublisher()); //$NON-NLS-1$
                    } else {
                        row.append(publication.getWherePublishedShortDescription());
                    }
                } else {
                    row.append((String) null);
                    row.append(publication.getWherePublishedShortDescription());
                }
                final var authors = publication.getAuthors();
                for (int i = 0; i < 15; ++i) {
                    if (i < authors.size()) {
                        row.append(authors.get(i).getFullNameWithLastNameFirst());
                    } else {
                        row.append((String) null);
                    }
                }
                final var doi = publication.getDOI();
                if (Strings.isNullOrEmpty(doi)) {
                    row.append(publication.getHalId());
                } else {
                    row.append(doi);
                }
                row.append(publication.getKeywords());
                row.append(organization.getAcronymOrName());
                boolean hasRank = false;
                boolean hasImpactFactor = false;
                if (publication instanceof JournalBasedPublication paper) {
                    final var journal = paper.getJournal();
                    final var quartile = journal.getWosQIndexByYear(year);
                    if (quartile != null && quartile != QuartileRanking.NR) {
                        hasRank = true;
                        row.append(quartile.name());
                    }
                    final var impactFactor = journal.getImpactFactorByYear(year);
                    if (impactFactor > 0f) {
                        hasImpactFactor = true;
                        row.append(Double.valueOf(impactFactor));
                    }
                } else if (publication instanceof ConferenceBasedPublication paper) {
                    final var conference = paper.getConference();
                    if (conference != null) {
                        final var ranking = conference.getCoreIndexByYear(year);
                        if (ranking != null && ranking != CoreRanking.NR) {
                            hasRank = true;
                            row.append(ranking.toString());
                        }
                    }
                }
                if (!hasRank) {
                    row.append((String) null);
                }
                if (!hasImpactFactor) {
                    row.append((String) null);
                }
            }
            subProgress.increment();
        }
        subProgress.end();
        progression.end();
    }

}
