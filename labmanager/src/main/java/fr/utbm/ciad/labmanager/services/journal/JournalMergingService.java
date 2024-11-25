package fr.utbm.ciad.labmanager.services.journal;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.journal.*;
import fr.utbm.ciad.labmanager.data.publication.AbstractJournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.type.JournalEdition;
import fr.utbm.ciad.labmanager.data.publication.type.JournalEditionRepository;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaperRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.utils.names.JournalNameOrPublisherComparator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Service for the merging journals.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class JournalMergingService extends AbstractEntityService<Journal> {

    private static final long serialVersionUID = 1L;

    private final JournalRepository journalRepository;

    private final JournalService journalService;

    private final JournalNameOrPublisherComparator nameComparator;

    private final JournalPaperRepository journalPaperRepository;

    private final JournalEditionRepository journalEditionRepository;


    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param sessionFactory the factory of JPA session.
     * @since 4.0
     */
    public JournalMergingService(MessageSourceAccessor messages, ConfigurationConstants constants, SessionFactory sessionFactory, JournalRepository journalRepository, JournalService journalService, JournalNameOrPublisherComparator nameComparator, JournalQualityAnnualIndicatorsRepository qualityIndicatorsRepository, JournalPaperRepository journalPaperRepository, JournalEditionRepository journalEditionRepository) {
        super(messages, constants, sessionFactory);
        this.journalRepository = journalRepository;
        this.journalService = journalService;
        this.nameComparator = nameComparator;
        this.journalPaperRepository = journalPaperRepository;
        this.journalEditionRepository = journalEditionRepository;
    }

    /** Replies the duplicate journal names.
     * The replied list contains groups of journals who have similar names and publishers.
     *
     * @param comparator comparator of journals that is used for sorting the groups of duplicates. If it is {@code null},
     *      a {@link JournalComparator} is used.
     * @param callback the callback invoked during the building.
     * @return the duplicate journals that is finally computed.
     * @throws Exception if a problem occurred during the building.
     */
    public List<Set<Journal>> getJournalDuplicates(Comparator<? super Journal> comparator, JournalMergingService.JournalDuplicateCallback callback,
                                                   double threshold) throws Exception {
        // Each list represents a group of journal that could be duplicate
        final var matchingJournals = new ArrayList<Set<Journal>>();

        // Copy the list of journals into another list in order to enable its
        // modification during the function's process
        final var journalsList = new ArrayList<>(this.journalRepository.findAll());

        final Comparator<? super Journal> theComparator = comparator == null ? EntityUtils.getPreferredJournalComparator() : comparator;

        final var total = journalsList.size();
        // Notify the callback
        if (callback != null) {
            callback.onDuplicate(0, 0, total);
        }
        var duplicateCount = 0;

        nameComparator.setSimilarityLevel(threshold);
        for (var i = 0; i < journalsList.size() - 1; ++i) {
            final var referenceJournal = journalsList.get(i);

            final var currentMatching = new TreeSet<Journal>(theComparator);
            currentMatching.add(referenceJournal);

            this.nameComparator.setSimilarityLevel(0.8);
            final ListIterator<Journal> iterator2 = journalsList.listIterator(i + 1);
            while (iterator2.hasNext()) {
                final var otherJournal = iterator2.next();
                if (this.nameComparator.isSimilar(
                        referenceJournal.getJournalName(), referenceJournal.getPublisher(),
                        otherJournal.getJournalName(), otherJournal.getPublisher())) {
                    currentMatching.add(otherJournal);
                    ++duplicateCount;

                    iterator2.remove();
                }
            }
            if (currentMatching.size() > 1) {
                matchingJournals.add(currentMatching);
            }
            // Notify the callback
            if (callback != null) {
                callback.onDuplicate(i, duplicateCount, total);
            }
        }

        return matchingJournals;
    }

    /** Merge the entities by replacing those with an old journal by those with the new journal.
     *
     * @param sources the list of journals to remove and replace by the target journal.
     * @param target the target journal who should replace the source journals.
     * @throws Exception if the merging cannot be completed.
     */
    public void mergeJournals(Iterable<Journal> sources, Journal target) throws Exception {
        assert target != null;
        assert sources != null;
        var changed = false;
        for (final var source : sources) {
            if (source.getId() != target.getId()) {
                //getLogger().info("Reassign to " + target.getAcronymOrName() + " the elements of " + source.getAcronymOrName()); //$NON-NLS-1$ //$NON-NLS-2$
                var lchange = reassignJournalProperties(source, target);
                lchange = reassignJournalPublicationPapers(source, target);
                lchange = reassignJournalQualityIndicators(source, target) || lchange;
                this.journalService.removeJournal(source.getId());
                changed = changed || lchange;
            }
        }
        if (changed) {
            this.journalRepository.save(target);
        }
    }

    /** Re-assign the properties attached to the source journal to the target journal. There are attached only if the
     * target journal has null properties.
     *
     * @param source the journal to remove and replace by the target journal.
     * @param target the target journal which should replace the source journals.
     * @return {@code true} if journal properties has changed.
     * @throws Exception if the change cannot be completed.
     */
    protected boolean reassignJournalProperties(Journal source, Journal target){

        boolean changed = false;

        if (target.getJournalName() == null && source.getJournalName() != null) {
            target.setJournalName(source.getJournalName());
            changed = true;
        }

        if (target.getPublisher() == null && source.getPublisher() != null) {
            target.setPublisher(source.getPublisher());
            changed = true;
        }

        if (target.getAddress() == null && source.getAddress() != null) {
            target.setAddress(source.getAddress());
            changed = true;
        }

        if (target.getJournalURL() == null && source.getJournalURL() != null) {
            target.setJournalURL(source.getJournalURL());
            changed = true;
        }

        if (target.getScimagoId() == null && source.getScimagoId() != null) {
            target.setScimagoId(source.getScimagoId());
            changed = true;
        }

        if (target.getScimagoCategory() == null && source.getScimagoCategory() != null) {
            target.setScimagoCategory(source.getScimagoCategory());
            changed = true;
        }

        if (target.getWosId() == null && source.getWosId() != null) {
            target.setWosId(source.getWosId());
            changed = true;
        }

        if (target.getWosCategory() == null && source.getWosCategory() != null) {
            target.setWosCategory(source.getWosCategory());
            changed = true;
        }

        if (target.getISBN() == null && source.getISBN() != null) {
            target.setISBN(source.getISBN());
            changed = true;
        }

        if (target.getISSN() == null && source.getISSN() != null) {
            target.setISSN(source.getISSN());
            changed = true;
        }

        return changed;
    }


    /** Re-assign the publication papers attached to the source journal to the target journal.
     *
     * @param source the journal to remove and replace by the target journal.
     * @param target the target journal which should replace the source journals.
     * @return {@code true} if journal publication papers has changed.
     * @throws Exception if the change cannot be completed.
     */
    protected boolean reassignJournalPublicationPapers(Journal source, Journal target) throws Exception {
        var changed = false;

        Set<AbstractJournalBasedPublication> sourcePapers = journalService.getPapersByJournalId(source.getId());
        Set<AbstractJournalBasedPublication> targetPapers = journalService.getPapersByJournalId(target.getId());

        for (AbstractJournalBasedPublication paper : sourcePapers) {
            if (!targetPapers.contains(paper)) {
                paper.setJournal(target);
                changed = true;
                if(paper instanceof JournalPaper) {
                    this.journalPaperRepository.save((JournalPaper) paper);
                } else {
                    this.journalEditionRepository.save((JournalEdition) paper);
                }
            }
        }
        return changed;
    }


    /** Re-assign the quality indicators attached to the source journal to the target journal. The function checks if the
     * target journal already has the quality indicators of the source journal.
     *
     * @param source the journal to remove and replace by the target journal.
     * @param target the target journal which should replace the source journals.
     * @return {@code true} if journal quality indicators has changed.
     */
    public boolean reassignJournalQualityIndicators(Journal source, Journal target) {
        var changed = false;
        List<JournalQualityAnnualIndicators> indicatorsSource = journalService.getJournalQualityIndicatorsByJournalId(source.getId());
        List<JournalQualityAnnualIndicators> indicatorsTarget = journalService.getJournalQualityIndicatorsByJournalId(target.getId());

        Map<Integer, JournalQualityAnnualIndicators> indicatorsMap = indicatorsTarget.stream()
                .collect(Collectors.toMap(JournalQualityAnnualIndicators::getReferenceYear, Function.identity()));

        for (JournalQualityAnnualIndicators indicatorSource : indicatorsSource) {
            Integer referenceYear = indicatorSource.getReferenceYear();

            if (!indicatorsMap.containsKey(referenceYear)) {

                Map<Integer, JournalQualityAnnualIndicators> sample = new TreeMap<>();
                sample.put(referenceYear, indicatorSource);

                target.setQualityIndicators(sample);

                changed = true;

            }
        }

        if (changed) {
            journalRepository.save(target);
        }

        return changed;
    }


    @Override
    public EntityEditingContext<Journal> startEditing(Journal entity, Logger logger) {
        return (EntityEditingContext<Journal>) new UnsupportedOperationException();
    }

    @Override
    public EntityDeletingContext<Journal> startDeletion(Set<Journal> entities, Logger logger) {
        return (EntityDeletingContext<Journal>) new UnsupportedOperationException();
    }


    /** Callback that is invoked when building the list of duplicate journals.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.2
     */
    @FunctionalInterface
    public interface JournalDuplicateCallback {

        /** Invoked for each journal.
         *
         * @param index the position of the reference journal in the list of journals. It represents the progress of the treatment
         *     of each journal.
         * @param duplicateCount the count of discovered duplicates.
         * @param total the total number of journals in the list.
         * @throws Exception if there is an error during the callback treatment. This exception is forwarded to the
         *     caller of the function that has invoked this callback.
         */
        void onDuplicate(int index, int duplicateCount, int total) throws Exception;

    }
}
