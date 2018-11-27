package com.mangues.searchdb.annotion;

import com.mangues.searchdb.aop.searchhandler.BetweenAndSymbol;
import com.mangues.searchdb.aop.searchhandler.EqualSymbol;
import com.mangues.searchdb.aop.searchhandler.InSymbol;
import com.mangues.searchdb.aop.searchhandler.LikeSymbol;
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
