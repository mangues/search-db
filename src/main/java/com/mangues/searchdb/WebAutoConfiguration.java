package com.mangues.searchdb;

import com.mangues.searchdb.aop.DictSearchAop;
import com.mangues.searchdb.aop.SearchDbAop;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WebAutoConfiguration {
    @Bean
    public DictSearchAop dictSearchAop() {
        return new DictSearchAop();
    }

    @Bean
    public SearchDbAop searchDbAop() {
        return new SearchDbAop();
    }
}

