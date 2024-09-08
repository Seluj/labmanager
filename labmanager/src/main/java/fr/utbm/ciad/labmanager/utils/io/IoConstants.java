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

/** Definition of constants for Inpout/Output.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class IoConstants {

	/** MIME for MS Excel file.
	 */
	public static final String EXCEL_MIME = "application/ms-excel"; //$NON-NLS-1$

	/** MIME for JSON file.
	 */
	public static final String JSON_MIME = "application/json"; //$NON-NLS-1$

	/** Filename extension for JSON file.
	 */
	public static final String JSON_FILENAME_EXTENSION = ".json"; //$NON-NLS-1$

	/** MIME for ZIP file.
	 */
	public static final String ZIP_MIME = "application/zip"; //$NON-NLS-1$

	/** Filename extension for ZIP file.
	 */
	public static final String ZIP_FILENAME_EXTENSION = ".zip"; //$NON-NLS-1$

	private IoConstants() {
		//
	}

}