package automat;

import exceptions.AlreadyExistsException;
import exceptions.EmptyListException;
import exceptions.FullAutomatException;
import exceptions.InvalidInputException;
import observer.Observable;
import observer.Observer;


import java.io.Serializable;
import java.util.*;

public class Automat implements Observable, Serializable {
    private final LinkedList<Hersteller> herstellerList = new LinkedList<>();
    private final KuchenKomponente[] kuchenList;
    private final transient LinkedList<Observer> observerList = new LinkedList<>();
    private volatile int kuchenCounter = 0;

    public Automat(int fachzahl) {
        this.kuchenList = new KuchenKomponente[fachzahl];
    }

    synchronized public void addHersteller(Hersteller hersteller) throws AlreadyExistsException {
        //check if manufacturer already exists
        for (Hersteller herst : this.herstellerList) {
            if (herst.getName().equals(hersteller.getName())) {
                throw new AlreadyExistsException();
            }
        }
        this.herstellerList.add(hersteller);
        notifyObservers();
    }

    synchronized public void removeHersteller(String hersteller) throws NoSuchElementException {
        for (Hersteller herst : this.herstellerList) {
            if (herst.getName().equals(hersteller)) {
                this.herstellerList.remove(herst);
                //remove all kuchen of the hersteller from the automat as well
                for (int i = 0; i < this.kuchenList.length; i++) {
                    if (this.kuchenList[i] != null && this.kuchenList[i].getHersteller().getName().equals(hersteller)) {
                        this.kuchenList[i] = null;
                        kuchenCounter--;
                    }
                }
                notifyObservers();
                return;
            }
        }
        throw new NoSuchElementException();
    }

    synchronized public void addKuchen(KuchenKomponente kuchen) throws NoSuchElementException, FullAutomatException {

        //check if there is already a hersteller with boolean flag
        boolean herstFlag = false;
        for (Hersteller herst : this.herstellerList) {
            if (kuchen.getHersteller().getName().equals(herst.getName())) {
                herstFlag = true;
                break;
            }
        }

        //throw exception if no hersteller has been found
        if (!herstFlag) {
            throw new NoSuchElementException();
        }

        //insert the kuchen at the first empty place
        boolean fullFlag = true;
        for (int i = 0; i < this.kuchenList.length; i++) {
            if (this.kuchenList[i] == null) {
                kuchen.setFachNummer(i);    //put this somewhere else?
                kuchen.setInspektionsDatum(new Date());
                kuchen.setEinfuegeDatum(new Date());
                this.kuchenList[i] = kuchen;
                fullFlag = false;
                this.kuchenCounter++;
                notifyObservers();
                break;
            }
        }
        if (fullFlag) {
            throw new FullAutomatException();
        }
    }

    synchronized public KuchenKomponente getKuchen(int fachnummer) throws NoSuchElementException, InvalidInputException {
        checkNumber(fachnummer);

        if (kuchenList[fachnummer] == null) {
            throw new NoSuchElementException();
        }
        return this.kuchenList[fachnummer];
    }

    synchronized public void removeKuchen(int fachnummer) throws NoSuchElementException, InvalidInputException {
        checkNumber(fachnummer);

        //check the list if the fachnummer exists, first
        if (this.kuchenList[fachnummer] == null) {
            throw new NoSuchElementException();
        }

        this.kuchenList[fachnummer] = null;
        this.kuchenCounter--;
        notifyObservers();
    }

    synchronized public void swapKuchen(int fachNummer1, int fachNummer2) throws InvalidInputException {
        checkNumber(fachNummer1);
        checkNumber(fachNummer2);

        if (this.kuchenList[fachNummer1] == null || this.kuchenList[fachNummer2] == null) {
            throw new NoSuchElementException();
        }

        KuchenKomponente tempKuchen = getKuchen(fachNummer1);

        changeKuchen(fachNummer1, getKuchen(fachNummer2));
        changeKuchen(fachNummer2, tempKuchen);
    }

    //only needed for swapping not modifying the automat
    synchronized private void changeKuchen(int fachnummer, KuchenKomponente kuchen) throws InvalidInputException {
        checkNumber(fachnummer);
        kuchen.setFachNummer(fachnummer);

        //exception removed for easier swap and the ability to inserti kuchen
        this.kuchenList[fachnummer] = kuchen;
    }

