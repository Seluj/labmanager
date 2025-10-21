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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

/**
 * Standard implementation of a list of persons.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public class StandardPersonListView extends AbstractEntityListView<Person> {

    private static final long serialVersionUID = 7976104010438012270L;

    private final PersonService personService;

    private final UserService userService;

    private final ResearchOrganizationService organizationService;

    private final MembershipService membershipService;

    private final ChronoMembershipComparator membershipComparator;

    private final PersonDataProvider dataProvider;

    private final PersonEditorFactory personEditorFactory;

    private Map<Long, User> personIdToUserMap = new HashMap<>();

    private Column<Person> validationColumn;

    private Column<Person> nameColumn;

    private Column<Person> orcidColumn;

    private Column<Person> organizationColumn;

    /**
     * Constructor.
     *
     * @param personService        the service for accessing to the persons.
     * @param userService          the service for accessing to the users.
     * @param organizationService  the service for accessing to the research organizations.
     * @param membershipService    the service for accessing to the memberships.
     * @param membershipComparator the comparator that must be used for comparing the memberships. It is assumed that
     *                             the memberships are sorted in reverse chronological order first.
     * @param dataProvider         the provider of lazy data.
     * @param personEditorFactory  the factory for creating the person editors.
     * @param authenticatedUser    the connected user.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     */
    public StandardPersonListView(
            PersonService personService, UserService userService, MembershipService membershipService,
            ResearchOrganizationService organizationService, ChronoMembershipComparator membershipComparator,
            PersonDataProvider dataProvider, PersonEditorFactory personEditorFactory,
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
            ContextualLoggerFactory loggerFactory) {
        super(Person.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.persons.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.persons.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.persons.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.persons.delete_error")); //$NON-NLS-1$
        this.personService = personService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.membershipService = membershipService;
        this.membershipComparator = membershipComparator;
        this.dataProvider = dataProvider;
        this.personEditorFactory = personEditorFactory;
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
        refreshUsers();
    }

    @Override
    protected AbstractFilters<Person> createFilters() {
        return new PersonFilters(() -> this.organizationService.getDefaultOrganization(), () -> this.organizationService.getFileManager(), this::refreshGrid);
    }

    @SuppressWarnings("static-method")
    private Component createOrganizationComponent(Gender personGender, Iterator<Membership> memberships) {
        if (memberships.hasNext()) {
            final var spans = new ArrayList<Span>();
            while (memberships.hasNext()) {
                final var mbr = memberships.next();
                final var organization = mbr.getDirectResearchOrganization();
                final var name = organization.getAcronymOrName();
                var span = new Span(name);
                if (mbr.isFormer()) {
                    BadgeState.CONTRAST_PILL.assignTo(span);
                } else {
                    BadgeState.SUCCESS_PILL.assignTo(span);
                }
                spans.add(span);
            }
            if (!spans.isEmpty()) {
                return new Span(spans.toArray(new Span[spans.size()]));
            }
        }
        return new Span();
    }

    private Component createOrganizationComponent(Person person) {
        return createOrganizationComponent(person.getGender(), new MembershipIterator(person));
    }

    private Component createNameComponent(Person person) {
        final var avatar = ComponentFactory.newPersonAvatar(person, this.personIdToUserMap.get(Long.valueOf(person.getId())),
                (login, role) -> getTranslation("views.persons.person_details.login", //$NON-NLS-1$
                        login,
                        role.getLabel(getMessageSourceAccessor(), getLocale())));
        return avatar;
    }

    @Override
    protected void addEntity() {
        openPersonEditor(new Person(), getTranslation("views.persons.add_person"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(Person person) {
        openPersonEditor(person, getTranslation("views.persons.edit_person", person.getFullName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a person.
     *
     * @param person     the person to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor must be opened for creating an entity or editing an entity.
     */
    protected void openPersonEditor(Person person, String title, boolean isCreation) {
        final AbstractEntityEditor<Person> editor;
        if (isCreation) {
            editor = this.personEditorFactory.createAdditionEditor(person, getLogger());
        } else {
            editor = this.personEditorFactory.createUpdateEditor(person, getLogger());
        }
        openPersonEditor(editor, title);
    }

    /**
     * Open the person editor.
     *
     * @param editor the editor to open
     * @param title  the title of the editor
     */
    private void openPersonEditor(AbstractEntityEditor<Person> editor, String title) {
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, Person> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<Person> createDeletionContextFor(Set<Person> entities) {
        final var personContext = this.personService.startDeletion(entities, getLogger());
        return this.userService.startDeletion(personContext);
    }

    @Override
    protected boolean createGridColumns(Grid<Person> grid) {
        this.nameColumn = grid.addColumn(it -> it.getFullNameWithLastNameFirst())
                .setRenderer(new ComponentRenderer<>(this::createNameComponent))
                .setAutoWidth(true)
                .setFrozen(true)
                .setSortProperty("lastName", "firstName"); //$NON-NLS-1$ //$NON-NLS-2$
        this.orcidColumn = grid.addColumn(person -> person.getORCID())
                .setAutoWidth(true)
                .setSortProperty("orcid"); //$NON-NLS-1$
        this.organizationColumn = grid.addColumn(person -> person)
                .setRenderer(new ComponentRenderer<>(this::createOrganizationComponent))
                .setAutoWidth(true)
                .setSortable(false);
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
    protected FetchCallback<Person, Void> getFetchCallback(AbstractFilters<Person> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.personService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected List<Column<Person>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    /**
     * Refresh the user list.
     */
    private void refreshUsers() {
        this.personIdToUserMap = new HashMap<>();
        for (final var user : this.userService.getAllUsers()) {
            final Person person = user.getPerson();
            this.personIdToUserMap.put(Long.valueOf(person.getId()), user);
        }
    }

    /**
     * Refresh the user for the given person.
     *
     * @param person the person to search for.
     */
    private void refreshUser(Person person) {
        final var id = Long.valueOf(person.getId());
        final var user = this.userService.getUserFor(person);
        if (user == null) {
            this.personIdToUserMap.remove(id);
        } else {
            this.personIdToUserMap.put(id, user);
        }
    }

    @Override
    protected void refreshGrid() {
        refreshUsers();
        super.refreshGrid();
    }

    @Override
    protected void refreshItem(Person item) {
        refreshUser(item);
        super.refreshItem(item);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.orcidColumn.setHeader(getTranslation("views.orcid")); //$NON-NLS-1$
        this.organizationColumn.setHeader(getTranslation("views.organizations")); //$NON-NLS-1$
    }

    /**
     * Provider of data for persons to be displayed in the list of persons view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface PersonDataProvider {

        /**
         * Fetch person data.
         *
         * @param personService the service to have access tothe JPA.
         * @param pageRequest   the request for paging the data.
         * @param filters       the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<Person> fetch(PersonService personService, PageRequest pageRequest, AbstractFilters<Person> filters);

    }

    /**
     * Membership iterator for the person list view.
     * This iterator assumes that the memberships are sorted according to a {@link ChronoMembershipComparator}
     * and it stops as soon as all the active memberships are returned, or if there is none, when the first
     * former memberships is returned. Future memberships are not considered.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private class MembershipIterator implements Iterator<Membership> {

        private final Iterator<Membership> base;

        private boolean foundActive;

        private Membership next;

        private MembershipIterator(Person person) {
            this.base = StandardPersonListView.this.membershipService.getMembershipsForPerson(person.getId()).stream()
                    .filter(it -> !it.isFuture()).sorted(StandardPersonListView.this.membershipComparator).iterator();
            searchNext();
        }

        private void searchNext() {
            this.next = null;
            if (this.base.hasNext()) {
                final var mbr = this.base.next();
                if (!mbr.isFormer() || !this.foundActive) {
                    this.foundActive = true;
                    this.next = mbr;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Membership next() {
            final var currentNext = this.next;
            searchNext();
            return currentNext;
        }

    }

}
