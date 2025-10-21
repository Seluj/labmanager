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

package fr.utbm.ciad.labmanager.views.appviews.publications;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.PublicationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.PublicationCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.publications.imports.DefaultPublicationImportWizard;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Wizard for importing publications.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "importpublication", layout = MainLayout.class)
@PermitAll
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class PublicationImportWizard extends DefaultPublicationImportWizard implements HasDynamicTitle {

    private static final long serialVersionUID = 5587493252838946400L;

    /**
     * Constructor.
     *
     * @param loggerFactory      the factory of the loggers.
     * @param publicationService the service for accessing the JPA entities of publications.
     * @param editorFactory      the factory for the publication editors.
     * @param statusComputer     the computer of the publication status.
     * @param messages           the accessor to the localized messages.
     */
    public PublicationImportWizard(
            @Autowired PublicationService publicationService,
            @Autowired PublicationEditorFactory editorFactory,
            @Autowired PublicationCreationStatusComputer statusComputer,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory) {
        super(publicationService, editorFactory, statusComputer, messages, loggerFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.publication.import.title"); //$NON-NLS-1$
    }

}
