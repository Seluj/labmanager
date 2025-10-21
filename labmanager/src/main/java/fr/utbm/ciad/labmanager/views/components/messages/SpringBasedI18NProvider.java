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

package fr.utbm.ciad.labmanager.views.components.messages;

import com.google.common.base.Strings;
import com.vaadin.flow.i18n.DefaultI18NProvider;
import fr.utbm.ciad.labmanager.configuration.messages.BaseMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Provider of I18N messages for Vaadin. This providers is linked to the {@link BaseMessageSource Spring message source}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class SpringBasedI18NProvider extends DefaultI18NProvider {

    private static final long serialVersionUID = 7109279618234895282L;

    private static final List<Locale> LOCALES;

    static {
        LOCALES = Arrays.asList(Locale.US, Locale.FRANCE);
    }

    private final BaseMessageSource springMessageSource;

    /**
     * Constructor.
     *
     * @param springMessageSource the spring message source.
     */
    public SpringBasedI18NProvider(@Autowired BaseMessageSource springMessageSource) {
        super(LOCALES);
        this.springMessageSource = springMessageSource;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        assert !Strings.isNullOrEmpty(key);
        final var concreteLocale = locale == null ? Locale.getDefault() : locale;
        try {
            return this.springMessageSource.getMessageSource().getMessage(key, params, concreteLocale);
        } catch (NoSuchMessageException ex) {
            return super.getTranslation(key, locale, params);
        }
    }

}
