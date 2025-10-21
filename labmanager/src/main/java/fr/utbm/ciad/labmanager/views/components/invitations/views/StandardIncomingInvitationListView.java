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

package fr.utbm.ciad.labmanager.views.components.invitations.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.invitation.PersonInvitationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.invitations.editors.InvitationEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the incoming invitations.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardIncomingInvitationListView extends AbstractEntityListView<PersonInvitation> {

    private static final long serialVersionUID = 4093173516761830625L;

    private final IncomingInvitationDataProvider dataProvider;

    private final InvitationEditorFactory invitationEditorFactory;

    private final PersonInvitationService invitationService;

    private Column<PersonInvitation> inviterColumn;

    private Column<PersonInvitation> universityColumn;

    private Column<PersonInvitation> countryColumn;

    private Column<PersonInvitation> dateColumn;

    private Column<PersonInvitation> guestColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (spring layer).
     * @param loggerFactory           the factory to be used for the composite logger.
     * @param invitationService       the service for accessing the incoming invitations.
     * @param invitationEditorFactory the factory for creating the invitation editors.
     */
    public StandardIncomingInvitationListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            PersonInvitationService invitationService, InvitationEditorFactory invitationEditorFactory) {
        super(PersonInvitation.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.incoming_invitation.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.incoming_invitation.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.incoming_invitation.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.incoming_invitation.delete_error")); //$NON-NLS-1$
        this.invitationEditorFactory = invitationEditorFactory;
        this.invitationService = invitationService;
        this.dataProvider = (ps, query, filters) -> ps.getAllIncomingInvitations(query, filters, this::initializeEntityFromJPA);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private void initializeEntityFromJPA(PersonInvitation entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        Hibernate.initialize(entity.getInviter());
        Hibernate.initialize(entity.getGuest());
    }

    @Override
    protected AbstractFilters<PersonInvitation> createFilters() {
        return new IncomingInvitationFilters(getAuthenticatedUser(), this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<PersonInvitation> grid) {
        this.guestColumn = grid.addColumn(new ComponentRenderer<>(this::createGuestComponent))
                .setAutoWidth(true);
        this.universityColumn = grid.addColumn(inviter -> inviter.getUniversity())
                .setAutoWidth(true)
                .setSortProperty("university"); //$NON-NLS-1$
        this.countryColumn = grid.addColumn(new ComponentRenderer<>(this::createCountryComponent))
                .setAutoWidth(true)
                .setSortProperty("country"); //$NON-NLS-1$
        this.dateColumn = grid.addColumn(this::getDateLabel)
                .setAutoWidth(true)
                .setSortProperty("startDate", "endDate"); //$NON-NLS-1$ //$NON-NLS-2$
        this.inviterColumn = grid.addColumn(new ComponentRenderer<>(this::createInviterComponent))
                .setAutoWidth(true);
        return isAdminRole();
    }

    private Component createCountryComponent(PersonInvitation invitation) {
        final var country = invitation.getCountry();
        if (country != null) {
            final var name = new Span(country.getDisplayCountry(getLocale()));
            name.getStyle().set("margin-left", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$

            final var flag = new CountryFlag(country);
            flag.setSizeFromHeight(1, Unit.REM);

            final var layout = new HorizontalLayout(flag, name);
            layout.setSpacing(false);
            layout.setAlignItems(Alignment.CENTER);

            return layout;
        }
        return new Span();
    }

    private Component createGuestComponent(PersonInvitation invitation) {
        final var guest = invitation.getGuest();
        if (guest != null) {
            return ComponentFactory.newPersonAvatar(guest);
        }
        return new Span();
    }

    private String getDateLabel(PersonInvitation invitation) {
        int year0;
        int year1;
        if (invitation.getStartDate() != null) {
            year0 = invitation.getStartDate().getYear();
            if (invitation.getEndDate() != null) {
                year1 = invitation.getEndDate().getYear();
            } else {
                year1 = year0;
            }
        } else if (invitation.getEndDate() != null) {
            year0 = invitation.getEndDate().getYear();
            year1 = year0;
        } else {
            year0 = LocalDate.now().getYear();
            year1 = year0;
        }
        if (year0 != year1) {
            return getTranslation("views.incoming_invitation.dates", Integer.toString(year0), Integer.toString(year1)); //$NON-NLS-1$
        }
        return Integer.toString(year0);
    }

    private Component createInviterComponent(PersonInvitation invitation) {
        final var inviter = invitation.getInviter();
        if (inviter != null) {
            return ComponentFactory.newPersonAvatar(inviter);
        }
        return new Span();
    }

    @Override
    protected List<Column<PersonInvitation>> getInitialSortingColumns() {
        return Collections.singletonList(this.dateColumn);
    }

    @Override
    protected SortDirection getInitialSortingDirection(Column<PersonInvitation> column) {
        if (column == this.dateColumn) {
            return SortDirection.DESCENDING;
        }
        return SortDirection.ASCENDING;
    }

    @Override
    protected FetchCallback<PersonInvitation, Void> getFetchCallback(AbstractFilters<PersonInvitation> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.invitationService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openInvitationEditor(new PersonInvitation(), getTranslation("views.incoming_invitation.add_invitation"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(PersonInvitation invitation) {
        openInvitationEditor(invitation, getTranslation("views.incoming_invitation.edit_invitation", invitation.getTitle()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a incoming invitation.
     *
     * @param invitation the incoming invitation to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openInvitationEditor(PersonInvitation invitation, String title, boolean isCreation) {
        final AbstractEntityEditor<PersonInvitation> editor;
        if (isCreation) {
            editor = this.invitationEditorFactory.createIncomingInvitationAdditionEditor(invitation, getLogger());
        } else {
            editor = this.invitationEditorFactory.createIncomingInvitationUpdateEditor(invitation, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, PersonInvitation> refreshAll = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.invitationService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshGrid();
        };
        final SerializableBiConsumer<Dialog, PersonInvitation> refreshOne = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.invitationService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshItem(entity);
        };
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<PersonInvitation> createDeletionContextFor(Set<PersonInvitation> entities) {
        return this.invitationService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.inviterColumn.setHeader(getTranslation("views.inviter")); //$NON-NLS-1$
        this.universityColumn.setHeader(getTranslation("views.university")); //$NON-NLS-1$
        this.countryColumn.setHeader(getTranslation("views.country")); //$NON-NLS-1$
        this.dateColumn.setHeader(getTranslation("views.date")); //$NON-NLS-1$
        this.guestColumn.setHeader(getTranslation("views.guest")); //$NON-NLS-1$
    }

    /**
     * Provider of data for incoming invitations to be displayed in the list of invitations view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface IncomingInvitationDataProvider {

        /**
         * Fetch incoming invitations data.
         *
         * @param invitationService the service to have access to the JPA.
         * @param pageRequest       the request for paging the data.
         * @param filters           the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<PersonInvitation> fetch(PersonInvitationService invitationService, PageRequest pageRequest, AbstractFilters<PersonInvitation> filters);

    }

    /**
     * UI and JPA filters for {@link StandardIncomingInvitationListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class IncomingInvitationFilters extends AbstractAuthenticatedUserDataFilters<PersonInvitation> {

        private static final long serialVersionUID = -9189492887286082033L;

        private Checkbox includeInviters;

        private Checkbox includeUniversities;

        private Checkbox includeGuests;

        /**
         * Constructor.
         *
         * @param user     the connected user, or {@code null} if the filter does not care about a connected user.
         * @param onSearch the callback function for running the filtering.
         */
        public IncomingInvitationFilters(AuthenticatedUser user, Runnable onSearch) {
            super(user, onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeInviters = new Checkbox(true);
            this.includeUniversities = new Checkbox(true);
            this.includeGuests = new Checkbox(true);

            options.add(this.includeInviters, this.includeUniversities, this.includeGuests);
        }

        @Override
        protected void resetFilters() {
            this.includeInviters.setValue(Boolean.TRUE);
            this.includeUniversities.setValue(Boolean.TRUE);
            this.includeGuests.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForAuthenticatedUser(Root<PersonInvitation> root, CriteriaQuery<?> query,
                                                               CriteriaBuilder criteriaBuilder, Person user) {
            return criteriaBuilder.equal(root.get("inviter"), user); //$NON-NLS-1$
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<PersonInvitation> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeInviters.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("inviter")), keywords)); //$NON-NLS-1$
            }
            if (this.includeUniversities.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("university")), keywords)); //$NON-NLS-1$
            }
            if (this.includeGuests.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("guest")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeInviters.setLabel(getTranslation("views.filters.include_inviters")); //$NON-NLS-1$
            this.includeUniversities.setLabel(getTranslation("views.filters.include_universities")); //$NON-NLS-1$
            this.includeGuests.setLabel(getTranslation("views.filters.include_guests")); //$NON-NLS-1$
        }

    }

}
