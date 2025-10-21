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

package fr.utbm.ciad.labmanager.utils.io.ris;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.IntegerRange;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Utilities for RIS.
 * RIS is a standardized tag format developed by Research Information Systems, Incorporated to enable citation programs
 * to exchange data.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/RIS_(file_format)"
 * @since 3.7
 */
public abstract class AbstractRIS implements RIS {

    private static final Pattern PAGES_PATTERN = Pattern.compile("^\\s*([0-9]+)(?:\\s*\\-+\\s*([0-9]+)\\s*)?$"); //$NON-NLS-1$

    private final Random random = new Random();

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param messages the accessor to the localized strings.
     */
    public AbstractRIS(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    /**
     * Parse the string that represents a page range.
     *
     * @param pages the string to parse.
     * @return the page range, or {@code null} if the given argument cannot be parsed.
     */
    protected static IntegerRange parsePages(String pages) {
        if (!Strings.isNullOrEmpty(pages)) {
            final var matcher = PAGES_PATTERN.matcher(pages);
            if (matcher.find()) {
                try {
                    final var p0 = matcher.group(1);
                    if (matcher.groupCount() > 1) {
                        final var p1 = matcher.group(2);
                        final var page0 = Integer.parseUnsignedInt(p0);
                        final var page1 = Integer.parseUnsignedInt(p1);
                        return new IntegerRange(page0, page1);
                    }
                    final var page = Integer.parseUnsignedInt(p0);
                    return new IntegerRange(page, page);
                } catch (Throwable ex) {
                    //
                }
            }
        }
        return null;
    }

    /**
     * Replies the accessor to the localized strings.
     *
     * @return the accessor.
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        return this.messages;
    }

    /**
     * Generate an UUID.
     *
     * @return the UUID.
     */
    protected Integer generateUUID() {
        return Integer.valueOf(Math.abs(this.random.nextInt()));
    }

}
