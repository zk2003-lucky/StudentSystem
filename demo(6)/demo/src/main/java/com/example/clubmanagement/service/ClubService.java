package com.example.clubmanagement.service;

import com.example.clubmanagement.entity.Club;
import com.example.clubmanagement.mapper.ClubMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ClubService {

    @Autowired
    private ClubMapper clubMapper;

    public List<Club> findAll() {
        return clubMapper.findAll();
    }

    public Club findById(Integer id) {
        return clubMapper.findById(id);
    }

    public void save(Club club) {
        if (club.getId() == null) {
            clubMapper.insert(club);
        } else {
            clubMapper.update(club);
        }
    }

    public void delete(Integer id) {

        clubMapper.delete(id);
    }

    public String uploadLogo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID() + fileExtension;


        String filePath = Paths.get("").toAbsolutePath() + "/uploads/" + newFilename;

        File dest = new File(filePath);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);

        return newFilename;
    }

    public File downloadLogo(String filename) {
        String filePath = "uploads/" + filename;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }
        return file;
    }

}