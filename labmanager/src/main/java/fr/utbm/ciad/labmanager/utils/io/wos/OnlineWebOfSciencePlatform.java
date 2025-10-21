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

package fr.utbm.ciad.labmanager.utils.io.wos;

import com.google.common.base.Strings;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import fr.utbm.ciad.labmanager.utils.io.AbstractWebScraper;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * Accessor to the online Web-of-Science platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.webofscience.com"
 * @since 2.5
 */
@Component
@Primary
public class OnlineWebOfSciencePlatform extends AbstractWebScraper implements WebOfSciencePlatform {

    /**
     * Hostname of the server that may provide informations about journals
     */
    protected static final String JOURNAL_PATH = "journalid"; //$NON-NLS-1$
    /**
     * Name of the column for the journal ISSN.
     */
    protected static final String ISSN_COLUMN = "ISSN"; //$NON-NLS-1$
    /**
     * Name of the column for the journal E-ISSN.
     */
    protected static final String EISSN_COLUMN = "EISSN"; //$NON-NLS-1$
    /**
     * Name of the column for the journal quartiles.
     */
    protected static final String CATEGORY_COLUMN = "Category & Journal Quartiles"; //$NON-NLS-1$
    /**
     * Prefix for the name of the column for the journal impact factor.
     */
    protected static final String IMPACT_FACTOR_COLUMN_PREFIX = "IF"; //$NON-NLS-1$
    private static final String SCHEME = "https"; //$NON-NLS-1$
    private static final String JOURNAL_HOST = "wos-journal.info"; //$NON-NLS-1$
    /**
     * Factory of URI builder.
     */
    protected final UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
    private final Map<Integer, Map<String, WebOfScienceJournal>> rankingCache = new ConcurrentHashMap<>();

    private static WebOfScienceJournal analyzeCsvRecord(Integer categoryColumn, Integer impactFactorColumn, String[] row) {
        final var quartiles = new TreeMap<String, QuartileRanking>();
        if (categoryColumn != null) {
            final var rawCategories = row[categoryColumn.intValue()];
            if (!Strings.isNullOrEmpty(rawCategories)) {
                final var categories = rawCategories.split("\\s*[;]\\s*"); //$NON-NLS-1$
                final var pattern = Pattern.compile("\\s*(.+?)(?:\\s*\\-.*)?\\s*\\(([^\\)]+)\\)\\s*"); //$NON-NLS-1$
                for (final var rawCategory : categories) {
                    final var matcher = pattern.matcher(rawCategory);
                    if (matcher.matches()) {
                        try {
                            final var quartile = QuartileRanking.valueOfCaseInsensitive(matcher.group(2));
                            final var name = matcher.group(1);
                            if (!Strings.isNullOrEmpty(name)) {
                                quartiles.put(name.toLowerCase(), quartile);
                            }
                        } catch (Throwable ex) {
                            //
                        }
                    }
                }
            }
        }
        var impactFactor = 0f;
        if (impactFactorColumn != null) {
            final var rawIf = row[impactFactorColumn.intValue()];
            if (!Strings.isNullOrEmpty(rawIf)) {
                try {
                    impactFactor = Float.parseFloat(rawIf);
                } catch (Throwable ex) {
                    impactFactor = 0f;
                }
            }
        }
        if (!quartiles.isEmpty()) {
            return new WebOfScienceJournal(quartiles, impactFactor);
        }
        return null;
    }

