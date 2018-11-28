package com.mangues.search.demo.controller;


import com.mangues.search.demo.common.UserSearch;
import com.mangues.search.demo.service.IUserService;
import com.mangues.searchdb.annotion.SearchDb;
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
@RequestMapping("/demo/user")
public class UserController {
    @Autowired
    IUserService iUserService;


    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表")
    @SearchDb
    public Object orderList(UserSearch userSearch) {
        return iUserService.list();
    }

}
