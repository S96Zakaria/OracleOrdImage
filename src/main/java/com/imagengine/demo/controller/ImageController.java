package com.imagengine.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class ImageController {


    @PostMapping("/")
    public void addImage(@RequestPart("file") MultipartFile multipartFile){


    }

}
