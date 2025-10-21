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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.publication.JournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import org.arakhne.afc.progress.Progression;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

/**
 * Exporter of publications to JSON using the Jackson API.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class JacksonJsonExporter extends AbstractJsonExporter {

    /**
     * Constructor.
     *
     * @param messages the accessors to the localized messages.
     * @param doiTools the tools for managing the DOI.
     */
    public JacksonJsonExporter(@Autowired MessageSourceAccessor messages) {
        super(messages);
    }

    @Override
    public String exportPublicationsWithRootKeys(Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                                 Progression progression, Logger logger, String... rootKeys) throws Exception {
        progression.setProperties(0, 0, publications.size() + 1, false);
        final var root = exportPublicationsAsTreeWithRootKeys(publications, configurator,
                progression.subTask(publications.size()), logger, null, rootKeys);
        final var mapper = JsonUtils.createMapper();
        final var output = mapper.writer().writeValueAsString(root);
        progression.end();
        return output;
    }

    @Override
    public JsonNode exportPublicationsAsTreeWithRootKeys(Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                                         Progression progression, Logger logger, Procedure2<Publication, ObjectNode> callback, String... rootKeys) throws Exception {
        progression.setProperties(0, 0, publications.size() + (rootKeys == null ? 0 : rootKeys.length), false);
        final var mapper = JsonUtils.createMapper();
        final var node = exportPublicationsAsTree(publications, configurator, progression.subTask(publications.size()), logger,
                callback, mapper, configurator.getLocaleOrLanguageLocale(null));
        var root = node;
        if (rootKeys != null) {
            for (var i = rootKeys.length - 1; i >= 0; --i) {
                final var rkey = rootKeys[i];
                final var onode = mapper.createObjectNode();
                onode.set(rkey, root);
                root = onode;
                progression.increment();
            }
        }
        progression.end();
        return root;
    }

    /**
     * Export the publications into a JSON tree.
     *
     * @param publications the publications to export.
     * @param configurator the configurator for the export, never {@code null}.
     * @param progression  the progression indicator.
     * @param logger       the logger to be used.
     * @param callback     a function that is invoked for each publication for giving the opportunity
     *                     to fill up the Json node of the publication.
     * @param mapper       the JSON object creator and mapper.
     * @param locale       the locale to use.
     * @return the representation of the publications.
     * @throws Exception if the publication cannot be converted.
     */
    public JsonNode exportPublicationsAsTree(Collection<? extends Publication> publications, ExporterConfigurator configurator,
                                             Progression progression, Logger logger, Procedure2<Publication, ObjectNode> callback, ObjectMapper mapper, Locale locale) throws Exception {
        if (publications == null) {
            progression.end();
            return mapper.nullNode();
        }
        progression.setProperties(0, 0, publications.size(), false);
        final var array = mapper.createArrayNode();
        for (final var publication : publications) {
            final var entryNode = exportPublication(publication, configurator, mapper);
            // Add additional fields by the callback function
            if (callback != null) {
                callback.apply(publication, entryNode);
            }
            // Make aliasing of the year
            if (entryNode.has("publicationYear")) { //$NON-NLS-1$
                entryNode.set("year", entryNode.get("publicationYear").deepCopy()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // Make aliasing for the Scimago Quartiles
            if (entryNode.has("scimagoQIndex") && publication instanceof JournalBasedPublication jbp) { //$NON-NLS-1$
                final var url = configurator.getJournalService().getScimagoQuartileImageURLByJournal(jbp.getJournal(), logger);
                if (url != null) {
                    entryNode.set("scimagoQIndex_imageUrl", entryNode.textNode(url.toExternalForm())); //$NON-NLS-1$
                }
            }
            // Add labels for type and category
            if (configurator.isTypeAndCategoryLabels()) {
                if (entryNode.has("type")) { //$NON-NLS-1$
                    entryNode.set("htmlTypeLabel", mapper.valueToTree(publication.getType().getLabel(getMessageSourceAccessor(), locale))); //$NON-NLS-1$
                }
                if (entryNode.has("category")) { //$NON-NLS-1$
                    entryNode.set("htmlCategoryLabel", mapper.valueToTree(publication.getCategory().getLabel(getMessageSourceAccessor(), locale))); //$NON-NLS-1$
                }
            }
            //
            array.add(entryNode);
            progression.increment();
        }
        progression.end();
        return array;
    }

    /**
     * Export in JSON a single publication.
     *
     * @param publication  the publication, never {@code null}.
     * @param configurator the configurator for the exporter.
     * @param mapper       the JSON object creator and mapper.
     * @return the representation of the publication.
     * @throws Exception if the publication cannot be converted.
     */
    @SuppressWarnings("static-method")
    public ObjectNode exportPublication(Publication publication, ExporterConfigurator configurator, ObjectMapper mapper) throws Exception {
        assert publication != null;
        return mapper.valueToTree(publication);
    }

    /**
     * Serializer of {@code IdentifiableEntity} that serializes the identifiers.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    protected static class IdentifierSerializer extends JsonSerializer<IdentifiableEntity> {

        @Override
        public void serialize(IdentifiableEntity value, JsonGenerator generator, SerializerProvider serializers)
                throws IOException {
            generator.writeNumber(value.getId());
        }

    }

}
