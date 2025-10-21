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

package fr.utbm.ciad.labmanager.utils.io.html;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.PublicationExporter;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * Utilities for exporting publications to HTML content.
 * This exporter is generic and not dedicated to a specific target.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface HtmlExporter extends PublicationExporter<String> {

    /**
     * Replies the unsecable separator.
     *
     * @return the HTML code.
     */
    String separator();

    /**
     * Replies the unsecable separator with double length.
     *
     * @return the HTML code.
     */
    String doubleSeparator();

    /**
     * Replies the HTML representation of the publications that are given as argument.
     * <p>The decorators for the names are usually the following (implementation may change them):
     * <table>
     * <thead>
     *   <tr>
     *     <th>Has person selector?</th>
     *     <th>Has no person selector?</th>
     *   </tr>
     * </thead>
     * <tbody>
     *   <tr>
     *     <td>Yes</td>
     *     <td>
     *       <ul>
     *         <li>Selected person: bold+underline</li>
     *         <li>Researcher: bold</li>
     *         <li>PhD student: underline</li>
     *         <li>PD or engineer: italic</li>
     *       </ul>
     *     </td>
     *   </tr>
     *   <tr>
     *     <td>No</td>
     *     <td>
     *       <ul>
     *         <li>Researcher: bold</li>
     *         <li>PhD student: underline</li>
     *         <li>PD or engineer: italic</li>
     *       </ul>
     *     </td>
     *   </tr>
     * </tbody>
     * </table>
     *
     * @param publications the publications to export.
     * @param configurator the configurator for the export, never {@code null}.
     * @param progression  the progression indicator.
     * @param logger       the logger to be used.
     * @return the HTML representation of the publications.
     * @throws Exception if the publication cannot be converted to HTML.
     */
    @Override
    String exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression, Logger logger) throws Exception;

}
