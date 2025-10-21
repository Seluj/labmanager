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

package fr.utbm.ciad.wprest.views;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract implementation of a JEE Controller that provides views to an external website.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractExternalView extends AbstractComponent {

    private static final long serialVersionUID = 4004274491609719167L;

    private static final String EMAIL_PATTERN_VALUE = "^([^@]+)@(.*?)\\.([^\\.]+)$"; //$NON-NLS-1$

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_VALUE);

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public AbstractExternalView(MessageSourceAccessor messages, ConfigurationConstants constants) {
        super(messages, constants);
    }

    /**
     * Fill the attributes of the given model-view with the standard properties.
     * The added object in the model are: {@code uuid} and {@code changeEnabled}.
     *
     * @param modelAndView  the model-view
     * @param embedded      indicates if the view will be embedded into a larger page, e.g., WordPress page.
     * @param changeEnabled indicates if the change of the view data is enabled or not.
     */
    protected void initModelViewWithInternalProperties(ModelAndView modelAndView, boolean embedded, boolean changeEnabled) {
        modelAndView.addObject("uuid", generateUUID()); //$NON-NLS-1$
        modelAndView.addObject("changeEnabled", Boolean.valueOf(changeEnabled)); //$NON-NLS-1$
        modelAndView.addObject("isEmbeddedPage", Boolean.valueOf(embedded)); //$NON-NLS-1$
    }

    /**
     * Fill the attributes that describe the components of an obfuscated email.
     *
     * @param values the attribute values to fill up.
     * @param email  the email to obfuscate.
     * @param id     the identifier to add to the attribute names for making them unique.
     */
    @SuppressWarnings("static-method")
    protected void addObfuscatedEmailFields(Map<String, Object> values, String email, String id) {
        if (!Strings.isNullOrEmpty(email)) {
            final Matcher matcher = EMAIL_PATTERN.matcher(email);
            if (matcher.matches()) {
                final String name = matcher.group(1);
                final String name0;
                final String name1;
                if (name.length() >= 2) {
                    final int nl = name.length() / 2;
                    name0 = name.substring(0, nl);
                    name1 = name.substring(nl);
                } else {
                    name0 = name;
                    name1 = ""; //$NON-NLS-1$
                }
                final String domain = matcher.group(2);
                final String tld = matcher.group(3);
                final String rid = Strings.isNullOrEmpty(id) ? "" : ("_" + id); //$NON-NLS-1$ //$NON-NLS-2$
                values.put("obfuscatedEmailName0" + rid, name0); //$NON-NLS-1$
                values.put("obfuscatedEmailName1" + rid, name1); //$NON-NLS-1$
                values.put("obfuscatedEmailDomain" + rid, domain); //$NON-NLS-1$
                values.put("obfuscatedEmailTld" + rid, tld); //$NON-NLS-1$
            }
        }
    }

    /**
     * Fill the attributes that describe the components of an obfuscated phone number.
     *
     * @param values the attribute values to fill up.
     * @param phone  the phone to obfuscate.
     * @param id     the identifier to add to the attribute names for making them unique.
     */
    @SuppressWarnings("static-method")
    protected void addObfuscatedPhoneFields(Map<String, Object> values, String phone, String id) {
        if (!Strings.isNullOrEmpty(phone)) {
            final String phone0;
            final String phone1;
            final String phone2;
            final String phone3;
            if (phone.length() >= 4) {
                final int nl = phone.length() / 4;
                int n = 0;
                phone0 = phone.substring(0, nl);
                n += nl;
                phone1 = phone.substring(n, n + nl);
                n += nl;
                phone2 = phone.substring(n, n + nl);
                n += nl;
                phone3 = phone.substring(n);
            } else if (phone.length() == 3) {
                phone0 = ""; //$NON-NLS-1$
                phone1 = phone.substring(0, 2);
                phone2 = ""; //$NON-NLS-1$
                phone3 = phone.substring(2, 3);
            } else {
                phone0 = ""; //$NON-NLS-1$
                phone1 = ""; //$NON-NLS-1$
                phone2 = phone;
                phone3 = ""; //$NON-NLS-1$
            }
            final String rid = Strings.isNullOrEmpty(id) ? "" : ("_" + id); //$NON-NLS-1$ //$NON-NLS-2$
            values.put("obfuscatedPhone0" + rid, phone0); //$NON-NLS-1$
            values.put("obfuscatedPhone1" + rid, phone1); //$NON-NLS-1$
            values.put("obfuscatedPhone2" + rid, phone2); //$NON-NLS-1$
            values.put("obfuscatedPhone3" + rid, phone3); //$NON-NLS-1$
        }
    }

    /**
     * Fill the attributes that describe the components of an obfuscated email.
     *
     * @param modelAndView the model-view object to fill up.
     * @param values       the attribute values to fill up.
     */
    @SuppressWarnings("static-method")
    protected void addObfuscatedValues(ModelAndView modelAndView, Map<String, Object> values) {
        modelAndView.addObject("obfuscatedValues", values); //$NON-NLS-1$
    }

}
