package com.mangues.searchdb.annotion;

import lombok.Getter;

@Getter
public enum  SearchParamEnum {
    like("like"),
    equals("="),
    in("in"),
    //必须是***|*** 类型 字符串
    between_and("between_and"),
    ;


    private String symbol;

    SearchParamEnum(String symbol) {
        this.symbol = symbol;
    }

}
