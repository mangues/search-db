package com.mangues.search.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by atom on 2017/1/10.
 * Copyright (C) 2017 Shanghai ZhiJia Information Technology Co., Ltd.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket manageApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(manageApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mangues.search.demo.controller"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    private ApiInfo manageApiInfo() {
        return new ApiInfoBuilder()
                .title("管理")
                .description("管理接口")
                .contact(new Contact("mangues", "", "mangues@yeah.net"))
                .version("1.0.0")
                .build();
    }
}
