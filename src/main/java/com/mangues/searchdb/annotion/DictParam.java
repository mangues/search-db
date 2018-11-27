package com.mangues.searchdb.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要字典搜索
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface DictParam {
    //字典表
    String dictTable();
    //需要的字段
    String[] columns();
    //需要查询的字典字段
    String dictId() default "id";

}