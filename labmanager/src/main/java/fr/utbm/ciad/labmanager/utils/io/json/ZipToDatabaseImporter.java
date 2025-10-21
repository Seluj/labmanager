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

import com.fasterxml.jackson.databind.JsonNode;
import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.json.JsonToDatabaseImporter.FileCallback;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipInputStream;

/**
 * Importer of ZIP (JSON+Files) data into the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see JsonToDatabaseImporter
 * @since 2.0.0
 */
@Component
public class ZipToDatabaseImporter extends AbstractComponent {

    private static final long serialVersionUID = 5922689907053791939L;

    private final JsonToDatabaseImporter jsonImporter;

    private final DownloadableFileManager download;

    /**
     * Constructor.
     *
     * @param messages     the provider of messages.
     * @param constants    the accessor to the constants.
     * @param jsonImporter the importer of JSON data.
     * @param download     the manager of downloaded files.
     */
    public ZipToDatabaseImporter(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired JsonToDatabaseImporter jsonImporter,
            @Autowired DownloadableFileManager download) {
        super(messages, constants);
        this.jsonImporter = jsonImporter;
        this.download = download;
    }

    private static void mkdirs(File file) {
        if (file != null && file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * Remove all the files from the target folders
     *
     * @throws IOException if there is problem for cleaning the folders.
     */
    public void cleanTargetFolders() throws IOException {
        deleteContent(this.download.getPdfRootFile());
        deleteContent(this.download.getAwardRootFile());
        deleteContent(this.download.getAddressBackgroundRootFile());
        deleteContent(this.download.getProjectImageRootFile());
        deleteContent(this.download.getProjectLogoRootFile());
        deleteContent(this.download.getProjectPowerpointRootFile());
        deleteContent(this.download.getProjectPressDocumentRootFile());
        deleteContent(this.download.getProjectScientificRequirementsRootFile());
    }

    private void deleteContent(File file) throws IOException {
        if (file != null) {
            final var absFile = this.download.normalizeForServerSide(file);
            final var files = absFile.listFiles(it -> {
                return FileSystem.hasExtension(it, ".pdf") || FileSystem.hasExtension(it, ".jpg") //$NON-NLS-1$ //$NON-NLS-2$
                        || FileSystem.hasExtension(it, ".jpeg") || FileSystem.hasExtension(it, ".gif") //$NON-NLS-1$ //$NON-NLS-2$
                        || FileSystem.hasExtension(it, ".png") || FileSystem.hasExtension(it, ".ppt") //$NON-NLS-1$ //$NON-NLS-2$
                        || FileSystem.hasExtension(it, ".pptx"); //$NON-NLS-1$
            });
            if (files != null) {
                for (final var child : files) {
                    FileSystem.delete(child);
                }
            }
        }
    }

    /**
     * Replies if the file with the given name is acceptable.
     *
     * @param filename the filename.
     * @return {@code true} if the file is accepted.
     */
    @SuppressWarnings("static-method")
    protected boolean isAcceptedDataFile(String filename) {
        final var ext = FileSystem.extension(filename);
        return ".pdf".equals(ext) || ".jpeg".equals(ext) //$NON-NLS-1$ //$NON-NLS-2$
                || ".jpg".equals(ext) || ".gif".equals(ext) //$NON-NLS-1$ //$NON-NLS-2$
                || ".png".equals(ext) || ".ppt".equals(ext) //$NON-NLS-1$ //$NON-NLS-2$
                || ".pptx".equals(ext); //$NON-NLS-1$
    }

    /**
     * Run the importer for ZIP data source only.
     *
     * @param url    the URL of the ZIP file to read.
     * @param logger the logger to use for put a message in the log.
     * @throws Exception if there is problem for importing.
     */
    public void importArchiveFileToDatabase(URL url, Logger logger) throws Exception {
        cleanTargetFolders();
        deleteTemporaryArea(logger);
        //
        try {
            JsonNode content = null;
            try (var zis = new ZipInputStream(url.openStream())) {
                var entry = zis.getNextEntry();
                while (entry != null) {
                    final var filename = entry.getName();
                    try (final var entryStream = new ZippedFileInputStream(zis)) {
                        final var lower = filename.toLowerCase();
                        if (lower.equals(ZipDatabaseConstants.DEFAULT_DBCONTENT_ATTACHMENT_BASENAME + ".json")) { //$NON-NLS-1$
                            if (content == null) {
                                content = readJson(entryStream, logger);
                            } else {
                                throw new Exception("To many JSON file in the ZIP archive."); //$NON-NLS-1$
                            }
                        } else if (isAcceptedDataFile(lower)) {
                            // Copy the files onto the server.
                            copyAttachedFileToTemporaryArea(filename, entryStream, logger);
                        }
                    }
                    entry = zis.getNextEntry();
                }
            }
            // Inject the JSON content into the database; Change the uploaded files on the fly.
            if (content != null) {
                final var callback = new UploadedFileManager();
                final var stats = this.jsonImporter.importJsonFileToDatabase(content, callback, logger);
                if (stats != null) {
                    stats.setPublicationAssociatedFileCount(callback.getFileCount());
                    stats.logSummaryOn(logger);
                }
            } else {
                cleanTargetFolders();
                logger.info("Nothing to be inserted from: " + url); //$NON-NLS-1$
            }
        } finally {
            deleteTemporaryArea(logger);
        }
    }

    /**
     * Copy the file from the ZIP file into the temporary folder.
     *
     * @param filename    the name of the file in the ZIP archive, i.e., in the original file system.
     * @param entryStream the stream of bytes for the file.
     * @param logger      the logger to use for put a message in the log.
     * @throws Exception if the file cannot be copied to the temporary folder.
     */
    protected void copyAttachedFileToTemporaryArea(String filename, InputStream entryStream, Logger logger) throws Exception {
        var outputFile = FileSystem.convertStringToFile(filename);
        if (!outputFile.isAbsolute()) {
            outputFile = FileSystem.join(this.download.getTemporaryRootFile(), outputFile);
            outputFile = this.download.normalizeForServerSide(outputFile);
            logger.info("Copying attached file: " + filename + "; to: " + outputFile.toString()); //$NON-NLS-1$ //$NON-NLS-2$
            mkdirs(outputFile);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                // Size of "-1" means that the size of the copy buffer is decided according to the operating system
                FileSystem.copy(entryStream, -1, fos);
            }
        } else {
            throw new Exception("Filename cannot be absolute: " + filename); //$NON-NLS-1$
        }
    }

    /**
     * Delete all the files in the temporary area.
     *
     * @param logger the logger to use for put a message in the log.
     */
    protected void deleteTemporaryArea(Logger logger) {
        var tempFile = this.download.getTemporaryRootFile();
        tempFile = this.download.normalizeForServerSide(tempFile);
        try {
            FileSystem.delete(tempFile);
        } catch (IOException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * Read the JSON content from the given ZIP entry.
     *
     * @param entryStream the stream for the ZIP entry.
     * @param logger      the logger to use for put a message in the log.
     * @return the content.
     * @throws IOException if the stream cannot be read.
     */
    @SuppressWarnings("static-method")
    protected JsonNode readJson(InputStream entryStream, Logger logger) throws IOException {
        logger.info("Reading JSON description"); //$NON-NLS-1$
        // Read the JSON content
        try (final var isr = new InputStreamReader(entryStream)) {
            final var mapper = JsonUtils.createMapper();
            return mapper.readTree(isr);
        }
    }

    /**
     * Input stream for a ZIP entry.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    private static class ZippedFileInputStream extends InputStream {

        private final ZipInputStream is;

        ZippedFileInputStream(ZipInputStream is) {
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            return this.is.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.is.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.is.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return this.is.readAllBytes();
        }

        @Override
        public void close() throws IOException {
            //
        }
    }

    /**
     * Manager of the uploaded file for computing and fixing the names in the database.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    private class UploadedFileManager implements FileCallback {

        private final File temporaryFolder;

        private int fileCount;

        /**
         * Constructor.
         */
        UploadedFileManager() {
            this.temporaryFolder = ZipToDatabaseImporter.this.download.normalizeForServerSide(
                    ZipToDatabaseImporter.this.download.getTemporaryRootFile());
        }

        /**
         * Replies the number of imported files.
         *
         * @return the number of imported files.
         */
        public int getFileCount() {
            return this.fileCount;
        }

        @Override
        public String publicationPdfFile(long dbId, String filename, Logger logger) {
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makePdfFilename(dbId),
                    ZipToDatabaseImporter.this.download.makePdfPictureFilename(dbId),
                    logger);
        }

        @Override
        public String publicationAwardFile(long dbId, String filename, Logger logger) {
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeAwardFilename(dbId),
                    ZipToDatabaseImporter.this.download.makeAwardPictureFilename(dbId),
                    logger);
        }

        @Override
        public String addressBackgroundImageFile(long dbId, String filename, Logger logger) {
            final var fileExtension = FileSystem.extension(filename);
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeAddressBackgroundImage(dbId, fileExtension),
                    logger);
        }

