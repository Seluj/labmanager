/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
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

package fr.utbm.ciad.labmanager.views.components.addons.uploads;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;

import java.util.List;

/**
 * Extension of a component that is clickable in order to start a task for uploading a file and use its content as input JPA entities.
 * This extension generates a dialog box for uploading files (see {@link UploadExtension}) and a second dialog box showing the uploaded
 * data in a grid in order to be validated by the user.
 *
 * @param <T> the type of the result computed by the task.
 * @param <C> the type of the extended component that must be a valid Vaadin component and a received of click event.
 * @author $Author: pschneiderlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ValidationUploadExtension<T extends IdentifiableEntity, C extends Component & ClickNotifier<C>> extends UploadExtension<T, C> {

    private static final long serialVersionUID = -5457774044304967504L;

    private Dialog validationDialog;

    private SerializableBiConsumer<Grid<AbstractEntityEditor<T>>, List<T>> gridConfigurer;

    /**
     * Constructor.
     *
     * @param component the component to extend.
     */
    protected ValidationUploadExtension(C component) {
        super(component);
    }

    /**
     * Extends the given component to enable upload of data from a client-side file.
     *
     * @param <T>       the type of the result computed by the asynchronous task.
     * @param <C>       the type of the component that must be a valid Vaadin component and a received of click event.
     * @param component the component to extend.
     * @return the extension.
     */
    public static <T extends IdentifiableEntity, C extends Component & ClickNotifier<C>> ValidationUploadExtension<T, C> extendWithValidation(C component) {
        return new ValidationUploadExtension<>(component);
    }

    /**
     * Extends the given component to enable upload of data from a client-side file.
     *
     * @param <T>       the type of the result computed by the asynchronous task.
     * @param <C>       the type of the component that must be a valid Vaadin component and a received of click event.
     * @param component the component to extend.
     * @param type      the type of the result computed by the asynchronous task.
     * @return the extension.
     */
    public static <T extends IdentifiableEntity, C extends Component & ClickNotifier<C>> ValidationUploadExtension<T, C> extendWithValidation(C component, Class<T> type) {
        return new ValidationUploadExtension<>(component);
    }

    /**
     * Change the configurer of grid that are show in the validation dialog.
     *
     * @param configurer the grid configurer.
     * @return {@code this}.
     */
    public ValidationUploadExtension<T, C> withValidationGridConfigurer(SerializableBiConsumer<Grid<AbstractEntityEditor<T>>, List<T>> configurer) {
        this.gridConfigurer = configurer;
        return this;
    }

    @Override
    protected void stopUploadTasks() {
        super.stopUploadTasks();
        if (this.validationDialog != null) {
            closeSafe(this.validationDialog);
            this.validationDialog = null;
        }
    }

    @Override
    protected void onDataUploaded(List<T> data) {
        try {
            this.validationDialog = new Dialog();
            ComponentFactory.configureModalDialog(this.validationDialog, getTitle(), false);

            final var grid = new Grid<AbstractEntityEditor<T>>();

            final var closeButton = new Button(ComponentFactory.getTranslation("views.import.dialog.close"), e -> cancel()); //$NON-NLS-1$
            final var saveAllButton = new Button(ComponentFactory.getTranslation("views.import.dialog.save"), e -> { //$NON-NLS-1$
                try {
                    grid.getListDataView().getItems().forEach(editor -> {
                        try {
                            if (!editor.getEditedEntity().isFakeEntity()) {
                                editor.save();
                            }
                        } catch (Exception ex) {
                            fireError(ex);
                        }
                    });
                    success(data);
                } catch (Throwable ex) {
                    error(ex);
                }
            });

            this.validationDialog.add(grid);
            this.validationDialog.getFooter().add(closeButton, saveAllButton);

            if (this.gridConfigurer != null) {
                //this.gridConfigurer.accept(grid, data);
            }

            // Open the dialog
            this.validationDialog.open();
        } catch (Throwable ex) {
            error(ex);
        }
    }

}
