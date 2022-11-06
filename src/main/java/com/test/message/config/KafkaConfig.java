package com.test.message.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {


    @Autowired
    private ConsumerFactory<Object, Object> consumerFactory;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> containerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 如果consumer有ack入参，则这句话是必须的
        factory.getContainerProperties().setAckMode(AckMode.MANUAL);
        // 最大重试次数3次
        factory.setErrorHandler(kafkaErrorHandler(kafkaTemplate));
        return factory;
    }

    /**
     * 消费端重试机制，超过重试次数则加入到死信队列
     *
     * @param template template
     * @return ErrorHandler
     */
    public ErrorHandler kafkaErrorHandler(KafkaTemplate<?, ?> template) {
        log.warn("kafkaErrorHandler begin to Handle");
        // 创建DLT对象，死信队列命名规则：原有Topic+.DLT后缀=死信队列Topic
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        // 设置重试间隔为10秒，次数3次
        BackOff backOff = new FixedBackOff(10 * 1000L, 3);
        // 单条失败重试
        return new SeekToCurrentErrorHandler(recoverer, backOff);
    }

//    /**
//     * 批量消息重试，暂不支持死信队列
//     *
//     * @return BatchErrorHandler
//     */
//    @Bean
//    @Primary
//    public BatchErrorHandler kafkaBatchErrorHandler() {
//        // 创建 SeekToCurrentBatchErrorHandler 对象
//        SeekToCurrentBatchErrorHandler batchErrorHandler = new SeekToCurrentBatchErrorHandler();
//        // 创建 FixedBackOff 对象
//        BackOff backOff = new FixedBackOff(10 * 1000L, 3L);
//        batchErrorHandler.setBackOff(backOff);
//        // 返回
//        return batchErrorHandler;
//    }

}
