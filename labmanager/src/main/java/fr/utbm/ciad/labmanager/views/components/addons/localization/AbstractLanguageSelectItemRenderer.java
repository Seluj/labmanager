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
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableFunction;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;

import java.util.Locale;

/**
 * Item renderer for the language select that is rendering only the flag and not the language name.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLanguageSelectItemRenderer implements SerializableFunction<Locale, Component> {

    private static final long serialVersionUID = 3923448249054863389L;

    private final Locale locale;

    /**
     * Constructor.
     *
     * @param locale the current locale for displaying the names.
     */
    protected AbstractLanguageSelectItemRenderer(Locale locale) {
        this.locale = locale;
    }

    /**
     * Replies the component corresponding to the language flag.
     *
     * @param language the code of the country/anguage
     * @return the flag icon resource.
     */
    @SuppressWarnings("static-method")
    protected CountryFlag getLanguageFlag(CountryCode language) {
        final CountryFlag flag = new CountryFlag(language);
        flag.addClassName("language-select-flag"); //$NON-NLS-1$
        flag.setSizeFromHeight(2, Unit.EX);
        return flag;
    }

    /**
     * Replies the name of the language.
     *
     * @param language the code of the country/anguage
     * @return the language name component.
     */
    protected Div getLanguageName(CountryCode language) {
        final var name = new Div();
        name.setText(language.getDisplayLanguage(this.locale));
        name.addClassName("language-select-name"); //$NON-NLS-1$
        name.getStyle().set("margin-left", "5px"); //$NON-NLS-1$ //$NON-NLS-2$
        return name;
    }

}
