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

package fr.utbm.ciad.labmanager.views.components.supervisions.fields;

import com.ibm.icu.text.MessageFormat;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.supervision.Supervisor;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorType;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListGridField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.HashSet;
import java.util.List;

/**
 * Implementation of a Vaadin component for input a list of supervisors using values in a grid row.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class SupervisorListGridField extends AbstractEntityListGridField<Supervisor> {

    private static final long serialVersionUID = -5957159897496043863L;

    private static final String PERCENTAGE_FORMAT = "{0} %"; //$NON-NLS-1$

    private final PersonFieldFactory personFieldFactory;

    private final Logger logger;

    private Column<Supervisor> personColumn;

    private Column<Supervisor> roleColumn;

    private Column<Supervisor> percentageColumn;

    /**
     * Constructor.
     *
     * @param personFieldFactory the factory for creating the person fields.
     * @param messages           accessor to the localized messages.
     * @param logger             the logger to be used by the component.
     */
    public SupervisorListGridField(PersonFieldFactory personFieldFactory, MessageSourceAccessor messages, Logger logger) {
        super(messages,
                "views.supervisor.edit"); //$NON-NLS-1$
        this.personFieldFactory = personFieldFactory;
        this.logger = logger;
    }

    @Override
    protected void createColumns(Grid<Supervisor> grid) {
        this.personColumn = grid.addColumn(it -> getPersonValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createPersonEditor);

        this.roleColumn = grid.addColumn(it -> getRoleValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createRoleEditor);

        this.percentageColumn = grid.addColumn(it -> getPercentageValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createPercentageEditor);
    }

    @SuppressWarnings("static-method")
    private String getPersonValueLabel(Supervisor supervisor) {
        if (supervisor == null) {
            return ""; //$NON-NLS-1$
        }
        final var person = supervisor.getSupervisor();
        if (person == null) {
            return ""; //$NON-NLS-1$
        }
        return person.getFullNameWithLastNameFirst();
    }

    private SinglePersonNameField createPersonEditor(Supervisor item) {
        final var field = this.personFieldFactory.createSingleNameField(getTranslation("views.supervisor.create_supervisor"), //$NON-NLS-1$
                this.logger);
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(Supervisor::getSupervisor, Supervisor::setSupervisor);
        return field;
    }

    private String getRoleValueLabel(Supervisor supervisor) {
        if (supervisor == null) {
            return ""; //$NON-NLS-1$
        }
        final var role = supervisor.getType();
        if (role == null) {
            return ""; //$NON-NLS-1$
        }
        final var person = supervisor.getSupervisor();
        final var gender = person == null ? null : person.getGender();
        return role.getLabel(this.messages, gender, getLocale());
    }

    private ComboBox<SupervisorType> createRoleEditor(Supervisor item) {
        final var field = createBaseEnumEditor(SupervisorType.class);
        final var person = item.getSupervisor();
        final var gender = person == null ? null : person.getGender();
        field.setItemLabelGenerator(it -> {
            return it.getLabel(this.messages, gender, getLocale());
        });
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(Supervisor::getType, Supervisor::setType);
        return field;
    }

    @SuppressWarnings("static-method")
    private String getPercentageValueLabel(Supervisor supervisor) {
        if (supervisor == null) {
            return ""; //$NON-NLS-1$
        }
        final var percentage = supervisor.getPercentage();
        if (percentage <= 0) {
            return ""; //$NON-NLS-1$
        }
        return MessageFormat.format(PERCENTAGE_FORMAT, Integer.valueOf(percentage));
    }

    private IntegerField createPercentageEditor(Supervisor item) {
        final var field = createBaseIntegerEditor(0, 100);
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(Supervisor::getPercentage, Supervisor::setPercentage);
        return field;
    }

    @Override
    protected Supervisor createEntityInstance() {
        return new Supervisor();
    }

    @Override
    public Validator<List<Supervisor>> newStandardValidator() {
        return new StandardValidator();
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.personColumn.setHeader(getTranslation("views.supervisor.person")); //$NON-NLS-1$
        this.roleColumn.setHeader(getTranslation("views.supervisor.role")); //$NON-NLS-1$
        this.percentageColumn.setHeader(getTranslation("views.supervisor.percentage")); //$NON-NLS-1$
    }

    /**
     * Implementation of a Vaadin component for input a list of supervisors using values in a grid row.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class StandardValidator implements Validator<List<Supervisor>> {

        private static final long serialVersionUID = 2597778084371475787L;

        /**
         * Default Constructor.
         */
        public StandardValidator() {
            //
        }

        @Override
        public ValidationResult apply(List<Supervisor> value, ValueContext context) {
            final var committee = new HashSet<>();
            int totalPercentage = 0;
            for (final var supervisor : value) {
                // Check the supervising person
                final var person = supervisor.getSupervisor();
                if (person == null) {
                    return ValidationResult.error(getTranslation("views.supervisor.person.error.null")); //$NON-NLS-1$
                }
                // Check for unicity of person
                if (!committee.add(person)) {
                    return ValidationResult.error(getTranslation("views.supervisor.person.error.duplicate", person.getFullName())); //$NON-NLS-1$
                }
                // Check the type of supervisor
                final var type = supervisor.getType();
                if (type == null) {
                    return ValidationResult.error(getTranslation("views.supervisor.type.error.null")); //$NON-NLS-1$
                }
                if (type.hasPercentage()) {
                    // Check the percentage
                    final var percentage = supervisor.getPercentage();
                    if (totalPercentage < 0 || totalPercentage > 100) {
                        return ValidationResult.error(getTranslation("views.supervisor.percentage.error.invalid_range")); //$NON-NLS-1$
                    }
                    totalPercentage += percentage;
                }
            }
            // Check the total percentage
            if (totalPercentage > 0 && totalPercentage != 100) {
                return ValidationResult.error(getTranslation("views.supervisor.percentage.error.invalid_sum")); //$NON-NLS-1$
            }
            //
            return ValidationResult.ok();
        }

    }

}
