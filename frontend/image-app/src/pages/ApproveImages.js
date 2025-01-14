import React, { useEffect, useState } from "react";
import axios from "axios";

const ApproveImages = () => {
  const [images, setImages] = useState([]);

  useEffect(() => {
    const fetchPendingImages = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/images/pending", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        setImages(response.data);
      } catch (error) {
        console.error("Error fetching pending images", error);
      }
    };

    fetchPendingImages();
  }, []);

  const handleApprove = async (id) => {
    try {
      await axios.post(`http://localhost:8080/api/images/approve/${id}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setImages(images.filter((image) => image.id !== id));
    } catch (error) {
      console.error("Error approving image", error);
    }
  };

  const handleReject = async (id) => {
    try {
      await axios.post(`http://localhost:8080/api/images/reject/${id}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setImages(images.filter((image) => image.id !== id));
    } catch (error) {
      console.error("Error rejecting image", error);
    }
  };

  const getImagePath = (filename) => {
    return require(`../images/${filename}`);
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center">Imágenes Pendientes de Aprobación</h1>
      <div className="mt-4">
        {images.length === 0 ? (
          <p>No hay imágenes pendientes.</p>
        ) : (
          images.map((image) => (
            <div key={image.id} className="card mb-3">
              <img 
                src={getImagePath(image.filename)} // Usando la función para obtener la ruta
                alt={image.filename} 
                className="card-img-top" 
                style={{ width: '200px', height: '150px', objectFit: 'cover' }} 
              />
              <div className="card-body">
                <h5 className="card-title">{image.filename}</h5>
                <button onClick={() => handleApprove(image.id)} className="btn btn-success mr-2">
                  Aprobar
                </button>
                <button onClick={() => handleReject(image.id)} className="btn btn-danger">
                  Rechazar
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ApproveImages;
