package com.muyi.bootdemo.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
public class HelloController {

	@RequestMapping("/hello/{name}")
	public String hello(@PathVariable("name")String name,Model model){
		model.addAttribute("name",name);
		return "hello";
	}
	public static void main(String[] args) {
        SpringApplication.run(HelloController.class, args);
    }
}
