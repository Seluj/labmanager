package fr.utbm.ciad.labmanager.views.components.persons.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.AbstractStreamResource;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.PersonCardDataProvider;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Builder for a card item associated to a person.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class StandardPersonCardGridItemFactory {

    private final MembershipService membershipService;

    private final PersonEditorFactory personEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    private final ChronoMembershipComparator membershipComparator;

    /**
     * Constructor.
     *
     * @param membershipService    the service for accessing the JPA entities of the person memberships.
     * @param personEditorFactory  the factory for creating the person editors.
     * @param authenticatedUser    the connected user.
     * @param membershipComparator the comparator of memberships.
     */
    public StandardPersonCardGridItemFactory(
            @Autowired MembershipService membershipService,
            @Autowired PersonEditorFactory personEditorFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired ChronoMembershipComparator membershipComparator) {
        this.membershipService = membershipService;
        this.personEditorFactory = personEditorFactory;
        this.authenticatedUser = authenticatedUser;
        this.membershipComparator = membershipComparator;
    }

    /**
     * Create a card item.
     *
     * @param person          the person who is associated to the card.
     * @param logger          the logger to be used by the card tools.
     * @param updaterCallback the functional function that is invoked for refreshing the container of this item.
     * @return the item.
     */
    public StandardPersonCardGridItem createCard(Person person, Logger logger, Runnable updaterCallback) {
        final var provider = new CardDataProvider(person, this.membershipService, this.membershipComparator);
        return new StandardPersonCardGridItem(provider, this.personEditorFactory, this.authenticatedUser, logger, updaterCallback);
    }

    /**
     * Create a card item.
     *
     * @param person the person who is associated to the card.
     * @param logger the logger to be used by the card tools.
     * @return the item.
     */
    public StandardPersonCardGridItem createCard(Person person, Logger logger) {
        return createCard(person, logger, null);
    }

    /**
     * Tool for building the specification of a person's card.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class CardDataProvider implements PersonCardDataProvider<Person> {

        private final Person person;

        private final MembershipService membershipService;

        private final Comparator<Membership> membershipComparator;

        /**
         * Constructor.
         *
         * @param person               the associated person.
         * @param membershipService    the service for accessing the membership entities.
         * @param membershipComparator the comparator of memberships for obtaining the role.
         */
        protected CardDataProvider(Person person, MembershipService membershipService, Comparator<Membership> membershipComparator) {
            this.person = person;
            this.membershipService = membershipService;
            this.membershipComparator = membershipComparator;
        }

        @Override
        public AbstractStreamResource getPhotoResource() {
            return null;
        }

        @Override
        public String getPhotoUrl() {
            final var url = this.person.getPhotoURL(ViewConstants.PHOTO_SIZE_IN_PERSON_CARD_GRID);
            if (url != null) {
                return url.toExternalForm();
            }
            return null;
        }

        @Override
        public String getName() {
            return this.person.getFullNameWithLastNameFirst();
        }

        @Override
        public String getRole() {
            return this.membershipService.getRoleForPerson(this.person, this.membershipComparator);
        }

        @Override
        public String getEmail() {
            return this.person.getEmail();
        }

        @Override
        public String getMobilePhone() {
            final var phone = this.person.getMobilePhone();
            if (phone != null) {
                return phone.toInternationalForm();
            }
            return null;
        }

        @Override
        public String getOfficePhone() {
            final var phone = this.person.getOfficePhone();
            if (phone != null) {
                return phone.toInternationalForm();
            }
            return null;
        }

        @Override
        public String getOfficeRoom() {
            return this.person.getOfficeRoom();
        }

        @Override
        public Iterable<Span> getLabels() {
            final var memberships = new MembershipIterator(this.person);
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
                return spans;
            }
            return Collections.emptyList();
        }

        @Override
        public Person getEntity() {
            return this.person;
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
                this.base = CardDataProvider.this.membershipService.getMembershipsForPerson(person.getId()).stream()
                        .filter(it -> !it.isFuture()).sorted(CardDataProvider.this.membershipComparator).iterator();
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

}
