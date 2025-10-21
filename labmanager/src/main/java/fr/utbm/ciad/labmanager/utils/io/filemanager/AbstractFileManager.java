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

package fr.utbm.ciad.labmanager.utils.io.filemanager;

import com.google.common.base.Strings;
import org.arakhne.afc.vmutil.FileSystem;

import java.io.File;

/**
 * Utilities for managing the downloadable files. This implementation is dedicated to the WordPress service
 * of the lab.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public abstract class AbstractFileManager implements FileManager {

    private static final long serialVersionUID = -292074420511265866L;

    /**
     * Path to the upload folder.
     */
    protected final File uploadFolder;

    /**
     * Constructor with the given stream factory.
     *
     * @param uploadFolder the path of the upload folder. It is defined by the property {@code labmanager.file.upload-directory}.
     */
    public AbstractFileManager(String uploadFolder) {
        final var f0 = Strings.emptyToNull(uploadFolder);
        if (f0 == null) {
            this.uploadFolder = null;
        } else {
            this.uploadFolder = FileSystem.convertStringToFile(f0).getAbsoluteFile();
        }
    }

    @Override
    public File normalizeForServerSide(File file) {
        if (file == null) {
            return null;
        }
        if (file.isAbsolute()) {
            return file;
        }
        if (this.uploadFolder != null) {
            return FileSystem.join(this.uploadFolder, file);
        }
        return file.getAbsoluteFile();
    }

}
