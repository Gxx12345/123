package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.enums.OrderTypeEnum;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

/**
 * 订单控制器
 *
 * @author Gmy
 * @since 2022/9/30 17:01
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("前后端联通");
        this.orderService.submit(orders);
        return R.success(GlobalConstant.FINISH);
    }

    @GetMapping("/page")
    @ApiOperation(value = "订单分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", readOnly = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", readOnly = true),
            @ApiImplicitParam(name = "number", value = "订单号", readOnly = true),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", readOnly = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", readOnly = true),
    })
    public R<Page<Orders>> getPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException(GlobalConstant.ERROR_PARAM);
        }
        return R.success(this.orderService.getPage(page, pageSize, number, beginTime, endTime));
    }

    @PutMapping
    @ApiOperation(value = "更改订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orders", value = "订单信息", readOnly = true)
    })
    public R<String> updateStatus(@RequestBody Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        if (OrderTypeEnum.ORDER_TYPE_ENUM_MAP.get(String.valueOf(orders.getStatus())) == null) {
            throw new CustomException(GlobalConstant.ERROR_PARAM);
        }
        boolean finished = this.orderService.updateById(orders);
        return finished ? R.success(GlobalConstant.FINISH) : R.error(GlobalConstant.FAILED);
    }

    @GetMapping("/userPage")
    @ApiOperation(value = "C端用户历史订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true)
    })
    public R<Page<OrderDto>> getUserPage(Integer page, Integer pageSize) {
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException(GlobalConstant.ERROR_PARAM);
        }
        return R.success(this.orderService.getUserPage(page, pageSize));
    }

    @PostMapping("/again")
    @ApiOperation(value = "再来一单")
    public R<String> againOrder(@RequestBody Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(GlobalConstant.ERROR_PARAM));
        boolean finished = this.orderService.againOrder(orders);
        return finished ? R.success(GlobalConstant.FINISH) : R.error(GlobalConstant.FAILED);
    }
}
