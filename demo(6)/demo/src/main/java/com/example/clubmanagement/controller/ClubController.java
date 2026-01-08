package com.example.clubmanagement.controller;

import com.example.clubmanagement.entity.Club;
import com.example.clubmanagement.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping
    public List<Club> findAll() {
        return clubService.findAll();
    }

    @GetMapping("/{id}")
    public Club findById(@PathVariable Integer id) {
        return clubService.findById(id);
    }

    @PostMapping
    public Club save(@RequestBody Club club) {

        clubService.save(club);
        return club;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        clubService.delete(id);
    }

    @PostMapping("/{id}/logo")
    public String uploadLogo(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        String filename = clubService.uploadLogo(file);
        Club club = clubService.findById(id);
        club.setLogo(filename);
        clubService.save(club);
        return filename;
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<Resource> downloadLogo(@PathVariable Integer id) {
        Club club = clubService.findById(id);
        if (club.getLogo() == null) {
            throw new IllegalArgumentException("该社团没有 logo");
        }
        File file = clubService.downloadLogo(club.getLogo());
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}