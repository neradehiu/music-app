import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Login = ({ setToken, setRole }) => {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage);
      }

      let token;
      let role = "USER";

      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const data = await response.json();
        token = data.token;
        role = data.role?.trim().toUpperCase() || role;
      } else {
        token = await response.text();
      }

      if (!token) throw new Error("Không nhận được token từ server!");

      localStorage.setItem("token", token);
      localStorage.setItem("role", role);

      setToken(token);
      setRole(role);
      navigate("/dashboard");
    } catch (error) {
      console.error("Lỗi đăng nhập:", error);
      alert(`Lỗi: ${error.message}`);
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url("/image/dangnhap.jpg")`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
      }}
    >
      <div
        style={{
          backgroundColor: "rgba(0, 0, 0, 0.6)",
          padding: "40px 30px",
          borderRadius: "20px",
          boxShadow: "0 8px 24px rgba(0, 0, 0, 0.3)",
          color: "white",
          width: "100%",
          maxWidth: "400px",
        }}
      >
        <h2
          style={{
            textAlign: "center",
            marginBottom: "30px",
            fontSize: "26px",
            fontWeight: "bold",
          }}
        >
          Đăng nhập
        </h2>

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "18px" }}>
          <input
            type="text"
            name="username"
            placeholder="Tên đăng nhập"
            onChange={handleChange}
            required
            style={{
              padding: "12px",
              borderRadius: "10px",
              border: "none",
              fontSize: "16px",
              backgroundColor: "#f0f4f8",
              color: "#333",
            }}
          />
          <input
            type="password"
            name="password"
            placeholder="Mật khẩu"
            onChange={handleChange}
            required
            style={{
              padding: "12px",
              borderRadius: "10px",
              border: "none",
              fontSize: "16px",
              backgroundColor: "#f0f4f8",
              color: "#333",
            }}
          />
          <button
            type="submit"
            style={{
              backgroundColor: "#007bff",
              color: "white",
              border: "none",
              padding: "12px",
              borderRadius: "20px",
              fontWeight: "bold",
              fontSize: "16px",
              cursor: "pointer",
              transition: "background 0.3s ease",
              width: "100%",
            }}
            onMouseOver={(e) => (e.target.style.backgroundColor = "#0056b3")}
            onMouseOut={(e) => (e.target.style.backgroundColor = "#007bff")}
          >
            Đăng nhập
          </button>
        </form>

        <p style={{ marginTop: "20px", textAlign: "center", fontSize: "14px" }}>
          Chưa có tài khoản?{" "}
          <span
            style={{
              color: "#ffd700",
              cursor: "pointer",
              fontWeight: "bold",
              textDecoration: "underline",
            }}
            onClick={() => navigate("/auth/register")}
          >
            Đăng ký ngay
          </span>
        </p>
      </div>
    </div>
  );
};

export default Login;
