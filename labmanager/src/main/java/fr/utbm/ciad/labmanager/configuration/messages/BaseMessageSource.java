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

package fr.utbm.ciad.labmanager.configuration.messages;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * Base implementation of a message source accessor, outside Spring run.
 * This source is implemented for enabling the enumeration types to use
 * the localized messages.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Configuration
public class BaseMessageSource {

    private static final String TRANSLATION_BASEFILE = "classpath:labmanager-i18n/translations"; //$NON-NLS-1$

    private static MessageSourceAccessor GLOBAL_ACCESSOR;

    /**
     * Replies a global message accessor. This function should not be invoked directly, please
     * inject the {@link MessageSourceAccessor} bean in your code.
     *
     * @return the global message accessor.
     */
    public static MessageSourceAccessor getGlobalMessageAccessor() {
        MessageSourceAccessor acc = null;
        synchronized (BaseMessageSource.class) {
            if (GLOBAL_ACCESSOR == null) {
                GLOBAL_ACCESSOR = createMessageSourceAccessor(createMessageSource());
            }
            acc = GLOBAL_ACCESSOR;
        }
        return acc;
    }

    /**
     * Create a well-configured message source.
     *
     * @return the message source.
     */
    protected static ReloadableResourceBundleMessageSource createMessageSource() {
        final var res = new ReloadableResourceBundleMessageSource();
        res.setFallbackToSystemLocale(false);
        res.addBasenames(TRANSLATION_BASEFILE);
        return res;
    }

    /**
     * Create a well-configured message source accessor.
     *
     * @param source the message source.
     * @return the message source accessor.
     */
    protected static MessageSourceAccessor createMessageSourceAccessor(MessageSource source) {
        return new MessageSourceAccessor(source, Locale.US);
    }

    /**
     * Replies the source of the localized messages.
     *
     * @return the manager of the messages.
     */
    @SuppressWarnings("static-method")
    @Bean(name = "messageSource")
    public MessageSource getMessageSource() {
        return createMessageSource();
    }

    /**
     * Replies the tool for accessing to the localized source that is provided as argument.
     *
     * @param messageSource the source to embed.
     * @return the accessor to the message source.
     */
    @SuppressWarnings("static-method")
    @Bean
    public MessageSourceAccessor getMessageSourceAccessor(final MessageSource messageSource) {
        return createMessageSourceAccessor(messageSource);
    }

}
