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

package fr.utbm.ciad.labmanager.utils.io.network;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Wrapper for the network connection that supports proxy configuration or not depending on the local configuration.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
public interface NetConnection {

    /**
     * Read the image that it referenced by the given URL.
     *
     * @param url the url of the image to be read.
     * @return the image.
     * @throws IOException if the image cannot be read. The exception gives details on the reason of the failure.
     */
    BufferedImage getImageFromURL(URL url) throws IOException;

}

