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

package fr.utbm.ciad.labmanager.views.components.assocstructures.fields;

import com.google.common.base.Strings;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolder;
import fr.utbm.ciad.labmanager.data.assostructure.HolderRole;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListGridField;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.HashSet;
import java.util.List;

/**
 * Implementation of a Vaadin component for input a list of associated structure holder using values in a grid row.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AssociatedStructureHolderListGridField extends AbstractEntityListGridField<AssociatedStructureHolder> {

    private static final long serialVersionUID = -3891347566824272385L;

    private final PersonFieldFactory personFieldFactory;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final Logger logger;

    private Column<AssociatedStructureHolder> personColumn;

    private Column<AssociatedStructureHolder> roleColumn;

    private Column<AssociatedStructureHolder> roleDescriptionColumn;

    private Column<AssociatedStructureHolder> organizationColumn;

    private Column<AssociatedStructureHolder> superOrganizationColumn;

    /**
     * Constructor.
     *
     * @param personFieldFactory       the factory for creating the person fields.
     * @param organizationFieldFactory the factory for creating the organization fields.
     * @param messages                 accessor to the localized messages.
     * @param logger                   the logger to be used by the component.
     * @since 4.0
     */
    public AssociatedStructureHolderListGridField(
            PersonFieldFactory personFieldFactory, OrganizationFieldFactory organizationFieldFactory,
            MessageSourceAccessor messages, Logger logger) {
        super(messages, "views.associated_structure.holders.edit"); //$NON-NLS-1$
        this.organizationFieldFactory = organizationFieldFactory;
        this.personFieldFactory = personFieldFactory;
        this.logger = logger;
    }

    @Override
    protected void createColumns(Grid<AssociatedStructureHolder> grid) {
        this.personColumn = grid.addColumn(it -> getPersonValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createPersonEditor);

        this.roleColumn = grid.addColumn(it -> getRoleValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createRoleEditor);

        this.roleDescriptionColumn = grid.addColumn(it -> getRoleDescriptionValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createRoleDescriptionEditor);

        this.organizationColumn = grid.addColumn(it -> getOrganizationValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createOrganizationEditor);

        this.superOrganizationColumn = grid.addColumn(it -> getSuperOrganizationValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createSuperOrganizationEditor);
    }

    @SuppressWarnings("static-method")
    private String getPersonValueLabel(AssociatedStructureHolder holder) {
        if (holder == null) {
            return ""; //$NON-NLS-1$
        }
        final var person = holder.getPerson();
        if (person == null) {
            return ""; //$NON-NLS-1$
        }
        return person.getFullNameWithLastNameFirst();
    }

    private SinglePersonNameField createPersonEditor(AssociatedStructureHolder holder) {
        final var field = this.personFieldFactory.createSingleNameField(getTranslation("views.projects.members.create"), //$NON-NLS-1$
                this.logger);
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(AssociatedStructureHolder::getPerson, AssociatedStructureHolder::setPerson);
        return field;
    }

    private String getRoleValueLabel(AssociatedStructureHolder holder) {
        if (holder == null) {
            return ""; //$NON-NLS-1$
        }
        final var role = holder.getRole();
        if (role == null) {
            return ""; //$NON-NLS-1$
        }
        return role.getLabel(this.messages, getLocale());
    }

    private ComboBox<HolderRole> createRoleEditor(AssociatedStructureHolder holder) {
        final var field = createBaseEnumEditor(HolderRole.class);
        field.setItemLabelGenerator(it -> {
            return it.getLabel(this.messages, getLocale());
        });
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(AssociatedStructureHolder::getRole, AssociatedStructureHolder::setRole);
        return field;
    }

    @SuppressWarnings("static-method")
    private String getRoleDescriptionValueLabel(AssociatedStructureHolder holder) {
        if (holder == null) {
            return ""; //$NON-NLS-1$
        }
        final var description = holder.getRoleDescription();
        return Strings.nullToEmpty(description);
    }

    private TextField createRoleDescriptionEditor(AssociatedStructureHolder holder) {
        final var field = new TextField();
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(AssociatedStructureHolder::getRoleDescription, AssociatedStructureHolder::setRoleDescription);
        return field;
    }

    @SuppressWarnings("static-method")
    private String getOrganizationValueLabel(AssociatedStructureHolder holder) {
        if (holder == null) {
            return ""; //$NON-NLS-1$
        }
        final var organization = holder.getOrganization();
        if (organization == null) {
            return ""; //$NON-NLS-1$
        }
        return organization.getAcronymAndName();
    }

    private SingleOrganizationNameField createOrganizationEditor(AssociatedStructureHolder holder) {
        final var field = this.organizationFieldFactory.createSingleNameField(getTranslation("views.associated_structure.holders.organization.create"), this.logger, null); //$NON-NLS-1$
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(AssociatedStructureHolder::getOrganization, AssociatedStructureHolder::setOrganization);
        return field;
    }

    @SuppressWarnings("static-method")
    private String getSuperOrganizationValueLabel(AssociatedStructureHolder holder) {
        if (holder == null) {
            return ""; //$NON-NLS-1$
        }
        final var organization = holder.getSuperOrganization();
        if (organization == null) {
            return ""; //$NON-NLS-1$
        }
        return organization.getAcronymAndName();
    }

    private SingleOrganizationNameField createSuperOrganizationEditor(AssociatedStructureHolder holder) {
        final var field = this.organizationFieldFactory.createSingleNameField(getTranslation("views.associated_structure.holders.super_organization.create"), this.logger, null); //$NON-NLS-1$
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(AssociatedStructureHolder::getSuperOrganization, AssociatedStructureHolder::setSuperOrganization);
        return field;
    }

    @Override
    protected AssociatedStructureHolder createEntityInstance() {
        return new AssociatedStructureHolder();
    }

    @Override
    public Validator<List<AssociatedStructureHolder>> newStandardValidator() {
        return new StandardValidator();
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.personColumn.setHeader(getTranslation("views.associated_structure.holders.person")); //$NON-NLS-1$
        this.roleColumn.setHeader(getTranslation("views.associated_structure.holders.role")); //$NON-NLS-1$
        this.roleDescriptionColumn.setHeader(getTranslation("views.associated_structure.holders.role_description")); //$NON-NLS-1$
        this.organizationColumn.setHeader(getTranslation("views.associated_structure.holders.organization")); //$NON-NLS-1$
        this.superOrganizationColumn.setHeader(getTranslation("views.associated_structure.holders.super_organization")); //$NON-NLS-1$
    }

    /**
     * Implementation of a Vaadin component for input a list of project members using values in a grid row.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class StandardValidator implements Validator<List<AssociatedStructureHolder>> {

        private static final long serialVersionUID = 4311121308670667317L;

        /**
         * Default Constructor.
         */
        public StandardValidator() {
            //
        }

        @Override
        public ValidationResult apply(List<AssociatedStructureHolder> value, ValueContext context) {
            final var committee = new HashSet<>();
            for (final var member : value) {
                // Check the member
                final var person = member.getPerson();
                if (person == null) {
                    return ValidationResult.error(getTranslation("views.associated_structure.holders.person.error.null")); //$NON-NLS-1$
                }
                // Check for unicity of person
                if (!committee.add(person)) {
                    return ValidationResult.error(getTranslation("views.associated_structure.holders.person.error.duplicate", person.getFullName())); //$NON-NLS-1$
                }
                // Check the role of member
                final var role = member.getRole();
                if (role == null) {
                    return ValidationResult.error(getTranslation("views.associated_structure.holders.role.error.null")); //$NON-NLS-1$
                }
            }
            //
            return ValidationResult.ok();
        }

    }

}
