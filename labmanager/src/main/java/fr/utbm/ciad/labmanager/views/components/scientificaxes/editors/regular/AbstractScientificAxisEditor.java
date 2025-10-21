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

package fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.regular;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Abstract implementation for the editor of the information related to a scientific axis.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractScientificAxisEditor extends AbstractEntityEditor<ScientificAxis> {

    private static final long serialVersionUID = -4153205626114372905L;

    private DetailsWithErrorMark descriptionDetails;

    private TextField acronym;

    private TextField name;

    private DatePicker startDate;

    private DatePicker endDate;

    /**
     * Constructor.
     *
     * @param context                    the editing context for the scientific axis.
     * @param axisCreationStatusComputer the tool for computer the creation status for the scientific axes.
     * @param relinkEntityWhenSaving     indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                   be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser          the connected user.
     * @param messages                   the accessor to the localized messages (Spring layer).
     * @param properties                 specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractScientificAxisEditor(EntityEditingContext<ScientificAxis> context,
                                        EntityCreationStatusComputer<ScientificAxis> axisCreationStatusComputer, boolean relinkEntityWhenSaving,
                                        AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                        ConstructionPropertiesBuilder properties) {
        super(ScientificAxis.class, authenticatedUser, messages,
                axisCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.scientific_axes.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.scientific_axes.administration.validated_address")); //$NON-NLS-1$
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer, it -> it.bind(ScientificAxis::isValidated, ScientificAxis::setValidated));
        }
    }

    /**
     * Create the section for editing the description of the scientific axis.
     *
     * @param rootContainer the container.
     */
    protected void createDescriptionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.acronym = new TextField();
        this.acronym.setPrefixComponent(VaadinIcon.HASH.create());
        this.acronym.setRequired(true);
        this.acronym.setClearButtonVisible(true);
        content.add(this.acronym, 1);

        this.name = new TextField();
        this.name.setPrefixComponent(VaadinIcon.HASH.create());
        this.name.setRequired(true);
        this.name.setClearButtonVisible(true);
        content.add(this.name, 1);

        this.startDate = new DatePicker();
        this.startDate.setPrefixComponent(VaadinIcon.SIGN_IN_ALT.create());
        this.startDate.setClearButtonVisible(true);
        content.add(this.startDate, 1);

        this.endDate = new DatePicker();
        this.endDate.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
        this.endDate.setClearButtonVisible(true);
        content.add(this.endDate, 1);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.scientific_axes.acronym.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.acronym, this.descriptionDetails))
                .bind(ScientificAxis::getAcronym, ScientificAxis::setAcronym);
        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.scientific_axes.name.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.name, this.descriptionDetails))
                .bind(ScientificAxis::getName, ScientificAxis::setName);
        getEntityDataBinder().forField(this.startDate)
                .bind(ScientificAxis::getStartDate, ScientificAxis::setStartDate);
        getEntityDataBinder().forField(this.endDate)
                .bind(ScientificAxis::getEndDate, ScientificAxis::setEndDate);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.scientific_axes.save_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.scientific_axes.validation_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.scientific_axes.delete_success2", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.scientific_axes.save_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.scientific_axes.validation_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.scientific_axes.delete_error2", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.descriptionDetails.setSummaryText(getTranslation("views.scientific_axes.description_details")); //$NON-NLS-1$
        this.acronym.setLabel(getTranslation("views.scientific_axes.acronym")); //$NON-NLS-1$
        this.acronym.setHelperText(getTranslation("views.scientific_axes.acronym.helper")); //$NON-NLS-1$
        this.name.setLabel(getTranslation("views.scientific_axes.name")); //$NON-NLS-1$
        this.name.setHelperText(getTranslation("views.scientific_axes.name.helper")); //$NON-NLS-1$
        this.startDate.setLocale(event.getLocale());
        this.startDate.setLabel(getTranslation("views.scientific_axes.start_date")); //$NON-NLS-1$
        this.startDate.setHelperText(getTranslation("views.scientific_axes.start_date.helper")); //$NON-NLS-1$
        this.endDate.setLocale(event.getLocale());
        this.endDate.setLabel(getTranslation("views.scientific_axes.end_date")); //$NON-NLS-1$
        this.endDate.setHelperText(getTranslation("views.scientific_axes.end_date.helper")); //$NON-NLS-1$
    }

}
