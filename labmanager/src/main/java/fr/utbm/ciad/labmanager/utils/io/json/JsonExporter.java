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

package fr.utbm.ciad.labmanager.utils.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.PublicationExporter;
import org.arakhne.afc.progress.Progression;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * Utilities for exporting publications to JSON.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface JsonExporter extends PublicationExporter<String> {

    @Override
    default String exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression,
                                      Logger logger) throws Exception {
        return exportPublicationsWithRootKeys(publications, configurator, progression, logger);
    }

    /**
     * Export publications. The content of the exported flow depends on the sub-interfaces.
     *
     * @param publications the publications to export.
     * @param configurator the configurator for the export, never {@code null}.
     * @param progression  the progression indicator.
     * @param logger       the logger to be used.
     * @param rootKeys     the sequence of keys for building the root of the tree. The exported data is then
     *                     output into the last created node with the {@code rootKeys}.
     * @return the representation of the publications.
     * @throws Exception if the publication cannot be converted.
     */
    String exportPublicationsWithRootKeys(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression,
                                          Logger logger, String... rootKeys) throws Exception;

    /**
     * Export publications. The content of the exported flow depends on the sub-interfaces.
     *
     * @param publications the publications to export.
     * @param configurator the configurator for the export, never {@code null}.
     * @param progression  the progression indicator.
     * @param logger       the logger to be used.
     * @param callback     a function that is invoked for each publication for giving the opportunity
     *                     to fill up the Json node of the publication.
     * @param rootKeys     the sequence of keys for building the root of the tree. The exported data is then
     *                     output into the last created node with the {@code rootKeys}.
     * @return the representation of the publications with the Jackson API.
     * @throws Exception if the publication cannot be converted.
     */
    JsonNode exportPublicationsAsTreeWithRootKeys(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression,
                                                  Logger logger, Procedure2<Publication, ObjectNode> callback, String... rootKeys) throws Exception;

}
