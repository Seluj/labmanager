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

import org.springframework.stereotype.Component;

import java.net.Proxy.Type;

/**
 * A wrapper for the network connection that assumes a connection from the UTBM network.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
@Component
public class UtbmNetConnection extends AbstractProxyNetConnection {

    private static final String PROXY_URL = "proxy.utbm.fr"; //$NON-NLS-1$

    private static final int PROXY_PORT = 3128;

    @Override
    protected int getProxyPort() {
        return PROXY_PORT;
    }

    @Override
    protected Type getProxyType() {
        return Type.HTTP;
    }

    @Override
    protected String getProxyAddress() {
        return PROXY_URL;
    }

}

