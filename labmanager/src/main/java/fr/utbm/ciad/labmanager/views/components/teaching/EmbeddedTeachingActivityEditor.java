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

package fr.utbm.ciad.labmanager.views.components.teaching;

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/** Editor of teaching activity information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited teaching activity.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedTeachingActivityEditor extends AbstractTeachingActivityEditor {
	
	private static final long serialVersionUID = -6189368227679492365L;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedTeachingActivityEditor.class);

	/** Constructor.
	 *
	 * @param context the editing context for the teaching activity.
	 * @param fileManager the manager of the downloadable files.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 */
	public EmbeddedTeachingActivityEditor(EntityEditingContext<TeachingActivity> context,
			DownloadableFileManager fileManager, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages) {
		super(context, fileManager, authenticatedUser, messages, LOGGER);
		createEditorContentAndLinkBeans();
	}

}
