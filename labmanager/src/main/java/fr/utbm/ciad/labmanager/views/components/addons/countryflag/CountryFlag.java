/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2017 Martin Vysny
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

package fr.utbm.ciad.labmanager.views.components.addons.countryflag;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;

/**
 * A component which shows the Country Flag for given country. The Country is provided as a
 * ISO 3166-1-alpha-2 code which can be looked up here:
 * {@link "https://www.iso.org/obp/ui/#search/code/"}.
 *
 * <p>The flags themselves are taken from <a href="https://github.com/lipis/flag-icon-css>Flag Icon CSS</a> project.
 *
 * <p>This class is a re-implementation of the <a href="https://github.com/mvysny/country-flag">CountryFlag project</a>
 * by Martin Vysny.
 *
 * @author $Author: sgalland$
 * @author $Author: mvysny$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://github.com/mvysny/country-flag"
 */
@StyleSheet("themes/labmanager/addons/countryflag/css/flag-icon.css")
public class CountryFlag extends Span {

    private static final long serialVersionUID = -3594718757953697624L;

    private static final String PREFIX = "flag-icon-"; //$NON-NLS-1$

    private static final String STYLE = PREFIX + "background"; //$NON-NLS-1$

    private static final String SQUARED_STYLE = PREFIX + "squared"; //$NON-NLS-1$


    private boolean squared = false;

    private CountryCode country = CountryCode.getDefault();

    /**
     * Creates the component. The component will not initially show any flag.
     * The component is initially of size 120px x 90px.
     */
    public CountryFlag() {
        setWidth(120, Unit.PIXELS);
        setHeight(90, Unit.REM);
        initStyles();
    }

    /**
     * Creates the component, showing given flag.
     *
     * @param country see {@link #setCountry(CountryCode)} for details.
     */
    public CountryFlag(CountryCode country) {
        this();
        setCountry(country);
    }

    /**
     * Creates the component, showing given flag.
     *
     * @param country   see {@link #setCountry(CountryCode)} for details.
     * @param isSquared see {@link #setSquared(boolean)} for details.
     */
    public CountryFlag(CountryCode country, boolean isSquared) {
        this();
        setCountry(country);
        setSquared(isSquared);
    }

    /**
     * Change the width and height of the component according to the given height
     * and preserving the aspect ratio.
     *
     * @param height the height value.
     * @param unit   the height unit.
     */
    public void setSizeFromHeight(float height, Unit unit) {
        setHeight(height, unit);
        if (this.squared) {
            setWidth(height, unit);
        } else {
            setWidth((120f * height) / 90f, unit);
        }
    }

    /**
     * Checks whether the flag is drawn using width-to-height ratio of 1:1 ({@code true}, squared)
     * or 4:3 ({@code false}, the default).
     *
     * @return {@code true} if squared, defaults to {@code false}.
     */
    public boolean isSquared() {
        return hasClassName(SQUARED_STYLE);
    }

    /**
     * Configures whether the flag is drawn using width-to-height ratio of 1:1 ({@code true}, squared)
     * or 4:3 ({@code false}, the default).
     *
     * @param squared {@code true} if squared, defaults to {@code false}.
     */
    public void setSquared(boolean squared) {
        this.squared = squared;
        updateStyles(this.country);
    }

    /**
     * Returns the country.
     *
     * @return country.
     */
    public CountryCode getCountry() {
        return this.country;
    }

    /**
     * Sets the country.
     *
     * @param country the country code.
     */
    public void setCountry(CountryCode country) {
        final var old = this.country;
        this.country = country == null ? CountryCode.getDefault() : country;
        updateStyles(old);
    }

    private void initStyles() {
        addClassName(STYLE);
        addClassName(PREFIX + this.country.getCode());
    }

    private void updateStyles(CountryCode old) {
        if (old != this.country) {
            removeClassName(PREFIX + old.getCode());
            addClassName(PREFIX + this.country.getCode());
        }
        if (this.squared) {
            addClassName(SQUARED_STYLE);
        } else {
            removeClassName(SQUARED_STYLE);
        }
    }

}
