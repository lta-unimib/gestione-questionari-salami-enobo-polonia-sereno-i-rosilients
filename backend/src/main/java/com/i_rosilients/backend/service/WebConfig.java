package com.i_rosilients.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private Environment env;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String urls = env.getProperty("cors.urls");
        if (urls != null) {
            CorsRegistration reg = registry.addMapping("/*");
            for (String url : urls.split(",")) {
                reg.allowedOrigins(url).allowedMethods("*");
            }
        }
    }
    
}
