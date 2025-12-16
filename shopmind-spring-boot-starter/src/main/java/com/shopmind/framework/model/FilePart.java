package com.shopmind.framework.model;

import lombok.Data;

/**
 * Description: 文件分片
 * Author: huangcy
 * Date: 2025-12-16
 */
@Data
public class FilePart {
    private Integer partNumber;
    private String eTag;
}
