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
    @KafkaListener(topics = "test_message",containerFactory = "containerFactory")
    public void consumerMessage(ConsumerRecord<String, String> consumerRecord,Acknowledgment acknowledgment)
            throws JsonProcessingException {
        // 如果在这个过程中没有死信队列和重试机制，那么假设前面有一条消息是异常的
        // 这次异常的offset，假设为1，loe预期为2，由于异常，本次手动ack并不会提交
        // 如果此时有新消息进来，消费成功，则当前ack提交前，offset为1，loe预期为3
        // 此时消费成功ack后，offset更新为3，在没有死信队列机制的情况下，造成上一次异常消息的不会再被消费，消息丢失
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
            acknowledgment.acknowledge();
        } catch (DuplicateKeyException e) {
            log.warn("触发消费者唯一索引，防止重复消费：{}", e.getMessage());
        } catch (Exception e) {
            log.error("未知错误：{}",e.getMessage());
            throw new RuntimeException("抛出异常交给死信队列");
        }
        log.info("消息消费成功");
    }

    /**
     * 死信队列处理，当消费者异常次数达到阈值时，将消息抛入死信队列
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
        // 手动提交offset，需要同时配置listener:ack-mode，或在kafkaConfig中修改
        // 死信队列收到后最好ack一次，因为如果不ack，那么死信队列会认为自己的消息滞后，重新消费死信队列消息
        // 此处应该配合告警，可靠性需要和上方的队列一样做保证

        // 此处的ack实现不仅ack了死信队列，同时也将异常的consumer的topic落后的offset给ack了，因为只有这样，失败的消息才会被跳过继续往下执行
        // 如果ack实现的时候只ack死信，不ack异常的队列，那么会造成消息一直堆积，无法越过异常消息
        
        // 但这里同样存在问题，由于consumer异常，会按照自定义的重试逻辑对这个批次重试，所以重试期间实际上是对后续的批次是阻塞的
        // 因为当前的offset没有提交，会从失败开始的offset开始执行，直到死信队列拿到消息，提交ack，才会越过错误的消息
        // 如果在这里做延迟重试，或者重试间隔过长都会引起主流程消息堆积
        
        // 解决方案1：这里最好是当consumer异常了直接抛入一个分布式延迟队列，或者重试主题，重试主题再做延时，依靠分布式延迟队列进行重新投递
        // 在分布式延迟队列中做好消息重新投递的记录，达到次数之后再丢入死信队列
        // 解决方案2：这里注意错误消息分类，只有IO异常的消息才进行重试，因为只有这种消息可能在重新消费后成功，非网络IO异常的消息全部不重试，直接投递到死信

        // 重试主题需要确定是可恢复错误，还是不可恢复错误，做分类处理
        // 更多重试主题技巧可见https://www.infoq.cn/article/51XSHW2opSmakhHmtth8
        acknowledgment.acknowledge();
    }
}
