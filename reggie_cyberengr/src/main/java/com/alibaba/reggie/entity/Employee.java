package com.alibaba.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("员工实体")
@Data
public class Employee implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    //@TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    @ApiModelProperty("用户名")
    private String username;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    private Short status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
