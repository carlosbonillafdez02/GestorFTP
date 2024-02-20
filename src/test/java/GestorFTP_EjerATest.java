import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;

import com.mycompany.gestorcopiasseguridad.GestorFTP_EjerA;

public class GestorFTP_EjerATest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testMain() {
        // Este test prueba la ejecución del método main con una entrada simulada.

        // Establecer la entrada simulada
        ByteArrayInputStream in = new ByteArrayInputStream("carpeta1\nFIN\n".getBytes());
        System.setIn(in);

        // Ejecutar el método main
        GestorFTP_EjerA.main(new String[]{});

        // Comprobar la salida estándar
        assertTrue(outContent.toString().contains("Introduce el nombre de la carpeta a subir al servidor FTP"));

        // Restaurar la entrada estándar
        System.setIn(System.in);
    }
}
