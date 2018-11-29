package com.mangues.search.demo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.mangues.searchdb.annotion.DictParam;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mangues
 * @since 2018-11-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @DictParam(dictTable = "user",columns = {"name","password"},dictId="id")
    private Integer userId;

    /**
     * 订单编号
     */
    private String orderNum;

    private LocalDateTime createAt;

    /**
     * 订单状态
     */
    private Integer state;


}
