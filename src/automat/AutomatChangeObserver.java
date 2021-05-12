package automat;
import observer.Observer;

public class AutomatChangeObserver implements Observer {
    private final String evenString = "Automat is over 90% capacity";
    private final AutomatImpl automat;
    int capacity;
    int obsCounter;

    public AutomatChangeObserver(AutomatImpl automat) {
        this.automat = automat;
        this.automat.addObserver(this);
        this.capacity = this.automat.getSize();
        this.obsCounter = this.automat.getKuchenCounter();
    }

    @Override
    public void update() {
        if(this.obsCounter > this.automat.getKuchenCounter()){
            System.out.println("KUCHEN ENTFERNT");
            this.obsCounter = this.automat.getKuchenCounter();
        } else if (this.obsCounter < this.automat.getKuchenCounter()){
            System.out.println("KUCHEN HINZUGEFÜGT");
            this.obsCounter = this.automat.getKuchenCounter();
        }
    }
}
