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

package fr.utbm.ciad.labmanager.data.indicator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Table for storing the global indicators to show up.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Entity
@Table(name = "GlobalIndicators")
public class GlobalIndicators implements Serializable, JsonSerializable, AttributeProvider, IdentifiableEntity {

    private static final long serialVersionUID = -1607404317550705953L;

    /**
     * Identifier of the journal in the database.
     *
     * <p>Using this instead of {@link GenerationType#IDENTITY} allows for JOINED or TABLE_PER_CLASS inheritance types to work.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    @Lob
    private String visibleIndicatorKeys;

    @Column
    private LocalDate cacheDate;

    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String cache;

    @Transient
    private Map<String, Number> cacheBuffer;

    /**
     * List of visible indicators. This list contains the indicators' keys. Order of keys is important.
     * Because the DB backend does not ensure the order of a ElementCollection of type list
     * (for example Derby ensure the order of the list elements, byt MySQL does not), it is
     * necessary to implement the order of the indicators in the JPA itself.
     * <p>
     * The list of visible indicators is stored as a string in order to be sure that the order
     * of the indicators is preserved.
     *
     * @see #visibleIndicatorKeys
     */
    @Transient
    private List<String> visibleIndicatorList;

    /**
     * Construct the global indicators.
     */
    public GlobalIndicators() {
        //
    }

    @Override
    public int hashCode() {
        return 1231;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return getClass() == obj.getClass();
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getId() != 0) {
            consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
        }
        if (!getVisibleIndicatorKeyList().isEmpty()) {
            consumer.accept("visibleIndicatorKeys", getVisibleIndicatorKeyList()); //$NON-NLS-1$
        }
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        //
        generator.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        serialize(generator, serializers);
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the identifier of the journal in the database.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the keys of the visible indicators into a string of characters.
     * Each key is separated by coma.
     *
     * @return the coma-separated list of the keys of the visible indicators.
     */
    public String getVisibleIndicatorKeys() {
        return this.visibleIndicatorKeys;
    }

    /**
     * Change the keys of the visible indicators into a string of characters.
     * Each key is separated by coma.
     *
     * @param keys the coma-separated list of the keys of the visible indicators.
     */
    public void setVisibleIndicatorKeys(String keys) {
        this.visibleIndicatorKeys = Strings.emptyToNull(keys);
        this.visibleIndicatorList = null;
    }

    /**
     * Replies the list of the identifiers of visible indicators.
     *
     * @return the list of the keys of the visible indicators.
     * @since 3.2
     */
    public List<String> getVisibleIndicatorKeyList() {
        if (this.visibleIndicatorList == null) {
            this.visibleIndicatorList = new ArrayList<>();
            final var str = getVisibleIndicatorKeys();
            if (!Strings.isNullOrEmpty(str)) {
                this.visibleIndicatorList.addAll(Arrays.asList(str.split("\\s*[,;:/]+\\s*"))); //$NON-NLS-1$
            }
        }
        return this.visibleIndicatorList;
    }

    /**
     * Replies the max age of the cached values.
     *
     * @return the max age of the currently cached values.
     * @since 3.2
     */
    public int getCacheAge() {
        if (this.cacheDate == null) {
            return Integer.MAX_VALUE;
        }
        return (int) ChronoUnit.DAYS.between(this.cacheDate, LocalDate.now());
    }

    /**
     * Reset the value cache and replies an empty map.
     *
     * @since 3.2
     */
    public void resetCachedValues() {
        this.cacheDate = null;
        this.cache = null;
        this.cacheBuffer = null;
    }

    /**
     * Replies the cached values in the context of the provided organization.
     *
     * @return the cache content.
     * @since 3.2
     */
    public Map<String, Number> getCachedValues() {
        ensureCacheBuffer();
        return Collections.unmodifiableMap(this.cacheBuffer);
    }

    /**
     * Force the value of the cache in the context of the provided organization.
     *
     * @param key   the name of the indicator to be set.
     * @param value the new value for the indicator.
     * @since 3.2
     */
    public void setCachedValues(String key, Number value) {
        ensureCacheBuffer();
        this.cacheBuffer.put(key, value);
        if (this.cacheDate == null) {
            this.cacheDate = LocalDate.now();
        }
        saveCacheBuffer();
    }

    @SuppressWarnings("unchecked")
    private void ensureCacheBuffer() {
        if (this.cacheBuffer == null) {
            final var mapper = JsonUtils.createMapper();
            if (!Strings.isNullOrEmpty(this.cache)) {
                try {
                    this.cacheBuffer = mapper.readValue(this.cache, Map.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (this.cacheBuffer == null) {
            this.cacheBuffer = new HashMap<>();
        }
    }

    private void saveCacheBuffer() {
        if (this.cacheBuffer == null) {
            this.cache = null;
        } else {
            final var mapper = JsonUtils.createMapper();
            try {
                this.cache = mapper.writeValueAsString(this.cacheBuffer);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

}
