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

package fr.utbm.ciad.labmanager.utils.phone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * International phone number.
 *
 * @author <a target="_blank" href="http://www.arakhne.org/homes/galland.html">St&eacute;phane GALLAND</a>
 * @version 18.0
 * @mavengroupid org.arakhne.afc.core
 * @mavenartifactid util
 * @since 18.0
 */
public class PhoneNumber implements Serializable, Comparable<PhoneNumber>, JsonSerializable {

    private static final long serialVersionUID = 3710363166328372777L;

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(
            // Plus sign
            "^(?<p>\\+?)" //$NON-NLS-1$
                    // International+national prefix
                    + "(:?(?<ip>[0-9A-Za-z]+)[\\[(](?<np>[0-9A-Za-z]+)[\\])])?" //$NON-NLS-1$
                    // Rest of the number
                    + "(?<n>[0-9A-Za-z]+)$"); //$NON-NLS-1$

    private static final String PLUS_PREFIX = "+"; //$NON-NLS-1$

    private static final String OPEN_NAT_PREFIX = "("; //$NON-NLS-1$

    private static final String CLOSE_NAT_PREFIX = ")"; //$NON-NLS-1$

    private static final String SERIALIZATION_SEPARATOR = "/"; //$NON-NLS-1$

    private CountryCode country;

    private String localNumber;

    /**
     * Constructor.
     *
     * @param country     the code of the country, never {@code null}.
     * @param localNumber the local number, without the national prefix, never {@code null} or empty.
     */
    public PhoneNumber(CountryCode country, String localNumber) {
        assert country != null;
        assert !Strings.isNullOrEmpty(localNumber);
        this.country = country;
        this.localNumber = cleanLocalNumber(localNumber);
    }

    /**
     * Format the given number by groups of digits or letters and append it to the given output argument.
     * The function try to create groups of three digits or letters first,
     * then groups of two digits or letters. Groups of digit or letter
     * are separated by a white space.
     *
     * @param number          the number to format, never {@code null}.
     * @param formattedNumber the result of the formatting.
     * @return {@code formattedNumber}
     */
    protected static StringBuilder formatNumberByGroups(CharSequence number, StringBuilder formattedNumber) {
        assert number != null;
        final var size = number.length();
        if (size > 0) {
            final var blockSize = (size % 3) == 0 ? 3 : 2;
            for (var i = 0; i < blockSize; ++i) {
                formattedNumber.append(number.charAt(i));
            }
            for (var i = blockSize; i < size; ) {
                formattedNumber.append(' ');
                for (var j = 0; i < size && j < blockSize; ++i, ++j) {
                    formattedNumber.append(number.charAt(i));
                }
            }
        }
        return formattedNumber;
    }

    /**
     * Parse the given string of characters for extracting a phone number.
     * The accepted forms are those replied by {@link #toNationalForm()},
     * {@link #toInternationalForm()}, {@link #toInternationalFormWithNationalExitPrefix()}
     * and {@link #toInternationalNationalForm()}.
     * This function does not check the consistency of the code country and the national
     * prefix if they are provided both. To validate this consistency, use
     * {@link #parse(String, boolean)}.
     *
     * @param number the string of characters to parse.
     * @return the phone number, never {@code null}.
     * @throws IllegalArgumentException if the given string cannot be parsed for extracting a phone number.
     * @see #parse(String, boolean)
     */
    public static PhoneNumber parse(String number) {
        return parse(number, false);
    }

