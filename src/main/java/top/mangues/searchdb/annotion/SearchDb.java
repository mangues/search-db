package top.mangues.searchdb.annotion;

import java.lang.annotation.*;

/**
 * 需要搜索的注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SearchDb {

}