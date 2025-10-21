package fr.utbm.ciad.labmanager.views.components.addons.entities;

import fr.utbm.ciad.labmanager.views.components.conferences.editors.regular.ConferenceCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.journals.editors.regular.JournalCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.regular.OrganizationCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.PersonCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.PublicationCreationStatusComputer;

import static fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory.getTranslation;

/**
 * Enum that represents the different types of similarity errors that can be found when importing data.
 * Each type of error has a message that will be displayed to the user.
 * The error can be a warning or an error, depending on the type of error.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public enum EntityCreationStatus {

    /**
     * No error found.
     */
    NO_ERROR {
        @Override
        public String getErrorMessage() {
            return ""; //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Error when the title of the entity is the same as another entity.
     */
    SAME_TITLE {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_title"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Error when the title and the publishing vector are the same as another entity.
     *
     * @see PublicationCreationStatusComputer
     */
    SAME_TITLE_AND_PUBLISHING_VECTOR {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_title_and_publishing_vector"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Error when the title are the same as another entity but in different publishing vectors.
     *
     * @see PublicationCreationStatusComputer
     */
    SAME_TITLE_AND_DIFFERENT_PUBLISHING_VECTORS {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_title_and_different_publishing_vectors"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isWarning() {
            return true;
        }
    },

    /**
     * Error when the title of the entity are the same as another entity but the authors are different.
     *
     * @see PublicationCreationStatusComputer
     */
    SAME_TITLE_AND_PUBLISHING_VECTOR_BUT_DIFFERENT_AUTHORS {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_title_vector_different_authors"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Warning when the abstract text is missed.
     *
     * @see PublicationCreationStatusComputer
     */
    MISSED_ABSTRACT_TEXT {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.missed_abstract_text"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public boolean isWarning() {
            return true;
        }
    },

    /**
     * Error when the title and the publisher of the entity are the same as another entity.
     *
     * @see JournalCreationStatusComputer
     */
    SAME_TITLE_AND_PUBLISHER {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_title_and_publisher"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Error when the title of the entity are the same as another entity but the publisher are different.
     *
     * @see PersonCreationStatusComputer
     */
    SAME_NAME {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_name"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    },

    /**
     * Error when the title and the acronym of the entity are the same as another entity.
     *
     * @see ConferenceCreationStatusComputer
     * @see OrganizationCreationStatusComputer
     */
    SAME_NAME_AND_ACRONYM {
        @Override
        public String getErrorMessage() {
            return getTranslation("views.import.error.same_name_and_acronym"); //$NON-NLS-1$
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isWarning() {
            return false;
        }
    };

    /**
     * Get the message of the error.
     *
     * @return the message of the error
     */
    public abstract String getErrorMessage();

    /**
     * Check if the error is an error.
     *
     * @return true if the error is an error, false otherwise.
     */
    public abstract boolean isError();

    /**
     * Check if the error is a warning.
     *
     * @return true if the error is a warning, false otherwise.
     */
    public abstract boolean isWarning();

}
