package com.espe.pageimage.service;

import com.espe.pageimage.controller.ImageIntegrityEvaluator;
import com.espe.pageimage.model.Image;
import com.espe.pageimage.repository.ImageRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
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
    //metodo opcional
    public void processImage(MultipartFile file) {
        ImageIntegrityEvaluator evaluator = new ImageIntegrityEvaluator();

        // Llamar al método para evaluar la imagen
        ImageIntegrityEvaluator.Report report = evaluator.evaluateImage(file);

        // Obtener el overall integrity
        report.toString();

        // Imprimir el resultado
        System.out.println("Overall Integrity: "+report.toString());
    }

    public Image uploadImage(MultipartFile file) throws IOException {
        //processImage(file);
        /*if(verifyData( file)){
            System.out.println("openpuff funcionando.");
        }*/
        // Verificar si hay mensajes ocultos
        if (detectHiddenMessages(file)) {
            System.out.println("Se detectaron mensajes ocultos en la imagen.");
        } else {
            System.out.println("No se detectaron mensajes ocultos.");
        }
        if (!isValidImage(file)) {
            throw new IllegalArgumentException("El archivo contiene elementos no permitidos o es inválido.");
        }

        // Crear el directorio si no existe
        String storagePath1 = "D:/ESCRITORIO/septimo/softwareseguro/parcial2/frontend/image-app/src/images"; // Cambia esta ruta según sea necesario
        File destinationDir = new File(storagePath1);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }


        // Guardar archivo en el sistema
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(destinationDir, filename);
        // Limpiar la imagen antes de guardarla
        BufferedImage cleanedImage = cleanImage(file);
        ImageIO.write(cleanedImage, "jpg", destination); // Guarda la imagen limpia
        //file.transferTo(destination);

        // Guardar en la base de datos
        Image image = new Image();
        image.setFilename(filename);
        image.setStatus("pending");
        return imageRepository.save(image);
    }

    public List<Image> getPendingImages() {
        return imageRepository.findByStatus("pending");
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
            System.out.println("Denegado: El archivo está vacío.");
            return false;
        }
        /*if(hasEmbeddedData(file)){
            System.out.println("Denegado: El archivo tiene datos embebidos");
            return false;

        }*/

        // Verificar el tipo de contenido
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            System.out.println("Denegado: Tipo de contenido no válido - " + contentType);
            return false; // No es un tipo de imagen
        }

        // Verificar la extensión del archivo utilizando una lista blanca
        String filename = file.getOriginalFilename();
        if (filename == null || !isAllowedExtension(filename)) {
            System.out.println("Denegado: Extensión no permitida - " + filename);
            return false; // Extensión no permitida
        }

        // Verificar el tamaño del archivo
        long maxSize = 5 * 1024 * 1024; // 5 MB
        if (file.getSize() > maxSize) {
            System.out.println("Denegado: El archivo es demasiado grande - " + file.getSize() + " bytes");
            return false; // El archivo es demasiado grande
        }

        // Validar formato del archivo con Apache Tika
        try (InputStream inputStream = file.getInputStream()) {
            String detectedType = new Tika().detect(inputStream);
            if (!detectedType.startsWith("image/")) {
                System.out.println("Denegado: Archivo no es una imagen válida según Tika - " + detectedType);
                return false; // No es un archivo de imagen válido
            }
        } catch (IOException e) {
            System.out.println("Denegado: Error al analizar el archivo - " + e.getMessage());
            return false; // Error al analizar el archivo
        }

        // Verificar contenido adicional sospechoso en la imagen
        /*if (hasSuspiciousSegments(file)) {
            System.out.println("Denegado: Se detectaron segmentos sospechosos en el archivo.");
            return false; // Se detectaron segmentos sospechosos
        }*/

        // Verificar estructura interna de la imagen
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                System.out.println("Denegado: La imagen no es válida.");
                return false; // No es una imagen válida
            }

            // Verificar LSB
            /*if (hasModifiedLSB(img)) {
                System.out.println("Denegado: La imagen tiene LSB modificados.");
                return false; // La imagen tiene LSB modificados
            }*/

            long estimatedSize = estimateImageSize(img, contentType);
            if (file.getSize() > estimatedSize + (1024 * 100)) { // Permitir un margen de 100 KB
                System.out.println("Denegado: Tamaño del archivo no coincide con su contenido.");
                return false; // Tamaño del archivo no coincide con su contenido
            }
        } catch (IOException e) {
            System.out.println("Denegado: Error al procesar la imagen - " + e.getMessage());
            return false; // Error al procesar la imagen
        }

        return true; // El archivo pasó todas las validaciones
    }


    private boolean hasSuspiciousSegments(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            String hex = bytesToHex(bytes);

            // Buscar patrones específicos de archivos embebidos como .rar o .zip
            if (hex.contains("504B0304") || hex.contains("52617221")) { // PK.. (ZIP) o Rar!
                return true;
            }
        } catch (IOException e) {
            return true; // Asumir sospechoso si hay un error
        }
        return false;
    }

    private boolean hasModifiedLSB(BufferedImage img) {

        int width = img.getWidth();
        int height = img.getHeight();
        int lsbCount = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(x, y);
                // Extraer el bit menos significativo
                int lsb = rgb & 1;
                // Contar la cantidad de LSB que son 1
                if (lsb == 1) {
                    lsbCount++;
                }
            }
        }

        // Definir un umbral para considerar que la imagen tiene esteganografía
        return lsbCount > (width * height) * 0.03; // Por ejemplo, más del 5% de los LSB modificados
    }

    private long estimateImageSize(BufferedImage img, String contentType) {
        int bytesPerPixel = switch (contentType) {
            case "image/jpeg" -> 3; // JPEG usa 3 bytes por píxel (aproximadamente)
            case "image/png" -> 4; // PNG usa 4 bytes por píxel
            default -> 3; // Asumir 3 por defecto
        };
        return (long) img.getWidth() * img.getHeight() * bytesPerPixel;
    }

    private boolean isAllowedExtension(String filename) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String extension : allowedExtensions) {
            if (filename.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public boolean hasEmbeddedData(MultipartFile file) {
        try {
            // Obtiene el tipo de imagen a partir de la extensión del archivo
            String formatName = getFormatName(file.getOriginalFilename());
            if (formatName == null) {
                return false; // Formato no soportado
            }
            System.out.println(formatName);

            // Crea un ImageInputStream para leer los metadatos
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(file.getInputStream());
            ImageReader reader = ImageIO.getImageReadersByFormatName(formatName).next();
            reader.setInput(imageInputStream, true);

            // Obtiene los metadatos de la imagen
            IIOMetadata metadata = reader.getImageMetadata(0);
            System.out.println(metadata);
            if (metadata != null) {
                // Aquí puedes verificar los metadatos específicos que te interesen
                return true; // Si hay metadatos, retorna true
            }
        } catch (IOException e) {
            e.printStackTrace(); // Manejo de excepciones
        }
        return false; // Si no hay metadatos, retorna false
    }

    // Método auxiliar para obtener el formato de la imagen
    private String getFormatName(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "jpeg";
        } else if (filename.endsWith(".png")) {
            return "png";
        } else if (filename.endsWith(".gif")) {
            return "gif";
        }
        // Agrega más formatos según sea necesario
        return null; // Formato no soportado
    }
    private BufferedImage cleanImage(MultipartFile file) throws IOException {
        // Leer la imagen original
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen.");
        }

        // Crear un nuevo BufferedImage con los mismos parámetros
        BufferedImage cleanedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        cleanedImage.getGraphics().drawImage(originalImage, 0, 0, null);

        return cleanedImage; // Retorna la imagen limpia
        //return originalImage;
    }
    public boolean verifyData(MultipartFile file) {
        File tempFile = null;
        try {
            // Crear un archivo temporal
            tempFile = File.createTempFile("coverFile", ".tmp");
            file.transferTo(tempFile); // Transferir el contenido del MultipartFile al archivo temporal

            // Ruta al ejecutable de OpenPuff
            String openPuffPath = "D:/ESCRITORIO/septimo/softwareseguro/parcial2/OpenPuff_release/OpenPuff.exe";

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
    public boolean detectHiddenMessages(MultipartFile file) {
        try {
            // Leer la imagen del archivo MultipartFile
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new IllegalArgumentException("La imagen no es válida.");
            }

            // Lógica para detectar mensajes ocultos usando LSB (Least Significant Bit)
            int width = img.getWidth();
            int height = img.getHeight();
            int lsbCount = 0;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = img.getRGB(x, y);
                    // Extraer el bit menos significativo
                    int lsb = rgb & 1;
                    // Contar la cantidad de LSB que son 1
                    if (lsb == 1) {
                        lsbCount++;
                    }
                }
            }

            // Definir un umbral para considerar que la imagen tiene esteganografía
            return lsbCount > (width * height) * 0.03; // Por ejemplo, más del 3% de los LSB modificados
        } catch (IOException e) {
            System.out.println("Error al procesar la imagen: " + e.getMessage());
            return false; // Retorna false en caso de error
        }
    }

}
