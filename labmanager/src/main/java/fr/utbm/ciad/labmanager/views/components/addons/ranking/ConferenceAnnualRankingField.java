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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;

/**
 * Vaadin component for input an annual ranking for a conference.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ConferenceAnnualRankingField extends AbstractAnnualRankingField<ConferenceQualityAnnualIndicators> {

    private static final long serialVersionUID = 3516999589059290212L;

    private final Column<DataItem<ConferenceQualityAnnualIndicators>> coreIndexColumn;

    /**
     * Constructor.
     */
    public ConferenceAnnualRankingField() {
        super();
        this.coreIndexColumn = getGrid().addColumn(new ComponentRenderer<>(this::getCoreIndexLabel))
                .setAutoWidth(true)
                .setEditorComponent(this::createCoreIndexEditor);
    }

    private static CoreRanking getCoreIndex(DataItem<ConferenceQualityAnnualIndicators> item) {
        return item.getIndicators().getCoreIndex();
    }

    private static void setCoreIndex(DataItem<ConferenceQualityAnnualIndicators> item, CoreRanking ranking) {
        item.getIndicators().setCoreIndex(ranking);
    }

    @Override
    protected ConferenceQualityAnnualIndicators createIndicatorInstance() {
        return new ConferenceQualityAnnualIndicators();
    }

    private ComboBox<CoreRanking> createCoreIndexEditor(DataItem<ConferenceQualityAnnualIndicators> item) {
        final var combo = createBaseCoreIndexEditor();
        final var binder = getGridEditor().getBinder();
        binder.forField(combo).bind(ConferenceAnnualRankingField::getCoreIndex, ConferenceAnnualRankingField::setCoreIndex);
        return combo;
    }

    private Span getCoreIndexLabel(DataItem<ConferenceQualityAnnualIndicators> item) {
        return getIndicatorLabel(item,
                it -> DataItem.getCoreIndexString(it.getIndicators().getCoreIndex()),
                it -> it.getPreviousItem(it0 -> it0.getCoreIndex() != null));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.coreIndexColumn.setHeader(getTranslation("views.rankings.coreIndex")); //$NON-NLS-1$
    }

}
