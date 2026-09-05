package it.giovanna.ecommerce.orderState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationListener {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderShipped(OrderShippedEvent event) {

        log.info(
                "OrderShippedEvent received after transaction commit. orderId={}",
                event.orderId()
        );

        emailSender.send(event.orderId());
    }
}
