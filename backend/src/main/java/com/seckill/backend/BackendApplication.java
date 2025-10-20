package com.seckill.backend;

import com.seckill.backend.entity.Voucher;
import com.seckill.backend.mapper.VoucherMapper;
import jakarta.annotation.PostConstruct;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@SpringBootApplication
@MapperScan("com.seckill.backend.mapper")
@Import(RocketMQAutoConfiguration.class)
public class BackendApplication {

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Register BCryptPasswordEncoder as a bean
     * for password encryption in the login module.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Automatically executed after the Spring context is initialized.
     * Load all vouchers (with stock > 0) from the database into Redis on startup.
     * This prevents key loss when Redis restarts or flushes data.
     */
    @PostConstruct
    public void preloadVoucherStock() {
        // 1. Fetch all vouchers from the database
        List<Voucher> vouchers = voucherMapper.findAll();
        if (vouchers == null || vouchers.isEmpty()) {
            System.out.println("No vouchers found in the database.");
            return;
        }
        System.out.println("有没有到这里来");
        // 2. Iterate through vouchers and load those with stock > 0
        for (Voucher voucher : vouchers) {
            // Skip vouchers with no remaining stock
            if (voucher.getStock() == null || voucher.getStock() <= 0) {
                continue;
            }

            String stockKey = "seckill:stock:" + voucher.getId();
            Boolean exists = stringRedisTemplate.hasKey(stockKey);
            if (Boolean.FALSE.equals(exists)) {
                // 4. Initialize voucher stock in Redis
                stringRedisTemplate.opsForValue().set(
                        stockKey,
                        String.valueOf(voucher.getStock())
                );
                System.out.println("Initialized Redis key: " + stockKey +
                        " = " + voucher.getStock());

            }
        }

    }
}
