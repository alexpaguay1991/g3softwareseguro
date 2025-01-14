import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = ({ setIsAuthenticated }) => {
  const [username, setUsername] = useState(""); // Cambiado de email a username
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async () => {
    console.log("Esta hasta aqui");
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", { 
        username, 
        password 
      });
  
      // Imprimir el token en la consola
      console.log("Token de acceso:", response.data.tokenDeAcceso);
  
      // Guardar el token en localStorage
      localStorage.setItem("token", response.data.tokenDeAcceso);
      
      setIsAuthenticated(true);
      navigate("/");
    } catch (error) {
      alert("Credenciales incorrectas.");
    }
  };
  

  return (
    <div className="container mt-5">
      <h1 className="text-center">Iniciar Sesión</h1>
      <div className="form-group">
        <label>Nombre de Usuarios</label> {/* Cambiado de Correo Electrónico a Nombre de Usuario */}
        <input
          type="text" // Cambiado de email a text
          className="form-control"
          value={username} // Cambiado de email a username
          onChange={(e) => setUsername(e.target.value)} // Cambiado de setEmail a setUsername
        />
      </div>
      <div className="form-group">
        <label>Contraseña</label>
        <input
          type="password"
          className="form-control"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <button onClick={handleLogin} className="btn btn-primary mt-3">
        Ingresar
      </button>
    </div>
  );
};

export default Login;
