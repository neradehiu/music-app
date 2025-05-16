package com.example.doan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.doan.model.Song;
import com.example.doan.model.User;
import com.example.doan.repository.SongRepository;
import com.example.doan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    /* ==================== UPLOAD ==================== */
    public Song uploadSong(MultipartFile file, String title, String artist, String genre) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));

        String cloudinaryUrl = (String) uploadResult.get("url");

        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCloudinaryUrl(cloudinaryUrl);
        song.setGenre(genre);
        song.setViewCount(0);   // Khởi tạo viewCount = 0
        song.setShareCount(0);  // Khởi tạo shareCount = 0

        return songRepository.save(song);
    }

    /* ==================== CRUD / QUERY ==================== */
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public List<Song> searchSongs(String query) {
        return songRepository.findByTitleContainingOrArtistContaining(query, query);
    }

    public boolean addToFavorites(Long songId) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isPresent()) {
            User user = getCurrentUser();
            if (!user.getFavorites().contains(song.get())) {
                user.getFavorites().add(song.get());
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    public boolean removeFromFavorites(Long songId) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isPresent()) {
            User user = getCurrentUser();
            if (user.getFavorites().contains(song.get())) {
                user.getFavorites().remove(song.get());
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    public List<Song> getFavorites() {
        User user = getCurrentUser();
        return new ArrayList<>(user.getFavorites());
    }

    public List<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenre(genre);
    }

    public Optional<Song> updateSong(Long id, Song songDetails) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setTitle(songDetails.getTitle());
            song.setArtist(songDetails.getArtist());
            song.setCloudinaryUrl(songDetails.getCloudinaryUrl());
            song.setGenre(songDetails.getGenre());
            return Optional.of(songRepository.save(song));
        }
        return Optional.empty();
    }

    public boolean deleteSong(Long id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /* ==================== VIEW / SHARE / TOP ==================== */

    /** Tăng viewCount lên +1 */
    @Transactional
    public boolean incrementView(Long songId) {
        return songRepository.incrementView(songId) > 0;
    }

    /** Tăng shareCount lên +1 */
    @Transactional
    public boolean incrementShare(Long songId) {
        return songRepository.incrementShare(songId) > 0;
    }

    /** Lấy Top N bài hát theo lượt xem giảm dần */
    @Transactional(readOnly = true)
    public List<Song> getTopSongs(int limit) {
        return songRepository
                .findAllByOrderByViewCountDesc(PageRequest.of(0, limit))
                .getContent();
    }

    /** Lấy Top 10 bài hát theo lượt chia sẻ nhiều nhất */
    @Transactional(readOnly = true)
    public List<Song> getTopSharedSongs() {
        return songRepository.findTop10ByOrderByShareCountDesc();
    }

    /* ==================== UTIL ==================== */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
