import React, { useState } from "react";
import axios from "axios";

const Home = () => {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      alert("Por favor selecciona un archivo.");
      return;
    }
    const formData = new FormData();
    formData.append("image", selectedFile); // Cambiado de "file" a "image"
  
    try {
      const response = await axios.post("http://localhost:8080/api/images/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      alert("Imagen subida exitosamente.");
    } catch (error) {
      console.error("Error subiendo la imagen", error);
      alert("Ocurrió un error."+error);
    }
  };
  

  return (
    <div className="container mt-5">
      <h1 className="text-center">Bienvenido a ImageApp</h1>
      <div className="mt-4">
        <input type="file" onChange={handleFileChange} className="form-control" />
        <button onClick={handleUpload} className="btn btn-primary mt-2">
          Subir Imagen
        </button>
      </div>
    </div>
  );
};

export default Home;
