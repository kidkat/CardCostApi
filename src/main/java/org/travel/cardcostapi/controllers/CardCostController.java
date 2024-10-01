package org.travel.cardcostapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.travel.cardcostapi.requests.UpdateCardCostRequest;
import org.travel.cardcostapi.responses.PaymentCardCostResponse;
import org.travel.cardcostapi.services.CardCostService;
import org.travel.cardcostapi.utils.Utils;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/")
public class CardCostController {
    private final String PREFIX = this.getClass().getSimpleName() + ":>";

    @Autowired
    private CardCostService cardCostService;

    @Tag(name = "Post", description = "POST methods of CardCost APIs")
    @Operation(summary = "Get payment card cost", description = "Getting a card cost of given card_number. The response is object with country & cost.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return of card cost"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "External issue with API request")
    })
    @PostMapping("/payment-card-cost")
    public ResponseEntity<PaymentCardCostResponse> getPaymentCardCost(
            @Parameter(
                    description = "Request in JSON format with card_number inside which user want to know the cost.",
                    required = true
            )
            @RequestBody PaymentCardCostRequest paymentCardCostRequest
    ){
        long startTime = Utils.getStartTime();
        paymentCardCostRequest.validate();
        String maskedCardNumber = Utils.getMaskedCardNumber(paymentCardCostRequest.getCardNumber());
        log.info("{} Received 'Payment Card Cost' request for country: '{}'", PREFIX, maskedCardNumber);

        CardCost cardCost = cardCostService.getPaymentCardCost(paymentCardCostRequest);
        PaymentCardCostResponse paymentCardCostResponse = new PaymentCardCostResponse(cardCost.getCountry(), cardCost.getCost());

        log.info("{} Request 'Payment Card Cost' request for country: '{}' executed within '{}' ms",
                PREFIX, maskedCardNumber, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(paymentCardCostResponse);
    }

    @Tag(name = "Post", description = "POST methods of CardCost APIs")
    @Operation(summary = "Create a new card cost", description = "Creation of new card cost. The response is new CardCost object with id, country, cost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card cost created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input/card cost already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/card-costs")
    public ResponseEntity<CardCost> createCardCost(
            @Parameter(
                    description = "Request in JSON format to create new cardCost.",
                    required = true
            )
            @RequestBody CreateCardCostRequest createCardCostRequest
    ) {
        long startTime = Utils.getStartTime();
        createCardCostRequest.validate();
        log.info("{} Received 'Create Card Cost' request for country: '{}'", PREFIX, createCardCostRequest.getCountry());

        CardCost cardCost = cardCostService.createCardCost(createCardCostRequest);

        log.info("{} Request 'Create Card Cost' request for country: '{}' executed within '{}' ms",
                PREFIX, createCardCostRequest.getCountry(), Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCost);
    }

    @Tag(name = "Get", description = "GET methods of CardCost APIs")
    @Operation(summary = "Get all card cost", description = "Getting all card cost. The response is list with all founded card costs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return all founded card costs"),
            @ApiResponse(responseCode = "404", description = "No card costs found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/card-costs")
    public ResponseEntity<List<CardCost>> getAllCardCost() {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Get ALl Card Cost' request.", PREFIX);

        List<CardCost> cardCostList = cardCostService.getAllCardCost();

        log.info("{} Request 'Get All Card Cost' request executed within '{}' ms",
                PREFIX, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCostList);
    }

    @Tag(name = "Get", description = "GET methods of CardCost APIs")
    @Operation(summary = "Get card cost by ID", description = "Getting card cost by given id. The response is CardCost object with id, country & cost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return found card cost by Id"),
            @ApiResponse(responseCode = "404", description = "No card costs found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/card-costs/{cardCostId}")
    public ResponseEntity<CardCost> getCardCost(
            @Parameter(
                    description = "Id of card cost to be retrieved.",
                    required = true
            )
            @PathVariable Long cardCostId
    ) {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Get Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        CardCost cardCost = cardCostService.getCardCostById(cardCostId);

        log.info("{} Request 'Get Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(cardCost);
    }

    @Tag(name = "Put", description = "PUT method of CardCost APIs")
    @Operation(summary = "Update card cost by ID", description = "Update an existing card cost. The response is updated CardCost object with id, country & cost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update & return updated card cost"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Card cost already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/card-costs/{cardCostId}")
    public ResponseEntity<CardCost> updateCardCost(
            @Parameter(
                    description = "Id of card cost to be updated",
                    required = true
            )
            @PathVariable Long cardCostId,
            @Parameter(
                    description = "Request in JSON format with field that have to be updated.",
                    required = true
            )
            @RequestBody UpdateCardCostRequest updateCardCostRequest
    ) {
        long startTime = Utils.getStartTime();
        updateCardCostRequest.validate();
        log.info("{} Received 'Update Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        CardCost updatedCardCost = cardCostService.updateCardCostById(cardCostId, updateCardCostRequest);

        log.info("{} Request 'Update Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));

        return ResponseEntity.ok(updatedCardCost);
    }

    @Tag(name = "Delete", description = "DELETE method of CardCost APIs")
    @Operation(summary = "Delete card cost by ID", description = "Delete of and existing card cost. The response is empty with response code 204.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Return no content if card cost removed"),
            @ApiResponse(responseCode = "404", description = "Card cost already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/card-costs/{cardCostId}")
    public ResponseEntity<Void> deleteCardCost(
            @Parameter(
                    description = "Id of card cost to be deleted",
                    required = true
            )
            @PathVariable Long cardCostId
    ) {
        long startTime = Utils.getStartTime();
        log.info("{} Received 'Delete Card Cost' request for cardCostId: '{}'", PREFIX, cardCostId);

        cardCostService.deleteCardCostById(cardCostId);

        log.info("{} Request 'Delete Card Cost' request for cardCostId: '{}' executed within '{}' ms",
                PREFIX, cardCostId, Utils.getExecutionTime(startTime));
        return ResponseEntity.noContent().build();
    }
}
