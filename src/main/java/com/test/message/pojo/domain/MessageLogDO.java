package com.test.message.pojo.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value = "message_log")
public class MessageLogDO implements Serializable {

    private static final long serialVersionUID = 1502554738938042514L;

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 通知id=notice_id+user_id
     */
    @TableField(value = "notice_user_id")
    private String noticeUserId;

    /**
     * 通知类型
     */
    @TableField(value = "notice_type")
    private Byte noticeType;

    /**
     * 事件同步状态，0表示prepared，1表示commit
     */
    @TableField(value = "status")
    private Byte status;

    /**
     * 删除标志位，1已删除，0未删除
     */
    @TableField(value = "deleted")
    private Byte deleted;

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

    /**
     * 创建者
     */
    @TableField(value = "created_by")
    private String createdBy;

    /**
     * 更新者
     */
    @TableField(value = "updated_by")
    private String updatedBy;

    /**
     * 消息动作类型，0表示新增，1表示更新，2表示删除
     */
    @TableField(value = "message_action")
    private Byte messageAction;

}