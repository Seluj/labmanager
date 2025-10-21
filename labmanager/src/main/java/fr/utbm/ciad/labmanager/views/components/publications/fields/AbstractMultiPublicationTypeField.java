package fr.utbm.ciad.labmanager.views.components.publications.fields;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox.AutoExpandMode;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasPrefix;
import com.vaadin.flow.function.SerializableConsumer;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMultiPublicationTypeField extends CustomField<List<String>> implements HasPrefix {

    private final MultiSelectComboBox<String> combo;

    private final SerializableConsumer<Consumer<String>> creationCallback;

    private String customString;

    public AbstractMultiPublicationTypeField(
            SerializableConsumer<MultiSelectComboBox<String>> comboConfigurator,
            SerializableConsumer<MultiSelectComboBox<String>> comboInitializer,
            SerializableConsumer<Consumer<String>> creationCallback) {

        this.creationCallback = creationCallback;

        this.combo = new MultiSelectComboBox<>();
        this.combo.setAutoOpen(true);
        this.combo.setAutoExpand(AutoExpandMode.BOTH);
        this.combo.setSelectedItemsOnTop(false);
        this.combo.setClearButtonVisible(true);
        this.combo.setAllowCustomValue(creationCallback != null);
        if (comboConfigurator != null) {
            comboConfigurator.accept(this.combo);
        }
        this.combo.setWidthFull();

        if (creationCallback != null) {
            final var tools = new MenuBar();
            tools.addThemeVariants(MenuBarVariant.LUMO_ICON);
            ComponentFactory.addIconItem(tools, LineAwesomeIcon.PLUS_SOLID, null, null, it -> addStringWithEditor());

            final var layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.AUTO);
            layout.setSpacing(false);
            layout.setPadding(false);
            layout.add(this.combo, tools);
            add(layout);
        } else {
            add(this.combo);
        }

        this.combo.setPlaceholder(getLabel());

        if (comboInitializer != null) {
            comboInitializer.accept(this.combo);
        }

        this.combo.addValueChangeListener(it -> {
            this.customString = null;
            updateValue();
        });

        if (creationCallback != null) {
            this.combo.addCustomValueSetListener(event -> {
                this.customString = event.getDetail();
                updateValue();
            });
        }
    }

    private void addString(SerializableConsumer<Consumer<String>> callback) {
        if (callback != null) {
            final var customValue = customString != null ? customString.trim() : "";
            callback.accept(newString -> {
                this.combo.getDataProvider().refreshAll();
                this.combo.select(newString);
            });
        }
    }

    protected void addStringWithEditor() {
        addString(this.creationCallback);
    }

    @Override
    protected List<String> generateModelValue() {
        return customString == null ? new ArrayList<>(this.combo.getValue()) : null;
    }

    @Override
    protected void setPresentationValue(List<String> newPresentationValue) {
        if (customString == null || newPresentationValue != null) {
            this.combo.deselectAll();
            if (newPresentationValue != null) {
                this.combo.select(newPresentationValue);
            }
        }
    }

    public String getPlaceholder() {
        return this.combo.getPlaceholder();
    }

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

    public void setCreateButtonTitle(String title) {
        // Implementation to set the title of the create button if needed
    }
}
