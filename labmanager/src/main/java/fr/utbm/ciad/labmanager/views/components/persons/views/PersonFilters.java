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

package fr.utbm.ciad.labmanager.views.components.persons.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractDefaultOrganizationDataFilters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.function.Supplier;

/**
 * UI and JPA filters for {@link PersonFilters}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PersonFilters extends AbstractDefaultOrganizationDataFilters<Person> {

    private static final long serialVersionUID = -127264050870541315L;

    private Checkbox includeNames;

    private Checkbox includeOrcids;

    private Checkbox includeOrganizations;

    /**
     * Constructor.
     *
     * @param defaultOrganizationSupplier the provider of the default organization.
     * @param fileManager                 the manager of files on the server.
     * @param onSearch                    the callback function for running the filtering.
     */
    public PersonFilters(Supplier<ResearchOrganization> defaultOrganizationSupplier, Supplier<FileManager> fileManager, Runnable onSearch) {
        super(defaultOrganizationSupplier, fileManager, onSearch);
    }

    @Override
    protected void buildOptionsComponent(HorizontalLayout options) {
        this.includeNames = new Checkbox(true);
        this.includeOrcids = new Checkbox(true);
        this.includeOrganizations = new Checkbox(false);

        options.add(this.includeNames, this.includeOrcids, this.includeOrganizations);
    }

    @Override
    protected void resetFilters() {
        this.includeNames.setValue(Boolean.TRUE);
        this.includeOrcids.setValue(Boolean.TRUE);
        this.includeOrganizations.setValue(Boolean.TRUE);
    }

    @Override
    protected Predicate buildPredicateForDefaultOrganization(Root<Person> root, CriteriaQuery<?> query,
                                                             CriteriaBuilder criteriaBuilder, ResearchOrganization defaultOrganization) {
        final var predicate = criteriaBuilder.equal(root.get("memberships").get("researchOrganization"), defaultOrganization); //$NON-NLS-1$ //$NON-NLS-2$
        // The following code line is mandatory for avoiding the duplicate entries in the results
        query.distinct(true);
        return predicate;
    }

    @Override
    protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Person> root,
                                 CriteriaBuilder criteriaBuilder) {
        if (this.includeNames.getValue() == Boolean.TRUE) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), keywords)); //$NON-NLS-1$
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), keywords)); //$NON-NLS-1$
        }

        if (this.includeOrcids.getValue() == Boolean.TRUE) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("orcid")), keywords)); //$NON-NLS-1$
        }
        if (this.includeOrganizations.getValue() == Boolean.TRUE) {
            final Predicate startPeriodPredicate = criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("memberships").get("memberSinceWhen")), //$NON-NLS-1$ //$NON-NLS-2$
                    criteriaBuilder.lessThanOrEqualTo(root.get("memberships").get("memberSinceWhen"), criteriaBuilder.localDate())); //$NON-NLS-1$ //$NON-NLS-2$

            final Predicate endPeriodPredicate = criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("memberships").get("memberToWhen")), //$NON-NLS-1$ //$NON-NLS-2$
                    criteriaBuilder.greaterThanOrEqualTo(root.get("memberships").get("memberToWhen"), criteriaBuilder.localDate())); //$NON-NLS-1$ //$NON-NLS-2$

            final Predicate textPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("memberships").get("researchOrganization").get("acronym")), keywords), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("memberships").get("researchOrganization").get("name")), keywords)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            final Predicate gpredicate = criteriaBuilder.and(startPeriodPredicate, endPeriodPredicate, textPredicate);

            predicates.add(gpredicate);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
        this.includeOrcids.setLabel(getTranslation("views.filters.include_orcids")); //$NON-NLS-1$
        this.includeOrganizations.setLabel(getTranslation("views.filters.include_organizations")); //$NON-NLS-1$
    }

}
