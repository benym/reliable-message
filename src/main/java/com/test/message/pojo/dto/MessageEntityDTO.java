package com.test.message.pojo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
public class MessageEntityDTO implements Serializable {

    private static final long serialVersionUID = 6743519381081331851L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 通知类型
     */
    private Byte noticeType;

    /**
     * 消息内容
     */
    private String noticeContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
