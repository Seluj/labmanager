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

import com.google.common.base.Strings;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.FloatRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.project.ProjectBudget;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import fr.utbm.ciad.labmanager.views.components.addons.converters.DoubleToFloatWithPrecisionConverter;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListGridField;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.List;

/**
 * Implementation of a Vaadin component for input a list of project budgets using values in a grid row.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ProjectBudgetListGridField extends AbstractEntityListGridField<ProjectBudget> {

    private static final long serialVersionUID = -184881234128681596L;

    private Column<ProjectBudget> fundingSchemeColumn;

    private Column<ProjectBudget> budgetColumn;

    private Column<ProjectBudget> grantColumn;

    /**
     * Constructor.
     *
     * @param messages accessor to the localized messages.
     */
    public ProjectBudgetListGridField(MessageSourceAccessor messages) {
        super(messages, "views.projects.budgets.edit"); //$NON-NLS-1$
    }

    @Override
    protected void createColumns(Grid<ProjectBudget> grid) {
        this.fundingSchemeColumn = grid.addColumn(it -> getFundingSchemeValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createFundingSchemeEditor);

        this.budgetColumn = grid.addColumn(it -> getBudgetValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createBudgetEditor);

        this.grantColumn = grid.addColumn(it -> getGrantValueLabel(it))
                .setAutoWidth(true)
                .setEditorComponent(this::createGrantEditor);
    }

    private String getFundingSchemeValueLabel(ProjectBudget budget) {
        if (budget == null) {
            return ""; //$NON-NLS-1$
        }
        final var scheme = budget.getFundingScheme();
        if (scheme == null) {
            return ""; //$NON-NLS-1$
        }
        return scheme.getLabel(this.messages, getLocale());
    }

    private String getBudgetValueLabel(ProjectBudget budget) {
        if (budget == null) {
            return ""; //$NON-NLS-1$
        }
        final var euros = budget.getBudget();
        if (euros <= 0f) {
            return ""; //$NON-NLS-1$
        }
        return getTranslation("views.projects.budgets.value", Float.valueOf(euros)); //$NON-NLS-1$
    }

    @SuppressWarnings("static-method")
    private String getGrantValueLabel(ProjectBudget budget) {
        if (budget == null) {
            return ""; //$NON-NLS-1$
        }
        final var grant = budget.getFundingReference();
        return Strings.nullToEmpty(grant);
    }

    private ComboBox<FundingScheme> createFundingSchemeEditor(ProjectBudget budget) {
        final var field = createBaseEnumEditor(FundingScheme.class);
        field.setItemLabelGenerator(it -> {
            return it.getLabel(this.messages, getLocale());
        });
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(ProjectBudget::getFundingScheme, ProjectBudget::setFundingScheme);
        return field;
    }

    private NumberField createBudgetEditor(ProjectBudget budget) {
        final var field = new NumberField();
        final var binder = getGridEditor().getBinder();
        binder.forField(field)
                .withConverter(new DoubleToFloatWithPrecisionConverter(3))
                .withValidator(new FloatRangeValidator(getTranslation("views.projects.budgets.budget.error"), Float.valueOf(0f), null)) //$NON-NLS-1$
                .bind(ProjectBudget::getBudget, ProjectBudget::setBudget);
        return field;
    }

    private TextField createGrantEditor(ProjectBudget budget) {
        final var field = new TextField();
        final var binder = getGridEditor().getBinder();
        binder.forField(field).bind(ProjectBudget::getFundingReference, ProjectBudget::setFundingReference);
        return field;
    }

    @Override
    protected ProjectBudget createEntityInstance() {
        return new ProjectBudget();
    }

    @Override
    public Validator<List<ProjectBudget>> newStandardValidator() {
        return new StandardValidator();
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.fundingSchemeColumn.setHeader(getTranslation("views.projects.budgets.scheme")); //$NON-NLS-1$
        this.budgetColumn.setHeader(getTranslation("views.projects.budgets.budget")); //$NON-NLS-1$
        this.grantColumn.setHeader(getTranslation("views.projects.budgets.grant")); //$NON-NLS-1$
    }

    /**
     * Implementation of a Vaadin component for input a list of project budgets using values in a grid row.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class StandardValidator implements Validator<List<ProjectBudget>> {

        private static final long serialVersionUID = -3222944273844477031L;

        /**
         * Default Constructor.
         */
        public StandardValidator() {
            //
        }

        @Override
        public ValidationResult apply(List<ProjectBudget> value, ValueContext context) {
            for (final var budget : value) {
                // Check the funding scheme of member
                final var scheme = budget.getFundingScheme();
                if (scheme == null) {
                    return ValidationResult.error(getTranslation("views.projects.budgets.scheme.error.null")); //$NON-NLS-1$
                }
                // Check the member
                final var euros = budget.getBudget();
                if (euros < 0f) {
                    return ValidationResult.error(getTranslation("views.projects.budgets.value.error.null")); //$NON-NLS-1$
                }
            }
            //
            return ValidationResult.ok();
        }

    }

}
