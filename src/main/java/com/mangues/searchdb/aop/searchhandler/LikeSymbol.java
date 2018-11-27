package com.mangues.searchdb.aop.searchhandler;

import com.mangues.searchdb.annotion.SearchParamEnum;
import org.springframework.stereotype.Component;

@Component
public class LikeSymbol implements SymbolHandler {
    @Override
    public void getNormalSymbol(StringBuilder baseSb, String key, Object object, SearchParamEnum searchParamEnum) {
        baseSb.append(key).
                append(" ")
                .append(searchParamEnum.getSymbol())
                .append(" ")
                .append("'%")
                .append(object)
                .append("%'")
                .append(" and ");
    }
}
