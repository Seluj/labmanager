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

package fr.utbm.ciad.labmanager.utils.bap;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * The French BAP is a classification of the types of jobs that engineers, technicians and other
 * not researcher staff could do. BAP means "Branch d'activité professionnelle".
 * Usually, a staff meber who is not researcher in public institution is associated to BAP.
 * <p>Rsearcher classification are {@link CnuSection CNU section} and {@link ConrsSection CoNRS section}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.galaxie.enseignementsup-recherche.gouv.fr/ensup/pdf/qualification/sections.pdf"
 * @see CnuSection
 * @see ConrsSection
 */
public enum FrenchBap {
    /**
     * BAP A.
     */
    BAP_A {
        @Override
        public String getShortName() {
            return "A"; //$NON-NLS-1$
        }
    },
    /**
     * BAP B.
     */
    BAP_B {
        @Override
        public String getShortName() {
            return "B"; //$NON-NLS-1$
        }
    },
    /**
     * BAP C.
     */
    BAP_C {
        @Override
        public String getShortName() {
            return "C"; //$NON-NLS-1$
        }
    },
    /**
     * BAP D.
     */
    BAP_D {
        @Override
        public String getShortName() {
            return "D"; //$NON-NLS-1$
        }
    },
    /**
     * BAP E.
     */
    BAP_E {
        @Override
        public String getShortName() {
            return "E"; //$NON-NLS-1$
        }
    },
    /**
     * BAP F.
     */
    BAP_F {
        @Override
        public String getShortName() {
            return "F"; //$NON-NLS-1$
        }
    },
    /**
     * BAP G.
     */
    BAP_G {
        @Override
        public String getShortName() {
            return "G"; //$NON-NLS-1$
        }
    },
    /**
     * BAP J.
     */
    BAP_J {
        @Override
        public String getShortName() {
            return "J"; //$NON-NLS-1$
        }
    };

    private static final String MESSAGE_PREFIX = "frenchBap."; //$NON-NLS-1$

    /**
     * Replies the French BAP that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the French BAP, to search for.
     * @return the French BAP.
     * @throws IllegalArgumentException if the given name does not corresponds to a French BAP.
     */
    public static FrenchBap valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var section : values()) {
                if (name.equalsIgnoreCase(section.name()) || name.equalsIgnoreCase(section.getShortName())) {
                    return section;
                }
            }
        }
        throw new IllegalArgumentException("Invalid French BAP: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the French BAP in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the French BAP in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the short name of this French BAP. The sohrt name is usually a letter.
     *
     * @return the short name.
     */
    public abstract String getShortName();

}
