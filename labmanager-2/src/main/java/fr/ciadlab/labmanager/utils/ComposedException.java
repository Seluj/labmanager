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

package fr.ciadlab.labmanager.utils;

import java.util.Collection;
import java.util.Collections;

import org.apache.jena.ext.com.google.common.base.Strings;

/** An exception that contains multiple causes.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class ComposedException extends Exception {

	private static final long serialVersionUID = 1077595052229473266L;

	private final Collection<? extends Throwable> errors;

	/** Constructor.
	 * 
	 * @param errors the causes.
	 */
	public ComposedException(Collection<? extends Throwable> errors) {
		super();
		this.errors = errors;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		final String msg = super.getMessage();
		if (Strings.isNullOrEmpty(msg)) {
			final StringBuilder b = new StringBuilder();
			for (final Throwable ex : getCauses()) {
				if (!Strings.isNullOrEmpty(ex.getLocalizedMessage())) {
					if (b.length() > 0) {
						b.append("\n"); //$NON-NLS-1$
					}
					b.append(ex.getLocalizedMessage());
				}
			}
			return b.toString();
		}
		return msg;
	}

	/** Replies the causes of this exception.
	 *
	 * @return the causes.
	 */
	public synchronized Collection<? extends Throwable> getCauses() {
		return this.errors == null ? Collections.emptyList() : this.errors;
	}

}
