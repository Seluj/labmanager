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

package fr.utbm.ciad.labmanager.services.admin;

import fr.utbm.ciad.labmanager.components.start.JsonDatabaseInitializer;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.services.AbstractService;
import fr.utbm.ciad.labmanager.services.admin.carnot.IcartsActivityReportGenerator;
import fr.utbm.ciad.labmanager.services.admin.spim.SpimActivityReportGenerator;
import fr.utbm.ciad.labmanager.services.admin.utbm.UtbmActivityReportGenerator;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.io.AutomaticDeletionFileInputStream;
import fr.utbm.ciad.labmanager.utils.io.IoConstants;
import fr.utbm.ciad.labmanager.utils.io.json.DatabaseToJsonExporter;
import fr.utbm.ciad.labmanager.utils.io.json.DatabaseToZipExporter;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.transaction.Transactional;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.util.OutputParameter;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * Service for managing database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Service
public class DatabaseService extends AbstractService {

    private static final long serialVersionUID = 9203318168830086896L;

    private final DatabaseToJsonExporter jsonExporter;

    private final DatabaseToZipExporter zipExporter;

    private final UtbmActivityReportGenerator utbmActivityReportGenerator;

    private final SpimActivityReportGenerator spimActivityReportGenerator;

    private final IcartsActivityReportGenerator icartsActivityReportGenerator;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param jsonExporter                  the database exporter to JSON file.
     * @param zipExporter                   the database exporter to ZIP file.
     * @param utbmActivityReportGenerator   the generator of activity report with UTBM standard.
     * @param ubActivityReportGenerator     the generator of activity report with UB standard.
     * @param spimActivityReportGenerator   the generator of activity report with SPIM standard.
     * @param icartsActivityReportGenerator the generator of activity report with IC ARTS standard.
     * @param messages                      the provider of localized messages.
     * @param constants                     the accessor to the live constants.
     * @param sessionFactory                the factory of JPA session.
     */
    public DatabaseService(
            @Autowired DatabaseToJsonExporter jsonExporter,
            @Autowired DatabaseToZipExporter zipExporter,
            @Autowired UtbmActivityReportGenerator utbmActivityReportGenerator,
            @Autowired SpimActivityReportGenerator spimActivityReportGenerator,
            @Autowired IcartsActivityReportGenerator icartsActivityReportGenerator,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(messages, constants, sessionFactory);
        this.jsonExporter = jsonExporter;
        this.zipExporter = zipExporter;
        this.utbmActivityReportGenerator = utbmActivityReportGenerator;
        this.spimActivityReportGenerator = spimActivityReportGenerator;
        this.icartsActivityReportGenerator = icartsActivityReportGenerator;
    }

    /**
     * Export database content to Json.
     *
     * @param locale      the locale to be used for obtaining the progression messages.
     * @param logger      the logger to use for put a message in the log.
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    @Transactional
    public InputStream exportJson(Locale locale, Logger logger, Progression progression) throws Exception {
        logger.info("Exporting database content to JSON"); //$NON-NLS-1$
        progression.setProperties(0, 0, 50, false);
        final var bytes = new OutputParameter<byte[]>();
        inSession(session -> {
            final var mapper = JsonUtils.createMapper();
            final var obj = this.jsonExporter.exportFromDatabaseToJsonObject(mapper.getNodeFactory(), null, null, locale, logger, progression.subTask(40));
            if (obj != null) {
                bytes.set(mapper.writeValueAsBytes(obj));
            }
            progression.increment(5);
        });
        if (bytes.isSet()) {
            final var is = new ByteArrayInputStream(bytes.get());
            progression.end();
            return is;
        }
        progression.end();
        return null;
    }

    /**
     * Export database content to ZIP.
     *
     * @param locale      the locale to be used for obtaining the progression messages.
     * @param logger      the logger to use for put a message in the log.
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    @SuppressWarnings("resource")
    @Transactional
    public InputStream exportZip(Locale locale, Logger logger, Progression progression) throws Exception {
        logger.info("Exporting database content to ZIP"); //$NON-NLS-1$
        final var exporter = inSessionWithResult(session -> {
            return this.zipExporter.startExportFromDatabase(locale, logger, progression);
        });

        final var tmpFile = File.createTempFile(JsonDatabaseInitializer.INITIALIZATION_BASENAME, IoConstants.ZIP_FILENAME_EXTENSION);
        exporter.exportToZip(new FileOutputStream(tmpFile));
        return new AutomaticDeletionFileInputStream(tmpFile);
    }

    /**
     * Export the Excel file for the UTBM annual activity report.
     *
     * @param organizationId the identifier of the organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale to be used for obtaining the progression messages.
     * @param logger         the logger to use for put a message in the log.
     * @param progression    the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    public DownloadableFileDescription exportUtbmActivityReport(long organizationId, int year, Locale locale, Logger logger, Progression progression) throws Exception {
        logger.info("Exporting UTBM's activity report for year " + year + " and organization " + organizationId); //$NON-NLS-1$ //$NON-NLS-2$
        return this.utbmActivityReportGenerator.exportUtbmAnnualReport(organizationId, year, locale, progression);
    }

    /**
     * Export the Excel file for the SPIM annual activity report.
     *
     * @param organizationId the identifier of the organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale to be used for obtaining the progression messages.
     * @param logger         the logger to use for put a message in the log.
     * @param progression    the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    public DownloadableFileDescription exportSpimActivityReport(long organizationId, int year, Locale locale, Logger logger, Progression progression) throws Exception {
        logger.info("Exporting SPIM's activity report for year " + year + " and organization " + organizationId); //$NON-NLS-1$ //$NON-NLS-2$
        return this.spimActivityReportGenerator.exportSpimAnnualReport(organizationId, year, locale, progression);
    }

    /**
     * Export the Excel file for the IC-ARTS annual activity report.
     *
     * @param organizationId the identifier of the organization for which the publications must be extracted.
     * @param year           the reference year.
     * @param locale         the locale to be used for obtaining the progression messages.
     * @param logger         the logger to use for put a message in the log.
     * @param progression    the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    public DownloadableFileDescription exportIcartsActivityReport(long organizationId, int year, Locale locale, Logger logger, Progression progression) throws Exception {
        logger.info("Exporting Institut Carnot ARTS' activity report for year " + year + " and organization " + organizationId); //$NON-NLS-1$ //$NON-NLS-2$
        return this.icartsActivityReportGenerator.exportIcartsAnnualReport(organizationId, year, locale, progression);
    }

}
