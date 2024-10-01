package org.travel.cardcostapi.services;

import org.hibernate.sql.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.travel.cardcostapi.exceptions.BadRequestException;
import org.travel.cardcostapi.exceptions.ExternalApiException;
import org.travel.cardcostapi.exceptions.ResourceNotFoundException;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.repositories.CardCostRepository;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.requests.UpdateCardCostRequest;
import org.travel.cardcostapi.responses.CardInfoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void createCardCost(){
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("US");
        request.setCost(5.0);

        CardCost savedCardCost = new CardCost();
        savedCardCost.setCountry("US");
        savedCardCost.setCost(5.0);

        when(cardCostRepository.save(any(CardCost.class))).thenReturn(savedCardCost);
        CardCost result = cardCostService.createCardCost(request);

        assertNotNull(result);
        assertEquals("US", result.getCountry());
        assertEquals(5.0, result.getCost());
        verify(cardCostRepository, times(1)).save(any(CardCost.class));
    }

    @Test
    void createCardCostExists(){
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("US");
        request.setCost(5.0);

        CardCost savedCardCost = new CardCost();
        savedCardCost.setCountry("US");
        savedCardCost.setCost(5.0);
        Optional<CardCost> optionalCardCost = Optional.of(savedCardCost);

        when(cardCostRepository.findByCountry(any())).thenReturn(optionalCardCost);

        assertThrows(BadRequestException.class, () -> {
            cardCostService.createCardCost(request);
        });
    }

    @Test
    void getAllCardCosts() {
        List<CardCost> cardCosts = new ArrayList<>();
        cardCosts.add(new CardCost(1L, "US", 5.0));
        cardCosts.add(new CardCost(2L, "GR", 15.0));

        when(cardCostRepository.findAll()).thenReturn(cardCosts);
        List<CardCost> result = cardCostService.getAllCardCost();

        assertEquals(2, result.size());
        verify(cardCostRepository, times(1)).findAll();
    }

    @Test
    void getAllCardCostsEmpty() {
        List<CardCost> cardCosts = new ArrayList<>();

        when(cardCostRepository.findAll()).thenReturn(cardCosts);

        assertThrows(ResourceNotFoundException.class, () -> {
            cardCostService.getAllCardCost();
        });
    }

    @Test
    void getCardCostByIdExists() {
        CardCost cardCost = new CardCost(1L, "US", 5.0);
        when(cardCostRepository.findById(1L)).thenReturn(Optional.of(cardCost));

        CardCost result = cardCostService.getCardCostById(1L);

        assertNotNull(result);
        assertEquals("US", result.getCountry());
        assertEquals(5.0, result.getCost());
        verify(cardCostRepository, times(1)).findById(1L);
    }

    @Test
    void updateCardCostByIdNotExists() {
        Long cardCostId = 1L;
        UpdateCardCostRequest updateRequest = new UpdateCardCostRequest();
        updateRequest.setCountry("US");
        updateRequest.setCost(15.0);

        when(cardCostRepository.findById(cardCostId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cardCostService.updateCardCostById(cardCostId, updateRequest);
        });
        verify(cardCostRepository, never()).save(any(CardCost.class));
    }

    @Test
    void updateCardCostByIdExists() {
        Long cardCostId = 1L;
        UpdateCardCostRequest updateRequest = new UpdateCardCostRequest();
        updateRequest.setCountry("US");
        updateRequest.setCost(15.0);

        CardCost existingCardCost = new CardCost();
        existingCardCost.setId(cardCostId);
        existingCardCost.setCountry("UK");
        existingCardCost.setCost(10.0);

        when(cardCostRepository.findById(cardCostId)).thenReturn(Optional.of(existingCardCost));
        when(cardCostRepository.save(any(CardCost.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CardCost updatedCardCost = cardCostService.updateCardCostById(cardCostId, updateRequest);

        assertNotNull(updatedCardCost);
        assertEquals("US", updatedCardCost.getCountry());
        assertEquals(15.0, updatedCardCost.getCost());
        verify(cardCostRepository, times(1)).save(existingCardCost);
    }

    @Test
    void deleteCardCostByIdNotExists() {
        Long cardCostId = 1L;
        when(cardCostRepository.findById(cardCostId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cardCostService.deleteCardCostById(cardCostId);
        });

        verify(cardCostRepository, never()).delete(any(CardCost.class));
    }

    @Test
    void deleteCardCostByIdExists() {
        Long cardCostId = 1L;
        CardCost existingCardCost = new CardCost();
        existingCardCost.setId(cardCostId);
        existingCardCost.setCountry("US");
        existingCardCost.setCost(10.0);

        when(cardCostRepository.findById(cardCostId)).thenReturn(Optional.of(existingCardCost));
        cardCostService.deleteCardCostById(cardCostId);

        verify(cardCostRepository, times(1)).delete(existingCardCost);
    }

    @Test
    void getPaymentCardCostValidResponse() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        CardInfoResponse cardInfoResponse = new CardInfoResponse();
        CardInfoResponse.Country country = new CardInfoResponse.Country();
        country.setAlpha2("US");
        cardInfoResponse.setCountry(country);

        when(restTemplate.getForObject(anyString(), eq(CardInfoResponse.class))).thenReturn(cardInfoResponse);

        CardCost cardCost = new CardCost(1L, "US", 5.0, 1L);
        cardCostService.setRestTemplate(restTemplate);
        when(cardCostRepository.findByCountry("US")).thenReturn(Optional.of(cardCost));

        CardCost result = cardCostService.getPaymentCardCost(request);

        assertNotNull(result);
        assertEquals("US", result.getCountry());
        assertEquals(5.0, result.getCost());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CardInfoResponse.class));
        verify(cardCostRepository, times(1)).findByCountry("US");
    }

    @Test
    void testGetPaymentCardCostInvalidResponse() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        when(restTemplate.getForObject(anyString(), eq(CardInfoResponse.class))).thenReturn(null);


        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            cardCostService.getPaymentCardCost(request);
        });

        assertEquals("Invalid response from external API.", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CardInfoResponse.class));
        verify(cardCostRepository, never()).findByCountry(anyString());
    }

    @Test
    void getPaymentCardCostExternalApiException() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        when(restTemplate.getForObject(anyString(), eq(CardInfoResponse.class)))
                .thenThrow(new ResourceAccessException("Connection failed"));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            cardCostService.getPaymentCardCost(request);
        });

        assertEquals("Failed to connect to external API. Please try again later.", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CardInfoResponse.class));
        verify(cardCostRepository, never()).findByCountry(anyString());
    }

    @Test
    void testGetPaymentCardCost_BadRequestExceptionForInvalidCountryCode() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        CardInfoResponse cardInfoResponse = new CardInfoResponse();
        CardInfoResponse.Country country = new CardInfoResponse.Country();
        country.setAlpha2(null);
        cardInfoResponse.setCountry(country);

        when(restTemplate.getForObject(anyString(), eq(CardInfoResponse.class))).thenReturn(cardInfoResponse);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cardCostService.getPaymentCardCost(request);
        });

        assertEquals("Country code is null or empty. Cause card_number is invalid.", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(CardInfoResponse.class));
        verify(cardCostRepository, never()).findByCountry(anyString());
    }

    @Test
    void testGetPaymentCardCost_ResourceNotFoundException() {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        CardInfoResponse cardInfoResponse = new CardInfoResponse();
        CardInfoResponse.Country country = new CardInfoResponse.Country();
        country.setAlpha2("US");
        cardInfoResponse.setCountry(country);

        when(restTemplate.getForObject(anyString(), eq(CardInfoResponse.class))).thenReturn(cardInfoResponse);
        when(cardCostRepository.findByCountry("US")).thenReturn(Optional.empty());
        when(cardCostRepository.findByCountry("Other")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            cardCostService.getPaymentCardCost(request);
        });

        assertEquals("Card Cost with country: US do not exists!", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(CardInfoResponse.class));
        verify(cardCostRepository, times(1)).findByCountry("US");
        verify(cardCostRepository, times(1)).findByCountry("OTHERS");
    }
}