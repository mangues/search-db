package com.mangues.searchdb.aop;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mangues.searchdb.annotion.SearchDb;
import com.mangues.searchdb.annotion.SearchParam;
import com.mangues.searchdb.annotion.SearchParamEnum;
import com.mangues.searchdb.aop.searchhandler.SymbolFactory;
import com.mangues.searchdb.aop.searchhandler.SymbolHandler;
import com.mangues.searchdb.common.Enum;
import com.mangues.searchdb.common.SearchBean;
import com.mangues.searchdb.mybatis.SearchHelper;
import com.mangues.searchdb.util.UnderLineUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/24 10:36 AM
 * @Description 搜索Aop
 */
@Aspect
public class SearchDbAop {



    @Around("@annotation(searchDb)")
    public Object doHandle(ProceedingJoinPoint point, SearchDb searchDb) throws Throwable {
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;


        StringBuilder stringBuilder = new StringBuilder(" ");
//        Annotation[][] pas = msig.getMethod().getParameterAnnotations();
        /**
         * 存放 {dictColumn-table: {
         *              [
         *                  {
         *                      column:**,
         *                      data:**,
         *                      searchParamEnum:**
         *                  }
         *              ]
         *          }
         *      }
         */
        Map<String,List<Map>> dictListMap = new HashMap<>();
        Object[] args = point.getArgs();
        for (Object obj:args){
            //是搜索类
            if (obj instanceof SearchBean) {
                Field[] fields = obj.getClass().getDeclaredFields();
                Field[] declaredFields = obj.getClass().getSuperclass().getDeclaredFields();
                field2String(stringBuilder, obj, fields,dictListMap);
                field2String(stringBuilder, obj, declaredFields,dictListMap);

            }
        }

        //统一处理字典表数据匹配出的外键
        //总拼接
        SymbolHandler allSymbolHandler = SymbolFactory.getSymbolHandler(SearchParamEnum.in);
        for (Map.Entry<String,List<Map>> dictListEntry:
                dictListMap.entrySet()) {

            String dictListEntryKey = dictListEntry.getKey();
            List<Map> dictList = dictListEntry.getValue();
            String[] dictKeySplit = dictListEntryKey.split("-");
            String dictColumn = dictKeySplit[0];
            String table = dictKeySplit[1];
            StringBuilder dictSb = new StringBuilder("select ")
                    .append(" id ")
                    .append(" from ")
                    .append(table)
                    .append(" where ");

            dictList.stream().forEach(map -> {
                //字典表字段
                String column = (String) map.get("column");
                //字典表数据
                Object data = map.get("data");
                //字典匹配类型
                SearchParamEnum searchParamEnum = (SearchParamEnum)map.get("searchParamEnum");
                String dateFormat = (String) map.get("dateFormat");
                //mysql需要时间格式化
                if (!dateFormat.equals("")) {
                    StringBuilder str = new StringBuilder("DATE_FORMAT(");
                    str.append(column).append(",'").append(dateFormat).append("')");
                    column = str.toString();
                }
                SymbolHandler symbolHandler = SymbolFactory.getSymbolHandler(searchParamEnum);
                symbolHandler.getNormalSymbol(dictSb,column,data,searchParamEnum);
            });
            removeAnd(dictSb);
            //字典表查询结果
            List<Map<String, Object>> dictResultList = SqlRunner.db().selectList(dictSb.toString());
            List<Object> collect = dictResultList.stream().map(m -> {return m.get("id")+"";}).collect(Collectors.toList());
            //没有不查询
            if (collect.size()==0){
                collect.add("-1");

            }
            allSymbolHandler.getNormalSymbol(stringBuilder,dictColumn,collect,SearchParamEnum.in);
        }
        removeAnd(stringBuilder);
        SearchHelper.startSearchBean(stringBuilder.toString());
        try {
            return point.proceed();
        }finally {
            SearchHelper.clearData();
        }
    }

    private void field2String(StringBuilder stringBuilder, Object obj, Field[] fieldList,Map<String,List<Map>> dictListMap) throws JsonProcessingException, IllegalAccessException {
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
                    boolean isDictColumn = searchParam.isDictColumn();

                    //需要查询的字典表外键字段
                    String dictColumn = searchParam.dictColumn();
                    String dictTable = searchParam.dictTable();
                    String key = column;
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


                        //遍历出所有字典字段
                        if (isDictColumn) {
                            if (column.equals("")) {
                                String name = field.getName();
                                column = UnderLineUtil.toUnderlineJSONString(name);
                            }
                            String dictListMapKey = new StringBuilder(dictColumn).append("-").append(dictTable).toString();
                            List<Map> dictList = dictListMap.get(dictListMapKey);
                            if (dictList==null) {
                                dictList = new ArrayList<>();
                                dictListMap.put(dictListMapKey,dictList);
                            }

                            Map hashMap = new HashMap();
                            //字典表字段
                            hashMap.put("column",column);
                            //字典表数据
                            hashMap.put("data",object);
                            //字典匹配类型
                            hashMap.put("searchParamEnum",searchParamEnum);
                            hashMap.put("dateFormat",dateFormat);
                            dictList.add(hashMap);
                            return;
                        }


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

                        SymbolHandler symbolHandler = SymbolFactory.getSymbolHandler(searchParamEnum);
                        symbolHandler.getNormalSymbol(stringBuilder,key,object,searchParamEnum);
                    }
                }
            }

        }
    }


    private void removeAnd(StringBuilder stringBuilder){
        int and = stringBuilder.lastIndexOf("and");
        if (and>0){
            stringBuilder.delete(and,stringBuilder.length());
        }
    }

}
