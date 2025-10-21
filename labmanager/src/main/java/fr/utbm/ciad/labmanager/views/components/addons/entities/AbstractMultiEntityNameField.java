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

import com.google.common.base.Strings;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox.AutoExpandMode;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasPrefix;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract implementation of a field for entering multiple entities, with auto-completion from the person JPA entities.
 *
 * @param <E> the type of edited entity.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractMultiEntityNameField<E extends IdentifiableEntity> extends CustomField<List<E>> implements HasPrefix {

    private static final long serialVersionUID = 8608252253714436879L;

    private final MultiSelectComboBox<E> combo;

    private final MenuItem createButton;

    private final SerializableBiConsumer<E, Consumer<E>> creationWithUiCallback;

    private final SerializableBiConsumer<E, Consumer<E>> creationWithoutUiCallback;

    private String customEntity;

    /**
     * Constructor.
     *
     * @param comboConfigurator         configure the combo box. The argument is the combo box to configure.
     * @param comboInitializer          initialize of the combo box with data. The argument is the combo box to initialize.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     */
    public AbstractMultiEntityNameField(
            SerializableConsumer<MultiSelectComboBox<E>> comboConfigurator,
            SerializableConsumer<MultiSelectComboBox<E>> comboInitializer,
            SerializableBiConsumer<E, Consumer<E>> creationWithUiCallback,
            SerializableBiConsumer<E, Consumer<E>> creationWithoutUiCallback) {
        final var enableDynamicCreationWithUi = creationWithUiCallback != null;
        final var enableDynamicCreationWithoutUi = creationWithoutUiCallback != null;

        this.creationWithUiCallback = creationWithUiCallback;
        this.creationWithoutUiCallback = creationWithoutUiCallback;

        this.combo = new MultiSelectComboBox<>();
        this.combo.setAutoOpen(true);
        this.combo.setAutoExpand(AutoExpandMode.BOTH);
        this.combo.setSelectedItemsOnTop(false);
        this.combo.setClearButtonVisible(true);
        this.combo.setManualValidation(false);
        this.combo.setAllowCustomValue(enableDynamicCreationWithUi || enableDynamicCreationWithoutUi);
        if (comboConfigurator != null) {
            comboConfigurator.accept(this.combo);
        }
        this.combo.setWidthFull();

        if (enableDynamicCreationWithUi) {
            final var tools = new MenuBar();
            tools.addThemeVariants(MenuBarVariant.LUMO_ICON);
            this.createButton = ComponentFactory.addIconItem(tools, LineAwesomeIcon.PLUS_SOLID, null, null, it -> addEntityWithEditor());

            final var layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.AUTO);
            layout.setSpacing(false);
            layout.setPadding(false);
            layout.add(this.combo, tools);
            add(layout);
        } else {
            this.createButton = null;
            add(this.combo);
        }

        this.combo.setPlaceholder(getLabel());

        if (comboInitializer != null) {
            comboInitializer.accept(this.combo);
        }

        this.combo.addValueChangeListener(it -> {
            this.customEntity = null;
            // Force the field to provide the new value to the form
            updateValue();
        });

        if (enableDynamicCreationWithUi || enableDynamicCreationWithoutUi) {
            this.combo.addCustomValueSetListener(event -> {
                this.customEntity = event.getDetail();
                // Force the field to provide the new value to the form
                updateValue();
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void addEntity(SerializableBiConsumer<E, Consumer<E>> callback) {
        if (callback != null) {
            final var customName = Strings.nullToEmpty(this.customEntity).trim();
            final E newEntity = createNewEntity(customName);
            callback.accept(newEntity, newEntity0 -> {
                this.combo.getGenericDataView().refreshAll();
                this.combo.select(newEntity0);
            });
        }
    }

    /**
     * Invoked for adding an entity dynamically using the editor.
     */
    protected void addEntityWithEditor() {
        addEntity(this.creationWithUiCallback);
    }

    /**
     * Invoked for adding an entity dynamically without using the editor.
     */
    protected void addEntityWithoutEditor() {
        addEntity(this.creationWithoutUiCallback);
    }

    /**
     * Invoked for creating the new entity.
     *
     * @param customName the custom name provided by the combo input field.
     * @return the new entity.
     */
    protected abstract E createNewEntity(String customName);

    @Override
    protected List<E> generateModelValue() {
        return Strings.isNullOrEmpty(this.customEntity) ? new ArrayList<>(this.combo.getValue()) : null;
    }

    @Override
    protected void setPresentationValue(List<E> newPresentationValue) {
        if (Strings.isNullOrEmpty(this.customEntity) || newPresentationValue != null) {
            this.combo.deselectAll();
            if (newPresentationValue != null) {
                this.combo.select(newPresentationValue);
            }
        }
    }

    /**
     * Replies the placeholder text that should be displayed in the input element, when the user has not entered a value
     *
     * @return the placeholder text
     */
    public String getPlaceholder() {
        return this.combo.getPlaceholder();
    }

    /**
     * Sets the placeholder text that should be displayed in the input element, when the user has not entered a value
     *
     * @param placeholder the placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.combo.setPlaceholder(placeholder);
    }

    @Override
    public String getHelperText() {
        return this.combo.getHelperText();
    }

    @Override
    public void setHelperText(String text) {
        this.combo.setHelperText(text);
    }

    @Override
    public String getErrorMessage() {
        return super.getErrorMessage();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.combo.setErrorMessage(errorMessage);
    }

    @Override
    public boolean isInvalid() {
        return this.combo.isInvalid();
    }

    @Override
    public void setInvalid(boolean invalid) {
        this.combo.setInvalid(invalid);
    }

    @Override
    public boolean isReadOnly() {
        return this.combo.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.combo.setReadOnly(readOnly);
    }

    /**
     * Change the title of the create button.
     *
     * @param title the text of the title.
     */
    public void setCreateButtonTitle(String title) {
        if (this.createButton != null) {
            this.createButton.setAriaLabel(title);
        }
    }

}
