import React, { createContext, useState } from "react";

// Tạo Context
export const FavoriteContext = createContext();

// Provider
export const FavoriteProvider = ({ children }) => {
  const [favorites, setFavorites] = useState([]);

  const addToFavorites = (song) => {
    // Tránh trùng lặp bài hát
    if (!favorites.find((item) => item.id === song.id)) {
      setFavorites((prev) => [...prev, song]);
    }
  };

  const removeFromFavorites = (id) => {
    setFavorites((prev) => prev.filter((song) => song.id !== id));
  };

  return (
    <FavoriteContext.Provider value={{ favorites, addToFavorites, removeFromFavorites }}>
      {children}
    </FavoriteContext.Provider>
  );
};
