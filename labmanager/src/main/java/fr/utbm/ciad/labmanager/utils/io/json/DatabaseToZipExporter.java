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

package fr.utbm.ciad.labmanager.utils.io.json;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.io.UnclosableStream;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exporter of ZIP (JSON+files) archive from the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see DatabaseToJsonExporter
 * @since 2.0.0
 */
@Component
public class DatabaseToZipExporter {

    private static final int COPY_BUFFER_SIZE = 4096;

    private static final int FIVE = 5;

    private static final int TWENTY = 20;

    private static final int EIGHTY = 80;

    private static final int FIVE_HUNDRED = 500;

    private static final int HUNDRED = 100;

    private final MessageSourceAccessor messages;

    private final DatabaseToJsonExporter jsonExporter;

    private final DownloadableFileManager download;

    /**
     * Constructor.
     *
     * @param messages     accessor to the localized messages.
     * @param jsonExporter the exporter to JSON file.
     * @param download     the tool for accessing the downloadable files.
     */
    public DatabaseToZipExporter(
            @Autowired MessageSourceAccessor messages,
            @Autowired DatabaseToJsonExporter jsonExporter,
            @Autowired DownloadableFileManager download) {
        this.messages = messages;
        this.jsonExporter = jsonExporter;
        this.download = download;
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
     * Start the exporting process to ZIP.
     *
     * @param locale   the locale to use for progression messages.
     * @param logger   the logger to use for put a message in the log.
     * @param progress the progression indicator.
     * @return the tool for finalizing the export to ZIP.
     * @throws Exception if there is problem for exporting.
     */
    public ZipExporter startExportFromDatabase(Locale locale, Logger logger, Progression progress) throws Exception {
        assert progress != null;
        progress.setProperties(0, 0, 100, false);
        final var content = this.jsonExporter.exportFromDatabase(locale, logger, progress.subTask(TWENTY));
        return new ZipExporter(content, locale, progress.subTask(EIGHTY));
    }

    private void writeJsonToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.in_file", locale)); //$NON-NLS-1$
        final var filename = ZipDatabaseConstants.DEFAULT_DBCONTENT_ATTACHMENT_BASENAME + ".json"; //$NON-NLS-1$
        final var entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        final var mapper = JsonUtils.createMapper();
        try (var ucs = new UnclosableStream(zos)) {
            mapper.writer().writeValue(ucs, json);
        }
        zos.closeEntry();
        zos.flush();
        progress.end();
    }

