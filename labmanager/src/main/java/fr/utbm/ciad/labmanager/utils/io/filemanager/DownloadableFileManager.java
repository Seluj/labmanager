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

package fr.utbm.ciad.labmanager.utils.io.filemanager;

import org.arakhne.afc.sizediterator.SizedIterator;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure3;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for managing the downloadable files.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface DownloadableFileManager extends FileManager {

    /**
     * Delete from the server the downloadable PDF file associated to the publication with given identifier.
     *
     * @param id     the identifier of the publication.
     * @param logger the logger to be used in this function.
     * @throws IOException if the file cannot be deleted.
     */
    void deletePublicationPdfFile(long id, Logger logger) throws IOException;

    /**
     * Delete from the server the downloadable award's PDF file associated to the publication with given identifier.
     *
     * @param id     the identifier of the publication.
     * @param logger the logger to be used in this function.
     * @throws IOException if the file cannot be deleted.
     */
    void deletePublicationAwardPdfFile(long id, Logger logger) throws IOException;

    /**
     * Delete from the server the address background image associated to the address with given identifier.
     *
     * @param id            the identifier of the address.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @param logger        the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     */
    void deleteAddressBackgroundImage(long id, String fileExtension, Logger logger);

    /**
     * Delete from the server the address background image associated to the address with given identifier.
     *
     * @param id     the identifier of the address.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 4.0
     */
    void deleteAddressBackgroundImage(long id, Logger logger);

    /**
     * Delete from the server the logo image associated to the project with given identifier.
     *
     * @param id            the identifier of the project.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @param logger        the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectLogo(long id, String fileExtension, Logger logger);

    /**
     * Delete from the server the logo image associated to the project with given identifier.
     *
     * @param id     the identifier of the project.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 4.0
     */
    void deleteProjectLogo(long id, Logger logger);

    /**
     * Delete from the server the image at the given index and associated to the project with given identifier.
     *
     * @param id            the identifier of the project.
     * @param imageIndex    the index of the image in the list of associated images.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @param logger        the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectImage(long id, int imageIndex, String fileExtension, Logger logger);

    /**
     * Delete from the server the image at the given index and associated to the project with given identifier.
     *
     * @param id     the identifier of the project.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 4.0
     */
    void deleteProjectImage(long id, Logger logger);

    /**
     * Delete from the server the scientific requirements associated to the project with given identifier.
     *
     * @param id     the identifier of the project.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectScientificRequirements(long id, Logger logger);

    /**
     * Delete from the server the Powerpoint associated to the project with given identifier.
     *
     * @param id            the identifier of the project.
     * @param fileExtension the filename extension for the image ({@code .ppt}, {@code .pptx}).
     * @param logger        the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectPowerpoint(long id, String fileExtension, Logger logger);

    /**
     * Delete from the server the Powerpoint associated to the project with given identifier.
     *
     * @param id     the identifier of the project.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectPowerpoint(long id, Logger logger);

    /**
     * Delete from the server the press document associated to the project with given identifier.
     *
     * @param id     the identifier of the project.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.0
     */
    void deleteProjectPressDocument(long id, Logger logger);

    /**
     * Delete from the server the slides associated to the teaching activities with given identifier.
     *
     * @param id     the identifier of the teaching activity.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.4
     */
    void deleteTeachingActivitySlides(long id, Logger logger);

    /**
     * Replies the path to the a folder that could be used temporary.
     *
     * @return the path to the temporary folder.
     */
    File getTemporaryRootFile();

    /**
     * Replies the path to the root folder for pdf files.
     *
     * @return the path to the root folder.
     */
    File getPdfRootFile();

    /**
     * Replies the path to the root folder for award files.
     *
     * @return the path to the root folder.
     */
    File getAwardRootFile();

    /**
     * Replies the path to the root folder for address background images.
     *
     * @return the path to the root folder.
     */
    File getAddressBackgroundRootFile();

    /**
     * Replies the path to the root folder for project logos.
     *
     * @return the path to the root folder.
     * @since 3.0
     */
    File getProjectLogoRootFile();

    /**
     * Replies the path to the root folder for project images.
     *
     * @return the path to the root folder.
     * @since 3.0
     */
    File getProjectImageRootFile();

    /**
     * Replies the path to the root folder for project scientific requirements.
     *
     * @return the path to the root folder.
     * @since 3.0
     */
    File getProjectScientificRequirementsRootFile();

    /**
     * Replies the path to the root folder for project PowerPoint.
     *
     * @return the path to the root folder.
     * @since 3.0
     */
    File getProjectPowerpointRootFile();

    /**
     * Replies the path to the root folder for project press document.
     *
     * @return the path to the root folder.
     * @since 3.0
     */
    File getProjectPressDocumentRootFile();

    /**
     * Replies the path to the root folder for slides of a teaching activity.
     *
     * @return the path to the root folder.
     * @since 3.4
     */
    File getTeachingActivitySlidesRootFile();

    /**
     * Replies the path to the root folder for saved files.
     *
     * @return the path to the root folder.
     * @since 2.2
     */
    File getSavingDataRootFile();

    /**
     * Make the path to the PDF downloadable file for the publication with the given identifier.
     *
     * @param publicationId the identifier of the publication.
     * @return the path to the downloadable file.
     */
    File makePdfFilename(long publicationId);

    /**
     * Make the path to the image that corresponds to the PDF downloadable file for the publication with the given identifier.
     *
     * @param publicationId the identifier of the publication.
     * @return the path to the JPEG image associated to the downloadable file.
     */
    File makePdfPictureFilename(long publicationId);

    /**
     * Make the path to the award downloadable file for the publication with the given identifier.
     *
     * @param publicationId the identifier of the publication.
     * @return the path to the downloadable file.
     */
    File makeAwardFilename(long publicationId);

    /**
     * Make the path to the image that corresponds to the award downloadable file for the publication with the given identifier.
     *
     * @param publicationId the identifier of the publication.
     * @return the path to the JPEG image associated to the downloadable file.
     */
    File makeAwardPictureFilename(long publicationId);

    /**
     * Make the path to the image that corresponds to the address background file for the address with the given identifier.
     *
     * @param addressId     the identifier of the address.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @return the path to the image associated to the background image.
     */
    File makeAddressBackgroundImage(long addressId, String fileExtension);

    /**
     * Make the path to the image that corresponds to the logo for the project with the given identifier.
     *
     * @param projectId     the identifier of the project.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @return the path to the image associated to the logo.
     * @since 3.0
     */
    File makeProjectLogoFilename(long projectId, String fileExtension);

    /**
     * Make the path to the image that corresponds to the image for the project with the given identifier.
     *
     * @param projectId     the identifier of the project.
     * @param imageIndex    the index of the image in the list of associated images.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @return the path to the image associated to the image.
     * @since 3.0
     */
    File makeProjectImageFilename(long projectId, int imageIndex, String fileExtension);

    /**
     * Make the path to the PDF that corresponds to the scientific requirements for the project with the given identifier.
     *
     * @param projectId the identifier of the project.
     * @return the path to the image associated to the project.
     * @since 3.0
     */
    File makeProjectScientificRequirementsFilename(long projectId);

    /**
     * Make the path to the PDF thumbnail that corresponds to the scientific requirements for the project with the given identifier.
     *
     * @param projectId the identifier of the project.
     * @return the path to the image associated to the project.
     * @since 3.0
     */
    File makeProjectScientificRequirementsPictureFilename(long projectId);

    /**
     * Make the path to the Powerpoint that corresponds to the presentation for the project with the given identifier.
     *
     * @param projectId     the identifier of the project.
     * @param fileExtension the filename extension for the image ({@code .ppt}, {@code .pptx}).
     * @return the path to the Powerpoint associated to the presentation.
     * @since 3.0
     */
    File makeProjectPowerpointFilename(long projectId, String fileExtension);

    /**
     * Make the path to the Powerpoint thumbnail that corresponds to the presentation for the project with the given identifier.
     *
     * @param projectId the identifier of the project.
     * @return the path to the Powerpoint tumbnail associated to the presentation.
     * @since 3.0
     */
    File makeProjectPowerpointPictureFilename(long projectId);

    /**
     * Make the path to the PDF that corresponds to the press document for the project with the given identifier.
     *
     * @param projectId the identifier of the project.
     * @return the path to the press document associated to the project.
     * @since 3.0
     */
    File makeProjectPressDocumentFilename(long projectId);

    /**
     * Make the path to the PDF thumbnail that corresponds to the press document for the project with the given identifier.
     *
     * @param projectId the identifier of the project.
     * @return the path to the press document thumbnail associated to the project.
     * @since 3.0
     */
    File makeProjectPressDocumentPictureFilename(long projectId);

    /**
     * Make the path to the PDF that corresponds to the slides for the teaching activity with the given identifier.
     *
     * @param activityId the identifier of the teaching activity.
     * @return the path to the slides associated to the teaching activity.
     * @since 3.4
     */
    File makeTeachingActivitySlidesFilename(long activityId);

    /**
     * Make the path to the PDF thumbnail that corresponds to the slides for the teaching activity with the given identifier.
     *
     * @param activityId the identifier of the teaching activity.
     * @return the path to the image associated to the sldies of the activity.
     * @since 3.4
     */
    File makeTeachingActivitySlidesPictureFilename(long activityId);

    /**
     * Save the uploaded PDF file and its associated picture.
     *
     * @param pdfFilename      the filename of the PDF file to upload.
     * @param pictureFilename  the filename of the JPEG file to create.
     * @param multipartPdfFile the content of the PDF file.
     * @param logger           the logger to be used in this function.
     * @throws IOException if the file cannot be created.
     */
    void savePdfAndThumbnailFiles(File pdfFilename, File pictureFilename, MultipartFile multipartPdfFile, Logger logger) throws IOException;

    /**
     * Save the uploaded image.
     *
     * @param filename        the filename of the image to upload.
     * @param backgroundImage the content of the image.
     * @param logger          the logger to be used in this function.
     * @throws IOException if the file cannot be created.
     */
    void saveImage(File filename, MultipartFile backgroundImage, Logger logger) throws IOException;

    /**
     * Save the uploaded project Powerpoint.
     *
     * @param pptFilename        the filename of the Powerpoint to upload.
     * @param pictureFilename    the filename of the JPEG file to create.
     * @param powerpointDocument the content of the Powerpoint.
     * @param logger             the logger to be used in this function.
     * @throws IOException if the file cannot be created.
     * @since 3.0
     */
    void savePowerpointAndThumbnailFiles(File pptFilename, File pictureFilename, MultipartFile powerpointDocument, Logger logger) throws IOException;

    /**
     * Ensure that the picture file representing the PDF/Powerpoint file is generated.
     *
     * @param inputFilename   the filename of the PDF/PowerPoint file to read.
     * @param pictureFilename the filename of the JPEG file to create.
     * @param logger          the logger to be used in this function.
     * @throws IOException if the file cannot be created.
     */
    void ensurePictureFile(File inputFilename, File pictureFilename, Logger logger) throws IOException;

    /**
     * Move the uploaded files from one publication to another publication.
     * If the target files exist, they must not be replaced by the source files; but the source files
     * must disappear from the file system.
     *
     * @param sourceId the identifier of the publication that is currently associated to the uploaded files.
     * @param targetId the identifier of the publication that should be associated to the uploaded files in place of the previous publication.
     * @param logger   the logger to be used in this function.
     * @param callback lambda that is invoked each time a file has changed. The arguments are the id of the file, the old name and the new name.
     * @throws IOException if the files cannot be moved.
     */
    void moveFiles(long sourceId, long targetId, Logger logger, Procedure3<String, String, String> callback) throws IOException;

    /**
     * Replies the list of all the uploaded PDF files.
     *
     * @return the list of uploaded PDF files
     * @since 2.2
     */
    SizedIterator<File> getUploadedPdfFiles();

    /**
     * Replies the list of all the thumbnail files.
     *
     * @return the list of uploaded PDF files
     * @since 2.2
     */
    SizedIterator<File> getThumbailFiles();

    /**
     * Regenerate all the thumbnail for the given file.
     *
     * @param file   the PDF or PPT file.
     * @param logger the logger to be used in this function.
     * @throws IOException if the file cannot be created.
     */
    void regenerateThumbnail(File file, Logger logger) throws IOException;

    /**
     * Regenerate the thumbnail for the given file.
     *
     * @param basename the basename of the PDF or PPT file.
     * @param input    the bytes of the PDF or PPT file.
     * @param output   the receiver of the thumbnail bytes (JPEG picture).
     * @param logger   the logger to be used in this function.
     * @throws IOException if the thumbnail cannot be created.
     * @since 4.0
     */
    void generateThumbnail(String basename, InputStream input, OutputStream output, Logger logger) throws IOException;

    /**
     * Delete from the server the logo image associated to the organization with given identifier.
     *
     * @param id            the identifier of the organization.
     * @param fileExtension the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @param logger        the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 3.2
     */
    void deleteOrganizationLogo(long id, String fileExtension, Logger logger);

    /**
     * Delete from the server the logo image associated to the organization with given identifier.
     *
     * @param id     the identifier of the organization.
     * @param logger the logger to be used in this function.
     * @throws Exception if the file cannot be deleted.
     * @since 4.0
     */
    void deleteOrganizationLogo(long id, Logger logger);

    /**
     * Replies the path to the root folder for organization logos.
     *
     * @return the path to the root folder.
     * @since 3.2
     */
    File getOrganizationLogoRootFile();

    /**
     * Make the path to the image that corresponds to the logo for the orgnization with the given identifier.
     *
     * @param organizationId the identifier of the organization.
     * @param fileExtension  the filename extension for the image ({@code .jpg}, {@code .gif}, {@code .png}).
     * @return the path to the image associated to the logo.
     * @since 3.0
     */
    File makeOrganizationLogoFilename(long organizationId, String fileExtension);

    /**
     * Replies the fielname of the thumbnail assuming that the given input filename is
     * generated by one of the "make" functions of this {@link DownloadableFileManager}.
     *
     * @param file the filename to convert.
     * @return the thumbnail filename.
     * @since 4.0
     */
    File toThumbnailFilename(File file);

}
