import React, { useState, useEffect, useRef, useCallback } from "react";
import { FaPlay, FaPause, FaStepForward, FaStepBackward, FaHeart, FaBars, FaPaperPlane } from "react-icons/fa";
import { useParams, useNavigate } from "react-router-dom";
import { getSongUrl, getSongById, favoriteSong, getFavoriteSongs, getSongs, unfavoriteSong } from "../services/api";
import "./Lyrics.css";

const Lyrics = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [song, setSong] = useState(null);
  const [songUrl, setSongUrl] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [favoriteIds, setFavoriteIds] = useState([]);
  const [songList, setSongList] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(-1);

  const audioRef = useRef(null);

  // Fetch the list of songs from the API
  const fetchSongList = useCallback(async () => {
    try {
      const data = await getSongs(); // Using the API method to fetch the songs
      setSongList(data);
      const index = data.findIndex((s) => s.id === parseInt(id));
      setCurrentIndex(index);
    } catch (error) {
      console.error("Error fetching song list:", error);
    }
  }, [id]);

  const fetchSong = useCallback(async () => {
    try {
      const data = await getSongById(id);
      setSong(data);
    } catch (error) {
      console.error("Error fetching song:", error);
    }
  }, [id]);

  const fetchSongUrl = useCallback(async () => {
    try {
      const url = await getSongUrl(id);
      setSongUrl(url);
    } catch (error) {
      console.error("Error fetching song URL:", error);
    }
  }, [id]);

  const fetchFavorites = useCallback(async () => {
    try {
      const favorites = await getFavoriteSongs();
      const ids = favorites.map((song) => song.id);
      setFavoriteIds(ids);
    } catch (error) {
      console.error("Error fetching favorites:", error);
    }
  }, []);

  // Fetch song list, song details, and song URL when the component loads
  useEffect(() => {
    fetchSongList();
    fetchSong();
    fetchSongUrl();
    fetchFavorites();
  }, [id, fetchSongList, fetchSong, fetchSongUrl, fetchFavorites]);

  // Play the song when it starts
  useEffect(() => {
    if (songUrl && isPlaying && audioRef.current) {
      audioRef.current.play().catch(console.error);
    }
  }, [songUrl, isPlaying]);

  const togglePlayPause = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play().catch(console.error);
    }
    setIsPlaying(!isPlaying);
  };

  const handleNextSong = () => {
    if (songList.length === 0 || currentIndex === -1) return;
    const nextIndex = (currentIndex + 1) % songList.length;
    const nextSongId = songList[nextIndex].id;

    setCurrentIndex(nextIndex); // Update current song index
    navigate(`/lyrics/${nextSongId}`, { replace: true }); // Navigate to the next song

    // Reset current time and play the next song
    setCurrentTime(0);
    setIsPlaying(true); // Automatically start playing the next song
  };

  const handlePrevSong = () => {
    if (songList.length === 0 || currentIndex === -1) return;
    const prevIndex = (currentIndex - 1 + songList.length) % songList.length;
    const prevSongId = songList[prevIndex].id;

    setCurrentIndex(prevIndex); // Update current song index
    navigate(`/lyrics/${prevSongId}`, { replace: true }); // Navigate to the previous song

    // Reset current time and play the previous song
    setCurrentTime(0);
    setIsPlaying(true); // Automatically start playing the previous song
  };

  // Handle adding/removing song from favorites
  const handleAddToFavorite = async (songId) => {
    try {
      if (favoriteIds.includes(songId)) {
        // If the song is already in the favorites, remove it
        await unfavoriteSong(songId);
      } else {
        // If the song is not in the favorites, add it
        await favoriteSong(songId);
      }
      fetchFavorites(); // Refresh favorite list after adding/removing
    } catch (error) {
      alert("Unable to update favorites!");
    }
  };

  const handleProgressClick = (e) => {
    const progressBar = e.target.closest(".progress");
    if (!progressBar || !audioRef.current) return;

    const progressWidth = progressBar.clientWidth;
    const clickX = e.nativeEvent.offsetX;
    const newTime = (clickX / progressWidth) * duration;
    audioRef.current.currentTime = newTime;
  };

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const secondsLeft = Math.floor(seconds % 60);
    return `${minutes}:${secondsLeft < 10 ? "0" : ""}${secondsLeft}`;
  };

  // Update current time from audio
  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.ontimeupdate = () => {
        setCurrentTime(audioRef.current.currentTime);
      };
    }
  }, []);

  // Calculate the position of the progress pointer
  const progressPointerPosition = (currentTime / duration) * 100;

  if (!song) return <p>Loading...</p>;

  return (
    <div className="lyrics-container" style={{ backgroundImage: "url('/image/Lyrics.jpg')" }}>
      <div className="music-player">
        <div className="album-art" style={{ backgroundImage: `url('/image/music.jpg')` }}></div>
        
        <div className="progress-bar" onClick={handleProgressClick}>
          <span>{formatTime(currentTime)}</span>
          <div className="progress">
            <div
              className="progress-filled"
              style={{ width: `${(currentTime / duration) * 100}%` }}
            ></div>
            <div
              className="progress-pointer"
              style={{ left: `${progressPointerPosition}%` }}
            >
              <FaPaperPlane />
            </div>
          </div>
          <span>{formatTime(duration)}</span>
        </div>

        <div className="controls">
          <button
            className={`icon favorite ${favoriteIds.includes(song.id) ? "clicked" : ""}`}
            onClick={() => handleAddToFavorite(song.id)}
          >
            <FaHeart />
          </button>
          <button className="icon" onClick={handlePrevSong}><FaStepBackward /></button>
          <button className="icon play-pause" onClick={togglePlayPause}>
            {isPlaying ? <FaPause /> : <FaPlay />}
          </button>
          <button className="icon" onClick={handleNextSong}><FaStepForward /></button>
          <button className="icon" onClick={() => navigate("/favorites")}>
            <FaBars />
          </button>
        </div>

        <h2 className="song-title">{song.title}</h2>
        <h3 className="song-artist">{song.artist}</h3>
        <p>{song.genre}</p>
        <p>{new Date(song.created_at).toLocaleDateString()}</p>

        <div className="separator"></div>
        <pre>{song.lyrics}</pre>
      </div>

      <button className="home-button" onClick={() => navigate("/")}>🏠 HOME</button>

      {songUrl && (
        <audio
          ref={audioRef}
          src={songUrl}
          preload="auto"
          onLoadedMetadata={() => setDuration(audioRef.current.duration)}
          onTimeUpdate={() => setCurrentTime(audioRef.current.currentTime)}
          onEnded={() => {
            setIsPlaying(false);
            handleNextSong(); // Automatically move to the next song when it ends
          }}
        />
      )}
    </div>
  );
};

export default Lyrics;
