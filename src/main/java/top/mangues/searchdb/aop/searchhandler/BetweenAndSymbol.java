package top.mangues.searchdb.aop.searchhandler;

import top.mangues.searchdb.annotion.SearchParamEnum;
import org.springframework.stereotype.Component;

@Component
public class BetweenAndSymbol implements SymbolHandler {
    @Override
    public void getNormalSymbol(StringBuilder baseSb, String key, Object object, SearchParamEnum searchParamEnum) {
        //必须是list类型
        String str = String.valueOf(object);
        String[] split = str.split(",");
        baseSb.append(" ( ").append(key).
                append(" between '")
                .append(split[0]).append("' and '").append(split[1])
                .append("' ) ").append(" and ");
    }
}
