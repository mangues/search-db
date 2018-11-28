package com.mangues.search.demo.service.impl;

import com.mangues.search.demo.entity.OrderInfo;
import com.mangues.search.demo.mapper.OrderInfoMapper;
import com.mangues.search.demo.service.IOrderInfoService;
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
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

}
