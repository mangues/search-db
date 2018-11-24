package com.mangues.searchdb.demo.service.impl;

import com.mangues.searchdb.demo.entity.OrderInfo;
import com.mangues.searchdb.demo.mapper.OrderInfoMapper;
import com.mangues.searchdb.demo.service.IOrderInfoService;
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
