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

package fr.ciadlab.labmanager.io.html;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.publication.Publication;
import fr.ciadlab.labmanager.io.ExporterConfigurator;
import fr.ciadlab.labmanager.io.hal.HalTools;
import fr.ciadlab.labmanager.utils.doi.DoiTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/** Utilities for exporting publications to HTML content based on the CIAD standard HTML style.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class CiadHtmlDocumentExporter extends AbstractCiadHtmlExporter implements HtmlDocumentExporter {
	
	/** Constructor.
	 *
	 * @param constants the accessor to the application constants.
	 * @param messages the accessor to the localized messages.
	 * @param doiTools the tools for managing DOI links.
	 * @param halTools the tools for manipulating HAL identifiers.
	 */
	public CiadHtmlDocumentExporter(@Autowired Constants constants, @Autowired MessageSourceAccessor messages,
			@Autowired DoiTools doiTools, @Autowired HalTools halTools) {
		super(constants, messages, doiTools, halTools);
	}

	@Override
	public String exportPublications(Iterable<? extends Publication> publications, ExporterConfigurator configurator) throws Exception {
		assert configurator != null;
		if (publications == null) {
			return null;
		}
		final StringBuilder html = new StringBuilder();
		html.append("<html><body>"); //$NON-NLS-1$
		exportPublicationsWithGroupingCriteria(publications, configurator,
				it -> html.append("<h1>").append(it).append("</h1>"), //$NON-NLS-1$ //$NON-NLS-2$
				it -> html.append("<h2>").append(it).append("</h2>"), //$NON-NLS-1$ //$NON-NLS-2$
				it -> exportFlatList(html, it, configurator));
		html.append("</html></body>"); //$NON-NLS-1$
		return html.toString();
	}

	/** Export the publications in a flat list.
	 *
	 * @param html the receiver of the HTML code.
	 * @param publications the publications to export.
	 * @param configurator the exporter configurator.
	 */
	protected void exportFlatList(StringBuilder html, Iterable<? extends Publication> publications, ExporterConfigurator configurator) {
		html.append("<ul>"); //$NON-NLS-1$
		for (final Publication publication : publications) {
			exportPublication(html, publication, configurator);
		}
		html.append("</ul>"); //$NON-NLS-1$
	}

	/** Export in HTML a single publication.
	 *
	 * @param html the receiver of the HTML.
	 * @param publication the publication, never {@code null}.
	 * @param configurator the configurator for the exporter.
	 */
	public void exportPublication(StringBuilder html, Publication publication, ExporterConfigurator configurator) {
		assert html != null;
		assert publication != null;
		assert configurator != null;
		html.append("<li align=\"justify\">"); //$NON-NLS-1$
		final java.util.Locale loc = java.util.Locale.getDefault();
		try {
			java.util.Locale.setDefault(publication.getMajorLanguage().getLocale());
			exportAuthors(html, publication, configurator, true);
			exportDescription(html, publication, configurator);
		} finally {
			java.util.Locale.setDefault(loc);
		}
		html.append("</li>"); //$NON-NLS-1$
	}

}
