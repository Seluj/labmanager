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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.publication.ConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.JournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class for JSON exporters.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public final class JsonUtils {

    private JsonUtils() {
        //
    }

    /**
     * Write a field and its value in the given generator.
     *
     * @param generator the JSO generator.
     * @param name      the name of the field.
     * @param value     the value of the field.
     * @throws IOException if the field cannot be written.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void writeField(JsonGenerator generator, String name, Object value) throws IOException {
        if (value instanceof JsonNode castValue) {
            generator.writeFieldName(name);
            generator.writeTree(castValue);
        } else if (value instanceof Byte castValue) {
            generator.writeNumberField(name, castValue.byteValue());
        } else if (value instanceof Short castValue) {
            generator.writeNumberField(name, castValue.shortValue());
        } else if (value instanceof Integer castValue) {
            generator.writeNumberField(name, castValue.intValue());
        } else if (value instanceof Long castValue) {
            generator.writeNumberField(name, castValue.longValue());
        } else if (value instanceof Float castValue) {
            generator.writeNumberField(name, castValue.floatValue());
        } else if (value instanceof Double castValue) {
            generator.writeNumberField(name, castValue.doubleValue());
        } else if (value instanceof BigInteger castValue) {
            generator.writeNumberField(name, castValue);
        } else if (value instanceof BigDecimal castValue) {
            generator.writeNumberField(name, castValue);
        } else if (value instanceof Number castValue) {
            generator.writeNumberField(name, castValue.doubleValue());
        } else if (value instanceof Boolean castValue) {
            generator.writeBooleanField(name, castValue.booleanValue());
        } else if (value != null && (value.getClass().isEnum() || value instanceof Enum)) {
            generator.writeObjectField(name, value);
        } else if (value instanceof Map iter) {
            final Iterator<Entry> iterator = iter.entrySet().iterator();
            generator.writeObjectFieldStart(name);
            while (iterator.hasNext()) {
                final var elt = iterator.next();
                if (elt.getKey() != null) {
                    writeField(generator, elt.getKey().toString(), elt.getValue());
                }
            }
            generator.writeEndObject();
        } else if (value instanceof Iterable iter) {
            final var iterator = iter.iterator();
            generator.writeArrayFieldStart(name);
            while (iterator.hasNext()) {
                final var elt = iterator.next();
                writeValue(generator, elt);
            }
            generator.writeEndArray();
        } else if (value != null && value.getClass().isArray()) {
            generator.writeArrayFieldStart(name);
            for (var i = 0; i < Array.getLength(value); ++i) {
                writeValue(generator, Array.get(value, i));
            }
            generator.writeEndArray();
        } else if (value instanceof JsonSerializable serializable) {
            generator.writeFieldName(name);
            serializable.serialize(generator, null);
        } else if (value != null) {
            generator.writeStringField(name, value.toString());
        }
    }

    /**
     * Write a value in the given generator.
     *
     * @param generator the JSO generator.
     * @param value     the value of the field.
     * @throws IOException if the field cannot be written.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void writeValue(JsonGenerator generator, Object value) throws IOException {
        if (value instanceof JsonNode castValue) {
            generator.writeTree(castValue);
        } else if (value instanceof Byte castValue) {
            generator.writeNumber(castValue.byteValue());
        } else if (value instanceof Short castValue) {
            generator.writeNumber(castValue.shortValue());
        } else if (value instanceof Integer castValue) {
            generator.writeNumber(castValue.intValue());
        } else if (value instanceof Long castValue) {
            generator.writeNumber(castValue.longValue());
        } else if (value instanceof Float castValue) {
            generator.writeNumber(castValue.floatValue());
        } else if (value instanceof Double castValue) {
            generator.writeNumber(castValue.doubleValue());
        } else if (value instanceof BigInteger castValue) {
            generator.writeNumber(castValue);
        } else if (value instanceof BigDecimal castValue) {
            generator.writeNumber(castValue);
        } else if (value instanceof Number castValue) {
            generator.writeNumber(castValue.doubleValue());
        } else if (value instanceof Boolean castValue) {
            generator.writeBoolean(castValue.booleanValue());
        } else if (value != null && (value.getClass().isEnum() || value instanceof Enum)) {
            generator.writeObject(value);
        } else if (value instanceof Map iter) {
            final Iterator<Entry> iterator = iter.entrySet().iterator();
            generator.writeStartObject();
            while (iterator.hasNext()) {
                final var elt = iterator.next();
                if (elt.getKey() != null) {
                    writeField(generator, elt.getKey().toString(), elt.getValue());
                }
            }
            generator.writeEndObject();
        } else if (value instanceof Iterable iter) {
            final var iterator = iter.iterator();
            generator.writeStartArray();
            while (iterator.hasNext()) {
                final var elt = iterator.next();
                writeValue(generator, elt);
            }
            generator.writeEndArray();
        } else if (value != null && value.getClass().isArray()) {
            generator.writeStartArray();
            for (var i = 0; i < Array.getLength(value); ++i) {
                writeValue(generator, Array.get(value, i));
            }
            generator.writeEndArray();
        } else if (value instanceof JsonSerializable serializable) {
            serializable.serialize(generator, null);
        } else if (value != null) {
            generator.writeString(value.toString());
        } else {
            // A value is expected to be written. So that a null node is output.
            generator.writeNull();
        }
    }

    /**
     * Write a reference to the given object.
     *
     * @param generator the JSON generator.
     * @param ref       the object to reference.
     * @throws IOException if the reference cannot be written.
     */
    public static void writeObjectRef(JsonGenerator generator, IdentifiableEntity ref) throws IOException {
        if (ref != null && ref.getId() != 0) {
            generator.writeStartObject();
            generator.writeNumberField("@id", ref.getId()); //$NON-NLS-1$
            generator.writeStringField("@type@", ref.getClass().getSimpleName()); //$NON-NLS-1$
            generator.writeEndObject();
        } else {
            generator.writeNull();
        }
    }

    /**
     * Write a field with a reference to the given object.
     *
     * @param generator the JSON generator.
     * @param fieldName the name of the field.
     * @param ref       the object to reference.
     * @throws IOException if the reference cannot be written.
     */
    public static void writeObjectRefField(JsonGenerator generator, String fieldName, IdentifiableEntity ref) throws IOException {
        if (ref != null && ref.getId() != 0) {
            generator.writeObjectFieldStart(fieldName);
            generator.writeNumberField("@id", ref.getId()); //$NON-NLS-1$
            generator.writeStringField("@type@", ref.getClass().getSimpleName()); //$NON-NLS-1$
            generator.writeEndObject();
        } else {
            generator.writeNullField(fieldName);
        }
    }

    /**
     * Write an object and its attributes.
     *
     * @param generator the JSON generator.
     * @param obj       the object to write.
     * @throws IOException if the reference cannot be written.
     */
    public static void writeObjectAndAttributes(JsonGenerator generator, AttributeProvider obj) throws IOException {
        if (obj != null) {
            generator.writeStartObject();
            obj.forEachAttribute((attrName, attrValue) -> {
                writeField(generator, attrName, attrValue);
            });
            generator.writeEndObject();
        } else {
            generator.writeNull();
        }
    }

    /**
     * Create a generator with cached entities.
     *
     * @param generator the JSON generator.
     * @return the generator.
     */
    public static CachedGenerator cache(JsonGenerator generator) {
        return new CachedGenerator(generator);
    }

    /**
     * Create a JSON object mapper with the proper configuration.
     * This mapper enables to serialize the {@link LocalDate}.
     *
     * @return the configured mapper.
     */
    public static ObjectMapper createMapper() {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }


    /**
     * Create the JSON node for the entity.
     *
     * @param entity the entity.
     * @return the JSON node, never {@code null}.
     * @throws JsonProcessingException if the nodes cannot be created
     */
    public static JsonNode getJsonNode(Object entity) throws JsonProcessingException {
        final var mapper = JsonUtils.createMapper();
        final var temporaryContent = mapper.writeValueAsString(entity);
        return mapper.readTree(temporaryContent);
    }

    /**
     * Generator of JSON with cached entities.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    public static class CachedGenerator {

        private final JsonGenerator generator;

        private final Set<Long> cache = new TreeSet<>();

        private CachedGenerator(JsonGenerator generator) {
            this.generator = generator;
        }

        /**
         * Write the reference to the given object, or the object itself if it is not cached.
         * Save the object in the cache.
         *
         * @param obj     the object to write.
         * @param creator the creator of the entity as JSON.
         * @throws IOException if the object cannot be written.
         */
        public void writeReferenceOrObject(IdentifiableEntity obj, CachedGeneratorCreator creator) throws IOException {
            if (obj == null) {
                this.generator.writeNull();
            } else if (this.cache.add(Long.valueOf(obj.getId()))) {
                creator.create();
            } else {
                writeObjectRef(this.generator, obj);
            }
        }

        /**
         * Write in a field the reference to the given object, or the object itself if it is not cached.
         * Save the object in the cache.
         *
         * @param fieldName the name of the field.
         * @param obj       the object to write.
         * @param creator   the creator of the entity as JSON.
         * @throws IOException if the object cannot be written.
         */
        public void writeReferenceOrObjectField(String fieldName, IdentifiableEntity obj, CachedGeneratorCreator creator) throws IOException {
            if (obj != null) {
                this.generator.writeFieldName(fieldName);
                writeReferenceOrObject(obj, creator);
            }
        }

        /**
         * Write a publication and its attributes. This function has a dedicated behavior for references to the
         * publication's receiver, e.g., the journals.
         *
         * @param generator         the JSON generator.
         * @param publication       the publication to write.
         * @param journalCreator    the creator of the journal entity as JSON.
         * @param conferenceCreator the creator of the conference entity as JSON.
         * @throws IOException if the reference cannot be written.
         */
        public void writePublicationAndAttributes(Publication publication, CachedGeneratorCreator1<Journal> journalCreator,
                                                  CachedGeneratorCreator1<Conference> conferenceCreator) throws IOException {
            if (publication != null) {
                this.generator.writeStartObject();
                publication.forEachAttribute((attrName, attrValue) -> {
                    writeField(this.generator, attrName, attrValue);
                });
                //
                if (publication instanceof JournalBasedPublication jpublication) {
                    final var journal = jpublication.getJournal();
                    writeReferenceOrObjectField("journal", journal, () -> journalCreator.create(journal)); //$NON-NLS-1$
                } else if (publication instanceof ConferenceBasedPublication cpublication) {
                    final var conference = cpublication.getConference();
                    writeReferenceOrObjectField("conference", conference, () -> conferenceCreator.create(conference)); //$NON-NLS-1$
                }
                //
                this.generator.writeEndObject();
            } else {
                this.generator.writeNull();
            }
        }

        /**
         * Creator for JSON.
         *
         * @author $Author: sgalland$
         * @version $Name$ $Revision$ $Date$
         * @mavengroupid $GroupId$
         * @mavenartifactid $ArtifactId$
         * @since 2.0.0
         */
        public interface CachedGeneratorCreator {

            /**
             * Create the JSON.
             *
             * @throws IOException if the object cannot be written.
             */
            void create() throws IOException;

        }

        /**
         * Creator for JSON.
         *
         * @param <T> type of object to serialize.
         * @author $Author: sgalland$
         * @version $Name$ $Revision$ $Date$
         * @mavengroupid $GroupId$
         * @mavenartifactid $ArtifactId$
         * @since 2.0.0
         */
        public interface CachedGeneratorCreator1<T> {

            /**
             * Create the JSON.
             *
             * @param obj object to serialize.
             * @throws IOException if the object cannot be written.
             */
            void create(T obj) throws IOException;

        }

    }

}
