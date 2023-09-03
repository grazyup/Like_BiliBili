package com.grazy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/3 14:52
 * @Description:
 */

@Data
@AllArgsConstructor
public class PageResult<T> {

    //数据总数
    private Integer total;

    //查询数据列表
    private List<T> records;
}
