package com.example.user.profile.service.config;

import com.example.userprofile.api.dto.UserProfileEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private static final long BACKOFF_INTERVAL_MS = 1_000L;
    private static final long MAX_RETRIES = 3L;

    @Bean
    public KafkaTemplate<String, Object> dlqKafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public DefaultErrorHandler errorHandler(@Qualifier("dlqKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) ->
                                new TopicPartition(
                                        record.topic() + ".DLQ",
                                        record.partition()
                                )
                );

        DefaultErrorHandler handler =
                new DefaultErrorHandler(
                        recoverer,
                        new FixedBackOff(BACKOFF_INTERVAL_MS, MAX_RETRIES)
                );

        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                SerializationException.class,
                DeserializationException.class,
                IllegalStateException.class
        );

        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            long totalAttempts = MAX_RETRIES + 1;

            Throwable cause = ex;
            if (cause.getCause() != null) {
                cause = cause.getCause();
            }

            UserProfileEvent event = null;
            Object value = record.value();
            if (value instanceof UserProfileEvent e) {
                event = e;
            }

            String eventId = event != null && event.getEventId() != null
                    ? event.getEventId().toString()
                    : "n/a";
            String eventType = event != null && event.getEventType() != null
                    ? event.getEventType().name()
                    : "n/a";
            String userId = event != null && event.getUserId() != null
                    ? event.getUserId().toString()
                    : "n/a";

            if (deliveryAttempt < totalAttempts) {
                log.warn(
                        "Retry {}/{} for record topic={}, partition={}, offset={}, key={}, eventId={}, eventType={}, userId={}, reason={}",
                        deliveryAttempt,
                        totalAttempts,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        eventId,
                        eventType,
                        userId,
                        cause.toString()
                );
            } else {
                log.error(
                        "DLQ: record failed after {} attempts. topic={}, partition={}, offset={}, key={}, eventId={}, eventType={}, userId={}, reason={}",
                        deliveryAttempt,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        eventId,
                        eventType,
                        userId,
                        cause.toString()
                );
            }
        });
        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserProfileEvent> kafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            DefaultErrorHandler errorHandler,
            KafkaTransactionManager<String, String> kafkaTransactionManager) {

        ConsumerFactory<String, UserProfileEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());

        ConcurrentKafkaListenerContainerFactory<String, UserProfileEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.getContainerProperties().setKafkaAwareTransactionManager(kafkaTransactionManager);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
