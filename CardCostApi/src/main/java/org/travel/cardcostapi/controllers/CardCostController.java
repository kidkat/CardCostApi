package org.travel.cardcostapi.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.services.CardCostService;
import org.travel.cardcostapi.utils.Utils;

import javax.smartcardio.Card;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/")
public class CardCostController {
    private final String PREFIX = this.getClass().getSimpleName() + ":>";

    @Autowired
    private CardCostService cardCostService;

    @PostMapping("/payment-card-cost")
    public ResponseEntity<CardCost> getPaymentCardCost(@RequestBody PaymentCardCostRequest paymentCardCostRequest){
        long startTime = Utils.getStartTime();
        paymentCardCostRequest.validate();
        String maskedCardNumber = Utils.getMaskedCardNumber(paymentCardCostRequest.getCardNumber());
        log.info("{} Received 'Payment Card Cost' request for country: '{}'", PREFIX, maskedCardNumber);

        CardCost cardCost = cardCostService.getPaymentCardCost(paymentCardCostRequest);

        log.info("{} Request 'Payment Card Cost' request for country: '{}' executed within '{}' ms",
                PREFIX, maskedCardNumber, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCost);
    }

    @PostMapping("/card-costs")
    public ResponseEntity<CardCost> createCardCost(@RequestBody CreateCardCostRequest createCardCostRequest) {
        long startTime = Utils.getStartTime();
        createCardCostRequest.validate();
        log.info("{} Received 'Create Card Cost' request for country: '{}'", PREFIX, createCardCostRequest.getCountry());

        CardCost cardCost = cardCostService.createCardCost(createCardCostRequest);

        log.info("{} Request 'Create Card Cost' request for country: '{}' executed within '{}' ms",
                PREFIX, createCardCostRequest.getCountry(), Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCost);
    }

    @GetMapping("/card-costs")
    public ResponseEntity<List<CardCost>> getCardCost() {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Get ALl Card Cost' request.", PREFIX);

        List<CardCost> cardCostList = cardCostService.getAllCardCost();

        log.info("{} Request 'Get All Card Cost' request executed within '{}' ms",
                PREFIX, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCostList);
    }

    @GetMapping("/card-costs/{cardCostId}")
    public ResponseEntity<CardCost> getCardCost(@PathVariable Long cardCostId) {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Get Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        CardCost cardCost = cardCostService.getCardCostById(cardCostId);

        log.info("{} Request 'Get Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCost);
    }

    @PutMapping("/card-costs/{cardCostId}")
    public ResponseEntity<CardCost> updateCardCost(@PathVariable Long cardCostId) {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Update Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        CardCost updatedCardCost = cardCostService.updateCardCostById(cardCostId);

        log.info("{} Request 'Update Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(updatedCardCost);
    }

    @DeleteMapping("/card-costs/{cardCostId}")
    public ResponseEntity<Void> deleteCardCost(@PathVariable Long cardCostId) {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Delete Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        cardCostService.deleteCardCostById(cardCostId);

        log.info("{} Request 'Delete Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));
        return ResponseEntity.noContent().build();
    }
}
