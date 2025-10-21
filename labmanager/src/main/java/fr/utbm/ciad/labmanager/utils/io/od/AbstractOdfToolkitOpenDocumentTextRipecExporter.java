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

package fr.utbm.ciad.labmanager.utils.io.od;

import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.io.ByteArrayOutputStream;

/**
 * Utilities for exporting an activity report based on RIPEC-C3 standard to Open Document Text.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.7
 */
public abstract class AbstractOdfToolkitOpenDocumentTextRipecExporter implements OpenDocumentTextRipecExporter {

    /**
     * Reference to the utility helper for ODF text documents.
     */
    protected final OdfTextDocumentHelper textHelper;

    private String odtTemplatePath = OpenDocumentConstants.ODT_REPEC_C3_TEMPLATE_PATH;

    /**
     * Construct the exporter using the given text document helper.
     *
     * @param textHelper the helper for the ODF text documents.
     */
    public AbstractOdfToolkitOpenDocumentTextRipecExporter(OdfTextDocumentHelper textHelper) {
        this.textHelper = textHelper;
    }

    /**
     * Replies the path to the ODT template for the RIPEC-C3 activity report.
     *
     * @return the template path.
     */
    public String getRipecC3TemplatePath() {
        return this.odtTemplatePath;
    }

    /**
     * Change the path to the ODT template for the RIPEC-C3 activity report.
     *
     * @param path the template path.
     */
    public void setRipecC3TemplatePath(String path) {
        this.odtTemplatePath = path;
    }

    @Override
    public byte[] exportRipecC3(ExporterConfigurator configurator) throws Exception {
        final byte[] content;
        try (final var odt = this.textHelper.openOdtTemplate(getRipecC3TemplatePath())) {
            exportRipecC3(odt, configurator);
            try (final var output = new ByteArrayOutputStream()) {
                odt.save(output);
                output.flush();
                content = output.toByteArray();
            }
        }
        return content;
    }

    /**
     * Export the inputs in the given receiver using the RIPEC-C3 template.
     *
     * @param receiver     the receiver.
     * @param configurator the export configurator.
     * @throws Exception if it is impossible to export.
     */
    protected abstract void exportRipecC3(OdfTextDocument receiver, ExporterConfigurator configurator) throws Exception;

}
