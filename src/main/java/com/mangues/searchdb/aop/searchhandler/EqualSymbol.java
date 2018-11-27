package com.mangues.searchdb.aop.searchhandler;

import com.mangues.searchdb.annotion.SearchParamEnum;
import org.springframework.stereotype.Component;

@Component
public class EqualSymbol implements SymbolHandler {
    @Override
    public void getNormalSymbol(StringBuilder baseSb, String key, Object object, SearchParamEnum searchParamEnum) {
        baseSb.append(key)
                .append(searchParamEnum.getSymbol())
                .append("'").append(object)
                .append("'").append(" and ");
    }
}
