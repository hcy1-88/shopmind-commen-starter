package com.shopmind.framework.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Description: 文件对象
 * Author: huangcy
 * Date: 2025-12-16
 */
@Data
public class FileObject {
    private String fileName;
    private String fileUrl;
    private String size;
    private LocalDateTime lastModifiedTime;
}
