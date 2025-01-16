package com.espe.pageimage.controller;

import com.espe.pageimage.model.Image;
import com.espe.pageimage.service.ImageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final String imageStoragePath = "D://imagenesnuevas"; // Ruta donde se almacenan las imágenes

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public Image uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }

    @GetMapping("/pending")
    public List<Image> getPendingImages() {
        return imageService.getPendingImages();
    }

    @GetMapping("/pending1")
    public String getPendingImages2() {
        return "hola mundo";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public void approveImage(@PathVariable Long id) {
        imageService.approveImage(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reject/{id}")
    public void rejectImage(@PathVariable Long id) {
        imageService.rejectImage(id);
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<String> getImage(@PathVariable String filename) {
        // Ruta completa de la imagen
        String imagePath = "D:\\imagenesnuevas\\" + filename;
        File file = new File(imagePath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Devolver la ruta de la imagen
        return ResponseEntity.ok(imagePath);
    }

}
