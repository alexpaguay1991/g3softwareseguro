import axios from "axios";

const API_URL = "http://localhost:8080/api/images";

export const uploadImage = async (imageFile) => {
  const formData = new FormData();
  formData.append("file", imageFile);

  const response = await axios.post(`${API_URL}/upload`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response.data;
};

export const fetchImages = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};
