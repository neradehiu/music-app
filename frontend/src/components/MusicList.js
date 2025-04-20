import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaMusic, FaPlay, FaHeart, FaListAlt } from "react-icons/fa";
import {
  getSongs,
  deleteSong,
  searchSongs,
  favoriteSong,
  getFavoriteSongs,
} from "../services/api";

export default function MusicList() {
  const [songs, setSongs] = useState([]);
  const [favoriteIds, setFavoriteIds] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [showDropdown, setShowDropdown] = useState(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const images = [
    "/image/sontung1.jpeg",
    "/image/minzy.jpeg",
    "/image/buitruonglinh.jpeg",
    "/image/tinhve.jpeg",
    "/image/dia.jpeg",
    "/image/dia1.jpeg",
  ];

  useEffect(() => {
    fetchSongs();
    fetchFavorites();
  }, []);

  const fetchSongs = async () => {
    try {
      const data = await getSongs();
      setSongs(data);
    } catch (error) {
      console.error("Lỗi khi tải danh sách bài hát:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchFavorites = async () => {
    try {
      const favorites = await getFavoriteSongs();
      const ids = favorites.map((song) => song.id);
      setFavoriteIds(ids);
    } catch (error) {
      console.error("Lỗi khi tải danh sách yêu thích:", error);
    }
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    }, 2000);
    return () => clearInterval(interval);
  }, []);

  const handleSearch = async (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    if (query.trim()) {
      const result = await searchSongs(query);
      setSongs(result);
    } else {
      fetchSongs();
    }
  };

  const handleDeleteSong = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa bài hát này?")) {
      try {
        await deleteSong(id);
        fetchSongs();
      } catch (error) {
        alert("Xóa bài hát thất bại!");
      }
    }
  };

  const handleAddToFavorite = async (id) => {
    try {
      await favoriteSong(id);
      fetchFavorites();
      navigate("/favorites");
    } catch (error) {
      alert("Không thể thêm vào yêu thích!");
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundImage: `url("/image/nenweb.jpeg")`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        color: "#fff",
        position: "relative",
        padding: "20px",
      }}
    >
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1 style={{ fontSize: "40px", marginBottom: "20px" }}>PLAYLIST</h1>
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <button
            onClick={() => navigate("/")}
            style={{
              background: "linear-gradient(45deg,rgb(134, 213, 199),rgb(161, 10, 206))",
              color: "#fff",
              border: "none",
              padding: "8px 16px",
              borderRadius: "10px",
              cursor: "pointer",
              fontSize: "16px",
              fontWeight: "bold",
              boxShadow: "0 2px 6px rgba(0,0,0,0.2)",
              transition: "all 0.2s",
            }}
          >
            🏠 HOME
          </button>
          <img
            src="/image/app_logo.png"
            alt="Logo"
            style={{ width: "45px", height: "45px", objectFit: "cover" }}
          />
        </div>
      </div>

      <div style={{ display: "flex", gap: "80px" }}>
        <div style={{ width: "50%" }}>
          <input
            type="text"
            placeholder="🔍 Tìm kiếm bài hát..."
            value={searchQuery}
            onChange={handleSearch}
            style={{
              padding: "10px 1px",
              borderRadius: "10px",
              border: "none",
              width: "100%",
              fontSize: "20px",
            }}
          />

          <div style={{ marginTop: "20px", maxHeight: "70vh", overflowY: "auto" }}>
            {loading ? (
              <p style={{ textAlign: "center", fontSize: "18px" }}>Đang tải...</p>
            ) : (
              songs.map((song, index) => (
                <div
                  key={song.id}
                  style={{
                    background: "rgba(0,0,0,0.5)",
                    padding: "12px 16px",
                    marginBottom: "10px",
                    borderRadius: "12px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    position: "relative",
                  }}
                >
                  <div style={{ display: "flex", alignItems: "center", flex: 1 }}>
                    <FaMusic size={18} style={{ marginRight: "10px" }} />
                    <div style={{ display: "flex", flexDirection: "column" }}>
                      <span style={{ fontSize: "15px", fontWeight: "bold" }}>
                        {index + 1}. {song.title}
                        {favoriteIds.includes(song.id) && (
                          <span style={{ color: "red", marginLeft: "8px" }}>❤️</span>
                        )}
                      </span>
                      <span style={{ fontSize: "13px", marginTop: "4px" }}>🎤 {song.artist}</span>
                      <span style={{ fontSize: "13px" }}>🎵 {song.genre}</span>
                      <span style={{ fontSize: "12px", opacity: 0.8 }}>
                        📅 {new Date(song.created_at).toLocaleDateString()}
                      </span>
                    </div>
                  </div>

                  <div style={{ display: "flex", alignItems: "center", gap: "5px" }}>
                    <button
                      onClick={() => navigate(`/lyrics/${song.id}`)}
                      style={{
                        background: "#fff",
                        border: "none",
                        padding: "6px",
                        borderRadius: "60%",
                        cursor: "pointer",
                        color: "#000",
                        fontWeight: "bold",
                        width: "30px",
                        height: "30px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                      }}
                    >
                      <FaPlay size={12} />
                    </button>

                    <button
                      onClick={() => setShowDropdown(showDropdown === index ? null : index)}
                      style={{
                        background: "none",
                        border: "none",
                        fontSize: "20px",
                        color: "white",
                        cursor: "pointer",
                        width: "30px",
                        height: "30px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                      }}
                    >
                      ☰
                    </button>

                    {showDropdown === index && (
                      <div
                        style={{
                          position: "absolute",
                          right: "10px",
                          top: "40px",
                          background: "rgba(0,0,0,0.8)",
                          borderRadius: "8px",
                          padding: "6px",
                          zIndex: 100,
                          whiteSpace: "nowrap",
                        }}
                      >
                        <button
                          onClick={() => handleAddToFavorite(song.id)}
                          style={dropdownItemStyle}
                          disabled={favoriteIds.includes(song.id)}
                        >
                          <FaHeart style={{ marginRight: "6px", color: "red" }} />
                          Yêu thích
                        </button>
                        <button onClick={() => navigate("/addmusic")} style={dropdownItemStyle}>
                          ➕ Thêm nhạc
                        </button>
                        <button onClick={() => handleDeleteSong(song.id)} style={dropdownItemStyle}>
                          ❌ Xóa bài hát
                        </button>
                        <button onClick={() => navigate("/favorites")} style={dropdownItemStyle}>
                          <FaListAlt style={{ marginRight: "6px" }} />
                          Danh sách yêu thích
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div style={{ width: "60%" }}>
          <img
            src={images[currentImageIndex]}
            alt="Slideshow"
            style={{
              width: "100%",
              height: "550px",
              objectFit: "cover",
              borderRadius: "15px",
              boxShadow: "0 4px 15px rgba(0,0,0,0.3)",
              transition: "opacity 0.5s ease-in-out",
            }}
          />
        </div>
      </div>
    </div>
  );
}

const dropdownItemStyle = {
  background: "none",
  color: "white",
  border: "none",
  padding: "8px 12px",
  display: "flex",
  alignItems: "center",
  width: "100%",
  textAlign: "left",
  fontSize: "14px",
  cursor: "pointer",
  borderBottom: "1px solid rgba(255,255,255,0.2)",
  gap: "6px",
};
