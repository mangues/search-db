package top.mangues.searchdb.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mangues.searchdb.annotion.SearchDb;
import top.mangues.searchdb.util.DictSearchHandler;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/24 10:36 AM
 * @Description 字典自动查询Aop
 */
@Aspect
@Component
public class DictSearchAop {

    @Autowired
    private DictSearchHandler dictSearchHandler;

    @Around("@annotation(searchDb)")
    public Object doHandle(ProceedingJoinPoint point, SearchDb searchDb) throws Throwable {
//        Signature sig = point.getSignature();
//        MethodSignature msig = (MethodSignature) sig;
        Object obj = point.proceed();
        Class aClass = searchDb.resultClass();
        return dictSearchHandler.wrap(obj,aClass);
    }
}
