package com.imagengine.demo.controller;

import com.imagengine.demo.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/images")
public class ImageController {

    ImageService imageService=new ImageService();

    @PostMapping("/")
    public void addImage(){

        imageService.insertNewImage();
    }

}
