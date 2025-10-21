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

package fr.utbm.ciad.labmanager.utils.io.od;

import com.google.common.base.Strings;
import org.arakhne.afc.vmutil.Resources;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Utility class for creating ODF text document.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.7
 */
@Component
public class OdfTextDocumentHelper {

    /**
     * Constructor without initial template.
     */
    public OdfTextDocumentHelper() {
        //
    }

    /**
     * Open the document that corresponds to the given template.
     *
     * @param templatePath the path to the initial ODT template.
     * @return the ODT document.
     * @throws Exception if it is impossible to create the ODF document.
     */
    @SuppressWarnings("static-method")
    public OdfTextDocument openOdtTemplate(String templatePath) throws Exception {
        if (Strings.isNullOrEmpty(templatePath)) {
            return OdfTextDocument.newTextDocument();
        }
        try (final InputStream is = Resources.getResourceAsStream(templatePath)) {
            return OdfTextDocument.loadDocument(is);
        }
    }

    /**
     * Append the given values, with the given separator to the receiver.
     *
     * @param receiver  the receiver.
     * @param separator the separator of values.
     * @param values    the values to add.
     * @return {@code true} if a value was added.
     */
    @SuppressWarnings("static-method")
    public boolean append(TextPElement receiver, String separator, String... values) {
        assert receiver != null;
        assert separator != null;
        assert values != null;
        var first = true;
        var added = false;
        for (final var value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                if (first) {
                    first = false;
                } else {
                    receiver.newTextNode(separator);
                }
                receiver.newTextNode(value);
                added = true;
            }
        }
        return added;
    }

    /**
     * Append the given value if it is not empty.
     *
     * @param receiver the receiver.
     * @param value    the values to add.
     * @return {@code true} if a value was added.
     */
    public boolean append(TextPElement receiver, String value) {
        return append(receiver, "", value); //$NON-NLS-1$
    }


    /**
     * Replies a text that is underlined.
     *
     * @param odtText the container.
     * @return the formating element.
     */
    @SuppressWarnings("static-method")
    public TextSpanElement newTextUnderline(TextPElement odtText) {
        final var odtSpan = odtText.newTextSpanElement();
        odtSpan.setProperty(StyleTextPropertiesElement.TextUnderlineType, "single"); //$NON-NLS-1$
        odtSpan.setProperty(StyleTextPropertiesElement.TextUnderlineStyle, "solid"); //$NON-NLS-1$
        return odtSpan;
    }

    /**
     * Replies a text that is italic.
     *
     * @param odtText the container.
     * @return the formating element.
     */
    @SuppressWarnings("static-method")
    public TextSpanElement newTextItalic(TextPElement odtText) {
        final var odtSpan = odtText.newTextSpanElement();
        odtSpan.setProperty(StyleTextPropertiesElement.FontStyle, "italic"); //$NON-NLS-1$
        return odtSpan;
    }

    /**
     * Replies a text that is bold.
     *
     * @param odtText the container.
     * @return the formating element.
     */
    @SuppressWarnings("static-method")
    public TextSpanElement newTextBold(TextPElement odtText) {
        final var odtSpan = odtText.newTextSpanElement();
        odtSpan.setProperty(StyleTextPropertiesElement.FontWeight, "bold"); //$NON-NLS-1$
        return odtSpan;
    }

    /**
     * Replies a text that is bold and underlined.
     *
     * @param odtText the container.
     * @return the formating element.
     */
    @SuppressWarnings("static-method")
    public TextSpanElement newTextBoldUnderline(TextPElement odtText) {
        final var odtSpan = odtText.newTextSpanElement();
        odtSpan.setProperty(StyleTextPropertiesElement.FontWeight, "bold"); //$NON-NLS-1$
        odtSpan.setProperty(StyleTextPropertiesElement.TextUnderlineType, "single"); //$NON-NLS-1$
        odtSpan.setProperty(StyleTextPropertiesElement.TextUnderlineStyle, "solid"); //$NON-NLS-1$
        return odtSpan;
    }

    /**
     * Replies the value preceded by its decoration if the value is not {@code null} or empty.
     *
     * @param value     the value to decorate.
     * @param decorator the text to append before the value.
     * @return the decorated value.
     */
    @SuppressWarnings("static-method")
    public String decorateBefore(String value, String decorator) {
        assert decorator != null;
        if (!Strings.isNullOrEmpty(value)) {
            return decorator + value;
        }
        return ""; //$NON-NLS-1$
    }


    /**
     * Replies the string representation of the given number if it is strictly positive.
     *
     * @param number the number.
     * @return the formatted number or {@code null} if the number is negative or nul.
     */
    @SuppressWarnings("static-method")
    public String formatNumberIfStrictlyPositive(float number) {
        if (number > 0f) {
            final var format = new DecimalFormat("#0.000"); //$NON-NLS-1$
            return format.format(number);
        }
        return null;
    }

}
