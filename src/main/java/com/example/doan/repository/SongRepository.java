package com.example.doan.repository;

import com.example.doan.model.Song;                 // Import model Song
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTitleContainingOrArtistContaining(String title, String artist);

    List<Song> findByGenre(String genre);

    List<Song> findTop10ByOrderByViewCountDesc();

    List<Song> findTop10ByOrderByShareCountDesc();

    @Modifying
    @Transactional
    @Query("update Song s set s.viewCount = s.viewCount + 1 where s.id = :songId")
    int incrementView(Long songId);

    @Modifying
    @Transactional
    @Query("update Song s set s.shareCount = s.shareCount + 1 where s.id = :songId")
    int incrementShare(Long songId);

    Page<Song> findAllByOrderByViewCountDesc(Pageable pageable);
}
