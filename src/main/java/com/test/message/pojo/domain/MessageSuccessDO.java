package com.test.message.pojo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


@Data
@TableName(value = "message_success")
public class MessageSuccessDO implements Serializable {

    private static final long serialVersionUID = -5165354697653927795L;
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 唯一消息id，用于消费幂等
     * notice_id+user_id+base64消息内容
     */
    @TableField("unique_id")
    private String uniqueId;

    /**
     * 通知id
     */
    @TableField("notice_id")
    private Long noticeId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}