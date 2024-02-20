package com.mycompany.gestorcopiasseguridad;

import java.io.IOException;

public class Compresor {

    public static void main(String[] args) {
        String nombreCarpeta = args[0];
        String nombreArchivoZip = args[1];
        try {
            comprimirCarpeta(nombreCarpeta, nombreArchivoZip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void comprimirCarpeta(String nombreCarpeta, String nombreArchivoZip) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("7z", "a", nombreArchivoZip, nombreCarpeta);
        System.out.println("      COMPRESOR - Comprimiendo carpeta "+nombreCarpeta+" ...");
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("      COMPRESOR - Carpeta comprimida: "+nombreArchivoZip);
    }
}