    synchronized public LinkedList<Hersteller> getHersteller() throws EmptyListException {
        if (this.herstellerList.isEmpty()) {
            throw new EmptyListException();
        }
        return this.herstellerList;
    }

    synchronized public HashMap<String, Integer> checkHersteller() throws NoSuchElementException, EmptyListException {
        if (this.herstellerList.isEmpty()) {
            throw new EmptyListException();
        }
        HashMap<String, Integer> manufacturerHashmap = new HashMap<>();

        for (Hersteller manu : this.herstellerList) {
            if (!manufacturerHashmap.containsKey(manu)) {
                manufacturerHashmap.put(manu.getName(), 0);
            }
        }

        for (KuchenKomponente kuch : this.kuchenList) {
            if (kuch == null) {
                break; // dont go into objekt if null to avoid NullPointerException
            }
            if (manufacturerHashmap.containsKey(kuch.getHersteller().getName())) {
                manufacturerHashmap.put(kuch.getHersteller().getName(), manufacturerHashmap.get(kuch.getHersteller().getName()) + 1);
            } else {
                manufacturerHashmap.put(kuch.getHersteller().getName(), 1);
            }
        }
        return manufacturerHashmap;
    }

    synchronized public List<KuchenKomponente> checkKuchen() throws EmptyListException {
        if(kuchListEmpty()) throw new EmptyListException();

        LinkedList<KuchenKomponente> res = new LinkedList<>();

        for(int i = 0; i < this.kuchenList.length; i++) {
            if (this.kuchenList[i] != null) {
                res.add(this.kuchenList[i]);
            }
        }

        return res;
    }

    synchronized public List<KuchenKomponente> checkKuchen(String kuchen) throws NoSuchElementException, EmptyListException {
        if(kuchListEmpty()) throw new EmptyListException();

        LinkedList<KuchenKomponente> res = new LinkedList<>();

        for (int i = 0; i < this.kuchenList.length; i++) {
            if (this.kuchenList[i] != null && this.kuchenList[i].getName().contains(kuchen)) {
                res.add(this.kuchenList[i]);
            }
        }

        if (res.isEmpty()) {
            throw new NoSuchElementException();
        }

        return res;
    }

    synchronized public Set<Allergen> checkAllergen() throws EmptyListException {
        if(kuchListEmpty()) throw new EmptyListException();

        HashSet<Allergen> res = new HashSet<Allergen>();

        for (KuchenKomponente kuch : kuchenList) {
            if (kuch != null) {
                res.addAll(kuch.getAllergene());
            }
        }
        return res;
    }

    synchronized public Set<Allergen> checkAbsentAllergen() throws EmptyListException {
        if(kuchListEmpty()) throw new EmptyListException();

        HashSet<Allergen> res = new HashSet<>();
        res.add(Allergen.Erdnuss);
        res.add(Allergen.Haselnuss);
        res.add(Allergen.Gluten);
        res.add(Allergen.Sesamsamen);

        for (KuchenKomponente kuch : kuchenList) {
            if (kuch != null) {
                res.removeAll(kuch.getAllergene());
            }
        }

        return res;
    }

    synchronized public void setInspectionDate(int fachnummer) throws InvalidInputException {
        checkNumber(fachnummer);

        if(this.kuchenList[fachnummer] == null){
            throw new NoSuchElementException();
        } else {
            this.kuchenList[fachnummer].setInspektionsDatum(new Date());
            notifyObservers();
        }
    }

    //function to check if the fachnummer is negative
    synchronized public void checkNumber(int num) throws InvalidInputException {
        if (num < 0 || num > this.kuchenList.length) {
            throw new InvalidInputException();
        }
    }

    synchronized public boolean kuchListEmpty() {
        boolean flag = false;
        for (KuchenKomponente kuchen : this.kuchenList) {
            if (kuchen != null) {
                flag = true;
                break;
            }
        }
        return !flag;
    }

    public int getSize() {
        return this.kuchenList.length;
    }

    @Override
    public void addObserver(Observer observer) {
        this.observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        if(observerList!=null)
        for (Observer observer : this.observerList) {
                observer.update();
        }
    }

    public int getKuchenCounter() {
        return kuchenCounter;
    }
}
