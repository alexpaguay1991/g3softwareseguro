package com.espe.pageimage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
@Service
public class OpenPuffService {

    public boolean verifyData(MultipartFile file) {
        File tempFile = null;
        try {
            // Crear un archivo temporal
            tempFile = File.createTempFile("coverFile", ".tmp");
            file.transferTo(tempFile); // Transferir el contenido del MultipartFile al archivo temporal

            // Ruta al ejecutable de OpenPuff
            String openPuffPath = "D:/ESCRITORIO/septimo/softwareseguro/parcial2/OpenPuff.exe";

            // Comando para verificar datos
            ProcessBuilder processBuilder = new ProcessBuilder(openPuffPath, tempFile.getAbsolutePath(), "-checkmark");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            return exitCode == 0; // Retorna true si la verificación fue exitosa
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false; // Retorna false en caso de error
        } finally {
            // Eliminar el archivo temporal
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
