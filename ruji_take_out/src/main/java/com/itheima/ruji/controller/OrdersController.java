package com.itheima.ruji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.ruji.common.AntPathmathcherSS;
import com.itheima.ruji.common.BaseTreadlock;
import com.itheima.ruji.common.CustomException;
import com.itheima.ruji.common.R;
import com.itheima.ruji.dto.OrderdsDto;
import com.itheima.ruji.entity.Orders;
import com.itheima.ruji.entity.ShoppingCart;
import com.itheima.ruji.entity.User;
import com.itheima.ruji.service.IOrdersService;
import com.itheima.ruji.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 订单控制层
 *
 * @author Gzz
 * @since 2022/9/30 17:21
 */

@RestController
@RequestMapping("/order")
@Slf4j
@Api(tags = "订单控制层")
public class OrdersController {
    @Autowired
    private IOrdersService iOrdersService;

    @PostMapping("/submit")
    public R<String> submin(@RequestBody Orders ordersParam){
        log.info("订单数据：{}",ordersParam);
        iOrdersService.submit(ordersParam);
        return R.success("下单成功");
    }

    /**
     * 服务端查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "订单分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "number", value = "订单号", required = false),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false)
    })
    public R<Page<Orders>> order(Integer page,Integer pageSize, String number, String beginTime, String endTime){
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException("传入数据有误");
        }
        Page<Orders> page1 = this.iOrdersService.getPage(page, pageSize, number, beginTime, endTime);
        return R.success(page1);
    }


    /**
     * 查询C端订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("userPage")
    @ApiOperation(value = "C端用户历史订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true)
    })
    public R<Page<OrderdsDto>>orderC(Integer page, Integer pageSize){
        if (page == null || pageSize == null || page <= 0 || pageSize <= 0) {
            throw new CustomException("错误");
        }
        return R.success(this.iOrdersService.getUserPage(page, pageSize));
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更改订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orders", value = "订单信息", required = true)
    })
    public R<String>updateStatus(@RequestBody Orders orders){
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.FAILED));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orders.getUserId()!=null,Orders::getStatus,orders.getStatus());
            Orders ord = new Orders();
            BeanUtils.copyProperties(orders,ord);
            ord.setStatus(orders.getStatus());
            iOrdersService.update(ord, wrapper);
    return R.success(AntPathmathcherSS.FINISH);
    }
    @PostMapping("/again")
    @ApiOperation(value = "再来一单")
    public R<String> againOrder(@RequestBody Orders orders) {
        Optional.ofNullable(orders.getId())
                .orElseThrow(() -> new CustomException(AntPathmathcherSS.FAILED));
        boolean finished = this.iOrdersService.againOrder(orders);
        return finished ? R.success(AntPathmathcherSS.FINISH) : R.error(AntPathmathcherSS.FINISH);
    }
}
