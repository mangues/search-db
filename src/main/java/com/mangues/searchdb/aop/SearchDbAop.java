package com.mangues.searchdb.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mangues.searchdb.annotion.SearchParam;
import com.mangues.searchdb.annotion.SearchParamEnum;
import com.mangues.searchdb.common.Enum;
import com.mangues.searchdb.common.SearchBean;
import com.mangues.searchdb.mybatis.SearchHelper;
import com.mangues.searchdb.util.UnderLineUtil;
import com.sun.deploy.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/24 10:36 AM
 * @Description 搜索Aop
 */
@Aspect
@Component
public class SearchDbAop {

    @Pointcut(value = "@annotation(com.mangues.searchdb.annotion.SearchDb)")
    private void searchdb() {

    }

    @Around("searchdb()")
    public Object doSearch(ProceedingJoinPoint point) throws Throwable {
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;


        StringBuilder stringBuilder = new StringBuilder(" ");


        Annotation[][] pas = msig.getMethod().getParameterAnnotations();
        Object[] args = point.getArgs();
        for (Object obj:args){
            //是搜索类
            if (obj instanceof SearchBean) {
                Field[] fields = obj.getClass().getDeclaredFields();
                Field[] declaredFields = obj.getClass().getSuperclass().getDeclaredFields();
                field2String(stringBuilder, obj, fields);
                field2String(stringBuilder, obj, declaredFields);

            }
        }
        int and = stringBuilder.lastIndexOf("and");
        if (and>0){
            stringBuilder.delete(and,stringBuilder.length());
        }
        SearchHelper.startSearchBean(stringBuilder.toString());
        try {
            return point.proceed();
        }finally {
            SearchHelper.clearData();
        }
    }

    private void field2String(StringBuilder stringBuilder, Object obj, Field[] fieldList) throws JsonProcessingException, IllegalAccessException {
        //遍历所有变量
        for (Field field : fieldList) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation:annotations){
                //是搜索注解
                if (annotation instanceof SearchParam) {
                    //获取变量注解
                    SearchParam searchParam = (SearchParam) annotation;
                    String column = searchParam.column();
                    SearchParamEnum searchParamEnum = searchParam.symbol();
                    String dateFormat = searchParam.dateFormat();
                    String key = column;
                    //如果注解没有设置column 默认使用变量的下划线name
                    if (column.equals("")) {
                        String name = field.getName();
                        key = UnderLineUtil.toUnderlineJSONString(name);
                    }
                    //mysql需要时间格式化
                    if (!dateFormat.equals("")) {
                        StringBuilder str = new StringBuilder("DATE_FORMAT(");
                        str.append(key).append(",'").append(dateFormat).append("')");
                        key = str.toString();
                    }

                    //设置改变属性为可访问
                    field.setAccessible(true);
                    Object object = field.get(obj);
                    if (object!=null){
                        //处理枚举类型
                        if (object instanceof Enum){
                            Enum anEnum = (Enum) object;
                            String string = anEnum.string();
                            object = string;
                        }

                        if (searchParamEnum.equals(SearchParamEnum.equals)) {
                            stringBuilder.append(key)
                                    .append(searchParamEnum.getSymbol())
                                    .append("'").append(object)
                                    .append("'").append(" and ");
                        }else if (searchParamEnum.equals(SearchParamEnum.like)) {
                            stringBuilder.append(key)
                                    .append(" ")
                                    .append(searchParamEnum.getSymbol())
                                    .append(" ")
                                    .append("'%")
                                    .append(object)
                                    .append("%'")
                                    .append(" and ");
                        }else if (searchParamEnum.equals(SearchParamEnum.in)) {
                            //必须是list类型
                            if (object instanceof List) {
                                List objList = (List) object;
                                if (objList.size()!=0) {
                                    String join = StringUtils.join(objList, ",");
                                    stringBuilder.append(key).
                                            append(searchParamEnum.getSymbol())
                                            .append("(").append(join).append(")")
                                            .append(" and ");
                                }

                            }
                        }else if (searchParamEnum.equals(SearchParamEnum.between_and)) {
                            //必须是list类型
                            String str = String.valueOf(object);
                            String[] split = str.split(",");
                            stringBuilder.append(" ( ").append(key).
                                    append(" between '")
                                    .append(split[0]).append("' and '").append(split[1])
                                    .append("' ) ").append(" and ");
                        }
                    }
                }
            }

        }

    }

}
