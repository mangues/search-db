package com.mangues.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author mangues
 */
@SpringBootApplication
@MapperScan("com.mangues.search.mapper")
@ComponentScan({"com.mangues.search","top.mangues.searchdb"})
public class SearchdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchdbApplication.class, args);
    }
}
