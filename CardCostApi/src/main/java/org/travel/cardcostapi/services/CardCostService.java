package org.travel.cardcostapi.services;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.travel.cardcostapi.exceptions.ExternalApiException;
import org.travel.cardcostapi.exceptions.ResourceNotFoundException;
import org.travel.cardcostapi.models.CardCost;
import org.travel.cardcostapi.repositories.CardCostRepository;
import org.travel.cardcostapi.requests.CreateCardCostRequest;
import org.travel.cardcostapi.requests.PaymentCardCostRequest;
import org.springframework.web.client.RestTemplate;
import org.travel.cardcostapi.responses.CardInfoResponse;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CardCostService {
    private final String PREFIX = this.getClass().getSimpleName() + ":>";
    public static String BINLIST_URL = "https://lookup.binlist.net/";
    public static int BINLIST_CARD_LENGTH = 6;

    @Autowired
    private CardCostRepository cardCostRepository;

    //create new
    @Transactional
    public CardCost createCardCost(CreateCardCostRequest createCardCostRequest){
        CardCost cardCost = new CardCost();
        cardCost.setCountry(createCardCostRequest.getCountry());
        cardCost.setCost(createCardCostRequest.getCost());

        return cardCostRepository.save(cardCost);
    }

    //get all
    public List<CardCost> getAllCardCost(){
        List<CardCost> cardCostList = cardCostRepository.findAll();

        if(cardCostList.isEmpty()){
            log.error("{} No card costs found in database!", PREFIX);
            throw new ResourceNotFoundException("No card costs found");
        }

        return cardCostList;
    }

    //get by id
    public CardCost getCardCostById(Long cardCostId){
        Optional<CardCost> cardCostOptional = cardCostRepository.findById(cardCostId);

        cardCostOptional.orElseThrow(()-> new ResourceNotFoundException("Card Cost with Id: " + cardCostId + " do not exists!"));

        return cardCostOptional.get();
    }

    //update
    @Transactional
    public CardCost updateCardCostById(Long cardCostId){
        Optional<CardCost> cardCostOptional = cardCostRepository.findById(cardCostId);

        return cardCostOptional.map(cardCost -> {
            cardCost.setCountry(cardCost.getCountry());
            cardCost.setCost(cardCost.getCost());
            return cardCostRepository.save(cardCost);
        }).orElseThrow(()-> new ResourceNotFoundException("Card Cost with Id: " + cardCostId + " do not exists!"));
    }

    //delete
    @Transactional
    public void deleteCardCostById(Long cardCostId) {
        Optional<CardCost> cardCostOptional = cardCostRepository.findById(cardCostId);
        CardCost cardCost = cardCostOptional.orElseThrow(()-> new ResourceNotFoundException("Card Cost with Id: " + cardCostId + " do not exists!"));

        cardCostRepository.delete(cardCost);
    }

    public CardCost getPaymentCardCost(PaymentCardCostRequest paymentCardCostRequest){
        String bin = paymentCardCostRequest.getCardNumber().substring(0,BINLIST_CARD_LENGTH);
        String binlistUrl = BINLIST_URL + bin;

        try {
            log.debug("{} Sending request to external API with BIN: '{}'", PREFIX, bin);
            RestTemplate restTemplate = new RestTemplate();
            CardInfoResponse cardInfoResponse = restTemplate.getForObject(binlistUrl, CardInfoResponse.class);

            if (cardInfoResponse == null || cardInfoResponse.getCountry() == null) {
                throw new ExternalApiException("Invalid response from external API.");
            }

            String countryCode = cardInfoResponse.getCountry().getAlpha2();
            Optional<CardCost> cardCostOptional = cardCostRepository.findByCountry(countryCode);

            if(cardCostOptional.isEmpty()) {
                cardCostOptional = cardCostRepository.findByCountry("Other");
            }

            return cardCostOptional.orElseThrow(()-> new ResourceNotFoundException("Card Cost with country: " + countryCode + " do not exists!"));
        }catch (ResourceAccessException e) {
            throw new ExternalApiException("Failed to connect to external API. Please try again later.");
        } catch (HttpStatusCodeException e) {
            // Handle specific HTTP status errors
            throw new ExternalApiException("External API returned an error: " + e.getStatusCode());
        } catch (RestClientException e) {
            // Catch any other RestClient-related errors
            throw new ExternalApiException("An error occurred while communicating with the external API.");
        }
    }
}
