package com.alibaba.reggie.controller;

import com.alibaba.reggie.service.IOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单明细
 *
 * @author cyberengr
 * @since 2022/9/30 10:23
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private IOrderDetailService orderDetailService;
}
