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

package fr.utbm.ciad.labmanager.services.admin.carnot;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureType;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationCategory;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.services.assostructure.AssociatedStructureService;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableContentHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableContentRowHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableHeaderHelper;
import fr.utbm.ciad.labmanager.utils.io.od.OdfSpreadsheetHelper.TableHelper;
import fr.utbm.ciad.labmanager.utils.trl.TRL;
import jakarta.transaction.Transactional;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A generator of an Excel file that contains the annual activity report with IC ARTS standard.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class IcartsActivityReportGenerator extends AbstractComponent {

    private static final long serialVersionUID = 5999486952125784994L;

    private final SupervisionService supervisionService;

    private final MembershipService membershipService;

    private final ResearchOrganizationService organizationService;

    private final PublicationService publicationService;

    private final AssociatedStructureService associatedStructureService;

    private final ProjectService projectService;

    /**
     * Constructor.
     *
     * @param messages                   the provider of messages.
     * @param constants                  the constants of the app.
     * @param organizationService        the organization service.
     * @param supervisionService         the supervision service.
     * @param membershipService          the membership service.
     * @param projectService             the project service.
     * @param publicationService         the publication service.
     * @param associatedStructureService the service for accessing the associated structures.
     * @param organizationService        the service for accessing the organizations.
     */
    public IcartsActivityReportGenerator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired SupervisionService supervisionService,
            @Autowired MembershipService membershipService,
            @Autowired ProjectService projectService,
            @Autowired PublicationService publicationService,
            @Autowired AssociatedStructureService associatedStructureService) {
        super(messages, constants);
        this.organizationService = organizationService;
        this.supervisionService = supervisionService;
        this.membershipService = membershipService;
        this.projectService = projectService;
        this.publicationService = publicationService;
        this.associatedStructureService = associatedStructureService;
    }

    /**
     * Extract the employers from the memberships.
     *
     * @param membership the membership for which the status should be converted.
     * @return the Carnot category.
     */
    protected static List<ResearchOrganization> extractEmployers(Membership membership) {
        return membership.getPerson().getMemberships().stream()
                .filter(it -> it.getId() != membership.getId() && it.getDirectResearchOrganization().getType().isEmployer())
                .map(it -> it.getDirectResearchOrganization())
                .collect(Collectors.toList());
    }

    /**
     * Extract the other employer value.
     *
     * @param employers the employers.
     * @return the other employer value.
     */
    protected static String otherEmployerValue(List<ResearchOrganization> employers) {
        StringBuilder buf = new StringBuilder();
        for (final ResearchOrganization organization : employers) {
            if (buf.length() > 0) {
                buf.append("; "); //$NON-NLS-1$
            }
            buf.append(organization.getAcronymOrName());
        }
        return buf.toString();
    }

    /**
     * Compute the number of months for the given time windows.
     *
     * @param yearStart       the first date of the year.
     * @param yearEnd         the last date of the year.
     * @param membershipStart the first date of the membership.
     * @param membershipEnd   the last date of the membership.
     * @return the number of months.
     */
    protected static Long monthsValue(LocalDate yearStart, LocalDate yearEnd, LocalDate membershipStart, LocalDate membershipEnd) {
        int firstMonth = 1;
        if (membershipStart != null && membershipStart.isAfter(yearStart)) {
            firstMonth = membershipStart.getMonthValue();
        }
        int lastMonth = 12;
        if (membershipEnd != null && membershipEnd.isBefore(yearEnd)) {
            lastMonth = membershipEnd.getMonthValue();
        }
        final int numberOfMonths = lastMonth - firstMonth + 1;
        return Long.valueOf(numberOfMonths);
    }

    /**
     * Export annual indicators to the IC-ARTS requirements.
     *
     * @param organizationId the identifier of organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale for the messages to the user.
     * @param progression    the progression indicator.
     * @return the report content (Excel file).
     * @throws Exception if the Excel components cannot be generated.
     */
    @Transactional
    public DownloadableFileDescription exportIcartsAnnualReport(long organizationId, int year, Locale locale, Progression progression) throws Exception {
        final var organization = this.organizationService.getResearchOrganizationById(organizationId).orElseThrow();
        progression.setProperties(0, 0, 4, false);
        try (final OdfSpreadsheetHelper ods = new OdfSpreadsheetHelper()) {
            exportETPs(ods, organization, year, locale, progression.subTask(1));
            exportProjects(ods, organization, year, locale, progression.subTask(1));
            exportSynthesis(ods, organization, year, locale, progression.subTask(1));
            progression.setComment(getMessage(locale, "IcartsActivityReportGenerator.bytes")); //$NON-NLS-1$
            return new DownloadableFileDescription(
                    ods.getFileExtension(),
                    ods.getMediaType(),
                    ods.toByteArray(true));
        } finally {
            progression.end();
        }
    }

    /**
     * Replies the type of supervision for the project.
     *
     * @param organization the organization of the project.
     * @param project      the project to analyze.
     * @return the string representing the type of supervision.
     */
    protected String projectPhD(ResearchOrganization organization, Project project) {
        final LocalDate startDate = project.getStartDate();
        final LocalDate endDate = project.getEndDate();
        for (final ProjectMember member : project.getParticipants()) {
            final Person person = member.getPerson();
            final Optional<Membership> mbr = this.membershipService.getMemberships(organization.getId(), person.getId()).stream()
                    .filter(it -> it.isActiveIn(startDate, endDate))
                    .findAny();
            if (mbr.isPresent()) {
                final Membership membership = mbr.get();
                if (membership.getMemberStatus() == MemberStatus.POSTDOC) {
                    return getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.postdoc_supervision"); //$NON-NLS-1$
                }
                if (membership.getMemberStatus() == MemberStatus.PHD_STUDENT) {
                    if (isCifre(membership)) {
                        return getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.cifre_supervision"); //$NON-NLS-1$
                    }
                    return getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.phd_thesis_supervision"); //$NON-NLS-1$
                }
            }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Replies the TRL of project.
     *
     * @param trl the TRL.
     * @return the string representing the TRL.
     */
    protected String projectTrl(TRL trl) {
        final String key;
        switch (trl) {
            case TRL1:
                key = "IcartsActivityReportGenerator.value.trl1"; //$NON-NLS-1$
                break;
            case TRL2:
                key = "IcartsActivityReportGenerator.value.trl2"; //$NON-NLS-1$
                break;
            case TRL3:
                key = "IcartsActivityReportGenerator.value.trl3"; //$NON-NLS-1$
                break;
            case TRL4:
                key = "IcartsActivityReportGenerator.value.trl4"; //$NON-NLS-1$
                break;
            case TRL5:
                key = "IcartsActivityReportGenerator.value.trl5"; //$NON-NLS-1$
                break;
            case TRL6:
                key = "IcartsActivityReportGenerator.value.trl6"; //$NON-NLS-1$
                break;
            case TRL7:
                key = "IcartsActivityReportGenerator.value.trl7"; //$NON-NLS-1$
                break;
            case TRL8:
                key = "IcartsActivityReportGenerator.value.trl8"; //$NON-NLS-1$
                break;
            case TRL9:
                key = "IcartsActivityReportGenerator.value.trl9"; //$NON-NLS-1$
                break;
            default:
                return ""; //$NON-NLS-1$
        }
        return getMessage(Locale.FRANCE, key);
    }

    /**
     * Replies the type of project's activity.
     *
     * @param type the activity type.
     * @return the type of project's activity.
     */
    protected String projectType(ProjectActivityType type) {
        final String key;
        switch (type) {
            case APPLIED_RESEARCH:
                key = "IcartsActivityReportGenerator.value.ra_type"; //$NON-NLS-1$
                break;
            case EXPERIMENTAL_DEVELOPMENT:
                key = "IcartsActivityReportGenerator.value.de_type"; //$NON-NLS-1$
                break;
            case FUNDAMENTAL_RESEARCH:
                key = "IcartsActivityReportGenerator.value.rf_type"; //$NON-NLS-1$
                break;
            default:
                return ""; //$NON-NLS-1$
        }
        return getMessage(Locale.FRANCE, key);
    }

    /**
     * Replies the type of project.
     *
     * @param type the type.
     * @return the type of project.
     */
    protected String projectType(ProjectContractType type) {
        final String key;
        switch (type) {
            case PI:
                key = "IcartsActivityReportGenerator.value.pi_type"; //$NON-NLS-1$
                break;
            case PR:
                key = "IcartsActivityReportGenerator.value.pr_type"; //$NON-NLS-1$
                break;
            case RCD:
                key = "IcartsActivityReportGenerator.value.rcd_type"; //$NON-NLS-1$
                break;
            case RCO:
                key = "IcartsActivityReportGenerator.value.rco_type"; //$NON-NLS-1$
                break;
            case NOT_SPECIFIED:
            default:
                key = "IcartsActivityReportGenerator.value.not_eligible"; //$NON-NLS-1$
                break;
        }
        return getMessage(Locale.FRANCE, key);
    }

    /**
     * Replies the list of the funders for the given project.
     *
     * @param project the project to analyze.
     * @return the list of funders.
     */
    protected String funders(Project project) {
        final StringBuilder buf = new StringBuilder();
        for (final ProjectBudget budget : project.getBudgets()) {
            if (buf.length() > 0) {
                buf.append(", "); //$NON-NLS-1$
            }
            switch (budget.getFundingScheme()) {
                case CIFRE:
                case EU_COMPANY:
                case EU_UNIVERSITY:
                case FRENCH_COMPANY:
                case FRENCH_OTHER:
                case FRENCH_UNIVERSITY:
                case INTERNATIONAL_COMPANY:
                case INTERNATIONAL_UNIVERSITY:
                case INTERNTATIONAL_OTHER:
                case EU_OTHER:
                case HOSTING_ORGANIZATION:
                    buf.append(project.getCoordinator().getNameOrAcronym());
                    break;
                case LOCAL_INSTITUTION:
                    buf.append(project.getLearOrganization().getNameOrAcronym());
                    break;
                case ADEME:
                case ANR:
                case CAMPUS_FRANCE:
                case CARNOT:
                case CONACYT:
                case COST_ACTION:
                case CPER:
                case CSC:
                case EDIH:
                case EUREKA:
                case EUROSTAR:
                case FEDER:
                case FITEC:
                case FUI:
                case FRANCE_2030:
                case H2020:
                case HORIZON_EUROPE:
                case IDEX:
                case INTERREG:
                case ISITE:
                case JPIEU:
                case LIFE:
                case NICOLAS_BAUDIN:
                case NOT_FUNDED:
                case PHC:
                case PIA:
                case REGION_BFC:
                case SELF_FUNDING:
                    buf.append(budget.getFundingScheme().getLabel(getMessageSourceAccessor(), Locale.FRANCE));
                    break;
                default:
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * Export the full-time equivalent persons in a spreadsheet page that corresponds to the Carnot standard.
     *
     * @param document     the output document.
     * @param organization the organization for which the publications must be extracted.
     * @param year         the reference year that is used for filtering the publications.
     * @param locale       the locale for the messages to the user.
     * @param progression  the progression indicator.
     * @throws Exception if the Excel cannot be generated.
     */
    protected void exportETPs(OdfSpreadsheetHelper document, ResearchOrganization organization, int year, Locale locale, Progression progression) throws Exception {
        progression.setProperties(0, 0, 4, false, getMessage(locale, "IcartsActivityReportGenerator.indicators.etps")); //$NON-NLS-1$
        //
        final var memberships = organization.getDirectOrganizationMemberships();
        progression.increment();
        //
        final TableHelper output = document.newTable(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.table.etp", Integer.valueOf(year))); //$NON-NLS-1$
        progression.increment();
        //
        final TableHeaderHelper header = output.getHeader();
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.lab")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.name")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.firstname")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.other_lab")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.permanent_staff")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.position")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.hdr")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.employer")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.other_employer")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.months", Integer.valueOf(year))); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.project_count", Integer.valueOf(year))); //$NON-NLS-1$
        progression.increment();
        //
        final var subProgress = progression.subTask(1);
        subProgress.setProperties(0, 0, memberships.size(), false);
        final LocalDate startDate = LocalDate.of(year, 1, 1);
        final LocalDate endDate = LocalDate.of(year, 12, 31);
        final TableContentHelper content = output.getContent();
        for (final var membership : memberships) {
            if (membership.isActiveIn(startDate, endDate) && membershipStatus(membership) != null) {
                final List<ResearchOrganization> employers = extractEmployers(membership);
                final String employerValue = employerValue(employers);
                final TableContentRowHelper row = content.appendRow();
                row.append(organization.getAcronym());
                row.append(membership.getPerson().getLastName());
                row.append(membership.getPerson().getFirstName());
                // No other lab
                row.append((String) null);
                row.append(booleanValue(Boolean.valueOf(membership.isPermanentPosition())));
                row.append(membershipStatus(membership));
                row.append(booleanValue(Boolean.valueOf(membership.getMemberStatus().isHdrOwner())));
                row.append(Strings.isNullOrEmpty(employerValue) ? getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.other_employer") : employerValue); //$NON-NLS-1$
                row.append(Strings.isNullOrEmpty(employerValue) ? otherEmployerValue(employers) : null);
                row.append(monthsValue(startDate, endDate, membership.getMemberSinceWhen(), membership.getMemberToWhen()));
                final long projectCount = this.projectService.getProjectsByPersonId(membership.getPerson().getId())
                        .stream().filter(it0 -> it0.getStatus() == ProjectStatus.ACCEPTED && it0.isActiveAt(year) && it0.getCategory().isContractualProject())
                        .count();
                row.append(Long.valueOf(projectCount));
            }
            subProgress.increment();
        }
        progression.end();
    }

    /**
     * Export the projects in a spreadsheet page that corresponds to the Carnot standard.
     *
     * @param document     the output document.
     * @param organization the organization for which the publications must be extracted.
     * @param year         the reference year that is used for filtering the publications.
     * @param locale       the locale for the messages to the user.
     * @param progression  the progression indicator.
     * @throws Exception if the Excel cannot be generated.
     */
    protected void exportProjects(OdfSpreadsheetHelper document, ResearchOrganization organization, int year, Locale locale, Progression progression) throws Exception {
        progression.setProperties(0, 0, 4, false, getMessage(locale, "IcartsActivityReportGenerator.indicators.projects")); //$NON-NLS-1$
        //
        final var projects = this.projectService.getAllProjects();
        progression.increment();
        //
        final TableHelper output = document.newTable(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.table.projects", Integer.valueOf(year))); //$NON-NLS-1$
        progression.increment();
        //
        final TableHeaderHelper header = output.getHeader();
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.acronym")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.project_name")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.budget")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.funder")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.contract_type")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.has_phd_thesis")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.category")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.trl")); //$NON-NLS-1$
        header.appendColumn(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.column.lear")); //$NON-NLS-1$
        progression.increment();
        //
        final var subProgress = progression.subTask(1);
        final AtomicInteger rowIndex = new AtomicInteger(1);
        final TableContentHelper content = output.getContent();
        for (final var project : projects) {
            if (project.getStartDate().getYear() == year && project.getStatus() == ProjectStatus.ACCEPTED) {
                final TableContentRowHelper row = content.appendRow();
                rowIndex.incrementAndGet();
                row.append(project.getAcronym());
                row.append(project.getScientificTitle());
                row.appendCurrency(Double.valueOf(project.getTotalLocalOrganizationBudget() * 1000f));
                row.append(funders(project));
                row.append(projectType(project.getContractType()));
                row.append(projectPhD(organization, project));
                row.append(projectType(project.getActivityType()));
                row.append(projectTrl(project.getTRL()));
                row.append(project.getLearOrganization().getAcronymOrName());
            }
            subProgress.increment();
        }
        progression.end();
    }

    /**
     * Export the annual synthesis in a spreadsheet page that corresponds to the Carnot standard.
     *
     * @param document     the output document.
     * @param organization the organization for which the synthesis must be extracted.
     * @param year         the reference year that is used for filtering the publications.
     * @param locale       the locale for the messages to the user.
     * @param progression  the progression indicator.
     * @throws Exception if the Excel cannot be generated.
     */
    protected void exportSynthesis(OdfSpreadsheetHelper document, ResearchOrganization organization, int year, Locale locale, Progression progression) throws Exception {
        progression.setProperties(0, 0, 4, false, getMessage(locale, "IcartsActivityReportGenerator.indicators.synthesis")); //$NON-NLS-1$
        //
        final TableHelper output = document.newTable(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.table.synthesis", Integer.valueOf(year))); //$NON-NLS-1$
        final TableContentHelper content = output.getContent();
        progression.increment();
        // Publications
        final long pubs = this.publicationService.getPublicationsByOrganizationId(organization.getId(), true, false).stream()
                .filter(it -> it.getPublicationYear() == year
                        && (it.getCategory() == PublicationCategory.OS
                        || ((it.getCategory() == PublicationCategory.C_ACTI || it.getCategory() == PublicationCategory.ACL) && it.isRanked())))
                .count();
        final TableContentRowHelper row0 = content.appendRow();
        row0.append(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.pubs_synthesis")); //$NON-NLS-1$
        row0.append(Long.valueOf(pubs));
        progression.increment();
        // Created companies
        final long companies = this.associatedStructureService.getAssociatedStructuresByOrganizationId(organization.getId()).stream()
                .filter(it -> it.getCreationDate().getYear() == year && it.getType() == AssociatedStructureType.PRIVATE_COMPANY)
                .count();
        final TableContentRowHelper row1 = content.appendRow();
        row1.append(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.companies_synthesis")); //$NON-NLS-1$
        row1.append(Long.valueOf(companies));
        progression.increment();
        // Created labs
        final long labs = this.associatedStructureService.getAssociatedStructuresByOrganizationId(organization.getId()).stream()
                .filter(it -> it.getCreationDate().getYear() == year
                        && (it.getType() == AssociatedStructureType.INTERNATIONAL_RESEARCH_LAB
                        || it.getType() == AssociatedStructureType.NATIONAL_RESEARCH_LAB))
                .count();
        final TableContentRowHelper row2 = content.appendRow();
        row2.append(getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.labs_synthesis")); //$NON-NLS-1$
        row2.append(Long.valueOf(labs));
        progression.end();
    }

    /**
     * Extract the employer value.
     *
     * @param employers the employers.
     * @return the employer value.
     */
    protected String employerValue(List<ResearchOrganization> employers) {
        final var utbmValue = getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.utbm_employer"); //$NON-NLS-1$
        final var ubValue = getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.ub_employer"); //$NON-NLS-1$
        final var ubsValue = getMessage(Locale.FRANCE, "IcartsActivityReportGenerator.value.ub_employer.short"); //$NON-NLS-1$
        for (final ResearchOrganization organization : employers) {
            if (utbmValue.equalsIgnoreCase(organization.getAcronym())) {
                return utbmValue;
            }
            if (ubsValue.equalsIgnoreCase(organization.getAcronym())) {
                return ubValue;
            }
        }
        return null;
    }

    /**
     * Convert the boolean value to its equivalent in the Carnot CSV.
     *
     * @param value the boolean value.
     * @return the string representation of the boolean value.
     */
    protected String booleanValue(Boolean value) {
        if (value == null) {
            return ""; //$NON-NLS-1$
        }
        final var key = value.booleanValue() ? "IcartsActivityReportGenerator.value.yes" : "IcartsActivityReportGenerator.value.no"; //$NON-NLS-1$ //$NON-NLS-2$
        return getMessage(Locale.FRANCE, key);
    }

    /**
     * Replies if the given person has a CIFRE contract during the time windows.
     *
     * @param membership the membership for which the status should be converted.
     * @return {@code true} if a CIFRE exists.
     */
    protected boolean isCifre(Membership membership) {
        final List<Supervision> res = this.supervisionService.getSupervisionsForMembership(membership.getId());
        if (!res.isEmpty()) {
            final Supervision supervision = res.get(0);
            switch (supervision.getFunding()) {
                case CIFRE:
                case EU_COMPANY:
                case FRENCH_COMPANY:
                case INTERNATIONAL_COMPANY:
                    return true;
                case ADEME:
                case ANR:
                case CAMPUS_FRANCE:
                case CARNOT:
                case CONACYT:
                case COST_ACTION:
                case CPER:
                case CSC:
                case EDIH:
                case EUREKA:
                case EUROSTAR:
                case EU_OTHER:
                case EU_UNIVERSITY:
                case FEDER:
                case FITEC:
                case FRANCE_2030:
                case FRENCH_OTHER:
                case FRENCH_UNIVERSITY:
                case FUI:
                case H2020:
                case HORIZON_EUROPE:
                case HOSTING_ORGANIZATION:
                case IDEX:
                case INTERNATIONAL_UNIVERSITY:
                case INTERNTATIONAL_OTHER:
                case INTERREG:
                case ISITE:
                case JPIEU:
                case LIFE:
                case LOCAL_INSTITUTION:
                case NICOLAS_BAUDIN:
                case NOT_FUNDED:
                case PHC:
                case PIA:
                case REGION_BFC:
                case SELF_FUNDING:
                default:
                    //
            }
        }
        return false;
    }

    /**
     * Convert the member status to its equivalent category for Carnot.
     *
     * @param membership the membership for which the status should be converted.
     * @return the Carnot category.
     */
    protected String membershipStatus(Membership membership) {
        final String key;
        switch (membership.getMemberStatus()) {
            case FULL_PROFESSOR:
                key = "IcartsActivityReportGenerator.value.pr"; //$NON-NLS-1$
                break;
            case RESEARCH_DIRECTOR:
                key = "IcartsActivityReportGenerator.value.dr"; //$NON-NLS-1$
                break;
            case ASSOCIATE_PROFESSOR:
            case ASSOCIATE_PROFESSOR_HDR:
            case CONTRACTUAL_RESEARCHER_TEACHER_PHD:
            case CONTRACTUAL_RESEARCHER_TEACHER:
                key = "IcartsActivityReportGenerator.value.mcf"; //$NON-NLS-1$
                break;
            case TEACHER:
            case TEACHER_PHD:
                key = "IcartsActivityReportGenerator.value.teacher"; //$NON-NLS-1$
                break;
            case RESEARCHER_PHD:
                key = "IcartsActivityReportGenerator.value.cr"; //$NON-NLS-1$
                break;
            case RESEARCHER:
                key = "IcartsActivityReportGenerator.value.ater_past"; //$NON-NLS-1$
                break;
            case POSTDOC:
                key = "IcartsActivityReportGenerator.value.postdoc"; //$NON-NLS-1$
                break;
            case PHD_STUDENT:
                key = isCifre(membership) ? "IcartsActivityReportGenerator.value.cifre" : "IcartsActivityReportGenerator.value.phd_student"; //$NON-NLS-1$ //$NON-NLS-2$
                break;
            case ENGINEER:
            case ENGINEER_PHD:
            case RESEARCH_ENGINEER:
            case RESEARCH_ENGINEER_PHD:
                key = "IcartsActivityReportGenerator.value.engineer"; //$NON-NLS-1$
                break;
            // The following positions are not considered by Carnot
            case ADMIN:
            case ASSOCIATED_MEMBER:
            case ASSOCIATED_MEMBER_PHD:
            case EMERITUS_ASSOCIATE_PROFESSOR:
            case EMERITUS_ASSOCIATE_PROFESSOR_HDR:
            case EMERITUS_FULL_PROFESSOR:
            case CONTRACT_MASTER_STUDENT:
            case MASTER_STUDENT:
            case OTHER_CONTRACT_STUDENT:
            case OTHER_STUDENT:
            default:
                return null;
        }
        return getMessage(Locale.FRANCE, key);
    }

}
