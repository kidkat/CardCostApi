package org.travel.cardcostapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.travel.cardcostapi.exceptions.BadRequestException;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.requests.UpdateCardCostRequest;
import org.travel.cardcostapi.responses.PaymentCardCostResponse;
import org.travel.cardcostapi.services.CardCostService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Log4j2
class CardCostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardCostService cardCostService;

    @InjectMocks
    private CardCostController cardCostController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardCostController).build();
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

//    @Test
//    public void createCardCostInvalidRequest() throws Exception {
//        CreateCardCostRequest request = new CreateCardCostRequest();
//        request.setCountry(null);
//        request.setCost(15.0);
//
//        System.out.println("Hello");
//        MvcResult result = mockMvc.perform(get("/test-bad-request")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))).andReturn();
////                .andExpect(status().isBadRequest())
////                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
////                .andExpect(result -> assertEquals("Country cannot be null or empty", result.getResolvedException().getMessage()));
//
//        String content = result.getResponse().getContentAsString();
//        System.out.println(content);
//
//        verify(cardCostService, never()).createCardCost(any(CreateCardCostRequest.class));
//    }

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
    void deleteCardCostSuccess() throws Exception {
        Long cardCostId = 1L;

        doNothing().when(cardCostService).deleteCardCostById(cardCostId);

        mockMvc.perform(delete("/card-costs/{cardCostId}", cardCostId))
                .andExpect(status().isNoContent());

        verify(cardCostService, times(1)).deleteCardCostById(cardCostId);
    }

    @Test
    void getPaymentCardCostSuccess() throws Exception {
        PaymentCardCostRequest request = new PaymentCardCostRequest();
        request.setCardNumber("4111111111111111");

        CardCost cardCost = new CardCost();
        cardCost.setCountry("USA");
        cardCost.setCost(15.0);

        PaymentCardCostResponse expectedResponse = new PaymentCardCostResponse("USA", 15.0);

        when(cardCostService.getPaymentCardCost(any(PaymentCardCostRequest.class))).thenReturn(cardCost);

        mockMvc.perform(post("/payment-card-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.cost").value(15.0));

        verify(cardCostService, times(1)).getPaymentCardCost(any(PaymentCardCostRequest.class));
    }
}