    /**
     * Parse the given string of characters for extracting a phone number.
     * The accepted forms are those replied by {@link #toNationalForm()},
     * {@link #toInternationalForm()}, {@link #toInternationalFormWithNationalExitPrefix()}
     * and {@link #toInternationalNationalForm()}.
     *
     * @param number            the string of characters to parse.
     * @param checkCodeValidity if {@code true} check if the detected country code and national prefix are coherent if
     *                          they are both provided. If {@code false}, only the country code is considered for determining the phone
     *                          number area.
     * @return the phone number, never {@code null}.
     * @throws IllegalArgumentException if the given string cannot be parsed for extracting a phone number.
     * @see #parse(String)
     */
    public static PhoneNumber parse(String number, boolean checkCodeValidity) {
        if (number != null && number.length() > 0) {
            // Remove irrelevant characters
            final var num = number.replaceAll("[^()\\[\\]+0-9A-Za-z]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
            // Detect the general pattern with all the components of the phone number
            final var matcher = PHONE_NUMBER_PATTERN.matcher(num);
            if (matcher.matches()) {
                final var endNumber = matcher.group("n"); //$NON-NLS-1$
                if (!Strings.isNullOrEmpty(endNumber)) {
                    final var plusSign = matcher.group("p"); //$NON-NLS-1$
                    if (!Strings.isNullOrEmpty(plusSign)) {
                        // Possible formats are:
                        // +00 0000000000
                        // +00 (0) 0000000000
                        final var countryCode = matcher.group("ip"); //$NON-NLS-1$
                        if (Strings.isNullOrEmpty(countryCode)) {
                            assert Strings.isNullOrEmpty(matcher.group("np")); //$NON-NLS-1$
                            return extractCountryCodeAndLocalNumberWithPlus(endNumber);
                        }
                        final var nationalCode = matcher.group("np"); //$NON-NLS-1$
                        final var cc = extractCountryCode(countryCode, nationalCode, checkCodeValidity);
                        return new PhoneNumber(cc, endNumber);
                    }
                    // Possible format is: 0000000000
                    assert Strings.isNullOrEmpty(matcher.group("ip")); //$NON-NLS-1$
                    return extractCountryCodeAndLocalNumber(endNumber);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static int toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static CountryCode extractCountryCode(String countryCode, String nationalCode, boolean checkCodeValidity) {
        final var cc = CountryCode.fromCallingCode(toInt(countryCode));
        if (cc == null) {
            throw new IllegalArgumentException();
        }
        if (checkCodeValidity && !Strings.isNullOrEmpty(nationalCode) && !cc.getNationalPhonePrefix().equals(nationalCode)) {
            throw new IllegalArgumentException();
        }
        return cc;
    }

    private static PhoneNumber extractCountryCodeAndLocalNumber(String fullNumber) {
        assert !Strings.isNullOrEmpty(fullNumber);
        // Search for international format first
        for (final var cc : CountryCode.values()) {
            final var intPrefix = cc.getInternationalPhonePrefix();
            if (!intPrefix.isEmpty() && fullNumber.startsWith(intPrefix)) {
                var rest = fullNumber.substring(intPrefix.length());
                final var code = Integer.toString(cc.getCallingCode());
                if (rest.startsWith(code)) {
                    rest = rest.substring(code.length());
                    return new PhoneNumber(cc, rest);
                }
            }
        }
        // Search for national format at a second step
        for (final var cc : CountryCode.values()) {
            final var natPrefix = cc.getNationalPhonePrefix();
            if (fullNumber.startsWith(natPrefix)) {
                var rest = fullNumber.substring(natPrefix.length());
                return new PhoneNumber(cc, rest);
            }
        }
        throw new IllegalArgumentException();
    }

    private static PhoneNumber extractCountryCodeAndLocalNumberWithPlus(String fullNumber) {
        assert !Strings.isNullOrEmpty(fullNumber);
        // Search for international format first
        for (final var cc : CountryCode.values()) {
            final var code = Integer.toString(cc.getCallingCode());
            if (fullNumber.startsWith(code)) {
                final var rest = fullNumber.substring(code.length());
                return new PhoneNumber(cc, rest);
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Parse the given string of characters as it contains the serialization of a phone number that is
     * created by the function {@link #serialize()}.
     *
     * @param number the string of characters to parse.
     * @return the phone number, never {@code null}.
     * @throws IllegalArgumentException if the given string cannot be parsed for extracting a phone number.
     * @see #serialize()
     */
    public static PhoneNumber unserialize(String number) {
        if (number != null) {
            final var parts = number.split(Pattern.quote(SERIALIZATION_SEPARATOR), 2);
            if (parts.length == 2 && !Strings.isNullOrEmpty(parts[0]) && !Strings.isNullOrEmpty(parts[1])) {
                final var cc = CountryCode.valueOfCaseInsensitive(parts[0]);
                return new PhoneNumber(cc, parts[1]);
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove irrelevant characters from the local number. Only numbers and letters are relevant.
     * This function also remove the national prefix that corresponds to
     * the current country.
     *
     * @param number the number to clean, never {@code null}.
     * @return the same number with only relevant numbers and upper case letters.
     */
    protected String cleanLocalNumber(String number) {
        assert number != null;
        var nnumber = number.toUpperCase();
        nnumber = nnumber.replaceAll("[^0-9A-Za-z]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
        final var natPrefix = this.country.getNationalPhonePrefix();
        if (!Strings.isNullOrEmpty(natPrefix) && nnumber.startsWith(natPrefix) && nnumber.length() > natPrefix.length()) {
            nnumber = nnumber.substring(natPrefix.length());
        }
        return nnumber;
    }

    @Override
    public String toString() {
        return serialize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this != obj && obj instanceof PhoneNumber pn) {
            return this.country == pn.country && this.localNumber.equalsIgnoreCase(pn.localNumber);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.country);
        h = HashCodeUtils.add(h, this.localNumber);
        return h;
    }

    @Override
    public int compareTo(PhoneNumber obj) {
        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = this.country.compareTo(obj.country);
        if (cmp != 0) {
            return cmp;
        }
        return this.localNumber.compareToIgnoreCase(obj.localNumber);
    }

    /**
     * Replies the country of the phone number.
     *
     * @return the country code, never {@code null}.
     */
    public CountryCode getCountry() {
        return this.country;
    }

    /**
     * Change the country of the phone number.
     *
     * @param country the country code, never {@code null}.
     */
    public void setCountry(CountryCode country) {
        assert country != null;
        this.country = country;
    }

    /**
     * Replies the local phone number without the national prefix.
     *
     * @return the local phone number without the national prefix.
     */
    public String getLocalNumber() {
        return this.localNumber;
    }

    /**
     * Change the local phone number without the national prefix.
     *
     * @param localNumber the local phone number without the national prefix, never {@code null} or empty.
     */
    public void setLocalNumber(String localNumber) {
        assert !Strings.isNullOrEmpty(localNumber);
        this.localNumber = cleanLocalNumber(localNumber);
    }

    /**
     * Replies the string representation of the phone number using the national form.
     * It is the local number with the national prefix of the country.
     *
     * <p>Example:
     * <table><thead>
     * <tr><th>Country</th><th>Local number</th><th>National Form</th></tr>
     * </thead><tbody>
     * <tr><td>France (<code>33</code>)</td><td><code>123 456 789</code></td><td><code>01 23 45 67 89</code></td></tr>
     * </tbody></table>
     *
     * @return the national form of this phone number, never {@code null}.
     */
    public String toNationalForm() {
        final var number = new StringBuilder(getCountry().getNationalPhonePrefix());
        number.append(getLocalNumber());
        return formatNumberByGroups(number, new StringBuilder()).toString();
    }

    /**
     * Replies the string representation of the phone number using the international form.
     * It is the local number with the international prefix of the country.
     *
     * <p>Example:
     * <table><thead>
     * <tr><th>Country</th><th>Local number</th><th>International Form</th></tr>
     * </thead><tbody>
     * <tr><td>France (<code>33</code>)</td><td><code>123 456 789</code></td><td><code>+33 123 456 789</code></td></tr>
     * </tbody></table>
     *
     * @return the national form of this phone number, never {@code null}.
     */
    public String toInternationalForm() {
        var number = new StringBuilder(PLUS_PREFIX);
        number.append(getCountry().getCallingCode()).append(' ');
        return formatNumberByGroups(getLocalNumber(), number).toString();
    }

    /**
     * Replies the string representation of the phone number using the international form
     * with the national exit prefix.
     * It is the local number with the national exit prefix, the code of the country.
     *
     * <p>Example:
     * <table><thead>
     * <tr><th>Country</th><th>Local number</th><th>International Form w/ National Exit Prefix</th></tr>
     * </thead><tbody>
     * <tr><td>France (<code>33</code>)</td><td><code>123 456 789</code></td><td><code>0033 123 456 789</code></td></tr>
     * </tbody></table>
     *
     * @return the national form of this phone number, never {@code null}.
     */
    public String toInternationalFormWithNationalExitPrefix() {
        final var cc = getCountry();
        var number = new StringBuilder(cc.getInternationalPhonePrefix());
        number.append(cc.getCallingCode()).append(' ');
        return formatNumberByGroups(getLocalNumber(), number).toString();
    }

    /**
     * Replies the string representation of the phone number using both the international and national forms.
     * It is the local number with the international prefix of the country and the national prefix of the country
     * between parentheses.
     *
     * <p>Example:
     * <table><thead>
     * <tr><th>Country</th><th>Local number</th><th>International+National Form</th></tr>
     * </thead><tbody>
     * <tr><td>France (<code>33</code>)</td><td><code>123 456 789</code></td><td><code>+33 (0) 123 456 789</code></td></tr>
     * </tbody></table>
     *
     * @return the national form of this phone number, never {@code null}.
     */
    public String toInternationalNationalForm() {
        final var cc = getCountry();
        var number = new StringBuilder(PLUS_PREFIX);
        number.append(cc.getCallingCode()).append(' ')
                .append(OPEN_NAT_PREFIX).append(cc.getNationalPhonePrefix())
                .append(CLOSE_NAT_PREFIX).append(' ');
        return formatNumberByGroups(getLocalNumber(), number).toString();
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("country", getCountry()); //$NON-NLS-1$
        generator.writeObjectField("localNumber", getLocalNumber()); //$NON-NLS-1$
        generator.writeObjectField("prefix", getCountry().getCallingCode()); //$NON-NLS-1$
        generator.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        serialize(generator, serializers);
    }

    /**
     * Replies a serialized version of this phone number as a string of characters.
     * This function differs from the other functions such as {@link #toInternationalNationalForm()}
     * because this latter does not output the country itself but its calling code. However,
     * several countries have the same calling code. Consequently, it is impossible to
     * revert totally to a phone number from a string of characters that is created with
     * {@link #toInternationalNationalForm()}. The function {@link #serialize()} provides a
     * solution to this issue.
     *
     * @return the string representation of this phone number.
     * @see #unserialize(String)
     * @see #toInternationalNationalForm()
     */
    public String serialize() {
        return getCountry().name() + SERIALIZATION_SEPARATOR + getLocalNumber();
    }

}
