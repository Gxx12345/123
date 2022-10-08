package com.itheima.reggie.utils;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * dozer
 *
 * @author wangxu
 * @create 2019-08-01 22:04
 */
public class DozerUtils {

    private DozerUtils() {
    }

    private static Mapper dozer = DozerBeanMapperBuilder.buildDefault();

    /**
     * 转换对象
     *
     * @param source           源数据
     * @param destinationClass 要转换的类型
     * @return 结果
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }

    /**
     * 转换集合对象
     */
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        List<T> destinationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceList)) {
            return destinationList;
        }
        for (Object source : sourceList) {
            T destinationObject = dozer.map(source, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 拷贝对象
     */
    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }
}
