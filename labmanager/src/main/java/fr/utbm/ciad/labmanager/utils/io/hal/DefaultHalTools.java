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

package fr.utbm.ciad.labmanager.utils.io.hal;

import com.google.common.base.Strings;
import org.arakhne.afc.vmutil.FileSystem;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Default implementation for the utilities for HAL numbers.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class DefaultHalTools implements HalTools {

    private static final URL HAL_BASE;

    private static final String HAL_PATTERN_STR = "^hal\\-.*+$"; //$NON-NLS-1$

    private static final Pattern HAL_PATTERN;

    static {
        try {
            HAL_BASE = new URI(HAL_URL_BASE).toURL();
        } catch (Throwable ex) {
            throw new Error(ex);
        }
        HAL_PATTERN = Pattern.compile(HAL_PATTERN_STR);
    }

    private static String validatePath(String path) {
        var pth = path;
        if (!Strings.isNullOrEmpty(pth)) {
            pth = pth.trim();
            if (!Strings.isNullOrEmpty(pth)) {
                final var matcher = HAL_PATTERN.matcher(pth);
                if (matcher.matches()) {
                    return pth;
                }
            }
        }
        return null;
    }

    @Override
    public String getHALNumberFromHALUrl(URL url) {
        if (url != null) {
            var path = url.getPath();
            if (!Strings.isNullOrEmpty(path)) {
                if (path.startsWith("/")) { //$NON-NLS-1$
                    path = path.substring(1);
                }
            }
            path = validatePath(path);
            if (path != null) {
                return path;
            }
        }
        throw new IllegalArgumentException("Invalid HAL: " + url); //$NON-NLS-1$
    }

    @Override
    public String getHALNumberFromHALUrl(String url) {
        if (!Strings.isNullOrEmpty(url)) {
            String path = null;
            try {
                final var urlObj = new URI(url);
                path = urlObj.getPath();
                if (Strings.isNullOrEmpty(path)) {
                    path = urlObj.getSchemeSpecificPart();
                } else if (path.startsWith("/")) { //$NON-NLS-1$
                    path = path.substring(1);
                }
            } catch (Throwable ex0) {
                path = url;
            }
            path = validatePath(path);
            if (path != null) {
                return path;
            }
        }
        throw new IllegalArgumentException("Invalid HAL: " + url); //$NON-NLS-1$
    }

    @Override
    public URL getHALUrlFromHALNumber(String number) {
        if (!Strings.isNullOrEmpty(number)) {
            return FileSystem.join(HAL_BASE, number);
        }
        return null;
    }

}
