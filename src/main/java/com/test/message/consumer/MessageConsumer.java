package com.test.message.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.message.pojo.domain.MessageSuccessDO;
import com.test.message.pojo.dto.MessageSuccessDTO;
import com.test.message.service.impl.MessageSuccessServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    private MessageSuccessServiceImpl messageSuccessService;

    /**
     * 消息消费者，将消息入库，强一致性时需要做幂等，比如唯一索引，数据覆盖，redis的set等
     * 此处采用的unique_id = notice_id+user_id+base64消息内容，作为唯一索引
     *
     *
     * @param consumerRecord consumerRecord
     */
    @KafkaListener(topics = "test_message")
    public void consumerMessage(ConsumerRecord<String, String> consumerRecord)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<MessageSuccessDTO> messageSuccessDTOList = objectMapper
                .readValue(consumerRecord.value(), new TypeReference<List<MessageSuccessDTO>>() {
                });
        BeanCopier beanCopier = BeanCopier
                .create(MessageSuccessDTO.class, MessageSuccessDO.class, false);
        List<MessageSuccessDO> messageSuccessDOList = messageSuccessDTOList.stream()
                .map(messageSuccessDTO -> {
                    MessageSuccessDO messageSuccessDO = new MessageSuccessDO();
                    beanCopier.copy(messageSuccessDTO, messageSuccessDO, null);
                    return messageSuccessDO;
                }).collect(Collectors.toList());
        try {
            messageSuccessService.saveOrUpdateBatch(messageSuccessDOList);
        } catch (DuplicateKeyException e) {
            log.warn("触发消费者唯一索引，防止重复消费：{}", e.getMessage());
        }
        log.info("消息消费成功");
    }

    /**
     * 死信队列处理
     *
     * @param record 消息
     * @param acknowledgment 手动提交offset
     * @param exception 异常信息
     * @param stacktrace 异常堆栈信息
     */
    @KafkaListener(topics = "test_message.DLT")
    public void DLTlisten(ConsumerRecord<String, String> record, Acknowledgment acknowledgment,
            @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exception,
            @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stacktrace) {
        log.info("收到死信队列消息：{},异常信息{},堆栈信息{}", record.value(), exception, stacktrace);
        // 手动提交offset
        acknowledgment.acknowledge();
    }
}
