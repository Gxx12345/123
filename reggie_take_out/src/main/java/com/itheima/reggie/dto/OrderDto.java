package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 *
 * @author my
 * @since 2022/10/5 23:04
 */
@Data
public class OrderDto implements Serializable {

    private Long id;
    //订单号
    private String number;

    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;
    //下单时间
    private LocalDateTime orderTime;
    //结账时间
    private LocalDateTime checkoutTime;
    //实收金额
    private BigDecimal amount;
    //备注
    private String remark;
    //用户名
    private String userName;
    //手机号
    private String phone;
    //地址
    private String address;
    //收货人
    private String consignee;
    /**
     * 订单详情
     */
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