        @Override
        public String organizationLogoFile(long dbId, String filename, Logger logger) {
            final var fileExtension = FileSystem.extension(filename);
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeOrganizationLogoFilename(dbId, fileExtension),
                    logger);
        }

        @Override
        public String projectLogoFile(long dbId, String filename, Logger logger) {
            final var fileExtension = FileSystem.extension(filename);
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeProjectLogoFilename(dbId, fileExtension),
                    logger);
        }

        @Override
        public String projectImageFile(long dbId, int index, String filename, Logger logger) {
            final var fileExtension = FileSystem.extension(filename);
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeProjectImageFilename(dbId, index, fileExtension),
                    logger);
        }

        @Override
        public String projectScientificRequirementsFile(long dbId, String filename, Logger logger) {
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeProjectScientificRequirementsFilename(dbId),
                    ZipToDatabaseImporter.this.download.makeProjectScientificRequirementsPictureFilename(dbId),
                    logger);
        }

        @Override
        public String projectPressDocumentFile(long dbId, String filename, Logger logger) {
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeProjectPressDocumentFilename(dbId),
                    ZipToDatabaseImporter.this.download.makeProjectPressDocumentPictureFilename(dbId),
                    logger);
        }

        @Override
        public String projectPowerpointFile(long dbId, String filename, Logger logger) {
            final var fileExtension = FileSystem.extension(filename);
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeProjectPowerpointFilename(dbId, fileExtension),
                    ZipToDatabaseImporter.this.download.makeProjectPowerpointPictureFilename(dbId),
                    logger);
        }

        @Override
        public String teachingActivitySlideFile(long dbId, String filename, Logger logger) {
            return moveFile(filename, dbId,
                    ZipToDatabaseImporter.this.download.makeTeachingActivitySlidesFilename(dbId),
                    ZipToDatabaseImporter.this.download.makeTeachingActivitySlidesPictureFilename(dbId),
                    logger);
        }

        private String moveFile(String inFilename, long outId, File outFilename, File outPictureName, Logger logger) {
            try {
                final var inFile = FileSystem.join(this.temporaryFolder, inFilename);
                if (inFile.canRead()) {
                    final var outFile = ZipToDatabaseImporter.this.download.normalizeForServerSide(outFilename);
                    mkdirs(outFile);
                    FileSystem.copy(inFile, outFile);
                    FileSystem.delete(inFile);
                    ZipToDatabaseImporter.this.download.ensurePictureFile(outFilename, outPictureName, logger);
                    ++this.fileCount;
                    logger.info("Renaming file from " + inFilename + " to " + outFilename); //$NON-NLS-1$ //$NON-NLS-2$
                    return outFilename.toString();
                }
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private String moveFile(String inFilename, long outId, File outFilename, Logger logger) {
            try {
                final var inFile = FileSystem.join(this.temporaryFolder, inFilename);
                if (inFile.canRead()) {
                    final var outFile = ZipToDatabaseImporter.this.download.normalizeForServerSide(outFilename);
                    mkdirs(outFile);
                    FileSystem.copy(inFile, outFile);
                    FileSystem.delete(inFile);
                    ++this.fileCount;
                    logger.info("Renaming file from " + inFilename + " to " + outFilename); //$NON-NLS-1$ //$NON-NLS-2$
                    return outFilename.toString();
                }
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }

}
