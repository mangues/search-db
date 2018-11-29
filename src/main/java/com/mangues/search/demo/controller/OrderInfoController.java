package com.mangues.search.demo.controller;


import com.mangues.search.demo.common.OrderSearch;
import com.mangues.search.demo.entity.OrderInfo;
import com.mangues.search.demo.service.IOrderInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mangues.searchdb.annotion.DictSearch;
import top.mangues.searchdb.annotion.SearchDb;

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
        @SearchDb
        @DictSearch(resultClass = OrderInfo.class)
        public Object orderList(OrderSearch orderSearch) {
            return orderInfoService.list();
        }



}
