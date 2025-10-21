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

package fr.utbm.ciad.labmanager.services.admin.spim;

import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableContentHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableContentRowHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableHeaderHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableHelper;
import jakarta.transaction.Transactional;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

/**
 * A generator of an Excel file that contains the annual activity report with SPIM standard.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class SpimActivityReportGenerator extends AbstractComponent {

    private static final long serialVersionUID = -4610282976415823204L;

    private final ResearchOrganizationService organizationService;

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param organizationService the service for accessing to research organizations.
     */
    public SpimActivityReportGenerator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired ResearchOrganizationService organizationService) {
        super(messages, constants);
        this.organizationService = organizationService;
    }

    private static boolean isAfter(Membership a, Membership b) {
        final var dt0 = a.getMemberSinceWhen();
        final var dt1 = b.getMemberToWhen();
        return dt0 != null && dt1 != null && !dt1.isAfter(dt0);
    }

    /**
     * Export annual indicators to the SPIM requirements.
     *
     * @param organizationId the identifier of the organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale for the messages to the user.
     * @param progression    the progression indicator.
     * @return the report content (Excel file).
     * @throws Exception if the Excel components cannot be generated.
     */
    @Transactional
    public DownloadableFileDescription exportSpimAnnualReport(long organizationId, int year, Locale locale, Progression progression) throws Exception {
        final var organization = this.organizationService.getResearchOrganizationById(organizationId).orElseThrow();
        progression.setProperties(0, 0, 2, false);
        try (final OdfSpreadsheetHelper ods = new OdfSpreadsheetHelper()) {
            exportHumanResources(ods, organization, year, locale, progression.subTask(1));
            progression.setComment(getMessage(locale, "SpimActivityReportGenerator.bytes")); //$NON-NLS-1$
            return new DownloadableFileDescription(
                    ods.getFileExtension(),
                    ods.getMediaType(),
                    ods.toByteArray(true));
        } finally {
            progression.end();
        }
    }

    /**
     * Export the humn resources in a spreadsheet page that corresponds to the SPIM standard.
     *
     * @param document     the output document.
     * @param organization the organization for which the publications must be extracted.
     * @param year         the reference year that is used for filtering the publications.
     * @param locale       the locale for the messages to the user.
     * @param progression  the progression indicator.
     * @throws Exception if the Excel cannot be generated.
     */
    protected void exportHumanResources(OdfSpreadsheetHelper document, ResearchOrganization organization, int year,
                                        Locale locale, Progression progression) throws Exception {
        progression.setProperties(0, 0, 5, false, getMessage(locale, "SpimActivityReportGenerator.human_resources")); //$NON-NLS-1$
        //
        Set<Membership> memberships = organization.getDirectOrganizationMemberships();
        progression.increment();
        //
        final TableHelper output = document.newTable(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.table.rh", Integer.valueOf(year))); //$NON-NLS-1$
        progression.increment();
        //
        final TableHeaderHelper header = output.getHeader();
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.name")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.firstname")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.position")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.hdr")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.university")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "SpimActivityReportGenerator.column.emeritus")); //$NON-NLS-1$
        progression.increment();
        //
        final LocalDate startDate = LocalDate.of(year, 1, 1);
        final LocalDate endDate = LocalDate.of(year, 12, 31);
        final var subProgress0 = progression.subTask(1);
        subProgress0.setProperties(0, 0, memberships.size(), false);
        final var selectedMemberships = new TreeMap<String, Membership>();
        for (final var membership : memberships) {
            if (membership.isActiveIn(startDate, endDate) && !membership.getMemberStatus().isExternalPosition()
                    && membership.isPermanentPosition() && (membership.getMemberStatus().isResearcher() || membership.getMemberStatus().isTechnicalStaff())) {
                final var key = membership.getPerson().getFullNameWithLastNameFirst();
                final var existing = selectedMemberships.get(key);
                if (existing == null || isAfter(membership, existing)) {
                    selectedMemberships.put(key, membership);
                }
            }
            subProgress0.increment();
        }
        subProgress0.end();
        //
        final var subProgress1 = progression.subTask(1);
        subProgress1.setProperties(0, 0, selectedMemberships.size(), false);
        final TableContentHelper content = output.getContent();
        for (final var membership : selectedMemberships.values()) {
            final TableContentRowHelper row = content.appendRow();
            final var person = membership.getPerson();
            final var status = membership.getMemberStatus();
            row.append(person.getLastName());
            row.append(person.getFirstName());
            row.append(status.getFrenchAcronym(getMessageSourceAccessor()));
            row.append(Boolean.valueOf(status.isHdrOwner()));
            final var employer = membership.getEmployer();
            row.append(employer.isPresent() ? employer.get().getAcronymOrName() : null);
            row.append(Boolean.valueOf(membership.getMemberStatus().isEmeritus()));
        }
        subProgress1.end();
        progression.end();
    }

}
