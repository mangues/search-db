package com.mangues.search.demo.common;

import com.mangues.searchdb.annotion.SearchParam;
import com.mangues.searchdb.annotion.SearchParamEnum;
import com.mangues.searchdb.common.SearchBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSearch implements SearchBean {
    @SearchParam(column = "order_num",symbol = SearchParamEnum.equals)
    @ApiModelProperty(value = "订单编号")
    private String orderNum;

    @SearchParam(column = "state",symbol = SearchParamEnum.equals)
    @ApiModelProperty(value = "订单状态")
    private OrderStateEnum orderStateEnum;


    @SearchParam(column = "create_at",symbol = SearchParamEnum.between_and,dateFormat = "%Y-%m-%d")
    @ApiModelProperty(value = "订单时间范围 2018-12-11,2018-12-24")
    private String createDate;


    @SearchParam(column = "name",symbol = SearchParamEnum.like,isDictColumn = true,dictColumn = "user_id",dictTable = "user")
    private String userName;

}
