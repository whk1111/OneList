package org.javaboy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */
@MapperScan("org.javaboy.mapper")
@SpringBootApplication
public class MyRoleApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyRoleApplication.class,args);
    }
}
