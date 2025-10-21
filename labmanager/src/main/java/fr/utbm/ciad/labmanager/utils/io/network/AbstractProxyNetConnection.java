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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * A wrapper for the network connection that assumes a connection through a proxy.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
public abstract class AbstractProxyNetConnection implements NetConnection {

    @Override
    public BufferedImage getImageFromURL(URL url) throws IOException {
        final var proxy = new Proxy(getProxyType(), new InetSocketAddress(getProxyAddress(), getProxyPort()));
        final var connection = url.openConnection(proxy);
        connection.connect();
        try (final var out = new ByteArrayOutputStream()) {
            try (final var in = connection.getInputStream()) {
                final var buf = new byte[1024];
                var n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
            }
            final var response = out.toByteArray();
            try (final var bis = new ByteArrayInputStream(response)) {
                return ImageIO.read(bis);
            }
        }
    }

    /**
     * Replies the type of the proxy server to be used for the connexion.
     *
     * @return the proxy server type.
     */
    protected abstract Proxy.Type getProxyType();

    /**
     * Replies the address of the proxy server to be used for the connexion.
     *
     * @return the proxy server address.
     */
    protected abstract String getProxyAddress();

    /**
     * Replies the port of the proxy server to be used for the connexion.
     *
     * @return the proxy port.
     */
    protected abstract int getProxyPort();

}

