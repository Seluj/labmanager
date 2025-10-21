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

package fr.utbm.ciad.labmanager.views.components.projects.fields;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.project.ProjectMember;
import fr.utbm.ciad.labmanager.data.project.Role;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListGridField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.HashSet;
import java.util.List;

/**
 * Implementation of a Vaadin component for input a list of project members using values in a grid row.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ProjectMemberListGridField extends AbstractEntityListGridField<ProjectMember> {

    private static final long serialVersionUID = 806904868753372410L;

    private final PersonFieldFactory personFieldFactory;

    private final Logger logger;

    private Column<ProjectMember> personColumn;

    private Column<ProjectMember> roleColumn;

    /**
     * Constructor.
     *
     * @param personFieldFactory the factory for creating the person fields.
     * @param messages           accessor to the localized messages.
     * @param logger             the logger to be used by the component.
     */
    public ProjectMemberListGridField(PersonFieldFactory personFieldFactory, MessageSourceAccessor messages, Logger logger) {
        super(messages,
                "views.projects.members.edit"); //$NON-NLS-1$
        this.personFieldFactory = personFieldFactory;
        this.logger = logger;
    }

    @Override
    protected void createColumns(Grid<ProjectMember> grid) {
        this.personColumn = grid.addColumn(it -> getPersonValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createPersonEditor);

        this.roleColumn = grid.addColumn(it -> getRoleValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createRoleEditor);
    }

    @SuppressWarnings("static-method")
    private String getPersonValueLabel(ProjectMember member) {
        if (member == null) {
            return ""; //$NON-NLS-1$
        }
        final var person = member.getPerson();
        if (person == null) {
            return ""; //$NON-NLS-1$
        }
        return person.getFullNameWithLastNameFirst();
    }

    private SinglePersonNameField createPersonEditor(ProjectMember member) {
        final var field = this.personFieldFactory.createSingleNameField(
                getTranslation("views.projects.members.create"), //$NON-NLS-1$
                this.logger);
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(ProjectMember::getPerson, ProjectMember::setPerson);
        return field;
    }

    private String getRoleValueLabel(ProjectMember member) {
        if (member == null) {
            return ""; //$NON-NLS-1$
        }
        final var role = member.getRole();
        if (role == null) {
            return ""; //$NON-NLS-1$
        }
        return role.getLabel(this.messages, getLocale());
    }

    private ComboBox<Role> createRoleEditor(ProjectMember member) {
        final var field = createBaseEnumEditor(Role.class);
        field.setItemLabelGenerator(it -> {
            return it.getLabel(this.messages, getLocale());
        });
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(ProjectMember::getRole, ProjectMember::setRole);
        return field;
    }

    @Override
    protected ProjectMember createEntityInstance() {
        return new ProjectMember();
    }

    @Override
    public Validator<List<ProjectMember>> newStandardValidator() {
        return new StandardValidator();
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.personColumn.setHeader(getTranslation("views.projects.members.person")); //$NON-NLS-1$
        this.roleColumn.setHeader(getTranslation("views.projects.members.role")); //$NON-NLS-1$
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
    protected class StandardValidator implements Validator<List<ProjectMember>> {

        private static final long serialVersionUID = -490467280635992893L;

        /**
         * Default Constructor.
         */
        public StandardValidator() {
            //
        }

        @Override
        public ValidationResult apply(List<ProjectMember> value, ValueContext context) {
            final var committee = new HashSet<>();
            for (final var member : value) {
                // Check the member
                final var person = member.getPerson();
                if (person == null) {
                    return ValidationResult.error(getTranslation("views.projects.members.person.error.null")); //$NON-NLS-1$
                }
                // Check for unicity of person
                if (!committee.add(person)) {
                    return ValidationResult.error(getTranslation("views.projects.members.person.error.duplicate", person.getFullName())); //$NON-NLS-1$
                }
                // Check the role of member
                final var role = member.getRole();
                if (role == null) {
                    return ValidationResult.error(getTranslation("views.projects.members.role.error.null")); //$NON-NLS-1$
                }
            }
            //
            return ValidationResult.ok();
        }

    }

}
