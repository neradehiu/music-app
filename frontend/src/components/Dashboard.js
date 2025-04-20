import React from "react";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.dispatchEvent(new Event("storage"));
    navigate("/auth/login");
  };

  return (
    <div
      style={{
        height: "100vh",
        backgroundImage: "url('/image/nenmayms.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        position: "relative",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        color: "#fff",
        textShadow: "2px 2px 4px rgba(0,0,0,0.4)",
        padding: "20px",
      }}
    >
      {/* Nút Logout */}
      <div style={{ position: "absolute", top: "20px", right: "20px" }}>
        <button
          onClick={handleLogout}
          style={{
            padding: "10px 24px",
            fontSize: "16px",
            background: "linear-gradient(45deg, #d63384, #6f42c1)",
            color: "#fff",
            border: "none",
            borderRadius: "999px",
            cursor: "pointer",
            fontWeight: "bold",
            boxShadow: "0 6px 12px rgba(0,0,0,0.2)",
            transition: "all 0.3s ease",
          }}
          onMouseEnter={(e) =>
            (e.target.style.background = " #ff6ec4")
          }
          onMouseLeave={(e) =>
            (e.target.style.background = "linear-gradient(45deg, #d63384, #6f42c1)")
          }
        >
          LOGOUT
        </button>
      </div>

      {/* Tiêu đề chính */}
      <div style={{ marginBottom: "20px", marginTop: "-60px", textAlign: "center" }}>
        <h1 style={{ fontSize: "44px", fontWeight: "bold" }}>WELCOME TO</h1>
        <h2 style={{ fontSize: "26px", letterSpacing: "2px" }}>WEB MUSIC</h2>
      </div>

      {/* Logo */}
      <div
        style={{
          marginBottom: "30px",
          cursor: "pointer",
          transition: "transform 0.3s ease",
        }}
        onClick={() => navigate("/musiclist")}
        onMouseEnter={(e) => (e.currentTarget.style.transform = "scale(1.05)")}
        onMouseLeave={(e) => (e.currentTarget.style.transform = "scale(1)")}
      >
        <img
          src="/image/app_logo.png"
          alt="Music Icon"
          style={{
            width: "200px",
            height: "200px",
            objectFit: "cover",
          }}
        />
      </div>

      {/* Nút Play Music */}
      <button
        onClick={() => navigate("/musiclist")}
        style={{
          padding: "14px 32px",
          fontSize: "16px",
          background: "linear-gradient(45deg, #d63384, #6f42c1)",
          color: "#fff",
          border: "none",
          borderRadius: "999px",
          fontWeight: "bold",
          cursor: "pointer",
          boxShadow: "0 6px 12px rgba(0,0,0,0.25)",
          transition: "all 0.3s ease",
          textTransform: "uppercase",
          letterSpacing: "1px",
        }}
        onMouseEnter={(e) =>
          (e.target.style.background = " #ff6ec4")
        }
        onMouseLeave={(e) =>
          (e.target.style.background = "linear-gradient(45deg, #d63384, #6f42c1)")
        }
      >
        ▶ PLAY MUSIC
      </button>
    </div>
  );
};

export default Dashboard;
