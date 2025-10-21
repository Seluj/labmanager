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

package fr.utbm.ciad.labmanager.utils.io.youtube;

import com.google.common.base.Strings;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities for YouTube.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Component
@Primary
public class DefaultYouTubeTools implements YouTubeTools {

    private static final String PROTOCOL = "https"; //$NON-NLS-1$

    private static final String EMBEDDED_HOST = "www.youtube.com"; //$NON-NLS-1$

    private static final String PATH_PREFIX = "embed"; //$NON-NLS-1$

    private static final String PARAM_NAME = "feature"; //$NON-NLS-1$

    private static final String PARAM_VALUE = "oembed"; //$NON-NLS-1$

    private static final Set<String> HOSTS = new HashSet<>();

    static {
        HOSTS.add("youtu.be"); //$NON-NLS-1$
        HOSTS.add("www.youtu.be"); //$NON-NLS-1$
        HOSTS.add("youtube.com"); //$NON-NLS-1$
        HOSTS.add(EMBEDDED_HOST);
        HOSTS.add("youtube.fr"); //$NON-NLS-1$
        HOSTS.add("www.youtube.fr"); //$NON-NLS-1$
    }

    private final UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();

    private static String getVideoId(URL url) {
        final var host = url.getHost();
        if (HOSTS.contains(host)) {
            var path = url.getPath();
            path = path.replaceFirst("^/+", "").replaceAll("/+$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            if (!Strings.isNullOrEmpty(path)) {
                return path;
            }
        }
        return null;
    }

    @Override
    public URL embeddedVideoLink(URL videoLink) {
        final var id = getVideoId(videoLink);
        if (!Strings.isNullOrEmpty(id)) {
            final var builder = this.uriBuilderFactory.builder();
            builder.scheme(PROTOCOL).host(EMBEDDED_HOST);
            builder.pathSegment(PATH_PREFIX, id);
            builder.queryParam(PARAM_NAME, PARAM_VALUE);
            try {
                return builder.build().toURL();
            } catch (MalformedURLException e) {
                //
            }
        }
        return null;
    }

}
