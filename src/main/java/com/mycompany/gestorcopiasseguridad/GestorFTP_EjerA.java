package com.mycompany.gestorcopiasseguridad;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class GestorFTP_EjerA {

    private static final String SERVIDOR_FTP = "127.0.0.1";
    private static final String USUARIO_FTP = "carlos";
    private static final String CONTRASEÑA_FTP = "1234";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String nombreCarpeta = "";

        // ArrayList que guardará los procesos de Compresión
        ArrayList<Process> procesos = new ArrayList<>();
        // ArrayList que guarda los nombres de los archivos .zip .
        // La posición del nombre del archivo zip siempre coincidirá con la posición del proceso al que pertenece
        ArrayList<String> nombresArchivosZip = new ArrayList<>();

        // Bucle para pedir carpetas para comprimir, hasta que el usuario introduzca "FIN"
        while(!nombreCarpeta.equals("FIN")) {
            try {
                System.out.println("Introduce el nombre de la carpeta a subir al servidor FTP (para finalizar programa escribe 'FIN'): ");
                nombreCarpeta = sc.nextLine();
                if(!nombreCarpeta.equals("FIN") && new File(nombreCarpeta).exists()) {
                    // El nombre del archivo zip contiene la fecha y la hora
                    String nombreArchivoZip = nombreCarpeta + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".zip";
                    nombresArchivosZip.add(nombreArchivoZip);
                    // Construimos el ProcessBuilder, pasando al proceso secundario el nombre de la carpeta que queremos comprimir y el nombre del zip que generará
                    String[] infoProceso = {"java", "-cp", "target/classes", "com.mycompany.gestorcopiasseguridad.Compresor", nombreCarpeta, nombreArchivoZip};
                    ProcessBuilder pBuilder = new ProcessBuilder(infoProceso);
                    // Inicio del proceso de compresión, lo añadimos al ArrayList de procesos
                    procesos.add(pBuilder.start());
                    // Hilo para leer la salida del proceso secundario mientras el programa principal sigue ejecutándose
                    Thread outputReader = new Thread(() -> {
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(procesos.get(procesos.size()-1).getInputStream()));
                            String linea;
                            while ((linea = br.readLine()) != null) {
                                System.out.println(linea);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    outputReader.start();

                    // Deja un tiempo de espera para que se comprima cualquier carpeta pequeña antes de volver a pedir otra.
                    // Si la carpeta es muy grande, el programa seguirá su ejecución mientras esta se comprime
                    Thread.sleep(2000);

                    // Compruebo los procesos que han finalizado para poder transferir el zip a la carpeta FTP.
                    for(int i = 0; i < procesos.size(); i++){
                        if(!procesos.get(i).isAlive()) {
                            transferirArchivoFTP(nombresArchivosZip.get(i), SERVIDOR_FTP, USUARIO_FTP, CONTRASEÑA_FTP);
                            procesos.remove(i);
                            new File(nombresArchivosZip.get(i)).delete();
                            nombresArchivosZip.remove(i);
                        } else {
                            System.out.println("    La carpeta "+nombresArchivosZip.get(i)+" sigue en proceso de compresion...");
                        }
                    }

                } else {
                    System.out.println("La carpeta no existe. Ruta incorrecta.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // Un pequeño retardo para que la excepción se imprima antes de volver a empezar el bucle
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Mato todos los procesos de compresión cuando el programa se termina
        for (int i = 0; i<procesos.size(); i++){
            procesos.get(i).destroyForcibly();
        }
    }

    public static void transferirArchivoFTP(String archivo, String servidor, String usuario, String contraseña) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(servidor);
            ftp.login(usuario, contraseña);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            FileInputStream archivoLocal = new FileInputStream(archivo);
            ftp.storeFile(new File(archivo).getName(), archivoLocal);
            archivoLocal.close();
            ftp.logout();
            System.out.println("        ¡"+archivo+" enviado exitosamente por FTP!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
