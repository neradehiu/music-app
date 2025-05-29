package com.example.doan.controller;

import com.example.doan.model.Song;
import com.example.doan.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(
        origins = {
                "http://10.0.2.2:8000",
                "https://music-app-b1ef.onrender.com",
                "https://music-app-1-f1ec.onrender.com",
		"https://aisearchbyvoice.onrender.com",
        	"https://maf-h4r6.onrender.com",
		"https://music-app-10.onrender.com",
		"https://hiu-g5j6.onrender.com"
        },
        allowCredentials = "true")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    /* ---------- CRUD / QUERY CƠ BẢN ---------- */

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}/play")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> playSong(@PathVariable Long id) {
        return songService.getSongById(id)
                .map(s -> ResponseEntity.ok(s.getCloudinaryUrl()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return songService.getSongById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ---------- SEARCH ---------- */
        
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> searchSongs(@RequestParam String query) {
        return songService.searchSongs(query);
    }

	@GetMapping("/search/voice")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> searchByVoice(@RequestParam String q) {
        return songService.searchByTitleOrArtist(q);
    }

    /* ---------- FAVORITE ---------- */

    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> addToFavorites(@PathVariable Long id) {
        return songService.addToFavorites(id)
                ? ResponseEntity.ok("Song added to favorites")
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> removeFromFavorites(@PathVariable Long id) {
        return songService.removeFromFavorites(id)
                ? ResponseEntity.ok("Song removed from favorites")
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Song> getFavorites() {
        return songService.getFavorites();
    }

    /* ---------- GENRE ---------- */

    @GetMapping("/genre/{genre}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getSongsByGenre(@PathVariable String genre) {
        return songService.getSongsByGenre(genre);
    }

    /* ---------- UPLOAD ---------- */

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Song> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam("genre") String genre) {

        try {
            return ResponseEntity.ok(songService.uploadSong(file, title, artist, genre));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /* ---------- UPDATE / DELETE ---------- */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Song> updateSong(@PathVariable Long id, @RequestBody Song song) {
        return songService.updateSong(id, song)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteSong(@PathVariable Long id) {
        return songService.deleteSong(id)
                ? ResponseEntity.ok("Song deleted successfully!")
                : ResponseEntity.notFound().build();
    }

    /* ---------- COUNTERS: VIEW / SHARE / LIKE ---------- */

    @PutMapping("/{id}/view")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        return songService.incrementView(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/share")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Void> incrementShare(@PathVariable Long id) {
        return songService.incrementShare(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> toggleLike(@PathVariable Long id) {
        return songService.toggleLike(id)
                .map(liked -> liked
                        ? ResponseEntity.ok("Liked ✅")      // vừa like
                        : ResponseEntity.ok("Unliked ❌"))   // vừa bỏ like
                .orElse(ResponseEntity.notFound().build());
    }

    /* ---------- TOP LISTS ---------- */

    @GetMapping("/top")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        return songService.getTopSongs(limit);
    }

    @GetMapping("/top-liked")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getTopLikedSongs(@RequestParam(defaultValue = "10") int limit) {
        return songService.getTopLikedSongs(limit);
    }

    @GetMapping("/top-shared")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getTopSharedSongs(@RequestParam(defaultValue = "10") int limit) {
        return songService.getTopSharedSongs(limit);
    }

    // ✅ Lấy link chia sẻ bài hát
    @GetMapping("/{id}/share-link")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> getShareableLink(@PathVariable Long id) {
        return songService.getSongById(id)
                .map(song -> ResponseEntity.ok(song.getCloudinaryUrl())) // Trả về trực tiếp URL Cloudinary
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/share-count")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Integer> getShareCount(@PathVariable Long id) {
        Optional<Song> songOpt = songService.getSongById(id);
        if (songOpt.isPresent()) {
            Integer shareCount = songOpt.get().getShareCount();  // giả sử trường shareCount trong entity Song
            return ResponseEntity.ok(shareCount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
