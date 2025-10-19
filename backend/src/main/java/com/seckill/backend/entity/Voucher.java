package com.seckill.backend.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Voucher {
    private String id;
    private String name;
    private Double amount;
    private Integer total;
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime createTime;
}
