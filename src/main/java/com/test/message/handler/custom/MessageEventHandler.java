package com.test.message.handler.custom;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.test.message.enums.MessageActionType;
import com.test.message.enums.MessageStatus;
import com.test.message.enums.NoticeType;
import com.test.message.event.custom.MessageBasicApplicationEvent;
import com.test.message.handler.BasicEventHandler;
import com.test.message.pojo.domain.MessageLogDO;
import com.test.message.pojo.dto.MessageEntityDTO;
import com.test.message.pojo.dto.MessageSuccessDTO;
import com.test.message.service.impl.MessageLogServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
public class MessageEventHandler extends
        BasicEventHandler<MessageBasicApplicationEvent<String>> {

    @Autowired
    private MessageLogServiceImpl messageLogService;

    @Value("${message.topic}")
    private String messageTopic;

    @Override
    public void insert(MessageBasicApplicationEvent<String> obj) {
        insertProcess(obj.getMessageEntity(), obj.getUserlist(), obj.getUserIds(),
                obj.getMap(), MessageActionType.INSERT);
    }

    private void insertProcess(MessageEntityDTO messageEntity, List<String> userlist,
            List<String> userIds, Map<String, Long> map, MessageActionType action) {
        Base64.Encoder base64 = Base64.getEncoder();
        AtomicInteger index = new AtomicInteger(0);
        // 构建消息成功记录表
        List<MessageSuccessDTO> successList = userlist.stream().map(userName -> {
            MessageSuccessDTO messageSuccessDTO = new MessageSuccessDTO();
            Long noticeId = messageEntity.getId();
            messageSuccessDTO.setNoticeId(noticeId);
            String userId = userIds.get(index.get());
            messageSuccessDTO.setUserId(Long.valueOf(userId));
            String messageContentBase64 = base64
                    .encodeToString(messageEntity.getNoticeContent().getBytes());
            // 幂等字段
            messageSuccessDTO.setUniqueId(noticeId + userId + messageContentBase64);
            messageSuccessDTO.setUserName(userName);
            index.getAndIncrement();
            return messageSuccessDTO;
        }).collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        try {
            String successJson = objectMapper.writeValueAsString(successList);
            // 发送到topic
            ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate
                    .send(messageTopic, successJson);
            // 更新消息日志
            AtomicInteger userIndex = new AtomicInteger(0);
            AtomicInteger successSize = new AtomicInteger(successList.size());
            List<MessageLogDO> commitList = new ArrayList<>();
            sendResultListenableFuture.addCallback(result -> {
                while (successSize.get() > 0) {
                    MessageLogDO messageLogDO = new MessageLogDO();
                    String userId = userIds.get(userIndex.get());
                    userIndex.getAndIncrement();
                    String noticeId = String.valueOf(messageEntity.getId());
                    // 唯一索引列noticeUserId，保证消息表幂等
                    String noticeUserId = noticeId + userId;
                    // prepare阶段相同的id，用于更新之前数据的messageLog状态
                    messageLogDO.setId(map.get(noticeUserId));
                    messageLogDO.setNoticeUserId(noticeUserId);
                    messageLogDO.setNoticeType(NoticeType.GLOBAL.getCode());
                    messageLogDO.setMessageAction(action.getNoticeTypeCode());
                    // 更新为commit状态
                    messageLogDO.setStatus(MessageStatus.COMMIT.getCode());
                    commitList.add(messageLogDO);

                    if (successSize.decrementAndGet() <= 0) {
                        messageLogService.saveOrUpdateBatch(commitList);
                    }
                }
            }, ex -> {
                if (successSize.decrementAndGet() <= 0) {
                    messageLogService.saveOrUpdateBatch(commitList);
                }
            });
        } catch (DuplicateKeyException e) {
            log.warn("重复的消息表id，不做处理：{}", e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(MessageBasicApplicationEvent<String> obj) {
        // todo
    }

    @Override
    public void delete(MessageBasicApplicationEvent<String> obj) {
        // todo
    }

    @Override
    public void run(String... args) throws Exception {
        map.put(MessageBasicApplicationEvent.class, this);
    }

}
