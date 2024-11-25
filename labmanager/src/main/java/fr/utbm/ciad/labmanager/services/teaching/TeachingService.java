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

package fr.utbm.ciad.labmanager.services.teaching;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.teaching.PedagogicalPracticeType;
import fr.utbm.ciad.labmanager.data.teaching.StudentType;
import fr.utbm.ciad.labmanager.data.teaching.TeacherRole;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityLevel;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityRepository;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityType;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Service for the teaching activities.
 * 
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
@Service
public class TeachingService extends AbstractEntityService<TeachingActivity> {

	private static final long serialVersionUID = -7947329525523816248L;

	private TeachingActivityRepository teachingActivityRepository;

	private DownloadableFileManager fileManager;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param teachingActivityRepository the repository for the teaching activities.
	 * @param fileManager the manager of the uploaded and downloadable files.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public TeachingService(
			@Autowired TeachingActivityRepository teachingActivityRepository,
			@Autowired DownloadableFileManager fileManager,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.teachingActivityRepository = teachingActivityRepository;
		this.fileManager = fileManager;
	}

	/** Replies the teaching activities for the person.
	 *
	 * @param person the identifier of the person.
	 * @return the list of teaching activities.
	 */
	public List<TeachingActivity> getActivitiesByPersonId(Person person) {
		return this.teachingActivityRepository.findDistinctByPerson(person);
	}

