package com.imagengine.demo.controller;

import com.imagengine.demo.service.ImageService;
import oracle.ord.im.OrdImage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.springframework.ui.Model;


@Controller
@RequestMapping("/images")
public class ImageController {

    ImageService imageService = new ImageService();


    @GetMapping("/")
    public String insert() {
        return "index";
    }

    @PostMapping("/")
    public String addImage(Model model, @RequestPart("file") MultipartFile multipartFile) {

        imageService.createFileFromMyltiPart(multipartFile);
        return "index";
    }

    @GetMapping("/getImage/{id}")
    public String getImage(Model model, @PathVariable("id") int id) throws IOException, SQLException {
        OrdImage ordImage = imageService.getImage(id);
        return imageService.stockImageLocaly(id, ordImage);
    }

    @PostMapping("/compareImages/")
    public int compareImages(Model model, @RequestPart("file1") MultipartFile multipartFile1
            , @RequestPart("file2") MultipartFile multipartFile2
            , @RequestParam int color
            , @RequestParam int texture
            , @RequestParam int shape) {
        return imageService.compareImages(multipartFile1, multipartFile2, color, texture, shape);

    }


}
