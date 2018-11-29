package com.mangues.search.demo.common;

import lombok.Data;
import top.mangues.searchdb.annotion.SearchParam;
import top.mangues.searchdb.annotion.SearchParamEnum;
import top.mangues.searchdb.common.SearchBean;

@Data
public class UserSearch implements SearchBean {
    @SearchParam(column = "name",symbol = SearchParamEnum.like)
    private String username;

}