    @SuppressWarnings("unchecked")
    private void writePublicationFilesToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        var publications = (List<Map<String, Object>>) json.get(JsonTool.PUBLICATIONS_SECTION);
        if (publications != null && !publications.isEmpty()) {
            progress.setProperties(0, 0, publications.size(), false, getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.publication_files", locale)); //$NON-NLS-1$
            for (final var publication : publications) {
                final var targetFilename0 = (String) publication.get("pathToDownloadableAwardCertificate"); //$NON-NLS-1$
                if (!Strings.isNullOrEmpty(targetFilename0)) {
                    progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename0}, locale)); //$NON-NLS-1$
                    if (!copyFileToZip(targetFilename0, zos, progress.subTask(1))) {
                        publication.remove("pathToDownloadableAwardCertificate"); //$NON-NLS-1$
                    }
                }
                final var targetFilename1 = (String) publication.get("pathToDownloadablePDF"); //$NON-NLS-1$
                if (!Strings.isNullOrEmpty(targetFilename1)) {
                    progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename1}, locale)); //$NON-NLS-1$
                    if (!copyFileToZip(targetFilename1, zos, progress.subTask(1))) {
                        publication.remove("pathToDownloadablePDF"); //$NON-NLS-1$
                    }
                }
                progress.increment();
            }
        }
        progress.end();
    }

    @SuppressWarnings("unchecked")
    private void writeAddressFilesToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        var addresses = (List<Map<String, Object>>) json.get(JsonTool.ORGANIZATIONADDRESSES_SECTION);
        if (addresses != null && !addresses.isEmpty()) {
            progress.setProperties(0, 0, addresses.size(), false, getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.addresses", locale)); //$NON-NLS-1$
            for (final var address : addresses) {
                final var targetFilename0 = (String) address.get("pathToBackgroundImage"); //$NON-NLS-1$
                if (!Strings.isNullOrEmpty(targetFilename0)) {
                    progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename0}, locale)); //$NON-NLS-1$
                    if (!copyFileToZip(targetFilename0, zos, progress.subTask(1))) {
                        address.remove("pathToBackgroundImage"); //$NON-NLS-1$
                    }
                }
                progress.increment();
            }
        }
        progress.end();
    }

    @SuppressWarnings("unchecked")
    private void writeOrganizationFilesToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        var organizations = (List<Map<String, Object>>) json.get(JsonTool.RESEARCHORGANIZATIONS_SECTION);
        if (organizations != null && !organizations.isEmpty()) {
            progress.setProperties(0, 0, organizations.size(), false, getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.organizations", locale)); //$NON-NLS-1$
            for (final var organization : organizations) {
                for (final var fieldName : List.of(
                        "pathToLogo")) { //$NON-NLS-1$
                    final var targetFilename0 = (String) organization.get(fieldName);
                    if (!Strings.isNullOrEmpty(targetFilename0)) {
                        progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename0}, locale)); //$NON-NLS-1$
                        if (!copyFileToZip(targetFilename0, zos, progress.subTask(1))) {
                            organization.remove(fieldName);
                        }
                    }
                }
                progress.increment();
            }
        }
        progress.end();
    }

    @SuppressWarnings("unchecked")
    private void writeProjectFilesToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        var projects = (List<Map<String, Object>>) json.get(JsonTool.PROJECTS_SECTION);
        if (projects != null && !projects.isEmpty()) {
            progress.setProperties(0, 0, projects.size(), false, getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.projects", locale)); //$NON-NLS-1$
            for (final var project : projects) {
                for (final var fieldName : Arrays.asList(
                        "pathToLogo", //$NON-NLS-1$
                        "pathToPowerpoint", //$NON-NLS-1$
                        "pathToPressDocument", //$NON-NLS-1$
                        "pathToScientificRequirements")) { //$NON-NLS-1$
                    final var targetFilename0 = (String) project.get(fieldName);
                    if (!Strings.isNullOrEmpty(targetFilename0)) {
                        progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename0}, locale)); //$NON-NLS-1$
                        if (!copyFileToZip(targetFilename0, zos, progress.subTask(1))) {
                            project.remove(fieldName);
                        }
                    }
                }
                final var images = project.get("pathsToImages"); //$NON-NLS-1$
                if (images != null) {
                    for (final var imagePath : (Collection<String>) images) {
                        if (!Strings.isNullOrEmpty(imagePath)) {
                            progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{imagePath}, locale)); //$NON-NLS-1$
                            copyFileToZip(imagePath, zos, progress.subTask(1));
                        }
                    }
                }
                progress.increment();
            }
        }
        progress.end();
    }

    @SuppressWarnings("unchecked")
    private void writeTeachingActivityFilesToZip(Map<String, Object> json, ZipOutputStream zos, Locale locale, Progression progress) throws Exception {
        var activities = (List<Map<String, Object>>) json.get(JsonTool.TEACHING_ACTIVITY_SECTION);
        if (activities != null && !activities.isEmpty()) {
            progress.setProperties(0, 0, activities.size(), false, getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.teaching", locale)); //$NON-NLS-1$
            for (final var activity : activities) {
                for (final var fieldName : List.of(
                        "pathToSlides")) { //$NON-NLS-1$
                    final var targetFilename0 = (String) activity.get(fieldName);
                    if (!Strings.isNullOrEmpty(targetFilename0)) {
                        progress.setComment(getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting.compressing_file", new Object[]{targetFilename0}, locale)); //$NON-NLS-1$
                        if (!copyFileToZip(targetFilename0, zos, progress.subTask(1))) {
                            activity.remove(fieldName);
                        }
                    }
                }
                progress.increment();
            }
        }
        progress.end();
    }

    private boolean copyFileToZip(String filename, ZipOutputStream zos, Progression progress) throws Exception {
        final var lfilename = FileSystem.convertStringToFile(filename);
        final var localFile = this.download.normalizeForServerSide(lfilename);
        if (localFile.canRead()) {
            final var entry = new ZipEntry(lfilename.toString());
            zos.putNextEntry(entry);
            try (var ucs = new UnclosableStream(zos)) {
                try (var bis = new BufferedInputStream(new FileInputStream(localFile))) {
                    final var bytesIn = new byte[COPY_BUFFER_SIZE];
                    var read = 0;
                    final var total = bis.available();
                    progress.setProperties(0, 0, total, false);
                    while ((read = bis.read(bytesIn)) != -1) {
                        ucs.write(bytesIn, 0, read);
                        progress.increment(read);
                    }
                }
            }
            zos.closeEntry();
            zos.flush();
            progress.end();
            return true;
        }
        progress.end();
        return false;
    }

    /**
     * Instance of a session of exporting to ZIP.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see DatabaseToZipExporter
     * @since 2.0.0
     */
    public class ZipExporter {

        private final Map<String, Object> content;

        private final Locale locale;

        private final Progression progress;

        /**
         * Constructor.
         *
         * @param content  the content of the JSON file.
         * @param locale   the locale to be used for obtaining the messages.
         * @param progress the progress indicator to be used by this exporter.
         */
        public ZipExporter(Map<String, Object> content, Locale locale, Progression progress) {
            this.content = content;
            this.locale = locale;
            this.progress = progress;
        }

        /**
         * Run the exporter.
         *
         * @param output the receiver of the ZIP content, usually a stream associated to an HTTP response.
         * @throws Exception if there is problem for exporting.
         */
        public void exportToZip(OutputStream output) throws Exception {
            this.progress.setProperties(0, 0, FIVE_HUNDRED + FIVE, false,
                    getMessageSourceAccessor().getMessage("DatabaseToZipExporter.exporting")); //$NON-NLS-1$
            try (var zos = new ZipOutputStream(output)) {
                writePublicationFilesToZip(this.content, zos, this.locale, this.progress.subTask(HUNDRED));
                writeAddressFilesToZip(this.content, zos, this.locale, this.progress.subTask(HUNDRED));
                writeOrganizationFilesToZip(this.content, zos, this.locale, this.progress.subTask(HUNDRED));
                writeProjectFilesToZip(this.content, zos, this.locale, this.progress.subTask(HUNDRED));
                writeTeachingActivityFilesToZip(this.content, zos, this.locale, this.progress.subTask(HUNDRED));
                writeJsonToZip(this.content, zos, this.locale, this.progress.subTask(FIVE));
            }
            this.progress.end();
        }

    }

}
