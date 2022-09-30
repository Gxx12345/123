package com.alibaba.reggie.service.impl;

import com.alibaba.reggie.entity.AddressBook;
import com.alibaba.reggie.mapper.AddressBookMapper;
import com.alibaba.reggie.service.IAddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * AddressBook
 *
 * @author cyberengr
 * @since 2022/9/30 9:18
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
}
