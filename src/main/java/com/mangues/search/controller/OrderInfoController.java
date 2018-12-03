package com.mangues.search.controller;


import top.mangues.searchdb.annotion.SearchDb;
import top.mangues.searchdb.annotion.SearchParam;
import top.mangues.searchdb.annotion.SearchParamEnum;
import com.mangues.search.common.OrderSearch;
import com.mangues.search.entity.OrderInfo;
import com.mangues.search.service.IOrderInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mangues
 * @since 2018-11-24
 */
@RestController
@RequestMapping("/demo/order-info")
public class OrderInfoController {

        @Autowired
        IOrderInfoService orderInfoService;

        @GetMapping("/list")
        @ApiOperation(value = "获取订单列表")
        @SearchDb(resultClass = OrderInfo.class)
        public Object orderList(OrderSearch orderSearch,@RequestParam @SearchParam(column = "order_num",symbol = SearchParamEnum.like) String orderNum) {
            return orderInfoService.list();
        }



}
