package com.mangues.search.common;

import top.mangues.searchdb.annotion.SearchParam;
import top.mangues.searchdb.annotion.SearchParamEnum;
import top.mangues.searchdb.common.SearchBean;
import lombok.Data;

@Data
public class UserSearch implements SearchBean {
    @SearchParam(column = "password",symbol = SearchParamEnum.like)
    private String password;

}
