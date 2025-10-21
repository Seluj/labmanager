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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinService;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionProperties;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.DelegateContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.users.UserIdentityChangedObserver;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

/**
 * Abstract implementation for the editor of the information related to an entity.
 *
 * @param <T> the type of the edited entity.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractEntityEditor<T extends IdentifiableEntity> extends AbstractLoggerComposite<VerticalLayout> implements LocaleChangeObserver {

    /**
     * Key of the property that is the key in the translation file that corresponds to the label of the administration section.
     */
    public static final String PROP_ADMIN_SECTION = "administationSectionTranslationKey"; //$NON-NLS-1$

    /**
     * Key of the property that is the key in the translation file that corresponds to the label of the validation checkbox.
     */
    public static final String PROP_ADMIN_VALIDATION_BOX = "validationTranslationKey"; //$NON-NLS-1$

    private static final long serialVersionUID = -9123030449423137764L;

    private final Class<T> entityType;

    private final MessageSourceAccessor messages;

    private final AuthenticatedUser authenticatedUser;

    private final Binder<T> entityBinder;
    private final boolean isBaseAdmin;
    private final boolean isAdvancedAdmin;
    private final EntityCreationStatusComputer<T> entityCreationStatusComputer;
    private final EntityEditingContext<T> editingContext;
    private final ConstructionProperties properties;
    private final boolean relinkEntityWhenSaving;
    private Details administrationDetails;
    private ToggleButton validatedEntity;
    private EntityCreationStatus status;

    /**
     * Constructor.
     *
     * @param entityType                   the type of the edited entity.
     * @param authenticatedUser            the connected user.
     * @param messages                     the accessor to the localized messages (Spring layer).
     * @param entityCreationStatusComputer the tool for computer the creation status for the entity.
     * @param editingContext               the context that is used for representing the edited entity and all the associated files and entities.
     * @param initialEntityStatus          the initial status of the entity.
     * @param relinkEntityWhenSaving       indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param properties                   specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractEntityEditor(Class<T> entityType, AuthenticatedUser authenticatedUser,
                                MessageSourceAccessor messages,
                                EntityCreationStatusComputer<T> entityCreationStatusComputer,
                                EntityEditingContext<T> editingContext,
                                EntityCreationStatus initialEntityStatus,
                                boolean relinkEntityWhenSaving,
                                ConstructionPropertiesBuilder properties) {
        super(new DelegateContextualLoggerFactory(editingContext.getLogger()));
        this.entityType = entityType;
        this.properties = properties.build();
        this.messages = messages;
        this.entityBinder = createBinder(this.entityType);
        this.authenticatedUser = authenticatedUser;
        this.entityCreationStatusComputer = entityCreationStatusComputer == null ? EntityCreationStatusComputer.getNoErrorEntityCreationStatusComputer() : entityCreationStatusComputer;
        this.editingContext = editingContext;
        this.status = initialEntityStatus;
        this.relinkEntityWhenSaving = relinkEntityWhenSaving;

        if (this.authenticatedUser != null && this.authenticatedUser.get().isPresent()) {
            final var role = this.authenticatedUser.get().get().getRole();
            this.isBaseAdmin = role.hasBaseAdministrationRights();
            this.isAdvancedAdmin = role.hasAdvancedAdministrationRights();
        } else {
            this.isBaseAdmin = false;
            this.isAdvancedAdmin = false;
        }
        this.entityBinder.addValueChangeListener(it -> clearEntityCreationStatus());
    }

    /**
     * Replies the construction properties.
     *
     * @return the properties.
     */
    protected final ConstructionProperties getProperties() {
        return this.properties;
    }

    /**
     * Create the instance of the binder for the provided type.
     *
     * @param type the type of the entities to be binded.
     * @return the binder instance.
     */
    protected Binder<T> createBinder(Class<T> type) {
        return new Binder<>(type);
    }

    /**
     * Replies the context that represents the editied entity and the associated files and entities.
     *
     * @return the editing context.
     */
    public EntityEditingContext<T> getEditingContext() {
        return this.editingContext;
    }

    /**
     * Replies if the data inside the editor is valid.
     *
     * @return {@code true} if the fields have passed all the validation checks.
     */
    public boolean isValidData() {
        return getEntityDataBinder().validate().isOk();
    }

    /**
     * Replies if the data inside the editor is already in the database or not with a similarity error.
     *
     * @return the import error.
     */
    public final EntityCreationStatus getEntityCreationStatus() {
        if (this.status == null) {
            this.status = this.entityCreationStatusComputer.computeEntityCreationStatusFor(getEditedEntity());
        }
        return this.status;
    }

    /**
     * Clear any value for the entity creation status.
     *
     * @see #getEntityCreationStatus()
     */
    protected final void clearEntityCreationStatus() {
        this.status = null;
    }

    /**
     * Recompute if the data inside the editor is already in the database or not with a similarity error.
     * This function is equivalent to a call to {@link #clearEntityCreationStatus()} and
     * {@link #getEntityCreationStatus()}.
     *
     * @return the import error.
     */
    public final EntityCreationStatus resetEntityCreationStatus() {
        clearEntityCreationStatus();
        return getEntityCreationStatus();
    }

    /**
     * Replies if the editor is launched by an user with base administration rights.
     *
     * @return {@code true} if the user has base administration rights.
     */
    public boolean isBaseAdmin() {
        return this.isBaseAdmin;
    }

    /**
     * Replies if the editor is launched by an user with advanced administration rights.
     *
     * @return {@code true} if the user has advanced administration rights.
     */
    public boolean isAdvancedAdmin() {
        return this.isAdvancedAdmin;
    }

    /**
     * Replies the accessor to the localized strings.
     *
     * @return the accessor.
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        return this.messages;
    }

    /**
     * Replies the binder between the data provider and the fields.
     *
     * @return the entity binder.
     */
    protected Binder<T> getEntityDataBinder() {
        return this.entityBinder;
    }

    /**
     * Replies the authenticated user.
     *
     * @return the user.
     */
    protected AuthenticatedUser getAuthenticatedUser() {
        return this.authenticatedUser;
    }

    /**
     * Replies the edited entity.
     *
     * @return the edited entity, never {@code null}.
     */
    public T getEditedEntity() {
        return getEditingContext().getEntity();
    }

    /**
     * Replies if the edited entity is new for the JPA infrastructure.
     *
     * @return {@code true} if the entity has no JPA identifier.
     */
    public boolean isNewEntity() {
        final var entity = getEditedEntity();
        return entity == null || entity.getId() == 0L;
    }

    /**
     * Replies the type of the edited entity.
     *
     * @return the entity type.
     */
    protected Class<T> getEntityType() {
        return this.entityType;
    }

    /**
     * Create the editor content and links the beans
     */
    protected final void createEditorContentAndLinkBeans() {
        final var rootContainer = getContent();
        rootContainer.setSpacing(false);
        createEditorContent(rootContainer);
        linkBeans();
    }

    /**
     * Link the beans to the editor.
     */
    protected void linkBeans() {
        getEntityDataBinder().setBean(getEditedEntity());
    }

    /**
     * Unlink the beans to the editor.
     *
     * @param saveInEntity indicates if the field values must be written in the entity.
     */
    protected void unlinkBeans(boolean saveInEntity) {
        final var binder = getEntityDataBinder();
        if (saveInEntity && binder.hasChanges()) {
            binder.writeBeanAsDraft(binder.getBean(), true);
        }
        binder.setBean(null);
    }

    /**
     * Create the content of the editor.
     * This function should invoke {@link #createAdministrationComponents(VerticalLayout, Consumer, Consumer)}.
     *
     * @param rootContainer the container.
     * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer)
     */
    protected abstract void createEditorContent(VerticalLayout rootContainer);

    /**
     * Create the components for administrator.
     *
     * @param receiver         the receiver of the component
     * @param builderCallback  the callback that is invoked to fill the administration form.
     * @param validationBinder invoked to bind the validation attributes of the entity.
     * @see #createAdministrationComponents(VerticalLayout, Consumer)
     */
    protected void createAdministrationComponents(VerticalLayout receiver,
                                                  Consumer<FormLayout> builderCallback,
                                                  Consumer<BindingBuilder<T, Boolean>> validationBinder) {
        final var content = ComponentFactory.newColumnForm(1);

        if (builderCallback != null) {
            builderCallback.accept(content);
        }

        if (validationBinder != null) {
            this.validatedEntity = new ToggleButton();
            content.add(this.validatedEntity);
            validationBinder.accept(this.entityBinder.forField(this.validatedEntity));
        }

        if (content.getElement().getChildCount() > 0) {
            this.administrationDetails = new Details("", content); //$NON-NLS-1$
            this.administrationDetails.setOpened(false);
            this.administrationDetails.addThemeVariants(DetailsVariant.FILLED);
            receiver.add(this.administrationDetails);
        }
    }

    /**
     * Create the components for administrator.
     *
     * @param builderCallback  the callback that is invoked to fill the administration form.
     * @param validationBinder invoked to bind the validation attributes of the entity.
     * @see #createAdministrationComponents(VerticalLayout, Consumer)
     */
    protected VerticalLayout createAdministrationComponents(Consumer<FormLayout> builderCallback,
                                                            Consumer<BindingBuilder<T, Boolean>> validationBinder) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(1);

        if (builderCallback != null) {
            builderCallback.accept(content);
        }

        if (validationBinder != null) {
            this.validatedEntity = new ToggleButton();
            content.add(this.validatedEntity);
            validationBinder.accept(this.entityBinder.forField(this.validatedEntity));
        }

		/*
		if (content.getElement().getChildCount() > 0) {
			this.administrationDetails = new Details("", content); //$NON-NLS-1$
			this.administrationDetails.setOpened(false);
			this.administrationDetails.addThemeVariants(DetailsVariant.FILLED);
			content.add(this.administrationDetails);
		}
		*/
        verticalLayout.add(content);
        return verticalLayout;
    }

    /**
     * Create the components for administrator with additional component.
     *
     * @param receiver         the receiver of the component
     * @param validationBinder invoked to bind the validation attributes of the entity.
     * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer)
     */
    protected final void createAdministrationComponents(VerticalLayout receiver, Consumer<BindingBuilder<T, Boolean>> validationBinder) {
        createAdministrationComponents(receiver, null, validationBinder);
    }


    /**
     * Invoked for validating the entity by an organizational structure manager. This function does not save.
     * This function must be overridden by the child class that need to do a specific
     * saving process.
     *
     * @see #doValidate(IdentifiableEntity)
     */
    public final void validateByOrganizationalStructureManager() {
        doValidate(getEditedEntity());
    }

    /**
     * Invoked for validating the entity. This function does not save.
     * This function must be overridden by the child class that need to do a specific
     * saving process.
     *
     * @param entity the entity to validate.
     */
    protected void doValidate(T entity) {
        try {
            this.validatedEntity.setValue(Boolean.TRUE);
            notifyValidated();
        } catch (Throwable ex) {
            notifyValidationError(ex);
        }
    }

    /**
     * Notify the user that the entity was validated.
     */
    public final void notifyValidated() {
        notifyValidated(computeValidationSuccessMessage());
    }

    /**
     * Compute the message for validation successs.
     *
     * @return the message.
     */
    protected abstract String computeValidationSuccessMessage();

    /**
     * Notify the user that the entity was validated.
     *
     * @param message the validation message to show to the user.
     */
    @SuppressWarnings("static-method")
    protected void notifyValidated(String message) {
        ComponentFactory.showSuccessNotification(message);
    }

    /**
     * Notify the user that its personal information has changed.
     */
    protected void notifyAuthenticatedUserChange() {
        UserIdentityChangedObserver app = null;
        final var view = UI.getCurrent().getCurrentView();
        if (view != null) {
            app = view.findAncestor(UserIdentityChangedObserver.class);
        }
        if (app != null) {
            app.authenticatedUserIdentityChange();
        }
        ComponentFactory.showInfoNotification(getTranslation("views.authenticated_user_change")); //$NON-NLS-1$
    }

    /**
     * Notify the user that the form contains invalid information.
     */
    public void notifyInvalidity() {
        ComponentFactory.showWarningNotification(getTranslation("views.save_invalid_data")); //$NON-NLS-1$
    }

    /**
     * Notify the user that the form is blocked because it contains information that is considered as similar to an information that is already in the database.
     *
     * @param message the explanation of the error.
     * @since 4.0
     */
    @SuppressWarnings("static-method")
    public void notifySimilarityError(String message) {
        ComponentFactory.showErrorNotification(message);
    }

    /**
     * Notify the user that the form contains information that is considered as similar to an information that is already in the database.
     *
     * @param message the explanation of the error.
     * @since 4.0
     */
    @SuppressWarnings("static-method")
    public void notifySimilarityWarning(String message) {
        ComponentFactory.showWarningNotification(message);
    }

    /**
     * Notify the user that the entity cannot be validated.
     *
     * @param error the error.
     */
    public final void notifyValidationError(Throwable error) {
        notifyValidationError(error, computeValidationErrorMessage(error));
    }

    /**
     * Compute the message for validation error.
     *
     * @param error the error.
     * @return the message.
     */
    protected abstract String computeValidationErrorMessage(Throwable error);

    /**
     * Notify the user that the entity cannot be validated.
     *
     * @param message the message to show up to the user.
     */
    protected void notifyValidationError(Throwable error, String message) {
        getLogger().warn("Error when validating the entity: " //$NON-NLS-1$
                + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$
        ComponentFactory.showErrorNotification(message);
    }

    /**
     * Invoked for saving the entity. This function must be overridden by the child class that need to do a specific
     * saving process.
     *
     * @throws Exception if saving cannot be done.
     */
    protected void doSave() throws Exception {
        getEditingContext().save();
    }

    /**
     * Invoked for saving the entity.
     *
     * @return {@code true} if the saving was a success.
     * @see #doSave()
     */
    public final boolean save() {
        try {
            doSave();
            // Relink the beans for ensuring the editor has
            // the last instance of the entity instance
            // that may have changed due to the saving process
            if (this.relinkEntityWhenSaving) {
                linkBeans();
            }
            notifySaved();
            return true;
        } catch (Throwable ex) {
            notifySavingError(ex);
        }
        return false;
    }

    /**
     * Notify the user that the entity was saved.
     */
    public final void notifySaved() {
        notifySaved(computeSavingSuccessMessage());
    }

    /**
     * Compute the message for saving success.
     *
     * @return the message.
     */
    protected abstract String computeSavingSuccessMessage();

    /**
     * Notify the user that the entity was saved.
     *
     * @param message the message to show up to the user.
     */
    protected void notifySaved(String message) {
        getLogger().info("Data successfully saved: " + message); //$NON-NLS-1$
        ComponentFactory.showSuccessNotification(message);
    }

    /**
     * Notify the user that the entity cannot be saved.
     *
     * @param error the error.
     */
    public final void notifySavingError(Throwable error) {
        notifySavingError(error, computeSavingErrorMessage(error));
    }

    /**
     * Compute the message for saving error.
     *
     * @param error the error.
     * @return the message.
     */
    protected abstract String computeSavingErrorMessage(Throwable error);

    /**
     * Notify the user that the entity cannot be saved.
     *
     * @param message the message to show up to the user.
     */
    protected void notifySavingError(Throwable error, String message) {
        getLogger().warn("Error when saving entity data: " + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$ //$NON-NLS-2$
        ComponentFactory.showErrorNotification(message);
    }

    /**
     * Invoked for preparing the deletion of the entity. This function must be overridden by the child class that need to do a specific
     * deletion process.
     *
     * @return the context for deleting the entity.
     */
    protected EntityDeletingContext<T> prepareDeletion() {
        return getEditingContext().startDeletion();
    }

    /**
     * Invoked for deleting the entity without querying the user.
     *
     * @return {@code true} if the deletion was a success.
     * @see #doDelete(EntityDeletingContext)
     */
    public final boolean delete() {
        try {
            final var context = prepareDeletion();
            assert context != null;
            if (context.isDeletionPossible()) {
                doDelete(context);
                notifyDeleted();
                // Unlink after notifying for having the capability to get the name of the deleted entity
                unlinkBeans(false);
                return true;
            }
            var ex = context.getDeletionStatus().getException(getMessageSourceAccessor(), getLocale());
            if (ex == null) {
                ex = new IllegalStateException();
            }
            notifyDeletionError(ex);
        } catch (Throwable ex) {
            notifyDeletionError(ex);
        }
        return false;
    }

    /**
     * Invoked for deleting the entity thourhg the provided context. This function must be overridden by the child class that need to do a specific
     * deletion process.
     *
     * @param deletionContext the context of deletion.
     * @throws Exception if saving cannot be done.
     */
    protected void doDelete(EntityDeletingContext<T> deletionContext) throws Exception {
        deletionContext.delete();
    }

    /**
     * Notify the user that the entity was deleted.
     */
    public final void notifyDeleted() {
        notifyDeleted(computeDeletionSuccessMessage());
    }

    /**
     * Compute the message for deletion success.
     *
     * @return the message.
     */
    protected abstract String computeDeletionSuccessMessage();

    /**
     * Notify the user that the entity was deleted.
     *
     * @param message the message to show up to the user.
     */
    protected void notifyDeleted(String message) {
        getLogger().info("Data successfully deleted: " + message); //$NON-NLS-1$
        ComponentFactory.showSuccessNotification(message);
    }

    /**
     * Notify the user that the entity cannot be deleted.
     *
     * @param error the error.
     */
    public final void notifyDeletionError(Throwable error) {
        notifyDeletionError(error, computeDeletionErrorMessage(error));
    }

    /**
     * Compute the error message for deletion.
     *
     * @param error the error.
     * @return the message.
     */
    protected abstract String computeDeletionErrorMessage(Throwable error);

    /**
     * Notify the user that the entity cannot be deleted.
     *
     * @param message the message to show up to the user.
     */
    protected void notifyDeletionError(Throwable error, String message) {
        getLogger().warn("Error when deleting entity data: " + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$ //$NON-NLS-2$
        ComponentFactory.showErrorNotification(message);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        final var props = getProperties();
        if (this.administrationDetails != null) {
            this.administrationDetails.setSummaryText(getTranslation(props.get(PROP_ADMIN_SECTION)));
        }
        if (this.validatedEntity != null) {
            this.validatedEntity.setLabel(getTranslation(props.get(PROP_ADMIN_VALIDATION_BOX)));
        }
    }

    /**
     * Create a "details" section that may be marked with error.
     *
     * @param container the container of the section.
     * @param content   the content of the section.
     * @param sectionId the identifier of the section in the editor.
     * @return the section container.
     * @see #createDetails(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected DetailsWithErrorMark createDetailsWithErrorMark(HasComponents container, Component content, String sectionId) {
        return createDetailsWithErrorMark(container, content, sectionId, false);
    }

    /**
     * Create a "details" section that may be marked with error.
     *
     * @param container     the container of the section.
     * @param content       the content of the section.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @return the section container.
     * @see #createDetails(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected DetailsWithErrorMark createDetailsWithErrorMark(HasComponents container, Component content, String sectionId, boolean openByDefault) {
        return createDetailsWithErrorMark(container, content, sectionId, openByDefault, false);
    }

    /**
     * Create a "details" section that may be marked with error.
     *
     * @param container     the container of the section.
     * @param content       the content of the section.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @param forceDefault  indicates if the default value must be forced.
     * @return the section container.
     * @see #createDetails(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected DetailsWithErrorMark createDetailsWithErrorMark(HasComponents container, Component content, String sectionId, boolean openByDefault, boolean forceDefault) {
        final var details = new DetailsWithErrorMark(content);
        if (container != null) {
            container.add(details);
        }
        initializeDetails(details, sectionId, openByDefault, forceDefault);
        return details;
    }

    /**
     * Create a "details" section.
     *
     * @param container the container of the section.
     * @param content   the content of the section.
     * @param sectionId the identifier of the section in the editor.
     * @return the section container.
     * @see #createDetailsWithErrorMark(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected Details createDetails(HasComponents container, Component content, String sectionId) {
        return createDetails(container, content, sectionId, false);
    }

    /**
     * Create a "details" section.
     *
     * @param container     the container of the section.
     * @param content       the content of the section.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @return the section container.
     * @see #createDetailsWithErrorMark(HasComponents, Component, String)
     * @see #createDetails(HasComponents, Component, String, boolean, boolean)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected Details createDetails(HasComponents container, Component content, String sectionId, boolean openByDefault) {
        return createDetails(container, content, sectionId, openByDefault, false);
    }

    /**
     * Create a "details" section.
     *
     * @param container     the container of the section.
     * @param content       the content of the section.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @param forceDefault  indicates if the default value must be forced.
     * @return the section container.
     * @see #createDetailsWithErrorMark(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean)
     */
    protected Details createDetails(HasComponents container, Component content, String sectionId, boolean openByDefault, boolean forceDefault) {
        final var details = new Details("", content); //$NON-NLS-1$
        if (container != null) {
            container.add(details);
        }
        initializeDetails(details, sectionId, openByDefault, forceDefault);
        return details;
    }

    /**
     * Initialize a "details" section.
     *
     * @param details       the component to initialize.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @see #createDetailsWithErrorMark(HasComponents, Component, String)
     * @see #createDetails(HasComponents, Component, String)
     * @see #initializeDetails(Details, String, boolean, boolean)
     */
    protected void initializeDetails(Details details, String sectionId, boolean openByDefault) {
        initializeDetails(details, sectionId, openByDefault, false);
    }

    /**
     * Initialize a "details" section.
     *
     * @param details       the component to initialize.
     * @param sectionId     the identifier of the section in the editor.
     * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
     * @param forceDefault  indicates if the default value must be forced.
     * @see #createDetailsWithErrorMark(HasComponents, Component, String)
     * @see #createDetails(HasComponents, Component, String)
     */
    protected void initializeDetails(Details details, String sectionId, boolean openByDefault, boolean forceDefault) {
        final var key = new StringBuilder().append(ViewConstants.DETAILS_SECTION_OPENING_ROOT).append(getClass().getName());
        final var sectionKey = key.toString();
        final var attributeKey = key.append('.').append(sectionId).toString();

        final var request = VaadinService.getCurrentRequest();
        final var session = request != null ? request.getWrappedSession() : null;

        if (forceDefault) {
            details.setOpened(openByDefault);
        } else {
            if (session == null || session.getAttribute(sectionKey) == null) {
                // Section was never defined. Use the default configuration.
                details.setOpened(openByDefault);
            } else {
                // Section was defined. Use the stored configuration.
                final var value = session.getAttribute(attributeKey);
                if (value instanceof Boolean bvalue) {
                    details.setOpened(bvalue.booleanValue());
                } else if (value instanceof String svalue) {
                    details.setOpened(Boolean.parseBoolean(svalue));
                } else {
                    details.setOpened(openByDefault);
                }
            }
        }

        details.addOpenedChangeListener(event -> {
            final var session0 = VaadinService.getCurrentRequest().getWrappedSession();
            session0.setAttribute(sectionKey, Boolean.TRUE.toString());
            session0.setAttribute(attributeKey, Boolean.toString(event.isOpened()));
        });
    }

}
