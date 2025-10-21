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

package fr.utbm.ciad.labmanager.utils.io;

import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Configurator for an exporter.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class ExporterConfigurator {

    private final Map<String, Object> queryParameters = new HashMap<>();
    private final JournalService service;
    private final Locale locale;
    private Predicate<Person> personSelector;
    private Predicate<ResearchOrganization> organizationSelector;
    private boolean enableSelectedPersonFormat = true;
    private boolean enableResearcherFormat = true;
    private boolean enablePhDStudentFormat = true;
    private boolean enablePostdocEngineerFormat = true;
    private boolean enableTitleColor = true;
    private boolean enableFormattedLinks = true;
    private boolean enableFormattedAuthorList = true;
    private boolean enableFormattedPublicationDetails = true;
    private boolean enableTypeAndCategoryLabels = true;
    private boolean enableGroupByCategory;
    private boolean enableGroupByYear;

    /**
     * Constructor.
     *
     * @param service the journal service.
     * @param locale  the locale to be used.
     */
    public ExporterConfigurator(JournalService service, Locale locale) {
        this.service = service;
        this.locale = locale;
    }

    private static boolean isValidPeriod(Membership membership, int year) {
        final var start = LocalDate.of(year, 1, 1);
        final var end = LocalDate.of(year, 12, 31);
        return membership.isActiveIn(start, end);
    }

    private static boolean isResearcher(Membership membership) {
        final var it = membership.getMemberStatus();
        return it.isResearcher() && !it.isExternalPosition() && it != MemberStatus.POSTDOC && it != MemberStatus.PHD_STUDENT;
    }

    private static boolean isPhDStudent(Membership membership) {
        final var it = membership.getMemberStatus();
        return it == MemberStatus.PHD_STUDENT;
    }

    private static boolean isPostdocEngineer(Membership membership) {
        final var it = membership.getMemberStatus();
        return it.isTechnicalStaff() || it == MemberStatus.POSTDOC;
    }

    private static ExportedAuthorStatus max(ExportedAuthorStatus status0, ExportedAuthorStatus status1) {
        if (status0.ordinal() <= status1.ordinal()) {
            return status0;
        }
        return status1;
    }

    /**
     * Replies the service dedicated to journals.
     *
     * @return the service.
     */
    public JournalService getJournalService() {
        return this.service;
    }

    /**
     * Replies the locale to be used.
     *
     * @return the locale.
     */
    public Locale getLocale_() {
        return this.locale;
    }

    /**
     * Replies the locale to be used if it is defined in this configuration, or the locale that corresponds to the given language, or the default system locale.
     *
     * @param language the publication's language.
     * @return the locale.
     * @since 4.0
     */
    public Locale getLocaleOrLanguageLocale(PublicationLanguage language) {
        var loc = this.locale;
        if (loc == null) {
            if (language != null) {
                loc = language.getLocale();
                if (loc == null) {
                    loc = Locale.getDefault();
                }
            } else {
                loc = Locale.getDefault();
            }
        }
        return loc;
    }

    /**
     * Replies if the publications must be grouped by year.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} if publications are grouped by year.
     */
    public boolean isGroupedByYear() {
        return this.enableGroupByYear;
    }

    /**
     * Enable the grouping of the publications by year.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator enableGroupByYear() {
        this.enableGroupByYear = true;
        return this;
    }

    /**
     * Replies if the publications must be grouped by category.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} if publications are grouped by category.
     */
    public boolean isGroupedByCategory() {
        return this.enableGroupByCategory;
    }

    /**
     * Enable the grouping of the publications by category.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator enableGroupByCategory() {
        this.enableGroupByCategory = true;
        return this;
    }

    /**
     * Replies if the labels of the publication type and category are included to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for including HTML labels.
     */
    public boolean isTypeAndCategoryLabels() {
        return this.enableTypeAndCategoryLabels;
    }

    /**
     * Disable the generation of the labels of the publication type and category are included to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableTypeAndCategoryLabels() {
        this.enableTypeAndCategoryLabels = false;
        return this;
    }

    /**
     * Replies if the HTML links is included to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for including HTML publication details.
     */
    public boolean isFormattedLinks() {
        return this.enableFormattedLinks;
    }

    /**
     * Disable the generation of the HTML links to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableFormattedLinks() {
        this.enableFormattedLinks = false;
        return this;
    }

    /**
     * Replies if the HTML publication details is included to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for including HTML publication details.
     */
    public boolean isFormattedPublicationDetails() {
        return this.enableFormattedPublicationDetails;
    }

    /**
     * Disable the generation of the HTML list of authors to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableFormattedPublicationDetails() {
        this.enableFormattedPublicationDetails = false;
        return this;
    }

    /**
     * Replies if the HTML list of authors is included to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for including HTML author list .
     */
    public boolean isFormattedAuthorList() {
        return this.enableFormattedAuthorList;
    }

    /**
     * Disable the generation of the HTML list of authors to the exported data.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableFormattedAuthorList() {
        this.enableFormattedAuthorList = false;
        return this;
    }

    /**
     * Replies if the titles could be drawn with color font.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for color font.
     */
    public boolean isColoredTitle() {
        return this.enableTitleColor;
    }

    /**
     * Disable the coloring of the titles.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableTitleColor() {
        this.enableTitleColor = false;
        return this;
    }

    /**
     * Replies if the names of the organization Postdoc/engineer should be formated.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for formatting the names.
     */
    public boolean isPostdocEngineerNameFormatted() {
        return this.enablePostdocEngineerFormat;
    }

    /**
     * Disable the format of the names of the organization Postdoc/engineer.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disablePostdocEngineerFormat() {
        this.enablePostdocEngineerFormat = false;
        return this;
    }

    /**
     * Replies if the names of the organization PhD students should be formated.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for formatting the names.
     */
    public boolean isPhDStudentNameFormatted() {
        return this.enablePhDStudentFormat;
    }

    /**
     * Disable the format of the names of the organization PhD students.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disablePhDStudentFormat() {
        this.enablePhDStudentFormat = false;
        return this;
    }

    /**
     * Replies if the names of the organization researchers should be formated.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for formatting the names.
     */
    public boolean isResearcherNameFormatted() {
        return this.enableResearcherFormat;
    }

    /**
     * Disable the format of the names of the organization researchers.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableResearcherFormat() {
        this.enableResearcherFormat = false;
        return this;
    }

    /**
     * Replies if the names of the selected authors should be formated.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return {@code true} for formatting the names.
     */
    public boolean isSelectedPersonNameFormatted() {
        return this.enableSelectedPersonFormat;
    }

    /**
     * Disable the format of the names of the selected authors.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @return this.
     */
    public ExporterConfigurator disableSelectedPersonFormat() {
        this.enableSelectedPersonFormat = false;
        return this;
    }

    /**
     * Change the selector for the persons.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @param selector the selector or {@code null}.
     * @return this.
     */
    public ExporterConfigurator selectPerson(Predicate<Person> selector) {
        this.personSelector = selector;
        return this;
    }

    /**
     * Replies if a person selector is defined
     *
     * @return {@code true} if a person selector is defined.
     */
    public boolean hasPersonSelector() {
        return this.personSelector != null;
    }

    /**
     * Change the selector for the organization.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @param selector the selector or {@code null}.
     * @return this.
     */
    public ExporterConfigurator selectOrganization(Predicate<ResearchOrganization> selector) {
        this.organizationSelector = selector;
        return this;
    }

    /**
     * Replies if an organization selector is defined
     *
     * @return {@code true} if an organization selector is defined.
     */
    public boolean hasOrganizationSelector() {
        return this.organizationSelector != null;
    }

    /**
     * Replies the status of an exported author. If there is multiple memberships active at the same time,
     * the highest membership position is considered.
     * <p>This feature may be ignored in the implementation of the exporter.
     *
     * @param person the person to test.
     * @param year   the current year.
     * @return the status, never {@code null}.
     */
    public ExportedAuthorStatus getExportedAuthorStatusFor(Person person, int year) {
        if (person != null) {
            if (this.personSelector != null && this.personSelector.test(person)) {
                return ExportedAuthorStatus.SELECTED_PERSON;
            }
            var status = ExportedAuthorStatus.OTHER;
            for (final var membership : person.getMemberships()) {
                if (isValidPeriod(membership, year)
                        && (this.organizationSelector == null || this.organizationSelector.test(membership.getDirectResearchOrganization()))) {
                    if (isResearcher(membership)) {
                        status = max(status, ExportedAuthorStatus.RESEARCHER);
                    } else if (isPhDStudent(membership)) {
                        status = max(status, ExportedAuthorStatus.PHD_STUDENT);
                    } else if (isPostdocEngineer(membership)) {
                        status = max(status, ExportedAuthorStatus.POSTDOC_ENGINEER);
                    }
                }
            }
            return status;
        }
        return ExportedAuthorStatus.OTHER;
    }

    /**
     * Register a parameter that should be added to the URLs as query parameter.
     *
     * @param name  the name of the parameter.
     * @param value the value of the parameter.
     */
    public void addUriQueryParam(String name, Integer value) {
        this.queryParameters.put(name, value);
    }

    /**
     * Register a parameter that should be added to the URLs as query parameter.
     *
     * @param name  the name of the parameter.
     * @param value the value of the parameter.
     */
    public void addUriQueryParam(String name, String value) {
        this.queryParameters.put(name, value);
    }

    /**
     * Add the registered qyery parameters to the given builder.
     *
     * @param uriBuilder the builder to fill up.
     * @return the {@code uriBuilder}.
     */
    public UriBuilder applyQueryParams(UriBuilder uriBuilder) {
        var b = uriBuilder;
        for (final var query : this.queryParameters.entrySet()) {
            b = b.queryParam(query.getKey(), query.getValue());
        }
        return b;
    }

}
