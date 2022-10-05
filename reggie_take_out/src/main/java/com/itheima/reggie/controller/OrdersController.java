package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.GlobalConstant;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.IOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 订单控制层
 *
 * @author yjiiie6
 * @since 2022/10/1 10:36
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {


    @Autowired
    private IOrdersService iOrdersService;


    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("前后端联通");
        iOrdersService.submit(orders);
        return R.success(GlobalConstant.FINISH);
    }

    /**
     * 订单明细分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException("传入的参数有误");
        }
        Page<Orders> result = iOrdersService.getPage(page, pageSize, number, beginTime, endTime);

        return R.success(result);
    }

    /**
     * 更改订单状态
     *
     * @return
     */
    @PutMapping
    public R<String> status(@RequestBody Orders orders) {
        log.info("前后端联通");
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orders != null, Orders::getId, orders.getId());

        Orders one = iOrdersService.getOne(queryWrapper);
        one.setStatus(orders.getStatus());
        iOrdersService.updateById(one);
        return R.success(GlobalConstant.FINISH);
    }


    /**
     * 个人中心 历史订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> CPage(Integer page, Integer pageSize) {
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException("传入的参数有误");
        }
        return R.success(iOrdersService.getUserPage(page, pageSize));
    }


    /**
     * 再来一单
     *
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> againOrder(@RequestBody Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException("传入的参数有误"));
        boolean finished = iOrdersService.againOrder(orders);
        return finished ? R.success(GlobalConstant.FINISH) : R.error(GlobalConstant.FAILED);
    }
}
