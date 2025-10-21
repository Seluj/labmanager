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

package fr.utbm.ciad.labmanager.utils.io.scimago;

import com.google.common.base.Strings;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Accessor to the online Scimago platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.scimagojr.com"
 * @since 2.5
 */
@Component
@Primary
public class OnlineScimagoPlatform implements ScimagoPlatform {

    /**
     * Name of the column for the journal id.
     *
     * @since 4.0
     */
    protected static final String SOURCE_ID_COLUMN_NAME = "Sourceid"; //$NON-NLS-1$

    /**
     * Zero-based index of the column for the journal id.
     *
     * @since 4.0
     */
    protected static final int SOURCE_ID_COLUMN_INDEX = 1;

    /**
     * Name of the column for the journal categories and their quartiles.
     *
     * @since 4.0
     */
    protected static final String CATEGORY_COLUMN_NAME = "Categories"; //$NON-NLS-1$

    /**
     * Zero-based index of the column for the journal categories and their quartiles.
     *
     * @since 4.0
     */
    protected static final int CATEGORY_COLUMN_INDEX = 22;

    /**
     * Name of the column for the journal best quartile.
     *
     * @since 4.0
     */
    protected static final String BEST_QUARTILE_COLUMN_NAME = "SJR Best Quartile"; //$NON-NLS-1$

    /**
     * Zero-based index of the column for the journal best quartile.
     *
     * @since 4.0
     */
    protected static final int BEST_QUARTILE_COLUMN_INDEX = 6;

    private static final String SCHEME = "https"; //$NON-NLS-1$

    private static final String HOST = "www.scimagojr.com"; //$NON-NLS-1$

    private static final String RANK_PATH = "journalrank.php"; //$NON-NLS-1$

    private static final String JOURNAL_PATH = "journalsearch.php"; //$NON-NLS-1$

    private static final String JOURNAL_PICTURE_PATH = "journal_img.php"; //$NON-NLS-1$

    private static final String YEAR_PARAM = "year"; //$NON-NLS-1$

    private static final String OUT_PARAM = "out"; //$NON-NLS-1$

    private static final String CSV_TYPE = "xls"; //$NON-NLS-1$

    private static final String TIP_PARAM = "tip"; //$NON-NLS-1$

    private static final String SID_TYPE = "sid"; //$NON-NLS-1$

    private static final String QUERY_PARAM = "q"; //$NON-NLS-1$

    private static final String ID_PARAM = "id"; //$NON-NLS-1$

    /**
     * Factory of URI builder.
     */
    protected final UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();

    private final Map<Integer, Map<String, Map<String, QuartileRanking>>> rankingCache = new ConcurrentHashMap<>();

    private boolean searchForColumnsFromNames;

    private static void analyzeCsvRecord(Integer categoryColumn, Integer bestQuartileColumn, String[] row,
                                         BiConsumer<String, QuartileRanking> callback) {
        if (categoryColumn != null) {
            final var rawCategories = row[categoryColumn.intValue()];
            if (!Strings.isNullOrEmpty(rawCategories)) {
                final var categories = rawCategories.split("\\s*[,;]\\s*"); //$NON-NLS-1$
                final var pattern = Pattern.compile("\\s*(.+?)\\s*\\(([^\\)]+)\\)\\s*"); //$NON-NLS-1$
                for (final var rawCategory : categories) {
                    final var matcher = pattern.matcher(rawCategory);
                    if (matcher.matches()) {
                        try {
                            final var quartile = QuartileRanking.valueOfCaseInsensitive(matcher.group(2));
                            final var name = matcher.group(1);
                            if (!Strings.isNullOrEmpty(name)) {
                                callback.accept(ScimagoPlatform.formatCategory(name), quartile);
                            }
                        } catch (Throwable ex) {
                            //
                        }
                    }
                }
            }
        }
        if (bestQuartileColumn != null) {
            try {
                final var quartile = QuartileRanking.valueOfCaseInsensitive(row[bestQuartileColumn.intValue()]);
                callback.accept(BEST, quartile);
            } catch (Throwable ex) {
                //
            }
        }
    }

