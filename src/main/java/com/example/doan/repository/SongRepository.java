package com.example.doan.repository;

import com.example.doan.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    /* ----- SEARCH & FILTER ----- */
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(String title, String artist);

    List<Song> findByGenre(String genre);

    /* ----- TOP LISTS (không cần query tùy biến vì đã dùng Pageable trong service) ----- */
    List<Song> findTop10ByOrderByViewCountDesc();

    List<Song> findTop10ByOrderByShareCountDesc();

    List<Song> findTop10ByOrderByLikeCountDesc();

    /* ----- INCREMENT COUNTERS ----- */
    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.viewCount = s.viewCount + 1 WHERE s.id = :songId")
    int incrementView(@Param("songId") Long songId);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.shareCount = s.shareCount + 1 WHERE s.id = :songId")
    int incrementShare(@Param("songId") Long songId);

    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.likeCount = s.likeCount + 1 WHERE s.id = :songId")
    int incrementLike(@Param("songId") Long songId);

    /* ----- PAGING BY VIEW (service dùng để lấy top N) ----- */
    Page<Song> findAllByOrderByViewCountDesc(Pageable pageable);

}
