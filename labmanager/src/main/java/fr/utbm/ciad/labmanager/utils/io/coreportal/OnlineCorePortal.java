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

package fr.utbm.ciad.labmanager.utils.io.coreportal;

import com.google.common.base.Strings;
import com.microsoft.playwright.ElementHandle;
import fr.utbm.ciad.labmanager.utils.io.AbstractWebScraper;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * Accessor to the online CORE Portal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://portal.core.edu.au/"
 * @since 3.6
 */
@Component
@Primary
public class OnlineCorePortal extends AbstractWebScraper implements CorePortal {

    private static final String SCHEME = "http"; //$NON-NLS-1$

    private static final String HOST = "portal.core.edu.au"; //$NON-NLS-1$

    private static final String CONFERENCE_PATH = "conf-ranks/"; //$NON-NLS-1$

    private static final Pattern SOURCE_PATTERN = Pattern.compile("^Source:.*?([0-9]+)$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    private static final Pattern RANK_PATTERN = Pattern.compile("^Rank:.*?([^\\s]+)$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    /**
     * Factory of URI builder.
     */
    protected final UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();

    private static boolean parseConferenceRankingBox(AtomicReference<CorePortalConference> output,
                                                     AtomicInteger outputYear, ElementHandle box) {
        final var rows = box.querySelectorAll("div"); //$NON-NLS-1$
        String foundRank = null;
        Integer foundYear = null;
        for (final var row : rows) {
            final var text = row.innerText().trim();
            if (!Strings.isNullOrEmpty(text)) {
                final var matcher0 = SOURCE_PATTERN.matcher(text);
                if (matcher0.find()) {
                    foundYear = Integer.valueOf(matcher0.group(1));
                }
                final var matcher1 = RANK_PATTERN.matcher(text);
                if (matcher1.find()) {
                    foundRank = matcher1.group(1);
                }
                if (foundRank != null && foundYear != null) {
                    break;
                }
            }
        }
        if (foundRank != null && foundYear != null) {
            if (!Strings.isNullOrEmpty(foundRank)) {
                try {
                    final var ranking = CoreRanking.valueOfCaseInsensitive(foundRank);
                    output.set(new CorePortalConference(ranking));
                    outputYear.set(foundYear.intValue());
                    return true;
                } catch (Throwable ex) {
                    //
                }
            }
        }
        return false;
    }

    @Override
    public URL getConferenceUrl(String conferenceId) {
        if (!Strings.isNullOrEmpty(conferenceId)) {
            try {
                var builder = this.uriBuilderFactory.builder();
                builder = builder.scheme(SCHEME);
                builder = builder.host(HOST);
                builder = builder.path(CONFERENCE_PATH).path(conferenceId);
                final var uri = builder.build();
                return uri.toURL();
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }

    @Override
    public CorePortalConference getConferenceRanking(int year, String identifier, Progression progress) throws Exception {
        final var prog = ensureProgress(progress);
        if (!Strings.isNullOrEmpty(identifier)) {
            final var url = getConferenceUrl(identifier);
            if (url != null) {
                final var output = new AtomicReference<CorePortalConference>();
                final var outputYear = new AtomicInteger(Integer.MIN_VALUE);
                loadHtmlPage(
                        DEFAULT_DEVELOPER,
                        url,
                        prog,
                        "div[id=detail]", //$NON-NLS-1$
                        0,
                        (page, element0) -> {
                            final var boxes = element0.querySelectorAll("div[class=detail]"); //$NON-NLS-1$
                            for (final var box : boxes) {
                                final var output0 = new AtomicReference<CorePortalConference>();
                                final var outputYear0 = new AtomicInteger(0);
                                if (parseConferenceRankingBox(output0, outputYear0, box)) {
                                    final var y = outputYear0.get();
                                    if (y <= year && (outputYear.get() < y)) {
                                        output.set(output0.get());
                                        outputYear.set(y);
                                    }
                                }
                            }
                        });
                final var conference = output.get();
                if (conference != null) {
                    return conference;
                }
            }
        }
        throw new IllegalArgumentException("Invalid CORE identifier or no valid access: " + identifier); //$NON-NLS-1$
    }

}
