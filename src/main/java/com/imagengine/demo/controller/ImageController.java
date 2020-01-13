package com.imagengine.demo.controller;

import com.imagengine.demo.bean.Compare;
import com.imagengine.demo.bean.Image;
import com.imagengine.demo.service.ImageService;
import oracle.ord.im.OrdImage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

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

        model.addAttribute("id", imageService.createFileFromMultiPart(multipartFile));
        return "index";
    }

    @PostMapping("/affiche/")
    public String getImage(Model model, @RequestParam("id") int id) throws IOException, SQLException {
        OrdImage ordImage = imageService.getImage(id);
        Image image = new Image();
        image.setImage(ordImage);
        image.setId(BigDecimal.valueOf(id));
        imageService.stockImageLocaly(image);
        model.addAttribute("image", image.getId());
        model.addAttribute("description", imageService.getDescription(image.getImage()));
        return "affiche";
    }

    @PostMapping("/compare/")
    public String compareImages(Model model, @RequestPart("file1") MultipartFile multipartFile1
            , @RequestPart("file2") MultipartFile multipartFile2
            , @RequestParam float color
            , @RequestParam float texture
            , @RequestParam float shape) throws SQLException, IOException {
        Compare compare = new Compare();
        compare = imageService.compareImages(multipartFile1, multipartFile2, color, texture, shape);
        model.addAttribute("id1", compare.getId1());
        model.addAttribute("id2", compare.getId2());
        model.addAttribute("score", compare.getScore());

        return "compare";
    }

    @PostMapping("/similar/")
    public String similarImages(Model model, @RequestPart("file1") MultipartFile multipartFile1
            , @RequestParam float color
            , @RequestParam float texture
            , @RequestParam float shape
            , @RequestParam float seuil) throws SQLException, IOException {
        int id = imageService.createFileFromMultiPart(multipartFile1);
        OrdImage ordImage = imageService.getImage(id);
        Image image = new Image();
        image.setImage(ordImage);
        image.setId(BigDecimal.valueOf(id));
        imageService.stockImageLocaly(image);


        List<Image> images = imageService.similarityRate(id, color, texture, shape, seuil);
        System.out.println("IS SIMILAR RESULT FORM CONTROLLER :" + images);

        model.addAttribute("images", images);
        model.addAttribute("similarTo", image.getId());
        return "similar";
    }


}
