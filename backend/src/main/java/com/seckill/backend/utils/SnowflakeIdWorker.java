package com.seckill.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdWorker {

    // Start timestamp (fixed base time for ID generation)
    private static final long START_TIMESTAMP = 1640995200000L; // 2022-01-01

    // Bit allocation for each part
    private static final long SEQUENCE_BITS = 12;
    private static final long MACHINE_BITS = 5;
    private static final long DATACENTER_BITS = 5;

    // Maximum values for each part
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BITS);
    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BITS);

    // Bit shifts for each part
    private static final long MACHINE_LEFT = SEQUENCE_BITS;
    private static final long DATACENTER_LEFT = MACHINE_BITS + SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT = DATACENTER_BITS + MACHINE_BITS + SEQUENCE_BITS;

    // Data center ID + Machine ID
    private final long datacenterId;
    private final long machineId;

    private long sequence = 0L; // Sequence number within the same millisecond
    private long lastTimestamp = -1L; // Last generated timestamp

    public SnowflakeIdWorker(@Value("${snowflake.datacenter-id:1}") long datacenterId,
                             @Value("${snowflake.machine-id:1}") long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || machineId > MAX_MACHINE_NUM) {
            throw new IllegalArgumentException("DataCenter or MachineId is too large");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * Generate the next unique ID (thread-safe)
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence exhausted within current millisecond, wait for the next one
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L; // Reset sequence for the new millisecond
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (datacenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }

    /**
     * Wait until the next millisecond
     */
    private long waitNextMillis(long timestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
