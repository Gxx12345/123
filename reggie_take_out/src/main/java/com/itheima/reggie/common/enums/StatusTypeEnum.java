package com.itheima.reggie.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gmy
 * @since 2022/10/5 16:11
 */
public enum StatusTypeEnum {
    DISABLED("0", "禁用/停售", 0),
    ENABLED("1", "启用/启售", 1);

    /**
     * 状态集合
     */
    public static final Map<String, StatusTypeEnum> STATUS_TYPE_ENUM_MAP;

    static {
        STATUS_TYPE_ENUM_MAP = new HashMap<>();
        for (StatusTypeEnum value : StatusTypeEnum.values()) {
            STATUS_TYPE_ENUM_MAP.put(value.strType, value);
        }
    }

    private String strType;

    private Integer intType;

    private String name;

    StatusTypeEnum(String strType, String name, Integer intType) {
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
