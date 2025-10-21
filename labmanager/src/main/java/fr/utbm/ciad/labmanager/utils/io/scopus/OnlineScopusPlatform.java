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

package fr.utbm.ciad.labmanager.utils.io.scopus;

import fr.utbm.ciad.labmanager.utils.io.AbstractWebScraper;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Accessor to the online Elsevier Scopus platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.scopus.com"
 * @since 3.3
 */
@Component
@Primary
public class OnlineScopusPlatform extends AbstractWebScraper implements ScopusPlatform {

    @Override
    public ScopusPerson getPersonRanking(URL scProfile, Progression progress) throws Exception {
        final var prog = ensureProgress(progress);
        if (scProfile != null) {
            final var output = new AtomicReference<ScopusPerson>();
            loadHtmlPage(
                    DEFAULT_DEVELOPER,
                    scProfile,
                    prog,
                    "[data-testid=metrics-section-citations-count]", //$NON-NLS-1$
                    0,
                    (page, element0) -> {
                        final var citations = readInt(element0, "[data-testid=unclickable-count]"); //$NON-NLS-1$
                        final var icitations = positiveInt(citations);
                        final var hindex = readInt(page, "[data-testid=metrics-section-h-index]", "[data-testid=unclickable-count]"); //$NON-NLS-1$ //$NON-NLS-2$
                        final var ihindex = positiveInt(hindex);
                        output.set(new ScopusPerson(ihindex, icitations));
                    });
            final var person = output.get();
            if (person != null) {
                return person;
            }
        }
        throw new IllegalArgumentException("Invalid scopus URL, or no valid access to the page, or invalid page structure: " + scProfile); //$NON-NLS-1$
    }

}
