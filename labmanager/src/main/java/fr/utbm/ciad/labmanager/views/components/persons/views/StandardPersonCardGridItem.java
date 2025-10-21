package fr.utbm.ciad.labmanager.views.components.persons.views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.PersonCardDataProvider;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.slf4j.Logger;

/**
 * Vaadin item for showing person's information in a virtual card that could be included in a grid of cards.
 *
 * @author $Author: callaire$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardPersonCardGridItem extends AbstractPersonCardItem<Person> {

    private static final long serialVersionUID = 8800813211433384109L;

    private final PersonEditorFactory personEditorFactory;

    private final boolean isAdmin;

    private final Logger logger;

    private final Runnable updaterCallback;

    /**
     * Constructor.
     *
     * @param dataProvider        the provider of data.
     * @param personEditorFactory the factory for creating the person editors.
     * @param authenticatedUser   the connected user.
     * @param logger              the logger to be used by the card item.
     * @param updaterCallback     the functional function that is invoked for refreshing the container of this item.
     */
    public StandardPersonCardGridItem(PersonCardDataProvider<Person> dataProvider, PersonEditorFactory personEditorFactory,
                                      AuthenticatedUser authenticatedUser, Logger logger, Runnable updaterCallback) {
        super(dataProvider, true, true, true, true);
        this.personEditorFactory = personEditorFactory;
        this.isAdmin = authenticatedUser != null && authenticatedUser.get().isPresent() && authenticatedUser.get().get().getRole().hasBaseAdministrationRights();
        this.logger = logger;
        this.updaterCallback = updaterCallback;
    }

    /**
     * Replies if this item is used in an administrator context.
     *
     * @return {@code true} if the authenticated user has an administrator role.
     */
    protected boolean isAdminRole() {
        return this.isAdmin;
    }

    @Override
    protected void onClickEvent(Person entity) {
        if (isAdminRole()) {
            openPersonEditor(entity, getTranslation("views.persons.edit_person", entity.getFullName()), false); //$NON-NLS-1$
        }
    }

    /**
     * Open the person editor.
     *
     * @param person     the person to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openPersonEditor(Person person, String title, boolean isCreation) {
        final var editor = this.personEditorFactory.createUpdateEditor(person, this.logger);
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> {
            if (this.updaterCallback != null) {
                this.updaterCallback.run();
            }
        };
        final SerializableBiConsumer<Dialog, Person> refreshOne = (dialog, entity) -> updateCard();
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if it has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

}
