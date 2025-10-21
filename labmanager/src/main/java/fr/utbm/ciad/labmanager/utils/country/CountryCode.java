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

package fr.utbm.ciad.labmanager.utils.country;

import com.google.common.base.Strings;
import org.eclipse.xtext.xbase.lib.Pure;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.stream.Collectors;

import static fr.utbm.ciad.labmanager.utils.country.DefaultLanguages.*;

/**
 * This enumeration gives a list of countries with the official codes of the countries
 * given by the ISO 3166-1, and the phone prefix information that is provided by
 * International Telecommunication Union (ITU) and defined in the ITU-T ISO standards E.123 and E.164.
 *
 * @author <a target="_blank" href="http://www.arakhne.org/homes/galland.html">St&eacute;phane GALLAND</a>
 * @version 17.0 2020-01-04 14:51:19
 * @mavengroupid org.arakhne.afc.core
 * @mavenartifactid util
 * @since 14.0
 */
public enum CountryCode {

    /**
     * AF.
     */
    AFGHANISTAN("af", PERSIAN, 93, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AX.
     */
    ALAND_ISLANDS("ax", FINNISH, 358, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return FINLAND;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AL.
     */
    ALBANIA("al", ALBANIAN, 355, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * DZ.
     */
    ALGERIA("dz", ARABIC, 213, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AS.
     */
    AMERICAN_SAMOA("as", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AD.
     */
    ANDORRA("ad", CATALAN, 376, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * AO.
     */
    ANGOLA("ao", PORTUGUESE, 244, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AI.
     */
    ANGUILLA("ai", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AQ.
     */
    ANTARCTICA("aq", Locale.US, 0, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AG.
     */
    ANTIGUA_AND_BARBUDA("ag", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AR.
     */
    ARGENTINA("ar", SPANNISH, 54, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AM.
     */
    ARMENIA("am", ARMENIAN, 374, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * AW.
     */
    ARUBA("aw", DUTCH, 297, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return NETHERLANDS;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AU.
     */
    AUSTRALIA("au", Locale.UK, 61, "0011", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AT.
     */
    AUSTRIA("at", Locale.GERMAN, 43, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * AZ.
     */
    AZERBAIJAN("az", AZERBAIJANI, 994, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * BS.
     */
    BAHAMAS("bs", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BH.
     */
    BAHRAIN("bh", ARABIC, 973, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BD.
     */
    BANGLADESH("bd", BENGALI, 880, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * BB.
     */
    BARBADOS("bb", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BY.
     */
    BELARUS("by", RUSSIAN, 375, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BE.
     */
    BELGIUM("be", DUTCH, 32, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BZ.
     */
    BELIZE("bz", Locale.UK, 501, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BJ.
     */
    BENIN("bj", Locale.FRENCH, 229, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BM.
     */
    BERMUDA("bm", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BT.
     */
    BHUTAN("BT", DZONGHKHA, 975, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * BO.
     */
    BOLIVIA("bo", SPANNISH, 591, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BQ.
     */
    SAINT_EUSTATIUS_AND_SABA_BONAIRE("bq", DUTCH, 599, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return NETHERLANDS;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BA.
     */
    BOSNIA_AND_HERZEGOVINA("ba", BOSNIAN, 387, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * BW.
     */
    BOTSWANA("bw", Locale.UK, 267, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BV.
     */
    BOUVET_ISLAND("BV", NORWEGIAN, 47, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return NORWAY;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BR.
     */
    BRAZIL("br", PORTUGUESE, 55, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * IO.
     */
    BRITISH_INDIAN_OCEAN_TERRITORY("io", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BN.
     */
    BRUNEI_DARUSSALAM("bn", Locale.UK, 673, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BG.
     */
    BULGARIA("bg", BULGARIAN, 359, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * BF.
     */
    BURKINA_FASO("bf", Locale.FRENCH, 226, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * BI.
     */
    BURUNDI("bi", Locale.FRENCH, 257, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * KH.
     */
    CAMBODIA("kh", KHMER, 855, "007", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * CM.
     */
    CAMEROON("cm", Locale.FRENCH, 237, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CA.
     */
    CANADA("ca", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CV.
     */
    CAPE_VERDE("cv", PORTUGUESE, 238, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KY.
     */
    CAYMAN_ISLANDS("ky", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CF.
     */
    CENTRAL_AFRICAN_REPUBLIC("cf", Locale.FRENCH, 236, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TD.
     */
    CHAD("td", ARABIC, 235, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CL.
     */
    CHILE("cl", SPANNISH, 56, "1YZ0", "1YZ") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CN.
     */
    CHINA("cn", Locale.CHINESE, 86, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * CX.
     */
    CHRISTMAS_ISLAND("cx", Locale.UK, 672, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return AUSTRALIA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CC.
     */
    COCOS_ISLANDS("cc", Locale.UK, 672, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return AUSTRALIA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CO.
     */
    COLOMBIA("co", SPANNISH, 57, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KM.
     */
    COMOROS("km", Locale.FRENCH, 269, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CG.
     */
    CONGO("cg", Locale.FRENCH, 242, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CD.
     */
    CONGO_DEMOCRATIC_REPUBLIC("cd", Locale.FRENCH, 243, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CK.
     */
    COOK_ISLANDS("ck", Locale.UK, 682, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CR.
     */
    COSTA_RICA("cr", SPANNISH, 506, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CI.
     */
    COTE_D_IVOIRE("ci", Locale.FRENCH, 225, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * HR.
     */
    CROATIA("hr", CROATIAN, 385, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * CU.
     */
    CUBA("cu", SPANNISH, 53, "119", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CW.
     */
    CURACAO("cw", DUTCH, 599, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return NETHERLANDS;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CY.
     */
    CYPRUS("cy", GREEK, 357, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * CZ.
     */
    CZECH_REPUBLIC("cz", CZECH, 420, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * DK.
     */
    DENMARK("dk", DANISH, 45, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * DJ.
     */
    DJIBOUTI("dj", ARABIC, 253, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * DM.
     */
    DOMINICA("dm", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * DO.
     */
    DOMINICAN_REPUBLIC("do", SPANNISH, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * EC.
     */
    ECUADOR("ec", SPANNISH, 593, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * EG.
     */
    EGYPT("eg", ARABIC, 20, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SV.
     */
    EL_SALVADOR("sv", SPANNISH, 503, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GQ.
     */
    EQUATORIAL_GUINEA("gq", Locale.FRENCH, 240, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * ER.
     */
    ERITREA("er", TIGRINYA, 291, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * EE.
     */
    ESTONIA("ee", ESTONIAN, 372, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * ET.
     */
    ETHIOPIA("et", OROMO, 251, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * FK.
     */
    FALKLAND_ISLANDS("fk", Locale.UK, 500, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * FO.
     */
    FAROE_ISLANDS("fo", DANISH, 298, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return DENMARK;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * FJ.
     */
    FIJI("fj", Locale.UK, 679, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * FI.
     */
    FINLAND("fi", FINNISH, 358, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * FR.
     */
    FRANCE("fr", Locale.FRENCH, 33, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * GF.
     */
    FRENCH_GUIANA("gf", Locale.FRENCH, 594, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PF.
     */
    FRENCH_POLYNESIA("pf", Locale.FRENCH, 689, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TF.
     */
    FRENCH_SOUTHERN_TERRITORIES("tf", Locale.FRENCH, 262, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * GA.
     */
    GABON("ga", Locale.FRENCH, 241, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GM.
     */
    GAMBIA("gm", Locale.UK, 220, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GE.
     */
    GEORGIA("ge", GEORGIAN, 995, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * DE.
     */
    GERMANY("de", Locale.GERMAN, 49, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * GH.
     */
    GHANA("ghg", Locale.UK, 233, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GI.
     */
    GIBRALTAR("gi", Locale.UK, 350, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GR.
     */
    GREECE("gr", GREEK, 30, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * GL.
     */
    GREENLAND("gl", DANISH, 299, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return DENMARK;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GD.
     */
    GRENADA("gd", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GP.
     */
    GUADELOUPE("gp", Locale.FRENCH, 590, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GU.
     */
    GUAM("gu", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GT.
     */
    GUATEMALA("gt", SPANNISH, 502, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GG.
     */
    GUERNSEY("GG", Locale.UK, 44, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GN.
     */
    GUINEA("gn", Locale.FRENCH, 224, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GW.
     */
    GUINEA_BISSAU("gw", PORTUGUESE, 245, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GY.
     */
    GUYANA("gy", Locale.UK, 592, "001", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * HT.
     */
    HAITI("ht", Locale.FRENCH, 509, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * HM.
     */
    HEARD_ISLAND_AND_MCDONALD_ISLANDS("hm", Locale.UK, 672, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return AUSTRALIA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * HN.
     */
    HONDURAS("hn", SPANNISH, 504, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * HK.
     */
    HONG_KONG("hk", Locale.CHINESE, 852, "001", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return CHINA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * HU.
     */
    HUNGARY("hu", HUNGARIAN, 36, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * IS.
     */
    ICELAND("is", ICELANDIC, 354, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * IN.
     */
    INDIA("in", HINDI, 91, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * ID.
     */
    INDONESIA("id", INDONESIAN, 62, "001", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * IR.
     */
    IRAN("ir", PERSIAN, 98, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * IQ.
     */
    IRAQ("iq", ARABIC, 964, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * IE.
     */
    IRELAND("ie", Locale.UK, 353, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * IM.
     */
    ISLE_OF_MAN("im", Locale.UK, 44, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * IL.
     */
    ISRAEL("il", HEBREW, 972, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * IT.
     */
    ITALY("it", Locale.ITALIAN, 39, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * JM.
     */
    JAMAICA("jm", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * JP.
     */
    JAPAN("jp", Locale.JAPANESE, 81, "010", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * JE.
     */
    JERSEY("je", Locale.UK, 44, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * JO.
     */
    JORDAN("jo", ARABIC, 962, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * KZ.
     */
    KAZAKHSTAN("kz", KAZAKH, 7, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * KE.
     */
    KENYA("ke", Locale.UK, 254, "000", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KI.
     */
    KIRIBATI("ki", Locale.UK, 686, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KP.
     */
    KOREA_DEMOCRATIC_PEOPLE_REPUBLIC("kp", Locale.KOREAN, 850, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KR.
     */
    KOREA_REPUBLIC("kr", Locale.KOREAN, 82, "001.002", "0.082") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * KW.
     */
    KUWAIT("kw", ARABIC, 965, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KG.
     */
    KYRGYZSTAN("kg", KYRGYZ, 996, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * LA.
     */
    LAO("la", DefaultLanguages.LAO, 856, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * LV.
     */
    LATVIA("lv", LATVIAN, 371, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * LB.
     */
    LEBANON("lb", ARABIC, 961, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LS.
     */
    LESOTHO("ls", Locale.UK, 266, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LR.
     */
    LIBERIA("lr", Locale.UK, 231, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LY.
     */
    LIBYAN_ARAB_JAMAHIRIYA("ly", ARABIC, 218, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LI.
     */
    LIECHTENSTEIN("li", Locale.GERMAN, 423, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LT.
     */
    LITHUANIA("lt", LITHUANIAN, 370, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * LU.
     */
    LUXEMBOURG("lu", LUXEMBOURGISH, 352, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * MO.
     */
    MACAO("mo", Locale.CHINESE, 853, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return CHINA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MK.
     */
    MACEDONIA("mk", MACEDONIAN, 389, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * MG.
     */
    MADAGASCAR("mg", Locale.FRENCH, 261, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MW.
     */
    MALAWI("mw", Locale.UK, 265, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MY.
     */
    MALAYSIA("my", MALAY, 60, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * MV.
     */
    MALDIVES("mv", DHIVEHI, 960, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * ML.
     */
    MALI("ml", Locale.FRENCH, 223, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MT.
     */
    MALTA("mt", MALTESE, 356, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * MH.
     */
    MARSHALL_ISLANDS("mh", Locale.UK, 692, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MQ.
     */
    MARTINIQUE("mq", Locale.FRENCH, 596, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MR.
     */
    MAURITANIA("mr", ARABIC, 222, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MU.
     */
    MAURITIUS("mu", Locale.UK, 230, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * YT.
     */
    MAYOTTE("yt", Locale.FRENCH, 262, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MX.
     */
    MEXICO("mx", SPANNISH, 52, null, "01") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * FM.
     */
    MICRONESIA("fm", Locale.UK, 691, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MD.
     */
    MOLDOVA("md", ROMANIAN, 373, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MC.
     */
    MONACO("mc", Locale.FRENCH, 377, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MN.
     */
    MONGOLIA("mn", MONGOLIAN, 976, "001", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * ME.
     */
    MONTENEGRO("me", SERBIAN, 382, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MS.
     */
    MONTSERRAT("mq", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MA.
     */
    MOROCCO("ma", ARABIC, 212, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MZ.
     */
    MOZAMBIQUE("mz", PORTUGUESE, 258, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MM.
     */
    MYANMAR("mm", BURMESE, 95, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * NA.
     */
    NAMIBIA("na", Locale.UK, 264, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NR.
     */
    NAURU("nr", Locale.UK, 674, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NP.
     */
    NEPAL("np", NEPALI, 977, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * NL.
     */
    NETHERLANDS("nl", DUTCH, 31, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * NC.
     */
    NEW_CALEDONIA("nc", Locale.FRENCH, 687, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NZ.
     */
    NEW_ZEALAND("nz", Locale.UK, 64, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NI.
     */
    NICARAGUA("no", SPANNISH, 505, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NE.
     */
    NIGER("ne", Locale.FRENCH, 227, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NG.
     */
    NIGERIA("ng", Locale.UK, 234, "009", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NU.
     */
    NIUE("nu", Locale.UK, 683, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NF.
     */
    NORFOLK_ISLAND("nf", Locale.UK, 672, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return AUSTRALIA;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MP.
     */
    NORTHERN_MARIANA_ISLANDS("mp", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * NO.
     */
    NORWAY("no", NORWEGIAN, 47, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * OM.
     */
    OMAN("om", ARABIC, 968, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * PK.
     */
    PAKISTAN("pk", URDU, 92, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * PW.
     */
    PALAU("pw", Locale.UK, 680, "011", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PS.
     */
    PALESTINIAN_TERRITORY("ps", ARABIC, 970, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PA.
     */
    PANAMA("pa", SPANNISH, 507, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PG.
     */
    PAPUA_NEW_GUINEA("pg", Locale.UK, 675, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PY.
     */
    PARAGUAY("py", SPANNISH, 595, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PE.
     */
    PERU("pe", SPANNISH, 51, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PH.
     */
    PHILIPPINES("ph", FILIPINO, 63, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * PN. TDOO
     */
    PITCAIRN("pn", Locale.UK, 0, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PL.
     */
    POLAND("pl", POLISH, 48, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * PT.
     */
    PORTUGAL("pt", PORTUGUESE, 351, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * PR.
     */
    PUERTO_RICO("pr", SPANNISH, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * QA.
     */
    QATAR("qa", ARABIC, 974, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * RE.
     */
    REUNION("RE", Locale.FRENCH, 262, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * RO.
     */
    ROMANIA("ro", ROMANIAN, 40, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * RU.
     */
    RUSSIAN_FEDERATION("ru", RUSSIAN, 7, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * RW.
     */
    RWANDA("rw", Locale.UK, 250, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * BL.
     */
    SAINT_BARTHELEMY("bl", Locale.FRENCH, 590, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SH.
     */
    SAINT_HELENA("sh", Locale.UK, 247, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * KN.
     */
    SAINT_KITTS_AND_NEVIS("kn", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * LC.
     */
    SAINT_LUCIA("lc", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * PM.
     */
    SAINT_PIERRE_AND_MIQUELON("pm", Locale.FRENCH, 508, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * VC.
     */
    SAINT_VINCENT_AND_THE_GRENADINES("vc", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * WS.
     */
    SAMOA("ws", Locale.UK, 685, "0", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SM.
     */
    SAN_MARINO("sm", Locale.ITALIAN, 378, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * MF.
     */
    SAINT_MARTIN_FRENCH_SIDE("mf", Locale.FRENCH, 590, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * ST.
     */
    SAO_TOME_AND_PRINCIPE("st", PORTUGUESE, 239, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SA.
     */
    SAUDI_ARABIA("sa", ARABIC, 966, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * SN.
     */
    SENEGAL("sn", Locale.FRENCH, 221, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * RS.
     */
    SERBIA("rs", SERBIAN, 381, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * SC.
     */
    SEYCHELLES("sc", Locale.FRENCH, 248, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SL.
     */
    SIERRA_LEONE("sl", Locale.UK, 232, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SG.
     */
    SINGAPORE("sg", Locale.UK, 65, "001.008", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SX.
     */
    SINT_MAARTEN_DUTCH_SIDE("sx", DUTCH, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return NETHERLANDS;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SK.
     */
    SLOVAKIA("sk", SLOVAK, 421, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * SI.
     */
    SLOVENIA("si", SLOVENIAN, 386, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * SB.
     */
    SOLOMON_ISLANDS("sb", Locale.UK, 677, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SO.
     */
    SOMALIA("so", ARABIC, 252, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * ZA.
     */
    SOUTH_AFRICA("za", ZULU, 27, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * GS.
     */
    SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("GS", Locale.UK, 500, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * ES.
     */
    SPAIN("es", SPANNISH, 34, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * LK.
     */
    SRI_LANKA("lk", SINHALA, 94, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * SD.
     */
    SUDAN("sd", ARABIC, 249, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SR.
     */
    SURINAME("sr", DUTCH, 597, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SJ.
     */
    SVALBARD_AND_JAN_MAYEN("sj", NORWEGIAN, 47, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return NORWAY;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SZ.
     */
    SWAZILAND("sz", Locale.UK, 268, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SE.
     */
    SWEDEN("se", SWEDISH, 46, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * CH.
     */
    SWITZERLAND("ch", Locale.GERMAN, 41, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * SY.
     */
    SYRIAN_ARAB_REPUBLIC("sy", ARABIC, 963, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * TW.
     */
    TAIWAN("tw", Locale.CHINESE, 886, "002", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TJ.
     */
    TAJIKISTAN("tj", TAJIK, 992, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * TZ.
     */
    TANZANIA("tz", SWAHILI, 255, "000", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * TH.
     */
    THAILAND("th", THAI, 66, "001", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * TL.
     */
    TIMOR_LESTE("tl", PORTUGUESE, 670, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TG.
     */
    TOGO("tg", Locale.FRENCH, 228, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TK.
     */
    TOKELAU("tk", Locale.UK, 690, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return NEW_ZEALAND;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TO.
     */
    TONGA("to", Locale.UK, 676, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TT.
     */
    TRINIDAD_AND_TOBAGO("tt", Locale.UK, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TN.
     */
    TUNISIA("tn", ARABIC, 216, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TR.
     */
    TURKEY("tr", TURKISH, 90, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * TM.
     */
    TURKMENISTAN("tm", TURKMEN, 993, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * TC.
     */
    TURKS_AND_CAICOS_ISLANDS("tc", Locale.UK, 1, "0", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * TV.
     */
    TUVALU("tv", Locale.UK, 688, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * UG.
     */
    UGANDA("ug", Locale.UK, 256, "000", null) { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * UA.
     */
    UKRAINE("ua", UKRAINIAN, 380, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * AE.
     */
    UNITED_ARAB_EMIRATES("ae", ARABIC, 971, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * GB.
     */
    UNITED_KINGDOM("gb", Locale.UK, 44, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * US.
     */
    UNITED_STATES("us", Locale.US, 1, "011", "1") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * UM. TDOO
     */
    UNITED_STATES_MINOR_OUTLYING_ISLANDS("um", Locale.US, 0, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * UY.
     */
    URUGUAY("uy", SPANNISH, 598, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * UZ.
     */
    UZBEKISTAN("uz", UZBEK, 998, "810", "8") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },

    /**
     * VU.
     */
    VANUATU("vu", Locale.UK, 678, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * VA.
     */
    VATICAN_CITY_STATE("va", Locale.ITALIAN, 379, "", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return true;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * VE.
     */
    VENEZUELA("ve", SPANNISH, 58, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * VN.
     */
    VIETNAM("vn", VIETNAMESE, 84, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return true;
        }
    },
    /**
     * VG.
     */
    VIRGIN_ISLANDS_BRITISH_SIDE("vg", Locale.UK, 284, "011", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_KINGDOM;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * VI.
     */
    VIRGIN_ISLANDS_US_SIDE("vi", Locale.US, 340, "011", "") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        @Override
        public CountryCode getSovereignCountry() {
            return UNITED_STATES;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return true;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * WF.
     */
    WALLIS_AND_FUTUNA("wf", Locale.FRENCH, 681, null, "") { //$NON-NLS-1$ //$NON-NLS-2$

        @Override
        public CountryCode getSovereignCountry() {
            return FRANCE;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return true;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * EH.
     */
    WESTERN_SAHARA("eh", ARABIC, 212, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return MOROCCO;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * Y.
     */
    YEMEN("y", ARABIC, 967, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return true;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return false;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },

    /**
     * ZM.
     */
    ZAMBIA("zm", Locale.UK, 260, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    },
    /**
     * ZW.
     */
    ZIMBABWE("zw", Locale.ENGLISH, 263, null, null) { //$NON-NLS-1$

        @Override
        public CountryCode getSovereignCountry() {
            return null;
        }

        @Override
        public boolean isEuropeanContinent() {
            return false;
        }

        @Override
        public boolean isNorthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isSouthAmericanContinent() {
            return false;
        }

        @Override
        public boolean isAsianContinent() {
            return false;
        }

        @Override
        public boolean isOceanicContinent() {
            return false;
        }

        @Override
        public boolean isAfricanContinent() {
            return true;
        }

        @Override
        public boolean isLanguageSource() {
            return false;
        }
    };

    private static final Map<String, List<CountryCode>> COUNTRY_LIST = new HashMap<>();

    private static final Map<String, List<CountryCode>> LANGUAGE_LIST = new HashMap<>();

    private final String code;

    private final Locale locale;

    private final int callingCode;

    private final String internationalPhonePrefix;

    private final String nationalPhonePrefix;

    CountryCode(String isoCode, Locale predefinedLocale, int callingCode, String internationalPhonePrefix, String nationalPhonePrefix) {
        this.code = isoCode;
        this.locale = predefinedLocale;
        this.callingCode = callingCode;
        this.internationalPhonePrefix = internationalPhonePrefix;
        this.nationalPhonePrefix = nationalPhonePrefix;
    }

    /**
     * Replies the country code for the given locale.
     *
     * @param locale the locale.
     * @return the country code, or {@code null} if there is no country code for the locale.
     */
    @Pure
    public static CountryCode fromLocale(Locale locale) {
        if (locale != null) {
            final var c = locale.getCountry();
            for (final var cc : CountryCode.values()) {
                if (cc.code.equalsIgnoreCase(c)) {
                    return cc;
                }
            }
        }
        return null;
    }

    /**
     * Replies the country code for the given calling code.
     * This function replies the first country that has the given
     * calling code, in the case these is multiple country with the
     * same calling code.
     *
     * @param code the calling code.
     * @return the country code, or {@code null} if no country was found.
     */
    @Pure
    public static CountryCode fromCallingCode(int code) {
        for (final var cc : CountryCode.values()) {
            if (cc.callingCode == code) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Replies the country code that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the code, to search for.
     * @return the code.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static CountryCode valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var code : CountryCode.values()) {
                if (name.equalsIgnoreCase(code.name())) {
                    return code;
                }
            }
        }
        throw new IllegalArgumentException("Invalid country code: " + name); //$NON-NLS-1$
    }

    /**
     * Replies all the display names of the countries.
     *
     * @param locale the locale to use for obtaining the names of the languages.
     * @return the display names of the countries.
     * @see #getAllDisplayLanguages()
     */
    public static List<CountryCode> getAllDisplayCountries(Locale locale) {
        synchronized (CountryCode.class) {
            final var languageKey = locale.getLanguage();
            List<CountryCode> list = COUNTRY_LIST.get(languageKey);
            if (list == null) {
                final Map<String, CountryCode> selection = new TreeMap<>();
                for (final var code : values()) {
                    if (code.getSovereignCountry() == null) {
                        final var codeLocale = code.getCountryLocale();
                        var language = codeLocale.getDisplayCountry(locale).toLowerCase(locale);
                        selection.putIfAbsent(language, code);
                    }
                }
                list = selection.values().stream().sorted(
                                (a, b) -> a.getDisplayCountry(locale).compareToIgnoreCase(b.getDisplayCountry(locale)))
                        .collect(Collectors.toList());
                COUNTRY_LIST.put(languageKey, list);
            }
            return list;
        }
    }

    /**
     * Replies all the language names of the countries.
     *
     * <p>The list of languages may be shorter than the list of countries
     * provided by {@link #getAllDisplayCountries()} because
     * many countries are speaking the same language.
     * In this case, the first country code in the list that is
     * associated to a {@link #getSovereignCountry() sovereign}
     * country is associated to the language.
     *
     * <p>The replied list is sorted by alphabetical order.
     *
     * @param locale the locale to use for obtaining the names of the languages.
     * @return the display names of the languages.
     * @since 4.0
     */
    public static List<CountryCode> getAllDisplayLanguages(Locale locale) {
        synchronized (CountryCode.class) {
            final var languageKey = locale.getLanguage();
            List<CountryCode> list = LANGUAGE_LIST.get(languageKey);
            if (list == null) {
                final Map<String, CountryCode> selection = new TreeMap<>();
                for (final var code : values()) {
                    if (code.isLanguageSource()) {
                        final var codeLocale = code.getLanguageLocale();
                        var language = codeLocale.getDisplayLanguage(locale).toLowerCase(locale);
                        selection.putIfAbsent(language, code);
                    }
                }
                list = selection.values().stream().sorted(
                                (a, b) -> a.getDisplayLanguage(locale).compareToIgnoreCase(b.getDisplayLanguage(locale)))
                        .collect(Collectors.toList());
                LANGUAGE_LIST.put(languageKey, list);
            }
            return list;
        }
    }

    /**
     * Replies the default country code.
     *
     * @return the default country code.
     */
    public static CountryCode getDefault() {
        return FRANCE;
    }

    /**
     * Replies the country calling code of this country.
     * The country calling code is a number prefix (usually two or three digit long), which help you dial
     * a phone number located in another country from the one you are in. It is assigned and managed
     * by International Telecommunication Union (ITU) and defined in the ITU-T ISO standards: E.123 and E.164.
     *
     * @return the country calling code, or {@code 0} if there is no code for this country.
     * @see #getInternationalPhonePrefix()
     * @see #getNationalPhonePrefix()
     * @since 18.0
     */
    public int getCallingCode() {
        return this.callingCode;
    }

    /**
     * Replies the international phone prefix. It is another representation of the
     * {@link #getCallingCode()} country calling code} for this country.
     * It is the part of the phone number when it is written using its international notation,
     * but without the famous {@code +} sign followed by the {@link #getCallingCode() calling code}
     * and the rest of number.
     *
     * <p>For example, the phone number {@code 0033123456789} is a french phone number because
     * it has the international prefix {@code 0033} that corresponds to the calling code {@code 33}.
     * This number could also be written {@code +33123456789} (another international number notation}
     * or {@code 0123456789} (the national number notation}.
     *
     * @return the international phone prefix.
     * @see #getCallingCode()
     * @see #getNationalPhonePrefix()
     * @since 18.0
     */
    public String getInternationalPhonePrefix() {
        if (this.internationalPhonePrefix == null) {
            return "00"; //$NON-NLS-1$
        }
        return this.internationalPhonePrefix;
    }

    /**
     * Replies the national phone prefix for this country.
     * It is the part of the phone number when it is written using its national notation.
     *
     * <p>For example, the phone number {@code 0123456789} is a french phone number because
     * it has the national prefix {@code 0}.
     * This number could also be written {@code +33123456789} or {@code 0033123456789} that
     * are using both the international number notation.
     *
     * @return the international phone prefix.
     * @see #getCallingCode()
     * @see #getInternationalPhonePrefix()
     * @since 18.0
     */
    public String getNationalPhonePrefix() {
        if (this.nationalPhonePrefix == null) {
            return "0"; //$NON-NLS-1$
        }
        return this.nationalPhonePrefix;
    }

    /**
     * Replies the ISO 3166-1 code for the current country.
     *
     * @return the iso code for the country in lower case.
     */
    @Pure
    public String getCode() {
        return this.code;
    }

    /**
     * Replies the locale for the country.
     *
     * @return the locale for the country.
     * @deprecated replaced by {@link #getLanguageLocale()} or {@link #getCountryLocale()}.
     */
    @Pure
    @Deprecated(since = "18.0", forRemoval = true)
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Replies the locale for the country that corresponds to the majority language in that country.
     *
     * @return the majority locale for the country. The returned locale is independent of the country.
     * @since 18.0
     */
    @Pure
    public Locale getLanguageLocale() {
        return this.locale;
    }

    /**
     * Replies the locale for the country that corresponds to the majority language in that country.
     *
     * @return the majority locale for the country. The returned locale depends on the country as
     * geographical area.
     * @since 18.0
     */
    @Pure
    public Locale getCountryLocale() {
        return Locale.of(this.locale.getLanguage(), this.code);
    }

    @Override
    @Pure
    public String toString() {
        return this.code;
    }

    /**
     * Replies the display name of the country.
     * The target language is the one provided by {@link LocaleContextHolder}.
     *
     * @return the display name of the country.
     */
    public String getDisplayCountry() {
        return getDisplayCountry(LocaleContextHolder.getLocale());
    }

    /**
     * Replies the display name of the country.
     *
     * @param locale the locale used for selecting the term.
     * @return the display name of the country.
     * @since 4.0
     */
    public String getDisplayCountry(Locale locale) {
        return getCountryLocale().getDisplayCountry(locale);
    }

    /**
     * Replies the display name of the language associated to the country.
     * The target language is the one provided by {@link LocaleContextHolder}.
     *
     * @return the display name of the country.
     */
    public String getDisplayLanguage() {
        return getDisplayLanguage(LocaleContextHolder.getLocale());
    }

    /**
     * Replies the display name of the language associated to the country.
     * The target language is the one provided by {@link LocaleContextHolder}.
     *
     * @param locale the locale used for selecting the term.
     * @return the display name of the country.
     * @since 4.0
     */
    public String getDisplayLanguage(Locale locale) {
        return getLanguageLocale().getDisplayLanguage(locale);
    }

    /**
     * Replies the country code of the sovereign/president/governor of the country with the current code.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return the country of the sovereign, or {@code null} if the sovereign is in the current country.
     */
    public abstract CountryCode getSovereignCountry();

    /**
     * Replies if the country code corresponds to a country with an European sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in Europe continent.
     */
    public boolean isEuropeanSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isEuropeanContinent();
        }
        return sovereign.isEuropeanContinent();
    }

    /**
     * Replies if the country code corresponds to a country located in European continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a European territory.
     */
    public abstract boolean isEuropeanContinent();

    /**
     * Replies if the country code corresponds to a country with an North-American sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in North-American continent.
     */
    public boolean isNorthAmericanSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isNorthAmericanContinent();
        }
        return sovereign.isNorthAmericanContinent();
    }

    /**
     * Replies if the country code corresponds to a country located in South-American continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a South-American territory.
     */
    public abstract boolean isNorthAmericanContinent();

    /**
     * Replies if the country code corresponds to a country with an South-American sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in South-American continent.
     */
    public boolean isSouthAmericanSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isSouthAmericanContinent();
        }
        return sovereign.isSouthAmericanContinent();
    }

    /**
     * Replies if the country code corresponds to a country located in South-American continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a South-American territory.
     */
    public abstract boolean isSouthAmericanContinent();

    /**
     * Replies if the country code corresponds to a country with an Asian sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in Asian continent.
     */
    public boolean isAsianSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isAsianContinent();
        }
        return sovereign.isAsianContinent();
    }

    /**
     * Replies if the country code corresponds to a country located in Asian continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a Asian territory.
     */
    public abstract boolean isAsianContinent();

    /**
     * Replies if the country code corresponds to a country with an Oceanic sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in Oceanic continent.
     */
    public boolean isAustralianSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isOceanicContinent();
        }
        return sovereign.isOceanicContinent();
    }

    /**
     * Replies if the country code corresponds to a country located in Oceanic continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a Oceanic territory.
     */
    public abstract boolean isOceanicContinent();

    /**
     * Replies if the country code corresponds to a country with an African sovereign/president/governor.
     *
     * <p>The information related to the sovereign/president/governor country is obtained from Wikipedia.
     *
     * @return {@code true} if the sovereign/president/governor of the country is in African continent.
     */
    public boolean isAfricanSovereign() {
        final var sovereign = getSovereignCountry();
        if (sovereign == null) {
            return isAfricanContinent();
        }
        return sovereign.isAfricanContinent();
    }

    /**
     * Replies if the country code corresponds to a country with a language that is unique or originated of that country.
     *
     * @return {@code true} if the code corresponds to a language provider.
     * @since 18.0
     */
    public abstract boolean isLanguageSource();

    /**
     * Replies if the country code corresponds to a country located in African continent.
     *
     * <p>This function implements the seven-contient approach provided on Wikipedia.
     *
     * @return {@code true} if the code corresponds to a African territory.
     */
    public abstract boolean isAfricanContinent();

    /**
     * Replies if the code corresponds to France or one of its associated territories.
     *
     * @return {@code true} if the code corresponds to a French territory.
     */
    public boolean isFrance() {
        return this == FRANCE || getSovereignCountry() == FRANCE;
    }

    /**
     * Replied the normalized country code for this code.
     * The normalized country code is a code without a sovereign code. If the current code
     * has a sovereign code, this sovereign code is used. This process is repeated until
     * a normalized code is found.
     *
     * @return the normalized code.
     */
    public CountryCode normalize() {
        var current = this;
        var sovereign = getSovereignCountry();
        while (sovereign != null) {
            current = sovereign;
            sovereign = current.getSovereignCountry();
        }
        return current;
    }

}
