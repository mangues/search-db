package com.mangues.search.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import top.mangues.searchdb.common.Enum;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrderStateEnum implements Enum {
    REFUND(-2,"已经退单"),
    NOFINISH(-1,"订单未完成锁定中"),
    NOSETTLEMWNT(0,"未结算"),
    SETTLEMWNT(1,"已结算");
    Integer code;
    String msg;

    private OrderStateEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static OrderStateEnum getByCode(Integer code){
        for (OrderStateEnum stateEnum: OrderStateEnum.values()){
            if (code.equals(stateEnum.code)){
                return stateEnum;
            }
        }
        return NOFINISH;
    }

    @Override
    public String string() {
        return code+"";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean equals(OrderStateEnum orderStateEnum){
        return orderStateEnum.code.equals(this.code);
    }
}
