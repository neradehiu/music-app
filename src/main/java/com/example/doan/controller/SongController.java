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
@CrossOrigin(origins = {
        "http://10.0.2.2:8000",
        "https://music-app-b1ef.onrender.com",
        "https://music-app-1-f1ec.onrender.com"
}, allowCredentials = "true")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // ✅ Lấy danh sách nhạc
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    // ✅ Phát một bài nhạc
    @GetMapping("/{id}/play")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> playSong(@PathVariable Long id) {
        Optional<Song> songOpt = songService.getSongById(id);
        return songOpt.map(song -> ResponseEntity.ok(song.getCloudinaryUrl()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Lấy thông tin 1 bài nhạc
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        return song.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Tìm kiếm bài hát
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> searchSongs(@RequestParam String query) {
        return songService.searchSongs(query);
    }

    // ✅ Thêm vào danh sách yêu thích
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> addToFavorites(@PathVariable Long id) {
        boolean success = songService.addToFavorites(id);
        return success ? ResponseEntity.ok("Song added to favorites") : ResponseEntity.notFound().build();
    }

    // ✅ Xóa khỏi yêu thích
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> removeFromFavorites(@PathVariable Long id) {
        boolean success = songService.removeFromFavorites(id);
        return success ? ResponseEntity.ok("Song removed from favorites") : ResponseEntity.notFound().build();
    }

    // ✅ Danh sách yêu thích
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Song> getFavorites() {
        return songService.getFavorites();
    }

    // ✅ Danh sách bài hát theo thể loại
    @GetMapping("/genre/{genre}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getSongsByGenre(@PathVariable String genre) {
        return songService.getSongsByGenre(genre);
    }

    // ✅ Upload bài hát
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Song> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam("genre") String genre) {
        try {
            Song song = songService.uploadSong(file, title, artist, genre);
            return ResponseEntity.ok(song);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // ✅ Sửa bài hát
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Song> updateSong(@PathVariable Long id, @RequestBody Song song) {
        return songService.updateSong(id, song)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Xoá bài hát
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteSong(@PathVariable Long id) {
        return songService.deleteSong(id)
                ? ResponseEntity.ok("Song deleted successfully!")
                : ResponseEntity.notFound().build();
    }

    // ✅ Tăng lượt xem
    @PutMapping("/{id}/view")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        return songService.incrementView(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    // ✅ Tăng lượt chia sẻ
    @PutMapping("/{id}/share")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Void> incrementShare(@PathVariable Long id) {
        return songService.incrementShare(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    // ✅ Lấy danh sách top bài hát (theo lượt xem)
    @GetMapping("/top")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Song> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        return songService.getTopSongs(limit);
    }
}
