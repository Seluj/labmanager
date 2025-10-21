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

package fr.utbm.ciad.labmanager.views.components.addons.ranking;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.FloatRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import fr.utbm.ciad.labmanager.views.components.addons.converters.DoubleToFloatWithPrecisionConverter;

/**
 * Vaadin component for input an annual ranking for a journal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class JournalAnnualRankingField extends AbstractAnnualRankingField<JournalQualityAnnualIndicators> {

    private static final long serialVersionUID = 8132932655736551687L;

    private final Column<DataItem<JournalQualityAnnualIndicators>> scimagoColumn;

    private final Column<DataItem<JournalQualityAnnualIndicators>> wosColumn;

    private final Column<DataItem<JournalQualityAnnualIndicators>> impactFactorColumn;

    /**
     * Constructor.
     */
    public JournalAnnualRankingField() {
        super();
        this.scimagoColumn = getGrid().addColumn(new ComponentRenderer<>(this::getScimagoQuartileLabel))
                .setAutoWidth(true)
                .setEditorComponent(this::createScimagoQuartileEditor);

        this.wosColumn = getGrid().addColumn(new ComponentRenderer<>(this::getWosQuartileLabel))
                .setAutoWidth(true)
                .setEditorComponent(this::createWosQuartileEditor);

        this.impactFactorColumn = getGrid().addColumn(new ComponentRenderer<>(this::getImpactFactorLabel))
                .setAutoWidth(true)
                .setEditorComponent(this::createImpactFactorEditor);
    }

    private static QuartileRanking getScimagoQuartile(DataItem<JournalQualityAnnualIndicators> item) {
        return item.getIndicators().getScimagoQIndex();
    }

    private static void setScimagoQuartile(DataItem<JournalQualityAnnualIndicators> item, QuartileRanking ranking) {
        item.getIndicators().setScimagoQIndex(ranking);
    }

    private static QuartileRanking getWosQuartile(DataItem<JournalQualityAnnualIndicators> item) {
        return item.getIndicators().getWosQIndex();
    }

    private static void setWosQuartile(DataItem<JournalQualityAnnualIndicators> item, QuartileRanking ranking) {
        item.getIndicators().setWosQIndex(ranking);
    }

    private static Float getImpactFactor(DataItem<JournalQualityAnnualIndicators> item) {
        return Float.valueOf(item.getIndicators().getImpactFactor());
    }

    private static void setImpactFactor(DataItem<JournalQualityAnnualIndicators> item, Float impactFactor) {
        item.getIndicators().setImpactFactor(impactFactor);
    }

    @Override
    protected JournalQualityAnnualIndicators createIndicatorInstance() {
        return new JournalQualityAnnualIndicators();
    }

    private ComboBox<QuartileRanking> createScimagoQuartileEditor(DataItem<JournalQualityAnnualIndicators> item) {
        final var combo = createBaseQuartileEditor();
        final var binder = getGridEditor().getBinder();
        binder.forField(combo).bind(JournalAnnualRankingField::getScimagoQuartile, JournalAnnualRankingField::setScimagoQuartile);
        return combo;
    }

    private Span getScimagoQuartileLabel(DataItem<JournalQualityAnnualIndicators> item) {
        return getIndicatorLabel(item,
                it -> DataItem.getQIndexString(it.getIndicators().getScimagoQIndex()),
                it -> it.getPreviousItem(it0 -> it0.getScimagoQIndex() != null));
    }

    private ComboBox<QuartileRanking> createWosQuartileEditor(DataItem<JournalQualityAnnualIndicators> item) {
        final var combo = createBaseQuartileEditor();
        final var binder = getGridEditor().getBinder();
        binder.forField(combo).bind(JournalAnnualRankingField::getWosQuartile, JournalAnnualRankingField::setWosQuartile);
        return combo;
    }

    private Span getWosQuartileLabel(DataItem<JournalQualityAnnualIndicators> item) {
        return getIndicatorLabel(item,
                it -> DataItem.getQIndexString(it.getIndicators().getWosQIndex()),
                it -> it.getPreviousItem(it0 -> it0.getWosQIndex() != null));
    }

    private NumberField createImpactFactorEditor(DataItem<JournalQualityAnnualIndicators> item) {
        final var field = createBaseFloatEditor();
        final var binder = getGridEditor().getBinder();
        binder.forField(field)
                .withConverter(new DoubleToFloatWithPrecisionConverter(2))
                .withValidator(new FloatRangeValidator(getTranslation("views.rankings.impactFactor.error"), Float.valueOf(0f), null)) //$NON-NLS-1$
                .bind(JournalAnnualRankingField::getImpactFactor, JournalAnnualRankingField::setImpactFactor);
        return field;
    }

    private Span getImpactFactorLabel(DataItem<JournalQualityAnnualIndicators> item) {
        return getIndicatorLabel(item,
                it -> DataItem.getImpactFactorString(it.getIndicators().getImpactFactor()),
                it -> it.getPreviousItem(it0 -> it0.getImpactFactor() > 0f));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.scimagoColumn.setHeader(getTranslation("views.rankings.scimagoQIndex")); //$NON-NLS-1$
        this.wosColumn.setHeader(getTranslation("views.rankings.wosQIndex")); //$NON-NLS-1$
        this.impactFactorColumn.setHeader(getTranslation("views.rankings.impactFactor")); //$NON-NLS-1$
    }

}
