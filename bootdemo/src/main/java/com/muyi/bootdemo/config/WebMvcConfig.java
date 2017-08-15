package com.muyi.bootdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// TODO Auto-generated method stub
		//super.addViewControllers(registry);
		registry.addViewController("/ws").setViewName("/ws");
		registry.addViewController("/login.jsp").setViewName("/login");
	}
}
