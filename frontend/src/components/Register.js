import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/api";

const Register = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    role: "ROLE_USER",
  });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(formData);
      alert("Đăng ký thành công!");
      navigate("/auth/login");
    } catch (error) {
      console.error("Lỗi đăng ký:", error);
      alert("Đăng ký thất bại! Vui lòng thử lại.");
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url("/image/dangki.jpg")`, // 👉 dùng cùng hình nền như login
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
          Đăng ký
        </h2>

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "18px" }}>
          <input
            type="text"
            placeholder="Tên đăng nhập"
            value={formData.username}
            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
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
            placeholder="Mật khẩu"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
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
              backgroundColor: "#e91e63",
              color: "white",
              border: "none",
              padding: "12px",
              borderRadius: "10px",
              fontWeight: "bold",
              fontSize: "16px",
              cursor: "pointer",
              transition: "background 0.3s ease",
              width: "100%", // full width như ô input
            }}
            onMouseOver={(e) => (e.target.style.backgroundColor = "#e91e63")}
            onMouseOut={(e) => (e.target.style.backgroundColor = "#e91e63")}
          >
            Đăng ký
          </button>
        </form>

        <p style={{ marginTop: "20px", textAlign: "center", fontSize: "14px" }}>
          Đã có tài khoản?{" "}
          <span
            style={{
              color: "#ffd700",
              cursor: "pointer",
              fontWeight: "bold",
              textDecoration: "underline",
            }}
            onClick={() => navigate("/auth/login")}
          >
            Đăng nhập
          </span>
        </p>
      </div>
    </div>
  );
};

export default Register;
