package com.shopmind.framework.context;

import lombok.Builder;
import lombok.Data;

/**
 * Description: 分页
 * Author: huangcy
 * Date: 2025-12-31
 */
@Data
@Builder
public class PageResult<T> {
    private T data;
    private Long total;
    private Long pageNumber;
    private Long pageSize;
}
