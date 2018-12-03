package com.mangues.search.service.impl;

import com.mangues.search.entity.User;
import com.mangues.search.mapper.UserMapper;
import com.mangues.search.service.IUserService;
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
