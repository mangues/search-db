package com.mangues.searchdb.annotion;

import com.mangues.searchdb.common.Ignore;

import java.lang.annotation.*;

/**
 * 需要搜索的注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SearchDb {
    Class resultClass() default Ignore.class;
}