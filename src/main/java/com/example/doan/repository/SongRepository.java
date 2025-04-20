package com.example.doan.repository;

import com.example.doan.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByTitleContainingOrArtistContaining(String title, String artist);
    List<Song> findByGenre(String genre);
}
