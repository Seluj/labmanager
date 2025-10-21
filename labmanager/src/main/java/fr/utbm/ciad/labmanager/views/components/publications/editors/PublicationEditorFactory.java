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

package fr.utbm.ciad.labmanager.views.components.publications.editors;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import org.slf4j.Logger;

/**
 * Factory that is providing a publication editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface PublicationEditorFactory {

    /**
     * Replies the editing context for the given publication.
     *
     * @param publication the publication to be edited.
     * @param logger      the logger to be associated to the context.
     * @return the editing context.
     */
    EntityEditingContext<Publication> createContextFor(Publication publication, Logger logger);

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param context                   the context for editing the entity.
     * @param initialPublicationStatus  the initial status for the publication.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Publication> createAdditionEditor(EntityEditingContext<Publication> context,
                                                           EntityCreationStatus initialPublicationStatus,
                                                           PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                           String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                           String personNullErrorKey, String personDuplicateErrorKey);

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param context                   the context for editing the entity.
     * @param initialPublicationStatus  the initial status for the publication.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(EntityEditingContext<Publication> context,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey) {
        return createAdditionEditor(context, null, supportedTypes, enableTypeSelector, mandatoryAbstractText,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param context                   the context for editing the entity.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(EntityEditingContext<Publication> context,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey) {
        return createAdditionEditor(context, null, supportedTypes, enableTypeSelector, true,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param context                   the context for editing the entity.
     * @param initialPublicationStatus  the initial status for the publication.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(EntityEditingContext<Publication> context,
                                                                   EntityCreationStatus initialPublicationStatus,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey) {
        return createAdditionEditor(context, initialPublicationStatus, supportedTypes, enableTypeSelector, true,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param publication               the publication to be edited.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(Publication publication,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        final var context = createContextFor(publication, logger);
        return createAdditionEditor(context, null, supportedTypes, enableTypeSelector, mandatoryAbstractText, personCreationLabelKey,
                personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param publication               the publication to be edited.
     * @param initialPublicationStatus  the initial status for the publication.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(Publication publication,
                                                                   EntityCreationStatus initialPublicationStatus,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        final var context = createContextFor(publication, logger);
        return createAdditionEditor(context, initialPublicationStatus, supportedTypes, enableTypeSelector, mandatoryAbstractText, personCreationLabelKey,
                personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param publication               the publication to be edited.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(Publication publication,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        return createAdditionEditor(publication, null, supportedTypes, enableTypeSelector, true,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey,
                personDuplicateErrorKey, logger);
    }

    /**
     * Create an editor that may be used for creating a new publication.
     *
     * @param publication               the publication to be edited.
     * @param initialPublicationStatus  the initial status for the publication.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createAdditionEditor(Publication publication,
                                                                   EntityCreationStatus initialPublicationStatus,
                                                                   PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                   String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        return createAdditionEditor(publication, initialPublicationStatus, supportedTypes, enableTypeSelector, true,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey,
                personDuplicateErrorKey, logger);
    }

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param context                   the context for editing the entity.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Publication> createUpdateEditor(EntityEditingContext<Publication> context,
                                                         PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                         String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                         String personNullErrorKey, String personDuplicateErrorKey);

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param context                   the context for editing the entity.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createUpdateEditor(EntityEditingContext<Publication> context,
                                                                 PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                 String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                 String personNullErrorKey, String personDuplicateErrorKey) {
        return createUpdateEditor(context, supportedTypes, enableTypeSelector, true,
                personCreationLabelKey, personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param publication               the publication to be edited.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText     indicates if the abstract text is considered as mandatory or not.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createUpdateEditor(Publication publication,
                                                                 PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                 String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                 String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        final var context = createContextFor(publication, logger);
        return createUpdateEditor(context, supportedTypes, enableTypeSelector, mandatoryAbstractText, personCreationLabelKey,
                personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
    }

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param publication               the publication to be edited.
     * @param supportedTypes            list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector        indicates if the type selector is enabled or disabled.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param logger                    the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Publication> createUpdateEditor(Publication publication,
                                                                 PublicationType[] supportedTypes, boolean enableTypeSelector,
                                                                 String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                 String personNullErrorKey, String personDuplicateErrorKey, Logger logger) {
        return createUpdateEditor(publication, supportedTypes, enableTypeSelector, true, personCreationLabelKey,
                personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey, logger);
    }

}
