package com.seckill.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class VoucherOrder {
   private Long id;
    private String userId;
    private String voucherId;
    private LocalDateTime orderTime;
}
