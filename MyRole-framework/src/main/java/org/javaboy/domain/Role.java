package org.javaboy.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

/**
 * @Author: WangHongKun
 * @Date: 2022/9/22 16:58
 * @Email: 2028911483@qq.com
 * @Phone: 18683977706
 */

@TableName("role") //指定类对应的表名
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    //表示自增，只适用于非分布式系统中

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String  name;

    private String  description;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date alarmTime;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date startTime;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date endTime;


    //0表示未完成 ，1表示完成
    private Integer state;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date updateTime;

    private Integer userId;

    /**
     * 1.重要且紧急
     * 2.重要但不紧急
     * 3.不重要但紧急
     * 4.不重要不紧急
     */
    private Integer priority;


    /**
     * 0 表示未回收
     * 1 表示已经被回收
     *
     *
     * 被回收的任务一般无法被查询到
     *
     */
    private Integer recycled;
}
