package com.espe.pageimage.service;
import com.espe.pageimage.model.Image;
import com.espe.pageimage.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Value("${image.storage.path}")
    private String storagePath;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image uploadImage(MultipartFile file) throws IOException {
        if (!isValidImage(file)) {
            throw new IllegalArgumentException("Archivo no válido");
        }
        // Guardar archivo en el sistema
        // Crear el directorio si no existe
        String storagePath1 = "D:/ESCRITORIO/septimo/softwareseguro/parcial2/frontend/image-app/src/images"; // Cambia esta ruta según sea necesario
        File destinationDir = new File(storagePath1);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs(); // Crea el directorio si no existe
        }

        // Guardar archivo en el sistema
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(destinationDir, filename);
        file.transferTo(destination);

        // Guardar en la base de datos
        Image image = new Image();
        image.setFilename(filename);
        image.setStatus("pending");
        return imageRepository.save(image);
    }

    public List<Image> getPendingImages() {
        return imageRepository.findByStatus("pending");
        //return null;
    }

    public void approveImage(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        image.setStatus("approved");
        imageRepository.save(image);
    }

    public void rejectImage(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        image.setStatus("rejected");
        imageRepository.save(image);
    }
    public boolean isValidImage(MultipartFile file) {
        // Verificar que el archivo no esté vacío
        if (file.isEmpty()) {
            return false;
        }
        // Verificar el tipo de contenido
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            return false; // No es un tipo de imagen
        }
        // Verificar la extensión del archivo
        String filename = file.getOriginalFilename();
        if (filename != null && (filename.endsWith(".bmp"))) {
            return false; // Prohibir archivos BMP
        }

        // Verificar el tamaño del archivo (opcional)
        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            return false; // El archivo es demasiado grande
        }

        // Verificar que el archivo sea una imagen válida
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            return img != null; // Si img es null, no es una imagen válida
        } catch (IOException e) {
            return false; // Error al leer el archivo
        }
        // Verificación de archivos ocultos
        // Aquí puedes implementar una lógica para detectar archivos ocultos
        // Por ejemplo, puedes verificar el contenido del archivo en bytes
        /*try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = new byte[(int) file.getSize()];
            inputStream.read(bytes);

            // Buscar patrones que indiquen archivos ocultos (esto es solo un ejemplo)
            String fileContent = new String(bytes);
            if (fileContent.contains("hidden.txt")) { // Cambia esto según tus necesidades
                return false; // Prohibir si se detecta un archivo oculto
            }
        } catch (IOException e) {
            return false; // Error al leer el archivo
        }*/
    }
}
