package it.giovanna.ecommerce.fulfillment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final FulfillmentMediator fulfillmentMediator;

    public void routeOrder(FulfillmentRequest request) {

        fulfillmentMediator.route(request);
    }
}
