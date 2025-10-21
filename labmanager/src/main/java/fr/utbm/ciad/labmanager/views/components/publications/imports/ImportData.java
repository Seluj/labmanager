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

package fr.utbm.ciad.labmanager.views.components.publications.imports;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.generic.UploadBuffer;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;
import org.arakhne.afc.vmutil.FileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for the wizard that import publications.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ImportData extends AbstractContextData {

    private static final long serialVersionUID = 6899799982482982442L;

    private final List<UploadBuffer> uploadBuffers = new ArrayList<>();

    private final List<QualifiedPublication> qualifiedPublications = new ArrayList<>();

    private final List<Publication> importablePublications = new ArrayList<>();

    /**
     * Constructor.
     */
    public ImportData() {
        //
    }

    /**
     * Replies the upload buffers of the files that have the given file extension.
     *
     * @param fileExtension the file extension for file matching.
     * @return the buffers.
     */
    public List<UploadBuffer> getUploadBuffersForExtension(String fileExtension) {
        return this.uploadBuffers.stream()
                .filter(it -> FileSystem.hasExtension(it.getFileName(), fileExtension))
                .toList();
    }

    /**
     * Replies the upload buffers of the files.
     *
     * @return the buffers.
     */
    public List<UploadBuffer> getUploadBuffers() {
        return this.uploadBuffers;
    }

    /**
     * Change the upload buffers of the files.
     *
     * @param buffers the buffers.
     */
    public void setUploadBuffers(List<UploadBuffer> buffers) {
        this.uploadBuffers.clear();
        if (buffers != null) {
            this.uploadBuffers.addAll(buffers);
        }
    }

    /**
     * Replies the list of the qualified publication.
     *
     * @return the list.
     */
    public List<QualifiedPublication> getQualifiedPublications() {
        return this.qualifiedPublications;
    }

    /**
     * Replies the list of the importable publications.
     * The set of publication replied by this function is included in the set of publications
     * given by {@link #getPublicationEditors()}.
     *
     * @return the list of publications that are marked as importable.
     */
    public List<Publication> getImportablePublications() {
        return this.importablePublications;
    }

    /**
     * Publication and creation status.
     *
     * @param publication the qualified publication.
     * @param status      the status of the publication.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record QualifiedPublication(Publication publication, EntityCreationStatus status) {
        //
    }

}
