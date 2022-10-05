package com.itheima.reggie.common.enums;

/**
 * @author Gmy
 * @since 2022/10/5 16:23
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态
 *
 * @author t3rik
 * @since 2022/9/28 16:59
 */
public enum OrderTypeEnum {
    WAIT_PAY("1", "待付款", 1),
    WAIT_DELIVERED("2", "待派送", 2),
    DELIVERED("3", "已派送", 3),
    FINISHED("4", "已完成", 4),
    CANCEL("5", "已取消", 5);

    /**
     * 状态集合
     */
    public static final Map<String, OrderTypeEnum> ORDER_TYPE_ENUM_MAP;

    static {
        ORDER_TYPE_ENUM_MAP = new HashMap<>();
        for (OrderTypeEnum value : OrderTypeEnum.values()) {
            ORDER_TYPE_ENUM_MAP.put(value.strType, value);
        }
    }

    private String strType;

    private Integer intType;

    private String name;

    OrderTypeEnum(String strType, String name, Integer intType) {
        this.strType = strType;
        this.name = name;
        this.intType = intType;
    }

    public Integer getIntType() {
        return intType;
    }

    public void setIntType(Integer intType) {
        this.intType = intType;
    }

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

