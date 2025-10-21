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

package fr.utbm.ciad.labmanager.utils.io;

import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationCategory;
import io.reactivex.functions.BiConsumer;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.util.ListUtil;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.*;
import java.util.function.Consumer;

/**
 * Provides tools for exporting publications.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class AbstractPublicationExporter {

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param messages the accessor to the localized strings.
     */
    protected AbstractPublicationExporter(MessageSourceAccessor messages) {
        this.messages = messages;
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
     * Replies the comparator of publications that is used for sorting the publications.
     *
     * @return the comparator of publications.
     */
    @SuppressWarnings("static-method")
    public Comparator<? super Publication> getPublicationComparator() {
        return EntityUtils.getPreferredPublicationComparatorInLists();
    }

    /**
     * Replies the preferred label for the given publication category.
     *
     * @param locale   the locale to use for retrieving the labels.
     * @param category the category.
     * @return the label.
     */
    protected String getCategoryLabel(Locale locale, PublicationCategory category) {
        if (category == null) {
            return ""; //$NON-NLS-1$
        }
        return category.getLabel(getMessageSourceAccessor(), locale) + " (" + category.name() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Replies the preferred label for the given publication year.
     *
     * @param year the publication year.
     * @return the label.
     */
    @SuppressWarnings("static-method")
    protected String getYearLabel(Integer year) {
        if (year == null) {
            return ""; //$NON-NLS-1$
        }
        return year.toString();
    }

    /**
     * Generic implementation of the exporting logic depending on the grouping configuration.
     *
     * @param publications the list of publications.
     * @param configurator the exporter configuration.
     * @param progression  the progression indicator.
     * @param section      the lambda for generating a section.
     * @param subsection   the lambda for generating a subsection.
     * @param list         the lambda for generating a list of publications.
     * @throws Exception if the export cannot be done.
     */
    protected void exportPublicationsWithGroupingCriteria(Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                                          Progression progression, Consumer<String> section, Consumer<String> subsection, BiConsumer<List<Publication>, Progression> list) throws Exception {
        if (configurator.isGroupedByCategory() || configurator.isGroupedByYear()) {
            final var currentLocale = configurator.getLocaleOrLanguageLocale(null);
            if (configurator.isGroupedByCategory() && configurator.isGroupedByYear()) {
                progression.setProperties(0, 0, publications.size() * 2, false);
                final var groupedPublications = groupByCategoryAndYear(publications, progression.subTask(publications.size()));
                for (final var entry0 : groupedPublications.entrySet()) {
                    section.accept(getCategoryLabel(currentLocale, entry0.getKey()));
                    for (final var entry1 : entry0.getValue().entrySet()) {
                        subsection.accept(getYearLabel(entry1.getKey()));
                        list.accept(entry1.getValue(), progression);
                    }
                }
            } else if (configurator.isGroupedByCategory()) {
                progression.setProperties(0, 0, publications.size() * 2, false);
                final var groupedPublications = groupByCategory(publications, progression.subTask(publications.size()));
                for (final var entry : groupedPublications.entrySet()) {
                    section.accept(getCategoryLabel(currentLocale, entry.getKey()));
                    list.accept(entry.getValue(), progression);
                }
            } else {
                progression.setProperties(0, 0, publications.size() * 2, false);
                final var groupedPublications = groupByYear(publications, progression.subTask(publications.size()));
                for (final var entry : groupedPublications.entrySet()) {
                    section.accept(getYearLabel(entry.getKey()));
                    list.accept(entry.getValue(), progression);
                }
            }
        } else {
            progression.setProperties(0, 0, publications.size() * 2, false);
            final var ungroupedPublications = new ArrayList<Publication>();
            final Comparator<? super Publication> sorter = getPublicationComparator();
            for (final var publication : publications) {
                ListUtil.add(ungroupedPublications, sorter, publication, true, false);
                progression.increment();
            }
            list.accept(ungroupedPublications, progression.subTask(publications.size()));
        }
        progression.end();
    }

    /**
     * Group the publications per category and year.
     *
     * @param publications the publications to export.
     * @param progression  the progression indicator.
     */
    protected Map<PublicationCategory, Map<Integer, List<Publication>>> groupByCategoryAndYear(Collection<? extends Publication> publications,
                                                                                               Progression progression) {
        progression.setProperties(0, 0, publications.size(), false);
        final var content = new TreeMap<PublicationCategory, Map<Integer, List<Publication>>>();
        final Comparator<? super Publication> sorter = getPublicationComparator();
        for (final var publication : publications) {
            final var category = publication.getCategory();
            final var years = content.computeIfAbsent(category, it -> new TreeMap<>(Collections.reverseOrder()));
            final var year = Integer.valueOf(publication.getPublicationYear());
            final var list = years.computeIfAbsent(year, it -> new ArrayList<>());
            ListUtil.add(list, sorter, publication, true, false);
            progression.increment();
        }
        progression.end();
        return content;
    }

    /**
     * Group the publications per category only.
     *
     * @param publications the publications to export.
     * @param progression  the progression indicator.
     */
    protected Map<PublicationCategory, List<Publication>> groupByCategory(Collection<? extends Publication> publications, Progression progression) {
        progression.setProperties(0, 0, publications.size(), false);
        final var content = new TreeMap<PublicationCategory, List<Publication>>();
        final Comparator<? super Publication> sorter = getPublicationComparator();
        for (final var publication : publications) {
            final var category = publication.getCategory();
            final var list = content.computeIfAbsent(category, it -> new ArrayList<>());
            ListUtil.add(list, sorter, publication, true, false);
            progression.increment();
        }
        progression.end();
        return content;
    }

    /**
     * Group the publications per year only.
     *
     * @param publications the publications to export.
     * @param progression  the progression indicator.
     */
    protected Map<Integer, List<Publication>> groupByYear(Collection<? extends Publication> publications, Progression progression) {
        progression.setProperties(0, 0, publications.size(), false);
        final var content = new TreeMap<Integer, List<Publication>>(Collections.reverseOrder());
        final Comparator<? super Publication> sorter = getPublicationComparator();
        for (final var publication : publications) {
            final var year = Integer.valueOf(publication.getPublicationYear());
            final var list = content.computeIfAbsent(year, it -> new ArrayList<>());
            ListUtil.add(list, sorter, publication, true, false);
            progression.increment();
        }
        progression.end();
        return content;
    }

}
