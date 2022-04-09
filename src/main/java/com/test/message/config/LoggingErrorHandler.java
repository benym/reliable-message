package com.test.message.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.util.ObjectUtils;

/**
 * 自定义逻辑处理消费异常，配置方式和SeekToCurrentErrorHandler
 */
@Slf4j
public class LoggingErrorHandler implements ErrorHandler {

    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord) {
        log.error("{},Error while processing: {}", e.getMessage(),
                ObjectUtils.nullSafeToString(consumerRecord));
    }
}
