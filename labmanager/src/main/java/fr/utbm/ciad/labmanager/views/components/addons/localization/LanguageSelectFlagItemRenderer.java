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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;

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
public class LanguageSelectFlagItemRenderer extends AbstractLanguageSelectItemRenderer {

    private static final long serialVersionUID = -2304075478884549020L;

    /**
     * Constructor.
     *
     * @param locale the current locale for displaying the names.
     */
    protected LanguageSelectFlagItemRenderer(Locale locale) {
        super(locale);
    }

    @Override
    public Component apply(Locale item) {
        final var wrapper = new FlexLayout();
        wrapper.setAlignItems(Alignment.CENTER);

        final var countryCode = CountryCode.fromLocale(item);
        final var flag = getLanguageFlag(countryCode);

        wrapper.add(flag);
        return wrapper;
    }

}
