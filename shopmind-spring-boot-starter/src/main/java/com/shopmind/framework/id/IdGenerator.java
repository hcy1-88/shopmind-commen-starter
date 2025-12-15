package com.shopmind.framework.id;

/**
 * ID生成器接口
 */
public interface IdGenerator {

    /**
     * 生成下一个ID
     *
     * @return 分布式唯一ID
     */
    long nextId();

    /**
     * 生成下一个ID（字符串形式）
     *
     * @return 分布式唯一ID字符串
     */
    default String nextIdStr() {
        return String.valueOf(nextId());
    }
}
