package com.mangues.search.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mangues.search.demo.mapper")
public class SearchdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchdbApplication.class, args);
    }
}
