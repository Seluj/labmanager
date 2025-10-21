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

package fr.utbm.ciad.labmanager.views.components.memberships.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.*;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.memberships.editors.MembershipEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * List all the organization memberships.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardMembershipListView extends AbstractTwoLevelTreeListView<Person, Membership> {

    private static final long serialVersionUID = 2320930215751419329L;

    private final MembershipService membershipService;

    private final MembershipEditorFactory membershipEditorFactory;

    private final ResearchOrganizationService organizationService;

    private MenuItem extendsContractButton;

    private Column<TreeListEntity<Person, Membership>> periodColumn;

    private Column<TreeListEntity<Person, Membership>> serviceColumn;

    private Column<TreeListEntity<Person, Membership>> employerColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (spring layer).
     * @param loggerFactory           the factory to be used for the composite logger.
     * @param membershipService       the service for accessing the memberships.
     * @param membershipEditorFactory the factory for creating the person membership editors.
     * @param organizationService     the service for accessing the JPA entities for research organizations.
     */
    public StandardMembershipListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            MembershipService membershipService, MembershipEditorFactory membershipEditorFactory,
            ResearchOrganizationService organizationService) {
        super(Person.class, Membership.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.memberships.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.memberships.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.membership.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.membership.delete_error")); //$NON-NLS-1$
        this.membershipService = membershipService;
        this.membershipEditorFactory = membershipEditorFactory;
        this.organizationService = organizationService;
        setHoverMenu(isAdminRole());
        setRootEntityFetcher(
                (parentId, pageRequest, filters) -> {
                    return this.membershipService.getAllPersonsWithMemberships(pageRequest, filters,
                            it -> Hibernate.initialize(it.getMemberships()));
                },
                (rootEntity) -> {
                    return rootEntity.getMemberships().size();
                });
        setChildEntityFetcher(this.membershipService::getMembershipsForPerson);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private static ResearchOrganization getService(Membership membership) {
        assert membership != null;
        final var employerOpt = membership.getEmployer();
        if (employerOpt.isPresent()) {
            final var employer = employerOpt.get();
            final var candidate = membership.getDirectResearchOrganization();
            if (candidate.getId() != employer.getId()) {
                return candidate;
            }
        }
        return null;
    }

    private static ResearchOrganization getEmployer(Membership membership) {
        assert membership != null;
        final var employer = membership.getEmployer();
        if (employer.isPresent()) {
            return employer.get();
        }
        return membership.getDirectResearchOrganization();
    }

    @Override
    protected List<Column<TreeListEntity<Person, Membership>>> getInitialSortingColumns() {
        return Arrays.asList(getFirstColumn(), this.periodColumn);
    }

    @Override
    protected SortDirection getInitialSortingDirection(Column<TreeListEntity<Person, Membership>> column) {
        if (column == this.periodColumn) {
            return SortDirection.DESCENDING;
        }
        return SortDirection.ASCENDING;
    }

    @Override
    protected MenuBar createMenuBar() {
        var menu = super.createMenuBar();
        if (menu == null) {
            menu = new MenuBar();
            menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        }

        this.extendsContractButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.EXPAND_SOLID, null, null, it -> extendContractSelection());
        this.extendsContractButton.setEnabled(false);

        return menu;
    }

    private void extendContractSelection() {
        final var selection = this.getGrid().getSelectionModel().getFirstSelectedItem();
        if (selection.isPresent()) {
            final TreeListEntity<Person, Membership> entity = selection.get();
            if (entity.getChildEntity() != null) {
                openExtendContractEditor(this.membershipService.startEditing(entity.getChildEntity(), getLogger()), getTranslation("views.membership.extend_contract_membership", entity.getChildEntity().getPerson().getFullName())); //$NON-NLS-1$;
            }
        }
    }

    /**
     * Show the editor of the contract extension.
     *
     * @param context the editing context.
     * @param title   the title of the editor.
     */
    protected void openExtendContractEditor(AbstractEntityService.EntityEditingContext<Membership> context, String title) {

        var membership = context.getEntity();

        LocalDate date = membership.getMemberToWhen() == null ? membership.getMemberSinceWhen() : membership.getMemberToWhen();

        Dialog dialog = new Dialog(title);
        dialog.setWidth("auto");
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);

        var errorMessage = new Span();
        errorMessage.setVisible(false);
        errorMessage.getStyle().setColor("var(--lumo-error-color)");
        errorMessage.getStyle().set("font-weight", "bold");

        var sinceDatePicker = new DatePicker(getTranslation("views.membership.since"));
        sinceDatePicker.setValue(LocalDate.now());
        sinceDatePicker.setVisible(false);
        sinceDatePicker.getStyle().set("width", "100%");

        var toDatePicker = new DatePicker(getTranslation("views.membership.to"));
        toDatePicker.setValue(date);

        var memberStatusComboBox = new ComboBox<MemberStatus>(getTranslation("views.membership.status"));
        memberStatusComboBox.setItems(MemberStatus.values());
        memberStatusComboBox.setValue(membership.getMemberStatus());
        memberStatusComboBox.setItemLabelGenerator(this::getStatusLabel);
        memberStatusComboBox.setRequired(true);
        memberStatusComboBox.setPrefixComponent(VaadinIcon.DOCTOR.create());
        memberStatusComboBox.getStyle().set("width", "100%");

        // Check if the status has changed, and adapt the UI accordingly
        memberStatusComboBox.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                if (event.getValue() != membership.getMemberStatus()) {
                    sinceDatePicker.setVisible(true);
                    if (toDatePicker.getValue().isBefore(sinceDatePicker.getValue()) || toDatePicker.getValue().isEqual(sinceDatePicker.getValue())) {
                        toDatePicker.setValue(LocalDate.now().plusDays(1));
                    }
                } else if (sinceDatePicker.isVisible()) {
                    sinceDatePicker.setVisible(false);
                }
            }
        });

        HorizontalLayout statusLayout = new HorizontalLayout(memberStatusComboBox, sinceDatePicker);
        statusLayout.setPadding(false);
        statusLayout.setSpacing(true);
        statusLayout.setMaxWidth("100%");

        VerticalLayout dialogLayout = new VerticalLayout(toDatePicker, statusLayout, errorMessage);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("max-width", "100%");
        dialog.add(dialogLayout);

        Button buttonAccept = new Button(getTranslation("views.validate"), event -> {
            membership.setMemberStatus(memberStatusComboBox.getValue());

            // If the status has changed, we need to create a new membership
            if (sinceDatePicker.isVisible()) {
                if (sinceDatePicker.getValue().isBefore(toDatePicker.getValue())) {
                    try {
                        var newMembership = membership.clone();

                        newMembership.setMemberSinceWhen(sinceDatePicker.getValue());
                        newMembership.setMemberToWhen(toDatePicker.getValue());

                        var editor = this.membershipService.startEditing(newMembership, getLogger());
                        editor.save();
                        refreshGrid();
                        dialog.close();
                        ComponentFactory.showSuccessNotification(getTranslation("views.membership.extend_contract_creation_success", newMembership.getPerson().getFullName()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    errorMessage.setText(getTranslation("views.membership.extend_contract_error"));
                    errorMessage.setVisible(true);
                }
            } else {
                if (toDatePicker.getValue().isAfter(date)) {
                    membership.setMemberToWhen(toDatePicker.getValue());
                    try {
                        context.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    refreshGrid();
                    dialog.close();
                    ComponentFactory.showSuccessNotification(getTranslation("views.membership.extend_contract_success", membership.getPerson().getFullName()));
                } else {
                    // Show error message
                    errorMessage.setText(getTranslation("views.membership.extend_contract_error"));
                    errorMessage.setVisible(true);
                }

            }
        });
        var buttonCancel = new Button(getTranslation("views.cancel"), event -> {
            dialog.close();
        });

        dialog.getFooter().add(buttonCancel, buttonAccept);
        dialog.open();
    }

    private String getStatusLabel(MemberStatus status) {
        return status.getLabel(getMessageSourceAccessor(), null, false, getLocale());
    }

    @Override
    protected void onChildSelectionChange(boolean hasOne, boolean hasTwo) {
        super.onChildSelectionChange(hasOne, hasTwo);
        this.extendsContractButton.setEnabled(hasOne && !hasTwo);
    }

    @Override
    protected AbstractFilters<Membership> createFilters() {
        return new MembershipFilters(() -> this.organizationService.getDefaultOrganization(), () -> this.organizationService.getFileManager(), this::refreshGrid);
    }

    @Override
    protected void configureFirstColumn(Column<TreeListEntity<Person, Membership>> rootColumn) {
        super.configureFirstColumn(rootColumn);
    }

    @Override
    protected Component createRootEntityComponent(Person entity) {
        return ComponentFactory.newPersonAvatar(entity);
    }

    @Override
    protected Component createChildEntityComponent(Membership entity) {
        final var person = entity.getPerson();
        final var gender = person == null ? null : person.getGender();
        return new Span(entity.getMemberStatus().getLabel(getMessageSourceAccessor(), gender, false, getLocale()));
    }

    @Override
    protected void createAdditionalColumns(TreeGrid<TreeListEntity<Person, Membership>> grid) {
        this.periodColumn = grid.addColumn(new ComponentRenderer<>(this::getPeriodLabel))
                .setAutoWidth(false);
        this.serviceColumn = grid.addColumn(new ComponentRenderer<>(this::getServiceName))
                .setAutoWidth(true);
        this.employerColumn = grid.addColumn(new ComponentRenderer<>(this::getEmployerName))
                .setAutoWidth(true);
    }

    private Component getPeriodLabel(TreeListEntity<Person, Membership> treeEntity) {
        final var entity = treeEntity.getChildEntity();
        if (entity == null) {
            return new Span();
        }
        final var sdate = entity.getMemberSinceWhen();
        final var edate = entity.getMemberToWhen();
        String periodStr;
        if (sdate == null) {
            if (edate == null) {
                periodStr = ""; //$NON-NLS-1$
            } else {
                periodStr = getTranslation("views.membership.date.to", Integer.toString(edate.getYear())); //$NON-NLS-1$
            }
        } else if (edate == null) {
            periodStr = getTranslation("views.membership.date.since", Integer.toString(sdate.getYear())); //$NON-NLS-1$
        } else {
            final var year0 = sdate.getYear();
            final var year1 = edate.getYear();
            if (year0 == year1) {
                periodStr = getTranslation("views.membership.date.single", Integer.toString(year0)); //$NON-NLS-1$
            } else {
                periodStr = getTranslation("views.membership.date.since_to", Integer.toString(year0), Integer.toString(year1)); //$NON-NLS-1$
            }
        }

        return new Span(periodStr);
    }

    private Component getServiceName(TreeListEntity<Person, Membership> entity) {
        if (entity != null) {
            final var membership = entity.getChildEntity();
            if (membership != null) {
                final var organization = getService(membership);
                if (organization != null) {
                    return ComponentFactory.newOrganizationAvatar(organization, this.organizationService.getFileManager());
                }
            }
        }
        return new Span();
    }

    private Component getEmployerName(TreeListEntity<Person, Membership> entity) {
        if (entity != null) {
            final var membership = entity.getChildEntity();
            if (membership != null) {
                final var organization = getEmployer(membership);
                if (organization != null) {
                    return ComponentFactory.newOrganizationAvatar(organization, this.organizationService.getFileManager());
                }
            }
        }
        return new Span();
    }

    @Override
    protected void addEntity() {
        openMembershipEditor(new Membership(), getTranslation("views.membership.add_membership"), true); //$NON-NLS-1$
    }

    @Override
    protected void editChildEntity(Membership membership) {
        openMembershipEditor(membership, getTranslation("views.membership.edit_membership", membership.getPerson().getFullName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a membership.
     *
     * @param membership the membership to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openMembershipEditor(Membership membership, String title, boolean isCreation) {
        final AbstractEntityEditor<Membership> editor;
        if (isCreation) {
            editor = this.membershipEditorFactory.createAdditionEditor(membership, getLogger(), membership.getPerson() == null);
        } else {
            editor = this.membershipEditorFactory.createUpdateEditor(membership, getLogger(), membership.getPerson() == null);
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Membership> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, Membership> refreshOne = (dialog, entity) -> refreshItem(TreeListEntity.child(entity));
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<Membership> createDeletionContextForChildEntities(Set<Membership> entities) {
        return this.membershipService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        if (this.extendsContractButton != null) {
            ComponentFactory.setIconItemText(this.extendsContractButton, getTranslation("views.extend_contract")); //$NON-NLS-1$
        }

        getFirstColumn().setHeader(getTranslation("views.person")); //$NON-NLS-1$
        this.periodColumn.setHeader(getTranslation("views.period")); //$NON-NLS-1$
        this.serviceColumn.setHeader(getTranslation("views.service")); //$NON-NLS-1$
        this.employerColumn.setHeader(getTranslation("views.employer")); //$NON-NLS-1$
    }

    /**
     * UI and JPA filters for {@link StandardMembershipListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class MembershipFilters extends AbstractDefaultOrganizationDataFilters<Membership> {

        private static final long serialVersionUID = -7866307628748739653L;

        private Checkbox includeTypes;

        /**
         * Constructor.
         *
         * @param defaultOrganizationSupplier the provider of the default organization.
         * @param fileManager                 the manager of files on the server.
         * @param onSearch                    the callback function for running the filtering.
         */
        public MembershipFilters(Supplier<ResearchOrganization> defaultOrganizationSupplier, Supplier<FileManager> fileManager, Runnable onSearch) {
            super(defaultOrganizationSupplier, fileManager, onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeTypes = new Checkbox(true);

            options.add(this.includeTypes);
        }

        @Override
        protected void resetFilters() {
            this.includeTypes.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForDefaultOrganization(Root<Membership> root, CriteriaQuery<?> query,
                                                                 CriteriaBuilder criteriaBuilder, ResearchOrganization defaultOrganization) {
            final var crit1 = criteriaBuilder.equal(root.get("researchOrganization"), defaultOrganization); //$NON-NLS-1$
            final var crit2 = criteriaBuilder.equal(root.get("superResearchOrganization"), defaultOrganization); //$NON-NLS-1$
            final var predicate = criteriaBuilder.or(crit1, crit2);
            // The following code line is mandatory for avoiding the duplicate entries in the results
            query.distinct(true);
            return predicate;
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Membership> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeTypes.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("memberStatus")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeTypes.setLabel(getTranslation("views.filters.include_types")); //$NON-NLS-1$
        }

    }

}
