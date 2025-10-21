package fr.utbm.ciad.labmanager.views.appviews.persons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import fr.utbm.ciad.labmanager.views.components.persons.views.AbstractPersonCardView;
import fr.utbm.ciad.labmanager.views.components.persons.views.StandardPersonCardGridItemFactory;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * View for showing person's information in a virtual card that could be included in a grid of cards.
 *
 * @author $Author: callaire$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "persons_cards", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class PersonsCardView extends AbstractPersonCardView implements HasDynamicTitle {

    private static final long serialVersionUID = -5194134794074230569L;

    /**
     * Constructor.
     *
     * @param loggerFactory       the factory of the loggers.
     * @param organizationService the service for accessing the JPA entities of the research organizations.
     * @param personService       the service for accessing the JPA entities of the persons.
     * @param personEditorFactory the factory for creating the person editors.
     * @param cardFactory         the factory for creating the person cards.
     * @param authenticatedUser   the user who is connected to the application.
     */
    public PersonsCardView(
            @Autowired ResearchOrganizationService organizationService,
            @Autowired PersonService personService,
            @Autowired PersonEditorFactory personEditorFactory,
            @Autowired StandardPersonCardGridItemFactory cardFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired ContextualLoggerFactory loggerFactory) {
        super(
                ViewConstants.DEFAULT_PERSON_CARDS_PER_ROW,
                ViewConstants.DEFAULT_ROWS_IN_PERSON_CARD_GRID,
                0,
                organizationService, personService, personEditorFactory,
                cardFactory, authenticatedUser, loggerFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.list_title.all"); //$NON-NLS-1$
    }

    @Override
    protected Class<? extends Component> getListViewType() {
        return PersonsListView.class;
    }

}
