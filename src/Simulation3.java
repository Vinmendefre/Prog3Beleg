import automat.Automat;
import observer.AutomatChangeObserver;
import automat.Hersteller;
import automat.HerstellerImpl;
import exceptions.AlreadyExistsException;
import simulation.*;

public class Simulation3 {
    public static void main(String[] args) {
        String BENJAMIN = "benjamin";
        String BLUEMCHEN = "blümchen";
        String MOSES = "moses";
        Hersteller herst1 = new HerstellerImpl(BENJAMIN);
        Hersteller herst2 = new HerstellerImpl(BLUEMCHEN);
        Hersteller herst3 = new HerstellerImpl(MOSES);
        Automat automat = new Automat(6000);
        AutomatChangeObserver observer = new AutomatChangeObserver(automat);
        AutomatSimulationWrapper wrapper = new AutomatSimulationWrapper();
        wrapper.setAutomat(automat);

        CreateSynchronizedThread createThread1 = new CreateSynchronizedThread();
        CreateSynchronizedThread createThread2 = new CreateSynchronizedThread();
        CreateSynchronizedThread createThread3 = new CreateSynchronizedThread();
        DeleteMultipleThread deleteThread1 = new DeleteMultipleThread();
        DeleteMultipleThread deleteThread2 = new DeleteMultipleThread();
        InspektionThread inspektionThread1 = new InspektionThread();
//        InspektionThread inspektionThread2 = new InspektionThread();
        createThread1.setSimulationWrapper(wrapper);
        createThread2.setSimulationWrapper(wrapper);
        deleteThread1.setSimulationWrapper(wrapper);
        deleteThread2.setSimulationWrapper(wrapper);
        inspektionThread1.setWrapper(wrapper);
//        inspektionThread2.setWrapper(wrapper);

        try {
            automat.addHersteller(herst1);
            automat.addHersteller(herst2);
            automat.addHersteller(herst3);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        }
        createThread1.start();
        deleteThread1.start();
        createThread2.start();
        deleteThread2.start();
        createThread3.start();
//        inspektionThread2.start();
    }
}
