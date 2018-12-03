package top.mangues.searchdb.aop.searchhandler;

import top.mangues.searchdb.annotion.SearchParamEnum;
import com.sun.deploy.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InSymbol implements SymbolHandler {
    @Override
    public void getNormalSymbol(StringBuilder baseSb, String key, Object object, SearchParamEnum searchParamEnum) {
        //必须是list类型
        if (object instanceof List) {
            List objList = (List) object;
            if (objList.size()==0) {
                return;
            }
            String join = StringUtils.join(objList, ",");
            baseSb.append(key).
                    append(" ")
                    .append(searchParamEnum.getSymbol())
                    .append(" (").append(join).append(")")
                    .append(" and ");
        }
    }
}
