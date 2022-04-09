package com.test.message.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson配置
 */
@Configuration
public class JacksonConfig {

    /**
     * DateTime格式化字符串
     */
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date格式化字符串
     */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Time格式化字符串
     */
    private static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer defaultJackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.timeZone(TimeZone.getDefault())
                    .dateFormat(new SimpleDateFormat(DEFAULT_DATETIME_PATTERN))
                    .serializerByType(LocalDateTime.class,
                            new LocalDateTimeSerializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)))
                    .deserializerByType(LocalDateTime.class,
                            new LocalDateTimeDeserializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)))
                    .serializerByType(LocalDate.class,
                            new LocalDateSerializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)))
                    .deserializerByType(LocalDate.class,
                            new LocalDateDeserializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)))
                    .serializerByType(LocalTime.class,
                            new LocalTimeSerializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)))
                    .deserializerByType(LocalTime.class,
                            new LocalTimeDeserializer(
                                    DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN)));
        };
    }
}
