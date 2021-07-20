package com.hyperledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@CrossOrigin
public class AuditorApplication {

//    @RequestMapping("/123")
//    public String Hello() {
//        return "hello this is /!";
//    }

    public static void main(String[] args) {
        SpringApplication.run(AuditorApplication.class, args);
    }

//    @RequestMapping("/")
//    public void HomePage() {
//
//    }
}
