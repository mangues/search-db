package com.mangues.searchdb.mybatis;

public class SearchHelper {
    private static final ThreadLocal<String> LOCAL_PAGE = new ThreadLocal<String>();

    public static void startSearchBean(String authList) {
        LOCAL_PAGE.set(authList);
    }

    public static String getSearchBean() {
        return LOCAL_PAGE.get();
    }

    /**
     * 清除数据源类型
     */
    public static void clearData() {
        LOCAL_PAGE.remove();
    }

}
