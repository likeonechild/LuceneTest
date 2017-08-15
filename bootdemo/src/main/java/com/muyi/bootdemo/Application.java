package com.muyi.bootdemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.muyi.bootdemo.bean.Person;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	@RequestMapping("/Hello_World")
	String home(){
		return "Hello World";
	}
	
	@RequestMapping("/now")
    String hehe() {
        return "现在时间：" + (new Date()).toLocaleString();
    }
	
	@RequestMapping("/")
    public String index(Model model) {
        Person single = new Person("aa", 11);
        List<Person> people = new ArrayList<>();
        Person p1 = new Person("zhangsan", 11);
        Person p2 = new Person("lisi", 22);
        Person p3 = new Person("wangwu", 33);
        people.add(p1);
        people.add(p2);
        people.add(p3);
        model.addAttribute("singlePerson", single);
        model.addAttribute("people", people);
        return "index";
    }
	
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
