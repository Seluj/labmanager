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

package fr.utbm.ciad.labmanager.views.components.publications.editors.wizard;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for the wizard that generate the thumbnail of the publications.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ThumbnailGeneratorData extends AbstractContextData {

    private static final long serialVersionUID = 5287751273038937188L;

    private List<Publication> publications = new ArrayList<>();

    /**
     * Constructor.
     */
    public ThumbnailGeneratorData() {
        //
    }

    /**
     * Replies the list of publications.
     *
     * @return the publications.
     */
    public synchronized List<Publication> getPublications() {
        return this.publications;
    }

    /**
     * Change the list of publications.
     *
     * @param publications the publications.
     */
    public synchronized void setPublications(List<Publication> publications) {
        assert publications != null;
        this.publications = publications;
    }

}
