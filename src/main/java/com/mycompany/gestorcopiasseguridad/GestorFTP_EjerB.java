package com.mycompany.gestorcopiasseguridad;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GestorFTP_EjerB {

    private static final String CARPETA_LOCAL = "C:\\Users\\carlo\\Desktop\\FTP local";
    private static final String CARPETA_REMOTA = "/";
    private static final String SERVIDOR_FTP = "127.0.0.1";
    private static final String USUARIO_FTP = "carlos";
    private static final String CONTRASEÑA_FTP = "1234";
    private static final int TIEMPO_SINCRONIZACION = 1;

    private static FTPClient ftpClient = new FTPClient();

    public static void setFtpClient(FTPClient ftpClient) {
        GestorFTP_EjerB.ftpClient = ftpClient;
    }

    public static void main(String[] args) {
        System.out.println("Sincronizador de archivos. Las carpetas se sincronizarán cada " +TIEMPO_SINCRONIZACION+" segundos.");
        conectarFTP();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(GestorFTP_EjerB::sincronizarCarpetas, 0, TIEMPO_SINCRONIZACION, TimeUnit.SECONDS);
    }

    public static void conectarFTP() {
        try {
            ftpClient.connect(SERVIDOR_FTP);
            ftpClient.login(USUARIO_FTP, CONTRASEÑA_FTP);
            ftpClient.enterLocalPassiveMode();
            System.out.println("Conexión FTP establecida.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sincronizarCarpetas() {
        try {
            // Obtengo todos los archivos remotos y locales
            FTPFile[] archivosRemotos = ftpClient.listFiles(CARPETA_REMOTA);
            File carpetaLocal = new File(CARPETA_LOCAL);
            File[] archivosLocales = carpetaLocal.listFiles();

            // Comprueba todos los archivos locales. Si no existen en la carpeta remoto, crea los archivos en esta.
            // También modifica los archivos de remoto si la ultima modificacion en local es más reciente que la subida de estos en remoto.
            if (archivosLocales != null) {
                for (File archivoLocal : archivosLocales) {
                    boolean existeEnRemoto = false;
                    for (FTPFile remoteFile : archivosRemotos) {
                        if (archivoLocal.getName().equals(remoteFile.getName())) {
                            existeEnRemoto = true;
                            if (archivoLocal.lastModified() > remoteFile.getTimestamp().getTimeInMillis()) {
                                subirArchivo(archivoLocal);
                            }
                            break;
                        }
                    }
                    if (!existeEnRemoto) {
                        subirArchivo(archivoLocal);
                    }
                }
            }

            // Bucle para comprobar si existe un archivo en local. En caso de no existir, este se borra de la carpeta remoto
            for (FTPFile archivoRemoto : archivosRemotos) {
                boolean existeEnLocal = false;
                if (archivosLocales != null) {
                    for (File archivoLocal : archivosLocales) {
                        if (archivoLocal.getName().equals(archivoRemoto.getName())) {
                            existeEnLocal = true;
                            break;
                        }
                    }
                }
                if (!existeEnLocal) {
                    borrarArchivo(archivoRemoto.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Función para subir archivo a la carpeta remoto
    public static void subirArchivo(File archivoLocal) {
        try (FileInputStream fis = new FileInputStream(archivoLocal)) {
            String rutaRemota = CARPETA_REMOTA + "/" + archivoLocal.getName();
            if (ftpClient.storeFile(rutaRemota, fis)) {
                System.out.println("Archivo subido con éxito: " + archivoLocal.getName());
            } else {
                System.out.println("Error al subir el archivo: " + archivoLocal.getName());
            }
        } catch (IOException e) {
            // Manejar la excepción de manera controlada
            System.err.println("Error al subir el archivo: " + archivoLocal.getName());
            e.printStackTrace();
        }
    }


    // Función para borrar un archivo o carpeta de la carpeta remoto
    public static void borrarArchivo(String remotePath) throws IOException {
        try {
            if (ftpClient.deleteFile(remotePath)) {
                System.out.println("Archivo eliminado con éxito: " + remotePath);
            } else if (ftpClient.removeDirectory(remotePath)) {
                System.out.println("Carpeta eliminada con éxito: " + remotePath);
            } else {
                System.out.println("No se pudo eliminar el archivo o carpeta: " + remotePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
