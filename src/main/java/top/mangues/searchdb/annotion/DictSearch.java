package top.mangues.searchdb.annotion;

import java.lang.annotation.*;

/**
 * 需要字典搜索
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DictSearch {
    Class resultClass();
}