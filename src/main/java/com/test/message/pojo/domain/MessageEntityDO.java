package com.test.message.pojo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


@Data
@TableName(value = "message_entity")
public class MessageEntityDO implements Serializable {

    private static final long serialVersionUID = -8968147047689900646L;
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 通知类型
     */
    @TableField(value = "notice_type")
    private Byte noticeType;

    /**
     * 消息内容
     */
    @TableField(value = "notice_content")
    private String noticeContent;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;


}