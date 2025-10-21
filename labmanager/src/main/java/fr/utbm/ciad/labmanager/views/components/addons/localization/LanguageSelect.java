/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
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

package fr.utbm.ciad.labmanager.views.components.addons.localization;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Locale;

/**
 * A select component that is able to change the current language of the Vaadin UI.
 *
 * <p>This component is a re-implementation and an extension of
 * <a href="https://github.com/KasparScherrer/language-select">LanguageSelect</a>
 * written by Karspar Scherrer under Apache 2 license.
 *
 * <h3>important note</h3>
 *
 * <p>If you put multiple {@link LanguageSelect} on the same UI page, they will be not
 * listening about the language changes that may be applied by the other selectors on the same page.
 * Consequently, if you change the language with one selector, the other selectors will not change
 * their own selected language dynamically. <strong>As a consequence, it is recommended to put
 * only one {@link LanguageSelect} per page.</strong>
 *
 * <h3>Language Flags</h3>
 *
 * <p>In opposite to the original code of this component, you no not need to provide a flag image.
 * Flags are provided by the {@link CountryFlag country flag component}.
 *
 * <h3>Translation of language names</h3>
 *
 * <p>In the ResourceBundle that your I18NProvider implementation uses, add translations for each Locale that you will use.
 * The keys must be named {@code views.language_select.[language_code]}
 * If you want to change the key prefix, call {@link #setFlagTranslationKeyPrefix(String)}.
 *
 * @author $Author: sgalland$
 * @author $Author: kscherrer$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@CssImport(value = "./themes/labmanager/components/language-select.css", themeFor = "vaadin-select")
public class LanguageSelect extends Select<Locale> implements LocaleChangeObserver {

    private static final long serialVersionUID = -9160658766711844756L;

    // 6 months
    private static final int LANGUAGE_COOKIE_DURATION = 262800;

    private static final String LANGUAGE_COOKIE_NAME = "VaadinPreferredLanguage"; //$NON-NLS-1$

    private final ComponentRenderer<? extends Component, Locale> defaultRenderer;

    /**
     * Constructor with the provided languages. This constructor does not use the language cookie.
     * To use the language cookie, invoke {@link #LanguageSelect(boolean, Locale...)}.
     *
     * @param items the supported languages.
     * @see #LanguageSelect(boolean, Locale...)
     */
    public LanguageSelect(Locale... items) {
        this(false, items);
    }

    /**
     * Constructor with the provided languages and that is ready the language cookie.
     *
     * @param useLanguageCookie indicates if the language cookie is read, or not.
     * @param items             the supported languages.
     * @see #LanguageSelect(Locale...)
     */
    public LanguageSelect(boolean useLanguageCookie, Locale... items) {
        this(UI.getCurrent().getLocale(), useLanguageCookie, items);
    }

    /**
     * Constructor with the provided languages and that is ready the language cookie.
     *
     * @param currentLocale     the initial locale to be selected.
     * @param useLanguageCookie indicates if the language cookie is read, or not.
     * @param items             the supported languages.
     * @see #LanguageSelect(Locale...)
     */
    public LanguageSelect(Locale currentLocale, boolean useLanguageCookie, Locale... items) {
        this.defaultRenderer = new ComponentRenderer<>(new LanguageSelectItemRenderer(currentLocale));

        setItems(items);
        addClassName("language-select"); //$NON-NLS-1$
        setEmptySelectionAllowed(false);

        setRenderer(this.defaultRenderer);

        setValue(currentLocale);

        // Important that valuechangeListener is defined after setValue
        addValueChangeListener(change -> {
            changeUILocale(change.getValue());
            if (useLanguageCookie) {
                changeLanguageCookie(change.getValue());
            }
        });
    }

    /**
     * Factory method for creating the language select from the current
     * {@link I18NProvider} provider.
     *
     * @param locale the locale to be used for displaying the names.
     * @return the language select.
     */
    public static LanguageSelect newStandardLanguageSelect(Locale locale) {
        final var locales = getAvailableLocales();
        final var select = new LanguageSelect(locale, true, locales);
        return select;
    }

    /**
     * Factory method for creating the language select from the current
     * {@link I18NProvider} provider and displaying only the flags.
     *
     * @param locale the locale to be used for displaying the names.
     * @return the language select.
     */
    public static LanguageSelect newFlagOnlyLanguageSelect(Locale locale) {
        final var locales = getAvailableLocales();
        final var select = new LanguageSelect(true, locales);
        select.setRenderer(new LanguageSelectFlagItemRenderer(locale));
        select.setMaxWidth(52, Unit.POINTS);
        return select;
    }

    /**
     * Replies the available locales that are provided by the i18n provider.
     *
     * @return the locales, never {@code null} or empty array
     * @throws IllegalStateException if there is no i18n provider defined.
     */
    public static Locale[] getAvailableLocales() {
        final var i18NProvider = LocaleUtil.getI18NProvider();
        if (i18NProvider.isPresent()) {
            final var locales = i18NProvider.get().getProvidedLocales().toArray(size -> new Locale[size]);
            if (locales.length > 0) {
                return locales;
            }
        }
        throw new IllegalStateException("i18nProvider provider not found; or no language support provided"); //$NON-NLS-1$
    }

    /**
     * Change the locale of the UI.
     *
     * @param locale the new UI locale.
     */
    public static void setUILocale(Locale locale) {
        UI.getCurrent().getSession().setLocale(locale);
    }

    /**
     * Read the language cookie and initialize the Vaadin session with the language.
     *
     * @param serviceInitEvent the initialization event from Vaadin framework.
     */
    public static void readLanguageCookies(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addSessionInitListener(sessionEvent -> {
            final var request = sessionEvent.getRequest();
            final var session = sessionEvent.getSession();

            final var cookies = request.getCookies();
            if (cookies != null) {
                final var localeCookie = Arrays.stream(cookies).filter(c -> c.getName().equals(LANGUAGE_COOKIE_NAME)).findFirst();
                localeCookie.ifPresent(cookie -> session.setLocale(Locale.of(cookie.getValue())));
            }
        });
    }

    @Override
    public void setRenderer(ComponentRenderer<? extends Component, Locale> renderer) {
        super.setRenderer(renderer == null ? this.defaultRenderer : renderer);
    }

    /**
     * Change the renderer of the items.
     *
     * @param renderer the new renderer.
     */
    public void setRenderer(SerializableFunction<Locale, Component> renderer) {
        setRenderer(renderer == null ? null : new ComponentRenderer<>(renderer));
    }

    /**
     * Change the locale of the UI. This function is provided for subclass overriding.
     *
     * @param locale the new UI locale.
     * @see #setUILocale(Locale)
     */
    @SuppressWarnings("static-method")
    protected void changeUILocale(Locale locale) {
        setUILocale(locale);
    }

    /**
     * Change the language cookie.
     *
     * @param locale the new UI locale.
     */
    @SuppressWarnings("static-method")
    protected void changeLanguageCookie(Locale locale) {
        final var languageCookie = new Cookie(LANGUAGE_COOKIE_NAME, locale.getLanguage());
        languageCookie.setMaxAge(LANGUAGE_COOKIE_DURATION);
        languageCookie.setPath("/"); //$NON-NLS-1$
        VaadinService.getCurrentResponse().addCookie(languageCookie);
    }

    /**
     * This will refresh all items and use the names of each language, translated in the new Locale
     */
    public void refresh() {
        // As soon as Select::refreshItems is public, use that!
        //  see https://github.com/vaadin/flow/issues/6337
        setRenderer(getItemRenderer());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        refresh();
    }

}
