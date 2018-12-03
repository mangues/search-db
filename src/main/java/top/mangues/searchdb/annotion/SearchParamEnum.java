package top.mangues.searchdb.annotion;

import top.mangues.searchdb.aop.searchhandler.BetweenAndSymbol;
import top.mangues.searchdb.aop.searchhandler.EqualSymbol;
import top.mangues.searchdb.aop.searchhandler.InSymbol;
import top.mangues.searchdb.aop.searchhandler.LikeSymbol;
import lombok.Getter;


@Getter
public enum  SearchParamEnum {
    like("like", LikeSymbol.class),
    equals("=", EqualSymbol.class),
    in("in", InSymbol.class),
    //必须是***|*** 类型 字符串
    between_and("between_and", BetweenAndSymbol.class),
    ;


    private String symbol;
    private Class handlerClass;


    SearchParamEnum(String symbol, Class handlerClass) {
        this.symbol = symbol;
        this.handlerClass = handlerClass;
    }
}
