package top.mangues.searchdb.aop;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.mangues.searchdb.annotion.SearchDb;
import top.mangues.searchdb.annotion.SearchParam;
import top.mangues.searchdb.annotion.SearchParamEnum;
import top.mangues.searchdb.aop.searchhandler.SymbolFactory;
import top.mangues.searchdb.aop.searchhandler.SymbolHandler;
import top.mangues.searchdb.common.Enum;
import top.mangues.searchdb.common.SearchBean;
import top.mangues.searchdb.mybatis.SearchHelper;
import top.mangues.searchdb.util.ClassUtil;
import top.mangues.searchdb.util.UnderLineUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/24 10:36 AM
 * @Description 搜索Aop
 */
@Aspect
@Component
public class SearchDbAop {



    @Around("@annotation(searchDb)")
    public Object doHandle(ProceedingJoinPoint point, SearchDb searchDb) throws Throwable {
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;


        StringBuilder stringBuilder = new StringBuilder(" ");


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
        Object target = point.getTarget();
        Map<String,List<Map>> dictListMap = new HashMap<>();
        Object[] args = point.getArgs();
        Parameter[] parameters = msig.getMethod().getParameters();
        for (int i=0;i<parameters.length;i++){
            Parameter parameter = parameters[i];
            Class<?> paramClazz = parameter.getType();
            Object obj = null;
            try {
                obj = Arrays.stream(args).filter(ar -> paramClazz.isAssignableFrom(ar.getClass())).findFirst().get();
            }catch (Exception e) {
                break;
            }
            //是搜索类
            if (obj instanceof SearchBean) {
                Field[] fields = obj.getClass().getDeclaredFields();
                Field[] declaredFields = obj.getClass().getSuperclass().getDeclaredFields();
                field2String(stringBuilder, obj, fields,dictListMap);
                field2String(stringBuilder, obj, declaredFields,dictListMap);
                //基本类型
            }else if(ClassUtil.isBasic(parameter.getType())){
                boolean annotationPresent = parameter.isAnnotationPresent(SearchParam.class);
                if (annotationPresent) {
                    SearchParam searchParam = parameter.getAnnotation(SearchParam.class);
                    String name = parameter.getType().getName();
                    coreHandler(stringBuilder, dictListMap, searchParam, name, obj);
                }
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
            boolean annotationPresent = field.isAnnotationPresent(SearchParam.class);
            if (annotationPresent) {
                //获取变量注解
                SearchParam searchParam = field.getAnnotation(SearchParam.class);
                //设置改变属性为可访问
                field.setAccessible(true);
                String name = field.getName();
                Object object = field.get(obj);
                coreHandler(stringBuilder, dictListMap, searchParam, name, object);
            }
        }
    }

    /**
     * filed注解处理方法
     * @param stringBuilder
     * @param dictListMap
     * @param searchParam
     * @param name
     * @param object
     * @throws JsonProcessingException
     */
    private void coreHandler(StringBuilder stringBuilder, Map<String, List<Map>> dictListMap, SearchParam searchParam, String name, Object object) throws JsonProcessingException {
        String column = searchParam.column();
        SearchParamEnum searchParamEnum = searchParam.symbol();
        String dateFormat = searchParam.dateFormat();
        boolean isDictColumn = searchParam.isDictColumn();
        //需要查询的字典表外键字段
        String dictColumn = searchParam.dictColumn();
        String dictTable = searchParam.dictTable();
        String key = column;

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
            }

            //如果注解没有设置column 默认使用变量的下划线name
            if (column.equals("")) {
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


    private void removeAnd(StringBuilder stringBuilder){
        int and = stringBuilder.lastIndexOf("and");
        if (and>0){
            stringBuilder.delete(and,stringBuilder.length());
        }
    }

}
