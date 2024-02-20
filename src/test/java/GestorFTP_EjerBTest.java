import com.mycompany.gestorcopiasseguridad.GestorFTP_EjerB;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.*;

public class GestorFTP_EjerBTest {

    private GestorFTP_EjerB gestorFTP;
    private FTPClient ftpClient;

    @Before
    public void setUp() {
        gestorFTP = new GestorFTP_EjerB();
        ftpClient = mock(FTPClient.class);
        gestorFTP.setFtpClient(ftpClient);
    }

    @After
    public void tearDown() {
        gestorFTP = null;
        ftpClient = null;
    }

    @Test
    public void testConectarFTP() throws IOException {
        gestorFTP.conectarFTP();
        verify(ftpClient).connect(anyString());
        verify(ftpClient).login(anyString(), anyString());
        verify(ftpClient).enterLocalPassiveMode();
    }

    @Test
    public void testBorrarArchivo() throws IOException {
        String remotePath = "ruta/remota/archivo.txt";

        // Configuramos comportamiento esperado de FTPClient
        when(ftpClient.deleteFile(remotePath)).thenReturn(true); // Simulamos que se elimina el archivo correctamente

        gestorFTP.borrarArchivo(remotePath);

        // Verificamos que se llame a deleteFile() para borrar el archivo
        verify(ftpClient).deleteFile(remotePath);
    }

    @Test
    public void testBorrarDirectorio() throws IOException {
        String remotePath = "ruta/remota/carpeta";

        // Configuramos comportamiento esperado de FTPClient
        when(ftpClient.deleteFile(remotePath)).thenReturn(false); // Simulamos que no se puede borrar el archivo
        when(ftpClient.removeDirectory(remotePath)).thenReturn(true); // Simulamos que se elimina la carpeta correctamente

        gestorFTP.borrarArchivo(remotePath);

        // Verificamos que se llame a removeDirectory() para borrar la carpeta
        verify(ftpClient).removeDirectory(remotePath);
    }
}
