package com.example.doan.controller;

import com.example.doan.model.Song;
import com.example.doan.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @Autowired
    private SongService songService;

    @PostMapping("/play")
    public ResponseEntity<?> playFromVoice(@RequestBody Map<String, String> body) {
        String query = body.get("query"); // ví dụ: "yesterday"
        Song song = songService.findSongByTitleIgnoreCase(query);
        if (song != null) {
            return ResponseEntity.ok(song); // Flutter sẽ nhận được URL và play
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy bài hát.");
        }
    }
}
