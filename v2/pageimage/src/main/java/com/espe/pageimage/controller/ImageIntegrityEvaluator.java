package com.espe.pageimage.controller;

//import org.apache.commons.imaging.Imaging;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageIntegrityEvaluator {

    public Report evaluateImage(MultipartFile file) {
        try {
            // Convertir MultipartFile a BufferedImage
            BufferedImage image = ImageIO.read(file.getInputStream());

            // Calcular la integridad media
            double meanIntegrity = calculateMeanIntegrity(image);

            // Calcular la puntuación de calidad de la imagen
            double qualityScore = getQualityScore(image);

            // Calcular la integridad general
            double overallIntegrity = calculateOverallIntegrity(meanIntegrity, qualityScore, file.getSize());

            return new Report(file.getOriginalFilename(), meanIntegrity, overallIntegrity);
        } catch (IOException e) {
            e.printStackTrace();
            return new Report(file.getOriginalFilename(), 0.0, 0.0);
        }
    }

    private double calculateMeanIntegrity(BufferedImage image) {
        // Aquí implementa la lógica para detectar marcas de agua
        return detectWatermark(image); // Simulación de detección de marcas de agua
    }

    private double detectWatermark(BufferedImage image) {
        // Implementa la lógica real de detección de marcas de agua
        // Retorna un valor entre 0 y 100 basado en la detección
        return 70.0; // Valor simulado; reemplaza con lógica real
    }

    private double getQualityScore(BufferedImage image) {
        double brightness = calculateBrightness(image);
        double contrast = calculateContrast(image);
        return (brightness * 0.5) + (contrast * 0.5);
    }

    private double calculateBrightness(BufferedImage image) {
        long totalBrightness = 0;
        int pixelCount = image.getWidth() * image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                totalBrightness += (rgb >> 16 & 0xff) + (rgb >> 8 & 0xff) + (rgb & 0xff);
            }
        }
        return (double) totalBrightness / (3 * pixelCount);
    }

    private double calculateContrast(BufferedImage image) {
        double minBrightness = Double.MAX_VALUE;
        double maxBrightness = Double.MIN_VALUE;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                double brightness = (rgb >> 16 & 0xff) + (rgb >> 8 & 0xff) + (rgb & 0xff) / 3.0;

                if (brightness < minBrightness) {
                    minBrightness = brightness;
                }
                if (brightness > maxBrightness) {
                    maxBrightness = brightness;
                }
            }
        }
        return maxBrightness - minBrightness;
    }

    private double calculateOverallIntegrity(double meanIntegrity, double qualityScore, long fileSize) {
        double sizeImpact = calculateFileSizeImpact(fileSize);
        return (meanIntegrity * 0.5) + (qualityScore * 0.3) + (sizeImpact * 0.2);
    }

    private double calculateFileSizeImpact(long fileSize) {
        long fileSizeKB = fileSize / 1024;
        if (fileSizeKB < 50) {
            return 20; // Pequeño tamaño
        } else if (fileSizeKB < 200) {
            return 50; // Tamaño medio
        } else {
            return 80; // Tamaño grande
        }
    }

    // Clase interna para el reporte
    public class Report {
        private String carrierName;
        private double meanIntegrity;
        private double overallIntegrity;

        public Report(String carrierName, double meanIntegrity, double overallIntegrity) {
            this.carrierName = carrierName;
            this.meanIntegrity = meanIntegrity;
            this.overallIntegrity = overallIntegrity;
        }

        @Override
        public String toString() {
            return "*** Begin of Report ***\n" +
                    "Carrier Name <- " + carrierName + "\n" +
                    "Mean Integrity <- " + meanIntegrity + "%\n" +
                    "Overall Integrity <- " + overallIntegrity + "%\n" +
                    "Mark <- " + detectWatermark(null) + "\n"; // Lógica de marca debe ser implementada
        }
    }
}
