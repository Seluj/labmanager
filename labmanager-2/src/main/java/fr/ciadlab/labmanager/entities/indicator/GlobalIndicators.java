/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.entities.indicator;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.ciadlab.labmanager.entities.AttributeProvider;
import fr.ciadlab.labmanager.entities.EntityUtils;
import fr.ciadlab.labmanager.entities.IdentifiableEntity;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.indicators.Indicator;
import fr.ciadlab.labmanager.io.json.JsonUtils;
import fr.ciadlab.labmanager.utils.HashCodeUtils;

/** Table for storing hte global indicators to show up.
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

	/** Identifier of the journal in the database.
	 * 
	 * <p>Using this instead of {@link GenerationType#IDENTITY} allows for JOINED or TABLE_PER_CLASS inheritance types to work.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private int id;

	/** Date of the last update of the global indicators. 
	 */
	@Column
	private LocalDate lastUpdate;

	/** String representation of the indicator values. 
	 */
	@Column(length =  EntityUtils.LARGE_TEXT_SIZE)
	@Lob
	private String values;

	@Transient
	private Map<String, Number> valueCache;

	/** List of visible indicators. This list contains the indicators' keys. Order of keys is important. 
	 */
	@ElementCollection
	private List<String> visibleIndicators;

	/** Construct the global indicators.
	 */
	public GlobalIndicators() {
		//
	}

	@Override
	public int hashCode() {
		int h = HashCodeUtils.start();
		h = HashCodeUtils.add(h, this.id);
		h = HashCodeUtils.add(h, this.values);
		h = HashCodeUtils.add(h, this.visibleIndicators);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final GlobalIndicators other = (GlobalIndicators) obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.values, other.values)) {
			return false;
		}
		if (!Objects.equals(this.visibleIndicators, other.visibleIndicators)) {
			return false;
		}
		return true;
	}

	@Override
	public void forEachAttribute(AttributeConsumer consumer) throws IOException {
		if (getId() != 0) {
			consumer.accept("id", Integer.valueOf(getId())); //$NON-NLS-1$
		}
		if (getLastUpdate() != null) {
			consumer.accept("lastUpdate", getLastUpdate()); //$NON-NLS-1$
		}
		if (getValues() != null) {
			consumer.accept("values", getValues()); //$NON-NLS-1$
		}
		if (!getVisibleIndicators().isEmpty()) {
			consumer.accept("visibleIndicatorKeys", getVisibleIndicators()); //$NON-NLS-1$
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
	public int getId() {
		return this.id;
	}

	/** Change the identifier of the journal in the database.
	 *
	 * @param id the identifier.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Replies the date of the last update of the indicators.
	 *
	 * @return the date or {@code null} if it is unknown.
	 */
	public LocalDate getLastUpdate() {
		return this.lastUpdate;
	}

	/** Change the date of the last update of the indicators.
	 *
	 * @param date the date or {@code null} if it is unknown.
	 */
	public void setLastUpdate(LocalDate date) {
		this.lastUpdate = date;
	}

	/** Change the date of the last update of the indicators.
	 *
	 * @param date the date in format {@code YYYY-MM-DD} or {@code null} if it is unknown.
	 */
	public void setLastUpdate(String date) {
		if (Strings.isNullOrEmpty(date)) {
			setLastUpdate((LocalDate) null);
		} else {
			setLastUpdate(LocalDate.parse(date));
		}
	}

	/** Replies the JSON representation of the indicator values.
	 *
	 * @return the JSON of the values.
	 */
	public String getValues() {
		return this.values;
	}

	/** Change the JSON representation of the indicator values.
	 *
	 * @param values the JSON of the indicator values
	 */
	public void setValues(String values) {
		this.values = Strings.emptyToNull(values);
		this.valueCache = null;
	}

	/** Change the indicator values from the list of indicators.
	 *
	 * @param organization the organization to consider for updating the global indicators.
	 * @param allIndicators the indicators to use for setting the values.
	 */
	public void setValues(ResearchOrganization organization, List<? extends Indicator> allIndicators) {
		final Map<String, Number> cache = new TreeMap<>();
		for (final Indicator indicator : allIndicators) {
			cache.put(indicator.getKey(), indicator.getNumericValue(organization));
		}
		try {
			final ObjectMapper mapper = new ObjectMapper();
			this.values = mapper.writeValueAsString(cache);
			this.valueCache = cache;
			this.lastUpdate = LocalDate.now();
		} catch (Throwable ex) {
			//
		}
	}

	/** Replies the global indicators.
	 *
	 * @return the indicator values
	 */
	public Map<String, Number> getIndicators() {
		return this.valueCache;
	}

	/** Build the cache of the values from the database values.
	 */
	public void updateCache() {
		this.valueCache = null;
		final String values = getValues();
		if (!Strings.isNullOrEmpty(values)) {
			try (final StringReader sr = new StringReader(values)) {
				final ObjectMapper mapper = new ObjectMapper();
				final ObjectReader or = mapper.readerForMapOf(Number.class);
				final Map<String, Number> cache = or.readValue(sr);
				this.valueCache = cache;
			} catch (Throwable ex) {
				//
			}
		}
	}

	/** Replies the visible indicators.
	 *
	 * @return the list of the keys of the visible indicators.
	 */
	public List<String> getVisibleIndicators() {
		if (this.visibleIndicators == null) {
			this.visibleIndicators = new ArrayList<>();
		}
		return this.visibleIndicators;
	}

	/** Change the keys of the visible indicators.
	 *
	 * @param visibleIndicators the list of the keys of the visible indicators.
	 */
	public void setVisibleIndicators(List<String> visibleIndicators) {
		if (this.visibleIndicators == null) {
			this.visibleIndicators = new ArrayList<>();
		} else {
			this.visibleIndicators.clear();
		}
		if (visibleIndicators != null && !visibleIndicators.isEmpty()) {
			this.visibleIndicators.addAll(visibleIndicators);
		}
	}

}
