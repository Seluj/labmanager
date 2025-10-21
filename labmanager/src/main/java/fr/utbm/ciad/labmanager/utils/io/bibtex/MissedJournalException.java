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

package fr.utbm.ciad.labmanager.utils.io.bibtex;

/**
 * Exception that indicates a journal is missed.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.4
 */
public class MissedJournalException extends RuntimeException {

    private static final long serialVersionUID = 8332179639940537811L;

    /**
     * Constructor.
     *
     * @param entryKey    the BibTeX key.
     * @param journalName the name of the missed journal.
     */
    public MissedJournalException(String entryKey, String journalName) {
        super("Unknown journal for entry " + entryKey + ": " + journalName); //$NON-NLS-1$ //$NON-NLS-2$);
    }

}
