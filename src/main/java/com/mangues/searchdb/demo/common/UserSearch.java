package com.mangues.searchdb.demo.common;

import com.mangues.searchdb.annotion.SearchParam;
import com.mangues.searchdb.annotion.SearchParamEnum;
import com.mangues.searchdb.common.SearchBean;
import lombok.Data;

@Data
public class UserSearch implements SearchBean {
    @SearchParam(column = "name",symbol = SearchParamEnum.like)
    private String username;

}
