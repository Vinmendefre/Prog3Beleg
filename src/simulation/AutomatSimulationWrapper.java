package simulation;

import automat.*;
import exceptions.EmptyListException;
import exceptions.FullAutomatException;
import exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutomatSimulationWrapper {
    private Automat automat;
    private final Lock lock = new ReentrantLock();
    private final Condition full = this.lock.newCondition();
    private final Condition empty = this.lock.newCondition();

    private final String MASCARPONE = "Mascarpone";
    private final String SENF = "Senf";
    private final String KIRSCHE = "Kirsche";
    private final String ERDBEERE = "Erdbeere";
    private final String BENJAMIN = "benjamin";
    private final String BLUEMCHEN = "blümchen";
    private final String MOSES = "moses";
    private final Hersteller herst1 = new HerstellerImpl(BENJAMIN);
    private final Hersteller herst2 = new HerstellerImpl(BLUEMCHEN);
    private final Hersteller herst3 = new HerstellerImpl(MOSES);
    private final Duration dur1 = Duration.ofDays(4);
    private final LinkedList<Allergen> allergList1 = new LinkedList<>(Arrays.asList(Allergen.Erdnuss, Allergen.Haselnuss));
    private final LinkedList<Allergen> allergList2 = new LinkedList<>(Arrays.asList(Allergen.Gluten, Allergen.Sesamsamen));
    private final KremkuchenImpl kuch1 = new KremkuchenImpl(herst1, allergList1, 350, dur1, new BigDecimal(500));
    private final ObstkuchenImpl kuch2 = new ObstkuchenImpl(herst1, allergList2, 400, dur1, new BigDecimal(250));
    private final ObsttorteImpl kuch3 = new ObsttorteImpl(herst1, allergList2, 700, dur1, new BigDecimal(300));
    private final KremkuchenImpl kuch4 = new KremkuchenImpl(herst1, allergList1, 250, dur1, new BigDecimal(400));
    private final KremkuchenImpl kuch5 = new KremkuchenImpl(herst2, allergList2, 300, dur1, new BigDecimal(500));
    private final ObstkuchenImpl kuch6 = new ObstkuchenImpl(herst2, allergList1, 400, dur1, new BigDecimal(250));
    private final ObsttorteImpl kuch7 = new ObsttorteImpl(herst2, allergList2, 500, dur1, new BigDecimal(300));
    private final KremkuchenImpl kuch8 = new KremkuchenImpl(herst3, allergList1, 250, dur1, new BigDecimal(400));
    private final ObsttorteImpl kuch9 = new ObsttorteImpl(herst3, allergList2, 600, dur1, new BigDecimal(300));
    private final KremkuchenImpl kuch10 = new KremkuchenImpl(herst3, allergList1, 250, dur1, new BigDecimal(400));
    private final BasisKuchenImpl[] kuchList = {kuch1, kuch2, kuch3, kuch4, kuch5, kuch6, kuch7, kuch8, kuch9, kuch10};

    synchronized void createRandomCake() {
        try {
            this.automat.addKuchen(copyCake(kuchList[(int) (Math.random() * 10)]));
        } catch (FullAutomatException e) {
            System.out.println("simulation: automat is full");
        }
        System.out.println("simulation added Cake");
    }

    synchronized void removeRandomCake() {
        try {
            int randomKuchen = this.automat.checkKuchen().get((int) (Math.random() *(this.automat.getKuchenCounter()))).getFachnummer();
            this.automat.removeKuchen(randomKuchen);
        } catch (InvalidInputException e) {
            System.out.println("simulation invalid input");
        } catch (NoSuchElementException e){
            System.out.println("Simulation removed random: NoSuchElement ");
        } catch (EmptyListException e) {
            System.out.println("Simulation emptylist");
        }
        System.out.println("simulation removed cake");
    }

    synchronized void removeOldestCake() {
        int oldestFachnummer = -1;//so i get an exception if this number is not replaced at least once
        Date oldestDate = new Date(3000, Calendar.FEBRUARY, 6);

        try {
            for (KuchenKomponente kuchen : this.automat.checkKuchen()) {
                if (kuchen.getEinfuegeDatum().before(oldestDate)) {
                    oldestDate = kuchen.getInspektionsdatum();
                    oldestFachnummer = kuchen.getFachnummer();
                }
            }
            this.automat.removeKuchen(oldestFachnummer);
            System.out.println("Simulation removed oldest cake");
        } catch (EmptyListException e) {
            System.out.println("simulation emptylist");
        } catch (InvalidInputException e) {
            System.out.println("simulation remove oldest: invalid input");
        } catch (NoSuchElementException e) {
            System.out.println("simulation remove oldest: nosuchelement");
        }
    }

    synchronized void causeInspection() {
        try {
            int randomKuchen = this.automat.checkKuchen().get((int) (Math.random() *(this.automat.getKuchenCounter()))).getFachnummer();
            this.automat.setInspectionDate((randomKuchen));
            System.out.println("Simulation inspektion");
        } catch (InvalidInputException e) {
            System.out.println("simulation inspektion: invalid input");
        } catch (NoSuchElementException e){
            System.out.println("Simulation inspektion NoSuchElement");
        } catch (EmptyListException e) {
            System.out.println("Simulation inspektion EmptyList");
        }
    }

    void createRandomCakeSynchronized() {
        this.lock.lock();
        try {
            while (this.automat.getKuchenCounter() == this.automat.getSize()) this.empty.await();
            createRandomCake();
            this.full.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    void removeOldestCakeSynchronized() {
        this.lock.lock();
        try {
            while (this.automat.getKuchenCounter() == 0) this.full.await();
            removeOldestCake();
            this.empty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    void removeMultipleOldestCakeSynchronized(Random random) {
        this.lock.lock();
        try {
            while (this.automat.getKuchenCounter() == 0) this.full.await();
            int randomInt = (random.nextInt(this.automat.getKuchenCounter()));

            for (int i = 0; i < randomInt; i++) {
                removeOldestCake();
            }
            this.full.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public void setAutomat(Automat automat) {
        this.automat = automat;
    }

    //to avoid reference chaos when adding the same cake over and over again
    private BasisKuchenImpl copyCake(BasisKuchenImpl kuchen) {
        return new BasisKuchenImpl(kuchen.getHersteller(), kuchen.getAllergene(), kuchen.getNaehrwert(), kuchen.getHaltbarkeit(), kuchen.getPreis());
    }
}