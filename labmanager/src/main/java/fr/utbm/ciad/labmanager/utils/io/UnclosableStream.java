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

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that ignore the closing requests.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class UnclosableStream extends OutputStream {

    private final OutputStream source;

    /**
     * Constructor.
     *
     * @param source the source stream.
     */
    public UnclosableStream(OutputStream source) {
        this.source = source;
    }

    @Override
    public void close() throws IOException {
        //
    }

    @Override
    public void flush() throws IOException {
        this.source.flush();
    }

    @Override
    public void write(int b) throws IOException {
        this.source.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.source.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.source.write(b, off, len);
    }

}
