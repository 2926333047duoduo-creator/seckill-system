package com.seckill.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdWorker {

    // 起始时间戳，建议固定一个时间点
    private static final long START_TIMESTAMP = 1640995200000L; // 2022-01-01

    // 每部分所占位数
    private static final long SEQUENCE_BITS = 12;
    private static final long MACHINE_BITS = 5;
    private static final long DATACENTER_BITS = 5;

    // 每部分的最大值
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BITS);
    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BITS);

    // 向左位移的位数
    private static final long MACHINE_LEFT = SEQUENCE_BITS;
    private static final long DATACENTER_LEFT = MACHINE_BITS + SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT = DATACENTER_BITS + MACHINE_BITS + SEQUENCE_BITS;

    // 数据中心ID + 机器ID
    private final long datacenterId;
    private final long machineId;

    private long sequence = 0L; // 当前毫秒的序列号
    private long lastTimestamp = -1L; // 上一次生成ID的时间戳

    public SnowflakeIdWorker(@Value("${snowflake.datacenter-id:1}") long datacenterId,
                             @Value("${snowflake.machine-id:1}") long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || machineId > MAX_MACHINE_NUM) {
            throw new IllegalArgumentException("DataCenter or MachineId too large");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 当前毫秒序列号用完了，等待下一毫秒
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L; // 新的一毫秒重置序列号
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (datacenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }

    private long waitNextMillis(long timestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
