package com.example.doan.controller;

import com.example.doan.service.SearchService;
import com.example.doan.model.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    private final SearchService searchService;
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/songs/search")
    public List<Song> searchSongs(@RequestParam String q) {
        return searchService.searchByTitleOrArtist(q);
    }
}
