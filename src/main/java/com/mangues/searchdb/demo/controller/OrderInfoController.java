package com.mangues.searchdb.demo.controller;


import com.mangues.searchdb.annotion.SearchDb;
import com.mangues.searchdb.demo.common.OrderSearch;
import com.mangues.searchdb.demo.service.IOrderInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        @SearchDb
        public Object orderList(OrderSearch orderSearch) {
            return orderInfoService.list();
        }
}