	/** Replies all the known teaching activities.
	 *
	 * @return all the activities, never {@code null}.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<TeachingActivity> getAllActivities() {
		return this.teachingActivityRepository.findAll();
	}

	/** Replies all the known teaching activities.
	 *
	 * @param pageable a manager of pages.
	 * @param filter the filter to apply to the activities.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return all the activities, never {@code null}.
	 * @since 4.0
	 */
	@Transactional
	public Page<TeachingActivity> getAllActivities(Pageable pageable, Specification<TeachingActivity> filter, Consumer<TeachingActivity> callback) {
		final var page = this.teachingActivityRepository.findAll(filter, pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the teaching activity that has the given identifier.
	 *
	 * @param id the identifier.
	 * @return the activity or {@code null} if the activity cannot be found.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public TeachingActivity getActivityById(long id) {
		final var opt = this.teachingActivityRepository.findById(Long.valueOf(id));
		if (opt.isPresent()) {
			return opt.get();
		}
		return null;
	}

	/** Delete the teaching activity with the given identifier.
	 *
	 * @param id the identifier.
	 * @param removeAssociatedFiles indicates if the uploaded files that are associated to the activity should be deleted also.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Transactional
	public void removeTeachingActivity(long id, boolean removeAssociatedFiles) {
		final var identifier = Long.valueOf(id);
		final var activityOpt = this.teachingActivityRepository.findById(identifier);
		if (activityOpt.isPresent()) {
			final var activity = activityOpt.get();
			//
			final var pathToSlides = activity.getPathToSlides();
			//
			activity.setPerson(null);
			activity.setUniversity(null);
			activity.setAnnualWorkPerType(null);
			activity.setPathToSlides(null);
			this.teachingActivityRepository.deleteById(identifier);
			if (removeAssociatedFiles) {
				try {
					if (!Strings.isNullOrEmpty(pathToSlides)) {
						this.fileManager.deleteTeachingActivitySlides(id, LoggerFactory.getLogger(getClass()));
					}
				} catch (Throwable ex) {
					// Silent
				}
			}
		}
	}

	/** Create a teaching activity with the given attributes.
	 *
	 * @param person the person that is associated to the teaching activity.
	 * @param code the code or acronym of the teaching activity.
	 * @param title the title of the teaching activity.
	 * @param level the level of teaching.
	 * @param studentType the type of the targeted students.
	 * @param language the language of the activity. If it not provided, the default value is applied.
	 * @param degree the name of the degree.
	 * @param university the university in which the teaching activity is realized.
	 * @param startDate the start date of the activity.
	 * @param endDate the end date of the activity, or {@code null} if it is still active.
	 * @param role the role of the person in the activity.
	 * @param differentHetdForTdTp indicates if the person has different factors for her/his hours of tutorials
	 *      and practice works for computing the number of hETD.
	 * @param numberOfStudents is an estimation of the number of students per year in the activity.
	 * @param explanation is a text that describes the activity.
	 * @param pedagogicalPracticeTypes the types of pedagogical practices that are applied in the teaching activity.
	 * @param activityUrl is a link to an external website that is dedicated to the activity. 
	 * @param sourceUrl is a link to an external website that provides source code for the activity.
	 * @param annualWorkPerType maps the types of teaching activity to the annual number of hours.
	 * @param pathToSlides are the uploaded slides, or {@code null} if no upload. If there is no uploaded,
	 *     any previously uploaded slides are kept.
	 * @param removePathToSlides indicates if previously uploaded slides must be removed.
	 * @return the created activity, or nothing if the activity cannot be created.
	 * @throws Exception if the slides cannot be uploaded.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Optional<TeachingActivity> createTeachingActivity(Person person, String code, String title,
			TeachingActivityLevel level, StudentType studentType,
			CountryCode language, String degree, ResearchOrganization university,
			LocalDate startDate, LocalDate endDate, TeacherRole role, boolean differentHetdForTdTp,
			int numberOfStudents, String explanation, Set<PedagogicalPracticeType> pedagogicalPracticeTypes,
			URL activityUrl, URL sourceUrl, Map<TeachingActivityType, Float> annualWorkPerType,
			MultipartFile pathToSlides, boolean removePathToSlides) throws Exception {
		final var activity = new TeachingActivity();
		try {
			updateTeachingActivity(activity,
					person, code, title, level, studentType, language, degree, university, startDate, endDate,
					role, differentHetdForTdTp, numberOfStudents, explanation, pedagogicalPracticeTypes,
					activityUrl, sourceUrl, annualWorkPerType, pathToSlides, removePathToSlides);
		} catch (Throwable ex) {
			// Delete created activity
			if (activity.getId() != 0) {
				try {
					removeTeachingActivity(activity.getId(), true);
				} catch (Throwable ex0) {
					// Silent
				}
			}
			//getLogger().error(ex.getLocalizedMessage(), ex);
			throw ex;
		}
		return Optional.of(activity);
	}

	/** Update an existing teaching activity with the given attributes.
	 *
	 * @param activity the identifier of the activity to be updated.
	 * @param person the person that is associated to the teaching activity.
	 * @param code the code or acronym of the teaching activity.
	 * @param title the title of the teaching activity.
	 * @param level the level of teaching.
	 * @param studentType the type of the targeted students.
	 * @param language the language of the activity. If it not provided, the default value is applied.
	 * @param degree the name of the degree.
	 * @param university the university in which the teaching activity is realized.
	 * @param startDate the start date of the activity.
	 * @param endDate the end date of the activity, or {@code null} if it is still active.
	 * @param role the role of the person in the activity.
	 * @param differentHetdForTdTp indicates if the person has different factors for her/his hours of tutorials
	 *      and practice works for computing the number of hETD.
	 * @param numberOfStudents is an estimation of the number of students per year in the activity.
	 * @param explanation is a text that describes the activity.
	 * @param pedagogicalPracticeTypes the types of pedagogical practices that are applied in the teaching activity.
	 * @param activityUrl is a link to an external website that is dedicated to the activity. 
	 * @param sourceUrl is a link to an external website that provides source code for the activity.
	 * @param annualWorkPerType maps the types of teaching activity to the annual number of hours.
	 * @param pathToSlides are the uploaded slides, or {@code null} if no upload. If there is no uploaded,
	 *     any previously uploaded slides are kept.
	 * @param removePathToSlides indicates if previously uploaded slides must be removed.
	 * @return the updated activity, or nothing if the activity cannot be created.
	 * @throws Exception if the slides cannot be uploaded.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Optional<TeachingActivity> updateTeachingActivity(
			long activity, Person person, String code, String title,
			TeachingActivityLevel level, StudentType studentType,
			CountryCode language, String degree, ResearchOrganization university,
			LocalDate startDate, LocalDate endDate, TeacherRole role, boolean differentHetdForTdTp,
			int numberOfStudents, String explanation, Set<PedagogicalPracticeType> pedagogicalPracticeTypes,
			URL activityUrl, URL sourceUrl, Map<TeachingActivityType, Float> annualWorkPerType,
			MultipartFile pathToSlides, boolean removePathToSlides) throws Exception {
		final Optional<TeachingActivity> res;
		if (activity >= 0) {
			res = this.teachingActivityRepository.findById(Long.valueOf(activity));
		} else {
			res = Optional.empty();
		}
		if (res.isPresent()) {
			updateTeachingActivity(res.get(),
					person, code, title, level, studentType, language, degree, university, startDate, endDate,
					role, differentHetdForTdTp, numberOfStudents, explanation, pedagogicalPracticeTypes,
					activityUrl, sourceUrl, annualWorkPerType, pathToSlides, removePathToSlides);
		}
		return res;
	}

	/** Update an existing teaching activity with the given attributes.
	 *
	 * @param activity the activity to be updated.
	 * @param person the person that is associated to the teaching activity.
	 * @param code the code or acronym of the teaching activity.
	 * @param title the title of the teaching activity.
	 * @param level the level of teaching.
	 * @param studentType the type of the targeted students.
	 * @param language the language of the activity. If it not provided, the default value is applied.
	 * @param degree the name of the degree.
	 * @param university the university in which the teaching activity is realized.
	 * @param startDate the start date of the activity.
	 * @param endDate the end date of the activity, or {@code null} if it is still active.
	 * @param role the role of the person in the activity.
	 * @param differentHetdForTdTp indicates if the person has different factors for her/his hours of tutorials
	 *      and practice works for computing the number of hETD.
	 * @param numberOfStudents is an estimation of the number of students per year in the activity.
	 * @param explanation is a text that describes the activity.
	 * @param pedagogicalPracticeTypes the types of pedagogical practices that are applied in the teaching activity.
	 * @param activityUrl is a link to an external website that is dedicated to the activity. 
	 * @param sourceUrl is a link to an external website that provides source code for the activity.
	 * @param annualWorkPerType maps the types of teaching activity to the annual number of hours.
	 * @param pathToSlides are the uploaded slides, or {@code null} if no upload. If there is no uploaded,
	 *     any previously uploaded slides are kept.
	 * @param removePathToSlides indicates if previously uploaded slides must be removed.
	 * @throws Exception if the slides cannot be uploaded.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	protected void updateTeachingActivity(
			TeachingActivity activity, Person person, String code, String title,
			TeachingActivityLevel level, StudentType studentType,
			CountryCode language, String degree, ResearchOrganization university,
			LocalDate startDate, LocalDate endDate, TeacherRole role, boolean differentHetdForTdTp,
			int numberOfStudents, String explanation, Set<PedagogicalPracticeType> pedagogicalPracticeTypes,
			URL activityUrl, URL sourceUrl, Map<TeachingActivityType, Float> annualWorkPerType,
			MultipartFile pathToSlides, boolean removePathToSlides) throws Exception {
		activity.setActivityUrl(activityUrl);
		activity.setCode(code);
		activity.setDifferentHetdForTdTp(differentHetdForTdTp);
		activity.setStartDate(startDate);
		activity.setEndDate(endDate);
		activity.setExplanation(explanation);
		activity.setPedagogicalPracticeTypes(pedagogicalPracticeTypes);
		activity.setLanguage(language);
		activity.setLevel(level);
		activity.setNumberOfStudents(numberOfStudents);
		activity.setRole(role);
		activity.setSourceUrl(sourceUrl);
		activity.setStudentType(studentType);
		activity.setTitle(title);
		activity.setPerson(person);
		activity.setDegree(degree);
		activity.setUniversity(university);
		activity.setAnnualWorkPerType(annualWorkPerType);
		this.teachingActivityRepository.save(activity);

		if (updateSlides(activity, removePathToSlides, pathToSlides)) {
			this.teachingActivityRepository.save(activity);
		}
	}

	@Deprecated(since = "4.0", forRemoval = true)
	private boolean updateSlides(TeachingActivity activity, boolean explicitRemove, MultipartFile uploadedFile) throws IOException {
		final var logger = LoggerFactory.getLogger(getClass());
		return updateUploadedFile(explicitRemove, uploadedFile,
				"Teachnig activity's slides uploaded at: ", //$NON-NLS-1$
				LoggerFactory.getLogger(getClass()),
				it -> activity.setPathToSlides(it),
				() -> this.fileManager.makeTeachingActivitySlidesFilename(activity.getId()),
				() -> this.fileManager.deleteTeachingActivitySlides(activity.getId(), logger),
				(fn, th) -> this.fileManager.savePdfAndThumbnailFiles(fn, th, uploadedFile, logger));
	}

	/** Replies if the given identifier is for a teacher.
	 *
	 * @param id the identifier of the person.
	 * @return {@code true} if the person is a teacher.
	 * @since 3.6
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean isTeacher(long id) {
		return !this.teachingActivityRepository.findDistinctByPersonId(Long.valueOf(id)).isEmpty();
	}

	@Override
	public EntityEditingContext<TeachingActivity> startEditing(TeachingActivity activity, Logger logger) {
		assert activity != null;
		// Force loading of the persons and universities that may be edited at the same time as the rest of the journal properties
		inSession(session -> {
			if (activity.getId() != 0l) {
				session.load(activity, Long.valueOf(activity.getId()));
				Hibernate.initialize(activity.getPerson());
				Hibernate.initialize(activity.getUniversity());
				Hibernate.initialize(activity.getPedagogicalPracticeTypes());
				Hibernate.initialize(activity.getAnnualWorkPerType());
			}
		});
		return new EditingContext(activity, logger);
	}

	@Override
	public EntityDeletingContext<TeachingActivity> startDeletion(Set<TeachingActivity> entities, Logger logger) {
		assert entities != null && !entities.isEmpty();
		return new DeletingContext(entities, logger);
	}

	/** Replies the teaching activities for the organization.
	 *
	 * @param organization the organization.
	 * @return the list of teaching activities.
	 */
	public List<TeachingActivity> getActivitiesByOrganizationId(ResearchOrganization organization) {
		return this.teachingActivityRepository.findDistinctByUniversity(organization);
	}

	/** Context for editing a {@link TeachingActivity}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityWithServerFilesEditingContext<TeachingActivity> {

		private static final long serialVersionUID = -7122364187938515699L;
		
		private UploadedFileTracker<TeachingActivity> pathToSlides;

		/** Constructor.
		 *
		 * @param activity the edited teaching activity.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(TeachingActivity activity, Logger logger) {
			super(activity, logger);
			this.pathToSlides = newUploadedFileTracker(activity,
					TeachingActivity::getPathToSlides,
					(id, savedPath) -> TeachingService.this.fileManager.deleteTeachingActivitySlides(id.longValue(), logger),
					null);
		}

		@Override
		protected TeachingActivity writeInJPA(TeachingActivity entity, boolean initialSaving) {
			return TeachingService.this.teachingActivityRepository.save(this.entity);
		}

		@Override
		protected void deleteOrRenameAssociatedFiles(long oldId) throws IOException {
			this.pathToSlides.deleteOrRenameFile(oldId, this.entity, getLogger());
		}

		@Override
		protected boolean prepareAssociatedFileUpload() throws IOException {
			return !this.pathToSlides.deleteFile(this.entity, getLogger());
		}

		@Override
		protected void postProcessAssociatedFiles() {
			this.pathToSlides.resetPathMemory(this.entity, getLogger());
		}

		@Override
		public EntityDeletingContext<TeachingActivity> createDeletionContext() {
			return TeachingService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link TeachingActivity}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<TeachingActivity> {

		private static final long serialVersionUID = 7902434669050350824L;

		/** Constructor.
		 *
		 * @param activities the teaching activities to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<TeachingActivity> activities, Logger logger) {
			super(activities, logger);
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			TeachingService.this.teachingActivityRepository.deleteAllById(identifiers);

			for (final var id : identifiers) {
				TeachingService.this.fileManager.deleteTeachingActivitySlides(id.longValue(), getLogger());
			}
		}

	}

}
