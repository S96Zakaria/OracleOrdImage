package com.imagengine.demo.controller;

import com.imagengine.demo.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import org.springframework.ui.Model;


@Controller
@RequestMapping("/images")
public class ImageController {
    public String fileLocation = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    ImageService imageService = new ImageService();


    @GetMapping("/")
    public String insert() {
        return "index";
    }

    @PostMapping("/")
    public String addImage(Model model, @RequestPart("file") MultipartFile multipartFile) {

        String filename = multipartFile.getOriginalFilename();
        System.out.println(filename);
        File file = new File(fileLocation + filename);
        boolean bool = false;
        try {
            multipartFile.transferTo(file);
            imageService.createImage(file);
            bool = file.delete();
            System.out.println("tmèaat ??" + bool);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return "index";
    }

    @GetMapping("/{id}")
    public String getImage(Model model,@PathVariable("id") int id)
    {
return "a";
    }


}
