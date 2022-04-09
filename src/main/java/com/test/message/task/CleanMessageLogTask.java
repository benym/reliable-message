package com.test.message.task;

import com.test.message.pojo.domain.MessageLogDO;
import com.test.message.service.impl.MessageLogServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CleanMessageLogTask {

    private static final Long batch_size = 500L;

    @Autowired
    private MessageLogServiceImpl messageLogService;

    /**
     * 每月清理一次已提交的500条日志
     */
    @Scheduled(cron = "* * * * 1/1 ? *")
    public void cleanExpireLog() {
        List<MessageLogDO> list = messageLogService.getAllcommitLog(batch_size);
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> ids = list.stream().map(MessageLogDO::getId)
                    .collect(Collectors.toList());
            messageLogService.removeByIds(ids);
        }
    }
}
