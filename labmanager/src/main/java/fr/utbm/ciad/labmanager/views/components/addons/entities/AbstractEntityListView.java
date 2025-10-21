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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Abstract implementation of a list of entities based on a grid view.
 *
 * @param <T> the type of the entities, that must be {@link IdentifiableEntity} to be able to provide their id.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see #AbstractEntityTreeListView
 * @since 4.0
 */
public abstract class AbstractEntityListView<T extends IdentifiableEntity> extends AbstractGridBaseEntityListView<T, T, Grid<T>> {

    private static final long serialVersionUID = 7445326290206390705L;

    /**
     * Constructor.
     *
     * @param entityType                the type of the entities to be listed.
     * @param authenticatedUser         the connected user.
     * @param messages                  the accessor to the localized messages (spring layer).
     * @param loggerFactory             the factory to be used for the composite logger.
     * @param deletionTitleMessageKey   the key in the localized messages for the dialog box title related to a deletion.
     * @param deletionMessageKey        the key in the localized messages for the message related to a deletion.
     * @param deletionSuccessMessageKey the key in the localized messages for the messages related to a deletion success.
     * @param deletionErrorMessageKey   the key in the localized messages for the messages related to an error of deletion.
     * @param properties                specification of properties that may be passed to the construction function {@link #createFilters()},
     *                                  {@link #createGrid()} and {@link #createMenuBar()} and {@link #createMobileFilters()}.
     * @since 4.0
     */
    public AbstractEntityListView(Class<T> entityType, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory, ConstructionPropertiesBuilder properties) {
        super(entityType, authenticatedUser, messages, loggerFactory, properties);
    }

    @Override
    protected Grid<T> createGridInstance() {
        return new Grid<>(getEntityType(), false);
    }

    @Override
    protected final void initializeDataInGrid(Grid<T> grid, AbstractFilters<T> filters) {
        final var dataView = grid.setItems(getFetchCallback(filters));
        dataView.setItemCountEstimate(ViewConstants.GRID_PAGE_SIZE * 10);
        dataView.setItemCountEstimateIncrease(ViewConstants.GRID_PAGE_SIZE);
    }

    /**
     * Replies the callback for fetching the data from the database.
     *
     * @param filters the filtering object to provide to the JPA service.
     * @return the callback
     */
    protected abstract FetchCallback<T, Void> getFetchCallback(AbstractFilters<T> filters);

}
