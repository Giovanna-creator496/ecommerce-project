package it.giovanna.ecommerce.orderState;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class SmsSender extends NotificationSender {

    public SmsSender(MeterRegistry meterRegistry) {
        super(meterRegistry, "sms");
    }

    @Override
    protected String formatMessage(Long orderId) {
        return "Il tuo ordine #" + orderId + " è stato spedito.";
    }

    @Override
    protected void doSend(String message) {
        // Simulazione dell'invio SMS.
    }

    @Override
    protected String getChannel() {
        return "sms";
    }
}
