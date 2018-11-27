package com.mangues.searchdb.aop;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangues.searchdb.annotion.DictParam;
import com.mangues.searchdb.annotion.DictSearch;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/24 10:36 AM
 * @Description 字典自动查询Aop
 */
@Aspect
@Component
public class DictSearchAop {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    SqlSession sqlSession;


    @Around("@annotation(dictSearch)")
    public Object doHandle(ProceedingJoinPoint point, DictSearch dictSearch) throws Throwable {
//        Signature sig = point.getSignature();
//        MethodSignature msig = (MethodSignature) sig;
        Object obj = point.proceed();
        Field[] declaredFields = dictSearch.resultClass().getDeclaredFields();
        // {
        //   field.name : {
        //                 0: select *,* from table where,
        //                 1:  dictId
        //}
        Map<String, Map<Integer,StringBuilder>> runSqlMap = getRunSqlMap(declaredFields);

        // {
        //    field.name : field:value
        //}
        Map<String, List<Object>> variableMap = new HashMap<>();

        //获取匹配参数
        if (obj instanceof List) {
            List listObj = (List) obj;
            for (Object object : listObj) {
                field2Map(object, declaredFields, variableMap);
            }
        } else {
            field2Map(obj, declaredFields, variableMap);
        }


        //变量名，变量值 -> 变量值对应的对象
        /**
         * {
         *     filed.name: {
         *                    filed.value:{dictModel}
         *                  }
         * }
         *
         */
        Map keyMap = new HashMap();
        //拼接查询条件
        for (Map.Entry<String, List<Object>> map : variableMap.entrySet()) {
            //字段参数
            String key = map.getKey();
            List<Object> value = map.getValue();
            if (!ObjectUtils.isEmpty(value)) {

                Map<Integer, StringBuilder> runSqlContentMap = runSqlMap.get(key);
                StringBuilder stringBuilder = runSqlContentMap.get(0);
                if (stringBuilder != null) {
                    String join = StringUtils.join(value, ",");
                    stringBuilder.append("(").append(join).append(")");
                    List<Map<String, Object>> resultMapList = SqlRunner.db().selectList(stringBuilder.toString());
                    if (resultMapList != null && resultMapList.size() > 0) {
                        Map<Object, List<Map<String, Object>>> collect = resultMapList.stream().collect(Collectors.groupingBy(o -> {
                           return String.valueOf(o.get(runSqlContentMap.get(1).toString()));
                        }));
                        keyMap.put(key, collect);
                    }
                }
            }
        }





        if (obj instanceof List) {
            List listObj = (List) obj;
            List resultList = new ArrayList();
            for (Object object : listObj) {
                JSONObject jsonObject = variable2Map(object, declaredFields, keyMap);
                resultList.add(jsonObject);
            }
            return resultList;
        } else {
            JSONObject jsonObject = variable2Map(obj, declaredFields, keyMap);
            return jsonObject;
        }
    }


    //获取搜索条件
    //字典表查询sql
    // {
    //   field.name : {
    //                 0: select *,* from table where,
    //                 1:  dictId
    //}
    private  Map<String,Map<Integer,StringBuilder>> getRunSqlMap(Field[] fieldList) throws Exception {
        Map<String,Map<Integer,StringBuilder>> resultMap = new HashMap<>();
        //遍历所有变量
        for (Field field : fieldList) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation:annotations){
                //是搜索注解
                if (annotation instanceof DictParam) {
                    StringBuilder stringBuilder = new StringBuilder("select ");
                    //获取变量注解
                    DictParam searchParam = (DictParam) annotation;
                    String dictId = searchParam.dictId();
                    String dictTable = searchParam.dictTable();
                    String[] columns = searchParam.columns();
                    String join = StringUtils.join(Arrays.asList(columns), ",");
                    stringBuilder.append(dictId).append(",").append(join).append(" from ").append(dictTable).append(" where ").append(dictId).append(" in ");
                    Map<Integer,StringBuilder> contentMap = new HashMap<>();
                    contentMap.put(0,stringBuilder);
                    contentMap.put(1,new StringBuilder(dictId));
                    resultMap.put(field.getName(),contentMap);
                }
            }
        }
        return resultMap;
    }


    //获取匹配参数
    // {
    //    field.name : List<field.value>
    //}
    private void field2Map(Object obj, Field[] fieldList,Map<String,List<Object>> variableMap) throws JsonProcessingException, IllegalAccessException {
        //遍历所有变量
        for (Field field : fieldList) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                //是搜索注解
                if (annotation instanceof DictParam) {
                    //设置改变属性为可访问
                    field.setAccessible(true);
                    Object object = field.get(obj);
                    if (object != null) {
                        String name = field.getName();
                        List<Object> objectList = variableMap.get(name);
                        if (objectList == null) {
                            objectList = new ArrayList<>();
                            variableMap.put(name, objectList);
                        }
                        objectList.add(object);
                    }
                }
            }

        }
    }


    //值变对象
    //变量名，变量值 -> 变量值对应的对象
    private JSONObject variable2Map(Object obj, Field[] fieldList,Map<String,Map<Object, List<Map<String, Object>>>> variableMap) throws Exception{

        String toJSONString = mapper.writeValueAsString(obj);
        JSONObject jsonObject = mapper.readValue(toJSONString, JSONObject.class);


        for (Field field : fieldList) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                //是搜索注解
                if (annotation instanceof DictParam) {
                    //设置改变属性为可访问
                    field.setAccessible(true);
                    DictParam searchParam = (DictParam) annotation;
                    String dictId = searchParam.dictId();
                    Object object = field.get(obj);
                    if (object != null) {
                        String name = field.getName();
                        Map<Object, List<Map<String, Object>>> objectListMap = variableMap.get(name);
                        if (objectListMap!=null){
                            List<Map<String, Object>> maps = objectListMap.get(String.valueOf(object));
                            if (maps!=null&&maps.size()>0) {
                                Map<String, Object> stringObjectMap = maps.get(0);
                                jsonObject.put(name+"DictMap",stringObjectMap);
                            }
                        }
                    }
                }
            }
        }
        return jsonObject;

    }

}
