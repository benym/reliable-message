package com.test.message.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.test.message.enums.MessageAction;
import com.test.message.enums.NoticeType;
import com.test.message.event.custom.MessageBasicApplicationEvent;
import com.test.message.handler.custom.MessageEventHandler;
import com.test.message.pojo.domain.MessageEntityDO;
import com.test.message.pojo.domain.MessageLogDO;
import com.test.message.pojo.dto.MessageEntityDTO;
import com.test.message.service.impl.MessageEntityServiceImpl;
import com.test.message.service.impl.MessageLogServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RetryMessageLogTask {

    private static final Long batch_size = 500L;

    @Autowired
    private MessageLogServiceImpl messageLogService;

    @Autowired
    private MessageEntityServiceImpl messageEntityService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 定时任务补偿，每5分钟扫描message_log表，对没有执行成功(状态0)的消息进行重试
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void retrySendMessage() {
        // 重新构建相同的用户，正确来说这里应该查表有哪些用户
        List<String> recevier = generalData(2);
        List<MessageLogDO> allUncommitLog = messageLogService.getAllUncommitLog(batch_size);
        QueryWrapper<MessageEntityDO> entityWrapper = new QueryWrapper<>();
        entityWrapper.lambda().eq(MessageEntityDO::getNoticeType, NoticeType.GLOBAL.getCode());
        MessageEntityDO messageEntityDO = messageEntityService.getOne(entityWrapper);
        // 赋值属性到DTO
        BeanCopier beanCopier = BeanCopier
                .create(MessageEntityDO.class, MessageEntityDTO.class, false);
        MessageEntityDTO messageEntityDTO = new MessageEntityDTO();
        beanCopier.copy(messageEntityDO, messageEntityDTO, null);
        List<String> userIdList = new ArrayList<>();
        allUncommitLog.forEach(uclog -> {
            String userId = uclog.getNoticeUserId().substring(1);
            userIdList.add(userId);
        });
        Map<String, Long> map = allUncommitLog.stream()
                .collect(Collectors.toMap(MessageLogDO::getNoticeUserId, MessageLogDO::getId));
        if (!CollectionUtils.isEmpty(recevier)) {
            MessageBasicApplicationEvent<String> insertEvent = new MessageBasicApplicationEvent<>(
                    null, MessageEventHandler.class, MessageAction.INSERT, recevier,
                    userIdList, map, messageEntityDTO
            );
            applicationContext.publishEvent(insertEvent);
        }
    }


    public List<String> generalData(Integer num) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            data.add("朋友" + i);
        }
        return data;
    }
}
