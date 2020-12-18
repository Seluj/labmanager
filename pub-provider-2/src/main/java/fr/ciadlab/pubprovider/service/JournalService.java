package fr.ciadlab.pubprovider.service;

import fr.ciadlab.pubprovider.PubProviderApplication;
import fr.ciadlab.pubprovider.entities.Journal;
import fr.ciadlab.pubprovider.entities.ReadingCommitteeJournalPopularizationPaper;
import fr.ciadlab.pubprovider.repository.JournalRepository;
import fr.ciadlab.pubprovider.repository.ReadingCommitteeJournalPopularizationPaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Service
public class JournalService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JournalRepository repo;

    @Autowired
    private ReadingCommitteeJournalPopularizationPaperRepository pubRepo;

    public static BufferedImage getImageFromURL(String url) {
        try {
            URL sciURL = new URL(url);
            BufferedImage image;

            if (PubProviderApplication.PROXYURL.compareTo("") != 0) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PubProviderApplication.PROXYURL, PubProviderApplication.PROXYPORT));
                URLConnection connection = sciURL.openConnection(proxy);
                connection.connect();
                //InputStream in = new BufferedInputStream(sciURL.openStream());
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                ByteArrayInputStream bis = new ByteArrayInputStream(response);
                image = ImageIO.read(bis);
            } else //I assume this should be enough to work but given that Im behind a proxy I cant try it
            {
                image = ImageIO.read(sciURL);
            }
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Journal> getAllJournals() {
        List<Journal> jours = repo.findAll();
        return jours;
    }

    public List<Journal> getJournal(int index) {
        List<Journal> result = new ArrayList<Journal>();
        final Optional<Journal> res = repo.findById(index);
        return result;
    }

    public void removeJournal(int index) {
        Optional<Journal> j = repo.findById(index);
        if (j.isPresent()) {
            //Deletion should be disabled if it still has pubs attached to it but just in case
            for (ReadingCommitteeJournalPopularizationPaper p : j.get().getJourPubs()) {
                p.setReaComConfPopPapJournal(null);
                pubRepo.save(p);
            }
            repo.deleteById(index);
        }
    }

    public int createJournal(String jourName, String jourPublisher, String jourElsevier, String jourScimago, String jourWos) {

        final Journal res = new Journal();
        //Generic pub fields
        System.out.println("assigning fields");
        res.setJourName(jourName);
        res.setJourPublisher(jourPublisher);
        res.setJourElsevier(jourElsevier);
        res.setJourScimago(jourScimago);
        res.setJourWos(jourWos);
        res.setJourQuartil(getJournalQuartilInfo(jourScimago));

        if (res.getJourName().contains("LNCS") && res.getJourQuartil() == "2") {
            res.setJourQuartil("2 (LNCS)");
        }
        System.out.println("Saving");
        this.repo.save(res);

        System.out.println("exiting");
        return res.getJourId();
    }

    public void updateJournal(int pubId, String jourName, String jourPublisher, String jourElsevier, String jourScimago, String jourWos) {
        final Optional<Journal> res = this.repo.findById(pubId);
        if (res.isPresent()) {
            //Generic pub fields
            if (!jourName.isEmpty())
                res.get().setJourName(jourName);
            if (!jourPublisher.isEmpty())
                res.get().setJourPublisher(jourPublisher);
            if (!jourElsevier.isEmpty())
                res.get().setJourElsevier(jourElsevier);
            if (!jourScimago.isEmpty())
                res.get().setJourScimago(jourScimago);
            res.get().setJourQuartil(getJournalQuartilInfo(jourScimago));
            if (!jourWos.isEmpty())
                res.get().setJourWos(jourWos);

            if (res.get().getJourName().contains("LNCS") && res.get().getJourQuartil() == "2") {
                res.get().setJourQuartil("2 (LNCS)");
            }

            this.repo.save(res.get());
        }
    }

    public void addJournalLink(int pubId, int jourId) {
        Optional<ReadingCommitteeJournalPopularizationPaper> pub = pubRepo.findById(pubId);
        Optional<Journal> jour = repo.findById(jourId);
        if (pub.isPresent() && jour.isPresent()) {
            removeJournalLink(pubId);
            jour.get().getJourPubs().add(pub.get());
            pub.get().setReaComConfPopPapJournal(jour.get());
            repo.save(jour.get());
            pubRepo.save(pub.get());
        }
    }

    public void removeJournalLink(int pubId) {
        Optional<ReadingCommitteeJournalPopularizationPaper> pub = pubRepo.findById(pubId);
        Journal jour;

        if (pub.isPresent() && pub.get().getReaComConfPopPapJournal() != null) {
            jour = pub.get().getReaComConfPopPapJournal();
            jour.getJourPubs().remove(pub.get());
            pub.get().setReaComConfPopPapJournal(null);
            repo.save(jour);
            pubRepo.save(pub.get());
        }
    }

    //Review if optional as result when several result possible is a good idea
    public int getJournalIdByName(String jourName) {
        List<Journal> result = new ArrayList<Journal>();
        final Optional<Journal> res = repo.findByJourName(jourName);
        if (res.isPresent()) {
            result.add(res.get());
        }


        if (!result.isEmpty()) {
            return result.get(0).getJourId(); //We assume theres no name dupes
        } else {
            return 0;
        }
    }

    public String getJournalQuartilInfo(String scimagoID) {

        String result = "0";

        BufferedImage image = getImageFromURL("https://www.scimagojr.com/journal_img.php?id=" + scimagoID);
        int rgba = image.getRGB(5, 55);
        String r = Integer.toString((rgba >> 16) & 0xff);
        switch (r) {
            case "164":
                result = "1";
                break;

            case "232":
                result = "2";
                break;

            case "251":
                result = "3";
                break;

            case "221":
                result = "4";
                break;
        }

        return result;
    }


}


