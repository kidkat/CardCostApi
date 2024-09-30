package org.travel.cardcostapi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.repositories.CardCostRepository;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.responses.CardInfoResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author asafronov
 */
class CardCostServiceTest {
    @Mock
    private CardCostRepository cardCostRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CardCostService cardCostService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCardCost_US() {
        String cardNumber = "4532756279624064";
        String bin = "453275";
        PaymentCardCostRequest paymentCardCostRequest = new PaymentCardCostRequest();
        paymentCardCostRequest.setCardNumber(cardNumber);

        CardInfoResponse.Country country = new CardInfoResponse.Country();
        country.setAlpha2("US");
        CardInfoResponse cardInfo = new CardInfoResponse();
        cardInfo.setCountry(country);

        Mockito.when(restTemplate.getForObject("https://lookup.binlist.net/" + bin, CardInfoResponse.class))
                .thenReturn(cardInfo);

        CardCost cardCost = new CardCost(1L, "US", 5.0);
        Mockito.when(cardCostRepository.findByCountry("US")).thenReturn(Optional.of(cardCost));

        // Call the service method
        CardCost result = cardCostService.getPaymentCardCost(paymentCardCostRequest);

        // Verify the result
        assertEquals("US", result.getCountry());
        assertEquals(5.0, result.getCost());

        Mockito.verify(cardCostRepository, Mockito.times(1)).findByCountry("US");
    }
}