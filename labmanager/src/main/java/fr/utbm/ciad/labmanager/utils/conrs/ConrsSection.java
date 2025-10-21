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

package fr.utbm.ciad.labmanager.utils.conrs;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * The CoNRS section is the part of the French public research community that is dedicated to
 * a specific domain. CoNRS means "Comité National de Recherche Scientifique".
 * Each section is associated to a number and a scientific topic.
 * Usually, a French research in public institution is associated to a major CoNRS section, and
 * may be associated to minor other sections.
 * <p>CoNRS section is one of the two major system used in France with the {@link CnuSection CNU system}.
 * Not researcher staff is classified according to the {@link FrenchBap French BAP}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.galaxie.enseignementsup-recherche.gouv.fr/ensup/pdf/qualification/sections.pdf"
 * @see CnuSection
 * @see FrenchBAP
 */
public enum ConrsSection {
    /**
     * CoNRS 01.
     */
    CONRS_01 {
        @Override
        public int getNumber() {
            return 1;
        }
    },
    /**
     * CoNRS 02.
     */
    CONRS_02 {
        @Override
        public int getNumber() {
            return 2;
        }
    },
    /**
     * CoNRS 03.
     */
    CONRS_03 {
        @Override
        public int getNumber() {
            return 3;
        }
    },
    /**
     * CoNRS 04.
     */
    CONRS_04 {
        @Override
        public int getNumber() {
            return 4;
        }
    },
    /**
     * CoNRS 05.
     */
    CONRS_05 {
        @Override
        public int getNumber() {
            return 5;
        }
    },
    /**
     * CoNRS 06.
     */
    CONRS_06 {
        @Override
        public int getNumber() {
            return 6;
        }
    },
    /**
     * CoNRS 07.
     */
    CONRS_07 {
        @Override
        public int getNumber() {
            return 7;
        }
    },
    /**
     * CoNRS 08.
     */
    CONRS_08 {
        @Override
        public int getNumber() {
            return 8;
        }
    },
    /**
     * CoNRS 09.
     */
    CONRS_09 {
        @Override
        public int getNumber() {
            return 9;
        }
    },
    /**
     * CoNRS 10.
     */
    CONRS_10 {
        @Override
        public int getNumber() {
            return 10;
        }
    },
    /**
     * CoNRS 11.
     */
    CONRS_11 {
        @Override
        public int getNumber() {
            return 11;
        }
    },
    /**
     * CoNRS 12.
     */
    CONRS_12 {
        @Override
        public int getNumber() {
            return 12;
        }
    },
    /**
     * CoNRS 13.
     */
    CONRS_13 {
        @Override
        public int getNumber() {
            return 13;
        }
    },
    /**
     * CoNRS 14.
     */
    CONRS_14 {
        @Override
        public int getNumber() {
            return 14;
        }
    },
    /**
     * CoNRS 15.
     */
    CONRS_15 {
        @Override
        public int getNumber() {
            return 15;
        }
    },
    /**
     * CoNRS 16.
     */
    CONRS_16 {
        @Override
        public int getNumber() {
            return 16;
        }
    },
    /**
     * CoNRS 17.
     */
    CONRS_17 {
        @Override
        public int getNumber() {
            return 17;
        }
    },
    /**
     * CoNRS 18.
     */
    CONRS_18 {
        @Override
        public int getNumber() {
            return 18;
        }
    },
    /**
     * CoNRS 19.
     */
    CONRS_19 {
        @Override
        public int getNumber() {
            return 19;
        }
    },
    /**
     * CoNRS 20.
     */
    CONRS_20 {
        @Override
        public int getNumber() {
            return 20;
        }
    },
    /**
     * CoNRS 21.
     */
    CONRS_21 {
        @Override
        public int getNumber() {
            return 21;
        }
    },
    /**
     * CoNRS 22.
     */
    CONRS_22 {
        @Override
        public int getNumber() {
            return 22;
        }
    },
    /**
     * CoNRS 23.
     */
    CONRS_23 {
        @Override
        public int getNumber() {
            return 23;
        }
    },
    /**
     * CoNRS 24.
     */
    CONRS_24 {
        @Override
        public int getNumber() {
            return 24;
        }
    },
    /**
     * CoNRS 25.
     */
    CONRS_25 {
        @Override
        public int getNumber() {
            return 25;
        }
    },
    /**
     * CoNRS 26.
     */
    CONRS_26 {
        @Override
        public int getNumber() {
            return 26;
        }
    },
    /**
     * CoNRS 27.
     */
    CONRS_27 {
        @Override
        public int getNumber() {
            return 27;
        }
    },
    /**
     * CoNRS 28.
     */
    CONRS_28 {
        @Override
        public int getNumber() {
            return 28;
        }
    },
    /**
     * CoNRS 29.
     */
    CONRS_29 {
        @Override
        public int getNumber() {
            return 29;
        }
    },
    /**
     * CoNRS 30.
     */
    CONRS_30 {
        @Override
        public int getNumber() {
            return 30;
        }
    },
    /**
     * CoNRS 31.
     */
    CONRS_31 {
        @Override
        public int getNumber() {
            return 31;
        }
    },
    /**
     * CoNRS 32.
     */
    CONRS_32 {
        @Override
        public int getNumber() {
            return 32;
        }
    },
    /**
     * CoNRS 33.
     */
    CONRS_33 {
        @Override
        public int getNumber() {
            return 33;
        }
    },
    /**
     * CoNRS 34.
     */
    CONRS_34 {
        @Override
        public int getNumber() {
            return 34;
        }
    },
    /**
     * CoNRS 35.
     */
    CONRS_35 {
        @Override
        public int getNumber() {
            return 35;
        }
    },
    /**
     * CoNRS 36.
     */
    CONRS_36 {
        @Override
        public int getNumber() {
            return 36;
        }
    },
    /**
     * CoNRS 37.
     */
    CONRS_37 {
        @Override
        public int getNumber() {
            return 37;
        }
    },
    /**
     * CoNRS 38.
     */
    CONRS_38 {
        @Override
        public int getNumber() {
            return 38;
        }
    },
    /**
     * CoNRS 39.
     */
    CONRS_39 {
        @Override
        public int getNumber() {
            return 39;
        }
    },
    /**
     * CoNRS 40.
     */
    CONRS_40 {
        @Override
        public int getNumber() {
            return 40;
        }
    },
    /**
     * CoNRS 41.
     */
    CONRS_41 {
        @Override
        public int getNumber() {
            return 41;
        }
    };

