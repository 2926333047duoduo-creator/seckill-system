package com.seckill.backend.dto;


import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating/updating vouchers.
 * (Used to receive data from the frontend)
 */
@Data
public class VoucherDTO {
    private Long id;
    private String name;
    private Double amount;
    private Integer total;
    private Integer stock;
    private LocalDateTime startTime;
}
