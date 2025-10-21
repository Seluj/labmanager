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

package fr.utbm.ciad.labmanager.components.start;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.io.IoConstants;
import fr.utbm.ciad.labmanager.utils.io.json.JsonToDatabaseImporter;
import fr.utbm.ciad.labmanager.utils.io.json.ZipToDatabaseImporter;
import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.afc.vmutil.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.URL;

/**
 * This component fill up the database with JSON format that is independent of the database engine.
 *
 * <p>The configuration variable {@code labmanager.init.enable} enables to turn on or off this feature.
 *
 * <p>It is searching for a file with the name {@code data.json} or {@code data.zip} inside the folder
 * that is specified by the configuration variable {@code labmanager.init.data-source}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
public class JsonDatabaseInitializer implements ApplicationRunner, Serializable {

    /**
     * Basename of the data file that could be used for initialization.
     *
     * @since 4.0
     */
    public static final String INITIALIZATION_BASENAME = "data"; //$NON-NLS-1$
    /**
     * Name of the Json data file that could be used for initialization.
     *
     * @since 4.0
     */
    public static final String INITIALIZATION_JSON_DATA_FILENAME = INITIALIZATION_BASENAME + IoConstants.JSON_FILENAME_EXTENSION;
    /**
     * Name of the ZIP data file that could be used for initialization.
     *
     * @since 4.0
     */
    public static final String INITIALIZATION_ZIP_DATA_FILENAME = INITIALIZATION_BASENAME + IoConstants.ZIP_FILENAME_EXTENSION;
    private static final long serialVersionUID = 564704530921099221L;
    private final JsonToDatabaseImporter jsonImporter;

    private final ZipToDatabaseImporter zipImporter;

    private final String dataSourceFolder;

    private final boolean enabled;

    /**
     * Constructor.
     *
     * @param jsonImporter     the importer of JSON.
     * @param zipImporter      the importer of ZIP.
     * @param dataSourceFolder the folder in the local file system in which the data source file could be located.
     * @param enabled          from configuration file, indicates if the data import is enabled or not.
     */
    public JsonDatabaseInitializer(
            @Autowired JsonToDatabaseImporter jsonImporter,
            @Autowired ZipToDatabaseImporter zipImporter,
            @Value("${labmanager.init.data-source}") String dataSourceFolder,
            @Value("${labmanager.init.enable}") boolean enabled) {
        this.jsonImporter = jsonImporter;
        this.zipImporter = zipImporter;
        this.dataSourceFolder = dataSourceFolder;
        this.enabled = enabled;
    }

    /**
     * Replies the URL to the data script to use in the current WAR.
     *
     * @return the URL or {@code null} if none.
     */
    protected static URL getDataURLInWar() {
        try {
            final var url = Resources.getResource("/" + INITIALIZATION_JSON_DATA_FILENAME); //$NON-NLS-1$
            if (url != null) {
                try {
                    @SuppressWarnings("resource") final var is = url.openStream();
                    if (is != null) {
                        is.close();
                    }
                    return url;
                } catch (Throwable ex) {
                    //
                }
            }
        } catch (Throwable ex) {
            //
        }
        return null;
    }

    /**
     * Replies the URL to the data script to use in the local file system.
     *
     * @return the URL or {@code null} if none.
     */
    protected URL getDataURLInLocalFileSystem() {
        if (!Strings.isNullOrEmpty(this.dataSourceFolder)) {
            try {
                final var root = FileSystem.convertStringToFile(this.dataSourceFolder);
                final var file = FileSystem.join(root, INITIALIZATION_JSON_DATA_FILENAME);
                if (file != null && file.exists() && file.canRead()) {
                    try {
                        return FileSystem.convertFileToURL(file);
                    } catch (Throwable ex) {
                        //
                    }
                }
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the URL to the data script to use.
     *
     * @return the URL or {@code null} if none.
     */
    protected URL getJsonDataURL() {
        final var url = getDataURLInWar();
        if (url != null) {
            return url;
        }
        return getDataURLInLocalFileSystem();
    }

    /**
     * Replies the URL to the data archive to use.
     *
     * @return the URL or {@code null} if none.
     */
    protected URL getZipDataURL() {
        if (!Strings.isNullOrEmpty(this.dataSourceFolder)) {
            try {
                final var root = FileSystem.convertStringToFile(this.dataSourceFolder);
                final var file = FileSystem.join(root, INITIALIZATION_ZIP_DATA_FILENAME);
                if (file != null && file.exists() && file.canRead()) {
                    try {
                        return FileSystem.convertFileToURL(file);
                    } catch (Throwable ex) {
                        //
                    }
                }
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the URL of the data source (Zip or Json).
     *
     * @return the URL of the data source or {@code null} if none.
     */
    protected URL detectDataUrl() {
        final var zipUrl = getZipDataURL();
        if (zipUrl != null) {
            return zipUrl;
        }
        final var jsonUrl = getJsonDataURL();
        return jsonUrl;
    }

    /**
     * Replies the importer code to be used for the given URL of the data source.
     *
     * @param url    the URL of the data source or {@code null} if none.
     * @param logger the logger to use for put a message in the log.
     * @return the importer.
     */
    protected Importer detectImporter(URL url, Logger logger) {
        assert url != null;
        if (FileSystem.hasExtension(url, ".zip")) { //$NON-NLS-1$
            return it -> this.zipImporter.importArchiveFileToDatabase(it, logger);
        }
        if (FileSystem.hasExtension(url, ".json")) { //$NON-NLS-1$
            return it -> this.jsonImporter.importDataFileToDatabase(it, logger);
        }
        return null;
    }

    /**
     * Import the given archive file into the database.
     *
     * @param url      the URL of data to be imported.
     * @param importer the callback function for doing the importation concretely.
     * @param logger   the logger to use for put a message in the log.
     * @throws Exception if import process cannot be done.
     */
    @SuppressWarnings("static-method")
    protected void doImport(URL url, Importer importer, Logger logger) throws Exception {
        assert url != null;
        assert importer != null;
        logger.info("Database initialization with: " + url.toExternalForm()); //$NON-NLS-1$
        importer.importFrom(url);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final var logger = LoggerFactory.getLogger(getClass());
        if (this.enabled) {
            final var dataUrl = detectDataUrl();
            if (dataUrl != null) {
                final var importer = detectImporter(dataUrl, logger);
                if (importer != null) {
                    doImport(dataUrl, importer, logger);
                } else {
                    logger.warn("Database initialization is skipped because of lake of importer for " + dataUrl); //$NON-NLS-1$
                }
            } else {
                logger.info("Database initialization is skipped because of lake of data source"); //$NON-NLS-1$
            }
        } else {
            logger.info("Database initialization is disabled"); //$NON-NLS-1$
        }
    }

    /**
     * Importer of data into the database.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.2
     */
    @FunctionalInterface
    protected interface Importer {

        /**
         * Do the import from the data source with the given URL.
         *
         * @param url the URL of the data source.
         * @throws Exception any error during import process.
         */
        void importFrom(URL url) throws Exception;

    }

}
