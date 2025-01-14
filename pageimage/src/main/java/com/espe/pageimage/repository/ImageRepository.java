package com.espe.pageimage.repository;

import com.espe.pageimage.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByStatus(String status);
}
