package observer;

import automat.*;
import exceptions.AlreadyExistsException;
import exceptions.FullAutomatException;
import observer.AutomatAllergenObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.fail;

public class AutomatAllergenObserverTest {


    @Test
    public void AutomatAllergenObserverTestValid()  {
        Hersteller herst1 = new HerstellerImpl("MOSES");
        Duration dur1 = Duration.ofDays(4);
        LinkedList<Allergen> allergList1 = new LinkedList<>(Arrays.asList(Allergen.Erdnuss, Allergen.Haselnuss));
        KremkuchenImpl kuch1 = new KremkuchenImpl(herst1, allergList1, 300, dur1, new BigDecimal(500));

        Automat auto = new Automat(20);
        AutomatAllergenObserver obs = new AutomatAllergenObserver(auto);

        final ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos1));

        try {
            auto.addHersteller(herst1);
            auto.addKuchen(kuch1);
        } catch (AlreadyExistsException | FullAutomatException e) {
            fail();
        } finally {
            System.setOut(System.out);
        }
        String testString = "Die Allergene im Automat haben sich verändert" + System.lineSeparator();

        Assertions.assertEquals(testString , bos1.toString());
    }
}
