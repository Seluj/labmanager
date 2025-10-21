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

package fr.utbm.ciad.labmanager.views.components.addons.markdown;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasClientValidation;
import com.vaadin.flow.component.shared.ValidationUtil;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import fr.utbm.ciad.labmanager.views.ViewConstants;

import java.io.Serializable;

/**
 * Vaadin component for input markdown text. This components is a rewriting of the {@code MarkdownArea} that is available in Vaadin addons to be a real input field and not a simple component.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class MarkdownField extends CustomField<String> implements HasValueChangeMode, HasValidator<String>, HasClientValidation, LocaleChangeObserver {

    /**
     * Default URL of the markdown documentation.
     */
    public static final String DEFAULT_MARKDOWN_DOC_URL = "https://vaadin.com/markdown-guide"; //$NON-NLS-1$
    private static final long serialVersionUID = 1336544542288511016L;
    private final Parser parser;

    private final HtmlRenderer renderer;

    private final TextArea input;

    private final Span writeTab;

    private final Div writeView;

    private final Anchor markdownDoc;

    private final Span previewTab;

    private final Div previewView;

    private final TabSheet tabs;

    private MarkdownFieldValidationSupport validationSupport;

    private boolean manualValidation;

    /**
     * Constructor with initial content.
     *
     * @param text the initial text value.
     */
    public MarkdownField(String text) {
        this();
        setValue(text);
    }

    /**
     * Constructor to create a phone number picker.
     */
    public MarkdownField() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();

        this.tabs = new TabSheet();

        this.writeTab = new Span();
        final var wb = new HorizontalLayout(LumoIcon.EDIT.create(), this.writeTab);
        wb.setSpacing(false);
        wb.setPadding(false);

        this.input = new TextArea();
        this.input.setWidthFull();
        this.writeView = new Div(this.input);
        this.writeView.setWidthFull();
        this.writeView.setMaxHeight(ViewConstants.DEFAULT_TEXT_AREA_HEIGHT, Unit.PIXELS);

        this.tabs.add(wb, this.writeView);
        this.tabs.setWidthFull();

        this.previewTab = new Span();
        final var pb = new HorizontalLayout(LumoIcon.EYE.create(), this.previewTab);
        pb.setSpacing(false);
        pb.setPadding(false);

        this.previewView = new Div();
        this.previewView.setMaxHeight(ViewConstants.DEFAULT_TEXT_AREA_HEIGHT, Unit.PIXELS);
        this.previewView.setWidthFull();

        this.tabs.add(pb, this.previewView);

        this.markdownDoc = new Anchor(DEFAULT_MARKDOWN_DOC_URL, ""); //$NON-NLS-1$
        this.markdownDoc.setTarget(AnchorTarget.BLANK);
        this.markdownDoc.getStyle().setFontSize("--lumo-font-size-xxs"); //$NON-NLS-1$

        final var vl = new VerticalLayout(this.tabs, this.markdownDoc);
        vl.setSpacing(false);
        vl.setPadding(false);
        add(vl);

        setInvalid(false);

        // Render the preview only when the tabs for preview is switched to visible
        this.tabs.addSelectedChangeListener(event -> {
            if (this.tabs.getSelectedIndex() == 1) {
                var text = getValue();
                if (Strings.isNullOrEmpty(text)) {
                    text = getTranslation("views.markdown.nothing"); //$NON-NLS-1$
                }
                renderMarkdown(text);
            }
        });
    }

    private void renderMarkdown(String value) {
        final var html = "<div>" + parseMarkdown(Strings.nullToEmpty(value)) + "</div>"; //$NON-NLS-1$ //$NON-NLS-2$
        final var item = new Html(html);
        this.previewView.removeAll();
        this.previewView.add(item);
    }

    private String parseMarkdown(String value) {
        final var text = this.parser.parse(value);
        return this.renderer.render(text);
    }

    @Override
    public boolean isReadOnly() {
        return this.input.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readonly) {
        this.input.setReadOnly(readonly);
    }

    /**
     * The placeholder text that should be displayed in the input element, when
     * the user has not entered a value
     *
     * @return the {@code placeholder} property from the web component
     */
    public String getPlaceholder() {
        return this.input.getPlaceholder();
    }

    /**
     * Sets the placeholder text that should be displayed in the input element,
     * when the user has not entered a value
     *
     * @param placeholder the placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.input.setPlaceholder(placeholder);
    }

    /**
     * Specify that this control should have input focus when the page loads.
     *
     * @return the {@code autofocus} property from the webcomponent
     */
    public boolean isAutofocus() {
        return this.input.isAutofocus();
    }

    /**
     * Sets the whether the component should automatically receive focus when
     * the page loads. Defaults to {@code false}.
     *
     * @param autofocus {@code true} component should automatically receive focus
     */
    public void setAutofocus(boolean autofocus) {
        this.input.setAutofocus(autofocus);
    }

    @Override
    public ValueChangeMode getValueChangeMode() {
        return this.input.getValueChangeMode();
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.input.setValueChangeMode(valueChangeMode);
    }

    @Override
    protected String generateModelValue() {
        return Strings.emptyToNull(this.input.getValue());
    }

    @Override
    protected void setPresentationValue(String newPresentationValue) {
        this.input.setValue(Strings.nullToEmpty(newPresentationValue));
    }

    /**
     * Specifies that the user must fill in a value.
     *
     * @param required the boolean value to set.
     */
    public void setRequired(boolean required) {
        getElement().setProperty("required", required); //$NON-NLS-1$
        getValidationSupport().setRequired(required);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        getElement().setProperty("required", requiredIndicatorVisible); //$NON-NLS-1$
        getValidationSupport().setRequired(requiredIndicatorVisible);
    }

    @Override
    public Validator<String> getDefaultValidator() {
        return (value, context) -> getValidationSupport().checkValidity(value);
    }

    private MarkdownFieldValidationSupport getValidationSupport() {
        if (this.validationSupport == null) {
            this.validationSupport = new MarkdownFieldValidationSupport();
        }
        return this.validationSupport;
    }

    /**
     * Performs server-side validation of the current value and the validation
     * constraints of the field, such as {@link #setPattern(String)}. This is
     * needed because it is possible to circumvent the client-side validation
     * constraints using browser development tools.
     */
    protected void validate() {
        if (!this.manualValidation) {
            setInvalid(getValidationSupport().isInvalid(getValue()));
        }
    }

    @Override
    public void setManualValidation(boolean enabled) {
        this.manualValidation = enabled;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.writeTab.setText(getTranslation("views.markdown.edit")); //$NON-NLS-1$
        this.markdownDoc.setText(getTranslation("views.markdown.doc")); //$NON-NLS-1$
        this.previewTab.setText(getTranslation("views.markdown.preview")); //$NON-NLS-1$
        if (this.tabs.getSelectedIndex() == 1 && Strings.isNullOrEmpty(getValue())) {
            renderMarkdown(getTranslation("views.markdown.nothing")); //$NON-NLS-1$
        }
    }

    /**
     * Validation support for markdown field.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private final class MarkdownFieldValidationSupport implements Serializable {

        private static final long serialVersionUID = 2676753116831209016L;

        private boolean required;

        private MarkdownFieldValidationSupport() {
            //
        }

        /**
         * Change the requirement for the number.
         *
         * @param required {@code true} if required
         */
        void setRequired(boolean required) {
            this.required = required;
        }

        /**
         * Test if value is invalid for the field.
         *
         * @param value value to be tested.
         * @return {@code true} if the value is invalid.
         */
        boolean isInvalid(String value) {
            var requiredValidation = ValidationUtil.checkRequired(this.required, value, getEmptyValue());
            return requiredValidation.isError() || checkValidity(value).isError();
        }

        /**
         * Do the check.
         *
         * @param value value to be tested.
         * @return the result of the validity check.
         */
        ValidationResult checkValidity(String value) {
            return ValidationResult.ok();
        }

    }

}
