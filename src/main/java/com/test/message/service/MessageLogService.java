package com.test.message.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.test.message.pojo.domain.MessageLogDO;
import java.util.List;

public interface MessageLogService extends IService<MessageLogDO> {

    /**
     * 获取所有未提交消息日志,不限制条数
     *
     * @return List
     */
    List<MessageLogDO> getAllUncommitLog();

    /**
     * 获取所有未提交消息日志
     *
     * @param limit 限制条数
     * @return List
     */
    List<MessageLogDO> getAllUncommitLog(Long limit);

    /**
     * 获取所有已提交消息日志
     *
     * @param limit 限制条数
     * @return List
     */
    List<MessageLogDO> getAllcommitLog(Long limit);
}
