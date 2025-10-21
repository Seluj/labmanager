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

package fr.utbm.ciad.labmanager.views.components.organizationaddresses.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.AddressEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardAddressListView extends AbstractEntityListView<OrganizationAddress> {

    private static final long serialVersionUID = -6686033498276980924L;

    private final AddressDataProvider dataProvider;

    private final AddressEditorFactory addressEditorFactory;

    private final OrganizationAddressService addressService;

    private Column<OrganizationAddress> nameColumn;

    private Column<OrganizationAddress> addressColumn;

    private Column<OrganizationAddress> validationColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser    the connected user.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     * @param addressService       the service for accessing the addresses.
     * @param addressEditorFactory the factory for creating the address editors.
     */
    public StandardAddressListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            OrganizationAddressService addressService, AddressEditorFactory addressEditorFactory) {
        super(OrganizationAddress.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.addresses.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.addresses.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.addresses.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.addresses.delete_error")); //$NON-NLS-1$
        this.addressService = addressService;
        this.addressEditorFactory = addressEditorFactory;
        this.dataProvider = (ps, query, filters) -> ps.getAllAddresses(query, filters);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    @Override
    protected AbstractFilters<OrganizationAddress> createFilters() {
        return new AddressFilters(this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<OrganizationAddress> grid) {
        this.nameColumn = grid.addColumn(address -> address.getName())
                .setAutoWidth(true)
                .setFrozen(true)
                .setSortProperty("name"); //$NON-NLS-1$
        this.addressColumn = grid.addColumn(address -> address.getFullAddress())
                .setAutoWidth(true)
                .setSortProperty("street", "zipCode", "city"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.validationColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
                    if (data.isValidated()) {
                        callback.create(BadgeState.SUCCESS, null, getTranslation("views.validated")); //$NON-NLS-1$
                    } else {
                        callback.create(BadgeState.ERROR, null, getTranslation("views.validable")); //$NON-NLS-1$
                    }
                }))
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortProperty("validated") //$NON-NLS-1$
                .setWidth("0%"); //$NON-NLS-1$
        // Create the hover tool bar only if administrator role
        return isAdminRole();
    }

    @Override
    protected List<Column<OrganizationAddress>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<OrganizationAddress, Void> getFetchCallback(AbstractFilters<OrganizationAddress> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.addressService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openAddressEditor(new OrganizationAddress(), getTranslation("views.addresses.add_address"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(OrganizationAddress address) {
        openAddressEditor(address, getTranslation("views.addresses.edit_address", address.getName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of an address.
     *
     * @param address    the address to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating an entity or editing it.
     */
    protected void openAddressEditor(OrganizationAddress address, String title, boolean isCreation) {
        final AbstractEntityEditor<OrganizationAddress> editor;
        if (isCreation) {
            editor = this.addressEditorFactory.createAdditionEditor(address, getLogger());
        } else {
            editor = this.addressEditorFactory.createUpdateEditor(address, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, OrganizationAddress> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, OrganizationAddress> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<OrganizationAddress> createDeletionContextFor(Set<OrganizationAddress> entities) {
        return this.addressService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.addressColumn.setHeader(getTranslation("views.address")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
    }

    /**
     * Provider of data for addresses to be displayed in the list of addresses view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface AddressDataProvider {

        /**
         * Fetch address data.
         *
         * @param addressService the service to have access to the JPA.
         * @param pageRequest    the request for paging the data.
         * @param filters        the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<OrganizationAddress> fetch(OrganizationAddressService addressService, PageRequest pageRequest, AbstractFilters<OrganizationAddress> filters);

    }

    /**
     * UI and JPA filters for {@link StandardAddressListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class AddressFilters extends AbstractFilters<OrganizationAddress> {

        private static final long serialVersionUID = -2735598855241668806L;

        private Checkbox includeNames;

        private Checkbox includeComplements;

        private Checkbox includeStreets;

        private Checkbox includeZipCodes;

        private Checkbox includeCities;

        /**
         * Constructor.
         *
         * @param onSearch the callback function for running the filtering.
         */
        public AddressFilters(Runnable onSearch) {
            super(onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeNames = new Checkbox(true);
            this.includeComplements = new Checkbox(true);
            this.includeStreets = new Checkbox(true);
            this.includeZipCodes = new Checkbox(true);
            this.includeCities = new Checkbox(true);

            options.add(this.includeNames, this.includeComplements, this.includeStreets,
                    this.includeZipCodes, this.includeCities);
        }

        @Override
        protected void resetFilters() {
            this.includeNames.setValue(Boolean.TRUE);
            this.includeComplements.setValue(Boolean.TRUE);
            this.includeStreets.setValue(Boolean.TRUE);
            this.includeZipCodes.setValue(Boolean.TRUE);
            this.includeCities.setValue(Boolean.TRUE);
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<OrganizationAddress> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywords)); //$NON-NLS-1$
            }
            if (this.includeComplements.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("complement")), keywords)); //$NON-NLS-1$
            }
            if (this.includeStreets.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("street")), keywords)); //$NON-NLS-1$
            }
            if (this.includeZipCodes.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("zipCode")), keywords)); //$NON-NLS-1$
            }
            if (this.includeCities.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
            this.includeComplements.setLabel(getTranslation("views.filters.include_complements")); //$NON-NLS-1$
            this.includeStreets.setLabel(getTranslation("views.filters.include_streets")); //$NON-NLS-1$
            this.includeZipCodes.setLabel(getTranslation("views.filters.include_zipcodes")); //$NON-NLS-1$
            this.includeCities.setLabel(getTranslation("views.filters.include_cities")); //$NON-NLS-1$
        }

    }

}
