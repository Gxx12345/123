package com.itheima.ruji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;;
import com.itheima.ruji.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址簿
 *
 * @author t3rik
 * @since 2022/7/29 11:22
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
