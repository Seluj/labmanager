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

package fr.utbm.ciad.labmanager.views.appviews.indicators;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Enable to edit the public indicators.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@PageTitle("Public Indicators")
@Route(value = "publicindicators", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PublicIndicatorsView extends AbstractLoggerComposite<VerticalLayout> {

    private static final long serialVersionUID = 738063190104767506L;

    /**
     * Constructor.
     *
     * @param loggerFactory the factory to be used for the composite logger.
     */
    public PublicIndicatorsView(@Autowired ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);
        final TextField textField = new TextField();
        TextField textField2 = new TextField();
        final ComboBox<?> comboBox = new ComboBox<>();
        final Button buttonPrimary = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        textField.setLabel("Name");
        textField.setWidth("192px");
        textField2.setLabel("First name");
        textField2.setWidth("192px");
        comboBox.setLabel("Gender");
        comboBox.setWidth("192px");
        setComboBoxSampleData(comboBox);
        buttonPrimary.setText("Save");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(textField);
        getContent().add(textField2);
        getContent().add(comboBox);
        getContent().add(buttonPrimary);
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        final List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

}
