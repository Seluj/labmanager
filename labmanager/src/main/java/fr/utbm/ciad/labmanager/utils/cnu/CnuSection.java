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

package fr.utbm.ciad.labmanager.utils.cnu;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * The CNU section is the part of the French public research community that is dedicated to
 * a specific domain. CNU means "Conseil National des Universités".
 * Each section is associated to a number and a scientific topic.
 * Usually, a French research in public institution is associated to a major CNU section, and
 * may be associated to minor other sections.
 * <p>CNU section is one of the two major system used in France with the {@link ConrsSection CoNRS system}.
 * Not researcher staff is classified according to the {@link FrenchBap French BAP}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.galaxie.enseignementsup-recherche.gouv.fr/ensup/pdf/qualification/sections.pdf"
 * @see ConrsSection
 * @see FrenchBAP
 */
public enum CnuSection {
    /**
     * CNU 01.
     */
    CNU_01 {
        @Override
        public int getGroup() {
            return 1;
        }

        @Override
        public int getNumber() {
            return 1;
        }
    },
    /**
     * CNU 02.
     */
    CNU_02 {
        @Override
        public int getGroup() {
            return 1;
        }

        @Override
        public int getNumber() {
            return 2;
        }
    },
    /**
     * CNU 03.
     */
    CNU_03 {
        @Override
        public int getGroup() {
            return 1;
        }

        @Override
        public int getNumber() {
            return 3;
        }
    },
    /**
     * CNU 04.
     */
    CNU_04 {
        @Override
        public int getGroup() {
            return 1;
        }

        @Override
        public int getNumber() {
            return 4;
        }
    },
    /**
     * CNU 05.
     */
    CNU_05 {
        @Override
        public int getGroup() {
            return 2;
        }

        @Override
        public int getNumber() {
            return 5;
        }
    },
    /**
     * CNU 06.
     */
    CNU_06 {
        @Override
        public int getGroup() {
            return 2;
        }

        @Override
        public int getNumber() {
            return 6;
        }
    },
    /**
     * CNU 07.
     */
    CNU_07 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 7;
        }
    },
    /**
     * CNU 08.
     */
    CNU_08 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 8;
        }
    },
    /**
     * CNU 09.
     */
    CNU_09 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 9;
        }
    },
    /**
     * CNU 10.
     */
    CNU_10 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 10;
        }
    },
    /**
     * CNU 11.
     */
    CNU_11 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 11;
        }
    },
    /**
     * CNU 12.
     */
    CNU_12 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 12;
        }
    },
    /**
     * CNU 13.
     */
    CNU_13 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 13;
        }
    },
    /**
     * CNU 14.
     */
    CNU_14 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 14;
        }
    },
    /**
     * CNU 15.
     */
    CNU_15 {
        @Override
        public int getGroup() {
            return 3;
        }

        @Override
        public int getNumber() {
            return 15;
        }
    },
    /**
     * CNU 16.
     */
    CNU_16 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 16;
        }
    },
    /**
     * CNU 17.
     */
    CNU_17 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 17;
        }
    },
    /**
     * CNU 18.
     */
    CNU_18 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 18;
        }
    },
    /**
     * CNU 19.
     */
    CNU_19 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 19;
        }
    },
    /**
     * CNU 20.
     */
    CNU_20 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 20;
        }
    },
    /**
     * CNU 21.
     */
    CNU_21 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 21;
        }
    },
    /**
     * CNU 22.
     */
    CNU_22 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 22;
        }
    },
    /**
     * CNU 23.
     */
    CNU_23 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 23;
        }
    },
    /**
     * CNU 24.
     */
    CNU_24 {
        @Override
        public int getGroup() {
            return 4;
        }

        @Override
        public int getNumber() {
            return 24;
        }
    },
    /**
     * CNU 25.
     */
    CNU_25 {
        @Override
        public int getGroup() {
            return 5;
        }

        @Override
        public int getNumber() {
            return 25;
        }
    },
    /**
     * CNU 26.
     */
    CNU_26 {
        @Override
        public int getGroup() {
            return 5;
        }

        @Override
        public int getNumber() {
            return 26;
        }
    },
    /**
     * CNU 27.
     */
    CNU_27 {
        @Override
        public int getGroup() {
            return 5;
        }

        @Override
        public int getNumber() {
            return 27;
        }
    },
    /**
     * CNU 28.
     */
    CNU_28 {
        @Override
        public int getGroup() {
            return 6;
        }

        @Override
        public int getNumber() {
            return 28;
        }
    },
    /**
     * CNU 29.
     */
    CNU_29 {
        @Override
        public int getGroup() {
            return 6;
        }

        @Override
        public int getNumber() {
            return 29;
        }
    },
    /**
     * CNU 30.
     */
    CNU_30 {
        @Override
        public int getGroup() {
            return 6;
        }

        @Override
        public int getNumber() {
            return 30;
        }
    },
    /**
     * CNU 31.
     */
    CNU_31 {
        @Override
        public int getGroup() {
            return 7;
        }

        @Override
        public int getNumber() {
            return 31;
        }
    },
    /**
     * CNU 32.
     */
    CNU_32 {
        @Override
        public int getGroup() {
            return 7;
        }

        @Override
        public int getNumber() {
            return 32;
        }
    },
    /**
     * CNU 33.
     */
    CNU_33 {
        @Override
        public int getGroup() {
            return 7;
        }

        @Override
        public int getNumber() {
            return 33;
        }
    },
    /**
     * CNU 34.
     */
    CNU_34 {
        @Override
        public int getGroup() {
            return 8;
        }

        @Override
        public int getNumber() {
            return 34;
        }
    },
    /**
     * CNU 35.
     */
    CNU_35 {
        @Override
        public int getGroup() {
            return 8;
        }

        @Override
        public int getNumber() {
            return 35;
        }
    },
    /**
     * CNU 36.
     */
    CNU_36 {
        @Override
        public int getGroup() {
            return 8;
        }

        @Override
        public int getNumber() {
            return 36;
        }
    },
    /**
     * CNU 37.
     */
    CNU_37 {
        @Override
        public int getGroup() {
            return 8;
        }

        @Override
        public int getNumber() {
            return 37;
        }
    },
    /**
     * CNU 4201.
     */
    CNU_4201 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4201;
        }
    },
    /**
     * CNU 4202.
     */
    CNU_4202 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4202;
        }
    },
    /**
     * CNU 4203.
     */
    CNU_4203 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4203;
        }
    },
    /**
     * CNU 4301.
     */
    CNU_4301 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4301;
        }
    },
    /**
     * CNU 4302.
     */
    CNU_4302 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4302;
        }
    },
    /**
     * CNU 4401.
     */
    CNU_4401 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4401;
        }
    },
    /**
     * CNU 4402.
     */
    CNU_4402 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4402;
        }
    },
    /**
     * CNU 4403.
     */
    CNU_4403 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4403;
        }
    },
    /**
     * CNU 4404.
     */
    CNU_4404 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4404;
        }
    },
    /**
     * CNU 4501.
     */
    CNU_4501 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4501;
        }
    },
    /**
     * CNU 4502.
     */
    CNU_4502 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4502;
        }
    },
    /**
     * CNU 4503.
     */
    CNU_4503 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4503;
        }
    },
    /**
     * CNU 4601.
     */
    CNU_4601 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4601;
        }
    },
    /**
     * CNU 4602.
     */
    CNU_4602 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4602;
        }
    },
    /**
     * CNU 4603.
     */
    CNU_4603 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4603;
        }
    },
    /**
     * CNU 4604.
     */
    CNU_4604 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4604;
        }
    },
    /**
     * CNU 4701.
     */
    CNU_4701 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4701;
        }
    },
    /**
     * CNU 4702.
     */
    CNU_4702 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4702;
        }
    },
    /**
     * CNU 4703.
     */
    CNU_4703 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4703;
        }
    },
    /**
     * CNU 4704.
     */
    CNU_4704 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4704;
        }
    },
    /**
     * CNU 4801.
     */
    CNU_4801 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4801;
        }
    },
    /**
     * CNU 4802.
     */
    CNU_4802 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4802;
        }
    },
    /**
     * CNU 4803.
     */
    CNU_4803 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4803;
        }
    },
    /**
     * CNU 4804.
     */
    CNU_4804 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4804;
        }
    },
    /**
     * CNU 4901.
     */
    CNU_4901 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4901;
        }
    },
    /**
     * CNU 4902.
     */
    CNU_4902 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4902;
        }
    },
    /**
     * CNU 4903.
     */
    CNU_4903 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4903;
        }
    },
    /**
     * CNU 4904.
     */
    CNU_4904 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4904;
        }
    },
    /**
     * CNU 4905.
     */
    CNU_4905 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 4905;
        }
    },
    /**
     * CNU 5001.
     */
    CNU_5001 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5001;
        }
    },
    /**
     * CNU 5002.
     */
    CNU_5002 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5002;
        }
    },
    /**
     * CNU 5003.
     */
    CNU_5003 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5003;
        }
    },
    /**
     * CNU 5004.
     */
    CNU_5004 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5004;
        }
    },
    /**
     * CNU 5101.
     */
    CNU_5101 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5101;
        }
    },
    /**
     * CNU 5102.
     */
    CNU_5102 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5102;
        }
    },
    /**
     * CNU 5103.
     */
    CNU_5103 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5103;
        }
    },
    /**
     * CNU 5104.
     */
    CNU_5104 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5104;
        }
    },
    /**
     * CNU 5201.
     */
    CNU_5201 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5201;
        }
    },
    /**
     * CNU 5202.
     */
    CNU_5202 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5202;
        }
    },
    /**
     * CNU 5203.
     */
    CNU_5203 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5203;
        }
    },
    /**
     * CNU 5204.
     */
    CNU_5204 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5204;
        }
    },
    /**
     * CNU 5301.
     */
    CNU_5301 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5301;
        }
    },
    /**
     * CNU 5302.
     */
    CNU_5302 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5302;
        }
    },
    /**
     * CNU 5401.
     */
    CNU_5401 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5401;
        }
    },
    /**
     * CNU 5402.
     */
    CNU_5402 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5402;
        }
    },
    /**
     * CNU 5403.
     */
    CNU_5403 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5403;
        }
    },
    /**
     * CNU 5404.
     */
    CNU_5404 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5404;
        }
    },
    /**
     * CNU 5405.
     */
    CNU_5405 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5405;
        }
    },
    /**
     * CNU 5501.
     */
    CNU_5501 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5501;
        }
    },
    /**
     * CNU 5502.
     */
    CNU_5502 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5502;
        }
    },
    /**
     * CNU 5503.
     */
    CNU_5503 {
        @Override
        public int getGroup() {
            return 13;
        }

        @Override
        public int getNumber() {
            return 5503;
        }
    },
    /**
     * CNU 5601.
     */
    CNU_5601 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5601;
        }
    },
    /**
     * CNU 5602.
     */
    CNU_5602 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5602;
        }
    },
    /**
     * CNU 5603.
     */
    CNU_5603 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5603;
        }
    },
    /**
     * CNU 5701.
     */
    CNU_5701 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5701;
        }
    },
    /**
     * CNU 5702.
     */
    CNU_5702 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5702;
        }
    },
    /**
     * CNU 5703.
     */
    CNU_5703 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5703;
        }
    },
    /**
     * CNU 5801.
     */
    CNU_5801 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5801;
        }
    },
    /**
     * CNU 5802.
     */
    CNU_5802 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5802;
        }
    },
    /**
     * CNU 5803.
     */
    CNU_5803 {
        @Override
        public int getGroup() {
            return 14;
        }

        @Override
        public int getNumber() {
            return 5803;
        }
    },
    /**
     * CNU 60.
     */
    CNU_60 {
        @Override
        public int getGroup() {
            return 9;
        }

        @Override
        public int getNumber() {
            return 60;
        }
    },
    /**
     * CNU 61.
     */
    CNU_61 {
        @Override
        public int getGroup() {
            return 9;
        }

        @Override
        public int getNumber() {
            return 61;
        }
    },
    /**
     * CNU 62.
     */
    CNU_62 {
        @Override
        public int getGroup() {
            return 9;
        }

        @Override
        public int getNumber() {
            return 62;
        }
    },
    /**
     * CNU 63.
     */
    CNU_63 {
        @Override
        public int getGroup() {
            return 9;
        }

        @Override
        public int getNumber() {
            return 63;
        }
    },
    /**
     * CNU 64.
     */
    CNU_64 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 64;
        }
    },
    /**
     * CNU 65.
     */
    CNU_65 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 65;
        }
    },
    /**
     * CNU 66.
     */
    CNU_66 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 66;
        }
    },
    /**
     * CNU 67.
     */
    CNU_67 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 67;
        }
    },
    /**
     * CNU 68.
     */
    CNU_68 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 68;
        }
    },
    /**
     * CNU 69.
     */
    CNU_69 {
        @Override
        public int getGroup() {
            return 10;
        }

        @Override
        public int getNumber() {
            return 69;
        }
    },
    /**
     * CNU 70.
     */
    CNU_70 {
        @Override
        public int getGroup() {
            return 12;
        }

        @Override
        public int getNumber() {
            return 70;
        }
    },
    /**
     * CNU 71.
     */
    CNU_71 {
        @Override
        public int getGroup() {
            return 12;
        }

        @Override
        public int getNumber() {
            return 71;
        }
    },
    /**
     * CNU 72.
     */
    CNU_72 {
        @Override
        public int getGroup() {
            return 12;
        }

        @Override
        public int getNumber() {
            return 72;
        }
    },
    /**
     * CNU 73.
     */
    CNU_73 {
        @Override
        public int getGroup() {
            return 12;
        }

        @Override
        public int getNumber() {
            return 73;
        }
    },
    /**
     * CNU 74.
     */
    CNU_74 {
        @Override
        public int getGroup() {
            return 12;
        }

        @Override
        public int getNumber() {
            return 74;
        }
    },
    /**
     * CNU 76.
     */
    CNU_76 {
        @Override
        public int getGroup() {
            return 0;
        }

        @Override
        public int getNumber() {
            return 76;
        }
    },
    /**
     * CNU 77.
     */
    CNU_77 {
        @Override
        public int getGroup() {
            return 0;
        }

        @Override
        public int getNumber() {
            return 77;
        }
    },
    /**
     * CNU 85.
     */
    CNU_85 {
        @Override
        public int getGroup() {
            return 0;
        }

        @Override
        public int getNumber() {
            return 85;
        }
    },
    /**
     * CNU 86.
     */
    CNU_86 {
        @Override
        public int getGroup() {
            return 0;
        }

        @Override
        public int getNumber() {
            return 86;
        }
    },
    /**
     * CNU 87.
     */
    CNU_87 {
        @Override
        public int getGroup() {
            return 0;
        }

        @Override
        public int getNumber() {
            return 87;
        }
    };

    private static final String MESSAGE_PREFIX = "cnuSection."; //$NON-NLS-1$

    /**
     * Replies the CNU section  that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the CNU section, to search for.
     * @return the CNU section.
     * @throws IllegalArgumentException if the given name does not corresponds to a CNU section.
     */
    public static CnuSection valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var section : values()) {
                if (name.equalsIgnoreCase(section.name())) {
                    return section;
                }
            }
        }
        throw new IllegalArgumentException("Invalid CNU section: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the CNU section that corresponds to the given number.
     *
     * @param number the number of the CNU section, to search for.
     * @return the CNU section.
     * @throws IllegalArgumentException if the given number does not corresponds to a CNU section.
     */
    public static CnuSection valueOf(Number number) {
        if (number != null) {
            final var n = number.intValue();
            return valueOf(n);
        }
        throw new IllegalArgumentException("Invalid CNU section: " + number); //$NON-NLS-1$
    }

    /**
     * Replies the CNU section that corresponds to the given number.
     *
     * @param number the number of the CNU section, to search for.
     * @return the CNU section.
     * @throws IllegalArgumentException if the given number does not corresponds to a CNU section.
     */
    public static CnuSection valueOf(int number) {
        for (final var section : values()) {
            if (number == section.getNumber()) {
                return section;
            }
        }
        throw new IllegalArgumentException("Invalid CNU section: " + number); //$NON-NLS-1$
    }

    /**
     * Replies the label of the status in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the CNU group in which this section is is.
     *
     * @return the CNU group, or {@code 0} if the group is undefined.
     */
    public abstract int getGroup();

    /**
     * Replies the number of the CNU section.
     *
     * @return the CNU section number, or {@code 0} if the number is undefined.
     */
    public abstract int getNumber();

}
