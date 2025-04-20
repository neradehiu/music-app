package com.example.doan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.doan.model.Song;
import com.example.doan.model.User;
import com.example.doan.repository.SongRepository;
import com.example.doan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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

    // Upload file lên Cloudinary và lưu vào MySQL
    public Song uploadSong(MultipartFile file, String title, String artist, String genre) throws IOException {
        // Upload file lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));  // "auto" giúp nhận diện đúng loại media

        String cloudinaryUrl = (String) uploadResult.get("url");

        // Tạo bài hát mới và thiết lập thông tin
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCloudinaryUrl(cloudinaryUrl);
        song.setGenre(genre);  // Thiết lập thể loại cho bài hát

        // Lưu bài hát vào cơ sở dữ liệu
        return songRepository.save(song);
    }


    // Lấy danh sách tất cả bài hát
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // Lấy bài hát theo ID
    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    // Tìm kiếm bài hát
    public List<Song> searchSongs(String query) {
        return songRepository.findByTitleContainingOrArtistContaining(query, query);
    }

    //Thêm vào danh sách yêu thích
    public boolean addToFavorites(Long songId) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isPresent()) {
            User user = getCurrentUser(); // Lấy người dùng hiện tại từ security context
            user.getFavorites().add(song.get());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Xóa khỏi yêu thích
    public boolean removeFromFavorites(Long songId) {
        Optional<Song> song = songRepository.findById(songId);
        if (song.isPresent()) {
            User user = getCurrentUser();
            user.getFavorites().remove(song.get());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    //Danh sách yêu thích
    public List<Song> getFavorites() {
        User user = getCurrentUser(); // Lấy người dùng hiện tại từ security context
        return new ArrayList<>(user.getFavorites());
    }


    // Danh sách theo THỂ LOẠI
    public List<Song> getSongsByGenre(String genre) {
        return songRepository.findByGenre(genre); // Giả sử bạn có thuộc tính genre trong model Song
    }


    // Cập nhật thông tin bài hát
    public Optional<Song> updateSong(Long id, Song songDetails) {
        Optional<Song> songOpt = songRepository.findById(id);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            song.setTitle(songDetails.getTitle());
            song.setArtist(songDetails.getArtist());
            song.setCloudinaryUrl(songDetails.getCloudinaryUrl());
            return Optional.of(songRepository.save(song));
        }
        return Optional.empty();
    }

    // Xóa bài hát theo ID
    public boolean deleteSong(Long id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Lấy người dùng hiện tại từ SecurityContext
    private User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
