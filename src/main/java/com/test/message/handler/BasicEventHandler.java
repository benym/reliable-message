package com.test.message.handler;


import com.test.message.enums.MessageAction;
import com.test.message.event.BasicApplicationEvent;
import com.test.message.listener.BasicDataListener;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class BasicEventHandler<T extends BasicApplicationEvent> extends
        BasicDataListener<T> implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicEventHandler.class);

    public abstract void insert(T obj);

    public abstract void update(T obj);

    public abstract void delete(T obj);

    @Autowired(required = false)
    protected KafkaTemplate<String, String> kafkaTemplate;


    static class NamedThreadFactory implements ThreadFactory {

        private final String prefix;

        private final AtomicLong threadId;

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
            this.threadId = new AtomicLong();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + threadId.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }


    protected static final ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2,
                    Runtime.getRuntime().availableProcessors(), 5L, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(1000), new NamedThreadFactory("basic-event-handler"),
                    (r, executor) -> LOGGER.warn("消息被丢弃：" + r.toString()));


    public void processData(MessageAction messageAction, T obj) {
        threadPoolExecutor.execute(() -> {
            try {
                switch (messageAction) {
                    case INSERT:
                        insert(obj);
                        break;
                    case UPDATE:
                        update(obj);
                        break;
                    case DELETE:
                        delete(obj);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("处理动作异常", e);
            }
        });
    }
}
