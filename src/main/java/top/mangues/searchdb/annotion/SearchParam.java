package top.mangues.searchdb.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface SearchParam {
    // 数据库对应的查询列 默认字段的驼峰
    String column() default "";
    //数据库匹配类型
    SearchParamEnum symbol() default SearchParamEnum.equals;
    //数据库时间格式化匹配
    String dateFormat() default "";

    //是否字典字段
    boolean isDictColumn() default false;
    //需要查询的字典表外键字段
    String dictColumn() default "";
    //字典表
    String dictTable() default "";
}
