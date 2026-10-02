package it.giovanna.ecommerce.orderState;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class EmailSender extends NotificationSender {

    public EmailSender(MeterRegistry meterRegistry) {
        super(meterRegistry, "email");
    }

    @Override
    protected String formatMessage(Long orderId) {
        return """
                <html>
                    <body>
                        <h2>Ordine spedito</h2>
                        <p>Il tuo ordine <strong>#%d</strong> è stato spedito.</p>
                        <p>Grazie per il tuo acquisto.</p>
                    </body>
                </html>
                """.formatted(orderId);
    }

    @Override
    protected void doSend(String message) {
        // Simulazione dell'invio email.
        // L'integrazione con un provider email reale non è richiesta dall'assignment.
    }

    @Override
    protected String getChannel() {
        return "email";
    }
}
