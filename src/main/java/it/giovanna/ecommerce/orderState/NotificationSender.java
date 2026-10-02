package it.giovanna.ecommerce.orderState;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NotificationSender {

    private final Counter successCounter;
    private final Counter failureCounter;

    protected NotificationSender(MeterRegistry meterRegistry, String channel) {
        this.successCounter = Counter.builder("notification.sent")
                .description("Number of successfully sent notifications")
                .tag("channel", channel)
                .register(meterRegistry);

        this.failureCounter = Counter.builder("notification.failed")
                .description("Number of failed notifications")
                .tag("channel", channel)
                .register(meterRegistry);
    }

    public final void send(Long orderId) {
        try {
            String message = formatMessage(orderId);

            doSend(message);

            successCounter.increment();
            log.info(
                    "Notification sent successfully. channel={}, orderId={}",
                    getChannel(),
                    orderId
            );

        } catch (Exception e) {
            failureCounter.increment();

            log.error(
                    "Notification sending failed. channel={}, orderId={}",
                    getChannel(),
                    orderId,
                    e
            );
        }
    }

    protected abstract String formatMessage(Long orderId);

    protected abstract void doSend(String message);

    protected abstract String getChannel();
}
