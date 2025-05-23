package com.example.doan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.doan.model.Song;
import com.example.doan.model.User;
import com.example.doan.repository.SongRepository;
import com.example.doan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SongService {

    private final Cloudinary cloudinary;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    /* ==================== UPLOAD ==================== */
    public Song uploadSong(MultipartFile file,
                           String title,
                           String artist,
                           String genre) throws IOException {

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        String url = ((String) uploadResult.get("url")).trim();
        Song song = Song.builder()
                .title(title)
                .artist(artist)
                .genre(genre)
                .cloudinaryUrl((String) uploadResult.get("url"))
                .viewCount(0)
                .shareCount(0)
                .likeCount(0)
                .build();

        return songRepository.save(song);
    }

    /* ==================== CRUD / QUERY ==================== */
    public List<Song> getAllSongs() { return songRepository.findAll(); }

    public Optional<Song> getSongById(Long id) { return songRepository.findById(id); }

    
    /* ==================== FAVORITES ==================== */

    public List<Song> searchSongs(String query) {
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(query, query);
    }

    public List<Song> searchByTitleOrArtist(String q) {
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(q, q);
    }

    /* ==================== FAVORITES ==================== */
    public boolean addToFavorites(Long songId) {
        return songRepository.findById(songId).map(song -> {
            User user = getCurrentUser();
            if (user.getFavorites().add(song)) { userRepository.save(user); }
            return true;
        }).orElse(false);
    }

    public boolean removeFromFavorites(Long songId) {
        return songRepository.findById(songId).map(song -> {
            User user = getCurrentUser();
            if (user.getFavorites().remove(song)) { userRepository.save(user); }
            return true;
        }).orElse(false);
    }

    public List<Song> getFavorites() {
        return new ArrayList<>(getCurrentUser().getFavorites());
    }

    /* ==================== GENRE ==================== */
    public List<Song> getSongsByGenre(String genre) { return songRepository.findByGenre(genre); }

    /* ==================== UPDATE / DELETE ==================== */
    public Optional<Song> updateSong(Long id, Song details) {
        return songRepository.findById(id).map(song -> {
            song.setTitle(details.getTitle());
            song.setArtist(details.getArtist());
            song.setCloudinaryUrl(details.getCloudinaryUrl());
            song.setGenre(details.getGenre());
            return songRepository.save(song);
        });
    }

    public boolean deleteSong(Long id) {
        if (songRepository.existsById(id)) { songRepository.deleteById(id); return true; }
        return false;
    }

    /* ==================== COUNTERS ==================== */
    @Transactional public boolean incrementView(Long songId)  { return songRepository.incrementView(songId)  > 0; }
    @Transactional public boolean incrementShare(Long songId) { return songRepository.incrementShare(songId) > 0; }

    /* ---------- Toggle Like ---------- */
    @Transactional
    public Optional<Boolean> toggleLike(Long songId) {
        return songRepository.findById(songId).map(song -> {
            User user = getCurrentUser();
            boolean likedNow;

            if (user.getLikedSongs().contains(song)) {          // UNLIKE
                user.getLikedSongs().remove(song);
                song.setLikeCount(song.getLikeCount() - 1);
                likedNow = false;
            } else {                                            // LIKE
                user.getLikedSongs().add(song);
                song.setLikeCount(song.getLikeCount() + 1);
                likedNow = true;
            }
            userRepository.save(user);
            songRepository.save(song);
            return likedNow;
        });
    }

    /* Giữ method cũ để Controller gọi */
    @Transactional
    public boolean incrementLike(Long songId) {
        return toggleLike(songId).isPresent();  // true nếu tồn tại bài hát
    }

    /* ==================== TOP LISTS ==================== */
    @Transactional(readOnly = true)
    public List<Song> getTopSongs(int limit) {
        return songRepository.findAllByOrderByViewCountDesc(PageRequest.of(0, limit)).getContent();
    }

    @Transactional(readOnly = true)
    public List<Song> getTopLikedSongs(int limit) {
        return songRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "likeCount"))).getContent();
    }

    @Transactional(readOnly = true)
    public List<Song> getTopSharedSongs(int limit) {
        return songRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "shareCount"))).getContent();
    }

    /* ==================== UTIL ==================== */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
