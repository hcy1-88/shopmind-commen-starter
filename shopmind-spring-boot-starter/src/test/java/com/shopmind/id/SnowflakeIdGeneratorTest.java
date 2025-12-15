package com.shopmind.id;

import com.shopmind.framework.id.IdGenerator;
import com.shopmind.framework.id.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snowflake ID生成器测试类
 */
class SnowflakeIdGeneratorTest {

    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new SnowflakeIdGenerator(1, 1);
    }

    @Test
    void testNextId() {
        long id = idGenerator.nextId();
        assertTrue(id > 0, "生成的ID应该大于0");
        System.out.println("生成的ID: " + id);
    }

    @Test
    void testNextIdStr() {
        String idStr = idGenerator.nextIdStr();
        assertNotNull(idStr);
        assertFalse(idStr.isEmpty());
        System.out.println("生成的ID字符串: " + idStr);
    }

    @Test
    void testIdUniqueness() {
        Set<Long> ids = new HashSet<>();
        int count = 10000;

        for (int i = 0; i < count; i++) {
            long id = idGenerator.nextId();
            assertTrue(ids.add(id), "ID应该是唯一的: " + id);
        }

        assertEquals(count, ids.size(), "应该生成指定数量的唯一ID");
        System.out.println("成功生成 " + count + " 个唯一ID");
    }

    @Test
    void testIdIncreasing() {
        long previousId = 0;
        int count = 1000;

        for (int i = 0; i < count; i++) {
            long currentId = idGenerator.nextId();
            assertTrue(currentId > previousId, "ID应该是递增的");
            previousId = currentId;
        }

        System.out.println("ID递增测试通过");
    }

    @Test
    void testConcurrentIdGeneration() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> ids = new HashSet<>();
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        long id = idGenerator.nextId();
                        synchronized (ids) {
                            if (!ids.add(id)) {
                                errorCount.incrementAndGet();
                                System.err.println("发现重复ID: " + id);
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        assertEquals(0, errorCount.get(), "不应该有重复的ID");
        assertEquals(threadCount * idsPerThread, ids.size(), "应该生成指定数量的唯一ID");
        System.out.println("并发测试通过，成功生成 " + ids.size() + " 个唯一ID");
    }

    @Test
    void testDifferentWorkerIds() {
        IdGenerator generator1 = new SnowflakeIdGenerator(1, 1);
        IdGenerator generator2 = new SnowflakeIdGenerator(2, 1);

        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            ids.add(generator1.nextId());
            ids.add(generator2.nextId());
        }

        assertEquals(2000, ids.size(), "不同workerId的生成器应该生成不同的ID");
        System.out.println("不同workerId测试通过");
    }

    @Test
    void testInvalidWorkerId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(32, 1);
        }, "workerId超出范围应该抛出异常");

        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(-1, 1);
        }, "workerId为负数应该抛出异常");
    }

    @Test
    void testInvalidDatacenterId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(1, 32);
        }, "datacenterId超出范围应该抛出异常");

        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(1, -1);
        }, "datacenterId为负数应该抛出异常");
    }

    @Test
    void testPerformance() {
        int count = 100000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            idGenerator.nextId();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double throughput = (count * 1000.0) / duration;

        System.out.println("生成 " + count + " 个ID耗时: " + duration + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " IDs/秒");
        assertTrue(duration < 10000, "生成10万个ID应该在10秒内完成");
    }
}