    @SuppressWarnings("resource")
    private static void analyzeCsvRecords(URL csvUrl, boolean searchColumnsByName, Progression progress, Consumer4 consumer) {
        progress.setProperties(0, 0, 100, false);
        try (final var reader = new BufferedReader(new InputStreamReader(csvUrl.openStream()))) {
            final var parserBuilder = new CSVParserBuilder();
            parserBuilder.withSeparator(';');
            parserBuilder.withIgnoreLeadingWhiteSpace(true);
            parserBuilder.withQuoteChar('"');
            parserBuilder.withStrictQuotes(false);
            final var csvBuilder = new CSVReaderBuilder(reader);
            csvBuilder.withCSVParser(parserBuilder.build());
            final var csvReader = csvBuilder.build();
            // Search for the column headers
            var row = csvReader.readNext();
            if (row == null) {
                throw new IOException("Unable to find the column \"" + SOURCE_ID_COLUMN_NAME + "\" (index: " + SOURCE_ID_COLUMN_INDEX + ") in the Scimago CSV data source"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            Integer bestQuartileColumn = null;
            Integer categoryColumn = null;
            Integer sourceIdColumn = null;
            if (searchColumnsByName) {
                var i = 0;
                while (i < row.length && (sourceIdColumn == null || categoryColumn == null || bestQuartileColumn == null)) {
                    final var name = row[i];
                    if (sourceIdColumn == null && SOURCE_ID_COLUMN_NAME.equalsIgnoreCase(name)) {
                        sourceIdColumn = Integer.valueOf(i);
                    }
                    if (categoryColumn == null && CATEGORY_COLUMN_NAME.equalsIgnoreCase(name)) {
                        categoryColumn = Integer.valueOf(i);
                    }
                    if (bestQuartileColumn == null && BEST_QUARTILE_COLUMN_NAME.equalsIgnoreCase(name)) {
                        bestQuartileColumn = Integer.valueOf(i);
                    }
                    ++i;
                }
            } else {
                sourceIdColumn = Integer.valueOf(SOURCE_ID_COLUMN_INDEX);
                categoryColumn = Integer.valueOf(CATEGORY_COLUMN_INDEX);
                bestQuartileColumn = Integer.valueOf(BEST_QUARTILE_COLUMN_INDEX);
            }
            if (sourceIdColumn == null) {
                throw new IOException("Unable to find the column \"" + SOURCE_ID_COLUMN_NAME + "\" (index: " + SOURCE_ID_COLUMN_INDEX + ") in the Scimago CSV data source"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            if (bestQuartileColumn == null && categoryColumn == null) {
                throw new IOException("No column for quartiles in the Scimago CSV data source"); //$NON-NLS-1$
            }
            progress.increment();
            // Read records
            consumer.accept(csvReader, sourceIdColumn, categoryColumn, bestQuartileColumn, progress);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            progress.end();
        }
    }

    private static Map<String, Map<String, QuartileRanking>> readJournalRanking(URL csvUrl, boolean searchColumnsByName, Progression rootProgress) {
        final var ranking = new TreeMap<String, Map<String, QuartileRanking>>();
        analyzeCsvRecords(csvUrl, searchColumnsByName, rootProgress, (stream, sourceIdColumn, categoryColumn, bestQuartileColumn, progress) -> {
            var row = stream.readNext();
            final var rowProgress = progress.subTask(99, 0, row == null ? 0 : row.length);
            while (row != null) {
                final var journalRanking = new HashMap<String, QuartileRanking>();
                analyzeCsvRecord(categoryColumn, bestQuartileColumn, row,
                        (a, b) -> journalRanking.put(a, b));
                if (!journalRanking.isEmpty()) {
                    final var journalId = row[sourceIdColumn.intValue()];
                    ranking.put(journalId, journalRanking);
                }
                rowProgress.increment();
                row = stream.readNext();
            }
            rowProgress.end();
        });
        return ranking;
    }

    private static Progression ensureProgress(Progression progress) {
        return progress == null ? new DefaultProgression() : progress;
    }

    /**
     * Replies if the CSV columns should be detemrines based on the column names.
     *
     * @return {@code true} if the CSV columns are searched based on their names.
     * @since 4.0
     */
    public boolean getSearchForColumnsFromNames() {
        return this.searchForColumnsFromNames;
    }

    /**
     * Change the flag if the CSV columns should be detemrines based on the column names.
     *
     * @param enable {@code true} if the CSV columns are searched based on their names.
     * @since 4.0
     */
    public void setSearchForColumnsFromNames(boolean enable) {
        this.searchForColumnsFromNames = enable;
    }

    @Override
    public URL getJournalPictureUrl(String journalId) {
        if (!Strings.isNullOrEmpty(journalId)) {
            try {
                var builder = this.uriBuilderFactory.builder();
                builder = builder.scheme(SCHEME);
                builder = builder.host(HOST);
                builder = builder.path(JOURNAL_PICTURE_PATH);
                builder = builder.queryParam(ID_PARAM, journalId);
                final var uri = builder.build();
                return uri.toURL();
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }

    @Override
    public URL getJournalUrl(String journalId) {
        if (!Strings.isNullOrEmpty(journalId)) {
            try {
                var builder = this.uriBuilderFactory.builder();
                builder = builder.scheme(SCHEME);
                builder = builder.host(HOST);
                builder = builder.path(JOURNAL_PATH);
                builder = builder.queryParam(TIP_PARAM, SID_TYPE);
                builder = builder.queryParam(QUERY_PARAM, journalId);
                final var uri = builder.build();
                return uri.toURL();
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }

    @Override
    public URL getJournalCsvUrl(int year) {
        try {
            var builder = this.uriBuilderFactory.builder();
            builder = builder.scheme(SCHEME);
            builder = builder.host(HOST);
            builder = builder.path(RANK_PATH);
            builder = builder.queryParam(OUT_PARAM, CSV_TYPE);
            builder = builder.queryParam(YEAR_PARAM, Integer.valueOf(year));
            final var uri = builder.build();
            return uri.toURL();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<String, Map<String, QuartileRanking>> getJournalRanking(int year, URL csvUrl, Progression progress) throws Exception {
        return this.rankingCache.computeIfAbsent(Integer.valueOf(year), it -> readJournalRanking(csvUrl, getSearchForColumnsFromNames(), ensureProgress(progress)));
    }

    /**
     * Call back for {@link OnlineScimagoPlatform#analyzeCsvRecords(int, Consumer)}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://www.scimagojr.com"
     * @since 2.5
     */
    @FunctionalInterface
    private interface Consumer4 {

        /**
         * Callback.
         *
         * @param stream             the stream of CSV records.
         * @param sourceIdColumn     the column for the journal indentifier.
         * @param categoryColumn     the column for the categories.
         * @param bestQuartileColumn the column for the best quartile.
         * @param progress           a progress monitor.
         * @throws Exception if the CSV cannot be read.
         */
        void accept(CSVReader stream, Integer sourceIdColumn, Integer categoryColumn, Integer bestQuartileColumn,
                    Progression progress) throws Exception;

    }

}
