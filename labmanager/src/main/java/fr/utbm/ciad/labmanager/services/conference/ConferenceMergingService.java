package fr.utbm.ciad.labmanager.services.conference;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.conference.ConferenceRepository;
import fr.utbm.ciad.labmanager.data.journal.*;
import fr.utbm.ciad.labmanager.data.publication.AbstractConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.AbstractJournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.type.*;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.AbstractService;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.services.publication.type.ConferencePaperService;
import fr.utbm.ciad.labmanager.services.publication.type.KeyNoteService;
import fr.utbm.ciad.labmanager.utils.names.ConferenceNameComparator;
import fr.utbm.ciad.labmanager.utils.names.JournalNameOrPublisherComparator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Service for the merging conferences.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @Deprecated no replacement.
 */
@Service
public class ConferenceMergingService extends AbstractEntityService<Conference> {

    private static final long serialVersionUID = 1L;

    private final ConferenceNameComparator nameComparator;

    private final ConferenceRepository conferenceRepository;

    private final ConferenceService conferenceService;

    private final ConferencePaperService conferencePaperService;

    private final ConferencePaperRepository conferencePaperRepository;

    private final KeyNoteService keyNoteService;

    private final KeyNoteRepository keyNoteRepository;

    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param sessionFactory the factory of JPA session.
     * @since 4.0
     */
    public ConferenceMergingService(MessageSourceAccessor messages, ConfigurationConstants constants, SessionFactory sessionFactory,
                                    ConferenceNameComparator nameComparator, ConferenceRepository conferenceRepository,
                                    ConferenceService conferenceService,
                                    ConferencePaperService conferencePaperService,
                                    ConferencePaperRepository conferencePaperRepository,
                                    KeyNoteService keyNoteService,
                                    KeyNoteRepository keyNoteRepository) {
        super(messages, constants, sessionFactory);
        this.nameComparator = nameComparator;
        this.conferenceRepository = conferenceRepository;
        this.conferenceService = conferenceService;
        this.conferencePaperService = conferencePaperService;
        this.conferencePaperRepository = conferencePaperRepository;
        this.keyNoteService = keyNoteService;
        this.keyNoteRepository = keyNoteRepository;
    }

    /** Replies the duplicate conference names.
     * The replied list contains groups of conferences who have similar names and publishers.
     *
     * @param comparator comparator of conferences that is used for sorting the groups of duplicates. If it is {@code null},
     *      a {@link ConferenceComparator} is used.
     * @param callback the callback invoked during the building.
     * @return the duplicate conferences that is finally computed.
     * @throws Exception if a problem occurred during the building.
     */
    public List<Set<Conference>> getConferenceDuplicates(Comparator<? super Conference> comparator, ConferenceMergingService.ConferenceDuplicateCallback callback,
                                                         double threshold) throws Exception {
        // Each list represents a group of conference that could be duplicate
        final var matchingConferences = new ArrayList<Set<Conference>>();

        // Copy the list of conferences into another list in order to enable its
        // modification during the function's process
        final var conferencesList = new ArrayList<>(this.conferenceRepository.findAll());

        final Comparator<? super Conference> theComparator = comparator == null ? EntityUtils.getPreferredConferenceComparator() : comparator;

        final var total = conferencesList.size();
        // Notify the callback
        if (callback != null) {
            callback.onDuplicate(0, 0, total);
        }
        var duplicateCount = 0;
        nameComparator.setSimilarityLevel(threshold);
        for (var i = 0; i < conferencesList.size() - 1; ++i) {
            final var referenceConference = conferencesList.get(i);

            final var currentMatching = new TreeSet<Conference>(theComparator);
            currentMatching.add(referenceConference);

            final ListIterator<Conference> iterator2 = conferencesList.listIterator(i + 1);
            while (iterator2.hasNext()) {
                final var otherConference = iterator2.next();
                if (this.nameComparator.isSimilar(
                        referenceConference.getName(), referenceConference.getAcronym(),
                        otherConference.getName(), otherConference.getAcronym())) {
                    currentMatching.add(otherConference);
                    ++duplicateCount;

                    iterator2.remove();
                }
            }
            if (currentMatching.size() > 1) {
                matchingConferences.add(currentMatching);
            }
            // Notify the callback
            if (callback != null) {
                callback.onDuplicate(i, duplicateCount, total);
            }
        }

        return matchingConferences;
    }

    /** Merge the entities by replacing those with an old conference by those with the new conference.
     *
     * @param sources the list of conferences to remove and replace by the target conference.
     * @param target the target conference who should replace the source conferences.
     * @throws Exception if the merging cannot be completed.
     */
    public void mergeConferences(Iterable<Conference> sources, Conference target) throws Exception {
        assert target != null;
        assert sources != null;
        var changed = false;
        for (final var source : sources) {
            if (source.getId() != target.getId()) {

                var lchange = reassignConferenceProperties(source, target);
                lchange = reassignConferencePublicationPapers(source, target);
                lchange = reassignSuperConference(source, target) || lchange;
                lchange = reassignConferenceQualityIndicators(source, target) || lchange;

                this.conferenceService.removeConference(source.getId());
                changed = changed || lchange ;
            }
        }
        if (changed) {
            this.conferenceRepository.save(target);
        }
    }

