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

package fr.utbm.ciad.labmanager.utils.io;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * Exporter of publications.
 *
 * @param <T> the type of the replied value after exporting.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface PublicationExporter<T> {

    /**
     * Export publications. The content of the exported flow depends on the sub-interfaces.
     *
     * @param publications the publications to export.
     * @param configurator the configurator for the export, never {@code null}.
     * @param progression  the progression indicator to be used.
     * @param logger       the logger to be used.
     * @return the representation of the publications.
     * @throws Exception if the publication cannot be converted.
     */
    T exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression,
                         Logger logger) throws Exception;

}
