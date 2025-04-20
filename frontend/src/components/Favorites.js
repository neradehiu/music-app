import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaHeart, FaPlay } from "react-icons/fa";
import {
  getFavoriteSongs,
  unfavoriteSong,
} from "../services/api";
import "./Favorites.css";

export default function Favorites() {
  const navigate = useNavigate();
  const [favoriteSongs, setFavoriteSongs] = useState([]);

  // Lấy danh sách bài hát yêu thích khi component mount
  useEffect(() => {
    fetchFavoriteSongs();
  }, []);

  const fetchFavoriteSongs = async () => {
    try {
      const songs = await getFavoriteSongs();
      setFavoriteSongs(songs);
    } catch (error) {
      console.error("Error fetching favorite songs:", error);
    }
  };

  // Xóa khỏi danh sách yêu thích
  const toggleLike = async (id) => {
    try {
      await unfavoriteSong(id);
      const updatedSongs = favoriteSongs.filter((song) => song.id !== id);
      setFavoriteSongs(updatedSongs);
    } catch (error) {
      console.error("Error removing favorite:", error);
    }
  };

  // Chuyển đến trang phát nhạc
  const playSong = (id) => {
    navigate(`/lyrics/${id}`);
  };

  return (
    <div
      className="favorites-container"
      style={{ backgroundImage: "url('/image/Favoritesonglist.jpg')" }}
    >
      <h1 className="favorites-title">FAVORITE SONG LIST</h1>

      <div className="song-list">
        {favoriteSongs.length > 0 ? (
          <ul>
            {favoriteSongs.map((song) => (
              <li key={song.id} className="song-item">
                <span className="song-name">{song.title}</span>
                <span className="song-artist">🎤 {song.artist}</span>
                <div className="icon-group">
                  <FaHeart
                    className="heart-icon liked"
                    onClick={() => toggleLike(song.id)}
                  />
                  <FaPlay
                    className="play-icon"
                    onClick={() => playSong(song.id)}
                  />
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="no-songs">Chưa có bài nhạc yêu thích nào.</p>
        )}
      </div>

      <button className="home-buttonf" onClick={() => navigate("/dashboard")}>
      🏠 HOME
      </button>
    </div>
  );
}
