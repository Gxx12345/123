package com.itheima.ruji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.ruji.entity.AddressBook;
import com.itheima.ruji.mapper.AddressBookMapper;
import com.itheima.ruji.service.IAddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author t3rik
 * @since 2022/7/28 17:15
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
}