    /** Re-assign the properties attached to the source conference to the target conference. There are attached only if
     * the target conference has null properties.
     *
     * @param source the journal to remove and replace by the target journal.
     * @param target the target journal which should replace the source journals.
     * @return {@code true} if journal publication papers has changed.
     * @throws Exception if the change cannot be completed.
     */
    protected boolean reassignConferenceProperties(Conference source, Conference target){

        boolean changed = false;

        if (target.getAcronym() == null && source.getAcronym() != null) {
            target.setAcronym(source.getAcronym());
            changed = true;
        }

        if (target.getName() == null && source.getName() != null) {
            target.setName(source.getName());
            changed = true;
        }

        if (target.getPublisher() == null && source.getPublisher() != null) {
            target.setPublisher(source.getPublisher());
            changed = true;
        }

        if (target.getConferenceURL() == null && source.getConferenceURL() != null) {
            target.setConferenceURL(source.getConferenceURL());
            changed = true;
        }

        if (target.getCoreId() == null && source.getCoreId() != null) {
            target.setCoreId(source.getCoreId());
            changed = true;
        }

        if (target.getISBN() == null && source.getISBN() != null) {
            target.setISBN(source.getISBN());
            changed = true;
        }

        return changed;
    }

    /** Reassign the conference publication papers of the source to the target.
     *
     * @param source the source conference to remove and replace by the target conference.
     * @param target the target conference who should replace the source conference.
     * @throws Exception if the merging cannot be completed.
     */
    protected boolean reassignConferencePublicationPapers(Conference source, Conference target) throws Exception {
        boolean changed = false;

        Set<ConferencePaper> conferencePapers = conferencePaperService.getConferencePapersByConference(source);
        Set<KeyNote> keyNotes = keyNoteService.getKeyNoteByConference(source);

        for (ConferencePaper paper : conferencePapers) {
            paper.setConference(target);
            changed = true;
        }
        for (KeyNote keyNote : keyNotes) {
            keyNote.setConference(target);
            changed = true;
        }

        if (changed) {
            this.conferenceRepository.save(target);
            this.conferencePaperRepository.saveAll(conferencePapers);
            this.keyNoteRepository.saveAll(keyNotes);
        }

        return changed;
    }

    /** Reassign the conferences under the source to the target.
     *
     * @param source the source conference to remove and replace by the target conference.
     * @param target the target conference who should replace the source conference.
     * @throws Exception if the merging cannot be completed.
     */
    protected boolean reassignSuperConference(Conference source, Conference target) throws Exception {
        boolean changed = false;

        Set<Conference> enclosedConferences = conferenceService.getEnclosedConferences(source);

        for(Conference enclosedConference : enclosedConferences) {
            enclosedConference.setEnclosingConference(target);
            changed = true;
        }

        if (changed) {
            this.conferenceRepository.save(target);
        }

        return changed;
    }


    /** Reassign the conference quality indicators of the source to the target. The function checks if the target conference already
     *  has the quality indicators of the source conference.
     *
     * @param source the source conference to remove and replace by the target conference.
     * @param target the target conference who should replace the source conference.
     * @throws Exception if the merging cannot be completed.
     */
    public boolean reassignConferenceQualityIndicators(Conference source, Conference target) {
        var changed = false;
        List<ConferenceQualityAnnualIndicators> indicatorsSource = conferenceService.getConferenceQualityIndicatorsByJournalId(source.getId());
        List<ConferenceQualityAnnualIndicators> indicatorsTarget = conferenceService.getConferenceQualityIndicatorsByJournalId(target.getId());

        Map<Integer, ConferenceQualityAnnualIndicators> indicatorsMap = indicatorsTarget.stream()
                .collect(Collectors.toMap(ConferenceQualityAnnualIndicators::getReferenceYear, Function.identity()));

        for (ConferenceQualityAnnualIndicators indicatorSource : indicatorsSource) {
            Integer referenceYear = indicatorSource.getReferenceYear();

            if (!indicatorsMap.containsKey(referenceYear)) {

                Map<Integer, ConferenceQualityAnnualIndicators> sample = new TreeMap<>();
                sample.put(referenceYear, indicatorSource);

                target.setQualityIndicators(sample);

                changed = true;

            }
        }

        if (changed) {
            conferenceRepository.save(target);
        }

        return changed;
    }

    @Override
    public EntityEditingContext<Conference> startEditing(Conference entity, Logger logger) {
        return (EntityEditingContext<Conference>) new UnsupportedOperationException();
    }

    @Override
    public EntityDeletingContext<Conference> startDeletion(Set<Conference> entities, Logger logger) {
        return (EntityDeletingContext<Conference>) new UnsupportedOperationException();
    }


    /** Callback that is invoked when building the list of duplicate conferences.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.2
     */
    @FunctionalInterface
    public interface ConferenceDuplicateCallback {

        /** Invoked for each conference.
         *
         * @param index the position of the reference conference in the list of conferences. It represents the progress of the treatment
         *     of each conference.
         * @param duplicateCount the count of discovered duplicates.
         * @param total the total number of conferences in the list.
         * @throws Exception if there is an error during the callback treatment. This exception is forwarded to the
         *     caller of the function that has invoked this callback.
         */
        void onDuplicate(int index, int duplicateCount, int total) throws Exception;

    }
}
