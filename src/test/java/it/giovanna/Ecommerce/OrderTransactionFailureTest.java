package it.giovanna.Ecommerce;

import it.giovanna.ecommerce.EcommerceApplication;
import it.giovanna.ecommerce.model.Status;
import it.giovanna.ecommerce.orderState.EmailSender;
import it.giovanna.ecommerce.orderState.OrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EcommerceApplication.class)
@AutoConfigureMockMvc
class OrderTransactionFailureTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderStatusRepository orderStatusRepository;

    @MockitoBean
    private EmailSender emailSender;

    @Test
    void shouldNotSendEmailWhenTransactionFails() throws Exception {

        when(orderStatusRepository.existsById(anyLong()))
                .thenReturn(true);

        when(orderStatusRepository.updateStatus(
                anyLong(),
                any(Status.class)
        )).thenThrow(
                new DataIntegrityViolationException(
                        "Errore database simulato"
                )
        );

        mockMvc.perform(
                        put("/api/v1/orders/1/status")
                                .header("X-User-Role", "ADMIN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SHIPPED"
                                        }
                                        """)
                )
                .andExpect(status().is5xxServerError());

        verify(emailSender, never()).send(any());
    }
}