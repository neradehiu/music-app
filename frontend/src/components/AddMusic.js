import React, { useState } from "react";
import { uploadSong } from "../services/api";
import { useNavigate } from "react-router-dom";
import { FaHome, FaMusic, FaUser, FaTags, FaUpload } from "react-icons/fa";

export default function AddMusic() {
  const [songData, setSongData] = useState({
    title: "",
    artist: "",
    genre: "",
    file: null,
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    setSongData((prev) => ({
      ...prev,
      [name]: files ? files[0] : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("title", songData.title);
    formData.append("artist", songData.artist);
    formData.append("genre", songData.genre);
    formData.append("file", songData.file);

    try {
      await uploadSong(formData);
      alert("Upload thành công!");
      navigate("/musiclist");
    } catch (error) {
      console.error("Upload lỗi", error);
      alert("Có lỗi khi upload!");
    }
  };

  return (
    <div
      style={{
        backgroundImage: "url('/image/nenweb.jpeg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
        position: "relative",
      }}
    >
      {/* Nút Home ở góc phải trên */}
      <button
        onClick={() => navigate("/")}
        style={{
          position: "absolute",
          top: "20px",
          right: "20px",
          backgroundColor: "#2c1365",
          color: "white",
          border: "none",
          padding: "10px 20px",
          borderRadius: "10px",
          cursor: "pointer",
          display: "flex",
          alignItems: "center",
          gap: "8px",
          fontWeight: "bold",
          fontSize: "16px",
        }}
      >
        <FaHome />
        Home
      </button>

      {/* Form thêm bài hát */}
      <div
        style={{
          background: "rgba(44, 19, 101, 0.2)",
          backdropFilter: "blur(8px)",
          padding: "30px",
          borderRadius: "12px",
          width: "100%",
          maxWidth: "500px",
          boxShadow: "0 0 20px rgba(0,0,0,0.3)",
        }}
      >
        <h2 style={{ textAlign: "center", marginBottom: "20px", color: "#2c1365" }}>
          Thêm bài hát mới
        </h2>
        <form onSubmit={handleSubmit}>
          {/* Tiêu đề */}
          <div style={{ marginBottom: "15px", display: "flex", alignItems: "center", background: "rgba(255,255,255,0.8)", padding: "10px", borderRadius: "8px" }}>
            <FaMusic style={{ marginRight: "10px" }} />
            <input
              type="text"
              name="title"
              placeholder="Tiêu đề"
              required
              onChange={handleChange}
              style={{ border: "none", outline: "none", background: "transparent", flex: 1 }}
            />
          </div>

          {/* Nghệ sĩ */}
          <div style={{ marginBottom: "15px", display: "flex", alignItems: "center", background: "rgba(255,255,255,0.8)", padding: "10px", borderRadius: "8px" }}>
            <FaUser style={{ marginRight: "10px" }} />
            <input
              type="text"
              name="artist"
              placeholder="Nghệ sĩ"
              required
              onChange={handleChange}
              style={{ border: "none", outline: "none", background: "transparent", flex: 1 }}
            />
          </div>

          {/* Thể loại */}
          <div style={{ marginBottom: "15px", display: "flex", alignItems: "center", background: "rgba(255,255,255,0.8)", padding: "10px", borderRadius: "8px" }}>
            <FaTags style={{ marginRight: "10px" }} />
            <input
              type="text"
              name="genre"
              placeholder="Thể loại"
              required
              onChange={handleChange}
              style={{ border: "none", outline: "none", background: "transparent", flex: 1 }}
            />
          </div>

          {/* File upload */}
          <div style={{ marginBottom: "20px", background: "rgba(255,255,255,0.8)", padding: "10px", borderRadius: "8px", display: "flex", alignItems: "center" }}>
            <FaUpload style={{ marginRight: "10px" }} />
            <input
              type="file"
              name="file"
              accept="audio/*"
              required
              onChange={handleChange}
              style={{ flex: 1 }}
            />
          </div>

          {/* Nút Submit */}
          <button
            type="submit"
            style={{
              backgroundColor: "#2c1365",
              color: "white",
              border: "none",
              padding: "12px 0",
              width: "100%",
              borderRadius: "8px",
              fontWeight: "bold",
              fontSize: "16px",
              cursor: "pointer",
            }}
          >
            Upload
          </button>
        </form>
      </div>
    </div>
  );
}
