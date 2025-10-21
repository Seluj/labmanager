/*
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

package fr.utbm.ciad.labmanager.utils.builders;

import java.io.Serializable;
import java.util.Objects;

/**
 * Builder for tool for constructing the view.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class ConstructionPropertiesBuilder implements Serializable {

    private static final long serialVersionUID = 3427758339522693365L;

    private ConstructionProperties properties = new ConstructionPropertiesImpl0();

    private ConstructionPropertiesBuilder() {
        //
    }

    /**
     * Create a builder.
     *
     * @return the builder.
     */
    public static ConstructionPropertiesBuilder create() {
        return new ConstructionPropertiesBuilder();
    }

    /**
     * Build the properties.
     *
     * @return the properties.
     */
    public ConstructionProperties build() {
        return this.properties;
    }

    /**
     * Add the property with the given name and value.
     *
     * @param name  the name of the property.
     * @param value the value.
     * @return {@code this}.
     */
    public ConstructionPropertiesBuilder map(String name, Object value) {
        this.properties = new ConstructionPropertiesImpl1(name, value, this.properties);
        return this;
    }

    /**
     * Add the property with the given name and a {@code null} value.
     *
     * @param name the name of the property.
     * @return {@code this}.
     */
    public ConstructionPropertiesBuilder mapToNull(String name) {
        this.properties = new ConstructionPropertiesImpl1(name, null, this.properties);
        return this;
    }

    private static class ConstructionPropertiesImpl0 implements ConstructionProperties {

        private static final long serialVersionUID = 1760663354339672305L;

        @Override
        public <PT> PT get(String name) {
            throw new IllegalArgumentException();
        }

    }

    private record ConstructionPropertiesImpl1(String name, Object value,
                                               ConstructionProperties parent) implements ConstructionProperties {

            private static final long serialVersionUID = -4418631141943849767L;

        @SuppressWarnings("unchecked")
            @Override
            public <PT> PT get(String name) {
                if (Objects.equals(this.name, name)) {
                    return (PT) this.value;
                }
                return this.parent.get(name);
            }

        }

}
