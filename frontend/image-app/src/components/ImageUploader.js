import React, { useState } from "react";
import { uploadImage } from "../services/apiService";

const ImageUploader = () => {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage("Selecciona una imagen primero");
      return;
    }

    try {
      await uploadImage(file);
      setMessage("Imagen subida exitosamente");
    } catch (error) {
      setMessage("Error al subir la imagen");
    }
  };

  return (
    <div>
      <h2>Subir Imagen</h2>
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleUpload}>Subir</button>
      {message && <p>{message}</p>}
    </div>
  );
};

export default ImageUploader;
