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

package fr.utbm.ciad.labmanager.utils.io.gscholar;

import fr.utbm.ciad.labmanager.utils.io.AbstractWebScraper;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Accessor to the online Web-of-Science platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.webofscience.com"
 * @since 2.5
 */
@Component
@Primary
public class OnlineGoogleScholarPlatform extends AbstractWebScraper implements GoogleScholarPlatform {

    @Override
    public GoogleScholarPerson getPersonRanking(URL gsProfile, Progression progress) throws Exception {
        final var prog = ensureProgress(progress);
        if (gsProfile != null) {
            final var output = new AtomicReference<GoogleScholarPerson>();
            //"onetrust-reject-all-handler"
            loadHtmlPage(
                    DEFAULT_DEVELOPER,
                    gsProfile,
                    prog,
                    "table[id='gsc_rsb_st']", //$NON-NLS-1$
                    0,
                    (page, element0) -> {
                        final var elements = element0.asElement().querySelectorAll("td[class='gsc_rsb_std']"); //$NON-NLS-1$
                        if (elements != null) {
                            int ihindex = -1;
                            if (elements.size() > 2) {
                                final var hindex = readInt(elements.get(2));
                                ihindex = positiveInt(hindex);
                            }
                            int icitations = -1;
                            if (elements.size() > 0) {
                                final var citations = readInt(elements.get(0));
                                icitations = positiveInt(citations);
                            }
                            output.set(new GoogleScholarPerson(ihindex, icitations));
                        }
                    });
            final var person = output.get();
            if (person != null) {
                return person;
            }
        }
        throw new IllegalArgumentException("Invalid Google Scholar URL or no valid access: " + gsProfile); //$NON-NLS-1$
    }

}
