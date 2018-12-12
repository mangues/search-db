package top.mangues.searchdb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mangues.searchdb.aop.DictSearchAop;
import top.mangues.searchdb.aop.SearchDbAop;
import top.mangues.searchdb.aop.searchhandler.BetweenAndSymbol;
import top.mangues.searchdb.aop.searchhandler.EqualSymbol;
import top.mangues.searchdb.aop.searchhandler.InSymbol;
import top.mangues.searchdb.aop.searchhandler.LikeSymbol;
import top.mangues.searchdb.util.DictSearchHandler;

@Configuration
public class WebAutoConfiguration {
    @Bean
    public DictSearchAop dictSearchAop() {
        return new DictSearchAop();
    }

    @Bean
    public SearchDbAop searchDbAop() {
        return new SearchDbAop();
    }

    @Bean
    BetweenAndSymbol betweenAndSymbol(){
        return new BetweenAndSymbol();
    }

    @Bean
    EqualSymbol equalSymbol(){
        return new EqualSymbol();
    }

    @Bean
    InSymbol inSymbol(){
        return new InSymbol();
    }

    @Bean
    LikeSymbol likeSymbol(){
        return new LikeSymbol();
    }

    @Bean
    DictSearchHandler dictSearchHandler(){
        return new DictSearchHandler();
    }
}

