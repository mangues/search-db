package com.mangues.searchdb.aop.searchhandler;


import com.mangues.searchdb.annotion.SearchParamEnum;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/27 11:52 AM
 * @Description
 */
public interface SymbolHandler {
    /**
     *  获取普通搜索 搜索条件的sql
     * @param baseSb
     * @param key
     * @param object
     */
    void getNormalSymbol(StringBuilder baseSb, String key, Object object, SearchParamEnum searchParamEnum);
}
