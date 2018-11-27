package com.mangues.searchdb.aop.searchhandler;


import com.mangues.searchdb.annotion.SearchParamEnum;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/11/27 11:52 AM
 * @Description
 */
public class SymbolFactory {
    private static ConcurrentHashMap<String,SymbolHandler> concurrentHashMap = new ConcurrentHashMap();
    public static SymbolHandler getSymbolHandler(SearchParamEnum searchParamEnum) {
        String symbol = searchParamEnum.getSymbol();
        SymbolHandler symbolHandler = concurrentHashMap.get(symbol);
        if (symbolHandler==null) {
            synchronized (symbol.intern()) {
                 symbolHandler = concurrentHashMap.get(symbol);
                 if (symbolHandler==null) {
                     Class handlerClass = searchParamEnum.getHandlerClass();
                     try {
                         symbolHandler = (SymbolHandler)handlerClass.newInstance();
                         concurrentHashMap.put(symbol,symbolHandler);
                     } catch (InstantiationException e) {
                         e.printStackTrace();
                     } catch (IllegalAccessException e) {
                         e.printStackTrace();
                     }
                 }

            }
        }
        return symbolHandler;
    }
}
