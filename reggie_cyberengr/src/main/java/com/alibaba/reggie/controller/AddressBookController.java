package com.alibaba.reggie.controller;

import com.alibaba.reggie.common.BaseContext;
import com.alibaba.reggie.common.GlobalConstant;
import com.alibaba.reggie.common.Result;
import com.alibaba.reggie.entity.AddressBook;
import com.alibaba.reggie.service.IAddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AddressBook
 *
 * @author cyberengr
 * @since 2022/9/30 9:20
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 新增
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<AddressBook> saveOne(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getSetThreadLocalCurrentId());
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getSetThreadLocalCurrentId())
                .set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 查询单个地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (id == null || addressBook == null || addressBook.getIsDeleted() == 1) {
            return Result.error("没有找到该对象");
        }
        return Result.success(addressBook);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getSetThreadLocalCurrentId())
                    .eq(AddressBook::getIsDeleted,0)
                    .eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return Result.error("没有找到该对象");
        } else {
            return Result.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getSetThreadLocalCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId())
                .eq(AddressBook::getIsDeleted,0)
                .orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return Result.success(addressBookService.list(queryWrapper));
    }

    /**
     * 删除用户地址
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper();
        wrapper.in(CollectionUtils.isNotEmpty(ids), AddressBook::getId, ids)
                .set(AddressBook::getIsDeleted, 1);
        addressBookService.update(wrapper);
        return Result.success(GlobalConstant.FINISHED);
    }

    /**
     * 修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success(GlobalConstant.FINISHED);
    }

}