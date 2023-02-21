package com.test.message.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.test.message.enums.MessageAction;
import com.test.message.enums.MessageActionType;
import com.test.message.enums.MessageStatus;
import com.test.message.enums.NoticeType;
import com.test.message.event.custom.MessageBasicApplicationEvent;
import com.test.message.handler.custom.MessageEventHandler;
import com.test.message.pojo.domain.MessageEntityDO;
import com.test.message.pojo.domain.MessageLogDO;
import com.test.message.pojo.dto.MessageEntityDTO;
import com.test.message.service.MessageEntityService;
import com.test.message.service.MessageService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageLogServiceImpl messageLogService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageEntityService messageEntityService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @Transactional
    public Integer sendToUser() {
        // 构建用户
        List<String> recevier = generalData(2);
        QueryWrapper<MessageEntityDO> entityWrapper = new QueryWrapper<>();
        // 获得要发送的消息实体
        entityWrapper.lambda().eq(MessageEntityDO::getNoticeType, NoticeType.GLOBAL.getCode());
        MessageEntityDO messageEntityDO = messageEntityService.getOne(entityWrapper);
        // 查询获得消息日志，用于有消息还没处理完，服务宕机了，幂等这部分消息日志，避免多个重复的日志被定时任务扫描到造成后续的重复消费
        // 同时也避免了DuplicateKeyException造成的批量写入失败，这里同样可以采用insert into ...on duplicate key update更新
        List<MessageLogDO> allUncommitLog = messageLogService.getAllUncommitLog();
        // 构建唯一索引值->主键id的map
        Map<String, Long> uncommitMap = allUncommitLog.stream()
                .collect(Collectors.toMap(MessageLogDO::getNoticeUserId, MessageLogDO::getId));
        List<String> userIdList = new ArrayList<>();
        // 先写消息日志，提交状态为prepared
        List<MessageLogDO> messagelogList = recevier.stream().map(i -> {
            MessageLogDO messageLogDO = new MessageLogDO();
            // 模拟用户id
            String userId = IdWorker.getIdStr();
            String noticeId = String.valueOf(messageEntityDO.getId());
            String noticeUserId = noticeId + userId;
            userIdList.add(userId);
            // 如果没有在map里面则，这个id为空，日志为新增，如果有id则就是更新
            messageLogDO.setId(uncommitMap.get(noticeUserId));
            messageLogDO.setNoticeUserId(noticeUserId);
            messageLogDO.setNoticeType(NoticeType.GLOBAL.getCode());
            messageLogDO.setMessageAction(MessageActionType.INSERT.getNoticeTypeCode());
            // prepared状态
            messageLogDO.setStatus(MessageStatus.PREPARED.getCode());
            messageLogDO.setCreatedBy("测试");
            messageLogDO.setUpdatedBy("测试");
            return messageLogDO;
        }).collect(Collectors.toList());
        // 由于此时的消息日志id已经有前置map查了一次库，所以原本的saveOrUpdateBatch中，getById确认这个id是否存在数据库的操作就是不必要的了
        // 覆写MessageLogServiceImpl中的Mybatis-plus提供的saveOrUpdateBatch方法
        // 先写消息日志
        messageLogService.saveOrUpdateBatch(messagelogList);
        // 回显id后构建map，如有重复已后一个为准
        Map<String, Long> map = messagelogList.stream()
                .collect(Collectors.toMap(MessageLogDO::getNoticeUserId, MessageLogDO::getId,
                        (a, b) -> b));
        // 赋值属性到DTO
        BeanCopier beanCopier = BeanCopier
                .create(MessageEntityDO.class, MessageEntityDTO.class, false);
        MessageEntityDTO messageEntityDTO = new MessageEntityDTO();
        beanCopier.copy(messageEntityDO, messageEntityDTO, null);
        // 异步发送消息到MessageEventHandler
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (!CollectionUtils.isEmpty(recevier)) {
                    MessageBasicApplicationEvent<String> insertEvent = new MessageBasicApplicationEvent<>(
                            null, MessageEventHandler.class, MessageAction.INSERT, recevier,
                            userIdList, map, messageEntityDTO);
                    applicationContext.publishEvent(insertEvent);
                }
            }
        });
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void testTransaction() {
        messageService.test1();
        messageService.test2();
        messageService.test3();
    }

    @Transactional(rollbackFor = Exception.class)
    public void test1(){
        MessageEntityDO messageEntityDO = new MessageEntityDO();
        messageEntityDO.setNoticeContent("123131");
        messageEntityDO.setNoticeType((byte) 1);
        messageEntityService.save(messageEntityDO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void test2(){
        MessageLogDO messageLogDO = new MessageLogDO();
        messageLogDO.setNoticeUserId("123456");
        messageLogDO.setNoticeType(NoticeType.GLOBAL.getCode());
        messageLogDO.setMessageAction(MessageActionType.INSERT.getNoticeTypeCode());
        // prepared状态
        messageLogDO.setStatus(MessageStatus.PREPARED.getCode());
        messageLogDO.setCreatedBy("测试");
        messageLogDO.setUpdatedBy("测试");
        messageLogService.save(messageLogDO);
    }


    public void test3(){
        MessageLogDO messageLogDO = new MessageLogDO();
        messageLogDO.setNoticeUserId("9879789");
        messageLogDO.setNoticeType(NoticeType.GLOBAL.getCode());
        messageLogDO.setMessageAction(MessageActionType.INSERT.getNoticeTypeCode());
        // prepared状态
        messageLogDO.setStatus(MessageStatus.PREPARED.getCode());
        messageLogDO.setCreatedBy("测试22");
        messageLogDO.setUpdatedBy("测试22");
        messageLogService.save(messageLogDO);
        try {
            int i = 1/0;
        } catch (Exception e) {
            QueryWrapper<MessageLogDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(MessageLogDO::getNoticeUserId,"9879789");
            messageLogService.remove(queryWrapper);
            throw new RuntimeException("123123");
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
