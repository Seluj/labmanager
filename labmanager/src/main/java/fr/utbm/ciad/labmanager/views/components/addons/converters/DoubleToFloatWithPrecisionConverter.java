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

package fr.utbm.ciad.labmanager.views.components.addons.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A converter from double to float with a given number of digit in the decimal part.
 * The values are rounded in order to have the given number of digits in the decimal part.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DoubleToFloatWithPrecisionConverter implements Converter<Double, Float> {

    private static final long serialVersionUID = 7328283571043899334L;
    private final int precision;

    /**
     * Constructor.
     *
     * @param precision the number of decimal digits to be included in the float value.
     */
    public DoubleToFloatWithPrecisionConverter(int precision) {
        assert precision >= 0;
        this.precision = precision;
    }

    @Override
    public Result<Float> convertToModel(Double value, ValueContext context) {
        if (value == null) {
            return Result.ok(Float.valueOf(0f));
        }
        var decimal = new BigDecimal(value.toString());
        decimal = decimal.setScale(this.precision, RoundingMode.HALF_UP);
        final var fvalue = decimal.floatValue();
        return Result.ok(Float.valueOf(fvalue));
    }

    @Override
    public Double convertToPresentation(Float value, ValueContext context) {
        if (value == null) {
            return Double.valueOf(0.);
        }
        var decimal = new BigDecimal(value.toString());
        decimal = decimal.setScale(this.precision, RoundingMode.HALF_UP);
        final var dvalue = decimal.doubleValue();
        return Double.valueOf(dvalue);
    }

}
