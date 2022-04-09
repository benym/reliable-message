package com.test.message.pojo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MessageSuccessDTO implements Serializable {

    private static final long serialVersionUID = -4200628379136203420L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 唯一消息id，用于消费幂等
     * notice_id+user_id+base64消息内容
     */
    private String uniqueId;

    /**
     * 通知id
     */
    private Long noticeId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
