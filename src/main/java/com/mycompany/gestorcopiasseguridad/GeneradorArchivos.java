package com.mycompany.gestorcopiasseguridad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneradorArchivos {
    public static void main(String[] args) {
        String folderPath = "C:\\Users\\carlo\\Desktop\\FTP local"; // Ruta de la carpeta donde se generarán los archivos
        int numberOfFiles = 100; // Cantidad de archivos a generar

        generateTextFiles(folderPath, numberOfFiles);
    }

    public static void generateTextFiles(String folderPath, int numberOfFiles) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Crea la carpeta si no existe
            }

            for (int i = 1; i <= numberOfFiles; i++) {
                String fileName = "archivo" + i + ".txt"; // Nombre del archivo
                String filePath = folderPath + "/" + fileName; // Ruta completa del archivo
                String fileContent = "Contenido del archivo " + i; // Contenido del archivo

                // Escribir el contenido en el archivo
                try (FileWriter writer = new FileWriter(filePath)) {
                    writer.write(fileContent);
                }

                System.out.println("Archivo generado: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
