package com.imagengine.demo;

import com.imagengine.demo.service.ImageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.math.BigDecimal;

@SpringBootApplication
public class ImagengineApplication  {

    public static void main(String[] args) {
        SpringApplication.run(ImagengineApplication.class, args);
        /*ImageService imageService = new ImageService();
        File file = new File(System.getProperty("user.dir")+"/src/main/resources/static/images/image.jpg");
        imageService.updateAndInsertImage(BigDecimal.valueOf(3),file);*/
        //System.out.println("hjkgskjsh");

    }

//    @Override
//    public void run(String... args) throws Exception {
//
//    }
}
