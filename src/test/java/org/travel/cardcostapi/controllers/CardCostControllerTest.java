package org.travel.cardcostapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.travel.cardcostapi.exceptions.BadRequestException;
import org.travel.cardcostapi.exceptions.ExternalApiException;
import org.travel.cardcostapi.exceptions.GlobalExceptionHandler;
import org.travel.cardcostapi.exceptions.ResourceNotFoundException;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.requests.UpdateCardCostRequest;
import org.travel.cardcostapi.services.CardCostService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test2")
@AutoConfigureMockMvc
class CardCostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardCostService cardCostService;

    @InjectMocks
    private CardCostController cardCostController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Standalone setup with exception handling
        mockMvc = MockMvcBuilders.standaloneSetup(cardCostController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCardCost() throws Exception {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("US");
        request.setCost(15.0);

        CardCost cardCost = new CardCost();
        cardCost.setCountry("US");
        cardCost.setCost(15.0);

        when(cardCostService.createCardCost(any(CreateCardCostRequest.class))).thenReturn(cardCost);

        mockMvc.perform(post("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("US"))
                .andExpect(jsonPath("$.cost").value(15.0));

        verify(cardCostService, times(1)).createCardCost(any(CreateCardCostRequest.class));
    }

    @Test
    void createCardCostInvalidRequest() throws Exception {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("");
        request.setCost(15.0);

        mockMvc.perform(post("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Country cannot be null or empty"));

        verify(cardCostService, never()).createCardCost(any(CreateCardCostRequest.class));
    }

    @Test
    void createCardCostInvalidRequest2() throws Exception {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("GR");
        request.setCost(-15);

        mockMvc.perform(post("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Cost cannot be negative"));

        verify(cardCostService, never()).createCardCost(any(CreateCardCostRequest.class));
    }

    @Test
    void createCardCostBadRequestException() throws Exception {
        CreateCardCostRequest request = new CreateCardCostRequest();
        request.setCountry("GR");
        request.setCost(15);

        when(cardCostService.createCardCost(any(CreateCardCostRequest.class)))
                .thenThrow(new BadRequestException("Country already exists"));

        mockMvc.perform(post("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Country already exists"));

        verify(cardCostService, times(1)).createCardCost(any(CreateCardCostRequest.class));
    }

    @Test
    void getAllCardCosts() throws Exception {
        List<CardCost> cardCosts = Arrays.asList(
                new CardCost(1L, "US", 5.0, 1L),
                new CardCost(2L, "GR", 15.0, 1L)
        );

        when(cardCostService.getAllCardCost()).thenReturn(cardCosts);

        mockMvc.perform(get("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("US"))
                .andExpect(jsonPath("$[0].cost").value(5.0))
                .andExpect(jsonPath("$[1].country").value("GR"))
                .andExpect(jsonPath("$[1].cost").value(15.0));

        verify(cardCostService, times(1)).getAllCardCost();
    }

    @Test
    void getAllCardCostsResourceNotFound() throws Exception {
        when(cardCostService.getAllCardCost()).thenThrow(new ResourceNotFoundException("No card costs found"));

        mockMvc.perform(get("/card-costs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("No card costs found"));

        verify(cardCostService, times(1)).getAllCardCost();
    }

    @Test
    void getCardCostById() throws Exception {
        CardCost cardCost = new CardCost(1L, "US", 5.0, 1L);

        when(cardCostService.getCardCostById(1L)).thenReturn(cardCost);

        mockMvc.perform(get("/card-costs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("US"))
                .andExpect(jsonPath("$.cost").value(5.0));

        verify(cardCostService, times(1)).getCardCostById(1L);
    }

    @Test
    void getCardCostByIdResourceNotFound() throws Exception {
        when(cardCostService.getCardCostById(1L)).thenThrow(new ResourceNotFoundException("Card Cost with Id: 1 do not exists!"));

        mockMvc.perform(get("/card-costs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Card Cost with Id: 1 do not exists!"));

        verify(cardCostService, times(1)).getCardCostById(1L);
    }

    @Test
    void updateCardCostSuccess() throws Exception {
        Long cardCostId = 1L;
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCountry("USA");
        request.setCost(25.0);

        CardCost updatedCardCost = new CardCost();
        updatedCardCost.setId(cardCostId);
        updatedCardCost.setCountry("USA");
        updatedCardCost.setCost(25.0);

        when(cardCostService.updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class))).thenReturn(updatedCardCost);

        mockMvc.perform(put("/card-costs/{cardCostId}", cardCostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardCostId))
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.cost").value(25.0));

        verify(cardCostService, times(1)).updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class));
    }

    @Test
    void updateCardCostBadRequestException1() throws Exception {
        Long cardCostId = 1L;
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCountry("");
        request.setCost(25.0);

        mockMvc.perform(put("/card-costs/{cardCostId}", cardCostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Country cannot be null or empty"));

        verify(cardCostService, never()).updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class));
    }

    @Test
    void updateCardCostBadRequestException2() throws Exception {
        Long cardCostId = 1L;
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCountry("GR");
        request.setCost(-25);

        mockMvc.perform(put("/card-costs/{cardCostId}", cardCostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Cost cannot be negative"));

        verify(cardCostService, never()).updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class));
    }

    @Test
    void updateCardCostResourceNotFound() throws Exception {
        Long cardCostId = 1L;
        UpdateCardCostRequest request = new UpdateCardCostRequest();
        request.setCountry("GR");
        request.setCost(25);

        when(cardCostService.updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class)))
                .thenThrow(new ResourceNotFoundException("Card Cost with Id: " + cardCostId + " do not exists!"));

        mockMvc.perform(put("/card-costs/{cardCostId}", cardCostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Card Cost with Id: " + cardCostId + " do not exists!"));

        verify(cardCostService, times(1)).updateCardCostById(eq(cardCostId), any(UpdateCardCostRequest.class));
    }

    @Test
    void deleteCardCostSuccess() throws Exception {
        Long cardCostId = 1L;

        doNothing().when(cardCostService).deleteCardCostById(cardCostId);

        mockMvc.perform(delete("/card-costs/{cardCostId}", cardCostId))
                .andExpect(status().isNoContent());

        verify(cardCostService, times(1)).deleteCardCostById(cardCostId);
    }

    @Test
    void deleteCardCostResourceNotFound() throws Exception {
        Long cardCostId = 1L;

        doThrow(new ResourceNotFoundException("Card Cost with Id: " + cardCostId + " do not exists!")).when(cardCostService).deleteCardCostById(cardCostId);

        mockMvc.perform(delete("/card-costs/{cardCostId}", cardCostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Card Cost with Id: " + cardCostId + " do not exists!"));

        verify(cardCostService, times(1)).deleteCardCostById(eq(cardCostId));
    }

    @Test
    void getPaymentCardCostSuccess() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("4111111111111111");

        CardCost cardCost = new CardCost();
        cardCost.setCountry("USA");
        cardCost.setCost(15.0);

        when(cardCostService.getPaymentCardCost(any(PaymentCardCostRequest.class))).thenReturn(cardCost);

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.cost").value(15.0));

        verify(cardCostService, times(1)).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostValidationError1() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("41111");

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("CardNumber must be greater than 8 and less than 19 digits"));

        verify(cardCostService, never()).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostValidationError2() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("41111131233211232332112232132");

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("CardNumber must be greater than 8 and less than 19 digits"));

        verify(cardCostService, never()).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostValidationError3() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("");

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("CardNumber cannot be null or empty"));

        verify(cardCostService, never()).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostExternalApiException() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        when(cardCostService.getPaymentCardCost(any(PaymentCardCostRequest.class))).thenThrow(new ExternalApiException("Invalid response from external API."));

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(result -> assertInstanceOf(ExternalApiException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Invalid response from external API."));

        verify(cardCostService, times(1)).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostBadRequestException() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        when(cardCostService.getPaymentCardCost(any(PaymentCardCostRequest.class)))
                .thenThrow(new BadRequestException("Country code is null or empty. Cause card_number is invalid."));

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BadRequestException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Country code is null or empty. Cause card_number is invalid."));

        verify(cardCostService, times(1)).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }

    @Test
    void getPaymentCardCostResourceNotFoundException() throws Exception {
        String countryCode = "GR";
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("1234567890123456");

        when(cardCostService.getPaymentCardCost(any(PaymentCardCostRequest.class)))
                .thenThrow(new ResourceNotFoundException("Card Cost with country: " + countryCode + " do not exists!"));

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(jsonPath("$.message").value("Card Cost with country: " + countryCode + " do not exists!"));

        verify(cardCostService, times(1)).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }
}