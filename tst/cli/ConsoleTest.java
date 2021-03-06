package cli;

import events.GetEventHandler;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


public class ConsoleTest {

    //TODO fix these tests
    @Test
    public void executeCommandInsertValid(){
        try {
            Console console = new Console();
            InputReader reader = mock(InputReader.class);
            console.setInputReader(reader);

            final String c = ":c";
            System.setIn(new ByteArrayInputStream(c.getBytes()));
            console.read();

            final String Gunther = "Gunther";
            System.setIn( new ByteArrayInputStream(Gunther.getBytes()));

            console.read();

            verify(reader).readInsert(Gunther);

        } finally {
            System.setIn(System.in);
        }
    }

    @Test
    public void executeCommandDeleteValid(){
        try {
            Console console = new Console();
            InputReader reader = mock(InputReader.class);
            console.setInputReader(reader);

            final String d = ":d";
            System.setIn(new ByteArrayInputStream(d.getBytes()));
            console.read();

            System.setIn( new ByteArrayInputStream("2".getBytes()));

            console.read();

            verify(reader).readDelete("2");

        }  finally {
            System.setIn(System.in);
        }
    }

    @Test
    public void executeCommandDisplayValid(){
        try {
            Console console = new Console();
            InputReader reader = mock(InputReader.class);
            console.setInputReader(reader);

            final String r = ":r";
            System.setIn(new ByteArrayInputStream(r.getBytes()));
            console.read();

            System.setIn( new ByteArrayInputStream("hersteller".getBytes()));
            console.read();

            verify(reader).readDisplay("hersteller");
            System.setIn(System.in);
        }  finally {
            System.setIn(System.in);
        }
    }

    @Test
    public void executeCommandChangeValid(){
        try {
            Console console = new Console();
            InputReader reader = mock(InputReader.class);
            console.setInputReader(reader);

            final String u = ":u";
            System.setIn(new ByteArrayInputStream(u.getBytes()));
            console.read();

            System.setIn( new ByteArrayInputStream("5".getBytes()));
            console.read();

            verify(reader).readChange("5");
            System.setIn(System.in);
        }  finally {
            System.setIn(System.in);
        }
    }

    @Test
    public void executeCommandPersistenceValid(){
        try {
            Console console = new Console();
            InputReader reader = mock(InputReader.class);
            console.setInputReader(reader);

            final String p = ":p";
            System.setIn(new ByteArrayInputStream(p.getBytes()));
            console.read();

            System.setIn( new ByteArrayInputStream("saveJOS".getBytes()));
            console.read();

            verify(reader).readPersistence("saveJOS");
            System.setIn(System.in);
        }  finally {
            System.setIn(System.in);
        }
    }
}
