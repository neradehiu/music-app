package com.example.doan.service;

import com.example.doan.model.Song;
import com.example.doan.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final SongRepository songRepo;
    public SearchService(SongRepository songRepo) {
        this.songRepo = songRepo;
    }

    public List<Song> searchByTitleOrArtist(String q) {
        return songRepo.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(q, q);
    }
}
