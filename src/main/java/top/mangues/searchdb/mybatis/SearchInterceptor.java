package top.mangues.searchdb.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import top.mangues.searchdb.util.ReflectUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author: mangues【mangues@yeah.net】
 * @Description: mybatis 搜索拦拦截器
 */
@Intercepts({
        @Signature(
                method = "prepare",
                type = StatementHandler.class,
                args = {Connection.class, Integer.class}
        )
})
public class SearchInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //拿到当前绑定Sql的参数对象，就是我们在调用对应的Mapper映射语句时所传入的参数对象
//        Object obj = boundSql.getParameterObject();
        //这里我们简单的通过传入的是Page对象就认定它是需要进行分页操作的。
        String searchBean = SearchHelper.getSearchBean();

        if (searchBean!=null && searchBean.length()>1) {

            //        RoutingStatementHandler handler = (RoutingStatementHandler) realTarget(invocation.getTarget());
//        //通过反射获取到当前RoutingStatementHandler对象的delegate属性
//        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
            StatementHandler delegate = (StatementHandler) realTarget(invocation.getTarget());

            //获取到当前StatementHandler的 boundSql，这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，因为之前已经说过了
            //RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
            BoundSql boundSql = delegate.getBoundSql();


            //对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，
            //BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，
            //SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是
            //处理CallableStatement的。Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个
            //StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、
            //PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。
            //我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候
            //是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。

            //通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
            //拦截到的prepare方法参数是一个Connection对象
//            Connection connection = (Connection) invocation.getArgs()[0];
            //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
            String sql = boundSql.getSql();
            String pageSql = sql;
            //获取分页Sql语句
            pageSql = this.getSql(sql, new StringBuilder(searchBean.replaceAll("\"","")));
            //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
            ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
        }
        try {
            return invocation.proceed();
        }finally {
            SearchHelper.clearData();
        }

    }


    private String getSql(String sql, StringBuilder needStrsb) {
        StringBuffer sqlSb = new StringBuffer(sql);
        //计算第一条记录的位置
        String s = sql.toLowerCase();
        int where = s.lastIndexOf("where");
        //不存在
        if (where == -1) {
            int group = s.lastIndexOf("group by");
            needStrsb.insert(0," where ");
            //不存在
            if (group == -1) {
                int limit = s.lastIndexOf("limit");
                //不存在
                if (limit == -1) {
                    sqlSb.append(needStrsb);
                } else {
                    sqlSb.insert(limit - 1, needStrsb);
                }
            } else {
                sqlSb.insert(group - 1, needStrsb);
            }


        } else {
            sqlSb.insert(where + 5, needStrsb.append("and "));
        }


        return sqlSb.toString();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


    public static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }

}
