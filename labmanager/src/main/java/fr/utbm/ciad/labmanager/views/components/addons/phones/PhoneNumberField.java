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

package fr.utbm.ciad.labmanager.views.components.addons.phones;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.*;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * Vaadin component for input phone number.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PhoneNumberField extends CustomField<PhoneNumber>
        implements HasClearButton, HasValueChangeMode, HasPrefix, HasSuffix,
        HasValidator<PhoneNumber>, HasClientValidation {

    private static final long serialVersionUID = 5585942453841536464L;

    private static final String LOCAL_NUMBER_VALIDATION_PATTERN = "[0-9a-zA-Z \\/\\-]*"; //$NON-NLS-1$

    private static final CountryCode[] SORTED_COUNTRIES = Arrays.asList(CountryCode.values()).stream().sorted((a, b) -> {
        var cmp = a.getCallingCode() - b.getCallingCode();
        if (cmp != 0) {
            return cmp;
        }
        return a.getDisplayCountry().compareToIgnoreCase(b.getDisplayCountry());
    }).toArray(size -> new CountryCode[size]);

    private final ComboBox<CountryCode> countries;

    private final TextField number;

    private boolean hasDynamicHelpText = true;

    private String dynamicHelpText;

    private PhoneNumberFieldValidationSupport validationSupport;

    private PhoneNumber oldValue;

    private boolean manualValidation;

    /**
     * Constructor to create a phone number picker.
     */
    public PhoneNumberField() {
        final var hl = new HorizontalLayout();
        hl.setSpacing(false);

        this.countries = new ComboBox<>();
        this.countries.setSizeUndefined();
        this.countries.setItems(SORTED_COUNTRIES);
        this.countries.setClearButtonVisible(false);
        this.countries.setManualValidation(true);
        this.countries.addThemeName(ComboBoxVariant.LUMO_ALIGN_CENTER.getVariantName());
        this.countries.setValue(getDefaultCountry());
        this.countries.setItemLabelGenerator(this::renderCountryItem);
        this.countries.addValueChangeListener(it -> {
            updateDynamicHelperText();
            fireValueChangeEventFromCountryChange(it.isFromClient());
        });

        this.number = new TextField();
        this.number.setSizeFull();
        this.number.setAutocapitalize(Autocapitalize.CHARACTERS);
        this.number.setPattern(LOCAL_NUMBER_VALIDATION_PATTERN);
        this.number.addValidationStatusChangeListener(it -> {
            validate();
        });
        this.number.addValueChangeListener(it -> {
            fireValueChangeEventFromNumberChange(it.isFromClient());
        });

        hl.add(this.countries, this.number);
        add(hl);

        setInvalid(false);
        setValueChangeMode(ValueChangeMode.ON_CHANGE);
    }

    /**
     * Fire the event change due to the change of country.
     *
     * @param isClientSide indicates if the event comes from the client.
     */
    protected void fireValueChangeEventFromCountryChange(boolean isClientSide) {
        final var current = generateModelValue();
        if ((current == null && this.oldValue != null)
                || (current != null && (!current.equals(this.oldValue)))) {
            final var old = this.oldValue;
            this.oldValue = current;
            final var evt = new ComponentValueChangeEvent<>(this, this, old, isClientSide);
            fireEvent(evt);
        }
    }

    /**
     * Fire the event change due to the change of number.
     *
     * @param isClientSide indicates if the event comes from the client.
     */
    protected void fireValueChangeEventFromNumberChange(boolean isClientSide) {
        final var current = generateModelValue();
        if ((current == null && this.oldValue != null)
                || (current != null && (!current.equals(this.oldValue)))) {
            final var old = this.oldValue;
            this.oldValue = current;
            final var evt = new ComponentValueChangeEvent<>(this, this, old, isClientSide);
            fireEvent(evt);
        }
    }

    /**
     * Fire the event change due.
     */
    protected void fireValueChangeEvent() {
        final var current = generateModelValue();
        final var old = this.oldValue;
        this.oldValue = current;
        final var evt = new ComponentValueChangeEvent<>(this, this, old, false);
        fireEvent(evt);
    }

    /**
     * Default rendering of the country code in the combo box.
     *
     * @param code the country codeto render.
     * @return the result of the rendering.
     */
    protected String renderCountryItem(CountryCode code) {
        return "+" + code.getCallingCode() +
                ' ' + code.getDisplayCountry();
    }

    /**
     * Replies the default country, that is not necessary the selected one.
     *
     * @return the default country.
     */
    public CountryCode getDefaultCountry() {
        final var cc = CountryCode.fromLocale(getLocale());
        if (cc == null) {
            return CountryCode.getDefault();
        }
        return cc;
    }

    @Override
    public boolean isReadOnly() {
        return this.number.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readonly) {
        this.countries.setReadOnly(readonly);
        this.number.setReadOnly(readonly);
    }

    @Override
    public boolean isClearButtonVisible() {
        return this.number.isClearButtonVisible();
    }

    @Override
    public void setClearButtonVisible(boolean clearButtonVisible) {
        this.number.setClearButtonVisible(clearButtonVisible);
    }

    /**
     * The placeholder text that should be displayed in the input element, when
     * the user has not entered a value
     *
     * @return the {@code placeholder} property from the web component
     */
    public String getPlaceholder() {
        return this.number.getPlaceholder();
    }

    /**
     * Sets the placeholder text that should be displayed in the input element,
     * when the user has not entered a value
     *
     * @param placeholder the placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.number.setPlaceholder(placeholder);
    }

    /**
     * Specify that this control should have input focus when the page loads.
     *
     * @return the {@code autofocus} property from the webcomponent
     */
    public boolean isAutofocus() {
        return this.number.isAutofocus();
    }

    /**
     * Sets the whether the component should automatically receive focus when
     * the page loads. Defaults to {@code false}.
     *
     * @param autofocus {@code true} component should automatically receive focus
     */
    public void setAutofocus(boolean autofocus) {
        this.number.setAutofocus(autofocus);
    }

    @Override
    public Component getPrefixComponent() {
        return this.countries.getPrefixComponent();
    }

    @Override
    public void setPrefixComponent(Component component) {
        this.countries.setPrefixComponent(component);
    }

    @Override
    public Component getSuffixComponent() {
        return this.number.getSuffixComponent();
    }

    @Override
    public void setSuffixComponent(Component component) {
        this.number.setSuffixComponent(component);
    }

    /**
     * Sets the item label generator that is used to produce the strings shown
     * in the combo box for each item.
     *
     * @param itemLabelGenerator the item label provider to use, not null
     */
    public void setItemLabelGenerator(ItemLabelGenerator<CountryCode> itemLabelGenerator) {
        assert itemLabelGenerator != null;
        this.countries.setItemLabelGenerator(itemLabelGenerator);
    }

    /**
     * Update the helper text with the dynamic text.
     */
    protected void updateDynamicHelperText() {
        if (this.hasDynamicHelpText && this.dynamicHelpText != null) {
            final var cc = this.countries.getValue();
            if (cc != null) {
                final var message = MessageFormat.format(this.dynamicHelpText,
                        Integer.valueOf(cc.getCallingCode()),
                        cc.getInternationalPhonePrefix(),
                        cc.getNationalPhonePrefix());
                super.setHelperText(message);
            }
        }
    }

    @Override
    public void setHelperText(String helperText) {
        this.dynamicHelpText = null;
        super.setHelperText(helperText);
    }

    @Override
    public void setHelperComponent(Component component) {
        this.hasDynamicHelpText = false;
        this.dynamicHelpText = null;
        super.setHelperComponent(component);
    }

    /**
     * Replies the string used for the helper text as a pattern in which the arguments are replaced by
     * the number information.
     *
     * @return the String value to set.
     */
    public String getDynamicHelperText() {
        if (this.hasDynamicHelpText) {
            return this.dynamicHelpText;
        }
        return null;
    }

    /**
     * String used for the helper text as a pattern in which the arguments are replaced by
     * the number information. It shows a text adjacent to the field
     * that can be used, e.g., to inform to the users which values it expects.
     *
     * <p>The arguments that are replaced are:<ul>
     * <li><code>{0}</code>: the calling code</li>
     * <li><code>{1}</code>: the international prefix</li>
     * <li><code>{2}</code>: the national prefix</li>
     * </ul>
     *
     * <p>In case {@link #setHelperText(String)}, {@link #setHelperComponent(Component)} and
     * {@link #setDynamicHelperText(String)} are invoked, they will be considered in
     * the following order: {@link #setHelperComponent(Component)}, {@link #setHelperText(String)},
     * {@link #setDynamicHelperText(String)}.
     *
     * @param helperText the String value to set.
     * @see #setHelperText(String)
     * @see #setHelperComponent(Component)
     */
    public void setDynamicHelperText(String helperText) {
        if (this.hasDynamicHelpText) {
            final var newValue = Strings.emptyToNull(helperText);
            if (!Objects.equals(newValue, this.dynamicHelpText)) {
                this.dynamicHelpText = newValue;
                updateDynamicHelperText();
            }
        }
    }

    @Override
    public ValueChangeMode getValueChangeMode() {
        return this.number.getValueChangeMode();
    }

    @Override
    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.number.setValueChangeMode(valueChangeMode);
    }

    @Override
    public int getValueChangeTimeout() {
        return this.number.getValueChangeTimeout();
    }

    @Override
    public void setValueChangeTimeout(int valueChangeTimeout) {
        this.number.setValueChangeTimeout(valueChangeTimeout);
    }

    @Override
    protected PhoneNumber generateModelValue() {
        final var num = this.number.getValue();
        if (Strings.isNullOrEmpty(num)) {
            return null;
        }
        var cc = this.countries.getValue();
        if (cc == null) {
            cc = getDefaultCountry();
        }
        try {
            return new PhoneNumber(cc, num);
        } catch (Throwable ex) {
            return null;
        }
    }

    @Override
    protected void setPresentationValue(PhoneNumber newPresentationValue) {
        if (newPresentationValue == null) {
            this.number.clear();
        } else {
            this.countries.setValue(newPresentationValue.getCountry());
            final var num = newPresentationValue.getLocalNumber();
            if (Strings.isNullOrEmpty(num)) {
                this.number.clear();
            } else {
                this.number.setValue(num);
            }
        }
    }

    @Override
    public Registration addValidationStatusChangeListener(ValidationStatusChangeListener<PhoneNumber> listener) {
        return this.number.addClientValidatedEventListener(
                event -> listener.validationStatusChanged(
                        new ValidationStatusChangeEvent<>(this, !isInvalid())));
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
    public Validator<PhoneNumber> getDefaultValidator() {
        return (value, context) -> getValidationSupport().checkValidity(value);
    }

    private PhoneNumberFieldValidationSupport getValidationSupport() {
        if (this.validationSupport == null) {
            this.validationSupport = new PhoneNumberFieldValidationSupport();
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

    /**
     * Validation support for phone number field.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private final class PhoneNumberFieldValidationSupport implements Serializable {

        private static final long serialVersionUID = 783554861994440078L;

        private boolean required;

        private PhoneNumberFieldValidationSupport() {
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
        boolean isInvalid(PhoneNumber value) {
            var requiredValidation = ValidationUtil.checkRequired(this.required, value, getEmptyValue());
            return requiredValidation.isError() || checkValidity(value).isError();
        }

        /**
         * Do the check.
         *
         * @param value value to be tested.
         * @return the result of the validity check.
         */
        ValidationResult checkValidity(PhoneNumber value) {
            final var valueViolatePattern = PhoneNumberField.this.number.isInvalid();
            if (valueViolatePattern) {
                return ValidationResult.error(""); //$NON-NLS-1$
            }
            return ValidationResult.ok();
        }

    }

}
