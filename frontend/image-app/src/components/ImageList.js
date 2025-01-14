import React, { useState, useEffect } from "react";
import { fetchImages } from "../services/apiService";

const ImageList = () => {
  const [images, setImages] = useState([]);

  useEffect(() => {
    const loadImages = async () => {
      try {
        const data = await fetchImages();
        setImages(data);
      } catch (error) {
        console.error("Error al cargar las imágenes", error);
      }
    };
    loadImages();
  }, []);

  return (
    <div>
      <h2>Galería de Imágenes</h2>
      <div style={{ display: "flex", flexWrap: "wrap" }}>
        {images.map((url, index) => (
          <img
            key={index}
            src={url}
            alt={`imagen-${index}`}
            style={{ width: "150px", margin: "10px" }}
          />
        ))}
      </div>
    </div>
  );
};

export default ImageList;
