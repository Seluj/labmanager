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

package fr.ciadlab.labmanager.io.od;

import fr.ciadlab.labmanager.entities.publication.Publication;
import fr.ciadlab.labmanager.io.ExporterConfigurator;
import fr.ciadlab.labmanager.io.PublicationExporter;

/** Utilities for exporting publications to Open Document Text.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface OpenDocumentTextPublicationExporter extends PublicationExporter<byte[]> {

	/** Replies the ODT representation of the publications that are given as argument.
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
	 *         <li>Selected person: bold + underline</li>
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
	 * @return the ODT representation of the publications.
	 * @throws Exception if the publication cannot be converted to ODT.
	 */
	@Override
	byte[] exportPublications(Iterable<? extends Publication> publications, ExporterConfigurator configurator) throws Exception;

}
