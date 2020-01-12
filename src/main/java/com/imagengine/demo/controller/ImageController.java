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
public class ImageController {

    ImageService imageService = new ImageService();


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/affiche/")
    public String affiche() {
        return "affiche";
    }

    @GetMapping("/compare/")
    public String compareImages() {
        return "compare";
    }


    @GetMapping("/similar/")
    public String similarImages() {
        return "similar";
    }

    @PostMapping("/")
    public String addImage(Model model, @RequestPart("file") MultipartFile multipartFile) {

        imageService.createFileFromMyltiPart(multipartFile);
        return "index";
    }

    @PostMapping("/affiche/")
    public String getImage(Model model, @RequestParam("id") int id) throws IOException, SQLException {
        OrdImage ordImage = imageService.getImage(id);
        int link=imageService.stockImageLocaly(id, ordImage);
        model.addAttribute("link",id);
        return "affiche";
    }

    @PostMapping("/compare/")
    public String compareImages(Model model, @RequestPart("file1") MultipartFile multipartFile1
            , @RequestPart("file2") MultipartFile multipartFile2
            , @RequestParam float color
            , @RequestParam float texture
            , @RequestParam float shape) throws SQLException {

        model.addAttribute("seuil",imageService.compareImages(multipartFile1, multipartFile2, color, texture, shape));
        return "compare";
    }


}
