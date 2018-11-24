package com.mangues.searchdb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mangues.searchdb.demo.mapper")
public class SearchdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchdbApplication.class, args);
    }
}
