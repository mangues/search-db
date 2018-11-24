package com.mangues.searchdb.demo.service.impl;

import com.mangues.searchdb.demo.entity.User;
import com.mangues.searchdb.demo.mapper.UserMapper;
import com.mangues.searchdb.demo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mangues
 * @since 2018-11-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
