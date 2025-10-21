/*
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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.google.common.base.Strings;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.server.VaadinService;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.avatars.AvatarItem;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.arakhne.afc.vmutil.FileSystem;

import java.util.function.Supplier;

/**
 * Default organization filtering inside the standard JPA filters for {@link AbstractDefaultOrganizationDataFilters}.
 * This filter panel adds the toggle button for enabling or disabling the filtering of data for the default organization.
 *
 * @param <T> the type of the entities to be filtered.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractDefaultOrganizationDataFilters<T> extends AbstractFilters<T> {

    private static final long serialVersionUID = 7953654886013901370L;

    private static final boolean DEFAULT_ORGANIZATION_FILTERING = true;

    private final Supplier<ResearchOrganization> defaultOrganization;

    private final Supplier<FileManager> fileManager;

    private final ToggleButton restrictToOrganization;

    private final AvatarItem defaultOrganizationAvatar;


    /**
     * Constructor.
     *
     * @param defaultOrganizationSupplier the provider of the default organization.
     * @param fileManager                 the manager of files on the server.
     * @param onSearch                    the callback function for running the filtering.
     */
    public AbstractDefaultOrganizationDataFilters(Supplier<ResearchOrganization> defaultOrganizationSupplier, Supplier<FileManager> fileManager, Runnable onSearch) {
        super(onSearch);

        this.defaultOrganization = defaultOrganizationSupplier;
        this.fileManager = fileManager;

        final var session = VaadinService.getCurrentRequest().getWrappedSession();
        final var attr = session.getAttribute(buildPreferenceSectionKeyForDefaultOrganizationFiltering());
        var checked = DEFAULT_ORGANIZATION_FILTERING;
        if (attr != null) {
            if (attr instanceof Boolean bvalue) {
                checked = bvalue.booleanValue();
            } else if (attr instanceof String svalue) {
                checked = Boolean.parseBoolean(svalue);
            }
        }
        this.restrictToOrganization = new ToggleButton(checked);

        this.defaultOrganizationAvatar = new AvatarItem();
        this.restrictToOrganization.setLabelComponent(this.defaultOrganizationAvatar);
        this.restrictToOrganization.addValueChangeListener(it -> onDefaultOrganizationFilteringChange(it.getValue() == null ? DEFAULT_ORGANIZATION_FILTERING : it.getValue().booleanValue(), onSearch));

        addComponentAsFirst(this.restrictToOrganization);
    }

    /**
     * Invoked to update the icon of the organization in the toggle button.
     */
    public final void updateDefaultOrganizationIconInToggleButton() {
        updateDefaultOrganizationIconInToggleButton(this.defaultOrganizationAvatar, this.defaultOrganization.get(), this.fileManager.get());
    }

    /**
     * Replies the default organization that is considered by this filter.
     *
     * @return the default organization.
     * @since 4.0
     */
    public ResearchOrganization getDefaultOrganization() {
        return this.defaultOrganization.get();
    }

    /**
     * Invoked to update the icon of the organization in the toggle button.
     *
     * @param organizationAvatar  the avatar of the research organization.
     * @param defaultOrganization the default organization to be considered.
     * @param fileManager         the manager of files on the server.
     */
    @SuppressWarnings("static-method")
    protected void updateDefaultOrganizationIconInToggleButton(AvatarItem organizationAvatar, ResearchOrganization defaultOrganization, FileManager fileManager) {
        final var logo = defaultOrganization.getPathToLogo();
        if (!Strings.isNullOrEmpty(logo)) {
            var logoFile = FileSystem.convertStringToFile(logo);
            if (logoFile != null) {
                logoFile = fileManager.normalizeForServerSide(logoFile);
                if (logoFile != null) {
                    organizationAvatar.setAvatarResource(ComponentFactory.newStreamImage(logoFile));
                }
            }
        }
    }

    private String buildPreferenceSectionKeyForDefaultOrganizationFiltering() {
        return ViewConstants.DEFAULT_ORGANIZATION_FILTER_ROOT + getClass().getName();
    }

    /**
     * Invoked when the filtering on the default organization has changed.
     *
     * @param enableOrganizationFilter indicates if the filtering is enable or disable.
     * @param onSearch                 the callback function for running the filtering.
     */
    protected void onDefaultOrganizationFilteringChange(boolean enableOrganizationFilter, Runnable onSearch) {
        final var session0 = VaadinService.getCurrentRequest().getWrappedSession();
        session0.setAttribute(buildPreferenceSectionKeyForDefaultOrganizationFiltering(), Boolean.valueOf(enableOrganizationFilter));
        if (onSearch != null) {
            onSearch.run();
        }
    }

    private boolean isRestrictedToDefaultOrganization() {
        if (this.restrictToOrganization != null) {
            final var bvalue = this.restrictToOrganization.getValue();
            if (bvalue == null) {
                return DEFAULT_ORGANIZATION_FILTERING;
            }
            return bvalue.booleanValue();
        }
        return false;
    }

    /**
     * Build the predicate for filtering the JPE entities that corresponds to the default organization with
     * the given identifier.
     *
     * @param root                the root element to filter.
     * @param query               the top-level query.
     * @param criteriaBuilder     the tool for building a filtering criteria.
     * @param defaultOrganization the default organization.
     * @return the predicate or {@code null} if there is no need for this predicate.
     */
    protected abstract Predicate buildPredicateForDefaultOrganization(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, ResearchOrganization defaultOrganization);

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        final var keywordFilter = super.toPredicate(root, query, criteriaBuilder);
        if (isRestrictedToDefaultOrganization()) {
            final var organizationPredicate = buildPredicateForDefaultOrganization(root, query, criteriaBuilder, this.defaultOrganization.get());
            if (organizationPredicate != null) {
                if (keywordFilter != null) {
                    return criteriaBuilder.and(organizationPredicate, keywordFilter);
                }
                return organizationPredicate;
            }
        }
        return keywordFilter;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.defaultOrganizationAvatar.setHeading(getTranslation("views.filters.default_organization_filter")); //$NON-NLS-1$
    }

}
