package org.travel.cardcostapi.controllers;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CardCostController {

    @PostMapping("/payment-card-cost")
    public void getPaymentCardCost(){
        //TODO
    }

    @PostMapping
    public ResponseEntity<String> createCardCost() {
        return ResponseEntity.ok("Create Card cost");
    }

    @GetMapping
    public ResponseEntity<String> getCardCost() {
        return ResponseEntity.ok("Get card cost");
    }

    @PatchMapping
    public ResponseEntity<String> updateCardCost() {
        return ResponseEntity.ok("Update Card cost");
    }

    @PutMapping
    public ResponseEntity<String> updateCardCost(@RequestParam int cardId, @RequestParam int cost) {
        return ResponseEntity.ok("Update Card cost");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCardCost() {
        return ResponseEntity.ok("Delete Card cost");
    }
}