    private static final String MESSAGE_PREFIX = "conrsSection."; //$NON-NLS-1$

    /**
     * Replies the CoNRS section that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the CoNRS section, to search for.
     * @return the CoNRS section.
     * @throws IllegalArgumentException if the given name does not corresponds to a CoNRS section.
     */
    public static ConrsSection valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var section : values()) {
                if (name.equalsIgnoreCase(section.name())) {
                    return section;
                }
            }
        }
        throw new IllegalArgumentException("Invalid CoNRS section: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the CoNRS section that corresponds to the given number.
     *
     * @param number the number of the CoNRS section, to search for.
     * @return the CoNRS section.
     * @throws IllegalArgumentException if the given number does not corresponds to a CoNRS section.
     */
    public static ConrsSection valueOf(Number number) {
        if (number != null) {
            final int n = number.intValue();
            return valueOf(n);
        }
        throw new IllegalArgumentException("Invalid CoNRS section: " + number); //$NON-NLS-1$
    }

    /**
     * Replies the CoNRS section that corresponds to the given number.
     *
     * @param number the number of the CoNRS section, to search for.
     * @return the CoNRS section.
     * @throws IllegalArgumentException if the given number does not corresponds to a CoNRS section.
     */
    public static ConrsSection valueOf(int number) {
        for (final var section : values()) {
            if (number == section.getNumber()) {
                return section;
            }
        }
        throw new IllegalArgumentException("Invalid CoNRS section: " + number); //$NON-NLS-1$
    }

    /**
     * Replies the label of the CoNRS section in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the CoNRS section in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the number of the CoNRS section.
     *
     * @return the CoNRS section number, or {@code 0} if the number is undefined.
     */
    public abstract int getNumber();

}
