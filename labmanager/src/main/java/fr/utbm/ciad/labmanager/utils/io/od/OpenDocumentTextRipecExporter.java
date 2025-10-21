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

import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;

/**
 * Utilities for exporting an activity report based on RIPEC standard to Open Document Text.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.7
 */
public interface OpenDocumentTextRipecExporter {

    /**
     * Replies the ODT representation of the RIPEC-C3 document.
     *
     * @param configurator the configurator for the export, never {@code null}.
     * @return the ODT representation of the report.
     * @throws Exception if the report cannot be generated to ODT.
     */
    byte[] exportRipecC3(ExporterConfigurator configurator) throws Exception;

}