    @SuppressWarnings("resource")
    private static void analyzeCsvRecords(InputStream csv, Progression progress, Consumer4 consumer) {
        progress.setProperties(0, 0, 100, false);
        try (final var reader = new BufferedReader(new InputStreamReader(csv))) {
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
                throw new IOException("Unable to find the column \"" + ISSN_COLUMN + "\" in the WoS CSV data source"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            Integer categoryColumn = null;
            Integer issnColumn = null;
            Integer eissnColumn = null;
            Integer ifColumn = null;
            final var prefix = IMPACT_FACTOR_COLUMN_PREFIX + " "; //$NON-NLS-1$
            var i = 0;
            while (i < row.length && ((issnColumn == null && eissnColumn == null) || categoryColumn == null || ifColumn == null)) {
                final var name = row[i];
                if (issnColumn == null && ISSN_COLUMN.equalsIgnoreCase(name)) {
                    issnColumn = Integer.valueOf(i);
                }
                if (eissnColumn == null && EISSN_COLUMN.equalsIgnoreCase(name)) {
                    eissnColumn = Integer.valueOf(i);
                }
                if (categoryColumn == null && CATEGORY_COLUMN.equalsIgnoreCase(name)) {
                    categoryColumn = Integer.valueOf(i);
                }
                if (ifColumn == null && (IMPACT_FACTOR_COLUMN_PREFIX.equalsIgnoreCase(name) || (name != null && name.startsWith(prefix)))) {
                    ifColumn = Integer.valueOf(i);
                }
                ++i;
            }
            if (issnColumn == null && eissnColumn == null) {
                throw new IOException("Unable to find the column \"" + ISSN_COLUMN + "\" in the WoS CSV data source"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (categoryColumn == null) {
                throw new IOException("No column for quartiles in the WoS CSV data source"); //$NON-NLS-1$
            }
            progress.increment();
            // Read records
            consumer.accept(csvReader, issnColumn, eissnColumn, categoryColumn, ifColumn, progress);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            progress.end();
        }
    }

    private Map<String, WebOfScienceJournal> readJournalRanking(InputStream csv, Progression rootProgress) {
        final var ranking = new TreeMap<String, WebOfScienceJournal>();
        analyzeCsvRecords(csv, rootProgress, (stream, issnColumn, eissnColumn, categoryColumn, ifColumn, progress) -> {
            var row = stream.readNext();
            final var rowProgress = progress.subTask(99, 0, row == null ? 0 : row.length);
            while (row != null) {
                final var journalRanking = analyzeCsvRecord(categoryColumn, ifColumn, row);
                if (journalRanking != null) {
                    // Put the ranking object two times in the map: one for the issn and one for the eissn
                    if (issnColumn != null) {
                        final var journalId = normalizeIssn(row[issnColumn.intValue()]);
                        if (!Strings.isNullOrEmpty(journalId)) {
                            ranking.put(journalId, journalRanking);
                        }
                    }
                    if (eissnColumn != null) {
                        final var journalId = normalizeIssn(row[eissnColumn.intValue()]);
                        if (!Strings.isNullOrEmpty(journalId)) {
                            ranking.put(journalId, journalRanking);
                        }
                    }
                }
                rowProgress.increment();
                row = stream.readNext();
            }
            rowProgress.end();
        });
        return ranking;
    }

    @Override
    public Map<String, WebOfScienceJournal> getJournalRanking(int year, InputStream csv, Progression progress)
            throws Exception {
        return this.rankingCache.computeIfAbsent(Integer.valueOf(year), it -> readJournalRanking(csv, ensureProgress(progress)));
    }

    @Override
    public WebOfSciencePerson getPersonRanking(URL wosProfile, Progression progress) throws Exception {
        final var prog = ensureProgress(progress);
        if (wosProfile != null) {
            final var output = new AtomicReference<WebOfSciencePerson>();
            loadHtmlPage(
                    DEFAULT_DEVELOPER,
                    wosProfile,
                    prog,
                    "[class=wat-author-metric]", //$NON-NLS-1$
                    5000,
                    (page, element0) -> {
                        final var elements = page.querySelectorAll("[class=wat-author-metric]"); //$NON-NLS-1$
                        var ihindex = -1;
                        var icitations = -1;
                        if (elements.size() >= 0) {
                            final var hindex = readInt(elements.get(0));
                            ihindex = positiveInt(hindex);
                        }
                        if (elements.size() >= 1) {
                            final var citations = readInt(elements.get(1));
                            icitations = positiveInt(citations);
                        }
                        output.set(new WebOfSciencePerson(ihindex, icitations));
                    });
            final var person = output.get();
            if (person != null) {
                return person;
            }
        }
        throw new IllegalArgumentException("Invalid Web-of-Science URL or no valid access: " + wosProfile); //$NON-NLS-1$
    }

    @Override
    public URL getJournalUrl(String journalId) {
        if (!Strings.isNullOrEmpty(journalId)) {
            try {
                var builder = this.uriBuilderFactory.builder();
                builder = builder.scheme(SCHEME);
                builder = builder.host(JOURNAL_HOST);
                builder = builder.pathSegment(JOURNAL_PATH, journalId);
                final var uri = builder.build();
                return uri.toURL();
            } catch (Exception ex) {
                //
            }
        }
        return null;
    }

    @Override
    public WebOfScienceJournal getJournalRanking(String journalId, Progression progress) throws Exception {
        final var prog = ensureProgress(progress);
        final var journalPage = getJournalUrl(journalId);
        if (journalPage != null) {
            final var output = new AtomicReference<WebOfScienceJournal>();
            loadHtmlPage(
                    DEFAULT_DEVELOPER,
                    journalPage,
                    prog,
                    "a[title=\"Search this journal with Google\"]", //$NON-NLS-1$
                    0,
                    (page, element0) -> {
                        final var impactFactorDiv = page.querySelector("//div[contains(text(), 'Impact Factor')]/following::div"); //$NON-NLS-1$
                        final var impactFactor = positiveFloat(readFloat(impactFactorDiv));

                        final var quartiles = new HashMap<String, QuartileRanking>();
                        final var categoryDiv = page.querySelector("//div[contains(text(), 'Category')]/following::div"); //$NON-NLS-1$
                        final var categoryText = readText(categoryDiv);
                        if (categoryText != null) {
                            final var categorieNames = categoryText.split("\\s*\\-\\s*SCIE\\s*"); //$NON-NLS-1$
                            final var categories = new TreeSet<String>();
                            for (final var category : categorieNames) {
                                categories.add(category.trim().toLowerCase());
                            }
                            final var quartilesDiv = page.querySelector("//div[contains(text(), 'Best ranking')]/following::div"); //$NON-NLS-1$
                            final var quartilesText = readText(quartilesDiv);
                            if (quartilesText != null) {
                                final var pattern = Pattern.compile("\\(\\s*(Q[1-4])\\s*\\)"); //$NON-NLS-1$
                                final var matcher = pattern.matcher(quartilesText);
                                if (matcher.find()) {
                                    final var quartileString = matcher.group(1);
                                    final var quartile = QuartileRanking.valueOfCaseInsensitive(quartileString);
                                    for (final var category : categories) {
                                        quartiles.put(category, quartile);
                                    }
                                }
                            }
                        }
                        output.set(new WebOfScienceJournal(quartiles, impactFactor));
                    });
            final var data = output.get();
            if (data != null) {
                return data;
            }
        }
        throw new IllegalArgumentException("Invalid Web-of-Science URL or no valid access: " + journalPage); //$NON-NLS-1$
    }

    /**
     * Call back for analyzing the journal CSV.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://www.webofscience.com"
     * @since 2.5
     */
    @FunctionalInterface
    private interface Consumer4 {

        /**
         * Callback.
         *
         * @param stream             the stream of CSV records.
         * @param issnColumn         the column for the journal ISSN.
         * @param eissnColumn        the column for the journal E-ISSN.
         * @param categoryColumn     the column for the categories.
         * @param impactFactorColumn the column for the impact factor.
         * @param progress           a progress monitor.
         * @throws Exception if the CSV cannot be read.
         */
        void accept(CSVReader stream, Integer issnColumn, Integer eissnColumn, Integer categoryColumn,
                    Integer impactFactorColumn, Progression progress) throws Exception;

    }

}
