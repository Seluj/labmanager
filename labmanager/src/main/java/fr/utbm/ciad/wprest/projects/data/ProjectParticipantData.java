package fr.utbm.ciad.wprest.projects.data;

import fr.utbm.ciad.labmanager.data.project.Role;
import fr.utbm.ciad.wprest.data.PersonOnWebsite;

/**
 * Describes a participant of a project
 *
 * @param person - the person
 * @param role   - the role of the person in the project
 */
public record ProjectParticipantData(PersonOnWebsite person,
                                     Role role) {
}
